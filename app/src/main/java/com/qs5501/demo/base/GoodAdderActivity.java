package com.qs5501.demo.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.TextView;

import com.qs.wiget.App;
import com.qs5501.demo.sale.entity.Product;
import com.qs5501.demo.utils.CheckComponent;
import com.qs5501.demo.utils.KeyboardHider;
import com.qs5501.demo.utils.MoneyTextWatcher;
import com.qs5501.demo.utils.ParseNumber;
import com.qs5501.demo.utils.SpinnerSelectionHelper;
import com.qs5501demo.aidl.R;
import com.satsuware.usefulviews.LabelledSpinner;

import org.w3c.dom.Text;

import java.util.Objects;

public class GoodAdderActivity extends AppCompatActivity {

    private Product product = new Product();

    private TextView titleText;
    private TextInputEditText goodName, goodBarcode, goodPrice, goodQuantityInStock;
    private LabelledSpinner goodUnit, goodTaxSystem, goodTaxRate;
    private CheckBox goodMark;
    private ScanBroadcastReceiver scanBroadcastReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_good_creator_redactor);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_good_adder);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initializeComponent();
        listenBtn();

        scanBroadcastReceiver = new ScanBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.qs.scancode");
        this.registerReceiver(scanBroadcastReceiver, intentFilter);

        //Редактирование существующего товара
        Bundle arg = getIntent().getExtras();
        if (arg != null){
            titleText.setText(arg.getString("type create redaction product", "create/redact product"));
            if (arg.containsKey(Product.class.getSimpleName())){
                product = (Product) arg.getSerializable(Product.class.getSimpleName());
                setDataComponentInputProduct();
            }else {
                setDataComponent();
            }
        }else {
            setDataComponent();
        }
    }

    private void setDataComponentInputProduct(){
        goodName.setText(product.getName());
        goodBarcode.setText(String.valueOf(product.getBarcode()));
        goodPrice.setText(ParseNumber.parseNumber("#0.00", product.getPrice()));
        goodMark.setChecked(product.isMark());
        goodUnit.getSpinner().setSelection(SpinnerSelectionHelper.getPosition(product.getUnit(),
                getResources().getStringArray(R.array.units)));
        goodQuantityInStock.setText(ParseNumber.parseNumber("#0.000", product.getQuantityStock()));
        goodTaxSystem.getSpinner().setSelection(product.getTaxSystem());
        goodTaxRate.getSpinner().setSelection(product.getTaxRate());
    }

    private void setDataComponent(){
        goodName.setText("");
        goodBarcode.setText("4680211101480");

        Objects.requireNonNull(goodPrice).setText("20.00");
        Objects.requireNonNull(goodQuantityInStock).setText("10.000");
    }

    private void initializeComponent() {
        TextInputLayout textInputLayout;

        titleText = (TextView) findViewById(R.id.title_redaction_create_product);

        textInputLayout = (TextInputLayout) findViewById(R.id.good_name);
        goodName = (TextInputEditText) textInputLayout.getEditText();

        textInputLayout = (TextInputLayout) findViewById(R.id.good_barcode);
        goodBarcode = (TextInputEditText) textInputLayout.getEditText();

        textInputLayout = (TextInputLayout) findViewById(R.id.good_price);
        goodPrice = (TextInputEditText) textInputLayout.getEditText();

        goodUnit = (LabelledSpinner) findViewById(R.id.good_unit);

        textInputLayout = (TextInputLayout) findViewById(R.id.good_quantity_in_stock);
        goodQuantityInStock = (TextInputEditText) textInputLayout.getEditText();

        goodTaxSystem = (LabelledSpinner) findViewById(R.id.good_tax_system);
        goodTaxRate = (LabelledSpinner) findViewById(R.id.good_tax_rate);

        goodMark = (CheckBox) findViewById(R.id.good_mark);
    }

    private void listenBtn() {
        findViewById(R.id.btn_good_adder_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();

                CheckComponent.setCorrectData(true);
                CheckComponent.checkEdits(goodName);
                CheckComponent.checkBarcode(goodBarcode);
                CheckComponent.checkEdits(goodPrice);
                CheckComponent.checkEdits(goodQuantityInStock);

                if (CheckComponent.isCorrectData()){
                    product.setName(goodName.getText().toString());
                    product.setBarcode(Long.parseLong(goodBarcode.getText().toString()));
                    product.setPrice(Double.parseDouble(goodPrice.getText().toString()));
                    product.setMark(goodMark.isChecked());
                    product.setUnit((String) goodUnit.getSpinner().getSelectedItem());
                    product.setQuantityStock(Double.parseDouble(goodQuantityInStock.getText().toString()));
                    product.setTaxSystem(goodTaxSystem.getSpinner().getSelectedItemPosition());
                    product.setTaxRate(goodTaxRate.getSpinner().getSelectedItemPosition());

                    Log.d("SQLiteAddGood", goodPrice.getText().toString() + "; " +
                            goodQuantityInStock.getText().toString());
                    Log.d("SQLiteAddGood", product.toString());

                    intent.putExtra(Product.class.getSimpleName(), product);

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });

        findViewById(R.id.btn_barcode_good_creator_redactor).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goodBarcode.requestFocus();
                App.openScan();
            }
        });

        goodBarcode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                CheckComponent.checkBarcode(goodBarcode);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        goodPrice.addTextChangedListener(new MoneyTextWatcher(goodPrice, "##0.00"));
        goodQuantityInStock.addTextChangedListener(new MoneyTextWatcher(goodQuantityInStock, "##0.000"));
    }

    class ScanBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Получить данные сканирования
            String barcode = Objects.requireNonNull(intent.getExtras()).getString("code");

            if (barcode != null) {
                goodBarcode.setText(barcode.trim());
            }
            Spannable text = goodBarcode.getText();
            Selection.setSelection(text, text.length());
        }
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
    protected void onDestroy() {
        unregisterReceiver(scanBroadcastReceiver);
        super.onDestroy();
    }
}