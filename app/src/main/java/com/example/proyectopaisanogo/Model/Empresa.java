package com.example.proyectopaisanogo.Model;

import com.google.firebase.firestore.PropertyName;

public class Empresa {
    // Campos de la clase
    private String cif;
    private String cp;
    private String direccion;
    private String email;
    private String nombreEmpresa;
    private String telefono;
    private String userID;

    // Constructor vacío requerido por Firestore
    public Empresa() {
        // Constructor vacío
    }

    // Constructor con todos los campos
    public Empresa(String cif, String cp, String direccion, String email, String nombreEmpresa, String telefono, String userID) {
        this.cif = cif;
        this.cp = cp;
        this.direccion = direccion;
        this.email = email;
        this.nombreEmpresa = nombreEmpresa;
        this.telefono = telefono;
        this.userID = userID;
    }

    // Getters y setters
    @PropertyName("cif")
    public String getCif() {
        return cif;
    }

    @PropertyName("cif")
    public void setCif(String cif) {
        this.cif = cif;
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

    @PropertyName("nombreEmpresa")
    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    @PropertyName("nombreEmpresa")
    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
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