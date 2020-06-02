
package data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CellSignalStrength {

    @SerializedName("strength")
    @Expose
    private int strength=0;
    @SerializedName("level")
    @Expose
    private int level=0;
    @SerializedName("lte")
    @Expose
    private Lte lte;
    @SerializedName("gsm")
    @Expose
    private Gsm gsm;
    @SerializedName("wcdma")
    @Expose
    private Wcdma wcdma;

    public int getStrength() {
        return strength;
    }

    public void setStrength(int strength) {
        this.strength = strength;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public Lte getLte() {
        return lte;
    }

    public void setLte(Lte lte) {
        this.lte = lte;
    }

    public Gsm getGsm() {
        return gsm;
    }

    public void setGsm(Gsm gsm) {
        this.gsm = gsm;
    }

    public Wcdma getWcdma() {
        return wcdma;
    }

    public void setWcdma(Wcdma wcdma) {
        this.wcdma = wcdma;
    }

    @Override
    public String toString() {
        return new String()+ strength+","+ level+","+ lte.toString()+","+ gsm.toString()+","+ wcdma.toString();
    }

    public String getStringInfo() {
        lte = new Lte();
        gsm = new Gsm();
        wcdma = new Wcdma();
        return "strength" + ","+"level"+ ","+lte.getStringInfo()+ ","+gsm.getStringInfo()+ ","+wcdma.getStringInfo();
    }
}
