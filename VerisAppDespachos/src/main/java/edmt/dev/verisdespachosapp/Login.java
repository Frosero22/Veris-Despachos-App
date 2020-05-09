package edmt.dev.verisdespachosapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.JsonObject;

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
private static ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        LoginD = findViewById(R.id.btn_login);
        LoginD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecuperaToken();

              //  Intent intent = new Intent(Login.this,Pantalla_Principal.class);
                //startActivity(intent);
            }
        });
    }



    public void RecuperaToken(){

        OkHttpClient client = new OkHttpClient();
        JsonObject postData = new JsonObject();
        postData.addProperty("user","wsapppaperless");
        postData.addProperty("pass","CAS5789b86Mdr5Pap3rl3$$");


        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody postBody = RequestBody.create(JSON, postData.toString());
        Request post = new Request.Builder()
                .url("http://52.7.160.244:8118/Verisrest/v1/paperless/portal/web/login")
                .post(postBody)
                .addHeader("Authorization", "Basic  d3NhcHBwYXBlcmxlc3M6Q0FTNTc4OWI4Nk1kcjVQYXAzcmwzJCQ=" )
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

                    Log.i("data", responseBody.string());
                    Log.e("Ok","Token Generado");
                } catch (Exception e) {

                    e.printStackTrace();
                    Log.e("Error","Error--->"+e);
                }
            }
        });

    }

}
