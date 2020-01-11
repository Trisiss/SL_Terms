package com.example.sl_terms;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatRadioButton;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;
import android.widget.RadioGroup;
import android.view.View.OnClickListener;

import static com.example.sl_terms.BusinessLogicTest.blt;

public class CheckInActivity extends AppCompatActivity{

    private  Button myButton;
    RadioButton rbn, rbn1;
    private  RadioGroup rgp, rgp1;

    AvailableTest[] availableTest;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_in);



        availableTest = blt.getAvailableTest();
        int buttons = availableTest.length;
        if(buttons == 0){
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Нет подключения к интернету", Toast.LENGTH_LONG );
            toast.show();
            finish();
        }

        AppCompatRadioButton[] rb = new AppCompatRadioButton[buttons];

        RadioGroup rgp = (RadioGroup) findViewById(R.id.radiogroup);
        rgp.setOrientation(LinearLayout.VERTICAL);


        for (int i = 0; i < buttons; i++) {
            RadioButton rbn = new RadioButton(this);
            rbn.setId(availableTest[i].id);
            rbn.setText(availableTest[i].name);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            rbn.setLayoutParams(params);
            rgp.addView(rbn);
        }


       // blt.startTest("Тарасенко", 1);
      /*  radioButton = (RadioButton) findViewById(R.id.radioButton8);
        radioButton.setText(blt.getAvailableTest()[0].name);*/


        addListenerOnButton();
    }


    public void addListenerOnButton() {

           // radioGroup = (RadioGroup) findViewById(R.id.radiogroup);

            myButton = (Button) findViewById(R.id.button2);

            final EditText editText = (EditText) findViewById(R.id.editText);

            myButton.setOnClickListener(new OnClickListener() {


                @Override
                public void onClick(View v) {

                    RadioGroup rgp1 = (RadioGroup) findViewById(R.id.radiogroup);
                    int selectedId = 0;

                    selectedId = rgp1.getCheckedRadioButtonId();

                    String numberAsString = Integer.toString(selectedId);

                if (editText.getText().toString().equals("")) {
                }
                else if (selectedId!=-1) {


                    int id_student = blt.startTest(editText.getText().toString());
                    String id_studentS = Integer.toString(id_student);
                    rbn1 = (RadioButton) findViewById(selectedId);

                    Intent intent = new Intent(CheckInActivity.this, TestActivity.class);
                    intent.putExtra("id_test", numberAsString);
                    intent.putExtra("id_student", id_studentS);
                    startActivity(intent);

                    Toast.makeText(CheckInActivity.this, numberAsString
                   // myButton1.getText()
                            , Toast.LENGTH_SHORT).show();

                }


              /*      // get selected radio button from radioGroup
                    int selectedId = rgp.getCheckedRadioButtonId();

                    // find the radiobutton by returned id
                    rbn = (RadioButton) findViewById(selectedId);

                    Toast.makeText(CheckInActivity.this,
                            rbn.getText(), Toast.LENGTH_SHORT).show();


*//*
                    Intent intent = new Intent(CheckInActivity.this, TestActivity.class);
                    startActivity(intent);*/
                }
           });

        }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(CheckInActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // закрываем эту активити
    }
}