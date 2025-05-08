package com.example.teleweather;

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
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teleweather.R;
import com.example.teleweather.PronosticoAdapter;
import com.example.teleweather.PronosticoViewModel;

public class PronosticoFragment extends Fragment {

    private PronosticoViewModel viewModel;
    private RecyclerView recyclerView;
    private PronosticoAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private TextView tvCityName;
    private EditText etLocationId;
    private EditText etDays;
    private View searchLayout;

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