package edmt.dev.verisdespachosapp.ApiS;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;



public class GenericUtil {

    public static ProgressDialog barraCargando(Context context, String mensaje){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        //progressDialog.setTitle(mensaje);
        progressDialog.setMessage(mensaje);
        progressDialog.show();
        return progressDialog;
    }

    public static void mostrarMensajeError(Context context, String strMensaje, String strTitulo){
        new AlertDialog.Builder(context)
                //.setIcon(R.mipmap.ic_exit_to_app_black_36dp)
                .setTitle("Acceso Denegaddo, Usted es un Cliente")
                .setMessage(strMensaje)
                .setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }


    public static void mostrarMensaje(Context context, String strMensaje, String strTitulo){
        new AlertDialog.Builder(context)
                //.setIcon(R.mipmap.ic_exit_to_app_black_36dp)
                .setTitle("strTitulo")
                .setMessage(strMensaje)
                .setPositiveButton("Aceptar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).show();
    }


}
