package com.ve.irrigation.irrigation;

import android.content.Context;

public interface EditConnectionContractor {

    interface EditConnectionView {
        void returnUpdate();
        void reflectNewChange();
    }

    interface EditConnectionPresenter {
        void saveConnectionData(Context context, String type, String name, String ssid, String url, String password);

    }
}
