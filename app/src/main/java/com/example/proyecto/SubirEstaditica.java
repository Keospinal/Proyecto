package com.example.proyecto;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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

public class SubirEstaditica extends AppCompatActivity {


    String id;

    Button subir;

    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_estaditica);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        requestQueue = Volley.newRequestQueue(this);

        EditText idtxt = findViewById(R.id.idEst);
        idtxt.setText(id);

        subir = findViewById(R.id.buttonEst);
        subir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cargaDtos();
                Intent abd = new Intent(SubirEstaditica.this, InicioActivity.class);
                startActivities(new Intent[]{abd});
            }
        });
    }


    public void cargaDtos(){
        Boolean subir;
        String id = String.valueOf(((EditText)findViewById(R.id.idEst)).getText());
        if(id.equals("")){
            Toast.makeText(SubirEstaditica.this, "id invalida ", Toast.LENGTH_SHORT).show();
            System.out.println("nosubido");
            subir = false;
        }else{
            subir = true;
        }

        String fecha = String.valueOf(((EditText)findViewById(R.id.fehcaest)).getText());
        String link = String.valueOf(((EditText)findViewById(R.id.linkest)).getText());
        String comentario = String.valueOf(((EditText)findViewById(R.id.ComenAdiest)).getText());

        if(subir){
            subirSQL(id,fecha,link,comentario);
            notificar();
        }

    }

    public void notificar(){
        System.out.println("Notificado");
        Intent intent = new Intent(this , Notificar.class);
        intent.putExtra("titulo","Estadistica");
        intent.putExtra("contenido","Gracias por subir su respuesta");
        startService(intent);
    }

    private void subirSQL(final String id,final String fecha,final String link,final String comentario) {

        String server = "http://"+getResources().getString(R.string.ipTxt)+"/android/subirestadistica.php";
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                server,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(SubirEstaditica.this, "Subido ", Toast.LENGTH_SHORT).show();
                        System.out.println("Subido");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SubirEstaditica.this, "No Subido", Toast.LENGTH_SHORT).show();
                        System.out.println(error.toString());
                    }
                }

        ){
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> param = new HashMap<>();
                param.put("ID",id);
                param.put("FECHA",fecha);
                param.put("LINK",link);
                param.put("COMENTARIO",comentario);
                return param;
            }
        };

        requestQueue.add(stringRequest);
    }
}