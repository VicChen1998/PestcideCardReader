package com.vicchen.pestcidecardreader.View;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vicchen.pestcidecardreader.R;

public class ReadingItem extends LinearLayout {

    private TextView tvTeading;
    private TextView tvComment;

    public ReadingItem(Context context) {
        super(context);

        LayoutInflater.from(context).inflate(R.layout.reading_item, this);

        tvTeading = findViewById(R.id.infoReading);
        tvComment = findViewById(R.id.infoComment);

    }

    public void setReading(String reading) {
        tvTeading.setText(reading);
    }

    public void setComment(String comment) {
        tvComment.setText(comment);
    }

}
