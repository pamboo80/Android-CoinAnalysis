package bangbit.in.coinanalysis.NetworkCall;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Nagarajan on 12/7/2017.
 */

public class LambdaApiService {

    public static final String BASE_URL_LAMBDA = "https://your-aws-lambda-url";

    private static Retrofit retrofitLambda = null;

    static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS).build();


    public static Retrofit getClient() {
        if (retrofitLambda == null) {
            retrofitLambda = new Retrofit.Builder()
                    .baseUrl(BASE_URL_LAMBDA)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofitLambda;
    }

    public static LambdaApiInterface getAPIInterface() {
        LambdaApiInterface apiInterface = LambdaApiService.getClient().create(LambdaApiInterface.class);
        return apiInterface;
    }

}

