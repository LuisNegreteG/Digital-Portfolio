# 📡 CDR Daily Ingestion Pipeline (Spark/Scala)

Ingest **daily-partitioned** Call Detail Records (CDR), apply **safe, parameterized filters**, normalize column names, and write a **curated Parquet** dataset partitioned by `period`.

> File: `pyspark_cdr_daily_ingestion.scala`  
> Entry point (object): `CdrDailyIngestionJob`

---

## 🚀 What this job does

1) **Discovers daily partitions** (e.g., `event_time_partition=yyyyMMdd`) within a month.  
2) **Reads all existing days at once** (no chained unions) for better performance.  
3) **Renames columns** to generic, non-sensitive names (`caller_id`, `callee_id`, …).  
4) **Applies parameterized filters** (record types, routes, MSISDN prefix/length, duration).  
5) **Writes curated output** to Parquet, partitioned by `period` (e.g., `period=202501`).

---

## 🧰 Tech stack

- **Apache Spark (Scala API)**
- Parquet I/O
- HDFS/S3 compatible
- Parameterized via `spark.driver.args`

---

## 📦 Input assumptions

- Source dataset is Parquet, **partitioned by day** using key `event_time_partition=yyyyMMdd`  
  Example paths:
  /raw/cdr/event_time_partition=20250101
  /raw/cdr/event_time_partition=20250102


---

## 🗺️ Output

- Parquet dataset **partitioned by** `period` (e.g., `period=202501`).  
Example:
  /curated/cdr/period=202501/part-*.snappy.parquet

## ⚙️ Parameters

All parameters are passed in a single, comma-separated string via `spark.driver.args`.

| Pos | Name             | Example                         | Notes                          |
|-----|------------------|----------------------------------|--------------------------------|
| 1   | `rawBasePath`    | `/raw/cdr`                      | Base of daily partitions       |
| 2   | `outputBasePath` | `/curated/cdr`                  | Destination base               |
| 3   | `period`         | `202501`                        | Year+month                     |
| 4   | `dayStart`       | `1`                              | First day to read              |
| 5   | `dayEnd`         | `31`                             | Last day to read               |
| 6   | `phonePrefix`    | `PRE`                            | Generic MSISDN prefix placeholder |
| 7   | `msisdnLength`   | `10`                             | Expected trimmed length        |
| 8   | `routeCodes`     | `ROUTE_A|ROUTE_B`                | Pipe-separated allowed routes  |
| 9   | `recordTypes`    | `IN_A|OUT_A|IN_B|OUT_B`          | Pipe-separated record types    |

---

## ▶️ How to run

```bash
spark-submit \
--class CdrDailyIngestionJob \
--conf "spark.driver.args=/raw/cdr,/curated/cdr,202501,1,31,PRE,10,ROUTE_A|ROUTE_B,IN_A|OUT_A|IN_B|OUT_B" \
data_engineering_projects/cdr_daily_ingestion_pipeline/pyspark_cdr_daily_ingestion.scala
