package com.vicchen.pestcidecardreader.Analysis;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.vicchen.pestcidecardreader.Global.Database;
import com.vicchen.pestcidecardreader.R;
import com.vicchen.pestcidecardreader.Global.GlobalPath;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

import static android.app.Activity.RESULT_CANCELED;


public class Analysis extends Fragment {

    private static final String[] PERMISSIONS = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};

    private static final int TAKE_PHOTO_REQUEST = 1;

    private ImageView imgvSampleBoard;
    private ImageView imgvSamples[];
    private TextView txtvReadings[];

    private String filename;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.analysis, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        Log.d("FRAGMENT START", "FRAGMENT ACTIVITY START");

        // 绑定控件
        imgvSampleBoard = getView().findViewById(R.id.sampleBoard);

        imgvSamples = new ImageView[8];
        for (int i = 0; i < 8; i++) {
            int id = getResources().getIdentifier("analysis_sample" + (i + 1), "id", getActivity().getPackageName());
            imgvSamples[i] = getView().findViewById(id);
        }

        txtvReadings = new TextView[8];
        for (int i = 0; i < 8; i++) {
            int id = getResources().getIdentifier("analysis_reading" + (i + 1), "id", getActivity().getPackageName());
            txtvReadings[i] = getView().findViewById(id);
        }


        // 按钮事件
        Button btTakePhoto = getView().findViewById(R.id.btTakePhoto);
        btTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAnalysis();
            }
        });


        //检查文件读取权限
        int permission = ActivityCompat.checkSelfPermission(getActivity(), "android.permission.WRITE_EXTERNAL_STORAGE");
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), PERMISSIONS, 1);
        }


        // 初始化opencv
        OpenCVLoader.initDebug();
    }


    private void startAnalysis() {
        // 调用相机
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        filename = System.currentTimeMillis() + ".jpg";
        File photo = new File(GlobalPath.getPhotoDir(), filename);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photo));

        startActivityForResult(intent, TAKE_PHOTO_REQUEST);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case TAKE_PHOTO_REQUEST:
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getActivity(), "取消", Toast.LENGTH_LONG).show();
                    return;
                }

                // 获取照片
                File originPhotoFile = new File(GlobalPath.getPhotoDir(), filename);
                Bitmap photo = BitmapFactory.decodeFile(originPhotoFile.getPath());


                // 识别样板
                PhotoDetecter photoDetecter = new PhotoDetecter();
                Mat sampleBoard = photoDetecter.detect(photo);
                // 如果未检测到则显示提示
                if (sampleBoard.rows() == 0 || sampleBoard.cols() == 0) {
                    Toast.makeText(getActivity(), "检测失败", Toast.LENGTH_LONG).show();
                    break;
                }

                // 白平衡
                ColorAdjuster colorAdjuster = new ColorAdjuster();
                sampleBoard = colorAdjuster.adjust(sampleBoard);

                // 显示样板图像
                Bitmap sampleBoardBitmap = Bitmap.createBitmap(
                        sampleBoard.width(),
                        sampleBoard.height(),
                        Bitmap.Config.ARGB_8888);
                Utils.matToBitmap(sampleBoard, sampleBoardBitmap);
                imgvSampleBoard.setImageBitmap(sampleBoardBitmap);

                // 保存样本图片
                File sampleBoardFile = new File(GlobalPath.getSampleBoardDir(), filename);
                try {
                    FileOutputStream fos = new FileOutputStream(sampleBoardFile);
                    sampleBoardBitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                    fos.flush();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 删除原图
                originPhotoFile.delete();


                // 分析样板 显示数据
                analysisSample(sampleBoard);
                break;
        }

    }

    /**
     * 分析样板
     *
     * @param sampleBoard Mat 接受处理好的图像进行分析并显示结果
     */
    protected void analysisSample(Mat sampleBoard) {

        // 8个样本roi锚点
        double anchor_x[] = {0.19, 0.19, 0.19, 0.19, 0.75, 0.75, 0.75, 0.75};
        double anchor_y[] = {0.135, 0.275, 0.415, 0.565, 0.135, 0.275, 0.415, 0.565};
        double anchor_xw[] = {0.28, 0.28, 0.28, 0.28, 0.84, 0.84, 0.84, 0.84};
        double anchor_yh[] = {0.165, 0.305, 0.445, 0.595, 0.165, 0.305, 0.445, 0.595};

        // 剪裁8个样本roi区域
        Mat samples[] = new Mat[8];

        int width = sampleBoard.width();
        int height = sampleBoard.height();

        for (int i = 0; i < 8; i++) {
            samples[i] = sampleBoard.submat(
                    (int) (anchor_y[i] * height),
                    (int) (anchor_yh[i] * height),
                    (int) (anchor_x[i] * width),
                    (int) (anchor_xw[i] * width));
        }


        // 显示样本roi
        for (int i = 0; i < 8; i++) {
            Bitmap s = Bitmap.createBitmap(samples[i].width(), samples[i].height(), Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(samples[i], s);
            imgvSamples[i].setImageBitmap(s);
        }


        String readings[] = new String[8];

        // 获取读数
        for (int i = 0; i < 8; i++) {
            // 检测颜色
            double blue_total = 0;
            for (int r = 0; r < samples[i].rows(); r++) {
                for (int c = 0; c < samples[i].cols(); c++) {
                    blue_total += samples[i].get(r, c)[2];
                }
            }
            // 计算数值
            double blue_avg = blue_total / (samples[i].width() * samples[i].height());

            Log.d("sample " + i, blue_total + "");
            Log.d("sample " + i, blue_avg + "");

            double value = blue_avg * 100 / 255;

            Log.d("sample " + i, value + "");

            // 显示读数
            DecimalFormat df = new DecimalFormat("0.0");
            txtvReadings[i].setText(df.format(value) + " %");

            readings[i] = df.format(value) + " %";

        }


        //保存数据
        Database database = new Database();
        database.insertReadings(filename, readings);
    }
}
