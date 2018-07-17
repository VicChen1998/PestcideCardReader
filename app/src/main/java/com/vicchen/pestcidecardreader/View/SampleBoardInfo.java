package com.vicchen.pestcidecardreader.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vicchen.pestcidecardreader.R;


public class SampleBoardInfo extends RelativeLayout {

    private ImageView thumbnail;

    private LinearLayout infoLayout;

    private TextView name;
    private TextView datetime;

    private TextView txtvReadings[];

    public SampleBoardInfo(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.sample_board_info, this);

        thumbnail = findViewById(R.id.sampleBoardThumbnail);

        infoLayout = findViewById(R.id.sampleBoardInfoLayout);

        name = findViewById(R.id.sampleBoardName);
        datetime = findViewById(R.id.sampleBoardDateTime);

        txtvReadings = new TextView[8];
        txtvReadings[0] = findViewById(R.id.history_reading1);
        txtvReadings[1] = findViewById(R.id.history_reading2);
        txtvReadings[2] = findViewById(R.id.history_reading3);
        txtvReadings[3] = findViewById(R.id.history_reading4);
        txtvReadings[4] = findViewById(R.id.history_reading5);
        txtvReadings[5] = findViewById(R.id.history_reading6);
        txtvReadings[6] = findViewById(R.id.history_reading7);
        txtvReadings[7] = findViewById(R.id.history_reading8);
    }

    public void setThumbnailBitmap(Bitmap bitmap) {
        thumbnail.setImageBitmap(bitmap);
    }

    public void setName(String s) {
        name.setText(s);
    }

    public void setDatetime(String s) {
        datetime.setText(s);
    }

    public void setReadings(String readings[]) {
        for (int i = 0; i < 8; i++)
            txtvReadings[i].setText(readings[i]);
    }
}
