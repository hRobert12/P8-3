package com.example.android.doyouknowmath.Data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Robert on 5/9/2017.
 */

public class HQuizDataDbHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + HQuizDataContract.HQuizData.TABLE_NAME + " (" +
                    HQuizDataContract.HQuizData._ID + " INTEGER PRIMARY KEY," +
                    HQuizDataContract.HQuizData.COLUMN_NAME_QNAME + TEXT_TYPE + COMMA_SEP +
                    HQuizDataContract.HQuizData.COLUMN_NAME_JSON_DATA + TEXT_TYPE + COMMA_SEP +
                    HQuizDataContract.HQuizData.COLUMN_NAME_INFO + TEXT_TYPE +
                    " )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + HQuizDataContract.HQuizData.TABLE_NAME;
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "InventoryReader.db";

    private Context mContext;

    public HQuizDataDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void deleteDatabase(SQLiteDatabase db) {
        mContext.deleteDatabase(DATABASE_NAME);
    }

}
