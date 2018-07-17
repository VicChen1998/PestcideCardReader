package com.vicchen.pestcidecardreader.Utils;

import android.os.Environment;

import java.io.File;

public class GlobalData {

    private static String baseDirName = "PestcideCardReader";
    private static File baseDir;

    private static String photoDirName = "Photo";
    private static File photoDir;

    private static String sampleBoardDirName = "SampleBoard";
    private static File sampleBoardDir;

    public GlobalData() {

    }

    // 应用根目录
    public static String getBaseDirName() {
        return baseDirName;
    }

    public static File getBaseDir() {
        if (baseDir == null) {
            baseDir = new File(Environment.getExternalStorageDirectory(), baseDirName);
            if (!baseDir.exists())
                baseDir.mkdirs();
        }
        return baseDir;
    }

    // 原始照片目录
    public static String getPhotoDirName() {
        return photoDirName;
    }

    public static File getPhotoDir() {
        if (photoDir == null) {
            photoDir = new File(getBaseDir(), photoDirName);
            if (!photoDir.exists())
                photoDir.mkdirs();
        }
        return photoDir;
    }

    // 样板图片目录
    public static String getSampleBoardDirName() {
        return sampleBoardDirName;
    }

    public static File getSampleBoardDir() {
        if (sampleBoardDir == null) {
            sampleBoardDir = new File(getBaseDir(), sampleBoardDirName);
            if (!sampleBoardDir.exists())
                sampleBoardDir.mkdirs();
        }
        return sampleBoardDir;
    }
}
