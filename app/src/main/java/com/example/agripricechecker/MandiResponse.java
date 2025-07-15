package com.example.agripricechecker;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MandiResponse {
    @SerializedName("records")
    public List<MandiRecord> records;
}
