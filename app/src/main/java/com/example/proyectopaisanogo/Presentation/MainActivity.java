package com.example.proyectopaisanogo.Presentation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.proyectopaisanogo.R;

public class MainActivity extends AppCompatActivity implements OnCalendarButtonClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainCliente recycleviewfragment = new mainCliente();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction1 = fragmentManager.beginTransaction();
        fragmentTransaction1.add(R.id.fragment_container, recycleviewfragment); // Reemplaza "fragment_container" con el ID de tu contenedor de fragmentos
        fragmentTransaction1.commit();

// En tu MainActivity
        FragmentLogin login =  new FragmentLogin();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, login); // Reemplaza "fragment_container" con el ID de tu contenedor de fragmentos
        fragmentTransaction.commit();

/**
        // Configurar el botón del calendario en el fragmento MainCliente
        mainCliente mainCliente = (mainCliente) getSupportFragmentManager().findFragmentById(R.id.mainCliente);
        mainCliente.setOnCalendarButtonClickListener(this);
**/

    }

    @Override
    public void onCalendarButtonClick() {
        // Cuando se hace clic en el botón del calendario en MainCliente, enviar información a los fragmentos correspondientes
        // Por ejemplo, aquí enviarías la información de la cita a través de los fragmentos para actualizar los calendarios
    }

}
