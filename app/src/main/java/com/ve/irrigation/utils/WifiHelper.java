package com.ve.irrigation.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;


import com.ve.irrigation.datavalues.Wifi;
import com.ve.irrigation.irrigation.listners.WifiObserver;
import com.ve.irrigation.widget.IrrigationWidgetProvider;

import java.util.List;

import static android.content.Context.WIFI_SERVICE;
import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class WifiHelper {

    private static final String TAG=WifiHelper.class.getSimpleName();

    public static void connectTo(Context context, Wifi wifi){connectTo(context,wifi.getSsid(),wifi.getPassword());}

    public static void connectTo(Context context,String ssid,String password){
        if(ssid.equals(Preferences.getCurrentNetworkName(context)))
            return;

        turnOffHotSpot(context);

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", password);
        //      For WEP connection
//        wifiConfig.wepKeys[0] = String.format("\"%s\"", wifi.getPassword());
//        wifiConfig.wepTxKeyIndex = 0;
//        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiConfig.priority = getHighestPriority(wifiManager)+1;

        int netId = wifiManager.addNetwork(wifiConfig);

        if(!wifiManager.isWifiEnabled())// Turn on wifi if not avilivable
            wifiManager.setWifiEnabled(true);

        disconnectCurrentNetwork(wifiManager);

        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        Preferences.setHotSpotRoomEnable(context,false);
        Utils.printLog(TAG, "Tring to connect with "+ssid);

    }

    public static boolean retryWithSecoundory=false;
    public static void connectToRetry(final Context context, final String ssid, final String pass){

        String currentSsid=getCurrentSsid(context);

        if(ssid.equals(currentSsid)) {
            Preferences.setWifiStatus(context, IrrigationWidgetProvider.WIFI_STATUS_CONNECT);
            WifiObserver.clearUpdate(context);
            return;
        }

        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
        wifiConfig.preSharedKey = String.format("\"%s\"", pass);
        //      For WEP connection
        wifiConfig.wepKeys[0] = String.format("\"%s\"", pass);
        wifiConfig.wepTxKeyIndex = 0;
        wifiConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        wifiConfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiConfig.priority = getHighestPriority(wifiManager)+1;

        int netId = wifiManager.addNetwork(wifiConfig);

        if(!wifiManager.isWifiEnabled())// Turn on wifi if not avilivable
            wifiManager.setWifiEnabled(true);

        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                String currentSsid=getCurrentSsid(context);
                if(ssid.equalsIgnoreCase(currentSsid)) {
                    Utils.startWidgetHBTask(context);
                    WifiObserver.clearUpdate(context);
                    Preferences.setWifiStatus(context,IrrigationWidgetProvider.WIFI_STATUS_CONNECT);
                }else if(!isWifiAvaliable(ssid,context)) {
                    if(retryWithSecoundory){
                        String newSsid=ssid.equals("sun00")?"sun01":"sun00";
                        connectToRetry(context,newSsid,pass);
                        retryWithSecoundory=false;
                    }else
                        WifiObserver.scheduleUpdate(context);
                }else {
                    WifiObserver.scheduleUpdate(context);
                    Preferences.setWifiStatus(context,IrrigationWidgetProvider.WIFI_STATUS_DISCONNECTED);
                }
                IrrigationWidgetProvider.updateWidget(context);
            }
        },10*1000);
        Utils.printLog(TAG, "Tring to connect with "+ssid);
    }
    public static String getCurrentSsid(Context context) {
        String ssid = null;
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo.isConnected()) {
            final WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            final WifiInfo connectionInfo = wifiManager.getConnectionInfo();
            if (connectionInfo != null && !TextUtils.isEmpty(connectionInfo.getSSID())) {
                ssid = connectionInfo.getSSID();
            }
        }
        try {
            ssid=ssid.replaceAll("\"|\"", "");
        }catch (Exception e){
            Utils.printLog(TAG+" getWifiSSID",e.toString());
        }
        return ssid;
    }

    public static boolean turnOffWifi(Context context){
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        if(wifiManager != null ){
            if(wifiManager.isWifiEnabled()) {// Turn on wifi if not avilivable
                wifiManager.setWifiEnabled(false);
                Preferences.setWifiStatus(context,IrrigationWidgetProvider.WIFI_STATUS_DISCONNECTED);
            }
        }
        return false;
    }
    public static boolean isWifiAvaliable(String ssid,Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
//        WifiInfo info = wifiManager.getConnectionInfo();
        final List<ScanResult> results = wifiManager.getScanResults();
        if (results != null) {
            for (int i = 0; i < results.size(); i++) {
                Utils.printLog("Wifi Avaliable",results.get(i).SSID);
                if(ssid.equals(results.get(i).SSID)) {
                    Utils.printLog("Wifi Founded ==",results.get(i).SSID);
                    Preferences.setWifiStatus(context,IrrigationWidgetProvider.WIFI_STATUS_DISCONNECTED);
                    return true;
                }
            }
        }
        Preferences.setWifiStatus(context,IrrigationWidgetProvider.WIFI_STATUS_ERROR);
        return false;
    }
    private static int getHighestPriority(WifiManager wifiManager) {
        int priority=0;
        try {
            List<WifiConfiguration> wifiConfigurations = wifiManager.getConfiguredNetworks();
            for (WifiConfiguration configuration : wifiConfigurations) {
                if (configuration.priority > priority)
                    priority = configuration.priority;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return priority;
    }

    public static boolean disconnectCurrentNetwork(WifiManager wifiManager){
        if(wifiManager != null && wifiManager.isWifiEnabled()){
            int netId = wifiManager.getConnectionInfo().getNetworkId();
            wifiManager.disableNetwork(netId);
            return wifiManager.disconnect();
        }
        return false;
    }

    public static void turnOffHotSpot(Context context) {
        try{
            ApManager ap = ApManager.newInstance(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ap.turnOffHotspot();
            }else {
                ap.turnWifiApOff();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static void turnOnHotSpot(Context context) {
        try{
            ApManager ap = ApManager.newInstance(context);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ap.turnOnHotspot();
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1) {
                if (Settings.System.canWrite(context)) {
                    ap.createNewNetwork("sun00","sun01234");
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    intent.setData(Uri.parse("package:" + context.getPackageName()));
                    intent.addFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }else {
                ap.createNewNetwork("sun00","sun01234");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getWifiSSID(Context context){
        WifiManager wifiManager= (WifiManager) context.getSystemService(WIFI_SERVICE);
        String ssid=wifiManager.getConnectionInfo().getSSID();
        try {
            ssid=ssid.replaceAll("\"|\"", "");
        }catch (Exception e){
            Utils.printLog(TAG+" getWifiSSID",e.toString());
        }
        return ssid;
    }

}

