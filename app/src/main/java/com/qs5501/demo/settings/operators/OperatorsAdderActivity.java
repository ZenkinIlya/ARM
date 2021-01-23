package com.qs5501.demo.settings.operators;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qs5501.demo.base.GoodAdderActivity;
import com.qs5501.demo.sale.entity.Product;
import com.qs5501.demo.settings.entity.Operator;
import com.qs5501.demo.utils.CheckComponent;
import com.qs5501.demo.utils.KeyboardHider;
import com.qs5501demo.aidl.R;
import com.satsuware.usefulviews.LabelledSpinner;

public class OperatorsAdderActivity extends AppCompatActivity {

    private TextView titleText;
    private Operator operator = new Operator();
    private TextInputEditText operatorFirstName, operatorSecondName, operatorThirdName,
            operatorLogin, operatorPassword, operatorInn;
    private LabelledSpinner operatorPost;
    private int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operator_adder);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_operator_adder);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initializeComponent();
        listenComponent();

        //Редактирование существующего товара
        Bundle arg = getIntent().getExtras();
        if (arg != null){
            titleText.setText(arg.getString("type create redaction operator", "create/redact operator"));
            if (arg.containsKey(Operator.class.getSimpleName())){
                operator = (Operator) arg.getSerializable(Operator.class.getSimpleName());
                position = arg.getInt("position");
                setDataComponentInputOperator();
            }
        }
    }

    private void listenComponent() {
        findViewById(R.id.btn_operator_adder_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                CheckComponent.setCorrectData(true);
                CheckComponent.checkEdits(operatorFirstName);
                CheckComponent.checkEdits(operatorSecondName);
                CheckComponent.checkEdits(operatorThirdName);
                CheckComponent.checkEdits(operatorLogin);
                CheckComponent.checkEdits(operatorPassword);
                CheckComponent.checkEdits(operatorInn);

                if (CheckComponent.isCorrectData()){
                    operator.setName(operatorFirstName.getText().toString());
                    operator.setSecondName(operatorSecondName.getText().toString());
                    operator.setThirdName(operatorThirdName.getText().toString());
                    operator.setPost(operatorPost.getSpinner().getSelectedItem().toString());
                    operator.setLogin(operatorLogin.getText().toString());
                    operator.setPassword(operatorPassword.getText().toString());
                    operator.setInn(Long.parseLong(operatorInn.getText().toString()));

                    Log.d("MenuSettings", position+ ": Оператор добавлен/отредактирован: " +operator.toString());

                    intent.putExtra(Operator.class.getSimpleName(), operator);
                    intent.putExtra("position", position);

                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private void initializeComponent() {
        TextInputLayout textInputLayout;

        titleText = (TextView) findViewById(R.id.title_redaction_create_operator);

        textInputLayout = (TextInputLayout) findViewById(R.id.operator_first_name);
        operatorFirstName = (TextInputEditText) textInputLayout.getEditText();

        textInputLayout = (TextInputLayout) findViewById(R.id.operator_second_name);
        operatorSecondName = (TextInputEditText) textInputLayout.getEditText();

        textInputLayout = (TextInputLayout) findViewById(R.id.operator_third_name);
        operatorThirdName = (TextInputEditText) textInputLayout.getEditText();

        textInputLayout = (TextInputLayout) findViewById(R.id.operator_login);
        operatorLogin = (TextInputEditText) textInputLayout.getEditText();

        textInputLayout = (TextInputLayout) findViewById(R.id.operator_password);
        operatorPassword = (TextInputEditText) textInputLayout.getEditText();

        textInputLayout = (TextInputLayout) findViewById(R.id.operator_inn);
        operatorInn = (TextInputEditText) textInputLayout.getEditText();

        operatorPost = (LabelledSpinner) findViewById(R.id.operator_post);
    }

    private void setDataComponentInputOperator(){
        Log.d("MenuSettings", position+ ": Редактируемый оператор: " +operator.toString());
        operatorFirstName.setText(operator.getName());
        operatorSecondName.setText(operator.getSecondName());
        operatorThirdName.setText(operator.getThirdName());
        if (position == 0){
            operatorPost.setVisibility(View.INVISIBLE);
            operatorPost.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,0));
        }else{
            operatorPost.setVisibility(View.VISIBLE);
            operatorPost.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        operatorPost.getSpinner().setSelection((operator.getPost().equals("Администратор") ? 0 : 1));
        operatorLogin.setText(operator.getLogin());
        operatorPassword.setText(operator.getPassword());
        operatorInn.setText(String.valueOf(operator.getInn()));
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