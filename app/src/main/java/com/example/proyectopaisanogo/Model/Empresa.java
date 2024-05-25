package com.example.proyectopaisanogo.Model;

import com.google.firebase.firestore.PropertyName;

/**
 * Clase que representa una empresa en el sistema.
 *
 * Esta clase almacena la información básica de una empresa como el CIF, nombre,
 * dirección, email, teléfono, y la URL de su imagen.
 */
public class Empresa {

    private String cif;
    private String cp;
    private String direccion;
    private String email;
    private String nombreEmpresa;
    private String telefono;
    private String userID;
    private String imagenURL;

    /**
     * Constructor vacío
     */
    public Empresa() {

    }



    /**
     * Constructor sobrecargado
     * @param cif CIF de la empresa
     * @param cp Código postal de la empresa
     * @param direccion Dirección de la empresa
     * @param email Email de la empresa
     * @param nombreEmpresa Nombre de la empresa
     * @param telefono Teléfono de la empresa
     * @param userID ID de usuario de la empresa
     * @param imagenURL URL de la imagen de la empresa
     */
    public Empresa(String cif, String cp, String direccion, String email, String nombreEmpresa, String telefono, String userID, String imagenURL) {
        this.cif = cif;
        this.cp = cp;
        this.direccion = direccion;
        this.email = email;
        this.nombreEmpresa = nombreEmpresa;
        this.telefono = telefono;
        this.userID = userID;
        this.imagenURL = imagenURL;
    }

    /**
     * Getters y setters
     */
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

    @PropertyName("imagenURL")
    public String getImagenURL() {
        return imagenURL;
    }

    @PropertyName("imagenURL")
    public void setImagenURL(String imagenURL) {
        this.imagenURL = imagenURL;
    }
}