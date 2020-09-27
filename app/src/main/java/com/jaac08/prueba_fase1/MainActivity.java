package com.jaac08.prueba_fase1;

import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.jaac08.prueba_fase1.ClassDB.ConexionSQliteHelper;
import com.jaac08.prueba_fase1.ClassDB.EstructuraBD;
import com.jaac08.prueba_fase1.classGlobal.AdapterPost;
import com.jaac08.prueba_fase1.classGlobal.General;
import com.jaac08.prueba_fase1.classGlobal.Mensaje;
import com.jaac08.prueba_fase1.classGlobal.SwipeDismissListViewTouchListener;
import com.jaac08.prueba_fase1.classGlobal.iComunicaFragments;
import com.jaac08.prueba_fase1.model.Post;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class MainActivity extends AppCompatActivity
        implements Toolbar.OnMenuItemClickListener{

    ArrayList<Post> listPost;
    FloatingActionButton fabDeletePost;
    Toolbar toolbar;
    Boolean init=false;
    AdapterPost adapterPost;
    ListView listVPost;
    SweetAlertDialog sweetAlertDialog;
    Mensaje mensaje;
    Post[] posts;
    SQLiteDatabase db;
    FragmentFavorite fragmentFavorite;
    FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listVPost = findViewById(R.id.listVPost);
        fabDeletePost = findViewById(R.id.fabDeletePost);
        toolbar = findViewById(R.id.toolbar);

        sweetAlertDialog = new SweetAlertDialog(this);
        mensaje = new Mensaje();
        init=true;
        listPost = new ArrayList<>();
        General.conn = new ConexionSQliteHelper(this,"PruebaFase1",null,1);
        db= General.conn.getWritableDatabase();


        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener((Toolbar.OnMenuItemClickListener) this);

        fragmentFavorite = new FragmentFavorite();
        manager = getSupportFragmentManager();


        if (!ConsultaInitDBPost())
            ConsultaUrlPost();
        else
            ConsultaDBPost();

        listVPost.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                Intent intent = new Intent(MainActivity.this,DetalleActivity.class);
                intent.putExtra("post", listPost.get(pos));
                startActivity(intent);

            }
        });

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listVPost,
                        new SwipeDismissListViewTouchListener.DismissCallbacks() {
                            @Override
                            public boolean canDismiss(int position) {
                                return true;
                            }

                            @Override
                            public void onDismiss(ListView listView, int[] reverseSortedPositions) {
                                for (int position : reverseSortedPositions) {
                                    deletePost(listPost.get(position));
                                    listPost.remove(position);
                                    adapterPost.notifyDataSetChanged();
                                }

                            }
                        });

        listVPost.setOnTouchListener(touchListener);


        fabDeletePost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(MainActivity.this,"Warning","Are you sure to remove all posts?");
                sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        sweetAlertDialog.dismiss();
                        try {
                            listPost.clear();
                            listVPost.setAdapter(null);
                            db.execSQL("delete from " + EstructuraBD.TABLA_POST);
                            mensaje.MensajeExitoso(MainActivity.this,"OK","All Post removed successfully");

                        } catch (Exception e) {
                            e.printStackTrace();
                            mensaje.MensajeAdvertencia(MainActivity.this, "Warning", e.getMessage());
                        }

                    }
                });
                sweetAlertDialog.show();
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

    }


    @Override
    protected void onRestart() {
        super.onRestart();
        if (init)
            ConsultaDBPost();
    }

    @Override
    public void onBackPressed(){
        sweetAlertDialog = mensaje.MensajeConfirmacionAdvertenciaConBotones(MainActivity.this,"Warning","Is sure to Exit?");
        sweetAlertDialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                sweetAlertDialog.dismiss();
                try {
                    finish();
                } catch (Exception e) {
                    e.printStackTrace();
                    mensaje.MensajeAdvertencia(MainActivity.this, "Warning", e.getMessage());
                }

            }
        });
        sweetAlertDialog.show();
    }

    public Boolean ConsultaInitDBPost(){

        try {

            SQLiteDatabase db = General.conn.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT  ID FROM POST" ,null);

            cursor.moveToFirst();

            if (cursor.getCount() != 0 )
                return true;
            else
                return false;
        }
        catch(Exception e){
            mensaje.MensajeConfirmacionAdvertencia(this,"Advertecia",e.getMessage());
            return false;
        }
    }

    public void ConsultaDBPost(){

        try {

            SQLiteDatabase db = General.conn.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT  ID,USERID,TITLE,BODY,READ,FAVORITE FROM POST" ,null);

            cursor.moveToFirst();

            if (cursor.getCount() == 0 )
                return;

            listPost.clear();
            for (int cont=0;cont< cursor.getCount();cont++){
              Post post = new Post();
              post.setId(Integer.parseInt(cursor.getString(0)));
              post.setUserId(Integer.parseInt(cursor.getString(1)));
              post.setTitle(cursor.getString(2));
              post.setBody(cursor.getString(3));
              post.setRead(Integer.parseInt(cursor.getString(4)));
              post.setFavorite(Integer.parseInt(cursor.getString(5)));
              listPost.add(post);
              cursor.moveToNext();
            }
            adapterPost = new AdapterPost(MainActivity.this,listPost);
            listVPost.setAdapter(adapterPost);
            return ;
        }
        catch(Exception e){
            mensaje.MensajeConfirmacionAdvertencia(this,"Advertecia",e.getMessage());
        }
    }

    public void ConsultaUrlPost()  {

        String URL = General.servidor + General.routesPosts;

        sweetAlertDialog = mensaje.progreso(MainActivity.this,"Consulting posts");
        sweetAlertDialog.show();


        StringRequest postRequest = new StringRequest(Request.Method.GET, URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject result = null;
                        try {

                            if (response.length() == 0  ){
                                sweetAlertDialog.dismiss();
                                mensaje.MensajeAdvertencia(MainActivity.this, "Warning" , "No data found!!!");
                            }
                            else {
                                posts = new Gson().fromJson(response, Post[].class);

                                for (Post post : posts) {
                                    post.setRead(0);
                                    post.setFavorite(0);
                                    insertPost(post);
                                    listPost.add(post);
                                }
                                adapterPost = new AdapterPost(MainActivity.this,listPost);
                                listVPost.setAdapter(adapterPost);
                                sweetAlertDialog.dismiss();
                            }
                        } catch (Exception e) {
                            sweetAlertDialog.dismiss();
                            mensaje.MensajeAdvertencia(MainActivity.this, "Warning" , e.getMessage());
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
        valores.put("FAVORITE",post.getFavorite());

        db.insertOrThrow(EstructuraBD.TABLA_POST,null,valores);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void deletePost(Post post){
        try {
            db.delete(EstructuraBD.TABLA_POST, "ID = ?", new String[]{String.valueOf(post.getId())});
            mensaje.MensajeExitoso(this,"OK","Post removed successfully");
        }
        catch (Exception ex){
            mensaje.MensajeAdvertencia(this,"Warning",ex.getMessage());
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
                    mensaje.MensajeAdvertencia(MainActivity.this, "Warning", responseBody);
                    return;
                }
                String msj = error.getMessage();
                if (msj == null) {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(MainActivity.this, "Warning", "Server Not Responding");
                    return;
                } else {
                    error.printStackTrace();
                    sweetAlertDialog.dismiss();
                    mensaje.MensajeAdvertencia(MainActivity.this, "Warning", msj.toString());
                    return;
                }
            }
            catch(WindowManager.BadTokenException e){
                e.printStackTrace();
            }
            catch(Exception e){
                mensaje.MensajeAdvertencia(MainActivity.this, "Warning", e.getMessage());
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemFav:
                View view= null;
                listVPost.setVisibility(View.INVISIBLE);
                fabDeletePost.setVisibility(View.INVISIBLE);
                manager.beginTransaction().add(R.id.contFragment,fragmentFavorite).commit();
                //Toast.makeText(MainActivity.this,"Opcion Favorites",Toast.LENGTH_SHORT).show();
                break;
            case R.id.itemRefres:
                Toast.makeText(MainActivity.this,"Opcion Refress",Toast.LENGTH_SHORT).show();
                break;
            default:
        }
        return false;
    }

}