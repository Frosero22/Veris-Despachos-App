package edmt.dev.verisdespachosapp.Atributos;

public class DetalleFactura {
    private String Cantidad;
    private String nombreProductos;
    private int total;

    public DetalleFactura(String cantidad, String nombreProductos, int total) {
        Cantidad = cantidad;
        this.nombreProductos = nombreProductos;
        this.total = total;
    }

    public DetalleFactura() {
    }

    public String getCantidad() {
        return Cantidad;
    }

    public void setCantidad(String cantidad) {
        Cantidad = cantidad;
    }

    public String getNombreProductos() {
        return nombreProductos;
    }

    public void setNombreProductos(String nombreProductos) {
        this.nombreProductos = nombreProductos;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
