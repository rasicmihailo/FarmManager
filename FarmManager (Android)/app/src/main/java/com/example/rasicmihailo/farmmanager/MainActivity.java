package com.example.rasicmihailo.farmmanager;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;

public class MainActivity extends AppCompatActivity  {




    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private Intent intent;
    private Locale myLocale;
    private String s;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

    }

    @Override
    protected void onStart(){
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user==null){
            Intent i = new Intent(MainActivity.this, LoginActivity.class);
            i.putExtra("lang", "srb");
            startActivity(i);
           finish();


        }else{

            final String uID = user.getUid();



            firebaseFirestore.collection("Users").document(uID).get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if(task.isSuccessful())
                            {
                                if(task.getResult().exists()) {//postoji user, ima osnovne podatke
                                    String tip = task.getResult().getString("tip");
                                    String jezik = task.getResult().getString("jezik");
                                    setLocale(jezik);

                                    String zaposljen = "";
                                    if (tip.matches("radnik")) {
                                        zaposljen = task.getResult().getString("zaposljen");
                                    if(zaposljen.matches("ne")){
                                        startActivity(new Intent(MainActivity.this, NeradnikActivity.class));
                                        finish();
                                    }else{
                                        startActivity(new Intent(MainActivity.this, RadnikActivity.class));
                                        finish();
                                    }

                                    } else if (tip.matches("vlasnik")) {

                                        firebaseFirestore.collection("Gazdinstva").document(uID)
                                                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task2) {

                                                if (task2.isSuccessful()) {
                                                    if (task2.getResult().exists()) {//ima sve podatke, vlasnik je

                                                        startActivity(new Intent(MainActivity.this, VlasnikActivity.class));
                                                        finish();

                                                    } else {//postoji user, ima podatke o sebi, vlasnik je, nema podatke o gazdinstvu
                                                        startActivity(new Intent(MainActivity.this, RegistracijaVlasnikActivity.class));
                                                        finish();
                                                    }

                                                } else {
                                                    Toast.makeText(MainActivity.this, R.string.greska, Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        });

                                    }


                                }else {//postoji user, nema nikakvih podataka o sebi
                                    Intent ne = new Intent(MainActivity.this, Registracija2Activity.class);
                                    ne.putExtra("lang", s);
                                    startActivity(ne);

                                    finish();

                                }


                            }else{

                                Toast.makeText(MainActivity.this, R.string.greska, Toast.LENGTH_LONG).show();
                            }

                        }
                    });

        }
    }

    public void setLocale(String lang) {

        myLocale = new Locale(lang);

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
       // Intent refresh = new Intent(this, MainActivity.class);
       // refresh.putExtra("lang", lang);
      //  startActivity(refresh);
    }
}



