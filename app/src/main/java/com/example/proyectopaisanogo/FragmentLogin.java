package com.example.proyectopaisanogo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

public class FragmentLogin extends Fragment {

    private loginViewModel mViewModel;
    private Button cliente, empresa;

    public static FragmentLogin newInstance() {
        return new FragmentLogin();
    }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_login, container, false);
            cliente = rootView.findViewById(R.id.buttonCliente);
            cliente.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Crear un nuevo fragmento y transacción
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, loginCliente.class, null)
                            .setReorderingAllowed(true)
                            .addToBackStack("nombre") // El nombre puede ser nulo
                            .commit();
                }
            });

            empresa = rootView.findViewById(R.id.buttonEmpresa);
            empresa.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // Crear un nuevo fragmento y transacción
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction()
                            .replace(R.id.fragment_container, loginEmpresa.class, null)
                            .setReorderingAllowed(true)
                            .addToBackStack("nombre") // El nombre puede ser nulo
                            .commit();
                }
            });
            return rootView;
        }

/**

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(loginViewModel.class);
        // TODO: Use the ViewModel
    }
**/
}