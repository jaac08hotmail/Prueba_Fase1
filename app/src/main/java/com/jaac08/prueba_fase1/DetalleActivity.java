package com.jaac08.prueba_fase1;

import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.WindowManager;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.jaac08.prueba_fase1.ClassDB.EstructuraBD;
import com.jaac08.prueba_fase1.classGlobal.AdapterPost;
import com.jaac08.prueba_fase1.classGlobal.General;
import com.jaac08.prueba_fase1.classGlobal.Mensaje;
import com.jaac08.prueba_fase1.model.Post;
import com.jaac08.prueba_fase1.model.User;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DetalleActivity extends AppCompatActivity {

    Post post;
    User user;
    SQLiteDatabase db;
    SweetAlertDialog sweetAlertDialog;
    Mensaje mensaje;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        sweetAlertDialog = new SweetAlertDialog(this);
        mensaje = new Mensaje();
        db= General.conn.getWritableDatabase();

        post = (Post) getIntent().getExtras().getSerializable("post");
        post.setRead(true);
        ConsultaUser();

    }

    public void ConsultaUser()  {

        String URL = General.servidor + General.routesPosts + "?id=" +post.getUserId();

        sweetAlertDialog = mensaje.progreso(DetalleActivity.this,"Consultando Usuario");
        sweetAlertDialog.show();


        StringRequest postRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject result = null;
                        try {

                            if (response.length() == 0  ){
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(DetalleActivity.this, "Advertencia" , "No se encontraron datos!!!");
                            }
                            else {
                                user = new Gson().fromJson(response, User.class);

                                sweetAlertDialog.dismiss();
                            }
                        } catch (Exception e) {
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(DetalleActivity.this, "Advertencia" , e.getMessage());
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
        Volley.newRequestQueue(DetalleActivity.this).add(postRequest);

    }


    public void UpdateInfoPost(){
        ContentValues valores = new ContentValues();
        try {

                valores.put("READ",post.getRead());
                valores.put("FAVORITE",post.getFavorite());
                db.update(EstructuraBD.TABLA_POST, valores, "ID = ?", new String[]{String.valueOf(post.getId())});

        }
        catch (Exception ex){
            mensaje.MensajeAdvertencia(this,"Advertencia",ex.getMessage());
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
                    mensaje.MensajeAdvertencia(DetalleActivity.this, "Advertencia", responseBody);
                    return;
                }
                String msj = error.getMessage();
                if (msj == null) {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(DetalleActivity.this, "Advertencia", "Servidor No Responde");
                    return;
                } else {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(DetalleActivity.this, "Advertencia", msj.toString());
                    return;
                }
            }
            catch(WindowManager.BadTokenException e){
                e.printStackTrace();
            }
            catch(Exception e){
                mensaje.MensajeAdvertencia(DetalleActivity.this, "Advertencia", e.getMessage());
            }
        }
    };

}