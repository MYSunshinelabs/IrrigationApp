package com.ve.irrigation.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.NumberPicker;

import com.ve.irrigation.datavalues.Schedule;
import com.ve.irrigation.customview.CustomTextViewLight;
import com.ve.irrigation.irrigation.R;

/**
 * Created by dalvendrakumar on 25/1/19.
 */

public class DialogUtils {

    public static AlertDialog.Builder showScheduleValueDialog(Context context, String msg, final boolean isVolume, final Schedule schedule, final Runnable runnableSave){
        final AlertDialog.Builder builder= new AlertDialog.Builder(context,R.style.AlertDialogTheme);
        builder.setTitle(context.getString(R.string.app_name));
        builder.setIcon(R.mipmap.ic_launcher);

        final EditText edittext = new EditText(context);
        edittext.setHint(msg);
        edittext.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(edittext);
        builder.setPositiveButton("save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value=edittext.getText().toString();
                if(value.length()>0) {
                    if (isVolume)
                        schedule.setVolume(value + "Ltr");
                    else
                        schedule.setMinutes(value + "Sec");
                    runnableSave.run();
                }else
                    edittext.setError("Please enter valid input");
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder;
    }

    public static AlertDialog.Builder showConfirmationDialog(Context context, String msg, final Runnable runnableSure){
        final AlertDialog.Builder builder= new AlertDialog.Builder(context,R.style.AlertDialogTheme);
        builder.setTitle(context.getString(R.string.app_name));
        builder.setMessage(msg);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setPositiveButton("Sure", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                runnableSure.run();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder;
    }

    public static void showNumPicker(Context context,int[] location,int currentValue,NumberPicker.OnValueChangeListener listener){
        final Dialog dialog = new Dialog(context);
//        d.setTitle("NumberPicker");

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        WindowManager.LayoutParams wmlp = dialog.getWindow().getAttributes();

//        wmlp.gravity = Gravity.TOP | Gravity.LEFT;

        wmlp.x = location[1];   //x position
        wmlp.y = location[0];   //y position

        dialog.setContentView(R.layout.dialog_number_picker);
        CustomTextViewLight txtCancel= dialog.findViewById(R.id.txtCancel);
        CustomTextViewLight txtDone = dialog.findViewById(R.id.txtDone);
        final NumberPicker np = dialog.findViewById(R.id.numberPicker);
        np.setMaxValue(Constants.Configration.MAX_VOLUME);
        np.setMinValue(Constants.Configration.MIN_VOLUME);
        np.setValue(currentValue);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(listener);

        txtCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        txtDone.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                dialog.dismiss(); // dismiss the dialog
            }
        });


        dialog.show();
    }
}
