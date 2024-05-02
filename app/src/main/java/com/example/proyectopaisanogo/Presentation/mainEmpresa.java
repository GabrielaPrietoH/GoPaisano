package com.example.proyectopaisanogo.Presentation;

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

import com.example.proyectopaisanogo.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class mainEmpresa extends Fragment implements NavigationView.OnNavigationItemSelectedListener{

    private MainEmpresaViewModel mViewModel;

    //FIREBASE AUTHENTICATOR. logout
    private FirebaseAuth mAuth;

    public static mainEmpresa newInstance() {
        return new mainEmpresa();
    }

    private DrawerLayout drawerLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        //FIREBASE Logout
        mAuth = FirebaseAuth.getInstance();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main_empresa, container, false);
        Toolbar toolbar = v.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        drawerLayout = v.findViewById(R.id.drawerLayout);
        NavigationView navigationView = v.findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);
        setupToolbar(v);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MainEmpresaViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.setting) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, settingEmpresa.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("Setting") // El nombre puede ser nulo
                    .commit();

        } else if (id == R.id.calendar) {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, calendarioEmpresa.class, null)
                    .setReorderingAllowed(true)
                    .addToBackStack("Calendario") // El nombre puede ser nulo
                    .commit();

        } else if (id == R.id.logout) {
            mAuth.signOut();
            // Crear un nuevo fragmento y transacción
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, loginEmpresa.class, null)
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
}