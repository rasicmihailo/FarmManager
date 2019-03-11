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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class PosaoRecyclerPomocni extends RecyclerView.Adapter<PosaoRecyclerPomocni.ViewHolder> {

    private List<Radnik> lista;
    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;

    private RelativeLayout relativeLayout;
    private TextView txtIme;
    private TextView txtPrez;
    private Button btnOtkazi;
    private Button btnSacuvaj;

    //objasnjeno je u zaposljavanjeActivity zasto ovakav konstruktor
    public PosaoRecyclerPomocni(List<Radnik> listaRadnika, RelativeLayout rel, TextView t,
                          TextView p,Button sacuvaj, Button otkazi){

        this.lista = listaRadnika;
        this.relativeLayout = rel;
        this.txtIme=t;
        this.txtPrez = p;
        this.btnOtkazi = otkazi;
        this.btnSacuvaj  = sacuvaj;

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
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        final String radnikId = lista.get(position).radnikID;

        final String ime = lista.get(position).getIme();
        final String prezime = lista.get(position).getPrezime();
        final String posao = lista.get(position).getZanimanje();

        holder.setTxtPrikaz(ime + " " + prezime + ", " + posao);


        holder.jedan_podatak.setOnClickListener(new View.OnClickListener() {

            //  final Map<String, String> mapObjekat = new HashMap<>();
            @Override
            public void onClick(View v) {

                    firebaseFirestore.collection("Users").document(radnikId).get()
                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                    if (task.isSuccessful()) {

                                        if (task.getResult().exists()) {

                                            Radnik radnik = task.getResult().toObject(Radnik.class);

                                            txtIme.setText(radnik.getIme());
                                            txtPrez.setText(radnik.getPrezime());




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