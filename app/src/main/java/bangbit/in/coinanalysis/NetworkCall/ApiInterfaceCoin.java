package bangbit.in.coinanalysis.NetworkCall;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by Nagarajan on 3/9/2018.
 */

public interface ApiInterfaceCoin {
    @GET("api/values")
    Call<String> getAllCoin();

    @GET("api/quotes")
    Call<String> getQuotes();
}
