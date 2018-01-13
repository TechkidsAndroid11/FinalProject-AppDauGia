package com.example.haihm.shelf.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;

/**
 * Created by Trần_Tân on 09/01/2018.
 */

public class ImageUtils {
    private static File temFile;
    private static final String TAG = "ImageUtils";
    public static Bitmap base64ToImage(String base64Image) {
        byte[] imageBytes = Base64.decode(base64Image, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        return Bitmap.createScaledBitmap(bitmap, 150, 150, false);
    }
    public static String endcodeImageToBase64(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,byteArrayOutputStream);
        byte []imageBytes = byteArrayOutputStream.toByteArray();
        String imageBas64 = Base64.encodeToString(imageBytes,Base64.DEFAULT);
//        Log.d(TAG, "endcodeImageToBase64: "+imageBas64);
        return imageBas64;
    }
    public static Uri getUriFromImage(Context context){
        //creat temp file
        temFile= null;
        try {
            temFile = File.createTempFile(
                    Calendar.getInstance().getTime().toString(), ".jpg",
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            );
            Log.d(TAG, "getUriFromImage: "+temFile.getPath());
            temFile.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //get uri
        Uri uri = null;
        if(temFile!=null){
            uri = FileProvider.getUriForFile(
                    context,
                    context.getPackageName()+".provider",
                    temFile
            );
        }
        Log.d(TAG, "getUriFromImage: "+uri);
        return uri;
    }
    public  static Bitmap getBitmap(Context context){
        Bitmap bitmap = BitmapFactory.decodeFile(temFile.getPath());
        Log.d(TAG, "getBitmap: "+temFile.getPath());
        int screenWitdh = context.getResources().getDisplayMetrics().widthPixels;
        double ratio = (double)bitmap.getWidth()/bitmap.getHeight();
        Bitmap scaleBimap = bitmap.createScaledBitmap(bitmap,screenWitdh,(int)(screenWitdh/ratio),false);
        return scaleBimap;
    }
}
