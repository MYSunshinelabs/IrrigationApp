package com.ve.irrigation.irrigation.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.ve.irrigation.datavalues.Wifi;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.databinding.ItemWifiLocationNewBinding;
import java.util.List;

/**
 * Created by dalvendrakumar on 29/11/18.
 */

public class WifiLocationAdapterNew extends RecyclerView.Adapter<WifiLocationAdapterNew.ViewHolder> {
    private static final String TAG = WifiLocationAdapterNew.class.getSimpleName();
    private List<Wifi> wifis;
    public WifiLocationListner listner;

    public interface WifiLocationListner {
        void setCurrentLocation(Wifi wifi);
        void removeWifiLocation(Wifi wifi);
    }

    public WifiLocationAdapterNew(List<Wifi> wifis, WifiLocationListner listner) {
        this.wifis = wifis;
        this.listner=listner;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWifiLocationNewBinding binding=DataBindingUtil.inflate( LayoutInflater.from(parent.getContext()),R.layout.item_wifi_location_new, parent, false);
        return new ViewHolder(binding);
    }
    @Override
    public int getItemViewType(int position){
        return position;
    }
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Wifi wifi=wifis.get(position);
        holder.binding.setWifi(wifi);

        holder.binding.imgRemoveWifiLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listner.removeWifiLocation(wifi);
            }
        });

        holder.binding.edtIpAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wifi.setIpAddress(holder.binding.edtIpAddress.getText().toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        holder.binding.edtPort.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wifi.setPort(holder.binding.edtPort.getText().toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        holder.binding.edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wifi.setName(holder.binding.edtName.getText().toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        holder.binding.edtSSID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wifi.setSsid(holder.binding.edtSSID.getText().toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });

        holder.binding.edtpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                wifi.setPassword(holder.binding.edtpassword.getText().toString());
            }
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }


    @Override
    public int getItemCount() {
        return wifis==null?0:wifis.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ItemWifiLocationNewBinding binding;

        public ViewHolder(ItemWifiLocationNewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
