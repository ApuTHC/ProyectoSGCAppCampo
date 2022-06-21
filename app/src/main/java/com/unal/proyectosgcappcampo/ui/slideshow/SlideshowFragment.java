package com.unal.proyectosgcappcampo.ui.slideshow;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.Settings;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.unal.proyectosgcappcampo.R;
import com.unal.proyectosgcappcampo.databinding.FragmentSlideshowBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.unal.proyectosgcappcampo.ui.gallery.Feature;

import org.jetbrains.annotations.Contract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.xml.transform.Result;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    private FirebaseAuth firebaseAuth;
    StorageReference storageRef;
    String userName;
    boolean login = false;

    private ActivityResultLauncher<Intent> intentLaucher;

    Button btnFormLoad;
    Button btnFormSync;
    Button btnAddForm;
    Button btnFoto;
    LinearLayout liFotosGeneral;
    EditText etEstacion;
    EditText etTipoEstacion;
    EditText etEste;
    EditText etNorte;
    EditText etAltitud;
    EditText etFotos;
    EditText etObservaciones;
    TextView tvEstadoGPS;

    JSONObject attrForm;
    JSONArray formComplete;

    JSONArray BDComplete;
    JSONObject feature;
    JSONObject layergeojson;
    JSONObject properties;
    String idparte;


    String[] files;

    InputStreamReader archivo;
    Boolean auxTextExist = false;
    String listaFormText = "";

    InputStreamReader archivoBD;
    Boolean auxBDMM = false;
    String listaBDText = "";


    List<ElementoFormato> listaElementosUGSR = new ArrayList<ElementoFormato>();
    List<ElementoFormato> listaElementosUGSRDiscont = new ArrayList<ElementoFormato>();
    List<ElementoFormato> listaElementosUGSFotosAnexas = new ArrayList<ElementoFormato>();
    List<ElementoFormato> listaElementosUGSS = new ArrayList<ElementoFormato>();
    List<ElementoFormato> listaElementosSGMF = new ArrayList<ElementoFormato>();
    List<ElementoFormato> listaElementosNuevoSGMF = new ArrayList<ElementoFormato>();
    List<ElementoFormato> listaElementosCAT = new ArrayList<ElementoFormato>();
    List<ElementoFormato> listaElementosCATDANOS = new ArrayList<ElementoFormato>();
    List<ElementoFormato> listaElementosINV = new ArrayList<ElementoFormato>();

    ElementoFormato ElementoSueloResidualUGSR = new ElementoFormato( "Horizonte",  "secuenciaestrati",  "secuenciaestratisuelor", R.array.SecuenciaEstratiRocasSueloRes);
    ElementoFormato ElementoSueloResidualUGSS = new ElementoFormato( "Horizonte",  "secuenciaestrati",  "secuenciaestratisuelor", R.array.SecuenciaEstratiSuelosSueloRes);


    List<Uri> listFotosGeneral = new ArrayList<Uri>();


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


    int sgmf = 0;
    List<Integer> listContSGMF = new ArrayList<Integer>();
    List<List<LinearLayout>> ListaSGMF = new ArrayList<List<LinearLayout>>();
    List<LinearLayout> listSGMF = new ArrayList<LinearLayout>();
    LinearLayout liFormSGMF;

    //Daños

    int daños = 0;
    List<Integer> listContDAÑOS = new ArrayList<Integer>();
    List<List<LinearLayout>> ListaDAÑOS = new ArrayList<List<LinearLayout>>();
    List<LinearLayout> listDAÑOS = new ArrayList<LinearLayout>();
    LinearLayout liFormDAÑOS;


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

        files = mcont.fileList();

        listaElementosUGSR = new ArrayList<ElementoFormato>();
        listaElementosUGSRDiscont = new ArrayList<ElementoFormato>();
        listaElementosUGSFotosAnexas = new ArrayList<ElementoFormato>();
        listaElementosUGSS = new ArrayList<ElementoFormato>();
        listaElementosSGMF = new ArrayList<ElementoFormato>();
        listaElementosNuevoSGMF = new ArrayList<ElementoFormato>();
        listaElementosCAT = new ArrayList<ElementoFormato>();
        listaElementosCATDANOS = new ArrayList<ElementoFormato>();
        listFormularios = new ArrayList<String>();
        listLiForm = new ArrayList<LinearLayout>();

        GenerarListas();

        btnFoto = binding.btnFoto;
        liFotosGeneral = binding.liFotos;
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
        tvEstadoGPS = binding.tvEstadoGPS;

        ActivityResult();


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
                    Toast.makeText(mcont, "Se añadió formulario: " + eleccion, Toast.LENGTH_SHORT).show();
                    if (eleccion.equals("Catálogo MM") || eleccion.equals("Inventario MM")) {
                        LinearLayout liMM = new LinearLayout(mcont);
                        liMM.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liMM.setOrientation(LinearLayout.HORIZONTAL);

                        EditText etMM = new EditText(mcont);
                        etMM.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etMM.setHint("ID_PARTE del MM");
                        etMM.setEms(10);
                        etMM.setTag("editMM");
                        liFormularios.addView(etMM);

                        Button bBuscarMM = new Button(mcont);
                        bBuscarMM.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        bBuscarMM.setText("Buscar MM");
                        bBuscarMM.setTag("buscar");
                        bBuscarMM.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (ArchivoExiste(files, "listaFeatures.txt")){
                                    Toast.makeText(mcont, "Por favor espere mientras se busca el MM: "+etMM.getText().toString(), Toast.LENGTH_SHORT).show();
                                    try {
                                        if(!auxBDMM){
                                            archivoBD = new InputStreamReader(mcont.openFileInput("listaFeatures.txt"));
                                            BufferedReader br = new BufferedReader(archivoBD);
                                            String linea = br.readLine();
                                            listaBDText = "";

                                            while (linea != null){
                                                listaBDText = listaBDText + linea + "\n";
                                                linea = br.readLine();
                                            }
                                            BDComplete = new JSONArray(listaBDText);
                                            Log.d("alprincipio", "GuardarForm: "+BDComplete);

                                            br.close();
                                            archivoBD.close();

                                            auxBDMM = true;
                                        }
                                        boolean featExist = false;
                                        for (int i = 1; i < BDComplete.length(); i++) {
                                            feature = BDComplete.getJSONObject(i);
                                            layergeojson = feature.getJSONObject("layergeojson");
                                            properties = layergeojson.getJSONObject("properties");
                                            idparte = properties.getString("ID_PARTE");

                                            if (etMM.getText().toString().equals(idparte)){
                                                featExist = true;
                                                break;
                                            }
                                        }
                                        if (featExist){
                                            Toast.makeText(mcont, "Cargando los datos del MM: "+etMM.getText().toString(), Toast.LENGTH_LONG).show();
                                            AddFormulario(eleccion, true);
                                        }
                                        else{
                                            Toast.makeText(mcont, "No se encontró en la base de datos el MM: "+etMM.getText().toString(), Toast.LENGTH_LONG).show();
                                        }


                                    } catch (IOException | JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                                else {
                                    Toast.makeText(mcont, "No se encuentra ninguna base de datos de MM en el dispositivo", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                        liMM.addView(bBuscarMM);

                        Button bAñadirMM = new Button(mcont);
                        bAñadirMM.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        bAñadirMM.setText("Añadir Nuevo MM");
                        bAñadirMM.setTag("añadir");
                        bAñadirMM.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                idparte = "Nuevo MM";
                                AddFormulario(eleccion, false);
                            }
                        });
                        liMM.addView(bAñadirMM);

                        liFormularios.addView(liMM);


                    }
                    else{
                        AddFormulario(eleccion, false);
                    }
                }
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
        storageRef =  FirebaseStorage.getInstance().getReference();

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

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CargarImagen();
            }
        });

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

        if (ActivityCompat.checkSelfPermission(mcont, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mcont, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
        } else {
            locationStart();
        }

    }

    private void ActivityResult() {
        intentLaucher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
           if (result.getResultCode() == Activity.RESULT_OK){
               Uri imageUri;
               if (result.getData().getClipData() != null){
                   //Seleccionar multiples Imagenes
                   int count = result.getData().getClipData().getItemCount();
                   for (int i = 0; i<count; i++){
                       imageUri = result.getData().getClipData().getItemAt(i).getUri();
                       listFotosGeneral.add(imageUri);
                   }
               }
               else{
                   //Seleccionar una imagen
                   imageUri = result.getData().getData();
                   listFotosGeneral.add(imageUri);
               }

               liFotosGeneral.removeAllViews();
               String name = "";
               for (int i = 0; i < listFotosGeneral.size(); i++) {
                   ImageView imagen = new ImageView(mcont);
                   imagen.setLayoutParams(new ActionBar.LayoutParams(400, 400));
                   imagen.setImageURI(listFotosGeneral.get(i));
                   String path = listFotosGeneral.get(i).getPath();

                   name += path.substring(path.lastIndexOf('/') + 1) + ", ";
                   liFotosGeneral.addView(imagen);
               }
               etFotos.setText(name);
           }
        });
    }

    private void CargarImagen() {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        intentLaucher.launch(Intent.createChooser(intent, "Seleccione la aplicación"));

    }


    private void locationStart() {
        LocationManager mlocManager = (LocationManager) mcont.getSystemService(Context.LOCATION_SERVICE);
        Localizacion Local = new Localizacion();
        Local.setMainActivity(this);
        final boolean gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!gpsEnabled) {
            Intent settingsIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(settingsIntent);
        }
        if (ActivityCompat.checkSelfPermission(mcont, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mcont, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            return;
        }
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, (LocationListener) Local);
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, (LocationListener) Local);
        //etNorte.setText("Localización agregada");
        //etEste.setText("");
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart();

            }
        }
    }


    private void AddFormulario(String formType, boolean auxMM){

        int mtop = 70;
        listFormularios.add(formType);
        idLinear = listFormularios.size() - 1;


        listFotosAnexas = new ArrayList<LinearLayout>();
        listDiscontinuidades = new ArrayList<LinearLayout>();
        listSGMF = new ArrayList<LinearLayout>();
        listDAÑOS = new ArrayList<LinearLayout>();
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
        ListaSGMF.add(listSGMF);
        ListaDAÑOS.add(listDAÑOS);
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
        sgmf = 0;
        daños = 0;

        listContFotosAnexas.add(fotosAnexas);
        listContDiscontinuidades.add(discontinuidades);
        listContSGMF.add(sgmf);
        listContDAÑOS.add(daños);

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

        if (formType.equals("SGMF")) {
            Button bAcordion = new Button(mcont);
            bAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
            bAcordion.setText("Formato SGMF");
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
                }
            });

            //------------> Titulo del Formato

            TextView tvTitulo = new TextView(mcont);
            tvTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvTitulo.setText("Formato SGMF");
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

            for (int i = 0; i < listaElementosSGMF.size(); i++) {
                ElementoFormato elementoActual = listaElementosSGMF.get(i);
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

                    if (nombreElemento.equals("COBERTURA, C") || nombreElemento.equals("USO DEL TERRENO, U") || nombreElemento.equals("PATRÓN, PT")){
                        TextView tvGenerico1 = new TextView(mcont);
                        tvGenerico1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvGenerico1.setText("Otro:");
                        tvGenerico1.setTextAppearance(R.style.TituloItem);
                        tvGenerico1.setPadding(0, mtop, 0, 0);
                        liForm.addView(tvGenerico1);

                        EditText etGenerico = new EditText(mcont);
                        etGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etGenerico.setHint(hintElemento);
                        etGenerico.setEms(10);
                        etGenerico.setTag(tagElemento+"otro");
                        ListaEditText.get(idLinear).add(etGenerico);
                        liForm.addView(etGenerico);
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
                if (claseElemento.equals("ambientes")){
                    Resources res = getResources();
                    String[] opciones = res.getStringArray(idStringArrayElemento);

                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloItem);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                    for (int j = 0; j < opciones.length ; j++) {

                        CheckBox checkbox = new CheckBox(mcont);
                        checkbox.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        String aux = opciones[j];
                        checkbox.setText(aux);
                        checkbox.setTag(tagElemento+j+"check");
                        ListaCheckBox.get(idLinear).add(checkbox);
                        liForm.addView(checkbox);

                        if (j == 0){
                            checkbox.setChecked(true);
                        }
                    }

                }
                if (claseElemento.equals("ubicacionGeo")){
                    Resources res = getResources();
                    String[] opciones = res.getStringArray(idStringArrayElemento);

                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloFormato);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                    for (int j = 0; j < opciones.length ; j++) {

                        LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                        liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                        TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                        tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(450, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvSecuenciaEstratiOpt.setText(opciones[j]);
                        tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                        liFormSecuenciaEstrati.addView(tvSecuenciaEstratiOpt);

                        EditText etSecuenciaEstratiOpt = new EditText(mcont);
                        etSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiOpt.setHint(opciones[j]);
                        etSecuenciaEstratiOpt.setEms(10);
                        etSecuenciaEstratiOpt.setTag(tagElemento+opciones[j]);
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt);
                        liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt);

                        liForm.addView(liFormSecuenciaEstrati);

                    }

                }


            }

            //------------> LEVANTAMIENTO DE SGMF

            TextView tvLevantamientoDisc = new TextView(mcont);
            tvLevantamientoDisc.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvLevantamientoDisc.setText("Caracterización de SGMF - EGMF");
            tvLevantamientoDisc.setTextAppearance(R.style.TituloFormato);
            tvLevantamientoDisc.setPadding(0, mtop, 0, 20);
            liForm.addView(tvLevantamientoDisc);

            LinearLayout liFormDiscontinuidades = new LinearLayout(mcont);
            liFormDiscontinuidades.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormDiscontinuidades.setOrientation(LinearLayout.VERTICAL);
            liForm.addView(liFormDiscontinuidades);

            Button bAnadirDiscont = new Button(mcont);
            bAnadirDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bAnadirDiscont.setText("Añadir SGMF-EGMF");
            bAnadirDiscont.setTag(idLinear);
            bAnadirDiscont.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.plus_circle, 0);
            bAnadirDiscont.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listContSGMF.set(Integer.parseInt(v.getTag().toString()), listContSGMF.get(Integer.parseInt(v.getTag().toString())) + 1);

                    Button bDiscont = new Button(mcont);
                    bDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    bDiscont.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                    bDiscont.setText("SGMF - EGMF "+ listContSGMF.get(Integer.parseInt(v.getTag().toString())));
                    bDiscont.setTag(Integer.parseInt(v.getTag().toString()));
                    liFormDiscontinuidades.addView(bDiscont);


                    LinearLayout liDiscontinuidades = new LinearLayout(mcont);
                    liDiscontinuidades.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liDiscontinuidades.setOrientation(LinearLayout.VERTICAL);
                    liDiscontinuidades.setBackgroundColor(0x22222200);
                    liDiscontinuidades.setVisibility(View.GONE);
                    liFormDiscontinuidades.addView(liDiscontinuidades);
                    ListaSGMF.get(Integer.parseInt(v.getTag().toString())).add(liDiscontinuidades);

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
                    String nuevo = "SGMF - EGMF "+ listContSGMF.get(Integer.parseInt(v.getTag().toString()));
                    tvNameDiscont.setText(nuevo);
                    tvNameDiscont.setTextAppearance(R.style.TituloFormato);
                    tvNameDiscont.setPadding(0, 100, 0, 50);
                    liDiscontinuidades.addView(tvNameDiscont);

                    for (int i = 0; i < listaElementosNuevoSGMF.size(); i++){
                        ElementoFormato elementoActual = listaElementosNuevoSGMF.get(i);
                        String nombreElemento = elementoActual.getNombreelemento();
                        String hintElemento = elementoActual.getNombreelemento();
                        String claseElemento = elementoActual.getClaseelemento();
                        String tagElemento = elementoActual.getTagelemento();
                        int idStringArrayElemento = elementoActual.getIdStringArray();
                        int aux = ListaSGMF.get(Integer.parseInt(v.getTag().toString())).size();

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

                            if (nombreElemento.equals("COBERTURA, C") || nombreElemento.equals("USO DEL TERRENO, U") || nombreElemento.equals("PATRÓN, PT")){
                                TextView tvGenerico1 = new TextView(mcont);
                                tvGenerico1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvGenerico1.setText("Otro:");
                                tvGenerico1.setTextAppearance(R.style.TituloItem);
                                tvGenerico1.setPadding(0, mtop, 0, 0);
                                liDiscontinuidades.addView(tvGenerico1);

                                EditText etGenerico = new EditText(mcont);
                                etGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                etGenerico.setHint(hintElemento);
                                etGenerico.setEms(10);
                                etGenerico.setTag(tagElemento+"otro"+aux);
                                ListaEditText.get(idLinear).add(etGenerico);
                                liDiscontinuidades.addView(etGenerico);
                            }
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

        if (formType.equals("Catálogo MM")) {
            Button bAcordion = new Button(mcont);
            bAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
            bAcordion.setText("Formato Cátalogo de Movimiento en Masa: "+idparte);
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
                }
            });

            //------------> Titulo del Formato

            TextView tvTitulo = new TextView(mcont);
            tvTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            tvTitulo.setText("Formato Cátalogo de Movimiento en Masa: "+idparte);
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

            for (int i = 0; i < listaElementosCAT.size(); i++) {
                ElementoFormato elementoActual = listaElementosCAT.get(i);
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
                if (claseElemento.equals("edittextMM")){
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
                    try {
                        etGenerico.setText(properties.getString(tagElemento));
                        Log.d("pruebis", "EditTextOPT: "+properties.getString(tagElemento));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
                if (claseElemento.equals("spinnerMM")){

                    Resources res = getResources();
                    String[] opciones = res.getStringArray(idStringArrayElemento);
                    int indexSpinner = 120;
                    String opc = "1";
                    try {
                        opc = properties.getString(tagElemento).toString();
                        Log.d("pruebis", "SpinnerOPT: "+opc);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (opc.equals("Rotacional")){
                        opc = "Deslizamiento rotacional";
                    }
                    if (opc.equals("Traslacional")){
                        opc = "Deslizamiento traslacional";
                    }
                    if (opc.equals("Flujo de Lodo")){
                        opc = "Flujo de lodo";
                    }
                    if (opc.equals("Flujo de tierra")){
                        opc = "Flujo de Tierra";
                    }
                    if (opc.split("").length == 0){
                        opc = "1";
                    }
                    if (opc.split(" ").length == 3){
                        if (opc.split(" ")[2].equals("Roca")) {
                            opc = "Caída de Roca";
                        }
                        if (opc.split(" ")[2].equals("Suelo")) {
                            opc = "Caída de Suelo";
                        }
                    }
                    for (int j = 0; j < opciones.length; j++) {
                        if (opciones[j].equals(opc)){
                            indexSpinner = j;
                        }
                    }
                    if (!tagElemento.equals("SUBTIPO_1") && !tagElemento.equals("SUBTIPO_2")){
                        if (indexSpinner == 120){
                            indexSpinner = Integer.parseInt(opc) - 1;
                        }
                    }
                    if (indexSpinner == 120){
                        indexSpinner = 0;
                    }

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
                    sGenerico.setSelection(indexSpinner);
                    ListaSpinner.get(idLinear).add(sGenerico);
                    liForm.addView(sGenerico);

                }
                if (claseElemento.equals("titulo")){
                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloFormato);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);
                }
                if (claseElemento.equals("radiobtnMM")){
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
                        //nuevoRadio.setText(opt);
                        nuevoRadio.setTag(opt);
                        nuevoRadio.setClickable(true);
                        nuevoRadio.setEnabled(true);
                        ListaRadioBtn.get(idLinear).add(nuevoRadio);
                        radioGroup2.addView(nuevoRadio);
                    }

                    Resources res2 = getResources();
                    String[] opciones = res2.getStringArray(idStringArrayElemento);
                    int indexSpinner2 = 0;
                    String opc = "0";
                    try {
                        opc = properties.getString(tagElemento+2).toString();
                        Log.d("pruebis", "SpinnerOPT: "+opc);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (opc.split("")[0].equals("C")) {
                        opc = "Caída";
                    }

                    for (int j = 0; j < opciones.length; j++) {
                        if (opciones[j].equals(opc)){
                            indexSpinner2 = j;
                        }
                    }


                    RadioButton primerRadio2 = (RadioButton) radioGroup2.getChildAt(indexSpinner2);
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

                    int indexSpinner1=0;
                    String opc1 = "1";
                    try {
                        opc1 = properties.getString(tagElemento+1);
                        indexSpinner1 = Integer.parseInt(opc1);
                        Log.d("pruebis", "RadioBtnOPT: "+opc1);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                    RadioButton primerRadio1 = (RadioButton) radioGroup1.getChildAt(indexSpinner1);
                    primerRadio1.setChecked(true);
                    liradiobtn.addView(radioGroup1);
                    ListaRadioGrp.get(idLinear).add(radioGroup1);

                    liForm.addView(liradiobtn);
                }
                if (claseElemento.equals("textview")){

                    Resources res = getResources();
                    String opciones = res.getString(idStringArrayElemento);

                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(opciones);
                    tvGenerico.setTextAppearance(R.style.TituloItem);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                }
            }

//            Levantamiento Daños

            LinearLayout liFormDiscontinuidades = new LinearLayout(mcont);
            liFormDiscontinuidades.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            liFormDiscontinuidades.setOrientation(LinearLayout.VERTICAL);
            liForm.addView(liFormDiscontinuidades);

            Button bAnadirDiscont = new Button(mcont);
            bAnadirDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bAnadirDiscont.setText("Añadir Daño");
            bAnadirDiscont.setTag(idLinear);
            bAnadirDiscont.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.plus_circle, 0);
            bAnadirDiscont.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    listContDAÑOS.set(Integer.parseInt(v.getTag().toString()), listContDAÑOS.get(Integer.parseInt(v.getTag().toString())) + 1);

                    Button bDiscont = new Button(mcont);
                    bDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    bDiscont.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                    bDiscont.setText("DAÑOS "+ listContDAÑOS.get(Integer.parseInt(v.getTag().toString())));
                    bDiscont.setTag(Integer.parseInt(v.getTag().toString()));
                    liFormDiscontinuidades.addView(bDiscont);


                    LinearLayout liDiscontinuidades = new LinearLayout(mcont);
                    liDiscontinuidades.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liDiscontinuidades.setOrientation(LinearLayout.VERTICAL);
                    liDiscontinuidades.setBackgroundColor(0x22222200);
                    liDiscontinuidades.setVisibility(View.GONE);
                    liFormDiscontinuidades.addView(liDiscontinuidades);
                    ListaDAÑOS.get(Integer.parseInt(v.getTag().toString())).add(liDiscontinuidades);

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
                    String nuevo = "DAÑOS "+ listContDAÑOS.get(Integer.parseInt(v.getTag().toString()));
                    tvNameDiscont.setText(nuevo);
                    tvNameDiscont.setTextAppearance(R.style.TituloFormato);
                    tvNameDiscont.setPadding(0, 100, 0, 50);
                    liDiscontinuidades.addView(tvNameDiscont);

                    for (int i = 0; i < listaElementosCATDANOS.size(); i++){
                        ElementoFormato elementoActual = listaElementosCATDANOS.get(i);
                        String nombreElemento = elementoActual.getNombreelemento();
                        String hintElemento = elementoActual.getNombreelemento();
                        String claseElemento = elementoActual.getClaseelemento();
                        String tagElemento = elementoActual.getTagelemento();
                        int idStringArrayElemento = elementoActual.getIdStringArray();
                        int aux = ListaDAÑOS.get(Integer.parseInt(v.getTag().toString())).size();

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

                            if (nombreElemento.equals("COBERTURA, C") || nombreElemento.equals("USO DEL TERRENO, U") || nombreElemento.equals("PATRÓN, PT")){
                                TextView tvGenerico1 = new TextView(mcont);
                                tvGenerico1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                tvGenerico1.setText("Otro:");
                                tvGenerico1.setTextAppearance(R.style.TituloItem);
                                tvGenerico1.setPadding(0, mtop, 0, 0);
                                liDiscontinuidades.addView(tvGenerico1);

                                EditText etGenerico = new EditText(mcont);
                                etGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                etGenerico.setHint(hintElemento);
                                etGenerico.setEms(10);
                                etGenerico.setTag(tagElemento+"otro"+aux);
                                ListaEditText.get(idLinear).add(etGenerico);
                                liDiscontinuidades.addView(etGenerico);
                            }
                        }
                    }

                }
            });
            liForm.addView(bAnadirDiscont);


            listLiForm.add(liForm);
            liFormularios.addView(liForm);
        }

    }

    @Contract(pure = true)
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

                                    FormFeature nuevaEstacion = new FormFeature(true, Estacion, TipoEstacion, Este, Norte, Altitud, Fotos, Observaciones, Fecha, Propietario);

                                    databaseReference.child("EstacionesCampo/estacion_"+cont).setValue(nuevaEstacion);

                                    int fotosCount = Integer.parseInt(form.getString("FotosCount"));

//                                    for (int j = 0; j < fotosCount; j++) {
//                                        String uriFoto = form.getString("Fotos_Generales_"+j);
//                                        Uri urifotos = Uri.parse(uriFoto);
//                                        Bitmap imagen = getBitmapFromUri (urifotos);
//                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                                        imagen.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                                        byte[] data = baos.toByteArray();
//                                        //Uri file = Uri.fromFile(new File(Uri.parse(uriFoto)));
//                                        //String path = listFotosGeneral.get(i).getPath();
//                                        String subpath = uriFoto.substring(uriFoto.lastIndexOf('/') + 1);
//                                        StorageReference estacionRef = storageRef.child("FotosEstaciones/estacion_"+cont+"/"+subpath);
////                                        UploadTask uploadTask = estacionRef.putFile(file);
//                                        UploadTask uploadTask = estacionRef.putBytes(data);
//                                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
//                                            @Override
//                                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
//                                                if (!task.isSuccessful()) {
//                                                    throw task.getException();
//                                                }
//
//                                                // Continue with the task to get the download URL
//                                                return estacionRef.getDownloadUrl();
//                                            }
//                                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
//                                            @Override
//                                            public void onComplete(@NonNull Task<Uri> task) {
//                                                if (task.isSuccessful()) {
//                                                    Uri downloadUri = task.getResult();
//
//                                                    Log.d("jaaj", "onComplete: "+downloadUri);
//                                                } else {
//                                                    // Handle failures
//                                                    // ...
//                                                }
//                                            }
//                                        });
//
//                                    }

                                    JSONObject Formularios = form.getJSONObject("Formularios");
                                    JSONObject counts = Formularios.getJSONObject("counts");

                                    int contUGS_Rocas = Integer.parseInt(counts.getString("UGS_Rocas"));
                                    int contUGS_Suelos = Integer.parseInt(counts.getString("UGS_Suelos"));
                                    int contSGMF = Integer.parseInt(counts.getString("SGMF"));
                                    int contCAT = Integer.parseInt(counts.getString("CATALOGO"));

                                    databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/count_UGS_Rocas").setValue(contUGS_Rocas);
                                    databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/count_UGS_Suelos").setValue(contUGS_Suelos);
                                    databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/count_SGMF").setValue(contSGMF);
                                    databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/count_CATALOGO").setValue(contCAT);


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

                                        FormatUGSRocas nuevoFormatoUGSRocas = new FormatUGSRocas(true, noformato, municipios,  claseaflor,  gsi,  fabrica1,  fabrica2,  humedad1,  humedad2,  tamanograno1,  tamanograno2,  gradometeo1,  gradometeo2,  resistenciacomp1,  resistenciacomp2,  vereda,  noestacion,  secuenciaestratiopt1orden,  secuenciaestratiopt1espesor,  secuenciaestratiopt2orden,  secuenciaestratiopt2espesor,  secuenciaestratiopt3orden,  secuenciaestratiopt3espesor,  secuenciaestratiopt4orden,  secuenciaestratiopt4espesor,  secuenciaestratisuelor1orden,  secuenciaestratisuelor1espesor,  secuenciaestratisuelor2orden,  secuenciaestratisuelor2espesor,  secuenciaestratisuelor3orden,  secuenciaestratisuelor3espesor,  perfilmeteorizacion,  litologiasasociadasopt1exist,  litologiasasociadasopt1espesor,  litologiasasociadasopt2exist,  litologiasasociadasopt2espesor,  nombreugs,  color1,  color2,  composicionmineral1,  composicionmineral2);

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

                                            FormatDiscont nuevoFormatoDiscont = new FormatDiscont(true, TipoDiscont,  PersistenciaDiscont,  AnchoAberDiscont,  TipoRellenoDiscont,  RugosidadSuperDiscont,  FormaSuperDiscont,  HumedadDiscont,  EspaciamientoDiscont,  MeteorizacionDiscont,  DirBuzamiento,  Buzamiento,  RakePitch,  DirRakePitch,  AzBzBz1,  AzBzBz2,  AlturaDiscont,  ObservacionesDiscont);
                                            databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Rocas/Form_UGS_Rocas_"+j+"/Discontinuidades/Discont_"+k).setValue(nuevoFormatoDiscont);

                                        }

                                        int contFotosAnexas = Integer.parseInt(FromatoAux.getString("FotosAnexas"));
                                        databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Rocas/Form_UGS_Rocas_"+j+"/FotosAnexas/count").setValue(contFotosAnexas);
                                        for (int k = 1; k <= contFotosAnexas; k++) {

                                            String NombreFotosAnexas = EditTextsAux.getString("NombreFotosAnexas"+k);
                                            String DescriFotosAnexas = EditTextsAux.getString("DescriFotosAnexas"+k);

                                            FormatFotosAnexas nuevoFormatoFotosAnexas = new FormatFotosAnexas(true, NombreFotosAnexas, DescriFotosAnexas);
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

                                        resiscorte2 = RadioGrpAux.getString("resiscorte2");
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



                                        FormatUGSSuelos NuevoFormatoUGSSuelos = new FormatUGSSuelos(true, municipios,  claseaflor,  estructurasoporte1,  estructurasoporte2,  condicionhumedad1,  condicionhumedad2,  estructurasrelictas1,  estructurasrelictas2,  granulometria1,  granulometria2,  forma1,  forma2,  redondez1,  redondez2,  orientacion1,  orientacion2,  dirimbricacion1,  dirimbricacion2,  meteorizacionclastos1,  meteorizacionclastos2,  granulometriamatriz1,  granulometriamatriz2,  gradacion1,  gradacion2,  seleccion1,  seleccion2,  plasticidad1,  plasticidad2,  resiscorte1,  resiscorte2,  formasuelosgruesos1,  formasuelosgruesos2,  redondezsuelosgruesos1,  redondezsuelosgruesos2,  orientacionsuelosgruesos1,  orientacionsuelosgruesos2,  dirimbricacionmatriz1,  dirimbricacionmatriz2,  noformato,  vereda,  noestacion,  secuenciaestratiopt1orden,  secuenciaestratiopt1espesor,  secuenciaestratiopt2orden,  secuenciaestratiopt2espesor,  secuenciaestratiopt3orden,  secuenciaestratiopt3espesor,  secuenciaestratisuelor1orden,  secuenciaestratisuelor1espesor,  secuenciaestratisuelor2orden,  secuenciaestratisuelor2espesor,  secuenciaestratisuelor3orden,  secuenciaestratisuelor3espesor,  litologiasasociadasopt1exist,  litologiasasociadasopt1espesor,  litologiasasociadasopt2exist,  litologiasasociadasopt2espesor,  nombreugs,  porcentajematriz1,  porcentajematriz2,  porcentajeclastos1,  porcentajeclastos2,  color1,  color2,  observacionessuelos, descripcionsuelos, compacidadsuelosgruesos1, compacidadsuelosgruesos2);


                                        databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Suelos/Form_UGS_Suelos_"+j).setValue(NuevoFormatoUGSSuelos);


                                        int contFotosAnexas = Integer.parseInt(FromatoAux.getString("FotosAnexas"));
                                        databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Suelos/Form_UGS_Suelos_"+j+"/FotosAnexas/count").setValue(contFotosAnexas);
                                        for (int k = 1; k <= contFotosAnexas; k++) {

                                            String NombreFotosAnexas = EditTextsAux.getString("NombreFotosAnexas"+k);
                                            String DescriFotosAnexas = EditTextsAux.getString("DescriFotosAnexas"+k);

                                            FormatFotosAnexas nuevoFormatoFotosAnexas = new FormatFotosAnexas(true, NombreFotosAnexas, DescriFotosAnexas);
                                            databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Suelos/Form_UGS_Suelos_"+j+"/FotosAnexas/FotoAnexa_"+k).setValue(nuevoFormatoFotosAnexas);

                                        }

                                    }

                                    for (int j = 0; j < contSGMF; j++) {
                                        JSONObject FromatoAux = Formularios.getJSONObject("Form_SGMF_"+j);
                                        JSONObject SpinnersAux = FromatoAux.getJSONObject("Spinners");
                                        JSONObject EditTextsAux = FromatoAux.getJSONObject("EditText");
                                        JSONObject CheckBoxAux = FromatoAux.getJSONObject("CheckBox");

                                        boolean activo = true;
                                        String municipios = SpinnersAux.getString("municipios");;
                                        String noformato = EditTextsAux.getString("noformato");
                                        String vereda = EditTextsAux.getString("vereda");
                                        String noestacion = EditTextsAux.getString("noestacion");
                                        String ubicacionGeomorfoestructura = EditTextsAux.getString("ubicacionGeomorfoestructura");
                                        String ubicacionProvincia = EditTextsAux.getString("ubicacionProvincia");
                                        String ubicacionRegion = EditTextsAux.getString("ubicacionRegion");
                                        String ubicacionUnidad = EditTextsAux.getString("ubicacionUnidad");
                                        String ubicacionSubunidad = EditTextsAux.getString("ubicacionSubunidad");
                                        String ubicacionElemento = EditTextsAux.getString("ubicacionElemento");
                                        String nombreSGMF = EditTextsAux.getString("nombreSGMF");
                                        String codigoSGMF = EditTextsAux.getString("codigoSGMF");
                                        String observacionesSGMF = EditTextsAux.getString("observacionesSGMF");
                                        String ambiente0check = CheckBoxAux.getString("ambiente0check");
                                        String ambiente1check = CheckBoxAux.getString("ambiente1check");
                                        String ambiente2check = CheckBoxAux.getString("ambiente2check");
                                        String ambiente3check = CheckBoxAux.getString("ambiente3check");
                                        String ambiente4check = CheckBoxAux.getString("ambiente4check");
                                        String ambiente5check = CheckBoxAux.getString("ambiente5check");
                                        String ambiente6check = CheckBoxAux.getString("ambiente6check");
                                        String ambiente7check = CheckBoxAux.getString("ambiente7check");
                                        String ambiente8check = CheckBoxAux.getString("ambiente8check");


                                        FormatSGMF nuevoFormatoSGMF = new FormatSGMF( activo,  municipios,  noformato,  vereda,  noestacion,  ubicacionGeomorfoestructura,  ubicacionProvincia,  ubicacionRegion,  ubicacionUnidad,  ubicacionSubunidad,  ubicacionElemento,  nombreSGMF,  codigoSGMF,  observacionesSGMF,  ambiente0check,  ambiente1check,  ambiente2check,  ambiente3check,  ambiente4check,  ambiente5check,  ambiente6check,  ambiente7check,  ambiente8check);

                                            databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_SGMF/Form_SGMF_"+j).setValue(nuevoFormatoSGMF);

                                        int contSGMFNew = Integer.parseInt(FromatoAux.getString("SGMF"));
                                        databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_SGMF/Form_SGMF_"+j+"/SGMF/count").setValue(contSGMFNew);
                                        for (int k = 1; k <= contSGMFNew; k++) {


                                            String tiporoca = SpinnersAux.getString("tiporoca"+k);
                                            String gradometeor = SpinnersAux.getString("gradometeor"+k);
                                            String gradofractura = SpinnersAux.getString("gradofractura"+k);
                                            String tiposuelo = SpinnersAux.getString("tiposuelo"+k);
                                            String tamanograno = SpinnersAux.getString("tamanograno"+k);
                                            String tiporelieve = SpinnersAux.getString("tiporelieve"+k);
                                            String indicerelieve = SpinnersAux.getString("indicerelieve"+k);
                                            String inclinacionladera = SpinnersAux.getString("inclinacionladera"+k);
                                            String longiladera = SpinnersAux.getString("longiladera"+k);
                                            String formaladera = SpinnersAux.getString("formaladera"+k);
                                            String formacresta = SpinnersAux.getString("formacresta"+k);
                                            String formavalle = SpinnersAux.getString("formavalle"+k);
                                            String cobertura = SpinnersAux.getString("cobertura"+k);
                                            String uso = SpinnersAux.getString("uso"+k);
                                            String densidad = SpinnersAux.getString("densidad"+k);
                                            String frecuencia = SpinnersAux.getString("frecuencia"+k);
                                            String textura = SpinnersAux.getString("textura"+k);
                                            String patron = SpinnersAux.getString("patron"+k);
                                            String tipoerosion = SpinnersAux.getString("tipoerosion"+k);
                                            String espaciamiento = SpinnersAux.getString("espaciamiento"+k);
                                            String intensidaderosion = SpinnersAux.getString("intensidaderosion"+k);
                                            String tipodemm = SpinnersAux.getString("tipodemm"+k);
                                            String tipomaterial = SpinnersAux.getString("tipomaterial"+k);
                                            String actividad = SpinnersAux.getString("actividad"+k);
                                            String codigonuevaSGMF = EditTextsAux.getString("codigonuevaSGMF"+k);
                                            String coberturaotro = EditTextsAux.getString("coberturaotro"+k);
                                            String usootro = EditTextsAux.getString("usootro"+k);
                                            String patronotro = EditTextsAux.getString("patronotro"+k);


                                            FormatNewSGMF nuevoFormatoNewSGMF = new FormatNewSGMF(true,  tiporoca,  gradometeor,  gradofractura,  tiposuelo,  tamanograno,  tiporelieve,  indicerelieve,  inclinacionladera,  longiladera,  formaladera,  formacresta,  formavalle,  cobertura,  uso,  densidad,  frecuencia,  textura,  patron,  tipoerosion,  espaciamiento,  intensidaderosion,  tipodemm,  tipomaterial,  actividad,  codigonuevaSGMF,  coberturaotro,  usootro,  patronotro);
                                            databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_SGMF/Form_SGMF_"+j+"/SGMF/SGMF_"+k).setValue(nuevoFormatoNewSGMF);

                                        }

                                        int contFotosAnexas = Integer.parseInt(FromatoAux.getString("FotosAnexas"));
                                        databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_SGMF/Form_SGMF_"+j+"/FotosAnexas/count").setValue(contFotosAnexas);
                                        for (int k = 1; k <= contFotosAnexas; k++) {

                                            String NombreFotosAnexas = EditTextsAux.getString("NombreFotosAnexas"+k);
                                            String DescriFotosAnexas = EditTextsAux.getString("DescriFotosAnexas"+k);

                                            FormatFotosAnexas nuevoFormatoFotosAnexas = new FormatFotosAnexas(true, NombreFotosAnexas, DescriFotosAnexas);
                                            databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_SGMF/Form_SGMF_"+j+"/FotosAnexas/FotoAnexa_"+k).setValue(nuevoFormatoFotosAnexas);

                                        }

                                    }

                                    for (int j = 0; j < contCAT; j++) {
                                        JSONObject FromatoAux = Formularios.getJSONObject("Form_CATALOGO_"+j);
                                        JSONObject SpinnersAux = FromatoAux.getJSONObject("Spinners");
                                        JSONObject EditTextsAux = FromatoAux.getJSONObject("EditText");
                                        JSONObject RadioGrpAux = FromatoAux.getJSONObject("RadioGrp");

                                        boolean activo = true;
                                        String IMPORTANC = SpinnersAux.getString("IMPORTANC");
                                        String FECHA_FUENTE = SpinnersAux.getString("FECHA_FUENTE");
                                        String confiFechaMM = SpinnersAux.getString("ConfiFechaMM");
                                        String NOM_MUN = SpinnersAux.getString("NOM_MUN");
                                        String SUBTIPO_1 = SpinnersAux.getString("SUBTIPO_1");
                                        String SUBTIPO_2 = SpinnersAux.getString("SUBTIPO_2");
                                        String ID_PARTE = EditTextsAux.getString("ID_PARTE");
                                        String ENCUESTAD = EditTextsAux.getString("ENCUESTAD");
                                        String FECHA_MOV = EditTextsAux.getString("FECHA_MOV");
                                        String FECHA_REP = EditTextsAux.getString("FECHA_REP");
                                        String COD_SIMMA = EditTextsAux.getString("COD_SIMMA");
                                        String VEREDA = EditTextsAux.getString("VEREDA");
                                        String SITIO = EditTextsAux.getString("SITIO");
                                        String REF_GEOGRF = EditTextsAux.getString("REF_GEOGRF");
                                        String HERIDOS = EditTextsAux.getString("HERIDOS");
                                        String VIDAS = EditTextsAux.getString("VIDAS");
                                        String DESAPARECIDOS = EditTextsAux.getString("DESAPARECIDOS");
                                        String PERSONAS = EditTextsAux.getString("PERSONAS");
                                        String FAMILIAS = EditTextsAux.getString("FAMILIAS");
                                        String sensoresremotos = EditTextsAux.getString("sensoresremotos");
                                        String FTE_INFSEC = EditTextsAux.getString("FTE_INFSEC");
                                        String notas = EditTextsAux.getString("notas");
                                        String TIPO_MOV2 = RadioGrpAux.getString("TIPO_MOV2");
                                        String TIPO_MOV1 = RadioGrpAux.getString("TIPO_MOV1");


                                        FormatCAT nuevoFormatoCAT = new FormatCAT(activo,IMPORTANC,FECHA_FUENTE,confiFechaMM,NOM_MUN,SUBTIPO_1,SUBTIPO_2,ID_PARTE,ENCUESTAD,FECHA_MOV,FECHA_REP,COD_SIMMA,VEREDA,SITIO,REF_GEOGRF,HERIDOS,VIDAS,DESAPARECIDOS,PERSONAS,FAMILIAS,sensoresremotos,FTE_INFSEC,notas,TIPO_MOV2,TIPO_MOV1);

                                            databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_CATALOGO/Form_CATALOGO_"+j).setValue(nuevoFormatoCAT);

                                        int contDANOSNew = Integer.parseInt(FromatoAux.getString("DAÑOS"));
                                        databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_CATALOGO/Form_CATALOGO_"+j+"/DANOS/count").setValue(contDANOSNew);
                                        for (int k = 1; k <= contDANOSNew; k++) {

                                            String tiposdano = SpinnersAux.getString("tiposdaño"+k);
                                            String clasedano = SpinnersAux.getString("clasedaño"+k);
                                            String tipodano = EditTextsAux.getString("tipodaño"+k);
                                            String cantidaddano = EditTextsAux.getString("cantidaddaño"+k);
                                            String unidaddano = EditTextsAux.getString("unidaddaño"+k);
                                            String valordano = EditTextsAux.getString("valordaño"+k);


                                            FormatNewDANO nuevoFormatoNewDANO = new FormatNewDANO(tiposdano, clasedano, tipodano, cantidaddano, unidaddano, valordano);
                                            databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_CATALOGO/Form_CATALOGO_"+j+"/DANOS/DANOS_"+k).setValue(nuevoFormatoNewDANO);

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

    private Bitmap getBitmapFromUri(Uri uri) throws IOException{
        ParcelFileDescriptor parcelFileDescriptor = mcont.getContentResolver(). openFileDescriptor ( uri , "r" );
        FileDescriptor fileDescriptor = parcelFileDescriptor . getFileDescriptor ();
        Bitmap image = BitmapFactory. decodeFileDescriptor ( fileDescriptor );
        parcelFileDescriptor . close ();
        return image ;
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
                .put("FotosCount", listFotosGeneral.size())
                .put("Observaciones", etObservaciones.getText().toString())
                .put("Fecha", s.toString())
                .put("Propietario", userName);
        for (int i = 0; i < listFotosGeneral.size(); i++) {
            attrForm.put("Fotos_Generales_"+i, listFotosGeneral.get(i).toString());
        }

        JSONObject FormatosList = new JSONObject();
        JSONObject countFormatos = new JSONObject();
        int countFormatosUGSRocas = 0;
        int countFormatosUGSSuelos = 0;
        int countFormatosSGMF = 0;
        int countFormatosCAT = 0;
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

            //-------------> SGMF

            if (listFormularios.get(i).equals("SGMF")){
                JSONObject FormatoTemp = new JSONObject()
                        .put("SGMF", listContSGMF.get(i))
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


                FormatosList.put("Form_SGMF_"+countFormatosSGMF, FormatoTemp);

                countFormatosSGMF++;
            }

            //-------------> CATALOGO

            if (listFormularios.get(i).equals("Catálogo MM")){
                JSONObject FormatoTemp = new JSONObject()
                        .put("DAÑOS", listContDAÑOS.get(i));

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

                JSONObject radioGrp = new JSONObject();
                for (int k = 0; k < ListaRadioGrp.get(i).size(); k++) {
                    RadioButton checkedRadioButton = (RadioButton)ListaRadioGrp.get(i).get(k).findViewById(ListaRadioGrp.get(i).get(k).getCheckedRadioButtonId());
//                    Log.d("jaaja", "onCheckedChanged: "+checkedRadioButton.getTag());
//                    Log.d("jaaja", "onCheckedChanged: "+ListaRadioGrp.get(i).get(k).getTag());
                    radioGrp.put(ListaRadioGrp.get(i).get(k).getTag().toString(), checkedRadioButton.getTag());
                }
                FormatoTemp.put("RadioGrp", radioGrp);


                FormatosList.put("Form_CATALOGO_"+countFormatosCAT, FormatoTemp);
                countFormatosCAT++;
            }


        }
        countFormatos.put("UGS_Rocas", countFormatosUGSRocas);
        countFormatos.put("UGS_Suelos", countFormatosUGSSuelos);
        countFormatos.put("SGMF", countFormatosSGMF);
        countFormatos.put("CATALOGO", countFormatosCAT);

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

        //--------------> SGMF

        listaElementosSGMF.add(new ElementoFormato( "Número Formato",  "edittext",  "noformato", 0));
        listaElementosSGMF.add(new ElementoFormato( "Municipio",  "spinner",  "municipios", R.array.Municipios));
        listaElementosSGMF.add(new ElementoFormato( "Vereda",  "edittext",  "vereda", 0));
        listaElementosSGMF.add(new ElementoFormato( "Número de la Estación",  "edittext",  "noestacion", 0));
        listaElementosSGMF.add(new ElementoFormato( "MORFOGÉNESIS",  "titulo",  "", 0));
        listaElementosSGMF.add(new ElementoFormato( "Tipo de Ambiente (Marque varios si es necesario)","ambientes","ambiente",R.array.Ambientes));
        listaElementosSGMF.add(new ElementoFormato( "UBICACIÓN GEOMORFOLÓGICA","ubicacionGeo","ubicacion",R.array.UbicacionGeomorfo));
        listaElementosSGMF.add(new ElementoFormato( "CARACTERIZACIÓN DE LA (S) GEOFORMA (S)",  "titulo",  "", 0));
        listaElementosSGMF.add(new ElementoFormato( "Nombre SGMF / EGMF","edittext","nombreSGMF",0));
        listaElementosSGMF.add(new ElementoFormato( "ID - Código SGMF / EGMF","edittext","codigoSGMF",0));
        listaElementosSGMF.add(new ElementoFormato( "Observaciones","edittext","observacionesSGMF",0));

        listaElementosNuevoSGMF.add(new ElementoFormato( "MORFOLITOLOGÍA - MORFOLOGÍA - MORFOMETRÍA - COBERTURA",  "titulo",  "", 0));
        listaElementosNuevoSGMF.add(new ElementoFormato( "ID-Código SGMF-EGMF","edittext","codigonuevaSGMF",0));
        listaElementosNuevoSGMF.add(new ElementoFormato( "TIPO DE ROCA, TRO","spinner","tiporoca",R.array.TipodeRocaGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "GRADO DE METEORIZACIÓN, GM","spinner","gradometeor",R.array.MeteorizacionGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "GRADO DE FRACTURAMIENTO, GF","spinner","gradofractura",R.array.GradoFracturamientoGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "TIPO DE SUELO, TSU","spinner","tiposuelo",R.array.TipoSueloGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "TAMAÑO DE GRANO, TG","spinner","tamanograno",R.array.TamañoGranoGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "TIPO DE RELIEVE, TR","spinner","tiporelieve",R.array.TipoRelieveGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "INDICE DE RELIEVE, IR","spinner","indicerelieve",R.array.IndiceRelieveGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "INCLINACIÓN LADERA, IL","spinner","inclinacionladera",R.array.InclinacionLaderaGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "LONGITUD LADERA, LL","spinner","longiladera",R.array.LongitudLaderaGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "FORMA LADERA, FL","spinner","formaladera",R.array.FormaLaderaGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "FORMA DE LA CRESTA, FC","spinner","formacresta",R.array.FormaCrestaGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "FORMAS DEL VALLE, FV","spinner","formavalle",R.array.FormaValleGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "COBERTURA, C","spinner","cobertura",R.array.CoberturaGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "USO DEL TERRENO, U","spinner","uso",R.array.UsoGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "CARACTERÍSTICAS DE DRENAJE",  "titulo",  "", 0));
        listaElementosNuevoSGMF.add(new ElementoFormato( "DENSIDAD, D","spinner","densidad",R.array.DensidadGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "FRECUENCIA, FR","spinner","frecuencia",R.array.FrecuenciaGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "TEXTURA, TEX","spinner","textura",R.array.TexturaGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "PATRÓN, PT","spinner","patron",R.array.PatronGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "MORFODINÁMICA",  "titulo",  "", 0));
        listaElementosNuevoSGMF.add(new ElementoFormato( "TIPO DE EROSIÓN, TE","spinner","tipoerosion",R.array.TipoErosionGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "ESPACIAMIENTO ENTRE CANALES, EC","spinner","espaciamiento",R.array.EspaciamientoGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "INTENSIDAD DE LA EROSIÓN, IER","spinner","intensidaderosion",R.array.IntensidadErosionGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "TIPOS DE MM, TMM","spinner","tipodemm",R.array.TipoMMGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "TIPO DE MATERIAL ASOCIADO, TMA","spinner","tipomaterial",R.array.TipoMaterialGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "ACTIVIDAD, ACT","spinner","actividad",R.array.ActividadGMF));

        //--------------> CATALOGO MM

        listaElementosCAT.add(new ElementoFormato("ID PARTE del MM","edittextMM","ID_PARTE", 0));
        listaElementosCAT.add(new ElementoFormato("IMPORTANCIA","spinnerMM","IMPORTANC",R.array.Importancia));
        listaElementosCAT.add(new ElementoFormato("ENCUESTADOR","edittextMM","ENCUESTAD",0));
        listaElementosCAT.add(new ElementoFormato("FECHA EVENTO","edittextMM","FECHA_MOV",0));
        listaElementosCAT.add(new ElementoFormato("FUENTE FECHA EVENTO","spinner","FECHA_FUENTE",R.array.FuenteFechaEvento));
        listaElementosCAT.add(new ElementoFormato("CONFIABILIDAD FECHA EVENTO","spinnerMM","ConfiFechaMM",R.array.ConfiFecha));
        listaElementosCAT.add(new ElementoFormato("FECHA REPORTE","edittextMM","FECHA_REP",0));
        listaElementosCAT.add(new ElementoFormato("SIMMA","edittextMM","COD_SIMMA",0));
        listaElementosCAT.add(new ElementoFormato("Municipio",  "spinnerMM",  "NOM_MUN", R.array.Municipios));
        listaElementosCAT.add(new ElementoFormato("Vereda",  "edittextMM",  "VEREDA", 0));
        listaElementosCAT.add(new ElementoFormato("SITIO","edittext","SITIO",0));
        listaElementosCAT.add(new ElementoFormato("REFERENCIA GEOGRÁFICA","edittextMM","REF_GEOGRF",0));
        listaElementosCAT.add(new ElementoFormato("CLASIFICACIÓN DEL MOVIMIENTO",  "titulo",  "", 0));
        listaElementosCAT.add(new ElementoFormato("TIPO MOVIMIENTO",  "radiobtnMM",  "TIPO_MOV", R.array.TipoMovimiento));
        listaElementosCAT.add(new ElementoFormato("SUBTIPO PRIMER MOVIMIENTO",  "spinnerMM",  "SUBTIPO_1", R.array.SubtipoMovimiento));
        listaElementosCAT.add(new ElementoFormato("SUBTIPO SEGUNDO MOVIMIENTO",  "spinnerMM",  "SUBTIPO_2", R.array.SubtipoMovimiento1));
        listaElementosCAT.add(new ElementoFormato("POBLACION AFECTADA",  "titulo",  "", 0));
        listaElementosCAT.add(new ElementoFormato("Heridos","edittext","HERIDOS",0));
        listaElementosCAT.add(new ElementoFormato("Vidas","edittext","VIDAS",0));
        listaElementosCAT.add(new ElementoFormato("Desaparecidos","edittext","DESAPARECIDOS",0));
        listaElementosCAT.add(new ElementoFormato("Personas","edittext","PERSONAS",0));
        listaElementosCAT.add(new ElementoFormato("Familias","edittext","FAMILIAS",0));
        listaElementosCAT.add(new ElementoFormato("IMÁGENES SATELITALES","edittext","sensoresremotos",0));
        listaElementosCAT.add(new ElementoFormato("FOTOGRAFÍAS AÉREAS","edittextMM","FTE_INFSEC",0));
        listaElementosCAT.add(new ElementoFormato("NOTAS (Ej: Causas y observaciones generales):","edittext","notas",0));
        listaElementosCAT.add(new ElementoFormato("DAÑOS A INFRASTRUCTURA, ACTIVIDADES ECONÓMICAS, DAÑOS AMBIENTALES:","titulo","",0));
        listaElementosCAT.add(new ElementoFormato("TIPO DE DAÑO:","textview","",R.string.NotaDaños));

        listaElementosCATDANOS.add(new ElementoFormato("CLASE DE DAÑO", "spinner", "clasedaño", R.array.ClaseDaño));
        listaElementosCATDANOS.add(new ElementoFormato("TIPO", "edittext", "tipodaño", 0));
        listaElementosCATDANOS.add(new ElementoFormato("CANTIDAD", "edittext", "cantidaddaño", 0));
        listaElementosCATDANOS.add(new ElementoFormato("UNIDAD", "edittext", "unidaddaño", 0));
        listaElementosCATDANOS.add(new ElementoFormato("TIPO DAÑO", "spinner", "tiposdaño", R.array.TiposDaño));
        listaElementosCATDANOS.add(new ElementoFormato("VALOR (US$)", "edittext", "valordaño", 0));


        //--------------> INVENTARIO MM

//        listaElementosINV.add(new ElementoFormato("ID PARTE del MM","edittext","ID_PARTE", 0));
        listaElementosINV.add(new ElementoFormato("IMPORTANCIA","checkradio","IMPORTANC",R.array.Importancia));
        listaElementosINV.add(new ElementoFormato("ENCUESTADOR","edittext","ENCUESTAD",0));
        listaElementosINV.add(new ElementoFormato("FECHA EVENTO","fecha","FECHA_MOV",0));
        listaElementosINV.add(new ElementoFormato("FUENTE FECHA EVENTO","spinner","FECHA_FUENTE",R.array.FuenteFechaEvento));
        listaElementosINV.add(new ElementoFormato("CONFIABILIDAD FECHA EVENTO","spinner","ConfiFechaMM",R.array.ConfiFecha));
        listaElementosINV.add(new ElementoFormato("FECHA REPORTE","fecha","FECHA_REP",0));
        listaElementosINV.add(new ElementoFormato("INSTITUCIÓN","edittext","INSTITUC",0));
        listaElementosINV.add(new ElementoFormato("SIMMA","edittext","COD_SIMMA",0));
        listaElementosINV.add(new ElementoFormato("Municipio",  "spinner",  "NOM_MUN", R.array.Municipios));
        listaElementosINV.add(new ElementoFormato("Vereda",  "edittext",  "VEREDA", 0));
        listaElementosINV.add(new ElementoFormato("Sitio","edittext","SITIO",0));
        listaElementosINV.add(new ElementoFormato("REFERENCIA GEOGRÁFICA","edittext","REF_GEOGRF",0));
        listaElementosINV.add(new ElementoFormato("DOCUMENTACIÓN",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("PLANCHAS","edittext","planchas",0));
        listaElementosINV.add(new ElementoFormato("SENSORES REMOTOS","edittext","sensoresremotos",0));
        listaElementosINV.add(new ElementoFormato("FOTOGRAFÍAS AÉREAS","edittext","FTE_INFSEC",0));
        listaElementosINV.add(new ElementoFormato("ACTIVIDAD DEL MOVIMIENTO",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("EDAD","spinner","edadmm",R.array.FuenteFechaEvento));
        listaElementosINV.add(new ElementoFormato("ESTADO","spinner","ESTADO_ACT",R.array.FuenteFechaEvento));
        listaElementosINV.add(new ElementoFormato("ESTILO","spinner","ESTILO",R.array.FuenteFechaEvento));
        listaElementosINV.add(new ElementoFormato("DISTRIBUCIÓN","spinner","DISTRIBUC",R.array.FuenteFechaEvento));
        listaElementosINV.add(new ElementoFormato("LITOLOGIA Y ESTRUCTURA",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("DESCRIPCIÓN","edittext","DESCRIP",0));
        listaElementosINV.add(new ElementoFormato("ESTRUCTURA","estructuras","estructura",R.array.FuenteFechaEvento));
        listaElementosINV.add(new ElementoFormato("CLASIFICACIÓN DEL MOVIMIENTO",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("TIPO MOVIMIENTO",  "radiobtn",  "TIPO_MOV", R.array.TipoMovimiento));
        listaElementosINV.add(new ElementoFormato("SUBTIPO PRIMER MOVIMIENTO",  "spinner",  "SUBTIPO1", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("SUBTIPO SEGUNDO MOVIMIENTO",  "spinner",  "SUBTIPO2", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("TIPO MATERIAL",  "radiobtn",  "tipomaterial", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("HUMEDAD",  "radiobtn",  "humedad", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("PLASTICIDAD",  "radiobtn",  "plasticidad", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("ORIGEN SUELO",  "origensuelo",  "origensuelo", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("VELOCIDAD",  "spinner",  "velocidad", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("VELOCIDAD MÁXIMA","edittext","velocidadmax",0));
        listaElementosINV.add(new ElementoFormato("VELOCIDAD MÍNIMA","edittext","velocidadmin",0));
        listaElementosINV.add(new ElementoFormato("SISTEMA DE CLASIFICACIÓN",  "spinner",  "sisclasificacion", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("MORFOMETRÍA",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("GENERAL",  "multitext",  "morfogeneral", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("DIMENSIONES DEL TERRENO",  "multitext",  "morfodimensiones", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("DEFORMACIÓN TERRENO",  "titulito",  "", 0));
        listaElementosINV.add(new ElementoFormato("MODO",  "spinner",  "morfomodo", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("SEVERIDAD",  "spinner",  "morfoseveridad", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("GEOFORMA",  "edittext",  "morfogeoforma", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("CAUSAS DEL MOVIMIENTO",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("INHERENTES",  "spinner",  "causasinherentes", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("CONTRIBUYENTES - DETONANTES",  "radiobtn2",  "causascontrideto", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("TIPO DE EROSIÓN",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("SUPERFICIAL",  "spinner",  "erosionsuperficial", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("SUBSUPERFICIAL",  "spinner",  "erosionsubsuperficial", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("EDAD",  "spinner",  "erosionedad", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("ESTADO",  "spinner",  "erosionestado", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("FLUVIAL",  "spinner",  "erosionfluvial", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("EOLICA",  "spinner",  "erosioneolica", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("COBERTURA Y USO DEL SUELO",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("COBERTURA DEL SUELO",  "multitext",  "cobertura", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("USO DEL SUELO",  "multitext",  "usosuelo", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("REFERENCIAS",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("AUTOR","edittext","referenciasautor",0));
        listaElementosINV.add(new ElementoFormato("AÑO","edittext","referenciasaño",0));
        listaElementosINV.add(new ElementoFormato("TITULO","edittext","referenciastitulo",0));
        listaElementosINV.add(new ElementoFormato("EDITOR","edittext","referenciaseditor",0));
        listaElementosINV.add(new ElementoFormato("CIUDAD","edittext","referenciasciudad",0));
        listaElementosINV.add(new ElementoFormato("PAGINAS","edittext","referenciaspaginas",0));
        listaElementosINV.add(new ElementoFormato("EFECTOS SECUNDARIOS",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("REPRESAMIENTO",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("TIPO (Costa & Schuster, 1988)",  "spinner",  "represamientotipo", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("MORFOMETRÍA DE LA PRESA",  "multitext",  "represamientopresa", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("CONDICIONES DE LA PRESA",  "multicheck",  "represamientocondiciones", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("EFECTOS",  "multicheck",  "represamientoefectos", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("POBLACION AFECTADA",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("HERIDOS","edittext","heridos",0));
        listaElementosINV.add(new ElementoFormato("VIDAS","edittext","vidas",0));
        listaElementosINV.add(new ElementoFormato("DESAPARECIDOS","edittext","desaparecidos",0));
        listaElementosINV.add(new ElementoFormato("PERSONAS","edittext","personas",0));
        listaElementosINV.add(new ElementoFormato("FAMILIAS","edittext","familias",0));
        listaElementosINV.add(new ElementoFormato("TIPO DE DAÑO:","textview","",0));
        listaElementosINV.add(new ElementoFormato("DAÑOS A INFRASTRUCTURA, ACTIVIDADES ECONÓMICAS, DAÑOS AMBIENTALES:","linear","",0));
        listaElementosINV.add(new ElementoFormato("NOTAS","edittext","notas",0));
        listaElementosINV.add(new ElementoFormato("APRECIACIÓN DEL RIESGO","edittext","apreciacionriesgo",0));
        listaElementosINV.add(new ElementoFormato("ESQUEMA DEL MOVIMIENTO",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("FOTO EN PLANTA",  "edittext",  "fotoplanta", 0));
        listaElementosINV.add(new ElementoFormato("OBSERVACIONES",  "edittext",  "fotoplantaobs", 0));
        listaElementosINV.add(new ElementoFormato("FOTO EN PERFIL",  "edittext",  "fotoperfil", 0));
        listaElementosINV.add(new ElementoFormato("OBSERVACIONES",  "edittext",  "fotoperfilobs", 0));

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public class Localizacion implements LocationListener {
        SlideshowFragment mainActivity;
        public SlideshowFragment getMainActivity() {
            return mainActivity;
        }
        public void setMainActivity(SlideshowFragment context) {
            this.mainActivity = context;
        }
        @Override
        public void onLocationChanged(Location loc) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            loc.getLatitude();
            loc.getLongitude();
            loc.getAltitude();
            loc.getAccuracy();
            String sLatitud = String.valueOf(loc.getLatitude());
            String sLongitud = String.valueOf(loc.getLongitude());
            String sAltitud = String.valueOf(loc.getAltitude());
            String sAccuracy = String.valueOf(loc.getAccuracy());
            etNorte.setText(sLatitud);
            etEste.setText(sLongitud);
            etAltitud.setText(sAltitud);
            //tvEstadoGPS.setText("Precisión: "+ sAccuracy);
            //this.mainActivity.setLocation(loc);
        }
        @Override
        public void onProviderDisabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            tvEstadoGPS.setText("GPS Desactivado");
        }
        @Override
        public void onProviderEnabled(String provider) {
            // Este metodo se ejecuta cuando el GPS es activado
            tvEstadoGPS.setText("GPS Activado");
        }
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            switch (status) {
                case LocationProvider.AVAILABLE:
                    Log.d("debug", "LocationProvider.AVAILABLE");
                    tvEstadoGPS.setText("GPS Disponible");
                    break;
                case LocationProvider.OUT_OF_SERVICE:
                    Log.d("debug", "LocationProvider.OUT_OF_SERVICE");
                    tvEstadoGPS.setText("GPS Fuera de Servicio");
                    break;
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
                    tvEstadoGPS.setText("GPS Temporalmente NO Disponible");
                    break;
            }
        }
    }

}
