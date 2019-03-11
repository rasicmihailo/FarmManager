package com.example.rasicmihailo.farmmanager;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    EditText lozinkaTxt;
    EditText korisnickoIme;
    Button btnLog;
    ProgressBar progressBar;
    private TextView eng;
    private TextView srb;
    private Locale myLocale;
    private boolean proveriJezik;
    private static final Locale ENGLISH = null;
    private String s;
    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        mAuth = FirebaseAuth.getInstance();

        //da se korisnik prijavi nakon klika na dugme enter sa tastature
        lozinkaTxt = (EditText) findViewById(R.id.passwordTxt);
        korisnickoIme = (EditText) findViewById(R.id.emailLogin);
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        eng = (TextView) findViewById(R.id.txtEng);
        srb = (TextView) findViewById(R.id.txtSrb);

        srb.setTypeface(null, Typeface.BOLD);

        Intent isti = getIntent();
        s = isti.getStringExtra("lang");


        if(s.matches("srb")) {

            srb.setTypeface(null, Typeface.BOLD);
            eng.setTypeface(null, Typeface.NORMAL);

        }else {

            eng.setTypeface(null, Typeface.BOLD);
            srb.setTypeface(null, Typeface.NORMAL);
        }

        lozinkaTxt.setOnKeyListener(this); //zove metod onKey

        //odavde se radi da bi se sklonila tastatura nakon klika bilo gde na layout-u
        RelativeLayout najveciLayout = (RelativeLayout) findViewById(R.id.loginLayout);
        RelativeLayout manjiLayout = (RelativeLayout) findViewById(R.id.login);

        najveciLayout.setOnClickListener(this);//zove se onClick metod
        manjiLayout.setOnClickListener(this);//zove se onClick metod
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }
    }

    public void onClickRegistracija (View view) {

        Intent intentRegistracija = new Intent(getApplicationContext(), RegistracijaActivity.class);
        intentRegistracija.putExtra("lang", s);
        startActivity(intentRegistracija);

    }

    public void onClickPrijava(View view) {
         if (validacija()) {

             prijaviSe();

        }
    }

    private void prijaviSe(){

        String email = korisnickoIme.getText().toString();
        String lozinka = lozinkaTxt.getText().toString();

        progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,lozinka)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

                   startActivity(new Intent(getApplicationContext(), MainActivity.class));
                   finish();

                }else{

                    Toast.makeText(LoginActivity.this, R.string.greska, Toast.LENGTH_LONG).show();

                }

            }
        });
        progressBar.setVisibility(View.INVISIBLE);
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

    public void promeniJezikNaEng(View view){
        setLocale("eng");
    }

    public void promeniJezikNaSrb(View view){
        setLocale("srb");
    }

    public void setLocale(String lang) {

        myLocale = new Locale(lang);

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, LoginActivity.class);
        refresh.putExtra("lang", lang);
        startActivity(refresh);
    }


    //ovo se zove kad se sa tastature korisnika pritisne enter
    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
            onClickPrijava(v);
        }

        return false;
    }

    public boolean validacija(){
        if(lozinkaTxt.getText().toString().isEmpty() && korisnickoIme.getText().toString().isEmpty()) {
            Toast.makeText(this, R.string.sviPodaciLogin, Toast.LENGTH_SHORT).show();
            return false;
        } else if(korisnickoIme.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.emailLogin, Toast.LENGTH_SHORT).show();
            return false;
        }
        else if(lozinkaTxt.getText().toString().isEmpty()){
            Toast.makeText(this, R.string.lozinkaLoginAct, Toast.LENGTH_SHORT).show();
            return false;
        }else if(!validateEmailAddress(korisnickoIme.getText().toString())){
            Toast.makeText(this, R.string.emailValidacija, Toast.LENGTH_SHORT).show();
            return false;
        }
        return  true;
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.login || v.getId() == R.id.loginLayout || v.getId() == R.id.imageView2){
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }

    }

}
