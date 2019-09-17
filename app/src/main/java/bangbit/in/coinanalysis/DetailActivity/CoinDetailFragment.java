package bangbit.in.coinanalysis.DetailActivity;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.util.ArrayList;

import bangbit.in.coinanalysis.CoinDetailFragment.ChartFragment;
import bangbit.in.coinanalysis.CoinDetailFragment.GeneralFragment;
import bangbit.in.coinanalysis.CoinDetailFragment.HistoricalDataFragment;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.Datum;

import static bangbit.in.coinanalysis.Constant.COIN;
import static bangbit.in.coinanalysis.Constant.CURRENCY;

public class CoinDetailFragment extends Fragment implements View.OnClickListener {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private TextView generalButton, chartButton, historicalButton;
    Coin coin;
    String TAG = CoinDetailFragment.class.getSimpleName();
    private String currency;
    public static int currentPage = 0;
    private DetailActivity detailActivity;
    private ChartFragment chartFragment;
    private HistoricalDataFragment historicalDataFragment;
    FrameLayout generalFrameLayout, chartFrameLayout, historicalFrameLayout;


    public CoinDetailFragment() {
        // Required empty public constructor
    }


    public void selected() {

    }

    public static CoinDetailFragment newInstance(Coin coin, String currency) {
        CoinDetailFragment fragment = new CoinDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(COIN, coin);
        args.putString(CURRENCY, currency);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        detailActivity = (DetailActivity) getActivity();
        if (getArguments() != null) {
            coin = getArguments().getParcelable(COIN);
            currency = getArguments().getString(CURRENCY);
        }

    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_coin_detail, container, false);
        generalButton = view.findViewById(R.id.general_button);
        chartButton = view.findViewById(R.id.chart_button);
        historicalButton = view.findViewById(R.id.historical_button);
        generalButton.setOnClickListener(this);
        chartButton.setOnClickListener(this);
        historicalButton.setOnClickListener(this);
        chartFragment = ChartFragment.newInstance(null, null);
        historicalDataFragment = HistoricalDataFragment.newInstance(null, null);
        generalFrameLayout = view.findViewById(R.id.container_general);
        chartFrameLayout = view.findViewById(R.id.container_chart);
        historicalFrameLayout = view.findViewById(R.id.container_historical);



        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_general, GeneralFragment.newInstance(coin));
//        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.commit();

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_chart, chartFragment);
        fragmentTransaction.commit();

        fragmentManager = getFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_historical, historicalDataFragment);
        fragmentTransaction.commit();
        return view;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (CoinDetailFragment.currentPage == 1 && DetailActivity.currentPage == 0) {
                detailActivity.displayTabandToolbar(false);
            }

        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            detailActivity.displayTabandToolbar(true);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        currentPage = 0;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.general_button:
                generalFrameLayout.setVisibility(View.VISIBLE);
                chartFrameLayout.setVisibility(View.GONE);
                historicalFrameLayout.setVisibility(View.GONE);
                currentPage = 0;
                detailActivity.displayTabandToolbar(true);
                break;
            case R.id.chart_button:
                generalFrameLayout.setVisibility(View.GONE);
                chartFrameLayout.setVisibility(View.VISIBLE);
                historicalFrameLayout.setVisibility(View.GONE);
                currentPage = 1;
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    detailActivity.displayTabandToolbar(false);
                }
                break;
            case R.id.historical_button:
                generalFrameLayout.setVisibility(View.GONE);
                chartFrameLayout.setVisibility(View.GONE);
                historicalFrameLayout.setVisibility(View.VISIBLE);
                currentPage = 2;
                detailActivity.displayTabandToolbar(true);
                break;
        }
        changeButtonColor(v.getId());
    }

    void changeButtonColor(int id) {
        generalButton.setBackground(getResources().getDrawable(R.drawable.rounded_pink_unfilled_button));
        chartButton.setBackground(getResources().getDrawable(R.drawable.rounded_pink_unfilled_button));
        historicalButton.setBackground(getResources().getDrawable(R.drawable.rounded_pink_unfilled_button));
        generalButton.setTextColor(getResources().getColor(R.color.coin_header));
        chartButton.setTextColor(getResources().getColor(R.color.coin_header));
        historicalButton.setTextColor(getResources().getColor(R.color.coin_header));
        switch (id) {
            case R.id.general_button:
                generalButton.setBackground(getResources().getDrawable(R.drawable.rounded_pink_filled_button));
                generalButton.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.chart_button:
                chartButton.setBackground(getResources().getDrawable(R.drawable.rounded_pink_filled_button));
                chartButton.setTextColor(getResources().getColor(R.color.white));
                break;
            case R.id.historical_button:
                historicalButton.setBackground(getResources().getDrawable(R.drawable.rounded_pink_filled_button));
                historicalButton.setTextColor(getResources().getColor(R.color.white));
                break;
        }
    }

    public void chartDatafetched(ArrayList<Datum> data) {
        if (chartFragment!=null) {
            Log.d(TAG, "chartDatafetched: chartFragment");
            chartFragment.chartDatafetched(data);
        }
        if (historicalDataFragment!=null) {
            Log.d(TAG, "chartDatafetched: historicalDataFragment");
            historicalDataFragment.chartDatafetched(data);
        }
    }
}
