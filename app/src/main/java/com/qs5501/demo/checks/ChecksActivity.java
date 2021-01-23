package com.qs5501.demo.checks;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.qs5501.demo.base.GoodAdderActivity;
import com.qs5501.demo.sale.entity.Check;
import com.qs5501.demo.utils.JsonHelper;
import com.qs5501.demo.utils.KeyboardHider;
import com.qs5501demo.aidl.R;

import java.util.ArrayList;

public class ChecksActivity extends AppCompatActivity {

    private JsonHelper<ArrayList<Check>> jsonHelper;
    private ArrayList<Check> checkArrayList;
    private ChecksAdapter checkAdapter;
    private ListView checkListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_checks);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initComponents();
        listenBtn();

        jsonHelper = new JsonHelper<ArrayList<Check>>("KL.json",
                new TypeToken<ArrayList<Check>>(){}, Context.MODE_PRIVATE);

        checkArrayList = jsonHelper.importFromJSON(ChecksActivity.this);

        if (checkArrayList == null){
            Toast.makeText(this, "Контрольная лента пустая", Toast.LENGTH_LONG).show();
        }else {
            checkAdapter = new ChecksAdapter(this, R.layout.list_item_check, checkArrayList);
            checkListView.setAdapter(checkAdapter);
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

    private void initComponents() {
        checkListView = (ListView) findViewById(R.id.checks_list_view);
    }

    private void listenBtn() {
        checkListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Check check = checkArrayList.get(position);
                Intent intent = new Intent(ChecksActivity.this, CheckViewActivity.class);
                intent.putExtra(Check.class.getSimpleName(), check);
                startActivity(intent);
            }
        });
    }


}