package bangbit.in.coinanalysis.NetworkCall;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import bangbit.in.coinanalysis.CurrencyUpdator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.ContentValues.TAG;

/**
 * Created by Nagarajan on 2/27/2018.
 */

public class GetValueFromUsd {
    
    public static void getValueFromUsd(CurrencyUpdator updator) {
        ApiInterfaceCoin apiInterfaceQuote = ApiService.getAPIInterfaceQuote();
        final CurrencyUpdator currencyUpdator = updator;
        apiInterfaceQuote.getQuotes().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {

                String json = null;
                json = response.body();

                JSONObject jsonObjectResponse;
                JSONObject rates = null;

                try {
                    if(json!=null || json.trim().compareTo("")!=0)
                    {
                        jsonObjectResponse=new JSONObject(json);
                        rates=jsonObjectResponse.getJSONObject("rates");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d(TAG, "onResponse: " + e.getMessage());
                }
                currencyUpdator.updateCurrency(rates);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
            }

            });
    }
}