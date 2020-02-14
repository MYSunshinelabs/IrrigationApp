package com.ve.irrigation.irrigation.listners;
import com.ve.irrigation.datavalues.Schedule;
/**
 * Created by dalvendrakumar on 26/11/18.
 */

public interface ScheduleListener {
    void onStartTime(Schedule schedule);
    void onMinutesClick(Schedule schedule);
    void onVolumeClick(Schedule schedule);
    void onDisableClick(Schedule schedule);
    void onDisableTodayClick(Schedule schedule);
}
