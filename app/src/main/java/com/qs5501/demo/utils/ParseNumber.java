package com.qs5501.demo.utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

public class ParseNumber {

    public static String parseNumber(String pattern, Double value){
        DecimalFormat numberFormat = new DecimalFormat(pattern);
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols();
        decimalFormatSymbols.setDecimalSeparator('.');
        numberFormat.setDecimalFormatSymbols(decimalFormatSymbols);
        String s = numberFormat.format(value);
        return numberFormat.format(value);
    }

}
