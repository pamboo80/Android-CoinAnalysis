package bangbit.in.coinanalysis.NetworkCall;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

/**
 * Created by Nagarajan on 3/9/2018.
 */

public interface LambdaApiInterface {

    @POST("test")
    Call<String> getResponse(@Header("Content-type") String contentType,
                             @Header("x-api-key") String authorization,
                             @Body String body);

}
