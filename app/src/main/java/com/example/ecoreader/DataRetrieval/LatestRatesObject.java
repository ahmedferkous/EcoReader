package com.example.ecoreader.DataRetrieval;

public class LatestRatesObject {
    private int amount;
    private String base;
    private String date;
    private String[] rates;

    public LatestRatesObject(int amount, String base, String date, String[] rates) {
        this.amount = amount;
        this.base = base;
        this.date = date;
        this.rates = rates;
    }

    public int getAmount() {
        return amount;
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

    public String[] getRates() {
        return rates;
    }

    public void setRates(String[] rates) {
        this.rates = rates;
    }
}
