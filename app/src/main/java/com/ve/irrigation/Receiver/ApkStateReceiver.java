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

public class ApkStateReceiver extends BroadcastReceiver {
    public static final String TAG=ApkStateReceiver.class.getSimpleName();
    public static final String ACTION_APK_STATE="action_on_apk_state_change";
    private ApkStateObserver espResponseObserver;

    public ApkStateReceiver(ApkStateObserver espResponseObserver) {
        this.espResponseObserver = espResponseObserver;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG+" onReceive"," apk_state ");
        boolean response=intent.getExtras().getBoolean("apk_state");
        espResponseObserver.onApkState(response);
    }

    public interface ApkStateObserver {
        void onApkState(boolean isActive);
    }

    public static ApkStateReceiver registerReceiver(ApkStateObserver espResponseObserver, Context context){
        Log.d(TAG+" registerReceiver",context.toString());
        ApkStateReceiver receiver=new ApkStateReceiver(espResponseObserver);
        context.registerReceiver(receiver,new IntentFilter(ACTION_APK_STATE));
        return receiver;
    }

    public static void unregisterReceiver(ApkStateReceiver receiver, Context context){
        try{
            Log.d(TAG," unregisterReceiver");
            context.unregisterReceiver(receiver);
        }catch (Exception e){
            Utils.printLog(TAG,e.getMessage());
        }
    }

    public static void sendBroadcast(Context context,Boolean isActive){
        Log.d(TAG+" sendBroadcast",isActive+"");
        Intent intent=new Intent(ACTION_APK_STATE);
        intent.putExtra("apk_state",isActive);
        context.sendBroadcast(intent);
    }
}
