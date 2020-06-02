
package data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Lte {

    @SerializedName("rsrp")
    @Expose
    private int rsrp = 0;
    @SerializedName("rsrq")
    @Expose
    private int rsrq = 0;
    @SerializedName("rssi")
    @Expose
    private int rssi = 0;
    @SerializedName("rssnr")
    @Expose
    private int rssnr = 0;
    @SerializedName("CQi")
    @Expose
    private int cQi = 0;
    @SerializedName("timingAdvance")
    @Expose
    private int timingAdvance = 0;

    public int getRsrp() {
        return rsrp;
    }

    public void setRsrp(int rsrp) {
        this.rsrp = rsrp;
    }

    public int getRsrq() {
        return rsrq;
    }

    public void setRsrq(int rsrq) {
        this.rsrq = rsrq;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getRssnr() {
        return rssnr;
    }

    public void setRssnr(int rssnr) {
        this.rssnr = rssnr;
    }

    public int getCQi() {
        return cQi;
    }

    public void setCQi(int cQi) {
        this.cQi = cQi;
    }

    public int getTimingAdvance() {
        return timingAdvance;
    }

    public void setTimingAdvance(int timingAdvance) {
        this.timingAdvance = timingAdvance;
    }

    @Override
    public String toString() {
        return new String()+ rsrp+","+ rsrq+","+ rssi+","+ rssnr+","+ cQi+","+ timingAdvance;
    }

    public String getStringInfo() {
        return "rsrpLte" + ","+"rsrqLte"+ ","+"rssiLte"+ ","+"rssnrLte"+ ","+"cQiLte"+","+"timingAdvanceLte";
    }
}
