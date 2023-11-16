package com.example.proyecto;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private boolean registroCo = true;

    RequestQueue requestQueue;
    
    private static String server,permisos = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        requestQueue = Volley.newRequestQueue(this);

        server = "http://"+getResources().getString(R.string.ipTxt)+"/android/save.php";
        requestQueue = Volley.newRequestQueue(this);

        Spinner spinnerCedula = (Spinner) findViewById(R.id.opcionesCedula);
        String[] opciones = {"Cedula","TI"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        spinnerCedula.setAdapter(adapter);

        Spinner spinnerTipoDeCliente = (Spinner) findViewById(R.id.opcionescliente);
        String[] opciones2 = {"Cliente","Administrador"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones2);
        spinnerTipoDeCliente.setAdapter(adapter2);

        Button registrarse = findViewById(R.id.resgistrarse);
        registrarse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verificarDatos();
                if(!registroCo) {
                    registroCo=true;
                }
            }
        });

    }

    public void notificar(){
        System.out.println("Notificado");
        Intent intent = new Intent(this , Notificar.class);
        intent.putExtra("titulo","Registro");
        intent.putExtra("contenido","Gracias por registrarte");
        startService(intent);
    }

    public void verificarDatos() {
        String correo = String.valueOf(((EditText)findViewById(R.id.Correo)).getText());
        String usuario = String.valueOf(((EditText)findViewById(R.id.Usuario)).getText());
        String nombre = String.valueOf(((EditText)findViewById(R.id.nombre)).getText());
        String Apellido = String.valueOf(((EditText)findViewById(R.id.Apellido)).getText());
        String contrasena = String.valueOf(((EditText)findViewById(R.id.contraseña)).getText());
        String Direccion = String.valueOf(((EditText)findViewById(R.id.Direccion)).getText());
        Spinner tipoCe = (Spinner) findViewById(R.id.opcionesCedula);
        String tipoceStr = tipoCe.getSelectedItem().toString();

        String cedula = String.valueOf(((EditText)findViewById(R.id.Cedula)).getText());
        Spinner tipoCl = (Spinner) findViewById(R.id.opcionescliente);
        String tipoclStr = tipoCl.getSelectedItem().toString();


        String telefono = String.valueOf(((EditText)findViewById(R.id.Telefono)).getText());


        if(tipoceStr.equals("Cedula")){
            tipoceStr="CC";
        }else{
            tipoceStr="TI";
        }

        if(tipoclStr.equals("Cliente")){
            tipoclStr="CL";
        }else{
            tipoclStr="AD";
        }

        if(nombre.equals("") || Apellido.equals("") || contrasena.equals("") || Direccion.equals("")  || cedula.equals("")  || telefono.equals("") || usuario.equals("") || correo.equals("") ){
            Toast.makeText(RegisterActivity.this,"TODOS LOS CAMPOS TIENEN QUE ESTAR LLENOS",Toast.LENGTH_LONG).show();
            registroCo = false;
        }else{
            if(revisarUsuario(usuario)) {

                if (correo.contains("@")) {
                    if (!correo.substring(correo.indexOf("@")).contains(".")) {
                        Toast.makeText(RegisterActivity.this, "CORREO INVALIDO", Toast.LENGTH_LONG).show();
                        registroCo = false;
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, "CORREO INVALIDO", Toast.LENGTH_LONG).show();
                    registroCo = false;
                }
            }
        }

        if (registroCo){
            subirSQL(nombre, Apellido,usuario, contrasena, Direccion, tipoceStr,correo, cedula,telefono, tipoclStr);
            notificar();
            Intent abd = new Intent(RegisterActivity.this, InicioActivity.class);
            abd.putExtra("usuario", String.valueOf(((EditText)findViewById(R.id.Usuario)).getText()));
            abd.putExtra("permisos", permisos);
            startActivities(new Intent[]{abd});
        }

    }

    public Boolean revisarUsuario(String usuario){

        String server = "http://"+getResources().getString(R.string.ipTxt)+"/android/buscador.php?USUARIO=" + usuario;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                server,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String USUARIO2;
                        try {
                            USUARIO2 = response.getString("USUARIO");

                            if(USUARIO2.equalsIgnoreCase(usuario)){
                                Toast.makeText(RegisterActivity.this,"Usuario ya existe",Toast.LENGTH_LONG).show();
                                registroCo = false;
                            }else{

                                registroCo = true;
                            }

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("No existe");
                        System.out.println(error.toString());
                        registroCo = true;
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
        return true;
    }

    private void subirSQL(final String nombre,final String Apellido,final String usuario,final String contrasena,final String Direccion,final String tipoceStr,final String correo,final String cedula,final String celular,final String tipoclStr) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                server,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(RegisterActivity.this, "Registrado", Toast.LENGTH_SHORT).show();
                        if(tipoclStr.equals("AD")) {
                            permisos = "AD";
                        }
                        System.out.println("Subido");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(RegisterActivity.this, "No Registrado", Toast.LENGTH_SHORT).show();
                        System.out.println(error.toString());
                    }
                }

        ){
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> param = new HashMap<>();
                param.put("NOMBRE",nombre);
                param.put("APELLIDO",Apellido);
                param.put("USUARIO",usuario);
                param.put("CONTRASENA",contrasena);
                param.put("DIRECCION",Direccion);
                param.put("TIPOCEDULA",tipoceStr);
                param.put("CORREO",correo);
                param.put("CEDULA",cedula);
                param.put("CELULAR",celular);
                param.put("TIPOCLIENTE",tipoclStr);
                return param;
            }
        };

        requestQueue.add(stringRequest);
    }




}

