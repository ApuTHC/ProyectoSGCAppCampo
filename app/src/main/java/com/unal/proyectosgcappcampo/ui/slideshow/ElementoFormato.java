package com.unal.proyectosgcappcampo.ui.slideshow;

public class ElementoFormato {
    private String nombreelemento;
    private String claseelemento;
    private String tagelemento;
    private int idStringArray;

    public ElementoFormato(String nombreelemento, String claseelemento, String tagelemento, int idStringArray) {
        this.nombreelemento = nombreelemento;
        this.claseelemento = claseelemento;
        this.tagelemento = tagelemento;
        this.idStringArray = idStringArray;
    }

    public String getNombreelemento() {
        return nombreelemento;
    }

    public void setNombreelemento(String nombreelemento) {
        this.nombreelemento = nombreelemento;
    }

    public String getClaseelemento() {
        return claseelemento;
    }

    public void setClaseelemento(String claseelemento) {
        this.claseelemento = claseelemento;
    }

    public String getTagelemento() {
        return tagelemento;
    }

    public void setTagelemento(String tagelemento) {
        this.tagelemento = tagelemento;
    }

    public int getIdStringArray() {
        return idStringArray;
    }

    public void setIdStringArray(int idStringArray) {
        this.idStringArray = idStringArray;
    }
}
