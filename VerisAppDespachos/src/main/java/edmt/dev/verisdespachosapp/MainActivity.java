package edmt.dev.verisdespachosapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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
    String Token, TokenFarmacia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecuperaTokenLogin();
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



    public String RecuperaTokenLogin(){

        OkHttpClient client = new OkHttpClient();
        JsonObject postData = new JsonObject();
        postData.addProperty("user","wsphantomcajas");
        postData.addProperty("pass","CAS5789b86Mdr5Ph@nT0mC@j@$");


        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody postBody = RequestBody.create(JSON, postData.toString());
        Request post = new Request.Builder()
                .url("http://52.7.160.244:8118/PhantomCajasWS/api/authentications/login")
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
                    SharedPreferences preferences = getSharedPreferences("token",Context.MODE_PRIVATE);
                    String TOKEN = Token;
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("token",TOKEN);
                    editor.commit();

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
