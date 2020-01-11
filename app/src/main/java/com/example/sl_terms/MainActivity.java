package com.example.sl_terms;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.RequiresApi;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.webkit.JavascriptInterface;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.os.Handler;
import android.content.Intent;
import android.net.Uri;

import static com.example.sl_terms.DataBase.GET_IMAGES;
import static com.example.sl_terms.DataBase.GET_SEARCH_IMAGE;
import static com.example.sl_terms.DataBase.GET_SEARCH_IMAGE_JSON;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private WebView mWebView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private BusinessLogic bl;
    private DataBase db;
    private String lastURL;

    private class MyJavaScriptInterface {
        @SuppressWarnings("unused")
        @JavascriptInterface
        public void loadHref(final String url) {
            if (url.contains("://")) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        String my_image_storage_dir = getFilesDir().getPath();
                        String my_3D_models_storage_dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                        Log.d("MY_URL", url);

                        String decode_url = "";
                        try {
                            decode_url = java.net.URLDecoder.decode(url, "UTF-8");
                            Log.d("MY_DECODE_URL", decode_url);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Matcher matcher;

                        String name = "";
                        matcher = Pattern.compile("([^/]+)[.]").matcher(decode_url);
                        while (matcher.find()) name = matcher.group(1);
                        Log.d("MY_NAME", name);

                        String full_name = "";
                        matcher = Pattern.compile("([^/]+)").matcher(decode_url);
                        while (matcher.find()) full_name = matcher.group(1);
                        Log.d("MY_FULL_NAME", full_name);

                        if (url.contains("/Image/")) {

                            name = name.toLowerCase() + ".jpg";
                            File imageFile = new File(my_image_storage_dir + "/" + name);
                            if (imageFile.exists()) {
                                //mWebView.loadUrl("file://" + my_image_storage_dir + "/" + name);
                                String imgData="<img src=" + name + ">";
                                mWebView.loadDataWithBaseURL("file://" + my_image_storage_dir + "/",
                                        imgData, "text/html", "utf-8", null);
                                Log.d("MY_IMAGE", "IS EXISTS");
                            } else {
                                int success = 0;
                                try {
                                    JSONObject imageJSON = new JSONObject(db.getResponse(GET_SEARCH_IMAGE_JSON + full_name));
                                    success = imageJSON.getInt("success");
                                    Log.d("MY_SUCCESS", "" + success);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (success == 1) {
                                    bl.loadImage(GET_SEARCH_IMAGE + full_name, name);
                                    //mWebView.loadUrl("file://" + my_image_storage_dir + "/" + name);
                                    String imgData="<img src=" + name + ">";
                                    mWebView.loadDataWithBaseURL("file://" + my_image_storage_dir + "/",
                                            imgData, "text/html", "utf-8", null);
                                    showToast("Изображение кэшировано");
                                } else {
                                    showToast("Изображение не загружено в БД");
                                }
                                Log.d("MY_IMAGE", "DO NOT EXISTS");
                            }

                        } else if (url.contains("/3D/")) {


                            name = name.toLowerCase() + ".stp";

                            File BinaryFile = new File(my_3D_models_storage_dir + "/" + name);
                            if (BinaryFile.exists()) {
                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("file://" + my_3D_models_storage_dir + "/" + name));
                                startActivity(intent);
                            } else {
                                int success = 0;
                                try {
                                    showToast(full_name);
                                    JSONObject BinaryJSON = new JSONObject(db.getResponse(GET_SEARCH_IMAGE_JSON + full_name));
                                    success = BinaryJSON.getInt("success");
                                    Log.d("MY_SUCCESS", "" + success);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (success == 1) {
                                    bl.loadBinary(GET_SEARCH_IMAGE + full_name, name);
                                    showToast("Модель кэширована");
                                } else {
                                    showToast("Модель не загружена в БД");
                                }
                                //Log.d("MY_IMAGE", "DO NOT EXISTS");
                            }

                        } else if (url.contains(getPackageName())) {

                            File termFile = new File(getFilesDir().getPath() + "/" + full_name);
                            if (termFile.exists()) {
                                mWebView.loadUrl(url);
                                Log.d("MY_TERM", "IS EXISTS");
                            } else {
                                TermRecord[] masTerms = db.searchTerm(name);
                                if (masTerms.length > 0) {
                                    for (TermRecord term : masTerms)
                                        if (term.name.equalsIgnoreCase(name))
                                            bl.loadTerm(term);
                                    mWebView.loadUrl(url);
                                    showToast("Термин кэширован");
                                } else {
                                    showToast("Термин не найден");
                                }
                                Log.d("MY_TERM", "DO NOT EXISTS");
                            }

                        } else {
                            mWebView.loadUrl(url);
                        }
                    }
                });
            }
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        mWebView = (WebView) findViewById(R.id.webView);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.GREEN);

        setTitle("Словарь терминов");

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setDefaultTextEncodingName("utf-8");
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);

        bl = new BusinessLogic(this);
        db = new DataBase();


        mWebView.addJavascriptInterface(new MyJavaScriptInterface(), "jsInterface");
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }

            @SuppressWarnings("deprecation")
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!url.contains("#")) lastURL = url;
                Log.d("LAST_URL", "url: " + lastURL);
                mWebView.loadUrl("javascript:" +
                        "var anchors = document.getElementsByTagName(\"a\");" +
                        "for (var i = 0; i < anchors.length; i++) {" +
                        "   anchors[i].addEventListener(\"click\", function(event) {" +
                        "       jsInterface.loadHref(event.target.href);" +
                        "   });" +
                        "}");
            }
        });
        searchTerms("");
    }

    public void showToast(String message) {
        Toast toast = Toast.makeText(getApplicationContext(),
                message, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }

    private void searchTerms(String query) {
        TermRecord[] masTerms;
        String html = "<html><body><ul>";
        if (db.checkInternet()){
            masTerms = db.searchTerm(query);
        } else {
            masTerms = bl.searchTerm(query);
        }
        if (masTerms.length > 0) {
            for (TermRecord term : masTerms) {
                html += "<li><a href=\"file://" +
                        getFilesDir().getPath() + "/" +
                        term.name.toLowerCase() + ".html\">" +
                        term.name.toUpperCase() + "</a>";
            }
        } else {
            html += "Ничего не найдено";
        }
        html += "</li></ul></body></html>";
        mWebView.loadData(html, "text/html; charset=UTF-8", null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (mWebView.canGoBack()) {
                        mWebView.goBack();
                    } else {
                        finish();
                    }
                    return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (null != searchManager) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        }

        final EditText searchPlate = ((EditText)searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));

        searchPlate.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchTerms(searchPlate.getText().toString());
                }
                return false;
            }
        });
        return true;
    }
//обработка нажатия в меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_load:  //загрузить словарь
                if (db.checkInternet()) {
                    new LoadAllTerms().execute();
                } else {
                    showToast("Отсутствует интернет-соединение");
                }
                return true;
            case R.id.action_about:  //о программе
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
                        .setNegativeButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                AlertDialog alert = builder.create();
                alert.show();
                return true;
            case R.id.action_test:  //Пройти тест
                Intent intent = new Intent(MainActivity.this, CheckInActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onStop() {
        super.onStop();
        finishAffinity();
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (db.checkInternet()) {
                    String decode_url = "";
                    try {
                        decode_url = java.net.URLDecoder.decode(lastURL, "UTF-8");
                        Log.d("MY_DECODE_URL", decode_url);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    String name = "";
                    Matcher matcher = Pattern.compile("([^/]+)[.]").matcher(decode_url);
                    while (matcher.find()) name = matcher.group(1);
                    Log.d("MY_NAME", name);

                    if (lastURL.contains("text/html")) {

                        searchTerms("");

                    } else if (lastURL.contains(".html")) {

                        TermRecord[] masTerms = db.searchTerm(name);
                        if (masTerms.length > 0) {
                            for (TermRecord term : masTerms)
                                if (term.name.equalsIgnoreCase(name))
                                    bl.loadTerm(term);
                            mWebView.loadUrl(lastURL);
                            showToast("Термин обновлён");
                        } else {
                            showToast("Термин не найден");
                        }

                    } else if (lastURL.contains(".jpg")||(lastURL.contains(".stp"))){

                        String full_name = name + ".html";
                        if (lastURL.contains(".jpg"))
                            name = name.toLowerCase() + ".jpg";
                        if (lastURL.contains(".stp"))
                            name = name.toLowerCase() + ".stp";
                        int success = 0;
                        try {
                            JSONObject imageJSON = new JSONObject(db.getResponse(GET_SEARCH_IMAGE_JSON + full_name));
                            success = imageJSON.getInt("success");
                            Log.d("MY_SUCCESS", "" + success);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (success == 1) {
                            if (lastURL.contains(".jpg")){
                                bl.loadImage(GET_SEARCH_IMAGE + full_name, name);
                                //mWebView.loadUrl("file://" + getFilesDir().getPath() + "/" + name);
                                String imgData="<img src=" + name + ">";
                                mWebView.loadDataWithBaseURL("file://" + getFilesDir().getPath() + "/",
                                        imgData, "text/html", "utf-8", null);
                                showToast("Изображение обновлено");
                            }
                            if (lastURL.contains(".stp")){
                                //блок кода для обработки 3D-моделей
                            }
                        } else {
                            if (lastURL.contains(".jpg"))
                                showToast("Изображение не загружено в БД");
                            if (lastURL.contains(".stp"))
                                showToast("3D-модель не загружена в БД");
                        }

                    }
                } else {
                    showToast("Отсутствует интернет-соединение");
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 2000);
    }

    private class LoadAllTerms extends AsyncTask<Void, Integer, Void> {
        private int currentMax = 0;
        private ProgressBar mHorizontalProgressBar = (ProgressBar) findViewById(R.id.horizontalProgressBar);
        private TextView mProgressTextView = (TextView) findViewById(R.id.progressTextView);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mHorizontalProgressBar.setVisibility(ProgressBar.VISIBLE);
            mProgressTextView.setVisibility(TextView.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {

            int stage = 0; // 0 - terms, 1 - images
            int progressCount = 0;

            TermRecord[] terms = db.searchTerm("");
            int num_terms = terms.length;
            for (TermRecord term : terms) {
                publishProgress(stage, progressCount++, num_terms);
                bl.loadTerm(term);
            }

            stage = 1;
            progressCount = 0;

            try {
                JSONObject imagesJSON = new JSONObject(db.getResponse(GET_IMAGES));
                int success = imagesJSON.getInt("success");
                if (success == 1) {
                    int num_images = imagesJSON.getInt("number");
                    JSONArray imagesJSONArr = imagesJSON.getJSONArray("images");
                    for (int i = 0; i < imagesJSONArr.length(); i++) {
                        publishProgress(stage, progressCount++, num_images);

                        String urlForSearch = imagesJSONArr.getJSONObject(i).getString("url");

                        String name = "";
                        Matcher matcher = Pattern.compile("([^/]+)[.]").matcher(urlForSearch);
                        while (matcher.find()) name = matcher.group(1);
                        name = name.toLowerCase() + ".jpg";

                        bl.loadImage(GET_SEARCH_IMAGE + urlForSearch, name);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (currentMax != values[2]) {
                currentMax = values[2];
                mHorizontalProgressBar.setMax(currentMax);
            }
            if (values[0] == 0) {
                mProgressTextView.setText("Загрузка терминов: " + values[1] + "/" + currentMax);
            } else if (values[0] == 1) {
                mProgressTextView.setText("Загрузка иллюстраций: " + values[1] + "/" + currentMax);
            }
            mHorizontalProgressBar.setProgress(values[1]);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            mHorizontalProgressBar.setVisibility(ProgressBar.INVISIBLE);
            mProgressTextView.setVisibility(TextView.INVISIBLE);
            showToast("Словарь терминов загружен");
        }
    }
}