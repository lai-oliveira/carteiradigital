package com.br.laisa_macedo.carteiradigital.deposito;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.br.laisa_macedo.carteiradigital.R;

import java.util.Locale;


public class DepositoFormActivity extends AppCompatActivity {


    private CurrencyEditText edtValor;
    private AlertDialog dialog;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deposito_form);

        configToolbar();
        iniciaComponente();

    }

    public void validaDeposito(View view){
       double valorDeposito = (double) edtValor.getRawValue()/100;
       if (valorDeposito > 0){
         ocultarTeclado();
         progressBar.setVisibility(View.VISIBLE);

         //salvar extrato

         }else{
           showDialog();
       }
    }
    private void showDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                this,R.style.CustomAlertDialog
        );
        View view = getLayoutInflater().inflate(R.layout.layout_dialog_info,null);

        TextView texTitulo = view.findViewById(R.id.textTitulo);
        texTitulo.setText("Atenção");

        TextView mensagem = view.findViewById(R.id.textMensagem);
        texTitulo.setText("Digite um valor maior que 0.0");

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