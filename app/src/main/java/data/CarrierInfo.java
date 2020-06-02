
package data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CarrierInfo {

    @SerializedName("operatorName")
    @Expose
    private String operatorName="NA";
    @SerializedName("mcc")
    @Expose
    private String mcc="NA";

    public String getMccMnc() {
        return mccMnc;
    }

    public void setMccMnc(String mccMnc) {
        this.mccMnc = mccMnc;
    }

    @SerializedName("mccMnc")
    @Expose
    private String mccMnc="NA";

    @SerializedName("mnc")
    @Expose
    private String mnc="NA";

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getMcc() {
        return mcc;
    }

    public void setMcc(String mcc) {
        this.mcc = mcc;
    }

    public String getMnc() {
        return mnc;
    }

    public void setMnc(String mnc) {
        this.mcc = mnc;
    }

    @Override
    public String toString() {
        return new String()+ operatorName+","+mccMnc+","+ mcc+","+mnc;
    }

    public String getStringInfo() {
        return new String("operatorName")+ ","+"mccMnc"+","+"mcc"+","+"mnc"+",";
    }
}
