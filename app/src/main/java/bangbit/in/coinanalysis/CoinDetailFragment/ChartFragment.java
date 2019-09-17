package bangbit.in.coinanalysis.CoinDetailFragment;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;

import bangbit.in.coinanalysis.ChartMarkerViews.DateMonthChartMarkerView;
import bangbit.in.coinanalysis.ChartMarkerViews.TimeDateMonthChartMarkerView;
import bangbit.in.coinanalysis.ChildNetworkAvailable;
import bangbit.in.coinanalysis.DetailActivity.CoinUpdator;
import bangbit.in.coinanalysis.DetailActivity.DetailActivity;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.axisFormator.CurrencyFormatter;
import bangbit.in.coinanalysis.axisFormator.DateMonFormatter;
import bangbit.in.coinanalysis.axisFormator.TimeDateFormatter;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.Datum;
import bangbit.in.coinanalysis.repository.HistoryRepository;

import static bangbit.in.coinanalysis.MyApplication.currencySymbol;
import static bangbit.in.coinanalysis.Util.getFormattedPrice;
import static bangbit.in.coinanalysis.Util.getTwoDecimalPointValue;

public class ChartFragment extends Fragment implements ChartFragmentContract.View, View.OnClickListener,
        CoinUpdator, ChildNetworkAvailable {

    private Coin coin;
    private LineChart chart;

    ChartFragmentContract.UserActionsListener mUserActionsListener;
    private ArrayList<Datum> historicalDataList;
    private ArrayList<Datum> hour24historicalDataList;
    private TextView day7Button, month1Button, month3Button, year1Button, hour24Button, allButton;
    private DateMonthChartMarkerView dateMonthChartMarkerView;
    private TimeDateMonthChartMarkerView timeDateMonthChartMarkerView;
    private XAxis xAxis;
    private DetailActivity detailActivity;
    boolean isAttached = false;
    boolean is24Hour = false;
    private FrameLayout frameLayout;
    private TextView priceTextView;
    private TextView profitTextView;
    private ProgressBar yearProgressBar;
    private boolean isFailedYearly = false;
    private boolean isFailed24Hours = false;
    LinearLayout tryAgainLinearLayout;
    private TextView tryAgainButtonTextView;

    public ChartFragment() {
        // Required empty public constructor
    }

    public static ChartFragment newInstance(String param1, String param2) {
        ChartFragment fragment = new ChartFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailActivity = (DetailActivity) getActivity();
        detailActivity.addChildReceiverListner(this);
        coin = detailActivity.getCoin();
        detailActivity.addCoinUpdator(this);
        historicalDataList = detailActivity.getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coin_detail_chart, container, false);
        tryAgainLinearLayout = view.findViewById(R.id.tryAgain);
        priceTextView = view.findViewById(R.id.price_textView);
        profitTextView = view.findViewById(R.id.profit_textView);
        yearProgressBar = view.findViewById(R.id.progressBar);
        tryAgainButtonTextView = view.findViewById(R.id.try_again_Button);

        String price = "";
        if (coin != null) {
            price = ": " + getFormattedPrice(coin.getPriceUsd());
            priceTextView.setText(price);
        }

        HistoryRepository historyRepository = new HistoryRepository();
        mUserActionsListener = new ChartFragmentPresenter(this, historyRepository, coin);
        frameLayout = new FrameLayout(getActivity());
        chart = view.findViewById(R.id.chart);
        chart.setNoDataTextColor(getResources().getColor(R.color.coin_header));
        chart.getAxisLeft().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDrawMarkerViews(true);
        dateMonthChartMarkerView = new DateMonthChartMarkerView(getContext(), R.layout.chart_mark_view);
        timeDateMonthChartMarkerView = new TimeDateMonthChartMarkerView(getContext(), R.layout.chart_mark_view);
        chart.setMarkerView(timeDateMonthChartMarkerView);

        xAxis = chart.getXAxis();
        YAxis yAxis = chart.getAxisRight();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10);
        xAxis.setTextColor(getResources().getColor(R.color.textColor));
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        // set historicalDataList custom value formatter
        xAxis.setValueFormatter(new TimeDateFormatter());
        yAxis.setValueFormatter(new CurrencyFormatter(currencySymbol));

        day7Button = view.findViewById(R.id.day7Button);
        month1Button = view.findViewById(R.id.month1Button);
        month3Button = view.findViewById(R.id.month3Button);
        year1Button = view.findViewById(R.id.year1Button);
        hour24Button = view.findViewById(R.id.hour24Button);
        allButton = view.findViewById(R.id.allButton);
        day7Button.setOnClickListener(this);
        month1Button.setOnClickListener(this);
        month3Button.setOnClickListener(this);
        year1Button.setOnClickListener(this);
        hour24Button.setOnClickListener(this);
        allButton.setOnClickListener(this);
        initialLoadChartData();

        //mUserActionsListener.loadHistoricalData(coin.getSymbol(),"USD",7);

        frameLayout.addView(view);
        tryAgainButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is24Hour) {
                    mUserActionsListener.load24HourData(coin.getSymbol(), "USD", 24);
                } else {
                    detailActivity.reLoadChartData();
                }
                showYearProgressBar(true, is24Hour);
            }
        });
        return frameLayout;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        frameLayout.removeAllViews();
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_coin_detail_chart, null);
        tryAgainLinearLayout = view.findViewById(R.id.tryAgain);
        tryAgainButtonTextView = view.findViewById(R.id.try_again_Button);

        yearProgressBar = view.findViewById(R.id.progressBar);
        priceTextView = view.findViewById(R.id.price_textView);
        profitTextView = view.findViewById(R.id.profit_textView);
        chart = view.findViewById(R.id.chart);
        chart.setNoDataTextColor(getResources().getColor(R.color.coin_header));
        chart.getAxisLeft().setEnabled(false);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setDrawMarkerViews(true);
        dateMonthChartMarkerView = new DateMonthChartMarkerView(getContext(), R.layout.chart_mark_view);
        timeDateMonthChartMarkerView = new TimeDateMonthChartMarkerView(getContext(), R.layout.chart_mark_view);
        chart.setMarkerView(timeDateMonthChartMarkerView);

        xAxis = chart.getXAxis();
        YAxis yAxis = chart.getAxisRight();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10);
        xAxis.setTextColor(getResources().getColor(R.color.textColor));
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(true);
        // set historicalDataList custom value formatter
        xAxis.setValueFormatter(new TimeDateFormatter());
        yAxis.setValueFormatter(new CurrencyFormatter(currencySymbol));

        day7Button = view.findViewById(R.id.day7Button);
        month1Button = view.findViewById(R.id.month1Button);
        month3Button = view.findViewById(R.id.month3Button);
        year1Button = view.findViewById(R.id.year1Button);
        hour24Button = view.findViewById(R.id.hour24Button);
        allButton = view.findViewById(R.id.allButton);
        day7Button.setOnClickListener(this);
        month1Button.setOnClickListener(this);
        month3Button.setOnClickListener(this);
        year1Button.setOnClickListener(this);
        hour24Button.setOnClickListener(this);
        allButton.setOnClickListener(this);

        frameLayout.addView(view);
        tryAgainButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (is24Hour) {
                    mUserActionsListener.load24HourData(coin.getSymbol(), "USD", 24);
                } else {
                    detailActivity.reLoadChartData();
                }
                showYearProgressBar(true, is24Hour);
            }
        });
        initialLoadChartData();
    }

    @Override
    public void onClick(View view) {
        is24Hour = view.getId() == R.id.hour24Button;
        int day = 0;
        switch (view.getId()) {
            case R.id.day7Button:
                day = 7;
                DetailActivity.currentChartdataButton = R.id.day7Button;
                break;
            case R.id.month1Button:
                day = 30;
                DetailActivity.currentChartdataButton = R.id.month1Button;
                break;
            case R.id.month3Button:
                day = 90;
                DetailActivity.currentChartdataButton = R.id.month3Button;
                break;
            case R.id.year1Button:
                DetailActivity.currentChartdataButton = R.id.year1Button;
                day = 365;
                break;
            case R.id.hour24Button:
                if (hour24historicalDataList == null) {
                    if (isFailed24Hours) {
                        showTryAgain(true);
                    } else {
                        mUserActionsListener.load24HourData(coin.getSymbol(), "USD", 24);
                    }

                } else {
                    displayChartDataFor24Hrs(hour24historicalDataList);
                    mUserActionsListener.calculatePercentage(hour24historicalDataList);
                    showYearProgressBar(false, is24Hour);
                    showTryAgain(false);
                }
                DetailActivity.currentChartdataButton = R.id.hour24Button;

                chart.setMarkerView(timeDateMonthChartMarkerView);
                xAxis.setValueFormatter(new TimeDateFormatter());
                changeButtonColor(view.getId());
                break;
            case R.id.allButton:
                if (historicalDataList != null) {
                    day = historicalDataList.size();
                    changeButtonColor(view.getId());
                } else {
                    day = 2000;
                }
                DetailActivity.currentChartdataButton = R.id.allButton;
                break;
        }
        if (day != 0) {
            if (historicalDataList == null) {
                historicalDataList = detailActivity.getData();
            }
            if (historicalDataList != null) {
                mUserActionsListener.loadChartData(historicalDataList, day);
                changeButtonColor(view.getId());
            } else {
                chart.setData(null);
                chart.invalidate();
                changeButtonColor(view.getId());
                float percentage = 0;
                updatePercentage("(" + getTwoDecimalPointValue(percentage) + "%)", percentage);
                showPercentage(false);
                if (!isFailedYearly) {
                    showYearProgressBar(true, false);
                } else {
                    showTryAgain(true);
                }
            }

            xAxis.setValueFormatter(new DateMonFormatter());
            chart.setMarkerView(dateMonthChartMarkerView);

//            mUserActionsListener.loadHistoricalData(coin.getSymbol(),"USD", day);
        }
    }

    private void initialLoadChartData() {

        switch (DetailActivity.currentChartdataButton) {
            case R.id.day7Button:
                onClick(day7Button);
                break;
            case R.id.month1Button:
                onClick(month1Button);
                break;
            case R.id.month3Button:
                onClick(month3Button);
                break;
            case R.id.year1Button:
                onClick(year1Button);
                break;
            case R.id.hour24Button:
                onClick(hour24Button);
                break;
            case R.id.allButton:
                onClick(allButton);
                break;
        }
    }

    private LineData getGraphData(ArrayList<Datum> historicalData) {

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        int i = 0;
        for (Datum datum : historicalData) {
            double open = datum.getOpen();
            float val = (float) open;
            yVals.add(new Entry(datum.getTime(), val));
            i++;
        }
        if (yVals.size() == 0) {
            return null;
        }
        // create historicalDataList dataset and give it historicalDataList type
        Collections.sort(yVals, new EntryXComparator());
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        if (isAttached) {

            set1.setDrawFilled(true);
            set1.setColor(getResources().getColor(R.color.coin_header));
            set1.setFillColor(getResources().getColor(R.color.coin_header));

            set1.setDrawValues(false);
            set1.setLineWidth(1f);

            set1.setDrawCircles(false);
            set1.setColor(getResources().getColor(R.color.coin_header));
            set1.setDrawValues(false);
        }
        // create historicalDataList data object with the datasets

        LineData data = new LineData(set1);
        return data;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isAttached = true;
    }

    @Override
    public void onDetach() {
        detailActivity.removeCoinUpdator(this);
        isAttached = false;
        detailActivity.removeChildReceiverListner(this);
        super.onDetach();
    }

    @Override
    public void displayChartData(ArrayList<Datum> data) {
        if (data != null && isAttached) {
            if(data!=null && data.size()>0){
                LineData lineData = getGraphData(data);
                chart.setData(lineData);
                chart.notifyDataSetChanged();
                chart.invalidate();
                tryAgainLinearLayout.setVisibility(View.GONE);
            }

        }
    }

    @Override
    public void displayChartDataFor24Hrs(ArrayList<Datum> data) {
        if (is24Hour) {
            if (data != null && data.size()>0 && isAttached) {
                LineData lineData = getGraphData(data);
                chart.setData(lineData);
                chart.notifyDataSetChanged();
                chart.invalidate();
                tryAgainLinearLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void set24HourData(ArrayList<Datum> data) {
        if (isAttached) {
            if (data != null) {
                hour24historicalDataList = data;
            } else {
                isFailed24Hours = true;
                showTryAgain(true);
            }
        }
    }

    @Override
    public void showPercentage(boolean isDisplay) {
        if (isDisplay) {
            profitTextView.setVisibility(View.VISIBLE);
        } else {
            profitTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void showYearProgressBar(boolean isDisplay, boolean is24Hour) {
        if (!isAttached) {
            return;
        }
        if (this.is24Hour == is24Hour) {
            if (isDisplay) {
                chart.setVisibility(View.GONE);
                showTryAgain(false);
                yearProgressBar.setVisibility(View.VISIBLE);

            } else {
                yearProgressBar.setVisibility(View.GONE);
                chart.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void showTryAgain(boolean isDisply) {
        if (!isAttached) {
            return;
        }
        if (isDisply) {
            tryAgainLinearLayout.setVisibility(View.VISIBLE);
            yearProgressBar.setVisibility(View.GONE);
            chart.setVisibility(View.GONE);
        } else {
            tryAgainLinearLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void updatePercentage(String percentageString, float percentage) {
        if (isAttached) {
            String price = ": " + getFormattedPrice(coin.getPriceUsd());
            priceTextView.setText(price);
            profitTextView.setText(getTwoDecimalPointValue(percentageString));
            if (percentage >= 0) {
                profitTextView.setTextColor(getResources().getColor(R.color.green));
            } else {
                profitTextView.setTextColor(getResources().getColor(R.color.red));
            }
        }
    }

    void changeButtonColor(int id) {
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

    public void chartDatafetched(ArrayList<Datum> data) {
        if (isAttached) {
            if (data != null) {
                historicalDataList = data;
                if (isAttached) {
                    if (data.size() > 0) {
                        if (!is24Hour) {
                            initialLoadChartData();
                        }
                    } else {
                        showYearProgressBar(false, is24Hour);
                        if (!is24Hour) {
                            showTryAgain(true);
                        }
                        isFailedYearly = true;
                    }
                }
            } else {
                if (!is24Hour) {
                    showYearProgressBar(false, is24Hour);
                    showTryAgain(true);
                }
                isFailedYearly = true;
            }
        }
    }

    @Override
    public void updateCoin(Coin coin) {
        if (isAttached) {
            this.coin = coin;
            String price = ": " + getFormattedPrice(coin.getPriceUsd());
            priceTextView.setText(price);
        }
    }

    @Override
    public void onChildNetworkAvailable() {
        if (hour24historicalDataList == null) {
            isFailed24Hours = false;
            mUserActionsListener.load24HourData(coin.getSymbol(), "USD", 24);

        } else if (hour24historicalDataList.size() == 0) {
            isFailed24Hours = false;
            mUserActionsListener.load24HourData(coin.getSymbol(), "USD", 24);
        }

        if (historicalDataList == null) {
            isFailedYearly = false;
            if (!is24Hour) {
                showYearProgressBar(true, is24Hour);
            }
            detailActivity.reLoadChartData();
        } else if (historicalDataList.size() == 0) {
            isFailedYearly = false;
            if (!is24Hour) {
                showYearProgressBar(true, is24Hour);
            }
            detailActivity.reLoadChartData();
        }
    }

    @Override
    public void onChildNetworkUnavailable() {

    }
}
