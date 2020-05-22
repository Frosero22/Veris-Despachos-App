package edmt.dev.verisdespachosapp.ApiS;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApisVeris {





    @POST("api/farmaciaDomicilio/actualizarPickingTransaccion")
    Call<ResponseBody> EstadoPicking (@Query("argNumeroTransaccion") int IdTransaccion, @Query("argCodUsuario") String Usuario, @Query("argCodSucursal") int CodSucursal);

    @GET("/api/farmaciaDomicilio/rolesPorSucursalUsuario")
    Call<ResponseBody> ObtenerRoles(@Query("argCodEmpresa") String CodEmpresa ,@Query("argCodSucursal") String CodSucursal,@Query("argUsuario") String User);

    @POST("/api/farmaciaDomicilio/loginUser")
    Call<ResponseBody> Login(@Query("user") String Usuario, @Query("pass") String Contraseña);

















}
