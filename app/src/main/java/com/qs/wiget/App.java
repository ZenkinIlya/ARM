package com.qs.wiget;

import java.io.UnsupportedEncodingException;
import java.util.Hashtable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.Layout.Alignment;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.widget.Toast;
import android.zyapi.CommonApi;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.qs5501demo.aidl.R;

/**
 * Класс приложения, обратите внимание, что только переменная mCommonApi может использоваться во всем коде,
 * и mCommonApi не может быть повторно создан, иначе будет задержка печати или печать не будет.
 * Сканирование будет сканироваться дважды, или лампа сканирования не будет выходить, обратите внимание
 * 
 * @author wsl
 * 
 */
public class App extends Application {

	private static int pin_L = 84;// 5501L
	private static int pin_H = 68;// 5501H
	
	public static int pin = 68;// 5501H
	
	private static String mCurDev1 = "";

	private static int mComFd = -1;
	static CommonApi mCommonApi;

	static App instance = null;

	public static boolean isCanprint = false;

	public static boolean isCanSend = true;

	public static boolean temHigh = false;

	private final int MAX_RECV_BUF_SIZE = 1024;
	private boolean isOpen = false;
	private MediaPlayer player;
	private final static int N0PAPER = -1;
	private final static int SHOW_RECV_DATA = 1;
	private byte[] recv;
	private String strRead;
	public static boolean isScanDomn = false;
	// GreenOnReceiver greenOnReceiver;

	public static StringBuffer sb1 = new StringBuffer();
	
	static Handler handler1 = new Handler();

	// Контроль кнопки SCAN
	private ScanBroadcastReceiver scanBroadcastReceiver;

	Handler h;

	public App() {
		super.onCreate();
		instance = this;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		// mDb = Database.getInstance(this);
		// начальная
		init();

		// Создать экземпляр MediaPlayer
		player = MediaPlayer.create(getApplicationContext(), R.raw.beep);
	}

	public void init() {

		openGPIO();
		initGPIO();

		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mComFd > 0) {
					open();
					isOpen = true;
					readData();
					// Отключить черную метку по умолчанию
					App.send(new byte[] { 0x1F, 0x1B, 0x1F, (byte) 0x80, 0x04,
							0x05, 0x06, 0x66 });
					//Включите черную метку по умолчанию
//					App.send(new byte[] { 0x1F, 0x1B, 0x1F, (byte) 0x80, 0x04,
//							0x05, 0x06, 0x44 });
				} else {
					isOpen = false;
				}
			}
		}, 2000);

		// Пользовательский интерфейс обновления обработчика использования
		h = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				if (msg.what == 0x123) {
					if (msg.obj != null) {
						String str = "" + msg.obj;
						if (!str.trim().contains("##56")) {
						if (!str.trim().contains("##55")) {
							if (!str.trim().equals("start")) {

								player.start();

								Intent intentBroadcast = new Intent();
								intentBroadcast.setAction("com.qs.scancode");

								intentBroadcast.putExtra("code", str.trim());

								sendBroadcast(intentBroadcast);
								
								if(pin==pin_L){
								//Закройте сканирующую головку
								mCommonApi.setGpioDir(75, 1);
								mCommonApi.setGpioOut(75, 0);
								}

							}
						}
					}
				}
				}
			}
		};

		scanBroadcastReceiver = new ScanBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction("ismart.intent.scandown");
		this.registerReceiver(scanBroadcastReceiver, intentFilter);
		
	}

	/**
	 * Читать поток данных
	 */
	private void readData() {
		new Thread() {
			public void run() {
				while (isOpen) {
					int ret = 0;
					byte[] buf = new byte[MAX_RECV_BUF_SIZE + 1];
					ret = mCommonApi.readComEx(mComFd, buf, MAX_RECV_BUF_SIZE,
							0, 0);
					if (ret <= 0) {
						Log.d("", "read failed!!!! ret:" + ret);
						try {
							sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						continue;
					} else {
						// Log.e("", "1read success:");
					}
					recv = new byte[ret];
					System.arraycopy(buf, 0, recv, 0, ret);

					try {
						strRead = new String(recv, "UTF-8");
						// Если есть искаженный код, это означает, что кодировка неправильная, переключитесь на GBK
						if (strRead.contains("�")) {
							strRead = new String(recv, "GBK");
						}
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if (strRead != null) {
						Message msg = handler.obtainMessage(SHOW_RECV_DATA);
						msg.obj = strRead;
						msg.sendToTarget();
					}

					String str1 = byteToString(buf, ret);
					Log.e("===","str====="+str1);
					if (str1.contains("14 00 0C 0F")) {
						isCanprint = false;
						Message msg = handler.obtainMessage(N0PAPER);
						msg.sendToTarget();
						Log.e("===","1str====="+str1);
					} else {
						isCanprint = true;
					}
				}
			}
		}.start();
	}

	boolean iscanScan = false;
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case SHOW_RECV_DATA:
				String barCodeStr1 = (String) msg.obj;
				Log.e("", "1read success:" + barCodeStr1);
				if (barCodeStr1.trim() != "") {
					if (isOpen) {
						if (!barCodeStr1.trim().contains("")) {
						if (!barCodeStr1.trim().contains("##55")) {
							if (!barCodeStr1.trim().equals("start")) {
								if (barCodeStr1.trim().length() != 0) {

									Message m = new Message();
									m.what = 0x123;
									m.obj = barCodeStr1;
									h.sendMessage(m);

								}

							}
						}
						}
					}
				}
				break;
				case N0PAPER:
                  Toast.makeText(getApplicationContext(),"no paper",Toast.LENGTH_SHORT).show();
					break;
			}

		};
	};

	int num = 1;
	Handler mHanlder = new Handler();
	Runnable run_getData = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			if (num > 1) {
				num = 1;
				mHanlder.removeCallbacks(run_getData);
				Message m = new Message();
				m.what = 0x123;
				Log.e("iiiiiii", "Отправить запрос GET");
				try {
					m.obj = sb1.toString();
					Log.e("возвращенные сообщения：", "" + m.obj);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				h.sendMessage(m);
			} else {
				num++;
				mHanlder.postDelayed(run_getData, 100);
			}
		}
	};

	// Войдите в приложение, чтобы поднять 55 и 56 футов
	public static void open() {
		/**
         * 1. Поднимите уровень контактов 55 и 56 (APP-> Printer) 1B 23 23 XXXX, где XXXX - код ASCII: 56UP - 1B 23 23
         * 35 36 55 50 Микроконтроллер получил 55, 56 контактный уровень
		 */
		// Заходи и поднимай 55 и 56 футов
		App.send(new byte[] { 0x1B, 0x23, 0x23, 0x35, 0x36, 0x55, 0x50 });

		if(pin==pin_L){
		// 74 подтягивания вверх, 75 опусканий
		mCommonApi.setGpioDir(74, 1);
		mCommonApi.setGpioOut(74, 1);
		mCommonApi.setGpioDir(75, 1);
		mCommonApi.setGpioOut(75, 0);
		}

	}

	// Выполните сканирование, то есть опустите вниз 74 и 75 футов, а затем потяните вверх.
	public static void openScan() {
		/**
		 * 3. Потяните вниз уровень контакта 55 (APP-> Printer) 1B 23 23 XXXX, где XXXX - это код ASCII: 55DN - 1B 23 23 35.
         * * 35 44 4E
		 */
		// Отправить команду сканирования
		App.send(new byte[] { 0x1B, 0x23, 0x23, 0x35, 0x35, 0x44, 0x4E });
		
		if(pin==pin_L){
			// Вытяните порт GPIO
			handler1.postDelayed(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					// Потяните вниз порт GPIO, чтобы загорелась сканирующая головка.
					mCommonApi.setGpioDir(75, 1);
					mCommonApi.setGpioOut(75, 1);

				}
			}, 50);
		}
	}

//	static boolean isScan = false;
//	static Handler handler1 = new Handler();
//	static Runnable run1 = new Runnable() {
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//				// Принудительно закройте сканирующую головку
//				mCommonApi.setGpioDir(74, 1);
//				mCommonApi.setGpioOut(74, 0);
//				mCommonApi.setGpioDir(75, 1);
//				mCommonApi.setGpioOut(75, 0);
//
//				isScan = true;
//		}
//	};

	public static App getInstance() {
		if (instance == null) {
			instance = new App();
		}
		return instance;
	}

	public String getCurDevice() {
		return mCurDev1;
	}

	public static void setCurDevice(String mCurDev) {
		mCurDev1 = mCurDev;
	}

	// Ссылки на переменные mCommonApi в другом месте
	public static CommonApi getCommonApi() {
		return mCommonApi;
	}

	public static void initGPIO() {
		// TODO Auto-generated method stub
		
		mComFd = mCommonApi.openCom("/dev/ttyMT1", 115200, 8, 'N', 1);

		if (mComFd > 0) {
			Toast.makeText(instance, "init success", Toast.LENGTH_SHORT).show();
		}
	}

	public static void openGPIO() {

		mCommonApi = new CommonApi();
		
//		mCommonApi.setGpioDir(84, 0);
//		mCommonApi.getGpioIn(84);
//
//		mCommonApi.setGpioDir(84, 1);
//		mCommonApi.setGpioOut(84, 1);
		
		  if(Build.MODEL.equalsIgnoreCase("5501H")) {
			   pin=pin_L;
			} else {
				pin=pin_H;
			}
			
			mCommonApi.setGpioDir(pin,0);
			mCommonApi.getGpioIn(pin);

			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					mCommonApi.setGpioDir(pin, 1);
					mCommonApi.setGpioOut(pin, 1);

				}
			}, 1000);
		
		
	}

	static Handler mHandler = new Handler();
	Runnable mRun = new Runnable() {
		@Override
		public void run() {
			// TODO Auto-generated method stub
			isCanSend = true;
		}
	};

	public String byteToString(byte[] b, int size) {
		byte high, low;
		byte maskHigh = (byte) 0xf0;
		byte maskLow = 0x0f;

		StringBuffer buf = new StringBuffer();

		for (int i = 0; i < size; i++) {
			high = (byte) ((b[i] & maskHigh) >> 4);
			low = (byte) (b[i] & maskLow);
			buf.append(findHex(high));
			buf.append(findHex(low));
			buf.append(" ");
		}
		return buf.toString();
	}

	private char findHex(byte b) {
		int t = new Byte(b).intValue();
		t = t < 0 ? t + 16 : t;
		if ((0 <= t) && (t <= 9)) {
			return (char) (t + '0');
		}
		return (char) (t - 10 + 'A');
	}

	/**
	 * Проверить, можно ли преобразовать строку в число
	 * 
	 * @param str
	 *            строка
	 * @return true 'жестяная банка'????; false не может
	 */
	public static boolean isStr2Num(String str) {
		Pattern pattern = Pattern.compile("^[0-9]*$");
		Matcher matcher = pattern.matcher(str);
		return matcher.matches();
	}

	/**
	 * отправить данные
	 */
	public static void send(byte[] data) {
		if (data == null)
			return;
		if (mComFd > 0) {
			mCommonApi.writeCom(mComFd, data, data.length);
		}

	}

	private static boolean isMessyCode(String strName) {
		try {
			Pattern p = Pattern.compile("\\s*|\t*|\r*|\n*");
			Matcher m = p.matcher(strName);
			String after = m.replaceAll("");
			String temp = after.replaceAll("\\p{P}", "");
			char[] ch = temp.trim().toCharArray();

			int length = (ch != null) ? ch.length : 0;
			for (int i = 0; i < length; i++) {
				char c = ch[i];
				if (!Character.isLetterOrDigit(c)) {
					String str = "" + ch[i];
					if (!str.matches("[\u4e00-\u9fa5]+")) {
						return true;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	public String deleteErr(String str_VarMboxRead) {

		String b = str_VarMboxRead.replace("�", "");
		b = b.replace("", "");

		return b.trim();
	}

	public static void closeCommonApi() {

		if (mComFd > 0) {
			
			if(pin==pin_L){
				mCommonApi.setGpioDir(74, 1);
				mCommonApi.setGpioOut(74, 0);
			}
			
			mCommonApi.setGpioMode(84, 0);
			mCommonApi.setGpioDir(84, 0);
			mCommonApi.setGpioOut(84, 0);
			mCommonApi.closeCom(mComFd);
		}

	}

	/**
     * Печатать текст
     * настройка размера текста, где 1 - нормальный шрифт, 2 цифры двойной ширины и шрифта высоты,
     * другие размеры шрифта в настоящее время не поддерживаются
     * выравнивание, где 0 - слева, 1 - по центру, а 2 - справа
     * текст печатный текст
	 */
	public static void printText(int size, int align, String text) {

		switch (align) {
		case 0:
			send(new byte[] { 0x1b, 0x61, 0x00 });
			break;
		case 1:
			send(new byte[] { 0x1b, 0x61, 0x01 });
			break;
		case 2:
			send(new byte[] { 0x1b, 0x61, 0x02 });
			break;
		default:
			break;
		}
		switch (size) {
		case 1:
			send(new byte[] { 0x1D, 0x21, 0x00 });
			break;
		case 2:
			send(new byte[] { 0x1D, 0x12, 0x11 });
			break;
		default:
			break;
		}
		// Распечатать
		try {
			send((text + "\n").getBytes("GBK"));  //GBK китайская кодировка
			send(new byte[] { 0x1D, 0x0c });
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Распечатать картинку
	 * 
	 * @param align Выравнивание, где 0 слева, 1 слева, 2 справа
	 * @param bitmap Изображение для печати
	 */
	public static void printBitmap(int align, Bitmap bitmap) {
		switch (align) {
		case 0:
			send(new byte[] { 0x1b, 0x61, 0x00 });
			break;
		case 1:
			send(new byte[] { 0x1b, 0x61, 0x01 });
			break;
		case 2:
			send(new byte[] { 0x1b, 0x61, 0x02 });
			break;
		default:
			break;
		}
		byte[] b = draw2PxPoint(bitmap);
		send(b);
	}

	/**
	 * Распечатать одномерный код
	 * 
	 * @param align Выравнивание, где 0 слева, 1 слева, 2 справа
	 * @param width Печать ширины одномерного кода. Из-за ограничения в 58 листов максимальная ширина
     *                 печати составляет 384. Если она превышает ее, печать может быть невозможна.
	 * @param height Высота печати одномерного кода
	 * @param data 一Содержание кода измерения (не может быть на китайском языке, иначе будет сообщено об ошибке)
	 */
	public static void printBarCode(int align, int width, int height,
			String data) {
		switch (align) {
		case 0:
			send(new byte[] { 0x1b, 0x61, 0x00 });
			break;
		case 1:
			send(new byte[] { 0x1b, 0x61, 0x01 });
			break;
		case 2:
			send(new byte[] { 0x1b, 0x61, 0x02 });
			break;

		default:
			break;
		}

		Bitmap mBitmap = BarcodeCreater.creatBarcode(getInstance(), data,
				width, height, true, 1);
		
		byte[] printData = draw2PxPoint(mBitmap);
		
		send(printData);

	}

	/**
	 * Распечатать QR-код
	 * 
	 * @param align
	 * @param width
	 * @param height
	 * @param data
	 */
	public static void printQRCode(int align,int width, int height, String data) {
		switch (align) {
		case 0:
			send(new byte[] { 0x1b, 0x61, 0x00 });
			break;
		case 1:
			send(new byte[] { 0x1b, 0x61, 0x01 });
			break;
		case 2:
			send(new byte[] { 0x1b, 0x61, 0x02 });
			break;
		default:
			break;
		}
		Bitmap mBitmap = createQRImage(data, width, height);
//		Bitmap textBitmap = word2bitmap(data,mBitmap.getWidth());
//		mBitmap = twoBtmap2One(mBitmap, textBitmap);
		byte[] printData1 = draw2PxPoint(mBitmap);
		send(printData1);
		send(new byte[] { 0x1d, 0x0c });
	}

	// Контроль кнопки SCAN
	class ScanBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			openScan();
		}
	}

	/**
	 * Текст к картинке
	 * 
	 * @param str
	 * @return
	 */
	public static Bitmap word2bitmap(String str,int width) {

		Bitmap bMap = Bitmap.createBitmap(width, 80, Config.ARGB_8888);
		Canvas canvas = new Canvas(bMap);
		canvas.drawColor(Color.WHITE);
		TextPaint textPaint = new TextPaint();
		textPaint.setStyle(Paint.Style.FILL);
		textPaint.setColor(Color.BLACK);
		textPaint.setTextSize(21.0F);
		StaticLayout layout = new StaticLayout(str, textPaint, bMap.getWidth(),
				Alignment.ALIGN_NORMAL, (float) 1.0, (float) 0.0, true);
		layout.draw(canvas);

		return bMap;
	}

	/**
	 * Объединить две картинки в одну
	 * 
	 * @param bitmap1
	 * @param bitmap2
	 * @return
	 */
	public static Bitmap twoBtmap2One(Bitmap bitmap1, Bitmap bitmap2) {
		Bitmap bitmap3 = Bitmap.createBitmap(bitmap1.getWidth(),
				bitmap1.getHeight() + bitmap2.getHeight(), bitmap1.getConfig());
		Canvas canvas = new Canvas(bitmap3);
		canvas.drawBitmap(bitmap1, new Matrix(), null);
		canvas.drawBitmap(bitmap2, 0, bitmap1.getHeight(), null);
		return bitmap3;
	}

	/**
	 * Два изображения объединяются слева и справа в одно
	 *
	 * @param bitmap1
	 * @param bitmap2
	 * @return
	 */
	public static Bitmap twoBtmap2One1(Bitmap bitmap1, Bitmap bitmap2) {
		Bitmap bitmap3 = Bitmap.createBitmap(bitmap1.getWidth()+bitmap2.getWidth(),
				bitmap1.getHeight() , bitmap1.getConfig());
		Canvas canvas = new Canvas(bitmap3);
		canvas.drawBitmap(bitmap1, new Matrix(), null);
		canvas.drawBitmap(bitmap2,bitmap1.getWidth(), 0, null);
		return bitmap3;
	}

	/**
	 * Поворот изображения
	 *
	 * @param bm Изображение, которое нужно повернуть
	 * @param orientationDegree Угол поворота
	 * @return
	 */
	public static Bitmap adjustPhotoRotation(Bitmap bm, final int orientationDegree) {

		Matrix m = new Matrix();
		m.setRotate(orientationDegree, (float) bm.getWidth() / 2,
				(float) bm.getHeight() / 2);
		float targetX, targetY;
		if (orientationDegree == 90) {
			targetX = bm.getHeight();
			targetY = 0;
		} else {
			targetX = bm.getHeight();
			targetY = bm.getWidth();
		}

		final float[] values = new float[9];
		m.getValues(values);

		float x1 = values[Matrix.MTRANS_X];
		float y1 = values[Matrix.MTRANS_Y];

		m.postTranslate(targetX - x1, targetY - y1);

		Bitmap bm1 = Bitmap.createBitmap(bm.getHeight(), bm.getWidth(),
				Bitmap.Config.ARGB_8888);
		Paint paint = new Paint();
		Canvas canvas = new Canvas(bm1);
		canvas.drawBitmap(bm, m, paint);

		return bm1;
	}

	// Увеличить картинку
	public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
		// Получите ширину и высоту изображения
		int width = bm.getWidth();
		int height = bm.getHeight();
		// Рассчитать коэффициент масштабирования
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// Получите параметры матрицы, которые вы хотите масштабировать
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// Получите новые картинки
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix,
				true);
		return newbm;
	}


	/**
	 * Создать QR-код Адрес или строка, которую нужно преобразовать, может быть китайской
	 * 
	 * @param url
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap createQRImage(String url, int width, int height) {
		try {
			// Оценка законности URL
			if (url == null || "".equals(url) || url.length() < 1) {
				return null;
			}
			Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
			hints.put(EncodeHintType.CHARACTER_SET, "GBK");
			// Преобразование данных изображения с использованием преобразования матрицы
			BitMatrix bitMatrix = new QRCodeWriter().encode(url,
					BarcodeFormat.QR_CODE, width, height, hints);
			// bitMatrix = deleteWhite(bitMatrix);// Удалить белую рамку
			bitMatrix = deleteWhite(bitMatrix);// Удалить белую рамку
			width = bitMatrix.getWidth();
			height = bitMatrix.getHeight();
			int[] pixels = new int[width * height];
			// Здесь по алгоритму QR-кода картинки QR-кода генерируются одна за другой.
            // Два цикла for являются результатом горизонтальной развертки изображения
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					if (bitMatrix.get(x, y)) {
						pixels[y * width + x] = 0xff000000;
					} else {
						pixels[y * width + x] = 0xffffffff;
					}
				}
			}
			// Формат для создания изображения QR-кода, используйте ARGB_8888
			Bitmap bitmap = Bitmap
					.createBitmap(width, height, Config.ARGB_8888);
			bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
			return bitmap;
		} catch (WriterException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static BitMatrix deleteWhite(BitMatrix matrix) {
		int[] rec = matrix.getEnclosingRectangle();
		int resWidth = rec[2] + 1;
		int resHeight = rec[3] + 1;

		BitMatrix resMatrix = new BitMatrix(resWidth, resHeight);
		resMatrix.clear();
		for (int i = 0; i < resWidth; i++) {
			for (int j = 0; j < resHeight; j++) {
				if (matrix.get(i + rec[0], j + rec[1]))
					resMatrix.set(i, j);
			}
		}
		return resMatrix;
	}

	/*************************************************************************
     * Предполагая, что изображение 240 * 240, разрешение установлено на 24, всего печатается 10.
     * Каждая строка представляет собой матрицу размером 240 * 24 точек, каждый столбец имеет 24 точки, хранящиеся в 3 байтах.
     * Каждый байт хранит информацию о 8 пикселях. Поскольку есть только черный и белый, бит,
     * соответствующий 1, черный, а бит, соответствующий 0, белый
	 **************************************************************************/
	/**
	 * Преобразование растрового изображения в поток байтов, который может быть распечатан на принтере
	 * 
	 * @param bmp
	 * @return
	 */
	public static byte[] draw2PxPoint(Bitmap bmp) {
    // Используется для хранения преобразованных данных растрового изображения. Зачем добавлять еще 1000?
        // Это нужно делать, когда высота изображения не может быть
    // Делим ситуацию на 24 часа. Например, разрешение растрового изображения 240 * 250, что занимает 7500 байт,
    // Но на самом деле для хранения 11 строк данных каждой строке требуется 24 * 240/8 = 720 байт пространства.
        // Плюс некоторые накладные расходы на хранение инструкций,
    // Таким образом, можно безопасно подать заявку на дополнительные 1000 байт пространства, в противном случае
        // во время выполнения будет выдано исключение доступа к массиву за пределами диапазона.
		int size = bmp.getWidth() * bmp.getHeight() / 8 + 1200;
		byte[] data = new byte[size];
		int k = 0;
		// Команда для установки межстрочного интервала на 0
		data[k++] = 0x1B;
		data[k++] = 0x33;
		data[k++] = 0x00;
		// Печатать построчно
		for (int j = 0; j < bmp.getHeight() / 24f; j++) {
			// Инструкция по печати картинок
			data[k++] = 0x1B;
			data[k++] = 0x2A;
			data[k++] = 33;
			data[k++] = (byte) (bmp.getWidth() % 256); // nL
			data[k++] = (byte) (bmp.getWidth() / 256); // nH
			// Для каждой строки печатать столбец за столбцом
			for (int i = 0; i < bmp.getWidth(); i++) {
				// 24 пикселя в каждом столбце, разделенные на 3 байта для хранения
				for (int m = 0; m < 3; m++) {
					// Каждый байт представляет 8 пикселей, 0 означает белый, 1 означает черный
					for (int n = 0; n < 8; n++) {
						byte b = px2Byte(i, j * 24 + m * 8 + n, bmp);
						if (k < size) {
							data[k] += data[k] + b;
						}
						// data[k] = (byte) (data[k]+ data[k] + b);
					}
					k++;
				}
			}
			if (k < size) {
				data[k++] = 10;// Заворачивать
			}
		}
		return data;
	}

	/**
	 * Изображения в градациях серого - черно-белые, черный - 1, белый - 0.
	 * 
	 * @param x
	 *            абсцисса
	 * @param y
	 *            Ось ординат
	 * @param bit
	 *            битовая карта
	 * @return
	 */
	public static byte px2Byte(int x, int y, Bitmap bit) {
		if (x < bit.getWidth() && y < bit.getHeight()) {
			byte b;
			int pixel = bit.getPixel(x, y);
			int red = (pixel & 0x00ff0000) >> 16; // Возьми два выше
			int green = (pixel & 0x0000ff00) >> 8; // Возьми два
			int blue = pixel & 0x000000ff; // Возьмите два нижних
			int gray = RGB2Gray(red, green, blue);
			if (gray < 128) {
				b = 1;
			} else {
				b = 0;
			}
			return b;
		}
		return 0;
	}

	/**
	 * Преобразование серой шкалы изображения
	 */
	private static int RGB2Gray(int r, int g, int b) {
		int gray = (int) (0.29900 * r + 0.58700 * g + 0.11400 * b); // Формула преобразования серого
		return gray;
	}

}
