# 📊 Sales Analytics – Power BI Project

This project was developed as part of a **Data Warehousing & Visualization course**.  
It transforms raw sales data into a **star schema model** and an interactive **Power BI dashboard** for decision-making.

---

## 🎯 Objective
- Build a stable **data model** with proper relationships.  
- Define **DAX measures** for KPIs (sales, profit, margins, YoY growth, repeat customers).  
- Design a **multi-page dashboard** to analyze performance by time, region, product, and customer.  
- Simulate **What-If scenarios** for price changes.

---

## 🗂️ Data Model
- **Fact table**: Sales  
- **Dimensions**: Products, Customers, Regions, DateTable, TimeAxis  
- **Auxiliary tables**: PriceChange, Scenarios  
- Schema: **Star model** with one-to-many relationships  
- Measures grouped in DateTable for optimization.

---

## 📐 DAX Measures (examples)
- **Total Sales** = SUM(Sales[SalesAmount])  
- **Profit** = [Total Sales] - [Total Cost]  
- **YoY %** = ([Total Sales] - [Previous Year Sales]) / [Previous Year Sales]  
- **Repeat Customers** = Customers with >1 purchase  
- **Profit Margin** = Profit / Total Sales  
- **Adjusted Sales / Profit** = Simulations with price change %  

---

## 📊 Dashboard Pages
1. **Sales Analytics** – trends, seasonality, anomalies (e.g., September slump).  
2. **Customer Analytics** – segmentation by age, gender, location, profitability.  
3. **Product Analytics** – performance by category (Clothing, Books, Electronics).  
4. **Region Analytics** – revenues/costs/profits by country & region.  
5. **Predictive Scenarios** – waterfall charts simulating price change impact.  

---

## 🔑 Insights
- Sales decline in **September**, driven by drops in Canada & UK.  
- Customers **40–50 years old** are the most profitable segment.  
- **Clothing** outperforms other categories; **Electronics** lag in profits.  
- Predictive scenarios highlight how small **price adjustments** impact revenue & profit.

---

## 🧰 Tools
- **Power BI** (visualization, DAX)  
- **Excel** (dataset preparation)  
- **SQL basics** (star schema model)  

---

## 📂 Folder Contents
- `sales_data.xlsx` → dataset source (dummy/demo data).  

---

## 📊 Dashboard Pages

<img width="1312" height="736" alt="image" src="https://github.com/user-attachments/assets/64353b92-0a14-4cb8-9dd1-d09b52d4a8d3" />

<img width="1312" height="737" alt="image" src="https://github.com/user-attachments/assets/6e183965-1ede-43c9-af77-c0e356c87655" />

<img width="1312" height="740" alt="image" src="https://github.com/user-attachments/assets/df107155-6663-4bd6-97f7-e7b7e36bbdfd" />

<img width="1312" height="737" alt="image" src="https://github.com/user-attachments/assets/b63d7296-1211-45cd-9b14-f95edbe9a912" />

<img width="1312" height="738" alt="image" src="https://github.com/user-attachments/assets/eaf626b8-4b68-43a1-83cd-2302b9d2c732" />





⚠️ Disclaimer: All data is **dummy/demo** provided for educational purposes. No confidential data is included.
