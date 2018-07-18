package com.vicchen.pestcidecardreader.History;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.vicchen.pestcidecardreader.Global.Database;
import com.vicchen.pestcidecardreader.R;
import com.vicchen.pestcidecardreader.Global.GlobalPath;
import com.vicchen.pestcidecardreader.View.SampleBoardInfo;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static android.app.Activity.RESULT_CANCELED;

public class History extends Fragment {

    public static final int COMMENT_REQUEST = 1;

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
        File files[] = GlobalPath.getSampleBoardDir().listFiles();
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new FileComparatorByTimeDesc());

        final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        Database database = new Database();

        for (final File file : fileList) {
            // 读取图片
            Bitmap photo = BitmapFactory.decodeFile(file.getPath());
            // 从数据库读取参数
            final String readings[] = database.searchReadings(file.getName());
            // 初始化布局
            SampleBoardInfo sampleBoardInfo = new SampleBoardInfo(getActivity());
            // 设置样板名
            sampleBoardInfo.setFilename(file.getName());
            // 设置样板图片
            sampleBoardInfo.setThumbnailBitmap(photo);
            // 设置日期时间
            sampleBoardInfo.setDatetime(dateFormat.format(file.lastModified()));
            // 设置读数
            sampleBoardInfo.setReadings(readings);
            // 设置菜单
            sampleBoardInfo.setMenuOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    PopupMenu popupMenu = new PopupMenu(getContext(), view);
                    popupMenu.getMenuInflater().inflate(R.menu.history_sample_board_info_menu, popupMenu.getMenu());
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getTitle().toString()) {
                                case "注释":
                                    Intent intent = new Intent();
                                    intent.setClass(getContext(), Comment.class);
                                    // 初始化Bundle
                                    Bundle bundle = new Bundle();
                                    // 插入文件名
                                    bundle.putString("filename", file.getName());
                                    // 插入日期时间
                                    bundle.putString("datetime", dateFormat.format(file.lastModified()));
                                    // 插入读数
                                    bundle.putStringArray("readings", readings);
                                    // 附上数据
                                    intent.putExtra("bundle", bundle);
                                    // 切换至Comment
                                    startActivityForResult(intent, COMMENT_REQUEST);
                                    break;
                                case "删除":
                                    Database database = new Database();
                                    database.deleteReadings(file.getName());
                                    file.delete();
                                    break;
                            }
                            return true;
                        }
                    });
                    popupMenu.show();
                }
            });
            // 显示布局
            linearLayout.addView(sampleBoardInfo);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {

            case COMMENT_REQUEST:
                if (resultCode == RESULT_CANCELED)
                    return;

                Log.d("COMMENT RETURN", "COMMENT RETURN");
                Bundle result = data.getBundleExtra("bundle");
                Log.d("RESULT", result.getString("filename"));

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
