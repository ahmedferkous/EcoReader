package com.example.ecoreader.DataRetrieval.PlainOldJavaObjects;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class StatisticsObject {
    @SerializedName("labour_force_statistics")
    private ArrayList<Values> labourStatistics;

    public StatisticsObject(ArrayList<Values> labourStatistics) {
        this.labourStatistics = labourStatistics;
    }

    public ArrayList<Values> getLabourStatistics() {
        return labourStatistics;
    }

    public void setLabourStatistics(ArrayList<Values> labourStatistics) {
        this.labourStatistics = labourStatistics;
    }

    public static class Values {
        @SerializedName("region_description")
        private String region;
        @SerializedName("data_item_description")
        private String dataItem;
        @SerializedName("sex_description")
        private String sexDesc;
        @SerializedName("age_description")
        private String ageDesc;
        @SerializedName("adjustment_type_description")
        private String adjustmentTypeDesc;
        @SerializedName("observation_month")
        private String observationMonth;
        @SerializedName("observation_value")
        private float observationValue;

        public Values(String region, String dataItem, String sexDesc, String ageDesc, String adjustmentTypeDesc, String observationMonth, float observationValue) {
            this.region = region;
            this.dataItem = dataItem;
            this.sexDesc = sexDesc;
            this.ageDesc = ageDesc;
            this.adjustmentTypeDesc = adjustmentTypeDesc;
            this.observationMonth = observationMonth;
            this.observationValue = observationValue;
        }

        public String getRegion() {
            return region;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public String getDataItem() {
            return dataItem;
        }

        public void setDataItem(String dataItem) {
            this.dataItem = dataItem;
        }

        public String getSexDesc() {
            return sexDesc;
        }

        public void setSexDesc(String sexDesc) {
            this.sexDesc = sexDesc;
        }

        public String getAgeDesc() {
            return ageDesc;
        }

        public void setAgeDesc(String ageDesc) {
            this.ageDesc = ageDesc;
        }

        public String getAdjustmentTypeDesc() {
            return adjustmentTypeDesc;
        }

        public void setAdjustmentTypeDesc(String adjustmentTypeDesc) {
            this.adjustmentTypeDesc = adjustmentTypeDesc;
        }

        public String getObservationMonth() {
            return observationMonth;
        }

        public void setObservationMonth(String observationMonth) {
            this.observationMonth = observationMonth;
        }

        public float getObservationValue() {
            return observationValue;
        }

        public void setObservationValue(float observationValue) {
            this.observationValue = observationValue;
        }
    }
}
