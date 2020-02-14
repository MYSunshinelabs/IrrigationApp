package com.ve.irrigation.datavalues;

import java.util.ArrayList;

public class ConfiguratonModel {
    private String not_aftertime;

    private Connection connection;

    private String device_inchain;

    private String not_beforetime;

    private String status;

    private String pumpcapacity;

    private String language;

    private String mode;

    public String getNot_aftertime ()
    {
        return not_aftertime;
    }

    public void setNot_aftertime (String not_aftertime)
    {
        this.not_aftertime = not_aftertime;
    }

    public Connection getConnection ()
    {
        return connection;
    }

    public void setConnection (Connection connection)
    {
        this.connection = connection;
    }

    public String getDevice_inchain ()
    {
        return device_inchain;
    }

    public void setDevice_inchain (String device_inchain)
    {
        this.device_inchain = device_inchain;
    }

    public String getNot_beforetime ()
    {
        return not_beforetime;
    }

    public void setNot_beforetime (String not_beforetime)
    {
        this.not_beforetime = not_beforetime;
    }

    public String getStatus ()
    {
        return status;
    }

    public void setStatus (String status)
    {
        this.status = status;
    }

    public String getPumpcapacity ()
    {
        return pumpcapacity;
    }

    public void setPumpcapacity (String pumpcapacity)
    {
        this.pumpcapacity = pumpcapacity;
    }

    public String getLanguage ()
    {
        return language;
    }

    public void setLanguage (String language)
    {
        this.language = language;
    }

    public String getMode ()
    {
        return mode;
    }

    public void setMode (String mode)
    {
        this.mode = mode;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [not_aftertime = "+not_aftertime+", connection = "+connection+", device_inchain = "+device_inchain+", not_beforetime = "+not_beforetime+", status = "+status+", pumpcapacity = "+pumpcapacity+", language = "+language+", mode = "+mode+"]";
    }

    public class Connection
    {
        private ArrayList<Hotspot> hotspot;

        private ArrayList<Url> url;

        public ArrayList<Hotspot> getHotspot ()
        {
            return hotspot;
        }

        public void setHotspot (ArrayList<Hotspot> hotspot)
        {
            this.hotspot = hotspot;
        }

        public ArrayList<Url> getUrl ()
        {
            return url;
        }

        public void setUrl (ArrayList<Url> url)
        {
            this.url = url;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [hotspot = "+hotspot+", url = "+url+"]";
        }
    }

    public class Url
    {
        private String name;

        private String url;

        public String getName ()
        {
            return name;
        }

        public void setName (String name)
        {
            this.name = name;
        }

        public String getUrl ()
        {
            return url;
        }

        public void setUrl (String url)
        {
            this.url = url;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [name = "+name+", url = "+url+"]";
        }
    }
    public class Hotspot
    {
        private String name;

        private String ssid;

        private String password;

        public String getName ()
        {
            return name;
        }

        public void setName (String name)
        {
            this.name = name;
        }

        public String getSsid ()
        {
            return ssid;
        }

        public void setSsid (String ssid)
        {
            this.ssid = ssid;
        }

        public String getPassword ()
        {
            return password;
        }

        public void setPassword (String password)
        {
            this.password = password;
        }

        @Override
        public String toString()
        {
            return "ClassPojo [name = "+name+", ssid = "+ssid+", password = "+password+"]";
        }
    }


}
			
			
