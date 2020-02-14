package com.ve.irrigation.utils;

import android.view.View;

import com.ve.irrigation.customview.CustomTextViewLight;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.listners.TimeListener;
/**
 * Created by dalvendrakumar on 19/2/19.
 */

public class IrrigationTimePicker implements View.OnClickListener {
    public static boolean is24HourFormate=true;
    private int HOUR_LIMIT=12;
    private TimeListener listener;
    private View lytTimePicker;
    private String time;
    private CustomTextViewLight txtAMPM,txtMinute,txtHour;

    public IrrigationTimePicker(TimeListener listener, String time, View lytTimePicker) {
        this.listener = listener;
        this.time = time;
        this.lytTimePicker = lytTimePicker;
        inItTimePicker();
    }

    private void inItTimePicker(){

        txtHour=lytTimePicker.findViewById(R.id.txtHour);
        txtMinute=lytTimePicker.findViewById(R.id.txtMinute);
        txtAMPM=lytTimePicker.findViewById(R.id.txtAMPM);

        lytTimePicker.findViewById(R.id.txtMinuseHours).setOnClickListener(this);
        lytTimePicker.findViewById(R.id.txtMinuseMinute).setOnClickListener(this);
        lytTimePicker.findViewById(R.id.txtPlusHours).setOnClickListener(this);
        lytTimePicker.findViewById(R.id.txtPlusMinute).setOnClickListener(this);
        lytTimePicker.findViewById(R.id.txtAMPM).setOnClickListener(this);
        lytTimePicker.findViewById(R.id.txtDoneTime).setOnClickListener(this);

        if(is24HourFormate){
            txtAMPM.setVisibility(View.GONE);
            HOUR_LIMIT=24;
        }else {
            txtAMPM.setVisibility(View.VISIBLE);
            HOUR_LIMIT=12;
        }

        setTime(time);

    }

    private void setTime(String time) {
        String amPm="";
        if(time.length()>5 && !is24HourFormate) {
            amPm=time.substring(6,7);
            time = time.replace("AM", "");
            time = time.replace("PM", "");
        }
        time=time.trim();
        String[] splitedTime=time.split(":");
        txtHour.setText(splitedTime[0].trim());
        txtMinute.setText(splitedTime[1].trim());
    }

    @Override
    public void onClick(View v) {
        int hour;
        int minute;
        switch (v.getId()){
            case R.id.txtMinuseHours:
                hour= Integer.parseInt(txtHour.getText().toString());
                if (hour==0)
                    hour=HOUR_LIMIT;
                txtHour.setText((hour-1)+"");
                break;

            case R.id.txtPlusHours:
                hour= Integer.parseInt(txtHour.getText().toString());
                if(hour==HOUR_LIMIT)
                    hour=0;
                txtHour.setText((hour+1)+"");
                break;

            case R.id.txtAMPM:
                if(txtAMPM.getText().toString().equals("AM"))
                    txtAMPM.setText("PM");
                else
                    txtAMPM.setText("AM");
                break;

            case R.id.txtMinuseMinute:
                minute= Integer.parseInt(txtMinute.getText().toString());
                if(minute==0)
                    minute=61;
                txtMinute.setText((minute-1)+"");
                break;

            case R.id.txtPlusMinute:
                minute= Integer.parseInt(txtMinute.getText().toString());
                if(minute==60)
                    minute=0;
                txtMinute.setText((minute+1)+"");
                break;

            case R.id.txtDoneTime:
                String time=txtHour.getText().toString()+":"+txtMinute.getText().toString();
                if(!is24HourFormate)
                    time=time+" "+txtAMPM.getText().toString();
                listener.onSetTime(time);
                break;
        }
    }

}
