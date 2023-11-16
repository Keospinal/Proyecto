package com.example.proyecto;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class formularioalerta extends AppCompatActivity {

    Button subir;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formularioalerta);
        requestQueue = Volley.newRequestQueue(this);


        subir = findViewById(R.id.subiralerta);
        subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String alerta = String.valueOf(((EditText)findViewById(R.id.alertarContenido)).getText());
                if (!alerta.equals("")) {
                    subirSQL(alerta);
                    Intent cdf = new Intent(formularioalerta.this, InicioActivity.class);
                    startActivities(new Intent[]{cdf});
                }
            }
        });
    }

    private void subirSQL(final String alerta) {

        String server = "http://"+getResources().getString(R.string.ipTxt)+"/android/subirreportes.php";
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                server,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(formularioalerta.this, "Subido ", Toast.LENGTH_SHORT).show();
                        System.out.println("Subido");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(formularioalerta.this, "No Subido", Toast.LENGTH_SHORT).show();
                        System.out.println(error.toString());
                    }
                }

        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> param = new HashMap<>();
                param.put("COMENTARIO",alerta);
                return param;
            }
        };

        requestQueue.add(stringRequest);
    }
}