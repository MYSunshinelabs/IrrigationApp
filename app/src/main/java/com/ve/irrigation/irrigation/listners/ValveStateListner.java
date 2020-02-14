package com.ve.irrigation.irrigation.listners;

import com.ve.irrigation.datavalues.Valve;

/**
 * Created by dalvendrakumar on 6/2/19.
 */

public interface ValveStateListner {
    void onValveStateChange(Valve valve);
}
