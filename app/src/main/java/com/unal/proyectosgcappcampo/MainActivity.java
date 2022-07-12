package com.unal.proyectosgcappcampo;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
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

}