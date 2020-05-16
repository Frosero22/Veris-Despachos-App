package edmt.dev.verisdespachosapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import edmt.dev.verisdespachosapp.ApiS.GenericUtil;
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
String Token;
EditText User;
EditText Pass;
 Integer val = 0;
private static ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
         User = findViewById(R.id.edit_user);
         Pass = findViewById(R.id.edit_pass);

        LoginD = findViewById(R.id.btn_login);
        LoginD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                    RecuperaToken();


            }
        });



    }

//TOKEN NECESARIO PARA TODOS LOS SERVICIOS RESTANTES
    public String RecuperaToken(){
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


    public void LoginConToken(String Token) throws JSONException {




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
                .url("http://52.7.160.244:8223/Verisrest/v1/formularioepi1/loginUser")
                .post(postBody)

                .addHeader("Authorization", "Bearer "+Token)
                .build();

        client.newCall(post).enqueue(new Callback() {


            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("Error","Error al ejecutarr servicio" +e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                try {
                    ResponseBody responseBody = response.body();
                    if (!response.isSuccessful()) {

                        throw new IOException("Error Inesperado " + response);
                    }

                    assert responseBody != null;
                    //Declaro el Jsonobjet para capturar el response
                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    // obtengo el primer dato dentro del objeto y condicion
                    if(jsonObject.getInt("codigo")==0) {

                        String listaRol = jsonObject.getString("lsUsuarioXRol");
                        JSONArray jsonArray = new JSONArray(listaRol);




                        for (int a = 0; a < jsonArray.length(); a++) {

                            JSONObject json = jsonArray.getJSONObject(a);


                            Log.e("ARRAYS","ENCONTRADOS " +json.getString("codigoRol"));


                            if (json.getString("codigoRol").equalsIgnoreCase("DESPACHO_FARMACIA")) {

                                    Log.e("JSON "," es " +json.getString("codigoRol"));

                                    val = 1;

                            }else{
                                progressDialog.dismiss();
                                Log.e("Acceso denegado","No se encontro Rol Requerido");

                            }

                        }

                        if(val == 1){
                            //ingresa
                            Log.e("Ok","Acceso Listo");

                            Intent intent = new Intent(Login.this,Pantalla_Principal.class);
                            intent.putExtra("User",User.getText().toString().trim());
                            startActivity(intent);
                            progressDialog.dismiss();
                        }else{
                            progressDialog.dismiss();
                            Log.e("Acceso Denegado","Credenciales Incorrectas" +jsonObject);

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

                    }else {
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



                } catch (Exception e) {
                    progressDialog.dismiss();

                    e.printStackTrace();
                    Log.e("Error","Error--->"+e);
                }
            }
            });

                val = 0;

            }
        }




