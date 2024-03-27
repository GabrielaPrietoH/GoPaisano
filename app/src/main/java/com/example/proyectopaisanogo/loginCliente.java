package com.example.proyectopaisanogo;

import static com.example.proyectopaisanogo.R.id.buttonLoginC;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

public class loginCliente extends Fragment {

    Button loginC;
    TextView registro;
    private LoginClienteViewModel mViewModel;

    public static loginCliente newInstance() {
        return new loginCliente();
    }

    @SuppressLint("ResourceType")

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_cliente, container, false);
        loginC = rootView.findViewById(R.id.buttonLoginC);
        loginC.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Crear un nuevo fragmento y transacción
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, mainCliente.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("nombre") // El nombre puede ser nulo
                        .commit();
            }

        });

        registro = rootView.findViewById(R.id.textViewRegistroC);
        registro.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Crear un nuevo fragmento y transacción
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, registroCliente.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("nombre") // El nombre puede ser nulo
                        .commit();
            }
        });
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginClienteViewModel.class);
        // TODO: Use the ViewModel
    }

}