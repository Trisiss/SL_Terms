package com.example.sl_terms;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DataBaseTest {
    private static final String GET_AVAILABLE_TEST = "http://btidb.ru/available_tests.php";
    private static final String GET_ID_QUESTIONS = "http://btidb.ru/id_questions.php?id_test=";
    private static final String GET_TEXT_QUESTION = "http://btidb.ru/name_question.php?id_question=";
    private static final String INSERT_STUDENT = "http://btidb.ru/Insert_student.php?surname=";
    private static final String GET_ID_VARIANT_NAME_VARIANT = "http://btidb.ru/id_variant_and_variant_name.php?id_question=";
    private static final String ANSWER_TO_CUR_QUESTION = "http://btidb.ru/insert_answer.php?id_student=";
    private static final String NUMBER_OF_CORRECT_ANSWERS = "http://btidb.ru/number_of_correct_answers.php?id_student=";
    private static final String PICTURE = "http://btidb.ru/picture.php?id_question=";
    private static final String PICTURE_NULL = "http://btidb.ru/null_picture.php?id_question=";


    private final OkHttpClient client = new OkHttpClient();

    static public DataBaseTest dbt;

    String getResponse(String url) {
        String strResponse = "";
        Request request = new Request.Builder().url(url).build();
        try {
            Response response = client.newCall(request).execute();
            strResponse = response.body().string();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strResponse;
    }
    //получить список доступных тестов
    AvailableTest[] getAvailableTest() {
        ArrayList<AvailableTest> listTest = new ArrayList<>();
        try {
            JSONArray dataJsonArr = new JSONObject(getResponse(GET_AVAILABLE_TEST))
                    .getJSONArray("tests");
            for (int i = 0; i < dataJsonArr.length(); i++) {
                JSONObject testJSON = dataJsonArr.getJSONObject(i);
                AvailableTest availabletest = new AvailableTest();
                availabletest.id = testJSON.getInt("id");
                availabletest.name = testJSON.getString("name");

                System.out.println(availabletest.id);
                System.out.println(availabletest.name);
                System.out.println("11111111111");
                listTest.add(availabletest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listTest.toArray(new AvailableTest[listTest.size()]);
    }

    //вернуть все вопросы данного теста (айди)
    AvailableTest[] getIdQuestion(int id_test) {
        ArrayList<AvailableTest> listTest = new ArrayList<>();
        try {
            JSONArray dataJsonArr = new JSONObject(getResponse(GET_ID_QUESTIONS + id_test))
                    .getJSONArray("id_questions");
            for (int i = 0; i < dataJsonArr.length(); i++) {
                JSONObject testJSON = dataJsonArr.getJSONObject(i);
                AvailableTest availabletest = new AvailableTest();

                availabletest.id = testJSON.getInt("id");

                listTest.add(availabletest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listTest.toArray(new AvailableTest[listTest.size()]);
    }

    //получить текст вопроса по айди
    AvailableTest[] getTextQuestion(int id_question) {
        ArrayList<AvailableTest> listTest = new ArrayList<>();
        try {
            JSONArray dataJsonArr = new JSONObject(getResponse(GET_TEXT_QUESTION + id_question))
                    .getJSONArray("name_questions");
            for (int i = 0; i < dataJsonArr.length(); i++) {
                JSONObject testJSON = dataJsonArr.getJSONObject(i);
                AvailableTest availabletest = new AvailableTest();

                availabletest.name = testJSON.getString("text");

                listTest.add(availabletest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listTest.toArray(new AvailableTest[listTest.size()]);
    }


    //добавить студента в бд
    int insertStudent(String FIO) {
        int id_student = 0;
        ArrayList<AvailableTest> listTest = new ArrayList<>();
        try {
            JSONArray dataJsonArr = new JSONObject(getResponse(INSERT_STUDENT + FIO))
                    .getJSONArray("id_student");
            for (int i = 0; i < dataJsonArr.length(); i++) {
                JSONObject testJSON = dataJsonArr.getJSONObject(i);
                AvailableTest availabletest = new AvailableTest();

                availabletest.id = testJSON.getInt("id_student");
               // System.out.println("2222222222");
               // System.out.println(availabletest.id);
                listTest.add(availabletest);

                if(i == (dataJsonArr.length()-1)){
                    id_student = availabletest.id;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println(id_student);
        return id_student;
    }


    //получить варианты ответов на конкретный вопрос
    AvailableTest[] geIdVariantVariantName(int id_question) {
        ArrayList<AvailableTest> listTest = new ArrayList<>();
        try {
            JSONArray dataJsonArr = new JSONObject(getResponse(GET_ID_VARIANT_NAME_VARIANT + id_question))
                    .getJSONArray("variants");
            for (int i = 0; i < dataJsonArr.length(); i++) {
                JSONObject testJSON = dataJsonArr.getJSONObject(i);
                AvailableTest availabletest = new AvailableTest();
                availabletest.id = testJSON.getInt("id_variant");
                availabletest.name = testJSON.getString("variant_name");
                System.out.println(availabletest.id);
                System.out.println(availabletest.name);
                listTest.add(availabletest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listTest.toArray(new AvailableTest[listTest.size()]);
    }
    //отправить вариант ответа
    void answerToCurQuestion(int id_student, int id_question, int id_variant, int id_session){
        getResponse(ANSWER_TO_CUR_QUESTION + id_student + "&id_question=" + id_question + "&id_answer="
                + id_variant + "&id_session=" + id_session);
    }

    //

    //получить количество правильных ответов
    int numberOfCorrectAnswers(int id_student) {
        int number = 0;
        ArrayList<AvailableTest> listTest = new ArrayList<>();
        try {
            JSONArray dataJsonArr = new JSONObject(getResponse(NUMBER_OF_CORRECT_ANSWERS + id_student))
                    .getJSONArray("count");
            for (int i = 0; i < dataJsonArr.length(); i++) {
                JSONObject testJSON = dataJsonArr.getJSONObject(i);
                AvailableTest availabletest = new AvailableTest();

                availabletest.id = testJSON.getInt("count");

                listTest.add(availabletest);

                if(i == (dataJsonArr.length()-1)){
                    number = availabletest.id;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return number;
    }
    AvailableTest[] getPicture(int id_question) {
        ArrayList<AvailableTest> listTest = new ArrayList<>();
        try {
            JSONArray dataJsonArr = new JSONObject(getResponse(PICTURE + id_question))
                    .getJSONArray("variants");
            for (int i = 0; i < dataJsonArr.length(); i++) {
                JSONObject testJSON = dataJsonArr.getJSONObject(i);
                AvailableTest availabletest = new AvailableTest();
                availabletest.id = testJSON.getInt("id_picture");
                availabletest.name = testJSON.getString("picture");
                System.out.println(availabletest.id);
                System.out.println(availabletest.name);
                listTest.add(availabletest);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listTest.toArray(new AvailableTest[listTest.size()]);
    }
    AvailableTest[] getPictureNull(int id_question) {
        ArrayList<AvailableTest> listTest = new ArrayList<>();
        try {
            JSONArray dataJsonArr = new JSONObject(getResponse(PICTURE_NULL + id_question))
                    .getJSONArray("variants");
            for (int i = 0; i < dataJsonArr.length(); i++) {
                JSONObject testJSON = dataJsonArr.getJSONObject(i);
                AvailableTest availabletest = new AvailableTest();
                availabletest.id = testJSON.getInt("id_picture");
                System.out.println(availabletest.id);
                listTest.add(availabletest);
            }
        } catch (Exception e) {
            e.printStackTrace();
            AvailableTest availabletest = new AvailableTest();
            availabletest.id = 0;
            listTest.add(availabletest);
            return listTest.toArray(new AvailableTest[listTest.size()]);
        }
        return listTest.toArray(new AvailableTest[listTest.size()]);
    }
    boolean checkInternet() {
        return !getResponse(GET_AVAILABLE_TEST ).equals("");
    }
}
