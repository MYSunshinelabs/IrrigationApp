package com.ve.irrigation.irrigation.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ve.irrigation.datavalues.Group;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.databinding.ItemGroupNewBinding;
import com.ve.irrigation.irrigation.listners.GroupListener;
import com.ve.irrigation.irrigation.listners.NumberListener;
import com.ve.irrigation.utils.Constants;
import com.ve.irrigation.utils.IrrigationNumberPicker;
import com.ve.irrigation.utils.Utils;

import java.util.ArrayList;

/**
 * Created by dalvendrakumar on 26/11/18.
 */

public class GroupRecyclerAdapterNew extends RecyclerView.Adapter<GroupRecyclerAdapterNew.ViewHolder> {

    private static final String TAG = GroupRecyclerAdapterNew.class.getSimpleName();
    private ArrayList<Group> groups;
    private GroupListener listener;
    private boolean isAdvMode;

    public GroupRecyclerAdapterNew(ArrayList<Group> groups, boolean isAdvMode, GroupListener listener) {
        this.groups = groups;
        this.isAdvMode=isAdvMode;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemGroupNewBinding binding= DataBindingUtil.inflate( LayoutInflater.from(parent.getContext()),R.layout.item_group_new, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Group group=groups.get(position);
        holder.binding.setGroup(group);
        int currentValue;
        try{
            currentValue= Integer.parseInt(groups.get(position).getTotalReq());
        }catch (Exception e){
            Utils.printLog(TAG,e.toString());
            currentValue=Constants.Configration.MIN_VOLUME;
        }

        //      Managing the Number picker for required group volume
        IrrigationNumberPicker volumePicker=new IrrigationNumberPicker(new NumberListener() {
            @Override
            public void onSetNumber(int value) {
                group.setTotalReq(value+"");
                holder.binding.txtEdit.setVisibility(View.VISIBLE);
                holder.binding.lytNumPicker.setVisibility(View.GONE);
            }
            @Override
            public void onSetNumber(int requestCode, int value) {

            }
        }, currentValue, holder.binding.lytNumPicker);
        volumePicker.setMAX_NUMBER_LIMIT(Constants.Configration.MAX_DURATION_MINUTES);
        volumePicker.setMIN_NUMBER_LIMIT(Constants.Configration.MIN_DURATION_MINUTES);


        holder.binding.txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.binding.txtEdit.setVisibility(View.GONE);
                holder.binding.lytNumPicker.setVisibility(View.VISIBLE);
            }
        });

        // Showing the group valve list
        ValveAdapter adapter=new ValveAdapter(group.valveArrayList);
        holder.binding.recyclerViewValves.setLayoutManager(new GridLayoutManager(holder.binding.getRoot().getContext(),3));
        holder.binding.recyclerViewValves.setItemAnimator(new DefaultItemAnimator());
        holder.binding.recyclerViewValves.setAdapter(adapter);

        //      Showing start schedules for a group if screen is in
        //      Advance mode and hide the Required volume edit button.
        if(isAdvMode){
            holder.binding.txtEdit.setVisibility(View.GONE);
            AvdGroupSchedulesAdapter schedulesAdapter=new AvdGroupSchedulesAdapter(listener,group.getSchedules());
            holder.binding.recyclerViewSchedules.setLayoutManager(new LinearLayoutManager(holder.binding.getRoot().getContext(),LinearLayoutManager.VERTICAL,false));
            holder.binding.recyclerViewSchedules.setItemAnimator(new DefaultItemAnimator());
            holder.binding.recyclerViewSchedules.setAdapter(schedulesAdapter);
        }
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ItemGroupNewBinding binding;
        public ViewHolder(ItemGroupNewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
