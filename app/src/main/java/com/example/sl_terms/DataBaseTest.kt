package com.example.sl_terms

import com.example.sl_terms.models.AvailableTest
import com.example.sl_terms.models.Option
import com.example.sl_terms.models.Question
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.*

class DataBaseTest {
    private val client = OkHttpClient()
    fun getResponse(url: String): String {
        var strResponse = ""
        val request = Request.Builder().url(url).build()
        try {
            val response = client.newCall(request).execute()
            strResponse = response.body()?.string().toString()
            println("$url -- ${response.code()}")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strResponse
    }

    fun getResponseObj(url: String): Any {
        data class Response (val code: Int, val body: String)
        var responseCode: Int = 200
        var responseStr: String = ""
        val request = Request.Builder().url(url).build()
        try {
            val response = client.newCall(request).execute()
            responseCode = response.code()
            if (response.body() != null) {
                responseStr = response.body().toString()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Response(code = responseCode, body = responseStr)
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
                    println(testJSON)
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
            // Test data
//            val availabletest = AvailableTest()
//            availabletest.id = 0
//            availabletest.name = "Test test"
//            listTest.add(AvailableTest())
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

    fun getQuestions(id_test: Int): Array<Question> {
        var listQuestions: List<Question> = listOf()
        try {
            val listType: Type = Types.newParameterizedType(List::class.java, Question::class.java)
            val dataJson = getResponse(GET_ID_QUESTIONS + id_test)
            val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
            val questionAdapter: JsonAdapter<List<Question>> = moshi.adapter(listType)
            listQuestions = questionAdapter.fromJson(dataJson)!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listQuestions.toTypedArray()
    }

    //добавить студента в бд
    fun insertStudent(FIO: String): Int {
        var id_student = 0
        try {
            val dataJson = JSONObject(getResponse(INSERT_STUDENT + FIO))
            id_student = dataJson.getInt("idStudent")
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

    fun getOptions(id_question: Int): Array<Option> {
        var listOptions: List<Option> = listOf()
        try {
            val listType: Type = Types.newParameterizedType(List::class.java, Option::class.java)
            val dataJson = getResponse(GET_ID_VARIANT_NAME_VARIANT + id_question)
            val moshi = Moshi.Builder()
                    .add(KotlinJsonAdapterFactory())
                    .build()
            val optionAdapter: JsonAdapter<List<Option>> = moshi.adapter(listType)
            listOptions = optionAdapter.fromJson(dataJson)!!
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listOptions.toTypedArray()
    }

    //отправить вариант ответа
    fun answerToCurQuestion(id_student: Int, id_question: Int, id_variant: Int, id_session: Int) {
        getResponse(ANSWER_TO_CUR_QUESTION + id_student + "&id_question=" + id_question + "&id_answer="
                + id_variant + "&id_session=" + id_session)
    }

    //
//получить количество правильных ответов
    fun numberOfCorrectAnswers(id_student: Int, idTest: Int): Int {
        var number = 0
        try {
            val dataJson = JSONObject(getResponse("$NUMBER_OF_CORRECT_ANSWERS$id_student&id_test=$idTest"))
            number = dataJson.getInt("count")
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
        private const val GET_AVAILABLE_TEST = "http://sl-terms.gearhostpreview.com/api/available_test.php"
        private const val GET_ID_QUESTIONS = "http://sl-terms.gearhostpreview.com/api/get_questions.php?id_test="
        private const val GET_TEXT_QUESTION = "http://sl-terms.gearhostpreview.com/api/name_question.php?id_question="
        private const val INSERT_STUDENT = "http://sl-terms.gearhostpreview.com/api/Insert_student.php?surname="
        private const val GET_ID_VARIANT_NAME_VARIANT = "http://sl-terms.gearhostpreview.com/api/get_options.php?id_question="
        private const val ANSWER_TO_CUR_QUESTION = "http://sl-terms.gearhostpreview.com/api/insert_answer.php?id_student="
        private const val NUMBER_OF_CORRECT_ANSWERS = "http://sl-terms.gearhostpreview.com/api/number_of_correct_answers.php?id_student="
        private const val PICTURE = "http://sl-terms.gearhostpreview.com/api/picture.php?id_question="
        private const val PICTURE_NULL = "http://sl-terms.gearhostpreview.com/api/null_picture.php?id_question="
        var dbt: DataBaseTest? = null
    }
}