package com.enviosya.client.domain;

import java.io.Serializable;

/**
 *
 * @author Gonzalo
 */
public class Client {
    private Long id;
    private String ci;
    private String nombre;
    private String apellido;
    private String email;
    private String numeroTarjeta;
    private String claveTarjeta;
    private boolean estado;
    private String contrasena;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCi() {
        return ci;
    }

    public void setCi(String ci) {
        this.ci = ci;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumeroTarjeta() {
        return numeroTarjeta;
    }

    public void setNumeroTarjeta(String numeroTarjeta) {
        this.numeroTarjeta = numeroTarjeta;
    }

    public String getClaveTarjeta() {
        return claveTarjeta;
    }

    public void setClaveTarjeta(String claveTarjeta) {
        this.claveTarjeta = claveTarjeta;
    }
    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String cont) {
        this.contrasena = cont;
    }
    @Override
    public String toString() {
        return "ID = " + id + ", "
                + "Nombre = " + nombre + ", "
                + "Apellido = " + apellido;
    }
}

