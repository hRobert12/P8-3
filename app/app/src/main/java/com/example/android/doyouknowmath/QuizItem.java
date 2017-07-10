package com.example.android.doyouknowmath;

/**
 * Created by Robert on 5/6/2017.
 */

public class QuizItem {

    private int score;
    private String name;

    public QuizItem(int score, String name) {
        setName(name);
        setScore(score);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
