package com.example.sl_terms;

public class BusinessLogicTest {

    //переменная хранящая id теста
    private int id_test;
    //переменная хранящая id студента
    private String id_student;
    private int countQuestion = 0;
    public static BusinessLogicTest blt = new BusinessLogicTest();

    AvailableTest[] ID_questions;
    AvailableTest[] IdVariantVariantName;
    AvailableTest[] Picture;


    DataBaseTest dataBaseTest = new DataBaseTest();

    //возвращает название доступных тестов, id теста и фамилию записать в переменные
    AvailableTest[] getAvailableTest() {
        return dataBaseTest.getAvailableTest();
    }

    //кнопка начать тест создает нового студента в бд
    int startTest(String FIO) {

        return  dataBaseTest.insertStudent(FIO);
        //подгрузить все айди вопросов
    }
    /*String getIdQuestions( int ID, int question) {

        ID_questions = dataBaseTest.getIdQuestion(ID);

        for (int i = 0; i < ID_questions.length; i++) {
            ID_questions[i].name = dataBaseTest.getTextQuestion(ID_questions[i].id)[0].name;

            // System.out.println(ID_questions[i].name);
        }
      /*  ID_questions = dataBaseTest.getTextQuestion(ID);
        System.out.println(ID_questions.length);
        for (int i = 0; i< ID_questions.length; i++)
            System.out.println(ID_questions[i].name);
        System.out.println("000000000");*/
      /*  return ID_questions[question].name;
    }*/

    AvailableTest[] getIdQuestions( int ID) {

        ID_questions = dataBaseTest.getIdQuestion(ID);

        for (int i = 0; i < ID_questions.length; i++) {
            ID_questions[i].name = dataBaseTest.getTextQuestion(ID_questions[i].id)[0].name;

            // System.out.println(ID_questions[i].name);
        }
      /*  ID_questions = dataBaseTest.getTextQuestion(ID);
        System.out.println(ID_questions.length);
        for (int i = 0; i< ID_questions.length; i++)
            System.out.println(ID_questions[i].name);
        System.out.println("000000000");*/
        return ID_questions;
    }

    //получить текст вопроса
    void getCurQuestionText() {

    }

    //получить варианты ответа
    AvailableTest[] getIdVariantVariantName(int idQuestion) {
        IdVariantVariantName = dataBaseTest.geIdVariantVariantName(idQuestion);
        return IdVariantVariantName;
    }
    //передать ответ студента на сервер
    void answerToCurQuestion(int id_student, int id_question, int id_variant, int id_session){
        dataBaseTest.answerToCurQuestion(id_student,  id_question,  id_variant, id_session);
    }

    //следующий вопрос
    int nextQuestion(){
        countQuestion++;
     return countQuestion;

    }
    //получить количество правильных ответов
    int numberOfCorrectAnswers(int id_student) {
        return dataBaseTest.numberOfCorrectAnswers(id_student);
    }
    AvailableTest[] getPicture(int idQuestion) {
        Picture = dataBaseTest.getPicture(idQuestion);
        return Picture;
    }
    AvailableTest[] getPictureNull(int idQuestion) {
        Picture = dataBaseTest.getPictureNull(idQuestion);
        return Picture;
    }


}
