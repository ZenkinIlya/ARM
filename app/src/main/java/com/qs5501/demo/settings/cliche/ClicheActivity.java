package com.qs5501.demo.settings.cliche;

import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.qs5501.demo.base.GoodAdderActivity;
import com.qs5501.demo.settings.entity.Cliche;
import com.qs5501.demo.utils.KeyboardHider;
import com.qs5501demo.aidl.R;

public class ClicheActivity extends AppCompatActivity {

    private TextInputEditText nameUser, addressOfSettlement, placeOfSettlement, advertising,
            nameOFD, websiteTaxAuthority, senderMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cliche);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_cliche);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initializeComponent();
        listenComponents();

        showCliche();
    }

    private void showCliche() {
        Cliche.load(ClicheActivity.this);
        nameUser.setText(Cliche.getNameUser());
        addressOfSettlement.setText(Cliche.getAddressOfSettlement());
        placeOfSettlement.setText(Cliche.getPlaceOfSettlement());
        advertising.setText(Cliche.getAdvertising());
        nameOFD.setText(Cliche.getNameOFD());
        websiteTaxAuthority.setText(Cliche.getWebsiteTaxAuthority());
        senderMail.setText(Cliche.getSenderMail());
    }

    private void listenComponents() {

        findViewById(R.id.btn_cliche_accept).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cliche.setNameUser(nameUser.getText().toString());
                Cliche.setAddressOfSettlement(addressOfSettlement.getText().toString());
                Cliche.setPlaceOfSettlement(placeOfSettlement.getText().toString());
                Cliche.setAdvertising(advertising.getText().toString());
                Cliche.setNameOFD(nameOFD.getText().toString());
                Cliche.setWebsiteTaxAuthority(websiteTaxAuthority.getText().toString());
                Cliche.setSenderMail(senderMail.getText().toString());
                Cliche.save(ClicheActivity.this);
                Toast.makeText(ClicheActivity.this, "Данные клише сохранены", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void initializeComponent() {
        TextInputLayout textInputLayout;

        textInputLayout = (TextInputLayout) findViewById(R.id.cliche_nameUser);
        nameUser = (TextInputEditText) textInputLayout.getEditText();

        textInputLayout = (TextInputLayout) findViewById(R.id.cliche_addressOfSettlement);
        addressOfSettlement = (TextInputEditText) textInputLayout.getEditText();

        textInputLayout = (TextInputLayout) findViewById(R.id.cliche_placeOfSettlement);
        placeOfSettlement = (TextInputEditText) textInputLayout.getEditText();

        textInputLayout = (TextInputLayout) findViewById(R.id.cliche_advertising);
        advertising = (TextInputEditText) textInputLayout.getEditText();

        textInputLayout = (TextInputLayout) findViewById(R.id.cliche_nameOFD);
        nameOFD = (TextInputEditText) textInputLayout.getEditText();

        textInputLayout = (TextInputLayout) findViewById(R.id.cliche_websiteTaxAuthority);
        websiteTaxAuthority = (TextInputEditText) textInputLayout.getEditText();

        textInputLayout = (TextInputLayout) findViewById(R.id.cliche_senderMail);
        senderMail = (TextInputEditText) textInputLayout.getEditText();
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