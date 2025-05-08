package com.example.teleweather;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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

public class DeporteViewModel extends ViewModel {
    private final OkHttpClient client = new OkHttpClient();
    private final String API_KEY = "ec24b1c6dd8a4d528c1205500250305";
    private final String BASE_URL = "https://api.weatherapi.com/v1/sports.json";

    private final MutableLiveData<List<Partido>> partidosList = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();

    public DeporteViewModel() {
        partidosList.setValue(new ArrayList<>());
        isLoading.setValue(false);
    }

    public LiveData<List<Partido>> getPartidosList() {
        return partidosList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<String> getError() {
        return error;
    }

    public void buscarPartidos(String location) {
        if (location == null || location.trim().isEmpty()) {
            error.setValue("Por favor, ingrese una ubicación válida");
            return;
        }

        isLoading.setValue(true);
        error.setValue(null);

        // Limpiar la lista actual
        partidosList.setValue(new ArrayList<>());

        String url = BASE_URL + "?key=" + API_KEY + "&q=" + location;

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
                isLoading.postValue(false);

                if (!response.isSuccessful()) {
                    try {
                        String errorBody = response.body() != null ? response.body().string() : "Error desconocido";
                        JSONObject errorJson = new JSONObject(errorBody);
                        if (errorJson.has("error")) {
                            error.postValue(errorJson.getJSONObject("error").optString("message", "Error al obtener datos"));
                        } else {
                            error.postValue("Error: " + response.code() + " - " + response.message());
                        }
                    } catch (Exception e) {
                        error.postValue("Error: " + response.code() + " - " + response.message());
                    }
                    return;
                }

                try {
                    String responseData = response.body().string();
                    List<Partido> partidos = parsePartidos(responseData);

                    partidosList.postValue(partidos);

                    if (partidos.isEmpty()) {
                        error.postValue("No se encontraron partidos para esta ubicación");
                    }
                } catch (Exception e) {
                    error.postValue("Error al procesar los datos: " + e.getMessage());
                }
            }
        });
    }

    private List<Partido> parsePartidos(String jsonData) {
        List<Partido> partidos = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(jsonData);

            // Verificar si hay datos de fútbol
            if (jsonObject.has("football")) {
                JSONArray footballArray = jsonObject.getJSONArray("football");

                for (int i = 0; i < footballArray.length(); i++) {
                    JSONObject matchObj = footballArray.getJSONObject(i);

                    // Intentar obtener el nombre del partido/match primero
                    String nombrePartido = matchObj.optString("match", "");

                    // Si no hay nombre de partido, intentar con "tournament_name"
                    if (nombrePartido.isEmpty()) {
                        nombrePartido = matchObj.optString("tournament_name", "");
                    }

                    // Si aún no hay nombre, intentar con el nombre del estadio
                    if (nombrePartido.isEmpty()) {
                        nombrePartido = matchObj.optString("stadium", "");
                    }

                    // Obtener equipos como respaldo
                    String equipoA = matchObj.optString("team_a", "");
                    String equipoB = matchObj.optString("team_b", "");

                    // Si no se encontraron team_a y team_b, intentar con home_team y away_team
                    if (equipoA.isEmpty() && matchObj.has("home_team")) {
                        equipoA = matchObj.getString("home_team");
                    }
                    if (equipoB.isEmpty() && matchObj.has("away_team")) {
                        equipoB = matchObj.getString("away_team");
                    }

                    String liga = matchObj.optString("tournament", "Torneo desconocido");
                    String estadio = matchObj.optString("stadium", "Estadio desconocido");
                    String fecha = matchObj.optString("start", "");
                    String estado = matchObj.optString("match_status", "Por comenzar");

                    // Formatear la fecha correctamente si es necesario
                    if (!fecha.isEmpty()) {
                        try {
                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                            fecha = inputFormat.format(inputFormat.parse(fecha));
                        } catch (ParseException e) {
                            // Mantener el formato original si hay error
                            e.printStackTrace();
                        }
                    }

                    // Componer el nombre del partido si no se encontró
                    if (nombrePartido.isEmpty() && !equipoA.isEmpty() && !equipoB.isEmpty()) {
                        // Usar el nombre del torneo o liga como contexto si está disponible
                        if (!liga.equals("Torneo desconocido")) {
                            nombrePartido = liga + ": " + equipoA + " - " + equipoB;
                        } else {
                            nombrePartido = "Partido: " + equipoA + " - " + equipoB;
                        }
                    } else if (nombrePartido.isEmpty()) {
                        nombrePartido = "Evento deportivo en " + estadio;
                    }

                    Partido partido = new Partido(
                            equipoA,
                            equipoB,
                            liga,
                            estadio,
                            fecha,
                            estado,
                            nombrePartido
                    );

                    partidos.add(partido);
                }
            } else {
                // Verificar si hay un mensaje de error en la respuesta
                if (jsonObject.has("error")) {
                    JSONObject errorObj = jsonObject.getJSONObject("error");
                    String errorMessage = errorObj.optString("message", "Error desconocido");
                    throw new JSONException("API Error: " + errorMessage);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return partidos;
    }
}