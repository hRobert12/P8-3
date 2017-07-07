package com.example.android.doyouknowmath.Data;

import android.provider.BaseColumns;

/**
 * Created by Robert on 5/9/2017.
 */

public final class HQuizDataContract {

    public HQuizDataContract() {}

    public static abstract class HQuizData implements BaseColumns {
        public static final String TABLE_NAME = "HQuizData";
        public static final String COLUMN_NAME_QNAME = "name";
        public static final String COLUMN_NAME_JSON_DATA = "JSONData";
        public static final String COLUMN_NAME_INFO = "Info";
    }

}
