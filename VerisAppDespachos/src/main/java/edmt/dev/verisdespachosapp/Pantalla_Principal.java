package edmt.dev.verisdespachosapp;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.JsonObject;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import edmt.dev.verisdespachosapp.ApiS.GenericUtil;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class Pantalla_Principal extends AppCompatActivity {

    CardView Picking;
    private ZXingScannerView mScannerView;

    private ProgressDialog progressDialog;
    private long backPressedTime;
    private Toast BackToast;
    String Usuario;
    String Token;
    String Nombre;
    int CodigoSucursal;
    String NombreSucursal;
    TextView Nombres, Nsucursal;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla);
        time time = new time();
        time.execute();

        Bundle bundle = this.getIntent().getExtras();

        Token = bundle.getString("Token","----");
        Log.e("TOKEN OBTENIDO","----------->" +Token);

        Nombres = findViewById(R.id.txt_Nombre);
        Nsucursal = findViewById(R.id.txt_Sucursal);
        CargarPreferencias();



        Picking = findViewById(R.id.btn_picking);
        Picking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialogo();




            }
        });


    }

    @Override
    public void onBackPressed() {



        if(backPressedTime + 2000 > System.currentTimeMillis()){
            Intent intent = new Intent(Pantalla_Principal.this,Login.class);
            startActivity(intent);
            finish();
            super.onBackPressed();
            return;
        }else{
         Toast.makeText(getBaseContext(), "VUELVA A PULSAR PARA CERRAR SESIÓN", Toast.LENGTH_SHORT).show();

        }



        backPressedTime = System.currentTimeMillis();


    }

    //EL PROCEDIMIENTO QUE SE LLEVA ACABO CUANDO SE ALCANZA LA HORA
    public void Ejecutar(){
        time time = new time();
        time.execute();
        Intent intent = new Intent(Pantalla_Principal.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

        //INDICA EL TIEMPO DE SESION MAXIMO
    public void ExpiraSesion() throws InterruptedException {
        //1 HORA
        Thread.sleep(86400000);
    }




    public class  time extends AsyncTask<Void,Integer,Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {
        //SE INDICA QUE CADA UNA HORA SE REPITA EL PROCESO
            for(int i = 1; i == 1; i++){

                try {
                    ExpiraSesion();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e("ERROR","---->"+e);
                }

            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {

            //PROCESO DESPUES DE EJECUTARSE EL TIEMPO DE EXPIRACION

                Ejecutar();

            Toast.makeText(Pantalla_Principal.this, "LA SESION EXPIRA CADA HORA", Toast.LENGTH_SHORT).show();




        }
    }






//METODO PARA RECUPERAR EL TOKEN NECESARIO PARA LO SERVICIOS DE FARMACIA




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

                final View view = inflater.inflate(R.layout.dialog_personalizado, null);

                builder.setView(view);


                final AlertDialog dialogP = builder.create();
                dialogP.show();
                dialogP.setCancelable(false);
                final EditText Id = view.findViewById(R.id.edit_id);

                Button BotonId = view.findViewById(R.id.btn_procesar);


                BotonId.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String Codigo = Id.getText().toString().trim();

                        if(Codigo.isEmpty()){

                            Toast.makeText(Pantalla_Principal.this, "INGRESE UN NUMERO DE SOLICITUD PARA CONTINUAR", Toast.LENGTH_LONG).show();
                        }else{

                        progressDialog = GenericUtil.barraCargando(Pantalla_Principal.this,"Realizando Picking...");

                        try {

                            Log.e("Usuario", "RETORNADO ---> " + Usuario);
                            Log.e("Codigo es --> ", "CODIGO : " + Id.getText().toString().trim());
                            Log.e("Token", "OBTENIDO ---> " +Token);


                            OkHttpClient client = new OkHttpClient();
                            JSONObject postData = new JSONObject();

                           // postData.put("argCodUsuario",Usuario);
                          //  postData.put("argNumeroTransaccion",Id.getText().toString().trim());


                            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                            RequestBody postBody = RequestBody.create(JSON, postData.toString());



                            Request post = new Request.Builder()
                                    .url("http://52.7.160.244:8118/PhantomCajasWS/api/farmaciaDomicilio/actualizarPickingTransaccion?argNumeroTransaccion="+Id.getText().toString().trim()+"&argCodUsuario="+Usuario+"&argCodSucursal="+CodigoSucursal)

                                    .post(postBody)
                                    .addHeader("Authorization", "Bearer "+Token)

                                    .build();
                            Log.e("POSTBODY","-------->"+postBody);
                            Log.e("POST","-------->" +post);

                            client.newCall(post).enqueue(new Callback() {


                                @Override
                                public void onFailure(Call call, IOException e) {

                                    MensajeErrorServicio(e);
                                    progressDialog.dismiss();
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {

                                        Log.e("CALL","--------> " +call);
                                    Log.e("RESPONSE","---------->" +response);

                                    ResponseBody responseBody = response.body();
                                    Log.e("RESPONSE BODY","--->"  +responseBody);

                                    try {




                                        //CAPTURO EL JSON QUE ME RETORNA EL SERVICIO
                                        JSONObject jsonObject = new JSONObject(responseBody.string());



                                                    if(jsonObject.getString("mensaje").equalsIgnoreCase("OK")) {


                                                        progressDialog.dismiss();
                                                        MensajeExito();


                                                    }else if(jsonObject.getString("mensaje").equalsIgnoreCase("No existe el codigo de solicitud o numero de transaccion. \nMensaje generado desde la aplicacion >>. MGM_K_ORD_SERV_FARMACIA.MGM_UPT_PIKING_TRANS")){

                                                    progressDialog.dismiss();
                                                    MensajeErrorAplicacion();

                                        }else if(jsonObject.getString("mensaje").equalsIgnoreCase("Ya se realizo picking a esta solicitud.")){

                                            progressDialog.dismiss();
                                            MensajeErrorPicking();



                                        }else if(jsonObject.getString("mensaje").equalsIgnoreCase("Esta solicitud ya fue asignada a una guia de despacho.")){

                                                        progressDialog.dismiss();
                                                        MensajeAsignacion();


                                                    }



                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        progressDialog.dismiss();
                                        Looper.prepare();
                                        Toast.makeText(Pantalla_Principal.this, "ERROR"+e, Toast.LENGTH_LONG).show();
                                        Looper.loop();

                                        Log.e("ERROR","ERROR --------> "+e);


                                    }

                                }

                            });

                        }catch (Exception e){

                            Looper.prepare();
                            AlertDialog.Builder builder = new AlertDialog.Builder(Pantalla_Principal.this);

                            LayoutInflater inflater = getLayoutInflater();

                            View viewV = inflater.inflate(R.layout.dialogo_error, null);

                            builder.setView(viewV);


                            final AlertDialog dialogM = builder.create();
                            dialogM.show();
                            dialogM.setCancelable(false);

                            TextView txt = viewV.findViewById(R.id.text_error);
                            txt.setText("Error ---> "+e);

                            Button Aceptar = view.findViewById(R.id.btn_acept);
                            Aceptar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialogM.dismiss();
                                }


                            });
                            Looper.loop();


                        }

                        }
                    }

                });



                Button BotonCancelar = view.findViewById(R.id.btn_cancelar_pick);
                BotonCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogP.dismiss();
                    }
                });



            }
        });


        BotonLector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



             IntentIntegrator integrator = new IntentIntegrator(Pantalla_Principal.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
                integrator.setPrompt("Escanea El Codigo Qr");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setCaptureActivity(LectorPortrait.class);
                integrator.setBarcodeImageEnabled(false);
                integrator.setOrientationLocked(false);
                integrator.initiateScan();


            }


        });


        Looper.loop();

    }



//METODO PARA EL LECTOR
    @Override
   protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);




        if (result != null) {


            String IdSoliticitud = result.getContents();

            OkHttpClient client = new OkHttpClient();


            JSONObject postData = new JSONObject();



            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody postBody = RequestBody.create(JSON, postData.toString());


            Request post = new Request.Builder()
                    .url("http://52.7.160.244:8118/PhantomCajasWS/api/farmaciaDomicilio/actualizarPickingTransaccion?argNumeroTransaccion="+IdSoliticitud+"&argCodUsuario=" + Usuario+"&argCodSucursal="+CodigoSucursal)

                    .post(postBody)

                    .addHeader("Authorization", "Bearer " + Token)

                    .build();
            Log.e("POSTBODY", "--------> " + postBody);
            Log.e("POST", "--------> " + post);
            Log.e("POST", "--------> " + Token);


            client.newCall(post).enqueue(new Callback() {


                @Override
                public void onFailure(Call call, IOException e) {

                    MensajeErrorServicio(e);
                    progressDialog.dismiss();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ResponseBody responseBody = response.body();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        Log.e("RESPONSE BODY","--->"  +responseBody);

                      if (jsonObject.getString("mensaje").equalsIgnoreCase("OK")) {


                         progressDialog.dismiss();
                         MensajeExito();

                      }else if (jsonObject.getString("mensaje").equalsIgnoreCase("No existe el codigo de solicitud o numero de transaccion. \nMensaje generado desde la aplicacion >>. MGM_K_ORD_SERV_FARMACIA.MGM_UPT_PIKING_TRANS")) {

                            progressDialog.dismiss();
                            MensajeErrorAplicacion();

                        } else if (jsonObject.getString("mensaje").equalsIgnoreCase("Ya se realizo picking a esta solicitud.")) {

                            progressDialog.dismiss();
                            MensajeErrorPicking();


                        }else if(jsonObject.getString("mensaje").equalsIgnoreCase("Esta solicitud ya fue asignada a una guia de despacho.")){

                          progressDialog.dismiss();
                          MensajeAsignacion();

                      }


                    }catch (Exception e){
                        Log.e("MENSAJE","ERROR ----> " +e);

                    }
                }
            });

        }
    }

    private void CargarPreferencias(){

    SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
    String user = preferences.getString("user","");
    String nombreSucursal = preferences.getString("nombreS","");
    Log.e("PREFERENCIA","-----> " +nombreSucursal);
    int codSucursal = preferences.getInt("codSucursal",CodigoSucursal);
    Log.e("PREFERENCIA","CODIGO"+codSucursal);
    String nombreUsuario = preferences.getString("nombre","");


    Nombres.setText("BIENVENIDO " +nombreUsuario);

    Usuario = user;

    Nsucursal.setText("SUCURSAL "+nombreSucursal);




}

    public void MensajeErrorPicking(){
        Looper.prepare();
        AlertDialog.Builder builder = new AlertDialog.Builder(Pantalla_Principal.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogo_error, null);

        builder.setView(view);


        final AlertDialog dialogM = builder.create();
        dialogM.show();
        dialogM.setCancelable(false);

        TextView txt = view.findViewById(R.id.text_error);
        txt.setText("Ya se realizó picking a esta solicitud.");

        Button Aceptar = view.findViewById(R.id.btn_acept);
        Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogM.dismiss();
            }


        });
        Looper.loop();



    }


    public void MensajeErrorAplicacion(){
            Looper.prepare();

        AlertDialog.Builder builder = new AlertDialog.Builder(Pantalla_Principal.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogo_error, null);

        builder.setView(view);


        final AlertDialog dialogE = builder.create();
        dialogE.show();
        dialogE.setCancelable(false);

        TextView txt = view.findViewById(R.id.text_error);
        txt.setText("No existe el código de solicitud o numero de transacción");

        Button Aceptar = view.findViewById(R.id.btn_acept);
        Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogE.dismiss();
            }


        });
        Looper.loop();


    }


    public void MensajeErrorServicio(IOException e){
        Looper.prepare();

        AlertDialog.Builder builder = new AlertDialog.Builder(Pantalla_Principal.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogo_error, null);

        builder.setView(view);

        final AlertDialog dialogE = builder.create();

        dialogE.show();

        dialogE.setCancelable(false);

        TextView txt = view.findViewById(R.id.text_error);
        txt.setText("Error al Ejecutar el Servicio Web, Comuníquese con el Área de Sistemas");

        Button Aceptar = view.findViewById(R.id.btn_acept);
        Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogE.dismiss();
            }


        });
        Looper.loop();


    }

    public void MensajeAsignacion(){
        Looper.prepare();

        AlertDialog.Builder builder = new AlertDialog.Builder(Pantalla_Principal.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogo_error, null);

        builder.setView(view);

        final AlertDialog dialogE = builder.create();

        dialogE.show();

        dialogE.setCancelable(false);

        TextView txt = view.findViewById(R.id.text_error);
        txt.setText("Esta solicitud ya fue asignada a una guía de despacho");

        Button Aceptar = view.findViewById(R.id.btn_acept);
        Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogE.dismiss();
            }


        });
        Looper.loop();


    }

    public void MensajeExito(){
        Looper.prepare();
        AlertDialog.Builder builder = new AlertDialog.Builder(Pantalla_Principal.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogo_exito, null);

        builder.setView(view);


        final AlertDialog dialogX = builder.create();
        dialogX.show();
        dialogX.setCancelable(false);

        TextView txt = view.findViewById(R.id.text_exito);
        txt.setText("¡Picking Realizado Con Éxito!");

        Button Aceptar = view.findViewById(R.id.btn_aceptar);
        Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogX.dismiss();


            }


        });
        Looper.loop();










    }






}





