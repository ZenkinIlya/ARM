package com.qs5501.demo.utils;

import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.qs.wiget.App;

public class Printer {

    private static String str_massage;

    // Печатать текст
    public static void printText(final String str, final int size, final int align) {
        //Отправьте команду запроса принтера, если бумаги нет, принтер вернет сообщение об отсутствии бумаги.
        App.send(new byte[] { 0x1d, 0x61, 0x00 });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                str_massage = str;
                //Оцените, нет ли бумаги, распечатайте, нет ли недостатка в бумаге
                if(App.isCanprint){
                    App.printText(size, align, str_massage + "\n");
                }
            }
        },300);
    }

    // Распечатать штрих-код
    public static void printBarCode(String str, View view, final int align) {
        str_massage = str.trim();
        if (str_massage.length() <= 0)
            return;

        // Определите, может ли текущий символ генерировать штрих-код
        if (str_massage.getBytes().length > str_massage.length()) {
            Snackbar.make(view, "Текущие данные не могут генерировать одномерные коды", Snackbar.LENGTH_LONG);
            return;
        }

        //Отправьте команду запроса принтера, если бумаги нет, принтер вернет сообщение об отсутствии бумаги.
        App.send(new byte[] { 0x1d, 0x61, 0x00 });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Оцените, нет ли бумаги, распечатайте, нет ли недостатка в бумаге
                if(App.isCanprint){
                    App.printBarCode(align, 380, 100, str_massage);
                }
            }
        },200);
    }

    // Распечатать QR-код
    public static void printQrCode(String str, final int align) {
        str_massage = str.trim();
        if (str_massage.length() <= 0)
            return;
        //Отправьте команду запроса принтера, если бумаги нет, принтер вернет сообщение об отсутствии бумаги.
        App.send(new byte[] { 0x1d, 0x61, 0x00 });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Оцените, нет ли бумаги, распечатайте, нет ли недостатка в бумаге
                if(App.isCanprint){
                    App.printQRCode(align, 250, 250,str_massage);
                }
            }
        },200);
    }
}
