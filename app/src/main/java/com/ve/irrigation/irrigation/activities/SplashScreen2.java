package com.ve.irrigation.irrigation.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;

import com.ve.irrigation.datavalues.ConfiguratonModel;
import com.ve.irrigation.irrigation.ConfigurationContractor;
import com.ve.irrigation.irrigation.ConfigurationPresenter;
import com.ve.irrigation.irrigation.R;

public class SplashScreen2 extends BaseActivity implements ConfigurationContractor.ConfiguratioView {

    ConfigurationPresenter mConfigurationPresenter;
    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen2);


        connectToWifi("Hello World","85858585");
        //startDownloading();
        returnUpdateForNextScreen();

    }

    private void connectToWifi(final String networkSSID, final String networkPassword) {

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);


        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = String.format("\"%s\"", networkSSID);
        conf.preSharedKey = String.format("\"%s\"", networkPassword);

        int netId = wifiManager.addNetwork(conf);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }


    private void sendToMainScreen() {
//        Intent intent = new Intent(this, MainActivityNew.class);
//        startActivity(intent);
    }


    private void startDownloading() {
        mConfigurationPresenter = new ConfigurationPresenter(SplashScreen2.this);
        mConfigurationPresenter.requestServer();
    }

    @Override
    public void returnUpdate(ConfiguratonModel response) {
        mConfigurationPresenter.saveConnectionData(this, response);
        sendToMainScreen();
        finish();

    }

    @Override
    public void returnUpdateForNextScreen() {
        mSharedPreferences = getSharedPreferences("myconfigdata", Context.MODE_PRIVATE);
        mEditor = mSharedPreferences.edit();
        mEditor.putBoolean("firstdownload", true);
        mEditor.commit();

        sendToMainScreen();
        finish();
    }


}
