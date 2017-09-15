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
public class Condicion {
    public final static String[] TIPO_CONDICIONES = new String[]{"=", "<", ">", "like"};
    
    private String nombreAtributo;
    private Object valor;
    private String tipo_condicion;

    public Condicion(String nombreAtributo, String tipo_condicion, Object valor) {
        this.nombreAtributo = nombreAtributo;
        this.tipo_condicion = tipo_condicion;
        this.valor = valor;
    }

    public String getNombreAtributo() {
        return nombreAtributo;
    }

    public void setNombreAtributo(String nombreAtributo) {
        this.nombreAtributo = nombreAtributo;
    }

    public Object getValor() {
        return valor;
    }

    public void setValor(Object valor) {
        this.valor = valor;
    }

    public String getTipo_condicion() {
        return tipo_condicion;
    }

    public void setTipo_condicion(String tipo_condicion) {
        this.tipo_condicion = tipo_condicion;
    }

    @Override
    public String toString() {
        if(valor instanceof String){
            return nombreAtributo + " " + this.tipo_condicion + " '" + valor.toString() + "'";
        }
        return nombreAtributo + " " + this.tipo_condicion + " " + valor.toString();
    }        
}
