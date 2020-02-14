package com.ve.irrigation.irrigation.listners;

/**
 * Created by dalvendrakumar on 15/2/19.
 */

public interface SwipeListener {
    int SWIPE_UP=101;
    int SWIPE_LEFT=102;
    int SWIPE_DOWN=103;
    int SWIPE_RIGHT=104;
    void onSwipe(int id);

}
