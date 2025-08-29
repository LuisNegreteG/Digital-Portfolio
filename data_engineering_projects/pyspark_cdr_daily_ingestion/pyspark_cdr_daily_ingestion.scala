
---

# 🧾 Código: `data_engineering_projects/cdr_daily_ingestion_pipeline/pyspark_cdr_daily_ingestion.scala`

```scala
// File: pyspark_cdr_daily_ingestion.scala
// Purpose: Read daily-partitioned raw CDRs -> apply safe, parameterized filters -> write curated parquet partitioned by period
// Notes: All names/filters are placeholders (generic & anonymized)

import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.hadoop.fs.{FileSystem, Path}
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object CdrDailyIngestionJob {

  case class Params(
    rawBasePath: String,
    outputBasePath: String,
    period: String,
    dayStart: Int,
    dayEnd: Int,
    phonePrefix: String,
    msisdnLength: Int,
    routeCodes: Seq[String],
    recordTypes: Seq[String]
  )

  def parseArgs(argstr: String): Params = {
    val a = argstr.split(",")
    require(a.length >= 9,
      s"Expected 9 args: rawBase, outputBase, period, dayStart, dayEnd, phonePrefix, msisdnLength, routeCodes, recordTypes")
    Params(
      rawBasePath     = a(0),
      outputBasePath  = a(1),
      period          = a(2),
      dayStart        = a(3).toInt,
      dayEnd          = a(4).toInt,
      phonePrefix     = a(5),
      msisdnLength    = a(6).toInt,
      routeCodes      = a(7).split("\\|").toSeq,
      recordTypes     = a(8).split("\\|").toSeq
    )
  }

  def main(sysArgs: Array[String]): Unit = {
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    try {
      val params = parseArgs(spark.sparkContext.getConf.get("spark.driver.args"))

      val fs   = FileSystem.get(spark.sparkContext.hadoopConfiguration)
      val fmt  = DateTimeFormatter.ofPattern("yyyyMMdd")

      // Build daily paths safely (raw expected partition: event_time_partition=yyyyMMdd)
      val baseDate   = LocalDate.parse(s"${params.period.take(4)}-${params.period.drop(4)}-01")
      val startDate  = baseDate.`with`(java.time.temporal.TemporalAdjusters.firstDayOfMonth()).withDayOfMonth(params.dayStart)
      val endDate    = baseDate.`with`(java.time.temporal.TemporalAdjusters.firstDayOfMonth()).withDayOfMonth(params.dayEnd)

      val dailyPaths = Iterator.iterate(startDate)(_.plusDays(1))
        .takeWhile(!_.isAfter(endDate))
        .map(d => s"${params.rawBasePath}/event_time_partition=${d.format(fmt)}")
        .filter(p => fs.exists(new Path(p)))
        .toSeq

      require(dailyPaths.nonEmpty,
        s"No input partitions found for period=${params.period} and range [${params.dayStart},${params.dayEnd}] under ${params.rawBasePath}")

      // Read all days at once (faster & simpler than chaining unions)
      val rawDF = spark.read
        .option("basePath", params.rawBasePath)
        .parquet(dailyPaths: _*)

      // Rename to generic column names (SAFE placeholders)
      val df = rawDF.select(
        col("TIM_NUMBER").as("caller_id"),
        col("NUMBER_B").as("callee_id"),
        col("RECORD_TYPE").as("record_type"),
        col("CHARGING_START_TIME").as("event_ts"),
        col("CALL_DURATION").as("call_duration"),
        col("RN_ORI").as("route_src"),
        col("RN_DES").as("route_dst")
      )

      // Parameterized filters (no proprietary codes)
      val curated = df
        .filter(col("call_duration").cast(IntegerType) > 5)
        .filter(col("record_type").isin(params.recordTypes: _*))
        .filter(col("route_src").isin(params.routeCodes: _*) && col("route_dst").isin(params.routeCodes: _*))
        .filter(startsWith(trim(col("caller_id")), params.phonePrefix) && length(trim(col("caller_id"))) === params.msisdnLength)
        .filter(startsWith(trim(col("callee_id")), params.phonePrefix) && length(trim(col("callee_id"))) === params.msisdnLength)
        .withColumn("period", lit(params.period))

      // Optional: control file sizes per partition
      val curatedOut = curated.repartition(col("period"))

      curatedOut.write
        .mode("overwrite")
        .parquet(s"${params.outputBasePath}/period=${params.period}")

      println(s"[OK] Wrote curated CDRs to ${params.outputBasePath}/period=${params.period}")

    } catch {
      case e: Throwable =>
        System.err.println(s"[ERROR] ${e.getMessage}")
        throw e
    } finally {
      spark.stop()
    }
  }
}
