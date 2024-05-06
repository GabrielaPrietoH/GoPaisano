package com.example.proyectopaisanogo.Presentation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class mainCliente extends Fragment implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private FirestoreRecyclerAdapter<Empresa, HelperViewHolder> firestoreAdapter;
    private StorageReference storageRef;

    private FirebaseAuth mAuth;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        storageRef = FirebaseStorage.getInstance().getReference();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_cliente, container, false);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        RecyclerView recyclerView = v.findViewById(R.id.RvEmpresas);
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

        firestoreAdapter = new FirestoreRecyclerAdapter<Empresa, HelperViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull HelperViewHolder viewHolder, int i, @NonNull Empresa empresa) {
                viewHolder.nombreEmpresa.setText(empresa.getNombreEmpresa());
                viewHolder.cif.setText(empresa.getCif());
                viewHolder.cp.setText(empresa.getCp());
                viewHolder.direccion.setText(empresa.getDireccion());
                viewHolder.email.setText(empresa.getEmail());
                viewHolder.telefono.setText(empresa.getTelefono());
                viewHolder.userID.setText(empresa.getUserID());

                // Cargar la imagen utilizando Glide
                loadImage(requireContext(), empresa.getUserID(), viewHolder.imageView);

                // Configurar OnClickListener para el botón de llamada
                viewHolder.botonLlamar.setOnClickListener(v -> {
                    Log.e("NAMG", "bottonLlamar");
                    String telefono = empresa.getTelefono();
                    realizarLlamada(telefono);
                });

                // Configurar OnClickListener para el botón de correo electrónico
                viewHolder.botonCorreo.setOnClickListener(v -> {
                    Log.e("NAMG", "bottonCorreo");
                    String email = empresa.getEmail();
                    enviarCorreo(email);
                });

                // Configurar OnClickListener para el botón de abrir dirección en Google Maps
                viewHolder.botonDireccion.setOnClickListener(v -> {
                    Log.e("NAMG", "bottonDireccion");
                    String direccion = empresa.getDireccion();
                    abrirDireccionEnMapas(direccion);
                });
            }

            @NonNull
            @Override
            public HelperViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.display_item, parent, false);
                return new HelperViewHolder(view);
            }

            private void realizarLlamada(String telefono) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + telefono));
                requireContext().startActivity(intent);
            }

            private void enviarCorreo(String correo) {
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + correo));
                requireContext().startActivity(intent);
            }

            private void abrirDireccionEnMapas(String direccion) {
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + Uri.encode(direccion));
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                requireContext().startActivity(mapIntent);
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
                    .into((ImageView) imageView.findViewById(R.id.imagen_empresa));
        }).addOnFailureListener(exception -> {
            // Handle any errors
            // For now, you can display a placeholder image or a message
            // For example:
            // imageView.setImageResource(R.drawable.placeholder_image);
            // Or
            //imageView.setVisibility(View.GONE);
        });
    }
}