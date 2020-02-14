package com.ve.irrigation.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.ve.irrigation.utils.Utils;

/**
 * Created by dalvendrakumar on 3/12/18.
 */

public class EspResponseReceiver extends BroadcastReceiver {

    public static final String TAG=EspResponseReceiver.class.getSimpleName();
    public static final String ACTION_ESP_RESPONSE="action_esp_response";
    private EspResponseObserver espResponseObserver;

    public EspResponseReceiver(EspResponseObserver espResponseObserver) {
        this.espResponseObserver = espResponseObserver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String response=intent.getExtras().getString("esp_response");
        Log.d(TAG+" onReceive",response);
        espResponseObserver.onEspResponse(response);
    }

    public interface EspResponseObserver{
        void onEspResponse(String response);
    }

    public static EspResponseReceiver registerReceiver(EspResponseObserver espResponseObserver,Context context){
        EspResponseReceiver receiver=new EspResponseReceiver(espResponseObserver);
        context.registerReceiver(receiver,new IntentFilter(ACTION_ESP_RESPONSE));
        return receiver;
    }

    public static void unregisterReceiver(EspResponseReceiver receiver,Context context){
        try{
            context.unregisterReceiver(receiver);
        }catch (Exception e){
            Utils.printLog(TAG,e.getMessage());
        }
    }

    public static void sendBroadcast(Context context,String response){
        Intent intent=new Intent(ACTION_ESP_RESPONSE);
        intent.putExtra("esp_response",response);
        context.sendBroadcast(intent);
    }
}
