package com.unal.proyectosgcappcampo;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.unal.proyectosgcappcampo.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;


public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseAuth firebaseAuth;

    TextView mainNameUser;
    TextView mainEmailUser;
    ImageView mainPhotoUser;

    private static final String TAG = "GOOGLE_SIGN_IN_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMain.toolbar);
//        binding.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_slideshow,R.id.guardadasFragment,R.id.planillasCoberturas, R.id.nav_gallery, R.id.nav_home, R.id.nav_profile)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        mainNameUser = (TextView) binding.navView.getHeaderView(0).findViewById(R.id.userName);
        mainEmailUser = (TextView) binding.navView.getHeaderView(0).findViewById(R.id.userEmail);
        mainPhotoUser = (ImageView) binding.navView.getHeaderView(0).findViewById(R.id.userPhoto);

        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

    }

    private void checkUser() {
        //if user is already signed
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            String uid = firebaseUser.getUid();
            String email = firebaseUser.getEmail();
            String name = firebaseUser.getDisplayName();
            Uri photo = firebaseUser.getPhotoUrl();

            Log.d(TAG, "onSuccess: UID: "+uid);
            Log.d(TAG, "onSuccess: Email: "+email);
            Log.d(TAG, "onSuccess: Name: "+name);

            mainNameUser.setText(name);
            mainEmailUser.setText(email);

            Picasso.get()
                    .load(photo)
                    .into(mainPhotoUser);
        }
        else {
            mainNameUser.setText(R.string.nav_header_name);
            mainEmailUser.setText(R.string.nav_header_email);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if(keyCode == event.KEYCODE_BACK){
//            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//            builder.setTitle("¡ Un Momento !");
//            builder.setMessage("¿Desea salir de la Aplicación?")
//                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Intent intent = new Intent(Intent.ACTION_MAIN);
//                            intent.addCategory(Intent.CATEGORY_HOME);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            startActivity(intent);
//                            dialog.dismiss();
//                            MainActivity.this.finish();
//                        }
//                    })
//                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            dialog.dismiss();
//                        }
//                    });
//            builder.show();
//        }
//        return super.onKeyDown(keyCode, event);
//    }


    @Override
    public void onBackPressed() {

        int count = getSupportFragmentManager().getBackStackEntryCount();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);

        Log.d("TAGis", "onBackPressed: "+navController.getCurrentDestination().getDisplayName());

        if (navController.getCurrentDestination().getDisplayName().toString().equals("com.unal.proyectosgcappcampo:id/nav_slideshow")){
            doExit();
        }else{
            super.onBackPressed();
        }


//        int count = getFragmentManager().getBackStackEntryCount();
//
//            Log.d("TAGis", "onBackPressed: "+count);
//        if (count == 0) {
//            super.onBackPressed();
//            getFragmentManager().popBackStack();
//        } else {
//            getFragmentManager().popBackStack();//No se porqué puse lo mismo O.o
//        }

    }

    public void doExit() {

            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("¡Un Momento!");
            builder.setMessage("¿Desea salir de la Aplicación?")
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_MAIN);
                            intent.addCategory(Intent.CATEGORY_HOME);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            dialog.dismiss();
                            MainActivity.this.finish();
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();


    }
}