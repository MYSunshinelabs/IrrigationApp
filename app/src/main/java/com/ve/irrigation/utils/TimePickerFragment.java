package com.ve.irrigation.utils;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import com.ve.irrigation.datavalues.Schedule;
import com.ve.irrigation.irrigation.MakeHttpRequest;

import java.util.Calendar;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {
    private static final String TAG=TimePickerFragment.class.getSimpleName();
    private Schedule schedule;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        schedule= (Schedule) getArguments().getSerializable("Schedule");
        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        try{
            String timeCase="";
            if(hourOfDay>12&&hourOfDay<24)
                timeCase=" PM";
            else
                timeCase=" AM";
            if(minute<10)
                schedule.setStartTime(hourOfDay+":0"+minute+timeCase);
            else
                schedule.setStartTime(hourOfDay+":"+minute+timeCase);
            Utils.printLog(TAG,hourOfDay+" "+minute);

            int stopTime=Utils.elapsedSecFromMidnight(schedule.getStartTime());
            int duration= Integer.parseInt(schedule.getMinutes().replace("Sec",""));

            schedule.setStopTime(Utils.getTime(stopTime+duration));
            updateScheduleOnServer(schedule);
        }catch (Exception e){
            Utils.printLog(TAG,e.toString());
        }
    }

    private void  updateScheduleOnServer(Schedule schedule){

        String duration=schedule.getMinutes().replace("Sec","");
        String volume=schedule.getVolume().replace("Ltr","");
        String enable=schedule.isDisable() ? "1" : "0";
        String enableToday=schedule.isDisableToday()? "1" : "0";

        String parametes =  schedule.getGroupId()+"/"+schedule.getScheduleId()+"/"+
                Utils.elapsedSecFromMidnight(schedule.getStartTime())+"/"+
                duration+"/"+volume+"/"+enable+"/"+enableToday;

        final String url="http://"+ Preferences.getHostIpAddress(getContext())+":"+ Constants.Url.PORT_COMMAND+Constants.Commands.COMMAND_P_SCHED+parametes;
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String response) {

            }
        });

    }
}