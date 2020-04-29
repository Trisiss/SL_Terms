package com.example.sl_terms

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.*

class DataBaseTest {
    private val client = OkHttpClient()
    fun getResponse(url: String?): String {
        var strResponse = ""
        val request = Request.Builder().url(url).build()
        try {
            val response = client.newCall(request).execute()
            strResponse = response.body()!!.string()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strResponse
    }

    //получить список доступных тестов
    val availableTest: Array<AvailableTest>
        get() {
            val listTest = ArrayList<AvailableTest>()
            try {
                val dataJsonArr = JSONObject(getResponse(GET_AVAILABLE_TEST))
                        .getJSONArray("tests")
                for (i in 0 until dataJsonArr.length()) {
                    val testJSON = dataJsonArr.getJSONObject(i)
                    val availabletest = AvailableTest()
                    availabletest.id = testJSON.getInt("id")
                    availabletest.name = testJSON.getString("name")
                    println(availabletest.id)
                    println(availabletest.name)
                    println("11111111111")
                    listTest.add(availabletest)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return listTest.toTypedArray()
        }

    //вернуть все вопросы данного теста (айди)
    fun getIdQuestion(id_test: Int): Array<AvailableTest> {
        val listTest = ArrayList<AvailableTest>()
        try {
            val dataJsonArr = JSONObject(getResponse(GET_ID_QUESTIONS + id_test))
                    .getJSONArray("id_questions")
            for (i in 0 until dataJsonArr.length()) {
                val testJSON = dataJsonArr.getJSONObject(i)
                val availabletest = AvailableTest()
                availabletest.id = testJSON.getInt("id")
                listTest.add(availabletest)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listTest.toTypedArray()
    }

    //получить текст вопроса по айди
    fun getTextQuestion(id_question: Int): Array<AvailableTest> {
        val listTest = ArrayList<AvailableTest>()
        try {
            val dataJsonArr = JSONObject(getResponse(GET_TEXT_QUESTION + id_question))
                    .getJSONArray("name_questions")
            for (i in 0 until dataJsonArr.length()) {
                val testJSON = dataJsonArr.getJSONObject(i)
                val availabletest = AvailableTest()
                availabletest.name = testJSON.getString("text")
                listTest.add(availabletest)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listTest.toTypedArray()
    }

    //добавить студента в бд
    fun insertStudent(FIO: String): Int {
        var id_student = 0
        val listTest = ArrayList<AvailableTest>()
        try {
            val dataJsonArr = JSONObject(getResponse(INSERT_STUDENT + FIO))
                    .getJSONArray("id_student")
            for (i in 0 until dataJsonArr.length()) {
                val testJSON = dataJsonArr.getJSONObject(i)
                val availabletest = AvailableTest()
                availabletest.id = testJSON.getInt("id_student")
                // System.out.println("2222222222");
// System.out.println(availabletest.id);
                listTest.add(availabletest)
                if (i == dataJsonArr.length() - 1) {
                    id_student = availabletest.id
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        println(id_student)
        return id_student
    }

    //получить варианты ответов на конкретный вопрос
    fun geIdVariantVariantName(id_question: Int): Array<AvailableTest> {
        val listTest = ArrayList<AvailableTest>()
        try {
            val dataJsonArr = JSONObject(getResponse(GET_ID_VARIANT_NAME_VARIANT + id_question))
                    .getJSONArray("variants")
            for (i in 0 until dataJsonArr.length()) {
                val testJSON = dataJsonArr.getJSONObject(i)
                val availabletest = AvailableTest()
                availabletest.id = testJSON.getInt("id_variant")
                availabletest.name = testJSON.getString("variant_name")
                println(availabletest.id)
                println(availabletest.name)
                listTest.add(availabletest)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listTest.toTypedArray()
    }

    //отправить вариант ответа
    fun answerToCurQuestion(id_student: Int, id_question: Int, id_variant: Int, id_session: Int) {
        getResponse(ANSWER_TO_CUR_QUESTION + id_student + "&id_question=" + id_question + "&id_answer="
                + id_variant + "&id_session=" + id_session)
    }

    //
//получить количество правильных ответов
    fun numberOfCorrectAnswers(id_student: Int): Int {
        var number = 0
        val listTest = ArrayList<AvailableTest>()
        try {
            val dataJsonArr = JSONObject(getResponse(NUMBER_OF_CORRECT_ANSWERS + id_student))
                    .getJSONArray("count")
            for (i in 0 until dataJsonArr.length()) {
                val testJSON = dataJsonArr.getJSONObject(i)
                val availabletest = AvailableTest()
                availabletest.id = testJSON.getInt("count")
                listTest.add(availabletest)
                if (i == dataJsonArr.length() - 1) {
                    number = availabletest.id
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return number
    }

    fun getPicture(id_question: Int): Array<AvailableTest> {
        val listTest = ArrayList<AvailableTest>()
        try {
            val dataJsonArr = JSONObject(getResponse(PICTURE + id_question))
                    .getJSONArray("variants")
            for (i in 0 until dataJsonArr.length()) {
                val testJSON = dataJsonArr.getJSONObject(i)
                val availabletest = AvailableTest()
                availabletest.id = testJSON.getInt("id_picture")
                availabletest.name = testJSON.getString("picture")
                println(availabletest.id)
                println(availabletest.name)
                listTest.add(availabletest)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listTest.toTypedArray()
    }

    fun getPictureNull(id_question: Int): Array<AvailableTest> {
        val listTest = ArrayList<AvailableTest>()
        try {
            val dataJsonArr = JSONObject(getResponse(PICTURE_NULL + id_question))
                    .getJSONArray("variants")
            for (i in 0 until dataJsonArr.length()) {
                val testJSON = dataJsonArr.getJSONObject(i)
                val availabletest = AvailableTest()
                availabletest.id = testJSON.getInt("id_picture")
                println(availabletest.id)
                listTest.add(availabletest)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            val availabletest = AvailableTest()
            availabletest.id = 0
            listTest.add(availabletest)
            return listTest.toTypedArray()
        }
        return listTest.toTypedArray()
    }

    fun checkInternet(): Boolean {
        return getResponse(GET_AVAILABLE_TEST) != ""
    }

    companion object {
        private const val GET_AVAILABLE_TEST = "http://btidb.ru/available_tests.php"
        private const val GET_ID_QUESTIONS = "http://btidb.ru/id_questions.php?id_test="
        private const val GET_TEXT_QUESTION = "http://btidb.ru/name_question.php?id_question="
        private const val INSERT_STUDENT = "http://btidb.ru/Insert_student.php?surname="
        private const val GET_ID_VARIANT_NAME_VARIANT = "http://btidb.ru/id_variant_and_variant_name.php?id_question="
        private const val ANSWER_TO_CUR_QUESTION = "http://btidb.ru/insert_answer.php?id_student="
        private const val NUMBER_OF_CORRECT_ANSWERS = "http://btidb.ru/number_of_correct_answers.php?id_student="
        private const val PICTURE = "http://btidb.ru/picture.php?id_question="
        private const val PICTURE_NULL = "http://btidb.ru/null_picture.php?id_question="
        var dbt: DataBaseTest? = null
    }
}