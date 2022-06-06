package com.unal.proyectosgcappcampo.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("Proyecto de Actualización del Mapa Nacional de Amenaza por Movimientos en Masa a Escala 1:25.000 - Bloque 05");
    }

    public LiveData<String> getText() {
        return mText;
    }
}