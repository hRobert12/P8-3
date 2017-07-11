package com.example.android.doyouknowmath;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import com.example.android.doyouknowmath.Data.QuizContract;
import com.example.android.doyouknowmath.Data.QuizDbHelper;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS_NAME = "init";
    QuizAdpter adpter;
    ArrayList<QuizItem> list;
    @BindView(R.id.adView) AdView mAdView;
    @BindView(R.id.quiz_list) ListView quizList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");
//
//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//        requestNewInterstitial();
//AdRequest.DEVICE_ID_EMULATOR
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("470-155807809")
                .build();
        mAdView.loadAd(adRequest);


        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (!settings.getBoolean("init", false)) {

            QuizDbHelper dbHelper = new QuizDbHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.clear();
            values.put(QuizContract.Quiz.COLUMN_NAME_QNAME, "Bacis");
            values.put(QuizContract.Quiz.COLUMN_NAME_SCORE, (byte[]) null);

            db.insert(QuizContract.Quiz.TABLE_NAME, null, values);

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("init", true);

            editor.apply();
        }

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                list = grabData(readData());
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                adpter = new QuizAdpter(MainActivity.this, R.layout.list_item, list, MainActivity.this);

                //if (list != null) {

                quizList.setAdapter(adpter);

                //}
            }
        }.execute();





    }
    
    private Cursor readData() {
        return getContentResolver().query(
                Uri.parse("content://com.example.android.doyouknowmath/Quizes"),
                null,
                null,
                null,
                null);
    }

    private ArrayList<QuizItem> grabData(Cursor c) {
        final int COLUMN_INDEX_ID = c.getColumnIndex(QuizContract.Quiz._ID);
        final int COLUMN_INDEX_QNAME = c.getColumnIndex(QuizContract.Quiz.COLUMN_NAME_QNAME);
        final int COLUMN_INDEX_SCORE = c.getColumnIndex(QuizContract.Quiz.COLUMN_NAME_SCORE);
        ArrayList<QuizItem> arrayList = new ArrayList<QuizItem>();
        if (c.moveToFirst()) {
            for (int i = 0; i < c.getCount(); i++) {
                c.moveToPosition(i);
                arrayList.add(new QuizItem(c.getInt(COLUMN_INDEX_SCORE),
                        c.getString(COLUMN_INDEX_QNAME)));
            }
        } else {
            c.close();
            return null;
        }
        c.close();
        return arrayList;
    }


}
