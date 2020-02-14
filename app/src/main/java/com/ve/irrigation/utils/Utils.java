package com.ve.irrigation.utils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.ve.irrigation.datavalues.ConfiguratonModel;
import com.ve.irrigation.datavalues.ConnectionSourceData;
import com.ve.irrigation.datavalues.Group;
import com.ve.irrigation.datavalues.Valve;
import com.ve.irrigation.Receiver.ApkStateReceiver;
import com.ve.irrigation.Receiver.EspErrorListReceiver;
import com.ve.irrigation.irrigation.BuildConfig;
import com.ve.irrigation.irrigation.activities.SplashActivity;
import com.ve.irrigation.services.ListenESPBarodJobService;
import com.ve.irrigation.services.ListenESPBarodService;
import com.ve.irrigation.services.ListenESPBrdWidgetJS;
import com.ve.irrigation.services.ListenESPBrdWidgetSer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by dalvendrakumar on 5/12/18.
 */

public class Utils {

    public static ArrayList<String> getScheduleVolumeSlabs(){
        ArrayList<String> volumeSlabsList=new ArrayList<>();
        for (int i=Constants.Configration.MIN_VOLUME;i<=Constants.Configration.MAX_VOLUME;i++){
            volumeSlabsList.add(i+"Ltr");
        }
        return volumeSlabsList;
    }

    public static void setDeviceInfo(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Constants.DEVICE_HEIGHT = displayMetrics.heightPixels;
        Constants.DEVICE_HEIGHT = displayMetrics.widthPixels;
    }

    public static void startHeartBeatTask(Context context){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            HeartBeatTask.isListen=true;
            JobScheduler jobScheduler =(JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(new JobInfo.Builder(Constants.JobId.JOB_HEART_BEAT, new ComponentName(context, ListenESPBarodJobService.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build());
        }else {
            ListenESPBarodService.isListen=true;
            Intent intentEspService =new Intent(context, ListenESPBarodService.class);
            context.startService(intentEspService);
        }
    }

    public static void stopHeartBeatTask(Context context){
        ApkStateReceiver.sendBroadcast(context,false);
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            HeartBeatTask.isListen=false;
            JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE );
            jobScheduler.cancel(Constants.JobId.JOB_HEART_BEAT);
        }else {
            ListenESPBarodService.isListen = false;
            Intent intent = new Intent(context, ListenESPBarodService.class);
            context.stopService(intent);
        }
        HeartBeatTask.countACK=0;
        HeartBeatTask.countHB=0;
        EspErrorListReceiver.espErrorList.clear();

    }
    public static void startWidgetHBTask(Context context){
        if(HeartBeatTask.isListen)
            return;
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            HBWidgetTask.isListen=true;
            JobScheduler jobScheduler =(JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
            jobScheduler.schedule(new JobInfo.Builder(Constants.JobId.JOB_HEART_BEAT_WIDGET,
                    new ComponentName(context, ListenESPBrdWidgetJS.class))
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY).build());
//            Intent intent = new Intent(context, ListenESPBrdWidgetJS.class);
//            context.startForegroundService(intent);
        }else {
            ListenESPBrdWidgetSer.isListen=true;
            Intent intentEspService =new Intent(context, ListenESPBrdWidgetSer.class);
            context.startService(intentEspService);
        }
    }

    public static void stopWidgetHBTask(Context context){
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            HBWidgetTask.isListen=false;
            JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE );
            jobScheduler.cancel(Constants.JobId.JOB_HEART_BEAT);
        }else {
            ListenESPBrdWidgetSer.isListen = false;
            Intent intent = new Intent(context, ListenESPBrdWidgetSer.class);
            context.stopService(intent);
        }
        HBWidgetTask.countACK=0;
        HBWidgetTask.countHB=0;
    }


    public static void startLocationService(Context context) {
//        if(LocationHelper.getInstance(context).hasPermission() && LocationHelper.getInstance(context).checkPlayServices()){
//            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
//                JobScheduler jobScheduler =(JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
//                jobScheduler.schedule(new JobInfo.Builder(Constants.JobId.JOB_LOCATION,
//                        new ComponentName(context, LocationMonitoringJobService.class))
//                        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
//                        .build());
//            }else {
//                //Start location sharing service to app server.........
//                Intent intent = new Intent(context, LocationMonitoringService.class);
//                context.startService(intent);
//
//            }
//        }
    }

    public static void stopLocationService(Context context){
//        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
//            JobScheduler jobScheduler = (JobScheduler)context.getSystemService(Context.JOB_SCHEDULER_SERVICE );
//            jobScheduler.cancel(Constants.JobId.JOB_LOCATION);
//        }else {
//            LocationMonitoringService.isStopService=true;
//            Intent intent = new Intent(context, LocationMonitoringService.class);
//            context.stopService(intent);
//        }
    }


    public static ArrayList<Valve> getGroupValves(String groupValves, String groupId) {
        ArrayList<Valve> valveArrayList=new ArrayList<>();
        int indexPrev=-1;
        int currIndex;
        do{
            currIndex=groupValves.indexOf(groupId,indexPrev+1);
            if(currIndex==indexPrev)
                return valveArrayList;
            if(currIndex>=0){
                Valve valve=new Valve();
                valve.setValveId(currIndex+1);
                valve.setEnable(true);
                valve.setGroupId(Integer.parseInt(groupId));
                valveArrayList.add(valve);
            }
            indexPrev=currIndex;

        }while(currIndex>=0);
        return valveArrayList;
    }

    public static ArrayList<Group> getGroupValvesList(int noGroups,int noValve, JSONArray groupValves) {
        final ArrayList<Group> groupArrayList=new ArrayList<>();
        for(int i=0;i<noGroups;i++) {
            try {
                JSONArray arrayValves=groupValves.getJSONArray(i);
                Group group=new Group();
                group.groupName="Group "+(i+1);
                group.id=i+1;
                group.setValves(getGroupValves(arrayValves, noValve));
                groupArrayList.add(group);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return groupArrayList;
    }

    public static ArrayList<Valve> getGroupValves(JSONArray valves,int noValve) {
        ArrayList<Valve> valveArrayList=new ArrayList<>();

        for(int i=0;i<noValve;i++){
            Valve valve=new Valve();
            valve.setValveId(i+1);
            valveArrayList.add(valve);
        }
        try {
            for (int i=0;i<valves.length();i++){
                Valve valve=new Valve();
                valve.setValveId(i+1);
                valveArrayList.add(valve);
                valveArrayList.get(valves.getInt(i)).setEnable(true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return valveArrayList;
    }

    public static ArrayList<Valve> getGrpEnableValves(JSONArray valves) {
        ArrayList<Valve> valveArrayList=new ArrayList<>();
        try {
            for (int i=0;i<valves.length();i++){
                int id=valves.optInt(i);
                Valve valve=new Valve();
                valve.setValveId(id+1);
                valve.setEnable(true);
                valveArrayList.add(valve);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return valveArrayList;
    }

    public static String valveAssignment(Context context,ArrayList<Valve> valves){
        String valveAssignment="";
        for (int i=0;i<valves.size();i++) {
            valveAssignment=valveAssignment+valves.get(i).getGroupId();
        }
        Preferences.setValveAssignment(context,valveAssignment);
        return valveAssignment;
    }

    public static String getTimeAddition(String time,int minutes){
        time=time.replace("AM","");
        time=time.replace("PM","");
        time=time.trim();

        String[] splitedTime=time.split(":");
        Date currentTime=new Date();
        currentTime.setHours(Integer.parseInt(splitedTime[0].trim()));
        currentTime.setMinutes(Integer.parseInt(splitedTime[1].trim()));

        long additionInMilisecond=currentTime.getTime()+minutes*60*1000;
        Date additionDate=new Date(additionInMilisecond);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        return simpleDateFormat.format(additionDate);
    }

    public static int elapsedSecFromMidnight(String time){
        time=time.replace("AM","");
        time=time.replace("PM","");
        time=time.trim();
        Date date=new Date();
        date.setSeconds(0);
        date.setMinutes(0);
        date.setHours(0);

        if (time.length()<5)
            return 0;

        String[] splitedTime=time.split(":");
        Date currentTime=new Date();
        currentTime.setHours(Integer.parseInt(splitedTime[0].trim()));
        currentTime.setMinutes(Integer.parseInt(splitedTime[1].trim()));
//        currentTime.setSeconds(Integer.parseInt(splitedTime[2]));
        long elapsedMilisecond=currentTime.getTime()-date.getTime();
        return (int) (elapsedMilisecond/1000);

    }

    public static int elapsedSecFromMidnight(){
        Date date=new Date();
        date.setSeconds(0);
        date.setMinutes(0);
        date.setHours(0);

        Date currentTime=new Date();

        long elapsedMilisecond=currentTime.getTime()-date.getTime();
        return (int) (elapsedMilisecond/1000);
    }

    public static int elapsedHours(Long enableTime){
        try {
            long elapsedMilisecond = enableTime - System.currentTimeMillis();
            return (int) (elapsedMilisecond / (60 * 60 * 1000));
        }catch (Exception e){
            e.printStackTrace();
        }
        return 0;
    }
    public static String getTime(int elapsedSecFromMidnight){
        return getTime(elapsedSecFromMidnight,false);
    }

    public static String getTime(int elapsedSecFromMidnight,boolean isSecFormat){
        SimpleDateFormat simpleDateFormat;
        if(isSecFormat)
            simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        else
            simpleDateFormat = new SimpleDateFormat("HH:mm");
        Calendar calendar = Calendar.getInstance(); // gets a calendar using the default time zone and locale.
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.HOUR, 0);
        calendar.add(Calendar.SECOND, elapsedSecFromMidnight);
        String timeStr=simpleDateFormat.format(calendar.getTime());
        return timeStr;
    }

    //  Stop the Application and Launch it again after 10 second
    public static void restartApp(final Activity activity){
        stopHeartBeatTask(activity);
        final ProgressDialog bar=new ProgressDialog(activity);
        bar.setTitle("Wait App Restarting.....");
        bar.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(activity.getApplicationContext(), SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("isRestart",true);
                activity.startActivity(intent);
                activity.finish();
                bar.dismiss();
            }
        },5*1000);

    }

    public static void createNotification(Context context,String title,String msg){
//        String CHANNEL_ID = "Irrigation_channel";// The id of the channel.
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,CHANNEL_ID);
//        NotificationManager mNotificationManager =(NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = context.getString(R.string.app_name);
//            NotificationChannel mChannel =
//                    new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
//            mNotificationManager.createNotificationChannel(mChannel);
//        }
//
//        // Define the notification settings.
//        mBuilder.setContentTitle(title)
//                .setContentText(msg)
//                .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
//                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setPriority(Notification.PRIORITY_DEFAULT)
//                .setAutoCancel(true)
//                .setOnlyAlertOnce(true);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            mBuilder.setChannelId(CHANNEL_ID); // Channel ID
//        }
//        mBuilder.setAutoCancel(true);
//        mNotificationManager.notify((int) System.currentTimeMillis(), mBuilder.build());
    }

    public static String getDeviceIpAddress(Context context){
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        return Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
    }

    //  To check device have internet conectivity or not
    public static boolean isDeviceConnected(Context context){
        ConnectivityManager cm =(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&activeNetwork.isConnectedOrConnecting();
    }
    //  Get previous response from the Shared Preferences and parse it to genrate the Connection Source Data List
    //  Return black List if Shared Preference does not have previous response.

    public static List<ConnectionSourceData> getHotspotList(Context context){
        String response=Preferences.getHotspotdocfile(context);
        ConfiguratonModel configuratonModel=new Gson().fromJson(response,ConfiguratonModel.class);
        final List<ConnectionSourceData> connectionSourceDataList = new ArrayList<>();
        if(configuratonModel==null)
            return connectionSourceDataList;
        for (ConfiguratonModel.Hotspot hotspot : configuratonModel.getConnection().getHotspot()) {
            final ConnectionSourceData connectionSourceData = new ConnectionSourceData();
            connectionSourceData.setName(hotspot.getName());
            connectionSourceData.setSSID(hotspot.getSsid());
            connectionSourceData.setPassword(hotspot.getPassword());
            connectionSourceData.setUrl("");
            connectionSourceData.setType("hotspot");
            connectionSourceDataList.add(connectionSourceData);
        }

        for (ConfiguratonModel.Url url : configuratonModel.getConnection().getUrl()) {
            final ConnectionSourceData connectionSourceData = new ConnectionSourceData();
            connectionSourceData.setName(url.getName());
            connectionSourceData.setSSID("");
            connectionSourceData.setPassword("");
            connectionSourceData.setUrl(url.getUrl());
            connectionSourceData.setType("url");
            connectionSourceDataList.add(connectionSourceData);
        }
        return connectionSourceDataList;
    }
    //  To Print the log only when apk is in Debug mode.
    public static void printLog(String tag,String msg){
        if(BuildConfig.DEBUG && msg!=null)
            Log.d(tag+" ====>>>",msg);
    }

    public static void showSnackbar(View view, String msg){
        showSnackbar(view,msg,Snackbar.LENGTH_LONG);
    }

    public static void showSnackbar(View view, String msg,Integer length){
        Snackbar snackbar = Snackbar.make(view, msg, length);
        snackbar.show();
    }

    public static void setStatusBarColor(Activity activity,int colorId){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            // clear FLAG_TRANSLUCENT_STATUS flag:
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            // finally change the color
            window.setStatusBarColor(ContextCompat.getColor(activity,colorId));
        }
    }

    public static void setNumberPickerTextColor(NumberPicker numberPicker, int color){
        try{
            Field selectorWheelPaintField = numberPicker.getClass()
                    .getDeclaredField("mSelectorWheelPaint");
            selectorWheelPaintField.setAccessible(true);
            ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
        }
        catch(Exception e){
            printLog("setNumberPickerTextColor", e.getMessage());
        }

        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText)
                ((EditText)child).setTextColor(color);
        }
        numberPicker.invalidate();
    }

    public static String getExeptionLog(Context context) {
        String log="";
        PackageManager manager = context.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo (context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e2) {
        }
        String model = Build.MODEL;
        if (!model.startsWith(Build.MANUFACTURER))
            model = Build.MANUFACTURER + " " + model;

        InputStreamReader reader = null;
        log=model+"\n";
        try
        {
            // For Android 4.0 and earlier, you will get all app's log output, so filter it to
            // mostly limit it to your app's output.  In later versions, the filtering isn't needed.
            String cmd = (Build.VERSION.SDK_INT <= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) ?
                    "logcat -d -v time MyApp:v dalvikvm:v System.err:v *:s" :"logcat -d -v time";

            // get input stream
            Process process = Runtime.getRuntime().exec(cmd);
            reader = new InputStreamReader (process.getInputStream());

            BufferedReader r = new BufferedReader(reader);
            StringBuilder total = new StringBuilder();
            for (String line; (line = r.readLine()) != null; ) {
                total.append(line).append('\n');
            }
            log=log+total.toString();
            reader.close();
        }catch (Exception e){
            if (reader != null)
                try {
                    reader.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            return null;
        }
        return log;
    }

    public static void sendLog(String log,Context context){
        try{
            Utils.printLog("sendLog","sendLog ");
            Intent intent = new Intent (Intent.ACTION_SEND);
            intent.setType ("plain/text");
            intent.putExtra (Intent.EXTRA_EMAIL, new String[] {"dalvendrakumar@virtualemployee.com"});
            intent.putExtra (Intent.EXTRA_SUBJECT, "Irrigation app log");
            intent.putExtra (Intent.EXTRA_TEXT, log); // do this so some email clients don't complain about empty body.
            context.startActivity (intent);
        }catch (Exception e){

        }
    }
}
