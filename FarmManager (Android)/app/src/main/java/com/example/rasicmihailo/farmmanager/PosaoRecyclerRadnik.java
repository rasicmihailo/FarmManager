package com.example.rasicmihailo.farmmanager;


import android.annotation.SuppressLint;
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

public class PosaoRecyclerRadnik extends RecyclerView.Adapter<PosaoRecyclerRadnik.ViewHolder>{


    private List<Posao> lista;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;


    public PosaoRecyclerRadnik(List<Posao> lista){
        this.lista = lista;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posao_jedan_podatak_radnik, parent, false);

        context = parent.getContext();

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        return new PosaoRecyclerRadnik.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {



        final String opis = lista.get(position).getOpis();

        final String zavrsen = lista.get(position).getZavrsen();

        final String idPosla = lista.get(position).posaoID;

        final String ime = lista.get(position).getIme();

        final String prezime = lista.get(position).getPrezime();

        String op = context.getString(R.string.opisPosla);
        String zav = context.getString(R.string.zavrsen);
        holder.setTxtPrikaz(op +"\n" + opis + "\n\n" + zav + zavrsen);


        holder.btnOdradi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {





                firebaseFirestore.collection("Users").document(mAuth.getUid()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                if (task.isSuccessful()) {

                                    if (task.getResult().exists()) {


                                        final String idVlasnika = task.getResult().getString("vlasnik");

                                        final String id = task.getResult().getId();

                                        firebaseFirestore.collection("Gazdinstva/" + idVlasnika + "/Poslovi")
                                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                                                        for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
                                                            if(doc.getType() == DocumentChange.Type.ADDED){

                                                                String idPoslaFire = doc.getDocument().getId();

                                                                if(idPosla.matches(idPoslaFire)) {


                                                                    Map<String, String> mapObjekat = new HashMap<>();

                                                                    mapObjekat.put("opis", opis);
                                                                    mapObjekat.put("radnik", id);
                                                                    mapObjekat.put("zavrsen", "da");
                                                                    mapObjekat.put("ime", ime);
                                                                    mapObjekat.put("prezime", prezime);


                                                                    firebaseFirestore.collection("Gazdinstva/" + idVlasnika + "/Poslovi").document(idPosla).set(mapObjekat);


                                                                    Activity activity = (Activity) context;

                                                                    FragmentManager fragmentManager = activity.getFragmentManager();

                                                                    fragmentManager.beginTransaction()
                                                                            .replace(R.id.radnik_nav, new PosloviRadnikActivity())
                                                                            .commit();

                                                                }
                                                            }
                                                        }

                                                    }
                                                });


                                    } else {

                                    }

                                } else {

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


    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtPrikazPosao;
        private View view;
        private Button btnOdradi;

        @SuppressLint("ResourceType")
        public ViewHolder(View itemView) {
            super(itemView);


            view = itemView;
            btnOdradi = (Button) view.findViewById(R.id.buttonOdradi);

            txtPrikazPosao = view.findViewById(R.id.txtPrikazPosaoRadnik);
        }

        public void setTxtPrikaz(String txt){

            txtPrikazPosao.setText(txt);

        }
    }
}
