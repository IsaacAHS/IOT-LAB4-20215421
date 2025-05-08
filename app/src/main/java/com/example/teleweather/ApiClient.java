package com.example.teleweather;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApiClient {
    private static final String API_KEY = "ec24b1c6dd8a4d528c1205500250305";
    private static final String BASE_URL = "https://api.weatherapi.com/v1/";

    private static ApiClient instance;
    private final OkHttpClient client;

    private ApiClient() {
        client = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .build();
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    public void searchLocation(String query, Callback callback) {
        String url = BASE_URL + "search.json?key=" + API_KEY + "&q=" + query;
        executeRequest(url, callback);
    }

    public void getForecast(String idLocation, int days, Callback callback) {
        String url = BASE_URL + "forecast.json?key=" + API_KEY + "&q=id:" + idLocation + "&days=" + days;
        executeRequest(url, callback);
    }

    public void getSportEvents(String location, Callback callback) {
        String url = BASE_URL + "sports.json?key=" + API_KEY + "&q=" + location;
        executeRequest(url, callback);
    }

    private void executeRequest(String url, Callback callback) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(callback);
    }

    public interface ApiResponseCallback {
        void onSuccess(String responseBody);
        void onError(Exception e);
    }
}