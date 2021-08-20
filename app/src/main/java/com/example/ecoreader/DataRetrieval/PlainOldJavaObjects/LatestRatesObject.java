package com.example.ecoreader.DataRetrieval.PlainOldJavaObjects;

import androidx.room.Entity;

import java.util.HashMap;

public class LatestRatesObject {
    private float amount;
    private String base;
    private String date;
    private HashMap<String, Float> rates;

    public LatestRatesObject(float amount, String base, String date, HashMap<String, Float> rates) {
        this.amount = amount;
        this.base = base;
        this.date = date;
        this.rates = rates;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public HashMap<String, Float> getRates() {
        return rates;
    }

    public void setRates(HashMap<String, Float> rates) {
        this.rates = rates;
    }
}
