package edmt.dev.verisdespachosapp.PickUp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

import edmt.dev.verisdespachosapp.Atributos.PickUp;
import edmt.dev.verisdespachosapp.R;

import static edmt.dev.verisdespachosapp.ApiS.AppVerisDespachos.getContext;


public class AdapterPickUp
        extends RecyclerView.Adapter<AdapterPickUp.HolderAdapterPickUp>
        implements View.OnClickListener{
    private List<PickUp> solicitudAdaptarPickUp;
    private Context context;

    private View.OnClickListener listener;

    public AdapterPickUp(List<PickUp> atributosDistribuidor, Context context){
        this.solicitudAdaptarPickUp = atributosDistribuidor;
        this.context = context;
    }

    public AdapterPickUp.HolderAdapterPickUp onCreateViewHolder(ViewGroup parent, int viewType){
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view_pickup,parent,false);
        v.setOnClickListener(this);
        return new AdapterPickUp.HolderAdapterPickUp(v);
    }

    @Override
    public void onBindViewHolder(AdapterPickUp.HolderAdapterPickUp holder, int position) {
        holder.Cedula.setText(solicitudAdaptarPickUp.get(position).getCedula());
        holder.Nombres.setText(solicitudAdaptarPickUp.get(position).getNombre());
        holder.Apellidos.setText(solicitudAdaptarPickUp.get(position).getApellidos());
        holder.FechaEmision.setText(solicitudAdaptarPickUp.get(position).getFechaEmision());
        holder.Sucursal.setText(solicitudAdaptarPickUp.get(position).getSucursal());
        holder.NTransaccion.setText(solicitudAdaptarPickUp.get(position).getNTransaccion());
    }

    @Override
    public int getItemCount() {
        return solicitudAdaptarPickUp.size();
    }


    public void setOnClickListener(View.OnClickListener listener){
        this.listener = listener;
    }
    @Override
    public void onClick(View v) {
        if (listener!=null){
            listener.onClick(v);
        }
    }

    static class HolderAdapterPickUp extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener {
        TextView Cedula;
        TextView Nombres;
        TextView Apellidos;
        TextView FechaEmision;
        TextView Sucursal;
        TextView NTransaccion;

        public HolderAdapterPickUp(View itemView){
            super(itemView);
            Cedula = (TextView)itemView.findViewById(R.id.idCedula);
            Nombres = (TextView)itemView.findViewById(R.id.idNombres);
            Apellidos = (TextView)itemView.findViewById(R.id.idApellidos);
            FechaEmision = (TextView)itemView.findViewById(R.id.idFecha);
            Sucursal = (TextView)itemView.findViewById(R.id.idSucursal);
            NTransaccion = (TextView)itemView.findViewById(R.id.idSolicitud);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {

            MenuItem Lector_QR = menu.add(Menu.NONE,2,2,"LECTOR QR");
            MenuItem Id_Solicitud = menu.add(Menu.NONE,3,3,"ID DE SOLICITUD");


            Lector_QR.setOnMenuItemClickListener(this);
            Id_Solicitud.setOnMenuItemClickListener(this);

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()){

                case 2:

                    Toast.makeText(getContext(), "SE ABRE LECTOR", Toast.LENGTH_SHORT).show();

                    break;

                case 3:

                    Toast.makeText(getContext(), "INGRESAR MANUALMENTE EL ID DE SOLICITUD", Toast.LENGTH_SHORT).show();

                    break;

            }
            return false;
        }
    }



}
