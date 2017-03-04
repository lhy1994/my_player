package com.liuhaoyuan.myplayer.manager;

import android.content.Context;
import android.os.Environment;

import java.io.File;

/**
 * Created by hyliu on 2017/1/31.
 */

public class MusicDownloadManager {
    private static MusicDownloadManager instance;
    private final String SAVEDIR_NAME="MyPlayerDownload";
    private MusicDownloadManager(){

    }

    public static synchronized MusicDownloadManager getInstance(){
        if (instance==null){
            instance=new MusicDownloadManager();
        }
        return instance;
    }

    public String getSavePath(Context context){
        boolean externalStorageAvailable  = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
        String savePath;
        if (externalStorageAvailable){
            savePath=Environment.getExternalStorageDirectory().getAbsolutePath()+ File.separator+SAVEDIR_NAME;
        }else {
            savePath=context.getFilesDir().getAbsolutePath()+File.separator+SAVEDIR_NAME;
        }
        return savePath;
    }
}
