package com.ve.irrigation.irrigation.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.GestureDetectorCompat;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.ve.irrigation.datavalues.ConfiguratonModel;
import com.ve.irrigation.datavalues.ConnectionSourceData;
import com.ve.irrigation.datavalues.WifiHotspot;
import com.ve.irrigation.hotspot.WifiApManager;
import com.ve.irrigation.irrigation.MakeHttpRequest;
import com.ve.irrigation.irrigation.adapters.HotSpotAdapter;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.utils.Constants;
import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class HotspotListActivity extends BaseActivity implements HotSpotAdapter.HotspotListener {


    private static final String TAG = HotspotListActivity.class.getSimpleName();
    private RecyclerView mRecyclerView_HotspotList;
    private HotSpotAdapter hotSpotAdapter;
    private GestureDetectorCompat mDetector;
    private WifiApManager wifiApManager;
    private RelativeLayout lytBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hotspot_list);

        wifiApManager = new WifiApManager(this);
        mDetector = new GestureDetectorCompat(this, new HotspotListActivity.MyGestureListner());

        initViews();

        List<ConnectionSourceData> connectionSourceData = Utils.getHotspotList(HotspotListActivity.this);

        if (connectionSourceData.size() > 0){
            showHotspotList(connectionSourceData);
            setData(false);
        }else
            setData(true);
    }

    private void initViews() {
        mRecyclerView_HotspotList = findViewById(R.id.recyclerview_hotspotlist);
        lytBar=findViewById(R.id.lytBar);
    }

    //    http://www.sunshinelabs.nl/apk/docfile.js
    private void setData(boolean isProgressBar) {
        if(isProgressBar)
            lytBar.setVisibility(View.VISIBLE);
        else
            lytBar.setVisibility(View.GONE);

        MakeHttpRequest.getMakeHttpRequest().sendRequest(Constants.Url.HOT_SPOT_LIST_URL).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String response) {
                lytBar.setVisibility(View.GONE);
                Utils.printLog(TAG,response);
                Preferences.setHotspotdocfile(HotspotListActivity.this,response);
                showHotspotList(Utils.getHotspotList(HotspotListActivity.this));
            }
        });
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                final List<ConnectionSourceData> connectionSourceDatas = AppDataBase.getAppDataBase(HotspotListActivity.this).connectionSourceDAO().getAllConnectionSource();
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        hotSpotAdapter = new HotSpotAdapter(HotspotListActivity.this, connectionSourceDatas);
//                        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(HotspotListActivity.this);
//                        mRecyclerView_HotspotList.setHasFixedSize(true);
//                        mRecyclerView_HotspotList.setLayoutManager(mLinearLayoutManager);
//                        mRecyclerView_HotspotList.setAdapter(hotSpotAdapter);
//                    }
//                });
//            }
//        }).start();
    }

    private void showHotspotList(List<ConnectionSourceData> connectionSourceDataList) {
        hotSpotAdapter = new HotSpotAdapter(HotspotListActivity.this, connectionSourceDataList);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(HotspotListActivity.this);
        mRecyclerView_HotspotList.setHasFixedSize(true);
        mRecyclerView_HotspotList.setLayoutManager(mLinearLayoutManager);
        mRecyclerView_HotspotList.setAdapter(hotSpotAdapter);
    }

    @Override
    public void setUpHotSpot(WifiHotspot wifiHotspot) {
        sendResultFromSelectedHotspot(wifiHotspot);
        finish();
    }


    public class MyGestureListner extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2,
                               float velocityX, float velocityY) {

            switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
                case 1:
                    finish();
                    HotspotListActivity.this.overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                    return true;
                case 2:

                    return true;
                case 3:

                    return true;
                case 4:

                    return true;
            }
            return false;
        }

        private int getSlope(float x1, float y1, float x2, float y2) {
            Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
            if (angle > 45 && angle <= 135)
                // top
                return 1;
            if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
                // left
                return 2;
            if (angle < -45 && angle >= -135)
                // down
                return 3;
            if (angle > -45 && angle <= 45)
                // right
                return 4;
            return 0;
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        HotspotListActivity.this.overridePendingTransition(R.anim.slide_up, R.anim.slide_down);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void sendResultFromSelectedHotspot(WifiHotspot wifiHotspot) {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", wifiHotspot);
        setResult(Activity.RESULT_OK, returnIntent);
    }

    //  Currently this method not in use
    private ConfiguratonModel getConnectionsFromfile() {
        SharedPreferences mSharedPreferences = getSharedPreferences("myconfigdata", MODE_PRIVATE);
        String responseInString = mSharedPreferences.getString("configdata", "");
        Gson gson = new Gson();
        ConfiguratonModel modelResponse = gson.fromJson(responseInString, ConfiguratonModel.class);
        return modelResponse;
    }


}
