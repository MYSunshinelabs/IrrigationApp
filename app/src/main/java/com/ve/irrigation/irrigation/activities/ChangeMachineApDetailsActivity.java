package com.ve.irrigation.irrigation.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.ve.irrigation.irrigation.MakeHttpRequest;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.utils.Constants;
import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
public class ChangeMachineApDetailsActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = ChangeMachineApDetailsActivity.class.getSimpleName();
    private EditText edtSSID,edtPass;
    Button btnSave,btnReset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_change_machine_ap_details);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        btnSave=findViewById(R.id.btnSave);
        btnReset=findViewById(R.id.btnReset);
        edtSSID=findViewById(R.id.edtSSID);
        edtPass=findViewById(R.id.edtPass);

        btnSave.setOnClickListener(this);
        btnReset.setOnClickListener(this);
        findViewById(R.id.btnSSID).setOnClickListener(this);
        findViewById(R.id.btnPass).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        String urlCommand = null;
        switch (v.getId()){
            case R.id.btnSSID:
                if(edtSSID.getText().toString().length()>2) {
                    urlCommand = "http://" + Preferences.getHostIpAddress(this) + ":" + Constants.Connection.COMMAND_PORT_NO + Constants.Commands.COMMAND_DB_SET_SSID+ edtSSID.getText().toString();
                    if(!btnSave.isEnabled())
                        btnSave.setEnabled(true);

                    if(!btnReset.isEnabled())
                        btnReset.setEnabled(true);
                }else
                    edtSSID.setError("Invalid SSID");
                break;

            case R.id.btnPass:
                if(edtPass.getText().toString().length()>2) {
                    urlCommand = "http://" + Preferences.getHostIpAddress(this) + ":" + Constants.Connection.COMMAND_PORT_NO + Constants.Commands.COMMAND_DB_SET_PSWD+ edtPass.getText().toString();
                    if(!btnSave.isEnabled())
                        btnSave.setEnabled(true);
                    if(!btnReset.isEnabled())
                        btnReset.setEnabled(true);
                }else
                    edtPass.setError("Invalid password");
                break;

            case R.id.btnSave:
                urlCommand="http://"+ Preferences.getHostIpAddress(this)+":"+ Constants.Connection.COMMAND_PORT_NO + Constants.Commands.COMMAND_DB_SAVE;
                break;
            case R.id.btnReset:
                break;
        }
        if(urlCommand!=null)
            setConnection(urlCommand);
    }

    private void setConnection(String url){
        Utils.printLog(TAG,"setConnection "+url);
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                if(s.length()>0)
                    Utils.printLog(TAG,s);
            }
        });
    }
}
