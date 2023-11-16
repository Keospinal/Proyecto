package com.example.proyecto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class DestacadosRepor extends AppCompatActivity {


    RequestQueue resquestQueue;
    public static class Destacados {
        private final int layoutId; // ID del layout personalizado
        private final String texto;
        private final String tipo;
        private final String id; // Título o información relacionada al elemento



        public Destacados(int layoutId, String tipo, String texto, String id) {
            this.tipo = tipo;
            this.layoutId = layoutId;
            this.texto = texto;
            this.id=id;
        }

        public int getLayoutId() {
            return layoutId;
        }

        public String getTexto() {
            return texto;
        }

        public String getId() {
            return id;
        }

        public String getTipo() {
            return tipo;
        }


    }


    public static class CustomListAdapterDes extends BaseAdapter {
        private final Context context;
        private final ArrayList<Destacados> listItemsDes;


        public CustomListAdapterDes(Context context, ArrayList<Destacados> listItems) {
            this.context = context;
            this.listItemsDes = listItems;
        }


        public int getCount() {
            return listItemsDes.size();
        }

        public Object getItem(int position) {
            return listItemsDes.get(position);
        }


        public long getItemId(int position) {
            return position;
        }


        public View getView(int position, View convertView, ViewGroup parent) {
            Destacados destacado = listItemsDes.get(position);
            int layoutId = destacado.getLayoutId();

            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            }

            // Asignar datos al diseño personalizado (puedes hacerlo aquí)

            TextView textoripo = convertView.findViewById(R.id.textTipoDes);
            textoripo.setText(destacado.getTipo());

            TextView textoDesc = convertView.findViewById(R.id.textDescripDes);
            textoDesc.setText(destacado.getTexto());

            return convertView;
        }
    }

    private ArrayList<Destacados> listItems;
    CustomListAdapterDes adapter2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_destacados_repor);


        listItems = new ArrayList<>();
        ListView listView = findViewById(R.id.listaDescta);

        resquestQueue = Volley.newRequestQueue(this);



        String server = "http://"+getResources().getString(R.string.ipTxt)+"/android/treardestacado.php";
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                server,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Procesa el JSONArray recibido
                        String descripcion, calamidad,id;
                        try {
                            for (int i = 0; i < response.length();i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                calamidad = jsonObject.getString("CALAMIDAD");
                                descripcion = jsonObject.getString("DESCRIPCCION");
                                id = jsonObject.getString("ID");
                                System.out.println(calamidad+" "+ descripcion+" "+ id);
                                listItems.add(new Destacados(R.layout.destacadoestilo, calamidad, descripcion, id));
                            }

                            adapter2.notifyDataSetChanged();
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

        //listItems.add(new Destacados(R.layout.destacadoestilo,"calamidad", "descripcion", "id"));

        adapter2 = new CustomListAdapterDes(this, listItems);

        listView.setAdapter((ListAdapter) adapter2);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent abd = new Intent(DestacadosRepor.this, PopupCliente.class);
                abd.putExtra("id", listItems.get(i).getId());
                startActivities(new Intent[]{abd});
            }
        });
    }


}

