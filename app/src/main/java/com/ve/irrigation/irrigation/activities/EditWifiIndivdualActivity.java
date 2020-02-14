package com.ve.irrigation.irrigation.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ve.irrigation.datavalues.AppDataBase;
import com.ve.irrigation.datavalues.Wifi;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.databinding.ActivityEditIndividualBinding;
import com.ve.irrigation.utils.DialogUtils;

public class EditWifiIndivdualActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = EditWifiIndivdualActivity.class.getSimpleName();
    private Wifi wifi;
    private boolean isAddNew=false;
    private ActivityEditIndividualBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= DataBindingUtil.setContentView(this,R.layout.activity_edit_individual);

        init();

    }

    private void init() {

        if(getIntent().getExtras()!=null)
            wifi = (Wifi) getIntent().getExtras().getSerializable("Device");

        if(wifi!=null) {
            isAddNew = true;
            binding.setWifi(wifi);
        }else
            isAddNew=false;

        findViewById(R.id.btnSave).setOnClickListener(this);
        findViewById(R.id.imgRemoveWifiLocation).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave:
                if(binding.edtName.getText().toString().length()<3){
                    binding.edtName.setError("Please enter valid name.");
                    return;
                }

                if(wifi==null)
                    wifi=new Wifi();

                wifi.setName(binding.edtName.getText().toString());
                wifi.setSsid(binding.edtSSID.getText().toString());
                wifi.setPassword(binding.edtpassword.getText().toString());
                wifi.setIpAddress(binding.edtIpAddress.getText().toString());
//                wifi.setPort(binding.edtPort.getText().toString());

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveChanges();
                    }
                }).start();
                setResult();

                break;
            case R.id.imgRemoveWifiLocation:
                showAlert(wifi);
                break;
        }
    }
    private void setResult(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent();
                intent.putExtra("Device",wifi);
                setResult(202,intent);
                onBackPressed();

            }
        },200);
    }
    private void saveChanges() {
        if(isAddNew)
            AppDataBase.getAppDataBase(getApplicationContext()).wifiDAO().updatewifi(wifi);
        else
            AppDataBase.getAppDataBase(getApplicationContext()).wifiDAO().insert(wifi);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showAlert(final Wifi wifi) {
        DialogUtils.showConfirmationDialog(this, getString(R.string.msg_confirmation_delete), new Runnable() {
            @Override
            public void run() {
                removeWifi(wifi);
            }
        }).show();
    }

    private void removeWifi(final Wifi wifi){
        if(wifi.getId()>0)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AppDataBase.getAppDataBase(getApplicationContext()).wifiDAO().deleteWifi(wifi);
                }
            }).start();
        setResult();
    }

}
