//
//
//package com.ve.irrigation.irrigation.activities;
//
//import android.content.Intent;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.v4.view.GestureDetectorCompat;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.RelativeLayout;
//
//import com.google.gson.Gson;
//import com.ve.irrigation.datavalues.EngineeringData;
//import com.ve.irrigation.datavalues.HeartBeat;
//import com.ve.irrigation.datavalues.MySharedPreferences;
//import com.ve.irrigation.datavalues.WifiHotspot;
//import com.ve.irrigation.Receiver.EspErrorListReceiver;
//import com.ve.irrigation.Receiver.EspNetCommMsgReceiver;
//import com.ve.irrigation.Receiver.EspResponseReceiver;
//import com.ve.irrigation.Receiver.NetworkConnectivityReceiver;
//import com.ve.irrigation.customview.CustomTextViewLight;
//import com.ve.irrigation.hotspot.WifiApManager;
//import com.ve.irrigation.irrigation.MakeHttpRequest;
//import com.ve.irrigation.irrigation.R;
//import com.ve.irrigation.irrigation.adapters.EspErrorAdapter;
//import com.ve.irrigation.irrigation.listners.SwipeListener;
//import com.ve.irrigation.services.ListenESPBarodService;
//import com.ve.irrigation.utils.Constants;
//import com.ve.irrigation.utils.HeartBeatTask;
//import com.ve.irrigation.utils.IrrigationGestureDetector;
//import com.ve.irrigation.utils.Preferences;
//import com.ve.irrigation.utils.Utils;
//
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.net.DatagramSocket;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.functions.Action1;
//import rx.schedulers.Schedulers;
//
//
//public class MainActivityNew extends BaseActivity implements EspResponseReceiver.EspResponseObserver, NetworkConnectivityReceiver.ConnectivityChangeObserver, EspNetCommMsgReceiver.EspNetCommMsgObserver, EspErrorListReceiver.EspErrorMsgObserver, SwipeListener {
//    private static final String TAG = MainActivityNew.class.getSimpleName();
//    private WifiApManager wifiApManager;
//    private GestureDetectorCompat mDetector;
//    private String LOGTAG = MainActivityNew.class.getSimpleName();
//
//    private RelativeLayout lytBar;
//    private CustomTextViewLight txtHeartBeatNo,txtAckCount,txtConnectedTime,txtTRunTime,txtTRunTime2,txtVolReq,txtVolAct,txtGroups,
//            txtActiveGroupsNo,txtNutrition,txtPumpInfo,txtFlowInfo,txtRainSensor,txtClimateSensors,txtNotification,txtNetCommStatus;
//
//    private CustomTextViewLight txtPump,txtPressBar,txtFlowIpm,txtRain,txtRainStarted,txtRainDuration;
//    private CustomTextViewLight txtGVol[]=new CustomTextViewLight[8];
//    private RecyclerView recyclerView;
//    public static int count;
//    private int REQUEST_CODE_HOTSPOT = 2018;
//    private  WifiHotspot wifiHotspot;
//
//    private boolean mListen = true;
//    private DatagramSocket socket;
//    private String pId;
//    private int noGroups;
//    private String rainDuration="Duration 4.0Hours",rainTime="Started at 1:00Pm";
//    private EspResponseReceiver espResponseReceiver;
//    private EspErrorListReceiver espErrorListReceiver;
//    private EspNetCommMsgReceiver espNetCommMsgReceiver;
//    private NetworkConnectivityReceiver connectivityReceiver;
//    private EspErrorAdapter espErrorAdapter;
//
//    /**
//     * Called when the activity is first created.
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main_3v2);
//
////        initViews();
//        initViews3V2();
//
//        lytBar.setVisibility(View.GONE);
//
//        mDetector = new GestureDetectorCompat(this, new IrrigationGestureDetector(this));
//
//        wifiApManager = new WifiApManager(this);
//
//        addFirstTimeNetwork();
//
//        showLocalHB();
//
//        espNetCommMsgReceiver= EspNetCommMsgReceiver.registerReceiver(this,this);
//        connectivityReceiver= NetworkConnectivityReceiver.registerNetworkReciver(this,MainActivityNew.this);
//        espErrorListReceiver= EspErrorListReceiver.registerReceiver(this,MainActivityNew.this);
//    }
//
//    private void showLocalHB() {
//        String localHB=Preferences.getHeartBeat(this);
//        if(localHB.length()>0 && HeartBeatTask.countHB>0){
//            try{
//                HeartBeat heartBeat = new Gson().fromJson(localHB, HeartBeat.class);
//                updateUI(heartBeat);
//
//            }catch (Exception e){
//                Utils.printLog(LOGTAG+" onEspResponse",e.getMessage());
//            }
//        }
//    }
//
//    private void initViews() {
//        txtConnectedTime = findViewById(R.id.txtConnectedTime);
//        txtHeartBeatNo = findViewById(R.id.txtHeartBeatNo);
//        txtTRunTime =  findViewById(R.id.txtTRunTime);
//        txtTRunTime2=  findViewById(R.id.txtTRunTime2);
//        txtVolReq =  findViewById(R.id.txtVolReq);
//        txtVolAct =  findViewById(R.id.txtVolActual);
//        txtGroups =  findViewById(R.id.txtGroups);
//        txtActiveGroupsNo=  findViewById(R.id.txtActiveGroupsNo);
//        txtNutrition=  findViewById(R.id.txtNutrition);
//        txtPumpInfo=  findViewById(R.id.txtPumpInfo);
//        txtFlowInfo=  findViewById(R.id.txtFlowInfo);
//        txtRainSensor=  findViewById(R.id.txtRainSensor);
//        txtClimateSensors=  findViewById(R.id.txtClimateSensors);
//        txtNotification=  findViewById(R.id.txtNotification);
//        lytBar=findViewById(R.id.lytBar);
//
//    }
//
//    private void initViews3V2() {
//        txtConnectedTime = findViewById(R.id.txtConnectedTime);
//        txtHeartBeatNo = findViewById(R.id.txtHeartBeatNo);
//        txtAckCount= findViewById(R.id.txtAckCount);
//        txtTRunTime =  findViewById(R.id.txtTRunTime);
//        txtTRunTime2=  findViewById(R.id.txtTRunTime2);
//        txtVolReq =  findViewById(R.id.txtVolReq);
//        txtVolAct =  findViewById(R.id.txtVolActual);
//        txtNetCommStatus =  findViewById(R.id.txtNetCommStatus);
//
//        txtPump=  findViewById(R.id.txtPump);
//        txtPressBar=  findViewById(R.id.txtPressBar);
//        txtFlowIpm=  findViewById(R.id.txtFlowIpm);
//        recyclerView=findViewById(R.id.recyclerView);
//
//        txtGVol[0]=  findViewById(R.id.txtGroup1);
//        txtGVol[1]=  findViewById(R.id.txtGroup2);
//        txtGVol[2]=  findViewById(R.id.txtGroup3);
//        txtGVol[3]=  findViewById(R.id.txtGroup4);
//        txtGVol[4]=  findViewById(R.id.txtGroup5);
//        txtGVol[5]=  findViewById(R.id.txtGroup6);
//        txtGVol[6]=  findViewById(R.id.txtGroup7);
//        txtGVol[7]=  findViewById(R.id.txtGroup8);
//
//        txtRain=  findViewById(R.id.txtRain);
//        txtRainStarted=  findViewById(R.id.txtRainStarted);
//        txtRainDuration=  findViewById(R.id.txtRainDuration);
//        txtClimateSensors=  findViewById(R.id.txtClimateSensors);
//        txtNotification=  findViewById(R.id.txtNotification);
//        txtNutrition=  findViewById(R.id.txtNutrition);
//        lytBar=findViewById(R.id.lytBar);
//
//        espErrorAdapter=new EspErrorAdapter(EspErrorListReceiver.espErrorList);
//        LinearLayoutManager manager=new LinearLayoutManager(this);
//        recyclerView.setLayoutManager(manager);
//        recyclerView.setAdapter(espErrorAdapter);
//
//        pId=Preferences.getProductId(this);
//        manageUIVisibility();
//    }
//    private void manageUIVisibility(){
//        try{
//            noGroups=Preferences.getNoGroups(this);
//
//            if(noGroups<=4)
//                findViewById(R.id.lytGVol2line).setVisibility(View.GONE);
//
//            for(int i=0;i<noGroups;i++)
//                txtGVol[i].setVisibility(View.VISIBLE);
//
//            int nutrition=Integer.parseInt(Preferences.getProductId(this).charAt(3)+"");
//            if(nutrition<=0)
//                txtNutrition.setVisibility(View.GONE);
//
//        }catch (Exception e){
//            Utils.printLog(LOGTAG+"initViews3V2 ",e.getMessage());
//        }
//    }
//    private void addFirstTimeNetwork() {
//        wifiHotspot = new WifiHotspot();
//        wifiHotspot.setSsid("sun00");
//        wifiHotspot.setPassword("sun01234");
//        wifiHotspot.setType("hotspot");
//
//    }
//
//    @Override
//    public void onEspResponse(String response) {
//        Utils.printLog(LOGTAG+" onEspResponse",response);
//        try{
//            JSONObject jsonHB=new JSONObject(response);
//            if (jsonHB.getInt("msg")==3) {
//                HeartBeat heartBeat = new Gson().fromJson(response, HeartBeat.class);
//                updateUI(heartBeat);
//            }else if (jsonHB.getInt("msg") == 2) {
//
//                String pId=jsonHB.optString("pid");
//                String group =jsonHB.optString("grp");
//
//                Preferences.setProductId(pId,this);
//                Preferences.setGroups(group,this);
//                Preferences.setNoGroups(this,Integer.parseInt(group.charAt(0)+""));
//                Preferences.setValveAssignment(this,group.substring(1));
//                Preferences.setNoValves(this,Integer.parseInt(pId.charAt(1)+""));
//
//                manageUIVisibility();
//            }
//
//            EngineeringData engineeringData = MySharedPreferences.getMySharedPreferences().getEngineeringData(this);
//            if(engineeringData.getmTest()==1){
//                EspErrorListReceiver.espErrorList.add(0,"HB "+response);
//                espErrorAdapter.notifyDataSetChanged();
//            }
//
//        }catch (Exception e){
//            Utils.printLog(LOGTAG+" onEspResponse",e.getMessage());
//        }
//
//    }
//
//    @Override
//    public void onEspNetCommMsg(String response) {
//        if(!response.equals(txtNetCommStatus.getText()))
//            txtNetCommStatus.setText(response);
//    }
//
//    @Override
//    public void onEspErrorUpdate(String response) {
//        Utils.printLog(TAG,response);
//        espErrorAdapter.notifyDataSetChanged();
//    }
//
//    @Override
//    public void onConnectivityChange(boolean isConnected) {
//        if(isConnected) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !HeartBeatTask.isListen) {
//                Utils.startHeartBeatTask(getApplicationContext());
//            } else if (!ListenESPBarodService.isListen) {
//                Utils.startHeartBeatTask(getApplicationContext());
//            }
//            txtNetCommStatus.setText(getString(R.string.msg_waiting_for_response));
//        }else {
//            Utils.stopHeartBeatTask(getApplicationContext());
//            Utils.createNotification(this,"Connectivity",getString(R.string.error_msg_no_network));
//            txtNetCommStatus.setText(getString(R.string.msg_waiting_for_connectivity));
//        }
//    }
//
//    private void updateUI(HeartBeat heartBeat) {
//        Preferences.setRequiredVolume(this,heartBeat.getRqdvol());
//        Preferences.setActualVolume(this,heartBeat.getTtvol());
//        setRealDataV3(heartBeat);
//        txtNotification.setText("Last heartbeat arrived at " + MySharedPreferences.getMySharedPreferences().getDateTime(Long.parseLong(heartBeat.getTs()) * 1000));
//        txtConnectedTime.setText(MySharedPreferences.getMySharedPreferences().getDateTime(Long.parseLong(heartBeat.getTs()) * 1000));
//        txtHeartBeatNo.setText("HB Count : " + HeartBeatTask.countHB);
//        txtAckCount.setText("Ack Count : " + HeartBeatTask.countACK);
//        txtNetCommStatus.setText(HeartBeatTask.countHB>0?"Connected "+Preferences.getCurrentNetworkName(this)+  " / "+ Preferences.getHostIpAddress(this)+" Port : "+ Constants.Connection.PORT_NO:getString(R.string.msg_waiting_for_response));
//    }
//
//    private void setRealData(HeartBeat heartBeat) {
//        String pumpStatus = (Integer.parseInt(heartBeat.getPump())) == 1 ? "On" : "Off";
////        String valveStatus = (Integer.parseInt(heartBeat.getValve1())) == 1 ? "On" : "Off";
//        txtFlowIpm.setText("Flow "+heartBeat.getFlow());
//        txtVolAct.setText("Volume Act "+heartBeat.getTtvol());
//        txtVolReq.setText("Volume Req "+heartBeat.getRqdvol());
//        txtPump.setText("Pump "+pumpStatus);
//
//    }
//    @Override
//    protected void onUserLeaveHint()
//    {
//        // When user presses home page
//        Log.v(TAG, "Home Button Pressed");
//        super.onUserLeaveHint();
//    }
//    private void setRealDataV3(HeartBeat heartBeat) {
//        String pumpStatus = (Integer.parseInt(heartBeat.getPump())) == 1 ? "On" : "Off";
//        txtFlowIpm.setText("Flow Ipm "+heartBeat.getFlow());
//        txtVolAct.setText("Volume Act "+heartBeat.getTtvol());
//        txtVolReq.setText("Volume Req "+heartBeat.getRqdvol());
//        txtPump.setText("Pump "+pumpStatus);
//
//        txtRainStarted.setText(rainTime);
//        txtRainDuration.setText(rainDuration);
//
//        int nutrition=Integer.parseInt(pId.charAt(3)+"");
//        if(nutrition>0)
//            txtNutrition.setText("Nutrition: "+pId.charAt(3));
//        else
//            txtNutrition.setVisibility(View.GONE);
//
//        txtNutrition.setText("Nutrition: "+pId.charAt(3));
//        txtGVol[0].setText("G1Vol "+heartBeat.getG1vol());
//        txtGVol[1].setText("G1Vol "+heartBeat.getG2vol());
//        txtGVol[2].setText("G1Vol "+heartBeat.getG3vol());
//        txtGVol[3].setText("G1Vol "+heartBeat.getG4vol());
//        txtGVol[4].setText("G1Vol "+heartBeat.getG5vol());
//        txtGVol[5].setText("G1Vol "+heartBeat.getG6vol());
//        txtGVol[6].setText("G1Vol "+heartBeat.getG7vol());
//        txtGVol[7].setText("G1Vol "+heartBeat.getG8vol());
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Utils.printLog(LOGTAG, "ON Resume");
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !HeartBeatTask.isListen) {
//            Utils.startHeartBeatTask(getApplicationContext());
//        } else if (!ListenESPBarodService.isListen ){
//            Utils.startHeartBeatTask(getApplicationContext());
//        }
//
//        espResponseReceiver=EspResponseReceiver.registerReceiver(this,this);
//
//    }
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        this.mDetector.onTouchEvent(event);
//        return super.onTouchEvent(event);
//    }
//
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        this.mDetector.onTouchEvent(ev);
//        return super.dispatchTouchEvent(ev);
//    }
//
//
//
//    @Override
//    public void onSwipe(int id) {
//        switch (id){
//            case SwipeListener.SWIPE_UP:
//                Utils.printLog(TAG, "top");
//                Intent i = new Intent(MainActivityNew.this, ConfigurationScreenActivity.class);
//                startActivityForResult(i,Constants.RequestCode.RESTART_APP);
//                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
//                break;
//
//            case SwipeListener.SWIPE_DOWN:
//                Utils.printLog(TAG, "down");
//                Intent iDown = new Intent(MainActivityNew.this, HotspotListActivity.class);
//                startActivityForResult(iDown, REQUEST_CODE_HOTSPOT);
//                overridePendingTransition(R.anim.slide_up2, R.anim.slide_down2);
//                break;
//
//            case SwipeListener.SWIPE_LEFT:
//                Utils.printLog(TAG, "left");
//                Intent iLeft = new Intent(MainActivityNew.this, ControlModeActivity.class);
//                iLeft.putExtra("noGroups",noGroups);
//                iLeft.putExtra("Valves",Preferences.getValveAssignment(MainActivityNew.this));
//                startActivity(iLeft);
//                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
//                break;
//
//            case SwipeListener.SWIPE_RIGHT:
//                Utils.printLog(TAG, "right");
//                Intent iRight = new Intent(MainActivityNew.this, SwipeRightActivity.class);
//                startActivity(iRight);
//                overridePendingTransition(R.anim.slide_left2, R.anim.slide_right2);
//                break;
//        }
//    }
//
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mListen = false;
//
//        EspResponseReceiver.unregisterReceiver(espResponseReceiver,this);
//
//        if (socket != null) {
//            if (socket.isConnected())
//                socket.disconnect();
//            socket = null;
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        try{
//            NetworkConnectivityReceiver.unregisterReceiver(connectivityReceiver,this);
//            EspErrorListReceiver.unregisterReceiver(espErrorListReceiver,this);
//            EspNetCommMsgReceiver.unregisterReceiver(espNetCommMsgReceiver,this);
//            wifiApManager.setWifiApEnabled(null, false);
//
//
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                finishAffinity();
//            }
////            System.exit(0);
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//    }
//
//
//    private void getGroupsData() throws IOException {
//        MakeHttpRequest.getMakeHttpRequest().sendRequest("http://"+Preferences.getHostIpAddress(this)+":5000/command/gsched/1").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
//            @Override
//            public void call(String s) {
//                Utils.printLog(LOGTAG,Preferences.getHostIpAddress(MainActivityNew.this)+ s);
//            }
//        });
//    }
//
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == 2018) {
//            if (resultCode == RESULT_OK) {
//                WifiHotspot wifiHotspotNew = (WifiHotspot) data.getSerializableExtra("result");
//                try {
//                    wifiHotspot = (WifiHotspot) wifiHotspotNew.clone();
//                } catch (CloneNotSupportedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }else if(requestCode==Constants.RequestCode.RESTART_APP && resultCode==Constants.ResultCode.RESTART_APP){
//            Utils.restartApp(this);
//        }
//    }
//
//}
