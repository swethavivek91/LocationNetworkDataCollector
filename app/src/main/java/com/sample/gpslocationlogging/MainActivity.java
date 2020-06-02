package com.sample.gpslocationlogging;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {


    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private Button stopLogging, closeActivity, startsvc, updateTime;
    private final static int ALL_PERMISSIONS_RESULT = 101;
    private FloatingActionButton mfab;
    private EditText time, filename;
    TextView mCurrentView, mFusedView;
    private int timeValue = 5;
    private String filenameString = "";
    Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FileHandler.getInstance().setContext(getApplicationContext());
        mCurrentView = findViewById(R.id.rss);
        mFusedView = findViewById(R.id.rssfused);
        timeValue = FileHandler.getInstance().getTime();
        RelativeLayout layout = findViewById(R.id.layout);
        time = layout.findViewById(R.id.time);
        mHandler = new Handler();
        updateTime = layout.findViewById(R.id.button2);

        //FileHandler.getInstance().getFileName();
        stopLogging = findViewById(R.id.stop);
        closeActivity = findViewById(R.id.close);
        startsvc = findViewById(R.id.start);

        RelativeLayout layout1 = findViewById(R.id.layout1);
        filename = layout1.findViewById(R.id.filename);
        mfab = layout1.findViewById(R.id.floatingActionButton);

        checkPermission();
        stopLogging.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isServiceRunningInForeground()) {
                    Toast.makeText(MainActivity.this,"Stopping Service",Toast.LENGTH_LONG).show();
                    stopService();
                }
            }
        });
        mfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Creating new file",Toast.LENGTH_LONG).show();
                FileHandler.getInstance().createFile(filenameString);
                FileHandler.getInstance().createFusedFile(filenameString);
            }
        });
        closeActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Closing UI",Toast.LENGTH_LONG).show();
                MainActivity.this.finish();
            }
        });
        time.addTextChangedListener(mWatcher);
        filename.addTextChangedListener(mWatcher1);
        updateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Updating time",Toast.LENGTH_LONG).show();
                updateTime();
            }
        });

        startsvc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Starting Service",Toast.LENGTH_LONG).show();
                if (!isServiceRunningInForeground()) {
                    startService();
                }
            }
        });
        time.setText(String.valueOf(timeValue));
        if (!isServiceRunningInForeground()) {
            startService();
        }
        mHandler.post(UIrunnable);
    }

    private void checkPermission() {
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_PHONE_STATE);
        permissions.add("android.permission.ACCESS_GPS");
        permissions.add("android.permission.ACCESS_ASSISTED_GPS");
        permissions.add("android.permission.ACCESS_BACKGROUND_LOCATION");
        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }
    }

    Runnable UIrunnable = new Runnable() {
        @Override
        public void run() {
            if(mCurrentView ==null || mFusedView ==null){
                mCurrentView = findViewById(R.id.rss);
                mFusedView = findViewById(R.id.rssfused);
            }
            mCurrentView.setText(FileHandler.getInstance().getmCurrentData());
            mFusedView.setText(FileHandler.getInstance().getmCurrentFusedData());
            mHandler.postDelayed(UIrunnable,FileHandler.getInstance().getDataFrequency());
        }
    };

    private void updateTime() {
        FileHandler.getInstance().setDataFrequency(timeValue);
    }

    TextWatcher mWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }


        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString() != null && s.toString().isEmpty() == false) {
                timeValue = Integer.parseInt(s.toString());
            }
        }
    };

    TextWatcher mWatcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }


        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString() != null && s.toString().isEmpty() == false) {
                filenameString = s.toString();
            }
        }
    };

    public boolean isServiceRunningInForeground() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (ForeGroundService.class.getName().equals(service.service.getClassName())) {
                if (service.foreground) {
                    // Log.d("MainActivity","isServiceRunningInForeground true");
                    return true;
                }
            }
        }
        return false;
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


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (String perms : permissionsToRequest) {
                    if (!hasPermission(perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    public void startService() {
        Intent serviceIntent = new Intent(this, ForeGroundService.class);
        serviceIntent.setAction(ForeGroundService.STARTFOREGROUND_ACTION);
        serviceIntent.putExtra("inputExtra", "Location Signal NetworkData logging");
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void stopService() {
        Intent serviceIntent = new Intent(this, ForeGroundService.class);
        serviceIntent.setAction(ForeGroundService.STOPFOREGROUND_ACTION);
        serviceIntent.putExtra("inputExtra", "Location Signal NetworkData logging");
        ContextCompat.startForegroundService(this, serviceIntent);
    }

}
