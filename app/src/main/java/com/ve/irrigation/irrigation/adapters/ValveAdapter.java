package com.ve.irrigation.irrigation.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ve.irrigation.datavalues.Valve;
import com.ve.irrigation.customview.CustomTextViewLight;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.utils.Utils;

import java.util.ArrayList;

/**
 * Created by dalvendrakumar on 6/12/18.
 */

public class ValveAdapter extends RecyclerView.Adapter<ValveAdapter.ViewHolder>  {
    private static final String TAG = ValveAdapter.class.getSimpleName();
    private ArrayList<Valve> valveArrayList;

    public ValveAdapter(ArrayList<Valve> valveArrayList) {
        this.valveArrayList = valveArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_valves,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try{
            holder.txtValve.setText(valveArrayList.get(position).getValveId()+"");
        }catch (Exception e){
            Utils.printLog(TAG,e.toString());
        }
        if(position>=valveArrayList.size())
            holder.txtValve.setBackgroundResource(R.drawable.bg_valve_disable);
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CustomTextViewLight txtValve;
        public ViewHolder(View itemView) {
            super(itemView);
            txtValve=itemView.findViewById(R.id.txtValve);
        }
    }
}
