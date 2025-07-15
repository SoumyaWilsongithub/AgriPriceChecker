package com.example.agripricechecker.models;

import java.util.List;

public class FertilizerModel {
    private String npk;
    private List<String> products;
    private String advice;

    public FertilizerModel() {
        // Required for Firebase
    }

    public FertilizerModel(String npk, List<String> products, String advice) {
        this.npk = npk;
        this.products = products;
        this.advice = advice;
    }

    public String getNpk() {
        return npk;
    }

    public List<String> getProducts() {
        return products;
    }

    public String getAdvice() {
        return advice;
    }
}
