package com.ve.irrigation.utils;

/**
 * Created by dalvendrakumar on 23/10/18.
 */


public class Constants {

    public static int DEVICE_HEIGHT;

    public static int DEVICE_WIDTH;

    public interface RequestCode{
        int RESTART_APP=101;
        int CONFIG_NETWORK=102;
    }

    public interface ResultCode{
        int RESTART_APP=201;
        int CONFIG_NETWORK=202;
    }

    public interface Url{
        String BASE_URL="http://192.168.43.252:80/";
        String PORT_COMMAND="5000";
        String HOT_SPOT_LIST_URL="http://www.sunshinelabs.nl/apk/docfile.js";
    }

    public interface Commands{
        String COMMAND_V_SET="/R/vset/";
        String COMMAND_V_REP="/R/vrep/";
        String COMMAND_V_SAVE="/R/vsave/";
        String COMMAND_G_SCHED="/R/gsched/";
        String COMMAND_P_SCHED="/R/psched/";

        String COMMAND_V_OPEN="/R/vopen/";
        String COMMAND_V_CLOSE="/R/vclose/";
        String COMMAND_G_OPEN="/R/gopen/";
        String COMMAND_G_CLOSE="/R/gclose/";
        String COMMAND_DATA_HB="/R/datahb/";
        String COMMAND_DB_SAVE="/R/dbsav/";
        String COMMAND_DB_SET_SSID="/R/dbset/ssid/";
        String COMMAND_DB_SET_PSWD="/R/dbset/pswd/";
        String COMMAND_DLIST="/R/dlist/";
        String COMMAND_MODE="/R/mode/";
        String COMMAND_STIME="/R/stime/";
        String COMMAND_PUMP="/R/pump/";
    }

    public  interface JobId{
        int JOB_HEART_BEAT=1001;
        int JOB_HEART_BEAT_WIDGET=2001;
        int JOB_LOCATION=3001;
    }

    public interface Connection{
        int MAGIC_NO=75139;
//        int PORT_NO=3009;
        int PORT_NO=30086;
        int COMMAND_PORT_NO =30080 ;
    }

    public interface Configration{
        int MIN_VOLUME=50;
        int MAX_VOLUME=5000;
        int MIN_DURATION_MINUTES=1;
        int MAX_DURATION_MINUTES=120;
        String NOT_BEFORE_DEFAULT="05:00";
        String NOT_AFTER_DEFAULT="18:00";
    }


}
