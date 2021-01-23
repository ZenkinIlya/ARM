package com.qs5501.demo.utils;

public class SpinnerSelectionHelper {

    public static int getPosition(String input, String[] stringArray){
        int position = 0;
        for (String string: stringArray){
            if (input.equals(string)){
                return position;
            }
            position++;
        }
        return position = 0;
    }
}
