package com.example.rasicmihailo.farmmanager;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class VremenskaPrognoza extends Fragment {

    View view;

    TextView weatherTextView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore firebaseFirestore;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.prognoza, container, false);

        weatherTextView = (TextView) view.findViewById(R.id.weatherTextView);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore= FirebaseFirestore.getInstance();




        nadjiTempZaVlasnikaIliRadnika();




        return view;
    }



    public void nadjiTempZaVlasnikaIliRadnika(){
        firebaseFirestore.collection("Gazdinstva").document(mAuth.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            if (task.getResult().exists()) {


                                try {
                                    String encodedCityName = URLEncoder.encode(task.getResult().getString("lokacija"), "UTF-8");

                                    DownloadTask task1 = new DownloadTask();
                                    task1.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&APPID=06dd97c248b0a5f9876a3c2402e4f512");

                                } catch (Exception e) {
                                    e.printStackTrace();

                                    //Toast.makeText(getApplicationContext(), "City not found!", Toast.LENGTH_SHORT);
                                }


                            } else {


                                nadjiTempZaRadnika();

                            }

                        } else {

                        }


                    }
                });
    }

    public void nadjiTempZaRadnika(){
        firebaseFirestore.collection("Users").document(mAuth.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (task.isSuccessful()) {

                            if (task.getResult().exists()) {


                                String idVlasnika = task.getResult().getString("vlasnik");


                                firebaseFirestore.collection("Gazdinstva").document(idVlasnika).get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                if (task.isSuccessful()) {

                                                    if (task.getResult().exists()) {


                                                        try {
                                                            String encodedCityName = URLEncoder.encode(task.getResult().getString("lokacija"), "UTF-8");

                                                            DownloadTask task1 = new DownloadTask();
                                                            task1.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&APPID=06dd97c248b0a5f9876a3c2402e4f512");

                                                        } catch (Exception e) {
                                                            e.printStackTrace();

                                                            //Toast.makeText(getApplicationContext(), "City not found!", Toast.LENGTH_SHORT);
                                                        }


                                                    } else {



                                                    }

                                                } else {

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


    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {
                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1){

                    char current = (char) data;

                    result +=current;

                    data = reader.read();

                }

                return result;

            } catch (Exception e) {
                //Toast.makeText(getApplicationContext(), "City not found!", Toast.LENGTH_SHORT);
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                String str = "";

                JSONObject jsonObject = new JSONObject(result);

                String weatherInfo = jsonObject.getString("weather");

                //Log.i("Website content", weatherInfo);

                JSONArray array = new JSONArray(weatherInfo);

                //Log.i("Loggg", Integer.toString(array.length()));


                for(int i = 0; i < array.length(); i++){

                    JSONObject jsonPart = array.getJSONObject(i);


                    //Log.i("main", jsonPart.getString("main"));
                    //Log.i("description", jsonPart.getString("description"));

                    String main = "";
                    String description = "";

                    main = jsonPart.getString("main").toString();
                    description = jsonPart.getString("description").toString();

                    if(main != "" && description != "")
                        str += main + ": " + description + "\r\n";

                }

                String mainInfo = jsonObject.getString("main");

                JSONObject jsonMainInfo = new JSONObject(mainInfo);

                String temp = "";
                temp = jsonMainInfo.getString("temp").toString();

                if(temp != ""){
                    str += "Temp: " + Integer.toString((int)(Double.parseDouble(temp) - 273.15)) + "°C";
                }

                if(str != ""){
                    weatherTextView.setText(str);
                }
                else{
                    //Toast.makeText(getApplicationContext(), "City not found!", Toast.LENGTH_SHORT);
                }

            } catch (Exception e) {
                e.printStackTrace();

                //Toast.makeText(getApplicationContext(), "City not found!", Toast.LENGTH_SHORT);
            }

            //Log.i("Website content", result);
        }
    }


}
