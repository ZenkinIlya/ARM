package com.qs5501.demo.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

import static java.lang.Math.max;
import static java.lang.Math.min;

public class MoneyTextWatcher implements TextWatcher {

    private EditText editText;
    private String mask;
    private String lastAmount = "";

    private int lastCursorPosition = -1;

    public MoneyTextWatcher(EditText editText, String mask) {
        super();
        this.editText = editText;
        this.mask = mask;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (!s.toString().equals(lastAmount)) {

            String cleanString = clearCurrencyToNumber(s.toString());

            try {

                String formattedAmount = transformToCurrency(cleanString);
                editText.removeTextChangedListener(this);
                editText.setText(formattedAmount);
                editText.setSelection(formattedAmount.length());
                editText.addTextChangedListener(this);

                if (lastCursorPosition != lastAmount.length() && lastCursorPosition != -1) {
                    int lengthDelta = formattedAmount.length() - lastAmount.length();
                    int newCursorOffset = max(0, min(formattedAmount.length(), lastCursorPosition + lengthDelta));
                    editText.setSelection(newCursorOffset);
                }
            } catch (Exception e) {
                //log something
            }
        }
    }

    private String clearCurrencyToNumber(String currencyValue) {
        String result = null;

        if (currencyValue == null) {
            result = "";
        } else {
            result = currencyValue.replaceAll("[(a-z)|(A-Z)|($,. )]", "");
        }
        return result;
    }

    private boolean isCurrencyValue(String currencyValue, boolean podeSerZero) {
        boolean result;

        if (currencyValue == null || currencyValue.length() == 0) {
            result = false;
        } else {
            if (!podeSerZero && currencyValue.equals("0,00")) {
                result = false;
            } else {
                result = true;
            }
        }
        return result;
    }

    private String transformToCurrency(String value) {
        double parsed = Double.parseDouble(value);
        DecimalFormat myFormatter = new DecimalFormat(mask);
        String formatted;
        switch (mask){
            case "##0.00":{
                formatted = myFormatter.format((parsed / 100));
                break;
            }
            case "##0.000":{
                formatted = myFormatter.format((parsed / 1000));
                break;
            }
            default:{
                formatted = myFormatter.format((parsed / 100));
            }
        }
        formatted = formatted.replaceAll("[^(0-9)(.,)]", "");
        formatted = formatted.replace(',','.');
        return formatted;
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
