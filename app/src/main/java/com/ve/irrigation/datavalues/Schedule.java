package com.ve.irrigation.datavalues;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.ve.irrigation.irrigation.BR;

import java.io.Serializable;

/**
 * Created by dalvendrakumar on 28/11/18.
 */

public class Schedule extends BaseObservable implements Serializable{
    private int groupId,scheduleId;
    private String startTime,minutes,volume,stopTime;
    private boolean isDisable=true,isDisableToday=true,statusNutrition=false;

    public boolean isStatusNutrition() {
        return statusNutrition;
    }

    public void setStatusNutrition(boolean statusNutrition) {
        this.statusNutrition = statusNutrition;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }
    @Bindable
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
        notifyPropertyChanged(BR.startTime);
    }
    @Bindable
    public String getMinutes() {
        return minutes;
    }

    public void setMinutes(String minutes) {
        this.minutes = minutes;
        notifyPropertyChanged(BR.minutes);
    }
    @Bindable
    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
        notifyPropertyChanged(BR.volume);
    }
    @Bindable
    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
        notifyPropertyChanged(BR.stopTime);
    }
    @Bindable
    public boolean isDisable() {
        return isDisable;
    }

    public void setDisable(boolean disable) {
        isDisable = disable;
        notifyPropertyChanged(BR.disable);
    }
    @Bindable
    public boolean isDisableToday() {
        return isDisableToday;
    }

    public void setDisableToday(boolean disableToday) {
        isDisableToday = disableToday;
        notifyPropertyChanged(BR.disableToday);
    }
    public String getGroupName(){
        return "Group "+groupId;
    }

    public String getScheduleName(){
        return "Start : "+getScheduleId();
    }

    @Bindable
    public String getScheduleMsg(){
//        return startTime+"  "+minutes+" "+volume+" "+ (isDisable==true ? "Enable":"Disable")+" "+ (isDisableToday==true ? "Enable Today":"Disable Today");
        return startTime+",  "+volume+", "+ (isDisable==true ? "Enable":"Disable")+", "+ (isDisableToday==true ? "Enable Today":"Disable Today");
    }
}
