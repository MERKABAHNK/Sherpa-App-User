package com.fr3estudio.sherpa.sherpav3p.model;

/**
 * Created by Alfr3 on 9/22/2015.
 */
public class Diligencia {

    String id_diligencia;
    String token_actualizaciones;
    String fecha;
    String calificacion_cliente;
    String id_estado_diligencia;
    String id_sherpa;
    String id_cliente;
    String costo;
    String bono;
    String comision;
    String latitud;
    String longitud;

    public String getId_diligencia() {
        return id_diligencia;
    }

    public void setId_diligencia(String id_diligencia) {
        this.id_diligencia = id_diligencia;
    }

    public String getToken_actualizaciones() {
        return token_actualizaciones;
    }

    public void setToken_actualizaciones(String token_actualizaciones) {
        this.token_actualizaciones = token_actualizaciones;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getCalificacion_cliente() {
        return calificacion_cliente;
    }

    public void setCalificacion_cliente(String calificacion_cliente) {
        this.calificacion_cliente = calificacion_cliente;
    }

    public String getId_estado_diligencia() {
        return id_estado_diligencia;
    }

    public void setId_estado_diligencia(String id_estado_diligencia) {
        this.id_estado_diligencia = id_estado_diligencia;
    }

    public String getId_sherpa() {
        return id_sherpa;
    }

    public void setId_sherpa(String id_sherpa) {
        this.id_sherpa = id_sherpa;
    }

    public String getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(String id_cliente) {
        this.id_cliente = id_cliente;
    }

    public String getCosto() {
        return costo;
    }

    public void setCosto(String costo) {
        this.costo = costo;
    }

    public String getBono() {
        return bono;
    }

    public void setBono(String bono) {
        this.bono = bono;
    }

    public String getComision() {
        return comision;
    }

    public void setComision(String comision) {
        this.comision = comision;
    }

    public String getLatitud() {
        return latitud;
    }

    public void setLatitud(String latitud) {
        this.latitud = latitud;
    }

    public String getLongitud() {
        return longitud;
    }

    public void setLongitud(String longitud) {
        this.longitud = longitud;
    }
}
