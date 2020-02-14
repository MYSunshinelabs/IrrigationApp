package com.ve.irrigation.utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.StrictMode;

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
 * Created by dalvendrakumar on 6/12/18.
 */

public class HBWidgetTask extends AsyncTask<Void,Void,Void> {
    private static final String TAG = HBWidgetTask.class.getSimpleName();
    private Context context;
    private MulticastSocket socket;
    private int countRetryMsgBroadcast=0;
    public static int countHB=0;
    public static int countACK=0;
    public static boolean isListen=false;
    InetAddress inetAddress=null;
    private TimerTask timerTask;
    private Timer timer;
    private Handler handlerNoRes=new Handler();
    private Runnable runnableNoRes;


    public HBWidgetTask(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        countRetryMsgBroadcast=0;
        Utils.printLog(TAG,"onPreExecute() ");
    }

    @Override
    protected Void doInBackground(Void... voids) {
        Utils.printLog(TAG,"doInBackground() ");
        try{
            sendBroadcast(getBroadcastMessage());
            String status= listernESPResponse();
            Utils.printLog(TAG,status);
        }catch (Exception e){
            Utils.printLog(TAG,"Exception "+e.getMessage());
            e.printStackTrace();
        }
        return null;
    }



    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        Utils.printLog(TAG,"onPostExecute() ");
        isListen=false;
        disConnectSocket();
    }
    public void disConnectSocket(){
        countHB=0;
        countACK=0;
        if (socket!=null){
            socket.disconnect();
            socket.close();
        }
    }
    public String listernESPResponse() throws JSONException {
        try {
            //Keep a socket open to listen to all the UDP trafic that is destined for this port
            //socket = new DatagramSocket(3009, InetAddress.getByName("0.0.0.0"));
            //socket.setBroadcast(true);
            isListen=true;
            if(socket==null)
                socket = new MulticastSocket(null);
            socket.setReuseAddress(true);
            //            InetAddress group = InetAddress.getByName("192.168.137.1");
            //            socket.joinGroup(group);

            socket.bind(new InetSocketAddress(Constants.Connection.PORT_NO));
            final ConcurrentHashMap<String, String> braodcastConnectionIPs = new ConcurrentHashMap<>();
            while (isListen) {
                //Receive a packet
                byte[] recvBuf = new byte[15000];
                final DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
                socket.receive(packet);
                //Packet received
                final String data = new String(packet.getData()).trim();

                String hostAddress=packet.getAddress().getHostAddress();
                Preferences.setHostIpAddress(context,hostAddress);

                Utils.printLog(TAG, "Received data: " + data + " From: " + hostAddress);

                JSONObject jsonObject=new JSONObject(data);

                if (jsonObject.has("msg")) {

                    int msgId=jsonObject.getInt("msg");

                    if (msgId==2) {
                        braodcastConnectionIPs.put("IP", packet.getAddress().getHostAddress());
                        String msg="Response Recived From: "+hostAddress+"Port: "+Constants.Connection.PORT_NO;
                    }
                }else if(jsonObject.optString("magicno").equalsIgnoreCase("75319")){
                    if(runnableNoRes!=null)
                        handlerNoRes.removeCallbacks(runnableNoRes);
                    else
                        runnableNoRes=getRunnableNoRes();
                    handlerNoRes.postDelayed(runnableNoRes,40*1000);
                    Preferences.setHeartBeat(context, data);
                    Preferences.setWifiStatus(context, IrrigationWidgetProvider.WIFI_STATUS_CONNECT);
                    IrrigationWidgetProvider.updateWidget(context);
                    String hbAck=getHBAckMsg();
                    byte []ackMsg=hbAck.getBytes();
                    InetSocketAddress remAddr = new InetSocketAddress(hostAddress,Constants.Connection.PORT_NO);
                    DatagramPacket packetAck = new DatagramPacket(ackMsg,ackMsg.length,remAddr);
                    socket.send(packetAck);
                    countHB++;
                    Utils.printLog(TAG," Heart Beat Ack :- "+hbAck);
                }
                if(!isListen) {
                    Utils.printLog(TAG,"listernESPResponse() Finished");
                    return "close";
                }
            }

        } catch (Exception ex) {
            Utils.printLog(TAG,ex.getMessage());
            isListen=false;
        }
        return "";
    }

    private Runnable getRunnableNoRes(){
        return  new Runnable() {
            @Override
            public void run() {
                Utils.printLog(TAG,"runnableNoRes HBWidgetTask canceled");
                Utils.stopWidgetHBTask(context);
            }
        };
    }
    public void sendHBAckMsg(){
        isListen=false;
        String hbAck=getHBAckMsg();
        byte []ackMsg=hbAck.getBytes();
        InetSocketAddress remAddr = new InetSocketAddress(Preferences.getHostIpAddress(context),Constants.Connection.PORT_NO);
        DatagramPacket packetAck = new DatagramPacket(ackMsg,ackMsg.length,remAddr);
        try {
            socket.send(packetAck);
        } catch (Exception e) {
            Utils.printLog(TAG,e.toString());
        }

        if(isListen==false)
            disConnectSocket();
    }

    private String getHBAckMsg() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("ts", Utils.elapsedSecFromMidnight());
            jsonObject.put("rc", ++countACK);
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
            inetAddress = getBroadcastAddress();
            socket = new DatagramSocket(Constants.Connection.PORT_NO);
            socket.setBroadcast(true);
            byte[] data;

            data = messageStr.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, inetAddress,Constants.Connection.PORT_NO);
            socket.send(packet);
            socket.getPort();
            if(countHB<=0)
                startTimer(messageStr);
            socket.disconnect();
            socket.close();

        }catch (Exception e) {
            Utils.printLog(TAG,"sendBroadcast "+e.getMessage());
            startTimer(messageStr);
            if(socket!=null) {
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
            jsonObject.put("ip", Utils.getDeviceIpAddress(context));
            jsonObject.put("port", Constants.Connection.PORT_NO);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";

    }

    public void startTimer(String msg) {
        try {
            //set a new Timer
//            timer = new Timer();
//            //initialize the TimerTask's job
//            initializeTimerTask(msg);
//            //schedule the timer, after the first 10000ms the TimerTask will run every 10000ms
//            timer.schedule(timerTask, 10000); //
        }catch (Exception e){
            Utils.printLog(TAG,e.getMessage());
        }
    }

    private void initializeTimerTask(final String msg){
        try{
            timerTask = new TimerTask() {
                public void run() {
                    if(countHB<=0) {
                        if(countRetryMsgBroadcast>2){
                            isListen=false;
                            stoptimertask();
                            countRetryMsgBroadcast=0;
                        }else {
                            countRetryMsgBroadcast++;
                            sendBroadcast(msg);
                        }
                    }
                }
            };
        }catch (Exception e){
            Utils.printLog(TAG,e.toString());
        }
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    InetAddress getBroadcastAddress() throws IOException {
        WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcp = wifi.getDhcpInfo();
        // handle null somehow

        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++)
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        return InetAddress.getByAddress(quads);
    }


}
