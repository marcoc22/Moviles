package com.una.app.placefinder506;

/**
 * Created by Marco on 19/4/2017.
 */
public class Lugar {

    public static final int  MACROBIOTICA = 0;
    public static final int FARMACIA = 1;
    public static final int CLINICA = 2;

    String nombre;
    double latitud;
    double longitud;
    int tipo;

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String crearClave()
    {
        return nombre+"_"+String.valueOf(latitud).replace(".",",")+"_"+String.valueOf(longitud).replace(".",",");
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPaginaWeb() {
        return paginaWeb;
    }

    public void setPaginaWeb(String paginaWeb) {
        this.paginaWeb = paginaWeb;
    }

    String telefono;
    String correo;
    String paginaWeb;

    public Lugar(){
        nombre = "";
        latitud = 0;
        longitud = 0;
        telefono = "";
        correo = "";
        paginaWeb = "http://";
    }
    public Lugar(String nombre,double latitud,double longitud, String telefono, String correo, String pagina,int t){
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.tipo = t;
        this.telefono = telefono;
        this.correo = correo;
        this.paginaWeb = pagina;
    }

    public int getTipo()
    {
        return this.tipo;
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
