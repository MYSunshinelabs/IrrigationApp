package com.ve.irrigation.datavalues;

/**
 * Created by laxmi on 29/3/18.
 */

public class ConfigurationSetting {

    String notBeforeTime;
    String notAfterTime;
    String pumpCapacity;
    String noofDevicesinChain;
    String language;

    public String getNotBeforeTime() {
        return notBeforeTime;
    }

    public void setNotBeforeTime(String notBeforeTime) {
        this.notBeforeTime = notBeforeTime;
    }

    public String getNotAfterTime() {
        return notAfterTime;
    }

    public void setNotAfterTime(String notAfterTime) {
        this.notAfterTime = notAfterTime;
    }

    public String getPumpCapacity() {
        return pumpCapacity;
    }

    public void setPumpCapacity(String pumpCapacity) {
        this.pumpCapacity = pumpCapacity;
    }

    public String getNoofDevicesinChain() {
        return noofDevicesinChain;
    }

    public void setNoofDevicesinChain(String noofDevicesinChain) {
        this.noofDevicesinChain = noofDevicesinChain;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    String mode;

}
