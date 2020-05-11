package edmt.dev.verisdespachosapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

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
    Boolean Validador = false;
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

                    JSONObject jsonObject = new JSONObject(responseBody.string());
                    String listaXrol = jsonObject.getString("lsUsuarioXRol");
                    JSONArray jsonArray = new JSONArray(listaXrol);

                        for(int a = 0; a < jsonArray.length(); a++){
                            JSONObject js = jsonArray.getJSONObject(a);
                            if(js.getString("codigoRol").equals("DESPACHO_FARMACIA")
                                    && js.getString("codigoUsuario").equals(User)){
                                    Validador = true;

                            }else{

                                Validador = false;
                            }
                        }

                            if(Validador = true){
                                Log.e("Ok","Acceso Listo");

                                Intent intent = new Intent(Login.this,Pantalla_Principal.class);
                                startActivity(intent);
                            }else{

                                Toast.makeText(Login.this, "No Contiene el Rol Correcto", Toast.LENGTH_SHORT).show();

                            }




                } catch (Exception e) {

                    e.printStackTrace();
                    Log.e("Error","Error--->"+e);
                }
            }
            });


            }
        }




