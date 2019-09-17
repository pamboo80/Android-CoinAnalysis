package bangbit.in.coinanalysis.repository;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import bangbit.in.coinanalysis.NetworkCall.ApiInterface;
import bangbit.in.coinanalysis.NetworkCall.ApiInterfaceCoin;
import bangbit.in.coinanalysis.NetworkCall.ApiInterfaceImage;
import bangbit.in.coinanalysis.NetworkCall.ApiService;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.database.DatabaseHelper;
import bangbit.in.coinanalysis.pojo.Coin;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Nagarajan on 3/13/2018.
 */

public class CoinRepository {

    private static final String TAG = CoinRepository.class.getSimpleName();
    private final Context context;
    DatabaseHelper databaseHelper;

    ApiInterface apiInterface = ApiService.getAPIInterface();
    ApiInterfaceCoin apiInterfaceCoin = ApiService.getAPIInterfaceCoin();

    public CoinRepository(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    public void getCoinUrlFromDb(CoinImageUrlHashMapCallback coinImageUrlListCallback) {
        HashMap<String, String> imageUrlHashMap = databaseHelper.getImageUrlHashMap();
        Log.d(TAG, "getCoinUrlFromDb: " + imageUrlHashMap.size());
        coinImageUrlListCallback.coinUrlCallback(imageUrlHashMap, true);
    }

    public void getCoinUrl(final CoinImageUrlHashMapCallback coinUrlListCallback) {
        ApiInterfaceImage apiInterfaceImage = ApiService.getAPIInterfaceImage();
        apiInterfaceImage.getImageCoinList().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String responseString = "";

                if (response.body() != null) {
                    responseString = response.body();
                } else
                    return;

                final HashMap<String, String> coinImageUrlHashmap = new HashMap<String, String>();
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(responseString);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String baseurl = null;
                try {
                    baseurl = jsonObject.getString("BaseImageUrl");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                try {
                    jsonObject = jsonObject.getJSONObject("Data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                Iterator<?> keys = jsonObject.keys();
                while (keys.hasNext()) {
                    String iteratorKey = (String) keys.next();
                    try {
                        if (jsonObject.get(iteratorKey) instanceof JSONObject) {
                            JSONObject coinObject = (JSONObject) jsonObject.get(iteratorKey);
                            if (!coinObject.isNull("Symbol") && !coinObject.isNull("ImageUrl")) {
                                String key = coinObject.getString("Symbol");
                                String value_url = baseurl + coinObject.getString("ImageUrl");
                                coinImageUrlHashmap.put(key, value_url);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                coinUrlListCallback.coinUrlCallback(coinImageUrlHashmap, true);

                String newImageURLQuery="";
                int insertDataCount=0;
                HashMap<String, String> dbImageUrlHashMap = databaseHelper.getImageUrlHashMap();
                for (String key : coinImageUrlHashmap.keySet()) {

                    try {
                        if (dbImageUrlHashMap.get(key) == null) {
                            //databaseHelper.insertOne(key, coinImageUrlHashmap.get(key));
                            if(newImageURLQuery.compareTo("")!=0) newImageURLQuery+=",";
                            newImageURLQuery += "('" + key +"','"+ coinImageUrlHashmap.get(key) + "')";
                            insertDataCount++;
                            if(insertDataCount>400){
                                insertDataCount=0;
                                databaseHelper.insertNewImageURL(newImageURLQuery);
                                newImageURLQuery="";
                            }
                        } else if (!coinImageUrlHashmap.get(key).equals(dbImageUrlHashMap.get(key))) {
                            databaseHelper.updateImageUrl(key, coinImageUrlHashmap.get(key));
                        }
                    } catch (NullPointerException e) {
                        //databaseHelper.insertOne(key, coinImageUrlHashmap.get(key));
                    }
                    catch(Exception ex)
                    {
                        //ignore other exceptions
                    }
                }
                databaseHelper.insertNewImageURL(newImageURLQuery);
            }


            @Override
            public void onFailure(Call<String> call, Throwable t) {
                coinUrlListCallback.coinUrlCallback(null, false);
            }
        });
    }

    public boolean isCoinFavorited(String coinSymbol) {
        return databaseHelper.isCoinFavorited(coinSymbol);

    }

    public void getCoinsV2(final CoinListCallback callback) {

        ArrayList<Coin> coinArrayListDatabase;
        String data = databaseHelper.getCoins();
        coinArrayListDatabase = Util.getCoinListFromString(data);

        if (coinArrayListDatabase == null) {
            coinArrayListDatabase = new ArrayList<>(0);
        }

        final ApiInterface apiInterface = ApiService.getAPIInterface();
        //final ApiInterfaceCoin apiInterfaceCoin = ApiService.getAPIInterfaceCoin();
        final ArrayList<Coin> finalCoinArrayListDatabase = coinArrayListDatabase;

        apiInterfaceCoin.getAllCoin().enqueue(new Callback<String>() {
        //apiInterface.getAllCoin(1, 2000).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.body() != null) {
                    String responseString = response.body().toString();
                    try {

                        JSONObject jsonObj = new JSONObject(responseString);
                        final JSONArray jsonArray = (JSONArray) jsonObj.get("data");

                        Log.d(TAG, "onResponse: " + finalCoinArrayListDatabase.size());
                        if (finalCoinArrayListDatabase.size() > 0) {
                            callback.getDatabaseData(finalCoinArrayListDatabase);
                            callback.getCoinDetailPro(jsonArray);
                        }
                        else
                        {
                           callback.coinCallbackFirst(jsonArray);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(TAG, "onResponse: null response");
                    callback.getDatabaseData(finalCoinArrayListDatabase);
                    callback.coinCallback(finalCoinArrayListDatabase,false);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                callback.getDatabaseData(finalCoinArrayListDatabase);
                callback.coinCallback(finalCoinArrayListDatabase,false);
                Log.d(TAG, "onResponse: onFailure");
            }
        });
    }

    public Set<String> getFavoriteSet() {
        Set<String> favoriteCoinSet = databaseHelper.getFavoriteCoinSet();
        return favoriteCoinSet;
    }

    public void insertFavoriteCoin(String symbol) {
        databaseHelper.insertFavoriteCoin(symbol);
    }

    public void removeFavoriteCoin(String symbol) {
        databaseHelper.removeFavoriteCoin(symbol);
    }


    public void getCoinsInBackground(final CoinListCallback callback) {

          //@@@ Good learning
          //https://www.intertech.com/Blog/android-non-ui-to-ui-thread-communications-part-4-of-5/

//        Runnable runnable = new Runnable() {
//            @Override
//            public void run() {
                Log.d(TAG, "getCoinsInBackground: updateCoinDetailsBG ");
                getRemainingCoinBackground(1, 2000, new ArrayList<Coin>(), callback);
                //new CoinRepositoryBG().execute(callback);
//            }
//        };
//        new Thread(runnable).start();

    }

    private void getRemainingCoinBackground(final int start, final int end, final ArrayList<Coin> coins, final CoinListCallback callback)
    {

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
                        callback.getCoinDetailPro(jsonArray);
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

                //@@@ do we need this on failure, always size =0
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
