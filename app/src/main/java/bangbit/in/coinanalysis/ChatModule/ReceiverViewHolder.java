package bangbit.in.coinanalysis.ChatModule;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bangbit.in.coinanalysis.Chat.ChatData;
import bangbit.in.coinanalysis.LambdaHistoryComparator;
import bangbit.in.coinanalysis.ListOperations;
import bangbit.in.coinanalysis.MyApplication;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.axisFormator.CurrencyFormatter;
import bangbit.in.coinanalysis.axisFormator.DateMonFormatter;
import bangbit.in.coinanalysis.axisFormator.TimeDateFormatter;
import bangbit.in.coinanalysis.pojo.LambdaHistory;
import bangbit.in.coinanalysis.pojo.LambdaResponse;

import static bangbit.in.coinanalysis.Constant.CHART;
import static bangbit.in.coinanalysis.Constant.COIN_DETAIL;
import static bangbit.in.coinanalysis.Constant.COIN_YEARLY_PRICE_TREND;
import static bangbit.in.coinanalysis.Constant.HISTORY;
import static bangbit.in.coinanalysis.Constant.TEXT_RECEIVER_MESSAGE;
import static bangbit.in.coinanalysis.MyApplication.currencySymbol;
import static bangbit.in.coinanalysis.Util.checkForNull;
import static bangbit.in.coinanalysis.Util.getMillionValue;
import static bangbit.in.coinanalysis.Util.multiplyWithCurrency;

public class ReceiverViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    private final HolderView chatView;
    private String coin;
    private JSONArray detail_list;
    private String days;
    private String currency;
    private String TAG = ReceiverViewHolder.class.getSimpleName();
    private LambdaResponse lambdaResponse;
    LinearLayout linearLayout;
    private final Context context;
    private ChatData chatData;

    public ReceiverViewHolder(View itemView, HolderView chatView) {
        super(itemView);
        context = itemView.getContext();
        linearLayout = itemView.findViewById(R.id.linearLayout);
        this.chatView = chatView;
    }

    void bind(ChatData chatData) {
        this.chatData = chatData;
        LambdaResponse lambdaResponse = chatData.getLambdaResponse();
        String dialogFlowResponse = chatData.getDialogResponse();
        linearLayout.removeAllViews();
        if (lambdaResponse != null) {
            this.lambdaResponse = lambdaResponse;
            try {
                JSONObject jsonObject = new JSONObject(dialogFlowResponse);
                parseData(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.response_text, null, false);
            TextView textView = view.findViewById(R.id.answerTextView);
            textView.setText(chatData.getMessage());
            textView.setOnClickListener(this);

            linearLayout.addView(view);
            if (chatData.getSugessionArrayList() != null) {
                addSugession(chatData.getSugessionArrayList());
            }
        }
    }

    void parseData(JSONObject jsonObject) throws JSONException {

        int day=-1;
        if (!jsonObject.isNull("coin")) {
            coin = jsonObject.getString("coin");
        }
        if (!jsonObject.isNull("detail_list")) {
            detail_list = jsonObject.getJSONArray("detail_list");
            Set<String> stringSet = new HashSet<>();
            for (int i = 0; i < detail_list.length(); i++) {
                stringSet.add(detail_list.getString(i));
            }
            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.create();
            String s = gson.toJson(stringSet);
            detail_list = new JSONArray(s);
            Log.d(TAG, "bind: " + jsonObject.getString("detail_list"));
        }

        if (!jsonObject.isNull("days")) {
            days = jsonObject.getString("days");
            if(days!=null)
            {
                day = Integer.parseInt(days);
            }
        }

        if (!jsonObject.isNull("duration")) {
            if(jsonObject.get("duration") instanceof JsonArray)
            {
                JsonArray duration = (JsonArray)jsonObject.get("duration");
                if(duration.size()>0) day = duration.size();
            }
            else if(jsonObject.get("duration") instanceof JSONArray)
            {
                JSONArray duration = (JSONArray)jsonObject.get("duration");
                if(duration.length()>0) day = duration.length();
            }
        }

        if (!jsonObject.isNull("currency-name")) {
            currency = jsonObject.getString("currency-name");
        }

        if (!jsonObject.isNull("miscellaneous") && !jsonObject.isNull("analyze_type")) {
            if(jsonObject.get("miscellaneous") instanceof JsonArray)
            {
                JsonArray miscellaneous_list = (JsonArray)jsonObject.get("miscellaneous");
                if(miscellaneous_list.size()>0) {
                    if (miscellaneous_list.toString().contains("coin") || miscellaneous_list.toString().contains("ico")) {
                        //AnalysisView //@@@
                    }
                }
            }
            else if(jsonObject.get("miscellaneous") instanceof JSONArray)
            {
                JSONArray miscellaneous_list = (JSONArray)jsonObject.get("miscellaneous");
                if(miscellaneous_list.length()>0) {
                    if (miscellaneous_list.toString().contains("coin") || miscellaneous_list.toString().contains("ico")) {
                        //AnalysisView //@@@
                    }
                }
            }
        }
        else
        if (detail_list != null && detail_list.length() > 0) {
            if (detail_list.toString().contains("chart")) {
                linearLayout.addView(getChart());
            } else if (detail_list.toString().contains("historical data")) {
                linearLayout.addView(getHistorical());
            }
            else if ( (day>0) && detail_list.toString().contains("price")) {
                linearLayout.addView(getHistorical());
            }
            else if( detail_list.toString().contains("price trend"))
            {
                View detailView = LayoutInflater.from(context).inflate(R.layout.response_yearly_price_trend, null, false);
                detailView.setOnClickListener(this);
                TextView contentTextView = detailView.findViewById(R.id.contentTextView);
                contentTextView.setText(lambdaResponse.getCoinName() + " (" + lambdaResponse.getSymbol() + ")");
                ImageView coinImageView = detailView.findViewById(R.id.coinImageView);
                Picasso.with(context).load(Util.getCoinUrl(lambdaResponse.getSymbol(), context)).placeholder(R.drawable.default_coin).into(coinImageView);

                linearLayout.addView(detailView);
                LinearLayout detialLinearLayout = detailView.findViewById(R.id.detailHolder);
                getCoinDetail("price trend", detialLinearLayout);
            }
            else {
                View detailView = LayoutInflater.from(context).inflate(R.layout.response_details, null, false);
                detailView.setOnClickListener(this);
                TextView contentTextView = detailView.findViewById(R.id.contentTextView);
                contentTextView.setText(lambdaResponse.getCoinName() + " (" + lambdaResponse.getSymbol() + ")");
                ImageView coinImageView = detailView.findViewById(R.id.coinImageView);
                Picasso.with(context).load(Util.getCoinUrl(lambdaResponse.getSymbol(), context)).placeholder(R.drawable.default_coin).into(coinImageView);

                linearLayout.addView(detailView);
                LinearLayout detialLinearLayout = detailView.findViewById(R.id.detailHolder);
                for (int i = 0; i < detail_list.length(); i++) {
                    getCoinDetail(detail_list.getString(i), detialLinearLayout);
                }
            }
        }
    }

    void getCoinDetail(String type, LinearLayout detialLinearLayout) {
        String message = "";
        View view = LayoutInflater.from(context).inflate(R.layout.response_detail_row, null, false);
        TextView lableTextView = view.findViewById(R.id.lableTextView);
        TextView contentTextView = view.findViewById(R.id.contentTextView);

        switch (type) {
            case "marketcap":
                if (lambdaResponse.getMarketCap() != null) {
                    lableTextView.setText("Market Cap");
                    String marketCap=multiplyWithCurrency(lambdaResponse.getMarketCap());
                    marketCap=Util.checkForNull(marketCap);
                    message = currencySymbol + marketCap;
                    contentTextView.setText(ifValueZero(message));
                    detialLinearLayout.addView(view, 0);
                }
                break;
            case "supply":
                if (lambdaResponse.getTotalSupply() != null) {
                    lableTextView.setText("Supply");
                    String supply = lambdaResponse.getTotalSupply();
                    supply = Util.checkForNull(supply);
                    message = supply
                            + " " + lambdaResponse.getSymbol();
                    contentTextView.setText(ifValueZero(message));
                    detialLinearLayout.addView(view, 0);
                }
                break;
            case "volume":
                if (lambdaResponse.getVolume24h() != null) {
                    lableTextView.setText("Volume");
                    String volume = multiplyWithCurrency(lambdaResponse.getVolume24h());
                    volume = checkForNull(volume);
                    message = currencySymbol + volume;
                    contentTextView.setText(ifValueZero(message));
                    detialLinearLayout.addView(view, 0);
                }
                break;
            case "percentage changes":
                if (lambdaResponse.getPercentChange7d() != null) {
                    lableTextView.setText("Percentage Change 7 Days");
                    String day7 = lambdaResponse.getPercentChange7d();
                    day7 = Util.checkForNull(day7);
                    message = day7 + " %";
                    contentTextView.setText(message);
                    setColor(contentTextView,lambdaResponse.getPercentChange7d());
                    detialLinearLayout.addView(view, 0);
                    detialLinearLayout.addView(get24View(), 0);
                    detialLinearLayout.addView(get1View(), 0);
                }
                break;
            case "price":
                if (lambdaResponse.getPrice() != null) {
                    lableTextView.setText("Price");
                    String price = multiplyWithCurrency(lambdaResponse.getPrice());
                    price = Util.checkForNull(price);
                    message = currencySymbol + price;
                    contentTextView.setText(message);
                    detialLinearLayout.addView(view, 0);
                }
                break;
            case "max supply":
                if (lambdaResponse.getMaxSupply() != null) {
                    lableTextView.setText("Max Supply");
                    String supply = lambdaResponse.getMaxSupply();
                    supply = Util.checkForNull(supply);
                    message = supply
                            + " " + lambdaResponse.getSymbol();
                    contentTextView.setText(ifValueZero(message));
                    detialLinearLayout.addView(view, 0);
                }
                break;
            case "rank":
                if (lambdaResponse.getRank() != null) {
                    lableTextView.setText("Rank");
                    String rank = lambdaResponse.getRank();
                    contentTextView.setText(ifValueZero(rank));
                    detialLinearLayout.addView(view, 0);
                }
                break;
            case "price trend":
                if (lambdaResponse.getYearlyTrend() != null && lambdaResponse.getYearlyTrend().size()>0) {
                    lableTextView.setText("Yearly Price Trend");
                    int size = lambdaResponse.getYearlyTrend().size();
                    int i =0;
                    if (size > 5) {
                        i = size-5;
                    }
                    for(;i<size;i++)
                    {
                        String price = multiplyWithCurrency(lambdaResponse.getYearlyTrend().get(i).getHistoryPrice());
                        price = Util.checkForNull(price);
                        message = currencySymbol + price;
                        detialLinearLayout.addView(getYearlyPriceTrendView(lambdaResponse.getYearlyTrend().get(i).getHistoryPriceDate(),message), 0);
                    }
                }
                break;
        }
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

    void setColor(TextView textView, String value){
        if (Double.parseDouble(value) < 0) {
            textView.setTextColor(Color.RED);
        } else {
            textView.setTextColor(Color.parseColor("#33A318"));
        }
    }

    View getYearlyPriceTrendView(String label, String Value) {
        String message;
        View view = LayoutInflater.from(context).inflate(R.layout.response_detail_row, null, false);
        TextView lableTextView = view.findViewById(R.id.lableTextView);
        TextView contentTextView = view.findViewById(R.id.contentTextView);
        lableTextView.setText(label);
        contentTextView.setText(Value);
        return view;
    }

    View get24View() {
        String message;
        View view = LayoutInflater.from(context).inflate(R.layout.response_detail_row, null, false);
        TextView lableTextView = view.findViewById(R.id.lableTextView);
        TextView contentTextView = view.findViewById(R.id.contentTextView);
        lableTextView.setText("Percentage Change 24 Hours");
        message = lambdaResponse.getPercentChange24h() + " %";
        contentTextView.setText(message);
        setColor(contentTextView,lambdaResponse.getPercentChange24h());
        return view;
    }

    View get1View() {
        String message;
        View view = LayoutInflater.from(context).inflate(R.layout.response_detail_row, null, false);
        TextView lableTextView = view.findViewById(R.id.lableTextView);
        TextView contentTextView = view.findViewById(R.id.contentTextView);
        lableTextView.setText("Percentage Change 1 Hour");
        message = lambdaResponse.getPercentChange1h() + " %";
        setColor(contentTextView,lambdaResponse.getPercentChange1h());
        contentTextView.setText(message);
        return view;
    }

    View getHistorical() {
        View view = LayoutInflater.from(context).inflate(R.layout.response_historical, null, false);
        LinearLayout historicalDataContainer = view.findViewById(R.id.historicalDataList);
        RelativeLayout historicalContainerRelativeLayout=view.findViewById(R.id.historical_container);
        View moreDataView = LayoutInflater.from(context).inflate(R.layout.more_data_row, null, false);
        TextView contentTextView = view.findViewById(R.id.contentTextView);
        ImageView coinImageView = view.findViewById(R.id.coinImageView);
        Picasso.with(context).load(Util.getCoinUrl(lambdaResponse.getSymbol(), context)).placeholder(R.drawable.default_coin).into(coinImageView);
        if (lambdaResponse.getData() != null) {
            String days = "";
            if (lambdaResponse.is24Hrs()){
                days=lambdaResponse.getData().size()+((lambdaResponse.getData().size()<=1)? " hour":" hours");
            }else {
                if (lambdaResponse.getData() != null) {
                    days = Util.getDayORMonthORYear(lambdaResponse.getData().size());
                }
            }
            contentTextView.setText(lambdaResponse.getCoinName() + " (" + lambdaResponse.getSymbol() + ") past " + days + " historical data");

            List<LambdaHistory> historyData = new ArrayList<LambdaHistory>(lambdaResponse.getData());;
            Collections.reverse(historyData);

            int size = historyData.size();
            if (size > 7) {
                size = 7;
            }
            for (int i = 0; i < size; i++) {
                LambdaHistory history = historyData.get(i);
                View row = LayoutInflater.from(context).inflate(R.layout.data_row, null, false);
                TextView dateTextView, openTextView, closeTextView, highTextView, lowTextView;
                dateTextView = row.findViewById(R.id.date_textView);
                openTextView = row.findViewById(R.id.open_textView);
                closeTextView = row.findViewById(R.id.close_textView);
                highTextView = row.findViewById(R.id.high_textView);
                lowTextView = row.findViewById(R.id.low_textView);
                if (lambdaResponse.is24Hrs()){
                    dateTextView.setText(Util.getTimeFromUnixTime(Float.parseFloat(history.getTime())));
                }else {
                    dateTextView.setText(Util.getDayMonthYearFromUnixTime(Float.parseFloat(history.getTime())-20*1000)); //@@@ //-20*1000
                }

                String low = currencySymbol + getMillionValue(Double.parseDouble(multiplyWithCurrency(history.getLow())));
                lowTextView.setText(low);
                String high = currencySymbol + getMillionValue(Double.parseDouble(multiplyWithCurrency(history.getHigh())));
                highTextView.setText(high);
                String open = currencySymbol + getMillionValue(Double.parseDouble(multiplyWithCurrency(history.getOpen())));
                openTextView.setText(open);
                String close = currencySymbol + getMillionValue(Double.parseDouble(multiplyWithCurrency(history.getClose())));
                closeTextView.setText(close);
                historicalDataContainer.addView(row);
            }
            historicalDataContainer.addView(moreDataView);
//            historicalDataContainer.setOnClickListener(this);
            historicalContainerRelativeLayout.setOnClickListener(this);
        }

        return view;

//        HistoricalDataAdapter historicalDataAdapter = new HistoricalDataAdapter(lambdaResponse.getData());
//        recyclerView.setAdapter(historicalDataAdapter);
//        recyclerView.setLayoutManager(new LinearLayoutManager(context,
//                LinearLayoutManager.HORIZONTAL, false));
//        PagerSnapHelper snapHelper = new PagerSnapHelper();
//        snapHelper.attachToRecyclerView(recyclerView);
//        linearLayout.addView(recyclerView);

//        HistoricalAdapter slidingImage_adapter = new HistoricalAdapter(context,lambdaResponse.getData());
//        viewPager.getLayoutParams();
//        viewPager.setAdapter(slidingImage_adapter);
//        viewPager.setVisibility(View.VISIBLE);
//        linearLayout.addView(viewPager);
    }

    View getChart() {
        View view = LayoutInflater.from(context).inflate(R.layout.response_chart, null, false);
        ImageView coinImageView = view.findViewById(R.id.coinImageView);
        Picasso.with(context).load(Util.getCoinUrl(lambdaResponse.getSymbol(), context)).placeholder(R.drawable.default_coin).into(coinImageView);
        RelativeLayout chartResponserRelativeLayout = view.findViewById(R.id.chartResponseView);
        TextView priceTextView = view.findViewById(R.id.priceTextView);
        LineChart chart = view.findViewById(R.id.chart);
        TextView contentTextView = view.findViewById(R.id.contentTextView);

        String price="Price : " + MyApplication.currencySymbol+
                Util.multiplyWithCurrency(lambdaResponse.getPrice());
        priceTextView.setText(price);

        String days = "";
        if (lambdaResponse.is24Hrs()){
            days=lambdaResponse.getData().size()+((lambdaResponse.getData().size()<=1)? " hour":" hours");
        }else {
            if (lambdaResponse.getData() != null) {
                days = Util.getDayORMonthORYear(lambdaResponse.getData().size());
            }
        }
        contentTextView.setText(lambdaResponse.getCoinName() + " (" + lambdaResponse.getSymbol() + ") past " + days + " chart");

        chart.setNoDataTextColor(context.getResources().getColor(R.color.coin_header));
        chart.getAxisLeft().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setTouchEnabled(false);
        chart.invalidate();
        chart.setDrawMarkerViews(true);
        XAxis xAxis = chart.getXAxis();
        YAxis yAxis = chart.getAxisRight();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10);
        xAxis.setTextColor(context.getResources().getColor(R.color.textColor));
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);

        // set historicalDataList custom value formatter
        if (lambdaResponse.is24Hrs()){
            xAxis.setValueFormatter(new TimeDateFormatter());
        }else {
            xAxis.setValueFormatter(new DateMonFormatter());
        }

        yAxis.setValueFormatter(new CurrencyFormatter(MyApplication.currencySymbol));
        LineData lineData = getGraphData((ArrayList<LambdaHistory>) lambdaResponse.getData());
        chart.setData(lineData);
        chart.notifyDataSetChanged();

        chartResponserRelativeLayout.setOnClickListener(this);

        return view;
    }

    private LineData getGraphData(List<LambdaHistory> historicalData) {
        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (LambdaHistory datum : historicalData) {
            double open = Double.parseDouble(datum.getOpen());
            float val = (float) open * MyApplication.currencyMultiplyingFactor;
            Integer integer = Integer.valueOf(datum.getTime());
            yVals.add(new Entry(integer, val));
        }
        if (yVals.size() == 0) {
            return null;
        }
        // create historicalDataList dataset and give it historicalDataList type
        Collections.sort(yVals, new EntryXComparator());
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        set1.setDrawFilled(true);
        set1.setColor(context.getResources().getColor(R.color.coin_header));
        set1.setFillColor(context.getResources().getColor(R.color.coin_header));

        set1.setDrawValues(false);
        set1.setLineWidth(1f);

        set1.setDrawCircles(false);
        set1.setColor(context.getResources().getColor(R.color.coin_header));
        set1.setDrawValues(false);
        // create historicalDataList data object with the datasets
        LineData data = new LineData(set1);
        return data;
    }

    void addSugession(ArrayList<String> sugessionArrayList) {
        View sugessionView = LayoutInflater.from(context).inflate(R.layout.response_sugession_text, null, false);
        RecyclerView recyclerView = sugessionView.findViewById(R.id.sugessionRecycleView);
        SugessionAdapter sugessionAdapter = new SugessionAdapter(sugessionArrayList, chatView);
        recyclerView.setAdapter(sugessionAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        linearLayout.addView(sugessionView);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.historical_container:
                chatView.showDetail(chatData, HISTORY);
                break;
            case R.id.chartResponseView:
                chatView.showDetail(chatData, CHART);
                break;
            case R.id.responseDetailView:
                chatView.showDetail(chatData, COIN_DETAIL);
                break;
            case R.id.answerTextView:
                chatView.showDetail(chatData, TEXT_RECEIVER_MESSAGE);
                break;
            case R.id.responseYearlyPriceTrendView:
                chatView.showDetail(chatData, COIN_YEARLY_PRICE_TREND);
                break;
            case R.id.responseRelativeLayout:
                chatView.showDetail(chatData, COIN_DETAIL);
//                TextView textView = view.findViewById(R.id.answerTextView);
//                if (textView.getText().toString().contains("price")) {
//                    chatView.showDetail(chatData, TEXT_PRICE);
//                } else if (textView.getText().toString().contains("marketcap")) {
//                    chatView.showDetail(chatData, TEXT_MARKETCAP);
//                } else if (textView.getText().toString().contains("supply")) {
//                    chatView.showDetail(chatData, TEXT_SUPPLY);
//                } else if (textView.getText().toString().contains("volume")) {
//                    chatView.showDetail(chatData, TEXT_VOLUME);
//                } else if (textView.getText().toString().contains("percentage")) {
//                    chatView.showDetail(chatData, TEXT_PERCENTAGE);
//                }
                break;
        }
    }
}
