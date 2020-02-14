package com.ve.irrigation.irrigation.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ve.irrigation.customview.CustomButtonView;
import com.ve.irrigation.customview.CustomTextViewLight;
import com.ve.irrigation.datavalues.Machine;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.utils.Utils;

import java.util.ArrayList;

/**
 * Created by dalvendrakumar on 6/12/18.
 */
//2131606011656
public class MachinesListAdapter extends RecyclerView.Adapter<MachinesListAdapter.ViewHolder>  {
    private static final String TAG = MachinesListAdapter.class.getSimpleName();
    private ArrayList<Machine> machineArrayList;
    private MachineListener listener;
    public interface MachineListener{
        void onMachineClick(Machine machine);
    }
    public MachinesListAdapter(ArrayList<Machine> machineArrayList, MachineListener listener) {
        this.machineArrayList = machineArrayList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MachinesListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_machines,parent,false);
        return new MachinesListAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MachinesListAdapter.ViewHolder holder, final int position) {
        try{
            final Machine machine=machineArrayList.get(position);
            holder.txtName.setText(machine.getName());
            holder.txtIpAddress.setText(machine.getIpAddress());
            if(machine.getStatus())
                holder.txtStatus.setText("Selected");
            else
                holder.txtStatus.setText("Select");
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onMachineClick(machine);
                }
            });
        }catch (Exception e){
            Utils.printLog(TAG,e.toString());
        }

    }

    @Override
    public int getItemCount() {
        return machineArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CustomTextViewLight txtName,txtIpAddress;
        private CustomTextViewLight txtStatus;
        public ViewHolder(View itemView) {
            super(itemView);
            txtName=itemView.findViewById(R.id.txtName);
            txtIpAddress=itemView.findViewById(R.id.txtIpAddress);
            txtStatus=itemView.findViewById(R.id.txtStatus);
        }
    }
}
