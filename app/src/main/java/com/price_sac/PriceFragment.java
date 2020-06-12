package com.price_sac;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.price_sac.util.MascaraMonetaria;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


/**
 * A simple {@link Fragment} subclass.
 */
public class PriceFragment extends Fragment {


    private EditText edtValor;
    private EditText edtNPrestacao;
    private EditText edtTaxa;
    private TextView txtPrestacao;
    private Button btCalcular;

    private ExpandableListView expandableListView;
    private List<String> listGroup;
    private HashMap<String, List<String>> listItem;
    private MainAdapter adapter;

    private Double valor = 0.0;
    private Double taxa = 0.0;
    private Double prestacao = 0.0;
    private int numero = 0;

    private Locale local = new Locale("pt","BR");
    private NumberFormat nf = NumberFormat.getCurrencyInstance(local);

    public PriceFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_price, container, false);
        inicar(view);
        expandableListView.setAdapter(adapter);
        calcular();
        return view;
    }


    private void inicar(View view) {
        edtValor = view.findViewById(R.id.edtValor);
        edtNPrestacao = view.findViewById(R.id.edtNPrestacao);
        edtTaxa = view.findViewById(R.id.edtTaxa);

        txtPrestacao = view.findViewById(R.id.txtPrestacao);
        btCalcular = view.findViewById(R.id.btCalcular);

        expandableListView = view.findViewById(R.id.expand);
        listGroup = new ArrayList<>();
        listItem = new HashMap<>();
        adapter = new MainAdapter(getActivity(), this.listGroup, this.listItem);

        mascara();
    }

    private void mascara() {
        edtValor.addTextChangedListener(new MascaraMonetaria(edtValor));
        edtTaxa.addTextChangedListener(new MascaraMonetaria(edtTaxa));
    }

    public void calcular() {
        btCalcular.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!listGroup.isEmpty() && !listItem.isEmpty()) {
                    listGroup = new ArrayList<>();
                    listItem = new HashMap<>();
                }
                if (verificarCampos()) {
                    prestacao = valor * ((Math.pow((1 + taxa), numero) * taxa)) / ((Math.pow((1 + taxa), numero) - 1));
                    txtPrestacao.setText("Prestação: R$ " + String.format("%.2f", prestacao));
                    iniciarLista();
                }
            }
        });
    }

    private void iniciarLista() {
        ArrayList<String> dados = new ArrayList<>();
        int n;
        double vetJuros[] = new double[numero + 1];
        double vetAmort[] = new double[numero + 1];
        double vetSaldo[] = new double[numero + 1];
        vetSaldo[0] = valor;
        vetJuros[0] = 0;
        vetAmort[0] = 0;
        for (n = 1; n <= numero; n++) {
            vetJuros[n] = vetSaldo[n - 1] * taxa;
            vetAmort[n] = prestacao - vetJuros[n];
            vetSaldo[n] = vetSaldo[n - 1] - vetAmort[n];
            listGroup.add("Pacerla Nº " + String.format("%d", n));
            dados = new ArrayList<>(Arrays.asList(
                    "Prestação: "   + nf.format(prestacao),
                    "Juros: "       + nf.format(vetJuros[n]),
                    "Amortização: " + nf.format(vetAmort[n]),
                    "Saldo: "       + nf.format(vetSaldo[n])));
            listItem.put(listGroup.get(n - 1), dados);
        }
        adapter.notifyDataSetChanged();
    }

    private boolean verificarCampos() {

        boolean validador = true;

        if (!edtValor.getText().toString().isEmpty()) {
            String s = removerMascara(edtValor.getText().toString());
            try {
                valor = DecimalFormat.getNumberInstance().parse(s).doubleValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            edtValor.setError("Preencha o campo!");
            validador = false;
        }

        if (!edtNPrestacao.getText().toString().isEmpty()) {
            numero = Integer.parseInt(edtNPrestacao.getText().toString());
        } else {
            edtNPrestacao.setError("Preencha o campo!");
            validador = false;
        }

        if (!edtTaxa.getText().toString().isEmpty()) {
            String s = removerMascara(edtTaxa.getText().toString());
            try {
                taxa = DecimalFormat.getNumberInstance().parse(s).doubleValue();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            edtTaxa.setError("Preencha o campo!");
            validador = false;
        }

        return validador;
    }

    private String removerMascara(String s) {
        s = s.replace("R$", "").
                replaceAll("\\.", "").
                replaceAll(",", ".");
        return s;
    }
}
