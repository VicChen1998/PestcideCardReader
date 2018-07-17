package com.vicchen.pestcidecardreader.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vicchen.pestcidecardreader.R;


public class SampleBoardInfo extends RelativeLayout {

    private ImageView thumbnail;
    private TextView datetime;

    public SampleBoardInfo(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.sample_board_info, this);

        thumbnail = findViewById(R.id.sampleBoardThumbnail);

        datetime = findViewById(R.id.sampleBoardDateTime);
    }

    public void setThumbnailBitmap(Bitmap bitmap) {
        thumbnail.setImageBitmap(bitmap);
    }

    public void setDatetime(String s){
        datetime.setText(s);
    }
}
