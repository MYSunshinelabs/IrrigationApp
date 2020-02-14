package com.ve.irrigation.irrigation.adapters;

/**
 * Created by dalvendrakumar on 4/1/19.
 */

public interface ValveAssignmentListener {
    void onGroup(String groupId);
    void onAssignmentChange(int valveId,int groupId);
}
