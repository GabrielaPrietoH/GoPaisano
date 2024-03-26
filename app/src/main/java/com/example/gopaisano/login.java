package com.example.gopaisano;

import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class login extends Fragment {

    private LoginViewModel mViewModel;
    private Button cliente, empresa;

    public static login newInstance() {
        return new login();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        cliente = (Button) view.findViewById(R.id.buttonCliente);
        cliente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear una instancia del fragmento al que deseas navegar (por ejemplo, FragmentB)
                Fragment fragmentB = getActivity().getSupportFragmentManager().findFragmentById(R.id.action_login_to_loginCliente);

                // Iniciar la transacción para reemplazar el fragmento actual con FragmentB
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.login, fragmentB); // R.id.content_main es el contenedor principal de tus fragmentos
                transaction.addToBackStack(null); // Opcional: agregar a la pila de retroceso
                transaction.commit();
            }
        });

        empresa = (Button) view.findViewById(R.id.buttonEmpresa);
        empresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear una instancia del fragmento al que deseas navegar (por ejemplo, FragmentB)
                Fragment fragmentB = getActivity().getSupportFragmentManager().findFragmentById(R.id.action_login_to_loginEmpresa);

                // Iniciar la transacción para reemplazar el fragmento actual con FragmentB
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.login, fragmentB); // R.id.content_main es el contenedor principal de tus fragmentos
                transaction.addToBackStack(null); // Opcional: agregar a la pila de retroceso
                transaction.commit();
            }
        });

        // Configurar otros elementos de tu fragmento si es necesario

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        // TODO: Use the ViewModel
    }

}