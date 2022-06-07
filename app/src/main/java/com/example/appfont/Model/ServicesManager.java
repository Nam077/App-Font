package com.example.appfont.Model;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import java.util.Random;

public class ServicesManager extends Service {

    private final IBinder iBinder = new LocalBinder();
    // Class that generated Random Numbers
    private final Random RNG = new Random();

    public class LocalBinder extends Binder {
        ServicesManager getService(){
            return ServicesManager.this;
        }
    }
    public ServicesManager() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return iBinder;
    }

    public int getRandomNumber(){
        return RNG.nextInt(20);
    }
}