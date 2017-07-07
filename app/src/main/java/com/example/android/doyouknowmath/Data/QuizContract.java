package com.example.android.doyouknowmath.Data;

import android.provider.BaseColumns;

/**
 * Created by Robert on 5/9/2017.
 */

public class QuizContract {

    public QuizContract() {
    }

    public static abstract class Quiz implements BaseColumns {
        public static final String TABLE_NAME = "Quizes";
        public static final String COLUMN_NAME_QNAME = "name";
        public static final String COLUMN_NAME_SCORE = "Score";
    }
}
