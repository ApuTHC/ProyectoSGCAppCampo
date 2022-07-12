package com.unal.proyectosgcappcampo;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.unal.proyectosgcappcampo.databinding.FragmentPlanillasCoberturasBinding;
import com.unal.proyectosgcappcampo.ui.slideshow.ElementoFormato;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class PlanillasCoberturas extends Fragment {


    private FragmentPlanillasCoberturasBinding binding;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageRef;
    private DatabaseReference databaseReference;
    private String userName;
    boolean login = false;

    private Context mcont = getActivity();

    private List<ElementoFormato> listaElementosPlanillas = new ArrayList<ElementoFormato>();
    private List<ElementoFormato> listaElementosPuntos = new ArrayList<ElementoFormato>();

    private LinearLayout liPlanillas;
    private Button addPlanillas;

    private JSONObject attrForm;
    private JSONArray formComplete;
    private String[] files;

    private InputStreamReader archivo;
    private Boolean auxTextExist = false;
    private String listaFormText = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentPlanillasCoberturasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        mcont = root.getContext();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        files = mcont.fileList();

        liPlanillas = binding.liPlanillas;
        addPlanillas = binding.addPlanillas;

        listaElementosPlanillas = new ArrayList<ElementoFormato>();
        listaElementosPuntos = new ArrayList<ElementoFormato>();
        GenerarListas();

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageRef =  FirebaseStorage.getInstance().getReference();


    }

    private void checkUser() {
        //if user is already signed
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            userName = firebaseUser.getDisplayName();
            Log.d("Firebase", "onSuccess: Name: "+userName);
            login = true;
        }
        else {
            Toast.makeText(mcont, "Por favor Inicie Sesión\n", Toast.LENGTH_LONG).show();
        }
    }

    public void GenerarListas() {

        listaElementosPlanillas.add(new ElementoFormato("NOMBRE DEL INTÉRPRETE", "edittext", "propietario", 0));
        listaElementosPlanillas.add(new ElementoFormato("FECHA", "edittext", "fecha", 0));
        listaElementosPlanillas.add(new ElementoFormato("PERIODO DE INTERPRETACIÓN (MES/ AÑO)", "edittext", "periodo", 0));
        listaElementosPlanillas.add(new ElementoFormato("SENSOR UTILIZADO", "edittext", "sensor", 0));
        listaElementosPlanillas.add(new ElementoFormato("MUNICIPIO", "spinner", "municipios", R.array.Municipios));
        listaElementosPlanillas.add(new ElementoFormato("ID PLANILLA", "textview", "idplanilla", 0));


        listaElementosPuntos.add(new ElementoFormato("Punto", "edittext", "punto", 0));
        listaElementosPuntos.add(new ElementoFormato("Este", "edittext", "este", 0));
        listaElementosPuntos.add(new ElementoFormato("Norte", "edittext", "norte", 0));
        listaElementosPuntos.add(new ElementoFormato("Fotos", "imageview", "images", 0));
        listaElementosPuntos.add(new ElementoFormato("Cobertura", "edittext", "cobertura", 0));
        listaElementosPuntos.add(new ElementoFormato("Observaciones", "edittext", "observaciones", 0));
        listaElementosPuntos.add(new ElementoFormato("Uso Suelo", "spinner", "", 0));
        listaElementosPuntos.add(new ElementoFormato("Densidad de siembra", "spinner", "densidadsiembra", 0));
        listaElementosPuntos.add(new ElementoFormato("Intensidad de Uso ", "spinner", "intensidaduso", 0));
        listaElementosPuntos.add(new ElementoFormato("Tecnicas de Producción", "spinner", "tecnicasprod", 0));
        listaElementosPuntos.add(new ElementoFormato("Arreglo espacial", "spinner", "arregloespacial", 0));
        listaElementosPuntos.add(new ElementoFormato("Riego", "spinner", "riego", 0));
        listaElementosPuntos.add(new ElementoFormato("Tipo de Establecimiento", "spinner", "tipoestablecimiento", 0));
        listaElementosPuntos.add(new ElementoFormato("Dirección de siembra", "spinner", "dirsiembra", 0));
        listaElementosPuntos.add(new ElementoFormato("Practica de Quema", "spinner", "practicaquema", 0));
        listaElementosPuntos.add(new ElementoFormato("Actividad Pecuaria", "spinner", "actividadpecuaria", 0));
        listaElementosPuntos.add(new ElementoFormato("Restricción de uso", "spinner", "restriccionuso", 0));
        listaElementosPuntos.add(new ElementoFormato("Afectación por uso del suelo al terreno", "spinner", "afectacion", 0));
        listaElementosPuntos.add(new ElementoFormato("Cobertura Intepretada", "textview", "coberturainter", 0));

    }


}