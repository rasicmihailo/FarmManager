package com.example.rasicmihailo.farmmanager;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class VlasnikKonkursRecycler extends RecyclerView.Adapter<VlasnikKonkursRecycler.ViewHolder> {


    private List<Konkurs> lista;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    public VlasnikKonkursRecycler(List<Konkurs> list){
        this.lista=list;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.vlasnik_jedan_konkurs, parent, false);
        context = parent.getContext();


        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();



        return new VlasnikKonkursRecycler.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final String radnik = lista.get(position).getRadnik();
        final String plata = lista.get(position).getPlata();
        final String vreme = lista.get(position).getVreme();


        firebaseFirestore.collection("Users").document(radnik).get() //preko radnikovog id-a nalazim njegove podatke prvo
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

//                            String ime = task.getResult().getString("ime");
//                            String prezime = task.getResult().getString("prezime");
//                            String zanimanje = task.getResult().getString("zanimanje");
                            final Neradnik neradnik = task.getResult().toObject(Neradnik.class);
                            holder.postaviPodatke(neradnik.getIme() + " " + neradnik.getPrezime() + ", " + neradnik.getZanimanje(), plata, vreme);
                        }

                    }
                });

        holder.btnPrihvatiUslove.setOnClickListener(new View.OnClickListener() {
            final Map<String, String> mapObjekat = new HashMap<>();

            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Users").document(radnik).get() //preko radnikovog id-a nalazim njegove podatke prvo
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (task.isSuccessful()) {

                                    final Neradnik n = task.getResult().toObject(Neradnik.class);

                                    Radnik radnikN = new Radnik(n.getZaposljen(), n.getZanimanje(), n.getTip(),
                                            n.getPrezime(), n.getJezik(), n.getIme(), n.getDatum(), plata, mAuth.getUid());


                                    mapObjekat.put("ime", radnikN.getIme());
                                    mapObjekat.put("prezime", radnikN.getPrezime());
                                    mapObjekat.put("datum", radnikN.getDatum());
                                    mapObjekat.put("tip", radnikN.getTip());
                                    mapObjekat.put("jezik", radnikN.getJezik());
                                    mapObjekat.put("zanimanje", radnikN.getZanimanje());
                                    mapObjekat.put("zaposljen", "da");
                                    mapObjekat.put("plata", radnikN.getPlata());
                                    mapObjekat.put("vlasnik", radnikN.getVlasnik());

                                    firebaseFirestore.collection("Users").document(radnik).set(mapObjekat)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    if(task.isSuccessful()){

                                                        firebaseFirestore.collection("Users/" + mAuth.getCurrentUser().getUid() + "/Konkurisu")
                                                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                                                                        for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                                                                            if(doc.getType() == DocumentChange.Type.ADDED){

                                                                                String rad = doc.getDocument().getString("radnik");
                                                                                if(rad.matches(radnik)){
                                                                                    doc.getDocument().getReference().delete();
                                                                                }

                                                                            }
                                                                        }
                                                                        Activity activity = (Activity) context;
                                                                        FragmentManager fragmentManager = activity.getFragmentManager();
                                                                        fragmentManager.beginTransaction()
                                                                                .replace(R.id.vlasnik_nav, new VlasnikPregledajKonkurse())
                                                                                .commit();

                                                                    }
                                                                });


                                                    }

                                                }
                                            });

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

    public class ViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private Button btnPrihvatiUslove;
        private TextView txtImePrezZan;
        private TextView txtZeljenaPlata;
        private TextView txtZeljenoVreme;

        public ViewHolder(View itemView) {
            super(itemView);

            view=itemView;

            txtImePrezZan = (TextView) view.findViewById(R.id.ImePrezimeRadnikKonkurs);
            txtZeljenaPlata = (TextView) view.findViewById(R.id.radnikZeljenaPlataKonkurs);
            txtZeljenoVreme = (TextView) view.findViewById(R.id.radnikZeljenoRadnoVremeKonkurs);
            btnPrihvatiUslove = (Button) view.findViewById(R.id.btnPrihvatiUsloveKonkurs);
        }

        public void postaviPodatke(String podaci, String pl, String vre){

            txtImePrezZan.setText(podaci);
            txtZeljenaPlata.setText(pl);
            txtZeljenoVreme.setText(vre);
        }
    }

}
