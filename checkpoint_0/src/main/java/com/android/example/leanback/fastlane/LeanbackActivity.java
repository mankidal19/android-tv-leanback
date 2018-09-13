package com.android.example.leanback.fastlane;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.example.leanback.R;

public class LeanbackActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leanback);
    }
}
