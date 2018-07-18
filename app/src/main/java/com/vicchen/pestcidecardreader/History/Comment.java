package com.vicchen.pestcidecardreader.History;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.vicchen.pestcidecardreader.R;

public class Comment extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("bundle");

        Log.d("BUNDLE", bundle.getString("filename"));
        Log.d("BUNDLE", bundle.getString("datetime"));
        for (String s : bundle.getStringArray("readings")) {
            Log.d("BUNDLE", s);
        }


        Intent result = new Intent();
        bundle.putString("filename", "fuck");
        result.putExtra("bundle", bundle);
        setResult(RESULT_OK, intent);
        finish();
    }
}
