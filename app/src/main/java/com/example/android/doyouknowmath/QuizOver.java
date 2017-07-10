package com.example.android.doyouknowmath;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.example.android.doyouknowmath.Data.QuizContract;
import com.example.android.doyouknowmath.Data.QuizDbHelper;

import java.util.ArrayList;

/**
 * Implementation of App Widget functionality.
 */
public class QuizOver extends AppWidgetProvider {

    static Context mContext;
    static public int[] IDList;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        mContext = context;

        Intent intent = new Intent(context, QuizWidgetService.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        RemoteViews rv = new RemoteViews(context.getPackageName(), R.layout.quiz_over);
        rv.setRemoteAdapter(R.id.quiz_over_id, intent);

        Intent clickIntentTemplate = new Intent(context, MainActivity.class);
        PendingIntent clickPendingIntentTemplate = TaskStackBuilder.create(context)
                .addNextIntentWithParentStack(clickIntentTemplate)
                .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(R.id.quiz_over_id, clickPendingIntentTemplate);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        IDList = appWidgetIds;
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static private Cursor readData() {
        QuizDbHelper dbHelper = new QuizDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        return db.query(QuizContract.Quiz.TABLE_NAME, null, null, null, null, null ,null);
    }

    static private ArrayList<QuizItem> grabData(Cursor c) {
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

//    static QuizOver getProvider() {
//        return AppWidgetManager.getInstance(mContext).;
//    }
}

