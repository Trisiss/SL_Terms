package com.example.sl_terms;
import java.util.ArrayList;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

class DataBase {
    private static final String GET_SEARCH_TERMS = "http://btidb.esy.es/terms/get_search_terms.php?search=";
    private static final String GET_TERM = "http://btidb.esy.es/terms/get_term.php?type=1&id=";
    static final String GET_SEARCH_IMAGE_JSON = "http://btidb.esy.es/terms/get_search_image.php?type=0&search=";
    static final String GET_SEARCH_IMAGE = "http://btidb.esy.es/terms/get_search_image.php?type=1&search=";
    static final String GET_IMAGES = "http://btidb.esy.es/terms/get_images.php";
    private final OkHttpClient client = new OkHttpClient();

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

    TermRecord[] searchTerm(String TermName) {
        ArrayList<TermRecord> listTerms = new ArrayList<>();
        try {
            JSONArray dataJsonArr = new JSONObject(getResponse(GET_SEARCH_TERMS + TermName))
                    .getJSONArray("terms");
            for (int i = 0; i < dataJsonArr.length(); i++) {
                JSONObject termJSON = dataJsonArr.getJSONObject(i);
                TermRecord term = new TermRecord();
                term.id = termJSON.getInt("id");
                term.name = termJSON.getString("name");
                listTerms.add(term);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listTerms.toArray(new TermRecord[listTerms.size()]);
    }

    String getTermByID(int id) {
        return getResponse(GET_TERM + id);
    }

    boolean checkInternet() {
        return !getResponse(GET_SEARCH_TERMS + "something").equals("");
    }
}
