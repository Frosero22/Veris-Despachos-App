package edmt.dev.verisdespachosapp.Atributos;

public class PickUp {

    private String Nombre;
    private String Apellidos;
    private String Cedula;
    private String FechaEmision;
    private int NTransaccion;
    private String Sucursal;

    public PickUp(String nombre, String apellidos, String cedula, String fechaEmision, int NTransaccion, String sucursal) {
        Nombre = nombre;
        Apellidos = apellidos;
        Cedula = cedula;
        FechaEmision = fechaEmision;
        this.NTransaccion = NTransaccion;
        Sucursal = sucursal;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getApellidos() {
        return Apellidos;
    }

    public void setApellidos(String apellidos) {
        Apellidos = apellidos;
    }

    public String getCedula() {
        return Cedula;
    }

    public void setCedula(String cedula) {
        Cedula = cedula;
    }

    public String getFechaEmision() {
        return FechaEmision;
    }

    public void setFechaEmision(String fechaEmision) {
        FechaEmision = fechaEmision;
    }

    public int getNTransaccion() {
        return NTransaccion;
    }

    public void setNTransaccion(int NTransaccion) {
        this.NTransaccion = NTransaccion;
    }

    public String getSucursal() {
        return Sucursal;
    }

    public void setSucursal(String sucursal) {
        Sucursal = sucursal;
    }

    public PickUp() {
    }
}
