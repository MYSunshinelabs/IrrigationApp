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

public class EspNetCommMsgReceiver extends BroadcastReceiver {

    public static final String TAG=EspNetCommMsgReceiver.class.getSimpleName();
    public static final String ACTION_ESP_RESPONSE="action_esp_net_communication_msg";
    private EspNetCommMsgObserver espNetCommMsgObserver;

    public EspNetCommMsgReceiver(EspNetCommMsgObserver espNetCommMsgObserver) {
        this.espNetCommMsgObserver = espNetCommMsgObserver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String response=intent.getExtras().getString("esp_net_comm_msg");
        Log.d(TAG+" onReceive",response);
        espNetCommMsgObserver.onEspNetCommMsg(response);
    }

    public interface EspNetCommMsgObserver{
        void onEspNetCommMsg(String response);
    }

    public static EspNetCommMsgReceiver registerReceiver(EspNetCommMsgObserver espNetCommMsgObserver, Context context){
        EspNetCommMsgReceiver receiver=new EspNetCommMsgReceiver(espNetCommMsgObserver);
        context.registerReceiver(receiver,new IntentFilter(ACTION_ESP_RESPONSE));
        return receiver;
    }

    public static void unregisterReceiver(EspNetCommMsgReceiver receiver, Context context){
        try{
            context.unregisterReceiver(receiver);
        }catch (Exception e){
            Utils.printLog(TAG,e.getMessage());
        }
    }

    public static void sendBroadcast(Context context,String response){
        Intent intent=new Intent(ACTION_ESP_RESPONSE);
        intent.putExtra("esp_net_comm_msg",response);
        context.sendBroadcast(intent);
    }
}
