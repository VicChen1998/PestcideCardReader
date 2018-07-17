package com.vicchen.pestcidecardreader.Global;

import android.os.Environment;

import java.io.File;

public class GlobalPath {

    private static String baseDirName = "PestcideCardReader";
    private static File baseDir;

    private static String photoDirName = "Photo";
    private static File photoDir;

    private static String sampleBoardDirName = "SampleBoard";
    private static File sampleBoardDir;

    private static String databaseName = "PestcideCardReader.db";
    private static String databasePath;

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


    // 数据库目录
    public static String getDatabaseName() {
        return databaseName;
    }

    public static String getDatabasePath() {
        if (databasePath == null)
            databasePath = getBaseDir().getPath() + "/" + databaseName;
        return databasePath;
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
