package com.ve.irrigation.irrigation.activities;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ve.irrigation.datavalues.Group;
import com.ve.irrigation.datavalues.Schedule;
import com.ve.irrigation.irrigation.MakeHttpRequest;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.adapters.SchedulesAdapter;
import com.ve.irrigation.irrigation.listners.ScheduleListener;
import com.ve.irrigation.utils.Constants;
import com.ve.irrigation.utils.DialogUtils;
import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.TimePickerFragment;
import com.ve.irrigation.utils.Utils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class EditGroupActivity extends BaseActivity implements ScheduleListener, View.OnClickListener {
    private Group group;
    private SchedulesAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_group);
        setSupportActionBar((Toolbar) findViewById(R.id.toolbar));

        init();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        final String url="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Url.PORT_COMMAND+ Constants.Commands.COMMAND_G_SCHED;
//        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
//            @Override
//            public void call(String response) {
//
//            }
//
//        });

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {
        group= (Group) getIntent().getExtras().getSerializable("group");

        TextView txtGroupDetail=findViewById(R.id.txtGroupDetail);
        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        findViewById(R.id.btnSave).setOnClickListener(this);

        adapter=new SchedulesAdapter(EditGroupActivity.this, group.getSchedules());
        LinearLayoutManager layoutManager=new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        getSupportActionBar().setTitle(group.groupName+" "+group.totalReq);

//      txtGroupDetail.setText(group.groupName+" "+group.totalReq);
        txtGroupDetail.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }
    @Override
    public void onStartTime(Schedule schedule) {
        DialogFragment timeFragment = new TimePickerFragment();
        Bundle args=new Bundle();
        args.putSerializable("Schedule",schedule);
        timeFragment.setArguments(args);
        timeFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @Override
    public void onMinutesClick(final Schedule schedule) {
        DialogUtils.showScheduleValueDialog(this, " Please enter Duration", false,schedule,new Runnable() {
            @Override
            public void run() {
                updateScheduleOnServer(schedule);
            }
        }).show();
    }

    @Override
    public void onVolumeClick(final Schedule schedule) {
        DialogUtils.showScheduleValueDialog(this, " Please enter volume", true,schedule,new Runnable() {
            @Override
            public void run() {
                updateScheduleOnServer(schedule);
            }
        }).show();
    }

    @Override
    public void onDisableClick(Schedule schedule) {
        updateScheduleOnServer(schedule);
    }

    @Override
    public void onDisableTodayClick(Schedule schedule) {
        if(!schedule.isDisableToday()){
            Preferences.setScheduleEnableTime(this,schedule.getScheduleId()+""+schedule.getGroupId()+"");
        }else
            Preferences.setScheduleEnableTime(this,schedule.getScheduleId()+""+schedule.getGroupId()+"", Long.valueOf(0));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave:
                updateGroupOnServer();
                break;
        }
    }

    private void updateGroupOnServer() {

    }

    private void  updateScheduleOnServer(Schedule schedule){
        String duration=schedule.getMinutes().replace("Sec","");
        String volume=schedule.getVolume().replace("Ltr","");
        String enable=schedule.isDisable()?"1":"0";
        String enableToday=schedule.isDisableToday()?"1":"0";

        String parametes=schedule.getGroupId()+"/"+schedule.getScheduleId()+"/"+ Utils.elapsedSecFromMidnight(schedule.getStartTime())+"/"+
                duration+"/"+volume+"/"+enable+"/"+enableToday;
        final String url="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Url.PORT_COMMAND+ Constants.Commands.COMMAND_P_SCHED+parametes;
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String response) {

            }

        });
    }
}
