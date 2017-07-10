package com.example.android.doyouknowmath;

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

public class Quiz extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;
    private DatabaseReference mDatabase;


    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            dataSnapshot.getValue();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    };

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

    String[] questions = {
            "What is 1+1?",
            "Simplify 18/20",
            "What makes up 50?",
            "What is 15/3?",
            "Which expressions evaluate to 16?"
    };

    String[][] answers = {
            {"2", "txt"},
            {"B", "mcr"},
            {"ACD", "mcc"},
            {"5", "txt"},
            {"BD", "mcc"}
    };

    String[][] possibleAnswers = {
            {},
            {"4/5", "9/10", "10/9", "3/5"},
            {"5", "3", "2", "5"},
            {},
            {"5*3", "8+8", "4*3", "4*4"}
    };

    int questionNumber = 0;
    int score = 0;
    boolean hasCompleted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        configure();

        currentQuiz = getIntent().getStringExtra("name");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Quiz1").child("Question1");
        answerRadioGroup.setVisibility(View.GONE);
        answerCheckboxGroup.setVisibility(View.GONE);
        answerTextField.setVisibility(View.GONE);

        int questionID = questionNumber;

        question.setText(questions[questionID]);
        if (answers[questionID][1] == "mcr") {
            answerRadioGroup.setVisibility(View.VISIBLE);
            aRadio.setText(possibleAnswers[questionID][0]);
            bRadio.setText(possibleAnswers[questionID][1]);
            cRadio.setText(possibleAnswers[questionID][2]);
            dRadio.setText(possibleAnswers[questionID][3]);
        } else if (answers[questionID][1] == "mcc") {
            aCheckbox.setChecked(false);
            bCheckbox.setChecked(false);
            cCheckbox.setChecked(false);
            dCheckbox.setChecked(false);
            answerCheckboxGroup.setVisibility(View.VISIBLE);
            aCheckbox.setText(possibleAnswers[questionID][0]);
            bCheckbox.setText(possibleAnswers[questionID][1]);
            cCheckbox.setText(possibleAnswers[questionID][2]);
            dCheckbox.setText(possibleAnswers[questionID][3]);
        } else if (answers[questionID][1] == "txt") {
            answerTextField.setVisibility(View.VISIBLE);
            answerTextField.setText("");
        } else {
            Log.w("MainActivity/QLoader", "Invalid answer type in answers");
        }
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

            if (questionNumber + 1 < questions.length) {
                questionNumber += 1;
                loadQuestion(questionNumber);
            } else {
                submit.setText(R.string.end_button_text);
                hasCompleted = true;
                answerCheckboxGroup.setVisibility(View.GONE);
                answerTextField.setVisibility(View.GONE);
                answerRadioGroup.setVisibility(View.GONE);
                question.setText(getString(R.string.end_toast_part_one) + score + getString(R.string.end_toast_part_two) + questions.length);
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
            values.put(QuizContract.Quiz.COLUMN_NAME_SCORE, (score / questions.length) * 100);

            db.delete(QuizContract.Quiz.TABLE_NAME, QuizContract.Quiz.COLUMN_NAME_QNAME + "=\'" + currentQuiz + "\'", null);

            db.insert(QuizContract.Quiz.TABLE_NAME, null, values);

            submit.setText(R.string.defualt_button_text);
            questionNumber = 0;
            hasCompleted = false;
            score = 0;

            startActivity(new Intent(Quiz.this, MainActivity.class));
        }

    }

    public void loadQuestion(int questionID) {
        answerRadioGroup.setVisibility(View.GONE);
        answerCheckboxGroup.setVisibility(View.GONE);
        answerTextField.setVisibility(View.GONE);

        question.setText(questions[questionID]);
        if (answers[questionID][1] == "mcr") {
            answerRadioGroup.setVisibility(View.VISIBLE);
            aRadio.setText(possibleAnswers[questionID][0]);
            bRadio.setText(possibleAnswers[questionID][1]);
            cRadio.setText(possibleAnswers[questionID][2]);
            dRadio.setText(possibleAnswers[questionID][3]);
        } else if (answers[questionID][1] == "mcc") {
            aCheckbox.setChecked(false);
            bCheckbox.setChecked(false);
            cCheckbox.setChecked(false);
            dCheckbox.setChecked(false);
            answerCheckboxGroup.setVisibility(View.VISIBLE);
            aCheckbox.setText(possibleAnswers[questionID][0]);
            bCheckbox.setText(possibleAnswers[questionID][1]);
            cCheckbox.setText(possibleAnswers[questionID][2]);
            dCheckbox.setText(possibleAnswers[questionID][3]);
        } else if (answers[questionID][1] == "txt") {
            answerTextField.setVisibility(View.VISIBLE);
            answerTextField.setText("");
        } else {
            Log.w("MainActivity/QLoader", "Invalid answer type in answers");
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

        if (answers[questionNumber][1] == "mcr") {
            if (answers[questionNumber][0] == "A") {
                return aRadio.isChecked();
            }
            if (answers[questionNumber][0] == "B") {
                return bRadio.isChecked();
            }
            if (answers[questionNumber][0] == "C") {
                return cRadio.isChecked();
            }
            if (answers[questionNumber][0] == "D") {
                return dRadio.isChecked();
            }
        } else if (answers[questionNumber][1] == "mcc") {
            boolean[] correctAnswer = {false, false, false, false};
            if (answers[questionNumber][0].contains("A")) {
                correctAnswer[0] = true;
            }
            if (answers[questionNumber][0].contains("B")) {
                correctAnswer[1] = true;
            }
            if (answers[questionNumber][0].contains("C")) {
                correctAnswer[2] = true;
            }
            if (answers[questionNumber][0].contains("D")) {
                correctAnswer[3] = true;
            }
            return (compareArrays(correctAnswer, checkboxChecked));

        } else if (answers[questionNumber][1] == "txt") {
            String correct = answers[questionNumber][0];
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
