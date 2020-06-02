
package data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Wcdma {

    @SerializedName("ecNo")
    @Expose
    private int ecNo = 0;

    public int getEcNo() {
        return ecNo;
    }

    public void setEcNo(int ecNo) {
        this.ecNo = ecNo;
    }

    @Override
    public String toString() {
        return new String()+ ecNo;
    }

    public String getStringInfo(){
        return "ecNoWcdma";
    }
}
