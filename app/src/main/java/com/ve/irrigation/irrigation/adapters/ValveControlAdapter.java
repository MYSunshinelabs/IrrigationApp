package com.ve.irrigation.irrigation.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import com.ve.irrigation.datavalues.Valve;
import com.ve.irrigation.customview.CustomTextViewLight;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.listners.ValveStateListner;

import java.util.ArrayList;

/**
 * Created by dalvendrakumar on 6/2/19.
 */
//2131606011656
public class ValveControlAdapter extends RecyclerView.Adapter<ValveControlAdapter.ViewHolder>  {
    private static final String TAG = ValveControlAdapter.class.getSimpleName();
    private ArrayList<Valve> valveArrayList;

    private ValveStateListner valveStateListner;
    public ValveControlAdapter(ArrayList<Valve> valveArrayList, ValveStateListner valveStateListner) {
        this.valveArrayList = valveArrayList;
        this.valveStateListner=valveStateListner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_control_valve,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Valve valve=valveArrayList.get(position);
        holder.txtValve.setText("Valve "+valve.getValveId());


        holder.radioValve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(valve.isEnable())
                    valve.setEnable(false);
                else
                    valve.setEnable(true);

                holder.radioValve.setChecked(valve.isEnable());
                valveStateListner.onValveStateChange(valve);
            }
        });

        if(valve.isEnable())
            holder.radioValve.setChecked(true);
        else
            holder.radioValve.setChecked(false);

    }

    @Override
    public int getItemCount() {
        return valveArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CustomTextViewLight txtValve;
        private RadioButton radioValve;
        public ViewHolder(View itemView) {
            super(itemView);
            txtValve=itemView.findViewById(R.id.txtValve);
            radioValve=itemView.findViewById(R.id.radioValve);

        }
    }
}
