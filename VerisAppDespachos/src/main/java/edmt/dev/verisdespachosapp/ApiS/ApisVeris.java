package edmt.dev.verisdespachosapp.ApiS;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApisVeris {





    @POST("api/farmaciaDomicilio/actualizarPickingTransaccion")
    Call<ResponseBody> EstadoPicking (@Query("argNumeroTransaccion") String IdTransaccion, @Query("argCodUsuario") String Usuario);














}
