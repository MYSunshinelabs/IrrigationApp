package com.ve.irrigation.datavalues;

import android.content.Context;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.text.format.DateFormat;

import com.google.gson.Gson;

import java.util.Calendar;
import java.util.Locale;


/**
 * Created by laxmi on 16/3/18.
 */

public class MySharedPreferences {


    public static MySharedPreferences mySharedPreferences;

    private MySharedPreferences() {

    }


    public static MySharedPreferences getMySharedPreferences() {
        if (mySharedPreferences == null)
            mySharedPreferences = new MySharedPreferences();
        return mySharedPreferences;
    }


    public SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences("com.ve.irrigation", Context.MODE_PRIVATE);
    }

    public void saveSimpleorAdvanceVersion(Context context, boolean versionVal) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putBoolean("versionVal", versionVal);
        editor.commit();
    }


    public boolean checkSimpleorAdvanceVersion(Context context) {
        return getSharedPreferences(context).getBoolean("versionVal", true);
    }


    public void saveConfigurationSetting(Context context, ConfigurationSetting configurationSetting) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        Gson gson = new Gson();
        String settinginString = gson.toJson(configurationSetting);
        editor.putString("setting", settinginString);
        editor.commit();
    }


    public ConfigurationSetting getConfigurationSetting(Context context) {
        String settinginString = getSharedPreferences(context).getString("setting", "null");
        Gson gson = new Gson();
        return gson.fromJson(settinginString, ConfigurationSetting.class);

    }


    public void saveEngineeringData(Context context, EngineeringData engineeringData) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        Gson gson = new Gson();
        String settinginString = gson.toJson(engineeringData);
        editor.putString("engineeringData", settinginString);
        editor.commit();
    }


    public EngineeringData getEngineeringData(Context context) {
        String engineeringdata = getSharedPreferences(context).getString("engineeringData", "null");
        Gson gson = new Gson();
        return gson.fromJson(engineeringdata, EngineeringData.class);

    }


    public String getDateTime(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm:ss dd-MM-yyyy", cal).toString();
        return date;

    }


    public void saveWaterActual(Context context, int waterActual) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt("waterActual", waterActual);
        editor.commit();
    }


    public int getWaterActual(Context context) {
        return getSharedPreferences(context).getInt("waterActual", 4000);
    }

    public void saveWaterTarget(Context context, int waterTarget) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putInt("waterTarget", waterTarget);
        editor.commit();
    }


    public int getWaterTarget(Context context) {
        return getSharedPreferences(context).getInt("waterTarget", 3000);
    }


    public void saveDDGET(Context context, String string) {
        SharedPreferences.Editor editor = getSharedPreferences(context).edit();
        editor.putString("ddget", string);
        editor.commit();
    }

    public String getDDGET(Context context) {
        return getSharedPreferences(context).getString("ddget", "");
    }


    public String convertSecondtoTime(int totalSecs) {
        int hours = totalSecs / 3600;
        int minutes = (totalSecs % 3600) / 60;

        String timeString = String.format("%02d:%02d", hours, minutes);
        return timeString;



    }




    public boolean isSimAvailable(Context context) {
        boolean isAvailable = false;
        TelephonyManager telMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        int simState = telMgr.getSimState();
        switch (simState) {
            case TelephonyManager.SIM_STATE_ABSENT: //SimState = “No Sim Found!”;
                break;
            case TelephonyManager.SIM_STATE_NETWORK_LOCKED: //SimState = “Network Locked!”;
                break;
            case TelephonyManager.SIM_STATE_PIN_REQUIRED: //SimState = “PIN Required to access SIM!”;
                break;
            case TelephonyManager.SIM_STATE_PUK_REQUIRED: //SimState = “PUK Required to access SIM!”; // Personal Unblocking Code
                break;
            case TelephonyManager.SIM_STATE_READY:
                isAvailable = true;
                break;
            case TelephonyManager.SIM_STATE_UNKNOWN: //SimState = “Unknown SIM State!”;
                break;
        }
        return isAvailable;
    }


}
