package com.example.teleweather;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
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

public class DeporteFragment extends Fragment {

    private DeporteViewModel viewModel;
    private RecyclerView recyclerView;
    private DeporteAdapter adapter;
    private ProgressBar progressBar;
    private TextView emptyView;
    private EditText etLocation;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_deporte, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(DeporteViewModel.class);

        // Inicializar vistas
        etLocation = view.findViewById(R.id.editTextCityRegion);
        Button btnSearch = view.findViewById(R.id.buttonSearchSports);
        recyclerView = view.findViewById(R.id.recyclerViewDeportes);
        progressBar = view.findViewById(R.id.progressBar);
        emptyView = view.findViewById(R.id.textViewNoResults);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new DeporteAdapter();
        recyclerView.setAdapter(adapter);

        // Configurar listeners
        btnSearch.setOnClickListener(v -> buscarPartidos());

        // Permitir búsqueda al presionar Enter
        etLocation.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                buscarPartidos();
                return true;
            }
            return false;
        });

        // Observar cambios en el ViewModel
        viewModel.getPartidosList().observe(getViewLifecycleOwner(), partidos -> {
            adapter.setPartidosList(partidos);
            updateEmptyView(partidos.isEmpty());
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            btnSearch.setEnabled(!isLoading);
            etLocation.setEnabled(!isLoading);
        });

        viewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                emptyView.setText(error);
                emptyView.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
            } else {
                emptyView.setVisibility(View.GONE);
            }
        });
    }

    private void buscarPartidos() {
        String location = etLocation.getText().toString().trim();
        if (location.isEmpty()) {
            Toast.makeText(requireContext(), R.string.error_empty_location, Toast.LENGTH_SHORT).show();
            return;
        }
        viewModel.buscarPartidos(location);
    }

    private void updateEmptyView(boolean isEmpty) {
        if (isEmpty && viewModel.getError().getValue() == null) {
            emptyView.setText(R.string.no_partidos_found);
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }
}