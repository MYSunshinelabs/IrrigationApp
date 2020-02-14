package com.ve.irrigation.datavalues;

public class FirstHeartBeat
{
    private String destip;

    private String ts;

    private String mid;

    public String getDestip ()
    {
        return destip;
    }

    public void setDestip (String destip)
    {
        this.destip = destip;
    }

    public String getTs ()
    {
        return ts;
    }

    public void setTs (String ts)
    {
        this.ts = ts;
    }

    public String getMid ()
    {
        return mid;
    }

    public void setMid (String mid)
    {
        this.mid = mid;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [destip = "+destip+", ts = "+ts+", mid = "+mid+"]";
    }
}

			