package com.smartstay.laboroptimizer.model;

import java.util.Map;

public class StaffingResponse {

    private Map<String, Integer> departmentStaff;
    private double estimatedSavingsPercent;
    private String overtimeRisk;
    private String recommendation;

    public Map<String, Integer> getDepartmentStaff() { return departmentStaff; }
    public void setDepartmentStaff(Map<String, Integer> departmentStaff) { this.departmentStaff = departmentStaff; }

    public double getEstimatedSavingsPercent() { return estimatedSavingsPercent; }
    public void setEstimatedSavingsPercent(double estimatedSavingsPercent) { this.estimatedSavingsPercent = estimatedSavingsPercent; }

    public String getOvertimeRisk() { return overtimeRisk; }
    public void setOvertimeRisk(String overtimeRisk) { this.overtimeRisk = overtimeRisk; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }
}