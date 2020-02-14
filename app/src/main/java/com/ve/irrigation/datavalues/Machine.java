package com.ve.irrigation.datavalues;

public class Machine {
    private String name,id, ipAddress,port,dtype;
    private boolean status;


    public Machine(String name, String ipAddress, String port, String dtype) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.port = port;
        this.dtype = dtype;
    }

    public Machine(String name, String ipAddress, boolean  status) {
        this.name = name;
        this.ipAddress = ipAddress;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public boolean  getStatus() {
        return status;
    }

    public String getId() {
        return id;
    }

    public String getIpAddress() {
        if (ipAddress==null)
            ipAddress="";
        return ipAddress;
    }

    public String getPort() {
        return port;
    }

    public String getDtype() {
        return dtype;
    }

    public boolean isStatus() {
        return status;
    }
}
