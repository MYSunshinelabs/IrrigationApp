package com.ve.irrigation.datavalues;

public class MeTooResponse
{
    private String device_name;

    private String message_id;

    private String port_acceptdeviceforudp;

    private String magic_number;

    private String port_http;

    private String message_count;

    private String device_ip;

    public String getDevice_name ()
    {
        return device_name;
    }

    public void setDevice_name (String device_name)
    {
        this.device_name = device_name;
    }

    public String getMessage_id ()
    {
        return message_id;
    }

    public void setMessage_id (String message_id)
    {
        this.message_id = message_id;
    }

    public String getPort_acceptdeviceforudp ()
    {
        return port_acceptdeviceforudp;
    }

    public void setPort_acceptdeviceforudp (String port_acceptdeviceforudp)
    {
        this.port_acceptdeviceforudp = port_acceptdeviceforudp;
    }

    public String getMagic_number ()
    {
        return magic_number;
    }

    public void setMagic_number (String magic_number)
    {
        this.magic_number = magic_number;
    }

    public String getPort_http ()
    {
        return port_http;
    }

    public void setPort_http (String port_http)
    {
        this.port_http = port_http;
    }

    public String getMessage_count ()
    {
        return message_count;
    }

    public void setMessage_count (String message_count)
    {
        this.message_count = message_count;
    }

    public String getDevice_ip ()
    {
        return device_ip;
    }

    public void setDevice_ip (String device_ip)
    {
        this.device_ip = device_ip;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [device_name = "+device_name+", message_id = "+message_id+", port_acceptdeviceforudp = "+port_acceptdeviceforudp+", magic_number = "+magic_number+", port_http = "+port_http+", message_count = "+message_count+", device_ip = "+device_ip+"]";
    }
}
			
			