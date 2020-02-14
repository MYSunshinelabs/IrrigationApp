package com.ve.irrigation.irrigation.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ve.irrigation.datavalues.AppDataBase;
import com.ve.irrigation.datavalues.WifiHotspot;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.databinding.ActivityWifiListBinding;
import com.ve.irrigation.utils.LocationHelper;
import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;
import com.ve.irrigation.utils.WifiManager;

public class WifiListActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = WifiListActivity.class.getSimpleName();
    private ActivityWifiListBinding binding;
    private Button[] ssidBtn= new Button[5];
    private boolean isAutoConnectEnable,isHotspotRoomEnable;
    private WifiHotspot wifiHotspot;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding= DataBindingUtil.setContentView(this,R.layout.activity_wifi_list);

        init();

        initClickListener();

        populateWifiList();

    }

    private void toggleSSIDBtn() {
        try{
            int listSize=LocationHelper.wifis.size();

            if(listSize<=0)
                return;

            for(int i=0;i<5;i++){
                if(i<listSize) {
                    ssidBtn[i].setText(LocationHelper.wifis.get(i).getSsid());
                    ssidBtn[i].setVisibility(View.VISIBLE);
                }else
                    ssidBtn[i].setVisibility(View.INVISIBLE);
            }
        }catch (Exception e){
            Utils.printLog(TAG,e.getMessage());
        }
    }

    private void populateWifiList(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                LocationHelper.wifis.clear();
                LocationHelper.wifis= AppDataBase.getAppDataBase(getApplicationContext()).wifiDAO().getAllWifi();
            }
        }).start();
    }

    private void init() {

        ssidBtn[0]=binding.btnSSID1;
        ssidBtn[1]=binding.btnSSID2;
        ssidBtn[2]=binding.btnSSID3;
        ssidBtn[3]=binding.btnSSID4;
        ssidBtn[4]=binding.btnSSID5;

        isAutoConnectEnable= Preferences.getAutoConnectEnable(this);
        isHotspotRoomEnable= Preferences.getHotSpotRoomEnable(this);


        if(isAutoConnectEnable)
            binding.btnAuto.setBackgroundResource(R.drawable.background_auto_mode);
        else
            binding.btnAuto.setBackgroundResource(R.drawable.background_manual_mode);

        if(isHotspotRoomEnable)
            binding.btnHotSpotRoom.setBackgroundResource(R.drawable.background_auto_mode);
        else
            binding.btnHotSpotRoom.setBackgroundResource(R.drawable.background_manual_mode);
    }

    private void initClickListener() {
        binding.btnAuto.setOnClickListener(this);
        binding.btnHotSpotRoom.setOnClickListener(this);
        binding.btnCrud.setOnClickListener(this);
        binding.btnHotSpot.setOnClickListener(this);

        binding.btnSSID1.setOnClickListener(this);
        binding.btnSSID2.setOnClickListener(this);
        binding.btnSSID3.setOnClickListener(this);
        binding.btnSSID4.setOnClickListener(this);
        binding.btnSSID5.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        toggleSSIDBtn();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnAuto:
                if(isAutoConnectEnable) {
                    binding.btnAuto.setBackgroundResource(R.drawable.background_auto_mode);
                    Utils.startLocationService(this);
                }else {
                    binding.btnAuto.setBackgroundResource(R.drawable.background_manual_mode);
                    Utils.stopLocationService(this);
                }
                isAutoConnectEnable=!isAutoConnectEnable;
                Preferences.setAutoConnectEnable(this,isAutoConnectEnable);

                break;
            case R.id.btnHotSpotRoom:
                if(isHotspotRoomEnable)
                    binding.btnHotSpotRoom.setBackgroundResource(R.drawable.background_auto_mode);
                else
                    binding.btnHotSpotRoom.setBackgroundResource(R.drawable.background_manual_mode);
                isHotspotRoomEnable=!isHotspotRoomEnable;
                Preferences.setHotSpotRoomEnable(this,isHotspotRoomEnable);

                break;
            case R.id.btnCrud:
                startActivity(new Intent(this,EditWifiListActivity.class));
                break;
            case R.id.btnHotSpot:
                openHotSpot();
                break;
            case R.id.btnSSID1:
                WifiManager.connectTo(this,LocationHelper.wifis.get(0));
                break;
            case R.id.btnSSID2:
                WifiManager.connectTo(this,LocationHelper.wifis.get(1));
                break;
            case R.id.btnSSID3:
                WifiManager.connectTo(this,LocationHelper.wifis.get(2));
                break;
            case R.id.btnSSID4:
                WifiManager.connectTo(this,LocationHelper.wifis.get(3));
                break;
            case R.id.btnSSID5:
                WifiManager.connectTo(this,LocationHelper.wifis.get(4));
                break;
        }
    }

    private void openHotSpot() {
        wifiHotspot = new WifiHotspot();
        wifiHotspot.setSsid("sun00");
        wifiHotspot.setPassword("sun01234");
        wifiHotspot.setType("hotspot");
    }

}
