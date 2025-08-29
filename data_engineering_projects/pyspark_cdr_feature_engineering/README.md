# 📡 PySpark CDR Feature Engineering Pipeline

## 📄 Project Overview
This project demonstrates how to build a **data engineering pipeline** with **PySpark/Scala** to transform raw telecom Call Detail Records (CDR) into **aggregated user-level features**.  
The pipeline computes call volume, durations, temporal distributions, and other aggregated features useful for **analytics, machine learning, and customer segmentation**.

---

## 🧰 Tech Stack
- Apache Spark (Scala API)
- PySpark SQL functions
- Parquet format for input/output
- Cluster execution ready (YARN, EMR, Dataproc)

---

## ⚙️ Pipeline Logic
1. **Input**  
   - Reads raw **CDR events** partitioned by `period` (e.g., month).  
   - Columns expected: `caller_id`, `callee_id`, `call_duration`, `record_type`, `day_name`, `day_of_month`, `hour_24`, `event_timestamp`.

2. **Transformations**  
   - Aggregates by user (both caller and callee perspective).  
   - Computes inbound/outbound calls, durations, max call length, weekend/weekday splits, and time-of-day activity.  
   - Builds per-user summary features.

3. **Output**  
   - Writes aggregated features to Parquet, partitioned by `period`.  
   - Features can be used for **churn prediction, customer segmentation, fraud detection** or general analytics.

---

## 🚀 How to Run
Example with `spark-submit`:

```bash
spark-submit \
  --class org.apache.spark.examples.SparkCDRPipeline \
  --conf "spark.driver.args=/input/path,/output/path,202401,BRAND_A|BRAND_B" \
  pyspark_cdr_feature_engineering.scala
