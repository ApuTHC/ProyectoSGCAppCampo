package com.unal.proyectosgcappcampo.ui.slideshow;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
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
import androidx.appcompat.app.AlertDialog;
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
import com.unal.proyectosgcappcampo.MainActivity;
import com.unal.proyectosgcappcampo.R;
import com.unal.proyectosgcappcampo.databinding.FragmentSlideshowBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.jetbrains.annotations.Contract;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SlideshowFragment extends Fragment {

    private FragmentSlideshowBinding binding;

    private FirebaseAuth firebaseAuth;
    StorageReference storageRef;
    String userName;
    boolean login = false;

    boolean manualLoc = false;

    boolean lluvias1 = false;
    boolean lluvias2 = false;
    boolean sismo1 = false;
    boolean sismo2 = false;

    private ActivityResultLauncher<Intent> intentLaucherGeneral;
    private ActivityResultLauncher<Intent> intentLaucherLib;
    private ActivityResultLauncher<Intent> intentLaucherFormatos;


    Button btnFormLoad;
    Button btnFormSync;
    Button btnAddForm;
    Button btnFoto;
    Button btnFotoBorrar;
    Button btnFotoLib;
    Button btnFotoLibBorrar;
    Button btnLocalization;
    LinearLayout liFotosGeneral;
    LinearLayout liFotosLib;
    EditText etEstacion;
    EditText etTipoEstacion;
    EditText etEste;
    EditText etNorte;
    EditText etAltitud;
    EditText etFotos;
    EditText etFotosLib;
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
    List<ElementoFormato> listaElementosINVFotosAnexas = new ArrayList<ElementoFormato>();

    ElementoFormato ElementoSueloResidualUGSR = new ElementoFormato( "Horizonte",  "secuenciaestrati",  "secuenciaestratisuelor", R.array.SecuenciaEstratiRocasSueloRes);
    ElementoFormato ElementoSueloResidualUGSS = new ElementoFormato( "Horizonte",  "secuenciaestrati",  "secuenciaestratisuelor", R.array.SecuenciaEstratiSuelosSueloRes);


    List<Uri> listFotosGeneral = new ArrayList<Uri>();
    List<Uri> listFotosLib = new ArrayList<Uri>();
    List<Uri> listFotosAnexaSame = new ArrayList<Uri>();
    List<List<Uri>> listFotosSame = new ArrayList<List<Uri>>();
    List<List<Uri>> listFotosUGSR = new ArrayList<List<Uri>>();
    List<List<Uri>> listFotosUGSS = new ArrayList<List<Uri>>();
    List<List<Uri>> listFotosSGMF = new ArrayList<List<Uri>>();
    List<List<Uri>> listFotosINV = new ArrayList<List<Uri>>();
    List<String> listNombresFotosGeneral = new ArrayList<String>();
    List<String> listNombresFotosLib = new ArrayList<String>();
    List<String> listNombresFotosSame = new ArrayList<String>();
    List<List<String>> ListaNombresFotosSame = new ArrayList<List<String>>();
    List<List<List<String>>> ListaNombresFotosFormatos = new ArrayList<List<List<String>>>();


    List<List<List<Uri>>> ListaUriFotosAnexas = new ArrayList<List<List<Uri>>>();


    List<LinearLayout> listLiFotosAnexas = new ArrayList<LinearLayout>();
    List<List<LinearLayout>> ListaLiFotosAnexas = new ArrayList<List<LinearLayout>>();
    List<EditText> listEtFotosAnexas = new ArrayList<EditText>();
    List<List<EditText>> ListaEtFotosAnexas = new ArrayList<List<EditText>>();

    String actualLiFotos = "";

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
    List<Integer> listContDANOS = new ArrayList<Integer>();
    List<List<LinearLayout>> ListaDAÑOS = new ArrayList<List<LinearLayout>>();
    List<LinearLayout> listDAÑOS = new ArrayList<LinearLayout>();
    LinearLayout liFormDAÑOS;

    //Format UGS


    int fotosAnexas = 0;
    List<List<LinearLayout>> ListaFotosAnexas = new ArrayList<List<LinearLayout>>();
    List<LinearLayout> listFotosAnexas = new ArrayList<LinearLayout>();
    LinearLayout liFormFotosAnexas;


    List<Integer> listContDiscontinuidades = new ArrayList<Integer>();
    List<Integer> listContFotosAnexas = new ArrayList<Integer>();

    int discontinuidades = 0;
    List<List<LinearLayout>> ListaDiscontinuidades = new ArrayList<List<LinearLayout>>();
    List<LinearLayout> listDiscontinuidades = new ArrayList<LinearLayout>();
    LinearLayout liFormDiscontinuidades;

    //FotosInventarios

    List<Integer> listContFotosAnexasINV = new ArrayList<Integer>();
    int fotosAnexasINV = 0;
    List<List<LinearLayout>> ListaFotosAnexasINV = new ArrayList<List<LinearLayout>>();
    List<LinearLayout> listFotosAnexasINV = new ArrayList<LinearLayout>();
    LinearLayout liFormFotosAnexasINV;

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
        listaElementosINV = new ArrayList<ElementoFormato>();
        listaElementosINVFotosAnexas = new ArrayList<ElementoFormato>();
        listFormularios = new ArrayList<String>();
        listLiForm = new ArrayList<LinearLayout>();

        GenerarListas();

        btnFoto = binding.btnFoto;
        btnFotoBorrar = binding.btnFotoBorrar;
        btnFotoLib = binding.btnFotoLib;
        btnFotoLibBorrar = binding.btnFotolibBorrar;
        btnLocalization = binding.btnLocalization;
        liFotosGeneral = binding.liFotos;
        liFotosLib = binding.liFotosLib;
        btnFormLoad = binding.btnFormLoad;
        btnFormSync = binding.btnFormSync;
        btnAddForm = binding.AddFormu;
        etEstacion = binding.etEstacion;
        etTipoEstacion = binding.etTipoEstacion;
        etNorte = binding.etNorte;
        etEste = binding.etEste;
        etAltitud = binding.etAltitud;
        etFotos = binding.etFotos;
        etFotosLib = binding.etFotosLib;
        etObservaciones = binding.etObservaciones;
        tvEstadoGPS = binding.tvEstadoGPS;

        ActivityResult();
        listFotosGeneral = new ArrayList<Uri>();
        listFotosLib = new ArrayList<Uri>();
        ListaUriFotosAnexas = new ArrayList<List<List<Uri>>>();
        ListaNombresFotosFormatos = new ArrayList<List<List<String>>>();


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
        }
        else {
            formComplete = new JSONArray();
        }

        btnFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CargarImagen("General");
            }
        });
        btnFotoBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CargarImagen("GeneralBorrar");
            }
        });

        btnFotoLib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CargarImagen("Lib");
            }
        });

        btnFotoLibBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CargarImagen("LibBorrar");
            }
        });

        btnLocalization.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (manualLoc){
                    manualLoc = false;
                    btnLocalization.setText("Añadir Manualmente");
                }
                else{
                    manualLoc = true;
                    btnLocalization.setText("Usar GPS del Celular");
                }
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
        }
        else {
            locationStart();
        }

//        if (ActivityCompat.checkSelfPermission(mcont, Manifest.permission.MANAGE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mcont, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
////            requestPermissions(new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE,}, 1000);
//            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 1000);
//        }
        int permissionCheck = ContextCompat.checkSelfPermission(mcont, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.i("Mensaje", "No se tiene permiso para leer.");
            requestPermissions( new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 225);
        }
        else {
            Log.i("Mensaje", "Se tiene permiso para leer y escribir!");
        }


    }

    private void ActivityResult() {
        intentLaucherGeneral = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            listNombresFotosGeneral = new ArrayList<String>();
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
               StringBuilder name = new StringBuilder();
               for (int i = 0; i < listFotosGeneral.size(); i++) {
                   ImageView imagen = new ImageView(mcont);
                   imagen.setLayoutParams(new ActionBar.LayoutParams(400, 400));
                   imagen.setImageURI(listFotosGeneral.get(i));
                   String path = listFotosGeneral.get(i).getPath();
                   listNombresFotosGeneral.add(path.substring(path.lastIndexOf('/') + 1));
                   name.append(path.substring(path.lastIndexOf('/') + 1)).append(", ");
                   liFotosGeneral.addView(imagen);
               }
               Log.d("images", "ActivityResult: "+listNombresFotosGeneral);
               etFotos.setText(name.toString());
           }
        });

        intentLaucherLib = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            listNombresFotosLib = new ArrayList<String>();
            if (result.getResultCode() == Activity.RESULT_OK){
               Uri imageUri;
               if (result.getData().getClipData() != null){
                   //Seleccionar multiples Imagenes
                   int count = result.getData().getClipData().getItemCount();
                   for (int i = 0; i<count; i++){
                       imageUri = result.getData().getClipData().getItemAt(i).getUri();
                       listFotosLib.add(imageUri);
                   }
               }
               else{
                   //Seleccionar una imagen
                   imageUri = result.getData().getData();
                   listFotosLib.add(imageUri);
               }

               liFotosLib.removeAllViews();
               StringBuilder name = new StringBuilder();
               for (int i = 0; i < listFotosLib.size(); i++) {
                   ImageView imagen = new ImageView(mcont);
                   imagen.setLayoutParams(new ActionBar.LayoutParams(400, 400));
                   imagen.setImageURI(listFotosLib.get(i));
                   String path = listFotosLib.get(i).getPath();
                   listNombresFotosLib.add(path.substring(path.lastIndexOf('/') + 1));
                   name.append(path.substring(path.lastIndexOf('/') + 1)).append(", ");
                   liFotosLib.addView(imagen);
               }
               Log.d("images", "ActivityResult: "+listNombresFotosLib);
               etFotosLib.setText(name.toString());
           }
        });

        intentLaucherFormatos = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            int auxIdForm = Integer.parseInt(actualLiFotos.split("-")[1]);
            int auxIdFotoAnexa = Integer.parseInt(actualLiFotos.split("-")[2]);


            ListaUriFotosAnexas.get(auxIdForm).set(auxIdFotoAnexa, new ArrayList<Uri>());
            ListaNombresFotosFormatos.get(auxIdForm).set(auxIdFotoAnexa, new ArrayList<String>());

            if (result.getResultCode() == Activity.RESULT_OK){
               Uri imageUri;
               if (result.getData().getClipData() != null){
                   //Seleccionar multiples Imagenes
                   int count = result.getData().getClipData().getItemCount();
                   for (int i = 0; i<count; i++){
                       imageUri = result.getData().getClipData().getItemAt(i).getUri();
                       ListaUriFotosAnexas.get(auxIdForm).get(auxIdFotoAnexa).add(imageUri);
                   }
               }
               else{
                   //Seleccionar una imagen
                   imageUri = result.getData().getData();
                   ListaUriFotosAnexas.get(auxIdForm).get(auxIdFotoAnexa).add(imageUri);
               }

                ListaLiFotosAnexas.get(auxIdForm).get(auxIdFotoAnexa).removeAllViews();
               StringBuilder name = new StringBuilder();
               for (int i = 0; i < ListaUriFotosAnexas.get(auxIdForm).get(auxIdFotoAnexa).size(); i++) {
                   ImageView imagen = new ImageView(mcont);
                   imagen.setLayoutParams(new ActionBar.LayoutParams(400, 400));
                   imagen.setImageURI(ListaUriFotosAnexas.get(auxIdForm).get(auxIdFotoAnexa).get(i));
                   String path = ListaUriFotosAnexas.get(auxIdForm).get(auxIdFotoAnexa).get(i).getPath();
                   ListaNombresFotosFormatos.get(auxIdForm).get(auxIdFotoAnexa).add(path.substring(path.lastIndexOf('/') + 1));
                   name.append(path.substring(path.lastIndexOf('/') + 1)).append(", ");
                   ListaLiFotosAnexas.get(auxIdForm).get(auxIdFotoAnexa).addView(imagen);
               }
               Log.d("images", "ActivityResult: "+ ListaNombresFotosFormatos);
                ListaEtFotosAnexas.get(auxIdForm).get(auxIdFotoAnexa).setText(name.toString());
           }
        });
    }

    private void CargarImagen(String Tipo) {

        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        if (Tipo.equals("UGSR") || Tipo.equals("UGSS") || Tipo.equals("SGMF") || Tipo.equals("INV")){
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false);
            intentLaucherFormatos.launch(Intent.createChooser(intent, "Seleccione la aplicación"));
        }
        if (Tipo.equals("General")){
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intentLaucherGeneral.launch(Intent.createChooser(intent, "Seleccione la aplicación"));
        }
        if (Tipo.equals("Lib")){
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intentLaucherLib.launch(Intent.createChooser(intent, "Seleccione la aplicación"));
        }
        if (Tipo.equals("GeneralBorrar")){
            listNombresFotosGeneral = new ArrayList<String>();
            listFotosGeneral = new ArrayList<Uri>();
            liFotosGeneral.removeAllViews();
            etFotos.setText("");
        }
        if (Tipo.equals("LibBorrar")){
            listNombresFotosLib = new ArrayList<String>();
            listFotosLib = new ArrayList<Uri>();
            liFotosLib.removeAllViews();
            etFotosLib.setText("");
        }

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
        listFotosAnexasINV = new ArrayList<LinearLayout>();
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

        listLiFotosAnexas = new ArrayList<LinearLayout>();
        listEtFotosAnexas = new ArrayList<EditText>();
        listFotosSame = new ArrayList<List<Uri>>();
        listFotosUGSR = new ArrayList<List<Uri>>();
        listFotosUGSS = new ArrayList<List<Uri>>();
        listFotosSGMF = new ArrayList<List<Uri>>();
        listFotosINV = new ArrayList<List<Uri>>();
        ListaNombresFotosSame = new ArrayList<List<String>>();




        ListaFotosAnexasINV.add(listFotosAnexasINV);
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
        fotosAnexasINV = 0;

        listContFotosAnexasINV.add(fotosAnexasINV);
        listContFotosAnexas.add(fotosAnexas);
        listContDiscontinuidades.add(discontinuidades);
        listContSGMF.add(sgmf);
        listContDANOS.add(daños);
        ListaUriFotosAnexas.add(listFotosSame);
        ListaNombresFotosFormatos.add(ListaNombresFotosSame);

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
                        etSecuenciaEstratiOpt.setTag(tagElemento+aux+"orden");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt);
                        liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt);

                        EditText etSecuenciaEstratiOpt1Espesor = new EditText(mcont);
                        etSecuenciaEstratiOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiOpt1Espesor.setHint("Espesor (m)");
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
                                            etSecuenciaEstratiSueloROrden.setTag(tagElemento+ aux +"orden");
                                            ListaEditText.get(idLinear).add(etSecuenciaEstratiSueloROrden);
                                            liFormSecuenciaEstratiSueloR1.addView(etSecuenciaEstratiSueloROrden);

                                            EditText etSecuenciaEstratiSueloREspesor = new EditText(mcont);
                                            etSecuenciaEstratiSueloREspesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            etSecuenciaEstratiSueloREspesor.setHint("Espesor (m)");
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

            ListaEtFotosAnexas.add(listEtFotosAnexas);
            ListaLiFotosAnexas.add(listLiFotosAnexas);

            Button bFotosAnexasSuelos = new Button(mcont);
            bFotosAnexasSuelos.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bFotosAnexasSuelos.setText("Añadir Foto");
            bFotosAnexasSuelos.setTag(idLinear);
            bFotosAnexasSuelos.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.plus_circle, 0);
            bFotosAnexasSuelos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listFotosAnexaSame = new ArrayList<Uri>();
                    listNombresFotosSame = new ArrayList<String>();
                    listFotosUGSR.add(listFotosAnexaSame);
                    ListaNombresFotosSame.add(listNombresFotosSame);
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
                            etGenerico.setTag(tagElemento+aux);
                            ListaEditText.get(Integer.parseInt(v.getTag().toString())).add(etGenerico);
                            liFotosAnexas.addView(etGenerico);

                            if (tagElemento.equals("NombreFotosAnexas")){
                                listEtFotosAnexas.add(etGenerico);
                                ListaEtFotosAnexas.set(Integer.parseInt(v.getTag().toString()), listEtFotosAnexas);
                            }
                        }
                        if (claseElemento.equals("button")){
                            HorizontalScrollView hScrollView = new HorizontalScrollView(mcont);
                            hScrollView.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                            LinearLayout liFormFotosAnexas = new LinearLayout(mcont);
                            liFormFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liFormFotosAnexas.setOrientation(LinearLayout.HORIZONTAL);
                            listLiFotosAnexas.add(liFormFotosAnexas);
                            hScrollView.addView(liFormFotosAnexas);
                            liFotosAnexas.addView(hScrollView);

                            ListaLiFotosAnexas.set(Integer.parseInt(v.getTag().toString()), listLiFotosAnexas);
                            ListaUriFotosAnexas.set(Integer.parseInt(v.getTag().toString()), listFotosUGSR);
                            ListaNombresFotosFormatos.set(Integer.parseInt(v.getTag().toString()), ListaNombresFotosSame);


                            Button tvGenerico = new Button(mcont);
                            tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            tvGenerico.setText(nombreElemento);
                            int auxIDFOTO =listContFotosAnexas.get(Integer.parseInt(v.getTag().toString()))-1;
                            tvGenerico.setTag("Tag-"+idLinear+"-"+auxIDFOTO);
                            tvGenerico.setCompoundDrawablesWithIntrinsicBounds(R.drawable.plus_circle, 0, 0, 0);
                            tvGenerico.setTextAppearance(R.style.BtnAgregarFotos);
                            liFotosAnexas.addView(tvGenerico);

                            tvGenerico.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View vi) {
                                    CargarImagen("UGSR");
                                    actualLiFotos = vi.getTag().toString();
                                    Log.d("TAG", "onClick: "+actualLiFotos);
                                }
                            });
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
                        etSecuenciaEstratiOpt.setTag(tagElemento+aux+"orden");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt);
                        liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt);

                        EditText etSecuenciaEstratiOpt1Espesor = new EditText(mcont);
                        etSecuenciaEstratiOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiOpt1Espesor.setHint("Espesor (m)");
                        etSecuenciaEstratiOpt1Espesor.setInputType(InputType.TYPE_CLASS_NUMBER);
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
                                            etSecuenciaEstratiSueloROrden.setTag(tagElemento+ aux +"orden");
                                            ListaEditText.get(idLinear).add(etSecuenciaEstratiSueloROrden);
                                            liFormSecuenciaEstratiSueloR1.addView(etSecuenciaEstratiSueloROrden);

                                            EditText etSecuenciaEstratiSueloREspesor = new EditText(mcont);
                                            etSecuenciaEstratiSueloREspesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            etSecuenciaEstratiSueloREspesor.setHint("Espesor (m)");
                                            etSecuenciaEstratiSueloREspesor.setInputType(InputType.TYPE_CLASS_NUMBER);
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
                        etLitologia.setInputType(InputType.TYPE_CLASS_NUMBER);
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
                        etSecuenciaEstratiOpt.setTag(tag+2);
                        etSecuenciaEstratiOpt.setInputType(InputType.TYPE_CLASS_NUMBER);
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt);
                        liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt);

                        EditText etSecuenciaEstratiOpt1Espesor = new EditText(mcont);
                        etSecuenciaEstratiOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiOpt1Espesor.setTag(tag+1);
                        etSecuenciaEstratiOpt1Espesor.setInputType(InputType.TYPE_CLASS_NUMBER);
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
//                        nuevoRadio.setClickable(false);
//                        nuevoRadio.setEnabled(false);
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
                            //ListaRadioBtn1Finos.get(idLinear).add(nuevoRadio);
                        }
                        if (auxGruesos){
                            //ListaRadioBtn1Gruesos.get(idLinear).add(nuevoRadio);
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

//                    if (nombreElemento.equals("Granulometría de la Matriz")){
//
//                        for (int j = 0; j < ListaRadioBtn1Gruesos.get(idLinear).size() ; j++){
//                            ListaRadioBtn1Gruesos.get(idLinear).get(j).setEnabled(false);
//                            ListaRadioBtn1Gruesos.get(idLinear).get(j).setClickable(false);
//                        }
//                        radioGroup1.setTag(tagElemento+1+"_"+idLinear);
//                        radioGroup2.setTag(tagElemento+2+"_"+idLinear);
//
//                        radioGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                            public void onCheckedChanged(RadioGroup group, int checkedId)
//                            {
//                                // This will get the radiobutton that has changed in its check state
//                                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
//                                // This puts the value (true/false) into the variable
//                                boolean isChecked = checkedRadioButton.isChecked();
//                                // If the radiobutton that has changed in check state is now checked...
//                                int aux = Integer.parseInt(group.getTag().toString().split("_")[1]);
//                                if (checkedRadioButton.getText().equals("Finos (Limos-Arcillas Menores de 0,075 mm)"))
//                                {
//                                    for (int j = 0; j < ListaRadioBtn1Finos.get(aux).size() ; j++){
//                                        ListaRadioBtn1Finos.get(aux).get(j).setEnabled(true);
//                                        ListaRadioBtn1Finos.get(aux).get(j).setClickable(true);
//                                    }
//                                    for (int j = 0; j < ListaRadioBtn1Gruesos.get(aux).size() ; j++){
//                                        ListaRadioBtn1Gruesos.get(aux).get(j).setEnabled(false);
//                                        ListaRadioBtn1Gruesos.get(aux).get(j).setClickable(false);
//                                    }
//                                }
//                                else {
//                                    for (int j = 0; j < ListaRadioBtn1Finos.get(aux).size() ; j++){
//                                        ListaRadioBtn1Finos.get(aux).get(j).setEnabled(false);
//                                        ListaRadioBtn1Finos.get(aux).get(j).setClickable(false);
//                                    }
//                                    for (int j = 0; j < ListaRadioBtn1Gruesos.get(aux).size() ; j++){
//                                        ListaRadioBtn1Gruesos.get(aux).get(j).setEnabled(true);
//                                        ListaRadioBtn1Gruesos.get(aux).get(j).setClickable(true);
//                                    }
//                                }
//                            }
//                        });
//                        radioGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//                            public void onCheckedChanged(RadioGroup group, int checkedId)
//                            {
//                                // This will get the radiobutton that has changed in its check state
//                                RadioButton checkedRadioButton = (RadioButton)group.findViewById(checkedId);
//                                // This puts the value (true/false) into the variable
//                                boolean isChecked = checkedRadioButton.isChecked();
//                                // If the radiobutton that has changed in check state is now checked...
//
//                                int aux = Integer.parseInt(group.getTag().toString().split("_")[1]);
//
//                                if (checkedId == 71)
//                                {
//                                    for (int j = 0; j < ListaRadioBtn2Finos.get(aux).size() ; j++){
//                                        ListaRadioBtn2Finos.get(aux).get(j).setEnabled(true);
//                                        ListaRadioBtn2Finos.get(aux).get(j).setClickable(true);
//                                    }
//                                    for (int j = 0; j < ListaRadioBtn2Gruesos.get(aux).size() ; j++){
//                                        ListaRadioBtn2Gruesos.get(aux).get(j).setEnabled(false);
//                                        ListaRadioBtn2Gruesos.get(aux).get(j).setClickable(false);
//                                    }
//                                }
//                                else {
//                                    for (int j = 0; j < ListaRadioBtn2Finos.get(aux).size() ; j++){
//                                        ListaRadioBtn2Finos.get(aux).get(j).setEnabled(false);
//                                        ListaRadioBtn2Finos.get(aux).get(j).setClickable(false);
//                                    }
//                                    for (int j = 0; j < ListaRadioBtn2Gruesos.get(aux).size() ; j++){
//                                        ListaRadioBtn2Gruesos.get(aux).get(j).setEnabled(true);
//                                        ListaRadioBtn2Gruesos.get(aux).get(j).setClickable(true);
//                                    }
//                                }
//                            }
//                        });
//
//                    }

//                    if (nombreElemento.equals("Compacidad de la Matriz")) {
//
//                        for (int j = 0; j < ListaRadioBtn1Gruesos.get(idLinear).size(); j++) {
//                            ListaRadioBtn1Gruesos.get(idLinear).get(j).setEnabled(false);
//                            ListaRadioBtn1Gruesos.get(idLinear).get(j).setClickable(false);
//                        }
//                    }

                }
                if (claseElemento.equals("multitext")){
                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloFormato);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                    LinearLayout liradiobtnTitulo = new LinearLayout(mcont);
                    liradiobtnTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liradiobtnTitulo.setOrientation(LinearLayout.HORIZONTAL);

                    TextView pruebatext = new TextView(mcont);
                    pruebatext.setLayoutParams(new ActionBar.LayoutParams(150, ViewGroup.LayoutParams.WRAP_CONTENT));
                    pruebatext.setText("2");
                    pruebatext.setTextAppearance(R.style.TituloItem);
                    pruebatext.setPadding(70, 0, 0, 0);
                    liradiobtnTitulo.addView(pruebatext);

                    TextView pruebatext1 = new TextView(mcont);
                    pruebatext1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    pruebatext1.setText("1");
                    pruebatext1.setTextAppearance(R.style.TituloItem);
                    pruebatext1.setPadding(50, 0, 0, 0);
                    liradiobtnTitulo.addView(pruebatext1);

                    liForm.addView(liradiobtnTitulo);

                    Resources res = getResources();
                    String[] opciones = res.getStringArray(idStringArrayElemento);
                    int secuEstratiWidth = 420;
                    int secuEstratiOrdenWidth = 150;
                    int secuEstratiEspesorWidth = 150;

                    for (int j = 0; j < opciones.length ; j++) {

                        LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                        liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                        EditText etSecuenciaEstratiOpt = new EditText(mcont);
                        etSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(secuEstratiOrdenWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiOpt.setHint("2");
                        etSecuenciaEstratiOpt.setInputType(InputType.TYPE_CLASS_NUMBER);
                        etSecuenciaEstratiOpt.setTag(tagElemento+j+"_2");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt);
                        liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt);

                        EditText etSecuenciaEstratiOpt1Espesor = new EditText(mcont);
                        etSecuenciaEstratiOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiOpt1Espesor.setHint("1");
                        etSecuenciaEstratiOpt1Espesor.setTag(tagElemento+j+"_1");
                        etSecuenciaEstratiOpt1Espesor.setInputType(InputType.TYPE_CLASS_NUMBER);
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt1Espesor);
                        liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt1Espesor);

                        TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                        tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvSecuenciaEstratiOpt.setText(opciones[j]);
                        tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                        liFormSecuenciaEstrati.addView(tvSecuenciaEstratiOpt);

                        liForm.addView(liFormSecuenciaEstrati);

                    }
                }
                if (claseElemento.equals("radiocheck")){
                    Resources res = getResources();
                    String[] opciones = res.getStringArray(idStringArrayElemento);

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
                    pruebatext.setPadding(30, 10, 0, 0);
                    liradiobtnTitulo.addView(pruebatext);

                    TextView pruebatext1 = new TextView(mcont);
                    pruebatext1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    pruebatext1.setText("1");
                    pruebatext1.setTextAppearance(R.style.TituloItem);
                    pruebatext1.setPadding(30, 10, 0, 0);
                    liradiobtnTitulo.addView(pruebatext1);

                    liForm.addView(liradiobtnTitulo);

                    for (int j = 0; j < opciones.length ; j++) {

                        LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                        liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                        CheckBox checkbox = new CheckBox(mcont);
                        checkbox.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        String aux = opciones[j];
//                        checkbox.setText(aux);
                        checkbox.setTag(tagElemento+j+"check_2");
                        ListaCheckBox.get(idLinear).add(checkbox);
                        liFormSecuenciaEstrati.addView(checkbox);

                        CheckBox checkbox1 = new CheckBox(mcont);
                        checkbox1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                        String aux = opciones[j];
                        checkbox1.setText(aux);
                        checkbox1.setTag(tagElemento+j+"check_1");
                        ListaCheckBox.get(idLinear).add(checkbox1);
                        liFormSecuenciaEstrati.addView(checkbox1);

                        liForm.addView(liFormSecuenciaEstrati);

                        if (j == 0){
                            checkbox.setChecked(true);
                            checkbox1.setChecked(true);
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

            ListaEtFotosAnexas.add(listEtFotosAnexas);
            ListaLiFotosAnexas.add(listLiFotosAnexas);

            Button bFotosAnexasSuelos = new Button(mcont);
            bFotosAnexasSuelos.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bFotosAnexasSuelos.setText("Añadir Foto");
            bFotosAnexasSuelos.setTag(idLinear);
            bFotosAnexasSuelos.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.plus_circle, 0);
            bFotosAnexasSuelos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listFotosAnexaSame = new ArrayList<Uri>();
                    listNombresFotosSame = new ArrayList<String>();
                    listFotosUGSS.add(listFotosAnexaSame);
                    ListaNombresFotosSame.add(listNombresFotosSame);
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
                            etGenerico.setTag(tagElemento+aux);
                            ListaEditText.get(Integer.parseInt(v.getTag().toString())).add(etGenerico);
                            liFotosAnexas.addView(etGenerico);

                            if (tagElemento.equals("NombreFotosAnexas")){
                                listEtFotosAnexas.add(etGenerico);
                                ListaEtFotosAnexas.set(Integer.parseInt(v.getTag().toString()), listEtFotosAnexas);
                            }
                        }
                        if (claseElemento.equals("button")){
                            HorizontalScrollView hScrollView = new HorizontalScrollView(mcont);
                            hScrollView.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                            LinearLayout liFormFotosAnexas = new LinearLayout(mcont);
                            liFormFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liFormFotosAnexas.setOrientation(LinearLayout.HORIZONTAL);
                            listLiFotosAnexas.add(liFormFotosAnexas);
                            hScrollView.addView(liFormFotosAnexas);
                            liFotosAnexas.addView(hScrollView);

                            ListaLiFotosAnexas.set(Integer.parseInt(v.getTag().toString()), listLiFotosAnexas);
                            ListaUriFotosAnexas.set(Integer.parseInt(v.getTag().toString()), listFotosUGSS);
                            ListaNombresFotosFormatos.set(Integer.parseInt(v.getTag().toString()), ListaNombresFotosSame);


                            Button tvGenerico = new Button(mcont);
                            tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            tvGenerico.setText(nombreElemento);
                            int auxIDFOTO =listContFotosAnexas.get(Integer.parseInt(v.getTag().toString()))-1;
                            tvGenerico.setTag("Tag-"+idLinear+"-"+auxIDFOTO);
                            tvGenerico.setCompoundDrawablesWithIntrinsicBounds(R.drawable.plus_circle, 0, 0, 0);
                            tvGenerico.setTextAppearance(R.style.BtnAgregarFotos);
                            liFotosAnexas.addView(tvGenerico);

                            tvGenerico.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View vi) {
                                    CargarImagen("UGSS");
                                    actualLiFotos = vi.getTag().toString();
                                    Log.d("TAG", "onClick: "+actualLiFotos);
                                }
                            });
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
                                etGenerico.setTag(tagElemento+"otro"+aux);
                                ListaEditText.get(idLinear).add(etGenerico);
                                liDiscontinuidades.addView(etGenerico);
                            }
                        }
                        if (claseElemento.equals("ambientes")){
                            Resources res = getResources();
                            String[] opciones = res.getStringArray(idStringArrayElemento);

                            TextView tvGenerico = new TextView(mcont);
                            tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            tvGenerico.setText(nombreElemento);
                            tvGenerico.setTextAppearance(R.style.TituloItem);
                            tvGenerico.setPadding(0, mtop, 0, 0);
                            liDiscontinuidades.addView(tvGenerico);

                            for (int j = 0; j < opciones.length ; j++) {

                                CheckBox checkbox = new CheckBox(mcont);
                                checkbox.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                String aux1 = opciones[j];
                                checkbox.setText(aux1);
                                checkbox.setTag(tagElemento+j+"check"+aux);
                                ListaCheckBox.get(idLinear).add(checkbox);
                                liDiscontinuidades.addView(checkbox);

                                if (j == 0){
                                    checkbox.setChecked(true);
                                }
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

            ListaEtFotosAnexas.add(listEtFotosAnexas);
            ListaLiFotosAnexas.add(listLiFotosAnexas);

            Button bFotosAnexasSuelos = new Button(mcont);
            bFotosAnexasSuelos.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bFotosAnexasSuelos.setText("Añadir Foto");
            bFotosAnexasSuelos.setTag(idLinear);
            bFotosAnexasSuelos.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.plus_circle, 0);
            bFotosAnexasSuelos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listFotosAnexaSame = new ArrayList<Uri>();
                    listNombresFotosSame = new ArrayList<String>();
                    listFotosSGMF.add(listFotosAnexaSame);
                    ListaNombresFotosSame.add(listNombresFotosSame);
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
                            etGenerico.setTag(tagElemento+aux);
                            ListaEditText.get(Integer.parseInt(v.getTag().toString())).add(etGenerico);
                            liFotosAnexas.addView(etGenerico);

                            if (tagElemento.equals("NombreFotosAnexas")){
                                listEtFotosAnexas.add(etGenerico);
                                ListaEtFotosAnexas.set(Integer.parseInt(v.getTag().toString()), listEtFotosAnexas);
                            }
                        }
                        if (claseElemento.equals("button")){
                            HorizontalScrollView hScrollView = new HorizontalScrollView(mcont);
                            hScrollView.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                            LinearLayout liFormFotosAnexas = new LinearLayout(mcont);
                            liFormFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liFormFotosAnexas.setOrientation(LinearLayout.HORIZONTAL);
                            listLiFotosAnexas.add(liFormFotosAnexas);
                            hScrollView.addView(liFormFotosAnexas);
                            liFotosAnexas.addView(hScrollView);

                            ListaLiFotosAnexas.set(Integer.parseInt(v.getTag().toString()), listLiFotosAnexas);
                            ListaUriFotosAnexas.set(Integer.parseInt(v.getTag().toString()), listFotosSGMF);
                            ListaNombresFotosFormatos.set(Integer.parseInt(v.getTag().toString()), ListaNombresFotosSame);


                            Button tvGenerico = new Button(mcont);
                            tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            tvGenerico.setText(nombreElemento);
                            int auxIDFOTO =listContFotosAnexas.get(Integer.parseInt(v.getTag().toString()))-1;
                            tvGenerico.setTag("Tag-"+idLinear+"-"+auxIDFOTO);
                            tvGenerico.setCompoundDrawablesWithIntrinsicBounds(R.drawable.plus_circle, 0, 0, 0);
                            tvGenerico.setTextAppearance(R.style.BtnAgregarFotos);
                            liFotosAnexas.addView(tvGenerico);

                            tvGenerico.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View vi) {
                                    CargarImagen("SGMF");
                                    actualLiFotos = vi.getTag().toString();
                                    Log.d("TAG", "onClick: "+actualLiFotos);
                                }
                            });
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
                    etGenerico.setTag(tagElemento);

                    if (tagElemento.equals("ID_PARTE") && !auxMM){
                        tvGenerico.setText("ID asigando al MM");
                        etGenerico.setHint("ID asignado al MM ");
                    }

                    if (auxMM) {
                        try {
                            etGenerico.setText(properties.getString(tagElemento));
                            Log.d("pruebis", "EditTextOPT: " + properties.getString(tagElemento));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                    if (auxMM) {
                        try {
                            opc = properties.getString(tagElemento).toString();
                            Log.d("pruebis", "SpinnerOPT: " + opc);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                    if (auxMM) {
                        try {
                            opc = properties.getString(tagElemento + 2).toString();
                            Log.d("pruebis", "SpinnerOPT: " + opc);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                    if (auxMM) {
                        try {
                            opc1 = properties.getString(tagElemento + 1);
                            indexSpinner1 = Integer.parseInt(opc1);
                            Log.d("pruebis", "RadioBtnOPT: " + opc1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

                    listContDANOS.set(Integer.parseInt(v.getTag().toString()), listContDANOS.get(Integer.parseInt(v.getTag().toString())) + 1);

                    Button bDiscont = new Button(mcont);
                    bDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    bDiscont.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                    bDiscont.setText("DAÑOS "+ listContDANOS.get(Integer.parseInt(v.getTag().toString())));
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
                    String nuevo = "DAÑOS "+ listContDANOS.get(Integer.parseInt(v.getTag().toString()));
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
                                etGenerico.setTag(tagElemento+"otro"+aux);
                                ListaEditText.get(idLinear).add(etGenerico);
                                liDiscontinuidades.addView(etGenerico);
                            }
                        }
                    }

                }
            });
            liForm.addView(bAnadirDiscont);

            ListaEtFotosAnexas.add(listEtFotosAnexas);
            ListaLiFotosAnexas.add(listLiFotosAnexas);

            listLiForm.add(liForm);
            liFormularios.addView(liForm);
        }

        if (formType.equals("Inventario MM")) {
            Button bAcordion = new Button(mcont);
            bAcordion.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bAcordion.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_up, 0);
            bAcordion.setText("Formato Inventario de Movimiento en Masa: "+idparte);
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
            tvTitulo.setText("Formato Inventario de Movimiento en Masa: "+idparte);
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

            for (int i = 0; i < listaElementosINV.size(); i++) {
                ElementoFormato elementoActual = listaElementosINV.get(i);
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
                    etGenerico.setTag(tagElemento);

                    if (tagElemento.equals("ID_PARTE") && !auxMM){
                        tvGenerico.setText("ID asigando al MM");
                        etGenerico.setHint("ID asignado al MM ");
                    }

                    if (auxMM) {
                        try {
                            etGenerico.setText(properties.getString(tagElemento));
                            Log.d("pruebis", "EditTextOPT: " + properties.getString(tagElemento));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                    if (auxMM) {
                        try {
                            opc = properties.getString(tagElemento).toString();
                            Log.d("pruebis", "SpinnerOPT: " + opc);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                    if (auxMM) {
                        try {
                            opc = properties.getString(tagElemento + 2).toString();
                            Log.d("pruebis", "SpinnerOPT: " + opc);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                    if (auxMM) {
                        try {
                            opc1 = properties.getString(tagElemento + 1);
                            indexSpinner1 = Integer.parseInt(opc1);
                            Log.d("pruebis", "RadioBtnOPT: " + opc1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                if (claseElemento.equals("estructuras")){
                    Resources res = getResources();
                    String[] opciones = res.getStringArray(idStringArrayElemento);

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
                    pruebatext.setLayoutParams(new ActionBar.LayoutParams(370, ViewGroup.LayoutParams.WRAP_CONTENT));
                    pruebatext.setText("Planos de");
                    pruebatext.setTextAppearance(R.style.TituloItem);
                    pruebatext.setPadding(30, 10, 0, 0);
                    liradiobtnTitulo.addView(pruebatext);

                    TextView pruebatext1 = new TextView(mcont);
                    pruebatext1.setLayoutParams(new ActionBar.LayoutParams(150, ViewGroup.LayoutParams.WRAP_CONTENT));
                    pruebatext1.setText("DirBuz");
                    pruebatext1.setTextAppearance(R.style.TituloItem);
                    pruebatext1.setPadding(30, 10, 0, 0);
                    liradiobtnTitulo.addView(pruebatext1);

                    TextView pruebatext2 = new TextView(mcont);
                    pruebatext2.setLayoutParams(new ActionBar.LayoutParams(150, ViewGroup.LayoutParams.WRAP_CONTENT));
                    pruebatext2.setText("Buz");
                    pruebatext2.setTextAppearance(R.style.TituloItem);
                    pruebatext2.setPadding(30, 10, 0, 0);
                    liradiobtnTitulo.addView(pruebatext2);

                    TextView pruebatext3 = new TextView(mcont);
                    pruebatext3.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    pruebatext3.setText("Espaciamiento (m)");
                    pruebatext3.setTextAppearance(R.style.TituloItem);
                    pruebatext3.setPadding(30, 10, 0, 0);
                    liradiobtnTitulo.addView(pruebatext3);

                    liForm.addView(liradiobtnTitulo);

                    for (int j = 0; j < opciones.length ; j++) {
                        String aux = opciones[j];

                        LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                        liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                        TextView pruebatext4 = new TextView(mcont);
                        pruebatext4.setLayoutParams(new ActionBar.LayoutParams(300, ViewGroup.LayoutParams.WRAP_CONTENT));
                        pruebatext4.setText(aux);
                        pruebatext4.setTextAppearance(R.style.TituloItem);
                        pruebatext4.setPadding(0, 10, 0, 0);
                        liFormSecuenciaEstrati.addView(pruebatext4);

                        CheckBox checkbox = new CheckBox(mcont);
                        checkbox.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                        checkbox.setText(aux);
                        checkbox.setTag(tagElemento+j+"check");
                        ListaCheckBox.get(idLinear).add(checkbox);
                        liFormSecuenciaEstrati.addView(checkbox);

                        EditText etSecuenciaEstratiOpt = new EditText(mcont);
                        etSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(150, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiOpt.setHint("DB");
                        etSecuenciaEstratiOpt.setInputType(InputType.TYPE_CLASS_NUMBER);
                        etSecuenciaEstratiOpt.setTag(tagElemento+j+"dirbuz");
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt);
                        liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt);

                        EditText etSecuenciaEstratiOpt1Espesor = new EditText(mcont);
                        etSecuenciaEstratiOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(150, ViewGroup.LayoutParams.WRAP_CONTENT));
                        etSecuenciaEstratiOpt1Espesor.setHint("BZ");
                        etSecuenciaEstratiOpt1Espesor.setTag(tagElemento+j+"buz");
                        etSecuenciaEstratiOpt1Espesor.setInputType(InputType.TYPE_CLASS_NUMBER);
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt1Espesor);
                        liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt1Espesor);

                        Spinner sGenerico = new Spinner(mcont);
                        sGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mcont, R.array.EstructurasEspaciamientoMM, android.R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        sGenerico.setAdapter(adapter);
                        sGenerico.setTag(tagElemento+j+"espaciamiento");
                        ListaSpinner.get(idLinear).add(sGenerico);
                        liFormSecuenciaEstrati.addView(sGenerico);


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
//                        nuevoRadio.setClickable(false);
//                        nuevoRadio.setEnabled(false);
                        radioGroup2.addView(nuevoRadio);
                    }



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

                    RadioButton primerRadio2 = (RadioButton) radioGroup2.getChildAt(0);
                    primerRadio2.setChecked(true);
                    liradiobtn.addView(radioGroup2);
                    ListaRadioGrp.get(idLinear).add(radioGroup2);

                    RadioButton primerRadio1 = (RadioButton) radioGroup1.getChildAt(0);
                    primerRadio1.setChecked(true);
                    liradiobtn.addView(radioGroup1);
                    ListaRadioGrp.get(idLinear).add(radioGroup1);

                    liForm.addView(liradiobtn);

                }
                if (claseElemento.equals("radiocheck")){
                    Resources res = getResources();
                    String[] opciones = res.getStringArray(idStringArrayElemento);

                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloItem);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                    LinearLayout liradiobtnTitulo = new LinearLayout(mcont);
                    liradiobtnTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liradiobtnTitulo.setOrientation(LinearLayout.HORIZONTAL);


                    liForm.addView(liradiobtnTitulo);

                    for (int j = 0; j < opciones.length ; j++) {

                        LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                        liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                        String aux = opciones[j];

                        CheckBox checkbox1 = new CheckBox(mcont);
                        checkbox1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                        String aux = opciones[j];
                        checkbox1.setText(aux);
                        checkbox1.setTag(tagElemento+j+"check");
                        ListaCheckBox.get(idLinear).add(checkbox1);
                        liFormSecuenciaEstrati.addView(checkbox1);

                        liForm.addView(liFormSecuenciaEstrati);

                    }


                }
                if (claseElemento.equals("radiocheckMM")){
                    Resources res = getResources();
                    String[] opciones = res.getStringArray(idStringArrayElemento);

                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloItem);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                    LinearLayout liradiobtnTitulo = new LinearLayout(mcont);
                    liradiobtnTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liradiobtnTitulo.setOrientation(LinearLayout.HORIZONTAL);

                    LinearLayout liradiobtnTitulo3 = new LinearLayout(mcont);
                    liradiobtnTitulo3.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liradiobtnTitulo3.setOrientation(LinearLayout.VERTICAL);

                    LinearLayout liradiobtnTitulo2 = new LinearLayout(mcont);
                    liradiobtnTitulo2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liradiobtnTitulo2.setOrientation(LinearLayout.VERTICAL);

                    String text2 = "2";
                    String text1 = "1";

                    if (nombreElemento.equals("CONTRIBUYENTES - DETONANTES")){
                        text2 = "C";
                        text1 = "D";
                    }


                    TextView pruebatext = new TextView(mcont);
                    pruebatext.setLayoutParams(new ActionBar.LayoutParams(100, ViewGroup.LayoutParams.WRAP_CONTENT));
                    pruebatext.setText(text2);
                    pruebatext.setTextAppearance(R.style.TituloItem);
                    pruebatext.setPadding(30, 10, 0, 0);
                    liradiobtnTitulo.addView(pruebatext);

                    TextView pruebatext1 = new TextView(mcont);
                    pruebatext1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    pruebatext1.setText(text1);
                    pruebatext1.setTextAppearance(R.style.TituloItem);
                    pruebatext1.setPadding(30, 10, 0, 0);
                    liradiobtnTitulo.addView(pruebatext1);

                    liForm.addView(liradiobtnTitulo);

                    for (int j = 0; j < opciones.length ; j++) {

                        LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                        liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                        String aux = opciones[j];

                        CheckBox checkbox2 = new CheckBox(mcont);
                        checkbox2.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                        checkbox2.setText(aux);
                        checkbox2.setTag(tagElemento+j+"check_2");
                        ListaCheckBox.get(idLinear).add(checkbox2);
                        liFormSecuenciaEstrati.addView(checkbox2);


                        CheckBox checkbox1 = new CheckBox(mcont);
                        checkbox1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                        checkbox1.setText(aux);
                        checkbox1.setTag(tagElemento+j+"check_1");
                        ListaCheckBox.get(idLinear).add(checkbox1);
                        liFormSecuenciaEstrati.addView(checkbox1);

                        TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                        tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvSecuenciaEstratiOpt.setText(aux);
                        tvSecuenciaEstratiOpt.setPadding(5, 0, 0, 0);
                        tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                        liFormSecuenciaEstrati.addView(tvSecuenciaEstratiOpt);

                        liForm.addView(liFormSecuenciaEstrati);

                        if (j == 1 && nombreElemento.equals("CONTRIBUYENTES - DETONANTES")){
                            checkbox1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if(((CheckBox) view).isChecked()){
                                        sismo1 = true;
                                        liradiobtnTitulo3.removeAllViews();
                                        TextView tvGenerico = new TextView(mcont);
                                        tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        tvGenerico.setText("Sismo");
                                        tvGenerico.setTextAppearance(R.style.TituloItem);
                                        tvGenerico.setPadding(0, mtop, 0, 0);
                                        liradiobtnTitulo3.addView(tvGenerico);

                                        String[] opciones4 = res.getStringArray(R.array.SismoMM);
                                        for (int j1 = 0; j1 < opciones4.length ; j1++) {
                                            String aux1 = opciones4[j1];

                                            TextView tvGenerico1 = new TextView(mcont);
                                            tvGenerico1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            tvGenerico1.setText(aux1);
                                            tvGenerico1.setTextAppearance(R.style.TituloItem);
                                            tvGenerico1.setPadding(0, mtop, 0, 0);
                                            liradiobtnTitulo3.addView(tvGenerico1);

                                            EditText etGenerico = new EditText(mcont);
                                            etGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            etGenerico.setHint(aux1);
                                            etGenerico.setTag("sismoMM"+j1);
                                            ListaEditText.get(idLinear).add(etGenerico);
                                            liradiobtnTitulo3.addView(etGenerico);
                                        }

                                    }
                                    else {
                                        sismo1 = false;
                                        if(!sismo2){
                                            liradiobtnTitulo3.removeAllViews();
                                        }
                                    }
                                }
                            });
                            checkbox2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if(((CheckBox) view).isChecked()){
                                        sismo2 = true;
                                        liradiobtnTitulo3.removeAllViews();

                                        TextView tvGenerico = new TextView(mcont);
                                        tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        tvGenerico.setText("Sismo");
                                        tvGenerico.setTextAppearance(R.style.TituloItem);
                                        tvGenerico.setPadding(0, mtop, 0, 0);
                                        liradiobtnTitulo3.addView(tvGenerico);

                                        String[] opciones4 = res.getStringArray(R.array.SismoMM);
                                        for (int j1 = 0; j1 < opciones4.length ; j1++) {
                                            String aux1 = opciones4[j1];

                                            TextView tvGenerico1 = new TextView(mcont);
                                            tvGenerico1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            tvGenerico1.setText(aux1);
                                            tvGenerico1.setTextAppearance(R.style.TituloItem);
                                            tvGenerico1.setPadding(0, mtop, 0, 0);
                                            liradiobtnTitulo3.addView(tvGenerico1);

                                            EditText etGenerico = new EditText(mcont);
                                            etGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            etGenerico.setHint(aux1);
                                            etGenerico.setTag("sismoMM"+j1);
                                            ListaEditText.get(idLinear).add(etGenerico);
                                            liradiobtnTitulo3.addView(etGenerico);
                                        }

                                    }
                                    else {
                                        sismo2 = false;
                                        if(!sismo1) {
                                            liradiobtnTitulo3.removeAllViews();
                                        }
                                    }
                                }
                            });
                        }
                        if (j == 3 && nombreElemento.equals("CONTRIBUYENTES - DETONANTES")){
                            checkbox1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if(((CheckBox) view).isChecked()){
                                        lluvias1 = true;
                                        liradiobtnTitulo2.removeAllViews();
                                        TextView tvGenerico = new TextView(mcont);
                                        tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        tvGenerico.setText("Lluvias");
                                        tvGenerico.setTextAppearance(R.style.TituloItem);
                                        tvGenerico.setPadding(0, mtop, 0, 0);
                                        liradiobtnTitulo2.addView(tvGenerico);

                                        String[] opciones4 = res.getStringArray(R.array.LluviasMM);
                                        for (int j1 = 0; j1 < opciones4.length ; j1++) {
                                            String aux1 = opciones4[j1];

                                            TextView tvGenerico1 = new TextView(mcont);
                                            tvGenerico1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            tvGenerico1.setText(aux1);
                                            tvGenerico1.setTextAppearance(R.style.TituloItem);
                                            tvGenerico1.setPadding(0, mtop, 0, 0);
                                            liradiobtnTitulo2.addView(tvGenerico1);

                                            EditText etGenerico = new EditText(mcont);
                                            etGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            etGenerico.setHint(aux1);
                                            etGenerico.setTag("lluviasMM"+j1);
                                            ListaEditText.get(idLinear).add(etGenerico);
                                            liradiobtnTitulo2.addView(etGenerico);
                                        }

                                    }
                                    else {
                                        lluvias1 = false;
                                        if (!lluvias2){
                                            liradiobtnTitulo2.removeAllViews();
                                        }
                                    }
                                }
                            });
                            checkbox2.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if(((CheckBox) view).isChecked()){
                                        lluvias2 = true;
                                        liradiobtnTitulo2.removeAllViews();

                                        TextView tvGenerico = new TextView(mcont);
                                        tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        tvGenerico.setText("Lluvias");
                                        tvGenerico.setTextAppearance(R.style.TituloItem);
                                        tvGenerico.setPadding(0, mtop, 0, 0);
                                        liradiobtnTitulo2.addView(tvGenerico);

                                        String[] opciones4 = res.getStringArray(R.array.LluviasMM);
                                        for (int j1 = 0; j1 < opciones4.length ; j1++) {
                                            String aux1 = opciones4[j1];

                                            TextView tvGenerico1 = new TextView(mcont);
                                            tvGenerico1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            tvGenerico1.setText(aux1);
                                            tvGenerico1.setTextAppearance(R.style.TituloItem);
                                            tvGenerico1.setPadding(0, mtop, 0, 0);
                                            liradiobtnTitulo2.addView(tvGenerico1);

                                            EditText etGenerico = new EditText(mcont);
                                            etGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                            etGenerico.setHint(aux1);
                                            etGenerico.setTag("lluviasMM"+j1);
                                            ListaEditText.get(idLinear).add(etGenerico);
                                            liradiobtnTitulo2.addView(etGenerico);
                                        }

                                    }
                                    else {
                                        lluvias2 = false;
                                        if (!lluvias1){
                                            liradiobtnTitulo2.removeAllViews();
                                        }
                                    }
                                }
                            });
                        }


                    }
                        liForm.addView(liradiobtnTitulo3);
                        liForm.addView(liradiobtnTitulo2);


                }
                if (claseElemento.equals("origensuelo")){
                    Resources res = getResources();
                    String[] opciones = res.getStringArray(idStringArrayElemento);

                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloItem);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                    LinearLayout liradiobtnTitulo = new LinearLayout(mcont);
                    liradiobtnTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liradiobtnTitulo.setOrientation(LinearLayout.HORIZONTAL);

                    LinearLayout liradiobtnTitulo1 = new LinearLayout(mcont);
                    liradiobtnTitulo1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liradiobtnTitulo1.setOrientation(LinearLayout.VERTICAL);


                    liForm.addView(liradiobtnTitulo);


                    for (int j = 0; j < opciones.length ; j++) {

                        LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                        liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                        String aux = opciones[j];

                        CheckBox checkbox1 = new CheckBox(mcont);
                        checkbox1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                        String aux = opciones[j];
                        checkbox1.setText(aux);
                        checkbox1.setTag(tagElemento+j+"check");
                        ListaCheckBox.get(idLinear).add(checkbox1);
                        liFormSecuenciaEstrati.addView(checkbox1);

                        liForm.addView(liFormSecuenciaEstrati);

                        if (j == opciones.length-1){
                            checkbox1.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {

                                    if(((CheckBox) view).isChecked()){

                                        TextView tvGenerico = new TextView(mcont);
                                        tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                                        tvGenerico.setText("Tipo Depósito");
                                        tvGenerico.setTextAppearance(R.style.TituloItem);
                                        tvGenerico.setPadding(0, mtop, 0, 0);
                                        liradiobtnTitulo1.addView(tvGenerico);

                                        String[] opciones4 = res.getStringArray(R.array.OrigenSueloSedimentarioMM);
                                        for (int j1 = 0; j1 < opciones4.length ; j1++) {
                                            String aux1 = opciones4[j1];

                                            CheckBox checkbox1 = new CheckBox(mcont);
                                            checkbox1.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                        String aux = opciones[j];
                                            checkbox1.setText(aux1);
                                            checkbox1.setTag("tipodeposito"+j1+"check");
                                            ListaCheckBox.get(idLinear).add(checkbox1);
                                            liradiobtnTitulo1.addView(checkbox1);
                                        }

                                    } else {
                                        liradiobtnTitulo1.removeAllViews();
                                    }
                                }
                            });
                        }

                    }

                    liForm.addView(liradiobtnTitulo1);

                }
                if (claseElemento.equals("multitext")){
                    TextView tvGenerico = new TextView(mcont);
                    tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    tvGenerico.setText(nombreElemento);
                    tvGenerico.setTextAppearance(R.style.TituloFormato);
                    tvGenerico.setPadding(0, mtop, 0, 0);
                    liForm.addView(tvGenerico);

                    LinearLayout liradiobtnTitulo = new LinearLayout(mcont);
                    liradiobtnTitulo.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    liradiobtnTitulo.setOrientation(LinearLayout.HORIZONTAL);


                    liForm.addView(liradiobtnTitulo);

                    Resources res = getResources();
                    String[] opciones = res.getStringArray(idStringArrayElemento);
                    int secuEstratiWidth = 420;
                    int secuEstratiOrdenWidth = 150;
                    int secuEstratiEspesorWidth = 150;

                    for (int j = 0; j < opciones.length ; j++) {

                        LinearLayout liFormSecuenciaEstrati = new LinearLayout(mcont);
                        liFormSecuenciaEstrati.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        liFormSecuenciaEstrati.setOrientation(LinearLayout.HORIZONTAL);

                        TextView tvSecuenciaEstratiOpt = new TextView(mcont);
                        tvSecuenciaEstratiOpt.setLayoutParams(new ActionBar.LayoutParams(700, ViewGroup.LayoutParams.WRAP_CONTENT));
                        tvSecuenciaEstratiOpt.setText(opciones[j]);
                        tvSecuenciaEstratiOpt.setTextAppearance(R.style.TituloItem);
                        liFormSecuenciaEstrati.addView(tvSecuenciaEstratiOpt);

                        EditText etSecuenciaEstratiOpt1Espesor = new EditText(mcont);
                        etSecuenciaEstratiOpt1Espesor.setLayoutParams(new ActionBar.LayoutParams(secuEstratiEspesorWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
//                        etSecuenciaEstratiOpt1Espesor.setHint();
                        etSecuenciaEstratiOpt1Espesor.setTag(tagElemento+j);
                        etSecuenciaEstratiOpt1Espesor.setInputType(InputType.TYPE_CLASS_NUMBER);
                        ListaEditText.get(idLinear).add(etSecuenciaEstratiOpt1Espesor);
                        liFormSecuenciaEstrati.addView(etSecuenciaEstratiOpt1Espesor);


                        liForm.addView(liFormSecuenciaEstrati);

                    }
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

                    listContDANOS.set(Integer.parseInt(v.getTag().toString()), listContDANOS.get(Integer.parseInt(v.getTag().toString())) + 1);

                    Button bDiscont = new Button(mcont);
                    bDiscont.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    bDiscont.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_arrow_down, 0);
                    bDiscont.setText("DAÑOS "+ listContDANOS.get(Integer.parseInt(v.getTag().toString())));
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
                    String nuevo = "DAÑOS "+ listContDANOS.get(Integer.parseInt(v.getTag().toString()));
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
                                etGenerico.setTag(tagElemento+"otro"+aux);
                                ListaEditText.get(idLinear).add(etGenerico);
                                liDiscontinuidades.addView(etGenerico);
                            }
                        }
                    }

                }
            });
            liForm.addView(bAnadirDiscont);

//------------> Fotografías Anexas INV

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

            ListaEtFotosAnexas.add(listEtFotosAnexas);
            ListaLiFotosAnexas.add(listLiFotosAnexas);

            Button bFotosAnexasSuelos = new Button(mcont);
            bFotosAnexasSuelos.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            bFotosAnexasSuelos.setText("Añadir Foto");
            bFotosAnexasSuelos.setTag(idLinear);
            bFotosAnexasSuelos.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.plus_circle, 0);
            bFotosAnexasSuelos.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listFotosAnexaSame = new ArrayList<Uri>();
                    listNombresFotosSame = new ArrayList<String>();
                    listFotosINV.add(listFotosAnexaSame);
                    ListaNombresFotosSame.add(listNombresFotosSame);
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


                    for (int i = 0; i < listaElementosINVFotosAnexas.size(); i++){
                        ElementoFormato elementoActual = listaElementosINVFotosAnexas.get(i);
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
                            etGenerico.setTag(tagElemento+aux);
                            ListaEditText.get(Integer.parseInt(v.getTag().toString())).add(etGenerico);
                            liFotosAnexas.addView(etGenerico);

                            if (tagElemento.equals("nombreFotosAnexasINV")){
                                listEtFotosAnexas.add(etGenerico);
                                ListaEtFotosAnexas.set(Integer.parseInt(v.getTag().toString()), listEtFotosAnexas);
                            }
                        }
                        if (claseElemento.equals("button")){
                            HorizontalScrollView hScrollView = new HorizontalScrollView(mcont);
                            hScrollView.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                            LinearLayout liFormFotosAnexas = new LinearLayout(mcont);
                            liFormFotosAnexas.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            liFormFotosAnexas.setOrientation(LinearLayout.HORIZONTAL);
                            listLiFotosAnexas.add(liFormFotosAnexas);
                            hScrollView.addView(liFormFotosAnexas);
                            liFotosAnexas.addView(hScrollView);

                            ListaLiFotosAnexas.set(Integer.parseInt(v.getTag().toString()), listLiFotosAnexas);
                            ListaUriFotosAnexas.set(Integer.parseInt(v.getTag().toString()), listFotosINV);
                            ListaNombresFotosFormatos.set(Integer.parseInt(v.getTag().toString()), ListaNombresFotosSame);


                            Button tvGenerico = new Button(mcont);
                            tvGenerico.setLayoutParams(new ActionBar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            tvGenerico.setText(nombreElemento);
                            int auxIDFOTO =listContFotosAnexas.get(Integer.parseInt(v.getTag().toString()))-1;
                            tvGenerico.setTag("Tag-"+idLinear+"-"+auxIDFOTO);
                            tvGenerico.setCompoundDrawablesWithIntrinsicBounds(R.drawable.plus_circle, 0, 0, 0);
                            tvGenerico.setTextAppearance(R.style.BtnAgregarFotos);
                            liFotosAnexas.addView(tvGenerico);

                            tvGenerico.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View vi) {
                                    CargarImagen("INV");
                                    actualLiFotos = vi.getTag().toString();
                                    Log.d("TAG", "onClick: "+actualLiFotos);
                                }
                            });
                        }
                    }

                }
            });
            liForm.addView(bFotosAnexasSuelos);


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
                                    String FotosLib = form.getString("FotosLib");
                                    String Observaciones = form.getString("Observaciones");
                                    String Fecha = form.getString("Fecha");
                                    String Propietario = form.getString("Propietario");
                                    String ListFotosGenerales = form.getString("Fotos_Generales");
                                    String ListFotosLib = form.getString("Fotos_Lib");

                                    FormFeature nuevaEstacion = new FormFeature(true, Estacion, TipoEstacion, Este, Norte, Altitud, Fotos, Observaciones, Fecha, Propietario, FotosLib);

                                    databaseReference.child("EstacionesCampo/estacion_"+cont).setValue(nuevaEstacion);


                                    JSONObject Formularios = form.getJSONObject("Formularios");
                                    JSONObject counts = Formularios.getJSONObject("counts");

                                    int contUGS_Rocas = Integer.parseInt(counts.getString("UGS_Rocas"));
                                    int contUGS_Suelos = Integer.parseInt(counts.getString("UGS_Suelos"));
                                    int contSGMF = Integer.parseInt(counts.getString("SGMF"));
                                    int contCAT = Integer.parseInt(counts.getString("CATALOGO"));
                                    int contINV = Integer.parseInt(counts.getString("INVENTARIO"));

                                    databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/count_UGS_Rocas").setValue(contUGS_Rocas);
                                    databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/count_UGS_Suelos").setValue(contUGS_Suelos);
                                    databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/count_SGMF").setValue(contSGMF);
                                    databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/count_CATALOGO").setValue(contCAT);
                                    databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/count_INVENTARIO").setValue(contINV);


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
                                        String FotosAnexasForm = FromatoAux.getString("FotosAnexasForm");
                                        SubirFotos(cont, FotosAnexasForm, "UGSR", j);

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
//                                        String granulometria1 = RadioGrpAux.getString("granulometria1");
//                                        String granulometria2 = RadioGrpAux.getString("granulometria2");
//                                        String forma1 = RadioGrpAux.getString("forma1");
//                                        String forma2 = RadioGrpAux.getString("forma2");
//                                        String redondez1 = RadioGrpAux.getString("redondez1");
//                                        String redondez2 = RadioGrpAux.getString("redondez2");
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
//                                        String granulometriamatriz1 = RadioGrpAux.getString("granulometriamatriz1");
//                                        String granulometriamatriz2 = RadioGrpAux.getString("granulometriamatriz2");
                                        String gradacion1 = RadioGrpAux.getString("gradacion1");
                                        String gradacion2 = RadioGrpAux.getString("gradacion2");
                                        String seleccion1 = RadioGrpAux.getString("seleccion1");
                                        String seleccion2 = RadioGrpAux.getString("seleccion2");
                                        String plasticidad1 = RadioGrpAux.getString("plasticidad1");
                                        String plasticidad2 = RadioGrpAux.getString("plasticidad2");

                                        String resiscorte1 = "";
                                        //String formasuelosgruesos1 = "";
                                        //String redondezsuelosgruesos1 = "";
                                        //String orientacionsuelosgruesos1 = "";
                                        //String dirimbricacionmatriz1 = "";
                                        String compacidadsuelosgruesos1 = "";

                                        resiscorte1 = RadioGrpAux.getString("resiscorte1");
                                        //formasuelosgruesos1 = RadioGrpAux.getString("formasuelosgruesos1");
                                        //redondezsuelosgruesos1 = RadioGrpAux.getString("redondezsuelosgruesos1");
                                        //orientacionsuelosgruesos1 = RadioGrpAux.getString("orientacionsuelosgruesos1");
                                        compacidadsuelosgruesos1 = RadioGrpAux.getString("compacidadsuelosgruesos1");

//                                        String elNuevoTexto2 = orientacionsuelosgruesos1;
//                                        elNuevoTexto2 = elNuevoTexto2.replace(" ","");
//                                        if (elNuevoTexto2.equals("Imbricado")){
//                                            dirimbricacionmatriz1 = EditTextsAux.getString("dirimbricacionmatriz1");
//                                        }


                                        String resiscorte2 = "";
                                        //String formasuelosgruesos2 = "";
                                        //String redondezsuelosgruesos2 = "";
                                        //String orientacionsuelosgruesos2 = "";
                                        //String dirimbricacionmatriz2 = "";
                                        String compacidadsuelosgruesos2 = "";

                                        resiscorte2 = RadioGrpAux.getString("resiscorte2");
                                        //formasuelosgruesos2 = RadioGrpAux.getString("formasuelosgruesos2");
                                        //redondezsuelosgruesos2 = RadioGrpAux.getString("redondezsuelosgruesos2");
                                        //orientacionsuelosgruesos2 = RadioGrpAux.getString("orientacionsuelosgruesos2");
                                        compacidadsuelosgruesos2 = RadioGrpAux.getString("compacidadsuelosgruesos2");

//                                        String elNuevoTexto3 = orientacionsuelosgruesos2;
//                                        elNuevoTexto3 = elNuevoTexto3.replace(" ","");
//                                        if (elNuevoTexto3.equals("Imbricado")){
//                                            dirimbricacionmatriz2 = EditTextsAux.getString("dirimbricacionmatriz2");
//                                        }


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

                                        String forma0check_2 = CheckBoxAux.getString("forma0check_2");
                                        String forma1check_2 = CheckBoxAux.getString("forma1check_2");
                                        String forma2check_2 = CheckBoxAux.getString("forma2check_2");
                                        String forma3check_2 = CheckBoxAux.getString("forma3check_2");
                                        String forma4check_2 = CheckBoxAux.getString("forma4check_2");

                                        String forma0check_1 = CheckBoxAux.getString("forma0check_1");
                                        String forma1check_1 = CheckBoxAux.getString("forma1check_1");
                                        String forma2check_1 = CheckBoxAux.getString("forma2check_1");
                                        String forma3check_1 = CheckBoxAux.getString("forma3check_1");
                                        String forma4check_1 = CheckBoxAux.getString("forma4check_1");

                                        String redondez0check_2 = CheckBoxAux.getString("redondez0check_2");
                                        String redondez1check_2 = CheckBoxAux.getString("redondez1check_2");
                                        String redondez2check_2 = CheckBoxAux.getString("redondez2check_2");
                                        String redondez3check_2 = CheckBoxAux.getString("redondez3check_2");
                                        String redondez4check_2 = CheckBoxAux.getString("redondez4check_2");
                                        String redondez5check_2 = CheckBoxAux.getString("redondez5check_2");

                                        String redondez0check_1 = CheckBoxAux.getString("redondez0check_1");
                                        String redondez1check_1 = CheckBoxAux.getString("redondez1check_1");
                                        String redondez2check_1 = CheckBoxAux.getString("redondez2check_1");
                                        String redondez3check_1 = CheckBoxAux.getString("redondez3check_1");
                                        String redondez4check_1 = CheckBoxAux.getString("redondez4check_1");
                                        String redondez5check_1 = CheckBoxAux.getString("redondez5check_1");



                                        String granulometria0_2 = EditTextsAux.getString("granulometria0_2");
                                        String granulometria1_2 = EditTextsAux.getString("granulometria1_2");
                                        String granulometria2_2 = EditTextsAux.getString("granulometria2_2");
                                        String granulometria3_2 = EditTextsAux.getString("granulometria3_2");
                                        String granulometria4_2 = EditTextsAux.getString("granulometria4_2");
                                        String granulometria5_2 = EditTextsAux.getString("granulometria5_2");

                                        String granulometria0_1 = EditTextsAux.getString("granulometria0_1");
                                        String granulometria1_1 = EditTextsAux.getString("granulometria1_1");
                                        String granulometria2_1 = EditTextsAux.getString("granulometria2_1");
                                        String granulometria3_1 = EditTextsAux.getString("granulometria3_1");
                                        String granulometria4_1 = EditTextsAux.getString("granulometria4_1");
                                        String granulometria5_1 = EditTextsAux.getString("granulometria5_1");

                                        String granulometriamatriz0_2 = EditTextsAux.getString("granulometriamatriz0_2");
                                        String granulometriamatriz1_2 = EditTextsAux.getString("granulometriamatriz1_2");
                                        String granulometriamatriz2_2 = EditTextsAux.getString("granulometriamatriz2_2");
                                        String granulometriamatriz3_2 = EditTextsAux.getString("granulometriamatriz3_2");
                                        String granulometriamatriz4_2 = EditTextsAux.getString("granulometriamatriz4_2");
                                        String granulometriamatriz5_2 = EditTextsAux.getString("granulometriamatriz5_2");
                                        String granulometriamatriz6_2 = EditTextsAux.getString("granulometriamatriz6_2");
                                        String granulometriamatriz7_2 = EditTextsAux.getString("granulometriamatriz7_2");

                                        String granulometriamatriz0_1 = EditTextsAux.getString("granulometriamatriz0_1");
                                        String granulometriamatriz1_1 = EditTextsAux.getString("granulometriamatriz1_1");
                                        String granulometriamatriz2_1 = EditTextsAux.getString("granulometriamatriz2_1");
                                        String granulometriamatriz3_1 = EditTextsAux.getString("granulometriamatriz3_1");
                                        String granulometriamatriz4_1 = EditTextsAux.getString("granulometriamatriz4_1");
                                        String granulometriamatriz5_1 = EditTextsAux.getString("granulometriamatriz5_1");
                                        String granulometriamatriz6_1 = EditTextsAux.getString("granulometriamatriz6_1");
                                        String granulometriamatriz7_1 = EditTextsAux.getString("granulometriamatriz7_1");




                                        FormatUGSSuelos NuevoFormatoUGSSuelos = new FormatUGSSuelos(true, municipios, claseaflor, estructurasoporte1, estructurasoporte2, condicionhumedad1, condicionhumedad2, estructurasrelictas1, estructurasrelictas2, orientacion1, orientacion2, dirimbricacion1, dirimbricacion2, meteorizacionclastos1, meteorizacionclastos2, gradacion1, gradacion2, seleccion1, seleccion2, plasticidad1, plasticidad2, resiscorte1, resiscorte2, compacidadsuelosgruesos1, compacidadsuelosgruesos2, noformato, vereda, noestacion, secuenciaestratiopt1orden, secuenciaestratiopt1espesor, secuenciaestratiopt2orden, secuenciaestratiopt2espesor, secuenciaestratiopt3orden, secuenciaestratiopt3espesor, secuenciaestratisuelor1orden, secuenciaestratisuelor1espesor, secuenciaestratisuelor2orden, secuenciaestratisuelor2espesor, secuenciaestratisuelor3orden, secuenciaestratisuelor3espesor, litologiasasociadasopt1exist, litologiasasociadasopt1espesor, litologiasasociadasopt2exist, litologiasasociadasopt2espesor, nombreugs, porcentajematriz1, porcentajematriz2, porcentajeclastos1, porcentajeclastos2, color1, color2, observacionessuelos, descripcionsuelos, forma0check_2, forma1check_2, forma2check_2, forma3check_2, forma4check_2, forma0check_1, forma1check_1, forma2check_1, forma3check_1, forma4check_1, redondez0check_2, redondez1check_2, redondez2check_2, redondez3check_2, redondez4check_2, redondez5check_2, redondez0check_1, redondez1check_1, redondez2check_1, redondez3check_1, redondez4check_1, redondez5check_1, granulometria0_2, granulometria1_2, granulometria2_2, granulometria3_2, granulometria4_2, granulometria5_2, granulometria0_1, granulometria1_1, granulometria2_1, granulometria3_1, granulometria4_1, granulometria5_1, granulometriamatriz0_2, granulometriamatriz1_2, granulometriamatriz2_2, granulometriamatriz3_2, granulometriamatriz4_2, granulometriamatriz5_2, granulometriamatriz0_1, granulometriamatriz1_1, granulometriamatriz2_1, granulometriamatriz3_1, granulometriamatriz4_1, granulometriamatriz5_1, granulometriamatriz6_1, granulometriamatriz7_1, granulometriamatriz6_2, granulometriamatriz7_2);


                                        databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Suelos/Form_UGS_Suelos_"+j).setValue(NuevoFormatoUGSSuelos);


                                        int contFotosAnexas = Integer.parseInt(FromatoAux.getString("FotosAnexas"));
                                        databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Suelos/Form_UGS_Suelos_"+j+"/FotosAnexas/count").setValue(contFotosAnexas);
                                        for (int k = 1; k <= contFotosAnexas; k++) {

                                            String NombreFotosAnexas = EditTextsAux.getString("NombreFotosAnexas"+k);
                                            String DescriFotosAnexas = EditTextsAux.getString("DescriFotosAnexas"+k);

                                            FormatFotosAnexas nuevoFormatoFotosAnexas = new FormatFotosAnexas(true, NombreFotosAnexas, DescriFotosAnexas);
                                            databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_UGS_Suelos/Form_UGS_Suelos_"+j+"/FotosAnexas/FotoAnexa_"+k).setValue(nuevoFormatoFotosAnexas);
                                        }
                                        String FotosAnexasForm = FromatoAux.getString("FotosAnexasForm");
                                        SubirFotos(cont, FotosAnexasForm, "UGSS", j);

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
//                                            String tipodemm = SpinnersAux.getString("tipodemm"+k);
//                                            String tipomaterial = SpinnersAux.getString("tipomaterial"+k);
                                            String actividad = SpinnersAux.getString("actividad"+k);
                                            String codigonuevaSGMF = EditTextsAux.getString("codigonuevaSGMF"+k);
                                            String coberturaotro = EditTextsAux.getString("coberturaotro"+k);
                                            String usootro = EditTextsAux.getString("usootro"+k);
                                            String patronotro = EditTextsAux.getString("patronotro"+k);

                                            String tipodemm0check = CheckBoxAux.getString("tipodemm0check"+k);
                                            String tipodemm1check = CheckBoxAux.getString("tipodemm1check"+k);
                                            String tipodemm2check = CheckBoxAux.getString("tipodemm2check"+k);
                                            String tipodemm3check = CheckBoxAux.getString("tipodemm3check"+k);
                                            String tipodemm4check = CheckBoxAux.getString("tipodemm4check"+k);
                                            String tipodemm5check = CheckBoxAux.getString("tipodemm5check"+k);
                                            String tipodemm6check = CheckBoxAux.getString("tipodemm6check"+k);
                                            String tipodemm7check = CheckBoxAux.getString("tipodemm7check"+k);
                                            String tipodemm8check = CheckBoxAux.getString("tipodemm8check"+k);

                                            String tipomaterial0check = CheckBoxAux.getString("tipomaterial0check"+k);
                                            String tipomaterial1check = CheckBoxAux.getString("tipomaterial1check"+k);
                                            String tipomaterial2check = CheckBoxAux.getString("tipomaterial2check"+k);
                                            String tipomaterial3check = CheckBoxAux.getString("tipomaterial3check"+k);
                                            String tipomaterial4check = CheckBoxAux.getString("tipomaterial4check"+k);


                                            FormatNewSGMF nuevoFormatoNewSGMF = new FormatNewSGMF(true, tiporoca, gradometeor, gradofractura, tiposuelo, tamanograno, tiporelieve, indicerelieve, inclinacionladera, longiladera, formaladera, formacresta, formavalle, cobertura, uso, densidad, frecuencia, textura, patron, tipoerosion, espaciamiento, intensidaderosion, actividad, codigonuevaSGMF, coberturaotro, usootro, patronotro, tipodemm0check, tipodemm1check, tipodemm2check, tipodemm3check, tipodemm4check, tipodemm5check, tipodemm6check, tipodemm7check, tipodemm8check, tipomaterial0check, tipomaterial1check, tipomaterial2check, tipomaterial3check, tipomaterial4check);
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
                                        String FotosAnexasForm = FromatoAux.getString("FotosAnexasForm");
                                        SubirFotos(cont, FotosAnexasForm, "SGMF", j);

                                    }

                                    for (int j = 0; j < contCAT; j++) {
                                        JSONObject FromatoAux = Formularios.getJSONObject("Form_CATALOGO_"+j);
                                        JSONObject SpinnersAux = FromatoAux.getJSONObject("Spinners");
                                        JSONObject EditTextsAux = FromatoAux.getJSONObject("EditText");
                                        JSONObject RadioGrpAux = FromatoAux.getJSONObject("RadioGrp");

                                        boolean activo = true;
                                        String IMPORTANC = SpinnersAux.getString("IMPORTANC");
                                        String FECHA_FUENTE = SpinnersAux.getString("FECHA_FUENTE");
                                        String ConfiFechaMM = SpinnersAux.getString("ConfiFechaMM");
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


                                        FormatCAT nuevoFormatoCAT = new FormatCAT(activo,IMPORTANC,FECHA_FUENTE,ConfiFechaMM,NOM_MUN,SUBTIPO_1,SUBTIPO_2,ID_PARTE,ENCUESTAD,FECHA_MOV,FECHA_REP,COD_SIMMA,VEREDA,SITIO,REF_GEOGRF,HERIDOS,VIDAS,DESAPARECIDOS,PERSONAS,FAMILIAS,sensoresremotos,FTE_INFSEC,notas,TIPO_MOV2,TIPO_MOV1);

                                            databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_CATALOGO/Form_CATALOGO_"+j).setValue(nuevoFormatoCAT);

                                        int contDANOSNew = Integer.parseInt(FromatoAux.getString("DANOS"));
                                        databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_CATALOGO/Form_CATALOGO_"+j+"/DANOS/count").setValue(contDANOSNew);
                                        for (int k = 1; k <= contDANOSNew; k++) {

                                            String tiposdano = SpinnersAux.getString("tiposdaño"+k);
                                            String clasedano = SpinnersAux.getString("clasedaño"+k);
                                            String tipodano = EditTextsAux.getString("tipodaño"+k);
                                            String cantidaddano = EditTextsAux.getString("cantidaddaño"+k);
                                            String unidaddano = EditTextsAux.getString("unidaddaño"+k);
                                            String valordano = EditTextsAux.getString("valordaño"+k);


                                            FormatNewDANO nuevoFormatoNewDANO = new FormatNewDANO(true, tiposdano, clasedano, tipodano, cantidaddano, unidaddano, valordano);
                                            databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_CATALOGO/Form_CATALOGO_"+j+"/DANOS/DANOS_"+k).setValue(nuevoFormatoNewDANO);

                                        }

                                    }

                                    for (int j = 0; j < contINV; j++) {
                                        JSONObject FromatoAux = Formularios.getJSONObject("Form_INVENTARIO_"+j);
                                        JSONObject SpinnersAux = FromatoAux.getJSONObject("Spinners");
                                        JSONObject EditTextsAux = FromatoAux.getJSONObject("EditText");
                                        JSONObject RadioGrpAux = FromatoAux.getJSONObject("RadioGrp");
                                        JSONObject CheckBoxAux = FromatoAux.getJSONObject("CheckBox");

                                        boolean activo = true;
                                        String IMPORTANC = SpinnersAux.getString("IMPORTANC");
                                        String FECHA_FUENTE = SpinnersAux.getString("FECHA_FUENTE");
                                        String ConfiFechaMM = SpinnersAux.getString("ConfiFechaMM");
                                        String NOM_MUN = SpinnersAux.getString("NOM_MUN");

                                        String edadmm = SpinnersAux.getString("edadmm");
                                        String ESTADO_ACT = SpinnersAux.getString("ESTADO_ACT");
                                        String ESTILO = SpinnersAux.getString("ESTILO");
                                        String DISTRIBUC = SpinnersAux.getString("DISTRIBUC");
                                        String estructura0espaciamiento = SpinnersAux.getString("estructura0espaciamiento");
                                        String estructura1espaciamiento = SpinnersAux.getString("estructura1espaciamiento");
                                        String estructura2espaciamiento = SpinnersAux.getString("estructura2espaciamiento");
                                        String estructura3espaciamiento = SpinnersAux.getString("estructura3espaciamiento");
                                        String estructura4espaciamiento = SpinnersAux.getString("estructura4espaciamiento");
                                        String estructura5espaciamiento = SpinnersAux.getString("estructura5espaciamiento");

                                        String SUBTIPO_1 = SpinnersAux.getString("SUBTIPO_1");
                                        String SUBTIPO_2 = SpinnersAux.getString("SUBTIPO_2");

                                        String velocidad = SpinnersAux.getString("velocidad");
                                        String sisclasificacion = SpinnersAux.getString("sisclasificacion");
                                        String morfomodo = SpinnersAux.getString("morfomodo");
                                        String morfoseveridad = SpinnersAux.getString("morfoseveridad");
                                        String erosionedad = SpinnersAux.getString("erosionedad");
                                        String erosionestado = SpinnersAux.getString("erosionestado");
                                        String erosionfluvial = SpinnersAux.getString("erosionfluvial");
                                        String erosioneolica = SpinnersAux.getString("erosioneolica");
                                        String represamientotipo = SpinnersAux.getString("represamientotipo");


                                        String ID_PARTE = EditTextsAux.getString("ID_PARTE");
                                        String ENCUESTAD = EditTextsAux.getString("ENCUESTAD");
                                        String FECHA_MOV = EditTextsAux.getString("FECHA_MOV");
                                        String FECHA_REP = EditTextsAux.getString("FECHA_REP");
                                        String COD_SIMMA = EditTextsAux.getString("COD_SIMMA");
                                        String VEREDA = EditTextsAux.getString("VEREDA");
                                        String SITIO = EditTextsAux.getString("SITIO");
                                        String REF_GEOGRF = EditTextsAux.getString("REF_GEOGRF");

                                        String planchas = EditTextsAux.getString("planchas");

                                        String sensoresremotos = EditTextsAux.getString("sensoresremotos");
                                        String FTE_INFSEC = EditTextsAux.getString("FTE_INFSEC");

                                        String LITOLOGIA = EditTextsAux.getString("LITOLOGIA");

                                        String estructura0dirbuz = EditTextsAux.getString("estructura0dirbuz");
                                        String estructura0buz = EditTextsAux.getString("estructura0buz");
                                        String estructura1dirbuz = EditTextsAux.getString("estructura1dirbuz");
                                        String estructura1buz = EditTextsAux.getString("estructura1buz");
                                        String estructura2dirbuz = EditTextsAux.getString("estructura2dirbuz");
                                        String estructura2buz = EditTextsAux.getString("estructura2buz");
                                        String estructura3dirbuz = EditTextsAux.getString("estructura3dirbuz");
                                        String estructura3buz = EditTextsAux.getString("estructura3buz");
                                        String estructura4dirbuz = EditTextsAux.getString("estructura4dirbuz");
                                        String estructura4buz = EditTextsAux.getString("estructura4buz");
                                        String estructura5dirbuz = EditTextsAux.getString("estructura5dirbuz");
                                        String estructura5buz = EditTextsAux.getString("estructura5buz");
                                        String velocidadmax = EditTextsAux.getString("velocidadmax");
                                        String velocidadmin = EditTextsAux.getString("velocidadmin");
                                        String morfogeneral0 = EditTextsAux.getString("morfogeneral0");
                                        String morfogeneral1 = EditTextsAux.getString("morfogeneral1");
                                        String morfogeneral2 = EditTextsAux.getString("morfogeneral2");
                                        String morfogeneral3 = EditTextsAux.getString("morfogeneral3");
                                        String morfogeneral4 = EditTextsAux.getString("morfogeneral4");
                                        String morfogeneral5 = EditTextsAux.getString("morfogeneral5");
                                        String morfogeneral6 = EditTextsAux.getString("morfogeneral6");
                                        String morfodimensiones0 = EditTextsAux.getString("morfodimensiones0");
                                        String morfodimensiones1 = EditTextsAux.getString("morfodimensiones1");
                                        String morfodimensiones2 = EditTextsAux.getString("morfodimensiones2");
                                        String morfodimensiones3 = EditTextsAux.getString("morfodimensiones3");
                                        String morfodimensiones4 = EditTextsAux.getString("morfodimensiones4");
                                        String morfodimensiones5 = EditTextsAux.getString("morfodimensiones5");
                                        String morfodimensiones6 = EditTextsAux.getString("morfodimensiones6");
                                        String morfodimensiones7 = EditTextsAux.getString("morfodimensiones7");
                                        String morfodimensiones8 = EditTextsAux.getString("morfodimensiones8");
                                        String morfodimensiones9 = EditTextsAux.getString("morfodimensiones9");
                                        String morfodimensiones10 = EditTextsAux.getString("morfodimensiones10");
                                        String morfodimensiones11 = EditTextsAux.getString("morfodimensiones11");
                                        String morfodimensiones12 = EditTextsAux.getString("morfodimensiones12");
                                        String AN_GMF = EditTextsAux.getString("AN_GMF");
                                        String cobertura0 = EditTextsAux.getString("cobertura0");
                                        String cobertura1 = EditTextsAux.getString("cobertura1");
                                        String cobertura2 = EditTextsAux.getString("cobertura2");
                                        String cobertura3 = EditTextsAux.getString("cobertura3");
                                        String cobertura4 = EditTextsAux.getString("cobertura4");
                                        String cobertura5 = EditTextsAux.getString("cobertura5");
                                        String cobertura6 = EditTextsAux.getString("cobertura6");
                                        String cobertura7 = EditTextsAux.getString("cobertura7");
                                        String usosuelo0 = EditTextsAux.getString("usosuelo0");
                                        String usosuelo1 = EditTextsAux.getString("usosuelo1");
                                        String usosuelo2 = EditTextsAux.getString("usosuelo2");
                                        String usosuelo3 = EditTextsAux.getString("usosuelo3");
                                        String usosuelo4 = EditTextsAux.getString("usosuelo4");
                                        String usosuelo5 = EditTextsAux.getString("usosuelo5");
                                        String usosuelo6 = EditTextsAux.getString("usosuelo6");
                                        String usosuelo7 = EditTextsAux.getString("usosuelo7");
                                        String usosuelo8 = EditTextsAux.getString("usosuelo8");
                                        String usosuelo9 = EditTextsAux.getString("usosuelo9");
                                        String referenciasautor = EditTextsAux.getString("referenciasautor");
                                        String referenciasaño = EditTextsAux.getString("referenciasaño");
                                        String referenciastitulo = EditTextsAux.getString("referenciastitulo");
                                        String referenciaseditor = EditTextsAux.getString("referenciaseditor");
                                        String referenciasciudad = EditTextsAux.getString("referenciasciudad");
                                        String referenciaspaginas = EditTextsAux.getString("referenciaspaginas");
                                        String represamientomorfometria0 = EditTextsAux.getString("represamientomorfometria0");
                                        String represamientomorfometria1 = EditTextsAux.getString("represamientomorfometria1");
                                        String represamientomorfometria2 = EditTextsAux.getString("represamientomorfometria2");
                                        String represamientomorfometria3 = EditTextsAux.getString("represamientomorfometria3");
                                        String represamientomorfometria4 = EditTextsAux.getString("represamientomorfometria4");
                                        String represamientomorfometria5 = EditTextsAux.getString("represamientomorfometria5");
                                        String represamientomorfoembalse0 = EditTextsAux.getString("represamientomorfoembalse0");
                                        String represamientomorfoembalse1 = EditTextsAux.getString("represamientomorfoembalse1");
                                        String represamientomorfoembalse2 = EditTextsAux.getString("represamientomorfoembalse2");
                                        String represamientomorfoembalse3 = EditTextsAux.getString("represamientomorfoembalse3");
                                        String represamientomorfoembalse4 = EditTextsAux.getString("represamientomorfoembalse4");
                                        String represamientomorfoembalse5 = EditTextsAux.getString("represamientomorfoembalse5");
                                        String represamientomorfoembalse6 = EditTextsAux.getString("represamientomorfoembalse6");
                                        String represamientomorfoembalse7 = EditTextsAux.getString("represamientomorfoembalse7");

                                        String HERIDOS = EditTextsAux.getString("heridos");
                                        String VIDAS = EditTextsAux.getString("vidas");
                                        String DESAPARECIDOS = EditTextsAux.getString("desaparecidos");
                                        String PERSONAS = EditTextsAux.getString("personas");
                                        String FAMILIAS = EditTextsAux.getString("familias");
                                        String notas = EditTextsAux.getString("notas");

                                        String apreciacionriesgo = EditTextsAux.getString("apreciacionriesgo");

                                        String TIPO_MOV2 = RadioGrpAux.getString("TIPO_MOV2");
                                        String TIPO_MOV1 = RadioGrpAux.getString("TIPO_MOV1");

                                        String humedad2 = RadioGrpAux.getString("humedad2");
                                        String humedad1 = RadioGrpAux.getString("humedad1");
                                        String plasticidad2 = RadioGrpAux.getString("plasticidad2");
                                        String plasticidad1 = RadioGrpAux.getString("plasticidad1");

                                        String estructura0check = CheckBoxAux.getString("estructura0check");
                                        String estructura1check = CheckBoxAux.getString("estructura1check");
                                        String estructura2check = CheckBoxAux.getString("estructura2check");
                                        String estructura3check = CheckBoxAux.getString("estructura3check");
                                        String estructura4check = CheckBoxAux.getString("estructura4check");
                                        String estructura5check = CheckBoxAux.getString("estructura5check");
                                        String tipomaterial0check_2 = CheckBoxAux.getString("tipomaterial0check_2");
                                        String tipomaterial0check_1 = CheckBoxAux.getString("tipomaterial0check_1");
                                        String tipomaterial1check_2 = CheckBoxAux.getString("tipomaterial1check_2");
                                        String tipomaterial1check_1 = CheckBoxAux.getString("tipomaterial1check_1");
                                        String tipomaterial2check_2 = CheckBoxAux.getString("tipomaterial2check_2");
                                        String tipomaterial2check_1 = CheckBoxAux.getString("tipomaterial2check_1");
                                        String tipomaterial3check_2 = CheckBoxAux.getString("tipomaterial3check_2");
                                        String tipomaterial3check_1 = CheckBoxAux.getString("tipomaterial3check_1");
                                        String tipomaterial4check_2 = CheckBoxAux.getString("tipomaterial4check_2");
                                        String tipomaterial4check_1 = CheckBoxAux.getString("tipomaterial4check_1");
                                        String origensuelo0check = CheckBoxAux.getString("origensuelo0check");
                                        String origensuelo1check = CheckBoxAux.getString("origensuelo1check");
                                        String origensuelo2check = CheckBoxAux.getString("origensuelo2check");
                                        String origensuelo3check = CheckBoxAux.getString("origensuelo3check");
                                        String causasinherentes0check = CheckBoxAux.getString("causasinherentes0check");
                                        String causasinherentes1check = CheckBoxAux.getString("causasinherentes1check");
                                        String causasinherentes2check = CheckBoxAux.getString("causasinherentes2check");
                                        String causasinherentes3check = CheckBoxAux.getString("causasinherentes3check");
                                        String causasinherentes4check = CheckBoxAux.getString("causasinherentes4check");
                                        String causasinherentes5check = CheckBoxAux.getString("causasinherentes5check");
                                        String causasinherentes6check = CheckBoxAux.getString("causasinherentes6check");
                                        String causasinherentes7check = CheckBoxAux.getString("causasinherentes7check");
                                        String causasinherentes8check = CheckBoxAux.getString("causasinherentes8check");
                                        String causasinherentes9check = CheckBoxAux.getString("causasinherentes9check");
                                        String causasinherentes10check = CheckBoxAux.getString("causasinherentes10check");
                                        String causasinherentes11check = CheckBoxAux.getString("causasinherentes11check");
                                        String causascontrideto0check_2 = CheckBoxAux.getString("causascontrideto0check_2");
                                        String causascontrideto0check_1 = CheckBoxAux.getString("causascontrideto0check_1");
                                        String causascontrideto1check_2 = CheckBoxAux.getString("causascontrideto1check_2");
                                        String causascontrideto1check_1 = CheckBoxAux.getString("causascontrideto1check_1");
                                        String causascontrideto2check_2 = CheckBoxAux.getString("causascontrideto2check_2");
                                        String causascontrideto2check_1 = CheckBoxAux.getString("causascontrideto2check_1");
                                        String causascontrideto3check_2 = CheckBoxAux.getString("causascontrideto3check_2");
                                        String causascontrideto3check_1 = CheckBoxAux.getString("causascontrideto3check_1");
                                        String causascontrideto4check_2 = CheckBoxAux.getString("causascontrideto4check_2");
                                        String causascontrideto4check_1 = CheckBoxAux.getString("causascontrideto4check_1");
                                        String causascontrideto5check_2 = CheckBoxAux.getString("causascontrideto5check_2");
                                        String causascontrideto5check_1 = CheckBoxAux.getString("causascontrideto5check_1");
                                        String causascontrideto6check_2 = CheckBoxAux.getString("causascontrideto6check_2");
                                        String causascontrideto6check_1 = CheckBoxAux.getString("causascontrideto6check_1");
                                        String causascontrideto7check_2 = CheckBoxAux.getString("causascontrideto7check_2");
                                        String causascontrideto7check_1 = CheckBoxAux.getString("causascontrideto7check_1");
                                        String causascontrideto8check_2 = CheckBoxAux.getString("causascontrideto8check_2");
                                        String causascontrideto8check_1 = CheckBoxAux.getString("causascontrideto8check_1");
                                        String causascontrideto9check_2 = CheckBoxAux.getString("causascontrideto9check_2");
                                        String causascontrideto9check_1 = CheckBoxAux.getString("causascontrideto9check_1");
                                        String causascontrideto10check_2 = CheckBoxAux.getString("causascontrideto10check_2");
                                        String causascontrideto10check_1 = CheckBoxAux.getString("causascontrideto10check_1");
                                        String causascontrideto11check_2 = CheckBoxAux.getString("causascontrideto11check_2");
                                        String causascontrideto11check_1 = CheckBoxAux.getString("causascontrideto11check_1");
                                        String causascontrideto12check_2 = CheckBoxAux.getString("causascontrideto12check_2");
                                        String causascontrideto12check_1 = CheckBoxAux.getString("causascontrideto12check_1");
                                        String causascontrideto13check_2 = CheckBoxAux.getString("causascontrideto13check_2");
                                        String causascontrideto13check_1 = CheckBoxAux.getString("causascontrideto13check_1");
                                        String causascontrideto14check_2 = CheckBoxAux.getString("causascontrideto14check_2");
                                        String causascontrideto14check_1 = CheckBoxAux.getString("causascontrideto14check_1");
                                        String causascontrideto15check_2 = CheckBoxAux.getString("causascontrideto15check_2");
                                        String causascontrideto15check_1 = CheckBoxAux.getString("causascontrideto15check_1");
                                        String causascontrideto16check_2 = CheckBoxAux.getString("causascontrideto16check_2");
                                        String causascontrideto16check_1 = CheckBoxAux.getString("causascontrideto16check_1");
                                        String causascontrideto17check_2 = CheckBoxAux.getString("causascontrideto17check_2");
                                        String causascontrideto17check_1 = CheckBoxAux.getString("causascontrideto17check_1");
                                        String causascontrideto18check_2 = CheckBoxAux.getString("causascontrideto18check_2");
                                        String causascontrideto18check_1 = CheckBoxAux.getString("causascontrideto18check_1");
                                        String causascontrideto19check_2 = CheckBoxAux.getString("causascontrideto19check_2");
                                        String causascontrideto19check_1 = CheckBoxAux.getString("causascontrideto19check_1");
                                        String causascontrideto20check_2 = CheckBoxAux.getString("causascontrideto20check_2");
                                        String causascontrideto20check_1 = CheckBoxAux.getString("causascontrideto20check_1");
                                        String causascontrideto21check_2 = CheckBoxAux.getString("causascontrideto21check_2");
                                        String causascontrideto21check_1 = CheckBoxAux.getString("causascontrideto21check_1");
                                        String causascontrideto22check_2 = CheckBoxAux.getString("causascontrideto22check_2");
                                        String causascontrideto22check_1 = CheckBoxAux.getString("causascontrideto22check_1");
                                        String causascontrideto23check_2 = CheckBoxAux.getString("causascontrideto23check_2");
                                        String causascontrideto23check_1 = CheckBoxAux.getString("causascontrideto23check_1");
                                        String causascontrideto24check_2 = CheckBoxAux.getString("causascontrideto24check_2");
                                        String causascontrideto24check_1 = CheckBoxAux.getString("causascontrideto24check_1");
                                        String erosionsuperficial0check = CheckBoxAux.getString("erosionsuperficial0check");
                                        String erosionsuperficial1check = CheckBoxAux.getString("erosionsuperficial1check");
                                        String erosionsuperficial2check = CheckBoxAux.getString("erosionsuperficial2check");
                                        String erosionsuperficial3check = CheckBoxAux.getString("erosionsuperficial3check");
                                        String erosionsuperficial4check = CheckBoxAux.getString("erosionsuperficial4check");
                                        String erosionsubsuperficial0check = CheckBoxAux.getString("erosionsubsuperficial0check");
                                        String erosionsubsuperficial1check = CheckBoxAux.getString("erosionsubsuperficial1check");
                                        String represamientocondiciones0check = CheckBoxAux.getString("represamientocondiciones0check");
                                        String represamientocondiciones1check = CheckBoxAux.getString("represamientocondiciones1check");
                                        String represamientocondiciones2check = CheckBoxAux.getString("represamientocondiciones2check");
                                        String represamientocondiciones3check = CheckBoxAux.getString("represamientocondiciones3check");
                                        String represamientocondiciones4check = CheckBoxAux.getString("represamientocondiciones4check");
                                        String represamientocondiciones5check = CheckBoxAux.getString("represamientocondiciones5check");
                                        String represamientocondiciones6check = CheckBoxAux.getString("represamientocondiciones6check");
                                        String represamientocondiciones7check = CheckBoxAux.getString("represamientocondiciones7check");
                                        String represamientoefectos0check = CheckBoxAux.getString("represamientoefectos0check");
                                        String represamientoefectos1check = CheckBoxAux.getString("represamientoefectos1check");
                                        String represamientoefectos2check = CheckBoxAux.getString("represamientoefectos2check");
                                        String represamientoefectos3check = CheckBoxAux.getString("represamientoefectos3check");
                                        String represamientoefectos4check = CheckBoxAux.getString("represamientoefectos4check");

                                        String tipodeposito0check = "";
                                        String tipodeposito1check = "";
                                        String tipodeposito2check = "";
                                        String tipodeposito3check = "";
                                        String tipodeposito4check = "";

                                        String sismoMM0 = "";
                                        String sismoMM1 = "";
                                        String sismoMM2 = "";
                                        String sismoMM3 = "";
                                        String lluviasMM0 = "";
                                        String lluviasMM1 = "";
                                        String lluviasMM2 = "";
                                        String lluviasMM3 = "";


                                        if (origensuelo3check.equals("true")){
                                            tipodeposito0check = CheckBoxAux.getString("tipodeposito0check");
                                            tipodeposito1check = CheckBoxAux.getString("tipodeposito1check");
                                            tipodeposito2check = CheckBoxAux.getString("tipodeposito2check");
                                            tipodeposito3check = CheckBoxAux.getString("tipodeposito3check");
                                            tipodeposito4check = CheckBoxAux.getString("tipodeposito4check");
                                        }
                                        if (causascontrideto1check_2.equals("true") || causascontrideto1check_1.equals("true")){
                                            sismoMM0 = EditTextsAux.getString("sismoMM0");
                                            sismoMM1 = EditTextsAux.getString("sismoMM1");
                                            sismoMM2 = EditTextsAux.getString("sismoMM2");
                                            sismoMM3 = EditTextsAux.getString("sismoMM3");
                                        }
                                        if (causascontrideto3check_2.equals("true") || causascontrideto3check_1.equals("true")){
                                            lluviasMM0 = EditTextsAux.getString("lluviasMM0");
                                            lluviasMM1 = EditTextsAux.getString("lluviasMM1");
                                            lluviasMM2 = EditTextsAux.getString("lluviasMM2");
                                            lluviasMM3 = EditTextsAux.getString("lluviasMM3");
                                        }


                                        FormatINV nuevoFormatoINV = new FormatINV(activo, IMPORTANC, FECHA_FUENTE, ConfiFechaMM, NOM_MUN, edadmm, ESTADO_ACT, ESTILO, DISTRIBUC, estructura0espaciamiento, estructura1espaciamiento, estructura2espaciamiento, estructura3espaciamiento, estructura4espaciamiento, estructura5espaciamiento, SUBTIPO_1, SUBTIPO_2, velocidad, sisclasificacion, morfomodo, morfoseveridad, erosionedad, erosionestado, erosionfluvial, erosioneolica, represamientotipo, ID_PARTE, ENCUESTAD, FECHA_MOV, FECHA_REP, COD_SIMMA, VEREDA, SITIO, REF_GEOGRF, planchas, sensoresremotos, FTE_INFSEC, LITOLOGIA, estructura0dirbuz, estructura0buz, estructura1dirbuz, estructura1buz, estructura2dirbuz, estructura2buz, estructura3dirbuz, estructura3buz, estructura4dirbuz, estructura4buz, estructura5dirbuz, estructura5buz, velocidadmax, velocidadmin, morfogeneral0, morfogeneral1, morfogeneral2, morfogeneral3, morfogeneral4, morfogeneral5, morfogeneral6, morfodimensiones0, morfodimensiones1, morfodimensiones2, morfodimensiones3, morfodimensiones4, morfodimensiones5, morfodimensiones6, morfodimensiones7, morfodimensiones8, morfodimensiones9, morfodimensiones10, morfodimensiones11, morfodimensiones12, AN_GMF, cobertura0, cobertura1, cobertura2, cobertura3, cobertura4, cobertura5, cobertura6, cobertura7, usosuelo0, usosuelo1, usosuelo2, usosuelo3, usosuelo4, usosuelo5, usosuelo6, usosuelo7, usosuelo8, usosuelo9, referenciasautor, referenciasaño, referenciastitulo, referenciaseditor, referenciasciudad, referenciaspaginas, represamientomorfometria0, represamientomorfometria1, represamientomorfometria2, represamientomorfometria3, represamientomorfometria4, represamientomorfometria5, HERIDOS, VIDAS, DESAPARECIDOS, PERSONAS, FAMILIAS, notas, apreciacionriesgo, TIPO_MOV2, TIPO_MOV1, humedad2, humedad1, plasticidad2, plasticidad1, estructura0check, estructura1check, estructura2check, estructura3check, estructura4check, estructura5check, tipomaterial0check_2, tipomaterial0check_1, tipomaterial1check_2, tipomaterial1check_1, tipomaterial2check_2, tipomaterial2check_1, tipomaterial3check_2, tipomaterial3check_1, tipomaterial4check_2, tipomaterial4check_1, origensuelo0check, origensuelo1check, origensuelo2check, origensuelo3check, causasinherentes0check, causasinherentes1check, causasinherentes2check, causasinherentes3check, causasinherentes4check, causasinherentes5check, causasinherentes6check, causasinherentes7check, causasinherentes8check, causasinherentes9check, causasinherentes10check, causasinherentes11check, causascontrideto0check_2, causascontrideto0check_1, causascontrideto1check_2, causascontrideto1check_1, causascontrideto2check_2, causascontrideto2check_1, causascontrideto3check_2, causascontrideto3check_1, causascontrideto4check_2, causascontrideto4check_1, causascontrideto5check_2, causascontrideto5check_1, causascontrideto6check_2, causascontrideto6check_1, causascontrideto7check_2, causascontrideto7check_1, causascontrideto8check_2, causascontrideto8check_1, causascontrideto9check_2, causascontrideto9check_1, causascontrideto10check_2, causascontrideto10check_1, causascontrideto11check_2, causascontrideto11check_1, causascontrideto12check_2, causascontrideto12check_1, causascontrideto13check_2, causascontrideto13check_1, causascontrideto14check_2, causascontrideto14check_1, causascontrideto15check_2, causascontrideto15check_1, causascontrideto16check_2, causascontrideto16check_1, causascontrideto17check_2, causascontrideto17check_1, causascontrideto18check_2, causascontrideto18check_1, causascontrideto19check_2, causascontrideto19check_1, causascontrideto20check_2, causascontrideto20check_1, causascontrideto21check_2, causascontrideto21check_1, causascontrideto22check_2, causascontrideto22check_1, causascontrideto23check_2, causascontrideto23check_1, causascontrideto24check_2, causascontrideto24check_1, erosionsuperficial0check, erosionsuperficial1check, erosionsuperficial2check, erosionsuperficial3check, erosionsuperficial4check, erosionsubsuperficial0check, erosionsubsuperficial1check, represamientocondiciones0check, represamientocondiciones1check, represamientocondiciones2check, represamientocondiciones3check, represamientocondiciones4check, represamientocondiciones5check, represamientocondiciones6check, represamientocondiciones7check, represamientoefectos0check, represamientoefectos1check, represamientoefectos2check, represamientoefectos3check, represamientoefectos4check, tipodeposito0check, tipodeposito1check, tipodeposito2check, tipodeposito3check, tipodeposito4check, sismoMM0, sismoMM1, sismoMM2, sismoMM3, lluviasMM0, lluviasMM1, lluviasMM2, lluviasMM3, represamientomorfoembalse0, represamientomorfoembalse1, represamientomorfoembalse2, represamientomorfoembalse3, represamientomorfoembalse4,represamientomorfoembalse5, represamientomorfoembalse6, represamientomorfoembalse7);

                                        databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_INVENTARIO/Form_INVENTARIO_"+j).setValue(nuevoFormatoINV);

                                        int contDANOSNew = Integer.parseInt(FromatoAux.getString("DANOS"));
                                        databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_INVENTARIO/Form_INVENTARIO_"+j+"/DANOS/count").setValue(contDANOSNew);
                                        for (int k = 1; k <= contDANOSNew; k++) {

                                            String tiposdano = SpinnersAux.getString("tiposdaño"+k);
                                            String clasedano = SpinnersAux.getString("clasedaño"+k);
                                            String tipodano = EditTextsAux.getString("tipodaño"+k);
                                            String cantidaddano = EditTextsAux.getString("cantidaddaño"+k);
                                            String unidaddano = EditTextsAux.getString("unidaddaño"+k);
                                            String valordano = EditTextsAux.getString("valordaño"+k);


                                            FormatNewDANO nuevoFormatoNewDANO = new FormatNewDANO(true, tiposdano, clasedano, tipodano, cantidaddano, unidaddano, valordano);
                                            databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_INVENTARIO/Form_INVENTARIO_"+j+"/DANOS/DANOS_"+k).setValue(nuevoFormatoNewDANO);

                                        }

                                        int contFotosAnexas = Integer.parseInt(FromatoAux.getString("FotosAnexas"));
                                        databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_INVENTARIO/Form_INVENTARIO_"+j+"/FotosAnexas/count").setValue(contFotosAnexas);
                                        for (int k = 1; k <= contFotosAnexas; k++) {

                                            String fechaFotosAnexas = EditTextsAux.getString("fechaFotosAnexas"+k);
                                            String nombreFotosAnexasINV = EditTextsAux.getString("nombreFotosAnexasINV"+k);
                                            String autorFotosAnexas = EditTextsAux.getString("autorFotosAnexas"+k);
                                            String obsFotosAnexas = EditTextsAux.getString("obsFotosAnexas"+k);

                                            FormatFotosAnexasINV nuevoFormatoFotosAnexasINV = new FormatFotosAnexasINV(true, fechaFotosAnexas, nombreFotosAnexasINV, autorFotosAnexas, obsFotosAnexas);
                                            databaseReference.child("EstacionesCampo/estacion_"+cont+"/Formularios/Form_INVENTARIO/Form_INVENTARIO_"+j+"/FotosAnexas/FotoAnexa_"+k).setValue(nuevoFormatoFotosAnexasINV);

                                        }
                                        String FotosAnexasForm = FromatoAux.getString("FotosAnexasForm");
                                        SubirFotos(cont, FotosAnexasForm, "INV", j);

                                    }

                                    form.put("Subido", true);
                                    Log.d("jaaja", "GuardarForm10: "+formComplete);
                                    OutputStreamWriter file = new OutputStreamWriter(mcont.openFileOutput("listaForm.txt", Activity.MODE_PRIVATE));
                                    file.write(String.valueOf(formComplete));
                                    file.flush();
                                    file.close();

                                    SubirFotos(cont, ListFotosGenerales, "General", 0);
                                    SubirFotos(cont, ListFotosLib, "Lib", 0);

                                    cont++;
                                }

                            } catch (JSONException | FileNotFoundException e) {
                                e.printStackTrace();
                                Log.d("jaaja", "AlgunError: "+e);
                                Toast.makeText(mcont, "Ocurrió un error a la hora de subir los datos.\n", Toast.LENGTH_LONG).show();
                            } catch (IOException e) {
                                e.printStackTrace();
                                Toast.makeText(mcont, "Ocurrió un error a la hora de subir los datos.\n", Toast.LENGTH_LONG).show();
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

    private void SubirFotos(int cont, String listaFotos, String tipo, int form) {

        List<String> myList = new ArrayList<String>(Arrays.asList(listaFotos.replace("[","").replace("]","").split(", ")));

        String ruta = "";
        if (tipo.equals("General")) {
            ruta = "EstacionesCampo/estacion_" + cont + "/FotosGenerales" ;
        }
        if (tipo.equals("Lib")) {
            ruta = "EstacionesCampo/estacion_" + cont + "/FotosLibreta" ;
        }
        if (tipo.equals("UGSR")) {
            ruta = "EstacionesCampo/estacion_" + cont + "/Formularios/Form_UGS_Rocas/Form_UGS_Rocas_"+form+"/FotosAnexas";
        }
        if (tipo.equals("UGSS")) {
            ruta = "EstacionesCampo/estacion_" + cont + "/Formularios/Form_UGS_Suelos/Form_UGS_Suelos_"+form+"/FotosAnexas";
        }
        if (tipo.equals("SGMF")) {
            ruta = "EstacionesCampo/estacion_" + cont + "/Formularios/Form_SGMF/Form_SGMF_"+form+"/FotosAnexas";
        }
        if (tipo.equals("INV")) {
            ruta = "EstacionesCampo/estacion_" + cont + "/Formularios/Form_INVENTARIO/Form_INVENTARIO_"+form+"/FotosAnexas";
        }

        if (listaFotos.equals("[]")){
            databaseReference.child(ruta +"/FotosURL/count").setValue(0);
        }else{
            databaseReference.child(ruta +"/FotosURL/count").setValue(myList.size());
        }

        for (int j = 0; j < myList.size(); j++) {
            if (!listaFotos.equals("[]")){
                try {
                    File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Estaciones/"+ myList.get(j));
                    if (file.exists()){
                        String rutaStorage = "";
                        if (tipo.equals("General")) {
                            rutaStorage = "/FotosGenerales/";
                        }
                        if (tipo.equals("Lib")) {
                            rutaStorage = "/FotosLibreta/";
                        }
                        if (tipo.equals("UGSR")) {
                            rutaStorage = "/Form_UGS_Rocas_"+form+"/";
                        }
                        if (tipo.equals("UGSS")) {
                            rutaStorage = "/Form_UGS_Suelos_"+form+"/";
                        }
                        if (tipo.equals("SGMF")) {
                            rutaStorage = "/Form_SGMF_"+form+"/";
                        }
                        if (tipo.equals("INV")) {
                            rutaStorage = "/Form_INVENTARIO_"+form+"/";
                        }

                        UploadTask uploadTask = storageRef.child("EstacionesCampo/estacion_"+cont+ rutaStorage + myList.get(j)).putFile(Uri.fromFile(file));

                        String name = myList.get(j);
                        int auxFoto = j;
                        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                            @Override
                            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                if (!task.isSuccessful()) {
                                    throw task.getException();
                                }
                                // Continue with the task to get the download URL
                                String rutaStorage1 = "";
                                if (tipo.equals("General")) {
                                    rutaStorage1 = "/FotosGenerales/";
                                }
                                if (tipo.equals("Lib")) {
                                    rutaStorage1 = "/FotosLibreta/";
                                }
                                if (tipo.equals("UGSR")) {
                                    rutaStorage1 = "/Form_UGS_Rocas_"+form+"/";
                                }
                                if (tipo.equals("UGSS")) {
                                    rutaStorage1 = "/Form_UGS_Suelos_"+form+"/";
                                }
                                if (tipo.equals("SGMF")) {
                                    rutaStorage1 = "/Form_SGMF_"+form+"/";
                                }
                                if (tipo.equals("INV")) {
                                    rutaStorage1 = "/Form_INVENTARIO_"+form+"/";
                                }

                                return storageRef.child("EstacionesCampo/estacion_"+cont+ rutaStorage1 + name).getDownloadUrl();

                            }
                        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                            @Override
                            public void onComplete(@NonNull Task<Uri> task) {
                                if (task.isSuccessful()) {
                                    Uri downloadUri = task.getResult();
                                    Log.d("Err", "onComplete: "+downloadUri);
                                    String rutaFotos = "";
                                    if (tipo.equals("General")){
                                        rutaFotos = "/FotosGenerales";
                                    }
                                    if (tipo.equals("Lib")){
                                        rutaFotos = "/FotosLibreta";
                                    }
                                    if (tipo.equals("UGSR")){
                                        rutaFotos = "/Formularios/Form_UGS_Rocas/Form_UGS_Rocas_"+form+"/FotosAnexas";
                                    }
                                    if (tipo.equals("UGSS")){
                                        rutaFotos = "/Formularios/Form_UGS_Suelos/Form_UGS_Suelos_"+form+"/FotosAnexas";
                                    }
                                    if (tipo.equals("SGMF")){
                                        rutaFotos = "/Formularios/Form_SGMF/Form_SGMF_"+form+"/FotosAnexas";
                                    }
                                    if (tipo.equals("INV")){
                                        rutaFotos = "/Formularios/Form_INVENTARIO/Form_INVENTARIO_"+form+"/FotosAnexas";
                                    }

                                    databaseReference.child("EstacionesCampo/estacion_"+cont+rutaFotos+"/FotosURL/Foto_"+auxFoto).setValue(downloadUri.toString());
                                    databaseReference.child("EstacionesCampo/estacion_"+cont+rutaFotos+"/FotosURL/FotoActivo_"+auxFoto).setValue(true);
                                } else {
                                    // Handle failures
                                    // ...
                                }
                            }
                        });
                    }

                }catch (Exception ex){
                    Toast.makeText(mcont, "Ocurrió un error al subir las imágenes\n", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    private void GuardarForm() throws JSONException, IOException {

        Date d = new Date();
        CharSequence s = DateFormat.format("yyyy-MM-dd", d.getTime());
        attrForm = new JSONObject()
                .put("Subido", false)
                .put("activo", true)
                .put("Estacion", etEstacion.getText().toString())
                .put("TipoEstacion", etTipoEstacion.getText().toString())
                .put("Este", etEste.getText().toString())
                .put("Norte", etNorte.getText().toString())
                .put("Altitud", etAltitud.getText().toString())
                .put("Fotos", etFotos.getText().toString())
                .put("FotosCount", listFotosGeneral.size())
                .put("Fotos_Generales", listNombresFotosGeneral.toString())
                .put("FotosLib", etFotosLib.getText().toString())
                .put("FotosLibCount", listFotosLib.size())
                .put("Fotos_Lib", listNombresFotosLib.toString())
                .put("Observaciones", etObservaciones.getText().toString())
                .put("Fecha", s.toString())
                .put("Propietario", userName);


        for (int i = 0; i < listFotosGeneral.size(); i++) {
            try {
                InputStream iStream = mcont.getContentResolver().openInputStream(listFotosGeneral.get(i));
                byte[] inputData = getBytes(iStream);

                File rutaArchivo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Estaciones/");
                if (!rutaArchivo.exists()) { //sino existe, se crea
                    rutaArchivo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Estaciones/");
                    rutaArchivo.mkdirs();//creamos
                }

                try {
                    File files = new File(rutaArchivo, listNombresFotosGeneral.get(i));
                    if (files.exists()) {
                        files.delete();//si este archivo existe, con ese nombre, lo reemplazará
                    }

                    FileOutputStream  out = new FileOutputStream(files);
                    out.write(inputData);
                    out.close();

                } catch (Exception ex) {
                    Toast.makeText(mcont, "Ocurrió un error al guardar la imagen\n", Toast.LENGTH_LONG).show();
                    Log.e("ErrorImagen1", "ex: " + ex);
                }

            }
            catch (Exception e) {
                Toast.makeText(mcont, "Ocurrió un error al guardar la imagen\n", Toast.LENGTH_LONG).show();
                Log.e("ErrorImagen2", "e: " + e);
            }
        }
        for (int i = 0; i < listFotosLib.size(); i++) {
            try {
                InputStream iStream = mcont.getContentResolver().openInputStream(listFotosLib.get(i));
                byte[] inputData = getBytes(iStream);

                File rutaArchivo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Estaciones/");
                if (!rutaArchivo.exists()) { //sino existe, se crea
                    rutaArchivo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Estaciones/");
                    rutaArchivo.mkdirs();//creamos
                }

                try {
                    File files = new File(rutaArchivo, listNombresFotosLib.get(i));
                    if (files.exists()) {
                        files.delete();//si este archivo existe, con ese nombre, lo reemplazará
                    }

                    FileOutputStream  out = new FileOutputStream(files);
                    out.write(inputData);
                    out.close();

                } catch (Exception ex) {
                    Toast.makeText(mcont, "Ocurrió un error al guardar la imagen\n", Toast.LENGTH_LONG).show();
                    Log.e("ErrorImagen1", "ex: " + ex);
                }
            }
            catch (Exception e) {
                Toast.makeText(mcont, "Ocurrió un error al guardar la imagen\n", Toast.LENGTH_LONG).show();
                Log.e("ErrorImagen2", "e: " + e);
            }
        }

        JSONObject FormatosList = new JSONObject();
        JSONObject countFormatos = new JSONObject();
        int countFormatosUGSRocas = 0;
        int countFormatosUGSSuelos = 0;
        int countFormatosSGMF = 0;
        int countFormatosCAT = 0;
        int countFormatosINV = 0;
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

                List<String> auxNombresFotos = new ArrayList<String>();
                for (int j = 0; j < ListaUriFotosAnexas.get(i).size(); j++) {
                    for (int k = 0; k < ListaUriFotosAnexas.get(i).get(j).size(); k++) {
                        try {
                            auxNombresFotos.add(ListaNombresFotosFormatos.get(i).get(j).get(k));

                            InputStream iStream = mcont.getContentResolver().openInputStream(ListaUriFotosAnexas.get(i).get(j).get(k));
                            byte[] inputData = getBytes(iStream);

                            File rutaArchivo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Estaciones/");
                            if (!rutaArchivo.exists()) { //sino existe, se crea
                                rutaArchivo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Estaciones/");
                                rutaArchivo.mkdirs();//creamos
                            }

                            try {
                                File files = new File(rutaArchivo, ListaNombresFotosFormatos.get(i).get(j).get(k));
                                if (files.exists()) {
                                    files.delete();//si este archivo existe, con ese nombre, lo reemplazará
                                }

                                FileOutputStream  out = new FileOutputStream(files);
                                out.write(inputData);
                                out.close();

                            } catch (Exception ex) {
                                Toast.makeText(mcont, "Ocurrió un error al guardar la imagen\n", Toast.LENGTH_LONG).show();
                                Log.e("ErrorImagen1", "ex: " + ex);
                            }
                        }
                        catch (Exception e) {
                            Toast.makeText(mcont, "Ocurrió un error al guardar la imagen\n", Toast.LENGTH_LONG).show();
                            Log.e("ErrorImagen2", "e: " + e);
                        }
                    }
                }

                FormatoTemp.put("FotosAnexasForm", auxNombresFotos.toString());

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
                    if (ListaCheckBox.get(i).get(k).getTag().toString().contains("litologias")){
                        checkBox.put(ListaCheckBox.get(i).get(k).getTag().toString().split("_")[0], ListaCheckBox.get(i).get(k).isChecked());
                    }
                    else{
                        checkBox.put(ListaCheckBox.get(i).get(k).getTag().toString(), ListaCheckBox.get(i).get(k).isChecked());
                    }

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

                List<String> auxNombresFotos = new ArrayList<String>();
                for (int j = 0; j < ListaUriFotosAnexas.get(i).size(); j++) {
                    for (int k = 0; k < ListaUriFotosAnexas.get(i).get(j).size(); k++) {
                        try {
                            auxNombresFotos.add(ListaNombresFotosFormatos.get(i).get(j).get(k));

                            InputStream iStream = mcont.getContentResolver().openInputStream(ListaUriFotosAnexas.get(i).get(j).get(k));
                            byte[] inputData = getBytes(iStream);

                            File rutaArchivo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Estaciones/");
                            if (!rutaArchivo.exists()) { //sino existe, se crea
                                rutaArchivo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Estaciones/");
                                rutaArchivo.mkdirs();//creamos
                            }

                            try {
                                File files = new File(rutaArchivo, ListaNombresFotosFormatos.get(i).get(j).get(k));
                                if (files.exists()) {
                                    files.delete();//si este archivo existe, con ese nombre, lo reemplazará
                                }

                                FileOutputStream  out = new FileOutputStream(files);
                                out.write(inputData);
                                out.close();

                            } catch (Exception ex) {
                                Toast.makeText(mcont, "Ocurrió un error al guardar la imagen\n", Toast.LENGTH_LONG).show();
                                Log.e("ErrorImagen1", "ex: " + ex);
                            }
                        }
                        catch (Exception e) {
                            Toast.makeText(mcont, "Ocurrió un error al guardar la imagen\n", Toast.LENGTH_LONG).show();
                            Log.e("ErrorImagen2", "e: " + e);
                        }
                    }
                }

                FormatoTemp.put("FotosAnexasForm", auxNombresFotos.toString());

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

                List<String> auxNombresFotos = new ArrayList<String>();
                for (int j = 0; j < ListaUriFotosAnexas.get(i).size(); j++) {
                    for (int k = 0; k < ListaUriFotosAnexas.get(i).get(j).size(); k++) {
                        try {
                            auxNombresFotos.add(ListaNombresFotosFormatos.get(i).get(j).get(k));

                            InputStream iStream = mcont.getContentResolver().openInputStream(ListaUriFotosAnexas.get(i).get(j).get(k));
                            byte[] inputData = getBytes(iStream);

                            File rutaArchivo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Estaciones/");
                            if (!rutaArchivo.exists()) { //sino existe, se crea
                                rutaArchivo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Estaciones/");
                                rutaArchivo.mkdirs();//creamos
                            }

                            try {
                                File files = new File(rutaArchivo, ListaNombresFotosFormatos.get(i).get(j).get(k));
                                if (files.exists()) {
                                    files.delete();//si este archivo existe, con ese nombre, lo reemplazará
                                }

                                FileOutputStream  out = new FileOutputStream(files);
                                out.write(inputData);
                                out.close();

                            } catch (Exception ex) {
                                Toast.makeText(mcont, "Ocurrió un error al guardar la imagen\n", Toast.LENGTH_LONG).show();
                                Log.e("ErrorImagen1", "ex: " + ex);
                            }
                        }
                        catch (Exception e) {
                            Toast.makeText(mcont, "Ocurrió un error al guardar la imagen\n", Toast.LENGTH_LONG).show();
                            Log.e("ErrorImagen2", "e: " + e);
                        }
                    }
                }

                FormatoTemp.put("FotosAnexasForm", auxNombresFotos.toString());


                FormatosList.put("Form_SGMF_"+countFormatosSGMF, FormatoTemp);

                countFormatosSGMF++;
            }

            //-------------> CATALOGO

            if (listFormularios.get(i).equals("Catálogo MM")){
                JSONObject FormatoTemp = new JSONObject()
                        .put("DANOS", listContDANOS.get(i));

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

            //-------------> Inventario

            if (listFormularios.get(i).equals("Inventario MM")){
                JSONObject FormatoTemp = new JSONObject()
                        .put("DANOS", listContDANOS.get(i))
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

                JSONObject radioGrp = new JSONObject();
                for (int k = 0; k < ListaRadioGrp.get(i).size(); k++) {
                    RadioButton checkedRadioButton = (RadioButton)ListaRadioGrp.get(i).get(k).findViewById(ListaRadioGrp.get(i).get(k).getCheckedRadioButtonId());
//                    Log.d("jaaja", "onCheckedChanged: "+checkedRadioButton.getTag());
//                    Log.d("jaaja", "onCheckedChanged: "+ListaRadioGrp.get(i).get(k).getTag());
                    radioGrp.put(ListaRadioGrp.get(i).get(k).getTag().toString(), checkedRadioButton.getTag());
                }
                FormatoTemp.put("RadioGrp", radioGrp);

                JSONObject checkBox = new JSONObject();
                for (int k = 0; k < ListaCheckBox.get(i).size(); k++) {
                    checkBox.put(ListaCheckBox.get(i).get(k).getTag().toString(), ListaCheckBox.get(i).get(k).isChecked());
                }
                FormatoTemp.put("CheckBox", checkBox);

                List<String> auxNombresFotos = new ArrayList<String>();
                for (int j = 0; j < ListaUriFotosAnexas.get(i).size(); j++) {
                    for (int k = 0; k < ListaUriFotosAnexas.get(i).get(j).size(); k++) {
                        try {
                            auxNombresFotos.add(ListaNombresFotosFormatos.get(i).get(j).get(k));

                            InputStream iStream = mcont.getContentResolver().openInputStream(ListaUriFotosAnexas.get(i).get(j).get(k));
                            byte[] inputData = getBytes(iStream);

                            File rutaArchivo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Estaciones/");
                            if (!rutaArchivo.exists()) { //sino existe, se crea
                                rutaArchivo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/Estaciones/");
                                rutaArchivo.mkdirs();//creamos
                            }

                            try {
                                File files = new File(rutaArchivo, ListaNombresFotosFormatos.get(i).get(j).get(k));
                                if (files.exists()) {
                                    files.delete();//si este archivo existe, con ese nombre, lo reemplazará
                                }

                                FileOutputStream  out = new FileOutputStream(files);
                                out.write(inputData);
                                out.close();

                            } catch (Exception ex) {
                                Toast.makeText(mcont, "Ocurrió un error al guardar la imagen\n", Toast.LENGTH_LONG).show();
                                Log.e("ErrorImagen1", "ex: " + ex);
                            }
                        }
                        catch (Exception e) {
                            Toast.makeText(mcont, "Ocurrió un error al guardar la imagen\n", Toast.LENGTH_LONG).show();
                            Log.e("ErrorImagen2", "e: " + e);
                        }
                    }
                }

                FormatoTemp.put("FotosAnexasForm", auxNombresFotos.toString());


                FormatosList.put("Form_INVENTARIO_"+countFormatosINV, FormatoTemp);
                countFormatosINV++;
            }

        }
        countFormatos.put("UGS_Rocas", countFormatosUGSRocas);
        countFormatos.put("UGS_Suelos", countFormatosUGSSuelos);
        countFormatos.put("SGMF", countFormatosSGMF);
        countFormatos.put("CATALOGO", countFormatosCAT);
        countFormatos.put("INVENTARIO", countFormatosINV);

        FormatosList.put("counts", countFormatos);
        attrForm.put("Formularios", FormatosList);



        auxTextExist = true;

        formComplete.put(attrForm);
        Log.d("jaaja", "GuardarForm3: "+formComplete);
        Log.d("jaaja", "GuardarForm1: "+listFormularios);
        Log.d("jaaja", "GuardarForm2: "+attrForm);

        try {
            OutputStreamWriter file = new OutputStreamWriter(mcont.openFileOutput("listaForm.txt", Activity.MODE_PRIVATE));
            file.write(String.valueOf(formComplete));
            file.flush();
            file.close();
            Toast.makeText(mcont, "Formulario Guardado\n", Toast.LENGTH_LONG).show();
        }
        catch (Exception ex) {
            Toast.makeText(mcont, "Ocurrió un error y no se pudo guardar el formulario\n", Toast.LENGTH_LONG).show();
            Log.e("Error12", "ex: " + ex);
        }

        try {
            File rutaArchivo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Estaciones/");
            if (!rutaArchivo.exists()) { //sino existe, se crea
                rutaArchivo = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/Estaciones/");
                rutaArchivo.mkdirs();//creamos
            }

            try {
                File files = new File(rutaArchivo, "DatosEstacionesSGC_"+s+ ".txt");
//                File files = new File(rutaArchivo, "DatosEstaciones_"+s.toString() + "_NO-ES-UNA-IMAGEN.png");
                if (files.exists()) {
                    files.delete();//si este archivo existe, con ese nombre, lo reemplazará
                }

                FileOutputStream  out = new FileOutputStream(files);
                PrintWriter pw = new PrintWriter(out);
                pw.println(String.valueOf(formComplete));
                pw.flush();
                pw.close();
                out.close();

            } catch (Exception ex) {
                Log.e("Error12", "ex: " + ex);
            }
            try {
                File files = new File(rutaArchivo, "DatosEstaciones_"+s+ "_NO-ES-UNA-IMAGEN.png");
                if (files.exists()) {
                    files.delete();//si este archivo existe, con ese nombre, lo reemplazará
                }

                FileOutputStream  out = new FileOutputStream(files);
                PrintWriter pw = new PrintWriter(out);
                pw.println(String.valueOf(formComplete));
                pw.flush();
                pw.close();
                out.close();

            } catch (Exception ex) {
                Log.e("Error12", "ex: " + ex);
            }
        }
        catch (Exception e) {
            Log.e("Error13", "e: " + e);
        }
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
        listaElementosUGSFotosAnexas.add(new ElementoFormato( "Subir Foto",  "button",  "SubirFotosAnexas", 0));


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
        listaElementosUGSS.add(new ElementoFormato( "Granulometria de los Clastos",  "multitext",  "granulometria", R.array.Granulometria1));
        listaElementosUGSS.add(new ElementoFormato( "Forma de los Clastos",  "radiocheck",  "forma", R.array.Forma1));
        listaElementosUGSS.add(new ElementoFormato( "Redondez de los Clastos",  "radiocheck",  "redondez", R.array.Redondez1));
        listaElementosUGSS.add(new ElementoFormato( "Orientacion de los Clastos",  "radiobtn",  "orientacion", R.array.OrientacionClastos1));
        listaElementosUGSS.add(new ElementoFormato( "Meteorizacion de los Clastos",  "radiobtn",  "meteorizacionclastos", R.array.MeteorizacionClastos1));
        listaElementosUGSS.add(new ElementoFormato( "CARACTERÍSTICAS DE LA MATRIZ",  "titulo",  "", 0));
        listaElementosUGSS.add(new ElementoFormato( "Granulometría de la Matriz",  "multitext",  "granulometriamatriz", R.array.GranulometriaMatriz1));
        listaElementosUGSS.add(new ElementoFormato( "Gradacion de la Matriz",  "radiobtn",  "gradacion", R.array.Gradacion1));
        listaElementosUGSS.add(new ElementoFormato( "Seleccion de la Matriz",  "radiobtn",  "seleccion", R.array.Seleccion1));
        listaElementosUGSS.add(new ElementoFormato( "Plasticidad de la Matriz",  "radiobtn",  "plasticidad", R.array.Plasticidad1));
        listaElementosUGSS.add(new ElementoFormato( "SUELOS FINOS",  "titulo",  "", 0));
        listaElementosUGSS.add(new ElementoFormato( "RESISTENCIA AL CORTE NO DRENADO kN/m2 (CONSISTENCIA)",  "radiobtn",  "resiscorte", R.array.ResistenciaAlCorte1));
        listaElementosUGSS.add(new ElementoFormato( "SUELOS GRUESOS",  "titulo",  "", 0));
//        listaElementosUGSS.add(new ElementoFormato( "Forma de la Matriz",  "radiocheck",  "formasuelosgruesos", R.array.FormaSuelos1));
//        listaElementosUGSS.add(new ElementoFormato( "Redondez de la Matriz",  "radiocheck",  "redondezsuelosgruesos", R.array.RedondezSuelos1));
//        listaElementosUGSS.add(new ElementoFormato( "Orientación de la Matriz",  "radiobtn",  "orientacionsuelosgruesos", R.array.OrientacionSuelos1));
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
        listaElementosNuevoSGMF.add(new ElementoFormato( "TIPOS DE MM, TMM","ambientes","tipodemm",R.array.TipoMMGMF));
        listaElementosNuevoSGMF.add(new ElementoFormato( "TIPO DE MATERIAL ASOCIADO, TMA","ambientes","tipomaterial",R.array.TipoMaterialGMF));
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

        listaElementosINV.add(new ElementoFormato("ID PARTE del MM","edittextMM","ID_PARTE", 0));
        listaElementosINV.add(new ElementoFormato("IMPORTANCIA","spinnerMM","IMPORTANC",R.array.Importancia));
        listaElementosINV.add(new ElementoFormato("ENCUESTADOR","edittextMM","ENCUESTAD",0));
        listaElementosINV.add(new ElementoFormato("FECHA EVENTO","edittextMM","FECHA_MOV",0));
        listaElementosINV.add(new ElementoFormato("FUENTE FECHA EVENTO","spinnerMM","FECHA_FUENTE",R.array.FuenteFechaEvento));
        listaElementosINV.add(new ElementoFormato("CONFIABILIDAD FECHA EVENTO","spinnerMM","ConfiFechaMM",R.array.ConfiFecha));
        listaElementosINV.add(new ElementoFormato("FECHA REPORTE","edittextMM","FECHA_REP",0));
//        listaElementosINV.add(new ElementoFormato("INSTITUCIÓN","edittext","INSTITUC",0));
        listaElementosINV.add(new ElementoFormato("SIMMA","edittextMM","COD_SIMMA",0));
        listaElementosINV.add(new ElementoFormato("Municipio",  "spinnerMM",  "NOM_MUN", R.array.Municipios));
        listaElementosINV.add(new ElementoFormato("Vereda",  "edittextMM",  "VEREDA", 0));
        listaElementosINV.add(new ElementoFormato("Sitio","edittext","SITIO",0));
        listaElementosINV.add(new ElementoFormato("REFERENCIA GEOGRÁFICA","edittextMM","REF_GEOGRF",0));
        listaElementosINV.add(new ElementoFormato("DOCUMENTACIÓN",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("PLANCHAS","edittext","planchas",0));
        listaElementosINV.add(new ElementoFormato("SENSORES REMOTOS","edittext","sensoresremotos",0));
        listaElementosINV.add(new ElementoFormato("FOTOGRAFÍAS AÉREAS","edittextMM","FTE_INFSEC",0));
        listaElementosINV.add(new ElementoFormato("ACTIVIDAD DEL MOVIMIENTO",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("EDAD","spinner","edadmm",R.array.EdadMM));
        listaElementosINV.add(new ElementoFormato("ESTADO","spinnerMM","ESTADO_ACT",R.array.EstadoMM));
        listaElementosINV.add(new ElementoFormato("ESTILO","spinnerMM","ESTILO",R.array.EstiloMM));
        listaElementosINV.add(new ElementoFormato("DISTRIBUCIÓN","spinnerMM","DISTRIBUC",R.array.DistribucionMM));
        listaElementosINV.add(new ElementoFormato("LITOLOGIA Y ESTRUCTURA",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("DESCRIPCIÓN (Incuir minimo origen de la roca,(I,M ó S) Edad, Fm, Litologia y estratigrafia, suelos)","edittextMM","LITOLOGIA",0));
        listaElementosINV.add(new ElementoFormato("ESTRUCTURA","estructuras","estructura",R.array.EstructurasMM));
        listaElementosINV.add(new ElementoFormato("CLASIFICACIÓN DEL MOVIMIENTO",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("TIPO MOVIMIENTO",  "radiobtnMM",  "TIPO_MOV", R.array.TipoMovimiento));
        listaElementosINV.add(new ElementoFormato("SUBTIPO PRIMER MOVIMIENTO",  "spinnerMM",  "SUBTIPO_1", R.array.SubtipoMovimiento));
        listaElementosINV.add(new ElementoFormato("SUBTIPO SEGUNDO MOVIMIENTO",  "spinnerMM",  "SUBTIPO_2", R.array.SubtipoMovimiento1));
        listaElementosINV.add(new ElementoFormato("TIPO MATERIAL",  "radiocheckMM",  "tipomaterial", R.array.TipoMaterialMM));
        listaElementosINV.add(new ElementoFormato("HUMEDAD",  "radiobtn",  "humedad", R.array.HumedadMM));
        listaElementosINV.add(new ElementoFormato("PLASTICIDAD",  "radiobtn",  "plasticidad", R.array.PlasticidadMM));
        listaElementosINV.add(new ElementoFormato("ORIGEN SUELO",  "origensuelo",  "origensuelo", R.array.OrigenSueloMM));
        listaElementosINV.add(new ElementoFormato("VELOCIDAD",  "spinner",  "velocidad", R.array.VelocidadMM));
        listaElementosINV.add(new ElementoFormato("VELOCIDAD MÁXIMA","edittext","velocidadmax",0));
        listaElementosINV.add(new ElementoFormato("VELOCIDAD MÍNIMA","edittext","velocidadmin",0));
        listaElementosINV.add(new ElementoFormato("SISTEMA DE CLASIFICACIÓN",  "spinner",  "sisclasificacion", R.array.SisClasificacionMM));
        listaElementosINV.add(new ElementoFormato("MORFOMETRÍA",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("GENERAL",  "multitext",  "morfogeneral", R.array.GeneralMM));
        listaElementosINV.add(new ElementoFormato("DIMENSIONES DEL TERRENO",  "multitext",  "morfodimensiones", R.array.DimenTerrenoMM));
        listaElementosINV.add(new ElementoFormato("DEFORMACIÓN TERRENO",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("MODO",  "spinner",  "morfomodo", R.array.ModoMM));
        listaElementosINV.add(new ElementoFormato("SEVERIDAD",  "spinner",  "morfoseveridad", R.array.SeveridadMM));
        listaElementosINV.add(new ElementoFormato("GEOFORMA",  "edittextMM",  "AN_GMF", 0));
        listaElementosINV.add(new ElementoFormato("CAUSAS DEL MOVIMIENTO",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("INHERENTES",  "radiocheck",  "causasinherentes", R.array.InherentesMM));
        listaElementosINV.add(new ElementoFormato("CONTRIBUYENTES - DETONANTES",  "radiocheckMM",  "causascontrideto", R.array.ContriDetonantesMM));
        listaElementosINV.add(new ElementoFormato("TIPO DE EROSIÓN",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("SUPERFICIAL",  "radiocheck",  "erosionsuperficial", R.array.ErosionSuperficialMM));
        listaElementosINV.add(new ElementoFormato("SUBSUPERFICIAL",  "radiocheck",  "erosionsubsuperficial", R.array.ErosionSubsuperficialMM));
        listaElementosINV.add(new ElementoFormato("EDAD",  "spinner",  "erosionedad", R.array.ErosionEdadMM));
        listaElementosINV.add(new ElementoFormato("ESTADO",  "spinner",  "erosionestado", R.array.ErosionEstadoMM));
        listaElementosINV.add(new ElementoFormato("FLUVIAL",  "spinner",  "erosionfluvial", R.array.ErosionFluvialMM));
        listaElementosINV.add(new ElementoFormato("EOLICA",  "spinner",  "erosioneolica", R.array.ErosionEolicaMM));
        listaElementosINV.add(new ElementoFormato("COBERTURA Y USO DEL SUELO",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("COBERTURA DEL SUELO",  "multitext",  "cobertura", R.array.CoberturaSueloMM));
        listaElementosINV.add(new ElementoFormato("USO DEL SUELO",  "multitext",  "usosuelo", R.array.UsoSueloMM));
        listaElementosINV.add(new ElementoFormato("REFERENCIAS",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("AUTOR","edittext","referenciasautor",0));
        listaElementosINV.add(new ElementoFormato("AÑO","edittext","referenciasaño",0));
        listaElementosINV.add(new ElementoFormato("TITULO","edittext","referenciastitulo",0));
        listaElementosINV.add(new ElementoFormato("EDITOR","edittext","referenciaseditor",0));
        listaElementosINV.add(new ElementoFormato("CIUDAD","edittext","referenciasciudad",0));
        listaElementosINV.add(new ElementoFormato("PAGINAS","edittext","referenciaspaginas",0));
        listaElementosINV.add(new ElementoFormato("EFECTOS SECUNDARIOS: REPRESAMIENTO",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("TIPO (Costa & Schuster, 1988)",  "spinner",  "represamientotipo", R.array.TipoEfectoSecMM));
        listaElementosINV.add(new ElementoFormato("MORFOMETRÍA DEL EMBALSE",  "multitext",  "represamientomorfoembalse", R.array.MorfometriaEmbalse));
        listaElementosINV.add(new ElementoFormato("MORFOMETRÍA DE LA PRESA",  "multitext",  "represamientomorfometria", R.array.MorfometriaPresaMM));
        listaElementosINV.add(new ElementoFormato("CONDICIONES DE LA PRESA",  "radiocheck",  "represamientocondiciones", R.array.CondicionesPresa));
        listaElementosINV.add(new ElementoFormato("EFECTOS",  "radiocheck",  "represamientoefectos", R.array.EfectosPresa));
        listaElementosINV.add(new ElementoFormato("POBLACION AFECTADA",  "titulo",  "", 0));
        listaElementosINV.add(new ElementoFormato("HERIDOS","edittext","heridos",0));
        listaElementosINV.add(new ElementoFormato("VIDAS","edittext","vidas",0));
        listaElementosINV.add(new ElementoFormato("DESAPARECIDOS","edittext","desaparecidos",0));
        listaElementosINV.add(new ElementoFormato("PERSONAS","edittext","personas",0));
        listaElementosINV.add(new ElementoFormato("FAMILIAS","edittext","familias",0));
        listaElementosINV.add(new ElementoFormato("NOTAS","edittext","notas",0));
        listaElementosINV.add(new ElementoFormato("APRECIACIÓN DEL RIESGO","edittext","apreciacionriesgo",0));
        listaElementosINV.add(new ElementoFormato("DAÑOS A INFRASTRUCTURA, ACTIVIDADES ECONÓMICAS, DAÑOS AMBIENTALES:","titulo","",0));
        listaElementosINV.add(new ElementoFormato("TIPO DE DAÑO:","textview","",R.string.NotaDaños));
//        listaElementosINV.add(new ElementoFormato("ESQUEMA DEL MOVIMIENTO",  "titulo",  "", 0));
//        listaElementosINV.add(new ElementoFormato("FOTO EN PLANTA",  "edittext",  "fotoplanta", 0));
//        listaElementosINV.add(new ElementoFormato("OBSERVACIONES",  "edittext",  "fotoplantaobs", 0));
//        listaElementosINV.add(new ElementoFormato("FOTO EN PERFIL",  "edittext",  "fotoperfil", 0));
//        listaElementosINV.add(new ElementoFormato("OBSERVACIONES",  "edittext",  "fotoperfilobs", 0));

        listaElementosINVFotosAnexas.add(new ElementoFormato( "Fecha",  "edittext",  "fechaFotosAnexas", 0));
        listaElementosINVFotosAnexas.add(new ElementoFormato( "Nombre de la Foto",  "edittext",  "nombreFotosAnexasINV", 0));
        listaElementosINVFotosAnexas.add(new ElementoFormato( "Autor/Derechos",  "edittext",  "autorFotosAnexas", 0));
        listaElementosINVFotosAnexas.add(new ElementoFormato( "Observaciones",  "edittext",  "obsFotosAnexas", 0));
        listaElementosINVFotosAnexas.add(new ElementoFormato( "Agregar Foto",  "button",  "SubirFotosAnexas", 0));


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
            if (!manualLoc){
                String sLatitud = String.valueOf(loc.getLatitude());
                String sLongitud = String.valueOf(loc.getLongitude());
                String sAltitud = String.valueOf(loc.getAltitude());
                String sAccuracy = String.valueOf(loc.getAccuracy());
                etNorte.setText(sLatitud);
                etEste.setText(sLongitud);
                etAltitud.setText(sAltitud);
            }
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
