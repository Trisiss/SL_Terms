package com.example.sl_terms

import android.util.Log
import com.example.sl_terms.models.TermRecord
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONObject
import java.util.*

internal class DataBase {
    private val client = OkHttpClient()
    fun getResponse(url: String): String {
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

    fun searchTerm(TermName: String): Array<TermRecord> {
        val listTerms = ArrayList<TermRecord>()
        try {
            val dataJsonArr = JSONObject(getResponse(Urls.GET_TERM.value + TermName))
                    .getJSONArray("terms")
            for (i in 0 until dataJsonArr.length()) {
                val termJSON = dataJsonArr.getJSONObject(i)
                val term = TermRecord()
                term.id = termJSON.getInt("id")
                term.name = termJSON.getString("name")
                listTerms.add(term)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listTerms.toTypedArray()
    }

    fun getTermByID(id: Int): String {
        return getResponse(Urls.GET_TERM.value + id)
    }

    fun checkInternet(): Boolean {
        return getResponse(Urls.GET_SEARCH_TERMS.value + "something") != ""
    }

    fun getStudents(): Array<String> {
        val listStudents = ArrayList<String>()
        try {
            val dataJsonArr = JSONObject(getResponse(Urls.GET_STUDENTS.value))
                    .getJSONArray("students")
            for (i in 0 until dataJsonArr.length()) {
                val studentJSON = dataJsonArr.getJSONObject(i)
                val student = studentJSON.getString("surname")
                listStudents.add(student)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return listStudents.toTypedArray()
    }

    fun auth(fio: String, pass: String): String {
        val JSON = MediaType.parse("application/json; charset=utf-8")
        var strResponse: String = ""
        val client = OkHttpClient.Builder().cache(null).build()
        val map: HashMap<String, String> = hashMapOf("Fullname" to fio, "Password" to pass)
        val mapJson = JSONObject(map as Map<*, *>).toString()
        val requestBody = RequestBody.create(JSON, mapJson)
        Log.e("TAG", requestBody.contentLength().toString())
        val request: Request = Request.Builder()
                .url(Urls.AUTH.value)
                .post(requestBody)
                .build()
        try {
            val response = client.newCall(request).execute()
            strResponse = response.body()!!.string()
            Log.e("TAG" , strResponse)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return strResponse

    }

    enum class Urls(val value: String) {
        GET_SEARCH_TERMS("http://sl-terms.gearhostpreview.com/api/get_search_terms.php?search="),
        GET_TERM("http://sl-terms.gearhostpreview.com/api/get_term.php?type=1&id="),
        GET_SEARCH_IMAGE_JSON("http://sl-terms.gearhostpreview.com/api/get_search_image.php?type=0&search="),
        GET_SEARCH_IMAGE("http://sl-terms.gearhostpreview.com/api/get_search_image.php?type=1&search="),
        GET_IMAGES("http://sl-terms.gearhostpreview.com/api/get_images.php"),
        GET_STUDENTS("http://sl-terms.gearhostpreview.com/api/get_students.php"),
        AUTH("http://sl-terms.gearhostpreview.com/api/auth.php")
    }


}