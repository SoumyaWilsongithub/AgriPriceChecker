package com.example.agripricechecker;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("resource/9ef84268-d588-465a-a308-a864a43d0070")
    Call<MandiResponse> getMandiPrices(
            @Query("api-key") String apiKey,
            @Query("format") String format,
            @Query("limit") int limit,
            @Query("offset") int offset,
            @Query("filters[commodity]") String crop
    );
}
