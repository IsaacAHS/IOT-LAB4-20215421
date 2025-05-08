package com.example.teleweather;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.navigation.fragment.NavHostFragment;


import com.google.android.material.bottomnavigation.BottomNavigationView;

public class AppActivity extends AppCompatActivity {

    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app);

        // Configurar la navegación inferior
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_nav_view);
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();


        // Configuración para que no se muestre la flecha de retroceso en los destinos principales
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.locationFragment, R.id.pronosticoFragment, R.id.deportesFragment)
                .build();
        /*NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);*/
        NavigationUI.setupWithNavController(bottomNavigationView, navController);

        // Evitar que los fragmentos se apilen en el backstack
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            // Si es uno de los destinos principales, limpiar el backstack
            if (destination.getId() == R.id.locationFragment ||
                    destination.getId() == R.id.pronosticoFragment ||
                    destination.getId() == R.id.deportesFragment) {
                controller.popBackStack(destination.getId(), false);
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Si estamos en uno de los destinos principales, salir de la app
        int currentDestinationId = navController.getCurrentDestination().getId();
        if (currentDestinationId == R.id.locationFragment ||
                currentDestinationId == R.id.pronosticoFragment ||
                currentDestinationId == R.id.deportesFragment) {
            finish();
        } else {
            super.onBackPressed();
        }
    }
}