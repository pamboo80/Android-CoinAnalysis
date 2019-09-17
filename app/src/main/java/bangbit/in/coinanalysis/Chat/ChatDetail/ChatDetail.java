package bangbit.in.coinanalysis.Chat.ChatDetail;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import bangbit.in.coinanalysis.Chat.ChatData;
import bangbit.in.coinanalysis.Constant;
import bangbit.in.coinanalysis.MyApplication;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.LambdaResponse;
import bangbit.in.coinanalysis.pojo.LambdaYearlyTrend;

import static bangbit.in.coinanalysis.Constant.TEXT_MARKETCAP;
import static bangbit.in.coinanalysis.Constant.TEXT_PERCENTAGE;
import static bangbit.in.coinanalysis.Constant.TEXT_SUPPLY;
import static bangbit.in.coinanalysis.Constant.TEXT_VOLUME;
import static bangbit.in.coinanalysis.MyApplication.currencySymbol;
import static bangbit.in.coinanalysis.Util.checkForNull;
import static bangbit.in.coinanalysis.Util.getDayMonthYearHHMMSSFromUnixTime;
import static bangbit.in.coinanalysis.Util.multiplyWithCurrency;
import static bangbit.in.coinanalysis.Util.trimTrailingZeros;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatDetail extends Fragment {

    private static final String CHAT_DATA = "CHAT_DATA";
    private static final String TYPE = "TYPE";
    private Context context;

    private ChatData chatData;
    private int type;
    private LinearLayout linearLayout;
    private LambdaResponse lambdaResponse;
    private String pricebtc;

    public ChatDetail() {
        // Required empty public constructor
    }

    public static ChatDetail newInstance(ChatData chatData, int type) {
        ChatDetail fragment = new ChatDetail();
        Bundle args = new Bundle();
        args.putParcelable(CHAT_DATA, chatData);
        args.putInt(TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        if (getArguments() != null) {
            chatData = getArguments().getParcelable(CHAT_DATA);
            type = getArguments().getInt(TYPE);
            lambdaResponse = chatData.getLambdaResponse();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_chat_detail, container, false);

        TextView timeDataTextView = view.findViewById(R.id.time_date_textView);
        String dateTime = "" + chatData.getDate() + " " + chatData.getTime();
        timeDataTextView.setText(dateTime);

        linearLayout = view.findViewById(R.id.detailLenearLayout);
        parseDetial();
        return view;
    }

    private void parseDetial() {
        if (getCoinDetail(type) != null) {
            linearLayout.addView(getCoinDetail(type));
        }
    }

    View getCoinDetail(int type) {
        String message = "";
        View view = LayoutInflater.from(context).inflate(R.layout.response_text, null, false);
        TextView textView = view.findViewById(R.id.answerTextView);
        switch (type) {
            case Constant.TEXT_SENDER:
                view = LayoutInflater.from(context).inflate(R.layout.sender_view_holder, null, false);
                TextView textView1 = view.findViewById(R.id.text_message_body);
                textView1.setTextIsSelectable(true);
                textView1.setSelectAllOnFocus(true);
                textView1.setText(chatData.getMessage());
                return view;
            case Constant.TEXT_RECEIVER_MESSAGE:
                textView.setText(chatData.getMessage());
                return view;
            case Constant.COIN_YEARLY_PRICE_TREND:
                if (lambdaResponse != null) {
                    view = getYearlyPriceTrendView(chatData);
                    return view;
                }
            case Constant.COIN_DETAIL:
                if (lambdaResponse != null) {
                    view = getDetailView(chatData);
                    return view;
                }
        }
        return null;
    }

    View getYearlyPriceTrendView(String label, String Value) {
        String message;
        View view = LayoutInflater.from(context).inflate(R.layout.response_yearly_trend_detail_row, null, false);
        TextView lableTextView = view.findViewById(R.id.lableTextView);
        TextView contentTextView = view.findViewById(R.id.contentTextView);
        lableTextView.setText(label);
        contentTextView.setText(Value);
        return view;
    }

    @SuppressLint("SetTextI18n")
    View getYearlyPriceTrendView(ChatData chatData) {
        View view = LayoutInflater.from(context).inflate(R.layout.response_yearly_price_trend_view, null, false);
        if (lambdaResponse == null) {
            return null;
        }
        ImageView coinImageView = view.findViewById(R.id.coinImageView);
        Picasso.with(context).load(Util.getCoinUrl(lambdaResponse.getSymbol(), context)).placeholder(R.drawable.default_coin).into(coinImageView);
        TextView priceLableTextView = view.findViewById(R.id.price_lable_textView);
//                priceLableTextView.setText(getResources().getString(R.string.price)+" ["+currencyNameSymbol+"] :");
        TextView coinNameTextView = view.findViewById(R.id.textCoinName);

        coinNameTextView.setText(lambdaResponse.getCoinName() + " (" + lambdaResponse.getSymbol() + ")");
        List<LambdaYearlyTrend> listLambdaYearlyTrend = null;
        if(chatData.getLambdaResponse()!=null)
        {
            listLambdaYearlyTrend = chatData.getLambdaResponse().getYearlyTrend();
        }

        LinearLayout detialLinearLayout = view.findViewById(R.id.yearlyPriceDetailHolder);

        if(listLambdaYearlyTrend!=null && listLambdaYearlyTrend.size()>0)
        {
            try
            {
                for(int i=0;i<lambdaResponse.getYearlyTrend().size();i++)
                {
                    String price = multiplyWithCurrency(lambdaResponse.getYearlyTrend().get(i).getHistoryPrice());
                    price = Util.checkForNull(price);
                    String message = currencySymbol + price;
                    detialLinearLayout.addView(getYearlyPriceTrendView(listLambdaYearlyTrend.get(i).getHistoryPriceDate(),message), 0);
                }
            }
            catch(NullPointerException ex)
            {

            }
        }

        return view;
    }

    @SuppressLint("SetTextI18n")
    View getDetailView(ChatData chatData) {
        View view = LayoutInflater.from(context).inflate(R.layout.response_detail_view, null, false);
        if (lambdaResponse == null) {
            return null;
        }
        ImageView coinImageView = view.findViewById(R.id.coinImageView);
        Picasso.with(context).load(Util.getCoinUrl(lambdaResponse.getSymbol(), context)).placeholder(R.drawable.default_coin).into(coinImageView);
        TextView priceLableTextView = view.findViewById(R.id.price_lable_textView);
//                priceLableTextView.setText(getResources().getString(R.string.price)+" ["+currencyNameSymbol+"] :");
        TextView coinNameTextView = view.findViewById(R.id.textCoinName);
        TextView priceBTCTextView = view.findViewById(R.id.price_btc);
        TextView priceUSDTextView = view.findViewById(R.id.price_usd);
        TextView hour24VolumeTextView = view.findViewById(R.id.hour_24_volume);
        TextView mCapTextView = view.findViewById(R.id.mcap);
        TextView availableSupplyTextView = view.findViewById(R.id.available_supply);
        TextView totalSupplyTextView = view.findViewById(R.id.total_supply);
        TextView maxSupplyTextView = view.findViewById(R.id.max_supply);
        TextView change1hourTextView = view.findViewById(R.id.change_1_hour);
        TextView change24hoursTextView = view.findViewById(R.id.change_24_hour);
        TextView change7daysTextView = view.findViewById(R.id.change_7_days);
        TextView lastUpdatedTextView = view.findViewById(R.id.last_updated);
        TextView rankTextView = view.findViewById(R.id.rank);
        LambdaResponse lambdaResponse = chatData.getLambdaResponse();

        try
        {
            String priceUSd = currencySymbol + getValueInCurrency(Util.getTwoDecimalPointValue(lambdaResponse.getPrice()));
            String volume24Hour = currencySymbol + getValueInCurrency(lambdaResponse.getVolume24h());
            String marketCap = currencySymbol + getValueInCurrency(lambdaResponse.getMarketCap());
            String percentageChange7Day = String.format("%.2f",Double.parseDouble(lambdaResponse.getPercentChange7d())) + " %";
            String percentageChange24Hr = String.format("%.2f",Double.parseDouble(lambdaResponse.getPercentChange24h())) + " %";
            String percentageChange1Hr = String.format("%.2f",Double.parseDouble(lambdaResponse.getPercentChange1h())) + " %";

            String circulatingSupply = checkForNull(lambdaResponse.getCirculatingSupply()) + " " + lambdaResponse.getSymbol();
            String maxSupply = checkForNull(lambdaResponse.getMaxSupply()) + " " + lambdaResponse.getSymbol();
            String totalsupply = checkForNull(lambdaResponse.getTotalSupply()) + " " + lambdaResponse.getSymbol();
            String pricebtc;

            if (lambdaResponse.getPrice_btc() != null && lambdaResponse.getPrice_btc().compareTo("")!=0 ) {
                pricebtc =  checkForNull(trimTrailingZeros(lambdaResponse.getPrice_btc())) + " BTC";
            } else {
                pricebtc = "NA";
            }

            priceUSDTextView.setText(priceUSd);
            hour24VolumeTextView.setText(ifValueZero(volume24Hour));
            mCapTextView.setText(ifValueZero(marketCap));
            availableSupplyTextView.setText(ifValueZero(circulatingSupply));
            totalSupplyTextView.setText(ifValueZero(totalsupply));
            maxSupplyTextView.setText(ifValueZero(maxSupply));
            priceBTCTextView.setText(pricebtc);

            try {
                float hour1 = Float.parseFloat(lambdaResponse.getPercentChange1h());
                float hour24 = Float.parseFloat(lambdaResponse.getPercentChange24h());
                float day7 = Float.parseFloat(lambdaResponse.getPercentChange7d());
                if (hour1 >= 0) {
                    change1hourTextView.setTextColor(getResources().getColor(R.color.green));
                } else {
                    change1hourTextView.setTextColor(getResources().getColor(R.color.red));
                }
                if (hour24 >= 0) {
                    change24hoursTextView.setTextColor(getResources().getColor(R.color.green));
                } else {
                    change24hoursTextView.setTextColor(getResources().getColor(R.color.red));
                }
                if (day7 >= 0) {
                    change7daysTextView.setTextColor(getResources().getColor(R.color.green));
                } else {
                    change7daysTextView.setTextColor(getResources().getColor(R.color.red));
                }
            } catch (NumberFormatException ex) {
            }

            change1hourTextView.setText(percentageChange1Hr);
            change24hoursTextView.setText(percentageChange24Hr);
            change7daysTextView.setText(percentageChange7Day);
            coinNameTextView.setText(lambdaResponse.getCoinName() + " (" + lambdaResponse.getSymbol() + ")");

            if(lambdaResponse.getLastUpdated()!=null)
            {
                lastUpdatedTextView.setText(getDayMonthYearHHMMSSFromUnixTime(Long.parseLong(lambdaResponse.getLastUpdated())));
            }
            if(lambdaResponse.getRank()!=null)
            {
                rankTextView.setText(ifValueZero(lambdaResponse.getRank()));
            }
        }
        catch(NullPointerException ex)
        {

        }

        return view;
    }

    String getValueInCurrency(String value) {
        value = Util.multiplyWithCurrency(value);
        value = checkForNull(value);
        return value;
    }

    String ifValueZero(String value)
    {
        value= value.trim();
        if(value.compareTo("")==0 || value.startsWith("0 ")
                || value.startsWith("0.0 ") || value.startsWith("0.00 ")
                || value.startsWith("00 ") || value.contains("NA "))
        {
            return "NA";
        }
        return value;
    }

}
