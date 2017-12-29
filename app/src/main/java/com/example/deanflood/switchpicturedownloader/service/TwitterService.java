package com.example.deanflood.switchpicturedownloader.service;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.deanflood.switchpicturedownloader.MainActivity;
import com.example.deanflood.switchpicturedownloader.R;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dean.flood on 12/23/17.
 */

public class TwitterService {



    public static String encodedToken(String consumerKey, String consumerSecret){
        byte[] toEncode = (consumerKey + ":" + consumerSecret).getBytes();
        return new String(Base64.encode(toEncode, Base64.NO_WRAP));
    }

    public static void getTwitterToken(final Context context, final MainActivity.VolleyCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.twitter.com/oauth2/token";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(context, response);
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                try {
                    return "grant_type=client_credentials".getBytes("utf-8");

                } catch (UnsupportedEncodingException uee) {
                    Log.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", "utf-8");
                    return null;
                }
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Basic " + encodedToken(context.getString(R.string.com_twitter_sdk_android_CONSUMER_KEY), context.getString(R.string.com_twitter_sdk_android_CONSUMER_SECRET)));
                params.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                return params;
            }

        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    public static void getTwitterFeed(final Context context, final String token, final String username, final MainActivity.VolleyCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://api.twitter.com/1.1/statuses/user_timeline.json?screen_name="+ username + "&count=10&exclude_replies=1&include_rts=0";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        callback.onSuccess(context, response);
                    }
                }
                , new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Log.wtf("yoy", error.getMessage());
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                Log.wtf("TOKEN", "Bearer " + token);

                params.put("Authorization", "Bearer " + token);
                params.put("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");

                return params;
            }

        };

// Add the request to the RequestQueue.
        queue.add(stringRequest);

    }


}
