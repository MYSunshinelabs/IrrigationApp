package com.ve.irrigation.datavalues;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.ve.irrigation.irrigation.BR;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by dalvendrakumar on 26/11/18.
 */

public class Group extends BaseObservable implements Serializable{
    public String totalReq;
    public String actual;
    public String groupName;
    public int id;
    public ArrayList<Schedule> schedules=new ArrayList<>();
    public ArrayList<Valve> valveArrayList=new ArrayList<>();
    private boolean isEnable=false;

    @Bindable
    public String getTotalReq() {
        return totalReq;
    }

    public void setTotalReq(String totalReq) {
        this.totalReq = totalReq;
        notifyPropertyChanged(BR.totalReq);
    }
    @Bindable
    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
        notifyPropertyChanged(BR.actual);
    }
    @Bindable
    public ArrayList<Valve> getValves() {
        return valveArrayList;
    }

    public void setValves(ArrayList<Valve> valveArrayList) {
//        int decimalValue=Integer.parseInt(valves);
//        this.valveArrayList = getEnableValves(decimalValue);
        this.valveArrayList=valveArrayList;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    @Bindable
    public ArrayList<Schedule> getSchedules() {
        return schedules;
    }

    public void setSchedules(ArrayList<Schedule> schedules) {
        this.schedules = schedules;
    }
    private ArrayList<Valve> getEnableValves(int decimalValue) {
        ArrayList<Valve> valveArrayList=new ArrayList<>();

        String binaryValveValue=Integer.toBinaryString(decimalValue);
        StringBuilder sb = new StringBuilder();
        // append a string into StringBuilder input1
        sb.append(binaryValveValue);
        // reverse StringBuilder input1
        binaryValveValue = String.valueOf(sb.reverse());

        int indexPrev=-1;
        int currIndex;
        do{
            currIndex=binaryValveValue.indexOf('1',indexPrev+1);
            if(currIndex==indexPrev)
                return valveArrayList;

            if(currIndex>=0) {
                Valve valve=new Valve();
                valve.setValveId(getValveId(currIndex+1));
                valveArrayList.add(valve);
            }
            indexPrev=currIndex;
        }while (currIndex>=0);

        return valveArrayList;
    }

    private int getValveId(int currIndex) {
        switch (currIndex){
            case 1:
                return Valve.VALVE_ONE;
            case 2:
                return Valve.VALVE_TWO;
            case 3:
                return Valve.VALVE_THREE;
            case 4:
                return Valve.VALVE_FOUR;
            case 5:
                return Valve.VALVE_FIVE;
            case 6:
                return Valve.VALVE_SIX;
            case 7:
                return Valve.VALVE_SEVEN;
            case 8:
                return Valve.VALVE_EIGHT;
            default:
                return 0;
        }
    }
}
