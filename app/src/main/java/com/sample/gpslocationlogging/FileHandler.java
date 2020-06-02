package com.sample.gpslocationlogging;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import data.NetworkData;

import static android.content.Context.MODE_PRIVATE;

class FileHandler {
    private static final FileHandler ourInstance = new FileHandler();
    private Context mContext;
    Handler mHandler;
    String mCurrentData="";
    String mCurrentFusedData="";

    public int getDataFrequency() {
        return (mDataFrequency*1000);
    }

    public String getmCurrentData() {
        return mCurrentData;
    }

    public String getmCurrentFusedData() {
        return mCurrentFusedData;
    }

    public void setDataFrequency(int mDataFrequency) {
        this.mDataFrequency = mDataFrequency;
        updateTime(mDataFrequency);
    }

    int mDataFrequency =5;
    BlockingDeque<String> deque = new LinkedBlockingDeque<String>();
    BlockingDeque<String> Fuseddeque = new LinkedBlockingDeque<String>();
    static FileHandler getInstance() {
        return ourInstance;
    }

    private FileHandler() {
    }

    public void setContext(Context ctx){
        this.mContext = ctx;
        mHandler = new Handler();
    }

    public synchronized int getTime() {
        SharedPreferences mPrefs = mContext.getSharedPreferences("SignalData", MODE_PRIVATE);
        int time = mPrefs.getInt("time",5);
        setDataFrequency(time);
        return time;
    }
    public synchronized String getFileName() {
        SharedPreferences mPrefs = mContext.getSharedPreferences("SignalData", MODE_PRIVATE);
        String filename = mPrefs.getString("Filename",null);
        File rootPath = new File(Environment.getExternalStorageDirectory(), "location");
        if(rootPath.exists() && filename!=null) {
            File file = new File(rootPath, filename);
            if (!file.exists()) {
                filename = createFile("");
                updateFileName(filename);
            } else if (isSizeGreater(file.length())) {
                filename = createFile("");
            }
        }else{
           filename = createFile("");
        }
        return filename;
    }

    public synchronized String getFusedFileName() {
        SharedPreferences mPrefs = mContext.getSharedPreferences("SignalData", MODE_PRIVATE);
        String filename = mPrefs.getString("FilenameFused",null);
        File rootPath = new File(Environment.getExternalStorageDirectory(), "location");
        if(rootPath.exists() && filename!=null) {
            File file = new File(rootPath, filename);
            if (!file.exists()) {
                filename = createFusedFile("");
                updateFusedFileName(filename);
            } else if (isSizeGreater(file.length())) {
                filename = createFusedFile("");
            }
        }else{
            filename = createFusedFile("");
        }
        return filename;
    }

    public synchronized String createFile(String append) {
        try {
            File rootPath = new File(Environment.getExternalStorageDirectory(), "location");
            if (!rootPath.exists()) {
                rootPath.mkdirs();
            }
            String filename = new SimpleDateFormat("yyyyMMddHHmm'.csv'").format(new Date());
            if(append!=null && append.isEmpty()==false) {
                filename = append + filename;
            }
            File f = new File(rootPath, filename);
            updateFileName(filename);
            String s = new NetworkData().getStringInfo() + "\n";
            FileOutputStream fileOutputStream = new FileOutputStream(f, true);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
            writer.append(s);
            writer.close();
            fileOutputStream.close();
            return filename;
        }catch (Exception e){
            Log.e("Error","Exception"+e.getMessage());
            return null;
        }

    }

    public synchronized String createFusedFile(String append) {
        try {
            File rootPath = new File(Environment.getExternalStorageDirectory(), "location");
            if (!rootPath.exists()) {
                rootPath.mkdirs();
            }
            String filename = new SimpleDateFormat("yyyyMMddHHmm'Fused.csv'").format(new Date());
            if(append!=null && append.isEmpty()==false) {
                filename = append + filename;
            }
            File f = new File(rootPath, filename);
            updateFusedFileName(filename);
            String s = new NetworkData().getStringInfo() + "\n";
            FileOutputStream fileOutputStream = new FileOutputStream(f, true);
            OutputStreamWriter writer = new OutputStreamWriter(fileOutputStream);
            writer.append(s);
            writer.close();
            fileOutputStream.close();
            return filename;
        }catch (Exception e){
            return null;
        }

    }
    private boolean isSizeGreater(long length) {
        if(length>0) {
            long fileSizeInMB = length / (1024 * 1024);
            if (fileSizeInMB > 20) {
                return true;
            } else {
                return false;
            }
        }return false;
    }

    public void updateFileName(String filename) {
        SharedPreferences mPrefs = mContext.getSharedPreferences("SignalData", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("Filename", filename);
        prefsEditor.commit();
    }

    public void updateTime(int time) {
        SharedPreferences mPrefs = mContext.getSharedPreferences("SignalData", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putInt("time", time);
        prefsEditor.commit();
    }

    public void updateFusedFileName(String filename) {
        SharedPreferences mPrefs = mContext.getSharedPreferences("SignalData", MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = mPrefs.edit();
        prefsEditor.putString("FilenameFused", filename);
        prefsEditor.commit();
    }

    public void writeToFile(){
        OutputStreamWriter writer = null;
        FileOutputStream fileOutputStream=null;
        try {
            String filename = getFileName();
            File f = new File(Environment.getExternalStorageDirectory() + "/location/" + filename);
            //Log.d("File","exists"+f.exists());
            String s = deque.takeLast();
           // Log.d("File","Inside write"+deque.size());
            String s1 = s.concat("\n");
            fileOutputStream = new FileOutputStream(f, true);
            writer = new OutputStreamWriter(fileOutputStream);
           // Log.d("File","Inside write" +s1);
            writer.append(s1);
            writer.flush();
            //writer.close();
           // fileOutputStream.close();
        }catch (Exception e){
            Log.e("FileHandler","exception in write to file RSS"+e.getMessage());
        }
        finally {
            try {
                if(writer!=null) {
                    writer.close();
                }
                if(fileOutputStream!=null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeToFusedFile(){
        OutputStreamWriter writer = null;
        FileOutputStream fileOutputStream=null;
        try {
            String filename = getFusedFileName();
            File f = new File(Environment.getExternalStorageDirectory() + "/location/" + filename);
            //Log.d("File","exists"+f.exists());
            String s = Fuseddeque.takeLast();
            //Log.d("File","Inside fused write"+Fuseddeque.size());
            String s1 = s.concat("\n");
            fileOutputStream = new FileOutputStream(f, true);
            writer = new OutputStreamWriter(fileOutputStream);
           // Log.d("File","Inside fused write" +s1);
            writer.append(s1);
            writer.flush();
            //writer.close();
            // fileOutputStream.close();
        }catch (Exception e){
            Log.e("FileHandler","exception in write to file RSS"+e.getMessage());
        }
        finally {
            try {
                if(writer!=null) {
                    writer.close();
                }
                if(fileOutputStream!=null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public synchronized void appendUsingBufferedWriter() {
        String filename = getFileName();
        //Log.d("File","filename"+filename);
        try {
            String s = deque.takeLast();

            String filePath = Environment.getExternalStorageDirectory() + "/location/" + filename;
            File file = new File(filePath);
           // Log.d("File","exists"+file.exists());
            FileWriter fr = null;
            BufferedWriter br = null;
            try {
                // to append to file, you need to initialize FileWriter using below constructor
                fr = new FileWriter(file, true);
                br = new BufferedWriter(fr);
                for (int i = 0; i < 1; i++) {
                    br.newLine();
                    // you can use write or append method
                    br.append(s);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public synchronized void appendFusedUsingBufferedWriter() {
        String filename = getFusedFileName();
     //   Log.d("File","filename"+filename);
        try {
            String s = Fuseddeque.takeLast();

            String filePath = Environment.getExternalStorageDirectory() + "/location/" + filename;
            File file = new File(filePath);
      //      Log.d("File","exists"+file.exists());
            FileWriter fr = null;
            BufferedWriter br = null;
            try {
                // to append to file, you need to initialize FileWriter using below constructor
                fr = new FileWriter(file, true);
                br = new BufferedWriter(fr);
                for (int i = 0; i < 1; i++) {
                    br.newLine();
                    // you can use write or append method
                    br.append(s);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    br.close();
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

        Runnable run = new Runnable() {
        @Override
        public void run() {
            writeToFile();
        }
    };
    Runnable run1 = new Runnable() {
        @Override
        public void run() {
            writeToFusedFile();
        }
    };

    public synchronized void addToQueue(String s){
        try {
            mCurrentData = s;
            deque.put(s);
            mHandler.postDelayed(run,1000);
            //Log.d("File","Inside addToQueue"+deque.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public synchronized void addToFusedQueue(String s){
        try {
            mCurrentFusedData = s;
            Fuseddeque.put(s);
            mHandler.postDelayed(run1,1000);
            //Log.d("File","Inside addToFusedQueue"+Fuseddeque.size());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
