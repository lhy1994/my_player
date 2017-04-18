package com.liuhaoyuan.myplayer.db;

import com.liuhaoyuan.myplayer.aidl.Song;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by liuhaoyuan on 17/4/15.
 */

public class Converter {
    public static Song bytesToSong(byte[] bytes){
        ByteArrayInputStream byteArrayInputStream=new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream=null;
        Song song=null;
        try {
            objectInputStream=new ObjectInputStream(byteArrayInputStream);
            song= (Song) objectInputStream.readObject();
            return song;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            if (objectInputStream!=null){
                try {
                    objectInputStream.close();
                    byteArrayInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return song;
    }

    public static byte[] songToBytes(Song song){
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(arrayOutputStream);
            objectOutputStream.writeObject(song);
            objectOutputStream.flush();
            byte[] bytes = arrayOutputStream.toByteArray();
            return bytes;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                    arrayOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
