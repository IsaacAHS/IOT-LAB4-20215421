package com.example.teleweather;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teleweather.R;
import com.example.teleweather.PronosticoAdapter;
import com.example.teleweather.PronosticoViewModel;

public class PronosticoFragment extends Fragment implements SensorEventListener {

    private PronosticoViewModel viewModel;
    private RecyclerView recyclerView;
    private PronosticoAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private TextView tvCityName;
    private EditText etLocationId;
    private EditText etDays;
    private View searchLayout;

    // Sensor de agitación
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private static final float SHAKE_THRESHOLD = 20.0f; // Umbral de aceleración para detectar agitación
    private long lastUpdate = 0;
    private float lastX, lastY, lastZ;
    private static final int MIN_TIME_BETWEEN_SHAKES = 1000; // Milisegundos mínimos entre agitaciones

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_pronostico, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(PronosticoViewModel.class);

        // Inicializar vistas
        etLocationId = view.findViewById(R.id.editTextLocationId);
        etDays = view.findViewById(R.id.editTextDays);
        Button btnSearch = view.findViewById(R.id.buttonSearchForecast);
        recyclerView = view.findViewById(R.id.recyclerViewPronosticos);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.textViewNoResults);
        searchLayout = view.findViewById(R.id.layoutInputs);
        tvCityName = view.findViewById(R.id.textViewCityName);

        // Inicializar el sensor manager y el acelerómetro
        sensorManager = (SensorManager) requireActivity().getSystemService(Context.SENSOR_SERVICE);
        if (sensorManager != null) {
            accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new PronosticoAdapter();
        recyclerView.setAdapter(adapter);

        // Configurar listeners
        btnSearch.setOnClickListener(v -> {
            String locationId = etLocationId.getText().toString().trim();
            String daysStr = etDays.getText().toString().trim();

            int days;
            try {
                days = Integer.parseInt(daysStr);
            } catch (NumberFormatException e) {
                days = 7; // Valor por defecto
            }

            if (days <= 0 || days > 14) {
                Toast.makeText(requireContext(), "El número de días debe estar entre 1 y 14", Toast.LENGTH_SHORT).show();
                return;
            }

            viewModel.getPronosticoPorIdLocation(locationId, days);
        });

        // Observar cambios en el ViewModel
        viewModel.getPronosticoList().observe(getViewLifecycleOwner(), pronosticos -> {
            adapter.setPronosticoList(pronosticos);
            updateEmptyView(pronosticos.isEmpty());
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                emptyView.setText(error);
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            } else {
                emptyView.setVisibility(View.GONE);
            }
        });

        viewModel.getCityName().observe(getViewLifecycleOwner(), cityName -> {
            if (cityName != null && !cityName.isEmpty()) {
                tvCityName.setText(cityName);
                tvCityName.setVisibility(View.VISIBLE);
            } else {
                tvCityName.setVisibility(View.GONE);
            }
        });

        // Verificar si se viene de LocationFragment
        String locationId = null;
        if (getArguments() != null) {
            locationId = getArguments().getString("locationId");
        }

        if (locationId != null && !locationId.isEmpty()) {
            // Si viene de LocationFragment, ocultar la búsqueda y cargar pronóstico automáticamente
            searchLayout.setVisibility(View.GONE);
            viewModel.getPronosticoPorIdLocation(locationId, 7); // 7 días por defecto
        } else {
            // Mostrar formulario de búsqueda
            searchLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // Registrar el listener del sensor cuando el fragmento está en primer plano
        if (sensorManager != null && accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Desregistrar el listener del sensor cuando el fragmento no está en primer plano
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            long currentTime = System.currentTimeMillis();

            // Sólo procesar si ha pasado suficiente tiempo desde la última actualización
            if ((currentTime - lastUpdate) > MIN_TIME_BETWEEN_SHAKES) {
                long diffTime = currentTime - lastUpdate;
                lastUpdate = currentTime;

                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                // Cálcular el cambio de aceleración
                float speed = Math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000;

                // Si la aceleración supera el umbral, detectamos una agitación
                if (speed > SHAKE_THRESHOLD) {
                    onShakeDetected();
                }

                lastX = x;
                lastY = y;
                lastZ = z;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No necesitamos implementar esto para el detector de agitación
    }

    /**
     * Método llamado cuando se detecta una agitación del dispositivo
     */
    private void onShakeDetected() {
        // Verificar si hay pronósticos para eliminar
        if (viewModel.getPronosticoList().getValue() != null &&
                !viewModel.getPronosticoList().getValue().isEmpty()) {

            // Mostrar diálogo de confirmación
            showConfirmationDialog();
        }
    }

    /**
     * Muestra un diálogo de confirmación para limpiar los pronósticos
     */
    private void showConfirmationDialog() {
        if (isAdded() && getContext() != null) {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Limpiar pronósticos")
                    .setMessage("¿Desea eliminar los pronósticos mostrados?")
                    .setPositiveButton("Sí", (dialog, which) -> {
                        // Limpiar los pronósticos
                        viewModel.clearPronosticos();
                        Toast.makeText(requireContext(), "Pronósticos eliminados", Toast.LENGTH_SHORT).show();

                        // Navegar a la AppActivity
                        navigateToAppActivity();
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }

    /**
     * Navega a la AppActivity
     */
    private void navigateToAppActivity() {
        // Crear Intent para abrir AppActivity
        Intent intent = new Intent(requireContext(), AppActivity.class);
        // Limpiar la pila de actividades para que AppActivity sea la única actividad
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void updateEmptyView(boolean isEmpty) {
        if (isEmpty && viewModel.getError().getValue() == null) {
            emptyView.setText(R.string.no_results);
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}