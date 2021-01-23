package com.qs5501.demo.settings;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.qs5501.demo.settings.cliche.ClicheActivity;
import com.qs5501.demo.settings.operators.OperatorsActivity;
import com.qs5501.demo.settings.test.TestActivity;
import com.qs5501.demo.settings.test.TestCustomDriverActivity;
import com.qs5501.demo.utils.KeyboardHider;
import com.qs5501demo.aidl.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initComponents();
        listenComponents();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                KeyboardHider.hideKeyboard(this);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void initComponents() {

    }

    private void listenComponents() {

        findViewById(R.id.btn_settings_operators).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, OperatorsActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_settings_cliche).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, ClicheActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_settings_test).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_settings_test_costum_driver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, TestCustomDriverActivity.class);
                startActivity(intent);
            }
        });
    }
}