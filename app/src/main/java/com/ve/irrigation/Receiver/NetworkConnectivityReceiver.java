package com.ve.irrigation.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;


public class NetworkConnectivityReceiver extends BroadcastReceiver {
    private static final String ACTION_CONNECTIVITY_CHANGE="android.net.conn.CONNECTIVITY_CHANGE";
    private static final String TAG = NetworkConnectivityReceiver.class.getSimpleName();
    private final ConnectivityChangeObserver connectivityChangeObserver;
    private static final String ACTION_WIFI_ON = "android.intent.action.WIFI_ON";
    private static final String ACTION_WIFI_OFF = "android.intent.action.WIFI_OFF";
    private static final String ACTION_CONNECT_TO_WIFI = "android.intent.action.CONNECT_TO_WIFI";

    public NetworkConnectivityReceiver(ConnectivityChangeObserver connectivityChangeObserver) {
        this.connectivityChangeObserver = connectivityChangeObserver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        try {

            boolean isConnected = false;

            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                isConnected = (!intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false));
            }

            connectivityChangeObserver.onConnectivityChange(isConnected);
            NetworkInfo networkInfo = (NetworkInfo) intent.getExtras().get("networkInfo");
            String name = intent.getExtras().getString("extraInfo");
              name = name.replaceAll("\"|\"", "");
            Preferences.setCurrentNetworkName(context, name);
            Utils.printLog(TAG, "onReceive=====>>> Connected " + isConnected +" " + name);
        }catch (Exception e){
            e.printStackTrace();
            Preferences.setCurrentNetworkName(context,"");
        }
    }


    public interface ConnectivityChangeObserver {
        void onConnectivityChange(boolean isConnected);
    }

    public static NetworkConnectivityReceiver registerNetworkReciver(NetworkConnectivityReceiver.ConnectivityChangeObserver observer, Context context) {
        NetworkConnectivityReceiver cartChangeReceiver = new NetworkConnectivityReceiver(observer);
        context.registerReceiver(cartChangeReceiver, new IntentFilter(ACTION_CONNECTIVITY_CHANGE));
        return cartChangeReceiver;
    }

    public static void unregisterReceiver(NetworkConnectivityReceiver receiver,Context context){
        try{
            context.unregisterReceiver(receiver);
        }catch (Exception e){
            Utils.printLog(TAG,e.getMessage());
        }
    }
}