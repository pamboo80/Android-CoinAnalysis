package bangbit.in.coinanalysis.ChatModule;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import bangbit.in.coinanalysis.Chat.ChatData;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;

import static bangbit.in.coinanalysis.Constant.TEXT_RECEIVER_MESSAGE;

public class ExchangeSugessionViewHolder extends RecyclerView.ViewHolder {

    private final Context context;
    private final LinearLayout linearLayout;
    private final HolderView chatView;
    private final TextView questionTextView;
    boolean isBind = false;
    private ChatData chatData;

    public ExchangeSugessionViewHolder(View itemView, HolderView chatView) {
        super(itemView);
        linearLayout = itemView.findViewById(R.id.exchangeLinearLayout);
        questionTextView = itemView.findViewById(R.id.questionTextView);
        context = itemView.getContext();
        this.chatView = chatView;
    }

    void bind(ChatData chatData) {
        this.chatData=chatData;
        linearLayout.removeAllViews();
        String coinName=chatData.getMessage();
        ArrayList<String> sugessionArrayList = Util.getPairCurrencyList();
        sugessionArrayList.remove(coinName.toUpperCase());
        addSugession(sugessionArrayList, coinName);
        isBind = true;
    }

    void addSugession(ArrayList<String> sugessionArrayList, String coinName) {
        View sugessionView = LayoutInflater.from(context).inflate(R.layout.response_sugession_text, null, false);
        final String message = "Select the currency pair for trading " + coinName.trim().toUpperCase();
        questionTextView.setText(message);
        questionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    ChatData chatDatanew =(ChatData)chatData.clone();
                    chatDatanew.setMessage(message);
                    chatView.showDetail(chatDatanew,TEXT_RECEIVER_MESSAGE);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        });

        RecyclerView recyclerView = sugessionView.findViewById(R.id.sugessionRecycleView);

        SugessionAdapter sugessionAdapter = new SugessionAdapter(sugessionArrayList, chatView, coinName);
        recyclerView.setAdapter(sugessionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        linearLayout.addView(sugessionView);
    }
}
