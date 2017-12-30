package com.example.deanflood.switchpicturedownloader.service;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by dean.flood on 12/30/17.
 */

public class ImageService {

    public static void imageDownload(Context ctx, String imageName, String imageUrl){
        Picasso.with(ctx)
                .load(imageUrl)
                .into(getTarget(ctx, imageName));
    }

    //target to save
    private static Target getTarget(final Context context, final String imageName){
        Target target = new Target(){

            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        File file = new File(Environment.getExternalStorageDirectory().getPath() + "/" + "Download/" + imageName);
                        try {
                            Log.wtf("PATH", Environment.getExternalStorageDirectory().getPath() + "/" + "Download/" + imageName);
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, ostream);
                            ostream.flush();
                            ostream.close();

                            Intent intent =
                                    new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                            intent.setData(Uri.fromFile(file));
                            context.sendBroadcast(intent);


                        } catch (IOException e) {
                            Log.wtf("IOException", e.getLocalizedMessage());
                        }
                    }
                }).start();

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        };
        return target;
    }

}
