package com.example.proyecto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

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

public class Cometariosdelreporte extends AppCompatActivity {

    RequestQueue resquestQueue;
    public class Comentarios {
        private int layoutId; // ID del layout personalizado
        private String texto, like , dislike; // Título o información relacionada al elemento

        private Layout layout;

        public Comentarios(int layoutId, String title, String like, String dislike) {
            this.layoutId = layoutId;
            this.texto = title;
            this.like=like;
            this.dislike=dislike;
        }

        public int getLayoutId() {
            return layoutId;
        }

        public String getTexto() {
            return texto;
        }

        public String getlike() {
            return like;
        }
        public String getdislike() {
            return dislike;
        }


        public void incrementLike(){
            Integer numeroEntero=0;
            try {
                numeroEntero = Integer.valueOf(like);
                // Aquí puedes usar 'numeroEntero'
            } catch (NumberFormatException e) {
                // Manejo de la excepción
                e.printStackTrace();
            }

            like=""+(numeroEntero+1);
        }

        public void incrementDislike(){
            Integer numeroEntero=0;
            try {
                numeroEntero = Integer.valueOf(like);
                // Aquí puedes usar 'numeroEntero'
            } catch (NumberFormatException e) {
                // Manejo de la excepción
                e.printStackTrace();
            }

            dislike=""+(numeroEntero+1);
        }

    }


    public class CustomListAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<Comentarios> listItems;


        public CustomListAdapter(Context context, ArrayList<Comentarios> listItems) {
            this.context = context;
            this.listItems = listItems;
        }


        public int getCount() {
            return listItems.size();
        }

        public Object getItem(int position) {
            return listItems.get(position);
        }


        public long getItemId(int position) {
            return position;
        }

        public void updateItemLike(int position) {
            Comentarios item = listItems.get(position);
            item.incrementLike(); // Incrementa el contador de "Me gusta"
            notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
        }

        public void updateItemDislike(int position) {
            Comentarios item = listItems.get(position);
            item.incrementDislike(); // Incrementa el contador de "No me gusta"
            notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado
        }



        public View getView(int position, View convertView, ViewGroup parent) {
            Comentarios item = listItems.get(position);
            int layoutId = item.getLayoutId();

            if (convertView == null) {
                System.out.println("inflado");
                convertView = LayoutInflater.from(context).inflate(layoutId, parent, false);
            }

            // Asignar datos al diseño personalizado (puedes hacerlo aquí)

            TextView texto = convertView.findViewById(R.id.textComent);
            texto.setText(item.getTexto());

            TextView likes = convertView.findViewById(R.id.textlike);
            likes.setText(""+item.getlike());

            TextView dislikes = convertView.findViewById(R.id.textDislike);
            dislikes.setText(""+item.getdislike());

            ImageView imglike = convertView.findViewById(R.id.imageLike);
            imglike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateItemLike(position);
                    System.out.println("Like " + item.getTexto());

                }
            });

            ImageView imgdislike = convertView.findViewById(R.id.imageDislike);
            imgdislike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateItemDislike(position);
                    System.out.println("DISLike " + item.getTexto());
                }
            });

            return convertView;
        }
    }


    private ArrayList<Comentarios> listItems;

    ImageView comentar;

    CustomListAdapter adapter;
    String id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cometariosdelreporte);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        listItems = new ArrayList<>();
        ListView listView = findViewById(R.id.listacomentarios);

        resquestQueue = Volley.newRequestQueue(this);


        String server = "http://"+getResources().getString(R.string.ipTxt)+"/android/traercomentario.php?ID="+id;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                server,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // Procesa el JSONArray recibido
                        String cometario, like, dislike;
                        try {
                            for (int i = 0; i < response.length();i++) {
                                JSONObject jsonObject = response.getJSONObject(i);
                                like = jsonObject.getString("LIKES");
                                dislike = jsonObject.getString("DISLIKE");
                                cometario = jsonObject.getString("COMENTARIO");
                                System.out.println(cometario+" "+ like+" "+ dislike);
                                listItems.add(new Comentarios(R.layout.comentarioestilo,cometario, like, dislike));
                            }

                            adapter.notifyDataSetChanged();
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

        adapter = new CustomListAdapter(this, listItems);

        listView.setAdapter((ListAdapter) adapter);


        comentar = findViewById(R.id.imageComentar);
        comentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent abd = new Intent(Cometariosdelreporte.this, formularioComentar.class);
                abd.putExtra("id",id);
                startActivities(new Intent[]{abd});
            }
        });
    }


}