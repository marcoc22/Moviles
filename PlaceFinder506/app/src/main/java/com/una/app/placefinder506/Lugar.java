package com.una.app.placefinder506;

/**
 * Created by Marco on 19/4/2017.
 */
public class Lugar {
    String nombre;
    double latitud;
    double longitud;

    public Lugar(){
        nombre = "";
        latitud = 0;
        longitud = 0;
    }
    public Lugar(String nombre,double latitud,double longitud){
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }
    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }
}
