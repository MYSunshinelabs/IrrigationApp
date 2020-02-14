package com.ve.irrigation.irrigation.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ve.irrigation.datavalues.Valve;
import com.ve.irrigation.customview.CustomTextViewLight;
import com.ve.irrigation.irrigation.R;

import java.util.ArrayList;

/**
 * Created by dalvendrakumar on 04/01/19.
 */
public class ValveAssignmentAdapter extends RecyclerView.Adapter<ValveAssignmentAdapter.ViewHolder>  {
    private ArrayList<Valve> valveArrayList;
    private ValveAssignmentListener listener;
    private int noGroups;

    public ValveAssignmentAdapter(ArrayList<Valve> valveArrayList,int noGroups,ValveAssignmentListener listener) {
        this.valveArrayList = valveArrayList;
        this.noGroups = noGroups;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_valve_assignment,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Valve valve=valveArrayList.get(position);
        holder.txtValve.setText("Valve "+valve.getValveId());
        for (int i=0;i<noGroups;i++){
            holder.radioButtonGroups[i].setVisibility(View.VISIBLE);
        }

        holder.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rdoGroup1:
                        valveArrayList.get(position).setGroupId(1);
                        break;
                    case R.id.rdoGroup2:
                        valveArrayList.get(position).setGroupId(2);
                        break;
                    case R.id.rdoGroup3:
                        valveArrayList.get(position).setGroupId(3);
                        break;
                    case R.id.rdoGroup4:
                        valveArrayList.get(position).setGroupId(4);
                        break;
                    case R.id.rdoGroup5:
                        valveArrayList.get(position).setGroupId(5);
                        break;
                    case R.id.rdoGroup6:
                        valveArrayList.get(position).setGroupId(6);
                        break;
                    case R.id.rdoGroup7:
                        valveArrayList.get(position).setGroupId(7);
                        break;
                    case R.id.rdoGroup8:
                        valveArrayList.get(position).setGroupId(8);
                        break;
                }

                listener.onAssignmentChange(valve.getValveId(),valve.getGroupId());

            }
        });

        switch (valveArrayList.get(position).getGroupId()){
            case 1:
                holder.radioButtonGroups[0].setChecked(true);
                break;
            case 2:
                holder.radioButtonGroups[1].setChecked(true);
                break;
            case 3:
                holder.radioButtonGroups[2].setChecked(true);
                break;
            case 4:
                holder.radioButtonGroups[3].setChecked(true);
                break;
            case 5:
                holder.radioButtonGroups[4].setChecked(true);
                break;
            case 6:
                holder.radioButtonGroups[5].setChecked(true);
                break;
            case 7:
                holder.radioButtonGroups[6].setChecked(true);
                break;
            case 8:
                holder.radioButtonGroups[7].setChecked(true);
                break;
        }


    }

    private void addGroupOptions(RadioGroup radioGroup, Valve valve, Context context) {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{ new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[] {context.getResources().getColor(R.color.white)}
        );

        for (int i=0;i<noGroups;i++){
            RadioButton radioButton=new RadioButton(context);
//            radioGroup.setId(R.id.group1);
            radioButton.setText("Group "+(i+1));
            radioButton.setTextColor(context.getResources().getColor(R.color.white));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                radioButton.setButtonTintList(colorStateList);
            }

            radioGroup.addView(radioButton);

            if(valve.getGroupId()==(i+1))
                radioButton.setChecked(true);

        }
    }

    @Override
    public int getItemCount() {
        return valveArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CustomTextViewLight txtValve;
        private RadioGroup radioGroup;
        private RadioButton[] radioButtonGroups=new RadioButton[8];
        public ViewHolder(View itemView) {
            super(itemView);
            txtValve=itemView.findViewById(R.id.txtValve);
            radioGroup=itemView.findViewById(R.id.radioGroup);
            radioButtonGroups[0]=itemView.findViewById(R.id.rdoGroup1);
            radioButtonGroups[1]=itemView.findViewById(R.id.rdoGroup2);
            radioButtonGroups[2]=itemView.findViewById(R.id.rdoGroup3);
            radioButtonGroups[3]=itemView.findViewById(R.id.rdoGroup4);
            radioButtonGroups[4]=itemView.findViewById(R.id.rdoGroup5);
            radioButtonGroups[5]=itemView.findViewById(R.id.rdoGroup6);
            radioButtonGroups[6]=itemView.findViewById(R.id.rdoGroup7);
            radioButtonGroups[7]=itemView.findViewById(R.id.rdoGroup8);
        }
    }

}
