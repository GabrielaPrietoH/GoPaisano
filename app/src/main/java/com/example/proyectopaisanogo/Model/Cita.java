package com.example.proyectopaisanogo.Model;

import com.google.firebase.Timestamp;

public class Cita {
    private String nombreUsuario, telefonoCliente;

    private Timestamp fecha;

    public Cita(String nombreUsuario, Timestamp fecha, String telefonoCliente) {
        this.nombreUsuario = nombreUsuario;
        this.fecha = fecha;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public Timestamp getFecha() {
        return fecha;
    }
    public String gettelefonoCliente() {
        return nombreUsuario;
    }
}

