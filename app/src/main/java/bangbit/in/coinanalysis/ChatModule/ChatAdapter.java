package bangbit.in.coinanalysis.ChatModule;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import bangbit.in.coinanalysis.Chat.ChatData;
import bangbit.in.coinanalysis.Chat.ChatDetail.ChatHelpFragment;
import bangbit.in.coinanalysis.ChatModule.ChatFragment.ChatFragmentContract;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.pojo.Exchange;
import bangbit.in.coinanalysis.pojo.LambdaAnalysis;
import bangbit.in.coinanalysis.pojo.LambdaResponse;

public class ChatAdapter extends RecyclerView.Adapter {

    List<ChatData> chatDataArrayList=new ArrayList<>();
    private static final int VIEW_TYPE_RECEIVER = 0;
    private static final int VIEW_TYPE_SENDER = 1;
    private static final int VIEW_TYPE_TYPING = 2;
    private static final int VIEW_TYPE_WELCOME = 3;
    private static final int VIEW_TYPE_EXCHANGE_SUGESSION = 4;
    private static final int VIEW_TYPE_EXCHANGE_DATA = 5;
    private static final int VIEW_TYPE_ANALYSIS_DATA = 6;
    private String TAG = ChatAdapter.class.getSimpleName();
    private Context context;
    private ChatData lastChatData;
    private final ChatFragmentContract.View chatView;

    public ChatAdapter(ChatFragmentContract.View view, List<ChatData> chatDataList) {
        this.chatView = view;
        ChatData welcome = new ChatData();
        welcome.setViewType(VIEW_TYPE_WELCOME);
        chatDataArrayList.add(welcome);
        if (chatDataList != null && chatDataList.size() > 0) {
            for (ChatData chatData : chatDataList) {
                chatDataArrayList.add(chatData);
            }
            lastChatData=chatDataArrayList.get(chatDataArrayList.size()-1);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return chatDataArrayList.get(position).getViewType();
//        if (chatDataArrayList.get(position).isGettingResponse()) {
//            return VIEW_TYPE_TYPING;
//        } else {
//            if (chatDataArrayList.get(position).isSender()) {
//                return VIEW_TYPE_SENDER;
//            } else {
//                return VIEW_TYPE_RECEIVER;
//            }
//        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View view;
        switch (viewType) {
            case VIEW_TYPE_SENDER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.sender_view_holder, parent, false);
                return new SenderViewHolder(view, chatView);
            case VIEW_TYPE_RECEIVER:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.receiver_view_holder_main, parent, false);
                return new ReceiverViewHolder(view, chatView);
            case VIEW_TYPE_TYPING:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.response_typing, parent, false);
                return new TypingViewHolder(view, chatView);
            case VIEW_TYPE_WELCOME:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.response_welcome_text, parent, false);
                return new WelcomeViewHolder(view, chatView);
            case VIEW_TYPE_EXCHANGE_SUGESSION:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.response_exchange_suggesion, parent, false);
                return new ExchangeSugessionViewHolder(view, chatView);
            case VIEW_TYPE_EXCHANGE_DATA:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.response_exchange, parent, false);
                return new ExchangeDataViewHolder(view, chatView);
            case VIEW_TYPE_ANALYSIS_DATA:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.response_analysis, parent, false);
                return new AnalysisDataViewHolder(view, chatView);
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ChatData chatData = chatDataArrayList.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_SENDER:
                ((SenderViewHolder) holder).bind(chatData);
                break;
            case VIEW_TYPE_RECEIVER:
                ((ReceiverViewHolder) holder).bind(chatData);
                break;
            case VIEW_TYPE_TYPING:
                ((TypingViewHolder) holder).bind();
                break;
            case VIEW_TYPE_WELCOME:
                ((WelcomeViewHolder) holder).bind(null);
                break;
            case VIEW_TYPE_EXCHANGE_SUGESSION:
                ((ExchangeSugessionViewHolder) holder).bind(chatData);
                break;
            case VIEW_TYPE_EXCHANGE_DATA:
                ((ExchangeDataViewHolder) holder).bind(chatData);
                break;
            case VIEW_TYPE_ANALYSIS_DATA:
                ((AnalysisDataViewHolder) holder).bind(chatData);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return chatDataArrayList.size();
    }

    public String getTime() {
        return chatDataArrayList.get(0).getTime();
    }

    public void addSenderMessage(String message) {
        ChatData chatData = new ChatData();
        chatData.setViewType(VIEW_TYPE_SENDER);
        chatData.setMessage(message);
        chatDataArrayList.add(0, chatData);
        addTypingMessage();
        notifyDataSetChanged();
        chatView.messageInserted();
        chatView.addToDatabase(chatData);
    }

    public void addExchangeData(ArrayList<Exchange> exchanges, String coinName, String currency) {
        removeTypingView();
        ChatData chatData = new ChatData();
        chatData.setViewType(VIEW_TYPE_EXCHANGE_DATA);
        chatData.setExchangeArrayList(exchanges);
        chatData.setExchangeCurrency(currency);
        chatData.setMessage(coinName);
        chatDataArrayList.add(0, chatData);
        notifyDataSetChanged();
        chatView.messageInserted();
        chatView.addToDatabase(chatData);
    }

    public void addTypingMessage() {
        ChatData chatData = new ChatData();
        chatData.setViewType(VIEW_TYPE_TYPING);
        chatDataArrayList.add(0, chatData);
        chatView.messageInserted();
    }

    public void addReceiverMessage(LambdaResponse lambdaResponse, String dialogFlowResponse) {
        removeTypingView();
        ChatData chatData = new ChatData();
        chatData.setViewType(VIEW_TYPE_RECEIVER);
        chatData.setDialogResponse(dialogFlowResponse);
        chatData.setLambdaResponse(lambdaResponse);
        chatDataArrayList.add(0, chatData);
        notifyDataSetChanged();
        chatView.messageInserted();
        chatView.addToDatabase(chatData);
    }

    public void showChatBotHelp()
    {
        removeTypingView();
        try
        {
            ((ChatActivity) context).loadFragment(new ChatHelpFragment(),"help");
            ((ChatActivity) context).titleTextView.setText("ChatBot Help");
        }
        catch(Exception ex)
        {
            Log.d(TAG, "showChatBotHelp: "+ ex.getMessage());
        }

    }

    public void addReceiverMessage(String message) {
        removeTypingView();
        ChatData chatData = new ChatData();
        chatData.setViewType(VIEW_TYPE_RECEIVER);
        chatData.setDialogResponse("");
        chatData.setLambdaResponse(null);
        chatData.setMessage(message);
        chatDataArrayList.add(0, chatData);
        notifyDataSetChanged();
        chatView.messageInserted();
        chatView.addToDatabase(chatData);
    }

    public void addAnalysisData(LambdaResponse lambdaResponse, String dialogFlowResponse) {
        removeTypingView();
        ChatData chatData = new ChatData();
        chatData.setViewType(VIEW_TYPE_ANALYSIS_DATA);
        chatData.setDialogResponse(dialogFlowResponse);
        chatData.setLambdaResponse(lambdaResponse);
        chatDataArrayList.add(0, chatData);
        notifyDataSetChanged();
        chatView.messageInserted();
        chatView.addToDatabase(chatData);
    }

    public void addExchangeMessage(String message) {
        removeTypingView();
        ChatData chatData = new ChatData();
        chatData.setViewType(VIEW_TYPE_EXCHANGE_SUGESSION);
        chatData.setLambdaResponse(null);
        chatData.setMessage(message);
        chatDataArrayList.add(0, chatData);
        notifyDataSetChanged();
        chatView.messageInserted();
        chatView.addToDatabase(chatData);
    }

    public void removeTypingView() {
        int size=chatDataArrayList.size();
        for (int i = 0; i < size - 1; i++) {
            ChatData chatData = chatDataArrayList.get(i);
            if (chatData.getViewType() == VIEW_TYPE_TYPING) {
                chatDataArrayList.remove(chatData);
                size--;
            }
        }
    }

    public void addReceiverMessageWithSugession(String message, ArrayList<String> sugession) {
        removeTypingView();
        ChatData chatData = new ChatData();
        chatData.setViewType(VIEW_TYPE_RECEIVER);
        chatData.setDialogResponse("");
        chatData.setLambdaResponse(null);
        chatData.setMessage(message);
        chatData.setSugessionArrayList(sugession);
        chatDataArrayList.add(0, chatData);

        chatView.addToDatabase(chatData);

        notifyDataSetChanged();
        chatView.messageInserted();
    }

    public void addNewDataToTop(List<ChatData> chatHistory) {
        for (ChatData chatData : chatHistory) {
            chatDataArrayList.add(chatData);
        }
        lastChatData=chatHistory.get(chatHistory.size()-1);
        android.os.Handler handler=new android.os.Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
    public ChatData getLastItem(){
        return lastChatData;
    }

    public ChatData getChatData(int position) {
        return chatDataArrayList.get(position);
    }
}
