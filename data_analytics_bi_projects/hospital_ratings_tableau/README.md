# 🏥 Hospital Ratings & Timeliness Dashboard – Tableau

This project was developed as part of the **Advanced Data Visualization course**.  
It delivers an **interactive Tableau dashboard** that enables regulators and healthcare stakeholders to benchmark hospital performance across the United States.

---

## 🎯 Objective
- Provide a **decision-support tool** for healthcare regulators.  
- Benchmark hospitals by **state, ownership type, and emergency services availability**.  
- Spot systemic patterns in quality and timeliness of care.  
- Identify **underperforming regions or organizations** requiring interventions.  

---

## 🗂️ Data Preparation
- **Source**: Hospital General Information dataset (CMS).  
- **Prep tool**: Tableau Prep Builder.  
- **Steps**:
  - Removed irrelevant fields (footnotes, admin codes).  
  - Standardized **ownership categories** (merged government types, nonprofit types).  
  - Recoded “Not Available” values to `NULL`.  
  - Created **calculated fields** mapping descriptive ratings into numeric scores (0–5).  
  - Grouped U.S. territories into a single **“Other” region**.  
  - Split latitude/longitude fields for mapping.  
- Output: clean `.hyper` extract used in Tableau dashboards.  

---

## 📊 Dashboard Views
1. **Which States Perform Best?**  
   - Bar chart comparing **average hospital rating** by state.  
   - Example: Connecticut & West Virginia near **2.7**, vs. national average **2.4**, and DC with only **1.4**.

2. **Ownership and Quality Patterns**  
   - Stacked bar chart of ratings by **ownership type**.  
   - Voluntary hospitals: wide distribution.  
   - Government hospitals: clustered at low-mid performance.  
   - Private hospitals: even distribution.  

3. **Do Emergency Services Align with Quality?**  
   - Hospitals with emergency services only slightly outperform those without.  
   - Non-emergency hospitals have higher share of “Poor” ratings.  

4. **Nationwide View of Quality**  
   - Map showing hospital density and rating distribution.  
   - Clusters in Northeast & Midwest; sparse in rural states.  

---

## 🔑 Insights
- Clear **regional disparities** (e.g., DC underperforms significantly).  
- **Ownership structures** correlate with performance patterns.  
- **Emergency services availability** is not a strong predictor of quality.  
- Visualization provides **easy filtering** by state, ownership, hospital type.  

---

## 🧰 Tools
- **Tableau Desktop** (dashboards, maps, stacked bars).  
- **Tableau Prep Builder** (data cleansing).  

---

## 📸 Dashboard Preview
<img width="1613" height="782" alt="image" src="https://github.com/user-attachments/assets/4509eff3-8383-4082-a833-d809402fa3fa" />

---

⚠️ Disclaimer: This project uses **public CMS datasets**. All data and insights are for educational/demo purposes.
