package com.example.proyectopaisanogo.Model;

import com.google.firebase.firestore.PropertyName;

public class Cliente {

    private String cp;
    private String direccion;
    private String email;
    private String nombreCliente;
    private String telefono;
    private String userID;

    public Cliente(){

    }

    public Cliente(String cp, String direccion, String email, String nombreCliente, String telefono, String userID) {
        this.cp = cp;
        this.direccion = direccion;
        this.email = email;
        this.nombreCliente = nombreCliente;
        this.telefono = telefono;
        this.userID = userID;
    }

    @PropertyName("cp")
    public String getCp() {
        return cp;
    }

    @PropertyName("cp")
    public void setCp(String cp) {
        this.cp = cp;
    }

    @PropertyName("direccion")
    public String getDireccion() {
        return direccion;
    }

    @PropertyName("direccion")
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @PropertyName("email")
    public String getEmail() {
        return email;
    }

    @PropertyName("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName("nombreCliente")
    public String getNombreCliente() {
        return nombreCliente;
    }

    @PropertyName("nombreCliente")
    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    @PropertyName("telefono")
    public String getTelefono() {
        return telefono;
    }

    @PropertyName("telefono")
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    @PropertyName("userID")
    public String getUserID() {
        return userID;
    }

    @PropertyName("userID")
    public void setUserID(String userID) {
        this.userID = userID;
    }


}

