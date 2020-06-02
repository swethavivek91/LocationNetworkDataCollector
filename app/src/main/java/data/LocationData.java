
package data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LocationData {

    @SerializedName("latitude")
    @Expose
    private Double latitude;
    @SerializedName("longitude")
    @Expose
    private Double longitude;
    @SerializedName("altitude")
    @Expose
    private Double altitude;
    @SerializedName("accuracy")
    @Expose
    private Float accuracy;
    @SerializedName("speed")
    @Expose
    private Float speed;
    @SerializedName("bearing")
    @Expose
    private Float bearing;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(Float accuracy) {
        this.accuracy = accuracy;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Float getBearing() {
        return bearing;
    }

    public void setBearing(Float bearing) {
        this.bearing = bearing;
    }

    @Override
    public String toString() {
        return new String()+ latitude+","+ longitude+","+ altitude+","+ accuracy+","+ speed+","+ bearing;
    }

    public String getStringInfo() {
        return "latitude" + ","+"longitude" +","+"altitude"+ ","+"accuracy"+ ","+"speed"+ ","+"bearing"+",";
    }
}
