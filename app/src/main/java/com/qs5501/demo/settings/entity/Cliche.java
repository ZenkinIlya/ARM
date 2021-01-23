package com.qs5501.demo.settings.entity;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Cliche{

    //Клише одно для всех пользователей поэтому все поля static

    private static String nameUser;            //имя пользователя
    private static String addressOfSettlement; //адресс расчета
    private static String placeOfSettlement;   //место расчета
    private static String advertising;         //реклама
    private static String nameOFD;             //наименование ОФД
    private static String websiteTaxAuthority; //сайт налогового органа
    private static String senderMail;          //электронная почта отправителя

    public static void save(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("nameUser", nameUser);
        editor.putString("addressOfSettlement", addressOfSettlement);
        editor.putString("placeOfSettlement", placeOfSettlement);
        editor.putString("advertising", advertising);
        editor.putString("nameOFD", nameOFD);
        editor.putString("websiteTaxAuthority", websiteTaxAuthority);
        editor.putString("senderMail", senderMail);
        editor.apply();
    }

    public static void load(Context context){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        nameUser = sharedPreferences.getString("nameUser", "");
        addressOfSettlement = sharedPreferences.getString("addressOfSettlement", "");
        placeOfSettlement = sharedPreferences.getString("placeOfSettlement", "");
        advertising = sharedPreferences.getString("advertising", "");
        nameOFD = sharedPreferences.getString("nameOFD", "");
        websiteTaxAuthority = sharedPreferences.getString("websiteTaxAuthority", "");
        senderMail = sharedPreferences.getString("senderMail", "");
    }

    public static String getNameUser() {
        return nameUser;
    }

    public static void setNameUser(String nameUser) {
        Cliche.nameUser = nameUser;
    }

    public static String getAddressOfSettlement() {
        return addressOfSettlement;
    }

    public static void setAddressOfSettlement(String addressOfSettlement) {
        Cliche.addressOfSettlement = addressOfSettlement;
    }

    public static String getPlaceOfSettlement() {
        return placeOfSettlement;
    }

    public static void setPlaceOfSettlement(String placeOfSettlement) {
        Cliche.placeOfSettlement = placeOfSettlement;
    }

    public static String getAdvertising() {
        return advertising;
    }

    public static void setAdvertising(String advertising) {
        Cliche.advertising = advertising;
    }

    public static String getNameOFD() {
        return nameOFD;
    }

    public static void setNameOFD(String nameOFD) {
        Cliche.nameOFD = nameOFD;
    }

    public static String getWebsiteTaxAuthority() {
        return websiteTaxAuthority;
    }

    public static void setWebsiteTaxAuthority(String websiteTaxAuthority) {
        Cliche.websiteTaxAuthority = websiteTaxAuthority;
    }

    public static String getSenderMail() {
        return senderMail;
    }

    public static void setSenderMail(String senderMail) {
        Cliche.senderMail = senderMail;
    }
}
