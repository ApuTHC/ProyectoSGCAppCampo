package com.unal.proyectosgcappcampo.ui.home;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.unal.proyectosgcappcampo.databinding.FragmentHomeBinding;

import java.io.File;
import java.io.FileOutputStream;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private ConstraintLayout contenido;
    private ImageView imagen;
    private TextView texto;

    private Context mcont = getActivity();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHome;
        homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        mcont = root.getContext();

//        contenido = binding.contenido;
//        imagen = binding.imagen;
//        texto = binding.texto;
//
//
//        contenido.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GuardarLayout();
//            }
//        });


        return root;
    }

//    private void GuardarLayout() {
//        contenido.setDrawingCacheEnabled(true);//habilitamos la función del caché
//        contenido.buildDrawingCache(); //crea el cache
//        Bitmap bmap = contenido.getDrawingCache(); //obtiene el mapa de bits del caché
//        try {
//            guardarImagenMedoto(bmap); //este método guarda la imagen en una ruta
//        } catch (Exception e) {
//            Log.d("GuardarLayout", "GuardarLayout: " + e.getMessage());
//            e.printStackTrace();
//        } finally {
//            contenido.destroyDrawingCache();
//        }
//    }
//
//    private void guardarImagenMedoto(Bitmap bitmap){
//
//        File rutaArchivo = new File(Environment.getExternalStorageDirectory() + "/Pictures/FotosAppProyectoSGC/");
//        if (!rutaArchivo.exists()) { //sino existe, se crea
//            rutaArchivo = new File(Environment.getExternalStorageDirectory() + "/Pictures/FotosAppProyectoSGC/");
//            Log.d("guardarImagenMedoto", "crearDir: "+rutaArchivo);
//            rutaArchivo.mkdirs();//creamos
//        }
//        Log.d("guardarImagenMedoto", "guardarImagenMedoto: "+rutaArchivo);
//        File archivo = new File(rutaArchivo, "tuImagenNombre" + ".jpg"); //crearemos el archivo en la ruta y con un nombre
//        if (archivo.exists()) {
//            archivo.delete();//si este archivo existe, con ese nombre, lo reemplazará
//        }
//        try {
//
//            FileOutputStream out = new FileOutputStream(archivo);
//            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);//creamos la imagen en formato jpg, con la calidad 100
//            out.flush();
//            out.close();
//            Toast.makeText(mcont, "¡Se ha guardado con éxito tu imagen!", Toast.LENGTH_SHORT).show();
//            Log.d("guardarImageb", "try: " + "\nRuta: " + rutaArchivo + "\nArchivo: " + archivo); //a traves del logd voy controlando si se crea o no el archivo viéndolo desde el logcat
//        } catch (Exception e) {
//            e.printStackTrace();
//            Toast.makeText(mcont, "¡Ha ocurrido un error al intentar guardar tu imagen!", Toast.LENGTH_SHORT).show();
//            Log.d("guardarImagen", "Catch: " + e.getMessage() + "\nRuta: " + rutaArchivo + "\nArchivo: " + archivo);
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}