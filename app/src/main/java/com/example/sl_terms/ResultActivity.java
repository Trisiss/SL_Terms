package com.example.sl_terms;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;

import static com.example.sl_terms.BusinessLogicTest.blt;

public class ResultActivity extends AppCompatActivity {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);


        String numberCorrectAnswersS = getIntent().getStringExtra("numberCorrectAnswersS");
        String countQuestionsS =" ";
        countQuestionsS = getIntent().getStringExtra("countQuestions");


     //   int id_student = Integer.parseInt(id_studentS);


        TextView textView = (TextView) findViewById(R.id.textView6);

     //   String result = Integer.toString(blt.numberOfCorrectAnswers(id_student));
        textView.setText(numberCorrectAnswersS +"/" +countQuestionsS);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(ResultActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // закрываем эту активити
    }
}
