package com.example.proyectopaisanogo.Presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.proyectopaisanogo.R;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

public class calendarioCliente extends Fragment  implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_calendario_cliente, container, false);

        setupToolbar(view);
        return view;
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbarCalendarioCliente);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(toolbar);
        Objects.requireNonNull(activity.getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setTitle("Calendario Cliente");
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Manejar el clic en el ícono de retroceso en la barra de herramientas
        if (menuItem.getItemId() == android.R.id.home) {
            // Navegar hacia atrás
            requireActivity().onBackPressed();
            // Mostrar un Toast indicando que se ha seleccionado la opción
            Toast.makeText(requireContext(), "Regresando al menú principal", Toast.LENGTH_SHORT).show();
            return true; // Devolver true para indicar que el evento fue manejado



        }
        return super.onOptionsItemSelected(menuItem);
    }

}

