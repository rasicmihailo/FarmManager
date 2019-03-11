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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Registracija2Activity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {
    Switch aSwitchPosao;

    private RelativeLayout layout3;
    private RelativeLayout layout2;
    private RelativeLayout layout1;

    private EditText ime;
    private EditText prezime;
    private EditText datumRodj;
    private RadioButton zemljoradnik;
    private RadioButton stocar;
    private RadioButton mehanicar;
    private ProgressBar progressBar;
    private Intent intent;
    private String jezik;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registracija2);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        intent = getIntent();
        jezik = intent.getStringExtra("lang");



        aSwitchPosao = (Switch) findViewById(R.id.switchPosao);

        ime = (EditText) findViewById(R.id.editTextIme);
        prezime = (EditText) findViewById(R.id.editTextPrezime);
        datumRodj = (EditText) findViewById(R.id.editTextDatum);
        zemljoradnik = (RadioButton) findViewById(R.id.radioButtonZemljoradnik);
        stocar = (RadioButton) findViewById(R.id.radioButtonStocar);
        mehanicar = (RadioButton) findViewById(R.id.radioButtonMehanicar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar3);

        datumRodj.setOnKeyListener(this);


        layout1= (RelativeLayout)findViewById(R.id.relativeNajveci);
        layout2= (RelativeLayout) findViewById(R.id.relativeSrednji);
        layout3 = (RelativeLayout) findViewById(R.id.relativeNajmanji);

        layout1.setOnClickListener(this);
        layout2.setOnClickListener(this);
        layout3.setOnClickListener(this);


    }

    private void registrujKorisnika(){

        String uID = mAuth.getCurrentUser().getUid();

        String ime_user = ime.getText().toString();
        String prezime_user = prezime.getText().toString();
        String datum_user = datumRodj.getText().toString();
        final String tip ;
        String posao = "";
        if(aSwitchPosao.isChecked()){
            tip = "radnik";
            if(zemljoradnik.isChecked())
                posao="zemljoradnik";
            else if(stocar.isChecked())
                posao = "stocar";
            else if(mehanicar.isChecked())
                    posao = "mehanicar";
        }else{
            tip="vlasnik";
        }

        Map<String, String> mapObjekat = new HashMap<>();

        mapObjekat.put("ime", ime_user);
        mapObjekat.put("prezime", prezime_user);
        mapObjekat.put("datum", datum_user);
        mapObjekat.put("tip", tip);
        if(tip.matches("radnik")){
            mapObjekat.put("zanimanje", posao);
            mapObjekat.put("zaposljen", "ne");
        }


        mapObjekat.put("jezik", jezik);


        progressBar.setVisibility(View.VISIBLE);

        firebaseFirestore.collection("Users").document(uID).set(mapObjekat)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){

                                if(tip.matches("radnik")){

                                startActivity(new Intent(Registracija2Activity.this, MainActivity.class));
                                finish();

                                }else{

                                    startActivity(new Intent(Registracija2Activity.this,MainActivity.class ));
                                    finish();
                                }
                            }else{

                                Toast.makeText(Registracija2Activity.this, R.string.greska, Toast.LENGTH_LONG).show();
                            }
                                progressBar.setVisibility(View.INVISIBLE);
                        }
                    });


    }



    public void onClickPosao (View view){
        if(aSwitchPosao.isChecked()){
             layout3.setVisibility(View.VISIBLE);
        }
        else {
            layout3.setVisibility(View.INVISIBLE);
        }
    }


    public void onClickRegistrujSe(View view){
        if(validacija()) {

                registrujKorisnika();

        }
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            onClickRegistrujSe(v);
        }
        return false;
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == R.id.relativeNajmanji || v.getId() == R.id.relativeNajveci || v.getId()==R.id.relativeSrednji){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }


    public boolean validacija(){

        if(ime.getText().toString().isEmpty() && prezime.getText().toString().isEmpty() && datumRodj.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.sviPodaciReg2, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(ime.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.imeReg2, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(prezime.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.prezimeReg2, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(datumRodj.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.datumReg2, Toast.LENGTH_SHORT).show();
            return false;
        }else if(aSwitchPosao.isChecked())
                     if(!zemljoradnik.isChecked() && !stocar.isChecked() && !mehanicar.isChecked()){
            Toast.makeText(this, R.string.delatnostReg2, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
