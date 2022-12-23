package com.deepwaterooo.sdk.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.deepwaterooo.sdk.appconfig.Numerics;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Class used for I/O utilities based on strings and file streams
 */
public class IOUtil {

    public static byte[] readFile(String file) throws IOException {
        return readFile(new File(file));
    }

    public static byte[] readFile(File file) throws IOException {
        // Open file
        RandomAccessFile f = new RandomAccessFile(file, "r");
        try {
            // Get and check length
            long longlength = f.length();
            int length = (int) longlength;
            if (length != longlength)
                throw new IOException("File size >= 2 GB");
            // Read file and return data
            byte[] data = new byte[length];
            f.readFully(data);
            return data;
        } finally {
            f.close();
        }
    }

    /**
     * It will return the base6 string of the image
     * After encoding we have observed \n in the base64 string, which is not acceptable. So, we are removing that \n from the base64 string.
     */
    public static String getBase64(String picturePath) throws IOException {
        byte[] ba = readFile(picturePath);

        String string = Base64.encodeToString(ba, Base64.DEFAULT).replaceAll("\n", "");
        return "data:image/png;base64," + string;
    }

    /**
     * It will return the base6 string of the image
     * After encoding we have observed \n in the base64 string, which is not acceptable. So, we are removing that \n from the base64 string.
     */
    public static String getBase64Scaled(String picturePath, int reqWidth, int reqHeight) throws IOException {

        Bitmap bmp = BitmapFactory.decodeFile(picturePath);

        Bitmap scaledBitmap = ScalingUtilities.createScaledBitmap(bmp, reqWidth, reqHeight, ScalingUtilities.ScalingLogic.FIT);
        bmp.recycle();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        scaledBitmap.compress(Bitmap.CompressFormat.PNG, Numerics.HUNDRED, stream);

        byte[] byteArray = stream.toByteArray();
        String string = Base64.encodeToString(byteArray, Base64.DEFAULT).replaceAll("\n", "");
        return "data:image/png;base64," + string;
    }

    /**
     * It will return the base6 string of the file
     * After encoding we have observed \n in the base64 string, which is not acceptable. So, we are removing that \n from the base64 string.
     */
    public static String getBase64FromFile(String picturePath) throws IOException {
        byte[] byteArray = readFile(picturePath);
        return Base64.encodeToString(byteArray, Base64.DEFAULT).replaceAll("\n", "");
    }
}