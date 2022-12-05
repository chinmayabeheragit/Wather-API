package com.example.weatherapp;


import com.example.weatherapp.Models.ChinuData;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface InterfaceAPI {

    @GET("weather")
    Call<ChinuData> getData(
            @Query("q") String city,
            @Query("appid") String API_Key,
            @Query("units") String units
    );

}