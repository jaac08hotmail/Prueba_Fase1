package com.jaac08.prueba_fase1.classGlobal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaac08.prueba_fase1.R;
import com.jaac08.prueba_fase1.model.Post;

import java.util.ArrayList;

public class AdapterPost extends BaseAdapter {
    private ArrayList<Post> listpost;
    private Context context;


    public AdapterPost(Context context, ArrayList<Post> listpost) {
        this.listpost = listpost;
        this.context = context;
    }

    @Override
    public int getCount() {
        return listpost.size();
    }

    @Override
    public Object getItem(int position) {
        return listpost.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Post item = (Post) getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.item_list,null);
        TextView txtVIdPost = convertView.findViewById(R.id.txtVIdPost);
        TextView txtVIdUser = convertView.findViewById(R.id.txtVIdUser);
        TextView txtVTitle = convertView.findViewById(R.id.txtVTitle);
        LinearLayout linerList = convertView.findViewById(R.id.linerList);

        txtVIdPost.setText("" + item.getId());
        txtVIdUser.setText("" + item.getUserId());
        txtVTitle.setText(item.getTitle());

        if (position<20)
            linerList.setBackgroundColor(Color.argb(100,0,145,234));

        return convertView;
    }

}

