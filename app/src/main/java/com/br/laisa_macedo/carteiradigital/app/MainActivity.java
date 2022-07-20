package com.br.laisa_macedo.carteiradigital.app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.br.laisa_macedo.carteiradigital.R;
import com.br.laisa_macedo.carteiradigital.adapter.ExtratoAdapter;
import com.br.laisa_macedo.carteiradigital.autenticacao.LoginActivity;
import com.br.laisa_macedo.carteiradigital.cobrar.CobrarFormActivity;
import com.br.laisa_macedo.carteiradigital.deposito.DepositoFormActivity;
import com.br.laisa_macedo.carteiradigital.extrato.ExtratoActivity;
import com.br.laisa_macedo.carteiradigital.helper.FirebaseHelper;
import com.br.laisa_macedo.carteiradigital.helper.GetMask;
import com.br.laisa_macedo.carteiradigital.model.Extrato;
import com.br.laisa_macedo.carteiradigital.model.Notificacao;
import com.br.laisa_macedo.carteiradigital.model.Usuario;
import com.br.laisa_macedo.carteiradigital.notificaoes.NotificacoesActivity;
import com.br.laisa_macedo.carteiradigital.recarga.RecargaFormActivity;
import com.br.laisa_macedo.carteiradigital.transferencia.TransferenciaFormActivity;
import com.br.laisa_macedo.carteiradigital.usuario.MinhaContaActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final List<Notificacao> notificacaoList = new ArrayList<>();

    private final List<Extrato> extratoListTemp = new ArrayList<>();
    private final List<Extrato> extratoList = new ArrayList<>();

    private ExtratoAdapter extratoAdapter;
    private RecyclerView rvExtrato;

    private TextView textSaldo;
    private ProgressBar progressBar;
    private TextView textInfo;
    private ImageView imagemPerfil;
    private TextView textNotificacao;

    private Usuario usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        iniciaComponentes();

        configCliques();

        configRv();

    }

    @Override
    protected void onStart() {
        super.onStart();

        recuperaDados();
    }

    private void recuperaNotificacoes() {
        DatabaseReference notificacaoRef = FirebaseHelper.getDatabaseReference()
                .child("notificacoes")
                .child(FirebaseHelper.getIdFirebase());
        notificacaoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                notificacaoList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Notificacao notificacao = ds.getValue(Notificacao.class);
                    notificacaoList.add(notificacao);
                }

                if (notificacaoList.isEmpty()) {
                    textNotificacao.setText("0");
                    textNotificacao.setVisibility(View.GONE);
                } else {
                    textNotificacao.setText(String.valueOf(notificacaoList.size()));
                    textNotificacao.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configRv() {
        rvExtrato.setLayoutManager(new LinearLayoutManager(this));
        rvExtrato.setHasFixedSize(true);
        extratoAdapter = new ExtratoAdapter(extratoList, getBaseContext());
        rvExtrato.setAdapter(extratoAdapter);
    }

    private void recuperaExtrato() {
        DatabaseReference extratoRef = FirebaseHelper.getDatabaseReference()
                .child("extratos")
                .child(FirebaseHelper.getIdFirebase());
        extratoRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    extratoList.clear();
                    extratoListTemp.clear();

                    for (DataSnapshot ds : snapshot.getChildren()) {
                        Extrato extrato = ds.getValue(Extrato.class);
                        extratoListTemp.add(extrato);
                    }

                    textInfo.setText("");
                } else {
                    textInfo.setText("Nenhuma movimentação encontrada.");
                }

                Collections.reverse(extratoListTemp);

                for (int i = 0; i < extratoListTemp.size(); i++) {
                    if (i <= 5) {
                        extratoList.add(extratoListTemp.get(i));
                    }
                }

                progressBar.setVisibility(View.GONE);
                extratoAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void recuperaDados() {

        recuperaUsuario();

        recuperaExtrato();

        recuperaNotificacoes();

    }

    private void recuperaUsuario() {
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuarios")
                .child(FirebaseHelper.getIdFirebase());
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuario = snapshot.getValue(Usuario.class);
                configDados();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void configDados() {
        textSaldo.setText(getString(R.string.text_valor, GetMask.getValor(usuario.getSaldo())));

        if (usuario.getUrlImagem() != null) {
            Picasso.get().load(usuario.getUrlImagem())
                    .placeholder(R.drawable.loading)
                    .into(imagemPerfil);
        }

        textInfo.setText("");
        progressBar.setVisibility(View.GONE);
    }

    private void configCliques() {
        findViewById(R.id.cardDeposito).setOnClickListener(v ->
                redirecionaUsuario(DepositoFormActivity.class));

        imagemPerfil.setOnClickListener(v -> perfilUsuario());

        findViewById(R.id.cardRecarga).setOnClickListener(v ->
                redirecionaUsuario(RecargaFormActivity.class));

        findViewById(R.id.cardTransferir).setOnClickListener(v ->
                redirecionaUsuario(TransferenciaFormActivity.class));

        findViewById(R.id.cardDeslogar).setOnClickListener(v -> {
            FirebaseHelper.getAuth().signOut();
            finish();
            startActivity(new Intent(this, LoginActivity.class));
        });

        findViewById(R.id.cardExtrato).setOnClickListener(v ->
                redirecionaUsuario(ExtratoActivity.class)
        );

        findViewById(R.id.textVerTodas).setOnClickListener(v ->
                redirecionaUsuario(ExtratoActivity.class)
        );

        findViewById(R.id.btnNotificacao).setOnClickListener(v ->
                redirecionaUsuario(NotificacoesActivity.class));

        findViewById(R.id.cardCobrar).setOnClickListener(v ->
                redirecionaUsuario(CobrarFormActivity.class));

        findViewById(R.id.cardMinhaConta).setOnClickListener(v -> perfilUsuario());
    }

    private void redirecionaUsuario(Class clazz){
        startActivity(new Intent(this, clazz));
    }

    private void perfilUsuario() {
        if (usuario != null) {
            Intent intent = new Intent(this, MinhaContaActivity.class);
            intent.putExtra("usuario", usuario);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Ainda estamos recuperando as informações.", Toast.LENGTH_SHORT).show();
        }
    }

    private void iniciaComponentes() {
        textSaldo = findViewById(R.id.textSaldo);
        progressBar = findViewById(R.id.progressBar);
        textInfo = findViewById(R.id.textInfo);
        rvExtrato = findViewById(R.id.rvExtrato);
        imagemPerfil = findViewById(R.id.imagemPerfil);
        textNotificacao = findViewById(R.id.textNotificacao);
    }

}