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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class VlasnikPlateRecycler extends RecyclerView.Adapter<VlasnikPlateRecycler.ViewHolder> {

    private Context context;
    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;
    private List<Radnik> listaRadnika;
    private TextView txtVlasnik;
    private TextView txtPlate;
    private Button isplata;

    public VlasnikPlateRecycler(List<Radnik> list, TextView txtVla, TextView txtPla, Button ispl){
        this.listaRadnika=list;
        this.txtVlasnik=txtVla;
        this.txtPlate=txtPla;
        this.isplata=ispl;
    }


    @NonNull
    @Override
    public VlasnikPlateRecycler.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.radnik_jedan_podatak, parent, false);
        context = parent.getContext();

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();


        return new VlasnikPlateRecycler.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VlasnikPlateRecycler.ViewHolder holder, int position) {

        String ime = listaRadnika.get(position).getIme();
        String prez = listaRadnika.get(position).getPrezime();
        String plata = listaRadnika.get(position).getPlata();

        String ukupna_plata= "";

        holder.postaviText(ime + " " + prez + ", " + plata);


        isplata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                firebaseFirestore.collection("Gazdinstva").document(mAuth.getCurrentUser().getUid())
                        .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){

                            String fin = task.getResult().getString("finansije");

                            int vf = Integer.parseInt(fin);
                            int rp = Integer.parseInt(txtPlate.getText().toString());

                            vf = vf-rp;

                            firebaseFirestore.collection("Gazdinstva").document(mAuth.getCurrentUser().getUid())
                                    .update("finansije", String.valueOf(vf));

                            Activity activity = (Activity) context;
                            FragmentManager fragmentManager = activity.getFragmentManager();
                            fragmentManager.beginTransaction()
                                    .replace(R.id.vlasnik_nav, new PlataActivity())
                                    .commit();
                        }

                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return listaRadnika.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{

        private View view;
        private TextView prikazRadnika;

        public ViewHolder(View itemView) {
            super(itemView);

            view=itemView;
            this.prikazRadnika= (TextView) view.findViewById(R.id.txtPrikaz);
        }

        public void postaviText(String s){
            this.prikazRadnika.setText(s);
        }
    }
}
