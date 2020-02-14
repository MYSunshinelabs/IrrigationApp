package com.ve.irrigation.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.ve.irrigation.utils.HeartBeatTask;
import com.ve.irrigation.utils.Utils;

/**
 * Created by dalvendrakumar on 3/12/18.
 */

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class ListenESPBarodJobService extends JobService {
    private final String TAG=ListenESPBarodJobService.class.getSimpleName();
    private HeartBeatTask heartBeatTask;
    @Override
    public void onCreate() {
        super.onCreate();
        heartBeatTask=new HeartBeatTask(getApplicationContext());
    }

    @Override
    public boolean onStartJob(JobParameters params) {
        if(heartBeatTask!=null)
            heartBeatTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Utils.printLog(TAG,"onStopJob");
        HeartBeatTask.countHB=0;
        if(heartBeatTask!=null)
            heartBeatTask.cancel(true);
        return true;
    }
}
