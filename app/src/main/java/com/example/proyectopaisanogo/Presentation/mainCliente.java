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
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectopaisanogo.Adapter.HelperAdapter;
import com.example.proyectopaisanogo.Model.Empresa;
import com.example.proyectopaisanogo.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class mainCliente extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private FirebaseFirestore firestore;
    private DrawerLayout drawerLayout;
    private RecyclerView recyclerView;
    private FirestoreRecyclerAdapter<Empresa, HelperAdapter.ViewHolder> firestoreAdapter;

    public static com.example.proyectopaisanogo.Presentation.mainCliente newInstance() {
        return new com.example.proyectopaisanogo.Presentation.mainCliente();
    }

    private FirebaseAuth mAuth;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_cliente, container, false);

        firestore = FirebaseFirestore.getInstance();
        recyclerView = v.findViewById(R.id.RvEmpresas);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        drawerLayout = v.findViewById(R.id.drawerLayout);
        NavigationView navigationView = v.findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);


        Query query = firestore.collection("registroEmpresa");
        FirestoreRecyclerOptions<Empresa> options = new FirestoreRecyclerOptions.Builder<Empresa>()
                .setQuery(query, Empresa.class)
                .build();

        firestoreAdapter = new FirestoreRecyclerAdapter<Empresa, HelperAdapter.ViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HelperAdapter.ViewHolder viewHolder, int i, @NonNull Empresa empresa) {
                viewHolder.nombreEmpresa.setText(empresa.getNombreEmpresa());
                viewHolder.cif.setText(empresa.getCif());
                viewHolder.cp.setText(empresa.getCp());
                viewHolder.direccion.setText(empresa.getDireccion());
                viewHolder.email.setText(empresa.getEmail());
                viewHolder.telefono.setText(empresa.getTelefono());
                viewHolder.userID.setText(empresa.getUserID());
            }

            @NonNull
            @Override
            public HelperAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_item, parent, false);
                return new HelperAdapter.ViewHolder(view);
            }
        };
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
        mAuth = FirebaseAuth.getInstance();
        drawerLayout.closeDrawers();
        return true;

    }
        @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
            MainClienteViewModel mViewModel = new ViewModelProvider(this).get(MainClienteViewModel.class);

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