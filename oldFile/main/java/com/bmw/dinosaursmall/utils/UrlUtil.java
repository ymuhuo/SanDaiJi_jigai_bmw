package com.bmw.dinosaursmall.utils;

import android.os.Environment;

import java.text.SimpleDateFormat;

/**
 * Created by admin on 2016/9/30.
 */
public class UrlUtil {

    private final static String local_video_path = "/dinosaur_data/video/";
    private final static String local_picture_path = "/dinosaur_data/capture/";


    public static String getLocal_video_path() {
        return getSDPath() + local_video_path;
    }

    public static String getLocal_picture_path() {
        return getSDPath() + local_picture_path;
    }

    public static String getFileName() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd-hhmmss");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    public static String getSDPath() {
        boolean hasSDCard = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        if (hasSDCard) {
            return Environment.getExternalStorageDirectory().toString();
        } else
            return Environment.getDownloadCacheDirectory().toString();
    }
}
