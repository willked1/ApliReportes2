/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author kecc
 */
public class Atributo {    
    private String nombre;
    private String tipoDato; //NUMBER, VARCHAR, DATE el que sea que salga de la BD
    private int longitud;

    public Atributo(String nombre, String tipoDato, int longitud) {
        this.nombre = nombre;
        this.tipoDato = tipoDato;
        this.longitud = longitud;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoDato() {
        return tipoDato;
    }

    public void setTipoDato(String tipoDato) {
        this.tipoDato = tipoDato;
    }

    public int getLongitud() {
        return longitud;
    }

    public void setLongitud(int longitud) {
        this.longitud = longitud;
    }    
}
