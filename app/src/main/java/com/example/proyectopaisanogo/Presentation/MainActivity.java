package com.example.proyectopaisanogo.Presentation;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.proyectopaisanogo.Presentation.Cliente.MainCliente;
import com.example.proyectopaisanogo.R;
/**
 * Actividad principal de la aplicación.
 *
 * Esta actividad gestiona la inicialización del fragmento de login y el fragmento
 * de la vista principal de cada usuario.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Método que se llama cuando se crea la actividad.
     *
     * @param savedInstanceState Si la actividad se está recreando a partir de un estado guardado
     *                           anteriormente, este es el estado.
     */
    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainCliente recycleviewfragment = new MainCliente();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
        fragmentTransaction1.add(R.id.fragment_container, recycleviewfragment);
        fragmentTransaction1.commit();


        FragmentLogin login = new FragmentLogin();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, login);
        fragmentTransaction.commit();

    }
}
