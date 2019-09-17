package bangbit.in.coinanalysis;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Locale;
import java.util.TimeZone;
import java.util.Date;

import bangbit.in.coinanalysis.CoinDetailPro.CoinDetailPro;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.repository.PojoUtilCallback;
import bangbit.in.coinanalysis.v2Pojo.CoinDetailV2;

import static bangbit.in.coinanalysis.Util.checkForNull;
import static java.lang.Long.parseLong;

public class PojoUtil {

    private static Double BTCPriceInUSD = 0.0;

    private static String getBTCValue(String usdPrice)
    {
        return( (BTCPriceInUSD==0.0)? "NA": checkForNull(String.valueOf(Double.parseDouble(usdPrice)/BTCPriceInUSD)) );
    }

    private static String getValue(String json,String key, String subKey1, String subKey2)
    {
        String retValue= "";
        JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();

        try {
            if(subKey1=="")
            {
                if(jsonObject.get(key) instanceof com.google.gson.JsonNull)
                {
                    if(key.toLowerCase().equals("max_supply") || key.toLowerCase().equals("total_supply") || key.toLowerCase().equals("circulating_supply"))
                    {
                        retValue ="0.0";
                    }
                }
                else
                if(key.toLowerCase().equals("last_updated"))
                {
                    Date date;
                    SimpleDateFormat sdfUTCFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                            Locale.ENGLISH);
                    SimpleDateFormat sdfUTCFormatWithOutMillisecond =new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'",
                            Locale.ENGLISH);
                    sdfUTCFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
                    sdfUTCFormatWithOutMillisecond.setTimeZone(TimeZone.getTimeZone("UTC"));
                    String dateString = jsonObject.get(key).getAsString();
                    Calendar calendar = Calendar.getInstance();
                    try {
                        //formatting the dateString to convert it into a Date
                        if(dateString.toLowerCase().contains("z") && dateString.toLowerCase().contains(":") && dateString.toLowerCase().contains("."))
                        {
                            date = sdfUTCFormat.parse(dateString);
                        }
                        else
                        if(dateString.toLowerCase().contains("z") && dateString.toLowerCase().contains(":"))
                        {
                            date = sdfUTCFormatWithOutMillisecond.parse(dateString);
                        }
                        else //Unix timestamp
                        {
                            long timeStamp = parseLong(dateString);
                            date = new java.util.Date(timeStamp*1000);
                            sdfUTCFormat.format(timeStamp);
                        }
                        calendar.setTime(date);

                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    retValue = Long.toString(calendar.getTimeInMillis());
                }
                else
                retValue = jsonObject.get(key).getAsString();
            }
            else
            if(subKey2=="")
            {
                if(jsonObject.get(key) instanceof com.google.gson.JsonNull)
                {
                   retValue ="0.0";
                }
                else {
                    retValue = jsonObject.get(key).getAsJsonObject().get(subKey1).getAsString();
                }
            }
            else
            {
                if( jsonObject.get(key).getAsJsonObject().get(subKey1)instanceof com.google.gson.JsonNull
                    || jsonObject.get(key).getAsJsonObject().get(subKey1).getAsJsonObject().get(subKey2) instanceof com.google.gson.JsonNull)
                {
//                    if(subKey2.toLowerCase()=="percent_change_1h" || subKey2.toLowerCase()=="percent_change_24h"
//                            || subKey2.toLowerCase()=="percent_change_7d" || subKey2.toLowerCase()=="volume_24h" || subKey2.toLowerCase()=="market_cap") {

                        retValue = "0.00";
//                    }
                }
                else
                if(subKey2.toLowerCase().equals("percent_change_1h") || subKey2.toLowerCase().equals("percent_change_24h")
                        || subKey2.toLowerCase().equals("percent_change_7d"))
                {
                    retValue = String.format("%.2f",Double.parseDouble(jsonObject.get(key).getAsJsonObject().get(subKey1).getAsJsonObject().get(subKey2).getAsString()));
                }
                else
                retValue = jsonObject.get(key).getAsJsonObject().get(subKey1).getAsJsonObject().get(subKey2).getAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
            if(subKey2.toLowerCase().equals("price"))
            {
                retValue="0.0";
            }
        }
        return retValue;
    }

    public static void getCoinDetailPro(final JSONArray coinDetailPro, final PojoUtilCallback pojoUtilCallback) {

        Runnable runnable = new Runnable() {
        //new Handler(Looper.getMainLooper()).post((new Runnable() {
            @Override
            public void run() {
                ArrayList<Coin> coins = new ArrayList<>();
                for (int i=0; i<coinDetailPro.length();i++) {
                    Coin coin = new Coin();
                    try {

                        String jsonString = coinDetailPro.getJSONObject(i).toString();

                        String id = getValue(jsonString,"id","","");
                        coin.setId(id);
                        String symbol = getValue(jsonString,"symbol","","");
                        coin.setSymbol(symbol);
                        String name = getValue(jsonString,"name","","");
                        coin.setName(name);
                        String rank = getValue(jsonString,"cmc_rank","","");
                        coin.setRank(rank);
                        String priceUsd =  getValue(jsonString,"quote","USD","price");
                        coin.setPriceUsd(priceUsd);
                        if(id.equals("1") && symbol.toLowerCase().equals("BTC".toLowerCase()))
                        {
                            BTCPriceInUSD = Double.parseDouble(priceUsd);
                        }
                        //String priceBtc = getValue(jsonString,"quote","USD","price"); //BTC //@@@
                        coin.setPriceBtc(getBTCValue(priceUsd));
                        String hVolumeUsd = getValue(jsonString,"quote","USD","volume_24h");
                        coin.set24hVolumeUsd(hVolumeUsd);
                        String marketCapUsd = getValue(jsonString,"quote","USD","market_cap");
                        coin.setMarketCapUsd(marketCapUsd);
                        String availableSupply = getValue(jsonString,"circulating_supply","","");
                        coin.setAvailableSupply(availableSupply);
                        String totalSupply = getValue(jsonString,"total_supply","","");
                        coin.setTotalSupply(totalSupply);
                        String maxSupply = getValue(jsonString,"max_supply","","");
                        coin.setMaxSupply(maxSupply);
                        String percentChange1h = getValue(jsonString,"quote","USD","percent_change_1h");
                        coin.setPercentChange1h(percentChange1h);
                        String percentChange7d = getValue(jsonString,"quote","USD","percent_change_7d");
                        coin.setPercentChange7d(percentChange7d);
                        String percentChange24h = getValue(jsonString,"quote","USD","percent_change_24h");
                        coin.setPercentChange24h(percentChange24h);
                        String lastUpdated = getValue(jsonString,"last_updated","","");
                        coin.setLastUpdated(lastUpdated);
                        coins.add(coin);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                pojoUtilCallback.onGetCoinDetailProCallback(coins);
            }
        //}));
        };
        new Thread(runnable).start();

    }

    public static ArrayList<Coin> getCoinDetailV2(JSONObject jsonObj) {
        Iterator<?> keys = jsonObj.keys();
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<CoinDetailV2>() {
        }.getType();

        ArrayList<CoinDetailV2> coinDetailV2s = new ArrayList<>();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            try {
                if (jsonObj.get(key) instanceof JSONObject) {
                    String data = jsonObj.get(key).toString();
                    CoinDetailV2 coinDetailV2 = gson.fromJson(data, type);
                    coinDetailV2s.add(coinDetailV2);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return mapV2CoinToCoin(coinDetailV2s);
    }

    public static ArrayList<Coin> mapV2CoinToCoin(ArrayList<CoinDetailV2> coinDetailV2s) {
        ArrayList<Coin> coins = new ArrayList<>();
        for (CoinDetailV2 coinDetailV2 : coinDetailV2s) {
            Coin coin = new Coin();
            String id = coinDetailV2.getId().toString();
            coin.setId(id);
            String symbol = coinDetailV2.getSymbol();
            coin.setSymbol(symbol);
            String name = coinDetailV2.getName();
            coin.setName(name);
            String rank = coinDetailV2.getRank().toString();
            coin.setRank(rank);
            String priceUsd = getValueInString(coinDetailV2.getQuotes().getUSD().getPrice());
            coin.setPriceUsd(priceUsd);
            String priceBtc = getValueInString(coinDetailV2.getQuotes().getbTC().getPrice());
            coin.setPriceBtc(priceBtc);
            String hVolumeUsd = getValueInString(coinDetailV2.getQuotes().getUSD().getVolume24h());
            coin.set24hVolumeUsd(hVolumeUsd);
            String marketCapUsd = getValueInString(coinDetailV2.getQuotes().getUSD().getMarketCap());
            coin.setMarketCapUsd(marketCapUsd);
            String availableSupply = getValueInString(coinDetailV2.getCirculatingSupply());
            coin.setAvailableSupply(availableSupply);
            String totalSupply = getValueInString(coinDetailV2.getTotalSupply());
            coin.setTotalSupply(totalSupply);
            String maxSupply = getValueInString(coinDetailV2.getMaxSupply());
            coin.setMaxSupply(maxSupply);
            String percentChange1h = getValueInString(coinDetailV2.getQuotes().getUSD().getPercentChange1h());
            coin.setPercentChange1h(percentChange1h);
            String percentChange7d = getValueInString(coinDetailV2.getQuotes().getUSD().getPercentChange7d());
            coin.setPercentChange7d(percentChange7d);
            String percentChange24h = getValueInString(coinDetailV2.getQuotes().getUSD().getPercentChange24h());
            coin.setPercentChange24h(percentChange24h);
            String lastUpdated = coinDetailV2.getLastUpdated().toString();
            coin.setLastUpdated(lastUpdated);
            coins.add(coin);
        }
        return coins;
    }

    public static String getValueInString(Double aDouble) {
        if (aDouble==null){
            return "0";
        }
        DecimalFormat df = new DecimalFormat("#");
        df.setMaximumFractionDigits(10);
        String str = String.valueOf(df.format(aDouble));
        str=str.replace("-.","-0.");
        if (str.length()>0 && str.charAt(0)=='.'){
            str=str.replace(".","0.");
        }
        return str;
    }
}
