package com.example.android.doyouknowmath;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.doyouknowmath.Data.QuizContract;
import com.example.android.doyouknowmath.Data.QuizDbHelper;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Quiz extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference mDatabase;

    RadioGroup answerRadioGroup;
    RadioButton aRadio;
    RadioButton bRadio;
    RadioButton cRadio;
    RadioButton dRadio;
    LinearLayout answerCheckboxGroup;
    CheckBox aCheckbox;
    CheckBox bCheckbox;
    CheckBox cCheckbox;
    CheckBox dCheckbox;
    EditText answerTextField;
    TextView question;
    Button submit;

    //Define needed variables and arrays
    String currentQuiz;

    boolean[] radioChecked = {
            false,
            false,
            false,
            false
    };

    boolean[] checkboxChecked = {
            false,
            false,
            false,
            false
    };

//    String[] questions = {
//            "What is 1+1?",
//            "Simplify 18/20",
//            "What makes up 50?",
//            "What is 15/3?",
//            "Which expressions evaluate to 16?"
//    };
//
//    String[][] answers = {
//            {"2", "txt"},
//            {"B", "mcr"},
//            {"ACD", "mcc"},
//            {"5", "txt"},
//            {"BD", "mcc"}
//    };
//
//    String[][] possibleAnswers = {
//            {},
//            {"4/5", "9/10", "10/9", "3/5"},
//            {"5", "3", "2", "5"},
//            {},
//            {"5*3", "8+8", "4*3", "4*4"}
//    };

    int questionNumber = 0;
    int score = 0;
    boolean hasCompleted = false;

    int numberOfQuestions = 0;
    Question d;
    String currentQuestion;
    String answer;
    String answerType;
    String answers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        configure();

        currentQuiz = getIntent().getStringExtra("name");

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                mDatabase = FirebaseDatabase.getInstance().getReference();
                int questionID = questionNumber;
                mDatabase.child("Quiz1").child("Question" + (questionID + 1)).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        d = dataSnapshot.getValue(Question.class);

                        Log.d("quizRTDB", "onDataChange: Data:" + d.toString());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("quizRTDB", "onCancelled: Failed to download data!", databaseError.toException());
                    }
                });
                mDatabase.child("Quiz1").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Number number = dataSnapshot.getValue(Number.class);

                        numberOfQuestions = number.Number;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w("quizRTDB", "onCancelled: Failed to download number of questions!", databaseError.toException());
                    }
                });
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                currentQuestion = d.Question;
                answer = d.Answer;
                answerType = d.AnswerType;
                answers = d.Answers;

                answerRadioGroup.setVisibility(View.GONE);
                answerCheckboxGroup.setVisibility(View.GONE);
                answerTextField.setVisibility(View.GONE);

                question.setText(currentQuestion);
                if (answerType == "mcr") {
                    answerRadioGroup.setVisibility(View.VISIBLE);
                    aRadio.setText(answers.split(",")[0]);
                    bRadio.setText(answers.split(",")[1]);
                    cRadio.setText(answers.split(",")[2]);
                    dRadio.setText(answers.split(",")[3]);
                } else if (answerType == "mcc") {
                    aCheckbox.setChecked(false);
                    bCheckbox.setChecked(false);
                    cCheckbox.setChecked(false);
                    dCheckbox.setChecked(false);
                    answerCheckboxGroup.setVisibility(View.VISIBLE);
                    aCheckbox.setText(answers.split(",")[0]);
                    bCheckbox.setText(answers.split(",")[1]);
                    cCheckbox.setText(answers.split(",")[2]);
                    dCheckbox.setText(answers.split(",")[3]);
                } else if (answerType == "txt") {
                    answerTextField.setVisibility(View.VISIBLE);
                    answerTextField.setText("");
                } else {
                    Log.w("MainActivity/QLoader", "Invalid answer type in RTDB");
                }
            }
        }.execute();
    }

    public void radioButtonClick(View view) {
        radioChecked[0] = aRadio.isChecked();
        radioChecked[1] = bRadio.isChecked();
        radioChecked[2] = cRadio.isChecked();
        radioChecked[3] = dRadio.isChecked();
    }

    public void checkboxClick(View view) {
        checkboxChecked[0] = aCheckbox.isChecked();
        checkboxChecked[1] = bCheckbox.isChecked();
        checkboxChecked[2] = cCheckbox.isChecked();
        checkboxChecked[3] = dCheckbox.isChecked();
    }

    public void submitAnswer(View view) {

        if (!hasCompleted) {

            if (checkAnswer()) {
                score += 1;
            }

            if (questionNumber + 1 < numberOfQuestions) {
                questionNumber += 1;
                loadQuestion(questionNumber);
            } else {
                submit.setText(R.string.end_button_text);
                hasCompleted = true;
                answerCheckboxGroup.setVisibility(View.GONE);
                answerTextField.setVisibility(View.GONE);
                answerRadioGroup.setVisibility(View.GONE);
                question.setText(getString(R.string.end_toast_part_one) + score + getString(R.string.end_toast_part_two) + numberOfQuestions);
                Toast.makeText(Quiz.this, R.string.end_display_text, Toast.LENGTH_SHORT).show();
                Bundle bundle = new Bundle();
                bundle.putInt("Score", score);
                mFirebaseAnalytics.logEvent("finish_quiz", bundle);
            }

        } else {

            QuizDbHelper dbHelper = new QuizDbHelper(this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(QuizContract.Quiz.COLUMN_NAME_QNAME, currentQuiz);

            float calcScore = (float) score / numberOfQuestions;

            values.put(QuizContract.Quiz.COLUMN_NAME_SCORE, calcScore * 100.0);

            db.delete(QuizContract.Quiz.TABLE_NAME, QuizContract.Quiz.COLUMN_NAME_QNAME + "=\'" + currentQuiz + "\'", null);

            db.insert(QuizContract.Quiz.TABLE_NAME, null, values);

            /*LocalBroadcastManager.getInstance(this).*/sendBroadcast(new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(QuizOver.mContext, QuizOver.class))));

            submit.setText(R.string.defualt_button_text);
            questionNumber = 0;
            hasCompleted = false;
            score = 0;

            startActivity(new Intent(Quiz.this, MainActivity.class));

            sendBroadcast(new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                    AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(QuizOver.mContext, QuizOver.class))));
        }

    }

    public void loadQuestion(int questionID) {

        mDatabase.child("Quiz1").child("Question" + questionID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                d = dataSnapshot.getValue(Question.class);

                Log.d("quizRTDB", "onDataChange: Data:" + d.toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("quizRTDB", "onCancelled: Failed to download data!", databaseError.toException());
            }
        });

        currentQuestion = d.Question;
        answer = d.Answer;
        answerType = d.AnswerType;
        answers = d.Answers;

        answerRadioGroup.setVisibility(View.GONE);
        answerCheckboxGroup.setVisibility(View.GONE);
        answerTextField.setVisibility(View.GONE);

        question.setText(currentQuestion);
        if (answerType == "mcr") {
            answerRadioGroup.setVisibility(View.VISIBLE);
            aRadio.setText(answers.split(",")[0]);
            bRadio.setText(answers.split(",")[1]);
            cRadio.setText(answers.split(",")[2]);
            dRadio.setText(answers.split(",")[3]);
        } else if (answerType == "mcc") {
            aCheckbox.setChecked(false);
            bCheckbox.setChecked(false);
            cCheckbox.setChecked(false);
            dCheckbox.setChecked(false);
            answerCheckboxGroup.setVisibility(View.VISIBLE);
            aCheckbox.setText(answers.split(",")[0]);
            bCheckbox.setText(answers.split(",")[1]);
            cCheckbox.setText(answers.split(",")[2]);
            dCheckbox.setText(answers.split(",")[3]);
        } else if (answerType == "txt") {
            answerTextField.setVisibility(View.VISIBLE);
            answerTextField.setText("");
        } else {
            Log.w("MainActivity/QLoader", "Invalid answer type in RTDB");
        }
    }

    private void configure() {

        answerRadioGroup = (RadioGroup) findViewById(R.id.raido_group);
        aRadio = (RadioButton) findViewById(R.id.A_radio_button);
        bRadio = (RadioButton) findViewById(R.id.B_radio_button);
        cRadio = (RadioButton) findViewById(R.id.C_radio_button);
        dRadio = (RadioButton) findViewById(R.id.D_radio_button);
        aCheckbox = (CheckBox) findViewById(R.id.A_checkbox);
        bCheckbox = (CheckBox) findViewById(R.id.B_checkbox);
        cCheckbox = (CheckBox) findViewById(R.id.C_checkbox);
        dCheckbox = (CheckBox) findViewById(R.id.D_checkbox);
        answerCheckboxGroup = (LinearLayout) findViewById(R.id.check_group);
        question = (TextView) findViewById(R.id.question);
        answerTextField = (EditText) findViewById(R.id.answer_text_input);
        submit = (Button) findViewById(R.id.submit_button);
    }

    private boolean checkAnswer() {

        if (answerType == "mcr") {
            if (answer == "A") {
                return aRadio.isChecked();
            }
            if (answer == "B") {
                return bRadio.isChecked();
            }
            if (answer == "C") {
                return cRadio.isChecked();
            }
            if (answer == "D") {
                return dRadio.isChecked();
            }
        } else if (answerType == "mcc") {
            boolean[] correctAnswer = {false, false, false, false};
            if (answer.contains("A")) {
                correctAnswer[0] = true;
            }
            if (answer.contains("B")) {
                correctAnswer[1] = true;
            }
            if (answer.contains("C")) {
                correctAnswer[2] = true;
            }
            if (answer.contains("D")) {
                correctAnswer[3] = true;
            }
            return (compareArrays(correctAnswer, checkboxChecked));

        } else if (answerType == "txt") {
            String correct = answer;
            return (correct.equalsIgnoreCase(answerTextField.getText().toString()));
        } else {
            Toast.makeText(Quiz.this, R.string.error_type_invalid, Toast.LENGTH_LONG).show();
            Log.w("MainActivity", "Error checking answer, answer type is invalid");
            return false;
        }

        Toast.makeText(Quiz.this, R.string.error, Toast.LENGTH_LONG).show();
        Log.w("MainActivity", "Error checking answer, unknown error");
        return false;
    }

    private boolean compareArrays(boolean[] array1, boolean[] array2) {
        if (array1.length != array2.length) return false;
        for (int i = 0; i < array2.length; i++) {
            if (array1[i] != array2[i]) return false;
        }
        return true;
    }
}
