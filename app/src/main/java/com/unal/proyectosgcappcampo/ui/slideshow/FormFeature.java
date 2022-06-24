package com.unal.proyectosgcappcampo.ui.slideshow;

public class FormFeature {

    public boolean activo;
    public String Estacion;
    public String TipoEstacion;
    public String Este;
    public String Norte;
    public String Altitud;
    public String Fotos;
    public String Observaciones;
    public String Fecha;
    public String Propietario;

    public FormFeature(boolean activo,String estacion, String tipoEstacion, String este, String norte, String altitud, String fotos, String observaciones, String fecha, String propietario) {
        this.activo = activo;
        this.Estacion = estacion;
        this.TipoEstacion = tipoEstacion;
        this.Este = este;
        this.Norte = norte;
        this.Altitud = altitud;
        this.Fotos = fotos;
        this.Observaciones = observaciones;
        this.Fecha = fecha;
        this.Propietario = propietario;
    }

}
