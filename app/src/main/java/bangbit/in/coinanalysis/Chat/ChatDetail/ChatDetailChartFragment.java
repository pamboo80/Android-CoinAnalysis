package bangbit.in.coinanalysis.Chat.ChatDetail;


import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bangbit.in.coinanalysis.BaseActivity;
import bangbit.in.coinanalysis.ChartMarkerViews.DateMonthChartMarkerView;
import bangbit.in.coinanalysis.ChartMarkerViews.TimeDateMonthChartMarkerView;
import bangbit.in.coinanalysis.Chat.ChatData;
import bangbit.in.coinanalysis.ChildNetworkAvailable;
import bangbit.in.coinanalysis.MyApplication;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.axisFormator.CurrencyFormatter;
import bangbit.in.coinanalysis.axisFormator.DateMonFormatter;
import bangbit.in.coinanalysis.axisFormator.TimeDateFormatter;
import bangbit.in.coinanalysis.pojo.Datum;
import bangbit.in.coinanalysis.pojo.HistoricalData;
import bangbit.in.coinanalysis.pojo.LambdaHistory;
import bangbit.in.coinanalysis.pojo.LambdaResponse;
import bangbit.in.coinanalysis.repository.HistoryRepository;

import static bangbit.in.coinanalysis.MyApplication.currencySymbol;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChatDetailChartFragment extends Fragment implements View.OnClickListener, ChildNetworkAvailable {
    private Context context;
    private static final String ARG_PARAM1 = "param1";
    private ChatData chatData;
    private LambdaResponse lambdaResponse;
    private boolean isAttached = false;
    private LineChart chart;
    private TextView hour24Button;
    private TextView day7Button;
    private TextView month1Button;
    private TextView month3Button;
    private TextView year1Button;
    private TextView allButton;
    private FrameLayout frameLayout;
    private ChatFragmentListner chatFragmentListner;
    ArrayList<LambdaHistory> lambdaHistories;
    ArrayList<LambdaHistory> histories24Hour = new ArrayList<>();
    ArrayList<LambdaHistory> historiesAll = new ArrayList<>();


    private int clickedId;
    private DateMonthChartMarkerView dateMonthChartMarkerView;
    private TimeDateMonthChartMarkerView timeDateMonthChartMarkerView;
    private TextView profitTextView;
    private boolean isNetworkAvailable = false;
    private Toast toast;
    private LinearLayout tryAgainLinearLayout;
    private ProgressBar progressBar;
    private BaseActivity baseActivity;


    public ChatDetailChartFragment() {
        // Required empty public constructor
    }

    public static ChatDetailChartFragment newInstance(ChatData chatData) {
        Bundle args = new Bundle();

        ChatDetailChartFragment fragment = new ChatDetailChartFragment();
        args.putParcelable(ARG_PARAM1, chatData);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isAttached = true;
        try {
            chatFragmentListner = (ChatFragmentListner) context;
        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString() + " must implement OnArticleSelectedListener");
        }
        if (getActivity() instanceof BaseActivity) {
            baseActivity = (BaseActivity) getActivity();
            baseActivity.addChildReceiverListner(this);
            isNetworkAvailable=baseActivity.isNetworkAvailable();

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        if (getArguments() != null) {
            chatData = getArguments().getParcelable(ARG_PARAM1);
            lambdaResponse = chatData.getLambdaResponse();
            lambdaHistories = (ArrayList<LambdaHistory>) lambdaResponse.getData();
            if (lambdaResponse.is24Hrs()) {
                histories24Hour.addAll(lambdaHistories);
            }
        }
        getAllData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        context = getContext();
        frameLayout = new FrameLayout(context);
        View view = LayoutInflater.from(context).inflate(R.layout.chat_detail_chart, null, false);
        tryAgainLinearLayout = view.findViewById(R.id.tryAgain);
        TextView tryAgainTextView = (TextView) view.findViewById(R.id.try_again_Button);
        tryAgainTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tryAgain();
            }
        });
        progressBar = view.findViewById(R.id.progressBar);
        hour24Button = view.findViewById(R.id.hour24Button);
        day7Button = view.findViewById(R.id.day7Button);
        month1Button = view.findViewById(R.id.month1Button);
        month3Button = view.findViewById(R.id.month3Button);
        year1Button = view.findViewById(R.id.year1Button);
        allButton = view.findViewById(R.id.allButton);
        hour24Button.setOnClickListener(this);
        day7Button.setOnClickListener(this);
        month1Button.setOnClickListener(this);
        month3Button.setOnClickListener(this);
        year1Button.setOnClickListener(this);
        allButton.setOnClickListener(this);
        ImageView coinImageView = view.findViewById(R.id.coinImageView);
        Picasso.with(context).load(Util.getCoinUrl(lambdaResponse.getSymbol(), context)).placeholder(R.drawable.default_coin).into(coinImageView);
        TextView timeDataTextView = view.findViewById(R.id.time_date_textView);
        String dateTime = "" + chatData.getDate() + " " + chatData.getTime();
        timeDataTextView.setText(dateTime);

        chart = view.findViewById(R.id.chart);
        LinearLayout linearLayout = view.findViewById(R.id.chartHolder);
        TextView contentTextView = view.findViewById(R.id.contentTextView);

        TextView priceTextView = view.findViewById(R.id.price_textView);
        String price = ": " + MyApplication.currencySymbol + Util.multiplyWithCurrency(lambdaResponse.getPrice());
        priceTextView.setText(price);

        profitTextView = view.findViewById(R.id.profit_textView);
        updatePercentage();

        String days = "";
        if (lambdaHistories != null) {
            if(lambdaResponse.is24Hrs())
            {
                days=lambdaResponse.getData().size()+((lambdaResponse.getData().size()<=1)? " hour":" hours");
            }
            else
            {
                days = Util.getDayORMonthORYear(lambdaHistories.size());
            }
        }
        setColor();
        dateMonthChartMarkerView = new DateMonthChartMarkerView(getContext(), R.layout.chart_mark_view);
        timeDateMonthChartMarkerView = new TimeDateMonthChartMarkerView(getContext(), R.layout.chart_mark_view);
        contentTextView.setText(lambdaResponse.getCoinName() + " (" + lambdaResponse.getSymbol() + ")"); // past " + days + " chart");
        chart.setMarkerView(dateMonthChartMarkerView);
        chart.setNoDataTextColor(context.getResources().getColor(R.color.coin_header));
        chart.getAxisLeft().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.invalidate();
        chart.setDrawMarkerViews(true);

        XAxis xAxis = chart.getXAxis();
        YAxis yAxis = chart.getAxisRight();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10);
        xAxis.setTextColor(context.getResources().getColor(R.color.textColor));

        // set historicalDataList custom value formatter
        if (lambdaResponse.is24Hrs()) {
            xAxis.setValueFormatter(new TimeDateFormatter());
            chart.setMarkerView(timeDateMonthChartMarkerView);
        } else {
            xAxis.setValueFormatter(new DateMonFormatter());
            chart.setMarkerView(dateMonthChartMarkerView);
        }

        yAxis.setValueFormatter(new CurrencyFormatter(currencySymbol));
        LineData lineData = getGraphData(lambdaHistories);

        chart.setData(lineData);
        chart.notifyDataSetChanged();
        frameLayout.addView(view);
        chatFragmentListner.onOrientationChange(isLandScape());

        return frameLayout;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        chatFragmentListner.onOrientationChange(isLandScape());
        frameLayout.removeAllViews();
        View view = LayoutInflater.from(context).inflate(R.layout.chat_detail_chart, null, false);
        hour24Button = view.findViewById(R.id.hour24Button);
        day7Button = view.findViewById(R.id.day7Button);
        month1Button = view.findViewById(R.id.month1Button);
        month3Button = view.findViewById(R.id.month3Button);
        year1Button = view.findViewById(R.id.year1Button);
        allButton = view.findViewById(R.id.allButton);
        hour24Button.setOnClickListener(this);
        day7Button.setOnClickListener(this);
        month1Button.setOnClickListener(this);
        month3Button.setOnClickListener(this);
        year1Button.setOnClickListener(this);
        allButton.setOnClickListener(this);
        ImageView coinImageView = view.findViewById(R.id.coinImageView);
        Picasso.with(context).load(Util.getCoinUrl(lambdaResponse.getSymbol(), context)).placeholder(R.drawable.default_coin).into(coinImageView);
        TextView timeDataTextView = view.findViewById(R.id.time_date_textView);
        String dateTime = "" + chatData.getDate() + " " + chatData.getTime();
        timeDataTextView.setText(dateTime);
        chart = view.findViewById(R.id.chart);
        LinearLayout linearLayout = view.findViewById(R.id.chartHolder);
        TextView contentTextView = view.findViewById(R.id.contentTextView);
        TextView priceTextView = view.findViewById(R.id.price_textView);
        String price = ": " + MyApplication.currencySymbol + Util.multiplyWithCurrency(lambdaResponse.getPrice());
        priceTextView.setText(price);
        profitTextView = view.findViewById(R.id.profit_textView);
        updatePercentage();
        String days = "";
        if (lambdaResponse.is24Hrs()) {
            days = lambdaResponse.getData().size()+((lambdaResponse.getData().size()<=1)? " hour":" hours");
        } else {
            if (lambdaResponse.getData() != null) {
                days = Util.getDayORMonthORYear(lambdaResponse.getData().size());
            }
        }
        contentTextView.setText(lambdaResponse.getCoinName() + " (" + lambdaResponse.getSymbol() + ")"); // past " + days + " chart");
        chart.setNoDataTextColor(context.getResources().getColor(R.color.coin_header));
        chart.getAxisLeft().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.invalidate();
        chart.setDrawMarkerViews(true);
        XAxis xAxis = chart.getXAxis();
        YAxis yAxis = chart.getAxisRight();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10);
        xAxis.setTextColor(context.getResources().getColor(R.color.textColor));
        // set historicalDataList custom value formatter
        if (lambdaResponse.is24Hrs()) {
            xAxis.setValueFormatter(new TimeDateFormatter());
            chart.setMarkerView(timeDateMonthChartMarkerView);
        } else {
            xAxis.setValueFormatter(new DateMonFormatter());
            chart.setMarkerView(dateMonthChartMarkerView);
        }
        yAxis.setValueFormatter(new CurrencyFormatter(currencySymbol));
        chart.notifyDataSetChanged();
        onClick(view.findViewById(clickedId));
        frameLayout.addView(view);
    }

    void updatePercentage() {
        if(lambdaHistories !=null && lambdaHistories.size() >0) {
            Double firstPrice = Double.valueOf(this.lambdaHistories.get(0).getOpen());
            Double lastPrice = Double.valueOf(this.lambdaHistories.get(this.lambdaHistories.size() - 1).getOpen());    //@@@ getClose()
            float percentage = 0;
            if (firstPrice != 0) {
                percentage = (float) (((lastPrice - firstPrice) / firstPrice) * 100);
            }
            profitTextView.setText(Util.getTwoDecimalPointValue(percentage) + " %");
        }
    }

    void displayProgresBar() {
        progressBar.setVisibility(View.VISIBLE);
        tryAgainLinearLayout.setVisibility(View.INVISIBLE);
        chart.setVisibility(View.INVISIBLE);
    }

    void tryAgain() {
        if (clickedId == R.id.hour24Button) {
            displayProgresBar();
            HistoryRepository historyRepository = new HistoryRepository();
            historyRepository.getHistoricalDataByHour(lambdaResponse.getSymbol(), "USD", 24, new HistoryRepository.HistoricalDataCallBack() {
                @Override
                public void onRetrieveHistoricalData(HistoricalData historicalData, boolean isSuccess) {
                    if (isSuccess && historicalData != null && isAttached==true) {
                        ArrayList<LambdaHistory> lambdaHistories = new ArrayList<>();
                        for (Datum datum : historicalData.getData()) {
                            LambdaHistory lambdaHistory = new LambdaHistory(lambdaResponse.getSymbol(),
                                    datum.getOpen().toString(), datum.getClose().toString(),
                                    datum.getLow().toString(), datum.getHigh().toString(),
                                    datum.getTime().toString());
                            lambdaHistories.add(lambdaHistory);
                        }
                        ChatDetailChartFragment.this.histories24Hour = lambdaHistories;
                        displayChartData(lambdaHistories, true, clickedId);
                    } else if(isAttached==true){
                        displayChartData(new ArrayList<LambdaHistory>(), true,clickedId);
                    }
                    if(isAttached==true) progressBar.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            int limit = 7;
            if (clickedId == R.id.day7Button) {
                limit = 7;
            } else if (clickedId == R.id.month1Button) {
                limit = 30;
            } else if (clickedId == R.id.month3Button) {
                limit = 90;
            } else if (clickedId == R.id.year1Button) {
                limit = 365;
            } else if (clickedId == R.id.allButton) {
                limit = 2000;
            }
            getAllData();
            displayProgresBar();
            HistoryRepository historyRepository = new HistoryRepository();
            historyRepository.getHistoricalData(lambdaResponse.getSymbol(), "USD", limit - 1, new HistoryRepository.HistoricalDataCallBack() {
                @Override
                public void onRetrieveHistoricalData(HistoricalData historicalData, boolean isSuccess) {
                    if (isSuccess && historicalData != null && isAttached==true) {
                        ArrayList<LambdaHistory> lambdaHistories = new ArrayList<>();
                        for (Datum datum : historicalData.getData()) {
                            LambdaHistory lambdaHistory = new LambdaHistory(lambdaResponse.getSymbol(),
                                    datum.getOpen().toString(), datum.getClose().toString(),
                                    datum.getLow().toString(), datum.getHigh().toString(),
                                    datum.getTime().toString());

                            lambdaHistories.add(lambdaHistory);
                        }
                        displayChartData(lambdaHistories, false, clickedId);
                    } else if(isAttached==true){
                        displayChartData(new ArrayList<LambdaHistory>(), false, clickedId);
                    }
                    if(isAttached==true) progressBar.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void setColor() {
        if (lambdaResponse.is24Hrs()) {
            changeButtonColor(R.id.hour24Button);
            clickedId = R.id.hour24Button;
        } else {
            if (lambdaHistories.size() == 7) {
                changeButtonColor(R.id.day7Button);
                clickedId = R.id.day7Button;
            } else if (lambdaHistories.size() == 30) {
                changeButtonColor(R.id.month1Button);
                clickedId = R.id.day7Button;
            } else if (lambdaHistories.size() == 90) {
                changeButtonColor(R.id.month3Button);
                clickedId = R.id.day7Button;
            } else if (lambdaHistories.size() == 365) {
                changeButtonColor(R.id.year1Button);
                clickedId = R.id.day7Button;
            } else if (lambdaHistories.size() == 2000) {
                changeButtonColor(R.id.allButton);
                clickedId = R.id.day7Button;
            }
        }
    }

    public boolean isLandScape() {
        Display getOrient = getActivity().getWindowManager().getDefaultDisplay();
        int orientation = Configuration.ORIENTATION_UNDEFINED;

        if (getOrient.getWidth() < getOrient.getHeight()) {
            orientation = Configuration.ORIENTATION_PORTRAIT;
            return false;
        } else {
            orientation = Configuration.ORIENTATION_LANDSCAPE;
            return true;
        }
    }

    private void changeMarkerView() {
        XAxis xAxis = chart.getXAxis();
        if (clickedId == R.id.hour24Button) {
            chart.setMarker(timeDateMonthChartMarkerView);
            xAxis.setValueFormatter(new TimeDateFormatter());
        } else {
            chart.setMarker(dateMonthChartMarkerView);
            xAxis.setValueFormatter(new DateMonFormatter());
        }
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
        set1.setDrawHorizontalHighlightIndicator(false);

        set1.setDrawCircles(false);
        set1.setColor(context.getResources().getColor(R.color.coin_header));
        set1.setDrawValues(false);
        // create historicalDataList data object with the datasets

        LineData data = new LineData(set1);
        return data;
    }


    @Override
    public void onClick(View v) {
        clickedId=v.getId();
        switch (v.getId()) {
            case R.id.hour24Button:
                setChartData(24, true, v.getId());
                break;
            case R.id.day7Button:
                setChartData(7, false, v.getId());
                break;
            case R.id.month1Button:
                setChartData(30, false, v.getId());
                break;
            case R.id.month3Button:
                setChartData(90, false, v.getId());
                break;
            case R.id.year1Button:
                setChartData(365, false, v.getId());
                break;
            case R.id.allButton:
                setChartData(2000, false, v.getId());
                break;
        }
    }

    void getAllData() {
        HistoryRepository historyRepository = new HistoryRepository();
        historyRepository.getHistoricalData(lambdaResponse.getSymbol(), "USD", 2000, new HistoryRepository.HistoricalDataCallBack() {
            @Override
            public void onRetrieveHistoricalData(HistoricalData historicalData, boolean isSuccess) {

                if (isSuccess && historicalData != null && isAttached==true) {
                    ArrayList<LambdaHistory> lambdaHistories = new ArrayList<>();
                    for (Datum datum : historicalData.getData()) {
                        LambdaHistory lambdaHistory = new LambdaHistory(lambdaResponse.getSymbol(),
                                datum.getOpen().toString(), datum.getClose().toString(),
                                datum.getLow().toString(), datum.getHigh().toString(),
                                datum.getTime().toString());
                        lambdaHistories.add(lambdaHistory);
                    }
                    ChatDetailChartFragment.this.historiesAll = lambdaHistories;
                }
            }
        });
    }

    private void displayTryAgain(boolean isDisplay) {
        if (!isNetworkAvailable){
            baseActivity.showSnackbar();
        }
        if (isDisplay) {
            chart.setVisibility(View.GONE);
            tryAgainLinearLayout.setVisibility(View.VISIBLE);
        } else {
            chart.setVisibility(View.VISIBLE);
            tryAgainLinearLayout.setVisibility(View.GONE);
        }
    }

    private void displayChartData(ArrayList<LambdaHistory> histories, boolean is24Hour, int id) {
        if (histories != null && histories.size() > 0) {
            displayTryAgain(false);
        } else {
            displayTryAgain(true);
        }
        chart.setData(getGraphData(histories));
        if (is24Hour) {
            chart.setMarkerView(timeDateMonthChartMarkerView);
        } else {
            chart.setMarkerView(dateMonthChartMarkerView);
        }
        chart.notifyDataSetChanged();
        changeMarkerView();
        chart.invalidate();

        changeButtonColor(id);
    }

    private void setChartData(int limit, final boolean is24Hour, final int id) {
        if (is24Hour) {
            if (histories24Hour != null && histories24Hour.size() > 23) {
                displayChartData(histories24Hour, is24Hour, id);
            } else {
                if (!isNetworkAvailable) {
                    displayChartData(new ArrayList<LambdaHistory>(), is24Hour, id);
                    displayTryAgain(true);
                    return;
                }
                displayProgresBar();
                HistoryRepository historyRepository = new HistoryRepository();
                historyRepository.getHistoricalDataByHour(lambdaResponse.getSymbol(), "USD", limit - 1, new HistoryRepository.HistoricalDataCallBack() {
                    @Override
                    public void onRetrieveHistoricalData(HistoricalData historicalData, boolean isSuccess) {
                        if (isSuccess && historicalData != null && isAttached==true) {
                            ArrayList<LambdaHistory> lambdaHistories = new ArrayList<>();
                            for (Datum datum : historicalData.getData()) {
                                LambdaHistory lambdaHistory = new LambdaHistory(lambdaResponse.getSymbol(),
                                        datum.getOpen().toString(), datum.getClose().toString(),
                                        datum.getLow().toString(), datum.getHigh().toString(),
                                        datum.getTime().toString());
                                lambdaHistories.add(lambdaHistory);
                            }
                            ChatDetailChartFragment.this.histories24Hour = lambdaHistories;
                            displayChartData(lambdaHistories, is24Hour, id);
                        } else if(isAttached==true){
                            displayChartData(new ArrayList<LambdaHistory>(), is24Hour, id);
                        }
                        if(isAttached==true) progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
        } else {
            if (historiesAll != null && historiesAll.size() > 0) {
                ArrayList<LambdaHistory> histories;
                if (limit != 2000) {
                    histories = new ArrayList<LambdaHistory>(historiesAll.subList(historiesAll.size() - limit, historiesAll.size()));
                } else {
                    histories = new ArrayList(historiesAll);
                }
                displayChartData(histories, is24Hour, id);
            } else {
                if (!isNetworkAvailable) {
                    ArrayList<LambdaHistory> histories = new ArrayList<>();
                    if (!lambdaResponse.is24Hrs()) {
                        if (lambdaHistories.size() >= limit) {
                            histories = new ArrayList<LambdaHistory>(lambdaHistories.subList(lambdaHistories.size() - limit, lambdaHistories.size()));
                        } else {
                            displayTryAgain(true);
                        }
                    }
                    displayChartData(histories, is24Hour, id);
                    return;
                }
                getAllData();
                displayProgresBar();
                HistoryRepository historyRepository = new HistoryRepository();
                historyRepository.getHistoricalData(lambdaResponse.getSymbol(), "USD", limit - 1, new HistoryRepository.HistoricalDataCallBack() {
                    @Override
                    public void onRetrieveHistoricalData(HistoricalData historicalData, boolean isSuccess) {
                        if (isSuccess && historicalData != null && isAttached==true) {
                            ArrayList<LambdaHistory> lambdaHistories = new ArrayList<>();
                            for (Datum datum : historicalData.getData()) {
                                LambdaHistory lambdaHistory = new LambdaHistory(lambdaResponse.getSymbol(),
                                        datum.getOpen().toString(), datum.getClose().toString(),
                                        datum.getLow().toString(), datum.getHigh().toString(),
                                        datum.getTime().toString());

                                lambdaHistories.add(lambdaHistory);
                            }
                            displayChartData(lambdaHistories, is24Hour, id);
                        } else if(isAttached==true){
                            displayChartData(new ArrayList<LambdaHistory>(), is24Hour, id);
                        }
                        if(isAttached==true) progressBar.setVisibility(View.INVISIBLE);

                    }
                });
            }
        }
    }

    void changeButtonColor(int id) {
        this.clickedId = id;
        day7Button.setBackground(getResources().getDrawable(R.drawable.rounded_pink_unfilled_button));
        month1Button.setBackground(getResources().getDrawable(R.drawable.rounded_pink_unfilled_button));
        month3Button.setBackground(getResources().getDrawable(R.drawable.rounded_pink_unfilled_button));
        year1Button.setBackground(getResources().getDrawable(R.drawable.rounded_pink_unfilled_button));
        hour24Button.setBackground(getResources().getDrawable(R.drawable.rounded_pink_unfilled_button));
        allButton.setBackground(getResources().getDrawable(R.drawable.rounded_pink_unfilled_button));

        day7Button.setTextColor(getResources().getColor(R.color.coin_header));
        month1Button.setTextColor(getResources().getColor(R.color.coin_header));
        month3Button.setTextColor(getResources().getColor(R.color.coin_header));
        year1Button.setTextColor(getResources().getColor(R.color.coin_header));
        hour24Button.setTextColor(getResources().getColor(R.color.coin_header));
        allButton.setTextColor(getResources().getColor(R.color.coin_header));
        switch (id) {
            case R.id.day7Button:
                day7Button.setBackground(getResources().getDrawable(R.drawable.rounded_pink_filled_button));
                day7Button.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.month1Button:
                month1Button.setBackground(getResources().getDrawable(R.drawable.rounded_pink_filled_button));
                month1Button.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.month3Button:
                month3Button.setBackground(getResources().getDrawable(R.drawable.rounded_pink_filled_button));
                month3Button.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.year1Button:
                year1Button.setBackground(getResources().getDrawable(R.drawable.rounded_pink_filled_button));
                year1Button.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.hour24Button:
                hour24Button.setBackground(getResources().getDrawable(R.drawable.rounded_pink_filled_button));
                hour24Button.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.allButton:
                allButton.setBackground(getResources().getDrawable(R.drawable.rounded_pink_filled_button));
                allButton.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).removeChildReceiverListner(this);
        }
        isAttached = false;

        chatFragmentListner.onOrientationChange(false);
    }

    @Override
    public void onChildNetworkAvailable() {
        isNetworkAvailable = true;
    }

    @Override
    public void onChildNetworkUnavailable() {
        isNetworkAvailable = false;
    }

    public interface ChatFragmentListner {
        void onOrientationChange(boolean isLandscape);
    }
}
