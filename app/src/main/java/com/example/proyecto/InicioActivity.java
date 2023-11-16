package com.example.proyecto;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class InicioActivity extends AppCompatActivity implements OnMapReadyCallback {

    static GoogleMap map;

    private static String usuario, permisos;

    RequestQueue resquestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        usuario = intent.getStringExtra("usuario");
        permisos = intent.getStringExtra("permisos");
        resquestQueue = Volley.newRequestQueue(this);

        leerusuario();


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapa);
        mapFragment.getMapAsync(this);

        Button reportar = findViewById(R.id.reportar);
        reportar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    Intent abd = new Intent(InicioActivity.this, ComentarioActivity.class);
                    abd.putExtra("usuario", usuario);
                    startActivities(new Intent[]{abd});

            }
        });


        ImageView destacados = findViewById(R.id.destacados);
        destacados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cdf = new Intent(InicioActivity.this, DestacadosRepor.class);
                startActivities(new Intent[]{cdf});
            }
        });

        ImageView alertas = findViewById(R.id.alertas);
        alertas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent cdf = new Intent(InicioActivity.this, formularioalerta.class);
                startActivities(new Intent[]{cdf});
            }
        });


    }


    public void cargarmarkers(){
        String server = "http://"+getResources().getString(R.string.ipTxt)+"/android/comentarios.php";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                server,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Procesa el JSONArray recibido
                        String lon, lat, id;
                            try {
                                for (int i = 0; i < response.length();i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    id = jsonObject.getString("ID");
                                    lon = jsonObject.getString("LON");
                                    lat = jsonObject.getString("LAT");
                                    LatLng temp = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                                    map.addMarker(new MarkerOptions().position(temp).title(id));
                                }
                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Maneja los errores de la solicitud
                        System.out.println(error.toString());
                        System.out.println("Error");
                    }
                }
        );


        resquestQueue.add(jsonArrayRequest);
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        map = googleMap;
        LatLng Colombia = new LatLng(4.6326049,-74.0678582);
        map.moveCamera(CameraUpdateFactory.newLatLng(Colombia));

        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                System.out.println(marker.getTitle());
                System.out.println("Eres "+permisos);

                Intent abd;
                if(permisos.equals("AD")){
                    abd = new Intent(InicioActivity.this, PopupAdmin.class);
                }else{
                    abd = new Intent(InicioActivity.this, PopupCliente.class);
                }
                abd.putExtra("id", marker.getTitle());
                startActivities(new Intent[]{abd});

                return false;
            }
        });
        cargarmarkers();
    }

    private void leerusuario(){

        String server = "http://"+getResources().getString(R.string.ipTxt)+"/android/buscador.php?USUARIO=" + usuario;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                server,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            permisos = response.getString("TIPOCLIENTE");

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
                    }
                }
        );


        resquestQueue.add(jsonObjectRequest);
    }
}
