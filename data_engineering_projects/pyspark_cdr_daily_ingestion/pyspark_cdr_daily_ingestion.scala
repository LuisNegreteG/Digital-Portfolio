import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import org.apache.hadoop.fs.{FileSystem, Path}
import java.time.LocalDate
import java.time.format.DateTimeFormatter

object DailyIngestionJob {

  case class Params(
    inBase: String,        // e.g., s3://bucket/raw
    outBase: String,       // e.g., s3://bucket/curated
    period: String,        // e.g., 202501 (YYYYMM)
    dStart: Int,           // first day (1..31)
    dEnd: Int,             // last day (1..31)
    idPrefix: String,      // generic prefix filter for ids (e.g., "PX")
    idLen: Int,            // expected trimmed length for ids (e.g., 10)
    routeList: Seq[String],// allowed route codes (pipe-separated in args)
    typeList: Seq[String]  // allowed type codes  (pipe-separated in args)
  )

  private def parseArgs(argstr: String): Params = {
    val a = argstr.split(",")
    require(
      a.length >= 9,
      "Expected 9 args: inBase,outBase,period,dStart,dEnd,idPrefix,idLen,routeList,typeList"
    )
    Params(
      inBase    = a(0),
      outBase   = a(1),
      period    = a(2),
      dStart    = a(3).toInt,
      dEnd      = a(4).toInt,
      idPrefix  = a(5),
      idLen     = a(6).toInt,
      routeList = a(7).split("\\|").toSeq,
      typeList  = a(8).split("\\|").toSeq
    )
  }

  def main(sysArgs: Array[String]): Unit = {
    val spark = SparkSession.builder().getOrCreate()
    import spark.implicits._

    try {
      val p = parseArgs(spark.sparkContext.getConf.get("spark.driver.args"))

      val fs  = FileSystem.get(spark.sparkContext.hadoopConfiguration)
      val fmt = DateTimeFormatter.ofPattern("yyyyMMdd")

      // Build daily paths (raw expected partition key is generic: dt=yyyyMMdd)
      val year  = p.period.take(4).toInt
      val month = p.period.drop(4).toInt
      val start = LocalDate.of(year, month, p.dStart)
      val end   = LocalDate.of(year, month, p.dEnd)

      val dailyPaths: Seq[String] =
        Iterator.iterate(start)(_.plusDays(1))
          .takeWhile(!_.isAfter(end))
          .map(d => s"${p.inBase}/dt=${d.format(fmt)}")
          .filter(path => fs.exists(new Path(path)))
          .toSeq

      require(dailyPaths.nonEmpty,
        s"No input partitions found for period=${p.period} and range [${p.dStart},${p.dEnd}] under ${p.inBase}")

      // Read all days at once
      val raw = spark.read
        .option("basePath", p.inBase)
        .parquet(dailyPaths: _*)

      // Map raw columns to neutral names (adjust the selectors on the left to your raw schema)
      // Left side: raw source columns (example placeholders) -> Right side: neutral names
      val df = raw.select(
        col("COL_A").as("id_a"),       // e.g., caller id
        col("COL_B").as("id_b"),       // e.g., callee id
        col("COL_T").as("type_code"),  // e.g., record type
        col("COL_TS").as("ts"),        // e.g., event timestamp
        col("COL_D").as("dur"),        // e.g., duration
        col("COL_S").as("src"),        // e.g., route source
        col("COL_R").as("dst")         // e.g., route destination
      )

      // Parameterized filters (fully generic)
      val curated = df
        .filter(col("dur").cast(IntegerType) > 5)
        .filter(col("type_code").isin(p.typeList: _*))
        .filter(col("src").isin(p.routeList: _*) && col("dst").isin(p.routeList: _*))
        .filter(trim(col("id_a")).startsWith(p.idPrefix) && length(trim(col("id_a"))) === p.idLen)
        .filter(trim(col("id_b")).startsWith(p.idPrefix) && length(trim(col("id_b"))) === p.idLen)
        .withColumn("prd", lit(p.period)) // neutral partition column

      // Optional: control file sizes/parallelism
      val outDf = curated.repartition(col("prd"))

      outDf.write
        .mode("overwrite")
        .parquet(s"${p.outBase}/prd=${p.period}")

      println(s"[OK] Wrote curated dataset to ${p.outBase}/prd=${p.period}")

    } catch {
      case e: Throwable =>
        System.err.println(s"[ERROR] " + e.getMessage)
        throw e
    } finally {
      spark.stop()
    }
  }
}
