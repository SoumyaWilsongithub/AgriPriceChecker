package com.example.agripricechecker;

public class PriceModel {
    private String crop;
    private String market;
    private String price;
    private String date;

    public PriceModel(String crop, String market, String price, String date) {
        this.crop = crop;
        this.market = market;
        this.price = price;
        this.date = date;
    }

    public String getCrop() { return crop; }
    public String getMarket() { return market; }
    public String getPrice() { return price; }
    public String getDate() { return date; }

    public float getPriceValue() {
        try {
            return Float.parseFloat(price);
        } catch (Exception e) {
            return 0;
        }
    }
}
