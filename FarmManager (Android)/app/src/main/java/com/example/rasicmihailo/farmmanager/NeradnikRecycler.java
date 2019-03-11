package com.example.rasicmihailo.farmmanager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class NeradnikRecycler extends RecyclerView.Adapter<NeradnikRecycler.ViewHolder> {

    private Map<Integer,Neradnik> lista;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private RelativeLayout relativeLayout;
    private TextView txtIme;
    private TextView txtPrez;
    private TextView txtDat;
    private TextView txtZap;
    private Button btnCancel;
    private Button btnZaposli;
    private TextView txtPlata;
    private TextView txtVreme;
    private boolean aktivan = false;

    //objasnjeno je u zaposljavanjeActivity zasto ovakav konstruktor
    public NeradnikRecycler(Map<Integer,Neradnik> listaNeradnika, RelativeLayout rel, TextView t,
                            TextView p, TextView d, TextView z, Button can, Button zap, TextView plata, TextView vreme){

        this.lista = listaNeradnika;
        this.relativeLayout = rel;
        this.txtIme=t;
        this.txtPrez = p;
        this.txtDat = d;
        this.txtZap = z;
        this.btnCancel=can;
        this.btnZaposli = zap;
        this.txtPlata=plata;
        this.txtVreme=vreme;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.radnik_jedan_podatak, parent, false);
        context = parent.getContext();

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final String neradnikId = lista.get(position).neradnikID;

        final String ime = lista.get(position).getIme();
        final String prezime = lista.get(position).getPrezime();
        final String posao = lista.get(position).getZanimanje();

        holder.setTxtPrikaz(ime + " " + prezime + ", " + posao);



            holder.jedan_podatak.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
            //// slanje ponuda vlasnika
                    if(!aktivan) { //ako se ne prikazuje trenutno radnik, prikazi, inace nemoj dok ne otkaze
                        firebaseFirestore.collection("Users").document(neradnikId).get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                        if (task.isSuccessful()) {

                                            if (task.getResult().exists()) {

                                                Neradnik neradnik = task.getResult().toObject(Neradnik.class);

                                                txtIme.setText(neradnik.getIme());
                                                txtPrez.setText(neradnik.getPrezime());
                                                txtDat.setText(neradnik.getDatum());
                                                txtZap.setText(neradnik.getZanimanje());

                                                relativeLayout.setVisibility(View.VISIBLE);

                                                aktivan = true;
                                            } else {

                                            }

                                        } else {

                                        }


                                    }
                                });

                        final Map<String, String> objekat = new HashMap<>();
                        btnZaposli.setOnClickListener(new View.OnClickListener() {



                            @Override
                            public void onClick(View v) {

                                objekat.put("vlasnik", mAuth.getCurrentUser().getUid()); //vlasnikov id
                                objekat.put("radnovreme", txtVreme.getText().toString());
                                objekat.put("plata", txtPlata.getText().toString());


                                firebaseFirestore.collection("Users/" + neradnikId + "/Ponude").add(objekat)
                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {

                                                if(task.isSuccessful()){
                                                    holder.setTxtPrikaz("proslo");

                                                }else{

                                                }

                                            }
                                        });

                                relativeLayout.setVisibility(View.INVISIBLE);
                                aktivan=false;
                            }
                        });

                    }

                }
            });


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.INVISIBLE);
                aktivan=false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{


        private TextView txtPrikaz;
        private View view;
        private TextView jedan_podatak;


        @SuppressLint("ResourceType")
        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            jedan_podatak = (TextView) view.findViewById(R.id.txtPrikaz);
        }


        public void setTxtPrikaz(String txt){

            txtPrikaz = view.findViewById(R.id.txtPrikaz);

            txtPrikaz.setText(txt);
        }
    }


}
