package com.qs5501.demo.data;

import android.provider.BaseColumns;

public final class KKMContract {

    public KKMContract() {
    }

    public static final class GoodEntry implements BaseColumns {
        public final static String TABLE_NAME = "goods";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_NAME = "name";
        public final static String COLUMN_BARCODE = "barcode";
        public final static String COLUMN_PRICE = "price";
        public final static String COLUMN_MARK = "mark";
        public final static String COLUMN_UNIT = "unit";
        public final static String COLUMN_GOOD_QUANTITY_IN_STOCK = "quantity_good_in_stock";
        public final static String COLUMN_TAX_SYSTEM = "tax_system";
        public final static String COLUMN_TAX_RATE = "tax_rate";
    }

}
