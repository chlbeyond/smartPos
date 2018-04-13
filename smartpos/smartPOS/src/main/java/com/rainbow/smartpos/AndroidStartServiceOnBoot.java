package com.rainbow.smartpos;


import android.app.Service;
import android.os.IBinder;
import android.content.Intent;

public class AndroidStartServiceOnBoot extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // do something when the service is created
    }
}