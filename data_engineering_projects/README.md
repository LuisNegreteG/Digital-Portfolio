# ⚙️ Data Engineering Projects

This folder contains a collection of projects focused on **data pipelines, ETL automation, SQL engineering, and big data frameworks**.  
Each project demonstrates how I design and implement robust data engineering solutions to **ingest, transform, and prepare data for analytics and data science**.

---

## 📂 Projects

### 1. 🛰️ PySpark CDR Daily Ingestion
- **Goal**: Build a daily ingestion pipeline for **Call Detail Records (CDR)** using **Apache Spark (Scala/PySpark)**.  
- **Highlights**:
  - Reads daily-partitioned raw records.  
  - Applies parameterized filters for **record type, routes, MSISDN prefix, and call duration**.  
  - Writes curated outputs to Parquet partitioned by **period (YYYYMM)**.  
- **Focus**: Demonstrates **data ingestion at scale**, partitioning strategies, and parameterized ETL.

---

### 2. 🔧 PySpark CDR Feature Engineering
- **Goal**: Engineer features from **telecom usage data** for analytics and ML models.  
- **Highlights**:
  - Aggregates call counts, durations, and behavioral patterns.  
  - Builds **time-of-day, weekday/weekend, and monthly segment features**.  
  - Outputs a structured dataset optimized for downstream **machine learning pipelines**.  
- **Focus**: Shows how to transform raw events into **analytical features** efficiently.

---

### 3. 🗄️ SQL Examples
- **Goal**: Provide reusable SQL/PLSQL scripts for data warehousing and analytics.  
- **Highlights**:
  - **Partitioned table DDLs** for scalable storage.  
  - **Dynamic SQL ETL** automation (PL/SQL).  
  - **Stored procedures** for balance and snapshot calculations.  
  - **Analytical queries**: RFM segmentation, usage buckets, window functions.  
- **Focus**: Demonstrates **SQL engineering best practices** for ETL, warehousing, and advanced analytics.

---

## 🛠️ Skills & Tools Highlighted
- **Big Data & Pipelines**: Apache Spark (PySpark, Scala), Parquet, HDFS/S3.  
- **SQL Engineering**: DDL, DML, partitioning, stored procedures, optimization.  
- **Feature Engineering**: building analytical datasets from raw telecom/usage logs.  
- **ETL Automation**: dynamic SQL, scheduling patterns, ingestion pipelines.  
- **Best Practices**: partitioning strategies, parameterization, data validation.

---

## 📌 Value
These projects demonstrate my ability to:  
- Design and build **scalable ingestion pipelines** for high-volume data.  
- Engineer **analytical features** from raw datasets for ML/BI.  
- Apply **SQL & PL/SQL** to manage warehousing and ETL automation.  
- Bridge **data engineering with analytics and data science**.
