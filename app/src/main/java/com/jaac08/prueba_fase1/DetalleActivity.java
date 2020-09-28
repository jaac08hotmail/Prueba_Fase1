package com.jaac08.prueba_fase1;

import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

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

    TextView txtVnameC,txtVBs,txtVCatchPhrase,txtVUserName,txtVIduser,txtPost;
    TextView txtVName,txtVEmail,txtVStreet,txtVSuite,txtVCity,txtVZipcode;
    TextView txtVLat,txtVlon,txtVBoby,txtVPhone,txtVWebsite,txtVFavorite;
    Button btnFavorite,btnMaps,btnExit;
    Post post;
    User users[];
        SQLiteDatabase db;
    SweetAlertDialog sweetAlertDialog;
    Mensaje mensaje;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        sweetAlertDialog = new SweetAlertDialog(this);
        mensaje = new Mensaje();
        txtVnameC = findViewById(R.id.txtVnameC);
        txtVBs = findViewById(R.id.txtVBs);
        txtVCatchPhrase = findViewById(R.id.txtVCatchPhrase);
        txtVUserName = findViewById(R.id.txtVUserName);
        txtVIduser = findViewById(R.id.txtVIduser);
        txtVName = findViewById(R.id.txtVName);
        txtVEmail = findViewById(R.id.txtVEmail);
        txtVStreet = findViewById(R.id.txtVStreet);
        txtVSuite = findViewById(R.id.txtVSuite);
        txtVCity = findViewById(R.id.txtVCity);
        txtVZipcode = findViewById(R.id.txtVZipcode);
        txtVLat = findViewById(R.id.textVLat);
        txtVlon = findViewById(R.id.txtVlon);
        txtVBoby = findViewById(R.id.txtVBoby);
        txtVPhone = findViewById(R.id.txtVPhone);
        txtVWebsite = findViewById(R.id.txtVWebsite);
        txtVFavorite = findViewById(R.id.txtVFavorite);
        txtPost = findViewById(R.id.txtPost);

        btnFavorite = findViewById(R.id.btnFavorite);
        btnMaps = findViewById(R.id.btnMaps);
        btnExit = findViewById(R.id.btnExit);

        db= General.conn.getWritableDatabase();

        post = (Post) getIntent().getExtras().getSerializable("post");
        ConsultaUser();

    }

    @Override
    public void onBackPressed(){
        UpdateInfoPost();
        /*sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(DetalleActivity.this,"Warning","Is sure to Exit?");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                try {
                    UpdateInfoPost();
                } catch (Exception e) {
                    e.printStackTrace();
                    mensaje.MensajeAdvertencia(DetalleActivity.this, "Warning", e.getMessage());
                }

            }
        });
        sweetAlertDialog.show();*/
    }

    public void ConsultaUser()  {

        String URL = General.servidor + General.routesUser + "?id=" + post.getUserId();

        sweetAlertDialog = mensaje.progreso(DetalleActivity.this,"Querying User");
        sweetAlertDialog.show();


        StringRequest postRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject result = null;
                        try {

                            if (response.length() == 0  ){
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(DetalleActivity.this, "Warning" , "No data found!!!");
                            }
                            else {
                                users = new Gson().fromJson(response, User[].class);
                                LlenarDatos(users[0]);
                                sweetAlertDialog.dismiss();
                            }
                        } catch (Exception e) {
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(DetalleActivity.this, "Warning" , e.getMessage());
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


    public void LlenarDatos(User user){

        try{
            txtVnameC.setText(user.getCompany().getName());
            txtVBs.setText(user.getCompany().getBs());
            txtVCatchPhrase.setText(user.getCompany().getCatchPhrase());
            txtVUserName.setText(user.getUsername());
            txtVIduser.setText(String.valueOf(user.getId()));
            txtVName.setText(user.getName());
            txtVEmail.setText(user.getEmail());
            txtVStreet.setText(user.getAddress().getStreet());
            txtVSuite.setText(user.getAddress().getSuite());
            txtVCity.setText(user.getAddress().getCity());
            txtVZipcode.setText(user.getAddress().getZipcode());
            txtVLat.setText(user.getAddress().getGeo().getLat());
            txtVlon.setText(user.getAddress().getGeo().getLng());
            txtVPhone.setText(user.getPhone());
            txtVWebsite.setText(user.getWebsite());
            txtVBoby.setText(post.getBody());
            txtPost.setText("INFO POST "+ post.getId());
            post.setRead(1);
            if (post.getFavorite()==1)
                btnFavorite.setBackgroundResource(R.drawable.animafavorite_on);
            else
                btnFavorite.setBackgroundResource(R.drawable.animafavorite_off);
        }
        catch(Exception e){
            mensaje.MensajeAdvertencia(DetalleActivity.this, "Warning" , e.getMessage());
        }

    }

    public void UpdateInfoPost(){
        ContentValues valores = new ContentValues();
        try {
                valores.put("READ",post.getRead());
                valores.put("FAVORITE",post.getFavorite());
                db.update(EstructuraBD.TABLA_POST, valores, "ID = ?", new String[]{String.valueOf(post.getId())});
                finish();
        }
        catch (Exception ex){
            mensaje.MensajeAdvertencia(this,"Warning",ex.getMessage());
        }
    }

    public void Onclick(View view){

        switch (view.getId()){
            case R.id.btnFavorite:
                String cadena= "";
                if (post.getFavorite()==0) {
                    cadena= "add to";
                }
                else {
                    cadena = "remove from";
                }

                sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(this,"Post Id:"+post.getId(),"You want to "+ cadena +" favorites?");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        try {
                            if (post.getFavorite()==0) {
                                post.setFavorite(1);
                                btnFavorite.setBackgroundResource(R.drawable.animafavorite_on);
                            }
                            else {
                                post.setFavorite(0);
                                btnFavorite.setBackgroundResource(R.drawable.animafavorite_off);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            mensaje.MensajeAdvertencia(DetalleActivity.this, "Warning", e.getMessage());
                        }

                    }
                });
                sweetAlertDialog.show();

                break;
            case R.id.btnMaps:
                Intent intentmaps = new Intent(Intent.ACTION_VIEW);
                intentmaps.setData(Uri.parse("http://maps.google.com/maps?f=q&q=" + txtVLat.getText() + ","+txtVlon.getText()));
                Intent chooser = Intent.createChooser(intentmaps, "launch Maps");
                startActivity(chooser);
                break;
            case R.id.btnExit:
                UpdateInfoPost();
                /*sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(DetalleActivity.this,"Warning","Is sure to Exit?");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        try {
                            UpdateInfoPost();
                        } catch (Exception e) {
                            e.printStackTrace();
                            mensaje.MensajeAdvertencia(DetalleActivity.this, "Warning", e.getMessage());
                        }

                    }
                });
                sweetAlertDialog.show();*/
                break;

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