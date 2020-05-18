package edmt.dev.verisdespachosapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.google.gson.JsonObject;

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


public class MainActivity extends AppCompatActivity {

    Handler handler;
    Runnable runnable;
    ImageView img;
    String Token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecuperaToken();
        img = findViewById(R.id.img);
         img.animate().alpha(4000).setDuration(0);

         handler = new Handler();
         handler.postDelayed(new Runnable() {
             @Override
             public void run() {
                 Intent dsp = new Intent(MainActivity.this,Login.class);
                 dsp.putExtra("Token",Token);
                 startActivity(dsp);
                 finish();
             }
         },4000);
    }



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


                    Log.e("Ok","Token Generado");
                } catch (Exception e) {

                    e.printStackTrace();
                    Log.e("Error","Error--->"+e);
                }
            }
        });

        return Token;
    }








}
