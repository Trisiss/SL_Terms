package com.example.sl_terms

import org.junit.Assert
import org.junit.Test
import com.example.sl_terms.DataBaseTest

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see [Testing documentation](http://d.android.com/tools/testing)
 */
class ExampleUnitTest {
    @Test
    @Throws(Exception::class)
    fun addition_isCorrect() {
        Assert.assertEquals(4, 2 + 2.toLong())
    }

    @Test
    fun getQuestions() {
        val dataBaseTest = DataBaseTest()
        val result = dataBaseTest.getQuestions(1)
        for (i in result) {
            println(i)
        }

    }

    @Test
    fun getOptions() {
        val dataBaseTest = DataBaseTest()
        val result = dataBaseTest.getOptions(10)
        for (i in result) {
            println(i)
        }

    }
}