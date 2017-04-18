package com.liuhaoyuan.myplayer.utils;

import android.text.TextUtils;

import com.liuhaoyuan.myplayer.domain.music.Lyric;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import io.vov.vitamio.utils.Log;

/**
 * Created by liuhaoyuan on 2016/7/25.
 */
public class LyricUtils {
    private ArrayList<Lyric> lyrics;

    public ArrayList<Lyric> getLyrics() {
        return lyrics;
    }

    public void readLyricFile(File file) throws IOException {
        if (file == null || !file.exists()) {

        } else {
            lyrics = new ArrayList<>();

            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            BufferedReader br = new BufferedReader(new InputStreamReader(bis, "utf-8"));
            String line;
            while ((line = br.readLine()) != null) {
                line = analyzeLyric(line);
            }

            Collections.sort(lyrics, new Sort());

            for (int i = 0; i < lyrics.size(); i++) {
                Lyric oneLyric = lyrics.get(i);
                if (i + 1 < lyrics.size()) {
                    Lyric twoLyric = lyrics.get(i + 1);
                    oneLyric.duration = twoLyric.timePoint - oneLyric.timePoint;
                }
            }
        }
    }

    public void readLyricString(String string) {
        if (string == null || string.length() <= 0) {

        } else {
            lyrics = new ArrayList<>();
            string = string.replace("&#10;", "#").replace("&#58;", ":").replace("&#46;", ".").replace("&#32;", " ").replace("&#45;", "-").replace("&#40;","(").replace("&#41;",")").replace("&#13;","").replace("&#39;","'");
            String[] strs = TextUtils.split(string, "#");
            String line;
            for (int i = 0; i < strs.length; i++) {
                line = analyzeLyric(strs[i]);
            }

            Collections.sort(lyrics, new Sort());

            for (int i = 0; i < lyrics.size(); i++) {
                Lyric oneLyric = lyrics.get(i);
                if (i + 1 < lyrics.size()) {
                    Lyric twoLyric = lyrics.get(i + 1);
                    oneLyric.duration = twoLyric.timePoint - oneLyric.timePoint;
                }
            }
        }
    }

    private class Sort implements Comparator<Lyric> {

        @Override
        public int compare(Lyric lyric1, Lyric lyric2) {
            if (lyric1.timePoint < lyric2.timePoint) {
                return -1;
            } else if (lyric1.timePoint > lyric2.timePoint) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private String analyzeLyric(String line) {
        int pos1 = line.indexOf("[");
        int pos2 = line.indexOf("]");
        if (pos1 == 0 && pos2 != 1) {
            long[] timePoints = new long[getTagCount(line)];
            String contentStr = line.substring(pos1 + 1, pos2);
            timePoints[0] = timeStrToLong(contentStr);
            if (timePoints[0] == -1) {
                return "";
            }

            String content = line;
            int i = 1;
            while (pos1 == 0 && pos2 != -1) {
                content = content.substring(pos2 + 1);
                pos1 = content.indexOf("[");
                pos2 = content.indexOf("]");

                if (pos2 != -1) {
                    contentStr = content.substring(pos1 + 1, pos2);
                    timePoints[i] = timeStrToLong(contentStr);
                    if (timePoints[i] == -1) {
                        return "";
                    }
                    i++;
                }

            }

            Lyric lyric = new Lyric();
            for (int j = 0; j < timePoints.length; j++) {
                if (timePoints[j] != 0 && !content.isEmpty()) {
                    lyric.text = content;
                    lyric.timePoint = timePoints[j];
                    lyrics.add(lyric);
                    lyric = new Lyric();
                }
            }
            return content;
        }
        return "";
    }

    private long timeStrToLong(String content) {
        long result = 0;

        try {
            String[] s1 = content.split(":");
            if (s1.length >= 2) {
                String[] s2 = s1[1].split("\\.");
                long min = Long.valueOf(s1[0]);
                long second = Long.valueOf(s2[0]);
                long mil = Long.valueOf(s2[1]);

                result = min * 60 * 1000 + second * 1000 + mil * 10;
            } else {
                return -1;
            }
        } catch (NumberFormatException e) {
//            e.printStackTrace();
            return -1;
        }

        return result;
    }

    private int getTagCount(String line) {
        String[] left = line.split("\\[");
        String[] right = line.split("\\]");
        if (left.length == 0 || right.length == 1) {
            return 1;
        } else if (left.length > right.length) {
            return left.length;
        } else {
            return right.length;
        }
    }
}
