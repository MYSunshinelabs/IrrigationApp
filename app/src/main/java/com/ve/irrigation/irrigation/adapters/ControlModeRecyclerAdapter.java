package com.ve.irrigation.irrigation.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ve.irrigation.datavalues.Group;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.databinding.ItemControlModeBinding;
import com.ve.irrigation.irrigation.listners.GroupStateListner;
import com.ve.irrigation.irrigation.listners.ValveStateListner;

import java.util.ArrayList;

/**
 * Created by dalvendrakumar on 26/11/18.
 */

public class ControlModeRecyclerAdapter extends RecyclerView.Adapter<ControlModeRecyclerAdapter.ViewHolder> {

    private ArrayList<Group> groups;
    private GroupStateListner listener;
    private ValveStateListner valveStateListner;

    public ControlModeRecyclerAdapter(ArrayList<Group> groups, GroupStateListner listener, ValveStateListner valveStateListner) {
        this.groups = groups;
        this.listener=listener;
        this.valveStateListner=valveStateListner;
    }

    public ControlModeRecyclerAdapter(ArrayList<Group> groups) {
        this.groups = groups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemControlModeBinding binding= DataBindingUtil.inflate( LayoutInflater.from(parent.getContext()),R.layout.item_control_mode, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Group group=groups.get(position);
        holder.binding.setGroup(group);
        // Showing the group valve list
        ValveControlAdapter adapter=new ValveControlAdapter(groups.get(position).valveArrayList,valveStateListner);
        holder.binding.recyclerView.setLayoutManager(new LinearLayoutManager(holder.binding.getRoot().getContext(),LinearLayoutManager.VERTICAL,false));
        holder.binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        holder.binding.recyclerView.setAdapter(adapter);

        holder.binding.radioGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(group.isEnable())
                    group.setEnable(false);
                else
                    group.setEnable(true);
                holder.binding.radioGroup.setChecked(group.isEnable());
                listener.onGroupStateChange(group);
            }
        });

        if(group.isEnable())
            holder.binding.radioGroup.setChecked(true);
        else
            holder.binding.radioGroup.setChecked(false);
    }

    @Override
    public int getItemCount(){
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ItemControlModeBinding binding;
        public ViewHolder(ItemControlModeBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
