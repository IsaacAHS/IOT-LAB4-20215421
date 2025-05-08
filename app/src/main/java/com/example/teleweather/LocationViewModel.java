package com.example.teleweather;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.teleweather.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LocationViewModel extends ViewModel {
    private final OkHttpClient client = new OkHttpClient();
    private final String API_KEY = "ec24b1c6dd8a4d528c1205500250305";
    private final String BASE_URL = "https://api.weatherapi.com/v1/search.json";

    private final MutableLiveData<List<Location>> locationList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public LocationViewModel() {
        locationList.setValue(new ArrayList<>());
        isLoading.setValue(false);
    }

    public LiveData<List<Location>> getLocationList() {
        return locationList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void searchLocation(String query) {
        if (query == null || query.trim().isEmpty()) {
            error.setValue("Por favor, ingrese una localidad");
            return;
        }

        isLoading.setValue(true);
        error.setValue(null);

        String url = BASE_URL + "?key=" + API_KEY + "&q=" + query;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                isLoading.postValue(false);
                error.postValue("Error de conexión: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    if (!response.isSuccessful()) {
                        throw new IOException("Error: " + response.code());
                    }

                    String responseData = response.body().string();
                    List<Location> locations = parseLocations(responseData);

                    locationList.postValue(locations);
                    isLoading.postValue(false);

                    if (locations.isEmpty()) {
                        error.postValue("No se encontraron resultados para esta búsqueda");
                    }
                } catch (Exception e) {
                    isLoading.postValue(false);
                    error.postValue("Error al procesar los datos: " + e.getMessage());
                }
            }
        });
    }

    private List<Location> parseLocations(String jsonData) {
        List<Location> locations = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(jsonData);

            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject locationObj = jsonArray.getJSONObject(i);

                Location location = new Location(
                        locationObj.getInt("id"),
                        locationObj.getString("name"),
                        locationObj.getString("region"),
                        locationObj.getString("country"),
                        locationObj.getDouble("lat"),
                        locationObj.getDouble("lon")
                );

                locations.add(location);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return locations;
    }
}