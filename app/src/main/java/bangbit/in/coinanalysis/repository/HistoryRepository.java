package bangbit.in.coinanalysis.repository;

import android.util.Log;

import java.util.ArrayList;

import bangbit.in.coinanalysis.NetworkCall.ApiInterfaceHistory;
import bangbit.in.coinanalysis.NetworkCall.ApiService;
import bangbit.in.coinanalysis.pojo.Exchange;
import bangbit.in.coinanalysis.pojo.ExchangeData;
import bangbit.in.coinanalysis.pojo.HistoricalData;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nagarajan on 3/22/2018.
 */

public class HistoryRepository {
    public String TAG = HistoryRepository.class.getSimpleName();

    //    private HashMap<String, String> coinMarketcapCryptoCompareHashMap= Util.getCoinMarketcapCryptoCompareHashMap();
    public void getHistoricalData(String symbol, String currency, int limit, final HistoricalDataCallBack historicalDataCallBack) {
//        if (coinMarketcapCryptoCompareHashMap.get(symbol)!=null){
//            symbol=coinMarketcapCryptoCompareHashMap.get(symbol);
//        }
        ApiInterfaceHistory apiInterfaceHistory = ApiService.getAPIInterfaceHistory();
        apiInterfaceHistory.getHistoricalaData(symbol, currency, limit, "CoinAnalysis").enqueue(new Callback<HistoricalData>() {


            @Override
            public void onResponse(Call<HistoricalData> call, Response<HistoricalData> response) {
                HistoricalData historicalData = null;
                if (response.body() != null) {
                    historicalData = response.body();
                    historicalDataCallBack.onRetrieveHistoricalData(historicalData, true);
                } else {
                    historicalDataCallBack.onRetrieveHistoricalData(null, false);
                }
            }

            @Override
            public void onFailure(Call<HistoricalData> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                historicalDataCallBack.onRetrieveHistoricalData(null, false);
            }
        });

    }

    public void getHistoricalDataByHour(String symbol, String currency, int limit, final HistoricalDataCallBack historicalDataCallBack) {
//        if (coinMarketcapCryptoCompareHashMap.get(symbol)!=null){
//            symbol=coinMarketcapCryptoCompareHashMap.get(symbol);
//        }
        ApiInterfaceHistory apiInterfaceHistory = ApiService.getAPIInterfaceHistory();
        apiInterfaceHistory.getHistoricalaDataByHour(symbol, currency, limit, "CoinAnalysis").enqueue(new Callback<HistoricalData>() {

            @Override
            public void onResponse(Call<HistoricalData> call, Response<HistoricalData> response) {
                HistoricalData historicalData = null;
                if (response.body() != null) {
                    historicalData = response.body();
                    historicalDataCallBack.onRetrieveHistoricalData(historicalData, true);
                } else {
                    historicalDataCallBack.onRetrieveHistoricalData(null, false);
                }
            }

            @Override
            public void onFailure(Call<HistoricalData> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                historicalDataCallBack.onRetrieveHistoricalData(null, false);
            }
        });

    }

    public void getExchangeData(String symbol, String currency, final ExchangeDataCallBack exchangeDataCallBack) {
//        if (coinMarketcapCryptoCompareHashMap.get(symbol)!=null){
//            symbol=coinMarketcapCryptoCompareHashMap.get(symbol);
//        }
        ApiInterfaceHistory apiInterfaceHistory = ApiService.getAPIInterfaceHistory();
        apiInterfaceHistory.getExchangeData(symbol, currency, 100, "CoinAnalysis").enqueue(new Callback<ExchangeData>() {
            @Override
            public void onResponse(Call<ExchangeData> call, Response<ExchangeData> response) {
                ExchangeData exchangeData = null;
                boolean isDataFound = false;
                if (response.body() != null) {
                    exchangeData = response.body();
                    isDataFound = true;
                }
                if (exchangeData != null) {
                    ArrayList<Exchange> exchanges = exchangeData.getData().getExchanges();
                    exchangeDataCallBack.onRetrieveExchangeData(exchanges, true, isDataFound);
                } else {
                    exchangeDataCallBack.onRetrieveExchangeData(null, false, false);
                }

            }

            @Override
            public void onFailure(Call<ExchangeData> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                exchangeDataCallBack.onRetrieveExchangeData(null, false, false);

            }
        });

    }

    public void getHistoricalData(final String symbol, String currency, int limit, final HistoricalDataCallBackWithName historicalDataCallBack) {
//        String newSymbol=symbol;
//        if (coinMarketcapCryptoCompareHashMap.get(symbol)!=null){
//            newSymbol=coinMarketcapCryptoCompareHashMap.get(symbol);
//        }
        ApiInterfaceHistory apiInterfaceHistory = ApiService.getAPIInterfaceHistory();
        apiInterfaceHistory.getHistoricalaData(symbol, currency, limit, "CoinAnalysis").enqueue(new Callback<HistoricalData>() {


            @Override
            public void onResponse(Call<HistoricalData> call, Response<HistoricalData> response) {
                HistoricalData historicalData = null;
                if (response.body() != null) {
                    historicalData = response.body();
                    if (historicalData.getData()!=null){
                        historicalDataCallBack.onRetrieveHistoricalData(historicalData, true, symbol);
                    }else {
                        historicalDataCallBack.onRetrieveHistoricalData(null, false, symbol);
                    }
                } else {
                    historicalDataCallBack.onRetrieveHistoricalData(null, false, symbol);
                }
            }

            @Override
            public void onFailure(Call<HistoricalData> call, Throwable t) {
                Log.d(TAG, "onFailure: " + t.getMessage());
                historicalDataCallBack.onRetrieveHistoricalData(null, false, symbol);
            }
        });

    }

    public interface HistoricalDataCallBackWithName {
        void onRetrieveHistoricalData(HistoricalData historicalData, boolean isSuccess, String name);
    }

    public interface HistoricalDataCallBack {
        void onRetrieveHistoricalData(HistoricalData historicalData, boolean isSuccess);
    }

    public interface ExchangeDataCallBack {
        void onRetrieveExchangeData(ArrayList<Exchange> exchanges, boolean isSuccess, boolean isDataFound);
    }
}

