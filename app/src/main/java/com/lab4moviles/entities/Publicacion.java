package com.lab4moviles.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Publicacion implements Serializable {

    private String id; //También será el ID de la Foto
    private Date fecha;
    private List<Comentario> comentarios;
    private String descripcion;
    private Map<String, String> usuario;

    public Publicacion() {
        comentarios = new ArrayList<>();
    }

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

    public List<Comentario> getComentarios() {
        return comentarios;
    }

    public void setComentarios(List<Comentario> comentarios) {
        this.comentarios = comentarios;
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

    public void addComentario(Comentario comentario){
        comentarios.add(comentario);
    }
}
