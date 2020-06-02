package com.sample.gpslocationlogging;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.CellInfo;
import android.telephony.CellInfoCdma;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoLte;
import android.telephony.CellInfoWcdma;
import android.telephony.CellSignalStrengthCdma;
import android.telephony.CellSignalStrengthGsm;
import android.telephony.CellSignalStrengthLte;
import android.telephony.CellSignalStrengthWcdma;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.apache.commons.lang3.ObjectUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import data.CarrierInfo;
import data.CellIdentity;
import data.CellSignalStrength;
import data.Gsm;
import data.LocationData;
import data.Lte;
import data.NetworkData;
import data.Wcdma;

/**
 * Created by anupamchugh on 28/11/16.
 */

public class DataCollector extends Service implements LocationListener {

    private final ForeGroundService mContext;
    FusedLocationProviderClient mFusedLocationClient;
    LocationData mLocationData = new LocationData(), mLocationFusedData = new LocationData();
    boolean checkGPS = false;
    boolean checkNetwork = false;
    boolean canGetLocation = false;
    Location loc;
    double latitude, longitude, altitude;
    float bearing, accuracy, speed;


    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;


    private static final long MIN_TIME_BW_UPDATES = 1000 * 5 * 1;
    protected LocationManager locationManager;

    public DataCollector(ForeGroundService mContext) {
        this.mContext = mContext;
        //getLocation();
    }

    protected void getFusedLocation() {
        NetworkData mFusedNetworkData = new NetworkData();
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(mContext);
        mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location mLastLocation) {

                if (mLastLocation != null) {
                    mLocationFusedData.setLatitude(mLastLocation.getLatitude());
                    mLocationFusedData.setLongitude(mLastLocation.getLongitude());
                    mLocationFusedData.setAltitude(mLastLocation.getAltitude());
                    mLocationFusedData.setBearing((mLastLocation.getBearing()));
                    mLocationFusedData.setSpeed(mLastLocation.getSpeed());
                    mLocationFusedData.setAccuracy(mLastLocation.getAccuracy());
                    mFusedNetworkData.setLocation(mLocationFusedData);
                    // Log.d("RSS Fused", "latitude: " + mLastLocation.getLatitude() + "longitude:" + mLastLocation.getLongitude());
                } else {
                    mLocationFusedData.setLatitude(0.0);
                    mLocationFusedData.setLongitude(0.0);
                    mLocationFusedData.setAltitude(0.0);
                    mLocationFusedData.setBearing(0f);
                    mLocationFusedData.setSpeed(0f);
                    mLocationFusedData.setAccuracy(0f);
                }
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                mFusedNetworkData.setTime(timestamp);
                mFusedNetworkData.setLocation(mLocationFusedData);
                getFusedSignalStrength(mContext, mFusedNetworkData);
                String s = mFusedNetworkData.toString();
                //Log.d("RSS fused values", s);
                FileHandler.getInstance().addToFusedQueue(s);
            }
        });

    }

    protected void getLocation() {

        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);
            latitude = 0;
            longitude = 0;
            altitude = 0;
            bearing = 0;
            accuracy = 0;
            speed = 0;
            // get GPS status
            checkGPS = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // get network provider status
            checkNetwork = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            if (!checkGPS && !checkNetwork) {
                Toast.makeText(mContext, "Location services disabled", Toast.LENGTH_LONG).show();
            } else {
                this.canGetLocation = true;

                // if GPS Enabled get lat/long using GPS Services
                if (checkGPS) {

                    // if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){//&& ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    // }
                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        Toast.makeText(mContext, "Permissions not given", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        locationManager.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                        if (locationManager != null) {
                            loc = locationManager
                                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                            if (loc != null) {
                                latitude = loc.getLatitude();
                                longitude = loc.getLongitude();
                                altitude = loc.getAltitude();
                                bearing = loc.getBearing();
                                speed = loc.getSpeed();
                                accuracy = loc.getAccuracy();
                            }
                        }
                    }

                }


                /*if (checkNetwork) {


                    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                    }
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

                    if (locationManager != null) {
                        loc = locationManager
                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                    }

                    if (loc != null) {
                        latitude = loc.getLatitude();
                        longitude = loc.getLongitude();
                    }
                }*/

            }
            mLocationData.setLatitude(latitude);
            mLocationData.setLongitude(longitude);
            mLocationData.setAccuracy(accuracy);
            mLocationData.setAltitude(altitude);
            mLocationData.setSpeed(speed);
            mLocationData.setBearing(bearing);
        } catch (Exception e) {
            mLocationData.setLatitude(latitude);
            mLocationData.setLongitude(longitude);
            mLocationData.setAccuracy(accuracy);
            mLocationData.setAltitude(altitude);
            mLocationData.setSpeed(speed);
            mLocationData.setBearing(bearing);
            // e.printStackTrace();
        }
        loc = null;
    }

    private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
        ArrayList<String> result = new ArrayList<String>();

        for (String perm : wanted) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }

    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }

    public double getLongitude() {
        if (loc != null) {
            longitude = loc.getLongitude();
        }
        return longitude;
    }

    public double getLatitude() {
        if (loc != null) {
            latitude = loc.getLatitude();
        }
        return latitude;
    }

    public boolean canGetLocation() {
        return this.canGetLocation;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);


        alertDialog.setTitle("GPS is not Enabled!");

        alertDialog.setMessage("Do you want to turn on GPS?");


        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });


        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        alertDialog.show();
    }


    public void stopListener() {
        if (locationManager != null) {

            if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.removeUpdates(DataCollector.this);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void getSignalStrength(Context context, NetworkData mNetworkData) {
        CellSignalStrength mSigStrength = new CellSignalStrength();
        CarrierInfo mCarrierInfo = new CarrierInfo();
        CellIdentity mIdentity = new CellIdentity();
        Gsm mGsm = new Gsm();
        Wcdma lWcdma = new Wcdma();
        Lte mLte = new Lte();
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mNetworkData.setNetworkDataType(telephonyManager.getDataNetworkType());
            mNetworkData.setNetworkVoiceType(telephonyManager.getVoiceNetworkType());
            mCarrierInfo.setOperatorName(telephonyManager.getNetworkOperatorName());
            mCarrierInfo.setMccMnc(telephonyManager.getNetworkOperator());

            List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();   //This will give info of all sims present inside your mobile
            if (cellInfos != null) {
                for (int i = 0; i < cellInfos.size(); i++) {
                    //Log.d("Swetha:","-->"+cellInfos.get(i).toString());
                    if (cellInfos.get(i).isRegistered()) {
                        if (cellInfos.get(i) instanceof CellInfoWcdma) {
                            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfos.get(i);
                            CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                mCarrierInfo.setMcc(cellInfoWcdma.getCellIdentity().getMccString().toString());
                                mCarrierInfo.setMnc(cellInfoWcdma.getCellIdentity().getMncString());
                                mIdentity.setOperatorAlpha(cellInfoWcdma.getCellIdentity().getOperatorAlphaLong().toString());
                            }
                            mSigStrength.setStrength(cellSignalStrengthWcdma.getDbm());
                            mNetworkData.setNetworkType("WCDMA");
                            mSigStrength.setLevel(cellSignalStrengthWcdma.getLevel());
                            lWcdma.setEcNo(0);//TODO : getEcNo Not available till Android R
                        } else if (cellInfos.get(i) instanceof CellInfoGsm) {
                            CellInfoGsm cellInfogsm = (CellInfoGsm) cellInfos.get(i);
                            CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();
                            mSigStrength.setStrength(cellSignalStrengthGsm.getDbm());

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                mGsm.setBitErrorRate(cellSignalStrengthGsm.getBitErrorRate());
                            }
                            mGsm.setRssi(0);//TODO : Not available till Android R
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                mGsm.setTimingAdvance(cellSignalStrengthGsm.getTimingAdvance());
                            }
                            mNetworkData.setNetworkType("GSM");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                mIdentity.setOperatorAlpha(cellInfogsm.getCellIdentity().getOperatorAlphaLong().toString());
                                mCarrierInfo.setMcc(cellInfogsm.getCellIdentity().getMccString());
                                mCarrierInfo.setMnc(cellInfogsm.getCellIdentity().getMncString());
                            }
                        } else if (cellInfos.get(i) instanceof CellInfoLte) {
                            CellInfoLte cellInfoLte = (CellInfoLte) cellInfos.get(i);
                            CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                            mSigStrength.setStrength(cellSignalStrengthLte.getDbm());

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                mLte.setCQi(cellSignalStrengthLte.getCqi());
                                mLte.setRsrp(cellSignalStrengthLte.getRsrp());
                                mLte.setRsrq(cellSignalStrengthLte.getRsrq());
                                mLte.setRssnr(cellSignalStrengthLte.getRssnr());
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                mLte.setRssi(cellSignalStrengthLte.getRssi());
                            }
                            mLte.setTimingAdvance(cellSignalStrengthLte.getTimingAdvance());

                            mNetworkData.setNetworkType("LTE");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                mIdentity.setOperatorAlpha(cellInfoLte.getCellIdentity().getOperatorAlphaLong().toString());
                                mIdentity.setBandwidth(cellInfoLte.getCellIdentity().getBandwidth());
                                mCarrierInfo.setMcc(cellInfoLte.getCellIdentity().getMccString());
                                mCarrierInfo.setMnc(cellInfoLte.getCellIdentity().getMncString());
                                mIdentity.setEarFcnLte(cellInfoLte.getCellIdentity().getEarfcn());
                                mIdentity.setTacLte(cellInfoLte.getCellIdentity().getTac());
                                mIdentity.setPciLte(cellInfoLte.getCellIdentity().getPci());
                                mIdentity.setCiLte(cellInfoLte.getCellIdentity().getCi());
                            }
                        } else if (cellInfos.get(i) instanceof CellInfoCdma) {
                            CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfos.get(i);
                            CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                            mSigStrength.setStrength(cellSignalStrengthCdma.getDbm());
                            mNetworkData.setNetworkType("CDMA");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                mIdentity.setOperatorAlpha(cellInfoCdma.getCellIdentity().getOperatorAlphaLong().toString());
                            }
                        }
                    }
                }
            }
            mSigStrength.setLte(mLte);
            mSigStrength.setWcdma(lWcdma);
            mSigStrength.setGsm(mGsm);
            mNetworkData.setCarrierInfo(mCarrierInfo);
            mNetworkData.setCellSignalStrength(mSigStrength);
            mNetworkData.setCellIdentity(mIdentity);
        } catch (Exception e) {
            Log.d("RSS", "Exception in getSigSTringth Fused");
        }
    }

    public void getFusedSignalStrength(Context context, NetworkData mNetworkData) {
        CellSignalStrength mSigStrength = new CellSignalStrength();
        CarrierInfo mCarrierInfo = new CarrierInfo();
        CellIdentity mIdentity = new CellIdentity();
        Gsm mGsm = new Gsm();
        Wcdma lWcdma = new Wcdma();
        Lte mLte = new Lte();
        try {
            TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mNetworkData.setNetworkDataType(telephonyManager.getDataNetworkType());
            mNetworkData.setNetworkVoiceType(telephonyManager.getVoiceNetworkType());
            mCarrierInfo.setMccMnc(telephonyManager.getNetworkOperator());
            mCarrierInfo.setOperatorName(telephonyManager.getNetworkOperatorName());

            List<CellInfo> cellInfos = telephonyManager.getAllCellInfo();   //This will give info of all sims present inside your mobile
            if (cellInfos != null) {
                for (int i = 0; i < cellInfos.size(); i++) {
                    //Log.d("Swetha:","-->"+cellInfos.get(i).toString());
                    if (cellInfos.get(i).isRegistered()) {
                        if (cellInfos.get(i) instanceof CellInfoWcdma) {
                            CellInfoWcdma cellInfoWcdma = (CellInfoWcdma) cellInfos.get(i);
                            CellSignalStrengthWcdma cellSignalStrengthWcdma = cellInfoWcdma.getCellSignalStrength();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                mIdentity.setOperatorAlpha(cellInfoWcdma.getCellIdentity().getOperatorAlphaLong().toString());
                                mCarrierInfo.setMcc(cellInfoWcdma.getCellIdentity().getMccString().toString());
                                mCarrierInfo.setMnc(cellInfoWcdma.getCellIdentity().getMncString());
                            }
                            mSigStrength.setStrength(cellSignalStrengthWcdma.getDbm());
                            mNetworkData.setNetworkType("WCDMA");
                            mSigStrength.setLevel(cellSignalStrengthWcdma.getLevel());
                            lWcdma.setEcNo(0);//TODO : getEcNo Not available till Android R

                        } else if (cellInfos.get(i) instanceof CellInfoGsm) {
                            CellInfoGsm cellInfogsm = (CellInfoGsm) cellInfos.get(i);
                            CellSignalStrengthGsm cellSignalStrengthGsm = cellInfogsm.getCellSignalStrength();
                            mSigStrength.setStrength(cellSignalStrengthGsm.getDbm());

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                mGsm.setBitErrorRate(cellSignalStrengthGsm.getBitErrorRate());
                            }
                            mGsm.setRssi(0);//TODO : Not available till Android R
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                mGsm.setTimingAdvance(cellSignalStrengthGsm.getTimingAdvance());
                            }
                            mNetworkData.setNetworkType("GSM");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                mIdentity.setOperatorAlpha(cellInfogsm.getCellIdentity().getOperatorAlphaLong().toString());
                                mCarrierInfo.setMcc(cellInfogsm.getCellIdentity().getMccString());
                                mCarrierInfo.setMnc(cellInfogsm.getCellIdentity().getMncString());
                            }
                        } else if (cellInfos.get(i) instanceof CellInfoLte) {
                            CellInfoLte cellInfoLte = (CellInfoLte) cellInfos.get(i);
                            CellSignalStrengthLte cellSignalStrengthLte = cellInfoLte.getCellSignalStrength();
                            mSigStrength.setStrength(cellSignalStrengthLte.getDbm());

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                mLte.setCQi(cellSignalStrengthLte.getCqi());
                                mLte.setRsrp(cellSignalStrengthLte.getRsrp());
                                mLte.setRsrq(cellSignalStrengthLte.getRsrq());
                                mLte.setRssnr(cellSignalStrengthLte.getRssnr());
                            }
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                mLte.setRssi(cellSignalStrengthLte.getRssi());
                            }
                            mLte.setTimingAdvance(cellSignalStrengthLte.getTimingAdvance());

                            mNetworkData.setNetworkType("LTE");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                mIdentity.setOperatorAlpha(cellInfoLte.getCellIdentity().getOperatorAlphaLong().toString());
                                mIdentity.setBandwidth(cellInfoLte.getCellIdentity().getBandwidth());
                                mCarrierInfo.setMcc(cellInfoLte.getCellIdentity().getMccString());
                                mCarrierInfo.setMnc(cellInfoLte.getCellIdentity().getMncString());
                                mIdentity.setEarFcnLte(cellInfoLte.getCellIdentity().getEarfcn());
                                mIdentity.setTacLte(cellInfoLte.getCellIdentity().getTac());
                                mIdentity.setPciLte(cellInfoLte.getCellIdentity().getPci());
                                mIdentity.setCiLte(cellInfoLte.getCellIdentity().getCi());
                            }
                        } else if (cellInfos.get(i) instanceof CellInfoCdma) {
                            CellInfoCdma cellInfoCdma = (CellInfoCdma) cellInfos.get(i);
                            CellSignalStrengthCdma cellSignalStrengthCdma = cellInfoCdma.getCellSignalStrength();
                            mSigStrength.setStrength(cellSignalStrengthCdma.getDbm());
                            mNetworkData.setNetworkType("CDMA");
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                mIdentity.setOperatorAlpha(cellInfoCdma.getCellIdentity().getOperatorAlphaLong().toString());
                            }
                        }
                    }
                }
            }
            mSigStrength.setLte(mLte);
            mSigStrength.setWcdma(lWcdma);
            mSigStrength.setGsm(mGsm);
            mNetworkData.setCarrierInfo(mCarrierInfo);
            mNetworkData.setCellSignalStrength(mSigStrength);
            mNetworkData.setCellIdentity(mIdentity);
        } catch (Exception e) {
            Log.e("RSS", "Exception in getSigSTringth Fused");
        }
    }

    public synchronized void getData() {
        try {
            NetworkData mData = new NetworkData();
            getLocation();
            Timestamp timestamp = new Timestamp(System.currentTimeMillis());
            mData.setTime(timestamp);
            mData.setLocation(mLocationData);
            getSignalStrength(mContext, mData);
            String s = mData.toString();
            //Log.d("RSS values", s);
            FileHandler.getInstance().addToQueue(s);
        } catch (Exception e) {
            Log.e("Error", "getData" + e.getLocalizedMessage());
        }
    }

    public synchronized void getFusedData() {
        //mFusedData = new LocationData();
        try {
            getFusedLocation();
        } catch (Exception e) {
            Log.e("Error", "getData" + e.getLocalizedMessage());
        }
    }

}
