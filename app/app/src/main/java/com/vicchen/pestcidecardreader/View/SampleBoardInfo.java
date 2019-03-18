package com.vicchen.pestcidecardreader.View;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.vicchen.pestcidecardreader.R;


public class SampleBoardInfo extends RelativeLayout {

    private String filename;

    private ImageView thumbnail;

    private LinearLayout infoLayout;

    private TextView name;
    private TextView datetime;

    private ImageButton btMenu;

    private LinearLayout topReadingsLayout;
    private LinearLayout bottomReadingsLayout;
    private ReadingItem readingItems[];

    public SampleBoardInfo(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.info_item, this);

        thumbnail = findViewById(R.id.sampleBoardThumbnail);

        infoLayout = findViewById(R.id.sampleBoardInfoLayout);

        name = findViewById(R.id.sampleBoardName);
        datetime = findViewById(R.id.sampleBoardDateTime);

        btMenu = findViewById(R.id.sampleBoardMenu);

        topReadingsLayout = findViewById(R.id.historyTopReadingsLayout);
        bottomReadingsLayout = findViewById(R.id.historyBottomReadingsLayout);

        readingItems = new ReadingItem[8];
        for (int i = 0; i < 4; i++) {
            ReadingItem readingItem = new ReadingItem(context);
            readingItems[i] = readingItem;
            topReadingsLayout.addView(readingItem);
        }

        for (int i = 4; i < 8; i++) {
            ReadingItem readingItem = new ReadingItem(context);
            readingItems[i] = readingItem;
            bottomReadingsLayout.addView(readingItem);
        }

    }


    public void setFilename(String s) {
        filename = s;
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
        for (int i = 0; i < 8; i++) {
            readingItems[i].setReading(readings[i]);
            readingItems[i].setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1.0f)
            );
        }
    }

    public void setMenuOnClickListener(OnClickListener listener) {
        btMenu.setOnClickListener(listener);
    }
}
