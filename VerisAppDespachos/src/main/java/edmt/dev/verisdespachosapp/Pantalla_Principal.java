package edmt.dev.verisdespachosapp;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import edmt.dev.verisdespachosapp.ApiS.GenericUtil;
import edmt.dev.verisdespachosapp.ApiS.Preferencias;
import edmt.dev.verisdespachosapp.Atributos.Sucursales;
import edmt.dev.verisdespachosapp.PickUp.ListadoPickUp;
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

    CardView Picking, PickUp;
    private ProgressDialog progressDialog;
    private long backPressedTime;
    String Usuario, Pass;
    String Token;
    String Nombre;
    Integer val = 0;
    int CodigoSucursal;
    TextView Nombres, Nsucursal;
    private final OkHttpClient client = new OkHttpClient();

    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pantalla);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Menu Principal");

        //EJECUTO LA TAREA QUE SE REALIZA POR SI EL USUARIO DEJA EN SEGUNDO PLANO LA APLICACION SIN CERRAR SESION
        time time = new time();
        time.execute();

        //INSTANCIO EL REPOSITORIO CREADO EN EL LOGIN
        SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);

        //RECUPERO LOS DATOS GUARDADOS EN MEMORIA
       String user = preferences.getString("user", "");
      String pass = preferences.getString("pass","");
       String nombreSucursal = preferences.getString("nombreS", "");

      Log.e("PREFERENCIA", "-----> " + nombreSucursal);

       // CREO UNA VARIABLE Y GUARDO EN ELLA EL CODIGO DE LA SUCURSAL
       CodigoSucursal = preferences.getInt("codSucursal", 0);
       Log.e("PREFERENCIA", "CODIGO" + CodigoSucursal);

        //GUARDO EN UNA VARIABLE EL NOMBRE DEL USUARIO
        String nombreUsuario = preferences.getString("nombre", "");

        //OBTENGO EL TOKEN GUARDADO EN MEMORIA
        Token = preferences.getString("Token", "");
        Log.e("Token Obtenido", "--------->" + Token);


        Nombres = findViewById(R.id.txt_Nombre);
        Nsucursal = findViewById(R.id.txt_Sucursal);


        Nombres.setText("Bienvenido: " + nombreUsuario);

        //GUARDO EN MEMORIA EL USUARIO Y LA CONTRASEÑA DEL USUARIO
        Usuario = user;
        Pass = pass;

        Nsucursal.setText("Sucursal: " + nombreSucursal);


        Picking = findViewById(R.id.btn_picking);
        Picking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Dialogo();


            }
        });

        PickUp = findViewById(R.id.btn_pickup);
        PickUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

              //  Intent intent = new Intent(Pantalla_Principal.this, ListadoPickUp.class);
                //startActivity(intent);

            }
        });


    }

    //METODO QUE CREA EL ITEM EN EL TOOLBAR
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);

    return true;
    }


    //METODO QUE CONTROLA LOS ITEMS DEL TOOLBAR
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //ITEM PARA REINICIAR EL APP
        if (id == R.id.menu_reiniciar_app) {
            Intent intent = new Intent(Pantalla_Principal.this,SplashScreen.class);
            startActivity(intent);
            finish();

            //ITEM PARA CAMBIAR LA SUCURSAL
        }else if(id == R.id.menu_cambiar_sucursal){

            CambiarSucursal();

        //ITEM PARA CERRAR SESION
        }else if(id == R.id.menu_cerar_sesion){

            //COLOCO EN FALSE EL ESTADO DE SESION PARA IR AL LOGIN
            Preferencias.savePreferenciaBoolean(Pantalla_Principal.this, false, "estado.buton.sesion");

            Intent intent = new Intent(Pantalla_Principal.this, Pantalla_Login.class);
            startActivity(intent);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

    //METODO PARA CAMBIAR DE SUCURSAL
    public void CambiarSucursal(){


        try {

            progressDialog = GenericUtil.barraCargando(Pantalla_Principal.this, "Buscando Sucursales...");

            //VERIFICO SI OBTENGO EL TOKEN GUARDADO EN MEMORIA DE FORMA EXITOSA
            Log.e("Token Recogido --->", "Token   " + Token);

            //INSTANCIO LA LIBRERIA PARA EJECUTAR EL SERVICIO CON SUS RESPECTIVOS LIMITES DE 10 SEGUNDOS
            OkHttpClient cliente = client.newBuilder()

                    .connectTimeout(10, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(10, TimeUnit.SECONDS)
                    .build();

            JSONObject postData = new JSONObject();


            //MANDO POR PARAMETRO LOS DATOS NECESARIOS PARA EJECUTAR DE FORMA CORRECTA EL SERVICIO, ESTOS DATOS ESTAN GUARDADOS EN MEMORIA
            postData.put("user", Usuario);
            postData.put("pass", Pass);

            Log.e("Usuario", "User es = " + Usuario);
            Log.e("Pass", "Pass es = " + Pass);

            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody postBody = RequestBody.create(JSON, postData.toString());
            final Request post = new Request.Builder()
                    .url("https://servicioscajas.veris.com.ec/PhantomCajasWS/api/farmaciaDomicilio/loginUser")
                    .post(postBody)
                    .addHeader("Authorization", "Bearer " + Token)

                    .build();

            cliente.newCall(post).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    progressDialog.dismiss();
                    MensajeError("Ocurrio un Error al conectarse con internet, Vuelva a intentarlo");
                    Log.e("Error", "Error al ejecutarr servicio" + e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    try{
                        ResponseBody responseBody = response.body();
                        final JSONObject jsonSucursales = new JSONObject(responseBody.string());


                        //SI EL TIEMPO DE ESPERA DEL SERVICIO LLEGA AL MAXIMO, SE SALTA ESTA PRIMERA VALIDACION
                        if (!response.isSuccessful()) {

                            progressDialog.dismiss();
                            MensajeExpiracion("TIEMPO LIMITE DE SESION ALCANZADO");
                            throw new IOException("Error Inesperado " + response);

                            //SI EL TOKEN ESTA EXPIRADO, BOTA EL SIGUIENTE MENSAJE
                        }else if (jsonSucursales.getString("mensaje").equalsIgnoreCase("Token incorrecto o expirado")) {

                            MensajeExpiracion("Tiempo limite de Sesion Excedido, Reinicie la Aplicacion");

                        }
                        final ArrayList<Sucursales> lista = new ArrayList<Sucursales>();


                        assert responseBody != null;

                        Log.e("DATOS", "--->" + jsonSucursales);


                            if(jsonSucursales.getInt("codigo")==0){


                                String listaSucursales = jsonSucursales.getString("lsSucursales");
                                JSONArray jsonArray = new JSONArray(listaSucursales);


                                Log.e("LISTA", "--->" + listaSucursales);

                                for (int sucursales = 0; sucursales < jsonArray.length(); sucursales++) {

                                    Sucursales e = new Sucursales();

                                    e.setNombreSucursal(jsonArray.getJSONObject(sucursales).getString("nombreSucursal"));
                                    e.setCodigoEmpresa(jsonArray.getJSONObject(sucursales).getInt("codigoEmpresa"));
                                    e.setCodigoSucursal(jsonArray.getJSONObject(sucursales).getInt("codigoSucursal"));

                                    lista.add(e);

                                }

                                String datosUsuario = jsonSucursales.getString("usuario");
                                JSONObject json = new JSONObject(datosUsuario);


                                Nombre = json.getString("nombreUsuario");
                                Log.e("USUARIO ", "----->" + Nombre);

                                Looper.prepare();
                                ArrayAdapter<Sucursales> adapterSucursales = new ArrayAdapter<Sucursales>(Pantalla_Principal.this, android.R.layout.simple_dropdown_item_1line, lista);
                                progressDialog.dismiss();

                                AlertDialog.Builder builder = new AlertDialog.Builder(Pantalla_Principal.this);
                                final LayoutInflater inflater = getLayoutInflater();
                                View view = inflater.inflate(R.layout.sucursales, null);
                                builder.setView(view);
                                final AlertDialog dialogM = builder.create();
                                dialogM.show();
                                // dialogM.setCancelable(false);
                                ListView ListaS = view.findViewById(R.id.lista_sucursales);

                                ListaS.setAdapter(adapterSucursales);

                                ListaS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                                         int CodEmpresa = lista.get(position).getCodigoEmpresa();
                                         final int CodSucurusal = lista.get(position).getCodigoSucursal();
                                         final String NombreSucursal = lista.get(position).getNombreSucursal();

                                        Log.e("CODIGO", "------>" + CodEmpresa);
                                        Log.e("SUCURSAL", "----->" + CodSucurusal);
                                        Log.e("NOMBRE", "----->" + NombreSucursal);


                                        OkHttpClient cliente = client.newBuilder()

                                                .connectTimeout(15, TimeUnit.SECONDS)
                                                .readTimeout(15, TimeUnit.SECONDS)
                                                .writeTimeout(15, TimeUnit.SECONDS)
                                                .build();


                                        Request request = new Request.Builder()
                                                .url("https://servicioscajas.veris.com.ec/PhantomCajasWS/api/farmaciaDomicilio/rolesPorSucursalUsuario?argCodEmpresa=" + CodEmpresa + "&argCodSucursal=" + CodSucurusal + "&argUsuario=" +Usuario)
                                                .method("GET", null)
                                                .addHeader("Authorization", "Bearer " + Token)
                                                .build();


                                        cliente.newCall(request).enqueue(new Callback() {
                                            @Override
                                            public void onFailure(Call call, IOException e) {

                                                progressDialog.dismiss();
                                                e.printStackTrace();
                                                MensajeError("Ocurrio un Error" +e);
                                                Log.e("ERROR", "----> " + e);

                                            }

                                            @Override
                                            public void onResponse(Call call, Response response) throws IOException {

                                                try {
                                                    ResponseBody responseBody = response.body();
                                                    JSONObject jsonObject = new JSONObject(responseBody.string());

                                                    if (jsonObject.getString("success").equalsIgnoreCase("OK")) {

                                                        String listaRol = jsonObject.getString("lsUsuarioXRol");
                                                        JSONArray jsonArray = new JSONArray(listaRol);


                                                        for (int a = 0; a < jsonArray.length(); a++) {

                                                            JSONObject json = jsonArray.getJSONObject(a);

                                                            Log.e("ARRAYS", "ENCONTRADOS " + json.getString("codigoRol"));

                                                            if (json.getString(("codigoRol")).equalsIgnoreCase("DESPACHO_FARMACIA")) {

                                                                Log.e("JSON ", " es " + json.getString("codigoRol"));

                                                                val = 1;


                                                            }

                                                        }

                                                        progressDialog.dismiss();

                                                        if (val == 1) {
                                                            Intent intent = new Intent(Pantalla_Principal.this, Pantalla_Principal.class);

                                                            SharedPreferences preferences = getSharedPreferences("credenciales", Context.MODE_PRIVATE);
                                                            SharedPreferences.Editor editor = preferences.edit();
                                                            editor.putString("user", Usuario);
                                                            editor.putString("pass", Pass);
                                                            editor.putString("nombreS", NombreSucursal);
                                                            editor.putString("nombre", Nombre);
                                                            editor.putInt("codSucursal", CodSucurusal);
                                                            editor.putString("Token", Token);

                                                            Log.e("TOKEN ENVIADO", "----> " + Token);

                                                            editor.commit();


                                                            progressDialog.dismiss();
                                                            dialogM.dismiss();
                                                            startActivity(intent);
                                                            finish();
                                                        } else {
                                                            progressDialog.dismiss();
                                                            MensajeError("Rol DESPACHO_FARMACIA no encontrado");
                                                        }

                                                    }

                                                    }catch(Exception e){
                                                        progressDialog.dismiss();
                                                        e.printStackTrace();
                                                        Log.e("ERROR", "---> " + e);
                                                        MensajeErrorServicio((IOException) e);
                                                    }

                                            }
                                        });


                                    }
                                });


                            }else if(jsonSucursales.getInt("codigo") == 30){
                                progressDialog.dismiss();
                                MensajeError(jsonSucursales.getString("mensaje"));
                            }






                    }catch (Exception e){
                        progressDialog.dismiss();
                        e.printStackTrace();
                        Log.e("ERROR","----> " +e);
                        MensajeErrorServicio(e);
                    }

                    Looper.loop();

                }
            });
        }catch (Exception e){
            progressDialog.dismiss();
            e.printStackTrace();
            Log.e("ERROR","---> " +e);
            MensajeErrorServicio((IOException) e);
        }
        Looper.loop();
    }

    @Override
    public void onBackPressed() {


        if (backPressedTime + 2000 > System.currentTimeMillis()) {


            Preferencias.savePreferenciaBoolean(Pantalla_Principal.this, false, "estado.buton.sesion");

            Intent intent = new Intent(Pantalla_Principal.this, Pantalla_Login.class);
            startActivity(intent);
            finish();
            super.onBackPressed();
            return;

        } else {

            Toast.makeText(getBaseContext(), "VUELVA A PULSAR PARA CERRAR SESIÓN", Toast.LENGTH_SHORT).show();

        }

        backPressedTime = System.currentTimeMillis();

    }

    //EL PROCEDIMIENTO QUE SE LLEVA ACABO CUANDO SE ALCANZA LA HORA
    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public void Ejecutar() {
        time time = new time();
        time.execute();
        Preferencias.savePreferenciaBoolean(Pantalla_Principal.this, false, "estado.buton.sesion");
        Intent intent = new Intent(Pantalla_Principal.this, SplashScreen.class);
        startActivity(intent);
        finish();
    }

    //INDICA EL TIEMPO DE SESION MAXIMO
    public void ExpiraSesion() throws InterruptedException {
        //1 HORA
        Thread.sleep(86400000);
    }


    @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
    public class time extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            //SE INDICA QUE CADA UNA HORA SE REPITA EL PROCESO
            for (int i = 1; i == 1; i++) {

                try {
                    ExpiraSesion();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e("ERROR", "---->" + e);
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

                        RealizaPicking(Id);


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


                AbreLector();


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

            OkHttpClient cliente = client.newBuilder()

                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(15, TimeUnit.SECONDS)
                    .writeTimeout(15, TimeUnit.SECONDS)
                    .build();


            JSONObject postData = new JSONObject();


            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody postBody = RequestBody.create(JSON, postData.toString());


            Request post = new Request.Builder()
                    .url("https://servicioscajas.veris.com.ec/PhantomCajasWS/api/farmaciaDomicilio/actualizarPickingTransaccion?argNumeroTransaccion=" + IdSoliticitud + "&argCodUsuario=" + Usuario + "&argCodSucursal=" + CodigoSucursal)

                    .post(postBody)

                    .addHeader("Authorization", "Bearer " + Token)

                    .build();
            Log.e("POSTBODY", "--------> " + postBody);
            Log.e("POST", "--------> " + post);
            Log.e("POST", "--------> " + Token);


            cliente.newCall(post).enqueue(new Callback() {


                @Override
                public void onFailure(Call call, IOException e) {

                    progressDialog.dismiss();
                    MensajeError("Tiempo de Espera Excedido, Vuelva a Intertarlo");
                    Log.e("MENSAJE", "-----> " + e);

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    ResponseBody responseBody = response.body();
                    try {

                        if (!response.isSuccessful()) {

                            JSONObject jsonObject = new JSONObject(responseBody.string());

                            if (jsonObject.getString("mensaje").equalsIgnoreCase("Token incorrecto o expirado")) {

                                MensajeExpiracion("Tiempo limite de Sesion Excedido, Reinicie la Aplicacion");

                            }


                            throw new IOException("Error Inesperado " + response);
                        }


                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        Log.e("RESPONSE BODY", "--->" + responseBody);


                        if (jsonObject.getString("mensaje").equalsIgnoreCase("OK")) {


                            MensajeExito();

                        } else if (jsonObject.getString("mensaje").equalsIgnoreCase("No existe el codigo de solicitud o numero de transaccion. \nMensaje generado desde la aplicacion >>. MGM_K_ORD_SERV_FARMACIA.MGM_UPT_PIKING_TRANS")) {

                            MensajeError("El codigo ingresado no existe");

                        } else if (jsonObject.getString("mensaje").equalsIgnoreCase("Ya se realizo picking a esta solicitud.")) {

                            MensajeError("Ya se realizo picking a esta solicitud.");


                        } else if (jsonObject.getString("mensaje").equalsIgnoreCase("Esta solicitud ya fue asignada a una guia de despacho.")) {

                            MensajeError("Esta solicitud ya fue asignada a una guia de despacho.");

                        } else if (jsonObject.getString("mensaje").equalsIgnoreCase("Token incorrecto o expirado")) {

                            MensajeExpiracion("Tiempo limite de Sesion Excedido, Reinicie la Aplicacion");
                        }


                    } catch (Exception e) {
                        Log.e("MENSAJE", "ERROR ----> " + e);


                    }
                }
            });

        }
    }

    public void AbreLector() {

        IntentIntegrator integrator = new IntentIntegrator(Pantalla_Principal.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Escanea El Codigo Qr");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(true);
        integrator.setCaptureActivity(LectorPortrait.class);
        integrator.setBarcodeImageEnabled(false);
        integrator.setOrientationLocked(false);
        integrator.initiateScan();

    }




    public void RealizaPicking(EditText Id){
        try{
        String Codigo = Id.getText().toString().trim();

        if (Codigo.isEmpty()) {

            Toast.makeText(Pantalla_Principal.this, "INGRESE UN NUMERO DE SOLICITUD PARA CONTINUAR", Toast.LENGTH_LONG).show();
        } else {

            progressDialog = GenericUtil.barraCargando(Pantalla_Principal.this, "Realizando Picking...");



                Log.e("Usuario", "RETORNADO ---> " + Usuario);
                Log.e("Codigo es --> ", "CODIGO : " + Id.getText().toString().trim());
                Log.e("Token", "OBTENIDO ---> " + Token);


                OkHttpClient cliente = client.newBuilder()

                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(15, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .build();

                JSONObject postData = new JSONObject();

            final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
            RequestBody postBody = RequestBody.create(JSON, postData.toString());


            Request post = new Request.Builder()
                    .url("https://servicioscajas.veris.com.ec/PhantomCajasWS/api/farmaciaDomicilio/actualizarPickingTransaccion?argNumeroTransaccion=" + Id.getText().toString().trim() + "&argCodUsuario=" + Usuario + "&argCodSucursal=" + CodigoSucursal)
                    .post(postBody)
                    .addHeader("Authorization", "Bearer " + Token)
                    .build();
            Log.e("POSTBODY", "-------->" + postBody);
            Log.e("POST", "-------->" + post);

            cliente.newCall(post).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                    progressDialog.dismiss();
                    MensajeError("Tiempo de Espera Excedido, Vuelva a Intertarlo");
                    Log.e("MENSAJE", "-----> " + e);

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    ResponseBody responseBody = response.body();

                    try {

                        if (!response.isSuccessful()) {

                            JSONObject jsonObject = new JSONObject(responseBody.string());

                            if (jsonObject.getString("mensaje").equalsIgnoreCase("Token incorrecto o expirado")) {

                                MensajeExpiracion("Tiempo limite de Sesion Excedido, Reinicie la Aplicacion");

                            }


                            throw new IOException("Error Inesperado " + response);
                        }


                        JSONObject jsonObject = new JSONObject(responseBody.string());
                        Log.e("RESPONSE BODY", "--->" + responseBody);


                        if (jsonObject.getString("mensaje").equalsIgnoreCase("OK")) {


                            MensajeExito();

                        } else if (jsonObject.getString("mensaje").equalsIgnoreCase("No existe el codigo de solicitud o numero de transaccion. \nMensaje generado desde la aplicacion >>. MGM_K_ORD_SERV_FARMACIA.MGM_UPT_PIKING_TRANS")) {

                            progressDialog.dismiss();
                            MensajeError("El codigo ingresado no existe");

                        } else if (jsonObject.getString("mensaje").equalsIgnoreCase("Ya se realizo picking a esta solicitud.")) {

                            progressDialog.dismiss();
                            MensajeError("Ya se realizo picking a esta solicitud.");


                        } else if (jsonObject.getString("mensaje").equalsIgnoreCase("Esta solicitud ya fue asignada a una guia de despacho.")) {

                            progressDialog.dismiss();
                            MensajeError("Esta solicitud ya fue asignada a una guia de despacho.");

                        } else if (jsonObject.getString("mensaje").equalsIgnoreCase("Token incorrecto o expirado")) {

                            progressDialog.dismiss();
                            MensajeExpiracion("Tiempo limite de Sesion Excedido, Reinicie la Aplicacion");
                        }


                    } catch (Exception e) {
                        progressDialog.dismiss();
                        Log.e("MENSAJE", "ERROR ----> " + e);
                        e.printStackTrace();
                        MensajeErrorServicio((IOException) e);


                    }


                }
            });


            }
        }catch (Exception e) {
            progressDialog.dismiss();
            Log.e("ERROR","---> " +e);
            e.printStackTrace();
            MensajeErrorServicio((IOException) e);
          }
        }




    public void MensajeExito() {
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

    public void MensajeError(String Mensaje) {
        Looper.prepare();

        AlertDialog.Builder builder = new AlertDialog.Builder(Pantalla_Principal.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogo_error, null);

        builder.setView(view);


        final AlertDialog dialogE = builder.create();
        dialogE.show();
        dialogE.setCancelable(false);

        TextView txt = view.findViewById(R.id.text_error);
        txt.setText(Mensaje);

        Button Aceptar = view.findViewById(R.id.btn_acept);
        Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogE.dismiss();
            }


        });
        Looper.loop();


    }


    public void MensajeErrorServicio(Exception e) {


        AlertDialog.Builder builder = new AlertDialog.Builder(Pantalla_Principal.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogo_error, null);

        builder.setView(view);

        final AlertDialog dialogE = builder.create();

        dialogE.show();

        dialogE.setCancelable(false);

        TextView txt = view.findViewById(R.id.text_error);
        txt.setText("Error al Ejecutar el Servicio Web " + e);

        Button Aceptar = view.findViewById(R.id.btn_acept);
        Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogE.dismiss();
            }


        });



    }




    public void MensajeExpiracion(String Mensaje) {
        Looper.prepare();

        AlertDialog.Builder builder = new AlertDialog.Builder(Pantalla_Principal.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogo_error, null);

        builder.setView(view);


        final AlertDialog dialogE = builder.create();
        dialogE.show();
        dialogE.setCancelable(false);

        TextView txt = view.findViewById(R.id.text_error);
        txt.setText(Mensaje);

        Button Aceptar = view.findViewById(R.id.btn_acept);
        Aceptar.setText("Reiniciar Aplicacion");
        Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogE.dismiss();
                Preferencias.savePreferenciaBoolean(Pantalla_Principal.this, false, "estado.buton.sesion");
                Intent intent = new Intent(Pantalla_Principal.this, Pantalla_Login.class);
                startActivity(intent);
                finish();
            }


        });
        Looper.loop();


    }


}





