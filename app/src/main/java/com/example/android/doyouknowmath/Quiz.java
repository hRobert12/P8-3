package com.example.android.doyouknowmath;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
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

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class Quiz extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference mDatabase;

    @BindView(R.id.raido_group) RadioGroup answerRadioGroup;
    @BindView(R.id.A_radio_button) RadioButton aRadio;
    @BindView(R.id.B_radio_button) RadioButton bRadio;
    @BindView(R.id.C_radio_button) RadioButton cRadio;
    @BindView(R.id.D_radio_button) RadioButton dRadio;
    @BindView(R.id.check_group) LinearLayout answerCheckboxGroup;
    @BindView(R.id.A_checkbox) CheckBox aCheckbox;
    @BindView(R.id.B_checkbox) CheckBox bCheckbox;
    @BindView(R.id.C_checkbox) CheckBox cCheckbox;
    @BindView(R.id.D_checkbox) CheckBox dCheckbox;
    @BindView(R.id.answer_text_input) EditText answerTextField;
    @BindView(R.id.question) TextView question;
    @BindView(R.id.submit_button) Button submit;

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
    Question data;
    String currentQuestion;
    String answer;
    String answerType;
    String answers;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        configure();

        currentQuiz = getIntent().getStringExtra("name");


        mDatabase = FirebaseDatabase.getInstance().getReference();
        if (savedInstanceState != null) {
            questionNumber = savedInstanceState.getInt("QNumber");
        }
        int questionID = questionNumber;
        mDatabase.child("Quiz1").child("Question" + (questionID + 1)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(Question.class);

                if (data != null && data.Question != null && data.Answer != null && data.AnswerType != null) {

                    Log.d("quizRTDB", "onDataChange: Data:" + data.toString());

                    currentQuestion = data.Question;
                    answer = data.Answer;
                    answerType = data.AnswerType;
                    answers = data.Answers;

                    answerRadioGroup.setVisibility(View.GONE);
                    answerCheckboxGroup.setVisibility(View.GONE);
                    answerTextField.setVisibility(View.GONE);

                    question.setText(currentQuestion);
                    if (Objects.equals(answerType, "mcr")) {
                        if (data.Answers != null && answers.split(",").length == 4) {
                            answerRadioGroup.setVisibility(View.VISIBLE);
                            aRadio.setText(answers.split(",")[0]);
                            bRadio.setText(answers.split(",")[1]);
                            cRadio.setText(answers.split(",")[2]);
                            dRadio.setText(answers.split(",")[3]);
                        } else {
                            Toast.makeText(Quiz.this, R.string.error_type_invalid, Toast.LENGTH_LONG).show();
                            Log.w("MainActivity", "Error checking answer, wrong answer format");
                            startActivity(new Intent(Quiz.this, MainActivity.class));
                        }
                    } else if (Objects.equals(answerType, "mcc")) {
                        if (data.Answers != null && answers.split(",").length == 4) {
                            aCheckbox.setChecked(false);
                            bCheckbox.setChecked(false);
                            cCheckbox.setChecked(false);
                            dCheckbox.setChecked(false);
                            answerCheckboxGroup.setVisibility(View.VISIBLE);
                            aCheckbox.setText(answers.split(",")[0]);
                            bCheckbox.setText(answers.split(",")[1]);
                            cCheckbox.setText(answers.split(",")[2]);
                            dCheckbox.setText(answers.split(",")[3]);
                        } else {
                            Toast.makeText(Quiz.this, R.string.error_type_invalid, Toast.LENGTH_LONG).show();
                            Log.w("MainActivity", "Error checking answer, wrong answer format");
                            startActivity(new Intent(Quiz.this, MainActivity.class));
                        }
                    } else if (Objects.equals(answerType, "txt")) {
                        answerTextField.setVisibility(View.VISIBLE);
                        answerTextField.setText("");
                    } else {
                        Log.w("MainActivity/QLoader", "Invalid answer type in RTDB");
                    }
                    if (savedInstanceState != null) {
                        if (Objects.equals(answerType, "txt")) {
                            answerTextField.setText(savedInstanceState.getString("EnterdAnswer"));
                        } else if (Objects.equals(answerType, "mcr")) {
                            if (Objects.equals(savedInstanceState.getString("Checked"), "A")) {
                                aRadio.setChecked(true);
                            } else if (Objects.equals(savedInstanceState.getString("Checked"), "B")) {
                                bRadio.setChecked(true);
                            } else if (Objects.equals(savedInstanceState.getString("Checked"), "C")) {
                                cRadio.setChecked(true);
                            } else if (Objects.equals(savedInstanceState.getString("Checked"), "D")) {
                                dRadio.setChecked(true);
                            }
                        } else if (Objects.equals(answerType, "mcc")) {
                            aCheckbox.setChecked(savedInstanceState.getBoolean("1Checked"));
                            bCheckbox.setChecked(savedInstanceState.getBoolean("2Checked"));
                            cCheckbox.setChecked(savedInstanceState.getBoolean("3Checked"));
                            dCheckbox.setChecked(savedInstanceState.getBoolean("4Checked"));
                        } else {
                            //welp
                        }
                    }
                } else {
                    Toast.makeText(Quiz.this, R.string.error_type_invalid, Toast.LENGTH_LONG).show();
                    Log.w("MainActivity", "Error checking answer, invalid data");
                    startActivity(new Intent(Quiz.this, MainActivity.class));
                }
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

            if (QuizOver.hasWidget)
            {
                sendBroadcast(new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE).putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,
                        AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(QuizOver.mContext, QuizOver.class))));
                AppWidgetManager.getInstance(this).notifyAppWidgetViewDataChanged(AppWidgetManager.getInstance(this).getAppWidgetIds(new ComponentName(this, QuizOver.class)), R.id.quiz_over_id);
            }

            AppWidgetManager.getInstance(this);
            submit.setText(R.string.defualt_button_text);
            questionNumber = 0;
            hasCompleted = false;
            score = 0;

            startActivity(new Intent(Quiz.this, MainActivity.class));
        }

    }

    public void loadQuestion(int questionID) {

        mDatabase.child("Quiz1").child("Question" + (questionID + 1)).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                data = dataSnapshot.getValue(Question.class);

                if (data != null && data.Question != null && data.Answer != null && data.AnswerType != null) {

                    Log.d("quizRTDB", "onDataChange: Data:" + data.toString());

                    currentQuestion = data.Question;
                    answer = data.Answer;
                    answerType = data.AnswerType;
                    answers = data.Answers;

                    answerRadioGroup.setVisibility(View.GONE);
                    answerCheckboxGroup.setVisibility(View.GONE);
                    answerTextField.setVisibility(View.GONE);

                    question.setText(currentQuestion);
                    if (Objects.equals(answerType, "mcr")) {
                        if (data.Answers != null && answers.split(",").length == 4) {
                            answerRadioGroup.setVisibility(View.VISIBLE);
                            aRadio.setText(answers.split(",")[0]);
                            bRadio.setText(answers.split(",")[1]);
                            cRadio.setText(answers.split(",")[2]);
                            dRadio.setText(answers.split(",")[3]);
                        } else {
                            Toast.makeText(Quiz.this, R.string.error_type_invalid, Toast.LENGTH_LONG).show();
                            Log.w("MainActivity", "Error checking answer, wrong answer format");
                            startActivity(new Intent(Quiz.this, MainActivity.class));
                        }
                    } else if (Objects.equals(answerType, "mcc")) {
                        if (data.Answers != null && answers.split(",").length == 4) {
                            aCheckbox.setChecked(false);
                            bCheckbox.setChecked(false);
                            cCheckbox.setChecked(false);
                            dCheckbox.setChecked(false);
                            answerCheckboxGroup.setVisibility(View.VISIBLE);
                            aCheckbox.setText(answers.split(",")[0]);
                            bCheckbox.setText(answers.split(",")[1]);
                            cCheckbox.setText(answers.split(",")[2]);
                            dCheckbox.setText(answers.split(",")[3]);
                        } else {
                            Toast.makeText(Quiz.this, R.string.error_type_invalid, Toast.LENGTH_LONG).show();
                            Log.w("MainActivity", "Error checking answer, wrong answer format");
                            startActivity(new Intent(Quiz.this, MainActivity.class));
                        }
                    } else if (Objects.equals(answerType, "txt")) {
                        answerTextField.setVisibility(View.VISIBLE);
                        answerTextField.setText("");
                    } else {
                        Log.w("MainActivity/QLoader", "Invalid answer type in RTDB");
                    }
                } else {
                    Toast.makeText(Quiz.this, R.string.error_type_invalid, Toast.LENGTH_LONG).show();
                    Log.w("MainActivity", "Error checking answer, invalid data");
                    startActivity(new Intent(Quiz.this, MainActivity.class));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("quizRTDB", "onCancelled: Failed to download data!", databaseError.toException());
            }
        });
    }

    private void configure() {
        ButterKnife.bind(this);
    }

    private boolean checkAnswer() {

        if (Objects.equals(answerType, "mcr")) {
            if (Objects.equals(answer, "A")) {
                return aRadio.isChecked();
            }
            if (Objects.equals(answer, "B")) {
                return bRadio.isChecked();
            }
            if (Objects.equals(answer, "C")) {
                return cRadio.isChecked();
            }
            if (Objects.equals(answer, "D")) {
                return dRadio.isChecked();
            }
        } else if (Objects.equals(answerType, "mcc")) {
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

        } else if (Objects.equals(answerType, "txt")) {
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("QNumber", questionNumber);
        if (Objects.equals(answerType, "txt")) {
            outState.putString("EnterdAnswer", answerTextField.getText().toString());
        } else if (Objects.equals(answerType, "mcr")) {
            if (radioChecked[0]) {
                outState.putString("Checked", "A");
            } else if (radioChecked[1]) {
                outState.putString("Checked", "B");
            } else if (radioChecked[2]) {
                outState.putString("Checked", "C");
            } else if (radioChecked[3]) {
                outState.putString("Checked", "D");
            }
        } else if (Objects.equals(answerType, "mcc")) {
            outState.putBoolean("1Checked", checkboxChecked[0]);
            outState.putBoolean("2Checked", checkboxChecked[1]);
            outState.putBoolean("3Checked", checkboxChecked[2]);
            outState.putBoolean("4Checked", checkboxChecked[3]);
        } else {
            //welp
        }
        super.onSaveInstanceState(outState);
    }
}
