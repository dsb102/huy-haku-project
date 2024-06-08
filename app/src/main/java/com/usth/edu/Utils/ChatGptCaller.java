package com.usth.edu.Utils;

import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ChatGptCaller {

    public interface OnCallerSuccess {
        void call(String result);
    }

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final String API_KEY = "sk-proj-fQGJV1oKdBk9lNG0WC23T3BlbkFJZ4iEtEXe0DUUJ6vljCif";

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client = new OkHttpClient();

    public static String getPrompt(Date startDate, Date endDate, String title, String description) {
        return "I'm about to start a job from " + sdf.format(startDate) + " to " + sdf.format(endDate) + ", Specific job title: " + title + " and detailed description of the job: " + description +
                ". What should I prepare for this job? Please help me briefly describe it in 100 words.";
    }

    public void callChatGpt(Date startDate, Date endDate, String title, String description, OnCallerSuccess callerSuccess) {
        String question = getPrompt(startDate, endDate, title, description);
        JSONObject jsonBody = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONObject message = new JSONObject();
        try {
            message.put("role", "user");
            message.put("content", question);
            jsonArray.put(message);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            jsonBody.put("model", "gpt-3.5-turbo");
            jsonBody.put("messages", jsonArray);
            jsonBody.put("max_tokens", 4000);
            jsonBody.put("temperature", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RequestBody body = RequestBody.create(jsonBody.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", "Bearer " + API_KEY)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.d("SOBIN ERROR", "Failed to load response due to " + e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        JSONArray jsonArray = jsonObject.getJSONArray("choices");
                        String result = jsonArray.getJSONObject(0).getJSONObject("message").getString("content");
                        callerSuccess.call(result.trim());
                        Log.d("SOBIN SUCCESS", result.trim());
                    } catch (JSONException e) {
                        callerSuccess.call(description);
                        e.printStackTrace();
                    }


                } else {
                    callerSuccess.call(description);
                    Log.d("SOBIN ERROR", "Failed to load response due to " + response);
                }
            }
        });
    }
}
