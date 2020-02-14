//
//
//package com.ve.irrigation.irrigation.activities;
//
//import android.app.Service;
//import android.content.Context;
//import android.content.Intent;
//import android.net.DhcpInfo;
//import android.net.wifi.WifiManager;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.os.Handler;
//import android.provider.Settings;
//import android.support.v4.view.GestureDetectorCompat;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.text.Html;
//import android.text.format.Formatter;
//import android.util.Log;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.gson.Gson;
//
//import com.ve.irrigation.datavalues.FirstHeartBeat;
//import com.ve.irrigation.datavalues.HeartBeat;
//import com.ve.irrigation.datavalues.MySharedPreferences;
//import com.ve.irrigation.datavalues.WifiHotspot;
//import com.ve.irrigation.customview.CustomTextView;
//import com.ve.irrigation.customview.CustomTextViewLight;
//import com.ve.irrigation.hotspot.ClientScanResult;
//import com.ve.irrigation.hotspot.FinishScanListener;
//import com.ve.irrigation.hotspot.WifiApManager;
//import com.ve.irrigation.irrigation.adapters.AllConnectedDevice;
//import com.ve.irrigation.irrigation.DetailedModeFragment;
//import com.ve.irrigation.irrigation.MakeHttpRequest;
//import com.ve.irrigation.irrigation.R;
//import com.ve.irrigation.irrigation.RXSocketReceiver;
//import com.ve.irrigation.utils.Constants;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.InetSocketAddress;
//import java.net.MulticastSocket;
//import java.net.SocketException;
//import java.util.ArrayList;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.TimeUnit;
//
//import rx.Observable;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.functions.Action1;
//import rx.schedulers.Schedulers;
//
//
//public class MainActivity extends BaseActivity {
//    private static final String TAG = "--MainActivity";
//    WifiApManager wifiApManager;
//    private GestureDetectorCompat mDetector;
//    String LOGTAG = MainActivity.class.getSimpleName();
//
//    CustomTextView mTextViewProgress;
//    CustomTextView mTextViewTimer;
//    RecyclerView mRecyclerView_Status;
//    LinearLayout mLinearLayoutMainView;
//    CustomTextView mTextView_Pressure, mTextView_Flow, mTextView_RealVol, mTextView_TotalVol, mTextView_Pump, mTextView_Valve;
//    CustomTextViewLight mTextView_ConnectedHotspotName, mTextView_ConnectedTime, mTextView_HeartBeat;
//    TextView mTextView_NotificationArea;
//    AllConnectedDevice allConnectedDevice;
//
//    boolean shouldSocketConnect;
//    Toast mToast;
//    Handler handler;
//    Runnable runnable;
//
//    LinearLayout mBlue, mGreen, mRed;
//    ProgressBar progressBar;
//    LinearLayout mLayoutMain;
//    boolean mboolBlue, mboolGreen, mboolRed;
//    int mSwitchModeVal = 0;
//
//    ImageView mImageViewBlue, mImageViewGreen, mImageViewRed, mImageViewLed, mImageViewRemoteMode;
//    DetailedModeFragment detailedModeFragment;
//    private int[] imageResource = {R.mipmap.led_off, R.mipmap.led_red, R.mipmap.led_green, R.mipmap.led_blue, R.mipmap.led_white, R.mipmap.led_yellow, R.mipmap.led_cyan, R.mipmap.led_purple};
//    private ImageView imgExit;
//    int count;
//    int REQUEST_CODE_HOTSPOT = 2018;
//    WifiHotspot wifiHotspot;
//
//    private boolean mListen = true;
//    DatagramSocket socket;
//
//    /**
//     * Called when the activity is first created.
//     */
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        initViews();
//
//
//        mDetector = new GestureDetectorCompat(this, new MyGestureListner());
//
//
//        wifiApManager = new WifiApManager(this);
//
//        //wifiApManager.showWritePermissionSettings(true);
//
//        //progressBar.setVisibility(View.VISIBLE);
//        //mTextViewProgress.setVisibility(View.VISIBLE);
//        //mTextViewTimer.setVisibility(View.VISIBLE);
//        //mLinearLayoutMainView.setVisibility(View.INVISIBLE);
//        //mLayoutMain.setVisibility(View.INVISIBLE);
//
//        addFirstTimeNetwork();
//
//        try {
//            makeRSWA(mSwitchModeVal, 0);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
//    }
//
//
//    private void initViews() {
//        mRecyclerView_Status = findViewById(R.id.recyclerview_status);
//        mTextView_Pressure =  findViewById(R.id.textview_realactivity_pressure);
//        mTextView_Flow =  findViewById(R.id.textview_realactivity_flow);
//        mTextView_RealVol =  findViewById(R.id.textview_realactivity_realvol);
//        mTextView_TotalVol =  findViewById(R.id.textview_realactivity_totalvol);
//        mTextView_Pump =  findViewById(R.id.textview_realactivity_pump);
//        mTextView_Valve =  findViewById(R.id.textview_realactivity_valve);
//
//        mTextView_NotificationArea = findViewById(R.id.textview_notificationactivity);
//
//        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
//
//        mBlue = findViewById(R.id.layout_blueswitch);
//        mGreen =findViewById(R.id.layout_greenwitch);
//        mRed = findViewById(R.id.layout_redswitch);
//
//        mImageViewLed = findViewById(R.id.img_led);
//        mImageViewBlue = findViewById(R.id.img_blueswitch);
//        mImageViewGreen =findViewById(R.id.img_greenwitch);
//        mImageViewRed = findViewById(R.id.img_redswitch);
//        mImageViewRemoteMode = findViewById(R.id.img_remote_mode);
//
//
//        progressBar = findViewById(R.id.progressbar);
//        mTextViewProgress =  findViewById(R.id.tv_progress);
//        mTextViewTimer = findViewById(R.id.tv_timer);
//
//        mLinearLayoutMainView =  findViewById(R.id.ll_mainview);
//
//
//        mLayoutMain = findViewById(R.id.layout_main);
//        registerSwitchEvent();
//
//        imgExit = findViewById(R.id.img_exit);
//
//        imgExit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                finish();
//            }
//        });
//
//
//        mTextView_ConnectedHotspotName = findViewById(R.id.textView_connectedhotspotname);
//        mTextView_ConnectedTime =  findViewById(R.id.textview_connectedhotspottime);
//        mTextView_HeartBeat =  findViewById(R.id.textview_heartbeatcount);
//        detailedModeFragment = (DetailedModeFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_detail);
//
//
//    }
//
//    private void addFirstTimeNetwork() {
//        wifiHotspot = new WifiHotspot();
//        wifiHotspot.setSsid("sun00");
//        wifiHotspot.setPassword("sun01234");
//        wifiHotspot.setType("hotspot");
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        Log.i(TAG, "ON Resume");
//        mListen = true;
//
//        if (wifiHotspot.getType().equalsIgnoreCase("hotspot") && MySharedPreferences.getMySharedPreferences().isSimAvailable(this)) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                showMainStatusAfterTimeoutFailure("App can not start hotspot with current Android version");
//                setNavigationMessage("Hotspot " + wifiHotspot.getSsid() + "not connected,create manually");
//                startSyncIfStartedManually();
//            } else {
//                wifiApManager.showWritePermissionSettings(false);
//                setupHotSpot(wifiHotspot);
//            }
//        } else {
//            if (MySharedPreferences.getMySharedPreferences().isSimAvailable(this)) {
//                //mToast.setText("The app should connect to URL to use app");
//                setNavigationMessage("Android can connect to " + wifiHotspot.getSsid());
//                showMainStatusAfterTimeoutFailure("The app should connect to " + wifiHotspot.getSsid() + " to use app");
//            } else {
//                //mToast.setText("We found no sim card in your mobile, Please connect with URL");
//                setNavigationMessage("Android can connect to " + wifiHotspot.getSsid());
//                showMainStatusAfterTimeoutFailure("The app should connect to " + wifiHotspot.getSsid() + " to use app");
//            }
//
//        }
//
////         For Node js Emulator
//        sendBroadCastForAndroid8Above();
//
//    }
//
//    private void sendBroadCastForAndroid8Above() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    listernBroadcastNew();
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        //sendBroadcast("Hello I am here");
//                        sendBroadcast(getBroadcastMessage());
//                    }
//                }).start();
//            }
//        }, 2000);
//    }
//
//
//    private String getBroadcastMessage() {
//
//        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        final String ipAddress = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());
//        try {
//            JSONObject jsonObject = new JSONObject();
//
//            jsonObject.put("msg", 1);
//            jsonObject.put("magicno", 75139);
//            jsonObject.put("msgcount", 1);
//            jsonObject.put("ip", ipAddress);
//            jsonObject.put("port", Constants.Connection.PORT_NO);
//            return jsonObject.toString();
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return "";
//
//    }
//
//
//    public void listernBroadcastNew() throws JSONException {
//        try {
//            //Keep a socket open to listen to all the UDP trafic that is destined for this port
//            //socket = new DatagramSocket(Constants.Connection.PORT_NO, InetAddress.getByName("0.0.0.0"));
//            //socket.setBroadcast(true);
//
//            socket = new MulticastSocket(null);
//            socket.setReuseAddress(true);
//            socket.bind(new InetSocketAddress(Constants.Connection.PORT_NO));
//
//            final ConcurrentHashMap<String, String> braodcastConnectionIPs = new ConcurrentHashMap<>();
//
//            while (mListen) {
//
//                //Receive a packet
//                byte[] recvBuf = new byte[15000];
//                final DatagramPacket packet = new DatagramPacket(recvBuf, recvBuf.length);
//                socket.receive(packet);
//
//                //Packet received
//                final String data = new String(packet.getData()).trim();
//                Log.i(TAG, "Received data: " + data + " From: " + packet.getIpAddress().getHostAddress());
//                if (new JSONObject(data).has("msg")) {
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Service s;
//                            try {
//                                if (new JSONObject(data).getInt("msg")==2) {
//                                    braodcastConnectionIPs.put("IP", packet.getIpAddress().getHostAddress());
//                                    Log.i(TAG, "Put IP: " +packet.getIpAddress().getHostAddress());
//                                } else if (new JSONObject(data).getInt("msg")==3) {
//                                    Log.i(TAG, "Message is 3");
//                                    if(braodcastConnectionIPs.size()>0)
//                                        if (braodcastConnectionIPs.get("IP").contains(packet.getIpAddress().getHostAddress())) {
//                                            Log.i(TAG, "IP Contain");
//                                            HeartBeat heartBeat = new Gson().fromJson(data, HeartBeat.class);
//                                            updateUI(heartBeat);
//                                            detailedModeFragment.setValueFromHeartBeat(heartBeat);
//                                            try {
//                                                getGroupsData();
//                                            } catch (IOException e) {
//                                                e.printStackTrace();
//                                                Log.i(TAG, "grp exp"+e.toString());
//                                            }
//                                        }
//                                }else{
//                                    Log.i(TAG, "Else block");
//                                }
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                Log.i(TAG, "Exce: "+e.toString());
//                            }
//                        }
//                    });
//                }
//            }
//        } catch (IOException ex) {
//            Log.i(TAG, "Oops" + ex.getMessage());
//        }
//    }
//
//
//    public void sendBroadcast(String messageStr) {
//        DatagramSocket socket = null;
//        try {
//            socket = new DatagramSocket();
//            socket.setBroadcast(true);
//            byte[] data;
//
//            data = messageStr.getBytes();
//            DatagramPacket packet = new DatagramPacket(data, data.length, getBroadcastAddress(), Constants.Connection.PORT_NO);
//            socket.send(packet);
//        } catch (SocketException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    InetAddress getBroadcastAddress() throws IOException {
//        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
//        DhcpInfo dhcp = wifi.getDhcpInfo();
//        // handle null somehow
//
//        int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
//        byte[] quads = new byte[4];
//        for (int k = 0; k < 4; k++)
//            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
//        return InetAddress.getByAddress(quads);
//    }
//
//    private void startSyncIfStartedManually() {
//        Handler handler = new Handler();
//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
//                scan();
//            }
//        };
//        handler.postDelayed(runnable, 2000);
//
//    }
//
//
//    private void showMainStatusAfterTimeoutFailure(String message) {
//        progressBar.setVisibility(View.INVISIBLE);
//        mTextViewProgress.setVisibility(View.INVISIBLE);
//        mTextViewTimer.setVisibility(View.INVISIBLE);
//        mLayoutMain.setVisibility(View.VISIBLE);
//        mLinearLayoutMainView.setVisibility(View.VISIBLE);
//        mTextView_NotificationArea.setText(message);
//    }
//
//
//    private void setNavigationMessage(String message) {
//        mTextView_ConnectedHotspotName.setText(message);
//
//    }
//
//    private void setupHotSpot(WifiHotspot wifiHotspot) {
//
//        startTimeout();
//        setNavigationMessage("Hotspot " + wifiHotspot.getSsid() + " connected");
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
//            if (Settings.System.canWrite(MainActivity.this) == true) {
//                mTextViewProgress.setText("Starting Hotspot");
//                wifiApManager.setWifiApEnabled(null, true);
//                if (wifiApManager.setWifiApConfiguration(wifiHotspot.getSsid(), wifiHotspot.getPassword()))
//                    startTimer();
//            }
//        }
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            mTextViewProgress.setText("Starting Hotspot");
//            wifiApManager.setWifiApEnabled(null, true);
//            if (wifiApManager.setWifiApConfiguration(wifiHotspot.getSsid(), wifiHotspot.getPassword()))
//                startTimer();
//        }
//
//
//    }
//
//
//    private void scan() {
//        wifiApManager.getClientList(false, new FinishScanListener() {
//
//            @Override
//            public void onFinishScan(final ArrayList<ClientScanResult> clients) {
//                shouldSocketConnect = false;
//
//                for (ClientScanResult clientScanResult : clients) {
//                    if (clientScanResult.isReachable())
//                        shouldSocketConnect = true;
//                }
//
//                if (shouldSocketConnect) {
//                    settData(clients);
//                    progressBar.setVisibility(View.INVISIBLE);
//                    mTextViewProgress.setVisibility(View.INVISIBLE);
//                    mTextViewTimer.setVisibility(View.INVISIBLE);
//                    mLinearLayoutMainView.setVisibility(View.VISIBLE);
//                    mLayoutMain.setVisibility(View.VISIBLE);
//                    syncData(3001);
//                    setNavigationMessage("Manual Hotspot in use");
//
//                } else {
//                    setNavigationMessage("Error in manual hotspot");
//                    showMainStatusAfterTimeoutFailure("Module is not connected to Hotspot " + wifiHotspot.getSsid());
//                }
//            }
//        });
//    }
//
//    private void startTimeout() {
//        new CountDownTimer(20000, 1000) {
//            public void onTick(long millisUntilFinished) {
//                mTextViewTimer.setText("Seconds Remaining: " + millisUntilFinished / 1000);
//                if (wifiApManager.isWifiApEnabled())
//                    mTextViewProgress.setText("Hotspot is Enabled\n" + "Looking for Connected Module with App");
//            }
//            public void onFinish() {
//                mTextViewTimer.setText("Timeout!");
//            }
//        }.start();
//    }
//
//    private void settData(ArrayList<ClientScanResult> clientScanResults) {
//
//        allConnectedDevice = new AllConnectedDevice(clientScanResults, this);
//        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
//        mRecyclerView_Status.setHasFixedSize(true);
//
//        mRecyclerView_Status.setLayoutManager(mLinearLayoutManager);
//        mRecyclerView_Status.setAdapter(allConnectedDevice);
//
//
//    }
//
//
//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        this.mDetector.onTouchEvent(event);
//        return super.onTouchEvent(event);
//    }
//
//    public class MyGestureListner extends GestureDetector.SimpleOnGestureListener {
//
//        @Override
//        public boolean onDown(MotionEvent e) {
//            return super.onDown(e);
//        }
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2,float velocityX, float velocityY) {
//
//            switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
//                case 1:
//                    Intent i = new Intent(MainActivity.this, ConfigurationScreenActivity.class);
//                    startActivity(i);
//                    overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
//                    Log.d(LOGTAG, "top");
//                    return true;
//                case 2:
//                    Intent iLeft = new Intent(MainActivity.this, SwipeLeftActivity.class);
//                    startActivity(iLeft);
//                    overridePendingTransition(R.anim.slide_left, R.anim.slide_right);
//                    Log.d(LOGTAG, "left");
//
//                    return true;
//                case 3:
//                    Log.d(LOGTAG, "down");
//                    Intent iDown = new Intent(MainActivity.this, HotspotListActivity.class);
//                    startActivityForResult(iDown, REQUEST_CODE_HOTSPOT);
//                    overridePendingTransition(R.anim.slide_up2, R.anim.slide_down2);
//                    Log.d(LOGTAG, "right");
//                    return true;
//                case 4:
//                    Intent iRight = new Intent(MainActivity.this, SwipeRightActivity.class);
//                    startActivity(iRight);
//                    overridePendingTransition(R.anim.slide_left2, R.anim.slide_right2);
//                    Log.d(LOGTAG, "right");
//
//                    return true;
//            }
//            return false;
//        }
//
//        private int getSlope(float x1, float y1, float x2, float y2) {
//            Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
//            if (angle > 45 && angle <= 135)
//                // top
//                return 1;
//            if (angle >= 135 && angle < 180 || angle < -135 && angle > -180)
//                // left
//                return 2;
//            if (angle < -45 && angle >= -135)
//                // down
//                return 3;
//            if (angle > -45 && angle <= 45)
//                // right
//                return 4;
//            return 0;
//        }
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        mListen = false;
//
//        if (socket != null) {
//            if (socket.isConnected())
//                socket.disconnect();
//            socket = null;
//        }
//
//
//    }
//
//
//    private void syncData(int port) {
//        final RXSocketReceiver newClass = new RXSocketReceiver();
//
//        try {
//            socket = new MulticastSocket(null);
//            socket.setReuseAddress(true);
//            socket.bind(new InetSocketAddress(port));
//        } catch (SocketException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Observable.interval(0, 3000, TimeUnit.MILLISECONDS)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Action1<Long>() {
//                    public void call(Long aLong) {
//                        // here is the task that should repeat
//                        newClass.request(socket)
//                                .subscribeOn(Schedulers.io())
//                                .observeOn(AndroidSchedulers.mainThread())
//                                .subscribe(new Action1<String>() {
//                                    @Override
//                                    public void call(String flag) {
//                                        JSONObject jsonObject = null;
//                                        try {
//                                            jsonObject = new JSONObject(flag);
//                                            if (jsonObject.has("destip")) {
//                                                FirstHeartBeat heartBeat = new Gson().fromJson(flag, FirstHeartBeat.class);
//                                                mTextView_NotificationArea.setText("First heartbear arrived at " + MySharedPreferences.getMySharedPreferences().getDateTime(Long.parseLong(heartBeat.getTs()) * 1000));
//                                                mTextView_ConnectedTime.setText(MySharedPreferences.getMySharedPreferences().getDateTime(Long.parseLong(heartBeat.getTs()) * 1000));
//                                            } else {
//                                                HeartBeat heartBeat = new Gson().fromJson(flag, HeartBeat.class);
//                                                updateUI(heartBeat);
//                                                detailedModeFragment.setValueFromHeartBeat(heartBeat);
//                                            }
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                    }
//                                }, new Action1<Throwable>() {
//                                    @Override
//                                    public void call(Throwable ex) {
//                                        Log.e("Heartbeat", "Unable to sync", ex);
//                                    }
//                                });
//                    }
//                });
//    }
//
//    private void updateUI(HeartBeat heartBeat) {
//        count = count + 1;
//        setRealData(heartBeat);
//        mTextView_NotificationArea.setText("Last heartbeat arrived at " + MySharedPreferences.getMySharedPreferences().getDateTime(Long.parseLong(heartBeat.getTs()) * 1000));
//        mTextView_ConnectedTime.setText(MySharedPreferences.getMySharedPreferences().getDateTime(Long.parseLong(heartBeat.getTs()) * 1000));
//        mTextView_HeartBeat.setText("Heart Beat No:" + count);
//    }
//
//
//    private void setRealData(HeartBeat heartBeat) {
//        String pumpStatus = (Integer.parseInt(heartBeat.getPump())) == 1 ? "On" : "Off";
////        String valveStatus = (Integer.parseInt(heartBeat.getValve1())) == 1 ? "On" : "Off";
//        String pressure = "<font color=#094316>Pressure: </font> <font color=#1e3224>" + heartBeat.getPress() + "</font>";
//        if (heartBeat.getPress() != null)
//            mTextView_Pressure.setText(Html.fromHtml(pressure));
//        else {
//            mTextView_Pressure.setText("Pressure: 0");
//        }
//
//        String flow = "<font color=#094316>Flow: </font> <font color=#1e3224>" + heartBeat.getFlow() + "</font>";
//        mTextView_Flow.setText(Html.fromHtml(flow));
//
//
//        String realVol = "<font color=#094316>Real Vol: </font> <font color=#1e3224>" + heartBeat.getRqdvol() + "</font>";
//        mTextView_RealVol.setText(Html.fromHtml(realVol));
//
//        String totalVol = "<font color=#094316>Total Vol: </font> <font color=#1e3224>" + heartBeat.getTtvol() + "</font>";
//        mTextView_TotalVol.setText(Html.fromHtml(totalVol));
//
//        String pump = "<font color=#094316>Pump: </font> <font color=#1e3224>" + pumpStatus + "</font>";
//        mTextView_Pump.setText(Html.fromHtml(pump));
//
////        String valve = "<font color=#094316>Valve: </font> <font color=#1e3224>" + valveStatus + "</font>";
////        mTextView_Valve.setText(Html.fromHtml(valve));
//
//    }
//
//
//    private void startTimer() {
//
//
//        handler = new Handler();
//        runnable = new Runnable() {
//            @Override
//            public void run() {
//                scan();
//            }
//        };
//        handler.postDelayed(runnable, 20000);
//
//    }
//
//
//    private void registerSwitchEvent() {
//        mImageViewRemoteMode.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (mSwitchModeVal == 0) {
//                    mImageViewRemoteMode.setImageResource(R.mipmap.mode_on);
//                    mSwitchModeVal = 1;
//                } else {
//                    mImageViewRemoteMode.setImageResource(R.mipmap.mode_off);
//                    mSwitchModeVal = 0;
//                }
//            }
//        });
//
//
//        mBlue.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (!mboolBlue) {
//                    mImageViewBlue.setImageResource(R.mipmap.blue_button_on);
//                } else {
//                    mImageViewBlue.setImageResource(R.mipmap.blue_button_off);
//                }
//                mboolBlue = !mboolBlue;
//                callSwitchfunctions();
//            }
//        });
//
//
//        mGreen.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                if (!mboolGreen) {
//                    mImageViewGreen.setImageResource(R.mipmap.green_button_on);
//                } else {
//                    mImageViewGreen.setImageResource(R.mipmap.green_button_off);
//                }
//                mboolGreen = !mboolGreen;
//                callSwitchfunctions();
//
//            }
//        });
//
//        mRed.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!mboolRed) {
//                    mImageViewRed.setImageResource(R.mipmap.red_button_on);
//                } else {
//                    mImageViewRed.setImageResource(R.mipmap.red_button_off);
//                }
//                mboolRed = !mboolRed;
//                callSwitchfunctions();
//            }
//        });
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        wifiApManager.setWifiApEnabled(null, false);
//    }
//
//
//    private void makeRSWA(int remoteNo, int switchCode) throws IOException {
//        MakeHttpRequest.getMakeHttpRequest().sendRequest(Constants.Url.BASE_URL+"rswa/" + remoteNo + "/" + switchCode).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
//            @Override
//            public void call(String s) {
//                Log.i("RSWA res: ", s);
//                setLEDColor();
//            }
//        });
//    }
//
//
//
//    private void getGroupsData() throws IOException {
//        MakeHttpRequest.getMakeHttpRequest().sendRequest("http://192.168.2.26:5000/command/test/grep/2").subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
//            @Override
//            public void call(String s) {
//                Log.i("Group res: ", s);
//            }
//        });
//    }
//
//
//    private void callSwitchfunctions() {
//        try {
//            if (mboolRed == true && mboolGreen == false && mboolBlue == false) {
//                makeRSWA(mSwitchModeVal, 1);
//            } else if (mboolRed == true && mboolGreen == true && mboolBlue == false) {
//                makeRSWA(mSwitchModeVal, 3);
//            } else if (mboolRed == false && mboolGreen == true && mboolBlue == false) {
//                makeRSWA(mSwitchModeVal, 2);
//            } else if (mboolRed == false && mboolGreen == true && mboolBlue == true) {
//                makeRSWA(mSwitchModeVal, 6);
//            } else if (mboolRed == false && mboolGreen == false && mboolBlue == true) {
//                makeRSWA(mSwitchModeVal, 4);
//            } else if (mboolRed == false && mboolGreen == false && mboolBlue == false) {
//                makeRSWA(mSwitchModeVal, 0);
//            }
//        } catch (IOException e) {
//        }
//    }
//
//
//    private void setLEDColor() {
//
//        if (mboolRed == true && mboolGreen == false && mboolBlue == false) {
//            setLed(1);
//        } else if (mboolRed == true && mboolGreen == true && mboolBlue == false) {
//            setLed(3);
//        } else if (mboolRed == false && mboolGreen == true && mboolBlue == false) {
//            setLed(2);
//        } else if (mboolRed == false && mboolGreen == true && mboolBlue == true) {
//            setLed(6);
//        } else if (mboolRed == false && mboolGreen == false && mboolBlue == true) {
//            setLed(4);
//        } else if (mboolRed == false && mboolGreen == false && mboolBlue == false) {
//            setLed(0);
//        }
//    }
//
//    private void setLed(int index) {
//        mImageViewLed.setImageResource(imageResource[index]);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 2018) {
//            if (resultCode == RESULT_OK) {
//                WifiHotspot wifiHotspotNew = (WifiHotspot) data.getSerializableExtra("result");
//                try {
//                    wifiHotspot = (WifiHotspot) wifiHotspotNew.clone();
//                } catch (CloneNotSupportedException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//}
