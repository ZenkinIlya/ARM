package com.qs5501.demo.sale.payment;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qs5501.demo.sale.CheckUpdater;
import com.qs5501.demo.sale.payment.complete.CheckCompletionHandler;
import com.qs5501.demo.utils.KeyboardHider;
import com.qs5501.demo.utils.MoneyTextWatcher;
import com.qs5501.demo.utils.ParseNumber;
import com.qs5501demo.aidl.R;

import java.util.Objects;

public class AdvancePaymentActivity extends AppCompatActivity {

    private TextInputEditText advanceTextView;
    private SwitchCompat switchCompatChoosePayment;
    private String advanceTypePayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advance_payment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_advance_payment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initComponents();
        listenBtn();

        advanceTypePayment = "Наличные";
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1: {
                if (resultCode == RESULT_OK) {   //Чек успешно пробит
                    setResult(RESULT_OK);
                    finish();
                }
            }
        }
    }

    private void initComponents() {
        TextInputLayout textInputLayout;
        textInputLayout = (TextInputLayout) findViewById(R.id.advance_payment_cash);
        advanceTextView = (TextInputEditText) textInputLayout.getEditText();

        switchCompatChoosePayment = (SwitchCompat) findViewById(R.id.advance_payment_choose_payment);
    }

    private void listenBtn() {
        advanceTextView.addTextChangedListener(new MoneyTextWatcher(advanceTextView, "0.00"));

        switchCompatChoosePayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchCompatChoosePayment.isChecked()){
                    advanceTypePayment = "Безналичные";
                }else {
                    advanceTypePayment = "Наличные";
                }
            }
        });

        findViewById(R.id.btn_advance_payment_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double advance, cash, nonCash;
                advance = Double.parseDouble(advanceTextView.getText().toString());

                if (advanceTypePayment.equals("Наличные")){
                    cash = advance;
                    nonCash = 0;
                }else {
                    cash = 0;
                    nonCash = advance;
                }

                //контекст, итог, тип оплаты, наличные, безналичные, оплата, сдача
                if (CheckCompletionHandler.checkCompletion(AdvancePaymentActivity.this, advance,
                        "Аванс", cash, nonCash, advance, 0)){
                    Toast.makeText(AdvancePaymentActivity.this, "Чек успешно пробит!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }
}