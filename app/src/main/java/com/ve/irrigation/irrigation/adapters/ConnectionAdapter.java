package com.ve.irrigation.irrigation.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ve.irrigation.datavalues.ConnectionSourceData;
import com.ve.irrigation.irrigation.R;

import java.util.List;

public class ConnectionAdapter
        extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    ConnectionListener mConnectionListener;
    List<ConnectionSourceData> connectionSourceDatas;

    public ConnectionAdapter(Activity activity, List<ConnectionSourceData> connectionSourceDatas) {
        mConnectionListener = (ConnectionListener) activity;
        this.connectionSourceDatas = connectionSourceDatas;
        Log.i("Connection--ad----ize: ",connectionSourceDatas.size()+"");
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_connection, parent, false);
        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;

    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final DataObjectHolder dataObjectHolder = (DataObjectHolder) holder;

        if (connectionSourceDatas.get(position).getSSID().length() > 0)
            dataObjectHolder.mTextViewName.setText(connectionSourceDatas.get(position).getSSID());
        else
            dataObjectHolder.mTextViewName.setText(connectionSourceDatas.get(position).getUrl());

        dataObjectHolder.mEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConnectionListener.editConnection(connectionSourceDatas.get(position));

            }
        });

        dataObjectHolder.mDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mConnectionListener.deleteConnection(connectionSourceDatas.get(position));

            }
        });


    }


    @Override
    public int getItemCount() {
        return (connectionSourceDatas.size());
    }


    public class DataObjectHolder extends RecyclerView.ViewHolder {

        private TextView mTextViewName;
        private ImageView mEdit, mDelete;

        public DataObjectHolder(View itemView) {
            super(itemView);

            mTextViewName =  itemView.findViewById(R.id.textview_hotspotname);
            mEdit = (ImageView) itemView.findViewById(R.id.img_editconnenction);
            mDelete = (ImageView) itemView.findViewById(R.id.img_deleteconnection);


        }

    }

    public interface ConnectionListener {
        public void editConnection(ConnectionSourceData connectionSourceData);

        public void deleteConnection(ConnectionSourceData connectionSourceData);
    }


}