package bangbit.in.coinanalysis.ChatModule.ChatFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

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
import bangbit.in.coinanalysis.CryptoToFiat.CryptoToFiatActivity;
import bangbit.in.coinanalysis.MyApplication;
import bangbit.in.coinanalysis.NetworkCall.LambdaApiInterface;
import bangbit.in.coinanalysis.NetworkCall.LambdaApiService;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.Currency;
import bangbit.in.coinanalysis.pojo.Exchange;
import bangbit.in.coinanalysis.pojo.LambdaResponse;
import bangbit.in.coinanalysis.repository.ChatRepository;
import bangbit.in.coinanalysis.repository.HistoryRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static bangbit.in.coinanalysis.MyApplication.currencyMultiplyingFactor;
import static bangbit.in.coinanalysis.Util.getTwoDecimalPointValue;

public class ChatFragmentPresenter implements AIListener, ChatFragmentContract.UserActionsListener {

    private static final String TAG = ChatFragmentPresenter.class.getSimpleName();
    private final ChatRepository chatRepository;
    private final AIService aiService;
    private final AIDataService aiDataService;
    private final List<ChatData> chatData = new ArrayList<>();
    boolean isAskingDays = false;
    List<AIContext> contextArrayList;

    private final ChatFragmentContract.View view;
    private final HistoryRepository historyRepository;
    String coin;
    boolean isAskingDetail;
    private Context context;

    public ChatFragmentPresenter(ChatFragmentContract.View view, Context context, ChatRepository chatRepository, AIService aiService, AIDataService aiDataService) {
        this.chatRepository = chatRepository;
        this.aiService = aiService;
        this.view = view;
        this.context = context;
        this.aiDataService = aiDataService;
        historyRepository = new HistoryRepository();
        aiService.setListener(this);
    }

    @Override
    public void loadInitialChatHistory() {
        List<ChatData> loadFromdb = chatRepository.getChatHistory(1000000000); //@@@
        if (loadFromdb.size() > 0) {
            chatData.addAll(loadFromdb);
            view.loadPreviousData(loadFromdb);
        }
    }

    @Override
    public void loadMoreData(ChatData lastChatData, String time) {
        if (lastChatData != null) {
            List<ChatData> loadFromdb = chatRepository.getChatHistory(lastChatData.getId());
            if (loadFromdb.size() > 0) {
                if (!loadFromdb.get(loadFromdb.size() - 1).getTime().equalsIgnoreCase(time)) {
                    chatData.addAll(loadFromdb);
                    view.loadPreviousData(loadFromdb);
                } else {
                    Log.d(TAG, loadFromdb.get(loadFromdb.size() - 1).getTime() + "    onLoadMore: " + time);
                }
            }
        }
    }

    @Override
    public void scrollToPosition(int position) {
        view.scrollToPosition(position);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public void getQuestionAnswer(String query) {
        if (!query.isEmpty()) {
            if (contextArrayList != null && isAskingDays) {
                try {
                    int days = Integer.parseInt(query);
                    query = query + " days";
                    isAskingDays = false;
                } catch (NumberFormatException e) {

                }
            }
            if (contextArrayList!=null && isAskingDetail){
                query=coin+" "+query;
                isAskingDetail=false;
                coin="";
            }
        }
        Log.d(TAG, "getQuestionAnswer: " + query);
        aiService.startListening();
        final AIRequest aiRequest = new AIRequest();
        if (contextArrayList != null) {
            aiRequest.setContexts(contextArrayList);
        } else {
            aiRequest.setResetContexts(true);
        }
        aiRequest.setQuery(query);

        new AsyncTask<AIRequest, Void, AIResponse>() {
            @Override
            protected AIResponse doInBackground(AIRequest... requests) {
                final AIRequest request = requests[0];
                try {
                    final AIResponse response = aiDataService.request(aiRequest);
                    return response;
                } catch (AIServiceException e) {
                }
                return null;
            }

            @Override
            protected void onPostExecute(AIResponse aiResponse) {
                if (aiResponse != null) {
                    onResult(aiResponse);
                }
            }
        }.execute(aiRequest);
    }

    void correctDurationJsonObject(JsonObject durationDetails)
    {
        if((durationDetails.get("amount").getAsString().compareTo("1")==0 ||
                durationDetails.get("amount").getAsString().compareTo("0")==0 ||
                durationDetails.get("amount").getAsString().compareTo("1.0")==0 ||
                durationDetails.get("amount").getAsString().compareTo("0.0")==0) &&
                durationDetails.get("unit").getAsString().compareTo("day")==0) //@@@
        {
            durationDetails.remove("amount");
            durationDetails.remove("unit");

            durationDetails.addProperty("amount",24);
            durationDetails.addProperty("unit","h");
        }
    }

    String generateJson(Result result) {
        String parameterString = "";
        JSONObject jsonFromDialog = new JSONObject();
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
                try {
                    if(entry.getKey().toLowerCase().compareTo("duration")==0)
                    {
                        if(entry.getValue() instanceof JsonArray ) //@@@
                        {
                            JsonArray duration = (JsonArray)entry.getValue();
                            for(int i=0; i< duration.size();i++) {
                                if(duration.get(i) instanceof JsonObject ) {
                                    JsonObject durationDetails = duration.get(i).getAsJsonObject();
                                    correctDurationJsonObject(durationDetails);
                                }
                            }
                        }
                        if(entry.getValue() instanceof JsonObject )
                        {
                            correctDurationJsonObject((JsonObject)entry.getValue());
                        }
                        jsonFromDialog.put(entry.getKey(), entry.getValue());
                    }
                    else
                    if(entry.getKey().toLowerCase().compareTo("coin")==0)
                    {
                        String value= entry.getValue().getAsString();
                        if(value.compareTo("\"\"")!=0)
                        {
                            value = value.replace("\"\"","\""); //@@@
                        }
                        jsonFromDialog.put(entry.getKey(), value);
                    }
                    else
                    {
                        if( !(entry.getValue() instanceof JsonArray) ) { //@@@
                            String value= entry.getValue().getAsString();
                            if(value.compareTo("\"\"")!=0)
                            {
                                value = value.replace("\"\"","\""); //@@@
                            }
                            jsonFromDialog.put(entry.getKey(), value);
                        }
                        else
                        {
                            jsonFromDialog.put(entry.getKey(), entry.getValue());
                        }
                    }
                } catch (Exception e) {
                    try {
                        jsonFromDialog.put(entry.getKey(), entry.getValue());
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
        String json = jsonFromDialog.toString().replace("\\", ""); //@@@
        json = json.replace("\"[", "["); //@@@
        json = json.replace("]\"", "]"); //@@@

        return json;
    }

    void parseDialogFlowApi(Result result) {
        String json = generateJson(result);

        Log.d(TAG, "onResult: " + json);
        if (!json.equalsIgnoreCase("{}")) {
            try {
                JSONObject jsonObject = new JSONObject(json);
                if (!jsonObject.isNull("coin")) {
                    callLambdaAPi(json);
                }
                else if (!jsonObject.isNull("number") && !jsonObject.isNull("custom_currency") )
                {
                    //Currency Conversion
                    currencyConversion(jsonObject);
                }
                else if (!jsonObject.isNull("miscellaneous") && !jsonObject.isNull("analyze_type") )
                {
                    if(jsonObject.get("miscellaneous") instanceof JSONArray)
                    {
                        JSONArray miscellaneous_list = (JSONArray)jsonObject.get("miscellaneous");
                        int i = 0;
                        for (; i < miscellaneous_list.length(); ) {
                            if(miscellaneous_list.getString(i).toLowerCase().trim().compareTo("coin")==0 ||
                                    miscellaneous_list.getString(i).toLowerCase().trim().compareTo("ico")==0)
                            {
                                callLambdaAPi(json);
                                break;
                            }
                            i++;
                        }
                        if(i==miscellaneous_list.length()) view.addReceiverMessage("Sorry, can you rephrase it better?");
                    }
                    else
                    if(jsonObject.get("miscellaneous") instanceof JsonArray) {
                        JsonArray miscellaneous_list = (JsonArray)jsonObject.get("miscellaneous");
                        int i = 0;
                        for (; i < miscellaneous_list.size(); ) {
                            if(miscellaneous_list.get(i).toString().toLowerCase().trim().compareTo("coin")==0 ||
                                    miscellaneous_list.get(i).toString().toLowerCase().trim().compareTo("ico")==0)
                            {
                                callLambdaAPi(json);
                                break;
                            }
                            i++;
                        }
                        if(i==miscellaneous_list.size()) view.addReceiverMessage("Sorry, can you rephrase it better?");
                    }
                }
                else if (!jsonObject.isNull("miscellaneous") )
                {
                    if(jsonObject.get("miscellaneous") instanceof JSONArray)
                    {
                        JSONArray miscellaneous_list = (JSONArray)jsonObject.get("miscellaneous");
                        if(miscellaneous_list.length()==1 && miscellaneous_list.getString(0).toLowerCase().contains("help"))
                        {
                            //Launch chatbot help fragment
                            view.showChatBotHelp();
                        }
                        else
                        {
                            showNotAbleToIntercept();
                        }
                    }
                    else
                    if(jsonObject.get("miscellaneous") instanceof JsonArray) {
                        JsonArray miscellaneous_list = (JsonArray)jsonObject.get("miscellaneous");
                        if(miscellaneous_list.size()==1 && miscellaneous_list.get(0).toString().toLowerCase().contains("help"))
                        {
                            //Launch chatbot help fragment
                            view.showChatBotHelp();
                        }
                        else
                        {
                            showNotAbleToIntercept();
                        }
                    }
                }
                else {
                    showNotAbleToIntercept();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                //view.addServerNotResponding();
                int random= new Random().nextInt(5);
                switch (random)
                {
                    case 0:
                        view.addReceiverMessage("Oops I missed it, say that again.");
                        break;
                    case 1:
                        view.addReceiverMessage("Sorry, something went wrong. Try again.");
                        break;
                    case 2:
                        view.addReceiverMessage("Sorry, I don't have an answer now. Try sometime later.");
                        break;
                    case 3:
                        view.addReceiverMessage("I'm little exhausted finding that. Ask something else or try later.");
                        break;
                    case 4:
                    default:
                        view.addReceiverMessage("Try after few seconds, I'm stepping out for some water.");
                }
            }
        } else {
            //@@@Handle it
            view.addReceiverMessage("Sorry, I didn't get that.");
        }
    }

    void showNotAbleToIntercept()
    {
        int random = new Random().nextInt(3);
        switch (random) {
            case 0:
                view.addReceiverMessage("Sorry, I didn't get that.");
                break;
            case 1:
                view.addReceiverMessage("Sorry, can you rephrase it better?");
                break;
            case 2:
            default:
                view.addReceiverMessage("Sorry, can you be little more specific?");
        }
    }

    boolean checkSpeech(Result result) {
        if (result.getFulfillment().getSpeech().toLowerCase().contains("which coin") ||
                result.getFulfillment().getSpeech().toLowerCase().contains("what is the coin")) {
            List<AIContext> contexts = new ArrayList<>();
            for (AIOutputContext a :
                    result.getContexts()) {
                contexts.add(new AIContext(a.getName()));
            }
            contextArrayList = contexts;
            ArrayList<String> sugessionArrayList = new ArrayList<>();
            sugessionArrayList.add("BTC");
            sugessionArrayList.add("ETH");
            sugessionArrayList.add("LTC");
            sugessionArrayList.add("XRP");
            sugessionArrayList.add("EOS");
            sugessionArrayList.add("DASH");

            view.addReceiverMessageWithSugession("Which coin?", sugessionArrayList);
            return true;
        }
        if (result.getFulfillment().getSpeech().toLowerCase().contains("how many days")) {
            if(result.getContexts().size()>0)
            {
                Map<String, JsonElement> mapData = result.getContexts().get(0).getParameters();
                JsonElement detailJsonElement = mapData.get("detail_list");
                if (detailJsonElement != null) {
                    String detail_list = detailJsonElement.toString().toLowerCase(); //@@@
                    if (detail_list.contains("chart") || detail_list.contains("historical")) {
                        List<AIContext> contexts = new ArrayList<>();
                        for (AIOutputContext a :
                                result.getContexts()) {
                            contexts.add(new AIContext(a.getName()));
                        }
                        isAskingDays = true;
                        contextArrayList = contexts;
                        ArrayList<String> sugessionArrayList = new ArrayList<>();
                        sugessionArrayList.add("7 days");
                        sugessionArrayList.add("15 days");
                        sugessionArrayList.add("1 month");
                        sugessionArrayList.add("6 months");
                        sugessionArrayList.add("1 year");
                        view.addReceiverMessageWithSugession("How many days?", sugessionArrayList);
                        return true;
                    } else {
                        contextArrayList = null;
                        return false;
                    }
                }
            }
        }
        if (result.getFulfillment().getSpeech().toLowerCase().contains("select any pair")) {
            List<AIContext> contexts = new ArrayList<>();
            for (AIOutputContext a :
                    result.getContexts()) {
                contexts.add(new AIContext(a.getName()));
            }
            contextArrayList = contexts;
            String json = generateJson(result);

            try {
                JSONObject jsonObject = new JSONObject(json);
                if (!jsonObject.isNull("coin")) {
                    String coinName = jsonObject.getString("coin");
                    view.addExchangeMessage(coinName);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;
        }
        if (result.getFulfillment().getSpeech().toLowerCase().contains("what detail you want")) {
            List<AIContext> contexts = new ArrayList<>();
            for (AIOutputContext a :
                    result.getContexts()) {
                contexts.add(new AIContext(a.getName()));
            }

            contextArrayList = contexts;
            ArrayList<String> sugessionArrayList = new ArrayList<>();
            sugessionArrayList.add("Price");
            sugessionArrayList.add("Marketcap");
            sugessionArrayList.add("Supply");
            sugessionArrayList.add("Chart");
            sugessionArrayList.add("Historical data");
            sugessionArrayList.add("Yearly price trend");
            sugessionArrayList.add("Buy/Sell");

            isAskingDetail=true;
            String json = generateJson(result);

            try {
                JSONObject jsonObject = new JSONObject(json);
                if (!jsonObject.isNull("coin")) {
                    coin= jsonObject.getString("coin");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            view.addReceiverMessageWithSugession("What detail you want?", sugessionArrayList);
            return true;
        }
        return false;
    }

    @Override
    public void insertOneChatData(String date, String time, String chat) {
        chatRepository.insertOneChatData(date, time, chat);
    }

    void callLambdaAPi(final String json) {
        view.removeServerNotResponding();
        LambdaApiInterface lambdaApiInterface = LambdaApiService.getAPIInterface();
        lambdaApiInterface.getResponse("application/json", "aws-lambda-token", json).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response == null) {
                    view.addServerNotResponding();
                } else {
                    Log.d(TAG, "onResponse: " + response.body());
                    Gson gson = new GsonBuilder().create();
                    Type type = new TypeToken<LambdaResponse>() {
                    }.getType();
                    LambdaResponse lambdaResponse = gson.fromJson(response.body(), type);
                    if (lambdaResponse.getData() != null) {
                        Collections.reverse(lambdaResponse.getData());
                    }
                    if (lambdaResponse.getSymbol() != null) {
                        view.addReceiverMessage(lambdaResponse, json);
                    }else {
                        if (lambdaResponse.getData() != null && lambdaResponse.getData().size() > 0) {
                            view.addReceiverMessage(lambdaResponse, json);
                        }else if (lambdaResponse.getAnalysis() != null && lambdaResponse.getAnalysis().size() > 0) {
                            view.addAnalysisData(lambdaResponse, json);
                            //view.addReceiverMessage("records count:" + String.valueOf(lambdaResponse.getAnalysis().size()));
                        }
                        else
                        {
                            view.addReceiverMessage("Sorry, no data available.");
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<String> call, Throwable t) {
                //callLambdaAPi(json);
                view.addServerNotResponding();
                Log.d(TAG, "addServerNotRespondingonFailure: " + t.getMessage());
            }
        });
    }

    @Override
    public void onResult(AIResponse response) {
        Result result = response.getResult();
        String message;
        if (result.getMetadata().getIntentName().equalsIgnoreCase("Default Fallback Intent")
                || result.getMetadata().getIntentName().equalsIgnoreCase("Default Welcome Intent")) {
            if (result.getFulfillment().getMessages() != null && result.getFulfillment().getMessages().size() > 0) {
                ResponseMessage.ResponseSpeech speech = (ResponseMessage.ResponseSpeech) result.getFulfillment().getMessages().get(0);
                message = speech.getSpeech().get(0);
                view.addReceiverMessage(message);
            } else {
                view.addReceiverMessage("Sorry, I didn't get that.");
            }
            return;
        }

//        if (result.getMetadata().getIntentName().contains("details - custom")) {
//
//            parseDetail(result);
//        }
        if (checkSpeech(result)) {
            return;
        }
        contextArrayList = null;

        Log.d(TAG, "onResult: action" + result.getAction());
        if (result.getAction().equals("exchange")) {
            String json = generateJson(result);

            try {
                JSONObject jsonObject = new JSONObject(json);
                if (!jsonObject.isNull("coin")) {
                    String coinName = jsonObject.getString("coin");
                    view.addExchangeMessage(coinName);
                }
                else
                {
                    view.addReceiverMessage("Sorry, I didn't get that. What is the coin?");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            parseDialogFlowApi(result);
        }
    }

    private void currencyConversion( JSONObject jsonObject )
    {
        try {
            String conversionValue="";
            Double number = -1.0;
            number = jsonObject.getDouble("number");
            if(number==-1.0)
            {
                number=1.0;
            }
            else
            {
                number = Math.abs(number);
            }

            String currencyFrom="BTC", currencyTo =  ((MyApplication) this.context.getApplicationContext()).getCurrency().getSymbol();
            String cryptoCurrencySymbol="BTC",fiatCurrencySymbol=currencyTo;

            if(jsonObject.get("custom_currency") instanceof JSONArray)
            {
                JSONArray currencies = (JSONArray)jsonObject.get("custom_currency");
                if(currencies.length()==1)
                {
                    currencyFrom = currencies.get(0).toString();
                }
                if(currencies.length()==2)
                {
                    currencyFrom = currencies.get(0).toString();
                    currencyTo = currencies.get(1).toString();
                }
            }
            else
            if(jsonObject.get("custom_currency") instanceof JsonArray)
            {
                JsonArray currencies = (JsonArray)jsonObject.get("custom_currency");
                if(currencies.size()==1)
                {
                    currencyFrom = currencies.get(0).getAsString(); //@@@
                }
                if(currencies.size()==2)
                {
                    currencyFrom = currencies.get(0).getAsString(); //@@@
                    currencyTo = currencies.get(1).getAsString(); //@@@
                }
            }

            String symbol=Util.isFiatCurrency(currencyFrom);
            Boolean isFiatcurrencyFrom = symbol.contains("1:");

            symbol=Util.isFiatCurrency(currencyTo);
            Boolean isFiatcurrencyTo = symbol.contains("1:");

            ArrayList<Coin> coinArrayList = new ArrayList<>();
            if (((MyApplication) this.context.getApplicationContext()).coinArrayList != null) {
                coinArrayList.addAll(((MyApplication) this.context.getApplicationContext()).coinArrayList);
            }
            HashMap<String, Float> currencyMultiplyingFactorMap = new HashMap<>();
            currencyMultiplyingFactorMap = MyApplication.currencyMultiplyingFactorMap;

            Double OneUnitOfCryptoInFiat = 0.0;
            if((isFiatcurrencyFrom==true && isFiatcurrencyTo==false) ||
                    (isFiatcurrencyFrom==false && isFiatcurrencyTo==true))
            {
                if(isFiatcurrencyFrom==false)
                {
                    cryptoCurrencySymbol = currencyFrom;
                    fiatCurrencySymbol = currencyTo;
                }
                else
                {
                    cryptoCurrencySymbol = currencyTo;
                    fiatCurrencySymbol = currencyFrom;
                }

                float factor = 1;
                if (currencyMultiplyingFactorMap.get(fiatCurrencySymbol) != null) {
                    factor = currencyMultiplyingFactorMap.get(fiatCurrencySymbol);
                }else if(currencyMultiplyingFactorMap.get("USD") != null) {
                    factor = currencyMultiplyingFactorMap.get(fiatCurrencySymbol);
                    if(isFiatcurrencyFrom==true) {
                        currencyFrom = "USD";
                    }
                    else
                    {
                        currencyTo ="USD";
                    }
                }
                else
                {
                    factor= MyApplication.currencyMultiplyingFactor;
                }

                //Crypto<->Fiat
                for (Coin coin : coinArrayList) {
                    if (coin.getSymbol().toLowerCase().equals(cryptoCurrencySymbol.toLowerCase())) {
                        OneUnitOfCryptoInFiat = Double.parseDouble(coin.getPriceUsd()) * factor;
                        break;
                    }
                }

                if(isFiatcurrencyFrom==false)
                {
                    conversionValue = Util.checkForNull(getTwoDecimalPointValue(String.valueOf(OneUnitOfCryptoInFiat * number)));
                }
                else
                {
                    conversionValue = Util.checkForNull(getTwoDecimalPointValue(String.valueOf( ( 1.0 / OneUnitOfCryptoInFiat) * number)));
                }
            }
            else  if((isFiatcurrencyFrom==true && isFiatcurrencyTo==true)) //Fiat<->Fiat
            {
                float factorFrom = 1;
                float factorTo = 1;
                if (currencyMultiplyingFactorMap.get(currencyFrom) != null) {
                    factorFrom = currencyMultiplyingFactorMap.get(currencyFrom);
                }

                if(currencyMultiplyingFactorMap.get(currencyTo) != null) {
                    factorTo = currencyMultiplyingFactorMap.get(currencyTo);
                }

                conversionValue = Util.checkForNull(getTwoDecimalPointValue(String.valueOf( ( factorTo / factorFrom) * number)));
            }
            else  if((isFiatcurrencyFrom==false && isFiatcurrencyTo==false)) //Crypto<->Crypto
            {
                float factorFrom = 1;
                float factorTo = 1;

                for (Coin coin : coinArrayList) {
                    if (coin.getSymbol().toLowerCase().equals(currencyFrom.toLowerCase())) {
                        factorFrom = Float.parseFloat(coin.getPriceUsd());
                        break;
                    }
                }

                for (Coin coin : coinArrayList) {
                    if (coin.getSymbol().toLowerCase().equals(currencyTo.toLowerCase())) {
                        factorTo = Float.parseFloat(coin.getPriceUsd());
                        break;
                    }
                }

                conversionValue = Util.checkForNull(getTwoDecimalPointValue(String.valueOf( ( factorFrom / factorTo ) * number)));
            }

            if(conversionValue.compareTo("")==0)
            {
                view.addReceiverMessage("Sorry, I don't have an answer.");
            }
            else
            {
                view.addReceiverMessage(number.toString()+ " "+currencyFrom.toUpperCase()+ " = "+ conversionValue + " "+ currencyTo.toUpperCase());
            }
        } catch (Exception e) {
            view.addReceiverMessage("Sorry, something went wrong. Try again.");
        }
    }

    @Override
    public void getExchangeData(final String coinName, final String currency) {
        historyRepository.getExchangeData(coinName, currency, new HistoryRepository.ExchangeDataCallBack() {
            @Override
            public void onRetrieveExchangeData(ArrayList<Exchange> exchanges, boolean isSuccess, boolean isDataFound) {
                if (isSuccess && exchanges != null) {
                    view.addExchangeData(exchanges, coinName,currency);
                    for (Exchange exchange :
                            exchanges) {
                        Log.d(TAG, "onRetrieveExchangeData: " + exchange.getMARKET());
                    }
                } else {
                    view.addReceiverMessage("Sorry, no data available.");
                }
            }
        });
    }

    @Override
    public void onError(AIError error) {
    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {
    }

    @Override
    public void onListeningCanceled() {
    }

    @Override
    public void onListeningFinished() {
    }

    public List<ChatData> getChatData() {
        return chatData;
    }
}
