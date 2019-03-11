package com.example.rasicmihailo.farmmanager;


import android.annotation.SuppressLint;
import android.content.Context;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PosaoRecycler extends RecyclerView.Adapter<PosaoRecycler.ViewHolder> {

    private List<Posao> lista;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private RelativeLayout relativeLayout;

    private EditText txtIme;
    private EditText txtPrezime;
    private EditText txtOpis;

    private Button btnDodajPosao;
    private Button btnCancel;
    private Button btnDodaj;

    //private boolean aktivan = false;

    public PosaoRecycler(List<Posao> lista){
        this.lista = lista;
    }

    public PosaoRecycler(List<Posao> lista, RelativeLayout relativeLayout, EditText txtIme, EditText txtPrezime, EditText txtOpis, Button btnDodajPosao, Button btnDodaj, Button btnCancel) {
        this.lista = lista;
        this.relativeLayout = relativeLayout;
        this.txtIme = txtIme;
        this.txtPrezime = txtPrezime;
        this.txtOpis = txtOpis;
        this.btnDodajPosao = btnDodajPosao;
        this.btnDodaj = btnDodaj;
        this.btnCancel = btnCancel;
    }




    @NonNull
    @Override
    public PosaoRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.posao_jedan_podatak, parent, false);

        context = parent.getContext();

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        return new PosaoRecycler.ViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull final PosaoRecycler.ViewHolder holder, int position) {


        final String opis = lista.get(position).getOpis();
        final String zavrsen = lista.get(position).getZavrsen();
        final String ime = lista.get(position).getIme();
        final String prezime = lista.get(position).getPrezime();

        String rp = context.getString(R.string.radnikPri);
        String op = context.getString(R.string.opisPosla);
        String zav = context.getString(R.string.zavrsen);
        holder.setTxtPrikaz(rp + ime + " " + prezime + "\n\n" + op + "\n" + opis + "\n\n" + zav + zavrsen);




    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView txtPrikazPosao;
        private View view;


        @SuppressLint("ResourceType")
        public ViewHolder(View itemView) {
            super(itemView);

            view = itemView;
        }

        public void setTxtPrikaz(String txt){

            txtPrikazPosao = view.findViewById(R.id.txtPrikazPosao);

            txtPrikazPosao.setText(txt);
        }
    }

//    public void dodajPosao(){
//
//
//
//        final String ime = txtIme.getText().toString();
//        final String prezime = txtPrezime.getText().toString();
//        final String opis = txtOpis.getText().toString();
//
//
//        firebaseFirestore.collection("Users")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
//                        for(DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()){
//                            if(doc.getType() == DocumentChange.Type.ADDED){
//
//
//                                String imee = doc.getDocument().getString("ime");
//                                String prezimee = doc.getDocument().getString("prezime");
//                                String vlasnikk = doc.getDocument().getString("vlasnik");
//
//                                if(imee.matches(ime) && prezimee.matches(prezime) && vlasnikk.matches(mAuth.getUid())){
//
//                                    Map<String, String> mapObjekat = new HashMap<>();
//
//                                    mapObjekat.put("opis", opis);
//                                    mapObjekat.put("radnik", doc.getDocument().getId());
//                                    mapObjekat.put("zavrsen", "ne");
//                                    mapObjekat.put("ime", ime);
//                                    mapObjekat.put("prezime", prezime);
//
//
//                                    firebaseFirestore.collection("Gazdinstva/" + mAuth.getUid() + "/Poslovi").document().set(mapObjekat);
//
//
//
//                                }else{
//
//                                }
//                            }
//                        }
//
//                    }
//                });
//
//    }



}