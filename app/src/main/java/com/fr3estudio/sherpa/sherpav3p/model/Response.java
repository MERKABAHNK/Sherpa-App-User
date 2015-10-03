package com.fr3estudio.sherpa.sherpav3p.model;

import java.util.ArrayList;

/**
 * Created by Alfr3 on 9/14/2015.
 */
public class Response {

    /**
     * Query Status:
     *      0 - Ok
     *      1 - not Ok
     *      2 - wrong usr/pass
     *      3 - user already exists
     *      4 - mysql conn error
     */
    private int status;
    private String message;
    private ArrayList<Result> results;

    private ArrayList<Cliente> clientes;
    private ArrayList<Diligencia> diligencias;
    private ArrayList<Punto> puntos;
    private ArrayList<Tarifa> tarifas;
    private ArrayList<Sherpa> sherpas;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ArrayList<Result> getResults() {
        return results;
    }

    public void setResults(ArrayList<Result> results) {
        this.results = results;
    }

    public ArrayList<Cliente> getClientes() {
        return clientes;
    }

    public void setClientes(ArrayList<Cliente> clientes) {
        this.clientes = clientes;
    }

    public ArrayList<Diligencia> getDiligencias() {
        return diligencias;
    }

    public void setDiligencias(ArrayList<Diligencia> diligencias) {
        this.diligencias = diligencias;
    }

    public ArrayList<Punto> getPuntos() {
        return puntos;
    }

    public void setPuntos(ArrayList<Punto> puntos) {
        this.puntos = puntos;
    }

    public ArrayList<Tarifa> getTarfias() {
        return tarifas;
    }

    public void setTarfias(ArrayList<Tarifa> tarfias) {
        this.tarifas = tarfias;
    }

    public ArrayList<Tarifa> getTarifas() {
        return tarifas;
    }

    public void setTarifas(ArrayList<Tarifa> tarifas) {
        this.tarifas = tarifas;
    }

    public ArrayList<Sherpa> getSherpas() {
        return sherpas;
    }

    public void setSherpas(ArrayList<Sherpa> sherpas) {
        this.sherpas = sherpas;
    }
}
