package com.appifiedtech.app.gcm;

import android.content.Context;
import android.os.PowerManager;

// Created by Priyabrat on 08-06-2015.


public abstract class WakeLocker {
    private static PowerManager.WakeLock wakeLock;

    public static void acquire(Context context)
    {
        if (wakeLock != null)
            wakeLock.release();
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.ON_AFTER_RELEASE, "WakeLock");
        wakeLock.acquire();
    }

    public static void release()
    {
        if (wakeLock != null)
            wakeLock.release();
        wakeLock = null;
    }
}
