package com.example.android.doyouknowmath;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.android.doyouknowmath.Data.QuizContract;
import com.example.android.doyouknowmath.Data.QuizDbHelper;

import java.util.ArrayList;

/**
 * Created by Robert on 7/9/2017.
 */

public class QuizWidgetFactory implements RemoteViewsService.RemoteViewsFactory {
    private Cursor cursor;
    private Context context;
    private int posOfID;

    QuizWidgetFactory(Context context) {
        super();
        this.context = context;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {

    }

    @Override
    public void onDestroy() {
        cursor.close();
        cursor = null;
    }

    @Override
    public int getCount() {
        
        QuizDbHelper dbHelper = new QuizDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        cursor = db.query(QuizContract.Quiz.TABLE_NAME, null, null, null, null, null, null);

        posOfID = cursor.getColumnIndex(QuizContract.Quiz._ID);

        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
        }
        return count;
    }

    @Override
    public RemoteViews getViewAt(int i) {

        cursor.moveToPosition(i);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.list_item);

        ArrayList<QuizItem> quizList = grabData(cursor);

        views.setTextViewText(R.id.quiz_name, quizList.get(i).getName());
        views.setTextViewText(R.id.quiz_score, String.valueOf(quizList.get(i).getScore()));

        final Intent fillInIntent = new Intent();

        views.setOnClickFillInIntent(R.id.quiz, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        cursor.moveToPosition(i);
        return cursor.getLong(posOfID);
    }

    @Override
    public boolean hasStableIds() {
        return true;
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

            return null;
        }

        return arrayList;
    }
}
