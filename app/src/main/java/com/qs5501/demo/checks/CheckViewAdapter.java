package com.qs5501.demo.checks;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qs5501.demo.sale.entity.Product;
import com.qs5501.demo.utils.ParseNumber;
import com.qs5501demo.aidl.R;

import java.util.ArrayList;

public class CheckViewAdapter extends RecyclerView.Adapter<CheckViewAdapter.ViewHolder> {

    private ArrayList<Product> products;
    private Context context;

    public CheckViewAdapter(Context context, ArrayList<Product> products) {
        this.products = products;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_check_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        Product product = products.get(position);

        viewHolder.productName.setText(product.getName());
        viewHolder.productPrice.setText(ParseNumber.parseNumber("#0.00", product.getPrice()));
        viewHolder.productQuantity.setText(formatValueQuantity(ParseNumber.parseNumber("#0.000",
                product.getQuantity()), product.getUnit()));
        viewHolder.productSum.setText(ParseNumber.parseNumber("#0.00", product.getSum()));

        //проверка маркированный товар или нет
        if (product.isMark()){
            viewHolder.linearLayoutMark.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
        }else {
            viewHolder.linearLayoutMark.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    0));
        }

        //проверка существования скидки %
        if (product.getDiscountPercent() != 0){
            viewHolder.linearLayoutDiscount.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT));
            viewHolder.productDiscountPercent.setText(ParseNumber.parseNumber("00.00", product.getDiscountPercent()));
            viewHolder.productDiscountNumber.setText(ParseNumber.parseNumber("#0.00", product.getDiscount()));
        }else {
            viewHolder.linearLayoutDiscount.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    0));
        }

        String[] stringMethodCalculation = context.getResources().getStringArray(R.array.method_calculation);
        viewHolder.productMethodOfCalculation.setText(stringMethodCalculation[product.getMethodOfCalculation()]);
    }

    private String formatValueQuantity(String quantity, String unit){
        return quantity + " " + unit;
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        final TextView productName, productPrice, productQuantity, productSum, productTaxRate, productTaxSystem;
        final LinearLayout linearLayoutMark, linearLayoutDiscount;
        final TextView productDiscountPercent, productDiscountNumber, productMethodOfCalculation;

        public ViewHolder(View view) {
            super(view);
            productName = (TextView) view.findViewById(R.id.check_view_product_name);
            productPrice = (TextView) view.findViewById(R.id.check_view_product_price);
            productQuantity = (TextView) view.findViewById(R.id.check_view_product_quantity);
            productSum = (TextView) view.findViewById(R.id.check_view_product_summ);

            productTaxRate = (TextView) view.findViewById(R.id.check_view_product_tax_rate);
            productTaxSystem = (TextView) view.findViewById(R.id.check_view_product_tax_system);

            linearLayoutMark = (LinearLayout) view.findViewById(R.id.check_view_product_layout_mark);
            linearLayoutDiscount = (LinearLayout) view.findViewById(R.id.check_view_product_layout_discount);

            productDiscountPercent = (TextView) view.findViewById(R.id.check_view_product_discount_percent);
            productDiscountNumber = (TextView) view.findViewById(R.id.check_view_product_discount_number);
            productMethodOfCalculation = (TextView) view.findViewById(R.id.check_view_product_method_of_calculation);
        }
    }
}
