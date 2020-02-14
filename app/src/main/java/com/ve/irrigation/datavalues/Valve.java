package com.ve.irrigation.datavalues;

import android.databinding.BaseObservable;

import java.io.Serializable;

/**
 * Created by dalvendrakumar on 27/11/18.
 */

public class Valve extends BaseObservable implements Serializable {
    public static final int VALVE_ONE=1;
    public static final int VALVE_TWO=2;
    public static final int VALVE_THREE=3;
    public static final int VALVE_FOUR=4;
    public static final int VALVE_FIVE=5;
    public static final int VALVE_SIX=6;
    public static final int VALVE_SEVEN=7;
    public static final int VALVE_EIGHT=8;

    private int valveId;
    private boolean isEnable;
    private int groupId;

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getValveId() {
        return valveId;
    }

    public void setValveId(int valveId) {
        this.valveId = valveId;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }


}
