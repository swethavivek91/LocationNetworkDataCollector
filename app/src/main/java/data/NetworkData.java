
package data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.sql.Timestamp;

public class NetworkData {

    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    @SerializedName("time")
    @Expose
    private Timestamp time;
    @SerializedName("networkType")
    @Expose
    private String networkType="NA";
    @SerializedName("networkDataType")
    @Expose
    private int networkDataType=0;
    @SerializedName("networkVoiceType")
    @Expose
    private int networkVoiceType=0;
    @SerializedName("carrierInfo")
    @Expose
    private CarrierInfo carrierInfo;
    @SerializedName("cellIdentity")
    @Expose
    private CellIdentity cellIdentity;
    @SerializedName("cellSignalStrength")
    @Expose
    private CellSignalStrength cellSignalStrength;
    @SerializedName("location")
    @Expose
    private LocationData location;

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public int getNetworkDataType() {
        return networkDataType;
    }

    public void setNetworkDataType(int networkDataType) {
        this.networkDataType = networkDataType;
    }

    public int getNetworkVoiceType() {
        return networkVoiceType;
    }

    public void setNetworkVoiceType(int networkVoiceType) {
        this.networkVoiceType = networkVoiceType;
    }

    public CarrierInfo getCarrierInfo() {
        return carrierInfo;
    }

    public void setCarrierInfo(CarrierInfo carrierInfo) {
        this.carrierInfo = carrierInfo;
    }

    public CellIdentity getCellIdentity() {
        return cellIdentity;
    }

    public void setCellIdentity(CellIdentity cellIdentity) {
        this.cellIdentity = cellIdentity;
    }

    public CellSignalStrength getCellSignalStrength() {
        return cellSignalStrength;
    }

    public void setCellSignalStrength(CellSignalStrength cellSignalStrength) {
        this.cellSignalStrength = cellSignalStrength;
    }

  public LocationData getLocation() {
        return location;
    }

    public void setLocation(LocationData location) {
        this.location = location;
    }
	
    public String getStringInfo(){
        location = new LocationData();
        carrierInfo = new CarrierInfo();
        cellSignalStrength = new CellSignalStrength();
        cellIdentity = new CellIdentity();
       return new String("time")+","+"networkType"+","+"networkDataType"+","+"networkVoiceType"+","+location.getStringInfo()+carrierInfo.getStringInfo()+cellIdentity.getStringInfo()+cellSignalStrength.getStringInfo();
    }
    @Override
    public String toString() {
        return time+","+ networkType+","+ networkDataType+","+ networkVoiceType+","+ location.toString()+","+ carrierInfo.toString()+","+ cellIdentity.toString()+","+ cellSignalStrength.toString();
    }
}
