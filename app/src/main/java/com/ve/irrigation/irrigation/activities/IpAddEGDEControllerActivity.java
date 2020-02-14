package com.ve.irrigation.irrigation.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import com.ve.irrigation.customview.CustomEditTextView;
import com.ve.irrigation.customview.CustomTextViewLight;
import com.ve.irrigation.irrigation.MakeHttpRequest;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.utils.Constants;
import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class IpAddEGDEControllerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = IpAddEGDEControllerActivity.class.getSimpleName();
    private CustomTextViewLight txtChange,txtLoadDeviceFile,txtDeleteDeviceFile;
    private CustomEditTextView edtIpAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_ip_add_egdecontroller);

        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        txtChange=findViewById(R.id.txtChange);
        txtLoadDeviceFile=findViewById(R.id.txtLoadDevice);
        txtDeleteDeviceFile=findViewById(R.id.txtDeleteDevice);
        edtIpAdd=findViewById(R.id.edtIpAdd);


        txtChange.setOnClickListener(this);
        txtLoadDeviceFile.setOnClickListener(this);
        txtDeleteDeviceFile.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(Preferences.getDeviceFiles(this).equals(""))
            txtDeleteDeviceFile.setVisibility(View.GONE);
        else
            txtDeleteDeviceFile.setVisibility(View.VISIBLE);

        edtIpAdd.setText(Preferences.getHostIpAddress(this));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtChange:

                break;
            case R.id.txtLoadDevice:
                loadDevices();
                break;
            case R.id.txtDeleteDevice:
                Preferences.setDeviceFiles(this,"");
                break;
        }
    }

    private void loadDevices() {
        String url="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Connection.COMMAND_PORT_NO+Constants.Commands.COMMAND_DLIST;
        Utils.printLog(TAG,"setConnection "+url);
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                if(s.length()>0) {
                    Utils.printLog(TAG, s);
                    Preferences.setDeviceFiles(getApplicationContext(),s);
                    txtDeleteDeviceFile.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}
