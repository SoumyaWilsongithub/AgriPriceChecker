package com.example.agripricechecker;

import com.google.gson.annotations.SerializedName;

public class MandiRecord {
    @SerializedName("commodity")
    public String commodity;
    @SerializedName("market")
    public String market;
    @SerializedName("modal_price")
    public String modal_price;
    @SerializedName("arrival_date")
    public String arrival_date;
}
