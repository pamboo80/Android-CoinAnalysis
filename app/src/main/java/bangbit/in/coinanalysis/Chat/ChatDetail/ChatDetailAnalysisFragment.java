package bangbit.in.coinanalysis.Chat.ChatDetail;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bangbit.in.coinanalysis.AnalysisAdapter;
import bangbit.in.coinanalysis.Chat.ChatData;

import bangbit.in.coinanalysis.Dialogs.WhereToBuyDialog;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.WhereToBuy.ItemClickListner;
import bangbit.in.coinanalysis.WhereToBuy.WhereToBuyAdapter;
import bangbit.in.coinanalysis.WhereToBuy.WhereToBuyFragment;
import bangbit.in.coinanalysis.pojo.CurrencyExchange;
import bangbit.in.coinanalysis.pojo.Exchange;
import bangbit.in.coinanalysis.pojo.LambdaAnalysis;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatDetailAnalysisFragment extends Fragment implements ItemClickListner{

    private Context context;
    private static final String ARG_PARAM1 = "param1";
    private ChatData chatData;
    private ArrayList<Exchange> exchanges;
    private RecyclerView recyclerView;
    List<LambdaAnalysis> analysisList;

    public ChatDetailAnalysisFragment() {
        // Required empty public constructor
    }

    public static ChatDetailAnalysisFragment newInstance(ChatData chatData) {
        Bundle args = new Bundle();
        ChatDetailAnalysisFragment fragment = new ChatDetailAnalysisFragment();
        args.putParcelable(ARG_PARAM1, chatData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        if (getArguments() != null) {
            chatData = getArguments().getParcelable(ARG_PARAM1);
            analysisList = chatData.getLambdaResponse().getAnalysis();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_detail_analysis, null, false);
        TextView analysisDetailTextView=view.findViewById(R.id.AnalysisDetailTextView);
        analysisDetailTextView.setText("Below are the cryptocurrencies listed based on the percentage changes of their price values from "
                + chatData.getLambdaResponse().getFromDate()+ " to " + chatData.getLambdaResponse().getToDate());
        recyclerView = view.findViewById(R.id.analysis_recycleView);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        AnalysisAdapter analysisAdapter = new AnalysisAdapter(analysisList, this);
        recyclerView.setAdapter(analysisAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);

        TextView timeDataTextView = view.findViewById(R.id.time_date_textView);
        String dateTime = "" + chatData.getDate() + " " + chatData.getTime();
        timeDataTextView.setText(dateTime);
        return view;
    }

    @Override
    public void itemClick(String marketname) {

    }
}
