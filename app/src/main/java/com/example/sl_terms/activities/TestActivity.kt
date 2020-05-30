package com.example.sl_terms.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.opengl.Visibility
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sl_terms.BusinessLogicTest
import com.example.sl_terms.R
import com.example.sl_terms.adapters.RVAdapter
import com.example.sl_terms.models.AvailableTest
import com.example.sl_terms.models.Option
import com.example.sl_terms.models.Question
import kotlinx.android.synthetic.main.activity_test.*


class TestActivity : AppCompatActivity(), View.OnClickListener {
    var myButton1: Button? = null
    var myButton2: Button? = null
    var rgp: RadioGroup? = null
    var rgp1: RadioGroup? = null
    var rbn: RadioButton? = null
    var rbn1: RadioButton? = null
    lateinit var editText: EditText

//    private var persons: MutableList<String>? = null
    private lateinit var rv: RecyclerView
    private lateinit var rv2: RecyclerView

    lateinit var ID_questions: Array<AvailableTest>
    lateinit var questions: Array<Question>
    lateinit var IdVariantVariantName: Array<AvailableTest>
    lateinit var options: Array<Option>
    lateinit var Picture_null: Array<AvailableTest>
    var pictureNull: Int = 0
    lateinit var Picture: Array<AvailableTest>
    var idStudentStr: String? = null
    var idStudent = 0
    var idTestStr: String? = null
    var countQuestionsS: String? = null
    var numberCorrectAnswersS: String? = null
    var idSession = 0
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)
        //установить по умолчанию включенным первую  радиокнопку, чтобы нельзя было не дать ответ
//   radioButton.setChecked(true);
//получение данных с превой активити
        try {
            idSession = 0
            idTestStr = intent.getStringExtra("id_test")
            idStudentStr = intent.getStringExtra("id_student")
            val idTest = idTestStr?.toInt()!!
            idStudent = idStudentStr?.toInt()!!
            //создание радио кнопок
            myButton1 = findViewById<View>(R.id.button1) as Button
            myButton2 = findViewById<View>(R.id.button2) as Button
            val textView = findViewById<View>(R.id.textView) as TextView
            ID_questions = BusinessLogicTest.blt.getIdQuestions(idTest)
            questions = BusinessLogicTest.blt.getQuestions(idTest)
//            Picture_null = BusinessLogicTest.blt.getPictureNull(ID_questions[0].id)
//            val result = Integer.toString(Picture_null[0].id)
            pictureNull = if (questions[0].idPicture == null) 0 else questions[0].idPicture!!
//            val countQuestions = ID_questions.size
            val countQuestions = questions.size
            countQuestionsS = countQuestions.toString()
//            textView.text = ID_questions[0].name
            textView.text = questions[0].text
//            IdVariantVariantName = BusinessLogicTest.blt.getIdVariantVariantName(ID_questions[0].id)
            options = BusinessLogicTest.blt.getOptions(questions[0].id)
//            val buttons = IdVariantVariantName.size
            val buttons = options.size
            val rb = arrayOfNulls<AppCompatRadioButton>(buttons)
            val rgp = findViewById<View>(R.id.radioGroup) as RadioGroup
            editText = text_answer
            rgp.orientation = LinearLayout.VERTICAL
            if (questions[0].type == 0 || questions[0].type == 1) {
                for (i in 0 until buttons) {
                    val rbn = if (questions[0].type == 0) RadioButton(this) else CheckBox(this)
//                rbn.id = IdVariantVariantName[i].id
                    rbn.id = options[i].id
//                rbn.text = IdVariantVariantName[i].name
                    rbn.text = options[i].name
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    rbn.layoutParams = params
                    rgp.addView(rbn)
                }
            }

            if (questions[0].type == 3) {
                editText.visibility = EditText.VISIBLE
            }
            if (questions[0].type == 4) {
                val layout = llm
//                layout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 350)
                layout.visibility = LinearLayout.VISIBLE
                rv = recycler_view
                rv2 = recycler_view2
                val llm = LinearLayoutManager(this)
                val llm2 = LinearLayoutManager(this)
                rv.layoutManager = llm
                rv2.layoutManager = llm2

//                initializeData()
//                initializeAdapter()
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
                val countQuestion2: Int = BusinessLogicTest.blt.nextQuestion()
                //отправляем ответ на сервер
//                val variant2 = rgp2.checkedRadioButtonId
//                BusinessLogicTest.blt.answerToCurQuestion(idStudent, questions[countQuestion2 - 1].id, variant2, idSession)
                val intent = Intent(this@TestActivity, ResultActivity::class.java)
                numberCorrectAnswersS = BusinessLogicTest.blt.numberOfCorrectAnswers(idStudent, idTestStr!!.toInt()).toString()
                intent.putExtra("numberCorrectAnswersS", numberCorrectAnswersS)
                intent.putExtra("countQuestions", countQuestionsS)
                startActivity(intent)
            }
            R.id.button2 -> {
                val rgp1 = findViewById<View>(R.id.radioGroup) as RadioGroup
                val countQuestion: Int = BusinessLogicTest.blt.nextQuestion()
                //отправляем ответ на сервер
//                val variant = rgp1.checkedRadioButtonId
//                BusinessLogicTest.blt.answerToCurQuestion(idStudent, questions[countQuestion - 1].id, variant, idSession)
                //если номер вопроса равен количеству вопросов, открыть активити с результатами
                if (countQuestion == questions.size) {
                    val intent2 = Intent(this@TestActivity, ResultActivity::class.java)
                    numberCorrectAnswersS = BusinessLogicTest.blt.numberOfCorrectAnswers(id_student = idStudent, idTest = idTestStr!!.toInt()).toString()
                    intent2.putExtra("numberCorrectAnswersS", numberCorrectAnswersS)
                    intent2.putExtra("countQuestions", countQuestionsS)
                    startActivity(intent2)
                }
                //удаляем все радио кнопки
                rgp1.removeAllViews()
                val layout = llm
                layout.visibility = LinearLayout.GONE
                editText.visibility = EditText.GONE
                //переходим к следующему вопросу
//                IdVariantVariantName = BusinessLogicTest.blt.getIdVariantVariantName(ID_questions[countQuestion].id)
                options = BusinessLogicTest.blt.getOptions(questions[countQuestion].id)
                val textView = findViewById<View>(R.id.textView) as TextView
                //проверка есть ли рисунок
                Picture_null = BusinessLogicTest.blt.getPictureNull(questions[countQuestion].id)
                val result = Picture_null[0].id.toString()
                //если рисунок есть то
                Picture = BusinessLogicTest.blt.getPicture(questions[countQuestion].id)
                val imageView = findViewById<View>(R.id.imageView) as ImageView
                imageView.setImageBitmap(null)
                imageView.destroyDrawingCache()
                if (Picture_null[0].id != 0) {
                    val decodedString = Base64.decode(Picture[0].name, Base64.DEFAULT)
                    val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
                    imageView.setImageBitmap(decodedByte)
                }
                textView.text = questions[countQuestion].text
                //динамическое создание радио кнопок
                val buttons = options.size
                val rb = arrayOfNulls<AppCompatRadioButton>(buttons)
                rgp1.orientation = LinearLayout.VERTICAL
                var i = 0
                if (questions[countQuestion].type == 0 || questions[countQuestion].type == 1 || questions[countQuestion].type == 2) {
                    while (i < buttons) {
                        val rbn1 = if (questions[countQuestion].type == 2) CheckBox(this) else RadioButton(this)
                        rbn1.id = options[i].id
                        rbn1.text = options[i].name
                        val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                        rbn1.layoutParams = params
                        rgp1.addView(rbn1)
                        i++
                    }
                }
                if (questions[countQuestion].type == 3) {
                    editText = EditText(this)
                    rgp1.addView(editText)
                }
                if (questions[countQuestion].type == 4) {
                    val layout = llm
//                layout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 350)
                    rv = recycler_view
                    rv2 = recycler_view2
                    val llm = LinearLayoutManager(this)
                    val llm2 = LinearLayoutManager(this)
                    rv.layoutManager = llm
                    rv2.layoutManager = llm2
                    var optionLeft = mutableListOf<Option>()
                    var optionRight = mutableListOf<Option>()

                    for (i in 0 until options.size) {
                        if (i >= (options.size / 2)) optionRight.add(options[i])
                        if (i < (options.size / 2)) optionLeft.add(options[i])
                    }

                    val adapterLeft = RVAdapter(options = optionLeft)
                    val adapterRight = RVAdapter(options = optionRight)
                    rv.adapter = adapterLeft
                    rv2.adapter = adapterRight
                    layout.visibility = LinearLayout.VISIBLE
                }

            }
        }
    }

    public override fun onStop() {
        super.onStop()
        idSession = 2
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