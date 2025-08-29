# 💳 Credit Risk Analysis Dashboard – Power BI + AI Insights

This project explores **credit card default risk** using the well-known **UCI Credit Card Clients dataset**.  
It combines **data modeling, DAX measures, interactive Power BI dashboards, and AI-driven segmentation** (PCA + clustering in Python).

---

## 🎯 Objective
- Build a **data model** for customer financial and demographic data.  
- Design **DAX measures** to track key risk KPIs.  
- Create **interactive dashboards** for credit risk monitoring.  
- Apply **AI techniques (clustering, PCA)** to enrich segmentation and recommendations.  

---

## 🗂️ Data Model
- **Main fact table**: `FinalMonthlyBehavior` (billing, payments, delinquency, credit limits).  
- **Dimensions & auxiliary tables**:  
  - `DateTable` – custom time intelligence.  
  - `ClientSummary` – aggregates for PCA insights.  
  - `ClientClassification` – assigns behavioral categories.  
  - `ClientWithClusters` – clustering results merged from Python.  
  - `CustomerSummary` – default vs. non-default comparison.  
  - `RatioBand` – billing-to-payment ratio mapped to risk levels.  
  - `KPI_Measures` – centralized DAX measure table.  

---

## 📐 DAX Measures (examples)
- **Total Billing** = SUM(BILLING_AMOUNT)  
- **Total Payment** = SUM(PAYMENT_AMOUNT)  
- **Default Rate (%)** = Customers with default / Total customers  
- **Recurring Balance** = AVG(Billing – Payment)  
- **Avg Bill/Payment Ratio** = [Avg Billing] / [Avg Payment]  
- **Billing/Payment MoM Growth (%)** = VAR Current – VAR Previous, DATEADD()  
- **Risk Score** = Combination of delinquency + credit usage  

---

## 📊 Dashboard Pages
1. **Overview** – Default rate (22%), billing & payment growth, credit usage vs. age.  
2. **Comparative Analysis** – Behavioral differences between default vs. non-default groups.  
3. **Risk & Behavioral Profiles** – Risk segmented by gender, marital status, education.  
4. **Advanced Risk Analysis** – Heatmaps & correlations (education, balances, risk score).  
5. **AI-Driven Insights** – Clusters (PCA) projected in 2D, profiling of 4 distinct customer segments.  

---

## 🔑 Insights
- **22% default rate**, with highest defaults among **clients 60+ years old**.  
- Customers with **bill-to-payment ratio > 5** are majority of defaults (high risk).  
- **Healthy usage ratio (0.5–1)** has lowest default risk (15%).  
- **Gender/Education bias**: males and less-educated groups show higher defaults.  
- **Clustering** revealed 4 distinct customer types:  
  - **Cluster 0** → High value, low risk.  
  - **Cluster 3** → Moderate value, highest risk of default.  

---

## 🧰 Tools
- **Power BI** (DAX, interactive dashboards)  
- **Python** (Clustering, PCA)  
- **Excel** (dataset preparation)  

---

## 📊 Dashboard Pages

<img width="1310" height="737" alt="image" src="https://github.com/user-attachments/assets/43423359-6f81-4a18-a641-86c4287a77cf" />

<img width="1308" height="737" alt="image" src="https://github.com/user-attachments/assets/45fe2ff2-a37e-4fee-9c60-732f1c6f56e3" />

<img width="1310" height="737" alt="image" src="https://github.com/user-attachments/assets/fe203e49-794b-4c95-8840-a97e801c7053" />

<img width="1308" height="731" alt="image" src="https://github.com/user-attachments/assets/d2fa2ce4-be70-4814-a6e8-63c094f9b89e" />

<img width="1302" height="732" alt="image" src="https://github.com/user-attachments/assets/a6773a73-b046-499b-af74-1e0800689c0d" />

---

⚠️ Disclaimer: This project is for **educational/demo purposes**. Dataset is from the **UCI Machine Learning Repository**.
