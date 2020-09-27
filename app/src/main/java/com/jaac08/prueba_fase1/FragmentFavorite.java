package com.jaac08.prueba_fase1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


public class FragmentFavorite extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite,container,false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*Button btnHome = view.findViewById(R.id.btnHomeP);
        Button btnPolit = view.findViewById(R.id.btnPrivacy);
        WebView webView = view.findViewById(R.id.webView);

        webView.setWebViewClient(new MyWebViewClient());
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);

        btnPolit.setTextColor(Color.YELLOW);
        webView.loadUrl("http://appsmoment.net/google/Huracanes%20y%20Tormentas.html");

        btnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.interfaceComunicaFragments.llamarFragment(v);
            }
        });*/
    }
}