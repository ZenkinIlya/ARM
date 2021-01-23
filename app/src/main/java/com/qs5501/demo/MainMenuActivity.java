package com.qs5501.demo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.facebook.stetho.Stetho;
import com.qs5501.demo.base.BaseActivity;
import com.qs5501.demo.checks.ChecksActivity;
import com.qs5501.demo.introductionAndPay.IntroductionAndPayActivity;
import com.qs5501.demo.sale.SaleActivity;
import com.qs5501.demo.settings.SettingsActivity;
import com.qs5501demo.aidl.R;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //удалить. Просмотр базы данных
        Stetho.initializeWithDefaults(this);

        listenBtn();
    }

    private void listenBtn() {
        findViewById(R.id.btn_main_menu_sale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, SaleActivity.class);
                intent.putExtra("typeCheck", "Продажа");
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_main_menu_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, SaleActivity.class);
                intent.putExtra("typeCheck", "Возврат");
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_main_menu_payment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, IntroductionAndPayActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_main_menu_base).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, BaseActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_main_menu_settings).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_main_menu_checks).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainMenuActivity.this, ChecksActivity.class);
                startActivity(intent);
            }
        });
    }

}
