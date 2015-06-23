package com.appifiedtech.app.gcm;

import android.app.IntentService;
import android.content.Intent;
import com.appifiedtech.app.utils.Config;
import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

/**
 * Created by Priyabrat on 08-06-2015.
 */
public class MyGcmRegistrationService extends IntentService {

    public MyGcmRegistrationService() {
        super(Config.TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            synchronized (Config.TAG)
            {
                InstanceID instanceID = InstanceID.getInstance(this);
                String token = instanceID.getToken(Config.GCM_SENDER_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                //sendTokenToServer(token);
                subscribeTopics(token);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void subscribeTopics(String token) throws IOException {
        for (String topic : Config.TOPICS) {
            GcmPubSub pubSub = GcmPubSub.getInstance(this);
            pubSub.subscribe(token, "/topics/" + topic, null);
        }
    }
}