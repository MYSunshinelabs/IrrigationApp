package com.ve.irrigation.irrigation.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ve.irrigation.datavalues.Group;
import com.ve.irrigation.datavalues.Valve;
import com.ve.irrigation.irrigation.MakeHttpRequest;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.adapters.ValveAssignmentAdapter;
import com.ve.irrigation.irrigation.adapters.ValveAssignmentAdapterNew;
import com.ve.irrigation.irrigation.adapters.ValveAssignmentListener;
import com.ve.irrigation.utils.Constants;
import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class ValveAssignmentActivity extends BaseActivity implements ValveAssignmentListener, View.OnClickListener, ValveAssignmentAdapterNew.ValveListener {
    private static final String TAG = ValveAssignmentActivity.class.getSimpleName();
    private ArrayList<Group> valveArrayList=new ArrayList<>();
    private int noGroups,noValve;
    private ValveAssignmentAdapterNew mAdapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_valve_assignment);

        init();

    }

    private void init() {
        noGroups =Preferences.getNoGroups(this);
        noValve =Preferences.getNoValves(this);
        try {
            valveArrayList= Utils.getGroupValvesList(noGroups,noValve,new JSONArray(Preferences.getVXG(this)));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mAdapter= new ValveAssignmentAdapterNew(valveArrayList, noValve, this);
        LinearLayoutManager manager=new LinearLayoutManager(this);

        findViewById(R.id.btnSave).setOnClickListener(this);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(manager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        if(Preferences.getEditableValveAssignment(this)){
            findViewById(R.id.frameEditProtector).setVisibility(View.GONE);
        }else {
            findViewById(R.id.frameEditProtector).setVisibility(View.VISIBLE);
            findViewById(R.id.btnSave).setVisibility(View.GONE);
        }
    }

    private void setValveChanges(int valveId,int groupId) {
        final String url="http://"+ Preferences.getHostIpAddress(this)+":"+Constants.Connection.COMMAND_PORT_NO+Constants.Commands.COMMAND_V_SET+valveId+"/"+groupId;
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String response) {
                response.length();
            }
        });

    }
    private void setValveChanges(String valveId,int groupId) {
        final String url="http://"+ Preferences.getHostIpAddress(this)+":"+Constants.Connection.COMMAND_PORT_NO+Constants.Commands.COMMAND_V_SET+groupId+valveId;
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String response) {
                response.length();
            }
        });

    }

    private void getValvesTable() {
        final String url="http://"+ Preferences.getHostIpAddress(this)+":"+Constants.Connection.COMMAND_PORT_NO+Constants.Commands.COMMAND_V_REP;
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String response) {
            }
        });
    }

    private void saveValveChanges() {
        final String url="http://"+ Preferences.getHostIpAddress(this)+":"+Constants.Connection.COMMAND_PORT_NO+Constants.Commands.COMMAND_V_SAVE;
        MakeHttpRequest.getMakeHttpRequest().sendRequest(url).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<String>() {
            @Override
            public void call(String response) {
                setResult(Constants.ResultCode.RESTART_APP);
                ValveAssignmentActivity.this.finish();
            }
        });
    }

    @Override
    public void onGroup(String groupId) {

    }

    @Override
    public void onAssignmentChange(int valveId, int groupId) {
        setValveChanges(valveId,groupId);

    }

    @Override
    public void onClick(View v) {
//        Utils.valveAssignment(this,valveArrayList);
        saveValveChanges();

    }

    @Override
    public void onValveChange(Group group) {

        String valve="";
        for (int i=0;i<group.getValves().size();i++)
            if(group.getValves().get(i).isEnable())
                valve=valve+"/"+(group.getValves().get(i).getValveId()-1);
        setValveChanges(valve,(group.id-1));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void restartApp(){
        finishAffinity();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(ValveAssignmentActivity.this,SplashActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        },2000);
//        System.exit(0);
    }
}
