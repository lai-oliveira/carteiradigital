package com.br.laisa_macedo.carteiradigital.deposito;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.br.laisa_macedo.carteiradigital.R;
import com.br.laisa_macedo.carteiradigital.helper.FirebaseHelper;
import com.br.laisa_macedo.carteiradigital.helper.GetMask;
import com.br.laisa_macedo.carteiradigital.model.Deposito;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class DepositoReciboActivity extends AppCompatActivity {

    private TextView textCodigo;
    private TextView textData;
    private TextView textValor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposito_recibo);

        configToolbar();

        iniciaComponentes();

        getDeposito();

        configCliques();

    }

    private void configCliques(){
        findViewById(R.id.btnOk).setOnClickListener(v -> finish());
    }

    private void getDeposito(){
        String idDeposito = (String) getIntent().getSerializableExtra("idDeposito");

        DatabaseReference depositoRef = FirebaseHelper.getDatabaseReference()
                .child("depositos")
                .child(idDeposito);
        depositoRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Deposito deposito = snapshot.getValue(Deposito.class);
                configDados(deposito);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configDados(Deposito deposito){
        textCodigo.setText(deposito.getId());
        textData.setText(GetMask.getDate(deposito.getData(), 3));
        textValor.setText(getString(R.string.text_valor, GetMask.getValor(deposito.getValor())));
    }

    private void configToolbar(){
        TextView textTitulo = findViewById(R.id.textTitulo);
        textTitulo.setText("Comprovante");
    }

    private void iniciaComponentes(){
        textCodigo = findViewById(R.id.textCodigo);
        textData = findViewById(R.id.textData);
        textValor = findViewById(R.id.textValor);
    }

}