package com.ve.irrigation.myretrofit;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyRestClient {

    Retrofit retrofit = new Retrofit.Builder().baseUrl("http://www.sunshinelabs.nl").addConverterFactory(GsonConverterFactory.create()).build();

    public MyRetrofitService getRetrofitService() {
        return retrofit.create(MyRetrofitService.class);
    }
}
