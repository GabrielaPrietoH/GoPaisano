package com.example.proyectopaisanogo.Presentation;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectopaisanogo.Adapter.HelperAdapterEmpresa;
import com.example.proyectopaisanogo.Model.Empresa;
import com.example.proyectopaisanogo.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class mainEmpresa extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private HelperAdapterEmpresa firestoreAdapter;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firestore;
    private String userID; // Variable para almacenar el ID del usuario


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAuth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Obtener el ID del usuario actual
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userID = currentUser.getUid();
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_empresa, container, false); // Asegúrate de que el layout sea el correcto

        RecyclerView recyclerView = v.findViewById(R.id.RvEmpresa);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        drawerLayout = v.findViewById(R.id.drawerLayout);
        NavigationView navigationView = v.findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        // Construir la consulta para obtener la empresa del usuario actual
        Query query = firestore.collection("registroEmpresa").whereEqualTo("userID", userID);
        FirestoreRecyclerOptions<Empresa> options = new FirestoreRecyclerOptions.Builder<Empresa>()
                .setQuery(query, Empresa.class)
                .build();

        firestoreAdapter = new HelperAdapterEmpresa(options, requireContext()); // Cambiado a HelperAdapterEmpresa

        setupToolbar(v);
        recyclerView.setAdapter(firestoreAdapter);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        firestoreAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        firestoreAdapter.stopListening();
    }

    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.setting) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, settingCliente.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("Setting") // El nombre puede ser nulo
                    .commit();

        } else if (id == R.id.calendar) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, calendarioCliente.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("Calendario") // El nombre puede ser nulo
                    .commit();

        } else if (id == R.id.logout) {
            mAuth.signOut();
            // Crear un nuevo fragmento y transacción
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, loginCliente.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("Logout") // El nombre puede ser nulo
                    .commit();
        }

        drawerLayout.closeDrawers();
        return true;
    }

    private void setupToolbar(View view) {
        Toolbar toolbar;
        toolbar = view.findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(requireActivity(), drawerLayout, toolbar,
                R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

}

