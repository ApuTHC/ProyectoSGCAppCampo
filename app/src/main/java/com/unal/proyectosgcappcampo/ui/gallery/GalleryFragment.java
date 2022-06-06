package com.unal.proyectosgcappcampo.ui.gallery;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.unal.proyectosgcappcampo.databinding.FragmentGalleryBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;

    private FirebaseAuth firebaseAuth;
    String userName;
    boolean login = false;

    ArrayList<Feature> listaFeatures = new ArrayList<>();
    TableLayout tlTabla;

    TableRow.LayoutParams layoutFila;
    TableRow.LayoutParams layoutIdMov;
    TableRow.LayoutParams layoutEncuestador;
    TableRow.LayoutParams layoutRefGeo;

    TextView tvIdMov, tvEncuestador, tvRefGeo;

    Button btnSync;
    Button btnSearch;
    Button btnRefresh;
    Button btnLoad;

    EditText inputIPM;

    TableRow fila;

    InputStreamReader archivo;

    String listaFeaturesText = "";

    boolean auxTextExist = false;


    private Context mcont = getActivity();

    private DatabaseReference databaseReference;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GalleryViewModel galleryViewModel =
                new ViewModelProvider(this).get(GalleryViewModel.class);

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mcont = root.getContext();

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        btnSync = binding.btnSync;
        btnSearch = binding.btnBuscarIpm;
        btnRefresh = binding.btnRefrescarIpm;
        btnLoad = binding.btnLoad;

        inputIPM = binding.inputIpm;

        tlTabla = binding.tlTabla;

        String[] files = mcont.fileList();

        if (ArchivoExiste(files, "listaFeatures.txt")){
            try {
                archivo = new InputStreamReader(mcont.openFileInput("listaFeatures.txt"));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                listaFeaturesText = "";

                while (linea != null){
                    listaFeaturesText = listaFeatures + linea + "\n";
                    linea = br.readLine();
                }
                br.close();
                archivo.close();

                auxTextExist = true;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        databaseReference = FirebaseDatabase.getInstance().getReference("features");

        //Sincronizar Base de Datos

        btnSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isOnlineNet() && login){
                    listaFeatures.clear();
                    Toast.makeText(mcont, "Descargando Base de datos, por favor espere\n", Toast.LENGTH_LONG).show();
                    getBD();
                }else{
                    if (login){
                        Toast.makeText(mcont, "Asegurese de estar conectado a internet\n", Toast.LENGTH_LONG).show();
                    }else if (isOnlineNet()){
                        Toast.makeText(mcont, "Por favor inicie sesión\n", Toast.LENGTH_LONG).show();
                    }else {
                        Toast.makeText(mcont, "Asegurese de estar conectado a internet\n y por favor inicie sesión", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        //Cargar Base de Datos Guardada

        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listaFeatures.clear();
                try {
                    Toast.makeText(mcont, "Cargando Base de datos, por favor espere\n", Toast.LENGTH_LONG).show();
                    getBDText();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        //Refrescar Tabla

        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputIPM.setText("");
                setTable(listaFeatures);
            }
        });

        // Escuchando Cambios en el Input de Busqueda

        inputIPM.addTextChangedListener(new TextWatcher() {
            // Antes de que el texto cambie (no debemos modificar nada aquí)
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            //Cuando esté cambiando (no debemos modificar el texto aquí)
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            // Aquí el texto ya ha cambiado completamente, tenemos el texto actualizado en pocas palabras
            @Override
            public void afterTextChanged(Editable s) {
                String elNuevoTexto = s.toString();
                // Toast.makeText(mcont, "Cambió a " + elNuevoTexto, Toast.LENGTH_SHORT).show();
                ArrayList<Feature> resultado  = new ArrayList<>();
                for (int i = 0; i < listaFeatures.size() ; i++) {
                    if (listaFeatures.get(i).getID_PARTE().contains(elNuevoTexto)) {
                        resultado.add(listaFeatures.get(i));
                    }
                }
                setTable(resultado);
            }
        });



        //Graficando header de la tabla

        setHeadTable();

        /*if (listaFeatures.size() != 0){
            setTable(listaFeatures);
        }*/

        return root;
    }

    private boolean ArchivoExiste(String[] file, String name) {
        for (String s : file)
            if (name.equals(s))
                return true;
        return false;
    }

    private void getBDText() throws JSONException {
        if (auxTextExist){
            JSONArray featuresComplete = new JSONArray(listaFeaturesText.substring(2));
            for (int i = 1; i < featuresComplete.length(); i++) {
                try {
                    JSONObject feature = featuresComplete.getJSONObject(i);
                    JSONObject layergeojson = feature.getJSONObject("layergeojson");
                    JSONObject properties = layergeojson.getJSONObject("properties");
                    String idparte = properties.getString("ID_PARTE");
                    String encuestad = properties.getString("ENCUESTAD");
                    String refgeo = properties.getString("REF_GEOGRF");
                    listaFeatures.add(new Feature(idparte, encuestad, refgeo));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            setTable(listaFeatures);
        } else{
            Toast.makeText(mcont, "No hay bases de datos locales\n", Toast.LENGTH_LONG).show();
        }
    }


    private void setHeadTable() {
        layoutFila = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        layoutRefGeo = new TableRow.LayoutParams(850, TableRow.LayoutParams.WRAP_CONTENT);
        layoutIdMov = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);
        layoutEncuestador = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.MATCH_PARENT);

        fila = new TableRow(mcont);
        fila.setLayoutParams(layoutFila);

        tvIdMov = new TextView(mcont);
        tvIdMov.setText("ID PARTE");
        tvIdMov.setGravity(Gravity.CENTER);
        tvIdMov.setBackgroundColor(android.graphics.Color.parseColor("#f9ae00"));
        tvIdMov.setTextColor(Color.BLACK);
        tvIdMov.setPadding(10, 10, 10, 10);
        tvIdMov.setLayoutParams(layoutIdMov);
        fila.addView(tvIdMov);

        tvRefGeo = new TextView(mcont);
        tvRefGeo.setText("REF GEOGRAFICA");
        tvRefGeo.setGravity(Gravity.CENTER);
        tvRefGeo.setBackgroundColor(android.graphics.Color.parseColor("#f9ae00"));
        tvRefGeo.setTextColor(Color.BLACK);
        tvRefGeo.setPadding(10, 10, 10, 10);
        tvRefGeo.setLayoutParams(layoutIdMov);
        fila.addView(tvRefGeo);

        tvEncuestador = new TextView(mcont);
        tvEncuestador.setText("ENCUESTADOR");
        tvEncuestador.setGravity(Gravity.CENTER);
        tvEncuestador.setBackgroundColor(android.graphics.Color.parseColor("#f9ae00"));
        tvEncuestador.setTextColor(Color.BLACK);
        tvEncuestador.setPadding(10, 10, 10, 10);
        tvEncuestador.setLayoutParams(layoutIdMov);
        fila.addView(tvEncuestador);

        tlTabla.addView(fila);
    }

    private void getBD() {

       /*databaseReference.child("procesos").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error Getting Data", task.getException());
                }
                else {
                    Log.d("firebase", "Success Getting Data "+String.valueOf(task.getResult().getValue()));

                    Object object = task.getResult().getValue(Object.class);
                    String json = new Gson().toJson(object);
                    Log.d("firebase", "Funchonoooo "+ json);
                    try {
                        JSONObject procesos = new JSONObject(json);
                        JSONObject contador = procesos.getJSONObject("count");
                        int cont = Integer. parseInt(contador.getString("count"));
                        for (int i = 0; i < cont; i++) {
                            JSONObject feature = procesos.getJSONObject("feature_"+i);
                            boolean activo = Boolean.parseBoolean(feature.getString("activo"));
                            if (activo) {
                                JSONObject layergeojson = feature.getJSONObject("layergeojson");
                                JSONObject properties = layergeojson.getJSONObject("properties");
                                String idparte = properties.getString("ID_PARTE");
                                String encuestad = properties.getString("ENCUESTAD");
                                String refgeo = properties.getString("REF_GEOGRF");
                                listaFeatures.add(new Feature(idparte, encuestad, refgeo));
                            }

                        }

                        setTable(listaFeatures);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.d("firebase", "Ño Funchono :c "+ json);
                    }


                }
            }
        });*/

        databaseReference.child("procesos/count/count").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error Getting Data", task.getException());
                }
                else {
                    Log.d("firebase", "Success Getting Data "+String.valueOf(task.getResult().getValue()));
                    int cont = Integer. parseInt(String.valueOf(task.getResult().getValue()));
                    JSONArray featuresArray = new JSONArray();
                    featuresArray.put(cont);
                    for (int i = 0; i < cont; i++) {

                        int finalI = i;
                        databaseReference.child("procesos/feature_"+i).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                            public void onComplete(@NonNull Task<DataSnapshot> task) {
                                if (!task.isSuccessful()) {
                                    Log.e("firebase", "Error Getting Data", task.getException());
                                }
                                else {
                                    Object object = task.getResult().getValue(Object.class);
                                    String json = new Gson().toJson(object);
                                    Log.d("firebase", "Funchonoooo "+ json);
                                    try {
                                        JSONObject feature = new JSONObject(json);
                                        boolean activo = Boolean.parseBoolean(feature.getString("activo"));
                                        if (activo) {
                                            featuresArray.put(feature);
                                            JSONObject layergeojson = feature.getJSONObject("layergeojson");
                                            JSONObject properties = layergeojson.getJSONObject("properties");
                                            String idparte = properties.getString("ID_PARTE");
                                            String encuestad = properties.getString("ENCUESTAD");
                                            String refgeo = properties.getString("REF_GEOGRF");
                                            listaFeatures.add(new Feature(idparte, encuestad, refgeo));
                                        }
                                        if (finalI == cont-1) {
                                            setTable(listaFeatures);
                                            OutputStreamWriter file = new OutputStreamWriter(mcont.openFileOutput("listaFeatures.txt", Activity.MODE_PRIVATE));
                                            file.write(String.valueOf(featuresArray));
                                            file.flush();
                                            file.close();
                                            Toast.makeText(mcont, "BD Guardada", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException | FileNotFoundException e) {
                                        e.printStackTrace();
                                        Log.d("firebase", "Ño Funchono :c "+ json);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                    }



                }
            }
        });

    }

    private void setTable(ArrayList<Feature> lista) {

        tlTabla.removeViews(1, Math.max(0, tlTabla.getChildCount() - 1));
        // TABLA
        String color;
        for(int i = 0; i < lista.size() ; i++) {

            if (i % 2 == 0) {
                color = "#dddddd";
            } else {
                color = "#bbbbbb";
            }

            fila = new TableRow(mcont);
            fila.setLayoutParams(layoutFila);

            tvIdMov = new TextView(mcont);
            tvIdMov.setGravity(Gravity.CENTER);
            tvIdMov.setText(lista.get(i).getID_PARTE());
            tvIdMov.setBackgroundColor(android.graphics.Color.parseColor(color));
            tvIdMov.setPadding(10, 10, 10, 10);
            tvIdMov.setLayoutParams(layoutIdMov);
            fila.addView(tvIdMov);

            tvRefGeo = new TextView(mcont);
            tvRefGeo.setGravity(Gravity.CENTER);
            tvRefGeo.setText(lista.get(i).getREF_GEOGRF());
            tvRefGeo.setBackgroundColor(android.graphics.Color.parseColor(color));
            tvRefGeo.setPadding(10, 10, 10, 10);
            tvRefGeo.setLayoutParams(layoutRefGeo);
            fila.addView(tvRefGeo);


            tvEncuestador = new TextView(mcont);
            tvEncuestador.setGravity(Gravity.CENTER);
            tvEncuestador.setText(lista.get(i).getENCUESTAD());
            tvEncuestador.setBackgroundColor(android.graphics.Color.parseColor(color));
            tvEncuestador.setPadding(10, 10, 10, 10);
            tvEncuestador.setLayoutParams(layoutEncuestador);
            fila.addView(tvEncuestador);

            tlTabla.addView(fila);

        }
    }

    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.es");

            int val = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}