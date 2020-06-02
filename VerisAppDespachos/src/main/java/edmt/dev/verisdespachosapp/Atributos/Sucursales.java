package edmt.dev.verisdespachosapp.Atributos;

public class Sucursales {

private String nombreSucursal;
private String NombreUsuario;
private int codigoSucursal;
private int codigoEmpresa;



    public Sucursales() {
    }

    public Sucursales(String nombreSucursal,String nombreUsuario, int codigoSucursal, int codigoEmpresa) {
        this.nombreSucursal = nombreSucursal;
        this.codigoSucursal = codigoSucursal;
        this.codigoEmpresa = codigoEmpresa;
        this.NombreUsuario = nombreUsuario;
    }

    public String getNombreSucursal() {
        return this.nombreSucursal;
    }

    public void setNombreSucursal(String nombreSucursal) {
        this.nombreSucursal = nombreSucursal;
    }

    public int getCodigoSucursal() {
        return codigoSucursal;
    }

    public void setCodigoSucursal(int codigoSucursal) {
        this.codigoSucursal = codigoSucursal;
    }

    public int getCodigoEmpresa() {
        return codigoEmpresa;
    }

    public void setCodigoEmpresa(int codigoEmpresa) {
        this.codigoEmpresa = codigoEmpresa;
    }


    public String toString(){
        return nombreSucursal;
    }

    public String getNombreUsuario() {
        return NombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        NombreUsuario = nombreUsuario;
    }



}
