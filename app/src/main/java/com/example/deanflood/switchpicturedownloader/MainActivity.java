package com.example.deanflood.switchpicturedownloader;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.deanflood.switchpicturedownloader.service.ImageService;
import com.example.deanflood.switchpicturedownloader.service.TwitterService;

import org.json.JSONArray;
import org.json.JSONObject;



public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void fetchTweets(View v) {


        TwitterService.getTwitterToken(this, new VolleyCallback() {
            @Override
            public void onSuccess(Context context, String result) {
                try {
                    final ProgressBar progressBar =findViewById(R.id.progressBar);


                    progressBar.setVisibility(View.VISIBLE);
                    JSONObject json = new JSONObject(result);
                    String token = json.getString("access_token");
                    EditText username = findViewById(R.id.twitterAccount);

                    TwitterService.getTwitterFeed(context, token, username.getText().toString(), new VolleyCallback() {
                        @Override
                        public void onSuccess(Context context, String result) {
                            try {
                                JSONArray tweetArray = new JSONArray(result);
                                int writePermissionCheck = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);

                                if (writePermissionCheck == PackageManager.PERMISSION_GRANTED) {
                                    for (int i = 0; i < tweetArray.length(); i++) {
                                        JSONObject tweet = tweetArray.getJSONObject(i);
                                        if (tweet.has("extended_entities")) {
                                            JSONObject media = tweet.getJSONObject("extended_entities").getJSONArray("media").getJSONObject(0);
                                            if (media.getString("type").equals("photo")) {

                                                ImageService.imageDownload(context, media.getString("id_str") + ".png", media.getString("media_url_https"));
                                                Toast.makeText(getApplicationContext(), media.getString("id_str") + ".png" + " downloaded!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }
                                } else{
                                    ActivityCompat.requestPermissions((Activity) context ,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                                }

                                progressBar.setVisibility(View.INVISIBLE);

                            } catch (Exception e){
                                e.printStackTrace();
                            }
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Button fetchTweets = findViewById(R.id.fetch_tweets);
                    fetchTweets.callOnClick();
                } else {
                    Toast.makeText(getApplicationContext(), "Write Permissions required to save images", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


}
