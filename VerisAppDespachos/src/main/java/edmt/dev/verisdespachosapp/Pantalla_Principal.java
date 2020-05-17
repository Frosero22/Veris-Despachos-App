package edmt.dev.verisdespachosapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.UiThread;
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


public class Pantalla_Principal extends AppCompatActivity  {

    CardView Picking;
    private ZXingScannerView mScannerView;
    private ProgressDialog progressDialog;
    String Usuario, Token;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantalla);
        time time = new time();
        time.execute();
        Bundle bundle = this.getIntent().getExtras();
        Usuario = bundle.getString("User","----");

        Picking = findViewById(R.id.btn_picking);
        Picking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                       RecuperaToken();



            }
        });


    }

    public void Ejecutar(){
        time time = new time();
        time.execute();
        Intent intent = new Intent(Pantalla_Principal.this,Login.class);
        startActivity(intent);
        finish();
    }


    public void ExpiraSesion() throws InterruptedException {
        Thread.sleep(3600000);
    }




    public class  time extends AsyncTask<Void,Integer,Boolean>{

        @Override
        protected Boolean doInBackground(Void... voids) {

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

                Ejecutar();

            Toast.makeText(Pantalla_Principal.this, "LA SESION EXPIRA CADA HORA", Toast.LENGTH_SHORT).show();




        }
    }







    public String RecuperaToken(){
        progressDialog = GenericUtil.barraCargando(Pantalla_Principal.this,"Levantando Informacion...");

        OkHttpClient client = new OkHttpClient();
        JsonObject postData = new JsonObject();
        postData.addProperty("user","wsphantomcajas");
        postData.addProperty("pass","CAS5789b86Mdr5Ph@nT0mC@j@$");


        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody postBody = RequestBody.create(JSON, postData.toString());
        Request post = new Request.Builder()
                .url("https://servicioscajas.veris.com.ec/PhantomCajasWS/api/authentications/login")
                .post(postBody)
                .addHeader("Authorization", "Basic  d3NwaGFudG9tY2FqYXM6Q0FTNTc4OWI4Nk1kcjVQaEBuVDBtQ0BqQCQ=" )
                .build();

        client.newCall(post).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Log.e("Error","Error"+e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {

                        throw new IOException("Error Inesperado " + response);
                    }
                    assert responseBody != null;
                    JSONObject object = new JSONObject(response.body().string());
                    Log.e("Token" ,"Token ->" + object.getString("accesToken"));
                    Token = object.getString("accesToken");
                    Dialogo(Token);



                    Log.e("Ok","Token Generado");
                } catch (Exception e) {

                    e.printStackTrace();
                    Log.e("Error","Error--->"+e);
                }
            }
        });

        return Token;
    }





    private void Dialogo(final String Token) {
        progressDialog.dismiss();
        Looper.prepare();
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
                                    .url("http://52.7.160.244:8118/PhantomCajasWS/api/farmaciaDomicilio/actualizarPickingTransaccion?argNumeroTransaccion="+Id.getText().toString().trim()+"&argCodUsuario="+Usuario)

                                    .post(postBody)

                                    .addHeader("Authorization", "Bearer "+Token)

                                    .build();
                            Log.e("POSTBODY","-------->"+postBody);
                            Log.e("POST","-------->" +post);

                            client.newCall(post).enqueue(new Callback() {


                                @Override
                                public void onFailure(Call call, IOException e) {

                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {

                                        Log.e("CALL","--------> " +call);
                                    Log.e("RESPONSE","---------->" +response);

                                    ResponseBody responseBody = response.body();
                                    Log.e("RESPONSE BODY","--->"  +responseBody);

                                    try {



                                        JSONObject jsonObject = new JSONObject(responseBody.string());



                                                    if(jsonObject.getString("mensaje").equalsIgnoreCase("OK")){


                                                        progressDialog.dismiss();
                                                        MensajeExito();



                                                    }




                                        if(jsonObject.getString("mensaje").equalsIgnoreCase("No existe el codigo de solicitud o numero de transaccion. \nMensaje generado desde la aplicacion >>. MGM_K_ORD_SERV_FARMACIA.MGM_UPT_PIKING_TRANS")){

                                                    progressDialog.dismiss();
                                                    MensajeErrorAplicacion();

                                        }else if(jsonObject.getString("mensaje").equalsIgnoreCase("Ya se realizo picking a esta solicitud.")){

                                            progressDialog.dismiss();
                                            MensajeErrorPicking();



                                        }else{

                                            progressDialog.dismiss();
                                            MensajeExito();

                                            finish();

                                        }








                                    } catch (JSONException e) {
                                        e.printStackTrace();

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

              new IntentIntegrator(Pantalla_Principal.this).initiateScan();




            }

        });


        Looper.loop();

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);

        if (result != null) {

            Log.e("MENSAJE", "---------->  " + result.getContents());

            String IdSoliticitud = result.getContents();

            OkHttpClient client = new OkHttpClient();
            JSONObject postData = new JSONObject();


            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody postBody = RequestBody.create(JSON, postData.toString());


            Request post = new Request.Builder()
                    .url("http://52.7.160.244:8118/PhantomCajasWS/api/farmaciaDomicilio/actualizarPickingTransaccion?argNumeroTransaccion=" + IdSoliticitud.trim() + "&argCodUsuario=" + Usuario)

                    .post(postBody)

                    .addHeader("Authorization", "Bearer " + Token)

                    .build();
            Log.e("POSTBODY", "--------> " + postBody);
            Log.e("POST", "--------> " + post);
            Log.e("POST", "--------> " + Token);


            client.newCall(post).enqueue(new Callback() {


                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ResponseBody responseBody = response.body();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        Log.e("RESPONSE BODY","--->"  +responseBody);

                   //     if (jsonObject.getString("success").equalsIgnoreCase("OK")) {


                        //    progressDialog.dismiss();
//

                    //    }


                        if (jsonObject.getString("mensaje").equalsIgnoreCase("No existe el codigo de solicitud o numero de transaccion. \nMensaje generado desde la aplicacion >>. MGM_K_ORD_SERV_FARMACIA.MGM_UPT_PIKING_TRANS")) {

                            progressDialog.dismiss();
                            MensajeErrorAplicacion();

                        } else if (jsonObject.getString("mensaje").equalsIgnoreCase("Ya se realizo picking a esta solicitud.")) {

                            progressDialog.dismiss();
                            MensajeErrorPicking();


                        } else {

                            progressDialog.dismiss();
                            MensajeExito();

                            finish();

                        }


                    }catch (Exception e){








                    }
                }
            });

        } else {


            Log.e("MENSAJE Error", "---------->  " + result.getContents());


        }
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
        txt.setText("Ya se realizo picking a esta solicitud.");

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
        txt.setText("No existe el codigo de solicitud o numero de transaccion. \nMensaje generado desde la aplicacion >>. MGM_K_ORD_SERV_FARMACIA.MGM_UPT_PIKING_TRANS");

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
        txt.setText("¡Picking Realizado Con Exito!");

        Button Aceptar = view.findViewById(R.id.btn_aceptar);
        Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogX.dismiss();
                Intent intent = new Intent(Pantalla_Principal.this,Pantalla_Principal.class);
                startActivity(intent);

            }


        });
        Looper.loop();










    }






}





