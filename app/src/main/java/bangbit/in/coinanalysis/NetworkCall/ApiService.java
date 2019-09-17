package bangbit.in.coinanalysis.NetworkCall;

import android.os.SystemClock;

import java.io.IOException;
//import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.Dispatcher;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Nagarajan on 12/7/2017.
 */

public class ApiService {

    public static final String BASE_URL = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/";
    public static final String BASE_URL_IMAGE = "https://www.cryptocompare.com/api/";
    public static final String BASE_URL_History = "https://min-api.cryptocompare.com/data/";
    public static final String BASE_URL_COIN = "http://<our-coin-market-cap-server>:8000/";

    private static Retrofit retrofit = null;
    private static Retrofit retrofitWithoutDelay = null;

    private static Retrofit retrofitImageUrl = null;
    private static Retrofit retrofitHistory = null;
    private static Retrofit retrofitCoin = null;
    private static Retrofit retrofitQuote = null;

    static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS).build();

    static OkHttpClient.Builder builderWithQueue =new OkHttpClient.Builder();
    static Dispatcher dispatcher = new Dispatcher();

    static Interceptor interceptor = new Interceptor() {
        @Override
        public Response intercept(Chain chain) throws IOException {
            SystemClock.sleep(1000);
            return chain.proceed(chain.request());
        }
    };

    public static Retrofit getClient(){
        if (retrofit == null) {
            dispatcher.setMaxRequests(5);
            builderWithQueue.addInterceptor(interceptor);
            builderWithQueue.dispatcher(dispatcher);
            OkHttpClient clientWithQueue =builderWithQueue.build();

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    //.callbackExecutor(Executors.newSingleThreadExecutor())
                    .client(clientWithQueue)
                    .build();
        }
        return retrofit;
    }

    public static Retrofit getClientWitoutDelay(){
        if (retrofitWithoutDelay == null) {

            retrofitWithoutDelay = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitWithoutDelay;
    }

    public static ApiInterface getAPIInterface() {
        ApiInterface apiInterface = ApiService.getClient().create(ApiInterface.class);
        return apiInterface;
    }

    public static ApiInterface getAPIInterfaceWithoutDealy() {
        ApiInterface apiInterface = ApiService.getClientWitoutDelay().create(ApiInterface.class);
        return apiInterface;
    }

    public static Retrofit getClientQuote(){
        if (retrofitQuote == null) {
            retrofitQuote = new Retrofit.Builder()
                    .baseUrl(BASE_URL_COIN)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitQuote;
    }

    public static Retrofit getClientCoin(){
        if (retrofitCoin == null) {
            dispatcher.setMaxRequests(5);
            builderWithQueue.addInterceptor(interceptor);
            builderWithQueue.dispatcher(dispatcher);
            OkHttpClient clientWithQueue =builderWithQueue
                    .connectTimeout(20, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .build();

            retrofitCoin = new Retrofit.Builder()
                    .baseUrl(BASE_URL_COIN)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    //.callbackExecutor(Executors.newSingleThreadExecutor())
                    .client(clientWithQueue)
                    .build();
        }
        return retrofitCoin;
    }


    public static Retrofit getClientImageUrl() {
        if (retrofitImageUrl == null) {
            retrofitImageUrl = new Retrofit.Builder()
                    .baseUrl(BASE_URL_IMAGE)
                    .client(client)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .build();
        }
        return retrofitImageUrl;
    }

    public static ApiInterfaceImage getAPIInterfaceImage() {
        ApiInterfaceImage apiInterfaceImage = ApiService.getClientImageUrl().create(ApiInterfaceImage.class);
        return apiInterfaceImage;
    }

    public static ApiInterfaceCoin getAPIInterfaceQuote() {
        ApiInterfaceCoin apiInterfaceQuote = ApiService.getClientQuote().create(ApiInterfaceCoin.class);
        return apiInterfaceQuote;
    }

    public static ApiInterfaceCoin getAPIInterfaceCoin() {
        ApiInterfaceCoin apiInterfaceCoin = ApiService.getClientCoin().create(ApiInterfaceCoin.class);
        return apiInterfaceCoin;
    }

    public static Retrofit getClientHistory() {
        if (retrofitHistory == null) {
            retrofitHistory = new Retrofit.Builder()
                    .baseUrl(BASE_URL_History)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitHistory;
    }

    public static ApiInterfaceHistory getAPIInterfaceHistory() {
        ApiInterfaceHistory apiInterfaceHistory = ApiService.getClientHistory().create(ApiInterfaceHistory.class);
        return apiInterfaceHistory;
    }

}