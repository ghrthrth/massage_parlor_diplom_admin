package com.example.massage_parlor_admin.ui.gallery;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

public class HttpRequestTask extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private final Context mContext;
    private final String urls;

    private final Map<String, String> params;

    public HttpRequestTask(Context context, String url, Map<String, String> param) {
        mContext = context;
        urls = url;
        params = param;
    }



    @Override
    protected String doInBackground(String... doInBackgroundParams) {

        try {
            // Create the JSON object
            JSONObject json = new JSONObject();
            for (Map.Entry<String, String> entry : params.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                json.put(key, value);
            }

            // Convert the JSON object to a string
            String jsonData = json.toString();

            // Create the request body
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json"), jsonData);

            Request request = new Request.Builder()
                    .url(urls)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String requestBodyToString(RequestBody requestBody) throws IOException {
        Buffer buffer = new Buffer();
        requestBody.writeTo(buffer);
        return buffer.readUtf8();
    }

    @Override
    protected void onPostExecute(String result) {
        if (result != null) {
            Toast.makeText(mContext, "Ошибка: " + result, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(mContext, "Данные успешно записаны", Toast.LENGTH_SHORT).show();
        }
    }
}
