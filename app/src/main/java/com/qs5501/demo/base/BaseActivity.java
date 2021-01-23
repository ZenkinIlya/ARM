package com.qs5501.demo.base;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.qs.wiget.App;
import com.qs5501.demo.MainPrinterActivity;
import com.qs5501.demo.data.KKMDbHelper;
import com.qs5501.demo.sale.CheckUpdater;
import com.qs5501.demo.sale.dialog.DialogListener;
import com.qs5501.demo.sale.entity.Product;
import com.qs5501.demo.utils.KeyboardHider;
import com.qs5501demo.aidl.R;

import java.util.ArrayList;
import java.util.Objects;

public class BaseActivity extends AppCompatActivity implements DialogListener {

    ArrayList<Product> products = new ArrayList<>();
    BaseProductAdapter adapter;
    ListView productList;
    EditText editTextSearch;
    ImageButton imageButtonBarcodeSearch;
    private ScanBroadcastReceiver scanBroadcastReceiver;

    private KKMDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_base);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        productList = (ListView) findViewById(R.id.productListBase);
        editTextSearch = (EditText) findViewById(R.id.edit_text_base_search);
        imageButtonBarcodeSearch = (ImageButton) findViewById(R.id.barcode_base_search);

        listenBtn();

        scanBroadcastReceiver = new ScanBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.qs.scancode");
        this.registerReceiver(scanBroadcastReceiver, intentFilter);

        dbHelper = new KKMDbHelper(this);
        viewData();
    }

    //Поиск всех элементов в SQLite базе и отображение их в ListView с помощью adapter
    private void viewData() {
        products.clear();
        Cursor cursor = dbHelper.viewData();
        if (cursor.getCount() == 0){
            Toast.makeText(this, "База пустая", Toast.LENGTH_SHORT).show();
        }else {
            while (cursor.moveToNext()){
                Product product = new Product();
                product.setId(cursor.getInt(cursor.getColumnIndex("_id")));
                product.setName(cursor.getString(cursor.getColumnIndex("name")));
                product.setBarcode(cursor.getLong(cursor.getColumnIndex("barcode")));
                product.setPrice(cursor.getDouble(cursor.getColumnIndex("price")));
                if (cursor.getInt(cursor.getColumnIndex("mark")) == 0){
                    product.setMark(false);
                }else product.setMark(true);

                product.setUnit(cursor.getString(cursor.getColumnIndex("unit")));
                product.setQuantityStock(cursor.getDouble(cursor.getColumnIndex("quantity_good_in_stock")));
                product.setTaxSystem(cursor.getInt(cursor.getColumnIndex("tax_system")));
                product.setTaxRate(cursor.getInt(cursor.getColumnIndex("tax_rate")));
                products.add(product);
            }
        }
        cursor.close();  //курсор нужно закрывать для освобождения памяти
        adapter = new BaseProductAdapter(this, R.layout.list_item_base, products);
        productList.setAdapter(adapter);
    }

    private void listenBtn() {
        findViewById(R.id.fab_base).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BaseActivity.this, GoodAdderActivity.class);
                intent.putExtra("type create redaction product", "Создание товара");
                startActivityForResult(intent, 1);  //Добавление товара
            }
        });

        productList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (products.get(position).isMark()){
                    CheckUpdater.setMarkProduct(products.get(position), getSupportFragmentManager());
                }else {
                    CheckUpdater.addProduct(BaseActivity.this, products.get(position));
                }
            }
        });

        editTextSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (CheckUpdater.dialog == null){
                    adapter.getFilter().filter(s.toString());
                }else {
                    s.clear();
                }

            }
        });

        imageButtonBarcodeSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editTextSearch.requestFocus();
                App.openScan();
            }
        });
    }

    @Override
    public void updateCheck() {
        //
    }

    @Override
    public void updateProduct(Product product) {
        CheckUpdater.addMarkProduct(BaseActivity.this, product);
    }

    class ScanBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //Получить данные сканирования
            String barcode = Objects.requireNonNull(intent.getExtras()).getString("code");

            if (barcode != null) {
                editTextSearch.setText(barcode.trim());
            }
            Spannable text = editTextSearch.getText();
            Selection.setSelection(text, text.length());
        }
    }

    //Редактирование товара
    public void updateGood(Product product) {
        Intent intent = new Intent(BaseActivity.this, GoodAdderActivity.class);
        intent.putExtra(Product.class.getSimpleName(), product);
        intent.putExtra("type create redaction product", "Редакт. товара");
        startActivityForResult(intent, 2);
    }

    //Удаление товара
    public void deleteGood(Product productFromFilter, int position) {
        int deletedRow = dbHelper.delete(productFromFilter.getId());
        if (deletedRow == 0){
            Toast.makeText(this, "Не удалось удалить товар", Toast.LENGTH_LONG).show();
        }else {
            products.remove(position);
            adapter.deleteProduct(productFromFilter);
            productList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:{  //Добавление товара
                if (resultCode == RESULT_OK){
                    final Product product;
                    product = (Product) data.getSerializableExtra(Product.class.getSimpleName());

                    //Добавление в базу sqlLite
                    if (!dbHelper.insertData(product)){
                        Toast.makeText(this, "Неудалось добавить товар в базу", Toast.LENGTH_SHORT).show();
                    }else {
                        product.setId(dbHelper.getIDAddedItem());  //получить id от последнего добавленного товара

                        adapter.addProductInList(product);
                        adapter.add(product);
                        productList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    }
                }else {
                    Toast.makeText(this, "Отмена добавления", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 2:{    //Обновление товара
                if (resultCode == RESULT_OK){
                    final Product product;
                    product = (Product) data.getSerializableExtra(Product.class.getSimpleName());
                    if (dbHelper.update(product) == 0){
                        Toast.makeText(this, "Не удалось обновить товар в SQLite", Toast.LENGTH_LONG).show();
                    }else {
                        adapter.updateProductList(product);
                        productList.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        CheckUpdater.updateProduct(this, product);
                    }
                }else {
                Toast.makeText(this, "Отмена редактирования", Toast.LENGTH_SHORT).show();
            }
                break;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.base_menu_items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                KeyboardHider.hideKeyboard(this);
                this.finish();
                return true;
            case R.id.menu_clean_base:
                dbHelper.deleteBase();
                viewData();
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
