package com.example.sl_terms.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatRadioButton
import android.view.View
import android.widget.*
import com.example.sl_terms.BusinessLogicTest
import com.example.sl_terms.R
import com.example.sl_terms.models.AvailableTest

class CheckInActivity : AppCompatActivity() {

    private var myButton: Button? = null
    var rbn: RadioButton? = null
    var rbn1: RadioButton? = null
    private val rgp: RadioGroup? = null
    private val rgp1: RadioGroup? = null
    lateinit var availableTest: Array<AvailableTest>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_check_in)
        availableTest = BusinessLogicTest.blt?.availableTest!!
        val buttons = availableTest.size
        if (buttons == 0) {
            val toast = Toast.makeText(applicationContext,
                    "Нет подключения к интернету", Toast.LENGTH_LONG)
            toast.show()
            finish()
        }
        val rb = arrayOfNulls<AppCompatRadioButton>(buttons)
        val rgp = findViewById<View>(R.id.radiogroup) as RadioGroup
        rgp.orientation = LinearLayout.VERTICAL
        for (i in 0 until buttons) {
            val rbn = RadioButton(this)
            rbn.id = availableTest[i].id
            rbn.text = availableTest[i].name
            val params = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            rbn.layoutParams = params
            rgp.addView(rbn)
        }
        // blt.startTest("Тарасенко", 1);
/*  radioButton = (RadioButton) findViewById(R.id.radioButton8);
        radioButton.setText(blt.getAvailableTest()[0].name);*/addListenerOnButton()
    }

    fun addListenerOnButton() { // radioGroup = (RadioGroup) findViewById(R.id.radiogroup);
        myButton = findViewById<View>(R.id.button2) as Button
        val selectUser = findViewById<View>(R.id.selectUser) as Spinner
        myButton!!.setOnClickListener {
            val rgp1 = findViewById<View>(R.id.radiogroup) as RadioGroup
            var selectedId = 0
            selectedId = rgp1.checkedRadioButtonId
            val numberAsString = Integer.toString(selectedId)
            if (selectUser.prompt.toString() == "") {
            } else if (selectedId != -1) {
                val id_student: Int = BusinessLogicTest.blt?.startTest(selectUser.prompt.toString())!!
                val id_studentS = Integer.toString(id_student)
                rbn1 = findViewById<View>(selectedId) as RadioButton
                val intent = Intent(this@CheckInActivity, TestActivity::class.java)
                intent.putExtra("id_test", numberAsString)
                intent.putExtra("id_student", id_studentS)
                startActivity(intent)
                Toast.makeText(this@CheckInActivity, numberAsString // myButton1.getText()
                        , Toast.LENGTH_SHORT).show()
            }
            /*      // get selected radio button from radioGroup
                    int selectedId = rgp.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    rbn = (RadioButton) findViewById(selectedId);

                    Toast.makeText(CheckInActivity.this,
                            rbn.getText(), Toast.LENGTH_SHORT).show();


*/
/*
                    Intent intent = new Intent(CheckInActivity.this, TestActivity.class);
                    startActivity(intent);*/
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this@CheckInActivity, MainActivity::class.java)
        startActivity(intent)
        finish() // закрываем эту активити
    }
}