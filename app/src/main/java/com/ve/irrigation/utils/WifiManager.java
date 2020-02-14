package com.ve.irrigation.utils;

import android.content.Context;
import android.net.wifi.WifiConfiguration;

import com.ve.irrigation.datavalues.Wifi;

import static android.content.Context.WIFI_SERVICE;

public class WifiManager {

    public static void connectTo(Context context,Wifi wifi){
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", wifi.getSsid());
        wifiConfig.preSharedKey = String.format("\"%s\"", wifi.getPassword());
        android.net.wifi.WifiManager wifiManager = (android.net.wifi.WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);

        if(!wifiManager.isWifiEnabled())// Turn on wifi if not avilivable
            wifiManager.setWifiEnabled(true);

        int netId = wifiManager.addNetwork(wifiConfig);
//        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }
}
