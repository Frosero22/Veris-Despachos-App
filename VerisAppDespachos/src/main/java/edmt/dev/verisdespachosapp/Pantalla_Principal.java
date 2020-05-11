package edmt.dev.verisdespachosapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;

import me.dm7.barcodescanner.zxing.ZXingScannerView;



public class Pantalla_Principal extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    CardView Picking;
    private ZXingScannerView mScannerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla);

        Picking = findViewById(R.id.btn_picking);
        Picking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                        Dialogo();


            }
        });

    }

    @Override
    public void handleResult(Result result) {





    }


    private void Dialogo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Pantalla_Principal.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogo_opcion, null);

        builder.setView(view);
        final AlertDialog dialog = builder.create();
        dialog.show();

        Button BotonIngresarId = view.findViewById(R.id.btn_id);
        Button BotonLector = view.findViewById(R.id.btn_lector);
        BotonIngresarId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(Pantalla_Principal.this);

                LayoutInflater inflater = getLayoutInflater();

                View view = inflater.inflate(R.layout.dialog_personalizado, null);

                builder.setView(view);


                final AlertDialog dialog = builder.create();
                dialog.show();

                final EditText Id = view.findViewById(R.id.edit_id);
                Button BotonId = view.findViewById(R.id.btn_procesar);
                BotonId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                            String Solicitud = Id.getText().toString().trim();
                    }
                });

                Button BotonCancelar = view.findViewById(R.id.btn_cancelar_pick);
                BotonCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                //SERVICIO REGISTRAR PICK




            }
        });

        BotonLector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                IntentIntegrator integrator = new IntentIntegrator(Pantalla_Principal.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                integrator.setPrompt("Escanea el Codigo Qr");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(true);
                integrator.initiateScan();


            }

        });

    }

}





