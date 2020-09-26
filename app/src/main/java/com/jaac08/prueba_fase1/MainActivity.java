package com.jaac08.prueba_fase1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.security.identity.CipherSuiteNotSupportedException;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.jaac08.prueba_fase1.ClassDB.ConexionSQliteHelper;
import com.jaac08.prueba_fase1.ClassDB.EstructuraBD;
import com.jaac08.prueba_fase1.classGlobal.AdapterPost;
import com.jaac08.prueba_fase1.classGlobal.General;
import com.jaac08.prueba_fase1.classGlobal.Mensaje;
import com.jaac08.prueba_fase1.model.Post;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ArrayList<Post> listPost;
    AdapterPost adapterPost;
    ListView listVPost;
    SweetAlertDialog sweetAlertDialog;
    Mensaje mensaje;
    Post[] posts;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listVPost = findViewById(R.id.listVPost);
        sweetAlertDialog = new SweetAlertDialog(this);
        mensaje = new Mensaje();
        listPost = new ArrayList<Post>();
        General.conn = new ConexionSQliteHelper(this,"PruebaFase1",null,1);
        db= General.conn.getWritableDatabase();


        ConsultaPost();

        listVPost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                listPost.get(pos).setRead(true);

                mensaje.MensajeExitoso(MainActivity.this,"Post"+listPost.get(pos).getId(),"Title:"+listPost.get(pos).getTitle());
                Intent intent = new Intent(MainActivity.this,DetalleActivity.class);
                intent.putExtra("post", listPost.get(pos));
                startActivity(intent);

            }
        });
    }


    public void ConsultaPost()  {

        String URL = General.servidor + General.routesPosts;

        sweetAlertDialog = mensaje.progreso(MainActivity.this,"Consultando Datos");
        sweetAlertDialog.show();


        StringRequest postRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject result = null;
                        try {

                            if (response.length() == 0  ){
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(MainActivity.this, "Advertencia" , "No se encontraron datos!!!");
                            }
                            else {
                                posts = new Gson().fromJson(response, Post[].class);

                                for (Post post : posts) {
                                    post.setRead(false);
                                    post.setFavorite(false);
                                    insertPost(post);
                                    listPost.add(post);
                                }
                                adapterPost = new AdapterPost(MainActivity.this,listPost);
                                listVPost.setAdapter(adapterPost);
                                sweetAlertDialog.dismiss();
                            }
                        } catch (Exception e) {
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(MainActivity.this, "Advertencia" , e.getMessage());
                            e.printStackTrace();
                        }
                    }
                },errorListener
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("", "");
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                return params;
            }

        };
        //tiempo de espera de conexcion initialTimeout 4000 maxNumRetries = 0
        postRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                0,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(MainActivity.this).add(postRequest);

    }


    public void insertPost(Post post){
        ContentValues valores = new ContentValues();
        try {

        valores.put("ID",post.getId());
        valores.put("USERID",post.getUserId());
        valores.put("TITLE",post.getTitle());
        valores.put("BODY",post.getBody());
        valores.put("READ",post.getRead());

        db.insertOrThrow(EstructuraBD.TABLA_POST,null,valores);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private final Response.ErrorListener errorListener = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            NetworkResponse networkResponse = error.networkResponse;

            try {
                sweetAlertDialog.dismiss();

                if (networkResponse != null && networkResponse.statusCode == 400) {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    String responseBody = new String(error.networkResponse.data, "utf-8");
                    mensaje.MensajeAdvertencia(MainActivity.this, "Advertencia", responseBody);
                    return;
                }
                String msj = error.getMessage();
                if (msj == null) {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(MainActivity.this, "Advertencia", "Servidor No Responde");
                    return;
                } else {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(MainActivity.this, "Advertencia", msj.toString());
                    return;
                }
            }
            catch(WindowManager.BadTokenException e){
                e.printStackTrace();
            }
            catch(Exception e){
                mensaje.MensajeAdvertencia(MainActivity.this, "Advertencia", e.getMessage());
            }
        }
    };



}