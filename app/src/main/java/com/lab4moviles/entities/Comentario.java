package com.lab4moviles.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

public class Comentario implements Serializable {

    private Map<String, String> usuario;
    private Date fecha;
    private String texto;

    public Comentario(){

    }

    public Comentario(Date fecha, String texto) {
        this.fecha = fecha;
        this.texto = texto;
    }

    public Map<String, String> getUsuario() {
        return usuario;
    }

    public void setUsuario(Map<String, String> usuario) {
        this.usuario = usuario;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getTexto() {
        return texto;
    }

    public void setTexto(String texto) {
        this.texto = texto;
    }
}
