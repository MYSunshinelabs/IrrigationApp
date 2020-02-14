package com.ve.irrigation.irrigation.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.ve.irrigation.Receiver.EspNetCommMsgReceiver;
import com.ve.irrigation.Receiver.NetworkConnectivityReceiver;
import com.ve.irrigation.customview.CustomTextViewLight;
import com.ve.irrigation.datavalues.Machine;
import com.ve.irrigation.irrigation.MakeHttpRequest;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.adapters.MachinesListAdapter;
import com.ve.irrigation.irrigation.listners.SwipeListener;
import com.ve.irrigation.utils.Constants;
import com.ve.irrigation.utils.HeartBeatTask;
import com.ve.irrigation.utils.IrrigationGestureDetector;
import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;
import com.ve.irrigation.utils.WifiHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SelectDeviceActivity extends BaseActivity implements  SwipeListener, NetworkConnectivityReceiver.ConnectivityChangeObserver,EspNetCommMsgReceiver.EspNetCommMsgObserver, MachinesListAdapter.MachineListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = SelectDeviceActivity.class.getSimpleName();
    private MachinesListAdapter adapter;
    private RadioButton radioConnectionStatus;
    private NetworkConnectivityReceiver receiver;
    private EspNetCommMsgReceiver msgReceiver;
    private GestureDetectorCompat mDetector;
    private RelativeLayout lytBarRecycler;
    private CustomTextViewLight txtConnectionInfo;

    private ArrayList machineArrayList =new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_select_device);

        init();

    }

    private void init(){
        mDetector = new GestureDetectorCompat(this, new IrrigationGestureDetector(this));
        lytBarRecycler=findViewById(R.id.lytBarRecycler);
        radioConnectionStatus=findViewById(R.id.radioConnectionStatus);
        txtConnectionInfo=findViewById(R.id.txtConnectionInfo);
        adapter=new MachinesListAdapter(machineArrayList,this);
        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onSwipe(int id) {
        switch (id){
            case SwipeListener.SWIPE_UP:
                Utils.printLog(TAG, "top");
                Intent intentConfig = new Intent(this, MainActivity4v2.class);
                startActivity(intentConfig);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                break;
            case SwipeListener.SWIPE_DOWN:
                Utils.printLog(TAG, "down");
                break;

            case SwipeListener.SWIPE_LEFT:
                Utils.printLog(TAG, "left");
                break;

            case SwipeListener.SWIPE_RIGHT:
                Utils.printLog(TAG, "right");
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.mDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onConnectivityChange(boolean isConnected) {
        if(isConnected ) {
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        msgReceiver=EspNetCommMsgReceiver.registerReceiver(this,getBaseContext());
        receiver=NetworkConnectivityReceiver.registerNetworkReciver(this,getBaseContext());

        if(HeartBeatTask.countHB>0) {
            radioConnectionStatus.setSelected(true);
            machineArrayList.add(new Machine("Machine name",Preferences.getHostIpAddress(this),true));
            adapter.notifyDataSetChanged();
        }

        String netType;
        String currentSSID=WifiHelper.getCurrentSsid(this);

        if(Preferences.getMachineAP(this).equals(currentSSID))
            netType="Machine AP    SSID : ";
        else if("sun00".equals(currentSSID))
            netType="Local Network    SSID : ";
        else
            netType="Other Network    SSID : ";

        txtConnectionInfo.setText(netType+currentSSID);

        locationsPermission();

        populateDlist();

    }

    private void locationsPermission(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED ) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 201);
        }else
            enableGPSAutoMatically();
    }

    private void enableGPSAutoMatically() {
        GoogleApiClient googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API).addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();
            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            builder.setAlwaysShow(true);

            PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                    .checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:

                            String netType;
                            String currentSSID=WifiHelper.getCurrentSsid(getApplication());

                            if(Preferences.getMachineAP(getApplication()).equals(currentSSID))
                                netType="Machine AP    SSID : ";
                            else if("sun00".equals(currentSSID))
                                netType="Local Network    SSID : ";
                            else
                                netType="Other Network    SSID : ";
                            txtConnectionInfo.setText(netType+currentSSID);
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(SelectDeviceActivity.this, 1000);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        NetworkConnectivityReceiver.unregisterReceiver(receiver,getBaseContext());
        EspNetCommMsgReceiver.unregisterReceiver(msgReceiver,getBaseContext());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==201 && resultCode==202){

        }
    }

    @Override
    public void onEspNetCommMsg(String response) {
        Utils.printLog(" onEspResponse",response);
        try{

        }catch (Exception e){
            Utils.printLog(" onEspResponse",e.getMessage());
        }
    }


    @Override
    public void onMachineClick(Machine machine) {
        if(machine!=null){
            // saygood bye to machine
            disconnectMachine();
        }
        // send Hello to selected machine
    }

    private void disconnectMachine() {
        String url="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Connection.COMMAND_PORT_NO+"/R/goodbye/";
        Utils.printLog(TAG,"setConnection "+url);
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                if(s.length()>0) {
                    Utils.printLog(TAG, s);
                }
            }
        });
    }
    private void populateDlist() {
        String url="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Connection.COMMAND_PORT_NO+"/R/dlist/";
        Utils.printLog(TAG,"setConnection "+url);
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                if(s.length()>0) {
                    try {
                        JSONObject response=new JSONObject(s);
                        JSONArray jsonMachine=  response.optJSONArray("dlist");
                        machineArrayList.clear();
                        for(int i=0;i<jsonMachine.length();i++){
                            JSONObject objMachine=jsonMachine.optJSONObject(i);
                            Machine machine=new Machine(objMachine.optString("name"),objMachine.optString("ip"), objMachine.optString("port"),objMachine.optString("dtype"));
                            machineArrayList.add(machine);
                        }
                        if(machineArrayList.size()>0)
                            adapter.notifyDataSetChanged();
                    }catch (Exception e){
                        Utils.printLog(TAG,e.toString());
                    }
                    Utils.printLog(TAG, s);
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
