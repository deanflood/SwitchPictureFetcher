package com.example.deanflood.switchpicturedownloader.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.example.deanflood.switchpicturedownloader.R;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by dean.flood on 1/7/18.
 */

public class VideoService extends AsyncTask<String, Void, String> {
    private Context context;

    public  VideoService(Context context) {
        this.context = context;
    }

    @Override
    protected String doInBackground(String[] urls) {
        try {
            URL url = new URL(urls[0]);
            URLConnection connection = null;
            InputStream inputStream = null;
            final int TIMEOUT_CONNECTION = 5000;//5sec
            final int TIMEOUT_SOCKET = 30000;//30sec


            long startTime = System.currentTimeMillis();
            Log.wtf("VIDEO", "image download beginning: " + urls[0]);
            File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "Download/" + urls[0].substring(36, 46) +".mp4");
            file.createNewFile();


            //Open a connection to that URL.
            connection = url.openConnection();

            //this timeout affects how long it takes for the app to realize there's a connection problem
            connection.setReadTimeout(TIMEOUT_CONNECTION);
            connection.setConnectTimeout(TIMEOUT_SOCKET);


            //Define InputStreams to read from the URLConnection.
            // uses 3KB download buffer
            inputStream = connection.getInputStream();

            BufferedInputStream inStream = new BufferedInputStream(inputStream, 1024 * 5);
            FileOutputStream outStream = new FileOutputStream(file);
            byte[] buff = new byte[5 * 1024];

            //Read bytes (and store them) until there inputStream nothing more to read(-1)
            int len;
            while ((len = inStream.read(buff)) != -1) {
                outStream.write(buff, 0, len);
            }

            //clean up
            outStream.flush();
            outStream.close();
            inStream.close();

            Intent intent =
                    new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            intent.setData(Uri.fromFile(file));
            context.sendBroadcast(intent);

            Log.wtf("VIDEO", "download completed in "
                    + ((System.currentTimeMillis() - startTime) / 1000)
                    + " sec");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }




}

