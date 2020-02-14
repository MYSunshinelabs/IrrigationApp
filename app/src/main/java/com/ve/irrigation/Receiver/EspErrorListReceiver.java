package com.ve.irrigation.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import com.ve.irrigation.utils.Utils;

import java.util.ArrayList;

/**
 * Created by dalvendrakumar on 3/12/18.
 */

public class EspErrorListReceiver extends BroadcastReceiver {

    public static final String TAG=EspErrorListReceiver.class.getSimpleName();
    public static final String ACTION_ESP_ERROR_MSG ="action_esp_error_msg";
    private EspErrorMsgObserver espErrorObserver;
    public static ArrayList<String> espErrorList=new ArrayList<>();

    public EspErrorListReceiver(EspErrorMsgObserver espErrorObserver) {
        this.espErrorObserver = espErrorObserver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String response=intent.getExtras().getString("esp_error_msg");
        Log.d(TAG+" onReceive",response);
        espErrorObserver.onEspErrorUpdate(response);
    }

    public interface EspErrorMsgObserver{
        void onEspErrorUpdate(String response);
    }

    public static EspErrorListReceiver registerReceiver(EspErrorMsgObserver espResponseObserver, Context context){
        EspErrorListReceiver receiver=new EspErrorListReceiver(espResponseObserver);
        context.registerReceiver(receiver,new IntentFilter(ACTION_ESP_ERROR_MSG));
        return receiver;
    }

    public static void unregisterReceiver(EspErrorListReceiver receiver, Context context){
        try{
            context.unregisterReceiver(receiver);
        }catch (Exception e){
            Utils.printLog(TAG,e.getMessage());
        }
    }

    public static void sendBroadcast(Context context,String errorMsg){
        Utils.printLog(TAG,"sendBroadcast "+errorMsg);

        if(espErrorList.size()>4) {
            espErrorList.add(0,errorMsg);
            espErrorList.remove(5);
        }else
            espErrorList.add(0,errorMsg);

        Intent intent=new Intent(ACTION_ESP_ERROR_MSG);
        intent.putExtra("esp_error_msg",errorMsg);
        context.sendBroadcast(intent);
    }
}
