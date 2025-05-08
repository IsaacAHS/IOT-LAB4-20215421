package com.example.teleweather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DeporteAdapter extends RecyclerView.Adapter<DeporteAdapter.PartidoViewHolder> {
    private List<Partido> partidosList = new ArrayList<>();

    public void setPartidosList(List<Partido> partidosList) {
        this.partidosList = partidosList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PartidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_deporte, parent, false);
        return new PartidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PartidoViewHolder holder, int position) {
        Partido partido = partidosList.get(position);
        holder.bind(partido);
    }

    @Override
    public int getItemCount() {
        return partidosList != null ? partidosList.size() : 0;
    }

    static class PartidoViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvEquipos;
        private final TextView tvLiga;
        private final TextView tvEstadio;
        private final TextView tvFecha;
        private final TextView tvHora;

        public PartidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEquipos = itemView.findViewById(R.id.textViewPartido);
            tvLiga = itemView.findViewById(R.id.textViewLiga);
            tvEstadio = itemView.findViewById(R.id.textViewEstadio);
            tvFecha = itemView.findViewById(R.id.textViewFecha);
            tvHora = itemView.findViewById(R.id.textViewHora);
        }

        public void bind(Partido partido) {
            // Mostrar el nombre del partido/match en lugar de los equipos
            String nombrePartido = partido.getNombrePartido();
            if (nombrePartido == null || nombrePartido.isEmpty() || nombrePartido.equals("Desconocido")) {
                nombrePartido = partido.getEquipoLocal() + " vs " + partido.getEquipoVisitante();
            }
            tvEquipos.setText(nombrePartido);

            // Mostrar liga y estadio
            tvLiga.setText(partido.getLiga());
            tvEstadio.setText(partido.getEstadio());

            // Formatear y separar fecha y hora
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

                Date date = inputFormat.parse(partido.getFecha());
                if (date != null) {
                    tvFecha.setText(dateFormat.format(date));
                    // Mostrar la hora o el estado del partido
                    if ("Por comenzar".equals(partido.getEstado())) {
                        tvHora.setText(timeFormat.format(date));
                    } else {
                        tvHora.setText(partido.getEstado());
                    }
                } else {
                    tvFecha.setText(partido.getFecha());
                    tvHora.setText(partido.getEstado());
                }
            } catch (ParseException e) {
                // En caso de error, mostrar los datos sin formato
                tvFecha.setText(partido.getFecha());
                tvHora.setText(partido.getEstado());
            }
        }
    }
}