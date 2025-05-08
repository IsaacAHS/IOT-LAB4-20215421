package com.example.teleweather;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teleweather.R;
import com.example.teleweather.Location;

import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.LocationViewHolder> {
    private List<Location> locationList;
    private final OnLocationClickListener locationClickListener;

    public interface OnLocationClickListener {
        void onLocationClick(Location location);
    }

    public LocationAdapter(OnLocationClickListener listener) {
        this.locationClickListener = listener;
    }

    public void setLocationList(List<Location> locationList) {
        this.locationList = locationList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public LocationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_location, parent, false);
        return new LocationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LocationViewHolder holder, int position) {
        Location location = locationList.get(position);
        holder.bind(location);
    }

    @Override
    public int getItemCount() {
        return locationList != null ? locationList.size() : 0;
    }

    class LocationViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvName;
        private final TextView tvRegion;
        private final TextView tvCountry;
        private final TextView tvCoordinates;

        public LocationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textViewLocationName);
            tvRegion = itemView.findViewById(R.id.textViewLocationRegion);
            tvCountry = itemView.findViewById(R.id.textViewLocationCountry);
            tvCoordinates = itemView.findViewById(R.id.textViewLocationCoordinates);


            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && locationClickListener != null) {
                    locationClickListener.onLocationClick(locationList.get(position));
                }
            });
        }

        public void bind(Location location) {
            tvName.setText(location.getName());
            tvRegion.setText(location.getRegion());
            tvCountry.setText(location.getCountry());
            String coordinates = String.format("Lat: %.2f, Lon: %.2f",
                    location.getLat(), location.getLon());
            tvCoordinates.setText(coordinates);
        }
    }
}