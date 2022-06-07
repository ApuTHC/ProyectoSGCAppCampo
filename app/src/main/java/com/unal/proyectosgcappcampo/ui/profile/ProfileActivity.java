package com.unal.proyectosgcappcampo.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.squareup.picasso.Picasso;
import com.unal.proyectosgcappcampo.MainActivity;
import com.unal.proyectosgcappcampo.R;
import com.unal.proyectosgcappcampo.databinding.ActivityProfileBinding;


public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;


    private static final int RC_SING_IN = 100;
    private GoogleSignInClient googleSignInClient;

    private FirebaseAuth firebaseAuth;

    private static final String TAG = "GOOGLE_SIGN_IN_TAG";

    private Context mCont=this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Configure the Google Signin
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        //Init firebase auth
        firebaseAuth = FirebaseAuth.getInstance();
        checkUser();

        //Google SignInButton: click to begin Google SignIn
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //begin google sign in
                Log.d(TAG, "onClick: Begin Google SignIn");
                Toast.makeText(mCont, "Conectando\n", Toast.LENGTH_SHORT).show();
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SING_IN);
            }
        });

        //handle click Logout
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseAuth.signOut();
                Toast.makeText(mCont, "Logout Finished...\n", Toast.LENGTH_SHORT).show();
                checkUser();
                startActivity(new Intent(mCont, MainActivity.class));
                finish();
            }
        });

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

            binding.userNameFragment.setText(name);
            binding.emailUserFragment.setText(email);

            binding.btnLogout.setVisibility(View.VISIBLE);
            binding.btnLogin.setVisibility(View.GONE);

            Picasso.get()
                    .load(photo)
                    .into(binding.userPhotoProfile);


        }
        else {
            binding.userNameFragment.setText(R.string.nav_header_name);
            binding.emailUserFragment.setText(R.string.nav_header_email);
            binding.btnLogout.setVisibility(View.GONE);
            binding.btnLogin.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SING_IN){
            Log.d(TAG, "onActivityResult: Google Signin intent result");

            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);
            }
            catch (Exception e){
                Log.d(TAG, "onActivityResult: "+e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthWithGoogleAccount: Begin firebase auth with google account");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(),null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //login success
                        Log.d(TAG, "onSuccess: Logged In");
                        //get logged in user
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        //get user info
                        String uid = firebaseUser.getUid();
                        String email = firebaseUser.getEmail();
                        String name = firebaseUser.getDisplayName();
                        Uri photo = firebaseUser.getPhotoUrl();

                        Log.d(TAG, "onSuccess: UID: "+uid);
                        Log.d(TAG, "onSuccess: Email: "+email);
                        Log.d(TAG, "onSuccess: Name: "+name);

                        binding.userNameFragment.setText(name);
                        binding.emailUserFragment.setText(email);

                        Picasso.get()
                                .load(photo)
                                .into(binding.userPhotoProfile);

                        binding.btnLogout.setVisibility(View.VISIBLE);
                        binding.btnLogin.setVisibility(View.GONE);

                        //check if user is new or existing
                        if (authResult.getAdditionalUserInfo().isNewUser()){
                            //user is new - Account Created
                            Log.d(TAG, "onSuccess: Account Created..."+email);
                            Toast.makeText(mCont, "Account Created...\n"+email, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //Existing user - Logged In
                            Log.d(TAG, "onSuccess: Existing user..."+email);
                            Toast.makeText(mCont, "Existing user...\n"+email, Toast.LENGTH_SHORT).show();
                        }

                        //start activity
                        startActivity(new Intent(mCont, MainActivity.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //login failed
                        Log.d(TAG, "onSuccess: Logged failed "+e.getMessage());
                    }
                });
    }
}