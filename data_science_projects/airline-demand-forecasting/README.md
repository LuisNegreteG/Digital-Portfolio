# ✈️ Airline Demand Predictive Analytics (Time Series Forecasting)

## 📄 Project Overview
This project builds a **time series forecasting** pipeline to model and predict **airline passenger demand**.  
It uses classical statistical models (**ARIMA / SARIMA**) from `statsmodels` and includes full diagnostics (stationarity tests, ACF/PACF, residual analysis).

> Notebook: [`Airline_Demand_PredAnalytics1.ipynb`](./Airline_Demand_PredAnalytics1.ipynb)

---

## 🎯 Objectives
- Explore airline demand and seasonality patterns.
- Test stationarity and apply transformations/differencing when needed.
- Fit and compare ARIMA/SARIMA candidates using information criteria (AIC).
- Validate on a hold-out set and report error metrics.
- Produce short-term forecasts with confidence intervals.

---

## 🧰 Tech Stack
- **Python**: `pandas`, `numpy`, `matplotlib`
- **Stats/TS**: `statsmodels` (`tsa.arima.model`, `tsa.stattools`)
- **Evaluation**: (MAE, RMSE, MAPE)

---

## 🔬 Methodology
1. **EDA & Preprocessing**
   - Parse datetime index, handle missing values/outliers.
   - Visual inspection of trend/seasonality.

2. **Stationarity**
   - Augmented Dickey–Fuller (ADF).
   - Differencing / transformations if needed.

3. **Model Identification**
   - ACF/PACF to suggest (p, d, q).
   - If seasonality detected: SARIMA (P, D, Q, s).

4. **Model Fitting & Selection**
   - Compare candidates via **AIC / BIC**.
   - Check residuals (white noise assumptions).

5. **Validation**
   - Train/Test split (time-ordered).
   - Metrics: **MAE**, **RMSE**, **MAPE** (report on test).

6. **Forecasting**
   - Out-of-sample forecast + **confidence intervals**.
   - Visualization and business interpretation.

---

## ✅ Results (to be updated)
- **Best model**: `ARIMA/SARIMA (p,d,q) x (P,D,Q,s)` *(fill after selection)*  
- **Test metrics**:
  - MAE: `...`
  - RMSE: `...`
  - MAPE: `...`
- **Key insights**:
  - Seasonality: `...`
  - Trend changes / shocks: `...`

---

## 📸 Plots (add saved figures from /images)

<img width="1389" height="590" alt="image" src="https://github.com/user-attachments/assets/498e4707-eb7d-4c76-93df-64d47cd00320" />

<img width="1389" height="590" alt="image" src="https://github.com/user-attachments/assets/7b052218-cacd-4fa5-a9e1-b197ad983000" />

