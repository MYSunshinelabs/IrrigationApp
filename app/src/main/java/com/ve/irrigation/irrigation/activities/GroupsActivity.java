package com.ve.irrigation.irrigation.activities;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

import com.ve.irrigation.datavalues.Group;
import com.ve.irrigation.datavalues.Schedule;
import com.ve.irrigation.irrigation.MakeHttpRequest;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.adapters.GroupRecyclerAdapterNew;
import com.ve.irrigation.irrigation.listners.GroupListener;
import com.ve.irrigation.irrigation.listners.SwipeListener;
import com.ve.irrigation.utils.Constants;
import com.ve.irrigation.utils.IrrigationGestureDetector;
import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * Created by dalvendrakumar on 26/11/18.
 */

public class GroupsActivity extends BaseActivity implements GroupListener, SwipeListener {

    private static final String TAG = GroupsActivity.class.getSimpleName();
    private RecyclerView recyclerView;
    private GestureDetectorCompat mDetector;
    private ArrayList<Group> groupArrayList;
    private GroupRecyclerAdapterNew mAdapter;
    private int noGroups;
    private String groupValves;
    private boolean isAdvMode,fromEdit;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_groups);

        Utils.setStatusBarColor(this, R.color.colorStatusBarAdvMode);

        init();

        if(!isAdvMode)
            if(groupArrayList.size()>0)
                getSchedules(1);
            else
                Utils.showSnackbar(recyclerView,getString(R.string.msg_unable_to_connect));


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(isAdvMode && fromEdit){
            if(groupArrayList.size()>0)
                getSchedules(1);
        }

    }

    private void getSchedules(final int groupId) {
        String url="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Connection.COMMAND_PORT_NO+ Constants.Commands.COMMAND_G_SCHED+groupId;
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String response) {
                groupArrayList.get(groupId-1).setSchedules(populateScheduleData(response,groupId));
                if(groupId<groupArrayList.size())
                    getSchedules(groupId+1);
                else{
                    mAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    private void init() {

        noGroups = (int) getIntent().getExtras().get("noGroups");
        groupValves=getIntent().getStringExtra("Valves");

        isAdvMode=getIntent().getBooleanExtra("isAdv_mode",false);

        if(isAdvMode)
            groupArrayList= (ArrayList<Group>) getIntent().getSerializableExtra("group_list");
        else
            groupArrayList=setGroups(noGroups,groupValves);

        mAdapter= new GroupRecyclerAdapterNew(groupArrayList, isAdvMode,this);
        LinearLayoutManager manager=new LinearLayoutManager(this);

        IrrigationGestureDetector gestureDetector=new IrrigationGestureDetector(this);
        mDetector=new GestureDetectorCompat(this,gestureDetector);

        recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

    }

    private ArrayList<Group> setGroups(int noGroups, String groupValvesStr) {
        final ArrayList<Group> groups=new ArrayList<>();
        try {
            JSONArray valveArray=new JSONArray(Preferences.getVXG(this));
            for(int i=0;i<noGroups;i++){
                JSONArray grpValve=valveArray.getJSONArray(i);
                Group group=new Group();
                group.setTotalReq(Preferences.getRequiredVolume(this));
                group.setActual(Preferences.getActualVolume(this));
                group.groupName="Group "+(i+1);
                group.setValves(Utils.getGrpEnableValves(grpValve));
                groups.add(group);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return groups;
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
            Utils.printLog(TAG,e.getMessage());
        }
        return schedules;
    }

    @Override
    public void onEditGroup(Group group) {
        Intent editGroupIntent=new Intent(this,EditGroupActivity.class);
        editGroupIntent.putExtra("group",group);
        startActivity(editGroupIntent);
    }

    @Override
    public void onEditRequiredVolume(final Group group) {

    }

    @Override
    public void onValvesClick(Group group) {
        Intent intentValve=new Intent(this,GroupStartEditActivity.class);
        intentValve.putExtra("group",group);
        startActivity(intentValve);
    }

    //      Launch Edit group schedule screen when user tap on schedule edit buttton
    @Override
    public void onSchedule(Schedule schedule) {
        Intent intentValve=new Intent(this,EditGroupScheduleActivity.class);
        intentValve.putExtra("schedule",schedule);
        startActivityForResult(intentValve,EditGroupScheduleActivity.REQUEST_CODE_EDIT_SCHEDULE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==EditGroupScheduleActivity.REQUEST_CODE_EDIT_SCHEDULE && resultCode== Activity.RESULT_OK)
            fromEdit=true;
        else
            fromEdit=false;
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
                if(!isAdvMode) {
                    Intent intentAvdGroup = new Intent(GroupsActivity.this, GroupsActivity.class);
                    intentAvdGroup.putExtra("noGroups", noGroups);
                    intentAvdGroup.putExtra("Valves", groupValves);
                    intentAvdGroup.putExtra("isAdv_mode", true);
                    intentAvdGroup.putExtra("group_list", groupArrayList);
                    startActivity(intentAvdGroup);
                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
                }
                break;
            case SwipeListener.SWIPE_RIGHT:
                Utils.printLog(TAG, "right");
                onBackPressed();
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
        this.overridePendingTransition(R.anim.slide_left2, R.anim.slide_right2);
    }
}
