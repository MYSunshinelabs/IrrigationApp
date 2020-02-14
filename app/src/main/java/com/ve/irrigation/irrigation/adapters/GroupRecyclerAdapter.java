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
import com.ve.irrigation.datavalues.Schedule;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.databinding.ItemGroupBinding;
import com.ve.irrigation.irrigation.listners.GroupListener;

import java.util.ArrayList;

/**
 * Created by dalvendrakumar on 26/11/18.
 */

public class GroupRecyclerAdapter extends RecyclerView.Adapter<GroupRecyclerAdapter.ViewHolder> {

    private ArrayList<Group> groups;
    private GroupListener listener;

    public GroupRecyclerAdapter(ArrayList<Group> groups,GroupListener listener) {
        this.groups = groups;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGroupBinding binding= DataBindingUtil.inflate( LayoutInflater.from(parent.getContext()),
                R.layout.item_group, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.binding.setGroup(groups.get(position));
        final Schedule[]schedules=new Schedule[4];
        holder.binding.setSchedules((groups.get(position).getSchedules().toArray(schedules)));
        holder.binding.btnValves.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onValvesClick(groups.get(position));
            }
        });

        holder.binding.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEditGroup(groups.get(position));
            }
        });

        holder.binding.txtSchedule1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSchedule(schedules[0]);
            }
        });
        holder.binding.txtSchedule2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSchedule(schedules[1]);
            }
        });
        holder.binding.txtSchedule3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSchedule(schedules[2]);
            }
        });
        holder.binding.txtSchedule4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSchedule(schedules[3]);
            }
        });

        // Showing the group valve list
        ValveAdapter adapter=new ValveAdapter(groups.get(position).valveArrayList);
        holder.binding.recyclerViewValves.setLayoutManager(new LinearLayoutManager(holder.binding.getRoot().getContext(),LinearLayoutManager.VERTICAL,false));
        holder.binding.recyclerViewValves.setItemAnimator(new DefaultItemAnimator());
        holder.binding.recyclerViewValves.setAdapter(adapter);
    }



    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ItemGroupBinding binding;
        public ViewHolder(ItemGroupBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
