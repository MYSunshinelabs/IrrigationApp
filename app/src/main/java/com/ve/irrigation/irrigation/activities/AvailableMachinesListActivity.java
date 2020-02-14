package com.ve.irrigation.irrigation.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;

import com.ve.irrigation.datavalues.Machine;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.adapters.MachinesListAdapter;
import com.ve.irrigation.irrigation.listners.SwipeListener;
import com.ve.irrigation.utils.IrrigationGestureDetector;
import com.ve.irrigation.utils.Utils;

import java.util.ArrayList;


public class AvailableMachinesListActivity extends BaseActivity implements SwipeListener, MachinesListAdapter.MachineListener {
    private static final String TAG = AvailableMachinesListActivity.class.getSimpleName();
    private GestureDetectorCompat mDetector;
    private ArrayList<Machine> machineArrayList=new ArrayList<>();
    private MachinesListAdapter adapter;
    private Machine machine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_available_machines_list);

        machineArrayList.add(new Machine("Machine 1","198.168.6.229",false));
        machineArrayList.add(new Machine("Machine 2","198.168.6.230",false));
        machineArrayList.add(new Machine("Machine 3","198.168.6.231",false));
        machineArrayList.add(new Machine("Machine 4","198.168.6.232",true));
        machineArrayList.add(new Machine("Machine 5","198.168.6.233",false));
        machineArrayList.add(new Machine("Machine 6","198.168.6.234",false));
        machineArrayList.add(new Machine("Machine 7","198.168.6.235",false));

        init();
    }

    private void init() {
        mDetector = new GestureDetectorCompat(this, new IrrigationGestureDetector(this));
        adapter=new MachinesListAdapter(machineArrayList,this);
        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        this.mDetector.onTouchEvent(ev);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onSwipe(int id) {
        switch (id){
            case SwipeListener.SWIPE_UP:
                Utils.printLog(TAG, "top");
                Intent intentConfig = new Intent(this, MainActivity4v2.class);
                startActivity(intentConfig);
                overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
                break;

            case SwipeListener.SWIPE_DOWN:
                Utils.printLog(TAG, "down");
                Intent iDown = new Intent(AvailableMachinesListActivity.this, SelectDeviceActivity.class);
                startActivity(iDown);
                overridePendingTransition(R.anim.slide_up2, R.anim.slide_down2);
                break;

            case SwipeListener.SWIPE_LEFT:
                Utils.printLog(TAG, "left");
                break;

            case SwipeListener.SWIPE_RIGHT:
                Utils.printLog(TAG, "right");
                break;
        }
    }

    @Override
    public void onMachineClick(Machine machine) {
            if(this.machine!=null){
                // saygood bye to machine
            }
            // send Hello to selected machine
    }
}
