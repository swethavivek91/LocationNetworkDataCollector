
package data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gsm {

    @SerializedName("bitErrorRate")
    @Expose
    private int bitErrorRate =0;
    @SerializedName("rssi")
    @Expose
    private int rssi =0;
    @SerializedName("timingAdvance")
    @Expose
    private int timingAdvance=0;

    public int getBitErrorRate() {
        return bitErrorRate;
    }

    public void setBitErrorRate(int bitErrorRate) {
        this.bitErrorRate = bitErrorRate;
    }

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public int getTimingAdvance() {
        return timingAdvance;
    }

    public void setTimingAdvance(int timingAdvance) {
        this.timingAdvance = timingAdvance;
    }

    @Override
    public String toString() {
        return new String()+ bitErrorRate+","+ rssi+","+ timingAdvance;
    }

    public String getStringInfo(){
        return "bitErrorRateGsm" + ","+"rssiGsm"+ ","+"timingAdvanceGsm";
    }
}
