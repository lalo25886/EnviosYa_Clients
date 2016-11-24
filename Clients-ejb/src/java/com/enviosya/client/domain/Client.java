/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.enviosya.client.domain;

/**
 *
 * @author Gonzalo
 */
public class Client {
    private Long id;
    private Long ci;
    private String nombre;
    private String apellido;
    private String email;
    private String numeroTarjeta;
    private String claveTarjeta;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public long getCi() {
        return ci;
    }

    public void setCi(long ci) {
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

    @Override
    public String toString() {
        return "Client{" + "ID=" + id + ", "
                + "Nombre=" + nombre + ", "
                + "Apellido=" + apellido + '}';
    }

    
}

