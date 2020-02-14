package com.ve.irrigation.irrigation.activities;
import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.google.gson.Gson;
import com.ve.irrigation.datavalues.AppDataBase;
import com.ve.irrigation.datavalues.EngineeringData;
import com.ve.irrigation.datavalues.HeartBeat;
import com.ve.irrigation.datavalues.MySharedPreferences;
import com.ve.irrigation.Receiver.EspErrorListReceiver;
import com.ve.irrigation.Receiver.EspNetCommMsgReceiver;
import com.ve.irrigation.Receiver.EspResponseReceiver;
import com.ve.irrigation.Receiver.NetworkConnectivityReceiver;
import com.ve.irrigation.customview.CustomTextViewLightBold;
import com.ve.irrigation.irrigation.MakeHttpRequest;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.adapters.EspErrorAdapter;
import com.ve.irrigation.irrigation.databinding.ActivityMain4v2Binding;
import com.ve.irrigation.irrigation.listners.NumberListener;
import com.ve.irrigation.irrigation.listners.SwipeListener;
import com.ve.irrigation.services.ListenESPBarodService;
import com.ve.irrigation.utils.Constants;
import com.ve.irrigation.utils.HeartBeatTask;
import com.ve.irrigation.utils.IrrigationGestureDetector;
import com.ve.irrigation.utils.IrrigationNumberPicker;
import com.ve.irrigation.utils.LocationHelper;
import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;

import org.json.JSONObject;
import java.net.DatagramSocket;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity4v2 extends BaseActivity implements EspResponseReceiver.EspResponseObserver, NetworkConnectivityReceiver.ConnectivityChangeObserver,
        EspNetCommMsgReceiver.EspNetCommMsgObserver, EspErrorListReceiver.EspErrorMsgObserver, SwipeListener, View.OnClickListener, NumberListener {

    private static final String TAG = MainActivity4v2.class.getSimpleName();
    private int REQUEST_CODE_HOTSPOT = 2018;
    private GestureDetectorCompat mDetector;
    private CustomTextViewLightBold txtGVol[]=new CustomTextViewLightBold[8];
    private RecyclerView recyclerView;
    public static int count;

    private int noGroups;
    private boolean isRestart;
    private String pId;
    private DatagramSocket socket;

    private EspResponseReceiver espResponseReceiver;
    private EspErrorListReceiver espErrorListReceiver;
    private EspNetCommMsgReceiver espNetCommMsgReceiver;
    private NetworkConnectivityReceiver connectivityReceiver;
    private EspErrorAdapter espErrorAdapter;
    private HeartBeat heartBeat;
    private ActivityMain4v2Binding binding;
    private IrrigationNumberPicker volumePicker;
    private CustomTextViewLightBold txtMtime;
    private Handler handlerNetworkStatus;
    private Runnable netStatusRunable;

    /**
     * Called when the activity is first created.
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= DataBindingUtil.setContentView(this,R.layout.activity_main_4v2);
        txtMtime=binding.lytActionBar.findViewById(R.id.txtMTime);
        txtMtime.setVisibility(View.VISIBLE);

        heartBeat=getLocalHB();
        if(heartBeat!=null) {
            HeartBeatTask.countHB=0;
            binding.setHeartBeat(heartBeat);
            updateSenserIndicator(heartBeat);
        }

        init();

        //  Set status Bar Color according to Advance mode GUI Theme
        Utils.setStatusBarColor(this,R.color.colorStatusBarAdvMode);


        new Thread(new Runnable() {
            @Override
            public void run() {
                LocationHelper.wifis= AppDataBase.getAppDataBase(getApplicationContext()).wifiDAO().getAllWifi();
            }
        }).start();
        binding.btnHeartBeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url="http://"+ Preferences.getHostIpAddress(getBaseContext())+":"+ Constants.Url.PORT_COMMAND+ Constants.Commands.COMMAND_DATA_HB;
                MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
                    @Override
                    public void call(String response) {
                        Utils.printLog(TAG, "COMMAND_DATA_HB : " +response);
                    }
                });
            }
        });

    }

    private HeartBeat getLocalHB() {
        try{
            HeartBeat heartBeat = Preferences.getHeartBeat(this);
            return heartBeat;
        }catch (Exception e){
            Utils.printLog(TAG+" onEspResponse",e.getMessage());
            return null;
        }
    }

    private void init() {
        int defaultValue=Constants.Configration.MIN_VOLUME;
        if(heartBeat!=null && !heartBeat.getEntity().equals("S0001"))
            defaultValue= Integer.parseInt(heartBeat.getMetric().getRvol());

        volumePicker=new IrrigationNumberPicker(this,defaultValue,binding.lytNumPicker);
        volumePicker.setMAX_NUMBER_LIMIT(Constants.Configration.MAX_VOLUME);
        volumePicker.setMIN_NUMBER_LIMIT(Constants.Configration.MIN_VOLUME);

        mDetector = new GestureDetectorCompat(this, new IrrigationGestureDetector(this));

        espNetCommMsgReceiver= EspNetCommMsgReceiver.registerReceiver(this,this);

        connectivityReceiver= NetworkConnectivityReceiver.registerNetworkReciver(this,MainActivity4v2.this);

        espErrorListReceiver= EspErrorListReceiver.registerReceiver(this,MainActivity4v2.this);

        binding.txtIpAddress.setText(Utils.getDeviceIpAddress(this));
        binding.txtNodeName.setText(Constants.Connection.PORT_NO+"");
        recyclerView=findViewById(R.id.recyclerView);

        txtGVol[0]=  findViewById(R.id.txtGroup1);
        txtGVol[1]=  findViewById(R.id.txtGroup2);
        txtGVol[2]=  findViewById(R.id.txtGroup3);
        txtGVol[3]=  findViewById(R.id.txtGroup4);
        txtGVol[4]=  findViewById(R.id.txtGroup5);
        txtGVol[5]=  findViewById(R.id.txtGroup6);
        txtGVol[6]=  findViewById(R.id.txtGroup7);
        txtGVol[7]=  findViewById(R.id.txtGroup8);

        binding.txtEditReqVol.setOnClickListener(this);

        espErrorAdapter=new EspErrorAdapter(EspErrorListReceiver.espErrorList);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(espErrorAdapter);

        pId=Preferences.getProductId(this);
        noGroups=Preferences.getNoGroups(this);

//        manageUIVisibility();
    }

    private void manageUIVisibility(){
        try{
            noGroups=Preferences.getNoGroups(this);
            for(int i=noGroups;i<8;i++) {
                txtGVol[i].setBackgroundResource(R.drawable.bg_group_volume_disable);
            }
        }catch (Exception e){
            Utils.printLog(TAG+"initViews3V2 ",e.getMessage());
        }
    }

    @Override
    public void onEspResponse(String response) {
        Utils.printLog(TAG+" onEspResponse",response);
        try{
            JSONObject jsonHB=new JSONObject(response);
            if (jsonHB.has("metric")) {
                heartBeat = new Gson().fromJson(response, HeartBeat.class);
                if(heartBeat.getEntity().equals("M0001")) {
                    binding.setHeartBeat(heartBeat);
                    manageMachineTime();
                    binding.radioPumpStatus.setChecked(heartBeat.getMetric().getPstate());
                    Preferences.setRequiredVolume(this,heartBeat.getMetric().getRvol()+"");
                    Preferences.setActualVolume(this,heartBeat.getMetric().getAvol()+"");
                }else{
                    if (heartBeat.getMetric().getRain()==0)
                        binding.radioRain.setChecked(false);
                    else {
                        binding.radioRain.setChecked(true);
                        binding.txtRain.setText(heartBeat.getMetric().getRain()+"");
                        binding.txtRainStarted.setText(heartBeat.getMetric().getRainc()+"");
                        binding.txtRainDuration.setText(heartBeat.getMetric().getRaint()+"");
                        binding.txtRainAmount.setText(heartBeat.getMetric().getRainv()+"");
                    }
                    binding.txtTemperature.setText(heartBeat.getMetric().getTemp()+"");
                    binding.txtParcentage.setText(heartBeat.getMetric().getBp()+"");
                    binding.txtadf.setText(heartBeat.getMetric().getLight()+"");
                    binding.txtHD.setText(heartBeat.getMetric().getRh()+"");
                    binding.radioTemperature.setChecked(true);
                }

                setStatusHandler();

                binding.radioStausNetwork.setChecked(true);
                binding.txtHeartBeat.setText(HeartBeatTask.countHB+"");

            }else if (jsonHB.getInt("msg") == 2) {
                String pId=jsonHB.optString("pid");
                String group =jsonHB.optString("grp");
                Preferences.setVXG(this,jsonHB.getJSONArray("vxg").toString());
                Preferences.setProductId(pId,this);
                Preferences.setGroups(group,this);
                Preferences.setNoGroups(this,Integer.parseInt(group));
                Preferences.setNoValves(this,Integer.parseInt(pId.charAt(1)+""));
            }
            EngineeringData engineeringData = MySharedPreferences.getMySharedPreferences().getEngineeringData(this);
            if(engineeringData.getmTest()==1){
                EspErrorListReceiver.espErrorList.add(0,"HB "+response);
                espErrorAdapter.notifyDataSetChanged();
            }
        }catch (Exception e){
            Utils.printLog(TAG +" onEspResponse",e.getMessage());
        }

    }

    private void setStatusHandler() {
        if(handlerNetworkStatus==null)
            handlerNetworkStatus=new Handler();
        handlerNetworkStatus.removeCallbacks(netStatusRunable);
        netStatusRunable=null;
        netStatusRunable=  new Runnable() {
            @Override
            public void run() {
                binding.radioStausNetwork.setChecked(false);
            }
        };

        handlerNetworkStatus.postDelayed(netStatusRunable,1000*10);

    }


    private void manageMachineTime() {
        int machineTime=heartBeat.getMetric().getTs();
        int deviceTime=Utils.elapsedSecFromMidnight();

        if(machineTime-deviceTime>30 ||deviceTime-machineTime>30)
            updateMachineTime(deviceTime);
        txtMtime.setText(heartBeat.getMetric().getFormatedTs());
    }

    private void updateMachineTime(int deviceTime) {
        final String url="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Connection.COMMAND_PORT_NO+ Constants.Commands.COMMAND_STIME+deviceTime+"/";

        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String response) {
                Utils.printLog("updateMachineTime",response);
            }
        });
    }

    private void updateSenserIndicator(HeartBeat heartBeat)  {
        if(!heartBeat.isRainSenser()) {
            binding.txtRain.setTextColor(getResources().getColor(R.color.textColorGray));
            binding.radioRain.setChecked(false);
        }else {
            binding.txtRain.setTextColor(getResources().getColor(R.color.colorTextSecondary));
            binding.radioRain.setChecked(true);
        }

        if(!heartBeat.isTempratureSenser()) {
            binding.txtTemperature.setTextColor(getResources().getColor(R.color.textColorGray));
            binding.radioTemperature.setChecked(false);
        }else {
            binding.txtTemperature.setTextColor(getResources().getColor(R.color.colorTextSecondary));
            binding.radioTemperature.setChecked(true);
        }
        binding.radioPumpStatus.setChecked(heartBeat.getMetric().getPstate());

    }

    @Override
    public void onEspNetCommMsg(String response){
        if(response.contains("Connected"))
            binding.radioStausNetwork.setChecked(true);
        else
            binding.radioStausNetwork.setChecked(false);
    }

    @Override
    public void onEspErrorUpdate(String response) {
        Utils.printLog(TAG,response);
        espErrorAdapter.notifyDataSetChanged();
    }

    @Override
    public void onConnectivityChange(boolean isConnected) {
        if(isConnected) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !HeartBeatTask.isListen) {
                binding.txtIpAddress.setText(Utils.getDeviceIpAddress(this));
                Utils.startHeartBeatTask(getApplicationContext());
            } else if (!ListenESPBarodService.isListen) {
                Utils.startHeartBeatTask(getApplicationContext());
            }
        }else {
            Utils.stopHeartBeatTask(getApplicationContext());
            binding.radioStausNetwork.setChecked(false);
        }
    }

    @Override
    protected void onUserLeaveHint(){
        // When user presses home page
        Log.v(TAG, "Home Button Pressed");
        super.onUserLeaveHint();
    }

    @Override
    protected void onResume() {
        super.onResume();

        Utils.printLog(TAG, "ON Resume");
        if(!isRestart)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !HeartBeatTask.isListen) {
                Utils.startHeartBeatTask(getApplicationContext());
            } else if (!ListenESPBarodService.isListen ){
                Utils.startHeartBeatTask(getApplicationContext());
            }
        espResponseReceiver=EspResponseReceiver.registerReceiver(this,this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
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
    public void onSwipe(int id) {
        switch (id){
            case SwipeListener.SWIPE_UP:
                Utils.printLog(TAG, "top");
                Intent i = new Intent(MainActivity4v2.this, ConfigurationScreenActivity.class);
                startActivityForResult(i,Constants.RequestCode.RESTART_APP);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                break;

            case SwipeListener.SWIPE_DOWN:
                Utils.printLog(TAG, "down");
                Intent iDown = new Intent(MainActivity4v2.this, SelectDeviceActivity.class);
                startActivity(iDown);
                overridePendingTransition(R.anim.slide_up2, R.anim.slide_down2);
                break;

            case SwipeListener.SWIPE_LEFT:
                Utils.printLog(TAG, "left");
                Intent iLeft = new Intent(MainActivity4v2.this, ControlModeActivityNew.class);
                iLeft.putExtra("noGroups",noGroups);
                iLeft.putExtra("Valves",Preferences.getValveAssignment(MainActivity4v2.this));
                iLeft.putExtra("Mode",heartBeat.getMetric().getMode());
                startActivity(iLeft);
                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                break;

            case SwipeListener.SWIPE_RIGHT:
                Utils.printLog(TAG, "right");
                Intent iRight = new Intent(MainActivity4v2.this, SwipeRightActivity.class);
                startActivity(iRight);
                overridePendingTransition(R.anim.slide_left2, R.anim.slide_right2);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handlerNetworkStatus.removeCallbacks(null);
        handlerNetworkStatus=null;
        EspResponseReceiver.unregisterReceiver(espResponseReceiver,this);

        if (socket != null) {
            if (socket.isConnected())
                socket.disconnect();
            socket = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try{
            NetworkConnectivityReceiver.unregisterReceiver(connectivityReceiver,this);
            EspErrorListReceiver.unregisterReceiver(espErrorListReceiver,this);
            EspNetCommMsgReceiver.unregisterReceiver(espNetCommMsgReceiver,this);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                finishAffinity();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isRestart=false;
        if (requestCode == 2018) {
        }else if(requestCode==Constants.RequestCode.RESTART_APP && resultCode==Constants.ResultCode.RESTART_APP){
            isRestart=true;
            Utils.restartApp(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtEditReqVol:
                binding.lytNumPicker.setVisibility(View.VISIBLE);
                binding.txtEditReqVol.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public void onSetNumber(int value) {
        binding.txtVolRequired.setText(value+"");
        binding.lytNumPicker.setVisibility(View.GONE);
        binding.txtEditReqVol.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSetNumber(int requestCode, int value) {

    }
}
