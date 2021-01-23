package com.qs5501.demo.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.qs5501.demo.sale.entity.Product;

import static com.qs5501.demo.data.KKMContract.GoodEntry.COLUMN_BARCODE;
import static com.qs5501.demo.data.KKMContract.GoodEntry.COLUMN_GOOD_QUANTITY_IN_STOCK;
import static com.qs5501.demo.data.KKMContract.GoodEntry.COLUMN_MARK;
import static com.qs5501.demo.data.KKMContract.GoodEntry.COLUMN_NAME;
import static com.qs5501.demo.data.KKMContract.GoodEntry.COLUMN_PRICE;
import static com.qs5501.demo.data.KKMContract.GoodEntry.COLUMN_TAX_RATE;
import static com.qs5501.demo.data.KKMContract.GoodEntry.COLUMN_TAX_SYSTEM;
import static com.qs5501.demo.data.KKMContract.GoodEntry.COLUMN_UNIT;
import static com.qs5501.demo.data.KKMContract.GoodEntry.TABLE_NAME;
import static com.qs5501.demo.data.KKMContract.GoodEntry._ID;

public class KKMDbHelper extends SQLiteOpenHelper {

    //Имя файла базы данных
    private static final String DATABASE_NAME = "kkm.db";

    //Версия базы данных. При изменении схемы увеличить на единицу
    private static final int DATABASE_VERSION = 1;

    public KKMDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Строка для создания таблицы
        String SQL_CREATE_GUESTS_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + KKMContract.GoodEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_BARCODE + " TEXT NOT NULL, "
                + COLUMN_PRICE + " REAL NOT NULL DEFAULT 0.00, "
                + COLUMN_MARK + " TEXT NOT NULL DEFAULT 'нет', "
                + COLUMN_UNIT + " TEXT NOT NULL, "
                + COLUMN_GOOD_QUANTITY_IN_STOCK + " REAL NOT NULL DEFAULT 0.000, "
                + COLUMN_TAX_SYSTEM + " INTEGER NOT NULL DEFAULT 1, "
                + COLUMN_TAX_RATE + " INTEGER NOT NULL DEFAULT 1);";

        // Запускаем создание таблицы
        db.execSQL(SQL_CREATE_GUESTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public boolean insertData(Product product){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, product.getName());
        contentValues.put(COLUMN_BARCODE, product.getBarcode());
        contentValues.put(COLUMN_PRICE, product.getPrice());
        contentValues.put(COLUMN_MARK, product.isMark());
        contentValues.put(COLUMN_UNIT, product.getUnit());
        contentValues.put(COLUMN_GOOD_QUANTITY_IN_STOCK, product.getQuantityStock());
        contentValues.put(COLUMN_TAX_SYSTEM, product.getTaxSystem());
        contentValues.put(COLUMN_TAX_RATE, product.getTaxRate());

        long result = db.insert(TABLE_NAME, null, contentValues);
        return result != -1;
    }

    //Проверка на существование
    public boolean tableExists(SQLiteDatabase db, String tableName) {
        if (tableName == null || db == null || !db.isOpen())
        {
            return false;
        }
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM sqlite_master WHERE type = ? " +
                "AND name = ?", new String[] {"table", tableName});
        if (!cursor.moveToFirst())
        {
            cursor.close();
            return false;
        }
        int count = cursor.getInt(0);
        cursor.close();
        return count > 0;
    }

    public Cursor viewData(){
        SQLiteDatabase db = this.getReadableDatabase();
        if (!tableExists(db, TABLE_NAME)){
            onCreate(db);
        }
        String query = "Select * from " +TABLE_NAME;
        return db.rawQuery(query, null);
    }

    public int getIDAddedItem(){
        SQLiteDatabase db = this.getReadableDatabase();
        if (!tableExists(db, TABLE_NAME)){
            onCreate(db);
        }
        String query = "SELECT ROWID from " +TABLE_NAME+ " order by ROWID DESC limit 1";
        Cursor c = db.rawQuery(query, null);
        if (c != null && c.moveToFirst()) {
            int id = c.getInt(0);
            c.close();
            return id;
        }
        return 0;
    }

    public int delete(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs ={""+id};
        return db.delete(TABLE_NAME, _ID+ "=?", whereArgs);
    }

    public int update(Product product) {
        SQLiteDatabase db = this.getWritableDatabase();
        String[] whereArgs ={""+product.getId()};

        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_NAME, product.getName());
        contentValues.put(COLUMN_BARCODE, product.getBarcode());
        contentValues.put(COLUMN_PRICE, product.getPrice());
        contentValues.put(COLUMN_MARK, product.isMark());
        contentValues.put(COLUMN_UNIT, product.getUnit());
        contentValues.put(COLUMN_GOOD_QUANTITY_IN_STOCK, product.getQuantityStock());
        contentValues.put(COLUMN_TAX_SYSTEM, product.getTaxSystem());
        contentValues.put(COLUMN_TAX_RATE, product.getTaxRate());

        return db.update(TABLE_NAME, contentValues, _ID+ "=?", whereArgs);
    }

    //удаление базы данных и создание новой, id обнуляются
    public void deleteBase() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    //очистка базы, id НЕ обнуляются
    public void cleanBase(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, null, null);
    }
}
