package com.qs5501.demo.introductionAndPay;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.qs5501.demo.base.GoodAdderActivity;
import com.qs5501.demo.utils.KeyboardHider;
import com.qs5501.demo.utils.Printer;
import com.qs5501demo.aidl.R;

public class IntroductionAndPayActivity extends AppCompatActivity {

    EditText editPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_introduction_and_pay);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_introduction_pay);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);  /*NullPointerException*/
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listenBtn();
    }

    private void listenBtn() {
        findViewById(R.id.btn_making).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPayment = (EditText) findViewById(R.id.edit_payment);
                if (editPayment != null) {
                    Printer.printText("Внесение", 1, 1);
                    Printer.printText(editPayment.getText().toString(), 1, 1);
                }
            }
        });

        findViewById(R.id.btn_payment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPayment = (EditText) findViewById(R.id.edit_payment);
                if (editPayment != null) {
                    Printer.printText("Выплата", 1, 1);
                    Printer.printText(editPayment.getText().toString(), 1, 1);
                }
            }
        });

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
}
