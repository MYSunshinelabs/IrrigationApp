package com.ve.irrigation.datavalues;

import java.io.Serializable;

public class Device implements Serializable {
    private String name,Ssid,password;
    private boolean status;

    public Device(String name, String ssid, String password, boolean status) {
        this.name = name;
        Ssid = ssid;
        this.password = password;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSsid() {
        return Ssid;
    }

    public void setSsid(String ssid) {
        Ssid = ssid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
