package com.example.deanflood.switchpicturedownloader;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import com.example.deanflood.switchpicturedownloader.service.TwitterService;
import com.twitter.sdk.android.core.Twitter;

import org.json.JSONObject;



public class MainActivity extends AppCompatActivity {
    public TwitterService twitterService;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Twitter.initialize(this);


        Button fetchTweets = findViewById(R.id.fetch_tweets);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void fetchTweets(View v) {
        // Instantiate the RequestQueue.

        twitterService.getTwitterToken(this, new VolleyCallback() {
            @Override
            public void onSuccess(Context context, String result) {
                try {
                    JSONObject json = new JSONObject(result);
                    String token = json.getString("access_token");
                   // Toast.makeText(getApplicationContext(), "Outside Response is: " + token, Toast.LENGTH_LONG).show();

                    twitterService.getTwitterFeed(context, token, new VolleyCallback() {
                        @Override
                        public void onSuccess(Context context, String result) {
                            Toast.makeText(getApplicationContext(), "Outside Response is: " + result, Toast.LENGTH_LONG).show();

                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public interface VolleyCallback{
        void onSuccess(Context context, String result);
    }

}
