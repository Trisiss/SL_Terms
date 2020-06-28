package com.example.sl_terms.activities

import android.content.Intent
import android.graphics.Color.red
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.sl_terms.R
import kotlinx.android.synthetic.main.activity_result.*
import kotlin.math.roundToInt

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val numberCorrectAnswersS = intent.getStringExtra("numberCorrectAnswersS")
        var countQuestionsS = " "
        countQuestionsS = intent.getStringExtra("countQuestions")
        //   int id_student = Integer.parseInt(id_studentS);
        val quantityAnswer = txtQuantityAnswer
        val quantityValue = txtQuantityValue
        //   String result = Integer.toString(blt.numberOfCorrectAnswers(id_student));
        quantityAnswer.text = "$numberCorrectAnswersS из $countQuestionsS"
        val value: Float = (numberCorrectAnswersS.toFloat() * 100) / countQuestionsS.toFloat()
        quantityValue.text = value.roundToInt().toString()
        if (value < 25) quantityValue.setTextColor(ContextCompat.getColor(this, R.color.colorRed))
        if (value >= 25) quantityValue.setTextColor(ContextCompat.getColor(this, R.color.colorGreen))
    }

    override fun onBackPressed() {
        val intent = Intent(this@ResultActivity, MainActivity::class.java)
        startActivity(intent)
        finish() // закрываем эту активити
    }
}