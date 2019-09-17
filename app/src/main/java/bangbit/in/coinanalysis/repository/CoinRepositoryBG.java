package bangbit.in.coinanalysis.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import bangbit.in.coinanalysis.NetworkCall.ApiInterface;
import bangbit.in.coinanalysis.NetworkCall.ApiInterfaceCoin;
import bangbit.in.coinanalysis.NetworkCall.ApiService;
import bangbit.in.coinanalysis.PojoUtil;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.database.DatabaseHelper;
import bangbit.in.coinanalysis.pojo.Coin;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by vm on 05-05-2019.
 */

public class CoinRepositoryBG extends AsyncTask<CoinListCallback, Void, Integer> {

    private static final String TAG = CoinRepository.class.getSimpleName();
    DatabaseHelper databaseHelper;
    ApiInterface apiInterface = ApiService.getAPIInterface();
    ApiInterfaceCoin apiInterfaceCoin = ApiService.getAPIInterfaceCoin();
    private CoinListCallback _CoinListCallback=null;

    @Override
    protected Integer doInBackground(CoinListCallback... callback) {
        //Yet to code
        _CoinListCallback = callback[0];
        getRemainingCoinBackground(1, 2000, new ArrayList<Coin>(), _CoinListCallback);
        return 0;
    }
    @Override
    protected void onProgressUpdate(Void... progress) {
        //Yet to code
    }
    @Override
    protected void onPostExecute(Integer result) {
        //Yet to code

    }

    private void getRemainingCoinBackground(final int start, final int end, final ArrayList<Coin> coins, final CoinListCallback callback) {
            apiInterfaceCoin.getAllCoin().enqueue(new Callback<String>() {
            //apiInterface.getAllCoin(start, end).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                Log.d(TAG, "onResponse: updateCoinDetailsBG " + start + " " + end);
                if (response.body() != null) {
                    String responseString = response.body().toString();
                    try {
                        JSONObject jsonObj = new JSONObject(responseString);
                        final JSONArray jsonArray = (JSONArray) jsonObj.get("data");
                        PojoUtil.getCoinDetailPro(jsonArray, new PojoUtilCallback() {
                            @Override
                            public void onGetCoinDetailProCallback(ArrayList<Coin> coinArrayList) {
                                synchronized (CoinRepository.class) {
                                    coins.addAll(coinArrayList);
                                }
                                callback.coinCallback(coins, true);

                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {
                                        String data = Util.getCoinListAsString(coins);
                                        databaseHelper.updateCoin(data);
                                    }
                                };
                                if (coins!=null && coins.size()>0){
                                    new Thread(runnable).start();
                                }
                            }
                        });

                    } catch (JSONException e) {

                    }
                } else {
                }


            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

                Log.d(TAG, "onFailure updateCoinDetailsBG");
                if (coins!=null && coins.size()>0){
                    callback.coinCallback(coins, true);
                }else {
                    callback.coinCallback(coins, false);
                }

                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        String data = Util.getCoinListAsString(coins);
                        databaseHelper.updateCoin(data);
                    }
                };
                if (coins!=null && coins.size()>0){
                    new Thread(runnable).start();
                }

                call.request().cacheControl().noCache();
                Log.d(TAG, "onFailure: updateCoinDetailsBG" + t.getMessage() + start + " " + end);
            }
        });
    }
}