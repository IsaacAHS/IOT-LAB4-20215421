package com.example.teleweather;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teleweather.R;
import com.example.teleweather.LocationAdapter;
import com.example.teleweather.Location;
import com.example.teleweather.LocationViewModel;

public class LocationFragment extends Fragment implements LocationAdapter.OnLocationClickListener {

    private LocationViewModel viewModel;
    private RecyclerView recyclerView;
    private LocationAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private EditText etLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(LocationViewModel.class);

        // Inicializar vistas
        etLocation = view.findViewById(R.id.editTextLocation);
        Button btnSearch = view.findViewById(R.id.buttonSearch);
        recyclerView = view.findViewById(R.id.recyclerViewLocations);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.textViewNoResults);



        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new LocationAdapter(this);
        recyclerView.setAdapter(adapter);

        // Configurar listeners
        btnSearch.setOnClickListener(v -> {
            String query = etLocation.getText().toString().trim();
            viewModel.searchLocation(query);
        });

        // Observar cambios en el ViewModel
        viewModel.getLocationList().observe(getViewLifecycleOwner(), locations -> {
            adapter.setLocationList(locations);
            updateEmptyView(locations.isEmpty());
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
    }

    @Override
    public void onLocationClick(Location location) {
        // Navegar al fragmento de pronóstico enviando el ID de ubicación
        Bundle args = new Bundle();
        args.putString("locationId", String.valueOf(location.getId()));
        Navigation.findNavController(requireView())
                .navigate(R.id.action_locationFragment_to_pronosticoFragment, args);
    }

    private void updateEmptyView(boolean isEmpty) {
        if (isEmpty && viewModel.getError().getValue() == null) {
            emptyView.setText(R.string.forecast_not_found);
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}