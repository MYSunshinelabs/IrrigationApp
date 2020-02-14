package com.ve.irrigation.irrigation.adapters;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ve.irrigation.datavalues.Wifi;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.databinding.ItemDevicesBinding;

import java.util.ArrayList;

public class DeviceRecyclerAdapter extends RecyclerView.Adapter<DeviceRecyclerAdapter.ViewHolder> {

    private ArrayList<Wifi> devices;
    private  DeviceListener listener;

    public interface DeviceListener{
        void onSelectDevice(Wifi device);
        void onEditDevice(Wifi device);
    }

    public DeviceRecyclerAdapter(ArrayList<Wifi> devices,DeviceListener listener) {
        this.devices = devices;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemDevicesBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_devices,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Wifi device=devices.get(position);
        holder.binding.setDevice(device);
        holder.binding.radioStaus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelectDevice(device);
            }
        });

        holder.binding.txtDeviceName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSelectDevice(device);
            }
        });

        holder.binding.txtEditNetwork.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEditDevice(device);
            }
        });
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        private ItemDevicesBinding binding;
        public ViewHolder(ItemDevicesBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
