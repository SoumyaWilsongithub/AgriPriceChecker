package com.example.agripricechecker;

/**
 * Model class representing a single price record for a crop.
 * Data is typically stored in English (as received from the API)
 * and translated via the PriceAdapter for UI display.
 */
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

    public String getCrop() {
        return crop;
    }

    public String getMarket() {
        return market;
    }

    public String getPrice() {
        return price;
    }

    public String getDate() {
        return date;
    }

    /**
     * Helper to get a float value of the price.
     * Useful for mathematical operations, sorting, or chart plotting.
     * It handles cases where the price string might contain non-numeric characters.
     */
    public float getPriceValue() {
        if (price == null || price.isEmpty()) {
            return 0f;
        }
        try {
            // Remove currency symbols, commas, or extra text if present
            String cleanPrice = price.replaceAll("[^\\d.]", "");
            return Float.parseFloat(cleanPrice);
        } catch (Exception e) {
            return 0f;
        }
    }

    /**
     * Returns the price increased by 10%.
     * Useful for plotting an upper bound/ceiling on a graph.
     */
    public float getPricePlusTen() {
        float basePrice = getPriceValue();
        return basePrice + (basePrice * 0.10f);
    }

    /**
     * Returns the price decreased by 10%.
     * Useful for plotting a lower bound/floor on a graph.
     */
    public float getPriceMinusTen() {
        float basePrice = getPriceValue();
        return basePrice - (basePrice * 0.10f);
    }

    /**
     * Returns a localized chart title based on the crop name.
     * Covers all crops seen in the selection menu.
     */
    public String getLocalizedChartTitle(boolean isHindi) {
        if (!isHindi) {
            return crop + " Price Chart";
        }

        String hindiCrop = crop;
        if (crop == null) return "फसल मूल्य चार्ट";

        switch (crop.toLowerCase()) {
            case "wheat": hindiCrop = "गेहूँ"; break;
            case "rice": hindiCrop = "धान"; break;
            case "maize": hindiCrop = "मक्का"; break;
            case "barley": hindiCrop = "जौ"; break;
            case "potato": hindiCrop = "आलू"; break;
            case "onion": hindiCrop = "प्याज"; break;
            case "tomato": hindiCrop = "टमाटर"; break;
            case "moong": hindiCrop = "मूंग"; break;
            case "chana": hindiCrop = "चना"; break;
            case "masoor": hindiCrop = "मसूर"; break;
            case "arhar": hindiCrop = "अरहर"; break;
            case "urad": hindiCrop = "उड़द"; break;
            case "apple": hindiCrop = "सेब"; break;
            case "banana": hindiCrop = "केला"; break;
            case "grapes": hindiCrop = "अंगूर"; break;
            default: hindiCrop = crop; break;
        }
        return hindiCrop + " मूल्य चार्ट";
    }

    /**
     * Returns localized legend for the trend graph.
     */
    public String getLocalizedTrendLabel(boolean isHindi) {
        return isHindi ? "अगले 7 दिनों का अनुमानित मूल्य रुझान" : "Next 7 Days Simulated Price Trend";
    }

    /**
     * Returns localized error message for API limits (429 Error seen in images).
     */
    public String getLocalizedErrorMessage(int errorCode, boolean isHindi) {
        if (errorCode == 429) {
            return isHindi ? "त्रुटि: बहुत अधिक अनुरोध (कृपया बाद में प्रयास करें)" : "Error: 429 Too Many Requests";
        }
        return isHindi ? "डेटा प्राप्त करने में त्रुटि" : "Error fetching data";
    }
}