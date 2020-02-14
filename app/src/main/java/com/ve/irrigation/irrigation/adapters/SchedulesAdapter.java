package com.ve.irrigation.irrigation.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ve.irrigation.datavalues.Schedule;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.databinding.ItemEditGroupScheduleBinding;
import com.ve.irrigation.irrigation.listners.ScheduleListener;
import com.ve.irrigation.utils.Preferences;
import com.ve.irrigation.utils.Utils;

import java.util.ArrayList;

/**
 * Created by dalvendrakumar on 29/11/18.
 */

public class SchedulesAdapter extends RecyclerView.Adapter<SchedulesAdapter.ViewHolder> {
    private static final String TAG = SchedulesAdapter.class.getSimpleName();
    private ScheduleListener listener;
    private ArrayList<Schedule> schedules;

    public SchedulesAdapter(ScheduleListener listener, ArrayList<Schedule> schedules) {
        this.listener = listener;
        this.schedules = schedules;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemEditGroupScheduleBinding binding=DataBindingUtil.inflate( LayoutInflater.from(parent.getContext()),R.layout.item_edit_group_schedule, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final Schedule schedule=schedules.get(position);
        //  Disable the schedule if it Elapsed 24 hours with enable status.
        int elapsedHours=Utils.elapsedHours(Preferences.getScheduleEnableTime(holder.binding.getRoot().getContext(),schedule.getScheduleId()+""+schedule.getGroupId()+""));
        if(!schedule.isDisableToday() && elapsedHours>23){
            schedule.setDisableToday(true);
        }

        holder.binding.setSchedule(schedule);
        holder.binding.txtStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onStartTime(schedule);
            }
        });

        holder.binding.txtMinutes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onMinutesClick(schedule);
            }
        });

        holder.binding.txtVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onVolumeClick(schedule);
            }
        });

        holder.binding.txtDisable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(schedule.isDisable()) {
                        schedule.setDisable(false);
                        holder.binding.txtDisable.setText("Enable");
                    }else {
                        schedule.setDisable(true);
                        holder.binding.txtDisable.setText("Disable");
                    }
                    listener.onDisableClick(schedule);
                }catch (Exception e){
                    Utils.printLog(TAG,e.getMessage());
                }
            }
        });

        holder.binding.txtDisableToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    if(schedules.get(position).isDisableToday()) {
                        schedules.get(position).setDisableToday(false);
                        holder.binding.txtDisableToday.setText("Enable Today");
                        Preferences.setScheduleEnableTime(holder.binding.getRoot().getContext(),schedule.getScheduleId()+""+schedule.getGroupId()+"");
                    }else {
                        schedules.get(position).setDisableToday(true);
                        holder.binding.txtDisableToday.setText("Disable Today");
                    }
                    listener.onDisableTodayClick(schedules.get(position));
                }catch (Exception e){
                    Utils.printLog(TAG,e.getMessage());
                }
            }
        });

        if(schedules.get(position).isDisableToday()) {
            holder.binding.txtDisableToday.setText("Disable Today");
        }else {
            holder.binding.txtDisableToday.setText("Enable Today");
        }
        if(schedules.get(position).isDisable()) {
            holder.binding.txtDisable.setText("Disable");
        }else {
            holder.binding.txtDisable.setText("Enable");
        }

    }


    @Override
    public int getItemCount() {
        return schedules==null?0:schedules.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ItemEditGroupScheduleBinding binding;
        public ViewHolder(ItemEditGroupScheduleBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
