package com.example.proyectopaisanogo.Presentation;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.proyectopaisanogo.R;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainCliente recycleviewfragment = new MainCliente();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
        fragmentTransaction1.add(R.id.fragment_container, recycleviewfragment); // Reemplaza "fragment_container" con el ID de tu contenedor de fragmentos
        fragmentTransaction1.commit();

// En tu MainActivity
        FragmentLogin login = new FragmentLogin();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, login); // Reemplaza "fragment_container" con el ID de tu contenedor de fragmentos
        fragmentTransaction.commit();

    }
}
