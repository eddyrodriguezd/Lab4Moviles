package com.lab4moviles.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Publicacion implements Serializable {

    private String id; //También será el ID de la Foto
    private Date fecha;
    private List<Comentario> listaComentarios;
    private String descripcion;
    private Map<String, String> usuario;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public List<Comentario> getListaComentarios() {
        return listaComentarios;
    }

    public void setListaComentarios(List<Comentario> listaComentarios) {
        this.listaComentarios = listaComentarios;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Map<String, String> getUsuario() {
        return usuario;
    }

    public void setUsuario(Map<String, String> usuario) {
        this.usuario = usuario;
    }
}
