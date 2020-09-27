package com.jaac08.prueba_fase1;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.jaac08.prueba_fase1.ClassDB.ConexionSQliteHelper;
import com.jaac08.prueba_fase1.ClassDB.EstructuraBD;
import com.jaac08.prueba_fase1.classGlobal.AdapterPost;
import com.jaac08.prueba_fase1.classGlobal.General;
import com.jaac08.prueba_fase1.classGlobal.Mensaje;
import com.jaac08.prueba_fase1.classGlobal.SwipeDismissListViewTouchListener;
import com.jaac08.prueba_fase1.model.Post;

import java.util.ArrayList;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class FragmentFavorite extends Fragment {

    ArrayList<Post> listPost;
    AdapterPost adapterPost;
    ListView listVPostFav;
    Mensaje mensaje;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        listVPostFav = view.findViewById(R.id.listVPostFav);
        listPost = new ArrayList<>();
        mensaje = new Mensaje();
        ConsultaDBPost();

        listVPostFav.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {

                Intent intent = new Intent(getContext(),DetalleActivity.class);
                intent.putExtra("post", listPost.get(pos));
                startActivity(intent);

            }
        });

        SwipeDismissListViewTouchListener touchListener =
                new SwipeDismissListViewTouchListener(
                        listVPostFav,
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

        listVPostFav.setOnTouchListener(touchListener);

    }

    public void ConsultaDBPost(){

        try {

            SQLiteDatabase db = General.conn.getReadableDatabase();

            Cursor cursor = db.rawQuery("SELECT  ID,USERID,TITLE,BODY,READ,FAVORITE FROM POST WHERE FAVORITE=1" ,null);

            cursor.moveToFirst();

            if (cursor.getCount() == 0 ) {
                mensaje.MensajeConfirmacionAdvertencia(getContext(),"Warning","Has no favorite Post!!!");
                return;
            }

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
            adapterPost = new AdapterPost(getContext(),listPost);
            listVPostFav.setAdapter(adapterPost);
            return ;
        }
        catch(Exception e){
            mensaje.MensajeConfirmacionAdvertencia(getContext(),"Warning",e.getMessage());
        }
    }

    public void deletePost(Post post){
        try {

            SQLiteDatabase db = General.conn.getReadableDatabase();

            db.delete(EstructuraBD.TABLA_POST, "ID = ?", new String[]{String.valueOf(post.getId())});
            mensaje.MensajeExitoso(getContext(),"OK","Post removed successfully");
        }
        catch (Exception ex){
            mensaje.MensajeAdvertencia(getContext(),"Warning",ex.getMessage());
        }
    }

}