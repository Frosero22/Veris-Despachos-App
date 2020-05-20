package edmt.dev.verisdespachosapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import edmt.dev.verisdespachosapp.ApiS.ApisVeris;
import edmt.dev.verisdespachosapp.ApiS.GenericUtil;
import edmt.dev.verisdespachosapp.ApiS.Sucursales;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.http.GET;


public class Login extends AppCompatActivity {
Button LoginD;
CheckBox VerContraseña;
String Token;
    String Nombre;
EditText User;
String Sucu;
EditText Pass;
 Integer val = 0;
private static ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         User = findViewById(R.id.edit_user);
         Pass = findViewById(R.id.edit_pass);
        Bundle bundle = this.getIntent().getExtras();
         Token = bundle.getString("Token","----");
         Log.e("TOKEN","RECOGIDO ----> "+Token);

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

                        try {

                            LoginConToken();

                        } catch (JSONException e) {

                            e.printStackTrace();
                        }

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
 /*   public String RecuperaToken(){
progressDialog = GenericUtil.barraCargando(Login.this,"Espere un Momento...");
        OkHttpClient client = new OkHttpClient();
        JsonObject postData = new JsonObject();
        postData.addProperty("user","wsformularioepi1");
        postData.addProperty("pass","CAS5789b86Mdr5F0rmular103pi1*");


        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody postBody = RequestBody.create(JSON, postData.toString());
        Request post = new Request.Builder()
                .url("http://52.7.160.244:8223/Verisrest/v1/formularioepi1/login")
                .post(postBody)
                .addHeader("Authorization", "Basic  d3Nmb3JtdWxhcmlvZXBpMTpDQVM1Nzg5Yjg2TWRyNUYwcm11bGFyMTAzcGkxKg==" )
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
                 LoginConToken(Token);

                    Log.e("Ok","Token Generado");
                } catch (Exception e) {

                    e.printStackTrace();
                    Log.e("Error","Error--->"+e);
                }
            }
        });

            return Token;
    }
*/

    public void LoginConToken() throws JSONException {

    progressDialog = GenericUtil.barraCargando(Login.this,"Ingresando...");



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
                .url("http://52.7.160.244:8118/PhantomCajasWS/api/farmaciaDomicilio/loginUser")
                .post(postBody)

                .addHeader("Authorization", "Bearer "+Token)
                .build();

        client.newCall(post).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {
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

                        Looper.prepare();
                        AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                        LayoutInflater inflater = getLayoutInflater();
                        View view = inflater.inflate(R.layout.sucursales, null);
                        builder.setView(view);
                        final AlertDialog dialogM = builder.create();
                        dialogM.show();
                        dialogM.setCancelable(false);
                        ListView ListaS = view.findViewById(R.id.lista_sucursales);

                        ListaS.setAdapter(adapterSucursales);

                        ListaS.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                                int CodEmpresa = lista.get(position).getCodigoEmpresa();
                                final int CodSucurusal =  lista.get(position).getCodigoSucursal();
                                final String NombreSucursal = lista.get(position).getNombreSucursal();

                                Log.e("CODIGO","------>"+CodEmpresa);
                                Log.e("SUCURSAL","----->"+CodSucurusal);
                                Log.e("NOMBRE","----->"+NombreSucursal);


                                OkHttpClient client = new OkHttpClient().newBuilder()
                                        .build();


                                Request request = new Request.Builder()
                                        .url("http://52.7.160.244:8118/PhantomCajasWS/api/farmaciaDomicilio/rolesPorSucursalUsuario?argCodEmpresa="+CodEmpresa+"&argCodSucursal="+CodSucurusal+"&argUsuario="+User.getText().toString())
                                        .method("GET",null)
                                        .addHeader("Authorization", "Bearer "+Token)
                                        .build();



                                client.newCall(request).enqueue(new Callback() {


                                    @Override
                                    public void onFailure(Call call, IOException e) {

                                        Log.e("ERROR","----> "+call);
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {


                                        ResponseBody responseBody = response.body();

                                        Log.e("RESPONDE","----> " +response);
                                        Log.e("CALL","----> "+call);


                                        Log.e("RESPONSEBODY --> ","---> " +responseBody);


                                      try {

                                          JSONObject jsonObject = new JSONObject(responseBody.string());

                                          Log.e("JSONOBJECT ","--->" +jsonObject);

                                              if (jsonObject.getString("success").equalsIgnoreCase("OK")) {




                                              String listaRol = jsonObject.getString("lsUsuarioXRol");
                                              JSONArray jsonArray = new JSONArray(listaRol);



                                              for (int a = 0; a < jsonArray.length(); a++) {

                                                  JSONObject json = jsonArray.getJSONObject(a);

                                                  Log.e("ARRAYS","ENCONTRADOS " +json.getString("codigoRol"));

                                                  if (json.getString(("codigoRol")).equalsIgnoreCase("DESPACHO_FARMACIA")) {

                                                      Log.e("JSON ", " es " + json.getString("codigoRol"));

                                                      val = 1;



                                                      Intent intent = new Intent(Login.this,Pantalla_Principal.class);
                                                      intent.putExtra("User",User.getText().toString().trim());
                                                      intent.putExtra("CodSucursal",CodSucurusal);
                                                      intent.putExtra("NombreSucursal",NombreSucursal);
                                                      intent.putExtra("NombreUsuario",Nombre);
                                                      startActivity(intent);


                                                  } else {

                                                      Log.e("Acceso denegado", "No se encontro Rol Requerido");
                                                      progressDialog.dismiss();
                                                      Looper.prepare();

                                                      AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);
                                                      LayoutInflater inflater = getLayoutInflater();
                                                      View view = inflater.inflate(R.layout.dialogo_error,null);
                                                      builder.setView(view);
                                                      final AlertDialog dialog = builder.create();
                                                      dialog.show();
                                                      dialog.setCancelable(false);
                                                      TextView txt = view.findViewById(R.id.text_error);
                                                      txt.setText("No Se Encontraron Roles Necesarios");

                                                      Button Aceptar = view.findViewById(R.id.btn_acept);
                                                      Aceptar.setOnClickListener(new View.OnClickListener() {
                                                          @Override
                                                          public void onClick(View view) {
                                                              dialog.dismiss();
                                                          }


                                                      });
                                                      Looper.loop();

                                                  }



                                              }



                                          }else{


                                              progressDialog.dismiss();
                                              Looper.prepare();
                                              AlertDialog.Builder builder = new AlertDialog.Builder(Login.this);

                                              LayoutInflater inflater = getLayoutInflater();

                                              View view = inflater.inflate(R.layout.dialogo_error,null);

                                              builder.setView(view);

                                              final AlertDialog dialog = builder.create();
                                              dialog.show();
                                              dialog.setCancelable(false);
                                              TextView txt = view.findViewById(R.id.text_error);
                                              txt.setText("Usuario o Contraseña Invalido --> Crendeciales Invalidas");

                                              Button Aceptar = view.findViewById(R.id.btn_acept);
                                              Aceptar.setOnClickListener(new View.OnClickListener() {
                                                  @Override
                                                  public void onClick(View view) {
                                                      dialog.dismiss();
                                                  }


                                              });
                                              Looper.loop();


                                              Log.e("MENSAJE","-> Credencioanles invalidas sea Usuario contra");
                                          }


                                      }catch (Exception e){
                                          e.printStackTrace();
                                          Log.e("ERROR","-------> " +e);

                                      }

                                    }



                                });


                        }


                    });

                    }


                }catch (Exception e){
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
        txt.setText("Por favor Conectate a Internet");

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
        txt.setText("Ocurrio un Error, Reinicia la Aplicación para poder Continuar");

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





