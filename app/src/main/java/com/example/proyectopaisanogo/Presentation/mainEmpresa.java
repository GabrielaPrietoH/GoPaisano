package com.example.proyectopaisanogo.Presentation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

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

import com.bumptech.glide.Glide;
import com.example.proyectopaisanogo.Adapter.HelperViewHolder;
import com.example.proyectopaisanogo.Model.Empresa;
import com.example.proyectopaisanogo.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class mainEmpresa extends Fragment implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private FirestoreRecyclerAdapter<Empresa, HelperViewHolder> firestoreAdapter;
    private StorageReference storageRef;

    private FirebaseAuth mAuth;
    private String userID;


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mAuth = FirebaseAuth.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

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

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
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

        firestoreAdapter = new FirestoreRecyclerAdapter<Empresa, HelperViewHolder>(options) {

            @Override
            protected void onBindViewHolder(@NonNull HelperViewHolder viewHolder, int i, @NonNull Empresa empresa) {
                viewHolder.nombreEmpresa.setText(String.format(empresa.getNombreEmpresa()));
                viewHolder.cif.setText(String.format(empresa.getCif()));
                viewHolder.cp.setText(String.format(empresa.getCp()));
                viewHolder.direccion.setText(String.format(empresa.getDireccion()));
                viewHolder.email.setText(String.format(empresa.getEmail()));
                viewHolder.telefono.setText(String.format(empresa.getTelefono()));
                // Cargar la imagen utilizando Glide
                loadImage(requireContext(), empresa.getUserID(), viewHolder.imageView);
            }

            @NonNull
            @Override
            public HelperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_item, parent, false);
                return new HelperViewHolder(view);
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
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, settingCliente.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("Setting")
                    .commit();

        } else if (id == R.id.calendar) {
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, calendarioCliente.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("Calendario")
                    .commit();

        } else if (id == R.id.logout) {
            mAuth.signOut();
            FragmentManager fragmentManager = getParentFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, loginCliente.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("Logout")
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

    // Método para cargar la imagen desde Firebase Storage usando Glide
    private void loadImage(Context context, String userID, ImageView imageView) {
        StorageReference imageRef = storageRef.child("images/" + userID);
        imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            // Load the image using Glide
            Glide.with(context)
                    .load(uri)
                    .into(imageView);
        }).addOnFailureListener(exception -> {
            // Handle any errors
            // For now, you can display a placeholder image or a message
            // For example:
            // imageView.setImageResource(R.drawable.placeholder_image);
            // Or
            // imageView.setVisibility(View.GONE);
        });
    }

}

