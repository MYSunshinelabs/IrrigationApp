package com.ve.irrigation.irrigation.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ve.irrigation.datavalues.ConnectionSourceData;
import com.ve.irrigation.datavalues.WifiHotspot;
import com.ve.irrigation.irrigation.R;

import java.util.ArrayList;
import java.util.List;

public class HotSpotAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    public ArrayList<WifiHotspot> networks = new ArrayList<>();
    HotspotListener mHotspotListener;
    List<ConnectionSourceData> connectionSourceDatas;

    public HotSpotAdapter(Activity activity, List<ConnectionSourceData> connectionSourceDatas) {
        mHotspotListener = (HotspotListener) activity;
        this.connectionSourceDatas = connectionSourceDatas;
        //addNetworks(connectionSourceDatas);


    }

/*    private void addNetworks(List<ConnectionSourceData> connectionSourceDatas) {

        for (int i = 0; i < configuratonModel.getConnection().getHotspot().size(); i++) {
            WifiHotspot wifiHotspot = new WifiHotspot();
            ConfiguratonModel.Hotspot hotspot = configuratonModel.getConnection().getHotspot().get(i);
            wifiHotspot.setSsid(hotspot.getSsid());
            wifiHotspot.setPassword(hotspot.getPassword());
            wifiHotspot.setType("hotspot");
            networks.add(wifiHotspot);
        }


        for (int i = 0; i < configuratonModel.getConnection().getUrl().size(); i++) {
            WifiHotspot wifiHotspot = new WifiHotspot();
            ConfiguratonModel.Url url = configuratonModel.getConnection().getUrl().get(i);
            wifiHotspot.setSsid(url.getUrl());
            wifiHotspot.setType("url");
            networks.add(wifiHotspot);
        }

        for (ConnectionSourceData connectionSourceData : connectionSourceDatas) {
            WifiHotspot wifiHotspot = new WifiHotspot();
            if (connectionSourceData.getSSID().length() > 0)
                wifiHotspot.setSsid(connectionSourceData.getSSID());
            else
                wifiHotspot.setSsid(connectionSourceData.getUrl());

            wifiHotspot.setPassword(connectionSourceData.getPassword());
            wifiHotspot.setType(connectionSourceData.getType());
            networks.add(wifiHotspot);
        }


    }*/


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_wifihotspot, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final DataObjectHolder dataObjectHolder = (DataObjectHolder) holder;

        if (connectionSourceDatas.get(position).getSSID().length() > 0)
            dataObjectHolder.mTextViewName.setText(connectionSourceDatas.get(position).getSSID());
        else
            dataObjectHolder.mTextViewName.setText(connectionSourceDatas.get(position).getUrl());

        dataObjectHolder.mLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WifiHotspot wifiHotspot = new WifiHotspot();
                wifiHotspot.setSsid(connectionSourceDatas.get(position).getSSID());
                wifiHotspot.setType("hotspot");
                wifiHotspot.setPassword(connectionSourceDatas.get(position).getPassword());

                mHotspotListener.setUpHotSpot(wifiHotspot);
            }
        });


    }


    @Override
    public int getItemCount() {
        return (connectionSourceDatas.size());
    }


    public class DataObjectHolder extends RecyclerView.ViewHolder {

        private TextView mTextViewName;
        private LinearLayout mLinearLayout;

        public DataObjectHolder(View itemView) {
            super(itemView);

            mTextViewName =  itemView.findViewById(R.id.textview_hotspotname);
            mLinearLayout = (LinearLayout) itemView.findViewById(R.id.layout_hotspot);

        }

    }

    public interface HotspotListener {
        void setUpHotSpot(WifiHotspot wifiHotspot);
    }


}