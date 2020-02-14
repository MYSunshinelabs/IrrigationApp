package com.ve.irrigation.irrigation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.ve.irrigation.customview.CustomTextViewLightBold;
import com.ve.irrigation.datavalues.AppDataBase;
import com.ve.irrigation.datavalues.Group;
import com.ve.irrigation.datavalues.Valve;
import com.ve.irrigation.datavalues.Wifi;
import com.ve.irrigation.irrigation.MakeHttpRequest;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.adapters.ControlModeRecyclerAdapter;
import com.ve.irrigation.irrigation.listners.GroupStateListner;
import com.ve.irrigation.irrigation.listners.SwipeListener;
import com.ve.irrigation.irrigation.listners.ValveStateListner;
import com.ve.irrigation.utils.Constants;
import com.ve.irrigation.utils.IrrigationGestureDetector;
import com.ve.irrigation.utils.LocationHelper;
import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;

import org.json.JSONArray;

import java.util.ArrayList;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ControlModeActivity extends BaseActivity implements GroupStateListner, ValveStateListner, SwipeListener, View.OnClickListener {
    private static final String TAG = ControlModeActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private Button btnMode;
    private View viewDisable;
    private ArrayList<Group> groupArrayList;
    private ControlModeRecyclerAdapter mAdapter;
    private GestureDetectorCompat mDetector;
    private int noGroups;
    private String groupValves,mode;
    private String[] test;
    private RelativeLayout lytNavBar;
    private CustomTextViewLightBold txtTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_control_mode);

        Utils.setStatusBarColor(this, R.color.colorStatusBarAdvMode);

        init();

        toggleModeBtn(mode);

    }

    private void init() {

        noGroups= (int) getIntent().getExtras().get("noGroups");

        groupValves=getIntent().getStringExtra("Valves");
        mode=getIntent().getStringExtra("Mode");

        groupArrayList=setGroups(noGroups,groupValves);

        mAdapter= new ControlModeRecyclerAdapter(groupArrayList,this,this);

        LinearLayoutManager manager=new LinearLayoutManager(this);

        mDetector=new GestureDetectorCompat(this,new IrrigationGestureDetector(this));

        recyclerView = findViewById(R.id.recyclerView);

        txtTitle=findViewById(R.id.title);

        lytNavBar=findViewById(R.id.lytNavBar);
        btnMode=findViewById(R.id.btnSwitchMode);
        viewDisable=findViewById(R.id.viewDisable);

        recyclerView.setLayoutManager(manager);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.setAdapter(mAdapter);
        btnMode.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private ArrayList<Group> setGroups(int noGroups, String groupValvesStr) {
        final ArrayList<Group> groups=new ArrayList<>();
        try {
            JSONArray valveArray = new JSONArray(Preferences.getVXG(this));
            for (int i = 0; i < noGroups; i++) {
                JSONArray grpValve = valveArray.getJSONArray(i);
                Group group = new Group();
                group.setTotalReq("Total Req" + (i + 1));
                group.setActual("Act " + (i + 1));
                group.groupName = "Group " + (i + 1);
                group.setEnable(true);
                group.setValves(Utils.getGrpEnableValves(grpValve));
                groups.add(group);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return groups;
    }


    private boolean isEnableManualMode(){
        for (Group group : groupArrayList) {
            if(group.isEnable()){
                for (Valve valve:group.valveArrayList)
                    if(valve.isEnable())
                        return true;
            }
        }
        return false;
    }

    @Override
    public void onGroupStateChange(Group group) {

        Utils.printLog(TAG,"onGroupStateChange");

        String groupId=group.groupName.substring(group.groupName.length()-1);

        setGroupStatusOnServer(groupId,group.isEnable());

        toggleModeBtn(mode);

    }

    @Override
    public void onValveStateChange(Valve valve) {
        Utils.printLog(TAG,"onValveStateChange");
        setValveStatusOnServer(valve.getValveId(),valve.isEnable());
        toggleModeBtn(mode);
    }

    private void toggleModeBtn(String mode){
//        if(isEnableManualMode()) {
        if(mode!=null)
            if(mode.equals("5")) {
                lytNavBar.setBackgroundResource(R.drawable.background_manual_mode);
                txtTitle.setText("Manual Mode");
                viewDisable.setVisibility(View.GONE);
            }else {
                lytNavBar.setBackgroundResource(R.drawable.background_auto_mode);
                txtTitle.setText("Automatic Mode");
                viewDisable.setVisibility(View.VISIBLE);
            }
    }

    private void setValveStatusOnServer(int valveId,boolean status){
        String command=status?Constants.Commands.COMMAND_V_OPEN+valveId:Constants.Commands.COMMAND_V_CLOSE+valveId;
        String url="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Connection.COMMAND_PORT_NO+ command;
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String response) {
                Utils.printLog(TAG,response);
            }
        });
    }

    private void setGroupStatusOnServer(String groupId,boolean status){
        String command=status?Constants.Commands.COMMAND_G_OPEN+groupId:Constants.Commands.COMMAND_G_CLOSE+groupId;
        String url="http://"+Preferences.getHostIpAddress(this)+":"+Constants.Connection.COMMAND_PORT_NO+command;

        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String response) {
                Utils.printLog(TAG,response);
            }
        });
    }

    private void setControlMode(final int modeId){
        String url="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Connection.COMMAND_PORT_NO+Constants.Commands.COMMAND_MODE +modeId+"/";
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String response) {
                Utils.printLog(TAG,response);
                mode=modeId==0 ?"3":"5";
                toggleModeBtn(mode);
            }
        });
    }
    @Override
    public void onSwipe(int id) {
        switch (id){
            case SwipeListener.SWIPE_UP:
                Utils.printLog(TAG, "top");
                break;

            case SwipeListener.SWIPE_DOWN:
                Utils.printLog(TAG, "down");
                break;

            case SwipeListener.SWIPE_LEFT:
                Utils.printLog(TAG, "left");
                Intent intentGroups=new Intent(ControlModeActivity.this,GroupsActivity.class);
                intentGroups.putExtra("noGroups",noGroups);
                intentGroups.putExtra("Valves",groupValves);
                startActivity(intentGroups);
                overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                break;

            case SwipeListener.SWIPE_RIGHT:
                Utils.printLog(TAG, "right");
                onBackPressed();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        ControlModeActivity.this.overridePendingTransition(R.anim.slide_left2, R.anim.slide_right2);
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

    private void insertWifi(){
        LocationHelper.wifis.add(new Wifi("ssid1","password1","28.540368","77.399600"));
        LocationHelper.wifis.add(new Wifi("ssid2","password2","28.546003","77.395378"));
        LocationHelper.wifis.add(new Wifi("Nokia 6","poiuytre","28.543827","77.403030"));
        LocationHelper.wifis.add(new Wifi("ssid4","password4","28.536647","77.403887"));
        LocationHelper.wifis.add(new Wifi("ssid4","password4","28.536647","77.403887"));
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDataBase.getAppDataBase(getApplicationContext()).wifiDAO().insert(LocationHelper.wifis.toArray(new Wifi[LocationHelper.wifis.size()]));
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSwitchMode:
                setControlMode(mode.equals("5")?0:1);
                break;
        }
    }
}
