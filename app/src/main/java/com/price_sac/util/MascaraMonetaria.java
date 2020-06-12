package com.price_sac.util;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.NumberFormat;
import java.util.Locale;

public class MascaraMonetaria implements TextWatcher {

    final EditText campo;

    public MascaraMonetaria(EditText campo) {
        super();
        this.campo = campo;
    }


    private boolean isUpdating = false;
    //No parametro de Locale passar os caracteres do pais
    private Locale local = new Locale("pt","BR");
    // Pega a formatacao do sistema, se for brasil R$ se EUA US$
    private NumberFormat nf = NumberFormat.getCurrencyInstance(local);


    @Override
    public void onTextChanged(CharSequence s, int start, int before, int after) {
        // Evita que o método seja executado varias vezes.
        // Se tirar ele entra em loop
        if (isUpdating) {
            isUpdating = false;
            return;
        }

        isUpdating = true;
        String str = s.toString();
        //Remove qualquer mascara se existir.
        str = str.replaceAll("[^\\d]", "");

        try {
            // Transformamos o número que está escrito no EditText em
            // monetário.
            str = nf.format(Double.parseDouble(str) / 100);
            campo.setText(str);
            campo.setSelection(campo.getText().length());
        } catch (NumberFormatException e) {
            s = "";
        }
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // Não utilizado
    }

    @Override
    public void afterTextChanged(Editable s) {
        // Não utilizado
    }
}

