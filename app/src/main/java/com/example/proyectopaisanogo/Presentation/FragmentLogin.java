package com.example.proyectopaisanogo.Presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.proyectopaisanogo.R;

public class FragmentLogin extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            Button cliente = rootView.findViewById(R.id.buttonCliente);
            cliente.setOnClickListener(v -> {
                // Crear un nuevo fragmento y transacción
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, LoginCliente.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("nombre") // El nombre puede ser nulo
                        .commit();
            });

            Button empresa = rootView.findViewById(R.id.buttonEmpresa);
            empresa.setOnClickListener(v -> {
                // Crear un nuevo fragmento y transacción
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, LoginEmpresa.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("nombre") // El nombre puede ser nulo
                        .commit();
            });
            return rootView;
        }
}