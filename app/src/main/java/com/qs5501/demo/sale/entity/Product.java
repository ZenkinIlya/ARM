package com.qs5501.demo.sale.entity;

import java.io.Serializable;
import java.util.Objects;

public class Product implements Serializable, Cloneable {

    private int id;                     //id (необходимо для SQLite базы)
    private String name;                //наименование
    private long  barcode;              //штрих-код
    private double price;               //цена
    private boolean mark;               //признак марикровки
    private String markValue;           //значение маркировки
    private String unit;                //еденицы измерения
    private double quantityStock;       //количество на складе
    private int taxSystem;              //СНО
    private int taxRate;                //ставка налога

    //изменяемое
    private double quantity;            //количество
    private double quantityStockChange; //изменяемое количество на складе
    private byte methodOfCalculation;   //Способ расчета
    private double discount;            //скидка
    private double discountPercent;     //скидка в процентах
    private double priceWithDiscount;   //цена за еденицу с учетом скидки
    private double sum;                 //сумма

    public Product(){}

    public Product(String name, int barcode, float price, boolean mark, String markValue,
                   String unit, float quantityStock, int taxSystem, int taxRate) {
        this.name = name;
        this.barcode = barcode;
        this.price = price;
        this.mark = mark;
        this.markValue = markValue;
        this.unit = unit;
        this.quantityStock = quantityStock;
        this.taxSystem = taxSystem;
        this.taxRate = taxRate;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public byte getMethodOfCalculation() {
        return methodOfCalculation;
    }

    public void setMethodOfCalculation(byte methodOfCalculation) {
        this.methodOfCalculation = methodOfCalculation;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getBarcode() {
        return barcode;
    }

    public void setBarcode(long barcode) {
        this.barcode = barcode;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isMark() {
        return mark;
    }

    public void setMark(boolean mark) {
        this.mark = mark;
    }

    public String getMarkValue() {
        return markValue;
    }

    public void setMarkValue(String markValue) {
        this.markValue = markValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public double getQuantityStock() {
        return quantityStock;
    }

    public void setQuantityStock(double quantityStock) {
        this.quantityStock = quantityStock;
    }

    public int getTaxSystem() {
        return taxSystem;
    }

    public void setTaxSystem(int taxSystem) {
        this.taxSystem = taxSystem;
    }

    public int getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(int taxRate) {
        this.taxRate = taxRate;
    }

    public double getQuantityStockChange() {
        return quantityStockChange;
    }

    public void setQuantityStockChange(double quantityStockConst) {
        this.quantityStockChange = quantityStockConst;
    }

    public double getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }

    public double getPriceWithDiscount() {
        return priceWithDiscount;
    }

    public void setPriceWithDiscount(double priceWithDiscount) {
        this.priceWithDiscount = priceWithDiscount;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", barcode=" + barcode +
                ", price=" + price +
                ", mark=" + mark +
                ", markValue='" + markValue + '\'' +
                ", unit='" + unit + '\'' +
                ", quantityStock=" + quantityStock +
                ", taxSystem=" + taxSystem +
                ", taxRate=" + taxRate +
                ", quantity=" + quantity +
                ", quantityStockChange=" + quantityStockChange +
                ", methodOfCalculation=" + methodOfCalculation +
                ", discount=" + discount +
                ", discountPercent=" + discountPercent +
                ", priceWithDiscount=" + priceWithDiscount +
                ", sum=" + sum +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id &&
                barcode == product.barcode &&
                Double.compare(product.price, price) == 0 &&
                Double.compare(product.priceWithDiscount, priceWithDiscount) == 0 &&
                mark == product.mark &&
                taxSystem == product.taxSystem &&
                taxRate == product.taxRate &&
                Objects.equals(name, product.name) &&
                Objects.equals(markValue, product.markValue) &&
                Objects.equals(unit, product.unit);
    }

    public boolean equalsWithOutMarkValue(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return id == product.id &&
                barcode == product.barcode &&
                Double.compare(product.price, price) == 0 &&
                Double.compare(product.priceWithDiscount, priceWithDiscount) == 0 &&
                mark == product.mark &&
                taxSystem == product.taxSystem &&
                taxRate == product.taxRate &&
                Objects.equals(name, product.name) &&
                Objects.equals(unit, product.unit);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, barcode, price, mark, markValue, unit, taxRate, taxSystem);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
