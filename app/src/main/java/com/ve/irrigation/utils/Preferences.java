package com.ve.irrigation.utils;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.ve.irrigation.datavalues.ConfiguratonModel;
import com.ve.irrigation.datavalues.HeartBeat;
import com.ve.irrigation.widget.IrrigationWidgetProvider;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by dalvendrakumar on 23/10/18.
 */

public class Preferences {
    private static final String PREFERENCE_NAME="Irrigation Preference";

    public static void setConfigurationData(Context context,String configData){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("config_data",configData).commit();
    }

    public static ConfiguratonModel getConfigurationData(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        String responseInString = mSharedPreferences.getString("configdata", "");
        Gson gson = new Gson();
        return gson.fromJson(responseInString, ConfiguratonModel.class);
    }

    public static void setProductId(String pId,Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("product_id",pId).commit();
    }

    public static void setGroups(String groups,Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("groups",groups).commit();
    }

    public static void setHostIpAddress(Context context,String ipAddress){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("host_ip_address",ipAddress).commit();
    }

    public static String getProductId(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getString("product_id", "");
    }

    public static String getGroups(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getString("groups", "");
    }

    public static String getHostIpAddress(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getString("host_ip_address", "");
    }

    public static void setNoGroups(Context context,int noGroups){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putInt("noGroups",noGroups).commit();
    }

    public static int getNoGroups(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getInt("noGroups", 0);
    }

    public static void setNoValves(Context context,int noValves){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putInt("noValves",noValves).commit();
    }

    public static String getVXG(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getString("VXG","");
    }

    public static void setVXG(Context context,String vxg){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("VXG",vxg).commit();
    }

    public static int getNoValves(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getInt("noValves", 0);
    }


    public static void setValveAssignment(Context context,String valveAssignment){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("valve_assignment",valveAssignment).commit();
    }

    public static String getValveAssignment(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getString("valve_assignment", "");
    }

    public static void setEditableValveAssignment(Context context,Boolean isEditable){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putBoolean("editable_valve_assignment",isEditable).commit();
    }

    public static Boolean getEditableValveAssignment(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getBoolean("editable_valve_assignment", false);
    }


    public static void setHeartBeat(Context context,String heartBeat){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("heart_beat",heartBeat).commit();
    }

    public static HeartBeat getHeartBeat(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        String strHB=mSharedPreferences.getString("heart_beat", "");
        return new Gson().fromJson(strHB, HeartBeat.class);
    }

    public static void setHotspotdocfile(Context context,String hotspotdocfile){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("hotspot_doc_file",hotspotdocfile).commit();
    }

    public static String getHotspotdocfile(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getString("hotspot_doc_file", "");
    }

    public static void setScheduleEnableTime(Context context,String scheduleKey){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putLong(scheduleKey,System.currentTimeMillis()).commit();
    }

    public static void setScheduleEnableTime(Context context,String scheduleKey,Long enableTime){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putLong(scheduleKey,enableTime).commit();
    }

    public static Long getScheduleEnableTime(Context context,String scheduleKey){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getLong(scheduleKey, 0);
    }

    public static void setRequiredVolume(Context context,String requiredVolume){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("required_volume",requiredVolume).commit();
    }

    public static String getRequiredVolume(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getString("required_volume", "0");
    }


    public static void setActualVolume(Context context,String actualVolume){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("actual_volume",actualVolume).commit();
    }

    public static String getActualVolume(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getString("actual_volume", "0");
    }


    public static void setExeptionLog(Context context,String exeptionLog){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("exeption_log",exeptionLog).commit();
    }

    public static String getExeptionLog(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getString("exeption_log", "");
    }

    public static void setAutoConnectEnable(Context context,boolean isEnable){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putBoolean("auto_connect_enable",isEnable).commit();
    }

    public static boolean getAutoConnectEnable(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getBoolean("auto_connect_enable",true);
    }


    public static void setHotSpotRoomEnable(Context context,boolean isEnable){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putBoolean("hotspot_room_enable",isEnable).commit();
    }

    public static boolean getHotSpotRoomEnable(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getBoolean("hotspot_room_enable",true);
    }

    public static void setNetworkJson(Context context,String networkJson){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("networkJson",networkJson).commit();
    }

    public static String getNetworkJson(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getString("networkJson", "");
    }

    public static void setSelectedSSID(Context context, String localNetSSID){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("setSelectedSSID",localNetSSID).commit();
    }

    public static String getSelectedSSID(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getString("setSelectedSSID", "sun00");
    }
    public static void setSelectedSsidPass(Context context, String localNetPass){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("getSelectedSsidPass",localNetPass).commit();
    }

    public static String getSelectedSsidPass(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getString("getSelectedSsidPass", "sun01234");
    }

    public static void setLanguage(Context context, String language){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("language",language).commit();
    }

    public static String getLanguage(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getString("language", "en");
    }

    public static void setDeviceFiles(Context context, String language){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("DeviceFiles",language).commit();
    }

    public static String getDeviceFiles(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getString("DeviceFiles", "");
    }
    public static void setMachineAP(Context context, String language){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("MachineAP",language).commit();
    }

    public static String getMachineAP(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getString("MachineAP", "sun01");
    }
    public static void setMachineAPPass(Context context, String language){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("MachineAPPass",language).commit();
    }

    public static String getMachineAPPass(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getString("MachineAPPass", "");
    }





    public static void setDisForeverStatus(Context context, int noValves){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putInt("disForeverStatus",noValves).commit();
    }

    public static int getDisForeverStatus(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getInt("disForeverStatus", 1);
    }
    public static void setDis24HStatus(Context context, int noValves){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putInt("dis24HStatus",noValves).commit();
    }

    public static int getDis24HStatus(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getInt("dis24HStatus", 1);
    }

    public static void setLocalNetSSID(Context context, String localNetSSID){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("local_newtwork_ssid",localNetSSID).commit();
    }

    public static String getLocalNetSSID(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getString("local_newtwork_ssid", "sun00");
    }
    public static void setLocalNetPass(Context context, String localNetPass){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("local_newtwork_password",localNetPass).commit();
    }

    public static String getLocalNetPass(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getString("local_newtwork_password", "sun01234");
    }


    public static void setWifiStatus(Context context, int wifiStatus){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putInt("wifi_status",wifiStatus).commit();
    }

    public static int getWifiStatus(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        return mSharedPreferences.getInt("wifi_status", IrrigationWidgetProvider.WIFI_STATUS_DISCONNECTED);
    }

    public static void setLastConnectTryTime(Context context, Long miliSec){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putLong("wifi_connect_try_time",miliSec).commit();
    }

    public static int getLastConnectTryTime(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        Long miliSec=mSharedPreferences.getLong("wifi_connect_try_time", 0L);
        return Utils.elapsedHours(miliSec);
    }

    public static void setCurrentNetworkName(Context context, String currentNetWorkName){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        mSharedPreferences.edit().putString("current_network_name",currentNetWorkName).commit();
    }

    public static String getCurrentNetworkName(Context context){
        SharedPreferences mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, MODE_PRIVATE);
        String currentSsid = mSharedPreferences.getString("current_network_name", "");
        if(currentSsid.length()<=0) {
            currentSsid = WifiHelper.getCurrentSsid(context);
            setCurrentNetworkName(context,currentSsid);
        }
        return currentSsid;
    }

//    NETWORK_JSON
}
