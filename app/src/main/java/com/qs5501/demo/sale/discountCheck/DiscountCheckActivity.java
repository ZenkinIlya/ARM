package com.qs5501.demo.sale.discountCheck;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.qs5501.demo.base.GoodAdderActivity;
import com.qs5501.demo.sale.CheckUpdater;
import com.qs5501.demo.utils.KeyboardHider;
import com.qs5501.demo.utils.MoneyTextWatcher;
import com.qs5501.demo.utils.ParseNumber;
import com.qs5501demo.aidl.R;

public class DiscountCheckActivity extends AppCompatActivity {

    private TextView payWithoutDiscount, payWithDiscount;
    private EditText checkDiscount, checkDiscountPercent;
    private Button btnAddDiscount, btnCancel;
    private double payWithoutDisc, payWithDisc, discount, discountPercent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount_check);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_discountCheck);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initComponents();
        listenBtn();

        payWithoutDiscount.setText(ParseNumber.parseNumber("#0.00", CheckUpdater.getCheck().getTotal()));
        checkDiscount.setText(ParseNumber.parseNumber("#0.00", CheckUpdater.getCheck().getDiscountCheck()));

        payWithoutDisc = Double.parseDouble(payWithoutDiscount.getText().toString());
        calculateDiscount();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            KeyboardHider.hideKeyboard(this);
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initComponents() {
        payWithoutDiscount = (TextView) findViewById(R.id.discount_check_pay_without_discount);
        payWithDiscount = (TextView) findViewById(R.id.discount_check_pay_with_discount);
        checkDiscount = (EditText) findViewById(R.id.discount_check_discount);
        checkDiscountPercent = (EditText) findViewById(R.id.discount_check_discount_percent);
        btnAddDiscount = (Button) findViewById(R.id.discount_check_btn_add_discount_check);
        btnCancel = (Button) findViewById(R.id.discount_check_cancel);
    }

    private void listenBtn() {

        checkDiscount.addTextChangedListener(new MoneyTextWatcher(checkDiscount, "#0.00"));
        checkDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkDiscount.hasFocus()){
                    calculateDiscount();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkValuesAndSetErrors();
            }
        });

        checkDiscountPercent.addTextChangedListener(new MoneyTextWatcher(checkDiscountPercent, "#0.00"));
        checkDiscountPercent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (checkDiscountPercent.hasFocus()){
                    calculateDiscountPercent();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                checkValuesAndSetErrors();
            }
        });

        btnAddDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("total", Double.parseDouble(payWithDiscount.getText().toString()));
                intent.putExtra("discountCheck", Double.parseDouble(checkDiscount.getText().toString()));
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void checkValuesAndSetErrors() {
        boolean error = false;
        if (discount > payWithoutDisc){
            checkDiscount.setError("Скидка не должна быть больше оплаты по скидке");
            error = true;
        }
        if (discountPercent > 100){
            checkDiscountPercent.setError("Скидка не должна превышать 100%");
            error = true;
        }
        if (error){
            btnAddDiscount.setEnabled(false);
            btnAddDiscount.setBackgroundTintList(ColorStateList.valueOf(0xffC6CFF6));
        }else {
            btnAddDiscount.setEnabled(true);
            checkDiscount.setError(null);
            checkDiscountPercent.setError(null);
            btnAddDiscount.setBackgroundTintList(ColorStateList.valueOf(0xff768fff));
        }
    }

    private void calculateDiscount() {
        discount = Double.parseDouble(checkDiscount.getText().toString());
        if (payWithoutDisc == 0){
            discountPercent = 0;
        }else{
            discountPercent = discount * 100 / payWithoutDisc;
        }
        payWithDisc = payWithoutDisc - discount;

        checkDiscountPercent.setText(ParseNumber.parseNumber("#0.00", discountPercent));
        payWithDiscount.setText(ParseNumber.parseNumber("#0.00", payWithDisc));
    }

    private void calculateDiscountPercent(){
        discountPercent = Double.parseDouble(checkDiscountPercent.getText().toString());
        discount = discountPercent * payWithoutDisc / 100;
        payWithDisc = payWithoutDisc - discount;

        checkDiscount.setText(ParseNumber.parseNumber("#0.00", discount));
        payWithDiscount.setText(ParseNumber.parseNumber("#0.00", payWithDisc));
    }
}