package com.example.ecoreader.DataRetrieval;

public class TimeSeriesObject {
    private int amount;
    private String base;
    private String start_date;
    private String end_date;
    private String[][] rates;

    public TimeSeriesObject(int amount, String base, String start_date, String end_date, String[][] rates) {
        this.amount = amount;
        this.base = base;
        this.start_date = start_date;
        this.end_date = end_date;
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

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public String[][] getRates() {
        return rates;
    }

    public void setRates(String[][] rates) {
        this.rates = rates;
    }
}
