package edmt.dev.verisdespachosapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import edmt.dev.verisdespachosapp.ApiS.GenericUtil;
import edmt.dev.verisdespachosapp.ApiS.Preferencias;
import edmt.dev.verisdespachosapp.ApiS.Sucursales;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


    public class Login extends AppCompatActivity {
    Button LoginD;
    CheckBox VerContraseña;
    String Token;
        String Nombre;
    EditText User;
    EditText Pass;
     Integer val = 0;

        public static final String PREFERENCE_ESTADO_BUTTON_SESION = "estado.buton.sesion";

        private static ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        if(Preferencias.obtenerPreferenciaBoolean(this,PREFERENCE_ESTADO_BUTTON_SESION)){
            Intent i = new Intent(Login.this,Pantalla_Principal.class);
            startActivity(i);
            finish();
        }


        User = findViewById(R.id.edit_user);
        Pass = findViewById(R.id.edit_pass);

        LoginD = findViewById(R.id.btn_login);
        LoginD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String Usu = User.getText().toString().trim();
                String Contra = Pass.getText().toString().trim();

                ConnectivityManager con = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                assert con != null;
                NetworkInfo networkInfo = con.getActiveNetworkInfo();

                if (networkInfo != null && networkInfo.isConnected()) {

                    if (Usu.isEmpty()) {
                        User.setError("Campo Obligatorio");
                    } else if (Contra.isEmpty()) {
                        Pass.setError("Campo Obligatorio");
                    } else {

                        GeneraToken();

                    }

                }else{

                    MensajeErrorInternet();
                }
            }

        });

        VerContraseña = findViewById(R.id.ver_contraseña);
        VerContraseña.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){

                    Pass.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else{

                    Pass.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }

            }
        });



    }

//TOKEN NECESARIO PARA TODOS LOS SERVICIOS RESTANTES - INACTIVO -
    public String GeneraToken(){


    progressDialog = GenericUtil.barraCargando(Login.this,"Ingresando...");

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
            progressDialog.dismiss();
            MensajeErrorServico();
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
                LoginConToken(Token);

                Log.e("Ok","Token Generado");
            } catch (Exception e) {
                progressDialog.dismiss();
                MensajeErrorServico();
                e.printStackTrace();
                Log.e("Error","Error--->"+e);
            }
        }
    });

    return Token;
}


    public void LoginConToken(final String Token) throws JSONException {




          Log.e("Token Recogido --->","Token   " + Token);



        OkHttpClient client = new OkHttpClient();
        JSONObject postData = new JSONObject();


        postData.put("user",User.getText().toString().trim());
        postData.put("pass",Pass.getText().toString().trim());

        Log.e("Usuario","User es = " +User.getText().toString());
        Log.e("Pass","Pass es = " +Pass.getText().toString());


        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody postBody = RequestBody.create(JSON, postData.toString());
        Request post = new Request.Builder()
                .url("https://servicioscajas.veris.com.ec/PhantomCajasWS/api/farmaciaDomicilio/loginUser")
                .post(postBody)
                .addHeader("Authorization", "Bearer "+Token)

                .build();

        client.newCall(post).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {
                progressDialog.dismiss();
                MensajeErrorServico();
                Log.e("Error", "Error al ejecutarr servicio" + e);


            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {

                        progressDialog.dismiss();
                        MensajeErrorToken();

                        throw new IOException("Error Inesperado " + response);
                    }
                    final ArrayList<Sucursales> lista = new ArrayList<Sucursales>();


                    assert responseBody != null;
                    JSONObject jsonSucursales = new JSONObject(responseBody.string());

                    Log.e("DATOS","--->" +jsonSucursales);

                    if (jsonSucursales.getInt("codigo") == 0) {



                        String listaSucursales = jsonSucursales.getString("lsSucursales");
                        JSONArray jsonArray = new JSONArray(listaSucursales);


                        Log.e("LISTA","--->"+listaSucursales);

                        for(int sucursales = 0; sucursales < jsonArray.length(); sucursales++){

                            Sucursales e = new Sucursales();

                            e.setNombreSucursal(jsonArray.getJSONObject(sucursales).getString("nombreSucursal"));
                            e.setCodigoEmpresa(jsonArray.getJSONObject(sucursales).getInt("codigoEmpresa"));
                            e.setCodigoSucursal(jsonArray.getJSONObject(sucursales).getInt("codigoSucursal"));

                            lista.add(e);

                        }

                        String datosUsuario = jsonSucursales.getString("usuario");
                        JSONObject json = new JSONObject(datosUsuario);


                              Nombre = json.getString("nombreUsuario");
                                Log.e("USUARIO ","----->"+Nombre);







                        ArrayAdapter<Sucursales> adapterSucursales = new ArrayAdapter<Sucursales>(Login.this, android.R.layout.simple_dropdown_item_1line, lista);
                        progressDialog.dismiss();
                        Looper.prepare();
                        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
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
                                progressDialog = GenericUtil.barraCargando(Login.this,"Espere un momento...");

                                final int CodEmpresa = lista.get(position).getCodigoEmpresa();
                                final int CodSucurusal =  lista.get(position).getCodigoSucursal();
                                final String NombreSucursal = lista.get(position).getNombreSucursal();

                                Log.e("CODIGO","------>"+CodEmpresa);
                                Log.e("SUCURSAL","----->"+CodSucurusal);
                                Log.e("NOMBRE","----->"+NombreSucursal);



                                OkHttpClient client = new OkHttpClient().newBuilder()
                                        .build();


                                Request request = new Request.Builder()
                                        .url("https://servicioscajas.veris.com.ec/PhantomCajasWS/api/farmaciaDomicilio/rolesPorSucursalUsuario?argCodEmpresa="+CodEmpresa+"&argCodSucursal="+CodSucurusal+"&argUsuario="+User.getText().toString())
                                        .method("GET",null)
                                        .addHeader("Authorization", "Bearer "+Token)
                                        .build();



                                client.newCall(request).enqueue(new Callback() {


                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        MensajeErrorServico();
                                        Log.e("ERROR","----> "+call);
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {


                                        ResponseBody responseBody = response.body();




                                      try {

                                          JSONObject jsonObject = new JSONObject(responseBody.string());



                                              if (jsonObject.getString("success").equalsIgnoreCase("OK")) {




                                              String listaRol = jsonObject.getString("lsUsuarioXRol");
                                              JSONArray jsonArray = new JSONArray(listaRol);



                                              for (int a = 0; a < jsonArray.length(); a++) {

                                                  JSONObject json = jsonArray.getJSONObject(a);

                                                  Log.e("ARRAYS","ENCONTRADOS " +json.getString("codigoRol"));

                                                  if (json.getString(("codigoRol")).equalsIgnoreCase("DESPACHO_FARMACIA")) {

                                                      Log.e("JSON ", " es " + json.getString("codigoRol"));

                                                      val = 1;


                                                  }

                                              }

                                              if(val == 1){
                                                    progressDialog.dismiss();
                                                  Intent intent = new Intent(Login.this,Pantalla_Principal.class);

                                                  SharedPreferences preferences = getSharedPreferences("credenciales",Context.MODE_PRIVATE);

                                                  String user = User.getText().toString();
                                                  String pass = Pass.getText().toString();
                                                  SharedPreferences.Editor editor = preferences.edit();
                                                  editor.putString("user",user);
                                                  editor.putString("nombreS",NombreSucursal);
                                                  editor.putString("nombre",Nombre);
                                                  editor.putInt("codSucursal",CodSucurusal);
                                                  editor.putString("Token",Token);

                                                  Log.e("TOKEN ENVIADO","----> "+Token);

                                                  editor.commit();

                                                  Preferencias.savePreferenciaBoolean(Login.this,true,PREFERENCE_ESTADO_BUTTON_SESION);

                                                  startActivity(intent);
                                                  finish();

                                              }else{

                                                  Log.e("Acceso Denegado","Credenciales Incorrectas" +jsonObject);
                                                  progressDialog.dismiss();
                                                  MensajeErrorRoles();


                                              }



                                          }


                                      }catch (Exception e){
                                          e.printStackTrace();
                                          progressDialog.dismiss();
                                          MensajeErrorServico();
                                          Log.e("ERROR","-------> " +e);

                                      }

                                    }



                                });


                        }


                    });

                    }else{

                        progressDialog.dismiss();
                       MensajeErroLogin();

                    }


                }catch (Exception e){
                    progressDialog.dismiss();
                    MensajeErrorServico();
                    e.printStackTrace();

                }




                Looper.loop();

                val = 0;

            }



        });
    }





    public void MensajeErrorInternet(){

        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogo_error, null);

        builder.setView(view);


        final AlertDialog dialogM = builder.create();
        dialogM.show();
        dialogM.setCancelable(false);

        TextView txt = view.findViewById(R.id.text_error);
        txt.setText("Por favor Conéctate a Internet");

        Button Aceptar = view.findViewById(R.id.btn_acept);
        Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogM.dismiss();
            }


        });
        Looper.loop();




    }

    public void MensajeErrorToken(){
        Looper.prepare();
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogo_error, null);

        builder.setView(view);


        final AlertDialog dialogM = builder.create();
        dialogM.show();
        dialogM.setCancelable(false);

        TextView txt = view.findViewById(R.id.text_error);
        txt.setText("Ocurrió un Error, Reinicia la Aplicación para poder Continuar");

        Button Aceptar = view.findViewById(R.id.btn_acept);
        Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogM.dismiss();
            }


        });
        Looper.loop();




    }



    public void MensajeErrorServico(){
        Looper.prepare();
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogo_error, null);

        builder.setView(view);


        final AlertDialog dialogM = builder.create();
        dialogM.show();
        dialogM.setCancelable(false);

        TextView txt = view.findViewById(R.id.text_error);
        txt.setText("Error al Ejecutar el Servicio Web, Comuníquese con el Área de Sistemas");

        Button Aceptar = view.findViewById(R.id.btn_acept);
        Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogM.dismiss();
            }


        });
        Looper.loop();




    }


    public void MensajeErroLogin(){
        Looper.prepare();
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogo_error, null);

        builder.setView(view);


        final AlertDialog dialogM = builder.create();
        dialogM.show();
        dialogM.setCancelable(false);

        TextView txt = view.findViewById(R.id.text_error);
        txt.setText("Usuario o Contraseña Invalido --> Credenciales Invalidas");

        Button Aceptar = view.findViewById(R.id.btn_acept);
        Aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogM.dismiss();
            }


        });
        Looper.loop();




    }


    public void MensajeErrorRoles(){
        Looper.prepare();
        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialogo_error, null);

        builder.setView(view);


        final AlertDialog dialogM = builder.create();
        dialogM.show();
        dialogM.setCancelable(false);

        TextView txt = view.findViewById(R.id.text_error);
        txt.setText("No Se Encontraron Roles Necesarios");

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





