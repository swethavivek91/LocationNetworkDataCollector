package com.sample.gpslocationlogging;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class ForeGroundService extends Service {
    public static final String CHANNEL_ID = "ForegroundServiceChannel";
    public static final String STARTFOREGROUND_ACTION = "START";
    public static final String STOPFOREGROUND_ACTION = "STOP";
    DataCollector dataCollector;
    Handler handler, handler1;

    @Override
    public void onCreate() {
        super.onCreate();
        dataCollector = new DataCollector(ForeGroundService.this);

        if (handler == null) {
            handler = new Handler();
        }
        if (handler1 == null) {
            handler1 = new Handler();
        }
        FileHandler.getInstance().setContext(getApplicationContext());
        //dataCollector.getData();
        //dataCollector.getFusedData();
        postLocationLogging();
        postLocationLoggingFused();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String input = intent.getStringExtra("inputExtra");
            if (intent.getAction().equals(STARTFOREGROUND_ACTION)) {
                createNotificationChannel();
                Intent notificationIntent = new Intent(this, MainActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(this,
                        0, notificationIntent, 0);

                Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle("Location Service")
                        .setContentText(input)
                        .setSmallIcon(android.R.drawable.ic_menu_mylocation)
                        .setContentIntent(pendingIntent)
                        .build();

                startForeground(1, notification);

            } else if (intent.getAction().equals(STOPFOREGROUND_ACTION)) {
              //  Log.d("Service", "Received Stop Foreground Intent");
                //your end servce code
                stopForeground(true);
                stopSelf();
            }
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        dataCollector.stopListener();
        stopData();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }


    private void postLocationLogging() {
        handler.postDelayed(runnable, FileHandler.getInstance().getDataFrequency());
    }

    public Runnable runnable = new Runnable() {
        @Override
        public void run() {
            dataCollector.getData();
            postLocationLogging();
        }
    };

    private void postLocationLoggingFused() {
        handler1.postDelayed(runnable1,  FileHandler.getInstance().getDataFrequency());
    }

    public Runnable runnable1 = new Runnable() {
        @Override
        public void run() {
            dataCollector.getFusedData();
            postLocationLoggingFused();
        }
    };

    public void stopData() {
        if (handler != null) {
            handler.removeCallbacks(runnable);
            handler = null;
        }
        if (handler1 != null) {
            handler1.removeCallbacks(runnable1);
            handler1 = null;
        }
    }
}