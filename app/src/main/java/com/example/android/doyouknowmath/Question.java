package com.example.android.doyouknowmath;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Robert on 7/10/2017.
 */

@IgnoreExtraProperties
public class Question {
    public String Question;
    public String Answer;
    public String AnswerType;
    public String Answers;

    public Question() {

    }

    public Question(String question, String answer, String answerType) {
        Question = question;
        Answer = answer;
        AnswerType = answerType;
    }

    @Override
    public String toString() {
        return "Question{" +
                "Question='" + Question + '\'' +
                ", Answer='" + Answer + '\'' +
                ", AnswerType='" + AnswerType + '\'' +
                '}';
    }
}
