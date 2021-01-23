package com.qs5501.demo.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.qs5501.demo.data.KKMDbHelper;
import com.qs5501.demo.sale.entity.Product;
import com.qs5501.demo.utils.ParseNumber;
import com.qs5501demo.aidl.R;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

class BaseProductAdapter extends ArrayAdapter<Product> implements Filterable {
    private LayoutInflater inflater;
    private int layout;
    private ArrayList<Product> productList;
    private ArrayList<Product> productListFilter;
    private Context context;
    private ModelFilter filter;

    public BaseProductAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Product> products) {
        super(context, resource, products);
        this.productList = new ArrayList<>();
        productList.addAll(products);
        this.productListFilter = new ArrayList<>();
        productListFilter.addAll(products);
        this.layout = resource;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
        getFilter();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new ModelFilter();
        }
        return filter;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final BaseProductAdapter.ViewHolder viewHolder;
        final Product productFromFilter = productListFilter.get(position);

        if(convertView==null){
            convertView = inflater.inflate(this.layout, parent, false);
            viewHolder = new BaseProductAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (BaseProductAdapter.ViewHolder) convertView.getTag();
        }

        viewHolder.nameView.setText(productFromFilter.getName());
        viewHolder.priceView.setText(ParseNumber.parseNumber("#0.00", productFromFilter.getPrice()));
        viewHolder.barcodeView.setText(String.valueOf(productFromFilter.getBarcode()));
        viewHolder.quantityInStockView.setText(formatValue(ParseNumber.parseNumber("#0.000",
                productFromFilter.getQuantityStock()), productFromFilter.getUnit()));


        //выпадающее меню для каждого товара в базе
        final PopupMenu dropDownMenu = new PopupMenu(context, viewHolder.pointsBaseView);
        final Menu menu = dropDownMenu.getMenu();
        dropDownMenu.getMenuInflater().inflate(R.menu.base_good_menu_items, menu);

        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.good_menu_redaction:
                        if (context instanceof BaseActivity){
                            ((BaseActivity)context).updateGood(productFromFilter);
                        }
                        return true;
                    case R.id.good_menu_delete:
                        if (context instanceof BaseActivity){
                            ((BaseActivity)context).deleteGood(productFromFilter, position);
                        }
                        return true;
                }
                return false;
            }
        });

        viewHolder.pointsBaseView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dropDownMenu.show();
            }
        });

        return convertView;
    }

    private String formatValue(String quantityStock, String unit){
        return "Остаток: " + quantityStock + " " + unit;
    }

    //Поиск товара для его замены на обновленный в двух миассивах (фильтрованный и исходный)
    public void updateProductList(Product product) {
        for (Product productItem: productListFilter){
            if (productItem.getId() == product.getId()){
                productList.set(productList.indexOf(productItem), product);
                productListFilter.set(productListFilter.indexOf(productItem), product);
                Toast.makeText(context, "Товар обновлен", Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    //Поиск товара для его удаления из двух массивов
    public void deleteProduct(Product product) {
        for (Product productItem: productListFilter){
            if (productItem.getId() == product.getId()){
                productList.remove(productItem);
                productListFilter.remove(productItem);
                Toast.makeText(context, "Товар удален", Toast.LENGTH_LONG).show();
                break;
            }
        }
    }

    public void addProductInList(Product product) {
        productListFilter.add(product);
        productList.add(product);
    }

    private class ViewHolder {
        final TextView nameView, priceView, barcodeView, quantityInStockView;
        final ImageButton pointsBaseView;
        ViewHolder(View view){
            nameView = (TextView) view.findViewById(R.id.nameBaseView);
            priceView = (TextView) view.findViewById(R.id.priceBaseView);
            barcodeView = (TextView) view.findViewById(R.id.barcodeBaseView);
            quantityInStockView = (TextView) view.findViewById(R.id.quantityInStockBaseView);
            pointsBaseView = (ImageButton) view.findViewById(R.id.pointsBaseView);
        }
    }

    private class ModelFilter extends Filter{

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if(constraint.toString().length() > 0)
            {
                ArrayList<Product> filteredItems = new ArrayList<>();

                for(int i = 0, l = productList.size(); i < l; i++)
                {
                    Product product = productList.get(i);
                    if(product.getName().toLowerCase().contains(constraint)){
                        filteredItems.add(product);
                    }else if (String.valueOf(product.getBarcode()).contentEquals(constraint)){
                        filteredItems.add(product);
                    }else if (String.valueOf(product.getPrice()).contentEquals(constraint)){
                        filteredItems.add(product);
                    }

                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            }
            else
            {
                synchronized(this)
                {
                    result.values = productList;
                    result.count = productList.size();
                }
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            productListFilter = (ArrayList<Product>)results.values;
            notifyDataSetChanged();
            clear();
            for(int i = 0, l = productListFilter.size(); i < l; i++)
                add(productListFilter.get(i));
            notifyDataSetInvalidated();
        }
    }
}
