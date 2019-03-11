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
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistracijaActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {


    private EditText e_mail;
    private EditText lozinka;
    private EditText potvrdaLozinke;
    private ProgressBar progressBar;
    private  String s;
    private Intent isti;
    private FirebaseAuth mAuth;
    private Intent reg2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registracija);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        e_mail= (EditText) findViewById(R.id.editTextEmail);
        lozinka = (EditText) findViewById(R.id.editTextLozinka);
        potvrdaLozinke = (EditText) findViewById(R.id.editTextLozinkaPonovo);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        isti = getIntent();
        s = isti.getStringExtra("lang");


        potvrdaLozinke.setOnKeyListener(this);


        //odavde se radi da bi se sklonila tastatura nakon klika bilo gde na layout-u
        RelativeLayout najveciLayout = (RelativeLayout) findViewById(R.id.Relative1);
        RelativeLayout manjiLayout = (RelativeLayout) findViewById(R.id.Relative2);

        najveciLayout.setOnClickListener(this);//zove se onClick metod
        manjiLayout.setOnClickListener(this);//zove se onClick metod

    }


    @Override
    protected void onStart(){
        super.onStart();

         FirebaseUser user = mAuth.getCurrentUser();


        if(user!=null){
            startActivity(new Intent(RegistracijaActivity.this, MainActivity.class));
            finish();

        }

    }

    private void registruj(){

        String email = e_mail.getText().toString();
        String lozinkaPotvrda = potvrdaLozinke.getText().toString();

        progressBar.setVisibility(View.VISIBLE);

        mAuth.createUserWithEmailAndPassword(email, lozinkaPotvrda)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            reg2= new Intent(RegistracijaActivity.this, Registracija2Activity.class);
                            reg2.putExtra("lang", s);
                            startActivity(reg2);

                            finish();

                        } else {
                            Toast.makeText(RegistracijaActivity.this, R.string.greska, Toast.LENGTH_LONG).show();

                        }

                        progressBar.setVisibility(View.INVISIBLE);

                    }

                });


    }


    public void onClickRegIVer (View view)  {
        if (validacija()) {

            registruj();
        }
    }

    public boolean validacija(){
        if(e_mail.getText().toString().isEmpty() && lozinka.getText().toString().isEmpty()
                && potvrdaLozinke.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.sviPodaciReg, Toast.LENGTH_SHORT).show();
            return false;
        }else if(e_mail.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.emailRegi, Toast.LENGTH_SHORT).show();
            return false;
        }else if(lozinka.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.lozReg, Toast.LENGTH_SHORT).show();
            return false;
        }   else if(potvrdaLozinke.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.lozPotReg, Toast.LENGTH_SHORT).show();
            return false;
        }else if(lozinka.getText().length() < 6){
            Toast.makeText(this, R.string.lozViseSest, Toast.LENGTH_SHORT).show();
            return false;
        }else if(!lozinka.getText().toString().matches(potvrdaLozinke.getText().toString())){
            Toast.makeText(this, R.string.lozPoklapanje, Toast.LENGTH_SHORT).show();
            return false;
        }else if(!validateEmailAddress(e_mail.getText().toString())){
            Toast.makeText(this, R.string.emailValidacijaReg, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean validateEmailAddress(String emailAddress) {

        Pattern regexPattern = Pattern.compile("^[(a-zA-Z-0-9-\\_\\+\\.)]+@[(a-z-A-z)]+\\.[(a-zA-z)]{2,3}$");
        Matcher regMatcher   = regexPattern.matcher(emailAddress);
        if(regMatcher.matches()) {
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction()==KeyEvent.ACTION_DOWN){
                onClickRegIVer(v);

        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.Relative1 || v.getId() == R.id.Relative2){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}
