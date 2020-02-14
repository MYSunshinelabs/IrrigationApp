package com.ve.irrigation.irrigation.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

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
import com.ve.irrigation.irrigation.MakeHttpRequest;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.utils.Constants;
import com.ve.irrigation.utils.HBWidgetTask;
import com.ve.irrigation.utils.HeartBeatTask;
import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;
import com.ve.irrigation.utils.WifiHelper;

import java.util.Locale;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ConfigActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private static final String TAG = ConfigActivity.class.getSimpleName();
    private TextView btnSetSSID,btnSetPass,txtSelectNet,txtLocalNet,txtHintPass,txtHintPass1,txtHintSSID,txtHintSSID1,txtMachineAp,txtCurrentSSID;
    private CheckBox radioOpt1,radioOpt2,radioOpt3;
    private Button btnSwitchLang,btnSave,btnStaSSID;
    private TextView txtHint,txtPass1,txtSSID1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_config);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        initView();

        btnSetSSID.setOnClickListener(this);
        btnSetPass.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        findViewById(R.id.lytOpt1).setOnClickListener(this);
        findViewById(R.id.lytOpt2).setOnClickListener(this);
        findViewById(R.id.lytOpt3).setOnClickListener(this);
        btnSwitchLang.setOnClickListener(this);
        btnStaSSID.setOnClickListener(this);

        String selectedSSID=Preferences.getSelectedSSID(this);

        if (selectedSSID.equals("sun00")){
            radioOpt1.setChecked(true);
            btnStaSSID.setVisibility(View.GONE);
        }else if(selectedSSID.equals(Preferences.getMachineAP(this))) {
            radioOpt2.setChecked(true);
            btnStaSSID.setVisibility(View.VISIBLE);
        }else if(selectedSSID.equals("current")) {
            radioOpt3.setChecked(true);
            btnStaSSID.setVisibility(View.GONE);
        }

        setLanguage();

        if(!HBWidgetTask.isListen)
            Utils.startWidgetHBTask(getBaseContext());

        locationsPermission();
    }

    private void initView() {

        txtSelectNet=findViewById(R.id.txtSelectNet);
        txtLocalNet=findViewById(R.id.txtLocalNet);
        txtMachineAp=findViewById(R.id.txtMachineAp);

        txtHintPass=findViewById(R.id.txtHintPass);
        txtHintPass1=findViewById(R.id.txtHintPass1);
        txtHint=findViewById(R.id.txtHint);
        txtHintSSID=findViewById(R.id.txtHintSSID);
        txtHintSSID1=findViewById(R.id.txtHintSSID1);
        txtCurrentSSID=findViewById(R.id.txtCurrentSSID);

        btnSwitchLang=findViewById(R.id.btnSwitchLang);
        btnStaSSID=findViewById(R.id.btnStaID);
        btnSetSSID=findViewById(R.id.btnSetSSID);
        btnSetPass=findViewById(R.id.btnSetPass);
        btnSave=findViewById(R.id.btnSave);

        radioOpt1=findViewById(R.id.radioOpt1);
        radioOpt2=findViewById(R.id.radioOpt2);
        radioOpt3=findViewById(R.id.radioOpt3);
        txtSSID1=findViewById(R.id.txtSSID1);
        txtPass1=findViewById(R.id.txtPass1);

        txtSSID1.setText(Preferences.getMachineAP(this));
        txtPass1.setText(Preferences.getMachineAPPass(this));

    }

    private void setConnection(String url){
        Utils.printLog(TAG,"setConnection "+url);
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                if(s.length()>0)
                    Utils.printLog(TAG,s);
            }
        });
    }

    @Override
    public void onClick(View v) {
        String urlCommand="";
        switch (v.getId()){

//            case R.id.btnSetSSID:
//                if(edtSSID.getText().toString().length()>2)
//                    urlCommand="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Connection.COMMAND_PORT_NO+"/R/dbset/pswd"+edtSSID.getText().toString();
//                else
//                    edtSSID.setError("Invalid SSID");
//                break;
//
//            case R.id.btnSetPass:
//                if(edtPass.getText().toString().length()>2)
//                    urlCommand="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Connection.COMMAND_PORT_NO+"/R/dbset/ssid/"+edtPass.getText().toString();
//                else
//                    edtPass.setError("Invalid password");
//                break;

            case R.id.btnSave:
                urlCommand="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Connection.COMMAND_PORT_NO+"/R/dbsav";
                break;

            case R.id.lytOpt1:
                radioOpt1.setChecked(true);
                radioOpt2.setChecked(false);
                radioOpt3.setChecked(false);
                btnStaSSID.setVisibility(View.GONE);
                Preferences.setSelectedSSID(this,"sun00");
                break;

            case R.id.lytOpt2:
                radioOpt1.setChecked(false);
                radioOpt2.setChecked(true);
                radioOpt3.setChecked(false);
                btnStaSSID.setVisibility(View.VISIBLE);
                Preferences.setSelectedSSID(this,"sun01");
                break;

            case R.id.lytOpt3:
                radioOpt1.setChecked(false);
                radioOpt2.setChecked(false);
                radioOpt3.setChecked(true);
                btnStaSSID.setVisibility(View.GONE);
                Preferences.setSelectedSSID(this,"current");
                break;
            case R.id.btnSwitchLang:
                if(Preferences.getLanguage(this).equals("en"))
                    Preferences.setLanguage(this,"zh");
                else
                    Preferences.setLanguage(this,"en");
//                IrrigationWidgetProvider.isLanguageChange=true;
//                IrrigationWidgetProvider.updateWidget(this);
                setLanguage();
                break;
            case R.id.btnStaID:
                startActivity(new Intent(this,ChangeMachineApDetailsActivity.class));
                break;
        }

        if(urlCommand.length()>1)
            setConnection(urlCommand);
    }

    private void switchLanguage() {
        Locale locale = new Locale(Preferences.getLanguage(this));
        Locale.setDefault(locale);

        Resources res = getResources();
        Configuration config = new Configuration(res.getConfiguration());
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) {
            config.setLocale(locale);
            createConfigurationContext(config);
        } else {
            config.locale = locale;
            res.updateConfiguration(config, res.getDisplayMetrics());
        }
//        IrrigationWidgetProvider.isLanguageChange=true;
//        IrrigationWidgetProvider.updateWidget(this);
        startActivity(new Intent(this,ConfigActivity.class));
        finish();
    }

    private void setLanguage() {
        if(Preferences.getLanguage(this).equals("en")) {
            btnSwitchLang.setText("Switch to English");
            txtSelectNet.setText("Select Network");
            txtLocalNet.setText("Local Network");
            txtMachineAp.setText("Machine AP");
            txtHint.setHint("Use current SSID.");
            txtHintPass.setText("Password");
            txtHintPass1.setText("Password");
            txtHintSSID.setText("SSID");
            txtHintSSID1.setText("SSID");
            btnSetSSID.setText("Set");
            btnSetPass.setText("Set");
            btnSave.setText("save");
//            edtPass.setHint("Password");
        }else {
            btnSwitchLang.setText("切换到中文");
            txtSelectNet.setText("选择网络");
            txtLocalNet.setText("本地网络");
            txtMachineAp.setText("机器AP");
            txtHint.setHint("使用当前的SSID。");
            txtHintPass.setText("密码");
            txtHintPass1.setText("密码");
            btnSetSSID.setText("组");
            btnSetPass.setText("组");
            btnSave.setText("保存");
//            edtPass.setHint("密码");
        }
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
                            txtCurrentSSID.setText(WifiHelper.getCurrentSsid(ConfigActivity.this));
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            toast("GPS is not on");
                            // Location settings are not satisfied. But could be
                            // fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling
                                // startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(ConfigActivity.this, 1000);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            toast("Setting change not allowed");
                            // Location settings are not satisfied. However, we have
                            // no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    private void toast(String msg) {
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1000) {
            if(resultCode == Activity.RESULT_OK){
                String result=data.getStringExtra("result");
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }
}
