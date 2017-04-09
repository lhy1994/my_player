package com.liuhaoyuan.myplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.annotation.ColorInt;
import android.support.v7.graphics.Palette;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by liuhaoyuan on 17/4/8.
 */

public class ImageUtils {
    public static int mCompressQuality = 30;
    public static final String IMAGE_CACHE_DIR = "/ImageCache";

    //    public static Bitmap cacheImg(Context context,Drawable drawable){
//        BitmapDrawable bitmapDrawable= (BitmapDrawable) drawable;
//        Bitmap bitmap = bitmapDrawable.getBitmap();
//        FileOutputStream op= null;
//        try {
//            op = new FileOutputStream(getCacheFilePath());
//            bitmap.compress(Bitmap.CompressFormat.JPEG,mCompressQuality,op);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static File getCacheFilePath(Context context,boolean useSd){
//        File filesDir=null;
//        if (useSd){
//            filesDir=Environment.getExternalStorageDirectory();
//        }else {
//            filesDir=context.getFilesDir();
//        }
//        File file=new File(filesDir, IMAGE_CACHE_DIR);
//        return  file;
//    }
    public static void getDominantColor(Bitmap bitmap, final PaletteCallBack callBack) {
        new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int color = palette.getDominantColor(Color.parseColor("#66000000"));
                Palette.Swatch swatch = palette.getDominantSwatch();
                int textColor;
                if (swatch != null) {
                    textColor = getOpaqueColor(swatch.getTitleTextColor());
                } else {
                    textColor = Color.parseColor("#ffffff");
                }
                callBack.onColorGenerated(color,textColor);
            }
        });
    }

    public static void getVibrantColor(Bitmap bitmap, final PaletteCallBack callBack) {
        new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int color = palette.getVibrantColor(Color.parseColor("#66000000"));
                Palette.Swatch swatch = palette.getVibrantSwatch();
                int textColor;
                if (swatch != null) {
                    textColor = getOpaqueColor(swatch.getTitleTextColor());
                } else {
                    textColor = Color.parseColor("#ffffff");
                }
                callBack.onColorGenerated(color,textColor);
            }
        });
    }

    public static void getMutedColor(Bitmap bitmap, final PaletteCallBack callBack) {
        new Palette.Builder(bitmap).generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                int color = palette.getMutedColor(Color.parseColor("#66000000"));
                Palette.Swatch swatch = palette.getMutedSwatch();
                int textColor;
                if (swatch != null) {
                    textColor = getOpaqueColor(swatch.getTitleTextColor());
                } else {
                    textColor = Color.parseColor("#ffffff");
                }
                callBack.onColorGenerated(color,textColor);
            }
        });
    }

    public interface PaletteCallBack {
        void onColorGenerated(int color, int textColor);
    }

    public static int getOpaqueColor(@ColorInt int paramInt) {
        return 0xFF000000 | paramInt;
    }
}
