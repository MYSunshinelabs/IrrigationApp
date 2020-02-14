package com.ve.irrigation.irrigation;

import android.content.Context;

import com.ve.irrigation.datavalues.AppDataBase;
import com.ve.irrigation.datavalues.ConfiguratonModel;
import com.ve.irrigation.datavalues.ConnectionSourceData;
import com.ve.irrigation.myretrofit.MyRestClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfigurationPresenter implements ConfigurationContractor.ConfigurationPresenter {

    ConfigurationContractor.ConfiguratioView configuratioView;

    public ConfigurationPresenter(ConfigurationContractor.ConfiguratioView configuratioView) {
        this.configuratioView = configuratioView;
    }

    @Override
    public void requestServer() {
        new MyRestClient().getRetrofitService().getConfiguarationData().enqueue(new Callback<ConfiguratonModel>() {
            @Override
            public void onResponse(Call<ConfiguratonModel> call, Response<ConfiguratonModel> response) { configuratioView.returnUpdate(response.body());}
            @Override
            public void onFailure(Call<ConfiguratonModel> call, Throwable t) {}
        });
    }


    @Override
    public void saveConnectionData(final Context context, ConfiguratonModel configuratonModel) {

        final List<ConnectionSourceData> connectionSourceDataList = new ArrayList<>();

        for (ConfiguratonModel.Hotspot hotspot : configuratonModel.getConnection().getHotspot()) {
            final ConnectionSourceData connectionSourceData = new ConnectionSourceData();
            connectionSourceData.setName(hotspot.getName());
            connectionSourceData.setSSID(hotspot.getSsid());
            connectionSourceData.setPassword(hotspot.getPassword());
            connectionSourceData.setUrl("");
            connectionSourceData.setType("hotspot");
            connectionSourceDataList.add(connectionSourceData);
        }

        for (ConfiguratonModel.Url url : configuratonModel.getConnection().getUrl()) {
            final ConnectionSourceData connectionSourceData = new ConnectionSourceData();
            connectionSourceData.setName(url.getName());
            connectionSourceData.setSSID("");
            connectionSourceData.setPassword("");
            connectionSourceData.setUrl(url.getUrl());
            connectionSourceData.setType("url");
            connectionSourceDataList.add(connectionSourceData);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDataBase.getAppDataBase(context).connectionSourceDAO().insert(connectionSourceDataList.toArray(new ConnectionSourceData[connectionSourceDataList.size()]));
            }
        }).start();

        configuratioView.returnUpdateForNextScreen();
    }

}
