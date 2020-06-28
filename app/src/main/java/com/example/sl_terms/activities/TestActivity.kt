package com.example.sl_terms.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
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

    private val businessLogicTest: BusinessLogicTest = BusinessLogicTest()

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
            ID_questions = businessLogicTest.getIdQuestions(idTest)
            questions = businessLogicTest.getQuestions(idTest)
            idPicture = questions.first().idPicture ?: 0
            val countQuestions = questions.size
            countQuestionsS = countQuestions.toString()
            textView.text = questions[0].text
            options = businessLogicTest.getOptions(questions[0].id)
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
                rv = recycler_view
                rv2 = recycler_view2
                val llm = LinearLayoutManager(this)
                val llm2 = LinearLayoutManager(this)
                rv.layoutManager = llm
                rv2.layoutManager = llm2
                val optionLeft = mutableListOf<Option>()
                val optionRight = mutableListOf<Option>()

                for (i in 0 until options.size) {
                    if (options[i].type == 1) optionRight.add(options[i])
                    if (options[i].type == 0) optionLeft.add(options[i])
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
                endTest()
            }
            R.id.button2 -> {
                val rgp1 = findViewById<View>(R.id.radioGroup) as RadioGroup
                val countQuestion: Int = businessLogicTest.nextQuestion()
                when (questions[countQuestion - 1].type) {
                    0, 1 -> {
                        //отправляем ответ на сервер
                        val variant2 = rgp1.checkedRadioButtonId
                        businessLogicTest.answerToCurQuestion(idStudent, questions[countQuestion - 1].id, variant2, "", idSession)
                        Log.e("TAG", variant2.toString())
                    }
                    2 -> {
                        val countButtons = rgp1.childCount
                        var checkedOptions = ""
                        for (i in 0 until countButtons) {
                            val option = rgp1.getChildAt(i) as CheckBox
                            if (option.isChecked) {
                                checkedOptions += "${option.id},"
                            }
                        }
                        Log.e("TAG", checkedOptions[checkedOptions.lastIndex].toString())
                        if (checkedOptions[checkedOptions.lastIndex] == ',') checkedOptions = checkedOptions.dropLast(1)
                        businessLogicTest.answerToCurQuestion(idStudent, questions[countQuestion - 1].id, rgp1.getChildAt(0).id, checkedOptions, idSession)
                        Log.e("TAG", checkedOptions)
                    }
                    3 -> {
                        val textAnswer = text_answer.text
                        businessLogicTest.answerToCurQuestion(idStudent, questions[countQuestion - 1].id, options.first().id, textAnswer.toString().toUpperCase(), idSession)
                        Log.e("TAG", textAnswer.toString())
                    }
                    4 -> {
                        val resView = recycler_view
                        val resView2 = recycler_view2
                        val countOptions = resView.childCount
                        var otherAnswers = ""
                        for (i in 0 until countOptions) {
                            val option = resView.getChildAt(i).id
                            val option2 = resView2.getChildAt(i).id
                            otherAnswers += "${option}:$option2,"
                        }
                        if (otherAnswers[otherAnswers.lastIndex] == ',') otherAnswers = otherAnswers.dropLast(1)
                        businessLogicTest.answerToCurQuestion(idStudent, questions[countQuestion - 1].id, options.first().id, otherAnswers, idSession)
                        Log.e("TAG", otherAnswers)
                    }
                }
                //отправляем ответ на сервер
//                val variant = rgp1.checkedRadioButtonId
//                BusinessLogicTest.blt.answerToCurQuestion(idStudent, questions[countQuestion - 1].id, variant, idSession)
                //если номер вопроса равен количеству вопросов, открыть активити с результатами
                if (countQuestion == questions.size) {
                    val intent2 = Intent(this@TestActivity, ResultActivity::class.java)
                    numberCorrectAnswersS = BusinessLogicTest.businessLogicTest.numberOfCorrectAnswers(id_student = idStudent, idTest = idTestStr.toInt()).toString()
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
                options = BusinessLogicTest.businessLogicTest.getOptions(questions[countQuestion].id)
                val textView = findViewById<View>(R.id.textView) as TextView
                //проверка есть ли рисунок
                Picture_null = BusinessLogicTest.businessLogicTest.getPictureNull(questions[countQuestion].id)
//                val result = Picture_null[0].id.toString()
                //если рисунок есть то
                Picture = BusinessLogicTest.businessLogicTest.getPicture(questions[countQuestion].id)
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
                    val optionLeft = mutableListOf<Option>()
                    val optionRight = mutableListOf<Option>()

                    for (i in 0 until options.size) {
                        if (options[i].type == 1) optionRight.add(options[i])
                        if (options[i].type == 0) optionLeft.add(options[i])
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

    fun endTest() {
        val rgp2 = findViewById<View>(R.id.radioGroup) as RadioGroup
        val countQuestion2: Int = businessLogicTest.nextQuestion()
        when (questions[countQuestion2 - 1].type) {
            0, 1 -> {
                //отправляем ответ на сервер
                val variant2 = rgp2.checkedRadioButtonId
                businessLogicTest.answerToCurQuestion(idStudent, questions[countQuestion2 - 1].id, variant2, "", idSession)
            }
            2 -> {
                val countButtons = rgp2.childCount
                var checkedOptions = ""
                for (i in 0 until countButtons) {
                    val option = rgp2.getChildAt(i) as CheckBox
                    if (option.isChecked) {
                        checkedOptions += "${option.id},"
                    }
                }
                if (checkedOptions[checkedOptions.lastIndex] == ',') checkedOptions = checkedOptions.dropLast(1)
                businessLogicTest.answerToCurQuestion(idStudent, questions[countQuestion2 - 1].id, rgp2.getChildAt(0).id, checkedOptions, idSession)
            }
            3 -> {
                val textAnswer = text_answer.text
                businessLogicTest.answerToCurQuestion(idStudent, questions[countQuestion2 - 1].id, options.first().id, textAnswer.toString().toUpperCase(), idSession)
            }
            4 -> {
                val resView = recycler_view
                val resView2 = recycler_view2
                val countOptions = resView.childCount
                var otherAnswers = ""
                for (i in 0 until countOptions) {
                    val option = resView.getChildAt(i).id
                    val option2 = resView2.getChildAt(i).id
                    otherAnswers += "${option}:$option2,"
                }
                if (otherAnswers[otherAnswers.lastIndex] == ',') otherAnswers = otherAnswers.dropLast(1)
                businessLogicTest.answerToCurQuestion(idStudent, questions[countQuestion2 - 1].id, options.first().id, otherAnswers, idSession)
                Log.e("TAG", otherAnswers)
            }
        }

        val intent = Intent(this@TestActivity, ResultActivity::class.java)
        numberCorrectAnswersS = BusinessLogicTest.businessLogicTest.numberOfCorrectAnswers(idStudent, idTestStr.toInt()).toString()
        intent.putExtra("numberCorrectAnswersS", numberCorrectAnswersS)
        intent.putExtra("countQuestions", countQuestionsS)
        startActivity(intent)
        finish()
    }

    public override fun onStop() {
        super.onStop()
        idSession = 2
    }

    override fun onResume() {
        super.onResume()
        if (idSession == 2) endTest()
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setMessage("Вы действительно хотите выйти?")
                .setCancelable(false)
                .setPositiveButton("Да") { dialog, id ->
                    val intent = Intent(this@TestActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("Нет", null)
                .show()
    }
}