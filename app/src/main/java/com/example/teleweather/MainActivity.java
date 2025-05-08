package com.example.teleweather;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    private MaterialButton btnCheckConnection;
    private MaterialButton btnStart;
    private TextView tvConnectionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializar vistas
        btnCheckConnection = findViewById(R.id.btnCheckConnection);
        btnStart = findViewById(R.id.btnStart);
        tvConnectionStatus = findViewById(R.id.tvConnectionStatus);

        // Verificar conexión automáticamente al iniciar la app
        checkInternetConnection();

        // Configurar el listener para el botón de comprobar conexión
        btnCheckConnection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkInternetConnection();
            }
        });

        // Configurar el listener para el botón de empezar
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Código para navegar a la siguiente actividad
                Toast.makeText(MainActivity.this, "¡Iniciando aplicación!", Toast.LENGTH_SHORT).show();

                // Lógica para iniciar la siguiente actividad
                Intent intent = new Intent(MainActivity.this, AppActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Verifica si hay conexión a Internet disponible
     */
    private void checkInternetConnection() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                // Hay conexión a Internet
                tvConnectionStatus.setText("Estado de conexión: Conectado");
                tvConnectionStatus.setTextColor(Color.parseColor("#4CAF50")); // Verde

                // Activar botón empezar
                btnStart.setEnabled(true);
                btnStart.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.mi_verde));


                Toast.makeText(this, "¡Conexión a Internet establecida!", Toast.LENGTH_SHORT).show();
            } else {
                // No hay conexión a Internet
                tvConnectionStatus.setText("Estado de conexión: Sin conexión");
                tvConnectionStatus.setTextColor(Color.parseColor("#F44336")); // Rojo

                // Desactivar botón empezar
                btnStart.setEnabled(false);
                btnStart.setBackgroundTintList(getResources().getColorStateList(android.R.color.darker_gray));

                // Mostrar diálogo para impedir el acceso y redirigir a ajustes
                showNoConnectionDialog();
            }
        }
    }

    /**
     * Muestra un diálogo de alerta cuando no hay conexión a Internet
     */
    private void showNoConnectionDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
        builder.setTitle("Sin conexión a Internet");
        builder.setMessage("No se puede acceder a la aplicación sin conexión a Internet. Por favor, verifica tu configuración de red.");
        builder.setCancelable(false);

        // Botón para ir a los ajustes de red
        builder.setPositiveButton("Configuración", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                // Abrir los ajustes de red del dispositivo
                startActivity(new android.content.Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
            }
        });

        // Botón para volver a comprobar la conexión
        builder.setNegativeButton("Reintentar", new android.content.DialogInterface.OnClickListener() {
            @Override
            public void onClick(android.content.DialogInterface dialog, int which) {
                checkInternetConnection();
            }
        });

        androidx.appcompat.app.AlertDialog dialog = builder.create();
        dialog.show();
    }
}