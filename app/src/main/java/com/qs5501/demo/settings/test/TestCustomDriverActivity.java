package com.qs5501.demo.settings.test;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.driverUSB.commands.StatusRequester;
import com.driverUSB.utils.HexDump;
import com.qs5501demo.aidl.R;

public class TestCustomDriverActivity extends AppCompatActivity {

    public static TextView textView;

    public static TextView getTextView() {
        return textView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_costum_driver);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_test_custom);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        findViewById(R.id.btn_custom_test_status_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StatusRequester statusRequester = new StatusRequester(TestCustomDriverActivity.this, 0);
                statusRequester.requestStatus();
            }
        });

        textView = (TextView) findViewById(R.id.text_view_custom_test);
    }
}