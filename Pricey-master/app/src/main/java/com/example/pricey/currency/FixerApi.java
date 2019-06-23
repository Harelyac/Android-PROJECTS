package com.example.pricey.currency;


import retrofit2.Call;
import retrofit2.http.GET;

public interface FixerApi {

    @GET("api/latest?access_key=d4fa53a5bb8f9eccdedcd42d647de093&symbols=ILS,EUR,USD,AUD,CAD,GBP,JPY,RUB")
    Call<Response> getResponse();
}