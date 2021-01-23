package com.qs5501.demo.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class JsonHelper<T> {

    private String FILE_NAME;
    private T items;
    private TypeToken<T> typeToken;
    private TypeToken<ArrayList<T>> typeTokenArray;
    private int mode;

    public JsonHelper(String FILE_NAME, TypeToken<T> typeToken, int mode) {
        this.FILE_NAME = FILE_NAME;
        this.typeToken = typeToken;
        this.mode = mode;
    }

    public JsonHelper(String FILE_NAME, TypeToken<ArrayList<T>> typeTokenArray) {
        this.FILE_NAME = FILE_NAME;
        this.typeTokenArray = typeTokenArray;
    }

    public boolean createJson(Context context){
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = context.openFileInput(FILE_NAME);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public boolean deleteJson(Context context){
        return context.deleteFile(FILE_NAME);
    }

    public boolean exportToJSON(Context context, T dataList) {

        Gson gson = new Gson();
        items = dataList;
        String jsonString = gson.toJson(items);

        FileOutputStream fileOutputStream = null;

        try {
            //MODE_PRIVATE - режим по умолчанию, в котором к созданному файлу может получить доступ
            //              только вызывающее приложение (или все приложения с одним и тем же идентификатором пользователя)
            //MODE_APPEND - если файл уже существует, записывать данные в конец существующего файла вместо его удаления
            fileOutputStream = context.openFileOutput(FILE_NAME, mode);
            fileOutputStream.write(jsonString.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }

    public T importFromJSON(Context context) {

        InputStreamReader streamReader = null;
        FileInputStream fileInputStream = null;
        try{
            fileInputStream = context.openFileInput(FILE_NAME);
            streamReader = new InputStreamReader(fileInputStream);
            Gson gson = new Gson();
            items = gson.fromJson(streamReader, typeToken.getType());
            return items;
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        finally {
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


//
    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        return sb.toString();
    }
    public static String getStringFromFile(String filePath) throws Exception {
        File fl = new File(filePath);
        FileInputStream fin = new FileInputStream(fl);
        String ret = convertStreamToString(fin);
        //Make sure you close all streams.
        fin.close();
        return ret;
    }
    public static void writeJsonFile(File file, String json) {
        BufferedWriter bufferedWriter = null;
        try {

            if (!file.exists()) {
                Log.e("App","file not exist");
                file.createNewFile();
            }

            FileWriter fileWriter = new FileWriter(file);
            bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(json);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedWriter != null) {
                    bufferedWriter.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }
    //Запись в JSON { "check":[{},{}...] }
    public boolean exportToJSONArray(Context context, T object, String nameArray) {
        File fileJson = new File(context.getFilesDir().getAbsolutePath() + "/" + FILE_NAME);
        String jsonString;
        JSONObject jsonObject;
        JSONArray jsonArray;
        try {
            jsonString = JsonHelper.getStringFromFile(fileJson.toString());
            if (jsonString.equals("")){
                jsonString = "{}";
                jsonObject = new JSONObject(jsonString);
                jsonArray = new JSONArray();
            }else{
                jsonObject = new JSONObject(jsonString);
                jsonArray = jsonObject.getJSONArray(jsonString);
            }
            jsonArray.put(object.toString());
            jsonObject.put("checks", jsonArray);
            JsonHelper.writeJsonFile(fileJson.getAbsoluteFile(), jsonObject.toString());

            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }




    //Обновление JSON [{},{}...]
    public boolean saveObjectInJSON(Context context, T object) {
        InputStreamReader streamReader = null;
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        ArrayList<T> tArrayList = new ArrayList<>();
        Gson gson = new Gson();

        //Чтение данных в список
        try{
            fileInputStream = context.openFileInput(FILE_NAME);
            streamReader = new InputStreamReader(fileInputStream);
            tArrayList = gson.fromJson(streamReader, typeTokenArray.getType());
        }
        catch (IOException ex){
            ex.printStackTrace();
        }
        finally {
            if (streamReader != null) {
                try {
                    streamReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //Добавление объекта в считанный список
        tArrayList.add(object);

        //Запись в JSON обновленного списка
        String jsonString = gson.toJson(tArrayList);
        try {
            fileOutputStream = context.openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            fileOutputStream.write(jsonString.getBytes());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
