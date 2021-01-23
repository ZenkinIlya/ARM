package com.qs5501.demo.sale;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.qs5501.demo.base.BaseActivity;
import com.qs5501.demo.base.GoodAdderActivity;
import com.qs5501.demo.sale.dialog.DialogListener;
import com.qs5501.demo.sale.dialog.DialogRedactionProductFragment;
import com.qs5501.demo.sale.discountCheck.DiscountCheckActivity;
import com.qs5501.demo.sale.entity.Check;
import com.qs5501.demo.sale.entity.Product;
import com.qs5501.demo.sale.payment.PaymentActivity;
import com.qs5501.demo.utils.KeyboardHider;
import com.qs5501.demo.utils.ParseNumber;
import com.qs5501demo.aidl.R;

import java.util.ArrayList;

public class SaleActivity extends AppCompatActivity implements DialogListener {

    private ArrayList<Product> products = new ArrayList<>();
    private ListView productList;
    private ProductAdapter adapter;
    private TextView pay;
    private TextView discount;
    private SwitchCompat switchSignCalculation;
    private TextView title;
    private LinearLayout layoutDiscountCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sale);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_sale);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initComponents();
        listenBtn();

        products = CheckUpdater.getProducts();
        adapter = new ProductAdapter(this, R.layout.list_item_sale, products);
        productList.setAdapter(adapter);

        //Получение вида чека из MainMenuActivity
        Bundle arg = getIntent().getExtras();
        if (arg != null){
            String typeCheck = arg.getString("typeCheck");
            CheckUpdater.getCheck().setTypeCheck(typeCheck);
            title.setText(typeCheck);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        products = CheckUpdater.getProducts();
        updateVisualCheck();
    }

    private void initComponents() {
        title = (TextView) findViewById(R.id.sale_title);
        productList = (ListView) findViewById(R.id.productListCheck);
        pay = (TextView) findViewById(R.id.sale_pay);
        discount = (TextView) findViewById(R.id.sale_discount);
        switchSignCalculation = (SwitchCompat) findViewById(R.id.sale_switch_sign_calculation);
        layoutDiscountCheck = (LinearLayout) findViewById(R.id.layout_discount_check);
    }

    private void listenBtn() {
        switchSignCalculation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switchSignCalculation.isChecked()){
                    CheckUpdater.getCheck().setSignCalculation("расход");
                }else {
                    CheckUpdater.getCheck().setSignCalculation("приход");
                }
            }
        });

        productList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                redactPosition(products.get(position));
                return false;
            }
        });

        findViewById(R.id.fab_sale).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SaleActivity.this, BaseActivity.class);
                startActivity(intent);
            }
        });

        findViewById(R.id.btn_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SaleActivity.this, PaymentActivity.class);
                startActivityForResult(intent, 2);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sale_menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                KeyboardHider.hideKeyboard(this);
                this.finish();
                return true;

            case R.id.sale_menu_barcode:
                return true;

            case R.id.sale_menu_discount_check:
                Intent intent = new Intent(this, DiscountCheckActivity.class);
                startActivityForResult(intent, 1);
                return true;

            case R.id.sale_menu_delete_check:
                CheckUpdater.clearProducts();
                updateVisualCheck();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){

            //скидка на весь чек
            case 1:{
                if (resultCode == RESULT_OK){
                    CheckUpdater.getCheck().setTotal(data.getDoubleExtra("total", 0.00));
                    CheckUpdater.getCheck().setDiscountCheck(data.getDoubleExtra("discountCheck", 0.00));
                    updateVisualCheck();
                    break;
                }
            }

            //чек успешно пробит
            case 2:{
                if (resultCode == RESULT_OK){
                    CheckUpdater.clearProducts();
                    updateVisualCheck();
                    break;
                }
            }
        }
    }

    private void updateVisualCheck() {
        CheckUpdater.setProducts(products);
        CheckUpdater.updateCheck();

        pay.setText(ParseNumber.parseNumber("#0.00", CheckUpdater.getCheck().getPayment()));

        discount.setText(ParseNumber.parseNumber("#0.00", CheckUpdater.getCheck().getDiscountCheck()));
        if (!discount.getText().equals("0.00")){
            layoutDiscountCheck.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }else {
            layoutDiscountCheck.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    0));
        }

        if (CheckUpdater.getCheck().getSignCalculation() == null){
            CheckUpdater.getCheck().setSignCalculation("приход");
        }else {
            switchSignCalculation.setChecked(CheckUpdater.getCheck().getSignCalculation().equals("расход"));
        }
        products = CheckUpdater.getCheck().getProductArrayList();
        adapter.notifyDataSetChanged();
    }

    //вызывается по долгому нажатию и из АДАПТЕРА
    public void redactPosition(Product product){
        DialogRedactionProductFragment dialog = new DialogRedactionProductFragment();
        Bundle args = new Bundle();
        args.putSerializable(Product.class.getSimpleName(), product);
        dialog.setArguments(args);
        dialog.show(getSupportFragmentManager(), "redaction product");
    }

    //вызывается из АДАПТЕРА
    public void deletePosition(Product product) {
        products.remove(product);           //удаление из списка
        updateVisualCheck();
    }

    @Override
    public void updateCheck() {
        updateVisualCheck();
    }

    @Override
    public void updateProduct(Product product) {
        //Этот метод вызывается при добавлении маркированного товара
    }
}
