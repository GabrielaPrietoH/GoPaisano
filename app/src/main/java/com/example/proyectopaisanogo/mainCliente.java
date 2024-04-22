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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class mainCliente extends Fragment {
    RecyclerView recyclerView;
    ArrayList images, names;
    private MainClienteViewModel mViewModel;

    //FIREBASE AUTHENTICATOR. logout
    private FirebaseAuth mAuth;


    //Calendario
    private OnCalendarButtonClickListener listener;

    public mainCliente() {
        // Required empty public constructor
    }
    public void setOnCalendarButtonClickListener(OnCalendarButtonClickListener listener) {
        this.listener = listener;
    }





    public static mainCliente newInstance() {
        return new mainCliente();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //FIREBASE Logout
        mAuth = FirebaseAuth.getInstance();

    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.logout){
            mAuth.signOut();
            // Crear un nuevo fragmento y transacción
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, loginCliente.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("nombre") // El nombre puede ser nulo
                    .commit();

        }else if(id == R.id.setting){
            // Crear un nuevo fragmento y transacción
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, settingCliente.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("nombre") // El nombre puede ser nulo
                    .commit();

        }else if(id == R.id.calendar){
            // Crear un nuevo fragmento y transacción
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, calendarioCliente.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("nombre") // El nombre puede ser nulo
                    .commit();

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_cliente, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewEmpresas);
        images = new ArrayList();
        names = new ArrayList();

        for(int i=0; i<Data.names.length; i++){
            images.add(Data.images);
            names.add(Data.names);
        }

        HelperAdapter helperAdapter = new HelperAdapter(getContext(), images, names);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(helperAdapter);



        //CALENDARIO
        Button calendarButton = view.findViewById(R.id.calendarButton);  //nombre
        calendarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Notificar a la actividad que se ha hecho clic en el botón del calendario
                if (listener != null) {
                    listener.onCalendarButtonClick();
                }
            }
        });

        return view;

        //Logout
       // mAuth = FirebaseAuth.getInstance();

/**
        //********Comunicación entre activities
        String nombre = getIntent().getStringExtra("nombre");
        //TextView etiquetaNomUser = findViewById(R.id.cajaCorreo);
        if (nombre != null && !nombre.isEmpty()) {
            Toast.makeText(MainActivity.this, "Hola " + nombre, Toast.LENGTH_SHORT).show();
        }
*/

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainClienteViewModel.class);
        // TODO: Use the ViewModel
    }

}