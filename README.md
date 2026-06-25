# 🏨 SmartStay Labor Cost Optimizer

> AI-powered staffing recommendation engine for Indian hotels  
> Built for Cognizant Delivery Excellence Hackathon 2026  
> Team: **SmartStay Architects** | Priti Mondal (2418076)

---

## 💡 Problem Statement

Labor cost is one of the largest controllable expenses for hotels. Indian hotels face uneven demand due to weddings, long weekends, festivals, corporate groups, and sudden occupancy changes — leading to overstaffing, overtime payments, or underutilized temporary workers.

## 🚀 Solution

An AI-powered labor planning engine that forecasts staffing needs by department and recommends optimal shift allocation based on:
- Occupancy percentage
- Upcoming on-property events (Wedding, Conference, Festival)
- Restaurant covers (F&B bookings)
- Hotel tier (Budget / Midscale / Upscale / Luxury)
- Special circumstances (VIP Guest, City Event, Long Weekend)

## 🏗️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17 + Spring Boot 3 |
| REST API | Spring MVC |
| Frontend | HTML5 + CSS3 + JavaScript |
| Build Tool | Maven |
| IDE | Eclipse |
| Version Control | Git + GitHub |
| Server | Embedded Tomcat |

## 📦 Departments Covered

Housekeeping · Front Office · F&B / Restaurant · Banquet · Kitchen · Security · Maintenance

## ▶️ How to Run Locally

**Prerequisites:** Java 17, Maven

```bash
git clone https://github.com/pssm2090/labor-cost-optimizer.git
cd labor-cost-optimizer
mvn clean install
java -jar target/labor-optimizer-0.0.1-SNAPSHOT.jar
```

Open browser at: `http://localhost:8080`

## 🔮 Future Scope

- ML model (Random Forest / Linear Regression) for demand forecasting
- NLP to auto-parse event descriptions into staffing signals
- PMS (Property Management System) API integration
- Historical data feedback loop for hotel-specific tuning

## 📊 Business Impact

- 15–20% reduction in overtime costs
- 10–12% overall labor cost savings per hotel
- Up to 25% improvement in shift utilization
- ROI achieved within 6–9 months
