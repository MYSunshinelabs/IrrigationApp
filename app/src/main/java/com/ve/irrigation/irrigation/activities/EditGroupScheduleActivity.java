package com.ve.irrigation.irrigation.activities;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.ve.irrigation.datavalues.Schedule;
import com.ve.irrigation.irrigation.MakeHttpRequest;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.databinding.ActivityEditScheduleBinding;
import com.ve.irrigation.irrigation.listners.NumberListener;
import com.ve.irrigation.irrigation.listners.TimeListener;
import com.ve.irrigation.utils.Constants;
import com.ve.irrigation.utils.IrrigationNumberPicker;
import com.ve.irrigation.utils.IrrigationTimePicker;
import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class EditGroupScheduleActivity extends BaseActivity implements View.OnClickListener,TimeListener, NumberListener {
    private Schedule schedule;
    private ActivityEditScheduleBinding binding;
    private IrrigationNumberPicker durationPicker,volumePicker;
    private int REQUEST_CODE_DURATION=101,REQUEST_CODE_VOLUME=102;
    public static int REQUEST_CODE_EDIT_SCHEDULE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= DataBindingUtil.setContentView(this,R.layout.activity_edit_schedule);

        init();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void init() {

        schedule= (Schedule) getIntent().getExtras().getSerializable("schedule");
        binding.setSchedule(schedule);

        findViewById(R.id.btnSave).setOnClickListener(this);

        IrrigationTimePicker timePicker=new IrrigationTimePicker(this,schedule.getStartTime(),binding.lytTimePicker);

        durationPicker=new IrrigationNumberPicker(this,Integer.parseInt(schedule.getMinutes()),binding.lytDurationPicker,REQUEST_CODE_DURATION);
        volumePicker=new IrrigationNumberPicker(this,Integer.parseInt(schedule.getVolume()),binding.lytVolumePicker,REQUEST_CODE_VOLUME);

        durationPicker.setMAX_NUMBER_LIMIT(Constants.Configration.MAX_DURATION_MINUTES);
        durationPicker.setMIN_NUMBER_LIMIT(Constants.Configration.MIN_DURATION_MINUTES);

        volumePicker.setMAX_NUMBER_LIMIT(Constants.Configration.MAX_VOLUME);
        volumePicker.setMIN_NUMBER_LIMIT(Constants.Configration.MIN_VOLUME);

        binding.txtMinute.setOnClickListener(this);
        binding.txtStartTime.setOnClickListener(this);
        binding.txtVolume.setOnClickListener(this);
        binding.radioEnable24hr.setOnClickListener(this);
        binding.radioEnablePermanently.setOnClickListener(this);
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

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave:
                updateScheduleOnServer(schedule);
                break;

            case R.id.txtMinute:
                binding.lytDurationPicker.setVisibility(View.VISIBLE);
                binding.lytVolumePicker.setVisibility(View.GONE);
                binding.lytTimePicker.setVisibility(View.GONE);
                break;

            case R.id.txtVolume:
                binding.lytDurationPicker.setVisibility(View.GONE);
                binding.lytVolumePicker.setVisibility(View.VISIBLE);
                binding.lytTimePicker.setVisibility(View.GONE);
                break;

            case R.id.txtStartTime:
                binding.lytTimePicker.setVisibility(View.VISIBLE);
                binding.lytVolumePicker.setVisibility(View.GONE);
                binding.lytDurationPicker.setVisibility(View.GONE);
                break;

            case R.id.radioEnablePermanently:
                schedule.setDisable(!schedule.isDisable());
//                updateScheduleOnServer(schedule);
                break;

            case R.id.radioEnable24hr:
                schedule.setDisableToday(!schedule.isDisableToday());
//                updateScheduleOnServer(schedule);
                break;
            case R.id.radioEnableNutrition:
//                updateScheduleOnServer(schedule);
                break;
        }
    }

    private void  updateScheduleOnServer(Schedule schedule){
        String duration=schedule.getMinutes();
        String volume=schedule.getVolume();
        String enable=schedule.isDisable()?"1":"0";
        String enableToday=schedule.isDisableToday()?"1":"0";
        duration= (Integer.parseInt(duration)*60)+"";// convert to sec

        String parametes=schedule.getGroupId()+"/"+schedule.getScheduleId()+"/"+ Utils.elapsedSecFromMidnight(schedule.getStartTime())+"/"+
                duration+"/"+volume+"/"+enable+"/"+enableToday;

        final String url="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Connection.COMMAND_PORT_NO+ Constants.Commands.COMMAND_P_SCHED+parametes;

        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String response) {
                setResult(Activity.RESULT_OK);
                finish();
            }
        });
    }

    @Override
    public void onSetTime(String time) {
        int duration= Integer.parseInt(schedule.getMinutes());
        schedule.setStartTime(time);
        schedule.setStopTime(Utils.getTimeAddition(schedule.getStartTime(),duration));
        binding.lytTimePicker.setVisibility(View.GONE);
//        updateScheduleOnServer(schedule);
    }

    @Override
    public void onSetNumber(int value) {
    }

    @Override
    public void onSetNumber(int requestCode, int value) {
        if(requestCode==REQUEST_CODE_VOLUME) {
            schedule.setVolume(value + "");
            binding.lytVolumePicker.setVisibility(View.GONE);
        }else if(requestCode==REQUEST_CODE_DURATION) {
            schedule.setMinutes(value + "");
            binding.lytDurationPicker.setVisibility(View.GONE);
        }
//        updateScheduleOnServer(schedule);
    }

}
