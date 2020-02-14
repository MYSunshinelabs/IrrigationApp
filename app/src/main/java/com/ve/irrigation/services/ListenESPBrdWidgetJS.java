package com.ve.irrigation.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;

import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.activities.SplashActivity;
import com.ve.irrigation.utils.HBWidgetTask;
import com.ve.irrigation.utils.Utils;

/**
 * Created by dalvendrakumar on 3/12/18.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ListenESPBrdWidgetJS extends JobService {
    private final String TAG= ListenESPBrdWidgetJS.class.getSimpleName();
    private static final String NOTIFICATION_CHANNEL_ID = "notificaton_channel_id";
    private static final String NOTIFICATION_CHANNEL_NAME = "Notification_channel_name";
    private static final String NOTIFICATION_CHANNEL_DESC= "Irrigation widget tracking you GPS Satellites Counts";
    private static final int NOTIFICATION_ID = 200;
    private HBWidgetTask hBTask;
    @Override
    public void onCreate() {
        super.onCreate();
        hBTask =new HBWidgetTask(getApplicationContext());
//        startInForeground();
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        if(hBTask !=null) {
            hBTask.execute();
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Utils.printLog(TAG,"onStopJob");
        try {
            stopForeground(true);
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(NOTIFICATION_ID);
            if(hBTask !=null) {
                hBTask.sendHBAckMsg();
                hBTask.cancel(true);
                hBTask =null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    private void startInForeground() {
        Intent notificationIntent = new Intent(this, SplashActivity.class);
        PendingIntent pendingIntent= PendingIntent.getActivity(this,0,notificationIntent,0);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Heart Beat")
                .setTicker("TICKER")
                .setContentIntent(pendingIntent);
        Notification notification=builder.build();
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(NOTIFICATION_CHANNEL_DESC);
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
        startForeground(NOTIFICATION_ID, notification);
    }
}
