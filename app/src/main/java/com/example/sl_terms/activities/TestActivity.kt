package com.example.sl_terms.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sl_terms.BusinessLogicTest
import com.example.sl_terms.ItemMoveCallback
import com.example.sl_terms.R
import com.example.sl_terms.adapters.RVAdapter
import com.example.sl_terms.models.AvailableTest
import com.example.sl_terms.models.Option
import com.example.sl_terms.models.Question
import kotlinx.android.synthetic.main.activity_test.*


class TestActivity : AppCompatActivity(), View.OnClickListener {
    private var myButton1: Button? = null
    private var myButton2: Button? = null
    private lateinit var editText: EditText
    
    private lateinit var rv: RecyclerView
    private lateinit var rv2: RecyclerView

    private lateinit var ID_questions: Array<AvailableTest>
    private lateinit var questions: Array<Question>
    private lateinit var options: Array<Option>
    private lateinit var Picture_null: Array<AvailableTest>
    private var idPicture: Int = 0
    private lateinit var Picture: Array<AvailableTest>
    private var idStudentStr: String? = null
    private var idStudent = 0
    private lateinit var idTestStr: String
    private var countQuestionsS: String? = null
    private var numberCorrectAnswersS: String? = null
    private var idSession = 0
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

//получение данных с предыдущей активити
        try {
            idSession = 0
            idTestStr = intent.getStringExtra("id_test") ?: "0"
            idStudentStr = intent.getStringExtra("id_student")
            val idTest = idTestStr.toInt()
            idStudent = idStudentStr?.toInt()!!
            //создание радио кнопок
            myButton1 = findViewById<View>(R.id.button1) as Button
            myButton2 = findViewById<View>(R.id.button2) as Button
            val textView = findViewById<View>(R.id.textView) as TextView
            ID_questions = BusinessLogicTest.blt.getIdQuestions(idTest)
            questions = BusinessLogicTest.blt.getQuestions(idTest)
            idPicture = questions.first().idPicture ?: 0
            val countQuestions = questions.size
            countQuestionsS = countQuestions.toString()
            textView.text = questions[0].text
            options = BusinessLogicTest.blt.getOptions(questions[0].id)
            val buttons = options.size
//            val rb = arrayOfNulls<AppCompatRadioButton>(buttons)
            val rgp = findViewById<View>(R.id.radioGroup) as RadioGroup
            editText = text_answer
            rgp.orientation = LinearLayout.VERTICAL
            if (questions.first().type == 0 || questions.first().type == 1 || questions.first().type == 2) {
                for (i in 0 until buttons) {
                    val rbn = if (questions.first().type == 2) CheckBox(this) else RadioButton(this)
                    rbn.id = options[i].id
                    rbn.text = options[i].name
                    val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                    rbn.layoutParams = params
                    if (i == 0 && questions.first().type != 2)
                        rbn.isChecked = true
                    rgp.addView(rbn)
                }
            }

            if (questions.first().type == 3) {
                editText.visibility = EditText.VISIBLE
            }
            if (questions.first().type == 4) {
                val layout = llm
//                layout.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 350)
                layout.visibility = LinearLayout.VISIBLE
                rv = recycler_view
                rv2 = recycler_view2
                val llm = LinearLayoutManager(this)
                val llm2 = LinearLayoutManager(this)
                rv.layoutManager = llm
                rv2.layoutManager = llm2

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
//                val rgp2 = findViewById<View>(R.id.radioGroup) as RadioGroup
//                val countQuestion2: Int = BusinessLogicTest.blt.nextQuestion()
                //отправляем ответ на сервер
//                val variant2 = rgp2.checkedRadioButtonId
//                BusinessLogicTest.blt.answerToCurQuestion(idStudent, questions[countQuestion2 - 1].id, variant2, idSession)
                val intent = Intent(this@TestActivity, ResultActivity::class.java)
                numberCorrectAnswersS = BusinessLogicTest.blt.numberOfCorrectAnswers(idStudent, idTestStr.toInt()).toString()
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
                    numberCorrectAnswersS = BusinessLogicTest.blt.numberOfCorrectAnswers(id_student = idStudent, idTest = idTestStr.toInt()).toString()
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
                options = BusinessLogicTest.blt.getOptions(questions[countQuestion].id)
                val textView = findViewById<View>(R.id.textView) as TextView
                //проверка есть ли рисунок
                Picture_null = BusinessLogicTest.blt.getPictureNull(questions[countQuestion].id)
//                val result = Picture_null[0].id.toString()
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
                        if (i == 0 && questions[countQuestion].type != 2)
                            rbn1.isChecked = true
                        rgp1.addView(rbn1)
                        i++
                    }
                }
                if (questions[countQuestion].type == 3) {
                    editText.visibility = EditText.VISIBLE
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
                    val callback: ItemTouchHelper.Callback = ItemMoveCallback(adapterLeft)
                    val callback2: ItemTouchHelper.Callback = ItemMoveCallback(adapterRight)
                    val touchHelper = ItemTouchHelper(callback)
                    val touchHelper2 = ItemTouchHelper(callback2)
                    touchHelper.attachToRecyclerView(recycler_view)
                    touchHelper2.attachToRecyclerView(recycler_view2)
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