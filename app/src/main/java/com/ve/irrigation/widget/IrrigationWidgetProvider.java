package com.ve.irrigation.widget;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.ve.irrigation.datavalues.HeartBeat;
import com.ve.irrigation.irrigation.BuildConfig;
import com.ve.irrigation.irrigation.MakeHttpRequest;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.activities.ConfigActivity;
import com.ve.irrigation.irrigation.listners.WifiObserver;
import com.ve.irrigation.utils.Constants;
import com.ve.irrigation.utils.HBWidgetTask;
import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;
import com.ve.irrigation.utils.WifiHelper;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import static com.ve.irrigation.utils.WifiHelper.getCurrentSsid;

public class IrrigationWidgetProvider extends AppWidgetProvider {
    private static final String CLICK_UP= "action_up";
    private static final String CLICK_DOWN= "action_down";
    private static final String CLICK_DIS= "action_disconect";
    private static final String CLICK_DIS24= "action_disconnect_24";
    private static final String CLICK_WIFI= "action_wifi";
    public static final String ACTION_WIFI_ALARAM= "action_alaram";
    private static final String TAG = IrrigationWidgetProvider.class.getSimpleName();
    public static int watchIndiState=0;
    public static boolean isLanguageChange=false;
    private static final int WATCH_IDLE=101;
    private static final int WATCH_IN_PROGRESS=102;
    private static final int WATCH_ERROR=103;
    private static final int WATCH_INACTIVE_AWAKE=104;
    public static final int WIFI_STATUS_CONNECT=201;
    public static final int WIFI_STATUS_DISCONNECTED=202;
    public static final int WIFI_STATUS_ERROR=203;

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        boolean isUiUpdateNeeded = false;
        String currentSsid;
        Utils.printLog(TAG,"onReceive "+intent.getAction());
        if(intent.getAction()!=null)
            switch (intent.getAction()){

                case CLICK_WIFI:
                    currentSsid=getCurrentSsid(context);
                    if(Preferences.getLocalNetSSID(context).equals(currentSsid)){
                        WifiHelper.turnOffWifi(context);
                        Utils.stopWidgetHBTask(context);
                        Preferences.setWifiStatus(context,WIFI_STATUS_DISCONNECTED);
                        isUiUpdateNeeded=true;
                    }else {
                        String ssid=Preferences.getLocalNetSSID(context);
                        if(ssid.length()<=0) {
                            ssid = "sun00";
                            WifiHelper.retryWithSecoundory=true;
                        }
                        WifiHelper.connectToRetry(context, ssid, Preferences.getLocalNetPass(context));
                    }
                    break;

                case CLICK_UP:
                    String urlVolUp="http://"+Preferences.getHostIpAddress(context)+":"+ Constants.Connection.COMMAND_PORT_NO+"/R/volup/20";
                    reflectOnChanges(urlVolUp);
                    if(!HBWidgetTask.isListen )
                        Utils.startWidgetHBTask(context);
                    break;

                case CLICK_DOWN:
                    String urlVolDwn="http://"+Preferences.getHostIpAddress(context)+":"+ Constants.Connection.COMMAND_PORT_NO+"/R/voldn/20";
                    reflectOnChanges(urlVolDwn);

                    if(!HBWidgetTask.isListen )
                        Utils.startWidgetHBTask(context);
                    break;

                case CLICK_DIS:
                    int staus=Preferences.getDisForeverStatus(context);
                    if(staus==0)
                        staus=1;
                    else
                        staus=0;
                    Preferences.setDisForeverStatus(context,staus);
                    String urlDis="http://"+Preferences.getHostIpAddress(context)+":"+ Constants.Connection.COMMAND_PORT_NO+"/R/disxx/"+staus;
                    reflectOnChanges(urlDis);
                    isUiUpdateNeeded=true;
                    break;

                case CLICK_DIS24:

                    int staus24=Preferences.getDis24HStatus(context);
                    if(staus24==0)
                        staus24=1;
                    else
                        staus24=0;
                    Preferences.setDis24HStatus(context,staus24);
                    String urlDis24="http://"+Preferences.getHostIpAddress(context)+":"+ Constants.Connection.COMMAND_PORT_NO+"/R/dis24/"+staus24;
                    reflectOnChanges(urlDis24);
                    isUiUpdateNeeded=true;
                    break;

                case ACTION_WIFI_ALARAM:

                    if(Preferences.getLastConnectTryTime(context)<10)
                        return;

                    isUiUpdateNeeded=true;
                    Utils.printLog("ACTION_WIFI_ALARAM", System.currentTimeMillis()+"");
                    currentSsid= getCurrentSsid(context);

                    if(Preferences.getLocalNetSSID(context).equalsIgnoreCase(currentSsid)) {
                        Utils.startWidgetHBTask(context);
                        WifiObserver.clearUpdate(context);
                        Preferences.setWifiStatus(context,WIFI_STATUS_CONNECT);
                    }else {
                        WifiHelper.connectToRetry(context, Preferences.getLocalNetSSID(context), Preferences.getLocalNetPass(context));
                        Preferences.setWifiStatus(context,WIFI_STATUS_DISCONNECTED);
                        Preferences.setLastConnectTryTime(context, System.currentTimeMillis());
                    }

                    break;
            }
        else
            isUiUpdateNeeded=true;

        if (isUiUpdateNeeded)
            onUpdate(context, AppWidgetManager.getInstance(context), AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, IrrigationWidgetProvider.class)));
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Utils.printLog(TAG,"onUpdate");
        if(Preferences.getLastConnectTryTime(context)>10) {
            String ssid=Preferences.getLocalNetSSID(context);
            WifiHelper.connectToRetry(context,ssid, Preferences.getLocalNetPass(context));
            Preferences.setLastConnectTryTime(context, System.currentTimeMillis());
        }else if(Preferences.getLocalNetSSID(context).equals(getCurrentSsid(context))) {
            Preferences.setWifiStatus(context,IrrigationWidgetProvider.WIFI_STATUS_CONNECT);
        }

        if(!HBWidgetTask.isListen )
            Utils.startWidgetHBTask(context);

        ComponentName thisWidget = new ComponentName(context, IrrigationWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_widget);

        if(isLanguageChange)
            toggleLanguage(context,remoteViews);

        remoteViews.setOnClickPendingIntent(R.id.imgWifi, getPendingSelfIntent(context, CLICK_WIFI));
        remoteViews.setOnClickPendingIntent(R.id.txtUP, getPendingSelfIntent(context, CLICK_UP));
        remoteViews.setOnClickPendingIntent(R.id.txtDown, getPendingSelfIntent(context, CLICK_DOWN));
        remoteViews.setOnClickPendingIntent(R.id.txtDis24, getPendingSelfIntent(context, CLICK_DIS24));
        remoteViews.setOnClickPendingIntent(R.id.txtDis, getPendingSelfIntent(context, CLICK_DIS));

        Intent intent = new Intent(context, ConfigActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        remoteViews.setOnClickPendingIntent(R.id.txtConfig, pendingIntent);

        int radioOn = context.getResources().getIdentifier("bg_radio_on", "drawable", context.getPackageName());
        int radioOff = context.getResources().getIdentifier("bg_radio_off", "drawable", context.getPackageName());
        int radioActiveIdle= context.getResources().getIdentifier("bg_radio_active_idle", "drawable", context.getPackageName());
        int radioInactiveAwake= context.getResources().getIdentifier("bg_radio_inactive_awake", "drawable", context.getPackageName());

        setWatchIndicator(remoteViews,radioOn,radioOff,radioActiveIdle,radioInactiveAwake);

        if (Preferences.getDis24HStatus(context)<=0) {
            remoteViews.setInt(R.id.txtDis24, "setBackgroundResource", R.color.backgroundManualMode);
            Utils.stopWidgetHBTask(context);
        } else {
            remoteViews.setInt(R.id.txtDis24, "setBackgroundResource", R.color.backgroundGray);
            if(!HBWidgetTask.isListen)
                Utils.startWidgetHBTask(context);
        }

        if (Preferences.getDisForeverStatus(context)<=0) {
            remoteViews.setInt(R.id.txtDis, "setBackgroundResource", R.color.backgroundManualMode);
            Utils.stopWidgetHBTask(context);
        } else {
            remoteViews.setInt(R.id.txtDis, "setBackgroundResource", R.color.backgroundGray);
            if(!HBWidgetTask.isListen)
                Utils.startWidgetHBTask(context);
        }

        if(Preferences.getDisForeverStatus(context)>0 || Preferences.getDis24HStatus(context)>0) {
            if (!HBWidgetTask.isListen)
                Utils.startWidgetHBTask(context);
        }else
            Utils.startWidgetHBTask(context);

        switch (Preferences.getWifiStatus(context)){
            case WIFI_STATUS_CONNECT:
                remoteViews.setImageViewResource(R.id.imgWifiIndi,radioOn);
                break;
            case WIFI_STATUS_DISCONNECTED:
                remoteViews.setImageViewResource(R.id.imgWifiIndi,radioActiveIdle);
                break;
            case WIFI_STATUS_ERROR:
                remoteViews.setImageViewResource(R.id.imgWifiIndi,radioOff);
                break;
        }

        HeartBeat heartBeat = Preferences.getHeartBeat(context);

        if(heartBeat!=null && heartBeat.getEntity().equals("M0001") ) {
            remoteViews.setTextViewText(R.id.txtActVol, heartBeat.getMetric().getAvol()+"");
            remoteViews.setTextViewText(R.id.txtReqVol, heartBeat.getMetric().getRvol()+"");
            remoteViews.setTextViewText(R.id.txtPressValue, heartBeat.getMetric().getPpres()+"");
            remoteViews.setTextViewText(R.id.txtLPM, heartBeat.getMetric().getPflow()+"");
            remoteViews.setTextViewText(R.id.txtGrp, heartBeat.getMetric().getGnr()+"");
            remoteViews.setTextViewText(R.id.txtStartTime, Utils.getTime(Integer.parseInt(heartBeat.getMetric().getStime())));
            remoteViews.setTextViewText(R.id.txtDuration, heartBeat.getMetric().getSlen()+" Min");
            remoteViews.setTextViewText(R.id.txtVolume, heartBeat.getMetric().getSvol()+"L");
        }
        appWidgetManager.updateAppWidget(allWidgetIds, remoteViews);
    }


    private void toggleLanguage(Context context, RemoteViews remoteViews) {
        isLanguageChange=false;
        String lang=Preferences.getLanguage(context);
        if(lang.equals("en")) {
            remoteViews.setTextViewText(R.id.txtAct, "Actual(L)");
            remoteViews.setTextViewText(R.id.txtReq, "Required(L)");
            remoteViews.setTextViewText(R.id.txtUP, "UP");
            remoteViews.setTextViewText(R.id.txtDown, "DOWN");
            remoteViews.setTextViewText(R.id.txtHintLPM, "Flow lpm");
            remoteViews.setTextViewText(R.id.txtHintPressBar, "Pressure Bar");
            remoteViews.setTextViewText(R.id.txtConfig, "Config");
            remoteViews.setTextViewText(R.id.txtDis24, "DIS 24");
            remoteViews.setTextViewText(R.id.txtDis, "DIS");
            remoteViews.setTextViewText(R.id.txtVer, "ver\n"+BuildConfig.VERSION_NAME);
        }else{
            remoteViews.setTextViewText(R.id.txtAct, "实际流量L");
            remoteViews.setTextViewText(R.id.txtReq, "需水量L");
            remoteViews.setTextViewText(R.id.txtUP, "加");
            remoteViews.setTextViewText(R.id.txtDown, "减");
            remoteViews.setTextViewText(R.id.txtHintLPM, "流量L/M");
            remoteViews.setTextViewText(R.id.txtHintPressBar, "压力Bar");
            remoteViews.setTextViewText(R.id.txtConfig, "网络配置");
            remoteViews.setTextViewText(R.id.txtDis24, "关24H");
            remoteViews.setTextViewText(R.id.txtDis, "关");
            remoteViews.setTextViewText(R.id.txtVer, "版本\n"+BuildConfig.VERSION_NAME);
        }
    }


    private void setWatchIndicator(RemoteViews remoteViews, int radioInProgress, int radioError, int radioActiveIdle, int radioInactiveAwake){
        int state;
        switch (watchIndiState){
            case WATCH_IDLE:
                state=radioActiveIdle;
                break;
            case WATCH_IN_PROGRESS:
                state=radioInProgress;
                break;
            case WATCH_INACTIVE_AWAKE:
                state=radioInactiveAwake;
                break;
            case WATCH_ERROR:
                state=radioError;
                break;
            default:
                state=radioActiveIdle;
        }
//        remoteViews.setImageViewResource(R.id.imgWatchIndi,state);
        remoteViews.setInt(R.id.txtWatchIndi, "setBackgroundResource",state);
    }

    public static void updateWidget(Context context){
        Intent intent = new Intent(context, IrrigationWidgetProvider.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        // Use an array and EXTRA_APPWIDGET_IDS instead of AppWidgetManager.EXTRA_APPWIDGET_ID,
        // since it seems the onUpdate() is only fired on that:
        int[] ids = AppWidgetManager.getInstance(context.getApplicationContext()).getAppWidgetIds(new ComponentName(context, IrrigationWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

    private void reflectOnChanges(String url){
        Utils.printLog(TAG,"reflectOnChanges "+url);
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                if(s.length()>0)
                    Utils.printLog(TAG,s);
            }
        });
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Utils.printLog(TAG,"onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Utils.printLog(TAG,"onDisabled");

    }
}
