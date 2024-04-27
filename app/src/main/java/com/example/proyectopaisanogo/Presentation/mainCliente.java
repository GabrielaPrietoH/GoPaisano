package com.example.proyectopaisanogo.Presentation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.proyectopaisanogo.Adapter.HelperAdapter;
import com.example.proyectopaisanogo.Model.Empresa;
import com.example.proyectopaisanogo.R;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class mainCliente extends Fragment {
    RecyclerView recyclerView;
    HelperAdapter helperAdapter;
    FirebaseFirestore firestore;
    private Context context;

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
    public void onCreateOptionsMenu(@NonNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.logout) {
            mAuth.signOut();
            // Crear un nuevo fragmento y transacción
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, loginCliente.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("nombre") // El nombre puede ser nulo
                    .commit();

        } else if (id == R.id.setting) {
            // Crear un nuevo fragmento y transacción
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, settingCliente.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("nombre") // El nombre puede ser nulo
                    .commit();

        } else if (id == R.id.calendar) {
            // Crear un nuevo fragmento y transacción
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, calendarioCliente.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("nombre") // El nombre puede ser nulo
                    .commit();

        }
        mAuth = FirebaseAuth.getInstance();
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("NotifyDataSetChanged")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_cliente, container, false);

        firestore = FirebaseFirestore.getInstance();
        recyclerView = v.findViewById(R.id.RvEmpresas);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.context));
        Query query = firestore.collection("registroEmpresa");
        FirestoreRecyclerOptions<Empresa> firestoresRecyclerOptions =
                new FirestoreRecyclerOptions.Builder<Empresa>().setQuery(query, Empresa.class).build();
        helperAdapter = new HelperAdapter(firestoresRecyclerOptions);
        helperAdapter.notifyDataSetChanged();
        recyclerView.setAdapter(helperAdapter);
        return  v ;
    }
    @Override
    public void onStart() {
        super.onStart();
        helperAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        helperAdapter.stopListening();
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MainClienteViewModel mViewModel = new ViewModelProvider(this).get(MainClienteViewModel.class);
    }

}