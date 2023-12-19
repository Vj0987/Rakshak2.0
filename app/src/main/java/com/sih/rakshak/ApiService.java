package com.sih.rakshak;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {
    @GET("sha")
    Call<String> getSHAKey(@Query("key") String shaKey);
}
