package com.ve.irrigation.irrigation.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.ve.irrigation.datavalues.Valve;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.utils.Utils;

import java.util.ArrayList;

/**
 * Created by dalvendrakumar on 6/12/18.
 */

public class AssignValveAdapter extends RecyclerView.Adapter<AssignValveAdapter.ViewHolder>  {
    private static final String TAG = AssignValveAdapter.class.getSimpleName();
    private ArrayList<Valve> valveArrayList;

    public AssignValveAdapter(ArrayList<Valve> valveArrayList) {
        this.valveArrayList = valveArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_assgin_valves,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        try{
            holder.cbValve.setText(valveArrayList.get(position).getValveId()+"");
        }catch (Exception e){
            Utils.printLog(TAG,e.toString());
        }
        if(position>=valveArrayList.size())
            holder.cbValve.setBackgroundResource(R.drawable.bg_valve_disable);
    }

    @Override
    public int getItemCount() {
        return 8;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CheckBox cbValve;
        public ViewHolder(View itemView) {
            super(itemView);
            cbValve=itemView.findViewById(R.id.cbValve);
        }
    }
}
