package com.ve.irrigation.irrigation.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.ve.irrigation.customview.CustomTextViewLightBold;
import com.ve.irrigation.datavalues.Group;
import com.ve.irrigation.datavalues.Valve;
import com.ve.irrigation.irrigation.MakeHttpRequest;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.databinding.ActivityControlModeNewBinding;
import com.ve.irrigation.irrigation.listners.GroupStateListner;
import com.ve.irrigation.irrigation.listners.SwipeListener;
import com.ve.irrigation.irrigation.listners.ValveStateListner;
import com.ve.irrigation.utils.Constants;
import com.ve.irrigation.utils.IrrigationGestureDetector;
import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;


import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ControlModeActivityNew extends BaseActivity implements GroupStateListner, ValveStateListner, SwipeListener, View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private static final String TAG = ControlModeActivityNew.class.getSimpleName();
    private Button btnMode;
    private View viewDisable;
    private GestureDetectorCompat mDetector;
    private int noGroups;
    private String groupValves,mode;
    private RelativeLayout lytNavBar;
    private CustomTextViewLightBold txtTitle;
    private ActivityControlModeNewBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= DataBindingUtil.setContentView(this,R.layout.activity_control_mode_new);

        Utils.setStatusBarColor(this, R.color.colorStatusBarAdvMode);

        init();

        toggleModeBtn();

        setView(mode.equals("5")?false:true);

    }

    private void init() {

        noGroups= (int) getIntent().getExtras().get("noGroups");

        groupValves=getIntent().getStringExtra("Valves");

        mode=getIntent().getStringExtra("Mode");

        mDetector=new GestureDetectorCompat(this,new IrrigationGestureDetector(this));

        txtTitle=findViewById(R.id.title);

        lytNavBar=findViewById(R.id.lytNavBar);
        btnMode=findViewById(R.id.btnSwitchMode);
        viewDisable=findViewById(R.id.dummyView);

        btnMode.setOnClickListener(this);

        binding.radioValve1.setOnCheckedChangeListener(this);
        binding.radioValve2.setOnCheckedChangeListener(this);
        binding.radioValve3.setOnCheckedChangeListener(this);
        binding.radioFill.setOnCheckedChangeListener(this);
        binding.radioMainPump.setOnCheckedChangeListener(this);
        binding.radioRecircPump.setOnCheckedChangeListener(this);
        binding.radioMixPump.setOnCheckedChangeListener(this);
        binding.radioInjectPump.setOnCheckedChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }


    @Override
    public void onGroupStateChange(Group group) {

        Utils.printLog(TAG,"onGroupStateChange");

        String groupId=group.groupName.substring(group.groupName.length()-1);

        setGroupStatusOnServer(groupId,group.isEnable());

        toggleModeBtn();

    }

    @Override
    public void onValveStateChange(Valve valve) {
        Utils.printLog(TAG,"onValveStateChange");
        setValveStatusOnServer(valve.getValveId(),valve.isEnable());
        toggleModeBtn();
    }

    public void toggleModeBtn(){
        if(mode!=null)
            if(mode.equals("5")) {
                txtTitle.setText("Mode : Manual ");
                viewDisable.setVisibility(View.GONE);
                btnMode.setText("Manual Off");
            }else {
                txtTitle.setText("Mode : Automatic");
                viewDisable.setVisibility(View.VISIBLE);
                btnMode.setText("Manual ON");
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
        binding.bar.setVisibility(View.VISIBLE);
        String url="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Connection.COMMAND_PORT_NO+Constants.Commands.COMMAND_MODE +modeId+"/";
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String response) {
                binding.bar.setVisibility(View.GONE);
                Utils.printLog(TAG,response);
                mode=modeId==0 ?"3":"5";
                toggleModeBtn();
                setView(modeId==0?true:false);
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
                Intent intentGroups=new Intent(ControlModeActivityNew.this,GroupsActivity.class);
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
        ControlModeActivityNew.this.overridePendingTransition(R.anim.slide_left2, R.anim.slide_right2);
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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSwitchMode:
                setControlMode(mode.equals("5")?0:1);
                break;
        }
    }

    private void setView(boolean isDisable){
        if(isDisable) {
            binding.lytValve1.setBackgroundResource(R.drawable.bg_disabled);
            binding.lytValve2.setBackgroundResource(R.drawable.bg_disabled);
            binding.lytValve3.setBackgroundResource(R.drawable.bg_disabled);
            binding.lytFill.setBackgroundResource(R.drawable.bg_disabled);
            binding.lytMainPump.setBackgroundResource(R.drawable.bg_disabled);
            binding.lytRecircPump.setBackgroundResource(R.drawable.bg_disabled);
            binding.lytMixPump.setBackgroundResource(R.drawable.bg_disabled);
            binding.lytInjectPump.setBackgroundResource(R.drawable.bg_disabled);
        }else {
            binding.lytValve1.setBackgroundResource(R.drawable.bg_group_adv_mode);
            binding.lytValve2.setBackgroundResource(R.drawable.bg_group_adv_mode);
            binding.lytValve3.setBackgroundResource(R.drawable.bg_group_adv_mode);
            binding.lytFill.setBackgroundResource(R.drawable.bg_group_adv_mode);
            binding.lytMainPump.setBackgroundResource(R.drawable.bg_group_adv_mode);
            binding.lytRecircPump.setBackgroundResource(R.drawable.bg_group_adv_mode);
            binding.lytMixPump.setBackgroundResource(R.drawable.bg_group_adv_mode);
            binding.lytInjectPump.setBackgroundResource(R.drawable.bg_group_adv_mode);
        }
    }


    public void switchValve(int valveId){
        int status=0;
        switch (valveId){
            case 1:
                status=binding.radioValve1.isChecked()==true? 1:0;
                break;
            case 2:
                status=binding.radioValve2.isChecked()==true? 1:0;
                break;
            case 3:
                status=binding.radioValve3.isChecked()==true? 1:0;
                break;
            case 4:
                status=binding.radioFill.isChecked()==true? 1:0;
                break;
        }
        String url="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Connection.COMMAND_PORT_NO+Constants.Commands.COMMAND_V_OPEN+valveId+"/"+status+"/";
        Utils.printLog(TAG,url);
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String response) {
                binding.bar.setVisibility(View.GONE);
                Utils.printLog(TAG,response);
            }
        });
    }
    public void switchPumpStatus(int pumpId){
        int pumpStatus=0;
        switch (pumpId){
            case 1:
                pumpStatus=binding.radioValve1.isChecked()==true? 1:0;
                break;
            case 2:
                pumpStatus=binding.radioValve2.isChecked()==true? 1:0;
                break;
            case 3:
                pumpStatus=binding.radioValve3.isChecked()==true? 1:0;
                break;
            case 4:
                pumpStatus=binding.radioFill.isChecked()==true? 1:0;
                break;
        }
        String url="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Connection.COMMAND_PORT_NO+Constants.Commands.COMMAND_PUMP+pumpId+"/"+pumpStatus+"/";
        Utils.printLog(TAG,url);
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String response) {
                binding.bar.setVisibility(View.GONE);
                Utils.printLog(TAG,response);
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.radioValve1:
                switchValve(1);
                break;
            case R.id.radioValve2:
                switchValve(2);
                break;
            case R.id.radioValve3:
                switchValve(3);
                break;
            case R.id.radioFill:
                switchValve(4);
                break;
            case R.id.radioMainPump:
                switchPumpStatus(1);
                break;
            case R.id.radioRecircPump:
                switchPumpStatus(2);
                break;
            case R.id.radioMixPump:
                switchPumpStatus(3);
                break;
            case R.id.radioInjectPump:
                switchPumpStatus(4);
                break;
        }
    }
}
