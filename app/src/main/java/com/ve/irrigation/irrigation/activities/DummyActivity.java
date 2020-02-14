package com.ve.irrigation.irrigation.activities;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;

import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.services.TcpClient;
import com.ve.irrigation.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class DummyActivity extends BaseActivity {
    TcpClient mTcpClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dummy);
        new ConnectTask().execute("");
        findViewById(R.id.txtSendMsg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //sends the message to the server
                if (mTcpClient != null) {
                    mTcpClient.sendMessage(getBroadcastMessage());
                }
            }
        });

    }
    private WifiManager.LocalOnlyHotspotReservation mReservation;

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void turnOnHotspot() {
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        manager.startLocalOnlyHotspot(new WifiManager.LocalOnlyHotspotCallback() {

            @Override
            public void onStarted(WifiManager.LocalOnlyHotspotReservation reservation) {
                super.onStarted(reservation);
                mReservation = reservation;
            }

            @Override
            public void onStopped() {
                super.onStopped();
            }

            @Override
            public void onFailed(int reason) {
                super.onFailed(reason);
            }
        }, new Handler());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void turnOffHotspot() {
        if (mReservation != null) {
            mReservation.close();
        }
    }

    private String getBroadcastMessage() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", 1);
            jsonObject.put("magicno", Constants.Connection.MAGIC_NO);
            jsonObject.put("msgcount", 1);
            jsonObject.put("ip", "192168.6.229");
            jsonObject.put("port", Constants.Connection.PORT_NO);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";

    }

    public class ConnectTask extends AsyncTask<String, String, TcpClient> {
        @Override
        protected TcpClient doInBackground(String... message) {
            //we create a TCPClient object
            mTcpClient = new TcpClient(new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    publishProgress(message);
                }
            });
            mTcpClient.run();
            return null;
        }

        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            //response received from server
            Log.d("test", "response " + values[0]);
            //process server response here....
        }
    }
}
