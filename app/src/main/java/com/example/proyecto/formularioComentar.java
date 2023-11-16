package com.example.proyecto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class formularioComentar extends AppCompatActivity {

    String id;
    Button comentar;

    RequestQueue requestQueue;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_comentar);

        DisplayMetrics medidasVen = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(medidasVen);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        requestQueue = Volley.newRequestQueue(this);


        int ancho = medidasVen.widthPixels;
        int largo = medidasVen.heightPixels;

        getWindow().setLayout((int)(ancho*0.8),(int)(largo*0.5));

        comentar = findViewById(R.id.buttonFormularioCom);
        comentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargaDtos();
                Intent abd = new Intent(formularioComentar.this, Cometariosdelreporte.class);
                startActivities(new Intent[]{abd});
            }
        });
    }

    public void cargaDtos(){
        Boolean subir;

        String comentario = String.valueOf(((EditText)findViewById(R.id.textoFormularioCom)).getText());

        if(comentario.equals("")){
            Toast.makeText(formularioComentar.this, "Comentario invalido", Toast.LENGTH_SHORT).show();
            System.out.println("no subido com");
            subir = false;
        }else{
            subir = true;
        }

        if(subir){
            subirSQL(id,comentario);
        }

    }

    private void subirSQL(final String id,final String comentario) {

        String server = "http://"+getResources().getString(R.string.ipTxt)+"/android/subircomentario.php";
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                server,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(formularioComentar.this, "Subido ", Toast.LENGTH_SHORT).show();
                        System.out.println("Subido");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(formularioComentar.this, "No Subido", Toast.LENGTH_SHORT).show();
                        System.out.println(error.toString());
                    }
                }

        ){
            @NonNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param = new HashMap<>();
                param.put("IDREPORTE",id);
                param.put("LIKES","0");
                param.put("DISLIKE","0");
                param.put("COMENTARIO",comentario);
                return param;
            }
        };

        requestQueue.add(stringRequest);
    }
}