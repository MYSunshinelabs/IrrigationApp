package com.ve.irrigation.irrigation.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ve.irrigation.customview.CustomTextViewLight;
import com.ve.irrigation.datavalues.Group;
import com.ve.irrigation.datavalues.Valve;
import com.ve.irrigation.irrigation.R;

import java.util.ArrayList;

/**
 * Created by dalvendrakumar on 04/01/19.
 */
public class ValveAssignmentAdapterNew extends RecyclerView.Adapter<ValveAssignmentAdapterNew.ViewHolder>  {
    private ArrayList<Group> groupArrayList;
    private ValveListener listener;
    private int noValve;
    public  interface ValveListener {
        void onValveChange(Group group);
    }
    public ValveAssignmentAdapterNew(ArrayList<Group> valveArrayList, int noValve, ValveListener listener) {
        this.groupArrayList = valveArrayList;
        this.noValve = noValve;
        this.listener=listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_valve_assignment_new,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final Group group= groupArrayList.get(position);
        final ArrayList<Valve> valves=group.getValves();
        holder.txtGroup.setText(group.groupName);
        for (int i = 0; i< noValve; i++){
            Valve valve=valves.get(i);
            holder.checkBoxes[i].setVisibility(View.VISIBLE);
            holder.checkBoxes[i].setChecked(valve.isEnable());
        }
        holder.checkBoxes[0].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                valves.get(0).setEnable(isChecked);
                listener.onValveChange(group);
            }
        });
        holder.checkBoxes[1].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                valves.get(1).setEnable(isChecked);
                listener.onValveChange(group);
            }
        });
        holder.checkBoxes[2].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                valves.get(2).setEnable(isChecked);
                listener.onValveChange(group);
            }
        });
        holder.checkBoxes[3].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                valves.get(3).setEnable(isChecked);
                listener.onValveChange(group);
            }
        });
        holder.checkBoxes[4].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                valves.get(4).setEnable(isChecked);
                listener.onValveChange(group);
            }
        });
        holder.checkBoxes[5].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                valves.get(5).setEnable(isChecked);
                listener.onValveChange(group);
            }
        });
        holder.checkBoxes[6].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                valves.get(6).setEnable(isChecked);
                listener.onValveChange(group);
            }
        });
        holder.checkBoxes[7].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                valves.get(7).setEnable(isChecked);
                listener.onValveChange(group);
            }
        });
//        switch (groupArrayList.get(position).getGroupId()){
//            case 1:
//                holder.checkBoxes[0].setChecked(true);
//                break;
//            case 2:
//                holder.checkBoxes[1].setChecked(true);
//                break;
//            case 3:
//                holder.checkBoxes[2].setChecked(true);
//                break;
//            case 4:
//                holder.checkBoxes[3].setChecked(true);
//                break;
//            case 5:
//                holder.checkBoxes[4].setChecked(true);
//                break;
//            case 6:
//                holder.checkBoxes[5].setChecked(true);
//                break;
//            case 7:
//                holder.checkBoxes[6].setChecked(true);
//                break;
//            case 8:
//                holder.checkBoxes[7].setChecked(true);
//                break;
//        }
    }

    private void addGroupOptions(RadioGroup radioGroup, Valve valve, Context context) {
        ColorStateList colorStateList = new ColorStateList(
                new int[][]{ new int[]{android.R.attr.state_enabled} //enabled
                },
                new int[] {context.getResources().getColor(R.color.white)}
        );

        for (int i = 0; i< noValve; i++){
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
        return groupArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CustomTextViewLight txtGroup;
        private CheckBox[] checkBoxes =new CheckBox[8];
        public ViewHolder(View itemView) {
            super(itemView);
            txtGroup =itemView.findViewById(R.id.txtGroup);
            checkBoxes[0]=itemView.findViewById(R.id.rdoGroup1);
            checkBoxes[1]=itemView.findViewById(R.id.rdoGroup2);
            checkBoxes[2]=itemView.findViewById(R.id.rdoGroup3);
            checkBoxes[3]=itemView.findViewById(R.id.rdoGroup4);
            checkBoxes[4]=itemView.findViewById(R.id.rdoGroup5);
            checkBoxes[5]=itemView.findViewById(R.id.rdoGroup6);
            checkBoxes[6]=itemView.findViewById(R.id.rdoGroup7);
            checkBoxes[7]=itemView.findViewById(R.id.rdoGroup8);
        }
    }

}
