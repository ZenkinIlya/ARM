package com.qs5501.demo.sale;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qs5501.demo.base.BaseActivity;
import com.qs5501.demo.sale.entity.Product;
import com.qs5501.demo.utils.ParseNumber;
import com.qs5501demo.aidl.R;

import java.util.ArrayList;

class ProductAdapter extends ArrayAdapter<Product> {
    private LayoutInflater inflater;
    private int layout;
    private ArrayList<Product> productList;
    private Context context;

    public ProductAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Product> products) {
        super(context, resource, products);
        this.productList = products;
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder viewHolder;
        //если нет вьюшки
        if(convertView==null){
            //получаем вью из (list_item_sale, parent, false)
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Product product = productList.get(position);

        viewHolder.nameView.setText(product.getName());
        viewHolder.quantityInStockChangeView.setText(formatValueQuantityStock(ParseNumber.parseNumber("#0.000",
                product.getQuantityStockChange()), product.getUnit()));
        colorQuantityInStoke(viewHolder.quantityInStockChangeView, product.getQuantityStockChange());
        viewHolder.priceView.setText(ParseNumber.parseNumber("#0.00", product.getPrice()));
        viewHolder.quantityView.setText(formatValueQuantity(ParseNumber.parseNumber("#0.000",
                product.getQuantity()), product.getUnit()));
        viewHolder.sumView.setText(ParseNumber.parseNumber("#0.00", product.getSum()));

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
            viewHolder.discountPercentView.setText(ParseNumber.parseNumber("00.00", product.getDiscountPercent()));
            viewHolder.discountView.setText(ParseNumber.parseNumber("#0.00", product.getDiscount()));
        }else {
            viewHolder.linearLayoutDiscount.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    0));
        }


        //выпадающее меню для каждого товара в чеке
        final PopupMenu dropDownMenu = new PopupMenu(context, viewHolder.pointsSaleView);
        final Menu menu = dropDownMenu.getMenu();
        dropDownMenu.getMenuInflater().inflate(R.menu.sale_good_menu_items, menu);

        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.good_sale_menu_redaction:
                        if (context instanceof SaleActivity){
                            ((SaleActivity)context).redactPosition(product);
                        }
                        return true;
                    case R.id.good_sale_menu_delete:
                        if (context instanceof SaleActivity){
                            ((SaleActivity)context).deletePosition(product);
                        }
                        return true;
                }
                return false;
            }
        });

        viewHolder.pointsSaleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropDownMenu.show();
            }
        });

        return convertView;
    }

    private void colorQuantityInStoke(TextView quantityInStockChangeView, double quantityStockChange){
        if (quantityStockChange < 0){
            quantityInStockChangeView.setTextColor(ContextCompat.getColor(context, R.color.Red));
        }else {
            quantityInStockChangeView.setTextColor(ContextCompat.getColor(context, R.color.Black));
        }
    }

    private String formatValueQuantityStock(String quantityStock, String unit){
        return "Остаток: " + quantityStock + " " + unit;
    }

    private String formatValueQuantity(String quantity, String unit){
        return quantity + " " + unit;
    }

    private class ViewHolder {
        final TextView nameView, quantityInStockChangeView, priceView, quantityView, sumView,
                discountPercentView, discountView;
        final ImageButton pointsSaleView;
        LinearLayout linearLayoutDiscount, linearLayoutMark;
        ViewHolder(View view){
            nameView = (TextView) view.findViewById(R.id.nameSaleView);
            quantityInStockChangeView = (TextView) view.findViewById(R.id.quantityInStockSaleView);
            priceView = (TextView) view.findViewById(R.id.priceSaleView);
            quantityView = (TextView) view.findViewById(R.id.quantitySaleView);
            sumView = (TextView) view.findViewById(R.id.sumSaleView);
            pointsSaleView = (ImageButton) view.findViewById(R.id.pointsSaleView);

            linearLayoutMark = (LinearLayout) view.findViewById(R.id.layoutMarkSaleView);

            linearLayoutDiscount = (LinearLayout) view.findViewById(R.id.layoutDiscountSaleView);
            discountPercentView = (TextView) view.findViewById(R.id.discountPercentSaleView);
            discountView = (TextView) view.findViewById(R.id.discountSaleView);
        }
    }
}