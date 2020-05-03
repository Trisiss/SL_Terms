package com.example.sl_terms.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.sl_terms.R
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val numberCorrectAnswersS = intent.getStringExtra("numberCorrectAnswersS")
        var countQuestionsS = " "
        countQuestionsS = intent.getStringExtra("countQuestions")
        //   int id_student = Integer.parseInt(id_studentS);
        val textView = textView6
        //   String result = Integer.toString(blt.numberOfCorrectAnswers(id_student));
        textView.text = "$numberCorrectAnswersS/$countQuestionsS"
    }

    override fun onBackPressed() {
        val intent = Intent(this@ResultActivity, MainActivity::class.java)
        startActivity(intent)
        finish() // закрываем эту активити
    }
}