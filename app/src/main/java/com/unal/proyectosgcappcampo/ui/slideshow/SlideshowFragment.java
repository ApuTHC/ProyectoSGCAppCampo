package com.unal.proyectosgcappcampo.ui.slideshow;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.unal.proyectosgcappcampo.R;
import com.unal.proyectosgcappcampo.databinding.FragmentSlideshowBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unal.proyectosgcappcampo.ui.gallery.Feature;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    private FirebaseAuth firebaseAuth;
    String userName;
    boolean login = false;

    Button btnFormLoad;
    Button btnFormSync;
    EditText etEstacion;
    EditText etTipoEstacion;
    EditText etEste;
    EditText etNorte;
    EditText etAltitud;
    EditText etFotos;
    EditText etObservaciones;

    JSONObject attrForm;
    JSONArray formComplete;

    InputStreamReader archivo;
    Boolean auxTextExist = false;
    String listaFormText = "";

    int idLinear;

    Spinner sFormularios;
    LinearLayout liFormularios;
    List<String> listFormularios = new ArrayList<String>();
    List<LinearLayout> listLiForm = new ArrayList<LinearLayout>();
    List<EditText> listEditText = new ArrayList<EditText>();
    List<Spinner> listSpinner = new ArrayList<Spinner>();
    List<Button> listBtnAcordion = new ArrayList<Button>();

    List<List<EditText>> ListaEditText = new ArrayList<List<EditText>>();
    List<List<Spinner>> ListaSpinner = new ArrayList<List<Spinner>>();


    //Format UGS
    List<Integer> listContDiscontinuidades = new ArrayList<Integer>();
    List<Integer> listContFotosAnexas = new ArrayList<Integer>();

    int discontinuidades = 0;
    List<LinearLayout> listDiscontinuidades = new ArrayList<LinearLayout>();
    LinearLayout liFormDiscontinuidades;

    int fotosAnexas = 0;
    List<LinearLayout> listFotosAnexas = new ArrayList<LinearLayout>();
    LinearLayout liFormFotosAnexas;

    private DatabaseReference databaseReference;

    private Context mcont = getActivity();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mcont = root.getContext();

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        btnFormLoad = binding.btnFormLoad;
        btnFormSync = binding.btnFormSync;
        etEstacion = binding.etEstacion;
        etTipoEstacion = binding.etTipoEstacion;
        etNorte = binding.etNorte;
        etEste = binding.etEste;
        etAltitud = binding.etAltitud;
        etFotos = binding.etFotos;
        etObservaciones = binding.etObservaciones;

        liFormularios = binding.liFormularios;
        sFormularios = binding.sFormularios;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mcont, R.array.Formularios , android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sFormularios.setAdapter(adapter);

        sFormularios.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> spn, android.view.View v, int posicion, long id) {
                String selectForm = spn.getItemAtPosition(posicion).toString();
                if (!selectForm.equals("Ninguno")) {
                    Toast.makeText(spn.getContext(), "Se añadió formulario: " + selectForm, Toast.LENGTH_LONG).show();
                    AddFormulario(selectForm);
                }
            }
            public void onNothingSelected(AdapterView<?> spn) {
            }
        });


        databaseReference = FirebaseDatabase.getInstance().getReference();

        String[] files = mcont.fileList();

        if (ArchivoExiste(files, "listaForm.txt")){
            try {
                archivo = new InputStreamReader(mcont.openFileInput("listaForm.txt"));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                listaFormText = "";

                while (linea != null){
                    listaFormText = listaFormText + linea + "\n";
                    linea = br.readLine();
                }
                formComplete = new JSONArray(listaFormText);
                Log.d("alprincipio", "GuardarForm: "+formComplete);

                br.close();
                archivo.close();

                auxTextExist = true;

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
        } else {
            formComplete = new JSONArray();
        }

        btnFormLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (login){
                        GuardarForm();
                    }else{
                        Toast.makeText(mcont, "Por favor Inicie Sesión\n", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });

        btnFormSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (login && isOnlineNet()){
                        SubirForm();
                    }else{
                        if (login){
                            Toast.makeText(mcont, "Asegurese de estar conectado a internet\n", Toast.LENGTH_LONG).show();
                        } else if (isOnlineNet()){
                            Toast.makeText(mcont, "Por favor Inicie Sesión\n", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(mcont, "Asegurese de estar conectado a internet\n e iniciar Sesión\n", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (JSONException | IOException e) {
                    e.printStackTrace();
                }
            }
        });


        return root;
    }

    private void AddFormulario(String formType) {

        int mtop = 70;
        listFormularios.add(formType);
        idLinear = listFormularios.size() - 1;

        listEditText = new ArrayList<EditText>();
        listSpinner = new ArrayList<Spinner>();

        ListaEditText.add(listEditText);
        ListaSpinner.add(listSpinner);

        fotosAnexas = 0;
        discontinuidades = 0;

        listContFotosAnexas.add(fotosAnexas);
        listContDiscontinuidades.add(discontinuidades);

        if (formType.equals("UGS Rocas")) {

            Button bAcordion = new Button(mcont);
            bAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
            bAcordion.setText("Formato UGS Rocas");
            bAcordion.setTag(idLinear);
            listBtnAcordion.add(bAcordion);
            liFormularios.addView(bAcordion);

            LinearLayout liForm = new LinearLayout(mcont);
            liForm.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liForm.setOrientation(LinearLayout.VERTICAL);
            liForm.setBackgroundColor(0x33333300);
            liForm.setVisibility(View.GONE);

            bAcordion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (liForm.getVisibility() == View.VISIBLE) {
                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                        animation.setDuration(220);
                        animation.setFillAfter(true);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                liForm.setVisibility(View.GONE);
                                bAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                            }
                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        liForm.startAnimation(animation);

                    }
                    else {
                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                        animation.setDuration(220);
                        animation.setFillAfter(true);
                        liForm.startAnimation(animation);
                        liForm.setVisibility(View.VISIBLE);
                        bAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                    }

                }
            });



            //------------> Titulo del Formato

            TextView tvTitulo = new TextView(mcont);
            tvTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvTitulo.setText("Formato UGSR");
            tvTitulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvTitulo.setTextAppearance(R.style.TituloFormato);
            tvTitulo.setPadding(0, 70, 0, 70);
            liForm.addView(tvTitulo);

            Button bBorrarForm = new Button(mcont);
            bBorrarForm.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bBorrarForm.setText("Borrar Este Formulario");
            bBorrarForm.setTag(idLinear);
            bBorrarForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listLiForm.get(Integer.parseInt(v.getTag().toString())).removeAllViews();
                    liFormularios.removeView(listBtnAcordion.get(Integer.parseInt(v.getTag().toString())));
                    listFormularios.set(Integer.parseInt(v.getTag().toString()), "Ninguno");
                }
            });
            liForm.addView(bBorrarForm);

            //------------> Numero de Formato

            TextView tvNoFormato = new TextView(mcont);
            tvNoFormato.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvNoFormato.setText("Número Formato");
            tvNoFormato.setTextAppearance(R.style.TituloItem);
            tvNoFormato.setPadding(0, mtop, 0, 0);
            liForm.addView(tvNoFormato);

            EditText etNoFormato = new EditText(mcont);
            etNoFormato.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etNoFormato.setHint("Número Formato");
            etNoFormato.setEms(10);
            etNoFormato.setTag("noformato");
            ListaEditText.get(idLinear).add(etNoFormato);
            liForm.addView(etNoFormato);

            //------------> Municipios

            TextView tvMunicipios = new TextView(mcont);
            tvMunicipios.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvMunicipios.setText("Municipio");
            tvMunicipios.setTextAppearance(R.style.TituloItem);
            tvMunicipios.setPadding(0, mtop, 0, 0);
            liForm.addView(tvMunicipios);

            Spinner sMunicipios = new Spinner(mcont);
            sMunicipios.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mcont, R.array.Municipios, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sMunicipios.setAdapter(adapter);
            sMunicipios.setTag("municipios");
            ListaSpinner.get(idLinear).add(sMunicipios);
            liForm.addView(sMunicipios);

            //------------> Vereda

            TextView tvVereda = new TextView(mcont);
            tvVereda.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvVereda.setText("Vereda");
            tvVereda.setTextAppearance(R.style.TituloItem);
            tvVereda.setPadding(0, mtop, 0, 0);
            liForm.addView(tvVereda);

            EditText etVereda = new EditText(mcont);
            etVereda.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etVereda.setHint("Vereda");
            etVereda.setEms(10);
            etVereda.setTag("vereda");
            ListaEditText.get(idLinear).add(etVereda);
            liForm.addView(etVereda);

//            //------------> Nombre Formato
//
//            TextView tvNombreFormato = new TextView(mcont);
//            tvNombreFormato.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            tvNombreFormato.setText("Nombre del Formato");
//            tvNombreFormato.setTextAppearance(R.style.TituloItem);
//            tvNombreFormato.setPadding(0, mtop, 0, 0);
//            liForm.addView(tvNombreFormato);
//
//            EditText etNombreFormato = new EditText(mcont);
//            etNombreFormato.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            etNombreFormato.setHint("Nombre del Formato");
//            etNombreFormato.setEms(10);
//            etNombreFormato.setTag("nombreformato");
//            ListaEditText.get(idLinear).add(etNombreFormato);
//            liForm.addView(etNombreFormato);

            //------------> Numero de la Estación

            TextView tvNoEstacion = new TextView(mcont);
            tvNoEstacion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvNoEstacion.setText("Número de la Estación");
            tvNoEstacion.setTextAppearance(R.style.TituloItem);
            tvNoEstacion.setPadding(0, mtop, 0, 0);
            liForm.addView(tvNoEstacion);

            EditText etNoEstacion = new EditText(mcont);
            etNoEstacion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etNoEstacion.setHint("Número de la Estación");
            etNoEstacion.setEms(10);
            etNoEstacion.setTag("noestacion");
            ListaEditText.get(idLinear).add(etNoEstacion);
            liForm.addView(etNoEstacion);

            //------------> Clase de Afloramiento

            TextView tvClaseAflor = new TextView(mcont);
            tvClaseAflor.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvClaseAflor.setText("Clase Afloramiento");
            tvClaseAflor.setTextAppearance(R.style.TituloItem);
            tvClaseAflor.setPadding(0, mtop, 0, 0);
            liForm.addView(tvClaseAflor);

            Spinner sClaseAflor = new Spinner(mcont);
            sClaseAflor.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(mcont, R.array.ClaseAfloramiento, android.R.layout.simple_spinner_item);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sClaseAflor.setAdapter(adapter1);
            sClaseAflor.setTag("claseaflor");
            ListaSpinner.get(idLinear).add(sClaseAflor);
            liForm.addView(sClaseAflor);

            //------------> Secuencia Estratigráfica

            TextView tvSecuenciaEstrati = new TextView(mcont);
            tvSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvSecuenciaEstrati.setText("Secuencia Estratigráfica");
            tvSecuenciaEstrati.setTextAppearance(R.style.TituloFormato);
            tvSecuenciaEstrati.setPadding(0, mtop, 0, 0);
            liForm.addView(tvSecuenciaEstrati);

            int secuEstratiWidth = 420;
            int secuEstratiOrdenWidth = 200;
            int secuEstratiEspesorWidth = 300;

            //-------------------> Depósito de Gravedad

            LinearLayout liFormSecuenciaEstrati1 = new LinearLayout(mcont);
            liFormSecuenciaEstrati1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormSecuenciaEstrati1.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvSecuenciaEstratiOpt1 = new TextView(mcont);
            tvSecuenciaEstratiOpt1.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvSecuenciaEstratiOpt1.setText("Depósito de Gravedad: ");
            tvSecuenciaEstratiOpt1.setTextAppearance(R.style.TituloItem);
            liFormSecuenciaEstrati1.addView(tvSecuenciaEstratiOpt1);

            EditText etSecuenciaEstratiOpt1Orden = new EditText(mcont);
            etSecuenciaEstratiOpt1Orden.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etSecuenciaEstratiOpt1Orden.setHint("Orden");
            etSecuenciaEstratiOpt1Orden.setEms(10);
            etSecuenciaEstratiOpt1Orden.setTag("secuenciaestratiopt1orden");
            ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt1Orden);
            liFormSecuenciaEstrati1.addView(etSecuenciaEstratiOpt1Orden);

            EditText etSecuenciaEstratiOpt1Espesor = new EditText(mcont);
            etSecuenciaEstratiOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etSecuenciaEstratiOpt1Espesor.setHint("Espesor (m)");
            etSecuenciaEstratiOpt1Espesor.setEms(10);
            etSecuenciaEstratiOpt1Espesor.setTag("secuenciaestratiopt1espesor");
            ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt1Espesor);
            liFormSecuenciaEstrati1.addView(etSecuenciaEstratiOpt1Espesor);

            liForm.addView(liFormSecuenciaEstrati1);

            //-------------------> Suelo Transportado

            LinearLayout liFormSecuenciaEstrati2 = new LinearLayout(mcont);
            liFormSecuenciaEstrati2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormSecuenciaEstrati2.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvSecuenciaEstratiOpt2 = new TextView(mcont);
            tvSecuenciaEstratiOpt2.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvSecuenciaEstratiOpt2.setText("Suelo Transportado: ");
            tvSecuenciaEstratiOpt2.setTextAppearance(R.style.TituloItem);
            liFormSecuenciaEstrati2.addView(tvSecuenciaEstratiOpt2);

            EditText etSecuenciaEstratiOpt2Orden = new EditText(mcont);
            etSecuenciaEstratiOpt2Orden.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etSecuenciaEstratiOpt2Orden.setHint("Orden");
            etSecuenciaEstratiOpt2Orden.setEms(10);
            etSecuenciaEstratiOpt2Orden.setTag("secuenciaestratiopt2orden");
            ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt2Orden);
            liFormSecuenciaEstrati2.addView(etSecuenciaEstratiOpt2Orden);

            EditText etSecuenciaEstratiOpt2Espesor = new EditText(mcont);
            etSecuenciaEstratiOpt2Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etSecuenciaEstratiOpt2Espesor.setHint("Espesor (m)");
            etSecuenciaEstratiOpt2Espesor.setEms(10);
            etSecuenciaEstratiOpt2Espesor.setTag("secuenciaestratiopt2espesor");
            ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt2Espesor);
            liFormSecuenciaEstrati2.addView(etSecuenciaEstratiOpt2Espesor);

            liForm.addView(liFormSecuenciaEstrati2);


            //-------------------> Suelo Residual

            LinearLayout liFormSecuenciaEstrati3 = new LinearLayout(mcont);
            liFormSecuenciaEstrati3.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormSecuenciaEstrati3.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvSecuenciaEstratiOpt3 = new TextView(mcont);
            tvSecuenciaEstratiOpt3.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvSecuenciaEstratiOpt3.setText("Suelo Residual: ");
            tvSecuenciaEstratiOpt3.setTextAppearance(R.style.TituloItem);
            liFormSecuenciaEstrati3.addView(tvSecuenciaEstratiOpt3);

            EditText etSecuenciaEstratiOpt3Orden = new EditText(mcont);
            etSecuenciaEstratiOpt3Orden.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etSecuenciaEstratiOpt3Orden.setHint("Orden");
            etSecuenciaEstratiOpt3Orden.setEms(10);
            etSecuenciaEstratiOpt3Orden.setTag("secuenciaestratiopt3orden");
            ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt3Orden);
            liFormSecuenciaEstrati3.addView(etSecuenciaEstratiOpt3Orden);

            EditText etSecuenciaEstratiOpt3Espesor = new EditText(mcont);
            etSecuenciaEstratiOpt3Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etSecuenciaEstratiOpt3Espesor.setHint("Espesor (m)");
            etSecuenciaEstratiOpt3Espesor.setEms(10);
            etSecuenciaEstratiOpt3Espesor.setTag("secuenciaestratiopt3espesor");
            ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt3Espesor);
            liFormSecuenciaEstrati3.addView(etSecuenciaEstratiOpt3Espesor);

            liForm.addView(liFormSecuenciaEstrati3);


            //-------------------> Roca

            LinearLayout liFormSecuenciaEstrati4 = new LinearLayout(mcont);
            liFormSecuenciaEstrati4.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormSecuenciaEstrati4.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvSecuenciaEstratiOpt4 = new TextView(mcont);
            tvSecuenciaEstratiOpt4.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvSecuenciaEstratiOpt4.setText("Roca: ");
            tvSecuenciaEstratiOpt4.setTextAppearance(R.style.TituloItem);
            liFormSecuenciaEstrati4.addView(tvSecuenciaEstratiOpt4);

            EditText etSecuenciaEstratiOpt4Orden = new EditText(mcont);
            etSecuenciaEstratiOpt4Orden.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etSecuenciaEstratiOpt4Orden.setHint("Orden");
            etSecuenciaEstratiOpt4Orden.setEms(10);
            etSecuenciaEstratiOpt4Orden.setTag("secuenciaestratiopt4orden");
            ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt4Orden);
            liFormSecuenciaEstrati4.addView(etSecuenciaEstratiOpt4Orden);

            EditText etSecuenciaEstratiOpt4Espesor = new EditText(mcont);
            etSecuenciaEstratiOpt4Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etSecuenciaEstratiOpt4Espesor.setHint("Espesor (m)");
            etSecuenciaEstratiOpt4Espesor.setEms(10);
            etSecuenciaEstratiOpt4Espesor.setTag("secuenciaestratiopt4espesor");
            ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt4Espesor);
            liFormSecuenciaEstrati4.addView(etSecuenciaEstratiOpt4Espesor);

            liForm.addView(liFormSecuenciaEstrati4);

            //-------------------> Listener para saber si se trata de un suelo residual

            LinearLayout liFormSecuenciaEstratiSueloR = new LinearLayout(mcont);
            liFormSecuenciaEstratiSueloR.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormSecuenciaEstratiSueloR.setOrientation(LinearLayout.VERTICAL);

            liForm.addView(liFormSecuenciaEstratiSueloR);

            etSecuenciaEstratiOpt3Orden.addTextChangedListener(new TextWatcher() {
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
                    elNuevoTexto = elNuevoTexto.replace(" ","");

                    //-------------------> Si es Suelo Residual

                    if (!elNuevoTexto.equals("")){

                        int secuEstratiWidth = 150;
                        int secuEstratiOrdenWidth = 200;
                        int secuEstratiEspesorWidth = 300;

                        TextView tvSecuenciaEstratiHorizonte1 = new TextView(mcont);
                        tvSecuenciaEstratiHorizonte1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvSecuenciaEstratiHorizonte1.setText("Si es Suelo Residual: ");
                        tvSecuenciaEstratiHorizonte1.setTextAppearance(R.style.TituloItem);
                        tvSecuenciaEstratiHorizonte1.setPadding(0, mtop, 0, 0);
                        liFormSecuenciaEstratiSueloR.addView(tvSecuenciaEstratiHorizonte1);

                        TextView tvSecuenciaEstratiHorizonte2 = new TextView(mcont);
                        tvSecuenciaEstratiHorizonte2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvSecuenciaEstratiHorizonte2.setText("Horizonte");
                        tvSecuenciaEstratiHorizonte2.setTextAppearance(R.style.TituloItem);
                        liFormSecuenciaEstratiSueloR.addView(tvSecuenciaEstratiHorizonte2);

                        //-------------------> III

                        LinearLayout liFormSecuenciaEstratiSueloR1 = new LinearLayout(mcont);
                        liFormSecuenciaEstratiSueloR1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormSecuenciaEstratiSueloR1.setOrientation(LinearLayout.HORIZONTAL);

                        TextView tvSecuenciaEstratiSueloR1 = new TextView(mcont);
                        tvSecuenciaEstratiSueloR1.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvSecuenciaEstratiSueloR1.setText("III: ");
                        tvSecuenciaEstratiSueloR1.setTextAppearance(R.style.TituloItem);
                        liFormSecuenciaEstratiSueloR1.addView(tvSecuenciaEstratiSueloR1);

                        EditText etSecuenciaEstratiSueloR1Orden = new EditText(mcont);
                        etSecuenciaEstratiSueloR1Orden.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiSueloR1Orden.setHint("Orden");
                        etSecuenciaEstratiSueloR1Orden.setEms(10);
                        etSecuenciaEstratiSueloR1Orden.setTag("secuenciaestratisuelor1orden");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiSueloR1Orden);
                        liFormSecuenciaEstratiSueloR1.addView(etSecuenciaEstratiSueloR1Orden);

                        EditText etSecuenciaEstratiSueloR1Espesor = new EditText(mcont);
                        etSecuenciaEstratiSueloR1Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiSueloR1Espesor.setHint("Espesor (m)");
                        etSecuenciaEstratiSueloR1Espesor.setEms(10);
                        etSecuenciaEstratiSueloR1Espesor.setTag("secuenciaestratisuelor1espesor");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiSueloR1Espesor);
                        liFormSecuenciaEstratiSueloR1.addView(etSecuenciaEstratiSueloR1Espesor);

                        liFormSecuenciaEstratiSueloR.addView(liFormSecuenciaEstratiSueloR1);

                        //-------------------> II

                        LinearLayout liFormSecuenciaEstratiSueloR2 = new LinearLayout(mcont);
                        liFormSecuenciaEstratiSueloR2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormSecuenciaEstratiSueloR2.setOrientation(LinearLayout.HORIZONTAL);

                        TextView tvSecuenciaEstratiSueloR2 = new TextView(mcont);
                        tvSecuenciaEstratiSueloR2.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvSecuenciaEstratiSueloR2.setText("II: ");
                        tvSecuenciaEstratiSueloR2.setTextAppearance(R.style.TituloItem);
                        liFormSecuenciaEstratiSueloR2.addView(tvSecuenciaEstratiSueloR2);

                        EditText etSecuenciaEstratiSueloR2Orden = new EditText(mcont);
                        etSecuenciaEstratiSueloR2Orden.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiSueloR2Orden.setHint("Orden");
                        etSecuenciaEstratiSueloR2Orden.setEms(10);
                        etSecuenciaEstratiSueloR2Orden.setTag("secuenciaestratisuelor2orden");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiSueloR2Orden);
                        liFormSecuenciaEstratiSueloR2.addView(etSecuenciaEstratiSueloR2Orden);

                        EditText etSecuenciaEstratiSueloR2Espesor = new EditText(mcont);
                        etSecuenciaEstratiSueloR2Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiSueloR2Espesor.setHint("Espesor (m)");
                        etSecuenciaEstratiSueloR2Espesor.setEms(10);
                        etSecuenciaEstratiSueloR2Espesor.setTag("secuenciaestratisuelor2espesor");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiSueloR2Espesor);
                        liFormSecuenciaEstratiSueloR2.addView(etSecuenciaEstratiSueloR2Espesor);

                        liFormSecuenciaEstratiSueloR.addView(liFormSecuenciaEstratiSueloR2);
                        //-------------------> I

                        LinearLayout liFormSecuenciaEstratiSueloR3 = new LinearLayout(mcont);
                        liFormSecuenciaEstratiSueloR3.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormSecuenciaEstratiSueloR3.setOrientation(LinearLayout.HORIZONTAL);

                        TextView tvSecuenciaEstratiSueloR3 = new TextView(mcont);
                        tvSecuenciaEstratiSueloR3.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvSecuenciaEstratiSueloR3.setText("I: ");
                        tvSecuenciaEstratiSueloR3.setTextAppearance(R.style.TituloItem);
                        liFormSecuenciaEstratiSueloR3.addView(tvSecuenciaEstratiSueloR3);

                        EditText etSecuenciaEstratiSueloR3Orden = new EditText(mcont);
                        etSecuenciaEstratiSueloR3Orden.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiSueloR3Orden.setHint("Orden");
                        etSecuenciaEstratiSueloR3Orden.setEms(10);
                        etSecuenciaEstratiSueloR3Orden.setTag("secuenciaestratisuelor3orden");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiSueloR3Orden);
                        liFormSecuenciaEstratiSueloR3.addView(etSecuenciaEstratiSueloR3Orden);

                        EditText etSecuenciaEstratiSueloR3Espesor = new EditText(mcont);
                        etSecuenciaEstratiSueloR3Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiSueloR3Espesor.setHint("Espesor (m)");
                        etSecuenciaEstratiSueloR3Espesor.setEms(10);
                        etSecuenciaEstratiSueloR3Espesor.setTag("secuenciaestratisuelor3espesor");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiSueloR3Espesor);
                        liFormSecuenciaEstratiSueloR3.addView(etSecuenciaEstratiSueloR3Espesor);

                        liFormSecuenciaEstratiSueloR.addView(liFormSecuenciaEstratiSueloR3);
                    }
                    else{
                        liFormSecuenciaEstratiSueloR.removeAllViews();
                    }

                }
            });


            //------------> CARACTERIZACIÓN DE LA UGS / UGI

            TextView tvCaracterizacionUGS = new TextView(mcont);
            tvCaracterizacionUGS.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvCaracterizacionUGS.setText("CARACTERIZACIÓN DE LA UGS / UGI");
            tvCaracterizacionUGS.setTextAppearance(R.style.TituloFormato);
            tvCaracterizacionUGS.setPadding(0, mtop, 0, 0);
            liForm.addView(tvCaracterizacionUGS);


            //------------> Perfil de meteorización (Dearman 1974)

            TextView tvPerfilMeteorizacion = new TextView(mcont);
            tvPerfilMeteorizacion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvPerfilMeteorizacion.setText("Perfil de meteorización (Dearman 1974)");
            tvPerfilMeteorizacion.setTextAppearance(R.style.TituloItem);
            tvPerfilMeteorizacion.setPadding(0, mtop, 0, 0);
            liForm.addView(tvPerfilMeteorizacion);

            EditText etPerfilMeteorizacion = new EditText(mcont);
            etPerfilMeteorizacion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etPerfilMeteorizacion.setHint("I-II-III-IV-V-VI");
            etPerfilMeteorizacion.setEms(10);
            etPerfilMeteorizacion.setTag("perfilmeteorizacion");
            ListaEditText.get(idLinear).add(etPerfilMeteorizacion);
            liForm.addView(etPerfilMeteorizacion);


            //------------> N° litologías asociadas a la UGS /UGI

            TextView tvlitologiasasociadas = new TextView(mcont);
            tvlitologiasasociadas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvlitologiasasociadas.setText("N° litologías asociadas a la UGS /UGI");
            tvlitologiasasociadas.setTextAppearance(R.style.TituloItem);
            tvlitologiasasociadas.setPadding(0, mtop, 0, 0);
            liForm.addView(tvlitologiasasociadas);

            secuEstratiWidth = 200;
            secuEstratiOrdenWidth = 400;

            //------------> Litología 1

            LinearLayout liFormlitologiasasociadas1 = new LinearLayout(mcont);
            liFormlitologiasasociadas1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormlitologiasasociadas1.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvlitologiasasociadasOpt1 = new TextView(mcont);
            tvlitologiasasociadasOpt1.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvlitologiasasociadasOpt1.setText("Litología 1");
            tvlitologiasasociadasOpt1.setTextAppearance(R.style.TituloItem);
            liFormlitologiasasociadas1.addView(tvlitologiasasociadasOpt1);

            EditText etlitologiasasociadasOpt1Exist = new EditText(mcont);
            etlitologiasasociadasOpt1Exist.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etlitologiasasociadasOpt1Exist.setHint("Marque X si Existe");
            etlitologiasasociadasOpt1Exist.setEms(10);
            etlitologiasasociadasOpt1Exist.setTag("litologiasasociadasopt1exist");
            ListaEditText.get(idLinear).add(etlitologiasasociadasOpt1Exist);
            liFormlitologiasasociadas1.addView(etlitologiasasociadasOpt1Exist);

            EditText etlitologiasasociadasOpt1Espesor = new EditText(mcont);
            etlitologiasasociadasOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etlitologiasasociadasOpt1Espesor.setHint("Espesor (m)");
            etlitologiasasociadasOpt1Espesor.setEms(10);
            etlitologiasasociadasOpt1Espesor.setTag("litologiasasociadasopt1espesor");
            ListaEditText.get(idLinear).add(etlitologiasasociadasOpt1Espesor);
            liFormlitologiasasociadas1.addView(etlitologiasasociadasOpt1Espesor);

            liForm.addView(liFormlitologiasasociadas1);

            //------------> Litología 2

            LinearLayout liFormlitologiasasociadas2 = new LinearLayout(mcont);
            liFormlitologiasasociadas2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormlitologiasasociadas2.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvlitologiasasociadasOpt2 = new TextView(mcont);
            tvlitologiasasociadasOpt2.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvlitologiasasociadasOpt2.setText("Litología 2");
            tvlitologiasasociadasOpt2.setTextAppearance(R.style.TituloItem);
            liFormlitologiasasociadas2.addView(tvlitologiasasociadasOpt2);

            EditText etlitologiasasociadasOpt2Exist = new EditText(mcont);
            etlitologiasasociadasOpt2Exist.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etlitologiasasociadasOpt2Exist.setHint("Marque X si Existe");
            etlitologiasasociadasOpt2Exist.setEms(10);
            etlitologiasasociadasOpt2Exist.setTag("litologiasasociadasopt2exist");
            ListaEditText.get(idLinear).add(etlitologiasasociadasOpt2Exist);
            liFormlitologiasasociadas2.addView(etlitologiasasociadasOpt2Exist);

            EditText etlitologiasasociadasOpt2Espesor = new EditText(mcont);
            etlitologiasasociadasOpt2Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etlitologiasasociadasOpt2Espesor.setHint("Espesor (m)");
            etlitologiasasociadasOpt2Espesor.setEms(10);
            etlitologiasasociadasOpt2Espesor.setTag("litologiasasociadasopt2espesor");
            ListaEditText.get(idLinear).add(etlitologiasasociadasOpt2Espesor);
            liFormlitologiasasociadas2.addView(etlitologiasasociadasOpt2Espesor);

            liForm.addView(liFormlitologiasasociadas2);


            //------------> Nombre de la UGS / UGI

            TextView tvNombreUGS = new TextView(mcont);
            tvNombreUGS.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvNombreUGS.setText("Nombre de la UGS / UGI");
            tvNombreUGS.setTextAppearance(R.style.TituloItem);
            tvNombreUGS.setPadding(0, mtop, 0, 0);
            liForm.addView(tvNombreUGS);

            EditText etNombreUGS = new EditText(mcont);
            etNombreUGS.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etNombreUGS.setHint("Nombre UGS / UGI");
            etNombreUGS.setEms(10);
            etNombreUGS.setTag("nombreugs");
            ListaEditText.get(idLinear).add(etNombreUGS);
            liForm.addView(etNombreUGS);


            //------------> GSI

            TextView tvGSI = new TextView(mcont);
            tvGSI.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvGSI.setText("GSI");
            tvGSI.setTextAppearance(R.style.TituloItem);
            tvGSI.setPadding(0, mtop, 0, 0);
            liForm.addView(tvGSI);

            Spinner sGSI = new Spinner(mcont);
            sGSI.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(mcont, R.array.GSI, android.R.layout.simple_spinner_item);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sGSI.setAdapter(adapter2);
            sGSI.setTag("gsi");
            ListaSpinner.get(idLinear).add(sGSI);
            liForm.addView(sGSI);

            //------------> CARACTERÍSTICAS DE LA UGS / UGI

            TextView tvCaracteristicasUGS = new TextView(mcont);
            tvCaracteristicasUGS.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvCaracteristicasUGS.setText("CARACTERÍSTICAS DE LA UGS / UGI");
            tvCaracteristicasUGS.setTextAppearance(R.style.TituloFormato);
            tvCaracteristicasUGS.setPadding(0, mtop, 0, 0);
            liForm.addView(tvCaracteristicasUGS);

            //------------> Fábrica Litologia 1

            TextView tvFabrica1 = new TextView(mcont);
            tvFabrica1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvFabrica1.setText("Fábrica Litología 1");
            tvFabrica1.setTextAppearance(R.style.TituloItem);
            tvFabrica1.setPadding(0, mtop, 0, 0);
            liForm.addView(tvFabrica1);

            Spinner sFabrica1 = new Spinner(mcont);
            sFabrica1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(mcont, R.array.Fabrica1, android.R.layout.simple_spinner_item);
            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sFabrica1.setAdapter(adapter3);
            sFabrica1.setTag("fabrica1");
            ListaSpinner.get(idLinear).add(sFabrica1);
            liForm.addView(sFabrica1);

            //------------> Fábrica Litologia 2

            TextView tvFabrica2 = new TextView(mcont);
            tvFabrica2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvFabrica2.setText("Fábrica Litología 2");
            tvFabrica2.setTextAppearance(R.style.TituloItem);
            tvFabrica2.setPadding(0, mtop, 0, 0);
            liForm.addView(tvFabrica2);

            Spinner sFabrica2 = new Spinner(mcont);
            sFabrica2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(mcont, R.array.Fabrica2, android.R.layout.simple_spinner_item);
            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sFabrica2.setAdapter(adapter4);
            sFabrica2.setTag("fabrica2");
            ListaSpinner.get(idLinear).add(sFabrica2);
            liForm.addView(sFabrica2);

            //------------> Humedad Natural 1

            TextView tvHumedad1 = new TextView(mcont);
            tvHumedad1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvHumedad1.setText("Humedad Natural Litología 1");
            tvHumedad1.setTextAppearance(R.style.TituloItem);
            tvHumedad1.setPadding(0, mtop, 0, 0);
            liForm.addView(tvHumedad1);

            Spinner sHumedad1 = new Spinner(mcont);
            sHumedad1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(mcont, R.array.HumedadNatural1, android.R.layout.simple_spinner_item);
            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sHumedad1.setAdapter(adapter5);
            sHumedad1.setTag("humedad1");
            ListaSpinner.get(idLinear).add(sHumedad1);
            liForm.addView(sHumedad1);

            //------------> Humedad Natural 2

            TextView tvHumedad2 = new TextView(mcont);
            tvHumedad2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvHumedad2.setText("Humedad Natural Litología 2");
            tvHumedad2.setTextAppearance(R.style.TituloItem);
            tvHumedad2.setPadding(0, mtop, 0, 0);
            liForm.addView(tvHumedad2);

            Spinner sHumedad2 = new Spinner(mcont);
            sHumedad2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter6 = ArrayAdapter.createFromResource(mcont, R.array.HumedadNatural2, android.R.layout.simple_spinner_item);
            adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sHumedad2.setAdapter(adapter6);
            sHumedad2.setTag("humedad2");
            ListaSpinner.get(idLinear).add(sHumedad2);
            liForm.addView(sHumedad2);

            //------------> Tamaño de grano  1

            TextView tvTamanoGrano1 = new TextView(mcont);
            tvTamanoGrano1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvTamanoGrano1.setText("Tamaño del Grano Litología 1");
            tvTamanoGrano1.setTextAppearance(R.style.TituloItem);
            tvTamanoGrano1.setPadding(0, mtop, 0, 0);
            liForm.addView(tvTamanoGrano1);

            Spinner sTamanoGrano1 = new Spinner(mcont);
            sTamanoGrano1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter7 = ArrayAdapter.createFromResource(mcont, R.array.TamañoGrano1, android.R.layout.simple_spinner_item);
            adapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sTamanoGrano1.setAdapter(adapter7);
            sTamanoGrano1.setTag("tamañograno1");
            ListaSpinner.get(idLinear).add(sTamanoGrano1);
            liForm.addView(sTamanoGrano1);

            //------------> Tamaño de grano  2

            TextView tvTamanoGrano2 = new TextView(mcont);
            tvTamanoGrano2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvTamanoGrano2.setText("Tamaño del Grano Litología 2");
            tvTamanoGrano2.setTextAppearance(R.style.TituloItem);
            tvTamanoGrano2.setPadding(0, mtop, 0, 0);
            liForm.addView(tvTamanoGrano2);

            Spinner sTamanoGrano2 = new Spinner(mcont);
            sTamanoGrano2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter8 = ArrayAdapter.createFromResource(mcont, R.array.TamañoGrano2, android.R.layout.simple_spinner_item);
            adapter8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sTamanoGrano2.setAdapter(adapter8);
            sTamanoGrano2.setTag("tamañograno2");
            ListaSpinner.get(idLinear).add(sTamanoGrano2);
            liForm.addView(sTamanoGrano2);

            //------------> Grado meteorización  1

            TextView tvGradoMeteo1 = new TextView(mcont);
            tvGradoMeteo1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvGradoMeteo1.setText("Grado de Meteorización Litología 1");
            tvGradoMeteo1.setTextAppearance(R.style.TituloItem);
            tvGradoMeteo1.setPadding(0, mtop, 0, 0);
            liForm.addView(tvGradoMeteo1);

            Spinner sGradoMeteo1 = new Spinner(mcont);
            sGradoMeteo1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter9 = ArrayAdapter.createFromResource(mcont, R.array.GradoMeteo1, android.R.layout.simple_spinner_item);
            adapter9.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sGradoMeteo1.setAdapter(adapter9);
            sGradoMeteo1.setTag("gradometeo1");
            ListaSpinner.get(idLinear).add(sGradoMeteo1);
            liForm.addView(sGradoMeteo1);

            //------------> Grado meteorización  2

            TextView tvGradoMeteo2 = new TextView(mcont);
            tvGradoMeteo2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvGradoMeteo2.setText("Grado de Meteorización Litología 2");
            tvGradoMeteo2.setTextAppearance(R.style.TituloItem);
            tvGradoMeteo2.setPadding(0, mtop, 0, 0);
            liForm.addView(tvGradoMeteo2);

            Spinner sGradoMeteo2 = new Spinner(mcont);
            sGradoMeteo2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter10 = ArrayAdapter.createFromResource(mcont, R.array.GradoMeteo2, android.R.layout.simple_spinner_item);
            adapter10.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sGradoMeteo2.setAdapter(adapter10);
            sGradoMeteo2.setTag("gradometeo2");
            ListaSpinner.get(idLinear).add(sGradoMeteo2);
            liForm.addView(sGradoMeteo2);

            //------------> Resistenca a la compresion  1

            TextView tvResistenciaComp1 = new TextView(mcont);
            tvResistenciaComp1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvResistenciaComp1.setText("Resistencia a la Compresión Simple (Mpa) Litología 1");
            tvResistenciaComp1.setTextAppearance(R.style.TituloItem);
            tvResistenciaComp1.setPadding(0, mtop, 0, 0);
            liForm.addView(tvResistenciaComp1);

            Spinner sResistenciaComp1 = new Spinner(mcont);
            sResistenciaComp1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter11 = ArrayAdapter.createFromResource(mcont, R.array.ResistenciaCompresionSimple1, android.R.layout.simple_spinner_item);
            adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sResistenciaComp1.setAdapter(adapter11);
            sResistenciaComp1.setTag("resistenciacomp1");
            ListaSpinner.get(idLinear).add(sResistenciaComp1);
            liForm.addView(sResistenciaComp1);

            //------------> Resistenca a la compresion  2

            TextView tvResistenciaComp2 = new TextView(mcont);
            tvResistenciaComp2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvResistenciaComp2.setText("Resistencia a la Compresión Simple (Mpa) Litología 2");
            tvResistenciaComp2.setTextAppearance(R.style.TituloItem);
            tvResistenciaComp2.setPadding(0, mtop, 0, 0);
            liForm.addView(tvResistenciaComp2);

            Spinner sResistenciaComp2 = new Spinner(mcont);
            sResistenciaComp2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter12 = ArrayAdapter.createFromResource(mcont, R.array.ResistenciaCompresionSimple2, android.R.layout.simple_spinner_item);
            adapter12.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sResistenciaComp2.setAdapter(adapter12);
            sResistenciaComp2.setTag("resistenciacomp2");
            ListaSpinner.get(idLinear).add(sResistenciaComp2);
            liForm.addView(sResistenciaComp2);

            //------------> Color 1

            TextView tvColor1 = new TextView(mcont);
            tvColor1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvColor1.setText("Color Litología 1");
            tvColor1.setTextAppearance(R.style.TituloItem);
            tvColor1.setPadding(0, mtop, 0, 0);
            liForm.addView(tvColor1);

            EditText etColor1 = new EditText(mcont);
            etColor1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etColor1.setHint("Color Litología 1");
            etColor1.setEms(10);
            etColor1.setTag("color1");
            ListaEditText.get(idLinear).add(etColor1);
            liForm.addView(etColor1);

            //------------> Color 2

            TextView tvColor2 = new TextView(mcont);
            tvColor2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvColor2.setText("Color Litología 2");
            tvColor2.setTextAppearance(R.style.TituloItem);
            tvColor2.setPadding(0, mtop, 0, 0);
            liForm.addView(tvColor2);

            EditText etColor2 = new EditText(mcont);
            etColor2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etColor2.setHint("Color Litología 2");
            etColor2.setEms(10);
            etColor2.setTag("color2");
            ListaEditText.get(idLinear).add(etColor2);
            liForm.addView(etColor2);

            //------------> Composición Mineralogica 1

            TextView tvComposicionMineral1 = new TextView(mcont);
            tvComposicionMineral1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvComposicionMineral1.setText("Composición Mineralógica (Macro) Litología 1");
            tvComposicionMineral1.setTextAppearance(R.style.TituloItem);
            tvComposicionMineral1.setPadding(0, mtop, 0, 0);
            liForm.addView(tvComposicionMineral1);

            EditText etComposicionMineral1 = new EditText(mcont);
            etComposicionMineral1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etComposicionMineral1.setHint("Composición Mineralógica (Macro) Litología 1");
            etComposicionMineral1.setEms(10);
            etComposicionMineral1.setTag("composicionmineral1");
            ListaEditText.get(idLinear).add(etComposicionMineral1);
            liForm.addView(etComposicionMineral1);

            //------------> Composición Mineralogica 2

            TextView tvComposicionMineral2 = new TextView(mcont);
            tvComposicionMineral2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvComposicionMineral2.setText("Composición Mineralógica (Macro) Litología 2");
            tvComposicionMineral2.setTextAppearance(R.style.TituloItem);
            tvComposicionMineral2.setPadding(0, mtop, 0, 0);
            liForm.addView(tvComposicionMineral2);

            EditText etComposicionMineral2 = new EditText(mcont);
            etComposicionMineral2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etComposicionMineral2.setHint("Composición Mineralógica (Macro) Litología 2");
            etComposicionMineral2.setEms(10);
            etComposicionMineral2.setTag("composicionmineral2");
            ListaEditText.get(idLinear).add(etComposicionMineral2);
            liForm.addView(etComposicionMineral2);


            //------------> LEVANTAMIENTO DE DISCONTINUIDADES

            TextView tvLevantamientoDisc = new TextView(mcont);
            tvLevantamientoDisc.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvLevantamientoDisc.setText("LEVANTAMIENTO DE DISCONTINUIDADES");
            tvLevantamientoDisc.setTextAppearance(R.style.TituloFormato);
            tvLevantamientoDisc.setPadding(0, mtop, 0, 20);
            liForm.addView(tvLevantamientoDisc);

            liFormDiscontinuidades = new LinearLayout(mcont);
            liFormDiscontinuidades.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormDiscontinuidades.setOrientation(LinearLayout.VERTICAL);
            liForm.addView(liFormDiscontinuidades);

            Button bAnadirDiscont = new Button(mcont);
            bAnadirDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bAnadirDiscont.setText("Añadir Discontinuidad");
            bAnadirDiscont.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.plus_circle, 0);
            bAnadirDiscont.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listContDiscontinuidades.set(idLinear, listContDiscontinuidades.get(idLinear) + 1);

                    Button bDiscont = new Button(mcont);
                    bDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    bDiscont.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                    bDiscont.setText("Discontinuidad "+ listContDiscontinuidades.get(idLinear));
                    bDiscont.setTag(idLinear);
                    liFormDiscontinuidades.addView(bDiscont);


                    LinearLayout liDiscontinuidades = new LinearLayout(mcont);
                    liDiscontinuidades.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liDiscontinuidades.setOrientation(LinearLayout.VERTICAL);
                    liDiscontinuidades.setBackgroundColor(0x22222200);
                    liDiscontinuidades.setVisibility(View.GONE);
                    liFormDiscontinuidades.addView(liDiscontinuidades);
                    listDiscontinuidades.add(liDiscontinuidades);

                    bDiscont.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (liDiscontinuidades.getVisibility() == View.VISIBLE) {
                                ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                animation.setDuration(220);
                                animation.setFillAfter(true);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                    }
                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        liDiscontinuidades.setVisibility(View.GONE);
                                        bDiscont.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                                    }
                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                    }
                                });
                                liDiscontinuidades.startAnimation(animation);

                            }
                            else {
                                ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                animation.setDuration(220);
                                animation.setFillAfter(true);
                                liDiscontinuidades.startAnimation(animation);
                                liDiscontinuidades.setVisibility(View.VISIBLE);
                                bDiscont.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                            }

                        }
                    });


                    TextView tvNameDiscont = new TextView(mcont);
                    tvNameDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvNameDiscont.setText("Discontinuidad "+ listContDiscontinuidades.get(idLinear));
                    tvNameDiscont.setTextAppearance(R.style.TituloFormato);
                    tvNameDiscont.setPadding(0, 100, 0, 50);
                    liDiscontinuidades.addView(tvNameDiscont);


                    //------------> Tipo

                    TextView tvTipoDiscont = new TextView(mcont);
                    tvTipoDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvTipoDiscont.setText("Tipo");
                    tvTipoDiscont.setTextAppearance(R.style.TituloItem);
                    tvTipoDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvTipoDiscont);

                    Spinner sTipoDiscont = new Spinner(mcont);
                    sTipoDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mcont, R.array.TipoDiscont, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sTipoDiscont.setAdapter(adapter);
                    sTipoDiscont.setTag("TipoDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaSpinner.get(idLinear).add(sTipoDiscont);
                    liDiscontinuidades.addView(sTipoDiscont);


                    //------------> Dir Buzamiento

                    TextView tvDirBuzamiento = new TextView(mcont);
                    tvDirBuzamiento.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvDirBuzamiento.setText("Dir. Buzamiento (Az. Bz.) ");
                    tvDirBuzamiento.setTextAppearance(R.style.TituloItem);
                    tvDirBuzamiento.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvDirBuzamiento);

                    EditText etDirBuzamiento = new EditText(mcont);
                    etDirBuzamiento.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etDirBuzamiento.setHint("Dir. Buzamiento (Az. Bz.) ");
                    etDirBuzamiento.setEms(10);
                    etDirBuzamiento.setTag("DirBuzamiento"+ listContDiscontinuidades.get(idLinear));
                    ListaEditText.get(idLinear).add(etDirBuzamiento);
                    liDiscontinuidades.addView(etDirBuzamiento);

                    //------------> Buzamiento

                    TextView tvBuzamiento = new TextView(mcont);
                    tvBuzamiento.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvBuzamiento.setText("Buzamiento (Bz.)");
                    tvBuzamiento.setTextAppearance(R.style.TituloItem);
                    tvBuzamiento.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvBuzamiento);

                    EditText etBuzamiento = new EditText(mcont);
                    etBuzamiento.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etBuzamiento.setHint("Buzamiento (Bz.)");
                    etBuzamiento.setEms(10);
                    etBuzamiento.setTag("Buzamiento"+ listContDiscontinuidades.get(idLinear));
                    ListaEditText.get(idLinear).add(etBuzamiento);
                    liDiscontinuidades.addView(etBuzamiento);


                    //------------> Persistencia

                    TextView tvPersistenciaDiscont = new TextView(mcont);
                    tvPersistenciaDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvPersistenciaDiscont.setText("Persistencia");
                    tvPersistenciaDiscont.setTextAppearance(R.style.TituloItem);
                    tvPersistenciaDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvPersistenciaDiscont);

                    Spinner sPersistenciaDiscont = new Spinner(mcont);
                    sPersistenciaDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(mcont, R.array.PersistenciaDiscont, android.R.layout.simple_spinner_item);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sPersistenciaDiscont.setAdapter(adapter1);
                    sPersistenciaDiscont.setTag("PersistenciaDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaSpinner.get(idLinear).add(sPersistenciaDiscont);
                    liDiscontinuidades.addView(sPersistenciaDiscont);

                    //------------> Ancho de Abertura

                    TextView tvAnchoAberDiscont = new TextView(mcont);
                    tvAnchoAberDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvAnchoAberDiscont.setText("Ancho de Abertura");
                    tvAnchoAberDiscont.setTextAppearance(R.style.TituloItem);
                    tvAnchoAberDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvAnchoAberDiscont);

                    Spinner sAnchoAberDiscont = new Spinner(mcont);
                    sAnchoAberDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(mcont, R.array.AnchoAberturaDiscont, android.R.layout.simple_spinner_item);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sAnchoAberDiscont.setAdapter(adapter2);
                    sAnchoAberDiscont.setTag("AnchoAberDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaSpinner.get(idLinear).add(sAnchoAberDiscont);
                    liDiscontinuidades.addView(sAnchoAberDiscont);

                    //------------> Tipo de Relleno

                    TextView tvTipoRellenoDiscont = new TextView(mcont);
                    tvTipoRellenoDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvTipoRellenoDiscont.setText("Tipo de Relleno");
                    tvTipoRellenoDiscont.setTextAppearance(R.style.TituloItem);
                    tvTipoRellenoDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvTipoRellenoDiscont);

                    Spinner sTipoRellenoDiscont = new Spinner(mcont);
                    sTipoRellenoDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(mcont, R.array.TipoRellenoDiscont, android.R.layout.simple_spinner_item);
                    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sTipoRellenoDiscont.setAdapter(adapter3);
                    sTipoRellenoDiscont.setTag("TipoRellenoDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaSpinner.get(idLinear).add(sTipoRellenoDiscont);
                    liDiscontinuidades.addView(sTipoRellenoDiscont);

                    //------------> Rugosidad de la Superficie

                    TextView tvRugosidadSuperDiscont = new TextView(mcont);
                    tvRugosidadSuperDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvRugosidadSuperDiscont.setText("Rugosidad de la Superficie");
                    tvRugosidadSuperDiscont.setTextAppearance(R.style.TituloItem);
                    tvRugosidadSuperDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvRugosidadSuperDiscont);

                    Spinner sRugosidadSuperDiscont = new Spinner(mcont);
                    sRugosidadSuperDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(mcont, R.array.RugosidadDiscont, android.R.layout.simple_spinner_item);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sRugosidadSuperDiscont.setAdapter(adapter4);
                    sRugosidadSuperDiscont.setTag("RugosidadSuperDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaSpinner.get(idLinear).add(sRugosidadSuperDiscont);
                    liDiscontinuidades.addView(sRugosidadSuperDiscont);

                    //------------> Forma de la Superficie

                    TextView tvFormaSuperDiscont = new TextView(mcont);
                    tvFormaSuperDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvFormaSuperDiscont.setText("Forma de la Superficie");
                    tvFormaSuperDiscont.setTextAppearance(R.style.TituloItem);
                    tvFormaSuperDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvFormaSuperDiscont);

                    Spinner sFormaSuperDiscont = new Spinner(mcont);
                    sFormaSuperDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(mcont, R.array.FormaSuperficieDiscont, android.R.layout.simple_spinner_item);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sFormaSuperDiscont.setAdapter(adapter5);
                    sFormaSuperDiscont.setTag("FormaSuperDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaSpinner.get(idLinear).add(sFormaSuperDiscont);
                    liDiscontinuidades.addView(sFormaSuperDiscont);

                    //------------> Humedad en Diaclasas

                    TextView tvHumedadDiscont = new TextView(mcont);
                    tvHumedadDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvHumedadDiscont.setText("Humedad en Diaclasas");
                    tvHumedadDiscont.setTextAppearance(R.style.TituloItem);
                    tvHumedadDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvHumedadDiscont);

                    Spinner sHumedadDiscont = new Spinner(mcont);
                    sHumedadDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter6 = ArrayAdapter.createFromResource(mcont, R.array.HumedadDiaclasasDiscont, android.R.layout.simple_spinner_item);
                    adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sHumedadDiscont.setAdapter(adapter6);
                    sHumedadDiscont.setTag("HumedadDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaSpinner.get(idLinear).add(sHumedadDiscont);
                    liDiscontinuidades.addView(sHumedadDiscont);

                    //------------> Espaciamiento

                    TextView tvEspaciamientoDiscont = new TextView(mcont);
                    tvEspaciamientoDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvEspaciamientoDiscont.setText("Espaciamiento");
                    tvEspaciamientoDiscont.setTextAppearance(R.style.TituloItem);
                    tvEspaciamientoDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvEspaciamientoDiscont);

                    Spinner sEspaciamientoDiscont = new Spinner(mcont);
                    sEspaciamientoDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter7 = ArrayAdapter.createFromResource(mcont, R.array.EspaciamientoDiscont, android.R.layout.simple_spinner_item);
                    adapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sEspaciamientoDiscont.setAdapter(adapter7);
                    sEspaciamientoDiscont.setTag("EspaciamientoDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaSpinner.get(idLinear).add(sEspaciamientoDiscont);
                    liDiscontinuidades.addView(sEspaciamientoDiscont);

                    //------------> Meteorización

                    TextView tvMeteorizacionDiscont = new TextView(mcont);
                    tvMeteorizacionDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvMeteorizacionDiscont.setText("Meteorizacion");
                    tvMeteorizacionDiscont.setTextAppearance(R.style.TituloItem);
                    tvMeteorizacionDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvMeteorizacionDiscont);

                    Spinner sMeteorizacionDiscont = new Spinner(mcont);
                    sMeteorizacionDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter8 = ArrayAdapter.createFromResource(mcont, R.array.MeteorizacionDiscont, android.R.layout.simple_spinner_item);
                    adapter8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sMeteorizacionDiscont.setAdapter(adapter8);
                    sMeteorizacionDiscont.setTag("MeteorizacionDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaSpinner.get(idLinear).add(sMeteorizacionDiscont);
                    liDiscontinuidades.addView(sMeteorizacionDiscont);

                    //------------> Rake/Pitch

                    TextView tvRakePitch = new TextView(mcont);
                    tvRakePitch.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvRakePitch.setText("Rake/Pitch");
                    tvRakePitch.setTextAppearance(R.style.TituloItem);
                    tvRakePitch.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvRakePitch);

                    EditText etRakePitch = new EditText(mcont);
                    etRakePitch.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etRakePitch.setHint("Rake/Pitch");
                    etRakePitch.setEms(10);
                    etRakePitch.setTag("RakePitch"+ listContDiscontinuidades.get(idLinear));
                    ListaEditText.get(idLinear).add(etRakePitch);
                    liDiscontinuidades.addView(etRakePitch);

                    //------------> Dir. del Rake/Pitch

                    TextView tvDirRakePitch = new TextView(mcont);
                    tvDirRakePitch.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvDirRakePitch.setText("Dir. del Rake/Pitch");
                    tvDirRakePitch.setTextAppearance(R.style.TituloItem);
                    tvDirRakePitch.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvDirRakePitch);

                    EditText etDirRakePitch = new EditText(mcont);
                    etDirRakePitch.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etDirRakePitch.setHint("Dir. del Rake/Pitch");
                    etDirRakePitch.setEms(10);
                    etDirRakePitch.setTag("DirRakePitch"+ listContDiscontinuidades.get(idLinear));
                    ListaEditText.get(idLinear).add(etDirRakePitch);
                    liDiscontinuidades.addView(etDirRakePitch);

                    //------------> Orientación talud/ladera

                    TextView tvOrientacion = new TextView(mcont);
                    tvOrientacion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvOrientacion.setText("Orientación talud/ladera");
                    tvOrientacion.setTextAppearance(R.style.TituloItem);
                    tvOrientacion.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvOrientacion);

                    //------------> Az Bz/Bz 1

                    TextView tvAzBzBz1 = new TextView(mcont);
                    tvAzBzBz1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvAzBzBz1.setText("Az Bz/Bz");
                    tvAzBzBz1.setTextAppearance(R.style.TituloItem);
                    tvAzBzBz1.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvAzBzBz1);

                    EditText etAzBzBz1 = new EditText(mcont);
                    etAzBzBz1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etAzBzBz1.setHint("Az Bz/Bz");
                    etAzBzBz1.setEms(10);
                    etAzBzBz1.setTag("AzBzBz1"+ listContDiscontinuidades.get(idLinear));
                    ListaEditText.get(idLinear).add(etAzBzBz1);
                    liDiscontinuidades.addView(etAzBzBz1);

                    //------------> Az Bz/Bz 2

                    TextView tvAzBzBz2 = new TextView(mcont);
                    tvAzBzBz2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvAzBzBz2.setText("Az Bz/Bz");
                    tvAzBzBz2.setTextAppearance(R.style.TituloItem);
                    tvAzBzBz2.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvAzBzBz2);

                    EditText etAzBzBz2 = new EditText(mcont);
                    etAzBzBz2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etAzBzBz2.setHint("Az Bz/Bz");
                    etAzBzBz2.setEms(10);
                    etAzBzBz2.setTag("AzBzBz2"+ listContDiscontinuidades.get(idLinear));
                    ListaEditText.get(idLinear).add(etAzBzBz2);
                    liDiscontinuidades.addView(etAzBzBz2);

                    //------------> Altura

                    TextView tvAltura = new TextView(mcont);
                    tvAltura.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvAltura.setText("Altura");
                    tvAltura.setTextAppearance(R.style.TituloItem);
                    tvAltura.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvAltura);

                    EditText etAltura = new EditText(mcont);
                    etAltura.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etAltura.setHint("Altura");
                    etAltura.setEms(10);
                    etAltura.setTag("AlturaDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaEditText.get(idLinear).add(etAltura);
                    liDiscontinuidades.addView(etAltura);

                    //------------> Observaciones

                    TextView tvObservacionesDiscont = new TextView(mcont);
                    tvObservacionesDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvObservacionesDiscont.setText("Observaciones");
                    tvObservacionesDiscont.setTextAppearance(R.style.TituloItem);
                    tvObservacionesDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvObservacionesDiscont);

                    EditText etObservacionesDiscont = new EditText(mcont);
                    etObservacionesDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etObservacionesDiscont.setHint("Observaciones");
                    etObservacionesDiscont.setEms(10);
                    etObservacionesDiscont.setTag("ObservacionesDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaEditText.get(idLinear).add(etObservacionesDiscont);
                    liDiscontinuidades.addView(etObservacionesDiscont);

                }
            });
            liForm.addView(bAnadirDiscont);

            //------------> Fotografías Anexas

            TextView tvFotosAnexas = new TextView(mcont);
            tvFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvFotosAnexas.setText("Fotografías Anexas");
            tvFotosAnexas.setTextAppearance(R.style.TituloFormato);
            tvFotosAnexas.setPadding(0, mtop, 0, 20);
            liForm.addView(tvFotosAnexas);

            liFormFotosAnexas = new LinearLayout(mcont);
            liFormFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormFotosAnexas.setOrientation(LinearLayout.VERTICAL);
            liForm.addView(liFormFotosAnexas);


            Button bFotosAnexas = new Button(mcont);
            bFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bFotosAnexas.setText("Añadir Foto");
            bFotosAnexas.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.plus_circle, 0);
            bFotosAnexas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listContFotosAnexas.set(idLinear, listContFotosAnexas.get(idLinear) + 1);

                    Button bFotosAnexasAcordion = new Button(mcont);
                    bFotosAnexasAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    bFotosAnexasAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                    bFotosAnexasAcordion.setText("Foto "+ listContFotosAnexas.get(idLinear));
                    bFotosAnexasAcordion.setTag(idLinear);
                    liFormFotosAnexas.addView(bFotosAnexasAcordion);

                    LinearLayout liFotosAnexas = new LinearLayout(mcont);
                    liFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liFotosAnexas.setOrientation(LinearLayout.VERTICAL);
                    liFotosAnexas.setBackgroundColor(0x22222200);
                    liFotosAnexas.setVisibility(View.GONE);
                    liFormFotosAnexas.addView(liFotosAnexas);
                    listFotosAnexas.add(liFotosAnexas);

                    bFotosAnexasAcordion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (liFotosAnexas.getVisibility() == View.VISIBLE) {
                                ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                animation.setDuration(220);
                                animation.setFillAfter(true);
                                animation.setAnimationListener(new Animation.AnimationListener() {
                                    @Override
                                    public void onAnimationStart(Animation animation) {
                                    }
                                    @Override
                                    public void onAnimationEnd(Animation animation) {
                                        liFotosAnexas.setVisibility(View.GONE);
                                        bFotosAnexasAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                                    }
                                    @Override
                                    public void onAnimationRepeat(Animation animation) {
                                    }
                                });
                                liFotosAnexas.startAnimation(animation);

                            }
                            else {
                                ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                animation.setDuration(220);
                                animation.setFillAfter(true);
                                liFotosAnexas.startAnimation(animation);
                                liFotosAnexas.setVisibility(View.VISIBLE);
                                bFotosAnexasAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                            }

                        }
                    });

                    TextView tvNameFotos = new TextView(mcont);
                    tvNameFotos.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvNameFotos.setText("Foto "+ listContFotosAnexas.get(idLinear));
                    tvNameFotos.setTextAppearance(R.style.TituloFormato);
                    tvNameFotos.setPadding(0, 100, 0, 50);
                    liFotosAnexas.addView(tvNameFotos);


                    //------------> Nombre Foto

                    TextView tvNombreFotosAnexas = new TextView(mcont);
                    tvNombreFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvNombreFotosAnexas.setText("Nombre de la Foto");
                    tvNombreFotosAnexas.setTextAppearance(R.style.TituloItem);
                    tvNombreFotosAnexas.setPadding(0, 70, 0, 0);
                    liFotosAnexas.addView(tvNombreFotosAnexas);

                    EditText etNombreFotosAnexas = new EditText(mcont);
                    etNombreFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etNombreFotosAnexas.setHint("Nombre de la Foto");
                    etNombreFotosAnexas.setEms(10);
                    etNombreFotosAnexas.setTag("NombreFotosAnexas"+listContFotosAnexas.get(idLinear));
                    ListaEditText.get(idLinear).add(etNombreFotosAnexas);
                    liFotosAnexas.addView(etNombreFotosAnexas);

                    //------------> Descripción

                    TextView tvDescriFotosAnexas = new TextView(mcont);
                    tvDescriFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvDescriFotosAnexas.setText("Descripción de la Foto");
                    tvDescriFotosAnexas.setTextAppearance(R.style.TituloItem);
                    tvDescriFotosAnexas.setPadding(0, 70, 0, 0);
                    liFotosAnexas.addView(tvDescriFotosAnexas);

                    EditText etDescriFotosAnexas = new EditText(mcont);
                    etDescriFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etDescriFotosAnexas.setHint("Descripción de la Foto");
                    etDescriFotosAnexas.setEms(10);
                    etDescriFotosAnexas.setTag("DescriFotosAnexas"+listContFotosAnexas.get(idLinear));
                    ListaEditText.get(idLinear).add(etDescriFotosAnexas);
                    liFotosAnexas.addView(etDescriFotosAnexas);
                }
            });
            liForm.addView(bFotosAnexas);


            listLiForm.add(liForm);
            liFormularios.addView(liForm);
        }

        if (formType.equals("UGS Suelos")) {

            Button bAcordion = new Button(mcont);
            bAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
            bAcordion.setText("Formato UGS Suelos");
            bAcordion.setTag(idLinear);
            listBtnAcordion.add(bAcordion);
            liFormularios.addView(bAcordion);

            LinearLayout liForm = new LinearLayout(mcont);
            liForm.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liForm.setOrientation(LinearLayout.VERTICAL);
            liForm.setBackgroundColor(0x33333300);
            liForm.setVisibility(View.GONE);

            bAcordion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (liForm.getVisibility() == View.VISIBLE) {
                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                        animation.setDuration(220);
                        animation.setFillAfter(true);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                liForm.setVisibility(View.GONE);
                                bAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                            }
                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        liForm.startAnimation(animation);

                    }
                    else {
                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                        animation.setDuration(220);
                        animation.setFillAfter(true);
                        liForm.startAnimation(animation);
                        liForm.setVisibility(View.VISIBLE);
                        bAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                    }

                }
            });



            //------------> Titulo del Formato

            TextView tvTitulo = new TextView(mcont);
            tvTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvTitulo.setText("Formato UGSS");
            tvTitulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvTitulo.setTextAppearance(R.style.TituloFormato);
            tvTitulo.setPadding(0, 70, 0, 70);
            liForm.addView(tvTitulo);

            Button bBorrarForm = new Button(mcont);
            bBorrarForm.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bBorrarForm.setText("Borrar Este Formulario");
            bBorrarForm.setTag(idLinear);
            bBorrarForm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listLiForm.get(Integer.parseInt(v.getTag().toString())).removeAllViews();
                    liFormularios.removeView(listBtnAcordion.get(Integer.parseInt(v.getTag().toString())));
                    listFormularios.set(Integer.parseInt(v.getTag().toString()), "Ninguno");
                }
            });
            liForm.addView(bBorrarForm);

            //------------> Numero de Formato

            TextView tvNoFormato = new TextView(mcont);
            tvNoFormato.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvNoFormato.setText("Número Formato");
            tvNoFormato.setTextAppearance(R.style.TituloItem);
            tvNoFormato.setPadding(0, mtop, 0, 0);
            liForm.addView(tvNoFormato);

            EditText etNoFormato = new EditText(mcont);
            etNoFormato.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etNoFormato.setHint("Número Formato");
            etNoFormato.setEms(10);
            etNoFormato.setTag("noformato");
            ListaEditText.get(idLinear).add(etNoFormato);
            liForm.addView(etNoFormato);

            //------------> Municipios

            TextView tvMunicipios = new TextView(mcont);
            tvMunicipios.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvMunicipios.setText("Municipio");
            tvMunicipios.setTextAppearance(R.style.TituloItem);
            tvMunicipios.setPadding(0, mtop, 0, 0);
            liForm.addView(tvMunicipios);

            Spinner sMunicipios = new Spinner(mcont);
            sMunicipios.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mcont, R.array.Municipios, android.R.layout.simple_spinner_item);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sMunicipios.setAdapter(adapter);
            sMunicipios.setTag("municipios");
            ListaSpinner.get(idLinear).add(sMunicipios);
            liForm.addView(sMunicipios);

            //------------> Vereda

            TextView tvVereda = new TextView(mcont);
            tvVereda.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvVereda.setText("Vereda");
            tvVereda.setTextAppearance(R.style.TituloItem);
            tvVereda.setPadding(0, mtop, 0, 0);
            liForm.addView(tvVereda);

            EditText etVereda = new EditText(mcont);
            etVereda.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etVereda.setHint("Vereda");
            etVereda.setEms(10);
            etVereda.setTag("vereda");
            ListaEditText.get(idLinear).add(etVereda);
            liForm.addView(etVereda);

//            //------------> Nombre Formato
//
//            TextView tvNombreFormato = new TextView(mcont);
//            tvNombreFormato.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            tvNombreFormato.setText("Nombre del Formato");
//            tvNombreFormato.setTextAppearance(R.style.TituloItem);
//            tvNombreFormato.setPadding(0, mtop, 0, 0);
//            liForm.addView(tvNombreFormato);
//
//            EditText etNombreFormato = new EditText(mcont);
//            etNombreFormato.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//            etNombreFormato.setHint("Nombre del Formato");
//            etNombreFormato.setEms(10);
//            etNombreFormato.setTag("nombreformato");
//            ListaEditText.get(idLinear).add(etNombreFormato);
//            liForm.addView(etNombreFormato);

            //------------> Numero de la Estación

            TextView tvNoEstacion = new TextView(mcont);
            tvNoEstacion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvNoEstacion.setText("Número de la Estación");
            tvNoEstacion.setTextAppearance(R.style.TituloItem);
            tvNoEstacion.setPadding(0, mtop, 0, 0);
            liForm.addView(tvNoEstacion);

            EditText etNoEstacion = new EditText(mcont);
            etNoEstacion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etNoEstacion.setHint("Número de la Estación");
            etNoEstacion.setEms(10);
            etNoEstacion.setTag("noestacion");
            ListaEditText.get(idLinear).add(etNoEstacion);
            liForm.addView(etNoEstacion);

            //------------> Clase de Afloramiento

            TextView tvClaseAflor = new TextView(mcont);
            tvClaseAflor.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvClaseAflor.setText("Clase Afloramiento");
            tvClaseAflor.setTextAppearance(R.style.TituloItem);
            tvClaseAflor.setPadding(0, mtop, 0, 0);
            liForm.addView(tvClaseAflor);

            Spinner sClaseAflor = new Spinner(mcont);
            sClaseAflor.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(mcont, R.array.ClaseAfloramiento, android.R.layout.simple_spinner_item);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sClaseAflor.setAdapter(adapter1);
            sClaseAflor.setTag("claseaflor");
            ListaSpinner.get(idLinear).add(sClaseAflor);
            liForm.addView(sClaseAflor);

            //------------> Secuencia Estratigráfica

            TextView tvSecuenciaEstrati = new TextView(mcont);
            tvSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvSecuenciaEstrati.setText("Secuencia Estratigráfica");
            tvSecuenciaEstrati.setTextAppearance(R.style.TituloFormato);
            tvSecuenciaEstrati.setPadding(0, mtop, 0, 0);
            liForm.addView(tvSecuenciaEstrati);

            int secuEstratiWidth = 420;
            int secuEstratiOrdenWidth = 200;
            int secuEstratiEspesorWidth = 300;

            //------------------->  Suelo Antrópico

            LinearLayout liFormSecuenciaEstrati1 = new LinearLayout(mcont);
            liFormSecuenciaEstrati1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormSecuenciaEstrati1.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvSecuenciaEstratiOpt1 = new TextView(mcont);
            tvSecuenciaEstratiOpt1.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvSecuenciaEstratiOpt1.setText("Suelo Antrópico: ");
            tvSecuenciaEstratiOpt1.setTextAppearance(R.style.TituloItem);
            liFormSecuenciaEstrati1.addView(tvSecuenciaEstratiOpt1);

            EditText etSecuenciaEstratiOpt1Orden = new EditText(mcont);
            etSecuenciaEstratiOpt1Orden.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etSecuenciaEstratiOpt1Orden.setHint("Orden");
            etSecuenciaEstratiOpt1Orden.setEms(10);
            etSecuenciaEstratiOpt1Orden.setTag("secuenciaestratiopt1orden");
            ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt1Orden);
            liFormSecuenciaEstrati1.addView(etSecuenciaEstratiOpt1Orden);

            EditText etSecuenciaEstratiOpt1Espesor = new EditText(mcont);
            etSecuenciaEstratiOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etSecuenciaEstratiOpt1Espesor.setHint("Espesor (m)");
            etSecuenciaEstratiOpt1Espesor.setEms(10);
            etSecuenciaEstratiOpt1Espesor.setTag("secuenciaestratiopt1espesor");
            ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt1Espesor);
            liFormSecuenciaEstrati1.addView(etSecuenciaEstratiOpt1Espesor);

            liForm.addView(liFormSecuenciaEstrati1);

            //-------------------> Suelo Residual

            LinearLayout liFormSecuenciaEstrati2 = new LinearLayout(mcont);
            liFormSecuenciaEstrati2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormSecuenciaEstrati2.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvSecuenciaEstratiOpt2 = new TextView(mcont);
            tvSecuenciaEstratiOpt2.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvSecuenciaEstratiOpt2.setText("Suelo Residual: ");
            tvSecuenciaEstratiOpt2.setTextAppearance(R.style.TituloItem);
            liFormSecuenciaEstrati2.addView(tvSecuenciaEstratiOpt2);

            EditText etSecuenciaEstratiOpt2Orden = new EditText(mcont);
            etSecuenciaEstratiOpt2Orden.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etSecuenciaEstratiOpt2Orden.setHint("Orden");
            etSecuenciaEstratiOpt2Orden.setEms(10);
            etSecuenciaEstratiOpt2Orden.setTag("secuenciaestratiopt2orden");
            ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt2Orden);
            liFormSecuenciaEstrati2.addView(etSecuenciaEstratiOpt2Orden);

            EditText etSecuenciaEstratiOpt2Espesor = new EditText(mcont);
            etSecuenciaEstratiOpt2Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etSecuenciaEstratiOpt2Espesor.setHint("Espesor (m)");
            etSecuenciaEstratiOpt2Espesor.setEms(10);
            etSecuenciaEstratiOpt2Espesor.setTag("secuenciaestratiopt2espesor");
            ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt2Espesor);
            liFormSecuenciaEstrati2.addView(etSecuenciaEstratiOpt2Espesor);

            liForm.addView(liFormSecuenciaEstrati2);


            //-------------------> Suelo Transportado

            LinearLayout liFormSecuenciaEstrati3 = new LinearLayout(mcont);
            liFormSecuenciaEstrati3.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormSecuenciaEstrati3.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvSecuenciaEstratiOpt3 = new TextView(mcont);
            tvSecuenciaEstratiOpt3.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvSecuenciaEstratiOpt3.setText("Suelo Transportado: ");
            tvSecuenciaEstratiOpt3.setTextAppearance(R.style.TituloItem);
            liFormSecuenciaEstrati3.addView(tvSecuenciaEstratiOpt3);

            EditText etSecuenciaEstratiOpt3Orden = new EditText(mcont);
            etSecuenciaEstratiOpt3Orden.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etSecuenciaEstratiOpt3Orden.setHint("Orden");
            etSecuenciaEstratiOpt3Orden.setEms(10);
            etSecuenciaEstratiOpt3Orden.setTag("secuenciaestratiopt3orden");
            ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt3Orden);
            liFormSecuenciaEstrati3.addView(etSecuenciaEstratiOpt3Orden);

            EditText etSecuenciaEstratiOpt3Espesor = new EditText(mcont);
            etSecuenciaEstratiOpt3Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etSecuenciaEstratiOpt3Espesor.setHint("Espesor (m)");
            etSecuenciaEstratiOpt3Espesor.setEms(10);
            etSecuenciaEstratiOpt3Espesor.setTag("secuenciaestratiopt3espesor");
            ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt3Espesor);
            liFormSecuenciaEstrati3.addView(etSecuenciaEstratiOpt3Espesor);

            liForm.addView(liFormSecuenciaEstrati3);



            //-------------------> Listener para saber si se trata de un suelo residual

            LinearLayout liFormSecuenciaEstratiSueloR = new LinearLayout(mcont);
            liFormSecuenciaEstratiSueloR.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormSecuenciaEstratiSueloR.setOrientation(LinearLayout.VERTICAL);

            liForm.addView(liFormSecuenciaEstratiSueloR);

            etSecuenciaEstratiOpt2Orden.addTextChangedListener(new TextWatcher() {
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
                    elNuevoTexto = elNuevoTexto.replace(" ","");

                    //-------------------> Si es Suelo Residual

                    if (!elNuevoTexto.equals("")){

                        int secuEstratiWidth = 450;
                        int secuEstratiOrdenWidth = 200;
                        int secuEstratiEspesorWidth = 300;

                        TextView tvSecuenciaEstratiHorizonte1 = new TextView(mcont);
                        tvSecuenciaEstratiHorizonte1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvSecuenciaEstratiHorizonte1.setText("Si es Suelo Residual: ");
                        tvSecuenciaEstratiHorizonte1.setTextAppearance(R.style.TituloItem);
                        tvSecuenciaEstratiHorizonte1.setPadding(0, mtop, 0, 0);
                        liFormSecuenciaEstratiSueloR.addView(tvSecuenciaEstratiHorizonte1);

                        TextView tvSecuenciaEstratiHorizonte2 = new TextView(mcont);
                        tvSecuenciaEstratiHorizonte2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvSecuenciaEstratiHorizonte2.setText("*F.R.:Fragmentos de roca sin meteorizar");
                        tvSecuenciaEstratiHorizonte2.setTextAppearance(R.style.TituloItem);
                        liFormSecuenciaEstratiSueloR.addView(tvSecuenciaEstratiHorizonte2);

                        //-------------------> VI (Suelo Residual <10% F.R.*)

                        LinearLayout liFormSecuenciaEstratiSueloR1 = new LinearLayout(mcont);
                        liFormSecuenciaEstratiSueloR1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormSecuenciaEstratiSueloR1.setOrientation(LinearLayout.HORIZONTAL);

                        TextView tvSecuenciaEstratiSueloR1 = new TextView(mcont);
                        tvSecuenciaEstratiSueloR1.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvSecuenciaEstratiSueloR1.setText("VI (Suelo Residual <10% F.R.*): ");
                        tvSecuenciaEstratiSueloR1.setTextAppearance(R.style.TituloItem);
                        liFormSecuenciaEstratiSueloR1.addView(tvSecuenciaEstratiSueloR1);

                        EditText etSecuenciaEstratiSueloR1Orden = new EditText(mcont);
                        etSecuenciaEstratiSueloR1Orden.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiSueloR1Orden.setHint("Orden");
                        etSecuenciaEstratiSueloR1Orden.setEms(10);
                        etSecuenciaEstratiSueloR1Orden.setTag("secuenciaestratisuelor1orden");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiSueloR1Orden);
                        liFormSecuenciaEstratiSueloR1.addView(etSecuenciaEstratiSueloR1Orden);

                        EditText etSecuenciaEstratiSueloR1Espesor = new EditText(mcont);
                        etSecuenciaEstratiSueloR1Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiSueloR1Espesor.setHint("Espesor (m)");
                        etSecuenciaEstratiSueloR1Espesor.setEms(10);
                        etSecuenciaEstratiSueloR1Espesor.setTag("secuenciaestratisuelor1espesor");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiSueloR1Espesor);
                        liFormSecuenciaEstratiSueloR1.addView(etSecuenciaEstratiSueloR1Espesor);

                        liFormSecuenciaEstratiSueloR.addView(liFormSecuenciaEstratiSueloR1);

                        //-------------------> V (Saprolito Fino 10%-35% F.R.*)

                        LinearLayout liFormSecuenciaEstratiSueloR2 = new LinearLayout(mcont);
                        liFormSecuenciaEstratiSueloR2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormSecuenciaEstratiSueloR2.setOrientation(LinearLayout.HORIZONTAL);

                        TextView tvSecuenciaEstratiSueloR2 = new TextView(mcont);
                        tvSecuenciaEstratiSueloR2.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvSecuenciaEstratiSueloR2.setText("V (Saprolito Fino 10%-35% F.R.*): ");
                        tvSecuenciaEstratiSueloR2.setTextAppearance(R.style.TituloItem);
                        liFormSecuenciaEstratiSueloR2.addView(tvSecuenciaEstratiSueloR2);

                        EditText etSecuenciaEstratiSueloR2Orden = new EditText(mcont);
                        etSecuenciaEstratiSueloR2Orden.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiSueloR2Orden.setHint("Orden");
                        etSecuenciaEstratiSueloR2Orden.setEms(10);
                        etSecuenciaEstratiSueloR2Orden.setTag("secuenciaestratisuelor2orden");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiSueloR2Orden);
                        liFormSecuenciaEstratiSueloR2.addView(etSecuenciaEstratiSueloR2Orden);

                        EditText etSecuenciaEstratiSueloR2Espesor = new EditText(mcont);
                        etSecuenciaEstratiSueloR2Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiSueloR2Espesor.setHint("Espesor (m)");
                        etSecuenciaEstratiSueloR2Espesor.setEms(10);
                        etSecuenciaEstratiSueloR2Espesor.setTag("secuenciaestratisuelor2espesor");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiSueloR2Espesor);
                        liFormSecuenciaEstratiSueloR2.addView(etSecuenciaEstratiSueloR2Espesor);

                        liFormSecuenciaEstratiSueloR.addView(liFormSecuenciaEstratiSueloR2);

                        //-------------------> IV (Saprolito Grueso 35%-70% F.R.*)

                        LinearLayout liFormSecuenciaEstratiSueloR3 = new LinearLayout(mcont);
                        liFormSecuenciaEstratiSueloR3.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormSecuenciaEstratiSueloR3.setOrientation(LinearLayout.HORIZONTAL);

                        TextView tvSecuenciaEstratiSueloR3 = new TextView(mcont);
                        tvSecuenciaEstratiSueloR3.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvSecuenciaEstratiSueloR3.setText("IV (Saprolito Grueso 35%-70% F.R.*): ");
                        tvSecuenciaEstratiSueloR3.setTextAppearance(R.style.TituloItem);
                        liFormSecuenciaEstratiSueloR3.addView(tvSecuenciaEstratiSueloR3);

                        EditText etSecuenciaEstratiSueloR3Orden = new EditText(mcont);
                        etSecuenciaEstratiSueloR3Orden.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiSueloR3Orden.setHint("Orden");
                        etSecuenciaEstratiSueloR3Orden.setEms(10);
                        etSecuenciaEstratiSueloR3Orden.setTag("secuenciaestratisuelor3orden");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiSueloR3Orden);
                        liFormSecuenciaEstratiSueloR3.addView(etSecuenciaEstratiSueloR3Orden);

                        EditText etSecuenciaEstratiSueloR3Espesor = new EditText(mcont);
                        etSecuenciaEstratiSueloR3Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiSueloR3Espesor.setHint("Espesor (m)");
                        etSecuenciaEstratiSueloR3Espesor.setEms(10);
                        etSecuenciaEstratiSueloR3Espesor.setTag("secuenciaestratisuelor3espesor");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiSueloR3Espesor);
                        liFormSecuenciaEstratiSueloR3.addView(etSecuenciaEstratiSueloR3Espesor);

                        liFormSecuenciaEstratiSueloR.addView(liFormSecuenciaEstratiSueloR3);
                    }
                    else{
                        liFormSecuenciaEstratiSueloR.removeAllViews();
                    }

                }
            });


            //------------> CARACTERIZACIÓN DE LA UGS / UGI

            TextView tvCaracterizacionUGS = new TextView(mcont);
            tvCaracterizacionUGS.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvCaracterizacionUGS.setText("CARACTERIZACIÓN DE LA UGS / UGI");
            tvCaracterizacionUGS.setTextAppearance(R.style.TituloFormato);
            tvCaracterizacionUGS.setPadding(0, mtop, 0, 0);
            liForm.addView(tvCaracterizacionUGS);


            //------------> N° litologías asociadas a la UGS /UGI

            TextView tvlitologiasasociadas = new TextView(mcont);
            tvlitologiasasociadas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvlitologiasasociadas.setText("N° litologías asociadas a la UGS /UGI");
            tvlitologiasasociadas.setTextAppearance(R.style.TituloItem);
            tvlitologiasasociadas.setPadding(0, mtop, 0, 0);
            liForm.addView(tvlitologiasasociadas);

            secuEstratiWidth = 200;
            secuEstratiOrdenWidth = 400;

            //------------> Litología 1

            LinearLayout liFormlitologiasasociadas1 = new LinearLayout(mcont);
            liFormlitologiasasociadas1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormlitologiasasociadas1.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvlitologiasasociadasOpt1 = new TextView(mcont);
            tvlitologiasasociadasOpt1.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvlitologiasasociadasOpt1.setText("Litología 1");
            tvlitologiasasociadasOpt1.setTextAppearance(R.style.TituloItem);
            liFormlitologiasasociadas1.addView(tvlitologiasasociadasOpt1);

            EditText etlitologiasasociadasOpt1Exist = new EditText(mcont);
            etlitologiasasociadasOpt1Exist.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etlitologiasasociadasOpt1Exist.setHint("Marque X si Existe");
            etlitologiasasociadasOpt1Exist.setEms(10);
            etlitologiasasociadasOpt1Exist.setTag("litologiasasociadasopt1exist");
            ListaEditText.get(idLinear).add(etlitologiasasociadasOpt1Exist);
            liFormlitologiasasociadas1.addView(etlitologiasasociadasOpt1Exist);

            EditText etlitologiasasociadasOpt1Espesor = new EditText(mcont);
            etlitologiasasociadasOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etlitologiasasociadasOpt1Espesor.setHint("Espesor (m)");
            etlitologiasasociadasOpt1Espesor.setEms(10);
            etlitologiasasociadasOpt1Espesor.setTag("litologiasasociadasopt1espesor");
            ListaEditText.get(idLinear).add(etlitologiasasociadasOpt1Espesor);
            liFormlitologiasasociadas1.addView(etlitologiasasociadasOpt1Espesor);

            liForm.addView(liFormlitologiasasociadas1);

            //------------> Litología 2

            LinearLayout liFormlitologiasasociadas2 = new LinearLayout(mcont);
            liFormlitologiasasociadas2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormlitologiasasociadas2.setOrientation(LinearLayout.HORIZONTAL);

            TextView tvlitologiasasociadasOpt2 = new TextView(mcont);
            tvlitologiasasociadasOpt2.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvlitologiasasociadasOpt2.setText("Litología 2");
            tvlitologiasasociadasOpt2.setTextAppearance(R.style.TituloItem);
            liFormlitologiasasociadas2.addView(tvlitologiasasociadasOpt2);

            EditText etlitologiasasociadasOpt2Exist = new EditText(mcont);
            etlitologiasasociadasOpt2Exist.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etlitologiasasociadasOpt2Exist.setHint("Marque X si Existe");
            etlitologiasasociadasOpt2Exist.setEms(10);
            etlitologiasasociadasOpt2Exist.setTag("litologiasasociadasopt2exist");
            ListaEditText.get(idLinear).add(etlitologiasasociadasOpt2Exist);
            liFormlitologiasasociadas2.addView(etlitologiasasociadasOpt2Exist);

            EditText etlitologiasasociadasOpt2Espesor = new EditText(mcont);
            etlitologiasasociadasOpt2Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
            etlitologiasasociadasOpt2Espesor.setHint("Espesor (m)");
            etlitologiasasociadasOpt2Espesor.setEms(10);
            etlitologiasasociadasOpt2Espesor.setTag("litologiasasociadasopt2espesor");
            ListaEditText.get(idLinear).add(etlitologiasasociadasOpt2Espesor);
            liFormlitologiasasociadas2.addView(etlitologiasasociadasOpt2Espesor);

            liForm.addView(liFormlitologiasasociadas2);


            //------------> Nombre de la UGS / UGI

            TextView tvNombreUGS = new TextView(mcont);
            tvNombreUGS.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvNombreUGS.setText("Nombre-Código de la UGS / UGI");
            tvNombreUGS.setTextAppearance(R.style.TituloItem);
            tvNombreUGS.setPadding(0, mtop, 0, 0);
            liForm.addView(tvNombreUGS);

            EditText etNombreUGS = new EditText(mcont);
            etNombreUGS.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etNombreUGS.setHint("Nombre UGS / UGI");
            etNombreUGS.setEms(10);
            etNombreUGS.setTag("nombreugs");
            ListaEditText.get(idLinear).add(etNombreUGS);
            liForm.addView(etNombreUGS);


            //------------> CARACTERÍSTICAS DE LA UGS / UGI

            TextView tvCaracteristicasUGS = new TextView(mcont);
            tvCaracteristicasUGS.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvCaracteristicasUGS.setText("CARACTERÍSTICAS DE LA UGS / UGI");
            tvCaracteristicasUGS.setTextAppearance(R.style.TituloFormato);
            tvCaracteristicasUGS.setPadding(0, mtop, 0, 0);
            liForm.addView(tvCaracteristicasUGS);

            //------------> Estructura Soporte Litologia 1

            TextView tvEstructuraSoporte1 = new TextView(mcont);
            tvEstructuraSoporte1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvEstructuraSoporte1.setText("Estructura Soporte 1");
            tvEstructuraSoporte1.setTextAppearance(R.style.TituloItem);
            tvEstructuraSoporte1.setPadding(0, mtop, 0, 0);
            liForm.addView(tvEstructuraSoporte1);

            Spinner sEstructuraSoporte1 = new Spinner(mcont);
            sEstructuraSoporte1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(mcont, R.array.EstructuraSoporte1, android.R.layout.simple_spinner_item);
            adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sEstructuraSoporte1.setAdapter(adapter3);
            sEstructuraSoporte1.setTag("estructurasoporte1");
            ListaSpinner.get(idLinear).add(sEstructuraSoporte1);
            liForm.addView(sEstructuraSoporte1);

            //------------> Estructura Soporte 2

            TextView tvEstructuraSoporte2 = new TextView(mcont);
            tvEstructuraSoporte2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvEstructuraSoporte2.setText("Estructura Soporte 2");
            tvEstructuraSoporte2.setTextAppearance(R.style.TituloItem);
            tvEstructuraSoporte2.setPadding(0, mtop, 0, 0);
            liForm.addView(tvEstructuraSoporte2);

            Spinner sEstructuraSoporte2 = new Spinner(mcont);
            sEstructuraSoporte2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(mcont, R.array.EstructuraSoporte2, android.R.layout.simple_spinner_item);
            adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sEstructuraSoporte2.setAdapter(adapter4);
            sEstructuraSoporte2.setTag("estructurasoporte2");
            ListaSpinner.get(idLinear).add(sEstructuraSoporte2);
            liForm.addView(sEstructuraSoporte2);

            //------------> Porcentaje Matriz 1

            TextView tvPorcentajeMatriz1 = new TextView(mcont);
            tvPorcentajeMatriz1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvPorcentajeMatriz1.setText("Porcentaje Matriz Litología 1");
            tvPorcentajeMatriz1.setTextAppearance(R.style.TituloItem);
            tvPorcentajeMatriz1.setPadding(0, mtop, 0, 0);
            liForm.addView(tvPorcentajeMatriz1);

            EditText etPorcentajeMatriz1 = new EditText(mcont);
            etPorcentajeMatriz1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etPorcentajeMatriz1.setHint("Porcentaje Matriz Litología 1");
            etPorcentajeMatriz1.setEms(10);
            etPorcentajeMatriz1.setTag("porcentajematriz1");
            ListaEditText.get(idLinear).add(etPorcentajeMatriz1);
            liForm.addView(etPorcentajeMatriz1);

            //------------> Porcentaje Clastos 1

            TextView tvPorcentajeClastos1 = new TextView(mcont);
            tvPorcentajeClastos1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvPorcentajeClastos1.setText("Porcentaje Clastos Litología 1");
            tvPorcentajeClastos1.setTextAppearance(R.style.TituloItem);
            tvPorcentajeClastos1.setPadding(0, mtop, 0, 0);
            liForm.addView(tvPorcentajeClastos1);

            EditText etPorcentajeClastos1 = new EditText(mcont);
            etPorcentajeClastos1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etPorcentajeClastos1.setHint("Porcentaje Clastos Litología 1");
            etPorcentajeClastos1.setEms(10);
            etPorcentajeClastos1.setTag("porcentajeclastos1");
            ListaEditText.get(idLinear).add(etPorcentajeClastos1);
            liForm.addView(etPorcentajeClastos1);

            //------------> Porcentaje Matriz 2

            TextView tvPorcentajeMatriz2 = new TextView(mcont);
            tvPorcentajeMatriz2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvPorcentajeMatriz2.setText("Porcentaje Matriz Litología 2");
            tvPorcentajeMatriz2.setTextAppearance(R.style.TituloItem);
            tvPorcentajeMatriz2.setPadding(0, mtop, 0, 0);
            liForm.addView(tvPorcentajeMatriz2);

            EditText etPorcentajeMatriz2 = new EditText(mcont);
            etPorcentajeMatriz2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etPorcentajeMatriz2.setHint("Porcentaje Matriz Litología 2");
            etPorcentajeMatriz2.setEms(10);
            etPorcentajeMatriz2.setTag("porcentajematriz2");
            ListaEditText.get(idLinear).add(etPorcentajeMatriz2);
            liForm.addView(etPorcentajeMatriz2);

            //------------> Porcentaje Clastos 2

            TextView tvPorcentajeClastos2 = new TextView(mcont);
            tvPorcentajeClastos2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvPorcentajeClastos2.setText("Porcentaje Clastos Litología 2");
            tvPorcentajeClastos2.setTextAppearance(R.style.TituloItem);
            tvPorcentajeClastos2.setPadding(0, mtop, 0, 0);
            liForm.addView(tvPorcentajeClastos2);

            EditText etPorcentajeClastos2 = new EditText(mcont);
            etPorcentajeClastos2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etPorcentajeClastos2.setHint("Porcentaje Clastos Litología 2");
            etPorcentajeClastos2.setEms(10);
            etPorcentajeClastos2.setTag("porcentajeclastos2");
            ListaEditText.get(idLinear).add(etPorcentajeClastos2);
            liForm.addView(etPorcentajeClastos2);

            //------------> CONDICIÓN DE HUMEDAD 1

            TextView tvCondicionHumedad1 = new TextView(mcont);
            tvCondicionHumedad1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvCondicionHumedad1.setText("Condicion de Humedad Litología 1");
            tvCondicionHumedad1.setTextAppearance(R.style.TituloItem);
            tvCondicionHumedad1.setPadding(0, mtop, 0, 0);
            liForm.addView(tvCondicionHumedad1);

            Spinner sCondicionHumedad1 = new Spinner(mcont);
            sCondicionHumedad1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(mcont, R.array.CondicionHumedad1, android.R.layout.simple_spinner_item);
            adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sCondicionHumedad1.setAdapter(adapter5);
            sCondicionHumedad1.setTag("condicionhumedad1");
            ListaSpinner.get(idLinear).add(sCondicionHumedad1);
            liForm.addView(sCondicionHumedad1);

            //------------> CONDICIÓN DE HUMEDAD 2

            TextView tvCondicionHumedad2 = new TextView(mcont);
            tvCondicionHumedad2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvCondicionHumedad2.setText("Condicion de Humedad Litología 2");
            tvCondicionHumedad2.setTextAppearance(R.style.TituloItem);
            tvCondicionHumedad2.setPadding(0, mtop, 0, 0);
            liForm.addView(tvCondicionHumedad2);

            Spinner sCondicionHumedad2 = new Spinner(mcont);
            sCondicionHumedad2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter6 = ArrayAdapter.createFromResource(mcont, R.array.CondicionHumedad2, android.R.layout.simple_spinner_item);
            adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sCondicionHumedad2.setAdapter(adapter6);
            sCondicionHumedad2.setTag("condicionhumedad2");
            ListaSpinner.get(idLinear).add(sCondicionHumedad2);
            liForm.addView(sCondicionHumedad2);



            //------------> ESTRUCTURAS RELICTAS  1

            TextView tvEstructurasRelictas1 = new TextView(mcont);
            tvEstructurasRelictas1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvEstructurasRelictas1.setText("Estructuras Relictas Litología 1");
            tvEstructurasRelictas1.setTextAppearance(R.style.TituloItem);
            tvEstructurasRelictas1.setPadding(0, mtop, 0, 0);
            liForm.addView(tvEstructurasRelictas1);

            Spinner sEstructurasRelictas1 = new Spinner(mcont);
            sEstructurasRelictas1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter7 = ArrayAdapter.createFromResource(mcont, R.array.EstructurasRelictas1, android.R.layout.simple_spinner_item);
            adapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sEstructurasRelictas1.setAdapter(adapter7);
            sEstructurasRelictas1.setTag("estructurasrelictas1");
            ListaSpinner.get(idLinear).add(sEstructurasRelictas1);
            liForm.addView(sEstructurasRelictas1);

            //------------> ESTRUCTURAS RELICTAS  2

            TextView tvEstructurasRelictas2 = new TextView(mcont);
            tvEstructurasRelictas2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvEstructurasRelictas2.setText("Estructuras Relictas Litología 2");
            tvEstructurasRelictas2.setTextAppearance(R.style.TituloItem);
            tvEstructurasRelictas2.setPadding(0, mtop, 0, 0);
            liForm.addView(tvEstructurasRelictas2);

            Spinner sEstructurasRelictas2 = new Spinner(mcont);
            sEstructurasRelictas2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter8 = ArrayAdapter.createFromResource(mcont, R.array.EstructurasRelictas2, android.R.layout.simple_spinner_item);
            adapter8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sEstructurasRelictas2.setAdapter(adapter8);
            sEstructurasRelictas2.setTag("estructurasrelictas2");
            ListaSpinner.get(idLinear).add(sEstructurasRelictas2);
            liForm.addView(sEstructurasRelictas2);



            //------------> Grado meteorización  1

            TextView tvGradoMeteo1 = new TextView(mcont);
            tvGradoMeteo1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvGradoMeteo1.setText("Grado de Meteorización Litología 1");
            tvGradoMeteo1.setTextAppearance(R.style.TituloItem);
            tvGradoMeteo1.setPadding(0, mtop, 0, 0);
            liForm.addView(tvGradoMeteo1);

            Spinner sGradoMeteo1 = new Spinner(mcont);
            sGradoMeteo1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter9 = ArrayAdapter.createFromResource(mcont, R.array.GradoMeteo1, android.R.layout.simple_spinner_item);
            adapter9.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sGradoMeteo1.setAdapter(adapter9);
            sGradoMeteo1.setTag("gradometeo1");
            ListaSpinner.get(idLinear).add(sGradoMeteo1);
            liForm.addView(sGradoMeteo1);

            //------------> Grado meteorización  2

            TextView tvGradoMeteo2 = new TextView(mcont);
            tvGradoMeteo2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvGradoMeteo2.setText("Grado de Meteorización Litología 2");
            tvGradoMeteo2.setTextAppearance(R.style.TituloItem);
            tvGradoMeteo2.setPadding(0, mtop, 0, 0);
            liForm.addView(tvGradoMeteo2);

            Spinner sGradoMeteo2 = new Spinner(mcont);
            sGradoMeteo2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter10 = ArrayAdapter.createFromResource(mcont, R.array.GradoMeteo2, android.R.layout.simple_spinner_item);
            adapter10.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sGradoMeteo2.setAdapter(adapter10);
            sGradoMeteo2.setTag("gradometeo2");
            ListaSpinner.get(idLinear).add(sGradoMeteo2);
            liForm.addView(sGradoMeteo2);

            //------------> Resistenca a la compresion  1

            TextView tvResistenciaComp1 = new TextView(mcont);
            tvResistenciaComp1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvResistenciaComp1.setText("Resistencia a la Compresión Simple (Mpa) Litología 1");
            tvResistenciaComp1.setTextAppearance(R.style.TituloItem);
            tvResistenciaComp1.setPadding(0, mtop, 0, 0);
            liForm.addView(tvResistenciaComp1);

            Spinner sResistenciaComp1 = new Spinner(mcont);
            sResistenciaComp1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter11 = ArrayAdapter.createFromResource(mcont, R.array.ResistenciaCompresionSimple1, android.R.layout.simple_spinner_item);
            adapter11.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sResistenciaComp1.setAdapter(adapter11);
            sResistenciaComp1.setTag("resistenciacomp1");
            ListaSpinner.get(idLinear).add(sResistenciaComp1);
            liForm.addView(sResistenciaComp1);

            //------------> Resistenca a la compresion  2

            TextView tvResistenciaComp2 = new TextView(mcont);
            tvResistenciaComp2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvResistenciaComp2.setText("Resistencia a la Compresión Simple (Mpa) Litología 2");
            tvResistenciaComp2.setTextAppearance(R.style.TituloItem);
            tvResistenciaComp2.setPadding(0, mtop, 0, 0);
            liForm.addView(tvResistenciaComp2);

            Spinner sResistenciaComp2 = new Spinner(mcont);
            sResistenciaComp2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            ArrayAdapter<CharSequence> adapter12 = ArrayAdapter.createFromResource(mcont, R.array.ResistenciaCompresionSimple2, android.R.layout.simple_spinner_item);
            adapter12.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sResistenciaComp2.setAdapter(adapter12);
            sResistenciaComp2.setTag("resistenciacomp2");
            ListaSpinner.get(idLinear).add(sResistenciaComp2);
            liForm.addView(sResistenciaComp2);

            //------------> Color 1

            TextView tvColor1 = new TextView(mcont);
            tvColor1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvColor1.setText("Color Litología 1");
            tvColor1.setTextAppearance(R.style.TituloItem);
            tvColor1.setPadding(0, mtop, 0, 0);
            liForm.addView(tvColor1);

            EditText etColor1 = new EditText(mcont);
            etColor1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            etColor1.setHint("Color Litología 1");
            etColor1.setEms(10);
            etColor1.setTag("color1");
            ListaEditText.get(idLinear).add(etColor1);
            liForm.addView(etColor1);




            //------------> LEVANTAMIENTO DE DISCONTINUIDADES

            TextView tvLevantamientoDisc = new TextView(mcont);
            tvLevantamientoDisc.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvLevantamientoDisc.setText("LEVANTAMIENTO DE DISCONTINUIDADES");
            tvLevantamientoDisc.setTextAppearance(R.style.TituloFormato);
            tvLevantamientoDisc.setPadding(0, mtop, 0, 20);
            liForm.addView(tvLevantamientoDisc);

            liFormDiscontinuidades = new LinearLayout(mcont);
            liFormDiscontinuidades.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormDiscontinuidades.setOrientation(LinearLayout.VERTICAL);
            liForm.addView(liFormDiscontinuidades);

            Button bAnadirDiscont = new Button(mcont);
            bAnadirDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bAnadirDiscont.setText("Añadir Discontinuidad");
            bAnadirDiscont.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout liDiscontinuidades = new LinearLayout(mcont);
                    liDiscontinuidades.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liDiscontinuidades.setOrientation(LinearLayout.VERTICAL);
                    liFormDiscontinuidades.addView(liDiscontinuidades);
                    listDiscontinuidades.add(liDiscontinuidades);


                    listContDiscontinuidades.set(idLinear, listContDiscontinuidades.get(idLinear) + 1);


                    TextView tvNameDiscont = new TextView(mcont);
                    tvNameDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvNameDiscont.setText("Discontinuidad "+ listContDiscontinuidades.get(idLinear));
                    tvNameDiscont.setTextAppearance(R.style.TituloFormato);
                    tvNameDiscont.setPadding(0, 100, 0, 50);
                    liDiscontinuidades.addView(tvNameDiscont);


                    //------------> Tipo

                    TextView tvTipoDiscont = new TextView(mcont);
                    tvTipoDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvTipoDiscont.setText("Tipo");
                    tvTipoDiscont.setTextAppearance(R.style.TituloItem);
                    tvTipoDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvTipoDiscont);

                    Spinner sTipoDiscont = new Spinner(mcont);
                    sTipoDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mcont, R.array.TipoDiscont, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sTipoDiscont.setAdapter(adapter);
                    sTipoDiscont.setTag("TipoDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaSpinner.get(idLinear).add(sTipoDiscont);
                    liDiscontinuidades.addView(sTipoDiscont);


                    //------------> Dir Buzamiento

                    TextView tvDirBuzamiento = new TextView(mcont);
                    tvDirBuzamiento.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvDirBuzamiento.setText("Dir. Buzamiento (Az. Bz.) ");
                    tvDirBuzamiento.setTextAppearance(R.style.TituloItem);
                    tvDirBuzamiento.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvDirBuzamiento);

                    EditText etDirBuzamiento = new EditText(mcont);
                    etDirBuzamiento.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etDirBuzamiento.setHint("Dir. Buzamiento (Az. Bz.) ");
                    etDirBuzamiento.setEms(10);
                    etDirBuzamiento.setTag("DirBuzamiento"+ listContDiscontinuidades.get(idLinear));
                    ListaEditText.get(idLinear).add(etDirBuzamiento);
                    liDiscontinuidades.addView(etDirBuzamiento);

                    //------------> Buzamiento

                    TextView tvBuzamiento = new TextView(mcont);
                    tvBuzamiento.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvBuzamiento.setText("Buzamiento (Bz.)");
                    tvBuzamiento.setTextAppearance(R.style.TituloItem);
                    tvBuzamiento.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvBuzamiento);

                    EditText etBuzamiento = new EditText(mcont);
                    etBuzamiento.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etBuzamiento.setHint("Buzamiento (Bz.)");
                    etBuzamiento.setEms(10);
                    etBuzamiento.setTag("Buzamiento"+ listContDiscontinuidades.get(idLinear));
                    ListaEditText.get(idLinear).add(etBuzamiento);
                    liDiscontinuidades.addView(etBuzamiento);


                    //------------> Persistencia

                    TextView tvPersistenciaDiscont = new TextView(mcont);
                    tvPersistenciaDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvPersistenciaDiscont.setText("Persistencia");
                    tvPersistenciaDiscont.setTextAppearance(R.style.TituloItem);
                    tvPersistenciaDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvPersistenciaDiscont);

                    Spinner sPersistenciaDiscont = new Spinner(mcont);
                    sPersistenciaDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(mcont, R.array.PersistenciaDiscont, android.R.layout.simple_spinner_item);
                    adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sPersistenciaDiscont.setAdapter(adapter1);
                    sPersistenciaDiscont.setTag("PersistenciaDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaSpinner.get(idLinear).add(sPersistenciaDiscont);
                    liDiscontinuidades.addView(sPersistenciaDiscont);

                    //------------> Ancho de Abertura

                    TextView tvAnchoAberDiscont = new TextView(mcont);
                    tvAnchoAberDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvAnchoAberDiscont.setText("Ancho de Abertura");
                    tvAnchoAberDiscont.setTextAppearance(R.style.TituloItem);
                    tvAnchoAberDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvAnchoAberDiscont);

                    Spinner sAnchoAberDiscont = new Spinner(mcont);
                    sAnchoAberDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(mcont, R.array.AnchoAberturaDiscont, android.R.layout.simple_spinner_item);
                    adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sAnchoAberDiscont.setAdapter(adapter2);
                    sAnchoAberDiscont.setTag("AnchoAberDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaSpinner.get(idLinear).add(sAnchoAberDiscont);
                    liDiscontinuidades.addView(sAnchoAberDiscont);

                    //------------> Tipo de Relleno

                    TextView tvTipoRellenoDiscont = new TextView(mcont);
                    tvTipoRellenoDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvTipoRellenoDiscont.setText("Tipo de Relleno");
                    tvTipoRellenoDiscont.setTextAppearance(R.style.TituloItem);
                    tvTipoRellenoDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvTipoRellenoDiscont);

                    Spinner sTipoRellenoDiscont = new Spinner(mcont);
                    sTipoRellenoDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter3 = ArrayAdapter.createFromResource(mcont, R.array.TipoRellenoDiscont, android.R.layout.simple_spinner_item);
                    adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sTipoRellenoDiscont.setAdapter(adapter3);
                    sTipoRellenoDiscont.setTag("TipoRellenoDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaSpinner.get(idLinear).add(sTipoRellenoDiscont);
                    liDiscontinuidades.addView(sTipoRellenoDiscont);

                    //------------> Rugosidad de la Superficie

                    TextView tvRugosidadSuperDiscont = new TextView(mcont);
                    tvRugosidadSuperDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvRugosidadSuperDiscont.setText("Rugosidad de la Superficie");
                    tvRugosidadSuperDiscont.setTextAppearance(R.style.TituloItem);
                    tvRugosidadSuperDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvRugosidadSuperDiscont);

                    Spinner sRugosidadSuperDiscont = new Spinner(mcont);
                    sRugosidadSuperDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter4 = ArrayAdapter.createFromResource(mcont, R.array.RugosidadDiscont, android.R.layout.simple_spinner_item);
                    adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sRugosidadSuperDiscont.setAdapter(adapter4);
                    sRugosidadSuperDiscont.setTag("RugosidadSuperDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaSpinner.get(idLinear).add(sRugosidadSuperDiscont);
                    liDiscontinuidades.addView(sRugosidadSuperDiscont);

                    //------------> Forma de la Superficie

                    TextView tvFormaSuperDiscont = new TextView(mcont);
                    tvFormaSuperDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvFormaSuperDiscont.setText("Forma de la Superficie");
                    tvFormaSuperDiscont.setTextAppearance(R.style.TituloItem);
                    tvFormaSuperDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvFormaSuperDiscont);

                    Spinner sFormaSuperDiscont = new Spinner(mcont);
                    sFormaSuperDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter5 = ArrayAdapter.createFromResource(mcont, R.array.FormaSuperficieDiscont, android.R.layout.simple_spinner_item);
                    adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sFormaSuperDiscont.setAdapter(adapter5);
                    sFormaSuperDiscont.setTag("FormaSuperDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaSpinner.get(idLinear).add(sFormaSuperDiscont);
                    liDiscontinuidades.addView(sFormaSuperDiscont);

                    //------------> Humedad en Diaclasas

                    TextView tvHumedadDiscont = new TextView(mcont);
                    tvHumedadDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvHumedadDiscont.setText("Humedad en Diaclasas");
                    tvHumedadDiscont.setTextAppearance(R.style.TituloItem);
                    tvHumedadDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvHumedadDiscont);

                    Spinner sHumedadDiscont = new Spinner(mcont);
                    sHumedadDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter6 = ArrayAdapter.createFromResource(mcont, R.array.HumedadDiaclasasDiscont, android.R.layout.simple_spinner_item);
                    adapter6.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sHumedadDiscont.setAdapter(adapter6);
                    sHumedadDiscont.setTag("HumedadDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaSpinner.get(idLinear).add(sHumedadDiscont);
                    liDiscontinuidades.addView(sHumedadDiscont);

                    //------------> Espaciamiento

                    TextView tvEspaciamientoDiscont = new TextView(mcont);
                    tvEspaciamientoDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvEspaciamientoDiscont.setText("Espaciamiento");
                    tvEspaciamientoDiscont.setTextAppearance(R.style.TituloItem);
                    tvEspaciamientoDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvEspaciamientoDiscont);

                    Spinner sEspaciamientoDiscont = new Spinner(mcont);
                    sEspaciamientoDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter7 = ArrayAdapter.createFromResource(mcont, R.array.EspaciamientoDiscont, android.R.layout.simple_spinner_item);
                    adapter7.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sEspaciamientoDiscont.setAdapter(adapter7);
                    sEspaciamientoDiscont.setTag("EspaciamientoDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaSpinner.get(idLinear).add(sEspaciamientoDiscont);
                    liDiscontinuidades.addView(sEspaciamientoDiscont);

                    //------------> Meteorización

                    TextView tvMeteorizacionDiscont = new TextView(mcont);
                    tvMeteorizacionDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvMeteorizacionDiscont.setText("Meteorizacion");
                    tvMeteorizacionDiscont.setTextAppearance(R.style.TituloItem);
                    tvMeteorizacionDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvMeteorizacionDiscont);

                    Spinner sMeteorizacionDiscont = new Spinner(mcont);
                    sMeteorizacionDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter8 = ArrayAdapter.createFromResource(mcont, R.array.MeteorizacionDiscont, android.R.layout.simple_spinner_item);
                    adapter8.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sMeteorizacionDiscont.setAdapter(adapter8);
                    sMeteorizacionDiscont.setTag("MeteorizacionDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaSpinner.get(idLinear).add(sMeteorizacionDiscont);
                    liDiscontinuidades.addView(sMeteorizacionDiscont);

                    //------------> Rake/Pitch

                    TextView tvRakePitch = new TextView(mcont);
                    tvRakePitch.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvRakePitch.setText("Rake/Pitch");
                    tvRakePitch.setTextAppearance(R.style.TituloItem);
                    tvRakePitch.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvRakePitch);

                    EditText etRakePitch = new EditText(mcont);
                    etRakePitch.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etRakePitch.setHint("Rake/Pitch");
                    etRakePitch.setEms(10);
                    etRakePitch.setTag("RakePitch"+ listContDiscontinuidades.get(idLinear));
                    ListaEditText.get(idLinear).add(etRakePitch);
                    liDiscontinuidades.addView(etRakePitch);

                    //------------> Dir. del Rake/Pitch

                    TextView tvDirRakePitch = new TextView(mcont);
                    tvDirRakePitch.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvDirRakePitch.setText("Dir. del Rake/Pitch");
                    tvDirRakePitch.setTextAppearance(R.style.TituloItem);
                    tvDirRakePitch.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvDirRakePitch);

                    EditText etDirRakePitch = new EditText(mcont);
                    etDirRakePitch.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etDirRakePitch.setHint("Dir. del Rake/Pitch");
                    etDirRakePitch.setEms(10);
                    etDirRakePitch.setTag("DirRakePitch"+ listContDiscontinuidades.get(idLinear));
                    ListaEditText.get(idLinear).add(etDirRakePitch);
                    liDiscontinuidades.addView(etDirRakePitch);

                    //------------> Orientación talud/ladera

                    TextView tvOrientacion = new TextView(mcont);
                    tvOrientacion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvOrientacion.setText("Orientación talud/ladera");
                    tvOrientacion.setTextAppearance(R.style.TituloItem);
                    tvOrientacion.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvOrientacion);

                    //------------> Az Bz/Bz 1

                    TextView tvAzBzBz1 = new TextView(mcont);
                    tvAzBzBz1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvAzBzBz1.setText("Az Bz/Bz");
                    tvAzBzBz1.setTextAppearance(R.style.TituloItem);
                    tvAzBzBz1.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvAzBzBz1);

                    EditText etAzBzBz1 = new EditText(mcont);
                    etAzBzBz1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etAzBzBz1.setHint("Az Bz/Bz");
                    etAzBzBz1.setEms(10);
                    etAzBzBz1.setTag("AzBzBz1"+ listContDiscontinuidades.get(idLinear));
                    ListaEditText.get(idLinear).add(etAzBzBz1);
                    liDiscontinuidades.addView(etAzBzBz1);

                    //------------> Az Bz/Bz 2

                    TextView tvAzBzBz2 = new TextView(mcont);
                    tvAzBzBz2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvAzBzBz2.setText("Az Bz/Bz");
                    tvAzBzBz2.setTextAppearance(R.style.TituloItem);
                    tvAzBzBz2.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvAzBzBz2);

                    EditText etAzBzBz2 = new EditText(mcont);
                    etAzBzBz2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etAzBzBz2.setHint("Az Bz/Bz");
                    etAzBzBz2.setEms(10);
                    etAzBzBz2.setTag("AzBzBz2"+ listContDiscontinuidades.get(idLinear));
                    ListaEditText.get(idLinear).add(etAzBzBz2);
                    liDiscontinuidades.addView(etAzBzBz2);

                    //------------> Altura

                    TextView tvAltura = new TextView(mcont);
                    tvAltura.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvAltura.setText("Altura");
                    tvAltura.setTextAppearance(R.style.TituloItem);
                    tvAltura.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvAltura);

                    EditText etAltura = new EditText(mcont);
                    etAltura.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etAltura.setHint("Altura");
                    etAltura.setEms(10);
                    etAltura.setTag("AlturaDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaEditText.get(idLinear).add(etAltura);
                    liDiscontinuidades.addView(etAltura);

                    //------------> Observaciones

                    TextView tvObservacionesDiscont = new TextView(mcont);
                    tvObservacionesDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvObservacionesDiscont.setText("Observaciones");
                    tvObservacionesDiscont.setTextAppearance(R.style.TituloItem);
                    tvObservacionesDiscont.setPadding(0, 70, 0, 0);
                    liDiscontinuidades.addView(tvObservacionesDiscont);

                    EditText etObservacionesDiscont = new EditText(mcont);
                    etObservacionesDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etObservacionesDiscont.setHint("Observaciones");
                    etObservacionesDiscont.setEms(10);
                    etObservacionesDiscont.setTag("ObservacionesDiscont"+ listContDiscontinuidades.get(idLinear));
                    ListaEditText.get(idLinear).add(etObservacionesDiscont);
                    liDiscontinuidades.addView(etObservacionesDiscont);

                }
            });
            liForm.addView(bAnadirDiscont);

            //------------> Fotografías Anexas

            TextView tvFotosAnexas = new TextView(mcont);
            tvFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvFotosAnexas.setText("Fotografías Anexas");
            tvFotosAnexas.setTextAppearance(R.style.TituloFormato);
            tvFotosAnexas.setPadding(0, mtop, 0, 20);
            liForm.addView(tvFotosAnexas);

            liFormFotosAnexas = new LinearLayout(mcont);
            liFormFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormFotosAnexas.setOrientation(LinearLayout.VERTICAL);
            liForm.addView(liFormFotosAnexas);


            Button bFotosAnexas = new Button(mcont);
            bFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bFotosAnexas.setText("Añadir Foto");
            bFotosAnexas.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout liFotosAnexas = new LinearLayout(mcont);
                    liFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liFotosAnexas.setOrientation(LinearLayout.VERTICAL);
                    liFormFotosAnexas.addView(liFotosAnexas);
                    listFotosAnexas.add(liFotosAnexas);

                    listContFotosAnexas.set(idLinear, listContFotosAnexas.get(idLinear) + 1);

                    TextView tvNameFotos = new TextView(mcont);
                    tvNameFotos.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvNameFotos.setText("Foto "+ listContFotosAnexas.get(idLinear));
                    tvNameFotos.setTextAppearance(R.style.TituloFormato);
                    tvNameFotos.setPadding(0, 100, 0, 50);
                    liFotosAnexas.addView(tvNameFotos);


                    //------------> Nombre Foto

                    TextView tvNombreFotosAnexas = new TextView(mcont);
                    tvNombreFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvNombreFotosAnexas.setText("Nombre de la Foto");
                    tvNombreFotosAnexas.setTextAppearance(R.style.TituloItem);
                    tvNombreFotosAnexas.setPadding(0, 70, 0, 0);
                    liFotosAnexas.addView(tvNombreFotosAnexas);

                    EditText etNombreFotosAnexas = new EditText(mcont);
                    etNombreFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etNombreFotosAnexas.setHint("Nombre de la Foto");
                    etNombreFotosAnexas.setEms(10);
                    etNombreFotosAnexas.setTag("NombreFotosAnexas"+listContFotosAnexas.get(idLinear));
                    ListaEditText.get(idLinear).add(etNombreFotosAnexas);
                    liFotosAnexas.addView(etNombreFotosAnexas);

                    //------------> Descripción

                    TextView tvDescriFotosAnexas = new TextView(mcont);
                    tvDescriFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvDescriFotosAnexas.setText("Descripción de la Foto");
                    tvDescriFotosAnexas.setTextAppearance(R.style.TituloItem);
                    tvDescriFotosAnexas.setPadding(0, 70, 0, 0);
                    liFotosAnexas.addView(tvDescriFotosAnexas);

                    EditText etDescriFotosAnexas = new EditText(mcont);
                    etDescriFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etDescriFotosAnexas.setHint("Descripción de la Foto");
                    etDescriFotosAnexas.setEms(10);
                    etDescriFotosAnexas.setTag("DescriFotosAnexas"+listContFotosAnexas.get(idLinear));
                    ListaEditText.get(idLinear).add(etDescriFotosAnexas);
                    liFotosAnexas.addView(etDescriFotosAnexas);
                }
            });
            liForm.addView(bFotosAnexas);


            listLiForm.add(liForm);
            liFormularios.addView(liForm);
        }
    }

    private boolean ArchivoExiste(String[] file, String name) {
        for (String s : file)
            if (name.equals(s))
                return true;
        return false;
    }

    private void SubirForm() throws IOException,JSONException {

        if (auxTextExist && login){
            databaseReference.child("EstacionesCampo/cont/cont").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error Getting Data", task.getException());
                    }
                    else {
                        Log.d("firebase", "Success Getting Data "+String.valueOf(task.getResult().getValue()));
                        int cont = Integer. parseInt(String.valueOf(task.getResult().getValue()));
                        boolean subido = false;
                        for (int i = 0; i < formComplete.length(); i++) {

                            JSONObject form = null;
                            try {
                                form = formComplete.getJSONObject(i);
                                subido = Boolean.parseBoolean(form.getString("Subido"));

                                if (!subido){
                                    String Estacion = form.getString("Estacion");
                                    String TipoEstacion = form.getString("TipoEstacion");
                                    String Este = form.getString("Este");
                                    String Norte = form.getString("Norte");
                                    String Altitud = form.getString("Altitud");
                                    String Fotos = form.getString("Fotos");
                                    String Observaciones = form.getString("Observaciones");
                                    String Fecha = form.getString("Fecha");
                                    String Propietario = form.getString("Propietario");

                                    FormFeature nuevaEstacion = new FormFeature(Estacion, TipoEstacion, Este, Norte, Altitud, Fotos, Observaciones, Fecha, Propietario);

                                    form.put("Subido", true);

                                    databaseReference.child("EstacionesCampo/estacion_"+cont).setValue(nuevaEstacion);

                                    JSONObject Formularios = form.getJSONObject("Formularios");
                                    JSONObject counts = Formularios.getJSONObject("counts");

                                    int contUGS_Rocas = Integer.parseInt(counts.getString("UGS_Rocas"));

                                    for (int j = 0; j < contUGS_Rocas; j++) {
                                        JSONObject FromatoAux = Formularios.getJSONObject("Form_UGS_Rocas_"+j);
                                        JSONObject SpinnersAux = FromatoAux.getJSONObject("Spinners");
                                        JSONObject EditTextsAux = FromatoAux.getJSONObject("EditText");
                                        String municipios = SpinnersAux.getString("municipios");
                                        String claseaflor = SpinnersAux.getString("claseaflor");
                                        String gsi = SpinnersAux.getString("gsi");
                                        String fabrica1 = SpinnersAux.getString("fabrica1");
                                        String fabrica2 = SpinnersAux.getString("fabrica2");
                                        String humedad1 = SpinnersAux.getString("humedad1");
                                        String humedad2 = SpinnersAux.getString("humedad2");
                                        String tamanograno1 = SpinnersAux.getString("tamañograno1");
                                        String tamanograno2 = SpinnersAux.getString("tamañograno2");
                                        String gradometeo1 = SpinnersAux.getString("gradometeo1");
                                        String gradometeo2 = SpinnersAux.getString("gradometeo2");
                                        String resistenciacomp1 = SpinnersAux.getString("resistenciacomp1");
                                        String resistenciacomp2 = SpinnersAux.getString("resistenciacomp2");
                                        String noformato = EditTextsAux.getString("noformato");
                                        String vereda = EditTextsAux.getString("vereda");
//                                        String nombreformato = EditTextsAux.getString("nombreformato");
                                        String noestacion = EditTextsAux.getString("noestacion");
                                        String secuenciaestratiopt1orden = EditTextsAux.getString("secuenciaestratiopt1orden");
                                        String secuenciaestratiopt1espesor = EditTextsAux.getString("secuenciaestratiopt1espesor");
                                        String secuenciaestratiopt2orden = EditTextsAux.getString("secuenciaestratiopt2orden");
                                        String secuenciaestratiopt2espesor = EditTextsAux.getString("secuenciaestratiopt2espesor");
                                        String secuenciaestratiopt3orden = EditTextsAux.getString("secuenciaestratiopt3orden");
                                        String secuenciaestratiopt3espesor = EditTextsAux.getString("secuenciaestratiopt3espesor");
                                        String secuenciaestratiopt4orden = EditTextsAux.getString("secuenciaestratiopt4orden");
                                        String secuenciaestratiopt4espesor = EditTextsAux.getString("secuenciaestratiopt4espesor");

                                        String secuenciaestratisuelor1orden = "";
                                        String secuenciaestratisuelor1espesor = "";
                                        String secuenciaestratisuelor2orden = "";
                                        String secuenciaestratisuelor2espesor = "";
                                        String secuenciaestratisuelor3orden = "";
                                        String secuenciaestratisuelor3espesor = "";

                                        String elNuevoTexto = secuenciaestratiopt3orden;
                                        elNuevoTexto = elNuevoTexto.replace(" ","");
                                        if (!elNuevoTexto.equals("")){
                                            secuenciaestratisuelor1orden = EditTextsAux.getString("secuenciaestratisuelor1orden");
                                            secuenciaestratisuelor1espesor = EditTextsAux.getString("secuenciaestratisuelor1espesor");
                                            secuenciaestratisuelor2orden = EditTextsAux.getString("secuenciaestratisuelor2orden");
                                            secuenciaestratisuelor2espesor = EditTextsAux.getString("secuenciaestratisuelor2espesor");
                                            secuenciaestratisuelor3orden = EditTextsAux.getString("secuenciaestratisuelor3orden");
                                            secuenciaestratisuelor3espesor = EditTextsAux.getString("secuenciaestratisuelor3espesor");
                                        }
                                        String perfilmeteorizacion = EditTextsAux.getString("perfilmeteorizacion");
                                        String litologiasasociadasopt1exist = EditTextsAux.getString("litologiasasociadasopt1exist");
                                        String litologiasasociadasopt1espesor = EditTextsAux.getString("litologiasasociadasopt1espesor");
                                        String litologiasasociadasopt2exist = EditTextsAux.getString("litologiasasociadasopt2exist");
                                        String litologiasasociadasopt2espesor = EditTextsAux.getString("litologiasasociadasopt2espesor");
                                        String nombreugs = EditTextsAux.getString("nombreugs");
                                        String color1 = EditTextsAux.getString("color1");
                                        String color2 = EditTextsAux.getString("color2");
                                        String composicionmineral1 = EditTextsAux.getString("composicionmineral1");
                                        String composicionmineral2 = EditTextsAux.getString("composicionmineral2");

                                        FormatUGSRocas nuevoFormatoUGSRocas = new FormatUGSRocas(noformato, municipios,  claseaflor,  gsi,  fabrica1,  fabrica2,  humedad1,  humedad2,  tamanograno1,  tamanograno2,  gradometeo1,  gradometeo2,  resistenciacomp1,  resistenciacomp2,  vereda,  noestacion,  secuenciaestratiopt1orden,  secuenciaestratiopt1espesor,  secuenciaestratiopt2orden,  secuenciaestratiopt2espesor,  secuenciaestratiopt3orden,  secuenciaestratiopt3espesor,  secuenciaestratiopt4orden,  secuenciaestratiopt4espesor,  secuenciaestratisuelor1orden,  secuenciaestratisuelor1espesor,  secuenciaestratisuelor2orden,  secuenciaestratisuelor2espesor,  secuenciaestratisuelor3orden,  secuenciaestratisuelor3espesor,  perfilmeteorizacion,  litologiasasociadasopt1exist,  litologiasasociadasopt1espesor,  litologiasasociadasopt2exist,  litologiasasociadasopt2espesor,  nombreugs,  color1,  color2,  composicionmineral1,  composicionmineral2);

                                        databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Rocas_"+j).setValue(nuevoFormatoUGSRocas);

                                        int contDiscont = Integer.parseInt(FromatoAux.getString("Discontinuidades"));
                                        for (int k = 1; k <= contDiscont; k++) {
                                            String TipoDiscont = SpinnersAux.getString("TipoDiscont"+k);
                                            String PersistenciaDiscont = SpinnersAux.getString("PersistenciaDiscont"+k);
                                            String AnchoAberDiscont = SpinnersAux.getString("AnchoAberDiscont"+k);
                                            String TipoRellenoDiscont = SpinnersAux.getString("TipoRellenoDiscont"+k);
                                            String RugosidadSuperDiscont = SpinnersAux.getString("RugosidadSuperDiscont"+k);
                                            String FormaSuperDiscont = SpinnersAux.getString("FormaSuperDiscont"+k);
                                            String HumedadDiscont = SpinnersAux.getString("HumedadDiscont"+k);
                                            String EspaciamientoDiscont = SpinnersAux.getString("EspaciamientoDiscont"+k);
                                            String MeteorizacionDiscont = SpinnersAux.getString("MeteorizacionDiscont"+k);
                                            String DirBuzamiento = EditTextsAux.getString("DirBuzamiento"+k);
                                            String Buzamiento = EditTextsAux.getString("Buzamiento"+k);
                                            String RakePitch = EditTextsAux.getString("RakePitch"+k);
                                            String DirRakePitch = EditTextsAux.getString("DirRakePitch"+k);
                                            String AzBzBz1 = EditTextsAux.getString("AzBzBz1"+k);
                                            String AzBzBz2 = EditTextsAux.getString("AzBzBz2"+k);
                                            String AlturaDiscont = EditTextsAux.getString("AlturaDiscont"+k);
                                            String ObservacionesDiscont = EditTextsAux.getString("ObservacionesDiscont"+k);

                                            FormatDiscont nuevoFormatoDiscont = new FormatDiscont( TipoDiscont,  PersistenciaDiscont,  AnchoAberDiscont,  TipoRellenoDiscont,  RugosidadSuperDiscont,  FormaSuperDiscont,  HumedadDiscont,  EspaciamientoDiscont,  MeteorizacionDiscont,  DirBuzamiento,  Buzamiento,  RakePitch,  DirRakePitch,  AzBzBz1,  AzBzBz2,  AlturaDiscont,  ObservacionesDiscont);
                                            databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Rocas_"+j+"/Discontinuidades/Discont_"+k).setValue(nuevoFormatoDiscont);

                                        }

                                        int contFotosAnexas = Integer.parseInt(FromatoAux.getString("FotosAnexas"));
                                        for (int k = 1; k <= contFotosAnexas; k++) {

                                            String NombreFotosAnexas = EditTextsAux.getString("NombreFotosAnexas"+k);
                                            String DescriFotosAnexas = EditTextsAux.getString("DescriFotosAnexas"+k);

                                            FormatFotosAnexas nuevoFormatoFotosAnexas = new FormatFotosAnexas(NombreFotosAnexas, DescriFotosAnexas);
                                            databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Rocas_"+j+"/FotosAnexas/FotoAnexa_"+k).setValue(nuevoFormatoFotosAnexas);

                                        }

                                    }



                                    cont++;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Log.d("jaaja", "AlgunError: "+e);
                            }

                        }


                        databaseReference.child("EstacionesCampo/cont/cont").setValue(cont);


                    }
                }
            });
            Log.d("jaaja", "GuardarForm: "+formComplete);
            OutputStreamWriter file = new OutputStreamWriter(mcont.openFileOutput("listaForm.txt", Activity.MODE_PRIVATE));
            file.write(String.valueOf(formComplete));
            file.flush();
            file.close();
            Toast.makeText(mcont, "Subidos a la Base de Datos los Formularios Guardados\n", Toast.LENGTH_LONG).show();
        }else{
            if (login){
                Toast.makeText(mcont, "No hay Formularios guardados para subir a la Base de Datos\n", Toast.LENGTH_LONG).show();
            }else{
                Toast.makeText(mcont, "Por favor Inicie Sesión\n", Toast.LENGTH_LONG).show();
            }
        }

    }

    private void GuardarForm() throws JSONException, IOException {

        Date d = new Date();
        CharSequence s = DateFormat.format("yyyy-MM-dd", d.getTime());
        attrForm = new JSONObject()
                .put("Subido", false)
                .put("Estacion", etEstacion.getText().toString())
                .put("TipoEstacion", etTipoEstacion.getText().toString())
                .put("Este", etEste.getText().toString())
                .put("Norte", etNorte.getText().toString())
                .put("Altitud", etAltitud.getText().toString())
                .put("Fotos", etFotos.getText().toString())
                .put("Observaciones", etObservaciones.getText().toString())
                .put("Fecha", s.toString())
                .put("Propietario", userName);

        JSONObject FormatosList = new JSONObject();
        JSONObject countFormatos = new JSONObject();
        int countFormatosUGSRocas = 0;
        int countFormatosUGSSuelos = 0;
        for (int i = 0; i < listFormularios.size(); i++) {
            if (listFormularios.get(i).equals("UGS Rocas")){
                JSONObject FormatoTemp = new JSONObject()
                        .put("Discontinuidades", listContDiscontinuidades.get(i))
                        .put("FotosAnexas", listContFotosAnexas.get(i));

                JSONObject spinnerList = new JSONObject();
                for (int j = 0; j < ListaSpinner.get(i).size(); j++) {
                    spinnerList.put(ListaSpinner.get(i).get(j).getTag().toString(), ListaSpinner.get(i).get(j).getSelectedItem().toString());
                }
                FormatoTemp.put("Spinners", spinnerList);

                JSONObject editTextList = new JSONObject();
                for (int k = 0; k < ListaEditText.get(i).size(); k++) {
                    editTextList.put(ListaEditText.get(i).get(k).getTag().toString(), ListaEditText.get(i).get(k).getText().toString());
                }
                FormatoTemp.put("EditText", editTextList);

                FormatosList.put("Form_UGS_Rocas_"+countFormatosUGSRocas, FormatoTemp);

                countFormatosUGSRocas++;
            }
        }
        countFormatos.put("UGS_Rocas", countFormatosUGSRocas);
        countFormatos.put("UGS_Suelos", countFormatosUGSSuelos);

        FormatosList.put("counts", countFormatos);
        attrForm.put("Formularios", FormatosList);




        formComplete.put(attrForm);
        Log.d("jaaja", "GuardarForm3: "+formComplete);
        Log.d("jaaja", "GuardarForm1: "+listFormularios);
        Log.d("jaaja", "GuardarForm2: "+attrForm);


        OutputStreamWriter file = new OutputStreamWriter(mcont.openFileOutput("listaForm.txt", Activity.MODE_PRIVATE));
        file.write(String.valueOf(formComplete));
        //file.write("");
        file.flush();
        file.close();
        Toast.makeText(mcont, "Formulario Guardado\n", Toast.LENGTH_LONG).show();
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}