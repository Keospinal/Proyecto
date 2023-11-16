package com.example.proyecto;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

public class DetallesReporte extends AppCompatActivity {


    Button comentarios;
    String id, calamidad, impacto, tiempo, desc;

    RequestQueue resquestQueue;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalles_noticia);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        calamidad = intent.getStringExtra("calamidad");
        impacto = intent.getStringExtra("impacto");
        tiempo = intent.getStringExtra("tiempo");
        desc = intent.getStringExtra("desc");

        resquestQueue = Volley.newRequestQueue(this);


        TextView titulo = findViewById(R.id.TituloPopAd);
        titulo.setText(calamidad);
        TextView impact = findViewById(R.id.ImpactoPopAd);
        impact.setText("Impacto:  "+impacto);
        TextView tiem = findViewById(R.id.tiempoPopAd);
        tiem.setText(tiempo);
        TextView des = findViewById(R.id.DescripcionPopAd);
        des.setText(desc);

        comentarios = findViewById(R.id.button);
        comentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent abd = new Intent(DetallesReporte.this, Cometariosdelreporte.class);
                abd.putExtra("id", id);
                startActivities(new Intent[]{abd});
            }
        });


        String server = "http://"+getResources().getString(R.string.ipTxt)+"/android/traerestadistica.php?ID=" + id;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                server,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String link, fecha, comentario;
                        try {
                            link = response.getString("LINK");
                            TextView linkV = findViewById(R.id.EstDestallesLink);
                            linkV.setText(link);

                            fecha = response.getString("FECHA");
                            TextView fechaV = findViewById(R.id.fechaEstaCon);
                            fechaV.setText(fecha);

                            comentario = response.getString("COMENTARIO");
                            TextView comentarioV = findViewById(R.id.ConmenEstadoAd);
                            comentarioV.setText(comentario);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("No esttad aun");
                        System.out.println(error.toString());
                    }
                }
        );


        resquestQueue.add(jsonObjectRequest);
    }
}