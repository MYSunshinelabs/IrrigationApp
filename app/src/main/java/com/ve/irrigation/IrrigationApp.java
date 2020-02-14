package com.ve.irrigation;

import android.app.Application;
import android.os.Handler;

import com.crashlytics.android.Crashlytics;
import com.ve.irrigation.utils.Utils;
import io.fabric.sdk.android.Fabric;

/**
 * Created by dalvendrakumar on 3/12/18.
 */

public class IrrigationApp extends Application {
    private static final String TAG = IrrigationApp.class.getSimpleName();
    @Override
    public void onCreate() {
        super.onCreate();

        //Add Crashlytics:
        Fabric.with(this, new Crashlytics());

        Utils.startHeartBeatTask(getApplicationContext());



    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if(level>=TRIM_MEMORY_UI_HIDDEN) {
            Utils.printLog(TAG,"Background");
            Utils.stopHeartBeatTask(this);
            Utils.stopLocationService(this);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    System.exit(0);
                }
            },1000);
        }
    }

//    private void setUpUnCaughtExHandler(){
        // Setup handler for uncaught exceptions.
//        Thread.setDefaultUncaughtExceptionHandler (new Thread.UncaughtExceptionHandler()
//        {
//            @Override
//            public void uncaughtException (Thread thread, Throwable e)
//            {
//                e.printStackTrace();
//                String log= Utils.getExeptionLog(getApplicationContext());
//                Preferences.setExeptionLog(getApplicationContext(),log);
////                String log=e.getLocalizedMessage()+"\n"+e.getMessage()+"\n Stack Trace" +e.getStackTrace().toString();
////                if(log!=null)
////                    Utils.sendLog(log,getApplicationContext());
//
//                System.exit(1); // kill off the crashed app
//            }
//        });
//    }


}
