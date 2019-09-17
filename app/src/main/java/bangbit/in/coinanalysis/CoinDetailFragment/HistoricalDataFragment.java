package bangbit.in.coinanalysis.CoinDetailFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;

import bangbit.in.coinanalysis.BottomSheetPair.DateBottomSheetFragment;
import bangbit.in.coinanalysis.ChildNetworkAvailable;
import bangbit.in.coinanalysis.DetailActivity.DetailActivity;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Datum;

import static bangbit.in.coinanalysis.Constant.SORT_BY_CLOSE;
import static bangbit.in.coinanalysis.Constant.SORT_BY_DATE;
import static bangbit.in.coinanalysis.Constant.SORT_BY_HIGH;
import static bangbit.in.coinanalysis.Constant.SORT_BY_LOW;
import static bangbit.in.coinanalysis.Constant.SORT_BY_OPEN;

public class HistoricalDataFragment extends Fragment implements HistoricalFragmentContract.View,
        View.OnClickListener, ChildNetworkAvailable {

    private Calendar fromDateCalendar = Calendar.getInstance();
    private Calendar toDateCalendar = Calendar.getInstance();
    private ArrayList<Datum> historicalDataList;
    private RecyclerView recyclerView;
    private HistoricalFragmentContract.UserActionsListener userActionsListener;
    private TextView strtDateTextView;
    private TextView endDateTextView;

    TextView dateTextView, openTextView, closeTextView, highTextView, lowTextView;
    private String firstDate = "";
    private String endDate = "";
    private HistoricalDataAdapter historicalDataAdapter;
    int sort = SORT_BY_DATE;
    private boolean isArrowUp = DetailActivity.isArrowUpHistorical;
    private DetailActivity detailActivity;
    private TextView nodataTextView;
    private boolean isAttached = false;
    private boolean isDataFetched = false;
    private TextView tryAgainButtonTextView;
    private ProgressBar progressBar;

    public HistoricalDataFragment() {
        // Required empty public constructor
    }

    public static HistoricalDataFragment newInstance(String param1, String param2) {
        HistoricalDataFragment fragment = new HistoricalDataFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailActivity = (DetailActivity) getActivity();
        historicalDataList = detailActivity.getData();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coin_detail_historical_data, container, false);
        detailActivity.addChildReceiverListner(this);
        progressBar = view.findViewById(R.id.progressBar);
        userActionsListener = new HistoricalFragmentPresenter(this);
        recyclerView = view.findViewById(R.id.historical_recycleview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        strtDateTextView = view.findViewById(R.id.start_date_editText);
        endDateTextView = view.findViewById(R.id.end_date_editText);
        nodataTextView = view.findViewById(R.id.no_data_TextView);
        tryAgainButtonTextView = view.findViewById(R.id.try_again_Button);

        dateTextView = view.findViewById(R.id.date_textView);
        openTextView = view.findViewById(R.id.open_textView);
        closeTextView = view.findViewById(R.id.close_textView);
        highTextView = view.findViewById(R.id.high_textView);
        lowTextView = view.findViewById(R.id.low_textView);

        dateTextView.setOnClickListener(this);
        openTextView.setOnClickListener(this);
        closeTextView.setOnClickListener(this);
        highTextView.setOnClickListener(this);
        lowTextView.setOnClickListener(this);
        updateIcon(R.id.date_textView);
        final Calendar calendar = Calendar.getInstance();
        final String todayDate = Util.getDayMonthYearFromUnixTime(calendar.getTimeInMillis() / 1000);
        endDateTextView.setText(todayDate);
        calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH) - 1);
        final String startDate = Util.getDayMonthYearFromUnixTime(calendar.getTimeInMillis() / 1000);
        strtDateTextView.setText(startDate);
        historicalDataAdapter = new HistoricalDataAdapter(null);
        recyclerView.setAdapter(historicalDataAdapter);
        if (historicalDataList != null) {
            progressBar.setVisibility(View.GONE);
            userActionsListener.loadHistoricalData(historicalDataList, 30);
        }

        strtDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateBottomSheetFragment dateBottomSheetFragment = DateBottomSheetFragment.newInstance(new DateBottomSheetFragment.OnDateInteractionListener() {
                    @Override
                    public void onDateInteraction(int date, int month, int year, String monthString) {
                        int tempYear = year - 2000;
                        String dateString = String.format("%02d", date);
                        String yearString = String.format("%02d", tempYear);
                        strtDateTextView.setText(dateString + "/" + monthString + "/" + yearString);
                        fromDateCalendar.set(Calendar.MONTH, month - 1);
                        fromDateCalendar.set(Calendar.DATE, date);
                        fromDateCalendar.set(Calendar.YEAR, year);

                        userActionsListener.filterData(historicalDataList,
                                (fromDateCalendar.getTimeInMillis() / 1000) + 86400, (toDateCalendar.getTimeInMillis() / 1000) + 86400, //@@@
                                sort, isArrowUp);
                    }
                }, fromDateCalendar);
                dateBottomSheetFragment.show(getFragmentManager(), dateBottomSheetFragment.getTag());
            }
        });
        endDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateBottomSheetFragment dateBottomSheetFragment = DateBottomSheetFragment.newInstance(new DateBottomSheetFragment.OnDateInteractionListener() {
                    @Override
                    public void onDateInteraction(int date, int month, int year, String monthString) {

                        int tempYear = year - 2000;
                        String dateString = String.format("%02d", date);
                        String yearString = String.format("%02d", tempYear);
                        endDateTextView.setText(dateString + "/" + monthString + "/" + yearString);
                        toDateCalendar.set(Calendar.MONTH, month - 1);
                        toDateCalendar.set(Calendar.DATE, date);
                        toDateCalendar.set(Calendar.YEAR, year);

                        userActionsListener.filterData(historicalDataList,
                                (fromDateCalendar.getTimeInMillis() / 1000) + 86400, (toDateCalendar.getTimeInMillis() / 1000) + 86400, //@@@
                                sort, isArrowUp);
                    }
                }, toDateCalendar);
                dateBottomSheetFragment.show(getFragmentManager(), dateBottomSheetFragment.getTag());
            }
        });

        tryAgainButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailActivity.reLoadChartData();
                displayProgress(true);
            }
        });
        if (isDataFetched) {
            if (historicalDataList==null) {
                progressBar.setVisibility(View.GONE);
                displayTryAgain(true);
            }else {
                userActionsListener.loadHistoricalData(historicalDataList, 30);
                progressBar.setVisibility(View.GONE);
            }
        }
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isAttached = true;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isAttached = false;
        isDataFetched=false;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void displayHistoricalData(ArrayList<Datum> data) {
        if (firstDate.equals("") && endDate.equals("")) {
            if (data!=null) {
                Datum first = data.get(0);
                Datum last = data.get(data.size() - 1);
                firstDate = Util.getDayMonthYearFromUnixTime(first.getTime()-86400); //@@@
                endDate = Util.getDayMonthYearFromUnixTime(last.getTime());
                long firstMillisecond = first.getTime()-86400; //@@@
                long lastMillisecond = last.getTime();
                firstMillisecond = firstMillisecond * 1000;
                lastMillisecond = lastMillisecond * 1000;
                fromDateCalendar.setTimeInMillis(firstMillisecond);
                toDateCalendar.setTimeInMillis(lastMillisecond);
                strtDateTextView.setText(firstDate);
                endDateTextView.setText(endDate);
                Collections.reverse(data);
                displayTryAgain(false);
            }else {
                displayTryAgain(true);
            }
        }
        historicalDataAdapter.setHistoricalDataList(data);
    }

    @Override
    public void displayNoDataAvailable(boolean active) {
        if (active) {
            nodataTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            nodataTextView.setVisibility(View.GONE);
            tryAgainButtonTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void displayTryAgain(boolean isDisplay) {
        if (isDisplay) {
            displayNoDataAvailable(isDisplay);
            tryAgainButtonTextView.setVisibility(View.VISIBLE);
        } else {
            tryAgainButtonTextView.setVisibility(View.GONE);
        }
    }

    void displayProgress(boolean isDisplay) {
        if (isDisplay) {
            progressBar.setVisibility(View.VISIBLE);
            nodataTextView.setVisibility(View.GONE);
            tryAgainButtonTextView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    void updateIcon(int id) {
        dateTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        openTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        closeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        highTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        lowTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);

        switch (id) {
            case R.id.date_textView:
                if (isArrowUp) {
                    dateTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.down_arrow), null);
                } else {
                    dateTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.up_arrow), null);
                }
                break;
            case R.id.open_textView:
                if (isArrowUp) {
                    openTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.down_arrow), null);
                } else {
                    openTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.up_arrow), null);
                }
                break;
            case R.id.close_textView:
                if (isArrowUp) {
                    closeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.down_arrow), null);
                } else {
                    closeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.up_arrow), null);
                }
                break;
            case R.id.high_textView:
                if (isArrowUp) {
                    highTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.down_arrow), null);
                } else {
                    highTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.up_arrow), null);
                }
                break;
            case R.id.low_textView:
                if (isArrowUp) {
                    lowTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.down_arrow), null);
                } else {
                    lowTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.up_arrow), null);
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        if (historicalDataAdapter == null ||
                historicalDataAdapter.getHistoricalDataList() == null ||
                historicalDataAdapter.getHistoricalDataList().size() == 0) {
            return;
        }
        int oldsort = sort;
        switch (v.getId()) {
            case R.id.date_textView:
                sort = SORT_BY_DATE;
                break;
            case R.id.open_textView:
                sort = SORT_BY_OPEN;
                break;
            case R.id.close_textView:
                sort = SORT_BY_CLOSE;
                break;
            case R.id.high_textView:
                sort = SORT_BY_HIGH;
                break;
            case R.id.low_textView:
                sort = SORT_BY_LOW;
                break;
        }
        if (v.getId() == R.id.date_textView || v.getId() == R.id.open_textView || v.getId() == R.id.close_textView ||
                v.getId() == R.id.high_textView || v.getId() == R.id.low_textView) {
            if (oldsort == sort) {
                isArrowUp = !isArrowUp;
                userActionsListener.reverse(historicalDataAdapter.getHistoricalDataList());
            } else {
                isArrowUp = false;
                userActionsListener.sortData(sort, historicalDataAdapter.getHistoricalDataList());
            }
            updateIcon(v.getId());
        }
    }

    // from the Activity
    public void chartDatafetched(final ArrayList<Datum> data) {
        isDataFetched=true;
        if (isAttached) {
            if (data != null) {
                historicalDataList = data;
                if (isAttached) {
                    userActionsListener.loadHistoricalData(historicalDataList, 30);
                    progressBar.setVisibility(View.GONE);
                }
            } else if (data == null) {
                progressBar.setVisibility(View.GONE);
                displayTryAgain(true);
            }
        }
    }

    @Override
    public void onChildNetworkAvailable() {
        if (historicalDataList == null) {
            detailActivity.reLoadChartData();
            displayProgress(true);
        }
    }

    @Override
    public void onChildNetworkUnavailable() {

    }
}
