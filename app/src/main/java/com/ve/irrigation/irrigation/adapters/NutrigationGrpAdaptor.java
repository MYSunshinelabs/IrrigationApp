package com.ve.irrigation.irrigation.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ve.irrigation.datavalues.Group;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.databinding.ItemNutrigationGrpBinding;

import java.util.ArrayList;

public class NutrigationGrpAdaptor extends RecyclerView.Adapter<NutrigationGrpAdaptor.ViewHolder> {
    private ArrayList<Group> groups;
    private NutrigationStartAdaptor.ScheduleListener listener;
    public NutrigationGrpAdaptor(ArrayList<Group> groups, NutrigationStartAdaptor.ScheduleListener listener) {
        this.groups = groups;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemNutrigationGrpBinding binding= DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_nutrigation_grp,parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.binding.setGroup(groups.get(position));
        NutrigationStartAdaptor adapter=new NutrigationStartAdaptor(groups.get(position).getSchedules(),listener);
        holder.binding.recyclerView.setLayoutManager(new LinearLayoutManager(holder.binding.getRoot().getContext()));
        holder.binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        holder.binding.recyclerView.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return groups.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ItemNutrigationGrpBinding binding;
        public ViewHolder(ItemNutrigationGrpBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}
