package com.ve.irrigation.irrigation.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ve.irrigation.hotspot.ClientScanResult;
import com.ve.irrigation.irrigation.R;

import java.util.ArrayList;

public class AllConnectedDevice  extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Activity activity;
    ArrayList<ClientScanResult> clientScanResults;

    public AllConnectedDevice(ArrayList<ClientScanResult> clientScanResults, Activity activity) {
        this.clientScanResults = clientScanResults;
        this.activity = activity;


    }

    public static  int myadd(){
        return 5;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_connecteddevice, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final DataObjectHolder dataObjectHolder = (DataObjectHolder) holder;

        dataObjectHolder.mTextViewName.append("IP Address of Module: " + clientScanResults.get(position).getIpAddr() + "\n");
        dataObjectHolder.mTextViewName.append("Device: " + clientScanResults.get(position).getDevice() + "\n");
        dataObjectHolder.mTextViewName.append("HW Address: " + clientScanResults.get(position).getHWAddr() + "\n");
        dataObjectHolder.mTextViewName.append("Rechability: " + clientScanResults.get(position).isReachable() + "\n");


    }


    @Override
    public int getItemCount() {
        return (clientScanResults.size());
    }


    public class DataObjectHolder extends RecyclerView.ViewHolder {

        private TextView mTextViewName;

        public DataObjectHolder(View itemView) {
            super(itemView);

            mTextViewName = (TextView) itemView.findViewById(R.id.textview_connecteddevice);

        }

    }


}