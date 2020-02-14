package com.ve.irrigation.irrigation;

import android.content.Context;
import com.ve.irrigation.datavalues.ConfiguratonModel;


public interface ConfigurationContractor {

    interface ConfiguratioView {
        void returnUpdate(ConfiguratonModel response);

        void returnUpdateForNextScreen();
    }

    interface ConfigurationPresenter {
        void requestServer();

        void saveConnectionData(Context context, ConfiguratonModel configuratonModel);

    }
}
