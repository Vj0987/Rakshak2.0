package com.sih.rakshak;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService2 {
    @GET("sha")
    Call<String> getURL(@Query("url") String shaKey);
}
