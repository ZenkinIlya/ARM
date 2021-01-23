package com.qs5501.demo.sale;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.widget.Toast;

import com.qs5501.demo.base.DialogSetMarkValueFragment;
import com.qs5501.demo.sale.dialog.DialogRedactionProductFragment;
import com.qs5501.demo.sale.entity.Check;
import com.qs5501.demo.sale.entity.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CheckUpdater {

    private static ArrayList<Product> products = new ArrayList<>();
    private static Check check = new Check();
    public static DialogSetMarkValueFragment dialog;

    public static ArrayList<Product> getProducts() {
        return products;
    }
    public static void setProducts(ArrayList<Product> products) {
        CheckUpdater.products = products;
    }

    public static Check getCheck() {
        return check;
    }
    public static void setCheck(Check check) {
        CheckUpdater.check = check;
    }



    public static void clearProducts(){
        CheckUpdater.products.clear();
    }

    //Вызов диалог окна для установки маркировки
    public static void setMarkProduct(Product product, FragmentManager supportFragmentManager) {
        dialog = new DialogSetMarkValueFragment();
        Bundle args = new Bundle();
        args.putSerializable(Product.class.getSimpleName(), product);
        dialog.setArguments(args);
        dialog.show(supportFragmentManager, "redaction product");
    }

    //Добавление в чек маркированного товара после подтверждения в диалоговом окне
    public static void addMarkProduct(Context context, Product product){
        dialog = null;

        //проверка есть ли товар с такой же марикровкой
        if (products.contains(product)){
            Toast.makeText(context, "Маркированный товар \"" +product.getName()+ "\" уже есть в чеке",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //если маркированного товара нет в чеке
        product.setQuantity(1.000);
        products.add(product);
        Toast.makeText(context, "Маркированный товар \"" +product.getName()+ "\" добавлен в чек",
                Toast.LENGTH_SHORT).show();
        updateCheck();
    }

    public static void addProduct(Context context, Product product) {
        boolean productInList = false;

        for (Product pr: products){
            if (pr.equals(product)){
                pr.setQuantity(pr.getQuantity() + 1.000);
                Toast.makeText(context, "Количество товара \"" +product.getName()+ "\" увеличено в чеке",
                        Toast.LENGTH_SHORT).show();
                productInList = true;
                break;
            }
        }

        if (!productInList){
            product.setQuantity(1.000);
            products.add(product);
            Toast.makeText(context, "Товар \"" +product.getName()+ "\" добавлен в чек",
                    Toast.LENGTH_SHORT).show();
        }
        updateCheck();
    }

    //Товар обновляется в чеке
    //Если товар маркированный, то обновляются все одниковые товары в чеке
    public static void updateProduct(Context context, Product product){
        for (Product pr: products){
            if (pr.getId() == product.getId()){
                if (!pr.equalsWithOutMarkValue(product)){
                    pr.setName(product.getName());
                    pr.setBarcode(product.getBarcode());
                    pr.setPrice(product.getPrice());
                    pr.setMark(product.isMark());
                    pr.setUnit(product.getUnit());
                    pr.setQuantityStock(product.getQuantityStock());
                    pr.setTaxSystem(product.getTaxSystem());
                    pr.setTaxRate(product.getTaxRate());

                    Toast.makeText(context, "Товар \"" +product.getName()+ "\" обновлен в чеке",
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
        updateCheck();
    }

    public static void updateCheck() {
        double totalSum = 0;
        if (products.isEmpty()){
            cleanCheck();
        }else {
            for (Product product: products){
                countQuantityInStokeProduct(product);       //подсчет сумм и количества на складе
                countDiscountProduct(product);              //подсчет скидки
                countSumProduct(product);                   //подсчет суммы товара зависимой от величины скидки
                totalSum = totalSum + product.getSum();     //подсчет суммы к оплате
            }
            updateTotalCheck(totalSum);
        }
    }

    private static void countQuantityInStokeProduct(Product product){
        double quantity = product.getQuantity();
        double quantityInStoke = product.getQuantityStock();
        double resultQuantity;

        if (product.isMark()){
            quantity = 0;
            for (Product pr: products){
                if (pr.equalsWithOutMarkValue(product)){  //сравнение товаров без учета QR Code
                    quantity = quantity + 1;
                }
            }
        }
        resultQuantity = quantityInStoke - quantity;
        product.setQuantityStockChange(resultQuantity);
    }

    private static void countSumProduct(Product product){
        double price = product.getPrice();
        double quantity = product.getQuantity();
        double discount = product.getDiscount();
        double sum;

        sum = price * quantity - discount;
        product.setSum(sum);
    }

    private static void countDiscountProduct(Product product) {
        double price = product.getPrice();
        double quantity = product.getQuantity();
        double discountPercent = product.getDiscountPercent();

        double discount = discountPercent * price * quantity / 100;
        product.setDiscount(discount);
    }

    private static void cleanCheck() {
        check.clean();
    }

    private static void updateTotalCheck(double totalSum) {
        //Проверка типа расчета, null бывает при создании нового чека
        if (check.getSignCalculation() == null){
            check.setSignCalculation("приход");
        }

        //Товары
        check.setProductArrayList(products);

        //Завершение чека
        check.setTotal(totalSum);       //итоговая сумма без учета общей скидки на весь чек
        check.setPayment(totalSum - check.getDiscountCheck());  //оплата
    }

    public static void setAllPayment(double total, String typePayment, double cash,
                              double nonCash, double payment, double surrender) {
        check.setTotal(total);
        check.setTypePayment(typePayment);
        check.setCash(cash);
        check.setNonCash(nonCash);
        check.setPayment(payment);
        check.setSurrender(surrender);
    }

    public static void setDataTime(){
        Date dateNow = new Date();
        SimpleDateFormat formatForDateNow = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat formatForTimeNow = new SimpleDateFormat("HH:mm");
        check.setDate(formatForDateNow.format(dateNow));
        check.setTime(formatForTimeNow.format(dateNow));
    }
}
