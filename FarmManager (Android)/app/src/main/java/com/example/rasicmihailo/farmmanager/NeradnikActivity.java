package com.example.rasicmihailo.farmmanager;

import android.app.FragmentManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Locale;

import javax.annotation.Nullable;

public class NeradnikActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentManager fragmentManager = getFragmentManager();
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private TextView ime2;
    private TextView email2;
    private Locale myLocale;
    private boolean provera = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        setContentView(R.layout.activity_neradnik);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        setTitle("Farm Manager");



        NavigationView nv = (NavigationView) findViewById(R.id.nav_view);
        View hView =  nv.getHeaderView(0);

        ime2 = (TextView) hView.findViewById(R.id.ime);
        email2 = (TextView) hView.findViewById(R.id.email);

        String em = mAuth.getCurrentUser().getEmail();
        email2.setText(em);

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
                                Toast.makeText(NeradnikActivity.this, "NE POSTOJI", Toast.LENGTH_LONG).show();
                            }

                        }else{

                        }

                    }
                });


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentManager.beginTransaction()
                .replace(R.id.nav_neradnik, new PregledajPonudeNeradnik())
                .commit();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = mAuth.getCurrentUser();
        if(user == null){
            startActivity(new Intent(NeradnikActivity.this, MainActivity.class));
        }
    }

    public void proveriZaposljenje(){

        firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String zaposljen = task.getResult().getString("zaposljen");

                    if(zaposljen.matches("da")){

                        startActivity(new Intent(NeradnikActivity.this, MainActivity.class));
                        finish();
                    }

                }

            }
        });

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
        Intent refresh = new Intent(this, NeradnikActivity.class);
        startActivity(refresh);
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
        getMenuInflater().inflate(R.menu.neradnik, menu);
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
        } else if(id==R.id.neradnik_logout){
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


         if (id == R.id.nav_pregled_farmi) {
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_neradnik, new PregledFarmiNeradnik())
                    .commit();
        } else if (id == R.id.nav_pregledaj_ponude) {
            fragmentManager.beginTransaction()
                    .replace(R.id.nav_neradnik, new PregledajPonudeNeradnik())
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
