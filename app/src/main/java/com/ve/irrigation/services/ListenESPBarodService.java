package com.ve.irrigation.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.annotation.Nullable;

import com.ve.irrigation.Receiver.ApkStateReceiver;
import com.ve.irrigation.Receiver.EspErrorListReceiver;
import com.ve.irrigation.Receiver.EspNetCommMsgReceiver;
import com.ve.irrigation.Receiver.EspResponseReceiver;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.utils.Constants;
import com.ve.irrigation.utils.HeartBeatTask;
import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;
import com.ve.irrigation.widget.IrrigationWidgetProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dalvendrakumar on 3/12/18.
 */

public class ListenESPBarodService extends IntentService implements ApkStateReceiver.ApkStateObserver {
    private static final String TAG = ListenESPBarodService.class.getSimpleName();
    private MulticastSocket socket;
    public static boolean isListen=true;
    private int countRetryMsgBroadcast=0;
    InetAddress inetAddress = null;
    private TimerTask timerTask;
    private Timer timer;
    private ApkStateReceiver receiver;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public ListenESPBarodService(String name) {
        super(name);
    }

    public ListenESPBarodService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utils.printLog(TAG," onCreate");

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        sendBroadcast(getBroadcastMessage());
        receiver=ApkStateReceiver.registerReceiver(this,this);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        try {
            listernESPResponse();
        } catch (JSONException e) {
            Utils.printLog(TAG+" onHandleIntent",e.getStackTrace().toString());
        }
    }

    public void listernESPResponse() throws JSONException {
        try {
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            //socket = new DatagramSocket(Constants.Connection.PORT_NO, InetAddress.getByName("0.0.0.0"));
            //socket.setBroadcast(true);
            if(socket==null)
                socket = new MulticastSocket(null);

            socket.setReuseAddress(true);
            socket.bind(new InetSocketAddress(Constants.Connection.PORT_NO));

            final ConcurrentHashMap<String, String> braodcastConnectionIPs = new ConcurrentHashMap<>();

            while (isListen) {

                if(isListen==false) {
                    stopSelf();
                    socket=null;
                }
                //Receive a packet
                byte[] recvBuf = new byte[15000];
                final DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);
                //Packet received
                final String data = new String(packet.getData()).trim();

                String hostAddress=packet.getAddress().getHostAddress();
                Preferences.setHostIpAddress(this,hostAddress);
                Utils.printLog(TAG, "Received data: " + data + " From: " + hostAddress);

                EspResponseReceiver.sendBroadcast(this,data);

                JSONObject jsonObject=new JSONObject(data);

                if (jsonObject.has("msg")) {
                    int msgId=jsonObject.getInt("msg");
                    if (msgId==2) {
                        EspNetCommMsgReceiver.sendBroadcast(this,"Connected "+Preferences.getCurrentNetworkName(this)+  " / "+hostAddress+" Port : "+Constants.Connection.PORT_NO);
                        braodcastConnectionIPs.put("IP", packet.getAddress().getHostAddress().toString());
                        String msg="Response Recived From: "+hostAddress+"Port: "+Constants.Connection.PORT_NO;
                        Utils.createNotification(this,getString(R.string.app_name),msg);
                        EspErrorListReceiver.sendBroadcast(this,msg);
                    }
                }else if(jsonObject.has("metric")){

//                    String hbAck=getHBAckMsg();
//                    byte []ackMsg=hbAck.getBytes();
                    Preferences.setHeartBeat(this,data);
                    Preferences.setWifiStatus(this, IrrigationWidgetProvider.WIFI_STATUS_CONNECT);
                    IrrigationWidgetProvider.updateWidget(this);
//                    InetSocketAddress remAddr = new InetSocketAddress(hostAddress,Constants.Connection.PORT_NO);
//                    DatagramPacket packetAck = new DatagramPacket(ackMsg,ackMsg.length,remAddr);
//                    socket.send(packetAck);
                    HeartBeatTask.countHB++;

                    EspNetCommMsgReceiver.sendBroadcast(this,"Connected "+Preferences.getCurrentNetworkName(this)+  " / "+hostAddress+" Port : "+Constants.Connection.PORT_NO);
//                    Utils.printLog(TAG," Heart Beat Ack :- "+hbAck);
                }
            }
        } catch (Exception e){
            Utils.printLog(TAG,e.getMessage());
            String msg=e.getMessage()+" Ip Mobile"+Utils.getDeviceIpAddress(this)+"Host Ip"+Preferences.getHostIpAddress(this)+" Port : "+Constants.Connection.PORT_NO;
            EspErrorListReceiver.sendBroadcast(this,msg);
            isListen=false;
            stopSelf();
        }
    }

    private void sendHbAckBroadcast(){
        String hbAck=getHBAckMsg();
        byte []ackMsg=hbAck.getBytes();
        String hostAddress=Preferences.getHostIpAddress(this);
        InetSocketAddress remAddr = new InetSocketAddress(hostAddress,Constants.Connection.PORT_NO);
        DatagramPacket packetAck = new DatagramPacket(ackMsg,ackMsg.length,remAddr);
        try {
            socket.send(packetAck);
            ++HeartBeatTask.countHB;
            EspNetCommMsgReceiver.sendBroadcast(this,"Connected "+Preferences.getCurrentNetworkName(this)+  " / "+hostAddress+" Port : "+Constants.Connection.PORT_NO);
            Utils.printLog(TAG," Heart Beat Ack :- "+hbAck);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getHBAckMsg() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ts", Utils.elapsedSecFromMidnight());
            jsonObject.put("rc", ++HeartBeatTask.countACK);
            jsonObject.put("apkstate", isListen==true? 1:0);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void sendBroadcast(final String messageStr) {
        Utils.printLog(TAG,"sendBroadcast() with "+messageStr);
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(Constants.Connection.PORT_NO);
            socket.setBroadcast(true);
            byte[] data;

            data = messageStr.getBytes();
            inetAddress = getBroadcastAddress();
            DatagramPacket packet = new DatagramPacket(data, data.length, getBroadcastAddress(), Constants.Connection.PORT_NO);
            socket.send(packet);

            if(countRetryMsgBroadcast==0) {
                String msg="Send Hello Message Ip Address " + Utils.getDeviceIpAddress(this) + " BroadCast Address " + inetAddress.getHostAddress() + " Port : " + Constants.Connection.PORT_NO;
                Utils.createNotification(this, "Irrigation App",msg );
                EspErrorListReceiver.sendBroadcast(this,msg);
            }

            EspNetCommMsgReceiver.sendBroadcast(this,getString(R.string.msg_waiting_for_response));
            socket.disconnect();
            socket.close();
        }catch (Exception e) {
            Utils.printLog(TAG,"sendBroadcast "+e.getMessage());
            String msg="";
            if (inetAddress!=null)
                msg=e.getMessage()+" Ip Address "+Utils.getDeviceIpAddress(this)+" BroadCast Address "+inetAddress.getHostAddress()+" Port : "+Constants.Connection.PORT_NO;
            Utils.createNotification(this,"Irrigation App",msg);
            EspErrorListReceiver.sendBroadcast(this,msg);
            if(socket!=null){
                socket.disconnect();
                socket.close();
            }
        }

    }

    private String getBroadcastMessage() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("msg", 1);
            jsonObject.put("magicno", Constants.Connection.MAGIC_NO);
            jsonObject.put("msgcount", 1);
            jsonObject.put("ip", Utils.getDeviceIpAddress(this));
            jsonObject.put("port", Constants.Connection.PORT_NO);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";

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


    public void startTimer(String msg) {
        try {
            timer=null;
            //set a new Timer
            timer = new Timer();
            //initialize the TimerTask's job
            initializeTimerTask(msg);
            //schedule the timer, after the first 10000ms the TimerTask will run every 10000ms
            timer.schedule(timerTask, 10000); //
        }catch (Exception e){
            Utils.printLog(TAG,e.getMessage());
        }
    }

    private void initializeTimerTask(final String msg){

        timerTask = new TimerTask() {
            public void run() {
                if(HeartBeatTask.countHB<=0) {
                    if(countRetryMsgBroadcast>2){
                        EspNetCommMsgReceiver.sendBroadcast(ListenESPBarodService.this, getString(R.string.msg_unable_to_connect));
                        isListen=false;
                        countRetryMsgBroadcast=0;
                        stoptimertask();
                        stopSelf();
                    }else {
                        countRetryMsgBroadcast++;
                        sendBroadcast(msg);
                        String errorMsg="Retry count " + countRetryMsgBroadcast + " For Send Hello Message Ip Address " + Utils.getDeviceIpAddress(ListenESPBarodService.this) + " BroadCast Address " + inetAddress.getHostAddress() + " Port : "+Constants.Connection.PORT_NO;
                        Utils.createNotification(ListenESPBarodService.this, "Irrigation App", errorMsg);
                        EspErrorListReceiver.sendBroadcast(ListenESPBarodService.this,errorMsg);
                        EspNetCommMsgReceiver.sendBroadcast(ListenESPBarodService.this, getString(R.string.msg_waiting_for_response));
                    }
                }
            }
        };
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onDestroy() {
        Utils.printLog(TAG,"onDestroy()");
        HeartBeatTask.isListen=false;
        HeartBeatTask.countHB=0;
        HeartBeatTask.countACK=0;
if(socket!=null){
    socket.disconnect();
    socket.close();
}
        if(receiver!=null)
            ApkStateReceiver.unregisterReceiver(receiver,getBaseContext());
        ListenESPBarodService.super.onDestroy();
    }

    @Override
    public void onApkState(boolean isActive) {
        isListen=isActive;
//        sendHbAckBroadcast();
    }

}
