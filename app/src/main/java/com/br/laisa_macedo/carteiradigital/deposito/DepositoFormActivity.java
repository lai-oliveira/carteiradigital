package com.br.laisa_macedo.carteiradigital.deposito;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.br.laisa_macedo.carteiradigital.R;
import com.br.laisa_macedo.carteiradigital.helper.FirebaseHelper;
import com.br.laisa_macedo.carteiradigital.model.Deposito;
import com.br.laisa_macedo.carteiradigital.model.Extrato;
import com.br.laisa_macedo.carteiradigital.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;


public class DepositoFormActivity extends AppCompatActivity {


    private CurrencyEditText edtValor;
    private AlertDialog dialog;
    private ProgressBar progressBar;
    private Usuario usuario;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposito_form);

        configToolbar();
        recuperaUsuario();
        iniciaComponente();

    }

    public void validaDeposito(View view){
       double valorDeposito = (double) edtValor.getRawValue()/100;
       if (valorDeposito > 0){
         ocultarTeclado();
         progressBar.setVisibility(View.VISIBLE);

       salvarExtrato(valorDeposito);



         }else{
           showDialog("Digite um valor maior que 0.0");
       }
    }
    private void salvarExtrato(double valorDeposito){
        Extrato extrato = new Extrato();
        extrato.setOperacao("DEPOSITO");
        extrato.setValor(valorDeposito);
        extrato.setTipo("ENTRADA");

        DatabaseReference extratoRef = FirebaseHelper.getDatabaseReference()
                .child("extratos")
                .child(FirebaseHelper.getIdFirebase())
                .child(extrato.getId());
        extratoRef.setValue(extrato).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
             DatabaseReference updateExtrato = extratoRef
                     .child("data");
             updateExtrato.setValue(ServerValue.TIMESTAMP);
             salvarDeposito(extrato);

            }else{
                showDialog("Não foi possivel efetuar o deposito,tente mais tarde.");

            }
        });



    }

    private void salvarDeposito(Extrato extrato) {
        Deposito deposito = new Deposito();
        deposito.setId(extrato.getId());
        deposito.setValor(extrato.getValor());

        DatabaseReference depositoRef = FirebaseHelper.getDatabaseReference()
                .child("depositos")
                .child(deposito.getId());
        depositoRef.setValue(deposito).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                DatabaseReference updateDeposito = depositoRef
                        .child("data");
                updateDeposito.setValue(ServerValue.TIMESTAMP);

                usuario.setSaldo(usuario.getSaldo() + deposito.getValor());
                usuario.atualizarSaldo();

            Intent intent = new Intent(this,DepositoReciboActivity.class);
            intent.putExtra("idDeposito",deposito.getId());
            startActivity(intent);
            finish();


            }else {
              progressBar.setVisibility(View.GONE);
              showDialog("Não foi possivel confirmar o deposito,");
            }
        });

    }
    private void recuperaUsuario(){
        DatabaseReference usuarioRef = FirebaseHelper.getDatabaseReference()
                .child("usuario")
                .child(FirebaseHelper.getIdFirebase());
        usuarioRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuario = snapshot.getValue(Usuario.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void showDialog(String msg){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                this,R.style.CustomAlertDialog
        );
        View view = getLayoutInflater().inflate(R.layout.layout_dialog_info,null);

        TextView texTitulo = view.findViewById(R.id.textTitulo);
        texTitulo.setText("Atenção");

        TextView mensagem = view.findViewById(R.id.textMensagem);
        texTitulo.setText(msg);

        Button btnOk = view.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(v -> dialog.dismiss());

        builder.setView(view);

        dialog = builder.create();
        dialog.show();
        progressBar = findViewById(R.id.progressBar);
    }

    private void configToolbar(){
        TextView textTitulo = findViewById(R.id.textTitulo);
        textTitulo.setText("Depositar");

        findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());
    }

    private void iniciaComponente() {
        edtValor = findViewById(R.id.edtValor);
        edtValor.setLocale(new Locale("PT", "br"));

    }
    //Ocultar teclado do dispositivo
    private void ocultarTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(edtValor.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);



    }

}