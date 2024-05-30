package com.example.proyectopaisanogo.Presentation;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.proyectopaisanogo.Presentation.Cliente.LoginCliente;
import com.example.proyectopaisanogo.Presentation.Empresa.LoginEmpresa;
import com.example.proyectopaisanogo.R;

/**
 * Fragmento de inicio de sesión.
 *
 * Este fragmento presenta dos botones que redirigen a los fragmentos de inicio de sesión
 * correspondientes para cada tipo de usuario, ya sean clientes o empresas.
 */
public class FragmentLogin extends Fragment {

    /**
     * Método que inicializa la vista del fragmento.
     *
     * @param inflater El LayoutInflater que se usa para inflar la vista del fragmento.
     * @param container  El ViewGroup padre al que se adjunta la vista del fragmento.
     * @param savedInstanceState Si no es nulo, se reutiliza el estado guardado previamente.
     * @return La vista inflada del fragmento.
     */
     @Override
     public View onCreateView(LayoutInflater inflater, ViewGroup container,
             Bundle savedInstanceState) {
         View rootView = inflater.inflate(R.layout.fragment_login, container, false);
         Button cliente = rootView.findViewById(R.id.buttonCliente);
         cliente.setOnClickListener(v -> {
             FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
             fragmentManager.beginTransaction()
                     .replace(R.id.fragment_container, LoginCliente.class, null)
                     .setReorderingAllowed(true)
                     .addToBackStack("nombre")
                     .commit();
         });
         Button empresa = rootView.findViewById(R.id.buttonEmpresa);
         empresa.setOnClickListener(v -> {
             FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
             fragmentManager.beginTransaction()
                     .replace(R.id.fragment_container, LoginEmpresa.class, null)
                     .setReorderingAllowed(true)
                     .addToBackStack("nombre")
                     .commit();
         });
         return rootView;
     }
}