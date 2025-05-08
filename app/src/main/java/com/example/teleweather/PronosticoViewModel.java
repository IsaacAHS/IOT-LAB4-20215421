package com.example.teleweather;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.teleweather.Pronostico;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class PronosticoViewModel extends ViewModel {
    private final OkHttpClient client = new OkHttpClient();
    private final String API_KEY = "ec24b1c6dd8a4d528c1205500250305";
    private final String BASE_URL = "https://api.weatherapi.com/v1/forecast.json";

    private final MutableLiveData<List<Pronostico>> pronosticoList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<String> cityName = new MutableLiveData<>();

    public PronosticoViewModel() {
        pronosticoList.setValue(new ArrayList<>());
        isLoading.setValue(false);
    }

    public LiveData<List<Pronostico>> getPronosticoList() {
        return pronosticoList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<String> getCityName() {
        return cityName;
    }

    public void getPronosticoPorIdLocation(String idLocation, int dias) {
        if (idLocation == null || idLocation.trim().isEmpty()) {
            error.setValue("Por favor, ingrese una ubicación válida");
            return;
        }

        if (dias <= 0 || dias > 14) {
            error.setValue("El número de días debe estar entre 1 y 14");
            return;
        }

        isLoading.setValue(true);
        error.setValue(null);

        String url = BASE_URL + "?key=" + API_KEY + "&q=id:" + idLocation + "&days=" + dias;

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
                    parsePronosticoResponse(responseData);

                    isLoading.postValue(false);
                } catch (Exception e) {
                    isLoading.postValue(false);
                    error.postValue("Error al procesar los datos: " + e.getMessage());
                }
            }
        });
    }

    private void parsePronosticoResponse(String jsonData) {
        List<Pronostico> pronosticos = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            // Obtener nombre de la ciudad
            JSONObject location = jsonObject.getJSONObject("location");
            String cityNameValue = location.getString("name");
            cityName.postValue(cityNameValue);

            // Obtener pronósticos
            JSONObject forecast = jsonObject.getJSONObject("forecast");
            JSONArray forecastDays = forecast.getJSONArray("forecastday");

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

            for (int i = 0; i < forecastDays.length(); i++) {
                JSONObject dayForecast = forecastDays.getJSONObject(i);
                String dateStr = dayForecast.getString("date");

                // Convertir formato de fecha
                Date date = inputFormat.parse(dateStr);
                String formattedDate = outputFormat.format(date);

                JSONObject day = dayForecast.getJSONObject("day");

                double maxTemp = day.getDouble("maxtemp_c");
                double minTemp = day.getDouble("mintemp_c");

                JSONObject condition = day.getJSONObject("condition");
                String weatherCondition = condition.getString("text");
                String iconUrl = "https:" + condition.getString("icon");

                double avgHumidity = day.getDouble("avghumidity");
                double chanceOfRain = day.getDouble("daily_chance_of_rain");

                Pronostico pronostico = new Pronostico(
                        formattedDate,
                        maxTemp,
                        minTemp,
                        weatherCondition,
                        iconUrl,
                        avgHumidity,
                        chanceOfRain
                );

                pronosticos.add(pronostico);
            }

            pronosticoList.postValue(pronosticos);

            if (pronosticos.isEmpty()) {
                error.postValue("No se encontraron pronósticos para esta ubicación");
            }

        } catch (JSONException | ParseException e) {
            error.postValue("Error al analizar los datos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Limpia la lista de pronósticos
     */
    public void clearPronosticos() {
        pronosticoList.setValue(new ArrayList<>());
        cityName.setValue(null);
        error.setValue(null);
    }
}