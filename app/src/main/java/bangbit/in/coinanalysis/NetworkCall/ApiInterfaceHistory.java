package bangbit.in.coinanalysis.NetworkCall;

import bangbit.in.coinanalysis.pojo.ExchangeData;
import bangbit.in.coinanalysis.pojo.HistoricalData;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Nagarajan on 3/22/2018.
 */

public interface ApiInterfaceHistory {
    @GET("histoday")
    Call<HistoricalData> getHistoricalaData(@Query("fsym") String coinSymbol,
                                            @Query("tsym") String currency,
                                            @Query("limit") int limit,
                                            @Query("extraParams") String appName);

    @GET("histohour")
    Call<HistoricalData> getHistoricalaDataByHour(@Query("fsym") String coinSymbol,
                                                  @Query("tsym") String currency,
                                                  @Query("limit") int limit,
                                                  @Query("extraParams") String appName);

    @GET("top/exchanges/full")
    Call<ExchangeData> getExchangeData(@Query("fsym") String coinSymbol,
                                       @Query("tsym") String currency,
                                       @Query("limit") int limit,
                                       @Query("extraParams") String appName);
}
