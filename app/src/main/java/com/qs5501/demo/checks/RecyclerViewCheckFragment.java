package com.qs5501.demo.checks;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.qs5501.demo.sale.entity.Check;
import com.qs5501.demo.sale.entity.Product;
import com.qs5501.demo.utils.ParseNumber;
import com.qs5501demo.aidl.R;

public class RecyclerViewCheckFragment extends Fragment {

    private View view;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CheckViewAdapter checkViewAdapter;
    private TextView total, discountCheck, typePayment, cash, notCash,
            payment, surrender, timeDate, numShift;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_recycle_view_check, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.my_recycler_view);
        return view;
    }

    private void initComponents() {
        total = (TextView) view.findViewById(R.id.check_view_total);
        discountCheck = (TextView) view.findViewById(R.id.check_view_discount_check);
        typePayment = (TextView) view.findViewById(R.id.check_view_type_payment);
        cash = (TextView) view.findViewById(R.id.check_view_cash);
        notCash = (TextView) view.findViewById(R.id.check_view_not_cash);
        payment = (TextView) view.findViewById(R.id.check_view_payment);
        surrender = (TextView) view.findViewById(R.id.check_view_surrender);
        timeDate = (TextView) view.findViewById(R.id.check_view_time_date);
        numShift = (TextView) view.findViewById(R.id.check_view_num_shift);
    }

    @Override
    public void onStart() {
        super.onStart();

        initComponents();
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);

        // use a linear layout manager
        linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        // Disabled nested scrolling since Parent scrollview will scroll the content.
        recyclerView.setNestedScrollingEnabled(false);

        Check check = (Check) getArguments().getSerializable(Check.class.getSimpleName());

        // specify an adapter (see also next example)
        if (check != null) {
            checkViewAdapter = new CheckViewAdapter(getContext(), check.getProductArrayList());
            total.setText(ParseNumber.parseNumber("0.00", check.getTotal()));
            discountCheck.setText(ParseNumber.parseNumber("0.00", check.getDiscountCheck()));
            typePayment.setText(check.getTypePayment());
            cash.setText(ParseNumber.parseNumber("0.00", check.getCash()));
            notCash.setText(ParseNumber.parseNumber("0.00", check.getNonCash()));
            payment.setText(ParseNumber.parseNumber("0.00", check.getPayment()));
            surrender.setText(ParseNumber.parseNumber("0.00", check.getSurrender()));
            String timDate = check.getTime() + "  " + check.getDate();
            timeDate.setText(timDate);
            numShift.setText(String.valueOf(check.getNumShift()));
        }
        recyclerView.setAdapter(checkViewAdapter);
    }
}