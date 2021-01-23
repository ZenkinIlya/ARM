package com.qs5501.demo.utils;

import android.support.design.widget.TextInputEditText;

import java.util.Objects;

public class CheckComponent {

    private static boolean correctData;

    public static void setCorrectData(boolean correctData) {
        CheckComponent.correctData = correctData;
    }

    public static boolean isCorrectData() {
        return correctData;
    }

    private static boolean checkLength(String s, TextInputEditText editText){
        if (s.length() == 0){
            editText.setError("Заполните поле");
            return false;
        }else return true;
    }

    public static void checkEdits(TextInputEditText editText){
        String text = Objects.requireNonNull(editText.getText().toString());

        if (!checkLength(text, editText)){
            correctData = false;
        }
    }

    public static void checkBarcode(TextInputEditText editText){
        String text = Objects.requireNonNull(editText.getText().toString());

        if (!checkLength(text, editText)){
            correctData = false;
        }else {
            if (verifyBarcode(text)){
                editText.setError(null);
            }else{
                editText.setError("Неверный формат штрих-кода");
                correctData = false;
            }
        }
    }

    private static boolean verifyBarcode(String text) {
        int sumChet = 0, sumNeChet = 0, a, ks;

        if (text.length() == 8 || text.length() == 13){
            for (int i = 1; i <= text.length() - 1; i++){
                a = Integer.parseInt(String.valueOf(text.charAt(i - 1)));
                if (text.length() == 8){
                    if (i % 2 == 0){  //если четное
                        sumChet = sumChet + a;
                    }else {
                        sumNeChet = sumNeChet + a * 3;
                    }
                }
                if (text.length() == 13){
                    if (i % 2 == 0){  //если четный
                        sumChet = sumChet + a * 3;
                    }else {
                        sumNeChet = sumNeChet + a;
                    }
                }
            }
            ks = ((sumNeChet + sumChet) / 10 + 1) * 10 - (sumNeChet + sumChet);
            return ks == Integer.parseInt(String.valueOf(text.charAt(text.length() - 1))) || ks == 10;
        }else return false;
    }

}
