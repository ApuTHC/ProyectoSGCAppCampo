package com.unal.proyectosgcappcampo.ui.gallery;

public class Feature {

    public String ID_PARTE;
    public String ENCUESTAD;
    public String REF_GEOGRF;

    public Feature(String ID_PARTE, String ENCUESTAD, String REF_GEOGRF) {
        this.ID_PARTE = ID_PARTE;
        this.ENCUESTAD = ENCUESTAD;
        this.REF_GEOGRF = REF_GEOGRF;
    }

    public Feature() {
    }

    public String getID_PARTE() {
        return ID_PARTE;
    }

    public void setID_PARTE(String ID_PARTE) {
        this.ID_PARTE = ID_PARTE;
    }

    public String getENCUESTAD() {
        return ENCUESTAD;
    }

    public void setENCUESTAD(String ENCUESTAD) {
        this.ENCUESTAD = ENCUESTAD;
    }

    public String getREF_GEOGRF() {
        return REF_GEOGRF;
    }

    public void setREF_GEOGRF(String REF_GEOGRF) {
        this.REF_GEOGRF = REF_GEOGRF;
    }
}
