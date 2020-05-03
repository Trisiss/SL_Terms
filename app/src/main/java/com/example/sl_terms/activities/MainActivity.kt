package com.example.sl_terms.activities

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.*
import android.support.annotation.RequiresApi
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import android.view.inputmethod.EditorInfo
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.example.sl_terms.BusinessLogic
import com.example.sl_terms.DataBase
import com.example.sl_terms.R
import com.example.sl_terms.models.TermRecord
import org.json.JSONObject
import java.io.File
import java.net.URLDecoder
import java.util.regex.Matcher
import java.util.regex.Pattern

class MainActivity : AppCompatActivity(), OnRefreshListener {

    private var mWebView: WebView? = null
    private var mSwipeRefreshLayout: SwipeRefreshLayout? = null
    private var bl: BusinessLogic? = null
    private var db: DataBase? = null
    private var lastURL: String? = null

    private inner class MyJavaScriptInterface {
        @JavascriptInterface
        fun loadHref(url: String) {
            if (url.contains("://")) {
                runOnUiThread {
                    val myImageStorageDir = filesDir.path
                    val my3dModelsStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).path
                    Log.d("MY_URL", url)
                    var decodeUrl: String? = ""
                    try {
                        decodeUrl = URLDecoder.decode(url, "UTF-8")
                        Log.d("MY_DECODE_URL", decodeUrl)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    var matcher: Matcher
                    var name = ""
                    matcher = Pattern.compile("([^/]+)[.]").matcher(decodeUrl)
                    while (matcher.find()) name = matcher.group(1)
                    Log.d("MY_NAME", name)
                    var full_name = ""
                    matcher = Pattern.compile("([^/]+)").matcher(decodeUrl)
                    while (matcher.find()) full_name = matcher.group(1)
                    Log.d("MY_FULL_NAME", full_name)
                    if (url.contains("/Image/")) {
                        name = name.toLowerCase() + ".jpg"
                        val imageFile = File("$myImageStorageDir/$name")
                        if (imageFile.exists()) { //mWebView.loadUrl("file://" + my_image_storage_dir + "/" + name);
                            val imgData = "<img src=$name>"
                            mWebView!!.loadDataWithBaseURL("file://$myImageStorageDir/",
                                    imgData, "text/html", "utf-8", null)
                            Log.d("MY_IMAGE", "IS EXISTS")
                        } else {
                            var success = 0
                            try {
                                val imageJSON = JSONObject(db!!.getResponse(DataBase.GET_SEARCH_IMAGE_JSON + full_name))
                                success = imageJSON.getInt("success")
                                Log.d("MY_SUCCESS", "" + success)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            if (success == 1) {
                                bl!!.loadImage(DataBase.GET_SEARCH_IMAGE + full_name, name)
                                //mWebView.loadUrl("file://" + my_image_storage_dir + "/" + name);
                                val imgData = "<img src=$name>"
                                mWebView!!.loadDataWithBaseURL("file://$myImageStorageDir/",
                                        imgData, "text/html", "utf-8", null)
                                showToast("Изображение кэшировано")
                            } else {
                                showToast("Изображение не загружено в БД")
                            }
                            Log.d("MY_IMAGE", "DO NOT EXISTS")
                        }
                    } else if (url.contains("/3D/")) {
                        name = name.toLowerCase() + ".stp"
                        val BinaryFile = File("$my3dModelsStorageDir/$name")
                        if (BinaryFile.exists()) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("file://$my3dModelsStorageDir/$name"))
                            startActivity(intent)
                        } else {
                            var success = 0
                            try {
                                showToast(full_name)
                                val BinaryJSON = JSONObject(db!!.getResponse(DataBase.GET_SEARCH_IMAGE_JSON + full_name))
                                success = BinaryJSON.getInt("success")
                                Log.d("MY_SUCCESS", "" + success)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                            if (success == 1) {
                                bl!!.loadBinary(DataBase.GET_SEARCH_IMAGE + full_name, name)
                                showToast("Модель кэширована")
                            } else {
                                showToast("Модель не загружена в БД")
                            }
                            //Log.d("MY_IMAGE", "DO NOT EXISTS");
                        }
                    } else if (url.contains(packageName)) {
                        val termFile = File(filesDir.path + "/" + full_name)
                        if (termFile.exists()) {
                            mWebView!!.loadUrl(url)
                            Log.d("MY_TERM", "IS EXISTS")
                        } else {
                            val masTerms = db!!.searchTerm(name)
                            if (masTerms!!.size > 0) {
                                for (term in masTerms) if (term!!.name.equals(name, ignoreCase = true)) bl!!.loadTerm(term)
                                mWebView!!.loadUrl(url)
                                showToast("Термин кэширован")
                            } else {
                                showToast("Термин не найден")
                            }
                            Log.d("MY_TERM", "DO NOT EXISTS")
                        }
                    } else {
                        mWebView!!.loadUrl(url)
                    }
                }
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        mWebView = findViewById<View>(R.id.webView) as WebView
        mSwipeRefreshLayout = findViewById<View>(R.id.swipe_container) as SwipeRefreshLayout
        mSwipeRefreshLayout!!.setOnRefreshListener(this)
        mSwipeRefreshLayout!!.setColorSchemeColors(Color.GREEN)
        title = "Словарь терминов"
        val webSettings = mWebView!!.settings
        webSettings.defaultTextEncodingName = "utf-8"
        webSettings.javaScriptEnabled = true
        webSettings.builtInZoomControls = true
        bl = BusinessLogic(this)
        db = DataBase()
        mWebView!!.addJavascriptInterface(MyJavaScriptInterface(), "jsInterface")
        mWebView!!.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                return true
            }

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                if (!url.contains("#")) lastURL = url
                Log.d("LAST_URL", "url: $lastURL")
                mWebView!!.loadUrl("javascript:" +
                        "var anchors = document.getElementsByTagName(\"a\");" +
                        "for (var i = 0; i < anchors.length; i++) {" +
                        "   anchors[i].addEventListener(\"click\", function(event) {" +
                        "       jsInterface.loadHref(event.target.href);" +
                        "   });" +
                        "}")
            }
        }
        searchTerms("")
    }

    fun showToast(message: String?) {
        val toast = Toast.makeText(applicationContext,
                message, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.BOTTOM, 0, 0)
        toast.show()
    }

    private fun searchTerms(query: String) {
        val masTerms: Array<TermRecord>
        var html = "<html><body><ul>"
        masTerms = if (db!!.checkInternet()) {
            db!!.searchTerm(query)
        } else {
            bl!!.searchTerm(query)
        }
        if (masTerms.size > 0) {
            for (term in masTerms) {
                html += "<li><a href=\"file://" +
                        filesDir.path + "/" +
                        term!!.name!!.toLowerCase() + ".html\">" +
                        term!!.name!!.toUpperCase() + "</a>"
            }
        } else {
            html += "Ничего не найдено"
        }
        html += "</li></ul></body></html>"
        mWebView!!.loadData(html, "text/html; charset=UTF-8", null)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (mWebView!!.canGoBack()) {
                        mWebView!!.goBack()
                    } else {
                        finish()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        val searchView = menu.findItem(R.id.action_search).actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        if (null != searchManager) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        }
        val searchPlate = searchView.findViewById<View>(android.support.v7.appcompat.R.id.search_src_text) as EditText
        searchPlate.setOnEditorActionListener { textView, actionId, keyEvent ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchTerms(searchPlate.text.toString())
            }
            false
        }
        return true
    }

    //обработка нажатия в меню
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        return when (id) {
            R.id.action_load -> {
                if (db!!.checkInternet()) {
                    LoadAllTerms().execute()
                } else {
                    showToast("Отсутствует интернет-соединение")
                }
                true
            }
            R.id.action_about -> {
                val builder = AlertDialog.Builder(this@MainActivity)
                builder.setTitle("О программе")
                        .setMessage(
                                "Автор идеи: \n" +
                                        "Казанцев Александр Геннадьевич \n\n" +
                                        "Разработали: \n" +
                                        "Голых Роман Николаевич \n" +
                                        "Вальтер Виктор Александрович \n" +
                                        "Евдокимова Ольга Алексеевна \n" +
                                        "Вохмин Александр Андреевич \n" +
                                        "Тарасенко Екатерина Николаевна \n")
                        .setCancelable(false)
                        .setNegativeButton("OK"
                        ) { dialog, id -> dialog.cancel() }
                val alert = builder.create()
                alert.show()
                true
            }
            R.id.action_test -> {
                val intent = Intent(this@MainActivity, CheckInActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    override fun onStop() {
        super.onStop()
        finishAffinity()
    }

    override fun onRefresh() {
        Handler().postDelayed({
            if (db!!.checkInternet()) {
                var decode_url: String? = ""
                try {
                    decode_url = URLDecoder.decode(lastURL, "UTF-8")
                    Log.d("MY_DECODE_URL", decode_url)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                var name = ""
                val matcher = Pattern.compile("([^/]+)[.]").matcher(decode_url)
                while (matcher.find()) name = matcher.group(1)
                Log.d("MY_NAME", name)
                if (lastURL!!.contains("text/html")) {
                    searchTerms("")
                } else if (lastURL!!.contains(".html")) {
                    val masTerms = db!!.searchTerm(name)
                    if (masTerms!!.size > 0) {
                        for (term in masTerms) if (term!!.name.equals(name, ignoreCase = true)) bl!!.loadTerm(term)
                        mWebView!!.loadUrl(lastURL)
                        showToast("Термин обновлён")
                    } else {
                        showToast("Термин не найден")
                    }
                } else if (lastURL!!.contains(".jpg") || lastURL!!.contains(".stp")) {
                    val full_name = "$name.html"
                    if (lastURL!!.contains(".jpg")) name = name.toLowerCase() + ".jpg"
                    if (lastURL!!.contains(".stp")) name = name.toLowerCase() + ".stp"
                    var success = 0
                    try {
                        val imageJSON = JSONObject(db!!.getResponse(DataBase.GET_SEARCH_IMAGE_JSON + full_name))
                        success = imageJSON.getInt("success")
                        Log.d("MY_SUCCESS", "" + success)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    if (success == 1) {
                        if (lastURL!!.contains(".jpg")) {
                            bl!!.loadImage(DataBase.GET_SEARCH_IMAGE + full_name, name)
                            //mWebView.loadUrl("file://" + getFilesDir().getPath() + "/" + name);
                            val imgData = "<img src=$name>"
                            mWebView!!.loadDataWithBaseURL("file://" + filesDir.path + "/",
                                    imgData, "text/html", "utf-8", null)
                            showToast("Изображение обновлено")
                        }
                        if (lastURL!!.contains(".stp")) { //блок кода для обработки 3D-моделей
                        }
                    } else {
                        if (lastURL!!.contains(".jpg")) showToast("Изображение не загружено в БД")
                        if (lastURL!!.contains(".stp")) showToast("3D-модель не загружена в БД")
                    }
                }
            } else {
                showToast("Отсутствует интернет-соединение")
            }
            mSwipeRefreshLayout!!.isRefreshing = false
        }, 2000)
    }

    @SuppressLint("StaticFieldLeak")
    private inner class LoadAllTerms : AsyncTask<Void?, Int?, Void?>() {
        private var currentMax = 0
        private val mHorizontalProgressBar = findViewById<View>(R.id.horizontalProgressBar) as ProgressBar
        private val mProgressTextView = findViewById<View>(R.id.progressTextView) as TextView
        override fun onPreExecute() {
            super.onPreExecute()
            mHorizontalProgressBar.visibility = ProgressBar.VISIBLE
            mProgressTextView.visibility = TextView.VISIBLE
        }

        protected override fun doInBackground(vararg p0: Void?): Void? {
            var stage = 0 // 0 - terms, 1 - images
            var progressCount = 0
            val terms = db!!.searchTerm("")
            val num_terms = terms!!.size
            for (term in terms) {
                publishProgress(stage, progressCount++, num_terms)
                bl!!.loadTerm(term)
            }
            stage = 1
            progressCount = 0
            try {
                val imagesJSON = JSONObject(db!!.getResponse(DataBase.GET_IMAGES))
                val success = imagesJSON.getInt("success")
                if (success == 1) {
                    val num_images = imagesJSON.getInt("number")
                    val imagesJSONArr = imagesJSON.getJSONArray("images")
                    for (i in 0 until imagesJSONArr.length()) {
                        publishProgress(stage, progressCount++, num_images)
                        val urlForSearch = imagesJSONArr.getJSONObject(i).getString("url")
                        var name = ""
                        val matcher = Pattern.compile("([^/]+)[.]").matcher(urlForSearch)
                        while (matcher.find()) name = matcher.group(1)
                        name = name.toLowerCase() + ".jpg"
                        bl!!.loadImage(DataBase.GET_SEARCH_IMAGE + urlForSearch, name)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        protected override fun onProgressUpdate(vararg values: Int?) {
            super.onProgressUpdate(*values)
            if (currentMax != values[2]) {
                currentMax = values[2]!!
                mHorizontalProgressBar.max = currentMax
            }
            if (values[0] == 0) {
                mProgressTextView.text = "Загрузка терминов: " + values[1] + "/" + currentMax
            } else if (values[0] == 1) {
                mProgressTextView.text = "Загрузка иллюстраций: " + values[1] + "/" + currentMax
            }
            mHorizontalProgressBar.progress = values[1]!!
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            mHorizontalProgressBar.visibility = ProgressBar.INVISIBLE
            mProgressTextView.visibility = TextView.INVISIBLE
            showToast("Словарь терминов загружен")
        }
    }
}