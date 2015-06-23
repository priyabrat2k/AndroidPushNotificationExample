package com.appifiedtech.app.push.main;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.appifiedtech.app.gcm.WakeLocker;
import com.appifiedtech.app.utils.AppController;
import com.appifiedtech.app.utils.Config;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    public ProgressDialog progressDialog;
    private GoogleCloudMessaging gcm;
    private TextView mDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mDisplay = (TextView) findViewById(R.id.msggg);
        progressDialog =new ProgressDialog(MainActivity.this);
        try {
            new RegisterWithGSMServer().execute();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "" + e, Toast.LENGTH_LONG).show();
        }
    }

    private final BroadcastReceiver mHandleMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String newMessage = intent.getExtras().getString("message");
            WakeLocker.acquire(getApplicationContext());
            mDisplay.append(newMessage + "\n");
            Toast.makeText(getApplicationContext(), "New Message: " + newMessage, Toast.LENGTH_LONG).show();
            WakeLocker.release();
        }
    };

    class RegisterWithGSMServer extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Void... params) {
            String msg = "";
            try {
                if (gcm == null) {
                    gcm = GoogleCloudMessaging.getInstance(MainActivity.this);
                }
                String regid = getToken();
                msg = regid;
            } catch (Exception ex) {
                msg = null;
            }
            return msg;
        }

        @Override
        protected void onPostExecute(String regId) {
            if (regId != null)
            {
                mDisplay.append("Device registered, registration ID=" + regId + "\n");
                sendRegistrationIdToBackend(regId);
            }
        }
    }

    private void sendRegistrationIdToBackend(final String regId) {

        final String tag_json_obj = "json_obj_req";
        final String url = Config.GCM_BACKEND_URL + "/register.php";
        final Map<String, String> params = new HashMap<String
                , String>();
        params.put("name", "Jagannath");
        params.put("regId", regId);
        String userId;
        try {
            userId = AccountManager.get(MainActivity.this).getAccounts()[0].name;
        } catch (Exception e) {
            userId = "priyabrat.padhy100@gmail.com";
        }
        params.put("email", userId);

        StringRequest sr = new StringRequest(
                Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(Config.LOG_TAG, response.toString());
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(), response.toString(), Toast.LENGTH_LONG).show();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.d(Config.LOG_TAG, "Error: " + error.getMessage());
                        if(progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(sr, tag_json_obj);
    }

    public String getToken(){
        String rData = null;
        InstanceID instanceID = InstanceID.getInstance(this);
        try {
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            rData = token;
        } catch (IOException e) {
            e.printStackTrace();
            rData = null;
        }
        return rData;
    }
}
