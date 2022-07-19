package com.br.laisa_macedo.carteiradigital.usuario;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.br.laisa_macedo.carteiradigital.R;

public class MinhaContaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minha_conta);

        configToolbar();

    }

    private void configToolbar(){
        TextView textTitulo = findViewById(R.id.textTitulo);
        textTitulo.setText("Perfil");

        findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());
    }

}