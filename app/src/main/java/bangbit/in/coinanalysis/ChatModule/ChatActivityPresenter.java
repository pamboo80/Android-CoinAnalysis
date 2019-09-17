package bangbit.in.coinanalysis.ChatModule;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import ai.api.AIDataService;
import ai.api.AIListener;
import ai.api.AIServiceException;
import ai.api.android.AIService;
import ai.api.model.AIContext;
import ai.api.model.AIError;
import ai.api.model.AIOutputContext;
import ai.api.model.AIRequest;
import ai.api.model.AIResponse;
import ai.api.model.ResponseMessage;
import ai.api.model.Result;
import bangbit.in.coinanalysis.Chat.ChatData;
import bangbit.in.coinanalysis.NetworkCall.LambdaApiInterface;
import bangbit.in.coinanalysis.NetworkCall.LambdaApiService;
import bangbit.in.coinanalysis.pojo.Exchange;
import bangbit.in.coinanalysis.pojo.LambdaResponse;
import bangbit.in.coinanalysis.repository.ChatRepository;
import bangbit.in.coinanalysis.repository.HistoryRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Nagarajan on 3/7/2018.
 */

public class ChatActivityPresenter implements ChatActivityContract.UserActionsListener {



    public ChatActivityPresenter() {
    }

}
