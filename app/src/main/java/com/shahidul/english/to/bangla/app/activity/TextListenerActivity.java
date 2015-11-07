package com.shahidul.english.to.bangla.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * @author Shahidul Islam
 * @since 10/25/2015.
 */
public class TextListenerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this,MainActivity.class);
        String sharedText = getIntent().getStringExtra(Intent.EXTRA_TEXT);
        intent.putExtra(Intent.EXTRA_TEXT, sharedText);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
