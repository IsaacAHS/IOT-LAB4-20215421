package com.example.teleweather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.teleweather.R;
import com.example.teleweather.Pronostico;

import java.util.List;

public class PronosticoAdapter extends RecyclerView.Adapter<PronosticoAdapter.PronosticoViewHolder> {
    private List<Pronostico> pronosticoList;

    public void setPronosticoList(List<Pronostico> pronosticoList) {
        this.pronosticoList = pronosticoList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PronosticoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pronostico, parent, false);
        return new PronosticoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PronosticoViewHolder holder, int position) {
        Pronostico pronostico = pronosticoList.get(position);
        holder.bind(pronostico);
    }

    @Override
    public int getItemCount() {
        return pronosticoList != null ? pronosticoList.size() : 0;
    }

    static class PronosticoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvFecha;
        private final TextView tvTemperaturas;
        private final TextView tvCondicion;
        private final TextView tvHumedad;
        private final TextView tvProbLluvia;
        private final ImageView ivIcono;

        public PronosticoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.textViewFecha);
            tvTemperaturas = itemView.findViewById(R.id.textViewTempMaxMin);
            tvCondicion = itemView.findViewById(R.id.textViewCondicion);
            tvHumedad = itemView.findViewById(R.id.textViewViento); // si quieres mostrar viento
            tvProbLluvia = itemView.findViewById(R.id.textViewProbLluvia);
            ivIcono = itemView.findViewById(R.id.imageViewCondicion);

        }

        public void bind(Pronostico pronostico) {
            tvFecha.setText(pronostico.getFecha());

            String temperaturas = String.format("Máx: %.1f°C / Mín: %.1f°C",
                    pronostico.getTemperaturaMaxima(), pronostico.getTemperaturaMinima());
            tvTemperaturas.setText(temperaturas);

            tvCondicion.setText(pronostico.getCondicionClima());

            String humedad = String.format("Humedad: %.0f%%", pronostico.getHumedad());
            tvHumedad.setText(humedad);

            String probLluvia = String.format("Prob. de lluvia: %.0f%%", pronostico.getProbabilidadLluvia());
            tvProbLluvia.setText(probLluvia);

            // Cargar icono usando Glide
            Glide.with(itemView.getContext())
                    .load(pronostico.getIconoUrl())
                    .placeholder(R.drawable.ic_placeholder_weather)
                    .error(R.drawable.ic_error_weather)
                    .into(ivIcono);
        }
    }
}