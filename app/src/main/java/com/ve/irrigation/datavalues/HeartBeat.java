package com.ve.irrigation.datavalues;

import com.ve.irrigation.utils.Utils;

public class HeartBeat  {
    private int magic,count,dtype;
    private String entity, rainduration="",rainAmount="";
    private boolean isRainSenser=true,isTempratureSenser=true;
    private Metric metric;

    public String getRainduration() {
        return rainduration;
    }

    public String getRainAmount() {
        return rainAmount;
    }

    public String getEntity() {
        return entity;
    }

    public int getMagic() {
        return magic;
    }

    public int getCount() {
        return count;
    }

    public int getDtype() {
        return dtype;
    }

    public Metric getMetric() {
        return metric;
    }

    public boolean isRainSenser() {
        return isRainSenser;
    }

    public boolean isTempratureSenser() {
        return isTempratureSenser;
    }

    @Override
    public String toString() {
        return "HeartBeat{" +
                "entity='" + entity + '\'' +
                ", magic=" + magic +
                ", count=" + count +
                ", dtype=" + dtype +
                ", metric=" + metric.toString()+
                '}';
    }

    public class Metric {
        private String mode,state,rvol,avol,stime="0",slen,svol,v1vol,v2vol,v3vol,nvol,ppres,pflow;
        private String gnr,snr,mhours,phours;
        private int vstate,pstate,ts;

        // Senser values
        private int temp,rh,bp,light,rain,raint,rainv,rainc;

        public int getRain() {
            return rain;
        }

        public int getRaint() {
            return raint;
        }

        public int getRainv() {
            return rainv;
        }

        public int getRainc() {
            return rainc;
        }

        public int getTemp() {
            return temp;
        }

        public int getRh() {
            return rh;
        }

        public int getBp() {
            return bp;
        }

        public int getLight() {
            return light;
        }

        public String getMode() {
            if(mode==null)
                return "5";
            return mode;
        }

        public String getState() {
            return state;
        }

        public String getRvol() {
            return rvol;
        }

        public String getAvol() {
            if(avol!=null && avol.indexOf('.')>0){
                avol=avol.substring(0,avol.indexOf('.')+2);
            }
            return avol;
        }

        public String getStime() {
            if(stime==null)
                stime="0";
            return stime;
        }

        public String getStimeFormated(){
            if(stime==null)
                return "";
            return Utils.getTime(Integer.parseInt(stime));
        }

        public String getSlen() {
            return slen;
        }

        public String getSvol() {
            return svol;
        }

        public String getPflow() {
            return pflow;
        }

        public String getGnr() {
            return gnr;
        }

        public String getMhours() {
            return mhours;
        }

        public String getPhours() {
            return phours;
        }

        public int getTs() {
            return ts;
        }
        public String getFormatedTs() {
            if(ts==0)
                return "";
            return Utils.getTime(ts,true);
        }

        public String getV1vol() {
            return v1vol;
        }

        public String getV2vol() {
            return v2vol;
        }

        public String getV3vol() {
            return v3vol;
        }

        public String getNvol() {
            return nvol;
        }

        public String getSnr() {
            return snr;
        }

        public int getVstate() {
            return vstate;
        }

        public int getPstateInt() {
            return pstate;
        }
        public boolean getPstate() {
            return pstate==0? false:true;
        }

        public String getPpres() {
            return ppres;
        }

        @Override
        public String toString() {
            return "Metric{" +
                    "mode='" + mode + '\'' +
                    ", state='" + state + '\'' +
                    ", rvol='" + rvol + '\'' +
                    ", avol='" + avol + '\'' +
                    ", stime='" + stime + '\'' +
                    ", slen='" + slen + '\'' +
                    ", svol='" + svol + '\'' +
                    ", v1vol='" + v1vol + '\'' +
                    ", v2vol='" + v2vol + '\'' +
                    ", v3vol='" + v3vol + '\'' +
                    ", ppres='" + ppres + '\'' +
                    ", pflow='" + pflow + '\'' +
                    ", gnr='" + gnr + '\'' +
                    ", Mhours='" + mhours + '\'' +
                    ", Phours='" + phours + '\'' +
                    ", ts=" + ts +
                    '}';
        }
    }
}

