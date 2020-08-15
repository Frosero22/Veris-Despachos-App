package edmt.dev.verisdespachosapp.PickUp;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import edmt.dev.verisdespachosapp.Atributos.PickUp;
import edmt.dev.verisdespachosapp.R;

public class ListadoPickUp extends AppCompatActivity {


    private RecyclerView RvClientes;
    private List<PickUp> DetallePickUp;
    private List<PickUp> listAuxiliar;
    private AdapterPickUp adapter;
    private EditText buscar;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado_pick_up);

        buscar = findViewById(R.id.idbuscarempresac);
        DetallePickUp = new ArrayList<>();

        listAuxiliar = new ArrayList<>();

        RvClientes = findViewById(R.id.idrecycler);

        adapter = new AdapterPickUp(DetallePickUp,this);

        adapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




            }
        });
        LinearLayoutManager lm = new LinearLayoutManager(this);

        RvClientes.setLayoutManager(lm);
        RvClientes.setAdapter(adapter);
        buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int count, int after) {
                buscador(""+s);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }


    public void buscador(String texto){
        DetallePickUp.clear();
        for(int i=0;i<listAuxiliar.size();i++){
            if(listAuxiliar.get(i).getCedula().contains(texto)||listAuxiliar.get(i).getNombre().toLowerCase().contains(texto.toLowerCase())){
                DetallePickUp.add(listAuxiliar.get(i));
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void agregarTransaccion(String nombres, String apellidos,int transaccion, String cedula, String sucursal, String fechat){
        PickUp pickUp = new PickUp();
        pickUp.setCedula(cedula);
        pickUp.setNombre(nombres);
        pickUp.setApellidos(apellidos);
        pickUp.setSucursal(sucursal);
        pickUp.setFechaEmision(fechat);
        pickUp.setNTransaccion(transaccion);


        DetallePickUp.add(pickUp);
        listAuxiliar.add(pickUp);
        adapter.notifyDataSetChanged();
    }

    public void WebServideObtenerSolicitudes(){

    }

}
