package com.qs5501.demo.settings.operators;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.qs5501.demo.base.GoodAdderActivity;
import com.qs5501.demo.utils.JsonHelper;
import com.qs5501.demo.settings.entity.Operator;
import com.qs5501.demo.utils.KeyboardHider;
import com.qs5501demo.aidl.R;

import java.util.ArrayList;
import java.util.List;

public class OperatorsActivity extends AppCompatActivity {

    OperatorsAdapter adapter;
    ListView operatorsList;
    ArrayList<Operator> operators = new ArrayList<>();
    JsonHelper<ArrayList<Operator>> jsonHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("MenuSettings", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operators);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_operators);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        jsonHelper = new JsonHelper<ArrayList<Operator>>("operators.json",
                new TypeToken<ArrayList<Operator>>(){}, Context.MODE_PRIVATE);

        operatorsList = (ListView) findViewById(R.id.operators_list);

        createOperators();
        listenComponents();
    }

    private void listenComponents() {

        findViewById(R.id.fab_operators_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OperatorsActivity.this, OperatorsAdderActivity.class);
                intent.putExtra("type create redaction operator", "Созд. оператора");
                startActivityForResult(intent, 1);
            }
        });

        operatorsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateOperator(operators.get(position), position);
            }
        });
    }

    //Редактирование товара
    public void updateOperator(Operator operator, int position) {
        Intent intent = new Intent(OperatorsActivity.this, OperatorsAdderActivity.class);
        intent.putExtra(Operator.class.getSimpleName(), operator);
        intent.putExtra("position", position);
        intent.putExtra("type create redaction operator", "Редакт. оператора");
        startActivityForResult(intent, 2);
    }

    //удаление оператора
    public void deleteOperator(Operator operator, int position) {
        operators.remove(operator);
        adapter.notifyDataSetChanged();

        boolean result = jsonHelper.exportToJSON(this, operators);
        if (result){
            Log.d("MenuSettings", position +": оператор успешно УДАЛЕН");
        }else {
            Log.d("MenuSettings", position +": не удалось УДАЛИТЬ оператора");
        }
    }

    private void createOperators() {
        Operator admin = new Operator("Администратор", "Илья", "Зенкин",
                "Николавевич", "admin", "1111", 432);

/*        if (jsonHelper.deleteJson(this)) {
            Log.d("MenuSettings", "operators.json УДАЛЕН");
        }*/

        //проверка на существование operators.json
        boolean result = jsonHelper.createJson(this);
        if(result){
            Log.d("MenuSettings", "operators.json существует");
            operators = jsonHelper.importFromJSON(this);

            //проверка существования администратора
            result = existAdmin(operators);
            if (result){
                Log.d("MenuSettings", "администратор существует");
                viewOperators();
            }else{
                Log.d("MenuSettings", "администратор не существует");
                operators.add(0, admin);
                result = jsonHelper.exportToJSON(this, operators);
                if (result){
                    Log.d("MenuSettings", "администратор успешно добавлен");
                    viewOperators();
                }else {
                    Log.d("MenuSettings", "не удалось доабвить администратора");
                }
            }
        }
        else{
            Log.d("MenuSettings", "operators.json не существует");
            operators.add(admin);
            result = jsonHelper.exportToJSON(this, operators);
            if (result){
                Log.d("MenuSettings", "администратор успешно добавлен");
                viewOperators();
            }else {
                Log.d("MenuSettings", "не удалось доабвить администратора");
            }
        }
    }

    private boolean existAdmin(List<Operator> operators) {
        for (Operator operator: operators){
            if (operator.getPost().equals("Администратор")){
                return true;
            }
        }
        return false;
    }

    private void viewOperators() {
        adapter = new OperatorsAdapter(this, R.layout.list_item_operators, operators);
        operatorsList.setAdapter(adapter);
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
        Log.d("MenuSettings", "onActivityResult()");
        switch (requestCode) {
            case 1: {  //Добавление оператора
                if (resultCode == RESULT_OK) {
                    Operator operator = (Operator) data.getSerializableExtra(Operator.class.getSimpleName());
                    operators.add(operator);
                    adapter.notifyDataSetChanged();

                    boolean result = jsonHelper.exportToJSON(this, operators);
                    if (result){
                        Log.d("MenuSettings", "оператор успешно ДОБАВЛЕН");
                    }else {
                        Log.d("MenuSettings", "не удалось ДОБАВИТЬ оператора");
                    }

                } else {
                    Toast.makeText(this, "Отмена добавления", Toast.LENGTH_SHORT).show();
                }
                break;
            }
            case 2:{  //Обновление оператора
                if (resultCode == RESULT_OK){
                    Operator operator = (Operator) data.getSerializableExtra(Operator.class.getSimpleName());
                    int position = data.getIntExtra("position", 0);
                    operators.set(position, operator);
                    adapter.notifyDataSetChanged();

                    boolean result = jsonHelper.exportToJSON(this, operators);
                    if (result){
                        Log.d("MenuSettings", "оператор успешно ОБНОВЛЕН");
                    }else {
                        Log.d("MenuSettings", "не удалось ОБНОВИТЬ оператора");
                    }

                }else {
                    Toast.makeText(this, "Отмена редактирования", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }
}