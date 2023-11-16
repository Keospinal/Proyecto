package com.example.proyecto;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity  {


    public static String USUARIO;
    public static String CONTRASENA;

    private boolean paso = true;

    private static Intent abd;
    RequestQueue resquestQueue;


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        paso=false;
        resquestQueue = Volley.newRequestQueue(this);
        Button ingresar = findViewById(R.id.login2);
        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarUsuario();
                if (paso) { //paso
                    abd= new Intent(MainActivity.this, InicioActivity.class);
                    abd.putExtra("usuario", String.valueOf(((EditText) findViewById(R.id.username)).getText()));
                    startActivities(new Intent[]{abd});
                } 

            }
        });

        Button resgistro = findViewById(R.id.resgister);
        resgistro.setOnClickListener(view -> {
            Intent abd= new Intent(MainActivity.this, RegisterActivity.class);
            startActivities(new Intent[]{abd});
        });
    }

    public void verificarUsuario(){

        USUARIO = String.valueOf(((EditText)findViewById(R.id.username)).getText());
        CONTRASENA = String.valueOf(((EditText)findViewById(R.id.password)).getText());


        if(USUARIO.equals("")||CONTRASENA.equals("")){
            paso=false;
        }
        leerusuario();
    }

    private void leerusuario(){

        String server = "http://"+getResources().getString(R.string.ipTxt)+"/android/buscador.php?USUARIO=" + USUARIO;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                server,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String USUARIO2, CONTRASENA2;
                        try {
                            USUARIO2 = response.getString("USUARIO");
                            CONTRASENA2 = response.getString("CONTRASENA");

                            if(USUARIO2.toLowerCase().equals(USUARIO.toLowerCase()) && CONTRASENA2.equals(CONTRASENA)){
                                paso=true;
                            }else{

                                Toast.makeText(MainActivity.this,"Usuario o Constraeña incorrectas",Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,"Invalido",Toast.LENGTH_LONG).show();
                        System.out.println("No existe");
                        System.out.println(error.toString());
                        paso=false;
                    }
                }
        );


        resquestQueue.add(jsonObjectRequest);
    }





}