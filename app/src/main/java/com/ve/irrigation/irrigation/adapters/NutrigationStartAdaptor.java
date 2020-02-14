package com.ve.irrigation.irrigation.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import com.ve.irrigation.datavalues.Schedule;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.databinding.ItemStartNutritoinBinding;

import java.util.ArrayList;

public class NutrigationStartAdaptor extends RecyclerView.Adapter<NutrigationStartAdaptor.ViewHolder> {
    private ArrayList<Schedule> schedules;
    private ScheduleListener listener;
    public NutrigationStartAdaptor(ArrayList<Schedule> schedules, ScheduleListener listener) {
        this.schedules = schedules;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStartNutritoinBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_start_nutritoin,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Schedule schedule=schedules.get(position);
        holder.binding.setSchedule(schedule);
        holder.binding.radioNutritionStauts.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                schedule.setStatusNutrition(isChecked);
                listener.onStatusChange(schedule);
            }
        });
    }

    @Override
    public int getItemCount() {
        return schedules.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ItemStartNutritoinBinding binding ;
        public ViewHolder(ItemStartNutritoinBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }

    public interface ScheduleListener {
        void onStatusChange(Schedule schedule);
    }
}
