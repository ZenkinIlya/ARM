package com.qs5501.demo.sale.payment;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.qs5501.demo.sale.CheckUpdater;
import com.qs5501.demo.sale.payment.complete.CheckCompletionHandler;
import com.qs5501.demo.utils.KeyboardHider;
import com.qs5501.demo.utils.ParseNumber;
import com.qs5501demo.aidl.R;

import java.util.Objects;

public class PaymentActivity extends AppCompatActivity {

    private TextView paymentPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_payment);
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

    private void listenBtn() {
        findViewById(R.id.btn_payment_no_change).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double vPaymentPayment;
                vPaymentPayment = Double.parseDouble(paymentPayment.getText().toString());

                //контекст, итог, тип оплаты, наличные, безналичные, оплата, сдача
                if (CheckCompletionHandler.checkCompletion(PaymentActivity.this, vPaymentPayment,
                        "Наличные", vPaymentPayment, 0, vPaymentPayment, 0)){
                    Toast.makeText(PaymentActivity.this, "Чек успешно пробит!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        findViewById(R.id.btn_payment_different).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, DifferentPaymentActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        findViewById(R.id.btn_payment_card).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double vPaymentPayment;
                vPaymentPayment = Double.parseDouble(paymentPayment.getText().toString());

                //контекст, итог, тип оплаты, наличные, безналичные, оплата, сдача
                if (CheckCompletionHandler.checkCompletion(PaymentActivity.this, vPaymentPayment,
                        "Безналичные", 0, vPaymentPayment, vPaymentPayment, 0)){
                    Toast.makeText(PaymentActivity.this, "Чек успешно пробит!", Toast.LENGTH_LONG).show();
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        findViewById(R.id.btn_payment_advance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, AdvancePaymentActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        findViewById(R.id.btn_payment_credit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentActivity.this, CreditPaymentActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        findViewById(R.id.btn_payment_other_form).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initComponents() {
        paymentPayment = (TextView) findViewById(R.id.payment_payment);
    }
}