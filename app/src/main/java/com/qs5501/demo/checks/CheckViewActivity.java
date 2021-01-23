package com.qs5501.demo.checks;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.qs5501.demo.base.GoodAdderActivity;
import com.qs5501.demo.sale.entity.Check;
import com.qs5501.demo.sale.entity.Product;
import com.qs5501.demo.utils.KeyboardHider;
import com.qs5501demo.aidl.R;

public class CheckViewActivity extends AppCompatActivity {

    private Check check;
    private TextView toolbarCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_check_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_check_view);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initComponents();
        listenBtn();

        //Принимаю чек для просмотра от ChecksActivity
        Bundle arg = getIntent().getExtras();
        if (arg != null){
            check = (Check) arg.getSerializable(Check.class.getSimpleName());
            setDataComponentInputCheck();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        RecyclerViewCheckFragment fragment = new RecyclerViewCheckFragment();

        //передача чека во фрагмент
        Bundle args = new Bundle();
        args.putSerializable(Check.class.getSimpleName(), check);
        fragment.setArguments(args);

        fragmentTransaction.replace(R.id.fragment_check_view, fragment);
        fragmentTransaction.commit();
    }

    private void listenBtn() {

    }

    private void initComponents() {
        toolbarCheck = (TextView) findViewById(R.id.check_view_type_check);
    }

    private void setDataComponentInputCheck() {
        String checkViewHeader = check.getTypeCheck() + " (" + check.getSignCalculation() + ") №" +
                check.getNumCheck();
        toolbarCheck.setText(checkViewHeader);
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