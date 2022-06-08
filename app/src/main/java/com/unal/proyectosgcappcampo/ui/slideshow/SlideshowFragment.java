package com.unal.proyectosgcappcampo.ui.slideshow;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
    Button btnAddForm;
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

    List<ElementoFormato> listaElementosUGSR = new ArrayList<ElementoFormato>();
    List<ElementoFormato> listaElementosUGSRDiscont = new ArrayList<ElementoFormato>();
    List<ElementoFormato> listaElementosUGSFotosAnexas = new ArrayList<ElementoFormato>();
    List<ElementoFormato> listaElementosUGSS = new ArrayList<ElementoFormato>();

    ElementoFormato ElementoSueloResidualUGSR = new ElementoFormato( "Horizonte",  "secuenciaestrati",  "secuenciaestratisuelor", R.array.SecuenciaEstratiRocasSueloRes);
    ElementoFormato ElementoSueloResidualUGSS = new ElementoFormato( "Horizonte",  "secuenciaestrati",  "secuenciaestratisuelor", R.array.SecuenciaEstratiSuelosSueloRes);



    int idLinear;

    Spinner sFormularios;
    LinearLayout liFormularios;
    List<String> listFormularios = new ArrayList<String>();
    List<LinearLayout> listLiForm = new ArrayList<LinearLayout>();
    List<EditText> listEditText = new ArrayList<EditText>();
    List<Spinner> listSpinner = new ArrayList<Spinner>();
    List<CheckBox> listCheckBox = new ArrayList<CheckBox>();
    List<RadioButton> listRadioBtn = new ArrayList<RadioButton>();
    List<RadioButton> listRadioBtn1Finos = new ArrayList<RadioButton>();
    List<RadioButton> listRadioBtn2Finos = new ArrayList<RadioButton>();
    List<RadioButton> listRadioBtn1Gruesos = new ArrayList<RadioButton>();
    List<RadioButton> listRadioBtn2Gruesos = new ArrayList<RadioButton>();
    List<RadioGroup> listRadioGrp = new ArrayList<RadioGroup>();
    List<Button> listBtnAcordion = new ArrayList<Button>();

    List<List<EditText>> ListaEditText = new ArrayList<List<EditText>>();
    List<List<Spinner>> ListaSpinner = new ArrayList<List<Spinner>>();
    List<List<CheckBox>> ListaCheckBox = new ArrayList<List<CheckBox>>();
    List<List<RadioButton>> ListaRadioBtn = new ArrayList<List<RadioButton>>();
    List<List<RadioButton>> ListaRadioBtn1Finos = new ArrayList<List<RadioButton>>();
    List<List<RadioButton>> ListaRadioBtn2Finos = new ArrayList<List<RadioButton>>();
    List<List<RadioButton>> ListaRadioBtn1Gruesos = new ArrayList<List<RadioButton>>();
    List<List<RadioButton>> ListaRadioBtn2Gruesos = new ArrayList<List<RadioButton>>();
    List<List<RadioGroup>> ListaRadioGrp = new ArrayList<List<RadioGroup>>();

    boolean subida = false;


    //Format UGS
    List<Integer> listContDiscontinuidades = new ArrayList<Integer>();
    List<Integer> listContFotosAnexas = new ArrayList<Integer>();

    int discontinuidades = 0;
    List<List<LinearLayout>> ListaDiscontinuidades = new ArrayList<List<LinearLayout>>();
    List<LinearLayout> listDiscontinuidades = new ArrayList<LinearLayout>();
    LinearLayout liFormDiscontinuidades;

    int fotosAnexas = 0;
    List<List<LinearLayout>> ListaFotosAnexas = new ArrayList<List<LinearLayout>>();
    List<LinearLayout> listFotosAnexas = new ArrayList<LinearLayout>();
    LinearLayout liFormFotosAnexas;

    private DatabaseReference databaseReference;

    private Context mcont = getActivity();

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        SlideshowViewModel slideshowViewModel =
                new ViewModelProvider(this).get(SlideshowViewModel.class);

        binding = FragmentSlideshowBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mcont = root.getContext();

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        listaElementosUGSR = new ArrayList<ElementoFormato>();
        listaElementosUGSRDiscont = new ArrayList<ElementoFormato>();
        listaElementosUGSFotosAnexas = new ArrayList<ElementoFormato>();
        listaElementosUGSS = new ArrayList<ElementoFormato>();
        listFormularios = new ArrayList<String>();
        listLiForm = new ArrayList<LinearLayout>();

        GenerarListas();

        btnFormLoad = binding.btnFormLoad;
        btnFormSync = binding.btnFormSync;
        btnAddForm = binding.AddFormu;
        etEstacion = binding.etEstacion;
        etTipoEstacion = binding.etTipoEstacion;
        etNorte = binding.etNorte;
        etEste = binding.etEste;
        etAltitud = binding.etAltitud;
        etFotos = binding.etFotos;
        etObservaciones = binding.etObservaciones;

        liFormularios = binding.liFormularios;
        sFormularios = binding.sFormularios;
        liFormularios.removeAllViews();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mcont, R.array.Formularios , android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sFormularios.setAdapter(adapter);

        btnAddForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String eleccion = sFormularios.getSelectedItem().toString();
                if (!eleccion.equals("Ninguno")) {
                    Toast.makeText(mcont, "Se añadió formulario: " + eleccion, Toast.LENGTH_LONG).show();
                    AddFormulario(eleccion);
                }
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

    }

    private void AddFormulario(String formType) {

        int mtop = 70;
        listFormularios.add(formType);
        idLinear = listFormularios.size() - 1;


        listFotosAnexas = new ArrayList<LinearLayout>();
        listDiscontinuidades = new ArrayList<LinearLayout>();
        listEditText = new ArrayList<EditText>();
        listSpinner = new ArrayList<Spinner>();
        listCheckBox = new ArrayList<CheckBox>();
        listRadioBtn = new ArrayList<RadioButton>();
        listRadioBtn1Finos = new ArrayList<RadioButton>();
        listRadioBtn2Finos = new ArrayList<RadioButton>();
        listRadioBtn1Gruesos = new ArrayList<RadioButton>();
        listRadioBtn2Gruesos = new ArrayList<RadioButton>();
        listRadioGrp = new ArrayList<RadioGroup>();


        ListaFotosAnexas.add(listFotosAnexas);
        ListaDiscontinuidades.add(listDiscontinuidades);
        ListaEditText.add(listEditText);
        ListaSpinner.add(listSpinner);
        ListaCheckBox.add(listCheckBox);
        ListaRadioBtn.add(listRadioBtn);
        ListaRadioBtn1Finos.add(listRadioBtn1Finos);
        ListaRadioBtn2Finos.add(listRadioBtn2Finos);
        ListaRadioBtn1Gruesos.add(listRadioBtn1Gruesos);
        ListaRadioBtn2Gruesos.add(listRadioBtn2Gruesos);
        ListaRadioGrp.add(listRadioGrp);

        fotosAnexas = 0;
        discontinuidades = 0;

        listContFotosAnexas.add(fotosAnexas);
        listContDiscontinuidades.add(discontinuidades);

        if (formType.equals("UGS Rocas")) {
            Button bAcordion = new Button(mcont);
            bAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
            bAcordion.setText("Formato UGS Rocas");
            bAcordion.setTag(idLinear);
            listBtnAcordion.add(bAcordion);
            liFormularios.addView(bAcordion);

            LinearLayout liForm = new LinearLayout(mcont);
            liForm.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liForm.setOrientation(LinearLayout.VERTICAL);
            liForm.setBackgroundColor(0x33333300);
            //liForm.setVisibility(View.GONE);

            bAcordion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listLiForm.get(Integer.parseInt(v.getTag().toString())).getVisibility() == View.VISIBLE) {
                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                        animation.setDuration(220);
                        animation.setFillAfter(false);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                listLiForm.get(Integer.parseInt(v.getTag().toString())).setVisibility(View.GONE);
                                listBtnAcordion.get(Integer.parseInt(v.getTag().toString())).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                            }
                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        listLiForm.get(Integer.parseInt(v.getTag().toString())).startAnimation(animation);

                    }
                    else {
                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                        animation.setDuration(220);
                        animation.setFillAfter(false);
                        listLiForm.get(Integer.parseInt(v.getTag().toString())).startAnimation(animation);
                        listLiForm.get(Integer.parseInt(v.getTag().toString())).setVisibility(View.VISIBLE);
                        listBtnAcordion.get(Integer.parseInt(v.getTag().toString())).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                    }

//                    if (listLiForm.get(Integer.parseInt(v.getTag().toString())).getVisibility() == View.VISIBLE) {
//                        listLiForm.get(Integer.parseInt(v.getTag().toString())).setVisibility(View.GONE);
//                    }
//                    else {
//                        listLiForm.get(Integer.parseInt(v.getTag().toString())).setVisibility(View.VISIBLE);
//                    }

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
                    Log.d("jaaj", "BOrrarRocas: "+listFormularios);
                    listLiForm.get(Integer.parseInt(v.getTag().toString())).removeAllViews();
                    liFormularios.removeView(listBtnAcordion.get(Integer.parseInt(v.getTag().toString())));
                    listFormularios.set(Integer.parseInt(v.getTag().toString()), "Ninguno");

                }
            });
            liForm.addView(bBorrarForm);

            for (int i = 0; i < listaElementosUGSR.size(); i++) {
                ElementoFormato elementoActual = listaElementosUGSR.get(i);
                String nombreElemento = elementoActual.getNombreelemento();
                String hintElemento = elementoActual.getNombreelemento();
                String claseElemento = elementoActual.getClaseelemento();
                String tagElemento = elementoActual.getTagelemento();
                int idStringArrayElemento = elementoActual.getIdStringArray();
                if(nombreElemento.equals("Perfil de meteorización (Dearman 1974)")){
                    hintElemento = tagElemento.split("_")[1];
                    tagElemento = tagElemento.split("_")[0];
                }

                if (claseElemento.equals("edittext")){
                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloItem);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                    EditText etGenerico = new EditText(mcont);
                    etGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etGenerico.setHint(hintElemento);
                    etGenerico.setEms(10);
                    etGenerico.setTag(tagElemento);
                    ListaEditText.get(idLinear).add(etGenerico);
                    liForm.addView(etGenerico);
                }
                if (claseElemento.equals("spinner")){
                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloItem);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                    Spinner sGenerico = new Spinner(mcont);
                    sGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mcont, idStringArrayElemento, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sGenerico.setAdapter(adapter);
                    sGenerico.setTag(tagElemento);
                    ListaSpinner.get(idLinear).add(sGenerico);
                    liForm.addView(sGenerico);
                }
                if (claseElemento.equals("secuenciaestrati")){
                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloFormato);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                    Resources res = getResources();
                    String[] opciones = res.getStringArray(idStringArrayElemento);
                    int secuEstratiWidth = 420;
                    int secuEstratiOrdenWidth = 200;
                    int secuEstratiEspesorWidth = 300;

                    for (int j = 0; j < opciones.length ; j++) {
                        int aux = j + 1;
                        LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                        liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                        TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                        tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvSecuenciaEstratiOpt.setText(opciones[j]);
                        tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                        liFormSecuenciaEstrati.addView(tvSecuenciaEstratiOpt);

                        EditText etSecuenciaEstratiOpt = new EditText(mcont);
                        etSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiOpt.setHint("Orden");
                        etSecuenciaEstratiOpt.setEms(10);
                        etSecuenciaEstratiOpt.setTag(tagElemento+aux+"orden");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt);
                        liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt);

                        EditText etSecuenciaEstratiOpt1Espesor = new EditText(mcont);
                        etSecuenciaEstratiOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiOpt1Espesor.setHint("Espesor (m)");
                        etSecuenciaEstratiOpt1Espesor.setEms(10);
                        etSecuenciaEstratiOpt1Espesor.setTag(tagElemento+aux+"espesor");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt1Espesor);
                        liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt1Espesor);

                        liForm.addView(liFormSecuenciaEstrati);

                        if (opciones[j].equals("Suelo Residual")){
                            LinearLayout liFormSecuenciaEstratiSueloR = new LinearLayout(mcont);
                            liFormSecuenciaEstratiSueloR.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liFormSecuenciaEstratiSueloR.setOrientation(LinearLayout.VERTICAL);

                            liForm.addView(liFormSecuenciaEstratiSueloR);

                            etSecuenciaEstratiOpt.addTextChangedListener(new TextWatcher() {
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
                                        liFormSecuenciaEstratiSueloR.removeAllViews();
                                        int secuEstratiWidth = 150;
                                        int secuEstratiOrdenWidth = 200;
                                        int secuEstratiEspesorWidth = 300;

                                        TextView tvSecuenciaEstratiHorizonte2 = new TextView(mcont);
                                        tvSecuenciaEstratiHorizonte2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        tvSecuenciaEstratiHorizonte2.setText("Horizonte");
                                        tvSecuenciaEstratiHorizonte2.setTextAppearance(R.style.TituloItem);
                                        liFormSecuenciaEstratiSueloR.addView(tvSecuenciaEstratiHorizonte2);

                                        String tagElemento = ElementoSueloResidualUGSR.getTagelemento();
                                        int idStringArrayElemento = ElementoSueloResidualUGSR.getIdStringArray();

                                        Resources res = getResources();
                                        String[] opciones = res.getStringArray(idStringArrayElemento);
                                        for (int i = 0; i < opciones.length; i++) {
                                            int aux = i + 1;
                                            LinearLayout liFormSecuenciaEstratiSueloR1 = new LinearLayout(mcont);
                                            liFormSecuenciaEstratiSueloR1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            liFormSecuenciaEstratiSueloR1.setOrientation(LinearLayout.HORIZONTAL);

                                            TextView tvSecuenciaEstratiSueloR = new TextView(mcont);
                                            tvSecuenciaEstratiSueloR.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            tvSecuenciaEstratiSueloR.setText(opciones[i]);
                                            tvSecuenciaEstratiSueloR.setTextAppearance(R.style.TituloItem);
                                            liFormSecuenciaEstratiSueloR1.addView(tvSecuenciaEstratiSueloR);

                                            EditText etSecuenciaEstratiSueloROrden = new EditText(mcont);
                                            etSecuenciaEstratiSueloROrden.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            etSecuenciaEstratiSueloROrden.setHint("Orden");
                                            etSecuenciaEstratiSueloROrden.setEms(10);
                                            etSecuenciaEstratiSueloROrden.setTag(tagElemento+ aux +"orden");
                                            ListaEditText.get(idLinear).add(etSecuenciaEstratiSueloROrden);
                                            liFormSecuenciaEstratiSueloR1.addView(etSecuenciaEstratiSueloROrden);

                                            EditText etSecuenciaEstratiSueloREspesor = new EditText(mcont);
                                            etSecuenciaEstratiSueloREspesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            etSecuenciaEstratiSueloREspesor.setHint("Espesor (m)");
                                            etSecuenciaEstratiSueloREspesor.setEms(10);
                                            etSecuenciaEstratiSueloREspesor.setTag(tagElemento+ aux +"espesor");
                                            ListaEditText.get(idLinear).add(etSecuenciaEstratiSueloREspesor);
                                            liFormSecuenciaEstratiSueloR1.addView(etSecuenciaEstratiSueloREspesor);

                                            liFormSecuenciaEstratiSueloR.addView(liFormSecuenciaEstratiSueloR1);

                                        }

                                    }
                                    else{
                                        liFormSecuenciaEstratiSueloR.removeAllViews();
                                    }

                                }
                            });
                        }
                    }

                }
                if (claseElemento.equals("titulo")){
                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloFormato);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);
                }
                if (claseElemento.equals("litologias")){
                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloItem);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                    LinearLayout liFormLitologias = new LinearLayout(mcont);
                    liFormLitologias.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liFormLitologias.setOrientation(LinearLayout.HORIZONTAL);
                    liForm.addView(liFormLitologias);
                    for (int j = 1; j < 3 ; j++){
                        CheckBox checkbox = new CheckBox(mcont);
                        checkbox.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        String aux = j+"";
                        checkbox.setText(aux);
                        checkbox.setTag(tagElemento+j+"exist_"+idLinear);
                        ListaCheckBox.get(idLinear).add(checkbox);
                        liFormLitologias.addView(checkbox);

                        EditText etLitologia = new EditText(mcont);
                        etLitologia.setLayoutParams(new ActionBar.LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etLitologia.setHint("Espesor (m)");
                        etLitologia.setEms(10);
                        etLitologia.setTag(tagElemento+j+"espesor");
                        ListaEditText.get(idLinear).add(etLitologia);
                        liFormLitologias.addView(etLitologia);

                        if (j == 1){
                            checkbox.setChecked(true);
                        }
                        if (j == 2){
                            checkbox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int aux = Integer.parseInt(view.getTag().toString().split("_")[1]);
                                    if(((CheckBox) view).isChecked()){
                                        for (int j = 0; j < ListaRadioBtn.get(aux).size() ; j++){
                                            ListaRadioBtn.get(aux).get(j).setEnabled(true);
                                            ListaRadioBtn.get(aux).get(j).setClickable(true);
                                        }

                                    } else {
                                        for (int j = 0; j < ListaRadioBtn.get(aux).size() ; j++){
                                            ListaRadioBtn.get(aux).get(j).setEnabled(false);
                                            ListaRadioBtn.get(aux).get(j).setClickable(false);
                                        }
                                    }
                                }
                            });
                        }
                    }

                }
                if (claseElemento.equals("radiobtn")){
                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloItem);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                    LinearLayout liradiobtnTitulo = new LinearLayout(mcont);
                    liradiobtnTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liradiobtnTitulo.setOrientation(LinearLayout.HORIZONTAL);

                    TextView pruebatext = new TextView(mcont);
                    pruebatext.setLayoutParams(new ActionBar.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT));
                    pruebatext.setText("2");
                    pruebatext.setTextAppearance(R.style.TituloItem);
                    pruebatext.setPadding(30, 20, 0, 0);
                    liradiobtnTitulo.addView(pruebatext);

                    TextView pruebatext1 = new TextView(mcont);
                    pruebatext1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    pruebatext1.setText("1");
                    pruebatext1.setTextAppearance(R.style.TituloItem);
                    pruebatext1.setPadding(30, 20, 0, 0);
                    liradiobtnTitulo.addView(pruebatext1);

                    liForm.addView(liradiobtnTitulo);

                    LinearLayout liradiobtn = new LinearLayout(mcont);
                    liradiobtn.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liradiobtn.setOrientation(LinearLayout.HORIZONTAL);

                    Resources res = getResources();
                    String[] opciones2 = res.getStringArray(idStringArrayElemento);

                    RadioGroup radioGroup2 = new RadioGroup(mcont);
                    radioGroup2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    radioGroup2.setTag(tagElemento+2);
                    for(String opt : opciones2) {
                        RadioButton nuevoRadio = new RadioButton(mcont);
                        LinearLayout.LayoutParams params = new RadioGroup.LayoutParams(
                                100,
                                RadioGroup.LayoutParams.WRAP_CONTENT);
                        nuevoRadio.setLayoutParams(params);
                        //nuevoRadio.setText(marca);
                        nuevoRadio.setTag(opt);
                        nuevoRadio.setClickable(false);
                        nuevoRadio.setEnabled(false);
                        ListaRadioBtn.get(idLinear).add(nuevoRadio);
                        radioGroup2.addView(nuevoRadio);
                    }
                    RadioButton primerRadio2 = (RadioButton) radioGroup2.getChildAt(0);
                    primerRadio2.setChecked(true);
                    liradiobtn.addView(radioGroup2);
                    ListaRadioGrp.get(idLinear).add(radioGroup2);

                    Resources res1 = getResources();
                    String[] opciones1 = res.getStringArray(idStringArrayElemento);

                    RadioGroup radioGroup1 = new RadioGroup(mcont);
                    radioGroup1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    radioGroup1.setTag(tagElemento+1);
                    for(String opt : opciones1) {
                        RadioButton nuevoRadio = new RadioButton(mcont);
                        LinearLayout.LayoutParams params = new RadioGroup.LayoutParams(
                                RadioGroup.LayoutParams.WRAP_CONTENT,
                                RadioGroup.LayoutParams.WRAP_CONTENT);
                        nuevoRadio.setLayoutParams(params);
                        nuevoRadio.setText(opt);
                        nuevoRadio.setTag(opt);
                        radioGroup1.addView(nuevoRadio);
                    }
                    RadioButton primerRadio1 = (RadioButton) radioGroup1.getChildAt(0);
                    primerRadio1.setChecked(true);
                    liradiobtn.addView(radioGroup1);
                    ListaRadioGrp.get(idLinear).add(radioGroup1);

                    liForm.addView(liradiobtn);
                }

            }

            //------------> LEVANTAMIENTO DE DISCONTINUIDADES

            TextView tvLevantamientoDisc = new TextView(mcont);
            tvLevantamientoDisc.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvLevantamientoDisc.setText("LEVANTAMIENTO DE DISCONTINUIDADES");
            tvLevantamientoDisc.setTextAppearance(R.style.TituloFormato);
            tvLevantamientoDisc.setPadding(0, mtop, 0, 20);
            liForm.addView(tvLevantamientoDisc);

            LinearLayout liFormDiscontinuidades = new LinearLayout(mcont);
            liFormDiscontinuidades.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormDiscontinuidades.setOrientation(LinearLayout.VERTICAL);
            liForm.addView(liFormDiscontinuidades);

            Button bAnadirDiscont = new Button(mcont);
            bAnadirDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bAnadirDiscont.setText("Añadir Discontinuidad");
            bAnadirDiscont.setTag(idLinear);
            bAnadirDiscont.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.plus_circle, 0);
            bAnadirDiscont.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listContDiscontinuidades.set(Integer.parseInt(v.getTag().toString()), listContDiscontinuidades.get(Integer.parseInt(v.getTag().toString())) + 1);

                    Button bDiscont = new Button(mcont);
                    bDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    bDiscont.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                    bDiscont.setText("Discontinuidad "+ listContDiscontinuidades.get(Integer.parseInt(v.getTag().toString())));
                    bDiscont.setTag(Integer.parseInt(v.getTag().toString()));
                    liFormDiscontinuidades.addView(bDiscont);


                    LinearLayout liDiscontinuidades = new LinearLayout(mcont);
                    liDiscontinuidades.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liDiscontinuidades.setOrientation(LinearLayout.VERTICAL);
                    liDiscontinuidades.setBackgroundColor(0x22222200);
                    liDiscontinuidades.setVisibility(View.GONE);
                    liFormDiscontinuidades.addView(liDiscontinuidades);
                    ListaDiscontinuidades.get(Integer.parseInt(v.getTag().toString())).add(liDiscontinuidades);

                    bDiscont.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View vi) {

                            if (liDiscontinuidades.getVisibility() == View.VISIBLE) {
                                ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                animation.setDuration(220);
                                animation.setFillAfter(false);
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
                                animation.setFillAfter(false);
                                liDiscontinuidades.startAnimation(animation);
                                liDiscontinuidades.setVisibility(View.VISIBLE);
                                bDiscont.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                            }

                        }
                    });


                    TextView tvNameDiscont = new TextView(mcont);
                    tvNameDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    String nuevo = "Discontinuidad "+ listContDiscontinuidades.get(Integer.parseInt(v.getTag().toString()));
                    tvNameDiscont.setText(nuevo);
                    tvNameDiscont.setTextAppearance(R.style.TituloFormato);
                    tvNameDiscont.setPadding(0, 100, 0, 50);
                    liDiscontinuidades.addView(tvNameDiscont);

                    for (int i = 0; i < listaElementosUGSRDiscont.size(); i++){
                        ElementoFormato elementoActual = listaElementosUGSRDiscont.get(i);
                        String nombreElemento = elementoActual.getNombreelemento();
                        String hintElemento = elementoActual.getNombreelemento();
                        String claseElemento = elementoActual.getClaseelemento();
                        String tagElemento = elementoActual.getTagelemento();
                        int idStringArrayElemento = elementoActual.getIdStringArray();
                        int aux = ListaDiscontinuidades.get(Integer.parseInt(v.getTag().toString())).size();

                        if (claseElemento.equals("edittext")){
                            TextView tvGenerico = new TextView(mcont);
                            tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            tvGenerico.setText(nombreElemento);
                            tvGenerico.setTextAppearance(R.style.TituloItem);
                            tvGenerico.setPadding(0, mtop, 0, 0);
                            liDiscontinuidades.addView(tvGenerico);

                            EditText etGenerico = new EditText(mcont);
                            etGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            etGenerico.setHint(hintElemento);
                            etGenerico.setEms(10);
                            etGenerico.setTag(tagElemento+aux);
                            ListaEditText.get(Integer.parseInt(v.getTag().toString())).add(etGenerico);
                            liDiscontinuidades.addView(etGenerico);
                        }
                        if (claseElemento.equals("spinner")){
                            TextView tvGenerico = new TextView(mcont);
                            tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            tvGenerico.setText(nombreElemento);
                            tvGenerico.setTextAppearance(R.style.TituloItem);
                            tvGenerico.setPadding(0, mtop, 0, 0);
                            liDiscontinuidades.addView(tvGenerico);

                            Spinner sGenerico = new Spinner(mcont);
                            sGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mcont, idStringArrayElemento, android.R.layout.simple_spinner_item);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            sGenerico.setAdapter(adapter);
                            sGenerico.setTag(tagElemento+aux);
                            ListaSpinner.get(Integer.parseInt(v.getTag().toString())).add(sGenerico);
                            liDiscontinuidades.addView(sGenerico);
                        }
                    }

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

            LinearLayout liFormFotosAnexasSuelos = new LinearLayout(mcont);
            liFormFotosAnexasSuelos.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormFotosAnexasSuelos.setOrientation(LinearLayout.VERTICAL);
            liForm.addView(liFormFotosAnexasSuelos);


            Button bFotosAnexasSuelos = new Button(mcont);
            bFotosAnexasSuelos.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bFotosAnexasSuelos.setText("Añadir Foto");
            bFotosAnexasSuelos.setTag(idLinear);
            bFotosAnexasSuelos.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.plus_circle, 0);
            bFotosAnexasSuelos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listContFotosAnexas.set(Integer.parseInt(v.getTag().toString()), listContFotosAnexas.get(Integer.parseInt(v.getTag().toString())) + 1);

                    Button bFotosAnexasAcordion = new Button(mcont);
                    bFotosAnexasAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    bFotosAnexasAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                    String foto = "Foto "+ listContFotosAnexas.get(Integer.parseInt(v.getTag().toString()));
                    bFotosAnexasAcordion.setText(foto);
                    bFotosAnexasAcordion.setTag(Integer.parseInt(v.getTag().toString()));
                    liFormFotosAnexasSuelos.addView(bFotosAnexasAcordion);

                    LinearLayout liFotosAnexas = new LinearLayout(mcont);
                    liFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liFotosAnexas.setOrientation(LinearLayout.VERTICAL);
                    liFotosAnexas.setBackgroundColor(0x22222200);
                    liFotosAnexas.setVisibility(View.GONE);
                    liFormFotosAnexasSuelos.addView(liFotosAnexas);
                    ListaFotosAnexas.get(Integer.parseInt(v.getTag().toString())).add(liFotosAnexas);

                    bFotosAnexasAcordion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (liFotosAnexas.getVisibility() == View.VISIBLE) {
                                ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                animation.setDuration(220);
                                animation.setFillAfter(false);
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
                                animation.setFillAfter(false);
                                liFotosAnexas.startAnimation(animation);
                                liFotosAnexas.setVisibility(View.VISIBLE);
                                bFotosAnexasAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                            }

                        }
                    });

                    TextView tvNameFotos = new TextView(mcont);
                    tvNameFotos.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    String foto1 = "Foto "+ listContFotosAnexas.get(Integer.parseInt(v.getTag().toString()));
                    tvNameFotos.setText(foto1);
                    tvNameFotos.setTextAppearance(R.style.TituloFormato);
                    tvNameFotos.setPadding(0, 100, 0, 50);
                    liFotosAnexas.addView(tvNameFotos);


                    for (int i = 0; i < listaElementosUGSFotosAnexas.size(); i++){
                        ElementoFormato elementoActual = listaElementosUGSFotosAnexas.get(i);
                        String nombreElemento = elementoActual.getNombreelemento();
                        String hintElemento = elementoActual.getNombreelemento();
                        String claseElemento = elementoActual.getClaseelemento();
                        String tagElemento = elementoActual.getTagelemento();
                        int idStringArrayElemento = elementoActual.getIdStringArray();
                        int aux = ListaFotosAnexas.get(Integer.parseInt(v.getTag().toString())).size();

                        if (claseElemento.equals("edittext")){
                            TextView tvGenerico = new TextView(mcont);
                            tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            tvGenerico.setText(nombreElemento);
                            tvGenerico.setTextAppearance(R.style.TituloItem);
                            tvGenerico.setPadding(0, 40, 0, 0);
                            liFotosAnexas.addView(tvGenerico);

                            EditText etGenerico = new EditText(mcont);
                            etGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            etGenerico.setHint(hintElemento);
                            etGenerico.setEms(10);
                            etGenerico.setTag(tagElemento+aux);
                            ListaEditText.get(Integer.parseInt(v.getTag().toString())).add(etGenerico);
                            liFotosAnexas.addView(etGenerico);
                        }
                    }

                }
            });
            liForm.addView(bFotosAnexasSuelos);

            listLiForm.add(liForm);
            liFormularios.addView(liForm);
        }

        if (formType.equals("UGS Suelos")) {

            Button bAcordion = new Button(mcont);
            bAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
            bAcordion.setText("Formato UGS Suelos");
            bAcordion.setTag(idLinear);
            listBtnAcordion.add(bAcordion);
            liFormularios.addView(bAcordion);

            LinearLayout liForm = new LinearLayout(mcont);
            liForm.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liForm.setOrientation(LinearLayout.VERTICAL);
            liForm.setBackgroundColor(0x33333300);
            //liForm.setVisibility(View.GONE);
            liForm.setTag(idLinear);

            bAcordion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("jaaj", "onClick: "+listFormularios);
                    if (listLiForm.get(Integer.parseInt(v.getTag().toString())).getVisibility() == View.VISIBLE) {
                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                        animation.setDuration(220);
                        animation.setFillAfter(false);
                        animation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                            }
                            @Override
                            public void onAnimationEnd(Animation animation) {
                                listLiForm.get(Integer.parseInt(v.getTag().toString())).setVisibility(View.GONE);
                                listBtnAcordion.get(Integer.parseInt(v.getTag().toString())).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                            }
                            @Override
                            public void onAnimationRepeat(Animation animation) {
                            }
                        });
                        listLiForm.get(Integer.parseInt(v.getTag().toString())).startAnimation(animation);

                    }
                    else {
                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                        animation.setDuration(220);
                        animation.setFillAfter(false);
                        listLiForm.get(Integer.parseInt(v.getTag().toString())).startAnimation(animation);
                        listLiForm.get(Integer.parseInt(v.getTag().toString())).setVisibility(View.VISIBLE);
                        listBtnAcordion.get(Integer.parseInt(v.getTag().toString())).setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
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
                    Log.d("jaaj", "BOrrar: "+listFormularios);
                    listLiForm.get(Integer.parseInt(v.getTag().toString())).removeAllViews();
                    liFormularios.removeView(listBtnAcordion.get(Integer.parseInt(v.getTag().toString())));
                    listFormularios.set(Integer.parseInt(v.getTag().toString()), "Ninguno");
                }
            });
            liForm.addView(bBorrarForm);

            Boolean auxFinos = false;
            Boolean auxGruesos = false;
            Boolean auxtodos2 = true;

            for (int i = 0; i < listaElementosUGSS.size(); i++) {
                ElementoFormato elementoActual = listaElementosUGSS.get(i);
                String nombreElemento = elementoActual.getNombreelemento();
                String hintElemento = elementoActual.getNombreelemento();
                String claseElemento = elementoActual.getClaseelemento();
                String tagElemento = elementoActual.getTagelemento();
                int idStringArrayElemento = elementoActual.getIdStringArray();


                if (claseElemento.equals("edittext")){
                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloItem);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                    EditText etGenerico = new EditText(mcont);
                    etGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    etGenerico.setHint(hintElemento);
                    etGenerico.setEms(10);
                    etGenerico.setTag(tagElemento);
                    ListaEditText.get(idLinear).add(etGenerico);
                    liForm.addView(etGenerico);
                }
                if (claseElemento.equals("spinner")){
                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloItem);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                    Spinner sGenerico = new Spinner(mcont);
                    sGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mcont, idStringArrayElemento, android.R.layout.simple_spinner_item);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    sGenerico.setAdapter(adapter);
                    sGenerico.setTag(tagElemento);
                    ListaSpinner.get(idLinear).add(sGenerico);
                    liForm.addView(sGenerico);
                }
                if (claseElemento.equals("secuenciaestrati")){
                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloFormato);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                    Resources res = getResources();
                    String[] opciones = res.getStringArray(idStringArrayElemento);
                    int secuEstratiWidth = 420;
                    int secuEstratiOrdenWidth = 200;
                    int secuEstratiEspesorWidth = 300;

                    for (int j = 0; j < opciones.length ; j++) {
                        int aux = j + 1;
                        LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                        liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                        TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                        tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvSecuenciaEstratiOpt.setText(opciones[j]);
                        tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                        liFormSecuenciaEstrati.addView(tvSecuenciaEstratiOpt);

                        EditText etSecuenciaEstratiOpt = new EditText(mcont);
                        etSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiOpt.setHint("Orden");
                        etSecuenciaEstratiOpt.setEms(10);
                        etSecuenciaEstratiOpt.setTag(tagElemento+aux+"orden");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt);
                        liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt);

                        EditText etSecuenciaEstratiOpt1Espesor = new EditText(mcont);
                        etSecuenciaEstratiOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiOpt1Espesor.setHint("Espesor (m)");
                        etSecuenciaEstratiOpt1Espesor.setEms(10);
                        etSecuenciaEstratiOpt1Espesor.setTag(tagElemento+aux+"espesor");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt1Espesor);
                        liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt1Espesor);

                        liForm.addView(liFormSecuenciaEstrati);

                        if (opciones[j].equals("Suelo Residual")){
                            LinearLayout liFormSecuenciaEstratiSueloR = new LinearLayout(mcont);
                            liFormSecuenciaEstratiSueloR.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liFormSecuenciaEstratiSueloR.setOrientation(LinearLayout.VERTICAL);

                            liForm.addView(liFormSecuenciaEstratiSueloR);

                            etSecuenciaEstratiOpt.addTextChangedListener(new TextWatcher() {
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
                                        liFormSecuenciaEstratiSueloR.removeAllViews();
                                        int secuEstratiWidth = 450;
                                        int secuEstratiOrdenWidth = 200;
                                        int secuEstratiEspesorWidth = 300;

                                        TextView tvSecuenciaEstratiHorizonte2 = new TextView(mcont);
                                        tvSecuenciaEstratiHorizonte2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        tvSecuenciaEstratiHorizonte2.setText("Horizonte");
                                        tvSecuenciaEstratiHorizonte2.setTextAppearance(R.style.TituloItem);
                                        liFormSecuenciaEstratiSueloR.addView(tvSecuenciaEstratiHorizonte2);

                                        String tagElemento = ElementoSueloResidualUGSS.getTagelemento();
                                        int idStringArrayElemento = ElementoSueloResidualUGSS.getIdStringArray();

                                        Resources res = getResources();
                                        String[] opciones = res.getStringArray(idStringArrayElemento);
                                        for (int i = 0; i < opciones.length; i++) {
                                            int aux = i + 1;
                                            LinearLayout liFormSecuenciaEstratiSueloR1 = new LinearLayout(mcont);
                                            liFormSecuenciaEstratiSueloR1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            liFormSecuenciaEstratiSueloR1.setOrientation(LinearLayout.HORIZONTAL);

                                            TextView tvSecuenciaEstratiSueloR = new TextView(mcont);
                                            tvSecuenciaEstratiSueloR.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            tvSecuenciaEstratiSueloR.setText(opciones[i]);
                                            tvSecuenciaEstratiSueloR.setTextAppearance(R.style.TituloItem);
                                            liFormSecuenciaEstratiSueloR1.addView(tvSecuenciaEstratiSueloR);

                                            EditText etSecuenciaEstratiSueloROrden = new EditText(mcont);
                                            etSecuenciaEstratiSueloROrden.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            etSecuenciaEstratiSueloROrden.setHint("Orden");
                                            etSecuenciaEstratiSueloROrden.setEms(10);
                                            etSecuenciaEstratiSueloROrden.setTag(tagElemento+ aux +"orden");
                                            ListaEditText.get(idLinear).add(etSecuenciaEstratiSueloROrden);
                                            liFormSecuenciaEstratiSueloR1.addView(etSecuenciaEstratiSueloROrden);

                                            EditText etSecuenciaEstratiSueloREspesor = new EditText(mcont);
                                            etSecuenciaEstratiSueloREspesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            etSecuenciaEstratiSueloREspesor.setHint("Espesor (m)");
                                            etSecuenciaEstratiSueloREspesor.setEms(10);
                                            etSecuenciaEstratiSueloREspesor.setTag(tagElemento+ aux +"espesor");
                                            ListaEditText.get(idLinear).add(etSecuenciaEstratiSueloREspesor);
                                            liFormSecuenciaEstratiSueloR1.addView(etSecuenciaEstratiSueloREspesor);

                                            liFormSecuenciaEstratiSueloR.addView(liFormSecuenciaEstratiSueloR1);

                                        }

                                    }
                                    else{
                                        liFormSecuenciaEstratiSueloR.removeAllViews();
                                    }

                                }
                            });
                        }
                    }

                }
                if (claseElemento.equals("titulo")){
                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloFormato);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);
                }
                if (claseElemento.equals("litologias")){
                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloItem);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                    LinearLayout liFormLitologias = new LinearLayout(mcont);
                    liFormLitologias.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liFormLitologias.setOrientation(LinearLayout.HORIZONTAL);
                    liForm.addView(liFormLitologias);
                    for (int j = 1; j < 3 ; j++){
                        CheckBox checkbox = new CheckBox(mcont);
                        checkbox.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        String aux = j+"";
                        checkbox.setText(aux);
                        checkbox.setTag(tagElemento+j+"exist_"+idLinear);
                        ListaCheckBox.get(idLinear).add(checkbox);
                        liFormLitologias.addView(checkbox);

                        EditText etLitologia = new EditText(mcont);
                        etLitologia.setLayoutParams(new ActionBar.LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etLitologia.setHint("Espesor (m)");
                        etLitologia.setEms(10);
                        etLitologia.setTag(tagElemento+j+"espesor");
                        ListaEditText.get(idLinear).add(etLitologia);
                        liFormLitologias.addView(etLitologia);

                        if (j == 1){
                            checkbox.setChecked(true);
                        }
                        if (j == 2){
                            checkbox.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    int aux = Integer.parseInt(view.getTag().toString().split("_")[1]);
                                    if(((CheckBox) view).isChecked()){
                                        for (int j = 0; j < ListaRadioBtn.get(aux).size() ; j++){
                                            ListaRadioBtn.get(aux).get(j).setEnabled(true);
                                            ListaRadioBtn.get(aux).get(j).setClickable(true);
                                        }

                                    } else {
                                        for (int j = 0; j < ListaRadioBtn.get(aux).size() ; j++){
                                            ListaRadioBtn.get(aux).get(j).setEnabled(false);
                                            ListaRadioBtn.get(aux).get(j).setClickable(false);
                                        }
                                    }
                                }
                            });
                        }
                    }

                }
                if (claseElemento.equals("porcentajes")){
                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloItem);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                    LinearLayout liradiobtnTitulo = new LinearLayout(mcont);
                    liradiobtnTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liradiobtnTitulo.setOrientation(LinearLayout.HORIZONTAL);

                    TextView pruebatext = new TextView(mcont);
                    pruebatext.setLayoutParams(new ActionBar.LayoutParams(400, ViewGroup.LayoutParams.WRAP_CONTENT));
                    pruebatext.setText("2");
                    pruebatext.setTextAppearance(R.style.TituloItem);
                    pruebatext.setPadding(345, 20, 0, 0);
                    liradiobtnTitulo.addView(pruebatext);

                    TextView pruebatext1 = new TextView(mcont);
                    pruebatext1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    pruebatext1.setText("1");
                    pruebatext1.setTextAppearance(R.style.TituloItem);
                    pruebatext1.setPadding(30, 20, 0, 0);
                    liradiobtnTitulo.addView(pruebatext1);

                    liForm.addView(liradiobtnTitulo);


                    Resources res = getResources();
                    String[] opciones = res.getStringArray(idStringArrayElemento);

                    for (int j = 0; j < opciones.length; j++){

                        String tag = tagElemento.toString().split("_")[j];

                        LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                        liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                        TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                        tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvSecuenciaEstratiOpt.setText(opciones[j]);
                        tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                        liFormSecuenciaEstrati.addView(tvSecuenciaEstratiOpt);

                        EditText etSecuenciaEstratiOpt = new EditText(mcont);
                        etSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiOpt.setEms(10);
                        etSecuenciaEstratiOpt.setTag(tag+2);
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt);
                        liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt);

                        EditText etSecuenciaEstratiOpt1Espesor = new EditText(mcont);
                        etSecuenciaEstratiOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiOpt1Espesor.setEms(10);
                        etSecuenciaEstratiOpt1Espesor.setTag(tag+1);
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt1Espesor);
                        liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt1Espesor);

                        liForm.addView(liFormSecuenciaEstrati);
                    }


                }
                if (claseElemento.equals("radiobtn")){
                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloItem);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                    if (nombreElemento.equals("RESISTENCIA AL CORTE NO DRENADO kN/m2 (CONSISTENCIA)")){
                        auxFinos = true;
                    }
                    if (nombreElemento.equals("Forma de la Matriz")){
                        auxFinos = false;
                        auxtodos2 = false;
                        auxGruesos = true;
                    }

                    LinearLayout liradiobtnTitulo = new LinearLayout(mcont);
                    liradiobtnTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liradiobtnTitulo.setOrientation(LinearLayout.HORIZONTAL);

                    TextView pruebatext = new TextView(mcont);
                    pruebatext.setLayoutParams(new ActionBar.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT));
                    pruebatext.setText("2");
                    pruebatext.setTextAppearance(R.style.TituloItem);
                    pruebatext.setPadding(30, 20, 0, 0);
                    liradiobtnTitulo.addView(pruebatext);

                    TextView pruebatext1 = new TextView(mcont);
                    pruebatext1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    pruebatext1.setText("1");
                    pruebatext1.setTextAppearance(R.style.TituloItem);
                    pruebatext1.setPadding(30, 20, 0, 0);
                    liradiobtnTitulo.addView(pruebatext1);

                    liForm.addView(liradiobtnTitulo);

                    LinearLayout liradiobtn = new LinearLayout(mcont);
                    liradiobtn.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liradiobtn.setOrientation(LinearLayout.HORIZONTAL);

                    Resources res = getResources();
                    String[] opciones2 = res.getStringArray(idStringArrayElemento);

                    RadioGroup radioGroup2 = new RadioGroup(mcont);
                    radioGroup2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    radioGroup2.setTag(tagElemento+2);
                    for(String opt : opciones2) {
                        RadioButton nuevoRadio = new RadioButton(mcont);
                        LinearLayout.LayoutParams params = new RadioGroup.LayoutParams(
                                100,
                                RadioGroup.LayoutParams.WRAP_CONTENT);
                        nuevoRadio.setLayoutParams(params);
                        //nuevoRadio.setText(marca);
                        nuevoRadio.setTag(opt);
                        nuevoRadio.setClickable(false);
                        nuevoRadio.setEnabled(false);
                        if (auxtodos2){
                            ListaRadioBtn.get(idLinear).add(nuevoRadio);
                        }
                        if (auxFinos){
                            ListaRadioBtn2Finos.get(idLinear).add(nuevoRadio);
                        }
                        if (auxGruesos){
                            ListaRadioBtn2Gruesos.get(idLinear).add(nuevoRadio);
                        }
                        radioGroup2.addView(nuevoRadio);
                    }


                    Resources res1 = getResources();
                    String[] opciones1 = res.getStringArray(idStringArrayElemento);

                    RadioGroup radioGroup1 = new RadioGroup(mcont);
                    radioGroup1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    radioGroup1.setTag(tagElemento+1);
                    for(String opt : opciones1) {
                        RadioButton nuevoRadio = new RadioButton(mcont);
                        LinearLayout.LayoutParams params = new RadioGroup.LayoutParams(
                                RadioGroup.LayoutParams.WRAP_CONTENT,
                                RadioGroup.LayoutParams.WRAP_CONTENT);
                        nuevoRadio.setLayoutParams(params);
                        nuevoRadio.setText(opt);
                        nuevoRadio.setTag(opt);
                        if (auxFinos){
                            ListaRadioBtn1Finos.get(idLinear).add(nuevoRadio);
                        }
                        if (auxGruesos){
                            ListaRadioBtn1Gruesos.get(idLinear).add(nuevoRadio);
                        }
                        radioGroup1.addView(nuevoRadio);
                    }

                    RadioButton primerRadio2 = (RadioButton) radioGroup2.getChildAt(0);
                    primerRadio2.setChecked(true);
                    liradiobtn.addView(radioGroup2);
                    ListaRadioGrp.get(idLinear).add(radioGroup2);

                    RadioButton primerRadio1 = (RadioButton) radioGroup1.getChildAt(0);
                    primerRadio1.setChecked(true);
                    liradiobtn.addView(radioGroup1);
                    ListaRadioGrp.get(idLinear).add(radioGroup1);

                    Log.d("jaaja", "onCheckedChangedfino: "+auxFinos);
                    Log.d("jaaja", "onCheckedChangedgrue: "+auxGruesos);

                    liForm.addView(liradiobtn);


                    if (nombreElemento.equals("Orientacion de los Clastos") || nombreElemento.equals("Orientación de la Matriz")){
                        LinearLayout liFormdirimbri = new LinearLayout(mcont);
                        liFormdirimbri.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormdirimbri.setOrientation(LinearLayout.VERTICAL);
                        LinearLayout liFormdirimbri1 = new LinearLayout(mcont);
                        liFormdirimbri1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormdirimbri1.setOrientation(LinearLayout.VERTICAL);

                        liForm.addView(liFormdirimbri1);
                        liForm.addView(liFormdirimbri);

                        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            public void onCheckedChanged(RadioGroup group, int checkedId)
                            {
//                                Log.d("jaaja", "onCheckedChanged: "+group.getTag());
//                                Log.d("jaaja", "onCheckedChanged: "+checkedId);
                                // This will get the radiobutton that has changed in its check state
                                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                                // This puts the value (true/false) into the variable
                                boolean isChecked = checkedRadioButton.isChecked();
//                                Log.d("jaaja", "onCheckedChanged: "+checkedRadioButton.getText());
                                // If the radiobutton that has changed in check state is now checked...
                                if (isChecked && checkedRadioButton.getText().equals("Imbricado"))
                                {
                                    String auxTag;
                                    if (group.getTag().toString().equals("orientacion1")){
                                        auxTag = "dirimbricacion1";
                                    }else{
                                        auxTag = "dirimbricacionmatriz1";
                                    }
                                    TextView DirImbricacion1 = new TextView(mcont);
                                    DirImbricacion1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                                    DirImbricacion1.setText("Dirección de la Imbricación Litología 1");
                                    DirImbricacion1.setTextAppearance(R.style.TituloItem);
                                    DirImbricacion1.setPadding(0, mtop, 0, 0);
                                    liFormdirimbri1.addView(DirImbricacion1);

                                    EditText etDirImbricacion1 = new EditText(mcont);
                                    etDirImbricacion1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    etDirImbricacion1.setHint("Dirección de la Imbricación Litología 1");
                                    etDirImbricacion1.setEms(10);
                                    etDirImbricacion1.setTag(auxTag);
                                    ListaEditText.get(idLinear).add(etDirImbricacion1);
                                    liFormdirimbri1.addView(etDirImbricacion1);
                                }
                                else{
                                    liFormdirimbri1.removeAllViews();
                                }
                            }
                        });
                        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            public void onCheckedChanged(RadioGroup group, int checkedId)
                            {
//                                Log.d("jaaja", "onCheckedChanged: "+group.getTag());
//                                Log.d("jaaja", "onCheckedChanged: "+checkedId);
                                // This will get the radiobutton that has changed in its check state
                                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                                // This puts the value (true/false) into the variable
                                boolean isChecked = checkedRadioButton.isChecked();
                                // If the radiobutton that has changed in check state is now checked...
//                                Log.d("jaaja", "onCheckedChanged: "+checkedRadioButton.getText());
                                if (isChecked && checkedRadioButton.getTag().equals("Imbricado"))
                                {
                                    String auxTag;
                                    if (group.getTag().toString().equals("orientacion2")){
                                        auxTag = "dirimbricacion2";
                                    }else{
                                        auxTag = "dirimbricacionmatriz2";
                                    }
                                    TextView DirImbricacion1 = new TextView(mcont);
                                    DirImbricacion1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                                    DirImbricacion1.setText("Dirección de la Imbricación Litología 2");
                                    DirImbricacion1.setTextAppearance(R.style.TituloItem);
                                    DirImbricacion1.setPadding(0, mtop, 0, 0);
                                    liFormdirimbri.addView(DirImbricacion1);

                                    EditText etDirImbricacion1 = new EditText(mcont);
                                    etDirImbricacion1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    etDirImbricacion1.setHint("Dirección de la Imbricación Litología 2");
                                    etDirImbricacion1.setEms(10);
                                    etDirImbricacion1.setTag(auxTag);
                                    ListaEditText.get(idLinear).add(etDirImbricacion1);
                                    liFormdirimbri.addView(etDirImbricacion1);
                                }
                                else{
                                    liFormdirimbri.removeAllViews();
                                }
                            }
                        });
                    }

                    if (nombreElemento.equals("Granulometría de la Matriz")){

                        for (int j = 0; j < ListaRadioBtn1Gruesos.get(idLinear).size() ; j++){
                            ListaRadioBtn1Gruesos.get(idLinear).get(j).setEnabled(false);
                            ListaRadioBtn1Gruesos.get(idLinear).get(j).setClickable(false);
                        }
                        radioGroup1.setTag(tagElemento+1+"_"+idLinear);
                        radioGroup2.setTag(tagElemento+2+"_"+idLinear);

                        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            public void onCheckedChanged(RadioGroup group, int checkedId)
                            {
                                // This will get the radiobutton that has changed in its check state
                                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                                // This puts the value (true/false) into the variable
                                boolean isChecked = checkedRadioButton.isChecked();
                                // If the radiobutton that has changed in check state is now checked...
                                int aux = Integer.parseInt(group.getTag().toString().split("_")[1]);
                                if (checkedRadioButton.getText().equals("Finos (Limos-Arcillas Menores de 0,075 mm)"))
                                {
                                    for (int j = 0; j < ListaRadioBtn1Finos.get(aux).size() ; j++){
                                        ListaRadioBtn1Finos.get(aux).get(j).setEnabled(true);
                                        ListaRadioBtn1Finos.get(aux).get(j).setClickable(true);
                                    }
                                    for (int j = 0; j < ListaRadioBtn1Gruesos.get(aux).size() ; j++){
                                        ListaRadioBtn1Gruesos.get(aux).get(j).setEnabled(false);
                                        ListaRadioBtn1Gruesos.get(aux).get(j).setClickable(false);
                                    }
                                }
                                else {
                                    for (int j = 0; j < ListaRadioBtn1Finos.get(aux).size() ; j++){
                                        ListaRadioBtn1Finos.get(aux).get(j).setEnabled(false);
                                        ListaRadioBtn1Finos.get(aux).get(j).setClickable(false);
                                    }
                                    for (int j = 0; j < ListaRadioBtn1Gruesos.get(aux).size() ; j++){
                                        ListaRadioBtn1Gruesos.get(aux).get(j).setEnabled(true);
                                        ListaRadioBtn1Gruesos.get(aux).get(j).setClickable(true);
                                    }
                                }
                            }
                        });
                        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                            public void onCheckedChanged(RadioGroup group, int checkedId)
                            {
                                // This will get the radiobutton that has changed in its check state
                                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
                                // This puts the value (true/false) into the variable
                                boolean isChecked = checkedRadioButton.isChecked();
                                // If the radiobutton that has changed in check state is now checked...

                                int aux = Integer.parseInt(group.getTag().toString().split("_")[1]);

                                if (checkedId == 71)
                                {
                                    for (int j = 0; j < ListaRadioBtn2Finos.get(aux).size() ; j++){
                                        ListaRadioBtn2Finos.get(aux).get(j).setEnabled(true);
                                        ListaRadioBtn2Finos.get(aux).get(j).setClickable(true);
                                    }
                                    for (int j = 0; j < ListaRadioBtn2Gruesos.get(aux).size() ; j++){
                                        ListaRadioBtn2Gruesos.get(aux).get(j).setEnabled(false);
                                        ListaRadioBtn2Gruesos.get(aux).get(j).setClickable(false);
                                    }
                                }
                                else {
                                    for (int j = 0; j < ListaRadioBtn2Finos.get(aux).size() ; j++){
                                        ListaRadioBtn2Finos.get(aux).get(j).setEnabled(false);
                                        ListaRadioBtn2Finos.get(aux).get(j).setClickable(false);
                                    }
                                    for (int j = 0; j < ListaRadioBtn2Gruesos.get(aux).size() ; j++){
                                        ListaRadioBtn2Gruesos.get(aux).get(j).setEnabled(true);
                                        ListaRadioBtn2Gruesos.get(aux).get(j).setClickable(true);
                                    }
                                }
                            }
                        });

                    }

                    if (nombreElemento.equals("Compacidad de la Matriz")) {

                        for (int j = 0; j < ListaRadioBtn1Gruesos.get(idLinear).size(); j++) {
                            ListaRadioBtn1Gruesos.get(idLinear).get(j).setEnabled(false);
                            ListaRadioBtn1Gruesos.get(idLinear).get(j).setClickable(false);
                        }
                    }

                }

            }


            //------------> Fotografías Anexas

            TextView tvFotosAnexas = new TextView(mcont);
            tvFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvFotosAnexas.setText("Fotografías Anexas");
            tvFotosAnexas.setTextAppearance(R.style.TituloFormato);
            tvFotosAnexas.setPadding(0, mtop, 0, 20);
            liForm.addView(tvFotosAnexas);

            LinearLayout liFormFotosAnexasSuelos = new LinearLayout(mcont);
            liFormFotosAnexasSuelos.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormFotosAnexasSuelos.setOrientation(LinearLayout.VERTICAL);
            liForm.addView(liFormFotosAnexasSuelos);


            Button bFotosAnexasSuelos = new Button(mcont);
            bFotosAnexasSuelos.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bFotosAnexasSuelos.setText("Añadir Foto");
            bFotosAnexasSuelos.setTag(idLinear);
            bFotosAnexasSuelos.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.plus_circle, 0);
            bFotosAnexasSuelos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listContFotosAnexas.set(Integer.parseInt(v.getTag().toString()), listContFotosAnexas.get(Integer.parseInt(v.getTag().toString())) + 1);

                    Button bFotosAnexasAcordion = new Button(mcont);
                    bFotosAnexasAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    bFotosAnexasAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                    String foto = "Foto "+ listContFotosAnexas.get(Integer.parseInt(v.getTag().toString()));
                    bFotosAnexasAcordion.setText(foto);
                    bFotosAnexasAcordion.setTag(Integer.parseInt(v.getTag().toString()));
                    liFormFotosAnexasSuelos.addView(bFotosAnexasAcordion);

                    LinearLayout liFotosAnexas = new LinearLayout(mcont);
                    liFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liFotosAnexas.setOrientation(LinearLayout.VERTICAL);
                    liFotosAnexas.setBackgroundColor(0x22222200);
                    liFotosAnexas.setVisibility(View.GONE);
                    liFormFotosAnexasSuelos.addView(liFotosAnexas);
                    ListaFotosAnexas.get(Integer.parseInt(v.getTag().toString())).add(liFotosAnexas);

                    bFotosAnexasAcordion.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (liFotosAnexas.getVisibility() == View.VISIBLE) {
                                ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                animation.setDuration(220);
                                animation.setFillAfter(false);
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
                                animation.setFillAfter(false);
                                liFotosAnexas.startAnimation(animation);
                                liFotosAnexas.setVisibility(View.VISIBLE);
                                bFotosAnexasAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                            }

                        }
                    });

                    TextView tvNameFotos = new TextView(mcont);
                    tvNameFotos.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    String foto1 = "Foto "+ listContFotosAnexas.get(Integer.parseInt(v.getTag().toString()));
                    tvNameFotos.setText(foto1);
                    tvNameFotos.setTextAppearance(R.style.TituloFormato);
                    tvNameFotos.setPadding(0, 100, 0, 50);
                    liFotosAnexas.addView(tvNameFotos);


                    for (int i = 0; i < listaElementosUGSFotosAnexas.size(); i++){
                        ElementoFormato elementoActual = listaElementosUGSFotosAnexas.get(i);
                        String nombreElemento = elementoActual.getNombreelemento();
                        String hintElemento = elementoActual.getNombreelemento();
                        String claseElemento = elementoActual.getClaseelemento();
                        String tagElemento = elementoActual.getTagelemento();
                        int idStringArrayElemento = elementoActual.getIdStringArray();
                        int aux = ListaFotosAnexas.get(Integer.parseInt(v.getTag().toString())).size();

                        if (claseElemento.equals("edittext")){
                            TextView tvGenerico = new TextView(mcont);
                            tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            tvGenerico.setText(nombreElemento);
                            tvGenerico.setTextAppearance(R.style.TituloItem);
                            tvGenerico.setPadding(0, 40, 0, 0);
                            liFotosAnexas.addView(tvGenerico);

                            EditText etGenerico = new EditText(mcont);
                            etGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            etGenerico.setHint(hintElemento);
                            etGenerico.setEms(10);
                            etGenerico.setTag(tagElemento+aux);
                            ListaEditText.get(Integer.parseInt(v.getTag().toString())).add(etGenerico);
                            liFotosAnexas.addView(etGenerico);
                        }
                    }

                }
            });
            liForm.addView(bFotosAnexasSuelos);


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
            subida = false;
            databaseReference.child("EstacionesCampo/cont/cont").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if (!task.isSuccessful()) {
                        Log.e("firebase", "Error Getting Data", task.getException());
                        Toast.makeText(mcont, "Hubo un error que no permitió la carga de sus datos, vuelva a intentarlo\n", Toast.LENGTH_LONG).show();

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
                                    subida = true;
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


                                    databaseReference.child("EstacionesCampo/estacion_"+cont).setValue(nuevaEstacion);

                                    JSONObject Formularios = form.getJSONObject("Formularios");
                                    JSONObject counts = Formularios.getJSONObject("counts");

                                    int contUGS_Rocas = Integer.parseInt(counts.getString("UGS_Rocas"));
                                    int contUGS_Suelos = Integer.parseInt(counts.getString("UGS_Suelos"));

                                    databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/count_UGS_Rocas").setValue(contUGS_Rocas);
                                    databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/count_UGS_Suelos").setValue(contUGS_Suelos);


                                    for (int j = 0; j < contUGS_Rocas; j++) {
                                        JSONObject FromatoAux = Formularios.getJSONObject("Form_UGS_Rocas_"+j);
                                        JSONObject SpinnersAux = FromatoAux.getJSONObject("Spinners");
                                        JSONObject EditTextsAux = FromatoAux.getJSONObject("EditText");
                                        JSONObject CheckBoxAux = FromatoAux.getJSONObject("CheckBox");
                                        JSONObject RadioGrpAux = FromatoAux.getJSONObject("RadioGrp");
                                        String municipios = SpinnersAux.getString("municipios");
                                        String claseaflor = SpinnersAux.getString("claseaflor");
                                        String gsi = SpinnersAux.getString("gsi");
                                        String fabrica1 = RadioGrpAux.getString("fabrica1");
                                        String fabrica2 = RadioGrpAux.getString("fabrica2");
                                        String humedad1 = RadioGrpAux.getString("humedad1");
                                        String humedad2 = RadioGrpAux.getString("humedad2");
                                        String tamanograno1 = RadioGrpAux.getString("tamañograno1");
                                        String tamanograno2 = RadioGrpAux.getString("tamañograno2");
                                        String gradometeo1 = RadioGrpAux.getString("gradometeo1");
                                        String gradometeo2 = RadioGrpAux.getString("gradometeo2");
                                        String resistenciacomp1 = RadioGrpAux.getString("resistenciacomp1");
                                        String resistenciacomp2 = RadioGrpAux.getString("resistenciacomp2");
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
                                        String litologiasasociadasopt1exist = CheckBoxAux.getString("litologiasasociadasopt1exist");
                                        String litologiasasociadasopt1espesor = EditTextsAux.getString("litologiasasociadasopt1espesor");
                                        String litologiasasociadasopt2exist = CheckBoxAux.getString("litologiasasociadasopt2exist");
                                        String litologiasasociadasopt2espesor = EditTextsAux.getString("litologiasasociadasopt2espesor");
                                        String nombreugs = EditTextsAux.getString("nombreugs");
                                        String color1 = EditTextsAux.getString("color1");
                                        String color2 = EditTextsAux.getString("color2");
                                        String composicionmineral1 = EditTextsAux.getString("composicionmineral1");
                                        String composicionmineral2 = EditTextsAux.getString("composicionmineral2");

                                        FormatUGSRocas nuevoFormatoUGSRocas = new FormatUGSRocas(noformato, municipios,  claseaflor,  gsi,  fabrica1,  fabrica2,  humedad1,  humedad2,  tamanograno1,  tamanograno2,  gradometeo1,  gradometeo2,  resistenciacomp1,  resistenciacomp2,  vereda,  noestacion,  secuenciaestratiopt1orden,  secuenciaestratiopt1espesor,  secuenciaestratiopt2orden,  secuenciaestratiopt2espesor,  secuenciaestratiopt3orden,  secuenciaestratiopt3espesor,  secuenciaestratiopt4orden,  secuenciaestratiopt4espesor,  secuenciaestratisuelor1orden,  secuenciaestratisuelor1espesor,  secuenciaestratisuelor2orden,  secuenciaestratisuelor2espesor,  secuenciaestratisuelor3orden,  secuenciaestratisuelor3espesor,  perfilmeteorizacion,  litologiasasociadasopt1exist,  litologiasasociadasopt1espesor,  litologiasasociadasopt2exist,  litologiasasociadasopt2espesor,  nombreugs,  color1,  color2,  composicionmineral1,  composicionmineral2);

                                        databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Rocas/Form_UGS_Rocas_"+j).setValue(nuevoFormatoUGSRocas);

                                        int contDiscont = Integer.parseInt(FromatoAux.getString("Discontinuidades"));
                                        databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Rocas/Form_UGS_Rocas_"+j+"/Discontinuidades/count").setValue(contDiscont);
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
                                            databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Rocas/Form_UGS_Rocas_"+j+"/Discontinuidades/Discont_"+k).setValue(nuevoFormatoDiscont);

                                        }

                                        int contFotosAnexas = Integer.parseInt(FromatoAux.getString("FotosAnexas"));
                                        databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Rocas/Form_UGS_Rocas_"+j+"/FotosAnexas/count").setValue(contFotosAnexas);
                                        for (int k = 1; k <= contFotosAnexas; k++) {

                                            String NombreFotosAnexas = EditTextsAux.getString("NombreFotosAnexas"+k);
                                            String DescriFotosAnexas = EditTextsAux.getString("DescriFotosAnexas"+k);

                                            FormatFotosAnexas nuevoFormatoFotosAnexas = new FormatFotosAnexas(NombreFotosAnexas, DescriFotosAnexas);
                                            databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Rocas/Form_UGS_Rocas_"+j+"/FotosAnexas/FotoAnexa_"+k).setValue(nuevoFormatoFotosAnexas);

                                        }

                                    }

                                    for (int j = 0; j < contUGS_Suelos; j++) {
                                        JSONObject FromatoAux = Formularios.getJSONObject("Form_UGS_Suelos_"+j);
                                        JSONObject SpinnersAux = FromatoAux.getJSONObject("Spinners");
                                        JSONObject EditTextsAux = FromatoAux.getJSONObject("EditText");
                                        JSONObject CheckBoxAux = FromatoAux.getJSONObject("CheckBox");
                                        JSONObject RadioGrpAux = FromatoAux.getJSONObject("RadioGrp");

                                        String municipios = SpinnersAux.getString("municipios");
                                        String claseaflor = SpinnersAux.getString("claseaflor");
                                        String estructurasoporte1 = RadioGrpAux.getString("estructurasoporte1");
                                        String estructurasoporte2 = RadioGrpAux.getString("estructurasoporte2");
                                        String condicionhumedad1 = RadioGrpAux.getString("condicionhumedad1");
                                        String condicionhumedad2 = RadioGrpAux.getString("condicionhumedad2");
                                        String estructurasrelictas1 = RadioGrpAux.getString("estructurasrelictas1");
                                        String estructurasrelictas2 = RadioGrpAux.getString("estructurasrelictas2");
                                        String granulometria1 = RadioGrpAux.getString("granulometria1");
                                        String granulometria2 = RadioGrpAux.getString("granulometria2");
                                        String forma1 = RadioGrpAux.getString("forma1");
                                        String forma2 = RadioGrpAux.getString("forma2");
                                        String redondez1 = RadioGrpAux.getString("redondez1");
                                        String redondez2 = RadioGrpAux.getString("redondez2");
                                        String orientacion1 = RadioGrpAux.getString("orientacion1");
                                        String orientacion2 = RadioGrpAux.getString("orientacion2");

                                        String dirimbricacion1 = "";
                                        String dirimbricacion2 = "";

                                        String elNuevoTexto = orientacion1;
                                        if (elNuevoTexto.equals("Imbricado")){
                                            dirimbricacion1 = EditTextsAux.getString("dirimbricacion1");
                                        }

                                        elNuevoTexto = orientacion2;
                                        if (elNuevoTexto.equals("Imbricado")){
                                            dirimbricacion2 = EditTextsAux.getString("dirimbricacion2");
                                        }

                                        String meteorizacionclastos1 = RadioGrpAux.getString("meteorizacionclastos1");
                                        String meteorizacionclastos2 = RadioGrpAux.getString("meteorizacionclastos2");
                                        String granulometriamatriz1 = RadioGrpAux.getString("granulometriamatriz1");
                                        String granulometriamatriz2 = RadioGrpAux.getString("granulometriamatriz2");
                                        String gradacion1 = RadioGrpAux.getString("gradacion1");
                                        String gradacion2 = RadioGrpAux.getString("gradacion2");
                                        String seleccion1 = RadioGrpAux.getString("seleccion1");
                                        String seleccion2 = RadioGrpAux.getString("seleccion2");
                                        String plasticidad1 = RadioGrpAux.getString("plasticidad1");
                                        String plasticidad2 = RadioGrpAux.getString("plasticidad2");

                                        String resiscorte1 = "";
                                        String formasuelosgruesos1 = "";
                                        String redondezsuelosgruesos1 = "";
                                        String orientacionsuelosgruesos1 = "";
                                        String dirimbricacionmatriz1 = "";
                                        String compacidadsuelosgruesos1 = "";

                                        resiscorte1 = RadioGrpAux.getString("resiscorte1");
                                        formasuelosgruesos1 = RadioGrpAux.getString("formasuelosgruesos1");
                                        redondezsuelosgruesos1 = RadioGrpAux.getString("redondezsuelosgruesos1");
                                        orientacionsuelosgruesos1 = RadioGrpAux.getString("orientacionsuelosgruesos1");
                                        compacidadsuelosgruesos1 = RadioGrpAux.getString("compacidadsuelosgruesos1");

                                        String elNuevoTexto2 = orientacionsuelosgruesos1;
                                        elNuevoTexto2 = elNuevoTexto2.replace(" ","");
                                        if (elNuevoTexto2.equals("Imbricado")){
                                            dirimbricacionmatriz1 = EditTextsAux.getString("dirimbricacionmatriz1");
                                        }


                                        String resiscorte2 = "";
                                        String formasuelosgruesos2 = "";
                                        String redondezsuelosgruesos2 = "";
                                        String orientacionsuelosgruesos2 = "";
                                        String dirimbricacionmatriz2 = "";
                                        String compacidadsuelosgruesos2 = "";


                                        formasuelosgruesos2 = RadioGrpAux.getString("formasuelosgruesos2");
                                        redondezsuelosgruesos2 = RadioGrpAux.getString("redondezsuelosgruesos2");
                                        orientacionsuelosgruesos2 = RadioGrpAux.getString("orientacionsuelosgruesos2");
                                        compacidadsuelosgruesos2 = RadioGrpAux.getString("compacidadsuelosgruesos2");

                                        String elNuevoTexto3 = orientacionsuelosgruesos2;
                                        elNuevoTexto3 = elNuevoTexto3.replace(" ","");
                                        if (elNuevoTexto3.equals("Imbricado")){
                                            dirimbricacionmatriz2 = EditTextsAux.getString("dirimbricacionmatriz2");
                                        }


                                        String noformato = EditTextsAux.getString("noformato");
                                        String vereda = EditTextsAux.getString("vereda");
                                        String noestacion = EditTextsAux.getString("noestacion");
                                        String secuenciaestratiopt1orden = EditTextsAux.getString("secuenciaestratiopt1orden");
                                        String secuenciaestratiopt1espesor = EditTextsAux.getString("secuenciaestratiopt1espesor");
                                        String secuenciaestratiopt2orden = EditTextsAux.getString("secuenciaestratiopt2orden");
                                        String secuenciaestratiopt2espesor = EditTextsAux.getString("secuenciaestratiopt2espesor");
                                        String secuenciaestratiopt3orden = EditTextsAux.getString("secuenciaestratiopt3orden");
                                        String secuenciaestratiopt3espesor = EditTextsAux.getString("secuenciaestratiopt3espesor");

                                        String secuenciaestratisuelor1orden = "";
                                        String secuenciaestratisuelor1espesor = "";
                                        String secuenciaestratisuelor2orden = "";
                                        String secuenciaestratisuelor2espesor = "";
                                        String secuenciaestratisuelor3orden = "";
                                        String secuenciaestratisuelor3espesor = "";

                                        elNuevoTexto = secuenciaestratiopt2orden;
                                        elNuevoTexto = elNuevoTexto.replace(" ","");
                                        if (!elNuevoTexto.equals("")){
                                            secuenciaestratisuelor1orden = EditTextsAux.getString("secuenciaestratisuelor1orden");
                                            secuenciaestratisuelor1espesor = EditTextsAux.getString("secuenciaestratisuelor1espesor");
                                            secuenciaestratisuelor2orden = EditTextsAux.getString("secuenciaestratisuelor2orden");
                                            secuenciaestratisuelor2espesor = EditTextsAux.getString("secuenciaestratisuelor2espesor");
                                            secuenciaestratisuelor3orden = EditTextsAux.getString("secuenciaestratisuelor3orden");
                                            secuenciaestratisuelor3espesor = EditTextsAux.getString("secuenciaestratisuelor3espesor");
                                        }

                                        String litologiasasociadasopt1exist = CheckBoxAux.getString("litologiasasociadasopt1exist");
                                        String litologiasasociadasopt1espesor = EditTextsAux.getString("litologiasasociadasopt1espesor");
                                        String litologiasasociadasopt2exist = CheckBoxAux.getString("litologiasasociadasopt2exist");
                                        String litologiasasociadasopt2espesor = EditTextsAux.getString("litologiasasociadasopt2espesor");
                                        String nombreugs = EditTextsAux.getString("nombreugs");
                                        String porcentajematriz1 = EditTextsAux.getString("porcentajematriz1");
                                        String porcentajematriz2 = EditTextsAux.getString("porcentajematriz2");
                                        String porcentajeclastos1 = EditTextsAux.getString("porcentajeclastos1");
                                        String porcentajeclastos2 = EditTextsAux.getString("porcentajeclastos2");
                                        String color1 = EditTextsAux.getString("color1");
                                        String color2 = EditTextsAux.getString("color2");
                                        String observacionessuelos = EditTextsAux.getString("observacionessuelos");
                                        String descripcionsuelos = EditTextsAux.getString("descripcionsuelos");



                                        FormatUGSSuelos NuevoFormatoUGSSuelos = new FormatUGSSuelos( municipios,  claseaflor,  estructurasoporte1,  estructurasoporte2,  condicionhumedad1,  condicionhumedad2,  estructurasrelictas1,  estructurasrelictas2,  granulometria1,  granulometria2,  forma1,  forma2,  redondez1,  redondez2,  orientacion1,  orientacion2,  dirimbricacion1,  dirimbricacion2,  meteorizacionclastos1,  meteorizacionclastos2,  granulometriamatriz1,  granulometriamatriz2,  gradacion1,  gradacion2,  seleccion1,  seleccion2,  plasticidad1,  plasticidad2,  resiscorte1,  resiscorte2,  formasuelosgruesos1,  formasuelosgruesos2,  redondezsuelosgruesos1,  redondezsuelosgruesos2,  orientacionsuelosgruesos1,  orientacionsuelosgruesos2,  dirimbricacionmatriz1,  dirimbricacionmatriz2,  noformato,  vereda,  noestacion,  secuenciaestratiopt1orden,  secuenciaestratiopt1espesor,  secuenciaestratiopt2orden,  secuenciaestratiopt2espesor,  secuenciaestratiopt3orden,  secuenciaestratiopt3espesor,  secuenciaestratisuelor1orden,  secuenciaestratisuelor1espesor,  secuenciaestratisuelor2orden,  secuenciaestratisuelor2espesor,  secuenciaestratisuelor3orden,  secuenciaestratisuelor3espesor,  litologiasasociadasopt1exist,  litologiasasociadasopt1espesor,  litologiasasociadasopt2exist,  litologiasasociadasopt2espesor,  nombreugs,  porcentajematriz1,  porcentajematriz2,  porcentajeclastos1,  porcentajeclastos2,  color1,  color2,  observacionessuelos, descripcionsuelos, compacidadsuelosgruesos1, compacidadsuelosgruesos2);


                                        databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Suelos/Form_UGS_Suelos_"+j).setValue(NuevoFormatoUGSSuelos);


                                        int contFotosAnexas = Integer.parseInt(FromatoAux.getString("FotosAnexas"));
                                        databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Suelos/Form_UGS_Suelos_"+j+"/FotosAnexas/count").setValue(contFotosAnexas);
                                        for (int k = 1; k <= contFotosAnexas; k++) {

                                            String NombreFotosAnexas = EditTextsAux.getString("NombreFotosAnexas"+k);
                                            String DescriFotosAnexas = EditTextsAux.getString("DescriFotosAnexas"+k);

                                            FormatFotosAnexas nuevoFormatoFotosAnexas = new FormatFotosAnexas(NombreFotosAnexas, DescriFotosAnexas);
                                            databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Suelos/Form_UGS_Suelos_"+j+"/FotosAnexas/FotoAnexa_"+k).setValue(nuevoFormatoFotosAnexas);

                                        }

                                    }

                                    form.put("Subido", true);
                                    Log.d("jaaja", "GuardarForm10: "+formComplete);
                                    OutputStreamWriter file = new OutputStreamWriter(mcont.openFileOutput("listaForm.txt", Activity.MODE_PRIVATE));
                                    file.write(String.valueOf(formComplete));
                                    file.flush();
                                    file.close();
                                    cont++;
                                }

                            } catch (JSONException | FileNotFoundException e) {
                                e.printStackTrace();
                                Log.d("jaaja", "AlgunError: "+e);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }

                        if (subida){
                            Toast.makeText(mcont, "Subidos a la Base de Datos los Formularios Guardados\n", Toast.LENGTH_LONG).show();
                        }else{
                            Toast.makeText(mcont, "Ya se encuentran Subidos Todos los Formularios Guardados\n", Toast.LENGTH_LONG).show();
                        }
                        databaseReference.child("EstacionesCampo/cont/cont").setValue(cont);


                    }
                }
            });

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

            //-------------> Rocas

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

                JSONObject checkBox = new JSONObject();
                for (int k = 0; k < ListaCheckBox.get(i).size(); k++) {
                    checkBox.put(ListaCheckBox.get(i).get(k).getTag().toString().split("_")[0], ListaCheckBox.get(i).get(k).isChecked());
                }
                FormatoTemp.put("CheckBox", checkBox);

                JSONObject radioGrp = new JSONObject();
                int contAux = 0;
                for (int k = 0; k < ListaRadioGrp.get(i).size(); k++) {
                    RadioButton checkedRadioButton = (RadioButton)ListaRadioGrp.get(i).get(k).findViewById(ListaRadioGrp.get(i).get(k).getCheckedRadioButtonId());
//                    Log.d("jaaja", "onCheckedChanged: "+checkedRadioButton.getTag());
//                    Log.d("jaaja", "onCheckedChanged: "+ListaRadioGrp.get(i).get(k).getTag());
                    radioGrp.put(ListaRadioGrp.get(i).get(k).getTag().toString(), checkedRadioButton.getTag());
                }
                FormatoTemp.put("RadioGrp", radioGrp);

                FormatosList.put("Form_UGS_Rocas_"+countFormatosUGSRocas, FormatoTemp);

                countFormatosUGSRocas++;
            }

            //-------------> Suelos

            if (listFormularios.get(i).equals("UGS Suelos")){
                JSONObject FormatoTemp = new JSONObject()
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

                JSONObject checkBox = new JSONObject();
                for (int k = 0; k < ListaCheckBox.get(i).size(); k++) {
                    checkBox.put(ListaCheckBox.get(i).get(k).getTag().toString().split("_")[0], ListaCheckBox.get(i).get(k).isChecked());
                }
                FormatoTemp.put("CheckBox", checkBox);

                JSONObject radioGrp = new JSONObject();
                int contAux = 0;
                for (int k = 0; k < ListaRadioGrp.get(i).size(); k++) {
                    RadioButton checkedRadioButton = (RadioButton)ListaRadioGrp.get(i).get(k).findViewById(ListaRadioGrp.get(i).get(k).getCheckedRadioButtonId());
                    String tagGroup;
                    if (ListaRadioGrp.get(i).get(k).getTag().toString().contains("granulometriamatriz")){
                        tagGroup = ListaRadioGrp.get(i).get(k).getTag().toString().split("_")[0];
                    }
                    else{
                        tagGroup = ListaRadioGrp.get(i).get(k).getTag().toString();
                    }
//                    Log.d("jaaja", "onCheckedChanged: "+checkedRadioButton.getTag());
//                    Log.d("jaaja", "onCheckedChanged: "+ListaRadioGrp.get(i).get(k).getTag());
                    radioGrp.put(tagGroup, checkedRadioButton.getTag());
                }
                FormatoTemp.put("RadioGrp", radioGrp);

                FormatosList.put("Form_UGS_Suelos_"+countFormatosUGSSuelos, FormatoTemp);

                countFormatosUGSSuelos++;
            }
        }
        countFormatos.put("UGS_Rocas", countFormatosUGSRocas);
        countFormatos.put("UGS_Suelos", countFormatosUGSSuelos);

        FormatosList.put("counts", countFormatos);
        attrForm.put("Formularios", FormatosList);



        auxTextExist = true;

        formComplete.put(attrForm);
        Log.d("jaaja", "GuardarForm3: "+formComplete);
        Log.d("jaaja", "GuardarForm1: "+listFormularios);
        Log.d("jaaja", "GuardarForm2: "+attrForm);


        OutputStreamWriter file = new OutputStreamWriter(mcont.openFileOutput("listaForm.txt", Activity.MODE_PRIVATE));
        file.write(String.valueOf(formComplete));
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

    public void GenerarListas() {
//        Formato UGS Rocas

        listaElementosUGSR.add(new ElementoFormato( "Número Formato",  "edittext",  "noformato", 0));
        listaElementosUGSR.add(new ElementoFormato( "Municipio",  "spinner",  "municipios", R.array.Municipios));
        listaElementosUGSR.add(new ElementoFormato( "Vereda",  "edittext",  "vereda", 0));
        listaElementosUGSR.add(new ElementoFormato( "Número de la Estación",  "edittext",  "noestacion", 0));
        listaElementosUGSR.add(new ElementoFormato( "Clase Afloramiento",  "spinner",  "claseaflor", R.array.ClaseAfloramiento));
        listaElementosUGSR.add(new ElementoFormato( "Secuencia Estratigráfica",  "secuenciaestrati",  "secuenciaestratiopt", R.array.SecuenciaEstratiRocas));
        listaElementosUGSR.add(new ElementoFormato( "CARACTERIZACIÓN DE LA UGS / UGI",  "titulo",  "", 0));
        listaElementosUGSR.add(new ElementoFormato( "Perfil de meteorización (Dearman 1974)",  "edittext",  "perfilmeteorizacion_I-II-III-IV-V-VI", 0));
        listaElementosUGSR.add(new ElementoFormato( "N° litologías asociadas a la UGS /UGI",  "litologias",  "litologiasasociadasopt", 0));
        listaElementosUGSR.add(new ElementoFormato( "Nombre de la UGS / UGI",  "edittext",  "nombreugs", 0));
        listaElementosUGSR.add(new ElementoFormato( "GSI",  "spinner",  "gsi", R.array.GSI));
        listaElementosUGSR.add(new ElementoFormato( "CARACTERÍSTICAS DE LA UGS / UGI",  "titulo",  "", 0));
        listaElementosUGSR.add(new ElementoFormato( "Fábrica",  "radiobtn",  "fabrica", R.array.Fabrica1));
        listaElementosUGSR.add(new ElementoFormato( "Humedad Natural",  "radiobtn",  "humedad", R.array.HumedadNatural1));
        listaElementosUGSR.add(new ElementoFormato( "Tamaño del Grano",  "radiobtn",  "tamañograno", R.array.TamañoGrano1));
        listaElementosUGSR.add(new ElementoFormato( "Grado de Meteorización",  "radiobtn",  "gradometeo", R.array.GradoMeteo1));
        listaElementosUGSR.add(new ElementoFormato( "Resistencia a la Compresión Simple (Mpa)",  "radiobtn",  "resistenciacomp", R.array.ResistenciaCompresionSimple1));
        listaElementosUGSR.add(new ElementoFormato( "Color Litología 1",  "edittext",  "color1", 0));
        listaElementosUGSR.add(new ElementoFormato( "Color Litología 2",  "edittext",  "color2", 0));
        listaElementosUGSR.add(new ElementoFormato( "Composición Mineralógica (Macro) Litología 1",  "edittext",  "composicionmineral1", 0));
        listaElementosUGSR.add(new ElementoFormato( "Composición Mineralógica (Macro) Litología 2",  "edittext",  "composicionmineral2", 0));

//        Discontinuidades del Formato UGS Rocas

        listaElementosUGSRDiscont.add(new ElementoFormato( "Tipo",  "spinner",  "TipoDiscont", R.array.TipoDiscont));
        listaElementosUGSRDiscont.add(new ElementoFormato( "Dir. Buzamiento (Az. Bz.)",  "edittext",  "DirBuzamiento", 0));
        listaElementosUGSRDiscont.add(new ElementoFormato( "Buzamiento (Bz.)",  "edittext",  "Buzamiento", 0));
        listaElementosUGSRDiscont.add(new ElementoFormato( "Persistencia",  "spinner",  "PersistenciaDiscont", R.array.PersistenciaDiscont));
        listaElementosUGSRDiscont.add(new ElementoFormato( "Ancho de Abertura",  "spinner",  "AnchoAberDiscont", R.array.AnchoAberturaDiscont));
        listaElementosUGSRDiscont.add(new ElementoFormato( "Tipo de Relleno",  "spinner",  "TipoRellenoDiscont", R.array.TipoRellenoDiscont));
        listaElementosUGSRDiscont.add(new ElementoFormato( "Rugosidad de la Superficie",  "spinner",  "RugosidadSuperDiscont", R.array.RugosidadDiscont));
        listaElementosUGSRDiscont.add(new ElementoFormato( "Forma de la Superficie",  "spinner",  "FormaSuperDiscont", R.array.FormaSuperficieDiscont));
        listaElementosUGSRDiscont.add(new ElementoFormato( "Humedad en Diaclasas",  "spinner",  "HumedadDiscont", R.array.HumedadDiaclasasDiscont));
        listaElementosUGSRDiscont.add(new ElementoFormato( "Espaciamiento",  "spinner",  "EspaciamientoDiscont", R.array.EspaciamientoDiscont));
        listaElementosUGSRDiscont.add(new ElementoFormato( "Meteorizacion",  "spinner",  "MeteorizacionDiscont", R.array.MeteorizacionDiscont));
        listaElementosUGSRDiscont.add(new ElementoFormato( "Rake/Pitch",  "edittext",  "RakePitch", 0));
        listaElementosUGSRDiscont.add(new ElementoFormato( "Dir. del Rake/Pitch",  "edittext",  "DirRakePitch", 0));
        listaElementosUGSRDiscont.add(new ElementoFormato( "Orientación talud/ladera",  "titulo",  "", 0));
        listaElementosUGSRDiscont.add(new ElementoFormato( "Az Bz/Bz",  "edittext",  "AzBzBz1", 0));
        listaElementosUGSRDiscont.add(new ElementoFormato( "Az Bz/Bz",  "edittext",  "AzBzBz2", 0));
        listaElementosUGSRDiscont.add(new ElementoFormato( "Altura",  "edittext",  "AlturaDiscont", 0));
        listaElementosUGSRDiscont.add(new ElementoFormato( "Observaciones",  "edittext",  "ObservacionesDiscont", 0));

        //----------> Fotos Anexas

        listaElementosUGSFotosAnexas.add(new ElementoFormato( "Nombre de la Foto",  "edittext",  "NombreFotosAnexas", 0));
        listaElementosUGSFotosAnexas.add(new ElementoFormato( "Descripción de la Foto",  "edittext",  "DescriFotosAnexas", 0));


        //----------> Formato UGSS

        listaElementosUGSS.add(new ElementoFormato( "Número Formato",  "edittext",  "noformato", 0));
        listaElementosUGSS.add(new ElementoFormato( "Municipio",  "spinner",  "municipios", R.array.Municipios));
        listaElementosUGSS.add(new ElementoFormato( "Vereda",  "edittext",  "vereda", 0));
        listaElementosUGSS.add(new ElementoFormato( "Número de la Estación",  "edittext",  "noestacion", 0));
        listaElementosUGSS.add(new ElementoFormato( "Clase Afloramiento",  "spinner",  "claseaflor", R.array.ClaseAfloramiento));
        listaElementosUGSS.add(new ElementoFormato( "Secuencia Estratigráfica",  "secuenciaestrati",  "secuenciaestratiopt", R.array.SecuenciaEstratiSuelos));
        listaElementosUGSS.add(new ElementoFormato( "CARACTERIZACIÓN DE LA UGS / UGI",  "titulo",  "", 0));
        listaElementosUGSS.add(new ElementoFormato( "Nombre-Código de la UGS / UGI",  "edittext",  "nombreugs", 0));
        listaElementosUGSS.add(new ElementoFormato( "N° litologías asociadas a la UGS /UGI",  "litologias",  "litologiasasociadasopt", 0));
        listaElementosUGSS.add(new ElementoFormato( "CARACTERÍSTICAS DE LA UGS / UGI",  "titulo",  "", 0));
        listaElementosUGSS.add(new ElementoFormato( "Estructura Soporte",  "radiobtn",  "estructurasoporte", R.array.EstructuraSoporte1));
        listaElementosUGSS.add(new ElementoFormato( "Porcentajes",  "porcentajes",  "porcentajematriz_porcentajeclastos", R.array.Porcentajes));
        listaElementosUGSS.add(new ElementoFormato( "Condicion de Humedad",  "radiobtn",  "condicionhumedad", R.array.CondicionHumedad1));
        listaElementosUGSS.add(new ElementoFormato( "Estructuras Relictas",  "radiobtn",  "estructurasrelictas", R.array.EstructurasRelictas1));
        listaElementosUGSS.add(new ElementoFormato( "Color Litología 1",  "edittext",  "color1", 0));
        listaElementosUGSS.add(new ElementoFormato( "Color Litología 2",  "edittext",  "color2", 0));
        listaElementosUGSS.add(new ElementoFormato( "CARACTERÍSTICAS DE LOS CLASTOS",  "titulo",  "", 0));
        listaElementosUGSS.add(new ElementoFormato( "Granulometria de los Clastos",  "radiobtn",  "granulometria", R.array.Granulometria1));
        listaElementosUGSS.add(new ElementoFormato( "Forma de los Clastos",  "radiobtn",  "forma", R.array.Forma1));
        listaElementosUGSS.add(new ElementoFormato( "Redondez de los Clastos",  "radiobtn",  "redondez", R.array.Redondez1));
        listaElementosUGSS.add(new ElementoFormato( "Orientacion de los Clastos",  "radiobtn",  "orientacion", R.array.OrientacionClastos1));
        listaElementosUGSS.add(new ElementoFormato( "Meteorizacion de los Clastos",  "radiobtn",  "meteorizacionclastos", R.array.MeteorizacionClastos1));
        listaElementosUGSS.add(new ElementoFormato( "CARACTERÍSTICAS DE LA MATRIZ",  "titulo",  "", 0));
        listaElementosUGSS.add(new ElementoFormato( "Granulometría de la Matriz",  "radiobtn",  "granulometriamatriz", R.array.GranulometriaMatriz1));
        listaElementosUGSS.add(new ElementoFormato( "Gradacion de la Matriz",  "radiobtn",  "gradacion", R.array.Gradacion1));
        listaElementosUGSS.add(new ElementoFormato( "Seleccion de la Matriz",  "radiobtn",  "seleccion", R.array.Seleccion1));
        listaElementosUGSS.add(new ElementoFormato( "Plasticidad de la Matriz",  "radiobtn",  "plasticidad", R.array.Plasticidad1));
        listaElementosUGSS.add(new ElementoFormato( "SUELOS FINOS",  "titulo",  "", 0));
        listaElementosUGSS.add(new ElementoFormato( "RESISTENCIA AL CORTE NO DRENADO kN/m2 (CONSISTENCIA)",  "radiobtn",  "resiscorte", R.array.ResistenciaAlCorte1));
        listaElementosUGSS.add(new ElementoFormato( "SUELOS GRUESOS",  "titulo",  "", 0));
        listaElementosUGSS.add(new ElementoFormato( "Forma de la Matriz",  "radiobtn",  "formasuelosgruesos", R.array.FormaSuelos1));
        listaElementosUGSS.add(new ElementoFormato( "Redondez de la Matriz",  "radiobtn",  "redondezsuelosgruesos", R.array.RedondezSuelos1));
        listaElementosUGSS.add(new ElementoFormato( "Orientación de la Matriz",  "radiobtn",  "orientacionsuelosgruesos", R.array.OrientacionSuelos1));
        listaElementosUGSS.add(new ElementoFormato( "Compacidad de la Matriz",  "radiobtn",  "compacidadsuelosgruesos", R.array.Compacidad1));
        listaElementosUGSS.add(new ElementoFormato( "Observaciones",  "edittext",  "observacionessuelos", 0));
        listaElementosUGSS.add(new ElementoFormato( "Descripción Composición Partículas del Suelo",  "edittext",  "descripcionsuelos", 0));


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
