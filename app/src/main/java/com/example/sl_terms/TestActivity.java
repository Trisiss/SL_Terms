package com.example.sl_terms;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRadioButton;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import static com.example.sl_terms.BusinessLogicTest.blt;


public class TestActivity extends AppCompatActivity implements View.OnClickListener{

    Button myButton1;
    Button myButton2;

    RadioGroup rgp, rgp1;
    RadioButton rbn, rbn1;
    AvailableTest[] ID_questions;
    AvailableTest[] IdVariantVariantName;
    AvailableTest[] Picture_null;
    AvailableTest[] Picture;
    String id_studentS;
    int id_student;
    String id_testS;
    String countQuestionsS;
    String numberCorrectAnswersS;

    int id_session;



    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
     //установить по умолчанию включенным первую  радиокнопку, чтобы нельзя было не дать ответ
     //   radioButton.setChecked(true);

    //получение данных с превой активити
        try {
            id_session=0;
            id_testS = getIntent().getStringExtra("id_test");
            id_studentS = getIntent().getStringExtra("id_student");
            int id_test = Integer.parseInt(id_testS);
            id_student = Integer.parseInt(id_studentS);

            //создание радио кнопок
            myButton1 = (Button) findViewById(R.id.button1);
            myButton2 = (Button) findViewById(R.id.button2);

            TextView textView = (TextView) findViewById(R.id.textView);


            ID_questions = blt.getIdQuestions(id_test);
            Picture_null = blt.getPictureNull(ID_questions[0].id);
            String result = Integer.toString(Picture_null[0].id);

            int countQuestions = ID_questions.length;
            countQuestionsS = Integer.toString(countQuestions);
            textView.setText(ID_questions[0].name);
            IdVariantVariantName = blt.getIdVariantVariantName(ID_questions[0].id);

            int buttons = IdVariantVariantName.length;
            AppCompatRadioButton[] rb = new AppCompatRadioButton[buttons];

            RadioGroup rgp = (RadioGroup) findViewById(R.id.radioGroup);
            rgp.setOrientation(LinearLayout.VERTICAL);


            for (int i = 0; i < buttons; i++) {
                RadioButton rbn = new RadioButton(this);
                rbn.setId(IdVariantVariantName[i].id);
                rbn.setText(IdVariantVariantName[i].name);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                rbn.setLayoutParams(params);
                rgp.addView(rbn);
            }

            myButton1.setOnClickListener(this);
            myButton2.setOnClickListener(this);
        }
        catch (Exception e) {
            e.printStackTrace();
            Toast toast = Toast.makeText(getApplicationContext(),
                    "Нет подключения к интернету", Toast.LENGTH_LONG );
            toast.show();
            finish();

        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button1:

                //перед закрытием теста отправить последний вопрос на сервер
                RadioGroup rgp2 = (RadioGroup) findViewById(R.id.radioGroup);
                int countQuestion2 = blt.nextQuestion();
                //отправляем ответ на сервер
                int variant2 = rgp2.getCheckedRadioButtonId();

                blt.answerToCurQuestion(id_student, ID_questions[countQuestion2-1].id, variant2, id_session);

                Intent intent = new Intent(TestActivity.this, ResultActivity.class);

                numberCorrectAnswersS = Integer.toString(blt.numberOfCorrectAnswers(id_student));
                intent.putExtra("numberCorrectAnswersS", numberCorrectAnswersS);
                intent.putExtra("countQuestions", countQuestionsS);
                startActivity(intent);
                break;

            case R.id.button2: //myTextView.setText("Вы нажали на 2-ю кнопку"); break;

                RadioGroup rgp1 = (RadioGroup) findViewById(R.id.radioGroup);
                int countQuestion = blt.nextQuestion();
                //отправляем ответ на сервер
                int variant = rgp1.getCheckedRadioButtonId();
                blt.answerToCurQuestion(id_student, ID_questions[countQuestion-1].id, variant, id_session);


                 //если номер вопроса равен количеству вопросов, открыть активити с результатами
                if (countQuestion==ID_questions.length){
                    Intent intent2 = new Intent(TestActivity.this, ResultActivity.class);

                    numberCorrectAnswersS = Integer.toString(blt.numberOfCorrectAnswers(id_student));
                    intent2.putExtra("numberCorrectAnswersS", numberCorrectAnswersS);
                    intent2.putExtra("countQuestions", countQuestionsS);

                    startActivity(intent2);
                    break;

                }
                //удаляем все радио кнопки
                rgp1.removeAllViews();
                //переходим к следующему вопросу

                IdVariantVariantName = blt.getIdVariantVariantName(ID_questions[countQuestion].id);
                TextView textView = (TextView) findViewById(R.id.textView);
                //проверка есть ли рисунок
                Picture_null = blt.getPictureNull(ID_questions[countQuestion].id);
                String result = Integer.toString(Picture_null[0].id);
                //если рисунок есть то
                Picture = blt.getPicture(ID_questions[countQuestion].id);
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(null);
                imageView.destroyDrawingCache();
                if (Picture_null[0].id!= 0) {

                    byte[] decodedString = Base64.decode(Picture[0].name, Base64.DEFAULT);
                    Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                    imageView.setImageBitmap(decodedByte);


                }

                textView.setText(ID_questions[countQuestion].name);

                //динамическое создание радио кнопок
                int buttons = IdVariantVariantName.length;
                AppCompatRadioButton[] rb = new AppCompatRadioButton[buttons];

                rgp1.setOrientation(LinearLayout.VERTICAL);

                for (int i = 0; i < buttons; i++) {
                    RadioButton rbn1 = new RadioButton(this);
                    rbn1.setId(IdVariantVariantName[i].id);
                    rbn1.setText(IdVariantVariantName[i].name);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
                    rbn1.setLayoutParams(params);
                    rgp1.addView(rbn1);
                }

        }
    }


    @Override
    public void onStop() {
        super.onStop();
        id_session=2;

    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(TestActivity.this, MainActivity.class);
                        startActivity(intent);
                        TestActivity.this.finish();

                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}
