package com.ve.irrigation.myretrofit;

import com.ve.irrigation.datavalues.ConfiguratonModel;

import retrofit2.Call;
import retrofit2.http.GET;

public interface MyRetrofitService {

    @GET("/apk/docfile.js")
    Call<ConfiguratonModel> getConfiguarationData();
}
