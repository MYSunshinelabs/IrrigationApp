package com.ve.irrigation.services;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.ve.irrigation.Receiver.CurrentLocationReceiver;
import com.ve.irrigation.datavalues.Wifi;
import com.ve.irrigation.utils.LocationHelper;
import com.ve.irrigation.utils.Utils;
import com.ve.irrigation.utils.WifiManager;


/**
 * Created by Dalvendra on 15-03-2019.
 */

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class LocationMonitoringJobService extends JobService implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    private static final String TAG = LocationMonitoringJobService.class.getSimpleName();
    private boolean isServiceStarted=false;
    private boolean isCheckForWifi=true;
    private GoogleApiClient mLocationClient;
    private LocationRequest mLocationRequest = new LocationRequest();
    private Location locationPrev;


    public static final String ACTION_LOCATION_BROADCAST = LocationMonitoringJobService.class.getName() + "LocationBroadcast";
    public static final String EXTRA_LATITUDE = "extra_latitude";
    public static final String EXTRA_LONGITUDE = "extra_longitude";

    @Override
    public boolean onStartJob(JobParameters params) {
        isServiceStarted=true;
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);


        int priority = LocationRequest.PRIORITY_HIGH_ACCURACY; //by default
        //PRIORITY_BALANCED_POWER_ACCURACY, PRIORITY_LOW_POWER, PRIORITY_NO_POWER are the other priority modes
        mLocationRequest.setPriority(priority);
        mLocationClient.connect();
        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        isServiceStarted=false;
        return false;
    }

    /*
     * LOCATION CALLBACKS
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            Log.d(TAG, "== Error On onConnected() Permission not granted");
            //Permission not granted by user so cancel the further execution.

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);

        Log.d(TAG, "Connected to Google API");
    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    //to get the location change
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");
        if (location != null) {
            CurrentLocationReceiver.sendBroadcast(getBaseContext(),location);
            Log.d(TAG, "location= "+location.getLatitude()+", "+location.getLongitude());
//            if(locationPrev!=null)
//                Log.d(TAG, "Location Distance = "+LocationHelper.meterDistanceBetweenLocationPoints(locationPrev,location)+" Meter");

            if(isCheckForWifi){
                isCheckForWifi=false;
                Wifi nearByWifi= LocationHelper.getNearByWifi(location,getBaseContext());
                if (nearByWifi!=null) {
                    Utils.printLog(TAG, nearByWifi.getSsid());
                    WifiManager.connectTo(getBaseContext(),nearByWifi);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isCheckForWifi=true;
                    }
                },1000*60);
            }

            locationPrev=location;
        }


    }

    private void sendMessageToUI(String lat, String lng) {
        Log.d(TAG, "Sending info...");

        Intent intent = new Intent(ACTION_LOCATION_BROADCAST);
        intent.putExtra(EXTRA_LATITUDE, lat);
        intent.putExtra(EXTRA_LONGITUDE, lng);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API");
    }

}