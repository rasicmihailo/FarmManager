package com.example.rasicmihailo.farmmanager;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Locale;


public class VlasnikActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FragmentManager fragmentManager = getFragmentManager();

    private FirebaseAuth mAuth;
    private TextView ime2;
    private TextView email2;
    private Locale myLocale;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_vlasnik);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Farm Manager");


        NavigationView nv = (NavigationView) findViewById(R.id.nav_view);
        View hView =  nv.getHeaderView(0);

        ime2 = (TextView) hView.findViewById(R.id.ime);
        email2 = (TextView) hView.findViewById(R.id.email);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager.beginTransaction()
                .replace(R.id.vlasnik_nav, new Poslovi())
                .commit();


        firebaseFirestore.collection("Users").document(mAuth.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){

                            if(task.getResult().exists()){

                                String iB = task.getResult().getString("ime");
                                String prez = task.getResult().getString("prezime");
                                ime2.setText(iB + " " + prez);

                            }else{
                                Toast.makeText(VlasnikActivity.this, "NE POSTOJI", Toast.LENGTH_LONG).show();
                            }

                        }else{



                        }

                    }
                });

        String eB = mAuth.getCurrentUser().getEmail();
        email2.setText(eB);


        }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();

        if(user==null){
            startActivity(new Intent(VlasnikActivity.this, MainActivity.class));
            finish();
        }


    }


    public void promeniJezik(){

        azurirajJezik();

        firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){

                            String jezik = task.getResult().getString("jezik");

                            if(jezik.matches("srb"))
                                setLocale("eng");
                            else{
                                setLocale("srb");
                            }
                        }

                    }
                });


    }

    public void azurirajJezik(){
        firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){

                            String jezik = task.getResult().getString("jezik");

                            if(jezik.matches("srb")) {
                                firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).update("jezik", "eng");

                            }
                            else{
                                firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).update("jezik", "srb");


                            }
                        }

                    }
                });

    }


    public void setLocale(String lang) {

        myLocale = new Locale(lang);

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, VlasnikActivity.class);
        startActivity(refresh);
    }

    private void setLanguageForApp(String languageToLoad){
        Locale locale;
        if(languageToLoad.equals("srb")){ //use any value for default
            locale = Locale.getDefault();
        }
        else {
            locale = new Locale(languageToLoad);
        }
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.vlasnik, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            promeniJezik();
            return true;
        }else if(id==R.id.logout){
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_pregledaj_svoje_radnike) {

            fragmentManager.beginTransaction()
                    .replace(R.id.vlasnik_nav, new PregledajSvojeRadnike())
                    .commit();
        } else if (id == R.id.nav_poslovi) {
            fragmentManager.beginTransaction()
                    .replace(R.id.vlasnik_nav, new Poslovi())
                    .commit();
        } else if (id == R.id.nav_plata) {
            fragmentManager.beginTransaction()
                    .replace(R.id.vlasnik_nav, new PlataActivity())
                    .commit();
        }else if (id == R.id.nav_zaposljavanje) {
            fragmentManager.beginTransaction()
                    .replace(R.id.vlasnik_nav, new ZaposljavanjeActivity())
                    .commit();
        }else if (id == R.id.nav_prognoza) {
            fragmentManager.beginTransaction()
                    .replace(R.id.vlasnik_nav, new VremenskaPrognoza())
                    .commit();
        }else if (id == R.id.nav_konkursi) {
            fragmentManager.beginTransaction()
                    .replace(R.id.vlasnik_nav, new VlasnikPregledajKonkurse())
                    .commit();
        }

    else if (id == R.id.nav_resursi_stoka) {
        fragmentManager.beginTransaction()
                .replace(R.id.vlasnik_nav, new ResursiStoka())
                .commit();
    }else if (id == R.id.nav_resursi_njive) {
        fragmentManager.beginTransaction()
                .replace(R.id.vlasnik_nav, new ResursiNjive())
                .commit();
    }else if (id == R.id.nav_resursi_mehanizacija) {
        fragmentManager.beginTransaction()
                .replace(R.id.vlasnik_nav, new ResursiMehanizacija())
                .commit();
    }else if (id == R.id.nav_resursi_plodovi) {
        fragmentManager.beginTransaction()
                .replace(R.id.vlasnik_nav, new ResursiPlodovi())
                .commit();
    }




    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
