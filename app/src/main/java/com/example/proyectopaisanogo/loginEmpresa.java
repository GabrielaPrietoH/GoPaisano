package com.example.proyectopaisanogo;

import static com.example.proyectopaisanogo.R.id.buttonLoginC;

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
import androidx.lifecycle.ViewModelProvider;

public class loginEmpresa extends Fragment {
    Button loginE;
    TextView registro;
    private LoginEmpresaViewModel mViewModel;

    public static loginEmpresa newInstance() {
        return new loginEmpresa();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login_empresa, container, false);
        loginE = rootView.findViewById(R.id.buttonLoginE);
        loginE.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Crear un nuevo fragmento y transacción
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, mainEmpresa.class, null)
                        .setReorderingAllowed(true)
                        .addToBackStack("nombre") // El nombre puede ser nulo
                        .commit();
            }

        });

        registro = rootView.findViewById(R.id.textViewRegistroE);
        registro.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Crear un nuevo fragmento y transacción
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, registroEmpresa.class, null)
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
        mViewModel = new ViewModelProvider(this).get(LoginEmpresaViewModel.class);
        // TODO: Use the ViewModel
    }

}