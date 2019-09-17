package bangbit.in.coinanalysis;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.DisplayMetrics;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import bangbit.in.coinanalysis.Chat.ChatData;
import bangbit.in.coinanalysis.database.DatabaseHelper;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.Currency;
import bangbit.in.coinanalysis.pojo.CurrencyExchange;

import static bangbit.in.coinanalysis.Constant.CURRENCY_LOGO;
import static bangbit.in.coinanalysis.Constant.CURRENCY_NAME;
import static bangbit.in.coinanalysis.Constant.CURRENCY_SHARED_PREFERENCES;
import static bangbit.in.coinanalysis.Constant.CURRENCY_SYMBOL;
import static bangbit.in.coinanalysis.Constant.SPLASH_SCREEN_ADDED;
import static bangbit.in.coinanalysis.MyApplication.currencyMultiplyingFactor;
import static bangbit.in.coinanalysis.MyApplication.currencySymbol;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public class Util {

    private static String TAG = Util.class.getSimpleName();

    public static SpannableStringBuilder getTitleText(String title, Context context) {
        Typeface face = Typeface.createFromAsset(context.getAssets(),
                "fonts/rokkitt_regular.ttf");
        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder(title);
        spannableStringBuilder.setSpan(new CustomTypefaceSpan(face), 0, title.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        return spannableStringBuilder;
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp      A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px      A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / ((float) metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

    public static String getMillionValue(double value, boolean showDecimalPrecision) {
        long valInLong = (long) value;
        String newValue = String.valueOf(valInLong);

        long trilion = 100000000000L;
        if (valInLong >= trilion) {
            double temp = (double) valInLong / trilion;
            newValue = checkForNull(String.format("%.2f", temp)) + "T";
        } else if (valInLong >= 1000000000) {
            double temp = valInLong / 1000000000.00;
            newValue = checkForNull(String.format("%.2f", temp)) + "B";
        } else if (valInLong >= 1000000) {
            double temp = valInLong / 1000000.00;
            newValue = checkForNull(String.format("%.2f", temp)) + "M";
        } else if (valInLong >= 100000) {
            double temp = valInLong / 1000.00;
            newValue = checkForNull(String.format("%.2f", temp)) + "K";
        } else if (value < 1) {
            newValue = String.format("%.8f", value);
        } else if (value >= 1) {
            if (showDecimalPrecision == true) {
                newValue = checkForNull(String.format("%.2f", value));
            } else {
                newValue = checkForNull(Long.toString(valInLong)) + "";
            }
        }
        return newValue;
    }

    public static String getMillionValue(double value) {
        //long valInLong = (long) value;
        String newValue = String.valueOf(value);

        long trilion = 100000000000L;
        if (value >= trilion) {
            double temp = value / trilion;
            newValue = checkForNull(String.format("%.2f", temp)) + "T";
        } else if (value >= 1000000000) {
            double temp = value / 1000000000.00;
            newValue = checkForNull(String.format("%.2f", temp)) + "B";
        } else if (value >= 1000000) {
            double temp = value / 1000000.00;
            newValue = checkForNull(String.format("%.2f", temp)) + "M";
        } else if (value >= 100000) {
            double temp = value / 1000.00;
            newValue = checkForNull(String.format("%.2f", temp)) + "K";
        } else if (value < 1) {
            newValue = BigDecimal.valueOf(value).toPlainString();
            if (newValue.length() > 10) {
                newValue = newValue.substring(0, 10);
            }
            //newValue = String.valueOf(Float.parseFloat(String.valueOf(value)));
        } else if (value >= 1) {
            newValue = checkForNull(String.format("%.2f", value));
        }
        return newValue;
    }

    public static String[] getDayMonthFromILineDataSet(ILineDataSet dataset, int lastIndex) {
        String[] dates = {"", ""};
        Entry firstentry = dataset.getEntryForIndex(0);
        Entry lastentry = dataset.getEntryForIndex(lastIndex);
        int start = (int) firstentry.getX();
        int last = (int) lastentry.getX();
        Date firstDate = new Date((long) start * 1000);
        Date lastDate = new Date((long) last * 1000);
        String firstDaymonth = firstDate.getDate() + "." + new DateFormatSymbols().getShortMonths()[firstDate.getMonth()];
        String lastDaymonth = lastDate.getDate() + "." + new DateFormatSymbols().getShortMonths()[lastDate.getMonth()];
        dates[0] = firstDaymonth;
        dates[1] = lastDaymonth;
        return dates;
    }

    public static String getDayMonthFromUnixTime(float unixTime) {
        Date firstDate = new Date((long) unixTime * 1000);
        String firstDaymonth = firstDate.getDate() + "/" + new DateFormatSymbols().getShortMonths()[firstDate.getMonth()];

        return firstDaymonth;
    }

    public static String getTimeFromUnixTime(float unixTime) {
        Date firstDate = new Date((long) unixTime * 1000);
        int hr=firstDate.getHours();
        String hour=hr+"";
        if (hr<=9){
            hour="0"+hr;
        }

        int min=firstDate.getMinutes();
        String minute=min+"";
        if (min<=9){
            minute="0"+minute;
        }

        String firstDaymonth = hour+ ":" + minute;
        return firstDaymonth;
    }

    public static String getTimeDayFromUnixTime(float unixTime) {
        Date firstDate = new Date((long) unixTime * 1000);
        String firstDaymonth = firstDate.getHours() + ":" + firstDate.getMinutes() + " " + firstDate.getDate() + "/" + new DateFormatSymbols().getShortMonths()[firstDate.getMonth()];

        return firstDaymonth;
    }

    public static String getDayMonthYearHHMMSSFromUnixTime(float unixTime) {
        if(unixTime<1000)
        {
            return "NA";
        }
        Date firstDate = new Date((long) unixTime * 1000);
        SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yy hh:mm a");
        String date = format.format(firstDate);
        return date;
    }

    public static String getDayMonthYearFromUnixTime(float unixTime) {
        Date firstDate = new Date((long) unixTime * 1000);
        SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yy");
        String date = format.format(firstDate);
        return date;
    }

    public static String getDecimalPointValueIfLessThanZero(Double value, int decimalPrecision) {
        if (value > 1 || value <= -1) {
            return String.format("%.2f", value);
        } else {
            String sValue = BigDecimal.valueOf(value).toPlainString();
            if (sValue.length() > decimalPrecision + 2) {
                sValue = sValue.substring(0, decimalPrecision + 2);
                return sValue;
            } else {
                return sValue;
            }
        }
    }

    public static String getDecimalPointValue(float value, int decimalPrecision) {
        String s = String.format("%." + decimalPrecision + "f", value);
        s = s.indexOf(".") < 0 ? s : s.replaceAll("0*$", "").replaceAll("\\.$", "");
        return (s.indexOf(".") == -1 ? s + ".00" : s);
    }

    public static String getTwoDecimalPointValue(Double value) {
        if (value >= 1 || value <= -1) {
            return String.format("%.2f", value);
        } else {
            String sValue = BigDecimal.valueOf(value).toPlainString();
            if (sValue.length() > 8 + 2) {
                sValue = sValue.substring(0, 8 + 2);
                return sValue;
            } else {
                return sValue;
            }
        }
    }

    public static String getTwoDecimalPointValue(float value) {
        if (value >= 1 || value <= -1) {
            return String.format("%.2f", value);
        } else {
            String sValue = BigDecimal.valueOf(value).toPlainString();
            if (sValue.length() > 8 + 2) {
                sValue = sValue.substring(0, 8 + 2);
                return sValue;
            } else {
                return sValue;
            }
        }
    }

    public static String getTwoDecimalPointValue(String value) {
        try {
            float val = Float.parseFloat(value);
            if (val >= 1 || val <= -1) {
                return String.format("%.2f", val);
            } else {
                return value;
            }
        } catch (NumberFormatException e) {
            return value;
        }

    }

    public static String getOnlyTwoDecimalPointValue(float value) {
        return String.format("%.2f", value);
    }

    public static String getTwoDecimalPointValueInExponential(Double value) {
        String val = "";
        if (value > 1 || value <= -1) {
            val = Util.getTwoDecimalPointValue(value);
        } else {
            val += value;
        }
        return val;

    }

    public static String getTwoDecimalPointValueInExponential(Float value) {
        String val = "";
        if (value > 1 || value <= -1) {
            val = Util.getTwoDecimalPointValue(value);
        } else {
            val += value;
        }
        return val;
    }

    public static HashMap<String, Integer> getExchangeIconHashMap() {
        HashMap<String, Integer> stringDrawableHashMap = new HashMap<>();
        stringDrawableHashMap.put("abucoin", R.drawable.europe);
        stringDrawableHashMap.put("bitbay", R.drawable.poland);
        stringDrawableHashMap.put("bitfinex", R.drawable.british_virgin_islands);
        stringDrawableHashMap.put("bitflip", R.drawable.russia);
        stringDrawableHashMap.put("bitflyer", R.drawable.japan);
        stringDrawableHashMap.put("bitstamp", R.drawable.united_kingdom);
        stringDrawableHashMap.put("bittrex", R.drawable.united_states);
        stringDrawableHashMap.put("btcchina", R.drawable.china);
        stringDrawableHashMap.put("ccedk", R.drawable.denmark);
        stringDrawableHashMap.put("ccex", R.drawable.germany);
        stringDrawableHashMap.put("cexio", R.drawable.united_kingdom);
        stringDrawableHashMap.put("coinbase", R.drawable.united_states);
        stringDrawableHashMap.put("coincap", R.drawable.malaysia);
        stringDrawableHashMap.put("coinfloor", R.drawable.united_kingdom);
        stringDrawableHashMap.put("coinroom", R.drawable.poland);
        stringDrawableHashMap.put("coinsetter", R.drawable.united_states);
        stringDrawableHashMap.put("cryptsy", R.drawable.united_states);
        stringDrawableHashMap.put("exmo", R.drawable.spain);
        stringDrawableHashMap.put("extstock", R.drawable.united_kingdom);
        stringDrawableHashMap.put("gatecoin", R.drawable.china);
        stringDrawableHashMap.put("hitbtc", R.drawable.united_kingdom);
        stringDrawableHashMap.put("huobi", R.drawable.china);
        stringDrawableHashMap.put("itbit", R.drawable.united_states);
        stringDrawableHashMap.put("kraken", R.drawable.united_states);
        stringDrawableHashMap.put("lakebtc", R.drawable.china);
        stringDrawableHashMap.put("livecoin", R.drawable.united_kingdom);
        stringDrawableHashMap.put("localbitcoins", R.drawable.finland);
        stringDrawableHashMap.put("lykke", R.drawable.switzerland);
        stringDrawableHashMap.put("okcoin", R.drawable.china);
        stringDrawableHashMap.put("poloniex", R.drawable.united_states);
        stringDrawableHashMap.put("quadrigacx", R.drawable.canada);
        stringDrawableHashMap.put("quoine", R.drawable.japan);
        stringDrawableHashMap.put("remitano", R.drawable.seychelles);
        stringDrawableHashMap.put("therocktrading", R.drawable.malta);
        stringDrawableHashMap.put("trustdex", R.drawable.united_kingdom);
        stringDrawableHashMap.put("yobit", R.drawable.russia);
        stringDrawableHashMap.put("tidex", R.drawable.united_kingdom);
        stringDrawableHashMap.put("upbit", R.drawable.south_korea);
        stringDrawableHashMap.put("binance", R.drawable.china);
        stringDrawableHashMap.put("okex", R.drawable.china);
        stringDrawableHashMap.put("bithumb", R.drawable.south_korea);
        stringDrawableHashMap.put("gemini", R.drawable.united_states);
        stringDrawableHashMap.put("coinone", R.drawable.south_korea);
        stringDrawableHashMap.put("bitbank", R.drawable.japan);
        stringDrawableHashMap.put("liqui", R.drawable.ukraine);
        stringDrawableHashMap.put("ethfinex", R.drawable.british_virgin_islands);
        stringDrawableHashMap.put("cryptopia", R.drawable.new_zealand);
        stringDrawableHashMap.put("livecoin", R.drawable.united_kingdom);
        stringDrawableHashMap.put("kucoin", R.drawable.china);
        stringDrawableHashMap.put("korbit", R.drawable.south_korea);
        stringDrawableHashMap.put("bitcoin indonesia", R.drawable.indonesia);
        stringDrawableHashMap.put("bx thailand", R.drawable.thailand);
        stringDrawableHashMap.put("zebpay", R.drawable.india);
        stringDrawableHashMap.put("unocoin", R.drawable.india);
        return stringDrawableHashMap;
    }

    public static ArrayList<Currency> getCurrencyList() {
        ArrayList<Currency> currencyList = new ArrayList<Currency>();
        currencyList.add(new Currency("USD", "United States Dollar", "$", R.drawable.united_states));
        currencyList.add(new Currency("EUR", "Euro Member Countries", "€", R.drawable.europe));
        currencyList.add(new Currency("GBP", "United Kingdom Pound", "£", R.drawable.united_kingdom));
        currencyList.add(new Currency("CNY", "China Yuan Renminbi", "¥", R.drawable.china));
        currencyList.add(new Currency("AUD", "Australia Dollar", "A$", R.drawable.australia));
        //currencyList.add(new Currency("AED", "Arab Emirates Dirham", "\u062f\u002e\u0625", R.drawable.united_arab_emirates));
        currencyList.add(new Currency("AED", "Arab Emirates Dirham", "AED", R.drawable.united_arab_emirates));
        currencyList.add(new Currency("BRL", "Brazil Real", "R$", R.drawable.brazil));
        currencyList.add(new Currency("CAD", "Canada Dollar", "c$", R.drawable.canada));
        currencyList.add(new Currency("CHF", "Switzerland Franc", "SFr", R.drawable.switzerland));
        currencyList.add(new Currency("CLP", "Chile Peso", "$", R.drawable.chile));
        currencyList.add(new Currency("CZK", "Czech Republic Koruna", "Kč", R.drawable.czech_cepublic));
        currencyList.add(new Currency("DKK", "Denmark Krone", "Kr", R.drawable.denmark));
        currencyList.add(new Currency("HKD", "Hong Kong Dollar", "HK$", R.drawable.hong_kong));
        currencyList.add(new Currency("HUF", "Hungary Forint", "Ft", R.drawable.hungary));
        currencyList.add(new Currency("IDR", "Indonesia Rupiah", "Rp", R.drawable.indonesia));
        currencyList.add(new Currency("ILS", "Israel Shekel", "₪", R.drawable.israel));
        currencyList.add(new Currency("INR", "Indian Rupee", "₹", R.drawable.india));
        currencyList.add(new Currency("JPY", "Japanese Yen", " ¥", R.drawable.japan));
        currencyList.add(new Currency("KRW", "Korea (South) Won", "₩", R.drawable.south_korea));
        currencyList.add(new Currency("MXN", "Mexico Peso", "M$", R.drawable.mexico));
        currencyList.add(new Currency("MYR", "Malaysian Ringgit", "RM", R.drawable.malaysia));
        currencyList.add(new Currency("NOK", "Norway Krone", "kr", R.drawable.norway));
        currencyList.add(new Currency("NZD", "New Zealand Dollar", "$", R.drawable.new_zealand));
        currencyList.add(new Currency("PHP", "Philippines Peso", "₱", R.drawable.philippines));
        currencyList.add(new Currency("PKR", "Pakistan Rupee", "Rs", R.drawable.pakistan));
        currencyList.add(new Currency("PLN", "Poland Zloty", "zł", R.drawable.poland));
        currencyList.add(new Currency("RUB", "Russia Ruble", "\u20BD", R.drawable.russia));
        currencyList.add(new Currency("SEK", "Sweden Krona", "kr", R.drawable.sweden));
        currencyList.add(new Currency("SGD", "Singapore Dollar", "S$", R.drawable.singapore));
        currencyList.add(new Currency("THB", "Thailand Baht", "฿", R.drawable.thailand));
        currencyList.add(new Currency("TRY", "Turkey Lira", "₺", R.drawable.turkey));
        currencyList.add(new Currency("TWD", "Taiwan New Dollar", "NT$", R.drawable.taiwan));
        currencyList.add(new Currency("ZAR", "South Africa Rand", "R", R.drawable.south_africa));

        return currencyList;
    }

    public static String getDayMonthYearFromUnixTime(String unixTime) {
        try {
            long unixTimeLong = Long.parseLong(unixTime);
            unixTimeLong = unixTimeLong * 1000;
            Date firstDate = new Date(unixTimeLong);
            SimpleDateFormat format = new SimpleDateFormat("dd/MMM/yy");
            String date = format.format(firstDate);
            return date;
        } catch (NumberFormatException e) {
            return unixTime;
        }
    }

    public static float getFloatValue(String value) {
        if (value != null) {
            try
            {
                return Float.parseFloat(value);
            }
            catch (NumberFormatException e) {
                return 0;
            }
            catch (NullPointerException e) {
                return 0;
            }
        } else {
            return 0;
        }
    }


    public static String getInitialNumberOfZeros(String value) {
        int a = 0;
        String zeros = "";

        while (value.length() > a && value.charAt(a) == '0') {
            a++;
            zeros += "0";
        }
        return zeros;
    }

    public static ArrayList<String> getPairCurrencyList(){
        ArrayList<String> pairs = new ArrayList<>();
        pairs.add("BTC");
        pairs.add("ETH");
        pairs.add("LTC");
        pairs.add("USD");
        pairs.add("EUR");
        pairs.add("AUD");
        pairs.add("CAD");
        pairs.add("CNY");
        pairs.add("GBP");
        pairs.add("INR");
        pairs.add("JPY");
        pairs.add("KRW");
        pairs.add("RUR");
        pairs.add("DOGE");
        pairs.add("XMR");
        pairs.add("WAVES");
        pairs.add("BCH");
        pairs.add("NEO");
        pairs.add("BNB");

        return pairs;
    }
    public static ArrayList<CurrencyExchange> getCurrencyExchangeList() {
        ArrayList<CurrencyExchange> currencyExchangeArrayList = new ArrayList<>();
        currencyExchangeArrayList.add(new CurrencyExchange("Abucoin", "Europe", "https://abucoins.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("BitBay", "Poland", "https://bitbay.net/en"));
        currencyExchangeArrayList.add(new CurrencyExchange("Bitfinex", "British Virgin Islands", "https://www.bitfinex.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("BitFlip", "Russia", "https://bitflip.li/"));
        currencyExchangeArrayList.add(new CurrencyExchange("bitFlyer", "Japan", "https://bitflyer.jp/"));
        currencyExchangeArrayList.add(new CurrencyExchange("BitSquare", "Unknown", "https://bisq.network/"));
        currencyExchangeArrayList.add(new CurrencyExchange("bitstamp", "United Kingdom", "https://www.bitstamp.net/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Bittrex", "United States of America", "https://bittrex.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("BTCChina", "China", "https://www.btcc.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("CCEDK", "Denmark", "https://www.ccedk.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("CCEX", "Germany", "https://c-cex.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Cexio", "United Kingdom", "https://cex.io/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Coinbase", "United States of America", "https://www.coinbase.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Coincap", "Malaysia", "https://coincap.exchange/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Coinfloor", "United Kingdom", "https://coinfloor.co.uk/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Coinroom", "Poland", "https://coinroom.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Coinsetter", "United States of America", "http://www.coinsetter.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Exmo", "Spain", "https://exmo.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("ExtStock", "United Kingdom", "https://extstock.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Gatecoin", "China", "https://gatecoin.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("HitBTC", "United Kingdom", "https://hitbtc.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Huobi", "China", "https://www.huobi.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("itBit", "United States of America", "https://www.itbit.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Kraken", "United States of America", "https://www.kraken.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("LakeBTC", "China", "https://www.lakebtc.com"));
        currencyExchangeArrayList.add(new CurrencyExchange("LiveCoin", "United Kingdom", "https://livecoin.net/"));
        currencyExchangeArrayList.add(new CurrencyExchange("LocalBitcoins", "Finland", "https://localbitcoins.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Lykke", "Switzerland", "https://www.lykke.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("MonetaGo", "Unknown", "https://monetago.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("OKCoin", "China", "https://www.okcoin.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Poloniex", "United States of America", "https://poloniex.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("QuadrigaCX", "Canada", "https://www.quadrigacx.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Quoine", "Japan", "https://www.quoine.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Remitano", "Seychelles", "https://remitano.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("TheRockTrading", "Malta", "https://www.therocktrading.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("TrustDEX", "United Kingdom", "https://trustdex.io/"));
        currencyExchangeArrayList.add(new CurrencyExchange("WavesDEX", "Unknown", "https://wavesplatform.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Yobit", "Russia", "https://yobit.net/en/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Tidex", "United Kingdom", "https://tidex.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Upbit", "South Korea", "https://upbit.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Binance", "China", "https://www.binance.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("OKEx", "China", "https://www.okex.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Bithumb", "South Korea", "https://www.bithumb.com/EN/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Gemini", "United States of America", "https://gemini.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Coinone", "South Korea", "https://coinone.co.kr/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Bitbank", "Japan", "https://bitbank.cc/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Liqui", "Ukraine", "https://liqui.io/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Ethfinex", "British Virgin Islands", "https://www.ethfinex.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Cryptopia", "New Zeland", "https://www.cryptopia.co.nz/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Livecoin", "United Kingdom", "https://livecoin.net/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Kucoin", "China", "https://www.kucoin.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Korbit", "South Korea", "https://www.korbit.co.kr/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Bitcoin Indonesia", "Indonesia", "http://bitcoin.co.id/"));
        currencyExchangeArrayList.add(new CurrencyExchange("BX Thailand", "Thailand", "https://bx.in.th/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Zebpay", "India", "https://www.zebpay.com/"));
        currencyExchangeArrayList.add(new CurrencyExchange("Unocoin", "India", "https://www.unocoin.com/"));
        return currencyExchangeArrayList;
    }

    public static HashMap<String, String> getCoinMarketcapCryptoCompareHashMap() {
        HashMap<String, String> coinMarketcapCryptoCompareHashMap = new HashMap<>();
        coinMarketcapCryptoCompareHashMap.put("MIOTA", "IOT");
        coinMarketcapCryptoCompareHashMap.put("NANO", "NAN");
        coinMarketcapCryptoCompareHashMap.put("ETHOS", "BQX");
        coinMarketcapCryptoCompareHashMap.put("SMT", "SMT*");
        coinMarketcapCryptoCompareHashMap.put("BCO", "BCO*");
        coinMarketcapCryptoCompareHashMap.put("SBD", "SBD*");
        coinMarketcapCryptoCompareHashMap.put("UTNP", "UTN");
        coinMarketcapCryptoCompareHashMap.put("BCO", "BCO*");
        coinMarketcapCryptoCompareHashMap.put("GETX", "GET");
        coinMarketcapCryptoCompareHashMap.put("$PAC", "PAC");
        return coinMarketcapCryptoCompareHashMap;
    }

    public static String trimTrailingZeros(String doubleValue)
    {
        Double fValue=0.0;
        try{
            if (doubleValue.contains("E")) {
                fValue = Double.parseDouble(doubleValue);
                doubleValue = BigDecimal.valueOf(fValue).toPlainString();
            }
            String[] numbers=doubleValue.split("\\."); //regex default
            if(numbers.length>=2)
            {
               int i=numbers[1].length()-1;
               for( ;i>=0;i--)
               {
                   if(numbers[1].charAt(i)!='0') break;
               }
               if(i<=0) i=1;
               doubleValue =  numbers[0] + "." + numbers[1].substring(0,i);
            }
        }
        catch(NumberFormatException ex)
        {
        }
        return doubleValue;
    }
    public static String checkForNull(String value) {
        String originalValue = value;
        try {
            if (value == null || value.trim().compareTo("")==0) {
                return "NA";
            } else {
                Double fValue = Double.parseDouble(originalValue);
                if (fValue < 1) {
                    originalValue = BigDecimal.valueOf(fValue).toPlainString();
                    if (originalValue.length() > 16) {
                        originalValue = originalValue.substring(0, 16);
                    }
                    return originalValue;
                }

                if (value.contains("-")) {
                    value = value.substring(1);
                    int decimalIndex = value.indexOf(".");
                    if (decimalIndex != -1) {
                        String decimalValue = value.substring(decimalIndex);
                        decimalValue = decimalValue.substring(1);
                        String zeros = Util.getInitialNumberOfZeros(decimalValue);
                        int tempDecimal = Integer.parseInt(decimalValue);
                        if (tempDecimal == 0) {
                            decimalValue = "." + zeros;
                        } else if (tempDecimal <= 15) {
                            decimalValue = "." + zeros + tempDecimal + ((zeros.length() == 0) ? "0" : "");
                        } else {
                            decimalValue = "." + zeros + (tempDecimal * 100) / 100;
                        }
                        value = value.substring(0, decimalIndex);
                        value = getComaValue(value);
                        value = "-" + value + decimalValue;
                    } else {
                        value = getComaValue(value);
                        value = "-" + value;
                    }

                } else {
                    int decimalIndex = value.indexOf(".");
                    if (decimalIndex != -1) {
                        String decimalValue = value.substring(decimalIndex);
                        decimalValue = decimalValue.substring(1);
                        String zeros = Util.getInitialNumberOfZeros(decimalValue);
                        int tempDecimal = Integer.parseInt(decimalValue);
                        if (tempDecimal == 0) {
                            decimalValue = "." + zeros;
                        } else if (tempDecimal <= 15) {
                            decimalValue = "." + zeros + tempDecimal + ((zeros.length() == 0) ? "0" : "");
                        } else {
                            decimalValue = "." + zeros + (tempDecimal * 100) / 100;
                        }
                        value = value.substring(0, decimalIndex);
                        value = getComaValue(value);
                        value = value + decimalValue;
                    } else {
                        value = getComaValue(value);
                    }
                }
                if(fValue>=1)
                {
                    String[] number = value.split("\\.");
                    if(number.length==2)
                    {
                        if(number[1].length()>2)
                        {
                            value = number[0] + "."+ number[1].substring(0,2);
                        }
                    }
                }
                return value;
            }
        } catch (NumberFormatException e) {
            if (originalValue.contains("E")) {

                Double fValue = Double.parseDouble(originalValue);
                if (fValue >= 1) {
                    originalValue = getComaValue(BigDecimal.valueOf(fValue).toPlainString());
                } else {
                    originalValue = BigDecimal.valueOf(fValue).toPlainString();
                    if (originalValue.length() > 16) {
                        originalValue = originalValue.substring(0, 16);
                    }
                }
                return originalValue;
            } else {
                return originalValue;
            }
        }
    }

    public static String getCoinListAsString(ArrayList<Coin> coinArrayList) {
        ArrayList<Coin> temp=new ArrayList<>(coinArrayList);
        Gson gson = new GsonBuilder().create();
        JsonArray myCustomArray = gson.toJsonTree(temp).getAsJsonArray();
        return myCustomArray.toString();
    }

    public static ArrayList<Coin> getCoinListFromString(String data) {
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<ArrayList<Coin>>() {
        }.getType();
        ArrayList<Coin> coinArrayList = gson.fromJson(data, type);
        return coinArrayList;
    }

    public static String getComaValue(String value) {
        String val = "";
        int length = value.length() - 1;
        int cnt = 1;
        while (length >= 0) {
            if (cnt == 3) {
                if (length == 0) {
                    val = value.charAt(length) + val;
                    cnt = 1;
                } else {
                    val = "," + value.charAt(length) + val;
                    cnt = 1;
                }
            } else {
                val = value.charAt(length) + val;
                cnt++;
            }
            length--;
        }
        return val;
    }

    public static String getCommaFormattedValue(String value) {
        float fValue = Float.parseFloat(value);
        if (fValue <= 1) return value;

        String val = "";
        int length = value.length() - 1;
        int cnt = 1;
        while (length >= 0) {
            if (cnt == 3) {
                if (length == 0) {
                    val = value.charAt(length) + val;
                    cnt = 1;
                } else {
                    val = "," + value.charAt(length) + val;
                    cnt = 1;
                }
            } else {
                val = value.charAt(length) + val;
                cnt++;
            }
            length--;
        }
        return val;
    }

    public static String getFormattedPrice(String value) {
        String price;
        if (!value.equals("0")) {
            try
            {
                float multipliedValue = Float.parseFloat(value) * currencyMultiplyingFactor;
                if (multipliedValue >= 1) {
                    price = getOnlyTwoDecimalPointValue(multipliedValue);
                    price = currencySymbol + Util.checkForNull(price);
                } else {
                    String sValue = BigDecimal.valueOf(multipliedValue).toPlainString();
                    if (sValue.length() > 10) {
                        sValue = sValue.substring(0, 10);
                    }
                    price = currencySymbol + sValue;
                }
            }
            catch(NumberFormatException ex)
            {
                price = "NA";
            }

        } else {
            price = "NA";
        }
        return price;
    }

    public static String getLastUpdatedString(String lastUpdatedString) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(Long.parseLong(lastUpdatedString)); //@@@ //* 1000
        int date = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        String AM_PM;
        String[] sMonth = new String[]
                {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug",
                        "Sep", "Oct", "Nov", "Dec"};
        if (calendar.get(Calendar.AM_PM) == 0) {
            AM_PM = "AM";
        } else {
            AM_PM = "PM";
        }
        return "\nData last updated on " + String.format("%02d", date) + "/" + sMonth[month] + "/"
                + year + " " + String.format("%02d", (AM_PM.compareToIgnoreCase("PM")==0 && hour==0)? 12:hour) + ":" + String.format("%02d", minute) + " "
                + AM_PM + "";
    }

    public static Currency getCurrency(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CURRENCY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String currencySymbol = sharedPreferences.getString(CURRENCY_SYMBOL, "USD");
        String currencyName = sharedPreferences.getString(CURRENCY_NAME, "United States Dollar");
        String currencyLogo = sharedPreferences.getString(CURRENCY_LOGO, "$");
        Currency currency = new Currency(currencySymbol, currencyName, currencyLogo, R.drawable.united_states);
        return currency;
    }

    public static void setCurrency(Currency currency, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CURRENCY_SHARED_PREFERENCES, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CURRENCY_SYMBOL, currency.getSymbol());
        editor.putString(CURRENCY_NAME, currency.getName());
        editor.putString(CURRENCY_LOGO, currency.getCurrencylogo());
        editor.commit();
    }

    public static boolean getSplashScreenStatus(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CURRENCY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        boolean splashScreenStatus = sharedPreferences.getBoolean(SPLASH_SCREEN_ADDED, false);
        return splashScreenStatus;
    }

    public static void setSplashScreenStatus(Context context, boolean splashScreenStatus) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CURRENCY_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(SPLASH_SCREEN_ADDED, splashScreenStatus);
        editor.commit();
    }

    //Chat Module Util
    public static String getDayORMonthORYear(int data) {
        float daysFloat = 0f;
        String days = "";

        if (data >= 365) {
            daysFloat = (float) data / 365;
            if (daysFloat - (int) daysFloat == 0) {
                days = "" + (int) daysFloat + ((daysFloat<=1)? " year":" years");
            } else {
                days = "" + String.format("%.1f", daysFloat) + ((daysFloat<=1)? " year":" years");
            }
        } else if (data >= 30) {
            daysFloat = (float) data / 30;
            if (daysFloat - (int) daysFloat == 0) {
                days = "" + (int) daysFloat + ((daysFloat<=1)? " month":" months");
            } else {
                //days = "" + String.format("%.1f", daysFloat) + ((daysFloat<=1)? " month":" months");
                days = data + ((data<=1)? " day":" days");
            }
        } else {
            days = data + ((data<=1)? " day":" days");
        }
        return days;
    }

    public static String getChatData(ChatData chatData) {
        Gson gson = new GsonBuilder().create();
        return gson.toJson(chatData);
    }

    public static ChatData getChatData(String data) {
        Gson gson = new GsonBuilder().create();
        Type type = new TypeToken<ChatData>() {
        }.getType();
        ChatData chatData = gson.fromJson(data, type);
        return chatData;
    }

    public static String getCoinUrl(String coinSymbol, Context context) {
        DatabaseHelper databaseHelper = new DatabaseHelper(context);
        return databaseHelper.getCoinUrl(coinSymbol);
    }

    public static String multiplyWithCurrency(String value){

        try{
            Float floatValue=Float.parseFloat(value);
            floatValue=floatValue*MyApplication.currencyMultiplyingFactor;
            value=getTwoDecimalPointValue(floatValue);
            return value;

        }catch (NumberFormatException ex){
            return MyApplication.currencySymbol+value;
        }

    }
    public static String parseDate(String date){
        try {
            Date mydate=new SimpleDateFormat("dd/MM/yyyy").parse(date);
            date=new SimpleDateFormat("d MMM yyyy").format(mydate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public static String isFiatCurrency(String currency)
    {
        if(currency==null || currency.equals("")) return "0:";
        ArrayList<Currency> currencyArrayList = Util.getCurrencyList();
        for (Currency currency1 :
                currencyArrayList) {
            if (currency1.getSymbol().toLowerCase().equals(currency.toLowerCase())) {
                return "1:"+currency1.getCurrencylogo();
            }
        }
        return "0:"+currency;
    }

}

