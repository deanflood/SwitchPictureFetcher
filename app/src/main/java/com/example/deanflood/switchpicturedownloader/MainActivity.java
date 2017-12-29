package com.example.deanflood.switchpicturedownloader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.example.deanflood.switchpicturedownloader.service.TwitterService;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends AppCompatActivity {
    public TwitterService twitterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void fetchTweets(View v) {


        twitterService.getTwitterToken(this, new VolleyCallback() {
            @Override
            public void onSuccess(Context context, String result) {
                try {
                    final ProgressBar progressBar =findViewById(R.id.progressBar);


                    progressBar.setVisibility(View.VISIBLE);
                    JSONObject json = new JSONObject(result);
                    String token = json.getString("access_token");
                    EditText username = findViewById(R.id.twitterAccount);

                    twitterService.getTwitterFeed(context, token, username.getText().toString(), new VolleyCallback() {
                        @Override
                        public void onSuccess(Context context, String result) {
                            ImageView iv = findViewById(R.id.imageView);
                            try {
                                JSONArray tweetArray = new JSONArray(result);
                                for (int i=0; i < tweetArray.length(); i++){
                                    JSONObject tweet = tweetArray.getJSONObject(i);
                                    if(tweet.has("extended_entities")) {
                                        JSONObject media = tweet.getJSONObject("extended_entities").getJSONArray("media").getJSONObject(0);
                                        Picasso.with(context).load(media.getString("media_url_https")).into(iv);



                                        Toast.makeText(getApplicationContext(), media.getString("media_url_https"), Toast.LENGTH_SHORT).show();
                                    }

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

}
