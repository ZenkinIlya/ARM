package com.qs5501.demo;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.Toast;

import com.qs.wiget.App;
import com.qs5501demo.aidl.R;

public class MainPrinterActivity extends AppCompatActivity {

    private ScanBroadcastReceiver scanBroadcastReceiver;

    private EditText tv;

    String str_massage;

/*    //Создание в правом верхнем углу меню опций
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the main_menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.item1:
                str_massage = tv.getText().toString();
                printeText(str_massage);
                break;
            case R.id.item2:
                //your action
                break;
            default:
                return super.onOptionsItemSelected(item);
        }

        return true;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initView();

        scanBroadcastReceiver = new ScanBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("com.qs.scancode");
        this.registerReceiver(scanBroadcastReceiver, intentFilter);

    }

    /**
     * Инициализация управления и мониторинг кнопок
     */
    private void initView() {
        // TODO Auto-generated method stub
        tv = (EditText) findViewById(R.id.tv);
        // сканирование
        findViewById(R.id.btn_scan1).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub

                App.openScan();

            }
        });

        // Печатать текст
        findViewById(R.id.btn_printText).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        str_massage = tv.getText().toString();
                        printeText(str_massage);
                    }
                });

        // Распечатать одномерный код
        findViewById(R.id.btn_printBarcode).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        printBarCode();
                    }
                });

        // Распечатать QR-код
        findViewById(R.id.btn_printQRcode).setOnClickListener(
                new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        printQrCode();
//						showDialog_qr();
                    }
                });

        // Сохранить контент
        findViewById(R.id.btn_save).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                saveData();
            }
        });

        // Получите разрешение. Эта операция требуется на компьютерах с Android 6.0 или более поздних
        // версий, в противном случае SD-карта не может быть прочитана или записана.
//		verifyStoragePermissions(this);
    }

    // Печатать текст
    private void printeText(final String str) {

        //Отправьте команду запроса принтера, если бумаги нет, принтер вернет сообщение об отсутствии бумаги.
        App.send(new byte[] { 0x1d, 0x61, 0x00 });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Оцените, нет ли бумаги, распечатайте, нет ли недостатка в бумаге
                if(App.isCanprint){
                    App.printText(1, 0, str + "\n");
                }
            }
        },300);
    }

    // Распечатать штрих-код
    private void printBarCode() {
        // Получите строку в поле редактирования
        str_massage = tv.getText().toString().trim();
        if (str_massage == null || str_massage.length() <= 0)
            return;

        // Определите, может ли текущий символ генерировать штрих-код
        if (str_massage.getBytes().length > str_massage.length()) {
            Toast.makeText(MainPrinterActivity.this, "当前数据不能生成一维码",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        //Отправьте команду запроса принтера, если бумаги нет, принтер вернет сообщение об отсутствии бумаги.
        App.send(new byte[] { 0x1d, 0x61, 0x00 });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Оцените, нет ли бумаги, распечатайте, нет ли недостатка в бумаге
                if(App.isCanprint){
                    App.printBarCode(0, 380, 100, str_massage);

                }

            }
        },200);

    }

    // Распечатать QR-код
    private void printQrCode() {
        // Получите строку в поле редактирования
        str_massage = tv.getText().toString().trim();
        if (str_massage == null || str_massage.length() <= 0)
            return;
        //Отправьте команду запроса принтера, если бумаги нет, принтер вернет сообщение об отсутствии бумаги.
        App.send(new byte[] { 0x1d, 0x61, 0x00 });

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //Оцените, нет ли бумаги, распечатайте, нет ли недостатка в бумаге
                if(App.isCanprint){
                    App.printQRCode(1, 250, 250,str_massage);
                }
            }
        },200);
    }

    // Сохранить контент на SD-карту
    private void saveData() {
        // Получите строку в поле редактирования
        str_massage = tv.getText().toString().trim();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        //Получить текущее время
        Date date = new Date(System.currentTimeMillis());
        // Прочтите сохраненные данные
        String str = readSDFile();
        // Сохранение данных
        saveDada2SD(str + "\n" + str_massage+"   "+simpleDateFormat.format(date));
    }

    /**
     *
     * Чтение текстовых файлов на SD-карте
     *
     * @param
     *
     * @return
     */
    @SuppressWarnings("resource")
    public String readSDFile() {
        try {
            File file = new File("/mnt/sdcard/pro.txt");
            FileInputStream is = new FileInputStream(file);
            byte[] b = new byte[is.available()];
            is.read(b);
            String result = new String(b);
            System.out.println("读取成功：" + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // Сохранить на SD-карту
    public void saveDada2SD(String sb) {
        String filePath = null;
        boolean hasSDCard = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (hasSDCard) { // Hello.text в корневом каталоге SD-карты
            filePath = "/mnt/sdcard/pro.txt";
        }
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                File dir = new File(file.getParent());
                dir.mkdirs();
                file.createNewFile();
            }
            FileOutputStream fileOut = null;
            BufferedOutputStream writer = null;
            OutputStreamWriter outputStreamWriter = null;
            BufferedWriter bufferedWriter = null;
            try {
                fileOut = new FileOutputStream(file);
                writer = new BufferedOutputStream(fileOut);
                outputStreamWriter = new OutputStreamWriter(writer, "UTF-8");
                bufferedWriter = new BufferedWriter(outputStreamWriter);
                bufferedWriter.write(new String(sb.toString()));
                bufferedWriter.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "保存成功，请到SD卡查看,文件名为pro.txt", Toast.LENGTH_SHORT)
                .show();
    }

//	// Проверьте разрешения на чтение и запись
//	public void verifyStoragePermissions(Activity activity) {
//		try {
//			// Проверьте, есть ли разрешение на запись
//			int permission = ActivityCompat.checkSelfPermission(activity,
//					"android.permission.WRITE_EXTERNAL_STORAGE");
//			if (permission != PackageManager.PERMISSION_GRANTED) {
//				// Нет разрешения на запись, подайте заявку на разрешение на запись, появится диалоговое окно
//				ActivityCompat.requestPermissions(activity,
//						PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

    class ScanBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            //Получить данные сканирования
            String text1 = intent.getExtras().getString("code");

            String str1 = tv.getText().toString();
            tv.setText(str1.trim()+"\n"+text1.trim());
            CharSequence text = tv.getText();
            if (text instanceof Spannable) {
                Spannable spanText = (Spannable) text;
                Selection.setSelection(spanText, text.length());
            }
        }
    }

    void showDialog_qr(){
        final EditText et = new EditText(MainPrinterActivity.this);
        et.setInputType(InputType.TYPE_CLASS_NUMBER);
        et.setText("1");
        AlertDialog.Builder ab = new AlertDialog.Builder(
                MainPrinterActivity.this);
        ab.setTitle("Введите количество напечатанных QR-кодов")
                .setView(et)
                .setPositiveButton("подтверждения",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                // TODO Auto-generated method stub

                                String str=et.getText()
                                        .toString().trim();

                                if(str.length()<=0){
                                    Toast.makeText(getApplicationContext(), "Пожалуйста, введите количество отпечатков", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                int a = Integer.valueOf(et.getText()
                                        .toString());

                                for (int i = 0; i < a; i++) {
                                    printQrCode();
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
        ab.create().show();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        // Включите черную метку по умолчанию
//		App.send(new byte[] { 0x1F, 0x1B, 0x1F, (byte) 0x80, 0x04,
//				0x05, 0x06, 0x44 });
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Toast.makeText(getApplicationContext(), "Закрыть приложение", Toast.LENGTH_SHORT).show();
        unregisterReceiver(scanBroadcastReceiver);
        App.closeCommonApi();
        System.exit(0);
        super.onDestroy();
    }

}
