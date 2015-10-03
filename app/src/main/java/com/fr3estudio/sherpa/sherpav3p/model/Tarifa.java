package com.fr3estudio.sherpa.sherpav3p.model;

/**
 * Created by Alfr3 on 9/24/2015.
 */
public class Tarifa {

    String id_tarifa;
    String minimo;
    String maximo;
    int valor;
    int comision;

    public String getId_tarifa() {
        return id_tarifa;
    }

    public void setId_tarifa(String id_tarifa) {
        this.id_tarifa = id_tarifa;
    }

    public String getMinimo() {
        return minimo;
    }

    public void setMinimo(String minimo) {
        this.minimo = minimo;
    }

    public String getMaximo() {
        return maximo;
    }

    public void setMaximo(String maximo) {
        this.maximo = maximo;
    }

    public int getValor() {
        return valor;
    }

    public void setValor(int valor) {
        this.valor = valor;
    }

    public int getComision() {
        return comision;
    }

    public void setComision(int comision) {
        this.comision = comision;
    }
}
