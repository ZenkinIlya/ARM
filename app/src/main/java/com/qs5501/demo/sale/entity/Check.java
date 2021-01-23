package com.qs5501.demo.sale.entity;

import java.io.Serializable;
import java.util.ArrayList;

public class Check implements Serializable {

//    Шапка чека
    private String typeCheck;       //тип чека
    private String signCalculation; //тип расчета (приход/расход)

//    Товары
    private ArrayList<Product> productArrayList;

//    Завершение чека
    private double discountCheck;   //скидка на весь чек
    private double total;           //итог
    private String typePayment;     //тип оплаты
    private double cash;            //наличные
    private double nonCash;         //безналичные
    private double payment;         //оплата
    private double surrender;       //сдача

//    Инфо
    private String date;
    private String time;
    private int numShift;           //номер смены
    private int numCheck;           //номер чека

    public Check(){};

    public Check(String typeCheck, String signCalculation, ArrayList<Product> productArrayList,
                 double discountCheck, double total, String typePayment, double cash, double nonCash,
                 double payment, double surrender, String date, String time, int numShift, int numCheck) {
        this.typeCheck = typeCheck;
        this.signCalculation = signCalculation;
        this.productArrayList = productArrayList;
        this.discountCheck = discountCheck;
        this.total = total;
        this.typePayment = typePayment;
        this.cash = cash;
        this.nonCash = nonCash;
        this.payment = payment;
        this.surrender = surrender;
        this.date = date;
        this.time = time;
        this.numShift = numShift;
        this.numCheck = numCheck;
    }

    public String getTypeCheck() {
        return typeCheck;
    }

    public void setTypeCheck(String typeCheck) {
        this.typeCheck = typeCheck;
    }

    public String getSignCalculation() {
        return signCalculation;
    }

    public void setSignCalculation(String signCalculation) {
        this.signCalculation = signCalculation;
    }

    public ArrayList<Product> getProductArrayList() {
        return productArrayList;
    }

    public void setProductArrayList(ArrayList<Product> productArrayList) {
        this.productArrayList = productArrayList;
    }

    public double getDiscountCheck() {
        return discountCheck;
    }

    public void setDiscountCheck(double discountCheck) {
        this.discountCheck = discountCheck;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getTypePayment() {
        return typePayment;
    }

    public void setTypePayment(String typePayment) {
        this.typePayment = typePayment;
    }

    public double getCash() {
        return cash;
    }

    public void setCash(double cash) {
        this.cash = cash;
    }

    public double getNonCash() {
        return nonCash;
    }

    public void setNonCash(double nonCash) {
        this.nonCash = nonCash;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public double getSurrender() {
        return surrender;
    }

    public void setSurrender(double surrender) {
        this.surrender = surrender;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getNumShift() {
        return numShift;
    }

    public void setNumShift(int numShift) {
        this.numShift = numShift;
    }

    public int getNumCheck() {
        return numCheck;
    }

    public void setNumCheck(int numCheck) {
        this.numCheck = numCheck;
    }

    public void clean(){
        this.productArrayList = new ArrayList<>();
        this.discountCheck = 0.0;
        this.total = 0.0;
        this.typePayment = null;
        this.cash = 0.0;
        this.nonCash = 0.0;
        this.payment = 0.0;
        this.surrender = 0.0;
        this.date = null;
        this.time = null;
        this.numShift = 0;
        this.numCheck = 0;
    }

    @Override
    public String toString() {
        return "Check{" +
                "typeCheck='" + typeCheck + '\'' +
                ", signCalculation='" + signCalculation + '\'' +
                ", productArrayList=" + productArrayList +
                ", discountCheck=" + discountCheck +
                ", total=" + total +
                ", typePayment=" + typePayment +
                ", cash=" + cash +
                ", nonCash=" + nonCash +
                ", payment=" + payment +
                ", surrender=" + surrender +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", numShift=" + numShift +
                ", numCheck=" + numCheck +
                '}';
    }
}
