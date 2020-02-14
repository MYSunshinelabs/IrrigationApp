package com.ve.irrigation.irrigation.activities;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ve.irrigation.Receiver.CurrentLocationReceiver;
import com.ve.irrigation.datavalues.AppDataBase;
import com.ve.irrigation.datavalues.Wifi;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.adapters.WifiLocationAdapterNew;
import com.ve.irrigation.utils.DialogUtils;
import com.ve.irrigation.utils.LocationHelper;
import com.ve.irrigation.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class EditWifiListActivity extends AppCompatActivity implements View.OnClickListener, CurrentLocationReceiver.LocationObserver, WifiLocationAdapterNew.WifiLocationListner {
    private static final String TAG = EditWifiListActivity.class.getSimpleName();
    private List<Wifi> wifis=new ArrayList<>();
    private WifiLocationAdapterNew mAdapter;
    private RecyclerView recyclerView;
    private CurrentLocationReceiver receiver;
    private String longtitude="",latitude="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_wifi_list);

        init();

    }

    private void init() {
        wifis = LocationHelper.wifis;

        receiver=CurrentLocationReceiver.registerReceiver(this,EditWifiListActivity.this);

        recyclerView=findViewById(R.id.recyclerView);
        mAdapter= new WifiLocationAdapterNew(wifis,this);
        LinearLayoutManager manager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        if(wifis.size()>50)
            findViewById(R.id.btnEdit).setVisibility(View.INVISIBLE);

        findViewById(R.id.btnSave).setOnClickListener(this);
        findViewById(R.id.btnEdit).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnSave:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        saveChanges();
                    }
                }).start();

                onBackPressed();
                break;
            case R.id.btnEdit:

                if(wifis.size()>=50)
                    findViewById(R.id.btnEdit).setVisibility(View.INVISIBLE);
                else
                    findViewById(R.id.btnEdit).setVisibility(View.VISIBLE);

                wifis.add(new Wifi("","","",""));
                mAdapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(wifis.size() - 1);
                break;
        }
    }

    private void saveChanges() {
        for (Wifi wifi:wifis){
            if(wifi.getId()<=0)
                AppDataBase.getAppDataBase(getApplicationContext()).wifiDAO().insert(wifi);
            else
                AppDataBase.getAppDataBase(getApplicationContext()).wifiDAO().updatewifi(wifi);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                LocationHelper.wifis= AppDataBase.getAppDataBase(getApplicationContext()).wifiDAO().getAllWifi();
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CurrentLocationReceiver.unregisterReceiver(receiver,this);
    }

    @Override
    public void onNewLocation(Location location) {
        latitude=location.getLatitude()+"";
        longtitude=location.getLongitude()+"";
        Utils.printLog(TAG,"onNewLocation "+latitude+" "+longtitude);
    }

    @Override
    public void setCurrentLocation(Wifi wifi) {
        if(longtitude.length()>0 && latitude.length()>0){
            wifi.setLatitude(latitude);
            wifi.setLongtitude(longtitude);
            mAdapter.notifyDataSetChanged();
        }else
            Utils.showSnackbar(recyclerView,getString(R.string.error_msg_location));
    }

    @Override
    public void removeWifiLocation(final Wifi wifi) {
        DialogUtils.showConfirmationDialog(this, getString(R.string.msg_confirmation_delete), new Runnable() {
            @Override
            public void run() {
                removeWifi(wifi);
            }
        }).show();
    }

    private void removeWifi(final Wifi wifi){
        wifis.remove(wifi);
        if(wifis.size()<5)
            findViewById(R.id.btnEdit).setVisibility(View.VISIBLE);
        mAdapter.notifyDataSetChanged();
        if(wifi.getId()>0)
            new Thread(new Runnable() {
                @Override
                public void run() {
                    AppDataBase.getAppDataBase(getApplicationContext()).wifiDAO().deleteWifi(wifi);
                }
            }).start();
    }
}
