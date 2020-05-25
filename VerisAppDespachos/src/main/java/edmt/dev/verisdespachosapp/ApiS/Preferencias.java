package edmt.dev.verisdespachosapp.ApiS;

import android.content.Context;
import android.content.SharedPreferences;

public class Preferencias {

    public static final String STRING_PREFERENCIAS = "PesoApp";
    public static final String PREFERENCIA_USUARIO_LOGIN="usuario.login";


    public static void savePreferenciaBoolean(Context c, boolean b, String key){
        SharedPreferences preferencias = c.getSharedPreferences(STRING_PREFERENCIAS,c.MODE_PRIVATE);
        preferencias.edit().putBoolean(key,b).apply();
    }

    public static void savePreferenciasString(Context c, String b, String key){
        SharedPreferences preferencias = c.getSharedPreferences(STRING_PREFERENCIAS,c.MODE_PRIVATE);
        preferencias.edit().putString(key,b).apply();
    }

    public static boolean obtenerPreferenciaBoolean(Context c, String key){
        SharedPreferences preferencia = c.getSharedPreferences(STRING_PREFERENCIAS,c.MODE_PRIVATE);
        return preferencia.getBoolean(key,false);

    }

    public static String obtenerPreferenciaString(Context c, String key){
        SharedPreferences preferencias = c.getSharedPreferences(STRING_PREFERENCIAS,c.MODE_PRIVATE);
        return preferencias.getString(key,"");
    }
}
