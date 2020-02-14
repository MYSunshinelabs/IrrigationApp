package com.ve.irrigation.irrigation;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.Formatter;
import android.util.Log;

import com.ve.irrigation.customview.CustomTextView;
import com.ve.irrigation.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ConnectLocalHost extends AppCompatActivity {

    DatagramSocket socket;
    Handler handler;
    Runnable runnable;
    CustomTextView mCustomTextView;
    String TAG = "connectlocalhost";
    private boolean mListen=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_connect_local_host);
        mCustomTextView =  findViewById(R.id.textview_heartbeat);


        new Thread(new Runnable() {
            @Override
            public void run() {
                listernBroadcastNew();
            }
        }).start();


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        //sendBroadcast("Hello I am here");
                        sendBroadcast(getBroadcastMessage());
                    }
                }).start();

            }
        }, 2000);


    }


    private String getBroadcastMessage() {

        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        final String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
        try {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("msgid", 1);
            jsonObject.put("magicno", 75139);
            jsonObject.put("msgcount", 1);
            jsonObject.put("ip", ipAddress);
            jsonObject.put("port0", Constants.Connection.PORT_NO);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";

    }


    private void makeLocalRequestToUpdate(String ip) {

        Log.i(TAG," url: "+"http://" + ip + ":8081/?tmode=laxmikant");
        MakeHttpRequest.getMakeHttpRequest().sendRequest("http://" + ip + ":8081/?tmode=laxmikant").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.i("localres: ", s);

            }
        });


    }


    public void sendBroadcast(String messageStr) {
        DatagramSocket socket = null;
        try {

            socket = new DatagramSocket();
            socket.setBroadcast(true);
            byte[] data;

            data = messageStr.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, getBroadcastAddress(), 3000);
            socket.send(packet);


        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }


    public void listernBroadcastNew() {
        try {
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            socket = new DatagramSocket(3000, InetAddress.getByName("0.0.0.0"));
            socket.setBroadcast(true);

            while (mListen) {

                //Receive a packet
                byte[] recvBuf = new byte[15000];
                DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);

                //Packet received
                String data = new String(packet.getData()).trim();
                Log.i(TAG, "Received data: " + data+" From: "+packet.getAddress().getHostAddress());


            }
        } catch (IOException ex) {
            Log.i(TAG, "Oops" + ex.getMessage());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mListen = false;
        if(socket!=null&&socket.isConnected())
        {
            socket.disconnect();
            socket=null;
        }
    }
}



