package com.ve.irrigation.irrigation;

import android.content.Context;

import com.ve.irrigation.datavalues.AppDataBase;
import com.ve.irrigation.datavalues.ConnectionSourceData;

public class EditConnectionMainPresenter implements EditConnectionContractor.EditConnectionPresenter {

    EditConnectionContractor.EditConnectionView editConnectionView;

    public EditConnectionMainPresenter(EditConnectionContractor.EditConnectionView editConnectionView) {
        this.editConnectionView = editConnectionView;
    }


    @Override
    public void saveConnectionData(final Context context, String type, String name, String ssid, String url, String password) {


        final ConnectionSourceData connectionSourceData = new ConnectionSourceData();
        connectionSourceData.setName(name);
        connectionSourceData.setSSID(ssid);
        connectionSourceData.setUrl(url);
        connectionSourceData.setPassword(password);
        connectionSourceData.setType(type);


        new Thread(new Runnable() {
            @Override
            public void run() {
                AppDataBase.getAppDataBase(context).connectionSourceDAO().insert(connectionSourceData);

            }
        }).start();

        editConnectionView.reflectNewChange();

    }
}
