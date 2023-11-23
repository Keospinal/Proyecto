package com.example.proyecto;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ComentarioActivity extends AppCompatActivity {

    RequestQueue requestQueue;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private static String server;

    private odometro odometer;
    private Boolean enlazado = false;

    private ServiceConnection connection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            odometro.OdometerBinder odometerBinder = (odometro.OdometerBinder) iBinder;
            odometer = odometerBinder.getOdometer();
            enlazado = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            enlazado = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comentario);

        requestQueue = Volley.newRequestQueue(this);

        server = "http://"+getResources().getString(R.string.ipTxt)+"/android/reportes.php";

        Intent intent = getIntent();
        String usuario = intent.getStringExtra("usuario");
        System.out.println(usuario);

        Spinner spinnerTiempo = (Spinner) findViewById(R.id.Tipotiempo);
        String[] opciones = {"Horas","Dias","Meses","Años"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones);
        spinnerTiempo.setAdapter(adapter);

        Spinner spinnerTipoImpacto = (Spinner) findViewById(R.id.TipoReporte);
        String[] opciones3 = {"Señal Dañada","Vial en mal estado","Semaforo Dañado","Otro"};
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones3);
        spinnerTipoImpacto.setAdapter(adapter3);

        Spinner spinnerImpacto = (Spinner) findViewById(R.id.Impacto);
        String[] opciones2;
        opciones2 = new String[]{"1","2","3","4","5","6","7","8","9","10"};
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, opciones2);
        spinnerImpacto.setAdapter(adapter2);

        ActivityCompat.requestPermissions(ComentarioActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        actualizarDistancia();


        Button comentario = findViewById(R.id.reportar);
        comentario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(verificarDatos()){
                    Intent abd = new Intent(ComentarioActivity.this, InicioActivity.class);
                    startActivities(new Intent[]{abd});
                }
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        ImageView ubicación = findViewById(R.id.ubicacionbotton);
        ubicación.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLocation();
            }
        });

    }

    public void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.


            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(
                    new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            Location location = task.getResult();
                            if (location != null) {
                                Geocoder geocoder = new Geocoder(ComentarioActivity.this, Locale.getDefault());
                                try {
                                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                                    EditText dirrecion = findViewById(R.id.Comentario_Direccion);

                                    dirrecion.setText("" + addresses.get(0).getAddressLine(0));

                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }


                            }
                        }
                    });
        }else {
            ActivityCompat.requestPermissions(ComentarioActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
    }


    public Boolean verificarDatos() {

        Spinner stipoRe = (Spinner) findViewById(R.id.TipoReporte);
        String tipoRe = stipoRe.getSelectedItem().toString();
        Spinner simpacto = (Spinner) findViewById(R.id.Impacto);
        String impacto = simpacto.getSelectedItem().toString();

        String tiempo = String.valueOf(((EditText)findViewById(R.id.tiempo)).getText());


        Spinner tipoTi = (Spinner) findViewById(R.id.Tipotiempo);
        String tipoTiempo= tipoTi.getSelectedItem().toString();
        String Direccion = String.valueOf(((EditText)findViewById(R.id.Comentario_Direccion)).getText());
        String descripcion = String.valueOf(((EditText)findViewById(R.id.Descripcion)).getText());


        if(tipoRe.equals("") || impacto.equals("") || tiempo.equals("") || Direccion.equals("") || tipoTiempo.equals("") || descripcion.equals("")){
            Toast.makeText(ComentarioActivity.this,"TODOS LOS CAMPOS TIENEN QUE ESTAR LLENOS",Toast.LENGTH_LONG).show();
            return false;
        }else
        {
            char[] tiempoSep = tiempo.toCharArray();
            for (char c : tiempoSep) {
                if (!Character.isDigit(c)) {
                    Toast.makeText(ComentarioActivity.this,"Tienen que ser el tiempo numero",Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }


        if(tipoRe.equals("Otro")) {
            String otro = String.valueOf(((EditText)findViewById(R.id.Otro)).getText());
            if (otro.equals("")){
                Toast.makeText(ComentarioActivity.this,"Llena el campo otro",Toast.LENGTH_LONG).show();
                return false;
            }else {
                tipoRe = otro;
            }
        }


        Geocoder geocoder2 = new Geocoder(ComentarioActivity.this);

        boolean dirrecionValida = false;
        double latitude = 0;
        double longitude = 0;

        try {
            List<Address> addresses2 = geocoder2.getFromLocationName(Direccion, 1);
            if (addresses2 != null && !Direccion.isEmpty()) {
                latitude = addresses2.get(0).getLatitude();
                longitude = addresses2.get(0).getLongitude();
                dirrecionValida = true;
            } else {
                dirrecionValida =false;
                Toast.makeText(ComentarioActivity.this,"Dirrecion Invalida",Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(ComentarioActivity.this,"Dirrecion Invalida",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        if (dirrecionValida ) {
            subirComentario(tipoRe, impacto, tiempo + " " + tipoTiempo, descripcion, "" + latitude, "" + longitude);
            notificar();

        }else {
            return false;
        }
        return true;
    }

    // Sobrescribe el método onActivityResult para recibir los datos de vuelta

    public void notificar(){
        System.out.println("Notificado");
        Intent intent = new Intent(this , Notificar.class);
        intent.putExtra("titulo","Reporte");
        intent.putExtra("contenido","Gracias por subir su reporte");
        startService(intent);
    }


    private void subirComentario(final String Calamidad,final String Impacto,final String Tiempo,final String Descripccion,final String Lat,final String Lon) {
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                server,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(ComentarioActivity.this,"SU COMENTARIO SE A GUARDADO",Toast.LENGTH_SHORT).show();
                        System.out.println("Comentario Guardado");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ComentarioActivity.this,"U COMENTARIO NO SE A GUARDADO",Toast.LENGTH_SHORT).show();
                        System.out.println("Comentario Fallo");
                    }
                }

        ){
            @NonNull
            @Override
            protected Map<String, String> getParams() {
                Map<String,String> param = new HashMap<>();
                param.put("CALAMIDAD",Calamidad);
                param.put("IMPACTO",Impacto);
                param.put("TIEMPO", Tiempo);
                param.put("DESCRIPCCION",Descripccion);
                param.put("LAT",Lat);
                param.put("LON",Lon);
                param.put("DISTANCIAAFEC", String.valueOf(distancia));
                return param;
            }
        };

        requestQueue.add(stringRequest);
    }

    public void onStart() {
        super.onStart();
        Intent intent = new Intent(this, odometro.class);
        bindService(intent,connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (enlazado){
            unbindService(connection);
            enlazado=false;
        }
    }

    public double distancia;
    public void actualizarDistancia(){
        final TextView distancieView =  findViewById(R.id.DistanciasAfectada);
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                distancia = 0.0;
                if (enlazado && odometer!=null){
                    distancia = odometer.getDistanciaMetros();
                }
                String distanciaStr =String.format(Locale.getDefault(),"%1$, .2f kilometros",distancia);
                distancieView.setText(distanciaStr);
                handler.postDelayed(this,1000);
            }
        });
    }


}