
package data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CellIdentity {

    @SerializedName("operatorAlpha")
    @Expose
    private String operatorAlpha="NA";
    @SerializedName("bandwidth")
    @Expose
    private int bandwidth=0;

    public int getEarFcnLte() {
        return earFcnLte;
    }

    public void setEarFcnLte(int earFcnLte) {
        this.earFcnLte = earFcnLte;
    }

    @SerializedName("earFcnLte")
    @Expose
    private int earFcnLte=0;

    public int getTacLte() {
        return tacLte;
    }

    public void setTacLte(int tacLte) {
        this.tacLte = tacLte;
    }

    @SerializedName("tacLte")
    @Expose
    private int tacLte=0;

    public int getPciLte() {
        return pciLte;
    }

    public void setPciLte(int pciLte) {
        this.pciLte = pciLte;
    }

    @SerializedName("pciLte")
    @Expose
    private int pciLte=0;

    public int getCiLte() {
        return ciLte;
    }

    public void setCiLte(int ciLte) {
        this.ciLte = ciLte;
    }

    @SerializedName("ciLte")
    @Expose
    private int ciLte=0;

    public String getOperatorAlpha() {
        return operatorAlpha;
    }

    public void setOperatorAlpha(String operatorAlpha) {
        this.operatorAlpha = operatorAlpha;
    }

    public int getBandwidth() {
        return bandwidth;
    }

    public void setBandwidth(int bandwidth) {
        this.bandwidth = bandwidth;
    }

    @Override
    public String toString() {
        return new String()+ operatorAlpha+","+ bandwidth+","+earFcnLte+","+tacLte+","+pciLte+","+ciLte;
    }

    public String getStringInfo() {
        return "operatorAlpha" + ","+"bandwidth"+","+"earFcnLte"+","+"tacLte"+","+"pciLte"+","+"ciLte"+",";
    }
}
