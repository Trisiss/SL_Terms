package com.example.sl_terms

import com.example.sl_terms.models.AvailableTest
import com.example.sl_terms.models.Option
import com.example.sl_terms.models.Question

class BusinessLogicTest {
    //переменная хранящая id теста
    private val id_test = 0

    //переменная хранящая id студента
    private val id_student: String? = null
    private var countQuestion = 0
    lateinit var ID_questions: Array<AvailableTest>
    lateinit var IdVariantVariantName: Array<AvailableTest>
    lateinit var Picture: Array<AvailableTest>
    var dataBaseTest = DataBaseTest()

    //возвращает название доступных тестов, id теста и фамилию записать в переменные
    val availableTest: Array<AvailableTest>
        get() = dataBaseTest.availableTest

    //кнопка начать тест создает нового студента в бд
    fun startTest(FIO: String): Int {
        return dataBaseTest.insertStudent(FIO)
        //подгрузить все айди вопросов
    }

    fun getIdQuestions(ID: Int, question: Int) {

        ID_questions = dataBaseTest.getIdQuestion(ID);

        for (i in 0 until ID_questions.size) {
            ID_questions[i].name = dataBaseTest.getTextQuestion(ID_questions[i].id)[0].name;

            // System.out.println(ID_questions[i].name);
        }
    }
        /*  ID_questions = dataBaseTest.getTextQuestion(ID);
        System.out.println(ID_questions.length);
        for (int i = 0; i< ID_questions.length; i++)
            System.out.println(ID_questions[i].name);
        System.out.println("000000000");*/
/*  return ID_questions[question].name;
    }*/
    fun getIdQuestions(ID:Int): Array<AvailableTest> {
        ID_questions = dataBaseTest.getIdQuestion(ID)
        for (i in ID_questions.indices) {
            ID_questions.get(i).name = dataBaseTest.getTextQuestion(ID_questions.get(i).id).get(0).name
        }
        return ID_questions
    }

    fun getQuestions(id: Int): Array<Question> = dataBaseTest.getQuestions(id)

    //получить текст вопроса
    fun getCurQuestionText(): kotlin.Unit {}

    //получить варианты ответа
    fun getIdVariantVariantName(idQuestion:Int): Array<AvailableTest> {
        IdVariantVariantName = dataBaseTest.geIdVariantVariantName(idQuestion)
        return IdVariantVariantName
    }

    fun getOptions(id: Int): Array<Option> = dataBaseTest.getOptions(id)

    //передать ответ студента на сервер
    fun answerToCurQuestion(id_student:Int, id_question:Int, id_variant:Int, id_session:Int): Unit {
        dataBaseTest.answerToCurQuestion(id_student, id_question, id_variant, id_session)
    }

    //следующий вопрос
    fun nextQuestion(): Int {
        countQuestion++
        return countQuestion
    }

    //получить количество правильных ответов
    fun numberOfCorrectAnswers(id_student:Int, idTest: Int): Int {
        return dataBaseTest.numberOfCorrectAnswers(id_student, idTest)
    }

    fun getPicture(idQuestion:Int): Array<AvailableTest> {
        Picture = dataBaseTest.getPicture(idQuestion)
        return Picture
    }

    fun getPictureNull(idQuestion:Int): Array<AvailableTest> {
        Picture = dataBaseTest.getPictureNull(idQuestion)
        return Picture
    }

    companion object  {
        var  blt:BusinessLogicTest = BusinessLogicTest()
    }
}