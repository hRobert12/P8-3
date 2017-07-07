package com.example.android.doyouknowmath;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Robert on 5/6/2017.
 */

public class QuizAdpter extends ArrayAdapter<QuizItem> {

    Activity currentActivity;

    public Context mContext;
    public QuizAdpter(@NonNull Context context, @LayoutRes int resource, @NonNull List<QuizItem> objects, @NonNull Activity activity) {
        super(context, resource, objects);
        mContext = context;
        currentActivity = activity;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView ==  null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        }

        final QuizItem currentItem = getItem(position);

        TextView name = (TextView) convertView.findViewById(R.id.quiz_name);
        name.setText(currentItem.getName());

        TextView price = (TextView) convertView.findViewById(R.id.quiz_score);
        price.setText(String.valueOf(currentItem.getScore()));

        RelativeLayout quiz = (RelativeLayout) convertView.findViewById(R.id.quiz);
        quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), Quiz.class);
                intent.putExtra("name", currentItem.getName());

                Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(currentActivity).toBundle();

                getContext().startActivity(intent, bundle);
            }
        });

        return convertView;
    }
}
