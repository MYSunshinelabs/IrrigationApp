package com.ve.irrigation.utils;

import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;

import com.ve.irrigation.customview.CustomTextViewLight;
import com.ve.irrigation.customview.CustomTextViewLightBold;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.listners.NumberListener;

/**
 * Created by dalvendrakumar on 19/2/19.
 */

public class IrrigationNumberPicker implements View.OnClickListener, View.OnLongClickListener, View.OnTouchListener {
    private int REQUEST_CODE;
    private int MAX_NUMBER_LIMIT=120;
    private int MIN_NUMBER_LIMIT=10;
    private NumberListener  listener;
    private View lytTimePicker;
    private int currentValue;
    private CustomTextViewLightBold txtValue;
    private Handler handler;

    private boolean mAutoIncrement = false;
    private boolean mAutoDecrement = false;

    public IrrigationNumberPicker(NumberListener listener, int currentValue, View lytTimePicker) {
        this.listener = listener;
        this.currentValue = currentValue;
        this.lytTimePicker = lytTimePicker;
        inItTimePicker();
    }

    public IrrigationNumberPicker(NumberListener listener, int currentValue, View lytTimePicker,int REQUEST_CODE) {
        this.listener = listener;
        this.currentValue = currentValue;
        this.lytTimePicker = lytTimePicker;
        this.REQUEST_CODE=REQUEST_CODE;
        inItTimePicker();
    }

    private void inItTimePicker(){

        handler=new Handler();

        txtValue=lytTimePicker.findViewById(R.id.txtValue);
        txtValue.setText(currentValue+"");

        lytTimePicker.findViewById(R.id.txtMinuse).setOnClickListener(this);
        lytTimePicker.findViewById(R.id.txtPlus).setOnClickListener(this);
        lytTimePicker.findViewById(R.id.txtSetValue).setOnClickListener(this);

        lytTimePicker.findViewById(R.id.txtMinuse).setOnLongClickListener(this);
        lytTimePicker.findViewById(R.id.txtPlus).setOnLongClickListener(this);


        lytTimePicker.findViewById(R.id.txtPlus).setOnTouchListener(this);
        lytTimePicker.findViewById(R.id.txtMinuse).setOnTouchListener(this);

//        lytTimePicker.findViewById(R.id.txtPlus).setOnTouchListener( new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)
//                        && mAutoIncrement ){
//                    mAutoIncrement = false;
//                }
//                return false;
//            }
//        });
//        lytTimePicker.findViewById(R.id.txtMinuse).setOnTouchListener( new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)
//                        && mAutoDecrement ){
//                    mAutoDecrement = false;
//                }
//                return false;
//            }
//        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtMinuse:
                currentValue= Integer.parseInt(txtValue.getText().toString());
                if (currentValue==MIN_NUMBER_LIMIT)
                    currentValue=MAX_NUMBER_LIMIT;
                else
                    currentValue=currentValue-1;
                txtValue.setText(currentValue+"");
                break;

            case R.id.txtPlus:
                currentValue= Integer.parseInt(txtValue.getText().toString());
                if(currentValue==MAX_NUMBER_LIMIT)
                    currentValue=MIN_NUMBER_LIMIT;
                else
                    currentValue=currentValue+1;
                txtValue.setText(currentValue+"");
                break;

            case R.id.txtSetValue:
                if(REQUEST_CODE<=0)
                    listener.onSetNumber(currentValue);
                else
                    listener.onSetNumber(REQUEST_CODE,currentValue);
        }

    }
    @Override
    public boolean onLongClick(View v) {
        switch (v.getId()){
            case R.id.txtMinuse:
                currentValue= Integer.parseInt(txtValue.getText().toString());
                mAutoDecrement=true;
                mAutoIncrement=false;
                handler.post(new RptUpdater());
                break;

            case R.id.txtPlus:
                currentValue= Integer.parseInt(txtValue.getText().toString());
                mAutoDecrement=false;
                mAutoIncrement=true;
                handler.post(new RptUpdater());
                break;
        }
        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (v.getId()){
            case R.id.txtMinuse:
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)
                        && mAutoDecrement ){
                    mAutoDecrement = false;
                }
                break;

            case R.id.txtPlus:
                if( (event.getAction()==MotionEvent.ACTION_UP || event.getAction()==MotionEvent.ACTION_CANCEL)
                        && mAutoIncrement ){
                    mAutoIncrement = false;
                }
                break;
        }
        return false;
    }

    public void setMAX_NUMBER_LIMIT(int MAX_NUMBER_LIMIT) {
        this.MAX_NUMBER_LIMIT = MAX_NUMBER_LIMIT;
    }

    public void setMIN_NUMBER_LIMIT(int MIN_NUMBER_LIMIT) {
        this.MIN_NUMBER_LIMIT = MIN_NUMBER_LIMIT;
    }



    class RptUpdater implements Runnable {
        public void run() {
            if( mAutoIncrement ){
                if(currentValue==MAX_NUMBER_LIMIT)
                    currentValue=MIN_NUMBER_LIMIT;
                else
                    currentValue=currentValue+1;
                handler.postDelayed( new RptUpdater(), 100 );
            } else if( mAutoDecrement ){
                if (currentValue==MIN_NUMBER_LIMIT)
                    currentValue=MAX_NUMBER_LIMIT;
                else
                    currentValue=currentValue-1;
                handler.postDelayed( new RptUpdater(), 100 );
            }
            txtValue.setText(currentValue+"");
        }
    }

}
