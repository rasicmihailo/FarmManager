package com.example.rasicmihailo.farmmanager;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NeradnikPonudeRecycler extends RecyclerView.Adapter<NeradnikPonudeRecycler.ViewHolder>{

    private List<Ponuda> lista;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    String nazivLok = "";


    public NeradnikPonudeRecycler(List<Ponuda> list){
    this.lista=list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.jedna_ponuda, parent, false);
        context = parent.getContext();

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {

        final String vreme = lista.get(position).getRadnovreme();
        final String plata = lista.get(position).getPlata();
        final String vlasnik = lista.get(position).getVlasnik();




        //  holder.popuniPodatke(vreme, plata, vlasnik, "nes");

//////prikaz ponuda kod neradnika
        firebaseFirestore.collection("Users").document(vlasnik).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){

                            if(task.getResult().exists()){
                                final String ime = task.getResult().getString("ime");
                                final String prezime = task.getResult().getString("prezime");

                                firebaseFirestore.collection("Gazdinstva").document(vlasnik).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                if(task.isSuccessful()){

                                                    if(task.getResult().exists()){

                                                        String naziv = task.getResult().getString("naziv");
                                                        String lok = task.getResult().getString("lokacija");

                                                        nazivLok = naziv + ", "+lok;
                                                        holder.popuniPodatke("Radno vreme: " + vreme, "Plata:" + plata, ime + " " + prezime, nazivLok);

                                                    }else{
                                                        holder.popuniPodatke("Radno vreme: " + vreme, "Plata:" + plata, "nema", "nema");

                                                    }
                                                }else {



                                                }

                                            }
                                        });


                            }else{
                                holder.popuniPodatke("Radno vreme: " + vreme, "Plata:" + plata, "nema", "nazivLok");



                            }
                        }else {



                        }

                    }
                });

        holder.btnPrihvati.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Map<String, String> mapObjekat = new HashMap<>();
                firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if(task.isSuccessful()){

                                    if(task.getResult().exists()){

                                        Neradnik n = task.getResult().toObject(Neradnik.class);



                                        Radnik radnik = new Radnik(n.getZaposljen(), n.getZanimanje(), n.getTip(),
                                                n.getPrezime(), n.getJezik(), n.getIme(), n.getDatum(), plata, vlasnik);

                                        mapObjekat.put("ime", radnik.getIme());
                                        mapObjekat.put("prezime", radnik.getPrezime());
                                        mapObjekat.put("datum", radnik.getDatum());
                                        mapObjekat.put("tip", radnik.getTip());
                                        mapObjekat.put("jezik", radnik.getJezik());
                                        mapObjekat.put("zanimanje", radnik.getZanimanje());
                                        mapObjekat.put("zaposljen", "da");
                                        mapObjekat.put("plata", radnik.getPlata());
                                        mapObjekat.put("vlasnik", radnik.getVlasnik());

                                        firebaseFirestore.collection("Users").document(mAuth.getCurrentUser().getUid())
                                                .set(mapObjekat)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if(task.isSuccessful()){//mora da obrise sve ponude

                                                            firebaseFirestore.collection("Users/" + mAuth.getCurrentUser().getUid() + "/Ponude").get()
                                                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                            if(task.isSuccessful()){

                                                                                for(DocumentSnapshot q : task.getResult()){

                                                                                    q.getReference().delete();

                                                                                }

                                                                            }

                                                                        }
                                                                    });

                                                            Intent intent = new Intent(context, MainActivity.class);
                                                            context.startActivity(intent);

                                                        }else{

                                                        }

                                                    }
                                                });
                                    }

                                }

                            }
                        });
            }
        });

        holder.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Users/" + mAuth.getCurrentUser().getUid() + "/Ponude").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                if (task.isSuccessful()) {

                                    for (DocumentSnapshot q : task.getResult()) {

                                        q.getReference().delete();

                                        Activity activity = (Activity) context;
                                        FragmentManager fragmentManager = activity.getFragmentManager();

                                        fragmentManager.beginTransaction()
                                                .replace(R.id.nav_neradnik, new PregledajPonudeNeradnik())
                                                .commit();


                                    }

                                }

                            }
                        });
            }
        });


    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View view;
        private TextView txtRadnoVr;
        private TextView txtPla;
        private TextView txtImePrez;
        private TextView txtNazivLok;
        private Button btnCancel;
        private Button btnPrihvati;


        public ViewHolder(View itemView) {
            super(itemView);

            view=itemView;
            txtRadnoVr = (TextView) view.findViewById(R.id.txtRadnoVremePonuda);
            txtPla = (TextView) view.findViewById(R.id.txtPlataPonuda);
            txtImePrez = (TextView) view.findViewById(R.id.imePrezimePonuda);
            txtNazivLok = (TextView) view.findViewById(R.id.nazivLokacijaPonuda);
            btnCancel = (Button) view.findViewById(R.id.otkazi);
            btnPrihvati = (Button) view.findViewById(R.id.ok);
        }

        public void popuniPodatke(String vr, String pl, String imePrez, String nazivLok){
            txtRadnoVr.setText(vr);
            txtPla.setText(pl);
            txtImePrez.setText(imePrez);
            txtNazivLok.setText(nazivLok);
        }


    }

}
