package com.example.sl_terms.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatRadioButton
import android.util.Base64
import android.view.View
import android.widget.*
import com.example.sl_terms.BusinessLogicTest
import com.example.sl_terms.R
import com.example.sl_terms.models.AvailableTest

class TestActivity : AppCompatActivity(), View.OnClickListener {
    var myButton1: Button? = null
    var myButton2: Button? = null
    var rgp: RadioGroup? = null
    var rgp1: RadioGroup? = null
    var rbn: RadioButton? = null
    var rbn1: RadioButton? = null
    lateinit var ID_questions: Array<AvailableTest>
    lateinit var IdVariantVariantName: Array<AvailableTest>
    lateinit var Picture_null: Array<AvailableTest>
    lateinit var Picture: Array<AvailableTest>
    var id_studentS: String? = null
    var id_student = 0
    var id_testS: String? = null
    var countQuestionsS: String? = null
    var numberCorrectAnswersS: String? = null
    var id_session = 0
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        //установить по умолчанию включенным первую  радиокнопку, чтобы нельзя было не дать ответ
//   radioButton.setChecked(true);
//получение данных с превой активити
        try {
            id_session = 0
            id_testS = intent.getStringExtra("id_test")
            id_studentS = intent.getStringExtra("id_student")
            val id_test = id_testS?.toInt()!!
            id_student = id_studentS?.toInt()!!
            //создание радио кнопок
            myButton1 = findViewById<View>(R.id.button1) as Button
            myButton2 = findViewById<View>(R.id.button2) as Button
            val textView = findViewById<View>(R.id.textView) as TextView
            ID_questions = BusinessLogicTest.blt!!.getIdQuestions(id_test)
            Picture_null = BusinessLogicTest.blt!!.getPictureNull(ID_questions[0].id)
            val result = Integer.toString(Picture_null[0].id)
            val countQuestions = ID_questions.size
            countQuestionsS = Integer.toString(countQuestions)
            textView.text = ID_questions[0].name
            IdVariantVariantName = BusinessLogicTest.blt!!.getIdVariantVariantName(ID_questions[0].id)
            val buttons = IdVariantVariantName.size
            val rb = arrayOfNulls<AppCompatRadioButton>(buttons)
            val rgp = findViewById<View>(R.id.radioGroup) as RadioGroup
            rgp.orientation = LinearLayout.VERTICAL
            for (i in 0 until buttons) {
                val rbn = RadioButton(this)
                rbn.id = IdVariantVariantName[i].id
                rbn.text = IdVariantVariantName[i].name
                val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                rbn.layoutParams = params
                rgp.addView(rbn)
            }
            myButton1!!.setOnClickListener(this)
            myButton2!!.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
            val toast = Toast.makeText(applicationContext,
                    "Нет подключения к интернету", Toast.LENGTH_LONG)
            toast.show()
            finish()
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button1 -> {
                //перед закрытием теста отправить последний вопрос на сервер
                val rgp2 = findViewById<View>(R.id.radioGroup) as RadioGroup
                val countQuestion2: Int = BusinessLogicTest.blt?.nextQuestion()!!
                //отправляем ответ на сервер
                val variant2 = rgp2.checkedRadioButtonId
                BusinessLogicTest.blt!!.answerToCurQuestion(id_student, ID_questions[countQuestion2 - 1].id, variant2, id_session)
                val intent = Intent(this@TestActivity, ResultActivity::class.java)
                numberCorrectAnswersS = Integer.toString(BusinessLogicTest.blt!!.numberOfCorrectAnswers(id_student))
                intent.putExtra("numberCorrectAnswersS", numberCorrectAnswersS)
                intent.putExtra("countQuestions", countQuestionsS)
                startActivity(intent)
            }
            R.id.button2 -> {
                val rgp1 = findViewById<View>(R.id.radioGroup) as RadioGroup
                val countQuestion: Int = BusinessLogicTest.blt!!.nextQuestion()
                //отправляем ответ на сервер
                val variant = rgp1.checkedRadioButtonId
                BusinessLogicTest.blt!!.answerToCurQuestion(id_student, ID_questions[countQuestion - 1].id, variant, id_session)
                //если номер вопроса равен количеству вопросов, открыть активити с результатами
                if (countQuestion == ID_questions.size) {
                    val intent2 = Intent(this@TestActivity, ResultActivity::class.java)
                    numberCorrectAnswersS = Integer.toString(BusinessLogicTest.blt!!.numberOfCorrectAnswers(id_student))
                    intent2.putExtra("numberCorrectAnswersS", numberCorrectAnswersS)
                    intent2.putExtra("countQuestions", countQuestionsS)
                    startActivity(intent2)
//                    break
                }
                //удаляем все радио кнопки
                rgp1.removeAllViews()
                //переходим к следующему вопросу
                IdVariantVariantName = BusinessLogicTest.blt!!.getIdVariantVariantName(ID_questions[countQuestion].id)
                val textView = findViewById<View>(R.id.textView) as TextView
                //проверка есть ли рисунок
                Picture_null = BusinessLogicTest.blt!!.getPictureNull(ID_questions[countQuestion].id)
                val result = Integer.toString(Picture_null[0].id)
                //если рисунок есть то
                Picture = BusinessLogicTest.blt!!.getPicture(ID_questions[countQuestion].id)
                val imageView = findViewById<View>(R.id.imageView) as ImageView
                imageView.setImageBitmap(null)
                imageView.destroyDrawingCache()
                if (Picture_null[0].id != 0) {
                    val decodedString = Base64.decode(Picture[0].name, Base64.DEFAULT)
                    val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    imageView.setImageBitmap(decodedByte)
                }
                textView.text = ID_questions[countQuestion].name
                //динамическое создание радио кнопок
                val buttons = IdVariantVariantName.size
                val rb = arrayOfNulls<AppCompatRadioButton>(buttons)
                rgp1.orientation = LinearLayout.VERTICAL
                var i = 0
                while (i < buttons) {
                    val rbn1 = RadioButton(this)
                    rbn1.id = IdVariantVariantName[i].id
                    rbn1.text = IdVariantVariantName[i].name
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    rbn1.layoutParams = params
                    rgp1.addView(rbn1)
                    i++
                }
            }
        }
    }

    public override fun onStop() {
        super.onStop()
        id_session = 2
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    val intent = Intent(this@TestActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("No", null)
                .show()
    }
}