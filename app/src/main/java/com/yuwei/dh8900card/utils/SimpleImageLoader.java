package com.yuwei.dh8900card.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created by ya!sb on 2018/3/9.
 */

public class SimpleImageLoader {
    public static Bitmap getBitmap(String path) throws FileNotFoundException {
        try {
            FileInputStream fis = new FileInputStream(path);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}

