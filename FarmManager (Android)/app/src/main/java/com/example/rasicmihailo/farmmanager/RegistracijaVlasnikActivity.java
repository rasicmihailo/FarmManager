package com.example.rasicmihailo.farmmanager;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistracijaVlasnikActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    private RelativeLayout layout1;
    private RelativeLayout layout2;

    private EditText naziv;
    private EditText lokacija;
    private EditText finansije;
    private ProgressBar progressBar;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private String uID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registracija_vlasnik);

        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        uID = mAuth.getCurrentUser().getUid();

        layout1 = (RelativeLayout) findViewById(R.id.relativeVeci);
        layout2 = (RelativeLayout) findViewById(R.id.relativeManji);
        progressBar = (ProgressBar) findViewById(R.id.progressBar4);

        layout1.setOnClickListener(this);
        layout2.setOnClickListener(this);

        naziv = (EditText) findViewById(R.id.editTextNaziv);
        lokacija = (EditText) findViewById(R.id.editTextLokacija);
        finansije = (EditText) findViewById(R.id.editTextFinansijskoStanje);

        finansije.setOnKeyListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user == null){
            startActivity(new Intent(RegistracijaVlasnikActivity.this, MainActivity.class));
            finish();
        }

    }

    public void onClickKreiraj(View view) {
        if (validacija()) {
            kreirajGazdinstvo();
        }
    }

    private  void kreirajGazdinstvo(){

        String naziv_user = naziv.getText().toString();
        String lokacija_user = lokacija.getText().toString();
        String finansije_user = finansije.getText().toString();

        Map<String, String> mapObjekat = new HashMap<>();

        mapObjekat.put("naziv", naziv_user);
        mapObjekat.put("lokacija", lokacija_user);
        mapObjekat.put("finansije", finansije_user);

        progressBar.setVisibility(View.VISIBLE);

        firebaseFirestore.collection("Gazdinstva").document(uID).set(mapObjekat)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){

                            startActivity(new Intent(RegistracijaVlasnikActivity.this, VlasnikActivity.class));
                            finish();

                        }else{

                            Toast.makeText(RegistracijaVlasnikActivity.this, R.string.greska, Toast.LENGTH_LONG).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.relativeManji || v.getId() == R.id.relativeVeci){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
            onClickKreiraj(v);
        }
        return false;
    }

    public boolean validacija(){
        if(naziv.getText().toString().isEmpty() && lokacija.getText().toString().isEmpty() && finansije.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.sviPodaciVlasnik, Toast.LENGTH_SHORT).show();
            return false;
        } else if(naziv.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.nazivGazd, Toast.LENGTH_SHORT).show();
            return false;
        }   else if(lokacija.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.lokacijaVlasnik, Toast.LENGTH_SHORT).show();
            return false;
        }   else if(finansije.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.finansijeVlasnik, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
