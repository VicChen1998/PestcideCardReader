package com.vicchen.pestcidecardreader.History;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.vicchen.pestcidecardreader.R;
import com.vicchen.pestcidecardreader.Utils.GlobalData;
import com.vicchen.pestcidecardreader.View.SampleBoardInfo;

import java.io.File;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class History extends Fragment {

    private boolean isViewCreated = false;
    private boolean isUIVisible = false;

    private boolean isInit = false;

    private LinearLayout linearLayout;
    private SwipeRefreshLayout refreshLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.history, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        onStartLazyLoad();
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isUIVisible = true;
            onStartLazyLoad();
        }
    }


    private void onStartLazyLoad() {
        if (!isViewCreated || !isUIVisible)
            return;

        if (isInit)
            return;

        linearLayout = getView().findViewById(R.id.historyLinearLayout);
        refreshLayout = getView().findViewById(R.id.historyRefresh);


        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                linearLayout.removeAllViews();
                loadSamples();
                refreshLayout.setRefreshing(false);
            }
        });

        loadSamples();

        isInit = true;
    }

    private void loadSamples() {
        File files[] = GlobalData.getSampleBoardDir().listFiles();
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new FileComparatorByTimeDesc());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (File file : fileList) {
            Bitmap photo = BitmapFactory.decodeFile(file.getPath());

            SampleBoardInfo sampleBoardInfo = new SampleBoardInfo(getContext());
            sampleBoardInfo.setThumbnailBitmap(photo);

            sampleBoardInfo.setDatetime(dateFormat.format(file.lastModified()));

            linearLayout.addView(sampleBoardInfo);
        }
    }
}

class FileComparatorByTimeDesc implements Comparator<File> {

    @Override
    public int compare(File file1, File file2) {
        if (file1.lastModified() < file2.lastModified())
            return 1;
        else
            return -1;
    }
}
