package com.example.proyecto;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class PopupAdmin extends AppCompatActivity {


    static String id;

    RequestQueue resquestQueue;
    TextView detalles,estaditica;

    static String calamidad, impacto, tiempo, desc, distancia;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popup_admin);
        resquestQueue = Volley.newRequestQueue(this);


        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        DisplayMetrics medidasVen = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVen);

        int ancho = medidasVen.widthPixels;
        int largo = medidasVen.heightPixels;

        getWindow().setLayout((int)(ancho*0.8),(int)(largo*0.6));

        detalles = findViewById(R.id.detallesPopAd);
        detalles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent abd = new Intent(PopupAdmin.this, DetallesReporte.class);
                abd.putExtra("id", id);
                abd.putExtra("calamidad", calamidad);
                abd.putExtra("impacto", impacto);
                abd.putExtra("tiempo", tiempo);
                abd.putExtra("desc", desc);
                abd.putExtra("distancia",distancia);
                startActivities(new Intent[]{abd});
            }
        });

        estaditica = findViewById(R.id.AgregarEstPopAd);
        estaditica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent abd = new Intent(PopupAdmin.this, SubirEstaditica.class);
                abd.putExtra("id", id);
                startActivities(new Intent[]{abd});
            }
        });
        datosdelreporte();
    }

    private void datosdelreporte(){

        String server = "http://"+getResources().getString(R.string.ipTxt)+"/android/buscadorRep.php?id=" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                server,
                null,
                new Response.Listener<JSONObject>() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            calamidad = response.getString("CALAMIDAD");
                            impacto = response.getString("IMPACTO");
                            tiempo = response.getString("TIEMPO");
                            desc = response.getString("DESCRIPCCION");
                            distancia = response.getString("DISTANCIAAFEC");

                            TextView titulo = findViewById(R.id.TituloPopAd);
                            titulo.setText(calamidad);
                            TextView impact = findViewById(R.id.ImpactoPopAd);
                            impact.setText("Impacto:  "+impacto);
                            TextView tiem = findViewById(R.id.tiempoPopAd);
                            tiem.setText(tiempo);
                            TextView des = findViewById(R.id.DescripcionPopAd);
                            des.setText(desc);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(id);
                        System.out.println("No existe el rerporte");
                    }
                }
        );
        resquestQueue.add(jsonObjectRequest);
    }
}