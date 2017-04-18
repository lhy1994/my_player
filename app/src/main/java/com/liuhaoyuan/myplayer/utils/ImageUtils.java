package com.liuhaoyuan.myplayer.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorInt;
import android.support.v7.graphics.Palette;
import android.support.v8.renderscript.RenderScript;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

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

    public static Drawable createBlurredImageFromBitmap(Bitmap bitmap, Context context, int inSampleSize) {

        RenderScript rs = RenderScript.create(context);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
        Bitmap blurTemplate = BitmapFactory.decodeStream(bis, null, options);

        final android.support.v8.renderscript.Allocation input = android.support.v8.renderscript.Allocation.createFromBitmap(rs, blurTemplate);
        final android.support.v8.renderscript.Allocation output = android.support.v8.renderscript.Allocation.createTyped(rs, input.getType());
        final android.support.v8.renderscript.ScriptIntrinsicBlur script = android.support.v8.renderscript.ScriptIntrinsicBlur.create(rs, android.support.v8.renderscript.Element.U8_4(rs));
        script.setRadius(8f);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(blurTemplate);

        return new BitmapDrawable(context.getResources(), blurTemplate);
    }
}
