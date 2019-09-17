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

import bangbit.in.coinanalysis.Chat.ChatData;

import bangbit.in.coinanalysis.Dialogs.WhereToBuyDialog;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.WhereToBuy.ItemClickListner;
import bangbit.in.coinanalysis.WhereToBuy.WhereToBuyAdapter;
import bangbit.in.coinanalysis.WhereToBuy.WhereToBuyFragment;
import bangbit.in.coinanalysis.pojo.CurrencyExchange;
import bangbit.in.coinanalysis.pojo.Exchange;

/**
 * A simple {@link Fragment} subclass.
 */
public class ChatDetailWhereToBuyFragment extends Fragment implements ItemClickListner{

    private Context context;
    private static final String ARG_PARAM1 = "param1";
    private ChatData chatData;
    private ArrayList<Exchange> exchanges;
    private RecyclerView recyclerView;
    ArrayList<CurrencyExchange> currencyExchangeList = Util.getCurrencyExchangeList();

    public ChatDetailWhereToBuyFragment() {
        // Required empty public constructor
    }

    public static ChatDetailWhereToBuyFragment newInstance(ChatData chatData) {
        Bundle args = new Bundle();
        ChatDetailWhereToBuyFragment fragment = new ChatDetailWhereToBuyFragment();
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
            exchanges = chatData.getExchangeArrayList();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_detail_where_to_buy, null, false);
        TextView exchangeDetailTextView=view.findViewById(R.id.exchangeDetailTextView);
        String  message="Below are the few of the exchanges to trade "+chatData.getMessage().toUpperCase()+ " with " + chatData.getExchangeCurrency().toUpperCase();
        exchangeDetailTextView.setText(message);
        recyclerView = view.findViewById(R.id.where_to_buy_recycleView);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

        String currency = chatData.getExchangeCurrency();
        String symbol=Util.isFiatCurrency(currency);
        Boolean isFiat = symbol.contains("1:");
        symbol = symbol.substring(2);

        WhereToBuyFragment.symbol = symbol;
        WhereToBuyFragment.isCurrencyFiat = isFiat;

        WhereToBuyAdapter whereToBuyAdapter = new WhereToBuyAdapter(exchanges, this);

        recyclerView.setAdapter(whereToBuyAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        TextView timeDataTextView = view.findViewById(R.id.time_date_textView);
        String dateTime = "" + chatData.getDate() + " " + chatData.getTime();
        timeDataTextView.setText(dateTime);
        return view;
    }

    @Override
    public void itemClick(String marketname) {
        boolean isUrlAvailable = false;
        for (CurrencyExchange currencyExchange :
                currencyExchangeList) {
            if (currencyExchange.getName().toLowerCase().equals(marketname.toLowerCase())) {
                isUrlAvailable = true;
                WhereToBuyDialog whereToBuyDialog = new WhereToBuyDialog(getActivity());
                whereToBuyDialog.showDialog(currencyExchange);
                break;
            }
        }
        if (!isUrlAvailable) {
           Snackbar mySnackbar = Snackbar.make(getView(), "Apologize. We are not able to find the official website for this exchange."
                    , Snackbar.LENGTH_LONG);
            View snackbarView = mySnackbar.getView();
            TextView textViewSnackbar = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textViewSnackbar.setTextAppearance(getContext(), R.style.regular_16_white);
            mySnackbar.show();
        }
    }
}
