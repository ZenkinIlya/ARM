package com.qs5501.demo.sale.payment;

import android.content.Context;
import android.content.res.ColorStateList;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.qs5501.demo.base.GoodAdderActivity;
import com.qs5501.demo.sale.CheckUpdater;
import com.qs5501.demo.sale.entity.Check;
import com.qs5501.demo.sale.payment.complete.CheckCompletionHandler;
import com.qs5501.demo.settings.entity.Operator;
import com.qs5501.demo.utils.JsonHelper;
import com.qs5501.demo.utils.KeyboardHider;
import com.qs5501.demo.utils.MoneyTextWatcher;
import com.qs5501.demo.utils.ParseNumber;
import com.qs5501demo.aidl.R;

import java.util.ArrayList;
import java.util.Objects;

public class DifferentPaymentActivity extends AppCompatActivity {

    private TextView paymentPayment, paymentSurrender;
    private TextInputEditText paymentCash;
    private AppCompatButton btnDifferentPaymentPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_different_payment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_payment_different);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initComponents();
        listenBtn();

        paymentPayment.setText(ParseNumber.parseNumber("0.00",
                Objects.requireNonNull(CheckUpdater.getCheck()).getPayment()));
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
        paymentPayment = (TextView) findViewById(R.id.different_payment_payment);
        paymentSurrender = (TextView) findViewById(R.id.different_payment_surrender);

        TextInputLayout textInputLayout;
        textInputLayout = (TextInputLayout) findViewById(R.id.different_payment_cash);
        paymentCash = (TextInputEditText) textInputLayout.getEditText();

        btnDifferentPaymentPay = (AppCompatButton) findViewById(R.id.btn_different_payment_pay);
    }

    private void listenBtn() {
        paymentCash.addTextChangedListener(new MoneyTextWatcher(paymentCash, "0.00"));
        paymentCash.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                double surrender = calculateSurrender(Double.valueOf(s.toString()),
                        Double.parseDouble(paymentPayment.getText().toString()));
                paymentSurrender.setText(ParseNumber.parseNumber("0.00", surrender));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnDifferentPaymentPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double vPaymentPayment, vPaymentSurrender, vPaymentCash;
                vPaymentPayment = Double.parseDouble(paymentPayment.getText().toString());
                vPaymentSurrender = Double.parseDouble(paymentSurrender.getText().toString());
                vPaymentCash = Double.parseDouble(paymentCash.getText().toString());

                //Проверка хватает ли наличных
                if (!checkCash(vPaymentCash, vPaymentPayment)) return;

                //контекст, итог, тип оплаты, наличные, безналичные, оплата, сдача
                if (CheckCompletionHandler.checkCompletion(DifferentPaymentActivity.this, vPaymentPayment,
                        "Наличные", vPaymentCash, 0, vPaymentCash, vPaymentSurrender)){
                    Toast.makeText(DifferentPaymentActivity.this, "Чек успешно пробит!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });
    }

    private boolean checkCash(Double cash, Double payment) {
        if (cash - payment < 0){
            paymentCash.setError("не хватает наличных");
            return false;
        }else {
            paymentCash.setError(null);
            return true;
        }
    }

    private double calculateSurrender(Double cash, Double payment){
        double surrender = cash - payment;
        if (surrender < 0){
            surrender = 0;
        }
        return surrender;
    }
}