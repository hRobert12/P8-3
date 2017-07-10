package com.example.android.doyouknowmath;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Robert on 7/9/2017.
 */

public class QuizWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new QuizWidgetFactory();
    }
}
