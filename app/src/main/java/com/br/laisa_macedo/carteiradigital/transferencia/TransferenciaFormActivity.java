package com.br.laisa_macedo.carteiradigital.transferencia;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.blackcat.currencyedittext.CurrencyEditText;
import com.br.laisa_macedo.carteiradigital.R;

import java.util.Locale;

public class TransferenciaFormActivity extends AppCompatActivity {

    private CurrencyEditText edtValor;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transferencia_form);

        configToolbar();
        iniciaComponentes();
    }

    public void validaDados(View view){
        double valor = (double) edtValor.getRawValue()/100;
        if(valor > 0){

            ocultarTeclado();
            Toast.makeText(this, "Transferencia realizada!", Toast.LENGTH_SHORT).show();
        }else{
            showDialog( );
        }
    }

    private void showDialog( ){
        AlertDialog.Builder builder = new AlertDialog.Builder(
                this,R.style.CustomAlertDialog
        );
        View view = getLayoutInflater().inflate(R.layout.layout_dialog_info,null);

        TextView texTitulo = view.findViewById(R.id.textTitulo);
        texTitulo.setText("Atenção");

        TextView mensagem = view.findViewById(R.id.textMensagem);
        mensagem.setText("");

        Button btnOk = view.findViewById(R.id.btnOk);
        btnOk.setOnClickListener(v -> dialog.dismiss());

        builder.setView(view);

        dialog = builder.create();
        dialog.show();

    }

    private void configToolbar(){
        TextView textTitulo = findViewById(R.id.textTitulo);
        textTitulo.setText("Transferir");

        findViewById(R.id.ibVoltar).setOnClickListener(v -> finish());
    }


    //Ocultar teclado do dispositivo
    private void ocultarTeclado() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(edtValor.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);

    }

    private void iniciaComponentes(){
        edtValor = findViewById(R.id.edtValor);
        edtValor.setLocale(new Locale("pt","BR"));
    }


}