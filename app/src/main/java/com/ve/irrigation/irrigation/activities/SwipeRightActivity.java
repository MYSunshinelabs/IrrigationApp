package com.ve.irrigation.irrigation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import com.ve.irrigation.datavalues.Group;
import com.ve.irrigation.datavalues.Schedule;
import com.ve.irrigation.irrigation.MakeHttpRequest;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.adapters.NutrigationGrpAdaptor;
import com.ve.irrigation.irrigation.adapters.NutrigationStartAdaptor;
import com.ve.irrigation.irrigation.listners.SwipeListener;
import com.ve.irrigation.utils.Constants;
import com.ve.irrigation.utils.IrrigationGestureDetector;
import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


public class SwipeRightActivity extends BaseActivity implements NutrigationStartAdaptor.ScheduleListener, SwipeListener {

    private static final String TAG = SwipeRightActivity.class.getSimpleName();
    private GestureDetectorCompat mDetector;
    private ArrayList<Group> groups;
    private RecyclerView recyclerView;
    private CheckBox cbAllOn,cbAllOff;
    private NutrigationGrpAdaptor adapter;
    private boolean isAllCb=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_swipe_right);

        init();

        adapter=new NutrigationGrpAdaptor (groups,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }

    private void init(){
        cbAllOff=findViewById(R.id.cbAllOff);
        cbAllOn=findViewById(R.id.cbAllON);

        groups=setGroups(Preferences.getNoGroups(this),Preferences.getNoValves(this)+"");

        mDetector = new GestureDetectorCompat(this, new IrrigationGestureDetector(this));

        recyclerView=findViewById(R.id.recyclerView);

        cbAllOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    enableAllCb();
                    cbAllOn.setChecked(false);
                    setNutritonStaus(false);
                    setStatusOnMachine(0,0,0);
                }
            }});

        cbAllOn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    cbAllOff.setChecked(false);
                    enableAllCb();
                    setNutritonStaus(true);
                    setStatusOnMachine(0,0,1);
                }
            }});

        if(groups.size()>0)
            getSchedules(1);

    }
    private void enableAllCb(){
        isAllCb=true;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                isAllCb=false;
            }
        },2000);
    }
    private void setNutritonStaus(boolean stausNutrition) {
        for (Group group:groups){
            for(Schedule schedule:group.getSchedules()){
                schedule.setStatusNutrition(stausNutrition);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private ArrayList<Group> setGroups(int noGroups, String groupValvesStr) {
        final ArrayList<Group> groups=new ArrayList<>();
        for(int i=0;i<noGroups;i++){
            Group group=new Group();
            group.setTotalReq("Total Req"+(i+1));
            group.setActual("Act "+(i+1));
            group.groupName="Group "+(i+1);
            group.setEnable(true);
//            group.setValves(Utils.getGroupValves(groupValvesStr, (i+1)+""));
            groups.add(group);
        }
        return groups;
    }

    private void getSchedules(final int groupId) {
        String url="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Connection.COMMAND_PORT_NO+ Constants.Commands.COMMAND_G_SCHED+groupId;
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String response) {
                groups.get(groupId-1).setSchedules(populateScheduleData(response,groupId));
                if(groupId<groups.size())
                    getSchedules(groupId+1);
                else{
                    enableAllCb();
                    adapter.notifyDataSetChanged();
                }
            }
        });

    }

    private ArrayList<Schedule> populateScheduleData(String response,int groupId){
        ArrayList<Schedule> schedules=new ArrayList<>();
        try{
            JSONObject jsonObject=new JSONObject(response);
            JSONArray scheduleJson=jsonObject.getJSONArray("item");
            for (int i=0;i<scheduleJson.length();i++){
                JSONObject json=scheduleJson.getJSONObject(i);
                Schedule schedule=new Schedule();
                schedule.setGroupId(groupId);
                schedule.setScheduleId(i+1);
                Integer startTime=Integer.parseInt(json.getString("start"));
                Integer duration=Integer.parseInt(json.optString("time"))/60;
                schedule.setStartTime( Utils.getTime(startTime));
                schedule.setMinutes(duration+"");
                schedule.setStopTime(Utils.getTimeAddition(schedule.getStartTime(),duration));
                schedule.setVolume(json.optString("vol"));
                schedule.setDisableToday(json.optString("dis24").equals("1")?true:false);
                schedule.setDisable(json.optString("dis").equals("1")?true:false);
                schedule.setStatusNutrition(json.optString("nutri").equals("1")?true:false);
                schedules.add(schedule);
            }
        }catch (Exception e){
            Utils.printLog(TAG,"populateScheduleData "+e.getMessage());
        }
        return schedules;
    }

    @Override
    public void onStatusChange(Schedule schedule) {
        if (isAllCb)
            return;
        if(schedule.isStatusNutrition())
            setStatusOnMachine(schedule.getGroupId(),schedule.getScheduleId(),1);
        else
            setStatusOnMachine(schedule.getGroupId(),schedule.getScheduleId(),0);
    }


    private void setStatusOnMachine(int grpId,int startId,int status ) {
        String url="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Connection.COMMAND_PORT_NO+"/R/nutri/"+grpId+"/"+startId+"/"+status;
        Utils.printLog(TAG,"setStatusOnMachine "+url);
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                if(s.length()>0) {
                    Utils.printLog(TAG, s);
                }
            }
        });
    }

    @Override
    public void onSwipe(int id) {
        switch (id){
            case SwipeListener.SWIPE_UP:
                break;
            case SwipeListener.SWIPE_DOWN:
                break;
            case SwipeListener.SWIPE_LEFT:
                SwipeRightActivity.this.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                finish();
                break;
            case SwipeListener.SWIPE_RIGHT:
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
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        SwipeRightActivity.this.overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
    }
}
