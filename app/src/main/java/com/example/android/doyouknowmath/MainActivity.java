package com.example.android.doyouknowmath;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.android.doyouknowmath.Data.QuizContract;
import com.example.android.doyouknowmath.Data.QuizDbHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "init";
    QuizAdpter adpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
//
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//        requestNewInterstitial();
//
//        AdView mAdView = (AdView) findViewById(R.id.ad);
//        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
//        mAdView.loadAd(adRequest);


        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (!settings.getBoolean("init", false)) {

            QuizDbHelper dbHelper = new QuizDbHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ArrayList<String> quizNames = new ArrayList<>();
            quizNames.add("Bacis");
            ContentValues values = new ContentValues();

            for (int i = 0;i==quizNames.size();i++) {
                values.clear();
                values.put(QuizContract.Quiz.COLUMN_NAME_QNAME, quizNames.get(i));
                values.put(QuizContract.Quiz.COLUMN_NAME_SCORE, (byte[]) null);

                db.insert(QuizContract.Quiz.TABLE_NAME, null, values);
            }

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("init", true);

            editor.apply();
        }

        ArrayList<QuizItem> list = grabData(readData());

        adpter = new QuizAdpter(this, R.layout.list_item, list, this);

        ListView quizList = (ListView) findViewById(R.id.quiz_list);

        if (list != null) {

            quizList.setAdapter(adpter);

        }

    }
    
    private Cursor readData() {
        QuizDbHelper dbHelper = new QuizDbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        
        return db.query(QuizContract.Quiz.TABLE_NAME, null, null, null, null, null ,null);
    }

    private ArrayList<QuizItem> grabData(Cursor c) {
        final int COLUMN_INDEX_ID = c.getColumnIndex(QuizContract.Quiz._ID);
        final int COLUMN_INDEX_QNAME = c.getColumnIndex(QuizContract.Quiz.COLUMN_NAME_QNAME);
        final int COLUMN_INDEX_SCORE = c.getColumnIndex(QuizContract.Quiz.COLUMN_NAME_SCORE);
        ArrayList<QuizItem> arrayList = new ArrayList<QuizItem>();
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                c.moveToPosition(i);
                arrayList.add(new QuizItem(c.getInt(COLUMN_INDEX_QNAME),
                        c.getString(COLUMN_INDEX_SCORE)));
            }
        } else {
            c.close();
            return null;
        }
        c.close();
        return arrayList;
    }


}
