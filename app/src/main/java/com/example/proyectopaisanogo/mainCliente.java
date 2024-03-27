package com.example.proyectopaisanogo;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;

public class mainCliente extends Fragment {

    private MainClienteViewModel mViewModel;

    //FIREBASE AUTHENTICATOR. logout
    private FirebaseAuth mAuth;


    public static mainCliente newInstance() {
        return new mainCliente();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_cliente, container, false);

        //Logout
        mAuth = FirebaseAuth.getInstance();


        //********Comunicación entre activities
        String nombre = getIntent().getStringExtra("nombre");
        //TextView etiquetaNomUser = findViewById(R.id.cajaCorreo);
        if (nombre != null && !nombre.isEmpty()) {
            Toast.makeText(MainActivity.this, "Hola " + nombre, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Infla los elementos del menú en la barra de opciones
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.inicio) {
            // Maneja el clic en el elemento "Settings"
            // Realiza las acciones relacionadas con la configuración
            return true;
        }else if (item.getItemId() == R.id.logout){
            mAuth.signOut();
            //Vuelta al login
            //startActivity(new Intent(MainActivity.this, Login.class));
            finish(); //finalizo la main

            return true;
        }else return super.onOptionsItemSelected(item);
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainClienteViewModel.class);
        // TODO: Use the ViewModel
    }

}