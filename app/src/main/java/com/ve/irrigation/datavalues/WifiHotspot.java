package com.ve.irrigation.datavalues;

import java.io.Serializable;

/**
 * Created by laxmi on 8/5/18.
 */

public class WifiHotspot implements Serializable, Cloneable {

    String ssid;
    String password;
    String type;

    public String getSsid() {
        return ssid;
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }




    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
