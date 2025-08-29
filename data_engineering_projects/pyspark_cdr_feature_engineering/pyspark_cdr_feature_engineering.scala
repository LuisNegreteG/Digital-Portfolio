import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.spark.storage.StorageLevel

// -----------------------------------------------------------------------------
// Parameters (use spark-submit --conf "spark.driver.args=inputBase,outputBase,202401,BRAND_A|BRAND_B")
val args = sc.getConf.get("spark.driver.args").split(",")
val inputBasePath  = args(0)                 // e.g., s3://bucket/data/cdr_events
val outputBasePath = args(1)                 // e.g., s3://bucket/features/call_agg
val period         = args(2)                 // e.g., "202401"
val brandCodesArg  = if (args.length >= 4) Option(args(3)) else None
val brandCodes     = brandCodesArg.map(_.split("\\|").toSeq).getOrElse(Seq.empty[String])
// -----------------------------------------------------------------------------

// Read partitioned input (generic partition name `period`)
val cdrDF = spark.read
  .option("basePath", s"$inputBasePath")
  .parquet(s"$inputBasePath/period=$period")
  .persist(StorageLevel.MEMORY_AND_DISK)

cdrDF.createOrReplaceTempView("vw_cdr_events")

// Optional brand filter clause (fully generic, SAFE)
val brandFilterSql = if (brandCodes.nonEmpty) {
  val inList = brandCodes.map(b => s"'$b'").mkString(",")
  s"AND brand_code IN ($inList)"
} else ""

// -----------------------------------------------------------------------------
// Helper: common aggregation SQL fragment reused for caller & callee sides
val aggColumnsSql =
  """
   COUNT(DISTINCT other_party_id)                         AS unique_contacts,
   COUNT(1)                                              AS total_calls,
   SUM(call_duration)                                    AS total_duration,

   SUM(CASE WHEN record_type IN ('IN_A','IN_B','IN_C')  THEN 1 ELSE 0 END) AS calls_inbound,
   SUM(CASE WHEN record_type IN ('OUT_A','OUT_B','OUT_C') THEN 1 ELSE 0 END) AS calls_outbound,

   SUM(CASE WHEN record_type IN ('IN_A','IN_B','IN_C')  THEN call_duration ELSE 0 END) AS duration_inbound,
   SUM(CASE WHEN record_type IN ('OUT_A','OUT_B','OUT_C') THEN call_duration ELSE 0 END) AS duration_outbound,

   SUM(CASE WHEN day_name IN ('Sat','Sun') THEN 1 ELSE 0 END) AS calls_weekend,
   SUM(CASE WHEN day_name NOT IN ('Sat','Sun') THEN 1 ELSE 0 END) AS calls_weekday,
   SUM(CASE WHEN day_name IN ('Sat','Sun') THEN call_duration ELSE 0 END) AS duration_weekend,
   SUM(CASE WHEN day_name NOT IN ('Sat','Sun') THEN call_duration ELSE 0 END) AS duration_weekday,

   SUM(CASE WHEN day_of_month < 8 THEN 1 ELSE 0 END) AS calls_day_01_07,
   SUM(CASE WHEN day_of_month BETWEEN 8 AND 24 THEN 1 ELSE 0 END) AS calls_day_08_24,
   SUM(CASE WHEN day_of_month > 24 THEN 1 ELSE 0 END) AS calls_day_25_31,
   SUM(CASE WHEN day_of_month < 8 THEN call_duration ELSE 0 END) AS dur_day_01_07,
   SUM(CASE WHEN day_of_month BETWEEN 8 AND 24 THEN call_duration ELSE 0 END) AS dur_day_08_24,
   SUM(CASE WHEN day_of_month > 24 THEN call_duration ELSE 0 END) AS dur_day_25_31,

   MAX(call_duration)                                    AS max_call_duration,
   MAX(event_timestamp)                                  AS last_event_ts,

   SUM(CASE WHEN hour_24 BETWEEN 0 AND 6  THEN 1 ELSE 0 END)  AS calls_h00_06,
   SUM(CASE WHEN hour_24 BETWEEN 7 AND 13 THEN 1 ELSE 0 END)  AS calls_h07_13,
   SUM(CASE WHEN hour_24 BETWEEN 14 AND 19 THEN 1 ELSE 0 END) AS calls_h14_19,
   SUM(CASE WHEN hour_24 BETWEEN 20 AND 23 THEN 1 ELSE 0 END) AS calls_h20_23,

   SUM(CASE WHEN hour_24 BETWEEN 0 AND 6  THEN call_duration ELSE 0 END)  AS dur_h00_06,
   SUM(CASE WHEN hour_24 BETWEEN 7 AND 13 THEN call_duration ELSE 0 END)  AS dur_h07_13,
   SUM(CASE WHEN hour_24 BETWEEN 14 AND 19 THEN call_duration ELSE 0 END) AS dur_h14_19,
   SUM(CASE WHEN hour_24 BETWEEN 20 AND 23 THEN call_duration ELSE 0 END) AS dur_h20_23,

   SUM(CASE WHEN call_duration <=   30 THEN 1 ELSE 0 END) AS calls_le_30s,
   SUM(CASE WHEN call_duration <=  300 THEN 1 ELSE 0 END) AS calls_le_5m,
   SUM(CASE WHEN call_duration <= 1800 THEN 1 ELSE 0 END) AS calls_le_30m,
   SUM(CASE WHEN call_duration <= 3600 THEN 1 ELSE 0 END) AS calls_le_1h
  """

// -----------------------------------------------------------------------------
// 1) Aggregate from the "caller" perspective
val aggCaller = spark.sql(s"""
  SELECT
    period,
    caller_id AS user_id,
    $aggColumnsSql
  FROM (
    SELECT
      period,
      caller_id,
      callee_id AS other_party_id,
      call_duration,
      record_type,
      day_name,
      day_of_month,
      hour_24,
      event_timestamp,
      brand_code
    FROM vw_cdr_events
  ) base
  WHERE 1=1
  $brandFilterSql
  GROUP BY period, caller_id
""")

aggCaller.createOrReplaceTempView("vw_agg_caller")

// 2) Aggregate from the "callee" perspective
val aggCallee = spark.sql(s"""
  SELECT
    period,
    callee_id AS user_id,
    $aggColumnsSql
  FROM (
    SELECT
      period,
      callee_id,
      caller_id AS other_party_id,
      call_duration,
      record_type,
      day_name,
      day_of_month,
      hour_24,
      event_timestamp,
      brand_code
    FROM vw_cdr_events
  ) base
  WHERE 1=1
  $brandFilterSql
  GROUP BY period, callee_id
""")

aggCallee.createOrReplaceTempView("vw_agg_callee")

// 3) Union caller/callee & second-stage aggregation (by user_id)
val aggUnion = aggCaller.unionByName(aggCallee)
aggUnion.createOrReplaceTempView("vw_union_features")

val features = spark.sql("""
  SELECT
    period,
    user_id,
    MAX(unique_contacts)                         AS unique_contacts,
    SUM(total_calls)                             AS total_calls,
    SUM(total_duration)                          AS total_duration,

    SUM(calls_inbound)                           AS calls_inbound,
    SUM(calls_outbound)                          AS calls_outbound,
    SUM(duration_inbound)                        AS duration_inbound,
    SUM(duration_outbound)                       AS duration_outbound,

    SUM(calls_weekend)                           AS calls_weekend,
    SUM(calls_weekday)                           AS calls_weekday,
    SUM(duration_weekend)                        AS duration_weekend,
    SUM(duration_weekday)                        AS duration_weekday,

    SUM(calls_day_01_07)                         AS calls_day_01_07,
    SUM(calls_day_08_24)                         AS calls_day_08_24,
    SUM(calls_day_25_31)                         AS calls_day_25_31,
    SUM(dur_day_01_07)                           AS dur_day_01_07,
    SUM(dur_day_08_24)                           AS dur_day_08_24,
    SUM(dur_day_25_31)                           AS dur_day_25_31,

    MAX(max_call_duration)                       AS max_call_duration,
    MAX(last_event_ts)                           AS last_event_ts,

    SUM(calls_h00_06)                            AS calls_h00_06,
    SUM(calls_h07_13)                            AS calls_h07_13,
    SUM(calls_h14_19)                            AS calls_h14_19,
    SUM(calls_h20_23)                            AS calls_h20_23,

    SUM(dur_h00_06)                              AS dur_h00_06,
    SUM(dur_h07_13)                              AS dur_h07_13,
    SUM(dur_h14_19)                              AS dur_h14_19,
    SUM(dur_h20_23)                              AS dur_h20_23,

    SUM(calls_le_30s)                            AS calls_le_30s,
    SUM(calls_le_5m)                             AS calls_le_5m,
    SUM(calls_le_30m)                            AS calls_le_30m,
    SUM(calls_le_1h)                             AS calls_le_1h
  FROM vw_union_features
  GROUP BY period, user_id
""").cache()

features.show(3, truncate = false)

// (Opcional) Cast a tipos específicos
val featuresTyped = features.select(
  col("period"),
  col("user_id"),
  col("unique_contacts").cast(IntegerType),
  col("total_calls").cast(IntegerType),
  col("total_duration").cast(IntegerType),
  col("calls_inbound").cast(IntegerType),
  col("calls_outbound").cast(IntegerType),
  col("duration_inbound").cast(IntegerType),
  col("duration_outbound").cast(IntegerType),
  col("calls_weekend").cast(IntegerType),
  col("calls_weekday").cast(IntegerType),
  col("duration_weekend").cast(IntegerType),
  col("duration_weekday").cast(IntegerType),
  col("calls_day_01_07").cast(IntegerType),
  col("calls_day_08_24").cast(IntegerType),
  col("calls_day_25_31").cast(IntegerType),
  col("dur_day_01_07").cast(IntegerType),
  col("dur_day_08_24").cast(IntegerType),
  col("dur_day_25_31").cast(IntegerType),
  col("max_call_duration").cast(IntegerType),
  col("last_event_ts"),
  col("calls_h00_06").cast(IntegerType),
  col("calls_h07_13").cast(IntegerType),
  col("calls_h14_19").cast(IntegerType),
  col("calls_h20_23").cast(IntegerType),
  col("dur_h00_06").cast(IntegerType),
  col("dur_h07_13").cast(IntegerType),
  col("dur_h14_19").cast(IntegerType),
  col("dur_h20_23").cast(IntegerType),
  col("calls_le_30s").cast(IntegerType),
  col("calls_le_5m").cast(IntegerType),
  col("calls_le_30m").cast(IntegerType),
  col("calls_le_1h").cast(IntegerType)
)

// Write partitioned by period (generic, SAFE)
featuresTyped
  .write
  .mode("overwrite")
  .parquet(s"$outputBasePath/period=$period")

features.unpersist()
cdrDF.unpersist()

spark.stop()
System.exit(0)
