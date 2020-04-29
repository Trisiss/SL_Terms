package com.example.sl_terms

import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.*

internal class DataBase {
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

    fun searchTerm(TermName: String): Array<TermRecord> {
        val listTerms = ArrayList<TermRecord>()
        try {
            val dataJsonArr = JSONObject(getResponse(GET_SEARCH_TERMS + TermName))
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
        return getResponse(GET_TERM + id)
    }

    fun checkInternet(): Boolean {
        return getResponse(GET_SEARCH_TERMS + "something") != ""
    }

    companion object {
        private const val GET_SEARCH_TERMS = "http://btidb.esy.es/terms/get_search_terms.php?search="
        private const val GET_TERM = "http://btidb.esy.es/terms/get_term.php?type=1&id="
        const val GET_SEARCH_IMAGE_JSON = "http://btidb.esy.es/terms/get_search_image.php?type=0&search="
        const val GET_SEARCH_IMAGE = "http://btidb.esy.es/terms/get_search_image.php?type=1&search="
        const val GET_IMAGES = "http://btidb.esy.es/terms/get_images.php"
    }
}