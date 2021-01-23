package com.qs5501.demo.sale.payment.complete;

import android.content.Context;
import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.qs5501.demo.sale.CheckUpdater;
import com.qs5501.demo.sale.entity.Check;
import com.qs5501.demo.utils.JsonHelper;

import java.util.ArrayList;

public class CheckCompletionHandler {

    private static JsonHelper<Check> jsonHelper;

    //контекст, итог, тип оплаты, наличные, безналичные, оплата, сдача
    public static boolean checkCompletion(Context context, double total, String typePayment, double cash,
                                    double nonCash, double payment, double surrender){

        //Установка всех итоговых значений
        //итог, тип оплаты, наличные, безналичные, оплата, сдача
        CheckUpdater.setAllPayment(total, typePayment,
                cash, nonCash, payment, surrender);

        //Установка даты и времени
        CheckUpdater.setDataTime();

        //TODO Обращение к фискалке, печатке
        //Получаем из фискалки парметры

        //Если все прошло без ошибок, сохраняем чек

        //Сохранение чека в КЛ
        boolean result;

        jsonHelper = new JsonHelper<Check>("KL.json", new TypeToken<ArrayList<Check>>(){});
        jsonHelper.createJson(context);
        result = jsonHelper.saveObjectInJSON(context, CheckUpdater.getCheck());
        if (result){
            Log.d("SaveCheck", "чек успешно записан в КЛ");
        }else {
            Log.d("SaveCheck", "не удалось записать чек в КЛ");
        }
        return result;
    }
}
