package com.ve.irrigation.irrigation.listners;

import com.ve.irrigation.datavalues.Group;
import com.ve.irrigation.datavalues.Schedule;

/**
 * Created by dalvendrakumar on 26/11/18.
 */

public interface GroupListener {
    void onEditGroup(Group group);
    void onEditRequiredVolume(Group group);
    void onValvesClick(Group group);
    void onSchedule(Schedule schedule);
}
