package com.appifiedtech.app.gcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by Priyabrat on 05-06-2015.
 */

// Fetch updated Instance ID token and notify our app's server of any changes (if applicable).
public class MyInstanceIDListenerService extends InstanceIDListenerService {
    public void onTokenRefresh() {
        Intent intent = new Intent(this, MyGcmRegistrationService.class);
        startService(intent);
    }
}
