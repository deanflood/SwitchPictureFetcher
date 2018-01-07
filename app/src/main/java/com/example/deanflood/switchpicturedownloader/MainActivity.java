package com.example.deanflood.switchpicturedownloader;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.deanflood.switchpicturedownloader.Interface.VolleyCallback;
import com.example.deanflood.switchpicturedownloader.service.PhotoService;
import com.example.deanflood.switchpicturedownloader.service.TwitterService;
import com.example.deanflood.switchpicturedownloader.service.VideoService;

import org.json.JSONArray;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void fetchTweets(View v) {
        final EditText username = findViewById(R.id.twitterAccount);

        if (username.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter a Twitter Account", Toast.LENGTH_SHORT).show();
        } else {
            final ProgressBar progressBar = findViewById(R.id.progressBar);
            int writePermissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (writePermissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            } else {


                TwitterService.getTwitterToken(this, new VolleyCallback() {

                    @Override
                    public void onSuccess(Context context, String result) {
                        try {

                            progressBar.setVisibility(View.VISIBLE);
                            JSONObject json = new JSONObject(result);
                            String token = json.getString("access_token");

                            TwitterService.getTwitterFeed(context, token, username.getText().toString(), new VolleyCallback() {
                                @Override
                                public void onSuccess(Context context, String result) {
                                    int photoCount = 0;
                                    int videoCount = 0;
                                    try {
                                        JSONArray tweetArray = new JSONArray(result);

                                        for (int i = 0; i < tweetArray.length(); i++) {
                                            JSONObject tweet = tweetArray.getJSONObject(i);
                                            if (tweet.has("extended_entities")) {
                                                JSONObject media = tweet.getJSONObject("extended_entities").getJSONArray("media").getJSONObject(0);
                                                if (media.getString("type").equals("photo")) {
                                                    PhotoService.imageDownload(context, media.getString("id_str") + ".png", media.getString("media_url_https"));
                                                    photoCount++;
                                                } else if (media.getString("type").equals("video")) {
                                                    JSONArray videoInfo = media.getJSONObject("video_info").getJSONArray("variants");
                                                    int max = -1;
                                                    for (int j = 0; j < videoInfo.length(); j++) {
                                                        JSONObject temp = videoInfo.getJSONObject(j);
                                                        if (temp.has("bitrate")) {
                                                            if (temp.getInt("bitrate") > max) {
                                                                max = temp.getInt("bitrate");
                                                            }
                                                        }
                                                    }
                                                    for (int j = 0; j < videoInfo.length(); j++) {
                                                        JSONObject temp = videoInfo.getJSONObject(j);
                                                        if (temp.has("bitrate")) {
                                                            if (temp.getInt("bitrate") == max) {
                                                                new VideoService(context).execute(temp.getString("url"));
                                                            }
                                                        }
                                                    }
                                                    videoCount++;
                                                }
                                            }
                                        }
                                        Toast.makeText(getApplicationContext(), "Found " + photoCount + " photo(s) and " + videoCount + " video(s)", Toast.LENGTH_SHORT).show();

                                        progressBar.setVisibility(View.INVISIBLE);

                                    } catch (Exception e) {
                                        Log.wtf("Exception", e.getMessage());
                                        Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }
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
                    Toast.makeText(getApplicationContext(), "Write Permissions required to save media", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


}
