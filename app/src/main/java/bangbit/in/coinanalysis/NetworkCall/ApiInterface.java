package bangbit.in.coinanalysis.NetworkCall;

import java.util.ArrayList;

import bangbit.in.coinanalysis.pojo.Coin;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Nagarajan on 12/7/2017.
 */

public interface ApiInterface {
  @GET("latest?limit=0")
  Call<ArrayList<Coin>> getCoinList();
  @GET("latest?limit=100&CMC_PRO_API_KEY=<API-KEY>") 
  Call<ArrayList<Coin>> get100CoinList();

  @GET("quotes/latest?CMC_PRO_API_KEY=<API-KEY>")
  Call<ResponseBody> getCoinDetail(@Query("id") String id, @Query("convert") String currency);

  @GET("listings/latest?sort=market_cap&convert=USD&CMC_PRO_API_KEY=<API-KEY>")
  Call<String> getAllCoin(@Query("start") int start,@Query("limit") int startend);
}
