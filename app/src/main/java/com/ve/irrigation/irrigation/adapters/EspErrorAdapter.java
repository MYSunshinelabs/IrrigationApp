package com.ve.irrigation.irrigation.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ve.irrigation.customview.CustomTextViewLight;
import com.ve.irrigation.irrigation.R;

import java.util.ArrayList;

/**
 * Created by dalvendrakumar on 17/01/19.
 */
//2131606011656
public class EspErrorAdapter extends RecyclerView.Adapter<EspErrorAdapter.ViewHolder>  {
    private ArrayList<String> espErrorList;

    public EspErrorAdapter(ArrayList<String > espErrorList) {
        this.espErrorList = espErrorList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_esp_error,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txtErrorMsg.setText(espErrorList.get(position));
    }

    @Override
    public int getItemCount() {
        return espErrorList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CustomTextViewLight txtErrorMsg;
        public ViewHolder(View itemView) {
            super(itemView);
            txtErrorMsg=itemView.findViewById(R.id.txtErrorMsg);
        }
    }
}
