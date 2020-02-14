package com.ve.irrigation.irrigation.listners;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;
import com.ve.irrigation.utils.WifiHelper;
import com.ve.irrigation.widget.IrrigationWidgetProvider;

public class WifiObserver extends BroadcastReceiver {
    private static final String TAG = WifiObserver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Utils.printLog(TAG,"onReceive");
        if(!WifiHelper.getCurrentSsid(context).equalsIgnoreCase(Preferences.getLocalNetSSID(context))){
            WifiHelper.connectToRetry(context, Preferences.getLocalNetSSID(context),Preferences.getLocalNetPass(context));
        }else
            scheduleUpdate(context);
    }

    public static void scheduleUpdate(Context context) {
        int interval=10;//min
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        long intervalMillis = interval*60*60*1000;
//        long intervalMillis = 1*60*1000;

        PendingIntent pi = getAlarmIntent(context);
        am.cancel(pi);
        am.setInexactRepeating(AlarmManager.RTC, System.currentTimeMillis(), intervalMillis, pi);
    }

    private static PendingIntent getAlarmIntent(Context context) {
        Intent intent = new Intent(context, IrrigationWidgetProvider.class);
        intent.setAction(IrrigationWidgetProvider.ACTION_WIFI_ALARAM);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        return pi;
    }

    public static void clearUpdate(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.cancel(getAlarmIntent(context));
    }
}
