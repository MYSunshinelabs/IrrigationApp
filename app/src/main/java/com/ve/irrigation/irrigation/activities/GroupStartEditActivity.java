package com.ve.irrigation.irrigation.activities;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ve.irrigation.datavalues.Group;
import com.ve.irrigation.irrigation.R;
import com.ve.irrigation.irrigation.adapters.ValveAdapter;


public class GroupStartEditActivity extends BaseActivity implements View.OnClickListener {

    private Group group;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_start_edit);
        init();
    }

    private void init() {
        group = (Group) getIntent().getExtras().getSerializable("group");

        RecyclerView recyclerView=findViewById(R.id.recyclerView);
        ValveAdapter adapter=new ValveAdapter(group.valveArrayList);

        findViewById(R.id.txtEditValveTable).setOnClickListener(this);
        TextView txtGroup=findViewById(R.id.txtGroup);
        txtGroup.setText(group.groupName);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.txtEditValveTable:
                break;
        }
    }
}
