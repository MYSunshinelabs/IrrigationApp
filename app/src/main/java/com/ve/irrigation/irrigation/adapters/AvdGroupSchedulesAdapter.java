package com.ve.irrigation.irrigation.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ve.irrigation.datavalues.Schedule;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.databinding.ItemAdvGroupScheduleNewBinding;
import com.ve.irrigation.irrigation.listners.GroupListener;

import java.util.ArrayList;

/**
 * Created by dalvendrakumar on 29/11/18.
 */

public class AvdGroupSchedulesAdapter extends RecyclerView.Adapter<AvdGroupSchedulesAdapter.ViewHolder> {
    private static final String TAG = AvdGroupSchedulesAdapter.class.getSimpleName();
    private GroupListener listener;
    private ArrayList<Schedule> schedules;

    public AvdGroupSchedulesAdapter(GroupListener listener, ArrayList<Schedule> schedules) {
        this.listener = listener;
        this.schedules = schedules;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdvGroupScheduleNewBinding binding=DataBindingUtil.inflate( LayoutInflater.from(parent.getContext()),
                R.layout.item_adv_group_schedule_new, parent, false);

        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final Schedule schedule=schedules.get(position);
        //  Disable the schedule if it Elapsed 24 hours with enable status.
        holder.binding.setSchedule(schedule);
        holder.binding.txtScheduleId.setText(schedule.getScheduleId()+"");
        holder.binding.txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onSchedule(schedule);
            }
        });

        if(schedule.isDisableToday())
            holder.binding.radioPause.setChecked(true);
        else
            holder.binding.radioPause.setChecked(false);

        if(schedule.isDisable())
            holder.binding.radioDisable.setChecked(true);
        else
            holder.binding.radioDisable.setChecked(false);


        if(schedule.isDisable() || schedule.isDisableToday())
            holder.binding.lytDisable.setVisibility(View.VISIBLE);
        else
            holder.binding.lytDisable.setVisibility(View.GONE);

        if(position>0)
            hideTextHints(holder.binding);

    }

    private void hideTextHints(ItemAdvGroupScheduleNewBinding binding){
        binding.txtHintPause.setVisibility(View.GONE);
        binding.txtHintDisable.setVisibility(View.GONE);
        binding.lytTitle.setVisibility(View.GONE);
//        binding.txtHintStart.setVisibility(View.GONE);
//        binding.txtHintStop.setVisibility(View.GONE);
//        binding.txtHintLitre.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return schedules==null?0:schedules.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ItemAdvGroupScheduleNewBinding binding;
        public ViewHolder(ItemAdvGroupScheduleNewBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
