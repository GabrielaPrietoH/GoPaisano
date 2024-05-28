package com.example.proyectopaisanogo.Model;

import com.google.firebase.firestore.PropertyName;

/**
 * Clase que representa a un cliente.
 * Esta clase almacena la información básica de un cliente, incluyendo su código postal, dirección,
 * email, nombre, teléfono y userID.
 */
public class Cliente {

    private String cp;
    private String direccion;
    private String email;
    private String nombreCliente;
    private String telefono;
    private String userID;

    /**
     * Constructor por defecto.
     */
    public Cliente(){

    }

    /**
     * Constructor sobrecargado que inicializa los campos de un cliente.
     *
     * @param cp            El código postal del cliente.
     * @param direccion     La dirección del cliente.
     * @param email         El email del cliente.
     * @param nombreCliente El nombre del cliente.
     * @param telefono      El teléfono del cliente.
     * @param userID        El ID de usuario del cliente.
     */
    public Cliente(String cp, String direccion, String email, String nombreCliente, String telefono, String userID) {
        this.cp = cp;
        this.direccion = direccion;
        this.email = email;
        this.nombreCliente = nombreCliente;
        this.telefono = telefono;
        this.userID = userID;
    }

    //Getters & Setters
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

