package com.unal.proyectosgcappcampo;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.unal.proyectosgcappcampo.databinding.FragmentGuardadasBinding;
import com.unal.proyectosgcappcampo.ui.slideshow.ElementoFormato;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class GuardadasFragment extends Fragment {

    private FragmentGuardadasBinding binding;

    private LinearLayout contenedorEstaciones;

    private JSONObject attrForm;
    private JSONArray formComplete;

    InputStreamReader archivo;
    Boolean auxTextExist = false;
    String listaFormText = "";

    List<LinearLayout> listLiForm = new ArrayList<LinearLayout>();
    List<Button> listBtnAcordion = new ArrayList<Button>();
    List<Button> listBtnSubido = new ArrayList<Button>();

    List<String[]> ListaRocas = new ArrayList<String[]>();
    List<String[]> ListaRocasDiscont = new ArrayList<String[]>();
    List<String[]> ListaFotosAnexas = new ArrayList<String[]>();
    List<String[]> ListaSuelos = new ArrayList<String[]>();
    List<String[]> ListaSGMF = new ArrayList<String[]>();
    List<String[]> ListaNewSGMF = new ArrayList<String[]>();
    List<String[]> ListaCATALOGO = new ArrayList<String[]>();
    List<String[]> ListaDANOS = new ArrayList<String[]>();
    List<String[]> ListaINVENTARIO = new ArrayList<String[]>();
    List<String[]> ListaFotosAnexasINV = new ArrayList<String[]>();

    int colorPrimary = Color.parseColor("#f9ae00");
    int colorSecu = Color.parseColor("#666666");
    int colorTerc = Color.parseColor("#cccccc");


    private Context mcont = getActivity();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentGuardadasBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mcont = root.getContext();

        contenedorEstaciones = binding.contenedorEstaciones;

        CargarForms();

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
        }
        else {
            formComplete = new JSONArray();
        }

        if (auxTextExist){

            JSONObject form = null;

            for (int i = 0; i < formComplete.length(); i++) {
                try {

                    form = formComplete.getJSONObject(i);

                    String Estacion = form.getString("Estacion");
                    boolean Subido = Boolean.parseBoolean(form.getString("Subido"));

                    Button bAcordion = new Button(mcont);
                    bAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    bAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                    bAcordion.setText(Estacion);
                    bAcordion.setTag(i);
                    listBtnAcordion.add(bAcordion);
                    contenedorEstaciones.addView(bAcordion);

                    LinearLayout liForm = new LinearLayout(mcont);
                    liForm.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liForm.setOrientation(LinearLayout.VERTICAL);
                    liForm.setBackgroundColor(colorTerc);
                    liForm.setVisibility(View.GONE);

                    listLiForm.add(liForm);
                    contenedorEstaciones.addView(liForm);

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
                        }
                    });

                    //------------> Titulo del Formato

                    TextView tvTitulo = new TextView(mcont);
                    tvTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvTitulo.setText(Estacion);
                    tvTitulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                    tvTitulo.setTextAppearance(R.style.TituloFormato);
                    tvTitulo.setPadding(0, 20, 0, 40);
                    liForm.addView(tvTitulo);

                    LinearLayout liHori1 = new LinearLayout(mcont);
                    liHori1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liHori1.setOrientation(LinearLayout.HORIZONTAL);
                    liHori1.setPadding(20, 0, 0, 20);

                    String estado;
                    String btnestado;
                    if (!Subido){
                        estado = "Pendiente";
                        btnestado = "Click para No Subir";
                    }else{
                        estado = "Subido";
                        btnestado = "Click para Subir de Nuevo";
                    }

                    TextView tvOpt = new TextView(mcont);
                    tvOpt.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvOpt.setText("Estado: ");
                    tvOpt.setTextAppearance(R.style.TituloItemEncabezado);
                    tvOpt.setPadding(0, 20, 0, 0);
                    liHori1.addView(tvOpt);

//                    TextView tvOpt1 = new TextView(mcont);
//                    tvOpt1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                    tvOpt1.setText(estado);
//                    tvOpt1.setTextAppearance(R.style.TituloItem);
//                    tvOpt1.setPadding(0, 20, 0, 0);
//                    liHori1.addView(tvOpt1);

                    Button bSubido = new Button(mcont);
                    bSubido.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                    bSubido.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                    bSubido.setText(estado+", "+btnestado);
                    bSubido.setTag(i);
                    listBtnSubido.add(bSubido);
                    liHori1.addView(bSubido);

                    liForm.addView(liHori1);


                    bSubido.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            JSONObject form1 = null;
                            int idBtn = Integer.parseInt(v.getTag().toString());
                            try {
                                form1 = formComplete.getJSONObject(idBtn);
                                boolean Subido1 = Boolean.parseBoolean(form1.getString("Subido"));
                                if (Subido1){
                                    form1.put("Subido", false);
                                    listBtnSubido.get(idBtn).setText("Pendiente, Click para No subir");
                                }else{
                                    form1.put("Subido", true);
                                    listBtnSubido.get(idBtn).setText("Subido, Click para Subir de Nuevo");
                                }
                                try {
                                    OutputStreamWriter file = new OutputStreamWriter(mcont.openFileOutput("listaForm.txt", Activity.MODE_PRIVATE));
                                    file.write(String.valueOf(formComplete));
                                    file.flush();
                                    file.close();
                                    Toast.makeText(mcont, "Cambio realizado\n", Toast.LENGTH_LONG).show();
                                }catch (Exception ex) {
                                    Toast.makeText(mcont, "Ocurrió un error y no se pudo realizar el cambio de estado\n", Toast.LENGTH_LONG).show();
                                    Log.e("Error12", "ex: " + ex);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(mcont, "Ocurrió un error y no se pudo realizar el cambio\n", Toast.LENGTH_LONG).show();
                            }

                        }
                    });



                    Resources res = getResources();
                    String[] opciones = res.getStringArray(R.array.OpcionesEstacion);

                    for (int j = 0; j < opciones.length; j++) {
                        LinearLayout liHori = new LinearLayout(mcont);
                        liHori.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liHori.setOrientation(LinearLayout.HORIZONTAL);
                        liHori.setPadding(0, 0, 0, 20);

                        String aux = form.getString(opciones[j]);

                        TextView tvOpt2 = new TextView(mcont);
                        tvOpt2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvOpt2.setText(opciones[j]+": ");
                        tvOpt2.setTextAppearance(R.style.TituloItemEncabezado);
                        tvOpt2.setPadding(0, 20, 0, 0);
                        liHori.addView(tvOpt2);

                        TextView tvOpt3 = new TextView(mcont);
                        tvOpt3.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvOpt3.setText(aux);
                        tvOpt3.setTextAppearance(R.style.TituloItem);
                        tvOpt3.setPadding(0, 20, 0, 0);
                        liHori.addView(tvOpt3);

                        liForm.addView(liHori);

                    }

                    JSONObject Formularios = form.getJSONObject("Formularios");
                    JSONObject counts = Formularios.getJSONObject("counts");

                    int contUGS_Rocas = Integer.parseInt(counts.getString("UGS_Rocas"));
                    int contUGS_Suelos = Integer.parseInt(counts.getString("UGS_Suelos"));
                    int contSGMF = Integer.parseInt(counts.getString("SGMF"));
                    int contCATALOGO = Integer.parseInt(counts.getString("CATALOGO"));
                    int contINVENTARIO = Integer.parseInt(counts.getString("INVENTARIO"));

                    for (int j = 0; j < contUGS_Rocas; j++) {
                        JSONObject FromatoAux = Formularios.getJSONObject("Form_UGS_Rocas_"+j);
                        JSONObject SpinnersAux = FromatoAux.getJSONObject("Spinners");
                        JSONObject EditTextsAux = FromatoAux.getJSONObject("EditText");
                        JSONObject CheckBoxAux = FromatoAux.getJSONObject("CheckBox");
                        JSONObject RadioGrpAux = FromatoAux.getJSONObject("RadioGrp");

                        int aux = j + 1;

                        Button btnFormAcordion = new Button(mcont);
                        btnFormAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        btnFormAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                        btnFormAcordion.setText("Formato UGS Rocas "+aux);
                        btnFormAcordion.setTag(j);
                        liForm.addView(btnFormAcordion);

                        LinearLayout liFormAcordion = new LinearLayout(mcont);
                        liFormAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormAcordion.setOrientation(LinearLayout.VERTICAL);
                        liFormAcordion.setBackgroundColor(0x22222200);
                        liFormAcordion.setVisibility(View.GONE);
                        liForm.addView(liFormAcordion);

                        btnFormAcordion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (liFormAcordion.getVisibility() == View.VISIBLE) {
                                    ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                    animation.setDuration(220);
                                    animation.setFillAfter(false);
                                    animation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {
                                        }
                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            liFormAcordion.setVisibility(View.GONE);
                                            btnFormAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                                        }
                                        @Override
                                        public void onAnimationRepeat(Animation animation) {
                                        }
                                    });
                                    liFormAcordion.startAnimation(animation);

                                }
                                else {
                                    ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                    animation.setDuration(220);
                                    animation.setFillAfter(false);
                                    liFormAcordion.startAnimation(animation);
                                    liFormAcordion.setVisibility(View.VISIBLE);
                                    btnFormAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                                }

                            }
                        });

                        for (int k = 0; k < ListaRocas.size(); k++) {
                            LinearLayout liHori = new LinearLayout(mcont);
                            liHori.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liHori.setOrientation(LinearLayout.HORIZONTAL);
                            liHori.setPadding(0, 0, 0, 20);

                            LinearLayout liVert = new LinearLayout(mcont);
                            liVert.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liVert.setOrientation(LinearLayout.VERTICAL);
                            liVert.setPadding(0, 0, 0, 20);

                            String clase = ListaRocas.get(k)[2];
                            String titulo = ListaRocas.get(k)[1];
                            String tag = ListaRocas.get(k)[0];

                            if (clase.equals("edittext") || clase.equals("spinner")){
                                String valor;
                                if(clase.equals("edittext")){
                                    valor = EditTextsAux.getString(tag);
                                }else{
                                    valor = SpinnersAux.getString(tag);
                                }

                                if (tag.equals("nombreugs") || tag.equals("composicionmineral1") || tag.equals("composicionmineral2") ){
                                    TextView tvOpte = new TextView(mcont);
                                    tvOpte.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpte.setText(titulo+": ");
                                    tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvOpte.setPadding(0, 20, 0, 0);
                                    liVert.addView(tvOpte);

                                    TextView tvOpts = new TextView(mcont);
                                    tvOpts.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpts.setText(valor);
                                    tvOpts.setTextAppearance(R.style.TituloItem);
                                    tvOpts.setPadding(0, 20, 0, 0);
                                    liVert.addView(tvOpts);

                                }
                                else{
                                    TextView tvOpte = new TextView(mcont);
                                    tvOpte.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpte.setText(titulo+": ");
                                    tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvOpte.setPadding(0, 20, 0, 0);
                                    liHori.addView(tvOpte);

                                    TextView tvOpts = new TextView(mcont);
                                    tvOpts.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpts.setText(valor);
                                    tvOpts.setTextAppearance(R.style.TituloItem);
                                    tvOpts.setPadding(0, 20, 0, 0);
                                    liHori.addView(tvOpts);

                                    liVert.addView(liHori);
                                }
                            }
                            if (clase.equals("titulito")){
                                TextView tvGenerico = new TextView(mcont);
                                tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvGenerico.setText(titulo);
                                tvGenerico.setTextAppearance(R.style.TituloFormato);
                                tvGenerico.setPadding(0, 30, 0, 0);
                                liHori.addView(tvGenerico);
                                liVert.addView(liHori);
                            }
                            if (clase.equals("secuenciaestrati")){
                                TextView tvGenerico = new TextView(mcont);
                                tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvGenerico.setText(titulo);
                                tvGenerico.setTextAppearance(R.style.TituloFormato);
                                tvGenerico.setPadding(0, 30, 0, 0);
                                liVert.addView(tvGenerico);

                                TextView pruebatext = new TextView(mcont);
                                pruebatext.setLayoutParams(new ActionBar.LayoutParams(600, ViewGroup.LayoutParams.WRAP_CONTENT));
                                pruebatext.setText("Orden");
                                pruebatext.setTextAppearance(R.style.TituloItemEncabezado);
                                pruebatext.setPadding(450, 20, 0, 0);
                                liHori.addView(pruebatext);

                                TextView pruebatext1 = new TextView(mcont);
                                pruebatext1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                pruebatext1.setText("Espesor");
                                pruebatext1.setTextAppearance(R.style.TituloItemEncabezado);
                                pruebatext1.setPadding(70, 20, 0, 0);
                                liHori.addView(pruebatext1);

                                liVert.addView(liHori);

                                Resources resForm = getResources();
                                String[] opcionesForm = resForm.getStringArray(R.array.SecuenciaEstratiRocas);
                                int secuEstratiWidth = 420;
                                int secuEstratiOrdenWidth = 200;
                                int secuEstratiEspesorWidth = 300;

                                for (int m = 0; m < opcionesForm.length ; m++) {
                                    int aux1 = m + 1;

                                    LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                                    liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                                    TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                                    tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvSecuenciaEstratiOpt.setText(opcionesForm[m]);
                                    tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItemEncabezado);
                                    liFormSecuenciaEstrati.addView(tvSecuenciaEstratiOpt);

                                    String opt1 = EditTextsAux.getString(tag+aux1+"orden");

                                    TextView etSecuenciaEstratiOpt = new TextView(mcont);
                                    etSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    etSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                                    etSecuenciaEstratiOpt.setText(opt1);
                                    etSecuenciaEstratiOpt.setPadding(80, 0, 0, 0);
                                    liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt);

                                    String opt2 = EditTextsAux.getString(tag+aux1+"espesor");

                                    TextView etSecuenciaEstratiOpt1Espesor = new TextView(mcont);
                                    etSecuenciaEstratiOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    etSecuenciaEstratiOpt1Espesor.setTextAppearance(R.style.TituloItem);
                                    etSecuenciaEstratiOpt1Espesor.setText(opt2+"m");
                                    etSecuenciaEstratiOpt1Espesor.setPadding(150, 0, 0, 0);
                                    liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt1Espesor);

                                    liVert.addView(liFormSecuenciaEstrati);

                                }
                                String opt1x = EditTextsAux.getString(tag+3+"orden");
                                opt1x = opt1x.replace(" ","");
                                if(!opt1x.equals("")){
                                    Resources resSuelor = getResources();
                                    String[] opcionesSuelor = resSuelor.getStringArray(R.array.SecuenciaEstratiRocasSueloRes);
                                    for (int l = 0; l < opcionesSuelor.length; l++) {
                                    int aux2 = l + 1;
                                    LinearLayout liFormSecuenciaEstratiSueloR1 = new LinearLayout(mcont);
                                    liFormSecuenciaEstratiSueloR1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    liFormSecuenciaEstratiSueloR1.setOrientation(LinearLayout.HORIZONTAL);

                                    TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                                    tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvSecuenciaEstratiOpt.setText(opcionesForm[j]);
                                    tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                                    liFormSecuenciaEstratiSueloR1.addView(tvSecuenciaEstratiOpt);

                                    String opt1 = EditTextsAux.getString("secuenciaestratisuelor"+aux2+"orden");

                                    TextView etSecuenciaEstratiOpt = new TextView(mcont);
                                    etSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    etSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                                    etSecuenciaEstratiOpt.setText(opt1);
                                    etSecuenciaEstratiOpt.setPadding(80, 0, 0, 0);
                                    liFormSecuenciaEstratiSueloR1.addView(etSecuenciaEstratiOpt);

                                    String opt2 = EditTextsAux.getString("secuenciaestratisuelor"+aux2+"espesor");

                                    TextView etSecuenciaEstratiOpt1Espesor = new TextView(mcont);
                                    etSecuenciaEstratiOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    etSecuenciaEstratiOpt1Espesor.setTextAppearance(R.style.TituloItem);
                                    etSecuenciaEstratiOpt1Espesor.setText(opt2+"m");
                                    etSecuenciaEstratiOpt1Espesor.setPadding(150, 0, 0, 0);
                                    liFormSecuenciaEstratiSueloR1.addView(etSecuenciaEstratiOpt1Espesor);

                                    liVert.addView(liFormSecuenciaEstratiSueloR1);

                                }
                                }

                            }
                            if (clase.equals("litologias")){
                                TextView tvOpte = new TextView(mcont);
                                tvOpte.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvOpte.setText(titulo+": ");
                                tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                tvOpte.setPadding(0, 20, 0, 0);
                                liVert.addView(tvOpte);

                                TextView tvOpts = new TextView(mcont);
                                tvOpts.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvOpts.setText("1");
                                tvOpts.setTextAppearance(R.style.TituloItem);
                                tvOpts.setPadding(0, 20, 0, 0);
                                liHori.addView(tvOpts);

                                CheckBox checkbox1 = new CheckBox(mcont);
                                checkbox1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                                    checkbox1.setText(aux);
                                checkbox1.setClickable(false);
                                checkbox1.setEnabled(false);
                                liHori.addView(checkbox1);

                                String valorcito1 = CheckBoxAux.getString("litologiasasociadasopt1exist");
                                if(valorcito1.equals("true")){
                                    checkbox1.setChecked(true);
                                }
                                else{
                                    checkbox1.setChecked(false);
                                }

                                String espesor1 = EditTextsAux.getString("litologiasasociadasopt1espesor");
                                TextView tvOpts2 = new TextView(mcont);
                                tvOpts2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvOpts2.setText(espesor1+"m");
                                tvOpts2.setTextAppearance(R.style.TituloItem);
                                tvOpts2.setPadding(20, 20, 20, 0);
                                liHori.addView(tvOpts2);

                                TextView tvOptsx = new TextView(mcont);
                                tvOptsx.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvOptsx.setText("2");
                                tvOptsx.setTextAppearance(R.style.TituloItem);
                                tvOptsx.setPadding(50, 20, 0, 0);
                                liHori.addView(tvOptsx);

                                CheckBox checkbox2 = new CheckBox(mcont);
                                checkbox2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                                    checkbox1.setText(aux);
                                checkbox2.setClickable(false);
                                checkbox2.setEnabled(false);
                                liHori.addView(checkbox2);

                                valorcito1 = CheckBoxAux.getString("litologiasasociadasopt2exist");
                                if(valorcito1.equals("true")){
                                    checkbox2.setChecked(true);
                                }
                                else{
                                    checkbox2.setChecked(false);
                                }

                                String espesor2 = EditTextsAux.getString("litologiasasociadasopt2espesor");
                                TextView tvOpts2x = new TextView(mcont);
                                tvOpts2x.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvOpts2x.setText(espesor2+"m");
                                tvOpts2x.setTextAppearance(R.style.TituloItem);
                                tvOpts2x.setPadding(20, 20, 20, 0);
                                liHori.addView(tvOpts2x);


                                liVert.addView(liHori);


                            }
                            if (clase.equals("radiobtn")){
                                for (int l = 1; l < 3; l++) {
                                    TextView tvGenerico = new TextView(mcont);
                                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvGenerico.setText(titulo+" Litología "+l);
                                    tvGenerico.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvGenerico.setPadding(0, 30, 0, 0);
                                    liVert.addView(tvGenerico);

                                    String valor = RadioGrpAux.getString(tag+l);

                                    TextView etGenerico = new TextView(mcont);
                                    etGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    etGenerico.setText(valor);
                                    etGenerico.setTextAppearance(R.style.TituloItem);
                                    etGenerico.setPadding(0, 10, 0, 0);
                                    etGenerico.setTag(valor);
                                    liVert.addView(etGenerico);
                                }
                            }
                            liFormAcordion.addView(liVert);

                        }

                        int contDiscont = Integer.parseInt(FromatoAux.getString("Discontinuidades"));
                        for (int f = 1; f <= contDiscont; f++) {
                            Button btnFormAcordionDis = new Button(mcont);
                            btnFormAcordionDis.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                            btnFormAcordionDis.setText("Discontinuidad "+f);
                            btnFormAcordionDis.setTag(j);
                            liFormAcordion.addView(btnFormAcordionDis);

                            LinearLayout liFormAcordionDis = new LinearLayout(mcont);
                            liFormAcordionDis.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liFormAcordionDis.setOrientation(LinearLayout.VERTICAL);
                            liFormAcordionDis.setBackgroundColor(0x22222200);
                            liFormAcordionDis.setVisibility(View.GONE);
                            liFormAcordion.addView(liFormAcordionDis);

                            btnFormAcordionDis.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (liFormAcordionDis.getVisibility() == View.VISIBLE) {
                                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                        animation.setDuration(220);
                                        animation.setFillAfter(false);
                                        animation.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }
                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                liFormAcordionDis.setVisibility(View.GONE);
                                                btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                                            }
                                            @Override
                                            public void onAnimationRepeat(Animation animation) {
                                            }
                                        });
                                        liFormAcordionDis.startAnimation(animation);

                                    }
                                    else {
                                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                        animation.setDuration(220);
                                        animation.setFillAfter(false);
                                        liFormAcordionDis.startAnimation(animation);
                                        liFormAcordionDis.setVisibility(View.VISIBLE);
                                        btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                                    }

                                }
                            });

                            for (int h = 0; h < ListaRocasDiscont.size(); h++) {
                                String clase1 = ListaRocasDiscont.get(h)[2];
                                String titulo1 = ListaRocasDiscont.get(h)[1];
                                String tag1 = ListaRocasDiscont.get(h)[0];

                                if (clase1.equals("spinner")){
                                    TextView tvGenerico = new TextView(mcont);
                                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvGenerico.setText(titulo1);
                                    tvGenerico.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvGenerico.setPadding(20, 30, 0, 0);
                                    liFormAcordionDis.addView(tvGenerico);

                                    String valor = SpinnersAux.getString(tag1+f);

                                    TextView etGenerico = new TextView(mcont);
                                    etGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    etGenerico.setText(valor);
                                    etGenerico.setTextAppearance(R.style.TituloItem);
                                    etGenerico.setPadding(20, 10, 0, 0);
                                    etGenerico.setTag(valor);
                                    liFormAcordionDis.addView(etGenerico);
                                }
                                if (clase1.equals("edittext")){

                                    LinearLayout liHori2 = new LinearLayout(mcont);
                                    if (tag1.equals("ObservacionesDiscont")){
                                        liHori2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        liHori2.setOrientation(LinearLayout.VERTICAL);
                                        liHori2.setPadding(20, 0, 0, 20);
                                    }
                                    else{
                                        liHori2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        liHori2.setOrientation(LinearLayout.HORIZONTAL);
                                        liHori2.setPadding(20, 0, 0, 20);
                                    }

                                    String valor = EditTextsAux.getString(tag1+f);

                                    TextView tvOpte = new TextView(mcont);
                                    tvOpte.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpte.setText(titulo1+": ");
                                    tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvOpte.setPadding(0, 20, 0, 0);
                                    liHori2.addView(tvOpte);

                                    TextView tvOpts = new TextView(mcont);
                                    tvOpts.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpts.setText(valor);
                                    tvOpts.setTextAppearance(R.style.TituloItem);
                                    tvOpts.setPadding(0, 20, 0, 0);
                                    liHori2.addView(tvOpts);
                                    liFormAcordionDis.addView(liHori2);
                                }
                                if (clase1.equals("titulito")){
                                    TextView tvGenerico = new TextView(mcont);
                                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvGenerico.setText(titulo1);
                                    tvGenerico.setTextAppearance(R.style.TituloFormato);
                                    tvGenerico.setPadding(0, 30, 0, 0);
                                    liFormAcordionDis.addView(tvGenerico);
                                }
                            }
                        }

                        int contFotosAnexas = Integer.parseInt(FromatoAux.getString("FotosAnexas"));
                        for (int f = 1; f <= contFotosAnexas; f++) {
                            Button btnFormAcordionDis = new Button(mcont);
                            btnFormAcordionDis.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                            btnFormAcordionDis.setText("Foto Anexa "+f);
                            btnFormAcordionDis.setTag(f);
                            liFormAcordion.addView(btnFormAcordionDis);

                            LinearLayout liFormAcordionDis = new LinearLayout(mcont);
                            liFormAcordionDis.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liFormAcordionDis.setOrientation(LinearLayout.VERTICAL);
                            liFormAcordionDis.setBackgroundColor(0x22222200);
                            liFormAcordionDis.setVisibility(View.GONE);
                            liFormAcordion.addView(liFormAcordionDis);

                            btnFormAcordionDis.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (liFormAcordionDis.getVisibility() == View.VISIBLE) {
                                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                        animation.setDuration(220);
                                        animation.setFillAfter(false);
                                        animation.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }
                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                liFormAcordionDis.setVisibility(View.GONE);
                                                btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                                            }
                                            @Override
                                            public void onAnimationRepeat(Animation animation) {
                                            }
                                        });
                                        liFormAcordionDis.startAnimation(animation);

                                    }
                                    else {
                                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                        animation.setDuration(220);
                                        animation.setFillAfter(false);
                                        liFormAcordionDis.startAnimation(animation);
                                        liFormAcordionDis.setVisibility(View.VISIBLE);
                                        btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                                    }

                                }
                            });

                            for (int h = 0; h < ListaFotosAnexas.size(); h++) {
                                String clase1 = ListaFotosAnexas.get(h)[2];
                                String titulo1 = ListaFotosAnexas.get(h)[1];
                                String tag1 = ListaFotosAnexas.get(h)[0];

                                if (clase1.equals("edittext")){

                                    LinearLayout liHori2 = new LinearLayout(mcont);
                                    liHori2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    liHori2.setOrientation(LinearLayout.VERTICAL);
                                    liHori2.setPadding(20, 0, 0, 20);

                                    String valor = EditTextsAux.getString(tag1+f);

                                    TextView tvOpte = new TextView(mcont);
                                    tvOpte.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpte.setText(titulo1+": ");
                                    tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvOpte.setPadding(0, 20, 0, 0);
                                    liHori2.addView(tvOpte);

                                    TextView tvOpts = new TextView(mcont);
                                    tvOpts.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpts.setText(valor);
                                    tvOpts.setTextAppearance(R.style.TituloItem);
                                    tvOpts.setPadding(0, 20, 0, 0);
                                    liHori2.addView(tvOpts);
                                    liFormAcordionDis.addView(liHori2);
                                }
                            }
                        }
                    }
                    for (int j = 0; j < contUGS_Suelos; j++)  {
                        JSONObject FromatoAux = Formularios.getJSONObject("Form_UGS_Suelos_"+j);
                        JSONObject SpinnersAux = FromatoAux.getJSONObject("Spinners");
                        JSONObject EditTextsAux = FromatoAux.getJSONObject("EditText");
                        JSONObject CheckBoxAux = FromatoAux.getJSONObject("CheckBox");
                        JSONObject RadioGrpAux = FromatoAux.getJSONObject("RadioGrp");

                        int aux = j + 1;

                        Button btnFormAcordion = new Button(mcont);
                        btnFormAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        btnFormAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                        btnFormAcordion.setText("Formato UGS Suelos "+aux);
                        btnFormAcordion.setTag(j);
                        liForm.addView(btnFormAcordion);

                        LinearLayout liFormAcordion = new LinearLayout(mcont);
                        liFormAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormAcordion.setOrientation(LinearLayout.VERTICAL);
                        liFormAcordion.setBackgroundColor(0x22222200);
                        liFormAcordion.setVisibility(View.GONE);
                        liForm.addView(liFormAcordion);

                        btnFormAcordion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (liFormAcordion.getVisibility() == View.VISIBLE) {
                                    ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                    animation.setDuration(220);
                                    animation.setFillAfter(false);
                                    animation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {
                                        }
                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            liFormAcordion.setVisibility(View.GONE);
                                            btnFormAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                                        }
                                        @Override
                                        public void onAnimationRepeat(Animation animation) {
                                        }
                                    });
                                    liFormAcordion.startAnimation(animation);

                                }
                                else {
                                    ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                    animation.setDuration(220);
                                    animation.setFillAfter(false);
                                    liFormAcordion.startAnimation(animation);
                                    liFormAcordion.setVisibility(View.VISIBLE);
                                    btnFormAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                                }

                            }
                        });

                        for (int k = 0; k < ListaSuelos.size(); k++) {
                            LinearLayout liHori = new LinearLayout(mcont);
                            liHori.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liHori.setOrientation(LinearLayout.HORIZONTAL);
                            liHori.setPadding(0, 0, 0, 20);

                            LinearLayout liVert = new LinearLayout(mcont);
                            liVert.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liVert.setOrientation(LinearLayout.VERTICAL);
                            liVert.setPadding(0, 0, 0, 20);

                            String clase = ListaSuelos.get(k)[2];
                            String titulo = ListaSuelos.get(k)[1];
                            String tag = ListaSuelos.get(k)[0];

                            if (clase.equals("edittext") || clase.equals("spinner")){
                                String valor;


                                if(clase.equals("edittext")){
                                    try {
                                        valor = EditTextsAux.getString(tag);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        valor = "";
                                    }
                                }else{
                                    try {
                                        valor = SpinnersAux.getString(tag);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        valor = "";
                                    }
                                }


                                if (tag.equals("nombreugs") || tag.equals("descripcionsuelos") || tag.equals("observacionessuelos") ){
                                    TextView tvOpte = new TextView(mcont);
                                    tvOpte.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpte.setText(titulo+": ");
                                    tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvOpte.setPadding(0, 20, 0, 0);
                                    liVert.addView(tvOpte);

                                    TextView tvOpts = new TextView(mcont);
                                    tvOpts.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpts.setText(valor);
                                    tvOpts.setTextAppearance(R.style.TituloItem);
                                    tvOpts.setPadding(0, 20, 0, 0);
                                    liVert.addView(tvOpts);

                                }
                                else{
                                    TextView tvOpte = new TextView(mcont);
                                    tvOpte.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpte.setText(titulo+": ");
                                    tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvOpte.setPadding(0, 20, 0, 0);
                                    liHori.addView(tvOpte);

                                    TextView tvOpts = new TextView(mcont);
                                    tvOpts.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpts.setText(valor);
                                    tvOpts.setTextAppearance(R.style.TituloItem);
                                    tvOpts.setPadding(0, 20, 0, 0);
                                    liHori.addView(tvOpts);

                                    liVert.addView(liHori);
                                }

                            }
                            if (clase.equals("titulito")){
                                TextView tvGenerico = new TextView(mcont);
                                tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvGenerico.setText(titulo);
                                tvGenerico.setTextAppearance(R.style.TituloFormato);
                                tvGenerico.setPadding(0, 30, 0, 0);
                                liHori.addView(tvGenerico);
                                liVert.addView(liHori);
                            }
                            if (clase.equals("secuenciaestrati")){
                                TextView tvGenerico = new TextView(mcont);
                                tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvGenerico.setText(titulo);
                                tvGenerico.setTextAppearance(R.style.TituloFormato);
                                tvGenerico.setPadding(0, 30, 0, 0);
                                liVert.addView(tvGenerico);

                                TextView pruebatext = new TextView(mcont);
                                pruebatext.setLayoutParams(new ActionBar.LayoutParams(600, ViewGroup.LayoutParams.WRAP_CONTENT));
                                pruebatext.setText("Orden");
                                pruebatext.setTextAppearance(R.style.TituloItemEncabezado);
                                pruebatext.setPadding(450, 20, 0, 0);
                                liHori.addView(pruebatext);

                                TextView pruebatext1 = new TextView(mcont);
                                pruebatext1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                pruebatext1.setText("Espesor");
                                pruebatext1.setTextAppearance(R.style.TituloItemEncabezado);
                                pruebatext1.setPadding(70, 20, 0, 0);
                                liHori.addView(pruebatext1);

                                liVert.addView(liHori);

                                Resources resForm = getResources();
                                String[] opcionesForm = resForm.getStringArray(R.array.SecuenciaEstratiSuelos);
                                int secuEstratiWidth = 420;
                                int secuEstratiOrdenWidth = 200;
                                int secuEstratiEspesorWidth = 300;

                                for (int m = 0; m < opcionesForm.length ; m++) {
                                    int aux1 = m + 1;

                                    LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                                    liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                                    TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                                    tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvSecuenciaEstratiOpt.setText(opcionesForm[m]);
                                    tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItemEncabezado);
                                    liFormSecuenciaEstrati.addView(tvSecuenciaEstratiOpt);

                                    String opt1 = EditTextsAux.getString(tag+aux1+"orden");

                                    TextView etSecuenciaEstratiOpt = new TextView(mcont);
                                    etSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    etSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                                    etSecuenciaEstratiOpt.setText(opt1);
                                    etSecuenciaEstratiOpt.setPadding(80, 0, 0, 0);
                                    liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt);

                                    String opt2 = EditTextsAux.getString(tag+aux1+"espesor");

                                    TextView etSecuenciaEstratiOpt1Espesor = new TextView(mcont);
                                    etSecuenciaEstratiOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    etSecuenciaEstratiOpt1Espesor.setTextAppearance(R.style.TituloItem);
                                    etSecuenciaEstratiOpt1Espesor.setText(opt2+"m");
                                    etSecuenciaEstratiOpt1Espesor.setPadding(150, 0, 0, 0);
                                    liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt1Espesor);

                                    liVert.addView(liFormSecuenciaEstrati);

                                }

                                String opt1x = EditTextsAux.getString(tag+2+"orden");
                                opt1x = opt1x.replace(" ","");
                                if(!opt1x.equals("")) {
                                    Resources resSuelor = getResources();
                                    String[] opcionesSuelor = resSuelor.getStringArray(R.array.SecuenciaEstratiSuelosSueloRes);
                                    for (int l = 0; l < opcionesSuelor.length; l++) {
                                        int aux2 = l + 1;
                                        LinearLayout liFormSecuenciaEstratiSueloR1 = new LinearLayout(mcont);
                                        liFormSecuenciaEstratiSueloR1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        liFormSecuenciaEstratiSueloR1.setOrientation(LinearLayout.HORIZONTAL);

                                        TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                                        tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(secuEstratiWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        tvSecuenciaEstratiOpt.setText(opcionesForm[j]);
                                        tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                                        liFormSecuenciaEstratiSueloR1.addView(tvSecuenciaEstratiOpt);

                                        String opt1 = EditTextsAux.getString("secuenciaestratisuelor" + aux2 + "orden");

                                        TextView etSecuenciaEstratiOpt = new TextView(mcont);
                                        etSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        etSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                                        etSecuenciaEstratiOpt.setText(opt1);
                                        etSecuenciaEstratiOpt.setPadding(80, 0, 0, 0);
                                        liFormSecuenciaEstratiSueloR1.addView(etSecuenciaEstratiOpt);

                                        String opt2 = EditTextsAux.getString("secuenciaestratisuelor" + aux2 + "espesor");

                                        TextView etSecuenciaEstratiOpt1Espesor = new TextView(mcont);
                                        etSecuenciaEstratiOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        etSecuenciaEstratiOpt1Espesor.setTextAppearance(R.style.TituloItem);
                                        etSecuenciaEstratiOpt1Espesor.setText(opt2 + "m");
                                        etSecuenciaEstratiOpt1Espesor.setPadding(150, 0, 0, 0);
                                        liFormSecuenciaEstratiSueloR1.addView(etSecuenciaEstratiOpt1Espesor);

                                        liVert.addView(liFormSecuenciaEstratiSueloR1);

                                    }
                                }
                            }
                            if (clase.equals("litologias")){
                                TextView tvOpte = new TextView(mcont);
                                tvOpte.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvOpte.setText(titulo+": ");
                                tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                tvOpte.setPadding(0, 20, 0, 0);
                                liVert.addView(tvOpte);

                                TextView tvOpts = new TextView(mcont);
                                tvOpts.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvOpts.setText("1");
                                tvOpts.setTextAppearance(R.style.TituloItem);
                                tvOpts.setPadding(0, 20, 0, 0);
                                liHori.addView(tvOpts);

                                CheckBox checkbox1 = new CheckBox(mcont);
                                checkbox1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                                    checkbox1.setText(aux);
                                checkbox1.setClickable(false);
                                checkbox1.setEnabled(false);
                                liHori.addView(checkbox1);

                                String valorcito1 = CheckBoxAux.getString("litologiasasociadasopt1exist");
                                if(valorcito1.equals("true")){
                                    checkbox1.setChecked(true);
                                }
                                else{
                                    checkbox1.setChecked(false);
                                }

                               String espesor1 = EditTextsAux.getString("litologiasasociadasopt1espesor");
                                TextView tvOpts2 = new TextView(mcont);
                                tvOpts2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvOpts2.setText(espesor1+"m");
                                tvOpts2.setTextAppearance(R.style.TituloItem);
                                tvOpts2.setPadding(20, 20, 20, 0);
                                liHori.addView(tvOpts2);

                                TextView tvOptsx = new TextView(mcont);
                                tvOptsx.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvOptsx.setText("2");
                                tvOptsx.setTextAppearance(R.style.TituloItem);
                                tvOptsx.setPadding(50, 20, 0, 0);
                                liHori.addView(tvOptsx);

                                CheckBox checkbox2 = new CheckBox(mcont);
                                checkbox2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                                    checkbox1.setText(aux);
                                checkbox2.setClickable(false);
                                checkbox2.setEnabled(false);
                                liHori.addView(checkbox2);

                                valorcito1 = CheckBoxAux.getString("litologiasasociadasopt2exist");
                                if(valorcito1.equals("true")){
                                    checkbox2.setChecked(true);
                                }
                                else{
                                    checkbox2.setChecked(false);
                                }

                                String espesor2 = EditTextsAux.getString("litologiasasociadasopt2espesor");
                                TextView tvOpts2x = new TextView(mcont);
                                tvOpts2x.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvOpts2x.setText(espesor2+"m");
                                tvOpts2x.setTextAppearance(R.style.TituloItem);
                                tvOpts2x.setPadding(20, 20, 20, 0);
                                liHori.addView(tvOpts2x);


                                liVert.addView(liHori);


                            }
                            if (clase.equals("radiobtn")){
                                for (int l = 1; l < 3; l++) {
                                    TextView tvGenerico = new TextView(mcont);
                                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvGenerico.setText(titulo+" Litología "+l);
                                    tvGenerico.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvGenerico.setPadding(0, 30, 0, 0);
                                    liVert.addView(tvGenerico);

                                    String valor = RadioGrpAux.getString(tag+l);

                                    TextView etGenerico = new TextView(mcont);
                                    etGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    etGenerico.setText(valor);
                                    etGenerico.setTextAppearance(R.style.TituloItem);
                                    etGenerico.setPadding(0, 10, 0, 0);
                                    etGenerico.setTag(valor);
                                    liVert.addView(etGenerico);
                                }
                            }
                            if (clase.equals("multitext")){
                                TextView tvGenerico = new TextView(mcont);
                                tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvGenerico.setText(titulo);
                                tvGenerico.setTextAppearance(R.style.TituloItemEncabezado);
                                tvGenerico.setPadding(0, 30, 0, 0);
                                liVert.addView(tvGenerico);

                                LinearLayout liradiobtnTitulo = new LinearLayout(mcont);
                                liradiobtnTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                liradiobtnTitulo.setOrientation(LinearLayout.HORIZONTAL);

                                TextView pruebatext = new TextView(mcont);
                                pruebatext.setLayoutParams(new ActionBar.LayoutParams(150, ViewGroup.LayoutParams.WRAP_CONTENT));
                                pruebatext.setText("2");
                                pruebatext.setTextAppearance(R.style.TituloItem);
                                pruebatext.setPadding(70, 10, 0, 0);
                                liradiobtnTitulo.addView(pruebatext);

                                TextView pruebatext1 = new TextView(mcont);
                                pruebatext1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                pruebatext1.setText("1");
                                pruebatext1.setTextAppearance(R.style.TituloItem);
                                pruebatext1.setPadding(50, 10, 0, 0);
                                liradiobtnTitulo.addView(pruebatext1);

                                liVert.addView(liradiobtnTitulo);

                                String[] opciones1 = {};


                                if (tag.equals("granulometria")){
                                    opciones1 = res.getStringArray(R.array.Granulometria1);
                                }
                                if (tag.equals("granulometriamatriz")){
                                    opciones1 = res.getStringArray(R.array.GranulometriaMatriz1);
                                }

                                int secuEstratiOrdenWidth = 150;
                                int secuEstratiEspesorWidth = 150;

                                for (int j1 = 0; j1 < opciones1.length ; j1++) {

                                    String valorcito2 = EditTextsAux.getString(tag+j1+"_2");
                                    String valorcito1 = EditTextsAux.getString(tag+j1+"_1");

                                    LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                                    liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                                    TextView etSecuenciaEstratiOpt = new TextView(mcont);
                                    etSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

                                    etSecuenciaEstratiOpt.setText(valorcito2);
                                    liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt);

                                    TextView etSecuenciaEstratiOpt1Espesor = new TextView(mcont);
                                    etSecuenciaEstratiOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

                                    etSecuenciaEstratiOpt1Espesor.setText(valorcito1);
                                    liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt1Espesor);

                                    TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                                    tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvSecuenciaEstratiOpt.setText(opciones1[j1]);

                                    tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                                    liFormSecuenciaEstrati.addView(tvSecuenciaEstratiOpt);

                                    liVert.addView(liFormSecuenciaEstrati);

                                }
                            }
                            if (clase.equals("radiocheck")){

                                TextView tvGenerico = new TextView(mcont);
                                tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvGenerico.setText(titulo);
                                tvGenerico.setTextAppearance(R.style.TituloItemEncabezado);
                                tvGenerico.setPadding(0, 30, 0, 0);
                                liVert.addView(tvGenerico);

                                LinearLayout liradiobtnTitulo = new LinearLayout(mcont);
                                liradiobtnTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                liradiobtnTitulo.setOrientation(LinearLayout.HORIZONTAL);

                                TextView pruebatext = new TextView(mcont);
                                pruebatext.setLayoutParams(new ActionBar.LayoutParams(150, ViewGroup.LayoutParams.WRAP_CONTENT));
                                pruebatext.setText("2");
                                pruebatext.setTextAppearance(R.style.TituloItem);
                                pruebatext.setPadding(50, 10, 0, 0);
                                liradiobtnTitulo.addView(pruebatext);

                                TextView pruebatext1 = new TextView(mcont);
                                pruebatext1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                pruebatext1.setText("1");
                                pruebatext1.setTextAppearance(R.style.TituloItem);
                                pruebatext1.setPadding(30, 10, 0, 0);
                                liradiobtnTitulo.addView(pruebatext1);

                                liVert.addView(liradiobtnTitulo);


                                int secuEstratiOrdenWidth = 150;
                                int secuEstratiEspesorWidth = 150;

                                String[] opciones2 = {};


                                if (tag.equals("forma")){
                                    opciones2 = res.getStringArray(R.array.Forma1);
                                }
                                if (tag.equals("redondez")){
                                    opciones2 = res.getStringArray(R.array.Redondez1);
                                }

                                for (int j1 = 0; j1 < opciones2.length ; j1++) {

                                    LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                                    liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                                    CheckBox checkbox2 = new CheckBox(mcont);
                                    checkbox2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                                    checkbox2.setText(aux);
                                    checkbox2.setClickable(false);
                                    checkbox2.setEnabled(false);
                                    liFormSecuenciaEstrati.addView(checkbox2);


                                    CheckBox checkbox1 = new CheckBox(mcont);
                                    checkbox1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                                    checkbox1.setText(aux);
                                    checkbox1.setClickable(false);
                                    checkbox1.setEnabled(false);
                                    liFormSecuenciaEstrati.addView(checkbox1);


                                    TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                                    tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvSecuenciaEstratiOpt.setText(opciones2[j1]);
                                    tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                                    liFormSecuenciaEstrati.addView(tvSecuenciaEstratiOpt);


                                    String valorcito2 = CheckBoxAux.getString(tag+j1+"check_2");
                                    if(valorcito2.equals("true")){
                                        checkbox2.setChecked(true);
                                    }
                                    else{
                                        checkbox2.setChecked(false);
                                    }
                                    String valorcito1 = CheckBoxAux.getString(tag+j1+"check_1");
                                    if(valorcito1.equals("true")){
                                        checkbox1.setChecked(true);
                                    }
                                    else{
                                        checkbox1.setChecked(false);
                                    }

                                    liVert.addView(liFormSecuenciaEstrati);

                                }


                            }
                            liFormAcordion.addView(liVert);

                        }

                        int contFotosAnexas = Integer.parseInt(FromatoAux.getString("FotosAnexas"));
                        for (int f = 1; f <= contFotosAnexas; f++) {
                            Button btnFormAcordionDis = new Button(mcont);
                            btnFormAcordionDis.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                            btnFormAcordionDis.setText("Foto Anexa "+f);
                            btnFormAcordionDis.setTag(f);
                            liFormAcordion.addView(btnFormAcordionDis);

                            LinearLayout liFormAcordionDis = new LinearLayout(mcont);
                            liFormAcordionDis.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liFormAcordionDis.setOrientation(LinearLayout.VERTICAL);
                            liFormAcordionDis.setBackgroundColor(0x22222200);
                            liFormAcordionDis.setVisibility(View.GONE);
                            liFormAcordion.addView(liFormAcordionDis);

                            btnFormAcordionDis.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (liFormAcordionDis.getVisibility() == View.VISIBLE) {
                                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                        animation.setDuration(220);
                                        animation.setFillAfter(false);
                                        animation.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }
                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                liFormAcordionDis.setVisibility(View.GONE);
                                                btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                                            }
                                            @Override
                                            public void onAnimationRepeat(Animation animation) {
                                            }
                                        });
                                        liFormAcordionDis.startAnimation(animation);

                                    }
                                    else {
                                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                        animation.setDuration(220);
                                        animation.setFillAfter(false);
                                        liFormAcordionDis.startAnimation(animation);
                                        liFormAcordionDis.setVisibility(View.VISIBLE);
                                        btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                                    }

                                }
                            });

                            for (int h = 0; h < ListaFotosAnexas.size(); h++) {
                                String clase1 = ListaFotosAnexas.get(h)[2];
                                String titulo1 = ListaFotosAnexas.get(h)[1];
                                String tag1 = ListaFotosAnexas.get(h)[0];

                                if (clase1.equals("edittext")){

                                    LinearLayout liHori2 = new LinearLayout(mcont);
                                    liHori2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    liHori2.setOrientation(LinearLayout.VERTICAL);
                                    liHori2.setPadding(20, 0, 0, 20);

                                    String valor = EditTextsAux.getString(tag1+f);

                                    TextView tvOpte = new TextView(mcont);
                                    tvOpte.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpte.setText(titulo1+": ");
                                    tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvOpte.setPadding(0, 20, 0, 0);
                                    liHori2.addView(tvOpte);

                                    TextView tvOpts = new TextView(mcont);
                                    tvOpts.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpts.setText(valor);
                                    tvOpts.setTextAppearance(R.style.TituloItem);
                                    tvOpts.setPadding(0, 20, 0, 0);
                                    liHori2.addView(tvOpts);
                                    liFormAcordionDis.addView(liHori2);
                                }
                            }
                        }
                    }
                    for (int j = 0; j < contSGMF; j++)  {
                        JSONObject FromatoAux = Formularios.getJSONObject("Form_SGMF_"+j);
                        JSONObject SpinnersAux = FromatoAux.getJSONObject("Spinners");
                        JSONObject EditTextsAux = FromatoAux.getJSONObject("EditText");
                        JSONObject CheckBoxAux = FromatoAux.getJSONObject("CheckBox");

                        int aux = j + 1;

                        Button btnFormAcordion = new Button(mcont);
                        btnFormAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        btnFormAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                        btnFormAcordion.setText("Formato SGMF "+aux);
                        btnFormAcordion.setTag(j);
                        liForm.addView(btnFormAcordion);

                        LinearLayout liFormAcordion = new LinearLayout(mcont);
                        liFormAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormAcordion.setOrientation(LinearLayout.VERTICAL);
                        liFormAcordion.setBackgroundColor(0x22222200);
                        liFormAcordion.setVisibility(View.GONE);
                        liForm.addView(liFormAcordion);

                        btnFormAcordion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (liFormAcordion.getVisibility() == View.VISIBLE) {
                                    ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                    animation.setDuration(220);
                                    animation.setFillAfter(false);
                                    animation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {
                                        }
                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            liFormAcordion.setVisibility(View.GONE);
                                            btnFormAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                                        }
                                        @Override
                                        public void onAnimationRepeat(Animation animation) {
                                        }
                                    });
                                    liFormAcordion.startAnimation(animation);

                                }
                                else {
                                    ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                    animation.setDuration(220);
                                    animation.setFillAfter(false);
                                    liFormAcordion.startAnimation(animation);
                                    liFormAcordion.setVisibility(View.VISIBLE);
                                    btnFormAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                                }

                            }
                        });

                        for (int k = 0; k < ListaSGMF.size(); k++) {
                            LinearLayout liHori = new LinearLayout(mcont);
                            liHori.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liHori.setOrientation(LinearLayout.HORIZONTAL);
                            liHori.setPadding(0, 0, 0, 20);

                            LinearLayout liVert = new LinearLayout(mcont);
                            liVert.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liVert.setOrientation(LinearLayout.VERTICAL);
                            liVert.setPadding(0, 0, 0, 20);

                            String clase = ListaSGMF.get(k)[1];
                            String titulo = ListaSGMF.get(k)[0];
                            String tag = ListaSGMF.get(k)[2];

                            if (clase.equals("edittext") || clase.equals("spinner")){
                                String valor;

                                if(clase.equals("edittext")){
                                    valor = EditTextsAux.getString(tag);
                                }else{
                                    valor = SpinnersAux.getString(tag);
                                }


                                TextView tvOpte = new TextView(mcont);
                                tvOpte.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvOpte.setText(titulo+": ");
                                tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                tvOpte.setPadding(0, 20, 0, 0);
                                liHori.addView(tvOpte);

                                TextView tvOpts = new TextView(mcont);
                                tvOpts.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvOpts.setText(valor);
                                tvOpts.setTextAppearance(R.style.TituloItem);
                                tvOpts.setPadding(0, 20, 0, 0);
                                liHori.addView(tvOpts);
                                liVert.addView(liHori);


                            }
                            if (clase.equals("titulito")){
                                TextView tvGenerico = new TextView(mcont);
                                tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvGenerico.setText(titulo);
                                tvGenerico.setTextAppearance(R.style.TituloFormato);
                                tvGenerico.setPadding(0, 30, 0, 0);
                                liHori.addView(tvGenerico);
                                liVert.addView(liHori);
                            }
                            if (clase.equals("ambientes")){

                                String[] opciones3 = res.getStringArray(R.array.Ambientes);

                                TextView tvGenerico = new TextView(mcont);
                                tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvGenerico.setText(titulo);
                                tvGenerico.setTextAppearance(R.style.TituloItemEncabezado);
                                tvGenerico.setPadding(0, 30, 0, 0);
                                liVert.addView(tvGenerico);

                                for (int j2 = 0; j2 < opciones3.length ; j2++) {

                                    LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                                    liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                                    CheckBox checkbox1 = new CheckBox(mcont);
                                    checkbox1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                                    checkbox1.setText(aux);
                                    checkbox1.setClickable(false);
                                    checkbox1.setEnabled(false);
                                    liFormSecuenciaEstrati.addView(checkbox1);

                                    TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                                    tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvSecuenciaEstratiOpt.setText(opciones3[j2]);
                                    tvSecuenciaEstratiOpt.setPadding(5, 0, 0, 0);
                                    tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                                    liFormSecuenciaEstrati.addView(tvSecuenciaEstratiOpt);


                                    String valorcito1 = CheckBoxAux.getString(tag+j2+"check");
                                    if(valorcito1.equals("true")){
                                        checkbox1.setChecked(true);
                                    }
                                    else{
                                        checkbox1.setChecked(false);
                                    }

                                    liVert.addView(liFormSecuenciaEstrati);
                                }

                            }
                            if (clase.equals("ubicacionGeo")){

                                String[] opciones3 = res.getStringArray(R.array.UbicacionGeomorfo);

                                TextView tvGenerico = new TextView(mcont);
                                tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvGenerico.setText(titulo);
                                tvGenerico.setTextAppearance(R.style.TituloFormato);
                                tvGenerico.setPadding(0, 30, 0, 0);
                                liVert.addView(tvGenerico);

                                for (int j2 = 0; j2 < opciones3.length ; j2++) {

                                    String valorcito1 = EditTextsAux.getString(tag+opciones3[j2]);

                                    LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                                    liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                                    TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                                    tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(450, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvSecuenciaEstratiOpt.setText(opciones3[j2]);
                                    tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                                    liFormSecuenciaEstrati.addView(tvSecuenciaEstratiOpt);

                                    TextView etSecuenciaEstratiOpt = new TextView(mcont);
                                    etSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    etSecuenciaEstratiOpt.setText(valorcito1);
                                    liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt);

                                    liVert.addView(liFormSecuenciaEstrati);

                                }

                            }


                            liFormAcordion.addView(liVert);

                        }

                        int contNewSGMF = Integer.parseInt(FromatoAux.getString("SGMF"));
                        for (int f = 1; f <= contNewSGMF; f++) {
                            Button btnFormAcordionDis = new Button(mcont);
                            btnFormAcordionDis.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                            btnFormAcordionDis.setText("SGMF - EGMF "+f);
                            btnFormAcordionDis.setTag(f);
                            liFormAcordion.addView(btnFormAcordionDis);

                            LinearLayout liFormAcordionDis = new LinearLayout(mcont);
                            liFormAcordionDis.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liFormAcordionDis.setOrientation(LinearLayout.VERTICAL);
                            liFormAcordionDis.setBackgroundColor(0x22222200);
                            liFormAcordionDis.setVisibility(View.GONE);
                            liFormAcordion.addView(liFormAcordionDis);

                            btnFormAcordionDis.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (liFormAcordionDis.getVisibility() == View.VISIBLE) {
                                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                        animation.setDuration(220);
                                        animation.setFillAfter(false);
                                        animation.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }
                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                liFormAcordionDis.setVisibility(View.GONE);
                                                btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                                            }
                                            @Override
                                            public void onAnimationRepeat(Animation animation) {
                                            }
                                        });
                                        liFormAcordionDis.startAnimation(animation);

                                    }
                                    else {
                                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                        animation.setDuration(220);
                                        animation.setFillAfter(false);
                                        liFormAcordionDis.startAnimation(animation);
                                        liFormAcordionDis.setVisibility(View.VISIBLE);
                                        btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                                    }

                                }
                            });

                            for (int h = 0; h < ListaNewSGMF.size(); h++) {
                                String clase1 = ListaNewSGMF.get(h)[1];
                                String titulo1 = ListaNewSGMF.get(h)[0];
                                String tag1 = ListaNewSGMF.get(h)[2];

                                if (clase1.equals("edittext") || clase1.equals("spinner")){
                                    String valor;

                                    if(clase1.equals("edittext")){
                                        valor = EditTextsAux.getString(tag1+f);
                                    }else{
                                        valor = SpinnersAux.getString(tag1+f);
                                    }


                                    TextView tvOpte = new TextView(mcont);
                                    tvOpte.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpte.setText(titulo1+": ");
                                    tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvOpte.setPadding(0, 20, 0, 0);
                                    liFormAcordionDis.addView(tvOpte);

                                    TextView tvOpts = new TextView(mcont);
                                    tvOpts.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpts.setText(valor);
                                    tvOpts.setTextAppearance(R.style.TituloItem);
                                    tvOpts.setPadding(0, 20, 0, 0);
                                    liFormAcordionDis.addView(tvOpts);



                                }
                                if (clase1.equals("ambientes")){
                                    String[] opciones3 = {};
                                    if(tag1.equals("tipodemm")){
                                        opciones3 = res.getStringArray(R.array.TipoMMGMF);
                                    }else{
                                        opciones3 = res.getStringArray(R.array.TipoMaterialGMF);
                                    }

                                    TextView tvGenerico = new TextView(mcont);
                                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvGenerico.setText(titulo1);
                                    tvGenerico.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvGenerico.setPadding(0, 30, 0, 0);
                                    liFormAcordionDis.addView(tvGenerico);

                                    for (int j2 = 0; j2 < opciones3.length ; j2++) {

                                        LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                                        liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                                        CheckBox checkbox1 = new CheckBox(mcont);
                                        checkbox1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                                    checkbox1.setText(aux);
                                        checkbox1.setClickable(false);
                                        checkbox1.setEnabled(false);
                                        liFormSecuenciaEstrati.addView(checkbox1);

                                        TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                                        tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        tvSecuenciaEstratiOpt.setText(opciones3[j2]);
                                        tvSecuenciaEstratiOpt.setPadding(5, 0, 0, 0);
                                        tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                                        liFormSecuenciaEstrati.addView(tvSecuenciaEstratiOpt);


                                        String valorcito1 = CheckBoxAux.getString(tag1+j2+"check"+f);
                                        if(valorcito1.equals("true")){
                                            checkbox1.setChecked(true);
                                        }
                                        else{
                                            checkbox1.setChecked(false);
                                        }

                                        liFormAcordionDis.addView(liFormSecuenciaEstrati);
                                    }

                                }
                            }
                        }

                        int contFotosAnexas = Integer.parseInt(FromatoAux.getString("FotosAnexas"));
                        for (int f = 1; f <= contFotosAnexas; f++) {
                            Button btnFormAcordionDis = new Button(mcont);
                            btnFormAcordionDis.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                            btnFormAcordionDis.setText("Foto Anexa "+f);
                            btnFormAcordionDis.setTag(f);
                            liFormAcordion.addView(btnFormAcordionDis);

                            LinearLayout liFormAcordionDis = new LinearLayout(mcont);
                            liFormAcordionDis.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liFormAcordionDis.setOrientation(LinearLayout.VERTICAL);
                            liFormAcordionDis.setBackgroundColor(0x22222200);
                            liFormAcordionDis.setVisibility(View.GONE);
                            liFormAcordion.addView(liFormAcordionDis);

                            btnFormAcordionDis.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (liFormAcordionDis.getVisibility() == View.VISIBLE) {
                                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                        animation.setDuration(220);
                                        animation.setFillAfter(false);
                                        animation.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }
                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                liFormAcordionDis.setVisibility(View.GONE);
                                                btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                                            }
                                            @Override
                                            public void onAnimationRepeat(Animation animation) {
                                            }
                                        });
                                        liFormAcordionDis.startAnimation(animation);

                                    }
                                    else {
                                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                        animation.setDuration(220);
                                        animation.setFillAfter(false);
                                        liFormAcordionDis.startAnimation(animation);
                                        liFormAcordionDis.setVisibility(View.VISIBLE);
                                        btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                                    }

                                }
                            });

                            for (int h = 0; h < ListaFotosAnexas.size(); h++) {
                                String clase1 = ListaFotosAnexas.get(h)[2];
                                String titulo1 = ListaFotosAnexas.get(h)[1];
                                String tag1 = ListaFotosAnexas.get(h)[0];

                                if (clase1.equals("edittext")){

                                    LinearLayout liHori2 = new LinearLayout(mcont);
                                    liHori2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    liHori2.setOrientation(LinearLayout.VERTICAL);
                                    liHori2.setPadding(20, 0, 0, 20);

                                    String valor = EditTextsAux.getString(tag1+f);

                                    TextView tvOpte = new TextView(mcont);
                                    tvOpte.setLayoutParams(new ActionBar.LayoutParams(450, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpte.setText(titulo1+": ");
                                    tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvOpte.setPadding(0, 20, 0, 0);
                                    liHori2.addView(tvOpte);

                                    TextView tvOpts = new TextView(mcont);
                                    tvOpts.setLayoutParams(new ActionBar.LayoutParams(450, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpts.setText(valor);
                                    tvOpts.setTextAppearance(R.style.TituloItem);
                                    tvOpts.setPadding(0, 20, 0, 0);
                                    liHori2.addView(tvOpts);
                                    liFormAcordionDis.addView(liHori2);
                                }
                            }
                        }
                    }
                    for (int j = 0; j < contCATALOGO; j++)  {
                        JSONObject FromatoAux = Formularios.getJSONObject("Form_CATALOGO_"+j);
                        JSONObject SpinnersAux = FromatoAux.getJSONObject("Spinners");
                        JSONObject EditTextsAux = FromatoAux.getJSONObject("EditText");
                        JSONObject RadioGrpAux = FromatoAux.getJSONObject("RadioGrp");

                        int aux = j + 1;

                        Button btnFormAcordion = new Button(mcont);
                        btnFormAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        btnFormAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                        btnFormAcordion.setText("Formato Catalogo de MM "+aux);
                        btnFormAcordion.setTag(j);
                        liForm.addView(btnFormAcordion);

                        LinearLayout liFormAcordion = new LinearLayout(mcont);
                        liFormAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormAcordion.setOrientation(LinearLayout.VERTICAL);
                        liFormAcordion.setBackgroundColor(0x22222200);
                        liFormAcordion.setVisibility(View.GONE);
                        liForm.addView(liFormAcordion);

                        btnFormAcordion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (liFormAcordion.getVisibility() == View.VISIBLE) {
                                    ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                    animation.setDuration(220);
                                    animation.setFillAfter(false);
                                    animation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {
                                        }
                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            liFormAcordion.setVisibility(View.GONE);
                                            btnFormAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                                        }
                                        @Override
                                        public void onAnimationRepeat(Animation animation) {
                                        }
                                    });
                                    liFormAcordion.startAnimation(animation);

                                }
                                else {
                                    ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                    animation.setDuration(220);
                                    animation.setFillAfter(false);
                                    liFormAcordion.startAnimation(animation);
                                    liFormAcordion.setVisibility(View.VISIBLE);
                                    btnFormAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                                }

                            }
                        });

                        for (int k = 0; k < ListaCATALOGO.size(); k++) {
                            LinearLayout liHori = new LinearLayout(mcont);
                            liHori.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liHori.setOrientation(LinearLayout.HORIZONTAL);
                            liHori.setPadding(0, 0, 0, 20);

                            LinearLayout liVert = new LinearLayout(mcont);
                            liVert.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liVert.setOrientation(LinearLayout.VERTICAL);
                            liVert.setPadding(0, 0, 0, 20);

                            String clase = ListaCATALOGO.get(k)[1];
                            String titulo = ListaCATALOGO.get(k)[0];
                            String tag = ListaCATALOGO.get(k)[2];

                            if (clase.equals("edittext") || clase.equals("spinner")){
                                String valor;

                                if(clase.equals("edittext")){
                                    valor = EditTextsAux.getString(tag);
                                }else{
                                    valor = SpinnersAux.getString(tag);
                                }

                                if (tag.equals("REF_GEOGRF") || tag.equals("FTE_INFSEC") || tag.equals("LITOLOGIA") || tag.equals("SUBTIPO_1") || tag.equals("SUBTIPO_2") || tag.equals("sisclasificacion") || tag.equals("AN_GMF") || tag.equals("notas") || tag.equals("sensoresremotos") || tag.equals("SITIO") ){
                                    TextView tvOpte = new TextView(mcont);
                                    tvOpte.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpte.setText(titulo+": ");
                                    tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvOpte.setPadding(0, 20, 0, 0);
                                    liVert.addView(tvOpte);

                                    TextView tvOpts = new TextView(mcont);
                                    tvOpts.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpts.setText(valor);
                                    tvOpts.setTextAppearance(R.style.TituloItem);
                                    tvOpts.setPadding(0, 20, 0, 0);
                                    liVert.addView(tvOpts);


                                }
                                else{
                                    TextView tvOpte = new TextView(mcont);
                                    tvOpte.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpte.setText(titulo+": ");
                                    tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvOpte.setPadding(0, 20, 0, 0);
                                    liHori.addView(tvOpte);

                                    TextView tvOpts = new TextView(mcont);
                                    tvOpts.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpts.setText(valor);
                                    tvOpts.setTextAppearance(R.style.TituloItem);
                                    tvOpts.setPadding(0, 20, 0, 0);
                                    liHori.addView(tvOpts);

                                    liVert.addView(liHori);
                                }


                            }
                            if (clase.equals("titulito")){
                                TextView tvGenerico = new TextView(mcont);
                                tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvGenerico.setText(titulo);
                                tvGenerico.setTextAppearance(R.style.TituloFormato);
                                tvGenerico.setPadding(0, 30, 0, 0);
                                liHori.addView(tvGenerico);
                                liVert.addView(liHori);
                            }
                            if (clase.equals("radiobtn")){
                                for (int l = 1; l < 3; l++) {
                                    TextView tvGenerico = new TextView(mcont);
                                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvGenerico.setText(titulo+" Litología "+l);
                                    tvGenerico.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvGenerico.setPadding(0, 30, 0, 0);
                                    liVert.addView(tvGenerico);

                                    String valor = RadioGrpAux.getString(tag+l);

                                    TextView etGenerico = new TextView(mcont);
                                    etGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    etGenerico.setText(valor);
                                    etGenerico.setTextAppearance(R.style.TituloItem);
                                    etGenerico.setPadding(0, 10, 0, 0);
                                    etGenerico.setTag(valor);
                                    liVert.addView(etGenerico);
                                }
                            }



                            liFormAcordion.addView(liVert);

                        }

                        int contDANOS = Integer.parseInt(FromatoAux.getString("DANOS"));
                        for (int f = 1; f <= contDANOS; f++) {
                            Button btnFormAcordionDis = new Button(mcont);
                            btnFormAcordionDis.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                            btnFormAcordionDis.setText("DAÑO "+f);
                            btnFormAcordionDis.setTag(f);
                            liFormAcordion.addView(btnFormAcordionDis);

                            LinearLayout liFormAcordionDis = new LinearLayout(mcont);
                            liFormAcordionDis.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liFormAcordionDis.setOrientation(LinearLayout.VERTICAL);
                            liFormAcordionDis.setBackgroundColor(0x22222200);
                            liFormAcordionDis.setVisibility(View.GONE);
                            liFormAcordion.addView(liFormAcordionDis);

                            btnFormAcordionDis.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (liFormAcordionDis.getVisibility() == View.VISIBLE) {
                                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                        animation.setDuration(220);
                                        animation.setFillAfter(false);
                                        animation.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }
                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                liFormAcordionDis.setVisibility(View.GONE);
                                                btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                                            }
                                            @Override
                                            public void onAnimationRepeat(Animation animation) {
                                            }
                                        });
                                        liFormAcordionDis.startAnimation(animation);

                                    }
                                    else {
                                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                        animation.setDuration(220);
                                        animation.setFillAfter(false);
                                        liFormAcordionDis.startAnimation(animation);
                                        liFormAcordionDis.setVisibility(View.VISIBLE);
                                        btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                                    }

                                }
                            });

                            for (int h = 0; h < ListaDANOS.size(); h++) {
                                String clase1 = ListaDANOS.get(h)[1];
                                String titulo1 = ListaDANOS.get(h)[0];
                                String tag1 = ListaDANOS.get(h)[2];

                                if (clase1.equals("edittext") || clase1.equals("spinner")){
                                    String valor;

                                    if(clase1.equals("edittext")){
                                        valor = EditTextsAux.getString(tag1+f);
                                    }else{
                                        valor = SpinnersAux.getString(tag1+f);
                                    }


                                    TextView tvOpte = new TextView(mcont);
                                    tvOpte.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpte.setText(titulo1+": ");
                                    tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvOpte.setPadding(0, 20, 0, 0);
                                    liFormAcordionDis.addView(tvOpte);

                                    TextView tvOpts = new TextView(mcont);
                                    tvOpts.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpts.setText(valor);
                                    tvOpts.setTextAppearance(R.style.TituloItem);
                                    tvOpts.setPadding(0, 20, 0, 0);
                                    liFormAcordionDis.addView(tvOpts);

                                }

                            }
                        }


                    }
                    for (int j = 0; j < contINVENTARIO; j++)  {
                        JSONObject FromatoAux = Formularios.getJSONObject("Form_INVENTARIO_"+j);
                        JSONObject SpinnersAux = FromatoAux.getJSONObject("Spinners");
                        JSONObject EditTextsAux = FromatoAux.getJSONObject("EditText");
                        JSONObject RadioGrpAux = FromatoAux.getJSONObject("RadioGrp");
                        JSONObject CheckBoxAux = FromatoAux.getJSONObject("CheckBox");

                        int aux = j + 1;

                        Button btnFormAcordion = new Button(mcont);
                        btnFormAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        btnFormAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                        btnFormAcordion.setText("Formato Inventario de MM "+aux);
                        btnFormAcordion.setTag(j);
                        liForm.addView(btnFormAcordion);

                        LinearLayout liFormAcordion = new LinearLayout(mcont);
                        liFormAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormAcordion.setOrientation(LinearLayout.VERTICAL);
                        liFormAcordion.setBackgroundColor(0x22222200);
                        liFormAcordion.setVisibility(View.GONE);
                        liForm.addView(liFormAcordion);

                        btnFormAcordion.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                if (liFormAcordion.getVisibility() == View.VISIBLE) {
                                    ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                    animation.setDuration(220);
                                    animation.setFillAfter(false);
                                    animation.setAnimationListener(new Animation.AnimationListener() {
                                        @Override
                                        public void onAnimationStart(Animation animation) {
                                        }
                                        @Override
                                        public void onAnimationEnd(Animation animation) {
                                            liFormAcordion.setVisibility(View.GONE);
                                            btnFormAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                                        }
                                        @Override
                                        public void onAnimationRepeat(Animation animation) {
                                        }
                                    });
                                    liFormAcordion.startAnimation(animation);

                                }
                                else {
                                    ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                    animation.setDuration(220);
                                    animation.setFillAfter(false);
                                    liFormAcordion.startAnimation(animation);
                                    liFormAcordion.setVisibility(View.VISIBLE);
                                    btnFormAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                                }

                            }
                        });

                        for (int k = 0; k < ListaINVENTARIO.size(); k++) {
                            LinearLayout liHori = new LinearLayout(mcont);
                            liHori.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liHori.setOrientation(LinearLayout.HORIZONTAL);
                            liHori.setPadding(0, 0, 0, 20);

                            LinearLayout liVert = new LinearLayout(mcont);
                            liVert.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liVert.setOrientation(LinearLayout.VERTICAL);
                            liVert.setPadding(0, 0, 0, 20);

                            String clase = ListaINVENTARIO.get(k)[1];
                            String titulo = ListaINVENTARIO.get(k)[0];
                            String tag = ListaINVENTARIO.get(k)[2];

                            if (clase.equals("edittext") || clase.equals("spinner")){
                                String valor;

                                if(clase.equals("edittext")){
                                    valor = EditTextsAux.getString(tag);
                                }else{
                                    valor = SpinnersAux.getString(tag);
                                }

                                if (tag.equals("REF_GEOGRF") || tag.equals("FTE_INFSEC") || tag.equals("LITOLOGIA") || tag.equals("SUBTIPO_1") || tag.equals("SUBTIPO_2") || tag.equals("sisclasificacion") || tag.equals("AN_GMF") || tag.equals("notas") || tag.equals("apreciacionriesgo") || tag.equals("SITIO")){
                                    TextView tvOpte = new TextView(mcont);
                                    tvOpte.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpte.setText(titulo+": ");
                                    tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvOpte.setPadding(0, 20, 0, 0);
                                    liVert.addView(tvOpte);

                                    TextView tvOpts = new TextView(mcont);
                                    tvOpts.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpts.setText(valor);
                                    tvOpts.setTextAppearance(R.style.TituloItem);
                                    tvOpts.setPadding(0, 20, 0, 0);
                                    liVert.addView(tvOpts);


                                }
                                else{
                                    TextView tvOpte = new TextView(mcont);
                                    tvOpte.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpte.setText(titulo+": ");
                                    tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvOpte.setPadding(0, 20, 0, 0);
                                    liHori.addView(tvOpte);

                                    TextView tvOpts = new TextView(mcont);
                                    tvOpts.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpts.setText(valor);
                                    tvOpts.setTextAppearance(R.style.TituloItem);
                                    tvOpts.setPadding(0, 20, 0, 0);
                                    liHori.addView(tvOpts);

                                    liVert.addView(liHori);
                                }


                            }
                            if (clase.equals("titulito")){
                                TextView tvGenerico = new TextView(mcont);
                                tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvGenerico.setText(titulo);
                                tvGenerico.setTextAppearance(R.style.TituloFormato);
                                tvGenerico.setPadding(0, 30, 0, 0);
                                liHori.addView(tvGenerico);
                                liVert.addView(liHori);
                            }
                            if (clase.equals("radiobtn")){
                                for (int l = 1; l < 3; l++) {
                                    TextView tvGenerico = new TextView(mcont);
                                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvGenerico.setText(titulo+" Litología "+l);
                                    tvGenerico.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvGenerico.setPadding(0, 30, 0, 0);
                                    liVert.addView(tvGenerico);

                                    String valor = RadioGrpAux.getString(tag+l);

                                    TextView etGenerico = new TextView(mcont);
                                    etGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    etGenerico.setText(valor);
                                    etGenerico.setTextAppearance(R.style.TituloItem);
                                    etGenerico.setPadding(0, 10, 0, 0);
                                    etGenerico.setTag(valor);
                                    liVert.addView(etGenerico);
                                }
                            }
                            if (clase.equals("multitext")){
                                TextView tvGenerico = new TextView(mcont);
                                tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvGenerico.setText(titulo);
                                tvGenerico.setTextAppearance(R.style.TituloItemEncabezado);
                                tvGenerico.setPadding(0, 30, 0, 0);
                                liVert.addView(tvGenerico);

                                LinearLayout liradiobtnTitulo = new LinearLayout(mcont);
                                liradiobtnTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                liradiobtnTitulo.setOrientation(LinearLayout.HORIZONTAL);


                                liVert.addView(liradiobtnTitulo);

                                String[] opciones1 = {};

                                int auxWidth = 800;

                                if (tag.equals("morfogeneral")){
                                    opciones1 = res.getStringArray(R.array.GeneralMM);
                                }
                                if (tag.equals("morfodimensiones")){
                                    opciones1 = res.getStringArray(R.array.DimenTerrenoMM);
                                }
                                if (tag.equals("cobertura")){
                                    opciones1 = res.getStringArray(R.array.CoberturaSueloMM);
                                }
                                if (tag.equals("usosuelo")){
                                    opciones1 = res.getStringArray(R.array.UsoSueloMM);
                                }
                                if (tag.equals("represamientomorfometria")){
                                    opciones1 = res.getStringArray(R.array.MorfometriaPresaMM);
                                }
                                if (tag.equals("sismoMM")){
                                    opciones1 = res.getStringArray(R.array.SismoMM);
                                    auxWidth = 600;
                                }
                                if (tag.equals("lluviasMM")){
                                    opciones1 = res.getStringArray(R.array.LluviasMM);
                                    auxWidth = 200;
                                }

                                for (int j1 = 0; j1 < opciones1.length ; j1++) {

                                    String valorcito1;
                                    try {
                                        valorcito1 = EditTextsAux.getString(tag+j1);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        valorcito1 = "";
                                    }


                                    LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                                    liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                                    TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                                    tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(auxWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvSecuenciaEstratiOpt.setText(opciones1[j1]+": ");
                                    tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                                    liFormSecuenciaEstrati.addView(tvSecuenciaEstratiOpt);

                                    TextView etSecuenciaEstratiOpt1Espesor = new TextView(mcont);
                                    etSecuenciaEstratiOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    etSecuenciaEstratiOpt1Espesor.setPadding(10, 0, 0, 0);
                                    etSecuenciaEstratiOpt1Espesor.setText(valorcito1);
                                    liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt1Espesor);

                                    liVert.addView(liFormSecuenciaEstrati);

                                }
                            }
                            if (clase.equals("radiocheckMM")){

                                String text2 = "2";
                                String text1 = "1";

                                if (titulo.equals("CONTRIBUYENTES - DETONANTES")){
                                    text2 = "C";
                                    text1 = "D";
                                }

                                TextView tvGenerico = new TextView(mcont);
                                tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvGenerico.setText(titulo);
                                tvGenerico.setTextAppearance(R.style.TituloItemEncabezado);
                                tvGenerico.setPadding(0, 30, 0, 0);
                                liVert.addView(tvGenerico);

                                LinearLayout liradiobtnTitulo = new LinearLayout(mcont);
                                liradiobtnTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                liradiobtnTitulo.setOrientation(LinearLayout.HORIZONTAL);

                                TextView pruebatext = new TextView(mcont);
                                pruebatext.setLayoutParams(new ActionBar.LayoutParams(150, ViewGroup.LayoutParams.WRAP_CONTENT));
                                pruebatext.setText(text2);
                                pruebatext.setTextAppearance(R.style.TituloItem);
                                pruebatext.setPadding(50, 10, 0, 0);
                                liradiobtnTitulo.addView(pruebatext);

                                TextView pruebatext1 = new TextView(mcont);
                                pruebatext1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                pruebatext1.setText(text1);
                                pruebatext1.setTextAppearance(R.style.TituloItem);
                                pruebatext1.setPadding(30, 10, 0, 0);
                                liradiobtnTitulo.addView(pruebatext1);

                                liVert.addView(liradiobtnTitulo);


                                int secuEstratiOrdenWidth = 150;
                                int secuEstratiEspesorWidth = 150;

                                String[] opciones2 = {};


                                if (tag.equals("tipomaterial")){
                                    opciones2 = res.getStringArray(R.array.TipoMaterialMM);
                                }
                                if (tag.equals("causascontrideto")){
                                    opciones2 = res.getStringArray(R.array.ContriDetonantesMM);
                                }

                                for (int j1 = 0; j1 < opciones2.length ; j1++) {
                                    LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                                    liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);


                                    CheckBox checkbox2 = new CheckBox(mcont);
                                    checkbox2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                                    checkbox2.setText(aux);
                                    checkbox2.setClickable(false);
                                    checkbox2.setEnabled(false);
                                    liFormSecuenciaEstrati.addView(checkbox2);


                                    CheckBox checkbox1 = new CheckBox(mcont);
                                    checkbox1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                                    checkbox1.setText(aux);
                                    checkbox1.setClickable(false);
                                    checkbox1.setEnabled(false);
                                    liFormSecuenciaEstrati.addView(checkbox1);


                                    TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                                    tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvSecuenciaEstratiOpt.setText(opciones2[j1]);
                                    tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                                    liFormSecuenciaEstrati.addView(tvSecuenciaEstratiOpt);


                                    String valorcito2 = CheckBoxAux.getString(tag+j1+"check_2");
                                    if(valorcito2.equals("true")){
                                        checkbox2.setChecked(true);
                                    }
                                    else{
                                        checkbox2.setChecked(false);
                                    }
                                    String valorcito1 = CheckBoxAux.getString(tag+j1+"check_1");
                                    if(valorcito1.equals("true")){
                                        checkbox1.setChecked(true);
                                    }
                                    else{
                                        checkbox1.setChecked(false);
                                    }

                                    liVert.addView(liFormSecuenciaEstrati);

                                }


                            }
                            if (clase.equals("radiocheck")){


                                TextView tvGenerico = new TextView(mcont);
                                tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvGenerico.setText(titulo);
                                tvGenerico.setTextAppearance(R.style.TituloItemEncabezado);
                                tvGenerico.setPadding(0, 30, 0, 0);
                                liVert.addView(tvGenerico);

                                LinearLayout liradiobtnTitulo = new LinearLayout(mcont);
                                liradiobtnTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                liradiobtnTitulo.setOrientation(LinearLayout.HORIZONTAL);

                                liVert.addView(liradiobtnTitulo);


                                int secuEstratiOrdenWidth = 150;
                                int secuEstratiEspesorWidth = 150;

                                String[] opciones2 = {};


                                if (tag.equals("causasinherentes")){
                                    opciones2 = res.getStringArray(R.array.InherentesMM);
                                }
                                if (tag.equals("erosionsuperficial")){
                                    opciones2 = res.getStringArray(R.array.ErosionSuperficialMM);
                                }
                                if (tag.equals("erosionsubsuperficial")){
                                    opciones2 = res.getStringArray(R.array.ErosionSubsuperficialMM);
                                }
                                if (tag.equals("represamientocondiciones")){
                                    opciones2 = res.getStringArray(R.array.CondicionesPresa);
                                }
                                if (tag.equals("represamientoefectos")){
                                    opciones2 = res.getStringArray(R.array.EfectosPresa);
                                }

                                for (int j1 = 0; j1 < opciones2.length ; j1++) {
                                    LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                                    liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                                    CheckBox checkbox1 = new CheckBox(mcont);
                                    checkbox1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                                    checkbox1.setText(aux);
                                    checkbox1.setClickable(false);
                                    checkbox1.setEnabled(false);
                                    liFormSecuenciaEstrati.addView(checkbox1);

                                    TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                                    tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvSecuenciaEstratiOpt.setText(opciones2[j1]);
                                    tvSecuenciaEstratiOpt.setPadding(5, 0, 0, 0);
                                    tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                                    liFormSecuenciaEstrati.addView(tvSecuenciaEstratiOpt);


                                    String valorcito1 = CheckBoxAux.getString(tag+j1+"check");
                                    if(valorcito1.equals("true")){
                                        checkbox1.setChecked(true);
                                    }
                                    else{
                                        checkbox1.setChecked(false);
                                    }

                                    liVert.addView(liFormSecuenciaEstrati);

                                }


                            }


                            liFormAcordion.addView(liVert);

                        }

                        int contDANOS = Integer.parseInt(FromatoAux.getString("DANOS"));
                        for (int f = 1; f <= contDANOS; f++) {
                            Button btnFormAcordionDis = new Button(mcont);
                            btnFormAcordionDis.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                            btnFormAcordionDis.setText("DAÑO "+f);
                            btnFormAcordionDis.setTag(f);
                            liFormAcordion.addView(btnFormAcordionDis);

                            LinearLayout liFormAcordionDis = new LinearLayout(mcont);
                            liFormAcordionDis.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liFormAcordionDis.setOrientation(LinearLayout.VERTICAL);
                            liFormAcordionDis.setBackgroundColor(0x22222200);
                            liFormAcordionDis.setVisibility(View.GONE);
                            liFormAcordion.addView(liFormAcordionDis);

                            btnFormAcordionDis.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (liFormAcordionDis.getVisibility() == View.VISIBLE) {
                                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                        animation.setDuration(220);
                                        animation.setFillAfter(false);
                                        animation.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }
                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                liFormAcordionDis.setVisibility(View.GONE);
                                                btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                                            }
                                            @Override
                                            public void onAnimationRepeat(Animation animation) {
                                            }
                                        });
                                        liFormAcordionDis.startAnimation(animation);

                                    }
                                    else {
                                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                        animation.setDuration(220);
                                        animation.setFillAfter(false);
                                        liFormAcordionDis.startAnimation(animation);
                                        liFormAcordionDis.setVisibility(View.VISIBLE);
                                        btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                                    }

                                }
                            });

                            for (int h = 0; h < ListaDANOS.size(); h++) {
                                String clase1 = ListaDANOS.get(h)[1];
                                String titulo1 = ListaDANOS.get(h)[0];
                                String tag1 = ListaDANOS.get(h)[2];

                                if (clase1.equals("edittext") || clase1.equals("spinner")){
                                    String valor;

                                    if(clase1.equals("edittext")){
                                        valor = EditTextsAux.getString(tag1+f);
                                    }else{
                                        valor = SpinnersAux.getString(tag1+f);
                                    }


                                    TextView tvOpte = new TextView(mcont);
                                    tvOpte.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpte.setText(titulo1+": ");
                                    tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvOpte.setPadding(0, 20, 0, 0);
                                    liFormAcordionDis.addView(tvOpte);

                                    TextView tvOpts = new TextView(mcont);
                                    tvOpts.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpts.setText(valor);
                                    tvOpts.setTextAppearance(R.style.TituloItem);
                                    tvOpts.setPadding(0, 20, 0, 0);
                                    liFormAcordionDis.addView(tvOpts);

                                }

                            }
                        }

                        int contFotosAnexas = Integer.parseInt(FromatoAux.getString("FotosAnexas"));
                        for (int f = 1; f <= contFotosAnexas; f++) {
                            Button btnFormAcordionDis = new Button(mcont);
                            btnFormAcordionDis.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                            btnFormAcordionDis.setText("Foto Anexa "+f);
                            btnFormAcordionDis.setTag(f);
                            liFormAcordion.addView(btnFormAcordionDis);

                            LinearLayout liFormAcordionDis = new LinearLayout(mcont);
                            liFormAcordionDis.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liFormAcordionDis.setOrientation(LinearLayout.VERTICAL);
                            liFormAcordionDis.setBackgroundColor(0x22222200);
                            liFormAcordionDis.setVisibility(View.GONE);
                            liFormAcordion.addView(liFormAcordionDis);

                            btnFormAcordionDis.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    if (liFormAcordionDis.getVisibility() == View.VISIBLE) {
                                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                        animation.setDuration(220);
                                        animation.setFillAfter(false);
                                        animation.setAnimationListener(new Animation.AnimationListener() {
                                            @Override
                                            public void onAnimationStart(Animation animation) {
                                            }
                                            @Override
                                            public void onAnimationEnd(Animation animation) {
                                                liFormAcordionDis.setVisibility(View.GONE);
                                                btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                                            }
                                            @Override
                                            public void onAnimationRepeat(Animation animation) {
                                            }
                                        });
                                        liFormAcordionDis.startAnimation(animation);

                                    }
                                    else {
                                        ScaleAnimation animation = new ScaleAnimation(1f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f);
                                        animation.setDuration(220);
                                        animation.setFillAfter(false);
                                        liFormAcordionDis.startAnimation(animation);
                                        liFormAcordionDis.setVisibility(View.VISIBLE);
                                        btnFormAcordionDis.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
                                    }

                                }
                            });

                            for (int h = 0; h < ListaFotosAnexasINV.size(); h++) {
                                String clase1 = ListaFotosAnexasINV.get(h)[1];
                                String titulo1 = ListaFotosAnexasINV.get(h)[0];
                                String tag1 = ListaFotosAnexasINV.get(h)[2];

                                if (clase1.equals("edittext")){

                                    LinearLayout liHori2 = new LinearLayout(mcont);
                                    liHori2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    liHori2.setOrientation(LinearLayout.VERTICAL);
                                    liHori2.setPadding(20, 0, 0, 20);

                                    String valor = EditTextsAux.getString(tag1+f);

                                    TextView tvOpte = new TextView(mcont);
                                    tvOpte.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpte.setText(titulo1+": ");
                                    tvOpte.setTextAppearance(R.style.TituloItemEncabezado);
                                    tvOpte.setPadding(0, 20, 0, 0);
                                    liHori2.addView(tvOpte);

                                    TextView tvOpts = new TextView(mcont);
                                    tvOpts.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                    tvOpts.setText(valor);
                                    tvOpts.setTextAppearance(R.style.TituloItem);
                                    tvOpts.setPadding(0, 20, 0, 0);
                                    liHori2.addView(tvOpts);
                                    liFormAcordionDis.addView(liHori2);
                                }
                            }
                        }

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
        else{
            TextView tvTitulo = new TextView(mcont);
            tvTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvTitulo.setText("No hay estaciones guardadas");
            tvTitulo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            tvTitulo.setTextAppearance(R.style.TituloFormato);
            tvTitulo.setPadding(0, 70, 0, 70);
            contenedorEstaciones.addView(tvTitulo);
        }


        return root;
    }

    private void CargarForms() {
        ListaRocas.add(new String[]{"noformato", "Número Formato", "edittext"});
        ListaRocas.add(new String[]{"municipios", "Municipio", "spinner"});
        ListaRocas.add(new String[]{"vereda", "Vereda", "edittext"});
        ListaRocas.add(new String[]{"noestacion", "Número de la Estación", "edittext"});
        ListaRocas.add(new String[]{"claseaflor", "Clase Afloramiento", "spinner"});
        ListaRocas.add(new String[]{"secuenciaestratiopt", "Secuencia Estratigráfica", "secuenciaestrati"});
        ListaRocas.add(new String[]{"titulito", "CARACTERIZACIÓN DE LA UGS / UGI", "titulito"});
        ListaRocas.add(new String[]{"perfilmeteorizacion", "Perfil de meteorización (Dearman 1974)", "edittext"});
        ListaRocas.add(new String[]{"litologiasasociadasopt", "N° litologías asociadas a la UGS /UGI", "litologias"});
        ListaRocas.add(new String[]{"nombreugs", "Nombre de la UGS / UGI", "edittext"});
        ListaRocas.add(new String[]{"gsi", "GSI", "spinner"});
        ListaRocas.add(new String[]{"titulito", "CARACTERÍSTICAS DE LA UGS / UGI","titulito"});
        ListaRocas.add(new String[]{"fabrica", "Fábrica", "radiobtn"});
        ListaRocas.add(new String[]{"humedad", "Humedad Natural", "radiobtn"});
        ListaRocas.add(new String[]{"tamañograno", "Tamaño del Grano", "radiobtn"});
        ListaRocas.add(new String[]{"gradometeo", "Grado de Meteorización", "radiobtn"});
        ListaRocas.add(new String[]{"resistenciacomp", "Resistencia a la Compresión Simple (Mpa)", "radiobtn"});
        ListaRocas.add(new String[]{"color1", "Color Litología 1", "edittext"});
        ListaRocas.add(new String[]{"color2", "Color Litología 2", "edittext"});
        ListaRocas.add(new String[]{"composicionmineral1", "Composición Mineralógica (Macro) Litología 1", "edittext"});
        ListaRocas.add(new String[]{"composicionmineral2", "Composición Mineralógica (Macro) Litología 2", "edittext"});


        ListaRocasDiscont.add(new String[]{"TipoDiscont", "Tipo",  "spinner"});
        ListaRocasDiscont.add(new String[]{"DirBuzamiento", "Dir. Buzamiento (Az. Bz.)",  "edittext"});
        ListaRocasDiscont.add(new String[]{"Buzamiento", "Buzamiento (Bz.)",  "edittext"});
        ListaRocasDiscont.add(new String[]{"PersistenciaDiscont", "Ancho de Abertura",  "spinner"});
        ListaRocasDiscont.add(new String[]{"TipoRellenoDiscont", "Tipo de Relleno",  "spinner"});
        ListaRocasDiscont.add(new String[]{"RugosidadSuperDiscont", "Rugosidad de la Superficie",  "spinner"});
        ListaRocasDiscont.add(new String[]{"FormaSuperDiscont", "Forma de la Superficie",  "spinner"});
        ListaRocasDiscont.add(new String[]{"HumedadDiscont", "Humedad en Diaclasas",  "spinner"});
        ListaRocasDiscont.add(new String[]{"EspaciamientoDiscont", "Espaciamiento",  "spinner"});
        ListaRocasDiscont.add(new String[]{"MeteorizacionDiscont", "Meteorizacion",  "spinner"});
        ListaRocasDiscont.add(new String[]{"RakePitch", "Rake/Pitch",  "edittext"});
        ListaRocasDiscont.add(new String[]{"DirRakePitch", "Dir. del Rake/Pitch",  "edittext"});
        ListaRocasDiscont.add(new String[]{"titulito", "Orientación talud/ladera",  "titulito"});
        ListaRocasDiscont.add(new String[]{"AzBzBz1", "Az Bz/Bz",  "edittext"});
        ListaRocasDiscont.add(new String[]{"AzBzBz2", "Az Bz/Bz",  "edittext"});
        ListaRocasDiscont.add(new String[]{"AlturaDiscont", "Altura",  "edittext"});
        ListaRocasDiscont.add(new String[]{"ObservacionesDiscont", "Observaciones",  "edittext"});


        ListaFotosAnexas.add(new String[]{"NombreFotosAnexas", "Nombre de la Foto",  "edittext"});
        ListaFotosAnexas.add(new String[]{"DescriFotosAnexas", "Descripción de la Foto",  "edittext"});


        ListaSuelos.add(new String[]{"noformato", "Número Formato",  "edittext"});
        ListaSuelos.add(new String[]{"municipios", "Municipio",  "spinner"});
        ListaSuelos.add(new String[]{"vereda", "Vereda",  "edittext"});
        ListaSuelos.add(new String[]{"noestacion", "Número de la Estación",  "edittext"});
        ListaSuelos.add(new String[]{"claseaflor", "Clase Afloramiento",  "spinner"});
        ListaSuelos.add(new String[]{"secuenciaestratiopt", "Secuencia Estratigráfica",  "secuenciaestrati"});
        ListaSuelos.add(new String[]{"titulito", "CARACTERIZACIÓN DE LA UGS / UGI", "titulito"});
        ListaSuelos.add(new String[]{"nombreugs", "Nombre-Código de la UGS / UGI",  "edittext"});
        ListaSuelos.add(new String[]{"litologiasasociadasopt", "N° litologías asociadas a la UGS /UGI",  "litologias"});
        ListaSuelos.add(new String[]{"titulito", "CARACTERÍSTICAS DE LA UGS / UGI", "titulito"});
        ListaSuelos.add(new String[]{"estructurasoporte", "Estructura Soporte",  "radiobtn"});
        ListaSuelos.add(new String[]{"porcentajeclastos1", "Porcentaje Clastos 1",  "edittext"});
        ListaSuelos.add(new String[]{"porcentajeclastos2", "Porcentaje Clastos 2",  "edittext"});
        ListaSuelos.add(new String[]{"porcentajematriz1", "Porcentajes Matriz 1",  "edittext"});
        ListaSuelos.add(new String[]{"porcentajematriz2", "Porcentajes Matriz 2",  "edittext"});
        ListaSuelos.add(new String[]{"condicionhumedad", "Condicion de Humedad",  "radiobtn"});
        ListaSuelos.add(new String[]{"estructurasrelictas", "Estructuras Relictas",  "radiobtn"});
        ListaSuelos.add(new String[]{"color1", "Color Litología 1",  "edittext"});
        ListaSuelos.add(new String[]{"color2", "Color Litología 2",  "edittext"});
        ListaSuelos.add(new String[]{"titulito", "CARACTERÍSTICAS DE LOS CLASTOS", "titulito"});
        ListaSuelos.add(new String[]{"granulometria", "Granulometria de los Clastos",  "multitext"});
        ListaSuelos.add(new String[]{"forma", "Forma de los Clastos",  "radiocheck"});
        ListaSuelos.add(new String[]{"redondez", "Redondez de los Clastos",  "radiocheck"});
        ListaSuelos.add(new String[]{"orientacion", "Orientacion de los Clastos",  "radiobtn"});
        ListaSuelos.add(new String[]{"dirimbricacion1", "Dirección Imbricación 1",  "edittext"});
        ListaSuelos.add(new String[]{"dirimbricacion2", "Dirección Imbricación 2",  "edittext"});
        ListaSuelos.add(new String[]{"meteorizacionclastos", "Meteorizacion de los Clastos",  "radiobtn"});
        ListaSuelos.add(new String[]{"titulito", "CARACTERÍSTICAS DE LA MATRIZ", "titulito"});
        ListaSuelos.add(new String[]{"granulometriamatriz", "Granulometría de la Matriz",  "multitext"});
        ListaSuelos.add(new String[]{"gradacion", "Gradacion de la Matriz",  "radiobtn"});
        ListaSuelos.add(new String[]{"seleccion", "Seleccion de la Matriz",  "radiobtn"});
        ListaSuelos.add(new String[]{"plasticidad", "Plasticidad de la Matriz",  "radiobtn"});
        ListaSuelos.add(new String[]{"titulito", "SUELOS FINOS", "titulito"});
        ListaSuelos.add(new String[]{"resiscorte", "RESISTENCIA AL CORTE NO DRENADO kN/m2 (CONSISTENCIA)",  "radiobtn"});
        ListaSuelos.add(new String[]{"titulito", "SUELOS GRUESOS", "titulito"});
//        ListaSuelos.add(new String[]{"formasuelosgruesos", "Forma de la Matriz",  "radiocheck"});
//        ListaSuelos.add(new String[]{"redondezsuelosgruesos", "Redondez de la Matriz",  "radiocheck"});
//        ListaSuelos.add(new String[]{"orientacionsuelosgruesos", "Orientación de la Matriz",  "radiobtn"});
//        ListaSuelos.add(new String[]{"dirimbricacionmatriz1", "Dirección Imbricación Matriz 1",  "edittext"});
//        ListaSuelos.add(new String[]{"dirimbricacionmatriz2", "Dirección Imbricación Matriz 2",  "edittext"});
        ListaSuelos.add(new String[]{"compacidadsuelosgruesos", "Compacidad de la Matriz",  "radiobtn"});
        ListaSuelos.add(new String[]{"observacionessuelos", "Observaciones",  "edittext"});
        ListaSuelos.add(new String[]{"descripcionsuelos", "Descripción Composición Partículas del Suelo",  "edittext"});


        ListaSGMF.add(new String[]{"Número Formato",  "edittext",  "noformato"});
        ListaSGMF.add(new String[]{"Municipio",  "spinner",  "municipios"});
        ListaSGMF.add(new String[]{"Vereda",  "edittext",  "vereda"});
        ListaSGMF.add(new String[]{"Número de la Estación",  "edittext",  "noestacion"});
        ListaSGMF.add(new String[]{"MORFOGÉNESIS",  "titulito",  "titulito"});
        ListaSGMF.add(new String[]{"Tipo de Ambiente (Marque varios si es necesario)","ambientes","ambiente"});
        ListaSGMF.add(new String[]{"UBICACIÓN GEOMORFOLÓGICA","ubicacionGeo","ubicacion"});
        ListaSGMF.add(new String[]{"CARACTERIZACIÓN DE LA (S) GEOFORMA (S)",  "titulito",  "titulito"});
        ListaSGMF.add(new String[]{"Nombre SGMF / EGMF","edittext","nombreSGMF"});
        ListaSGMF.add(new String[]{"ID - Código SGMF / EGMF","edittext","codigoSGMF"});
        ListaSGMF.add(new String[]{"Observaciones","edittext","observacionesSGMF"});

        ListaNewSGMF.add(new String[]{"MORFOLITOLOGÍA - MORFOLOGÍA - MORFOMETRÍA - COBERTURA",  "titulito",  "titulito"});
        ListaNewSGMF.add(new String[]{"ID-Código SGMF-EGMF","edittext","codigonuevaSGMF"});
        ListaNewSGMF.add(new String[]{"TIPO DE ROCA, TRO","spinner","tiporoca"});
        ListaNewSGMF.add(new String[]{"GRADO DE METEORIZACIÓN, GM","spinner","gradometeor"});
        ListaNewSGMF.add(new String[]{"GRADO DE FRACTURAMIENTO, GF","spinner","gradofractura"});
        ListaNewSGMF.add(new String[]{"TIPO DE SUELO, TSU","spinner","tiposuelo"});
        ListaNewSGMF.add(new String[]{"TAMAÑO DE GRANO, TG","spinner","tamanograno"});
        ListaNewSGMF.add(new String[]{"TIPO DE RELIEVE, TR","spinner","tiporelieve"});
        ListaNewSGMF.add(new String[]{"INDICE DE RELIEVE, IR","spinner","indicerelieve"});
        ListaNewSGMF.add(new String[]{"INCLINACIÓN LADERA, IL","spinner","inclinacionladera"});
        ListaNewSGMF.add(new String[]{"LONGITUD LADERA, LL","spinner","longiladera"});
        ListaNewSGMF.add(new String[]{"FORMA LADERA, FL","spinner","formaladera"});
        ListaNewSGMF.add(new String[]{"FORMA DE LA CRESTA, FC","spinner","formacresta"});
        ListaNewSGMF.add(new String[]{"FORMAS DEL VALLE, FV","spinner","formavalle"});
        ListaNewSGMF.add(new String[]{"COBERTURA, C","spinner","cobertura"});
        ListaNewSGMF.add(new String[]{"USO DEL TERRENO, U","spinner","uso"});
        ListaNewSGMF.add(new String[]{"CARACTERÍSTICAS DE DRENAJE",  "titulito",  "titulito"});
        ListaNewSGMF.add(new String[]{"DENSIDAD, D","spinner","densidad"});
        ListaNewSGMF.add(new String[]{"FRECUENCIA, FR","spinner","frecuencia"});
        ListaNewSGMF.add(new String[]{"TEXTURA, TEX","spinner","textura"});
        ListaNewSGMF.add(new String[]{"PATRÓN, PT","spinner","patron"});
        ListaNewSGMF.add(new String[]{"MORFODINÁMICA",  "titulito",  "titulito"});
        ListaNewSGMF.add(new String[]{"TIPO DE EROSIÓN, TE","spinner","tipoerosion"});
        ListaNewSGMF.add(new String[]{"ESPACIAMIENTO ENTRE CANALES, EC","spinner","espaciamiento"});
        ListaNewSGMF.add(new String[]{"INTENSIDAD DE LA EROSIÓN, IER","spinner","intensidaderosion"});
        ListaNewSGMF.add(new String[]{"TIPOS DE MM, TMM","ambientes","tipodemm"});
        ListaNewSGMF.add(new String[]{"TIPO DE MATERIAL ASOCIADO, TMA","ambientes","tipomaterial"});
        ListaNewSGMF.add(new String[]{"ACTIVIDAD, ACT","spinner","actividad"});



        ListaCATALOGO.add(new String[]{"ID PARTE del MM","edittext","ID_PARTE"});
        ListaCATALOGO.add(new String[]{"IMPORTANCIA","spinner","IMPORTANC"});
        ListaCATALOGO.add(new String[]{"ENCUESTADOR","edittext","ENCUESTAD"});
        ListaCATALOGO.add(new String[]{"FECHA EVENTO","edittext","FECHA_MOV"});
        ListaCATALOGO.add(new String[]{"FUENTE FECHA EVENTO","spinner","FECHA_FUENTE"});
        ListaCATALOGO.add(new String[]{"CONFIABILIDAD FECHA EVENTO","spinner","ConfiFechaMM"});
        ListaCATALOGO.add(new String[]{"FECHA REPORTE","edittext","FECHA_REP"});
        ListaCATALOGO.add(new String[]{"SIMMA","edittext","COD_SIMMA"});
        ListaCATALOGO.add(new String[]{"Municipio",  "spinner",  "NOM_MUN"});
        ListaCATALOGO.add(new String[]{"Vereda",  "edittext",  "VEREDA"});
        ListaCATALOGO.add(new String[]{"SITIO","edittext","SITIO"});
        ListaCATALOGO.add(new String[]{"REFERENCIA GEOGRÁFICA","edittext","REF_GEOGRF"});
        ListaCATALOGO.add(new String[]{"CLASIFICACIÓN DEL MOVIMIENTO",  "titulito",  "titulito"});
        ListaCATALOGO.add(new String[]{"TIPO MOVIMIENTO",  "radiobtn",  "TIPO_MOV"});
        ListaCATALOGO.add(new String[]{"SUBTIPO PRIMER MOVIMIENTO",  "spinner",  "SUBTIPO_1"});
        ListaCATALOGO.add(new String[]{"SUBTIPO SEGUNDO MOVIMIENTO",  "spinner",  "SUBTIPO_2"});
        ListaCATALOGO.add(new String[]{"POBLACION AFECTADA",  "titulito",  "titulito"});
        ListaCATALOGO.add(new String[]{"Heridos","edittext","HERIDOS"});
        ListaCATALOGO.add(new String[]{"Vidas","edittext","VIDAS"});
        ListaCATALOGO.add(new String[]{"Desaparecidos","edittext","DESAPARECIDOS"});
        ListaCATALOGO.add(new String[]{"Personas","edittext","PERSONAS"});
        ListaCATALOGO.add(new String[]{"Familias","edittext","FAMILIAS"});
        ListaCATALOGO.add(new String[]{"IMÁGENES SATELITALES","edittext","sensoresremotos"});
        ListaCATALOGO.add(new String[]{"FOTOGRAFÍAS AÉREAS","edittext","FTE_INFSEC"});
        ListaCATALOGO.add(new String[]{"NOTAS (Ej: Causas y observaciones generales):","edittext","notas"});
        ListaCATALOGO.add(new String[]{"DAÑOS A INFRASTRUCTURA, ACTIVIDADES ECONÓMICAS, DAÑOS AMBIENTALES:","titulito","titulito"});


        ListaDANOS.add(new String[]{"CLASE DE DAÑO", "spinner", "clasedaño"});
        ListaDANOS.add(new String[]{"TIPO", "edittext", "tipodaño"});
        ListaDANOS.add(new String[]{"CANTIDAD", "edittext", "cantidaddaño"});
        ListaDANOS.add(new String[]{"UNIDAD", "edittext", "unidaddaño"});
        ListaDANOS.add(new String[]{"TIPO DAÑO", "spinner", "tiposdaño"});
        ListaDANOS.add(new String[]{"VALOR (US$)", "edittext", "valordaño"});

        ListaINVENTARIO.add(new String[]{"ID PARTE del MM","edittext","ID_PARTE"});
        ListaINVENTARIO.add(new String[]{"IMPORTANCIA","spinner","IMPORTANC"});
        ListaINVENTARIO.add(new String[]{"ENCUESTADOR","edittext","ENCUESTAD"});
        ListaINVENTARIO.add(new String[]{"FECHA EVENTO","edittext","FECHA_MOV"});
        ListaINVENTARIO.add(new String[]{"FUENTE FECHA EVENTO","spinner","FECHA_FUENTE"});
        ListaINVENTARIO.add(new String[]{"CONFIABILIDAD FECHA EVENTO","spinner","ConfiFechaMM"});
        ListaINVENTARIO.add(new String[]{"FECHA REPORTE","edittext","FECHA_REP"});
//      ListaINVENTARIO.add(new String[]{o("INSTITUCIÓN","edittext","INSTITUC"});
        ListaINVENTARIO.add(new String[]{"SIMMA","edittext","COD_SIMMA"});
        ListaINVENTARIO.add(new String[]{"Municipio",  "spinner",  "NOM_MUN"});
        ListaINVENTARIO.add(new String[]{"Vereda",  "edittext",  "VEREDA"});
        ListaINVENTARIO.add(new String[]{"Sitio","edittext","SITIO"});
        ListaINVENTARIO.add(new String[]{"REFERENCIA GEOGRÁFICA","edittext","REF_GEOGRF"});
        ListaINVENTARIO.add(new String[]{"DOCUMENTACIÓN",  "titulo",  ""});
        ListaINVENTARIO.add(new String[]{"PLANCHAS","edittext","planchas"});
        ListaINVENTARIO.add(new String[]{"SENSORES REMOTOS","edittext","sensoresremotos"});
        ListaINVENTARIO.add(new String[]{"FOTOGRAFÍAS AÉREAS","edittext","FTE_INFSEC"});
        ListaINVENTARIO.add(new String[]{"ACTIVIDAD DEL MOVIMIENTO",  "titulo",  ""});
        ListaINVENTARIO.add(new String[]{"EDAD","spinner","edadmm"});
        ListaINVENTARIO.add(new String[]{"ESTADO","spinner","ESTADO_ACT"});
        ListaINVENTARIO.add(new String[]{"ESTILO","spinner","ESTILO"});
        ListaINVENTARIO.add(new String[]{"DISTRIBUCIÓN","spinner","DISTRIBUC"});
        ListaINVENTARIO.add(new String[]{"LITOLOGIA Y ESTRUCTURA",  "titulo",  ""});
        ListaINVENTARIO.add(new String[]{"DESCRIPCIÓN (Incuir minimo origen de la roca,(I,M ó S) Edad, Fm, Litologia y estratigrafia, suelos)","edittext","LITOLOGIA"});
        ListaINVENTARIO.add(new String[]{"ESTRUCTURA","estructuras","estructura"});
        ListaINVENTARIO.add(new String[]{"CLASIFICACIÓN DEL MOVIMIENTO",  "titulo",  ""});
        ListaINVENTARIO.add(new String[]{"TIPO MOVIMIENTO",  "radiobtn",  "TIPO_MOV"});
        ListaINVENTARIO.add(new String[]{"SUBTIPO PRIMER MOVIMIENTO",  "spinner",  "SUBTIPO_1"});
        ListaINVENTARIO.add(new String[]{"SUBTIPO SEGUNDO MOVIMIENTO",  "spinner",  "SUBTIPO_2"});
        ListaINVENTARIO.add(new String[]{"TIPO MATERIAL",  "radiocheckMM",  "tipomaterial"});
        ListaINVENTARIO.add(new String[]{"HUMEDAD",  "radiobtn",  "humedad"});
        ListaINVENTARIO.add(new String[]{"PLASTICIDAD",  "radiobtn",  "plasticidad"});
        ListaINVENTARIO.add(new String[]{"ORIGEN SUELO",  "origensuelo",  "origensuelo"});
        ListaINVENTARIO.add(new String[]{"TIPO DE DEPOSITO (Origen suelo sedimentario)",  "radiocheck",  "tipodeposito"});
        ListaINVENTARIO.add(new String[]{"VELOCIDAD",  "spinner",  "velocidad"});
        ListaINVENTARIO.add(new String[]{"VELOCIDAD MÁXIMA","edittext","velocidadmax"});
        ListaINVENTARIO.add(new String[]{"VELOCIDAD MÍNIMA","edittext","velocidadmin"});
        ListaINVENTARIO.add(new String[]{"SISTEMA DE CLASIFICACIÓN",  "spinner",  "sisclasificacion"});
        ListaINVENTARIO.add(new String[]{"MORFOMETRÍA",  "titulo",  ""});
        ListaINVENTARIO.add(new String[]{"GENERAL",  "multitext",  "morfogeneral"});
        ListaINVENTARIO.add(new String[]{"DIMENSIONES DEL TERRENO",  "multitext",  "morfodimensiones"});
        ListaINVENTARIO.add(new String[]{"DEFORMACIÓN TERRENO",  "titulo",  ""});
        ListaINVENTARIO.add(new String[]{"MODO",  "spinner",  "morfomodo"});
        ListaINVENTARIO.add(new String[]{"SEVERIDAD",  "spinner",  "morfoseveridad"});
        ListaINVENTARIO.add(new String[]{"GEOFORMA",  "edittext",  "AN_GMF"});
        ListaINVENTARIO.add(new String[]{"CAUSAS DEL MOVIMIENTO",  "titulo",  ""});
        ListaINVENTARIO.add(new String[]{"INHERENTES",  "radiocheck",  "causasinherentes"});
        ListaINVENTARIO.add(new String[]{"CONTRIBUYENTES - DETONANTES",  "radiocheckMM",  "causascontrideto"});
        ListaINVENTARIO.add(new String[]{"Sismo",  "multitext",  "sismoMM"});
        ListaINVENTARIO.add(new String[]{"Lluvias",  "multitext",  "lluviasMM"});
        ListaINVENTARIO.add(new String[]{"TIPO DE EROSIÓN",  "titulo",  ""});
        ListaINVENTARIO.add(new String[]{"SUPERFICIAL",  "radiocheck",  "erosionsuperficial"});
        ListaINVENTARIO.add(new String[]{"SUBSUPERFICIAL",  "radiocheck",  "erosionsubsuperficial"});
        ListaINVENTARIO.add(new String[]{"EDAD",  "spinner",  "erosionedad"});
        ListaINVENTARIO.add(new String[]{"ESTADO",  "spinner",  "erosionestado"});
        ListaINVENTARIO.add(new String[]{"FLUVIAL",  "spinner",  "erosionfluvial"});
        ListaINVENTARIO.add(new String[]{"EOLICA",  "spinner",  "erosioneolica"});
        ListaINVENTARIO.add(new String[]{"COBERTURA Y USO DEL SUELO",  "titulo",  ""});
        ListaINVENTARIO.add(new String[]{"COBERTURA DEL SUELO",  "multitext",  "cobertura"});
        ListaINVENTARIO.add(new String[]{"USO DEL SUELO",  "multitext",  "usosuelo"});
        ListaINVENTARIO.add(new String[]{"REFERENCIAS",  "titulo",  ""});
        ListaINVENTARIO.add(new String[]{"AUTOR","edittext","referenciasautor"});
        ListaINVENTARIO.add(new String[]{"AÑO","edittext","referenciasaño"});
        ListaINVENTARIO.add(new String[]{"TITULO","edittext","referenciastitulo"});
        ListaINVENTARIO.add(new String[]{"EDITOR","edittext","referenciaseditor"});
        ListaINVENTARIO.add(new String[]{"CIUDAD","edittext","referenciasciudad"});
        ListaINVENTARIO.add(new String[]{"PAGINAS","edittext","referenciaspaginas"});
        ListaINVENTARIO.add(new String[]{"EFECTOS SECUNDARIOS: REPRESAMIENTO",  "titulo",  ""});
        ListaINVENTARIO.add(new String[]{"TIPO (Costa & Schuster, 1988)",  "spinner",  "represamientotipo"});
        ListaINVENTARIO.add(new String[]{"MORFOMETRÍA DE LA PRESA",  "multitext",  "represamientomorfometria"});
        ListaINVENTARIO.add(new String[]{"CONDICIONES DE LA PRESA",  "radiocheck",  "represamientocondiciones"});
        ListaINVENTARIO.add(new String[]{"EFECTOS",  "radiocheck",  "represamientoefectos"});
        ListaINVENTARIO.add(new String[]{"POBLACION AFECTADA",  "titulo",  ""});
        ListaINVENTARIO.add(new String[]{"HERIDOS","edittext","heridos"});
        ListaINVENTARIO.add(new String[]{"VIDAS","edittext","vidas"});
        ListaINVENTARIO.add(new String[]{"DESAPARECIDOS","edittext","desaparecidos"});
        ListaINVENTARIO.add(new String[]{"PERSONAS","edittext","personas"});
        ListaINVENTARIO.add(new String[]{"FAMILIAS","edittext","familias"});
        ListaINVENTARIO.add(new String[]{"NOTAS","edittext","notas"});
        ListaINVENTARIO.add(new String[]{"APRECIACIÓN DEL RIESGO","edittext","apreciacionriesgo"});
        ListaINVENTARIO.add(new String[]{"DAÑOS A INFRASTRUCTURA, ACTIVIDADES ECONÓMICAS, DAÑOS AMBIENTALES:","titulo",""});

        ListaFotosAnexasINV.add(new String[]{"Fecha",  "edittext",  "fechaFotosAnexas"});
        ListaFotosAnexasINV.add(new String[]{"Nombre de la Foto",  "edittext",  "nombreFotosAnexasINV"});
        ListaFotosAnexasINV.add(new String[]{"Autor/Derechos",  "edittext",  "autorFotosAnexas"});
        ListaFotosAnexasINV.add(new String[]{"Observaciones",  "edittext",  "obsFotosAnexas"});

    }

    private boolean ArchivoExiste(String[] file, String name) {
        for (String s : file)
            if (name.equals(s))
                return true;
        return false;
    }
}