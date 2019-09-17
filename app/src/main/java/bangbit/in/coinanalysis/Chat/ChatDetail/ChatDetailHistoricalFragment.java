package bangbit.in.coinanalysis.Chat.ChatDetail;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import bangbit.in.coinanalysis.Chat.ChatData;
import bangbit.in.coinanalysis.Chat.HistoricalDataAdapter;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.LambdaResponse;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatDetailHistoricalFragment extends Fragment {
    private Context context;
    private static final String ARG_PARAM1 = "param1";
    private ChatData chatData;
    private LambdaResponse lambdaResponse;

    public ChatDetailHistoricalFragment() {
        // Required empty public constructor
    }

    public static ChatDetailHistoricalFragment newInstance(ChatData chatData) {
        Bundle args = new Bundle();
        ChatDetailHistoricalFragment fragment = new ChatDetailHistoricalFragment();
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
            lambdaResponse = chatData.getLambdaResponse();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = LayoutInflater.from(context).inflate(R.layout.chat_detail_history, null, false);
        ImageView coinImageView = view.findViewById(R.id.coinImageView);
        Picasso.with(context).load(Util.getCoinUrl(lambdaResponse.getSymbol(), context)).placeholder(R.drawable.default_coin).into(coinImageView);

        TextView contentTextView = view.findViewById(R.id.contentTextView);
        TextView timeDataTextView = view.findViewById(R.id.time_date_textView);
        String dateTime = "" + chatData.getDate() + " " + chatData.getTime();
        timeDataTextView.setText(dateTime);

        String days = "";
        if (lambdaResponse.is24Hrs()){
            days=lambdaResponse.getData().size()+((lambdaResponse.getData().size()<=1)? " hour":" hours");
        }else {
            if (lambdaResponse.getData() != null) {
                days = Util.getDayORMonthORYear(lambdaResponse.getData().size());
            }
        }

        String contentText= lambdaResponse.getCoinName() + " (" + lambdaResponse.getSymbol() + ") past " + days + " historical data";

        contentTextView.setText(contentText);
        RecyclerView historicalDataRecyclerView = view.findViewById(R.id.historicalDataRecycleView);
        HistoricalDataAdapter historicalDataAdapter = new HistoricalDataAdapter(lambdaResponse.getData(),lambdaResponse.is24Hrs());
        historicalDataRecyclerView.setAdapter(historicalDataAdapter);

        historicalDataRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        historicalDataRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        return view;
    }

}
