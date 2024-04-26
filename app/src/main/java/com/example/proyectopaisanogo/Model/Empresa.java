package com.example.proyectopaisanogo.Model;

public class Empresa {
    static String cif;
    static String cp;
    static String direccion;
    static String email;
    static String nombreEmpresa;
    static String telefono;
    static String userID;

    public Empresa(String cif, String cp, String direccion, String email, String nombreEmpresa, String telefono, String userID) {
        this.cif = cif;
        this.cp = cp;
        this.direccion = direccion;
        this.email = email;
        this.nombreEmpresa = nombreEmpresa;
        this.telefono = telefono;
        this.userID = userID;
    }

    public static String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        Empresa.cif = cif;
    }

    public static String getCp() {
        return cp;
    }

    public void setCp(String cp) {
        Empresa.cp = cp;
    }

    public static String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        Empresa.direccion = direccion;
    }

    public static String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        Empresa.email = email;
    }

    public static String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        Empresa.nombreEmpresa = nombreEmpresa;
    }

    public static String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        Empresa.telefono = telefono;
    }

    public static String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        Empresa.userID = userID;
    }
}
