package bangbit.in.coinanalysis.DashboardInvestmentActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import bangbit.in.coinanalysis.BaseActivity;
import bangbit.in.coinanalysis.ChildNetworkAvailable;
import bangbit.in.coinanalysis.Constant;
import bangbit.in.coinanalysis.DashBoardAddInvestment.DashAddEditInvestmentActivity;
import bangbit.in.coinanalysis.ListUpdatorListner;
import bangbit.in.coinanalysis.MyApplication;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.broadcast.NetworkStateReceiver;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.DashInvestment;
import bangbit.in.coinanalysis.repository.CoinRepository;
import bangbit.in.coinanalysis.repository.InvestmentRepository;

import static bangbit.in.coinanalysis.Constant.ADD_FRAGMENT;
import static bangbit.in.coinanalysis.Constant.COIN;
import static bangbit.in.coinanalysis.Constant.COIN_IMAGE_URL;
import static bangbit.in.coinanalysis.Constant.CURRENCY;
import static bangbit.in.coinanalysis.Constant.LOAD_FRAGMENT;
import static bangbit.in.coinanalysis.Constant.MY_INVESTMENT_FRAGMENT;
import static bangbit.in.coinanalysis.Constant.SORT_BY_COIN;
import static bangbit.in.coinanalysis.Constant.SORT_BY_COST;
import static bangbit.in.coinanalysis.Constant.SORT_BY_INVESTMENT;
import static bangbit.in.coinanalysis.MyApplication.currencySymbol;

public class DashInvestmentActivity extends BaseActivity implements RecyclerViewItemClick,
        DashInvestmentActivityContract.View, ListUpdatorListner, View.OnClickListener,
        ChildNetworkAvailable {

    private String TAG = DashInvestmentActivity.class.getSimpleName();
    private TextView textViewTotalMarketValuePrice;
    private TextView textViewProfit;

    RecyclerViewItemClick recyclerViewItemClick = this;
    private ArrayList<Coin> coinArrayList;

    private RecyclerView recyclerView;
    DashInvestmentActivityContract.UserActionsListener mUserActionsListener;
    private TextView myTotalCostTextView;
    static final int DASH_ADD_INVESTMENT_ACTIVITY = 100;
    static final int DASH_COIN_INVESTMENT_ACTIVITY = 101;
    TextView noInvestmentTextView;
    private FloatingActionButton floatingActionButton;

    int sort = Constant.SORT_BY_COIN;
    private boolean isArrowUp = true;
    private TextView sortCoinTextView;
    private TextView sortInvestmentTextView;
    private TextView sortCostTextView;
    private DashInvestmentRecyclerViewAdapter mainInvestmentRecyclerViewAdapter;
    private ArrayList<DashInvestment> dashInvestments = new ArrayList<>();
    private NetworkStateReceiver networkStateReceiver;

    @Override
    public void displayData(ArrayList<DashInvestment> dashInvestments) {
        mainInvestmentRecyclerViewAdapter.setDashInvestments(dashInvestments);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addChildReceiverListner(this);
        setContentView(R.layout.activity_main_investment);
        ImageView backImageView = findViewById(R.id.back_imageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        sortCoinTextView = findViewById(R.id.sort_coin_TextView);
        sortInvestmentTextView = findViewById(R.id.sort_investment_TextView);
        sortCostTextView = findViewById(R.id.sort_cost_TextView);
        textViewTotalMarketValuePrice = findViewById(R.id.textView_total_market_value_usd);
        myTotalCostTextView = findViewById(R.id.textView_current_total_cost);
        noInvestmentTextView = findViewById(R.id.no_investment_TextView);

        textViewProfit = findViewById(R.id.textView_gain_loss);

        coinArrayList = ((MyApplication) getApplicationContext()).coinArrayList;

        CoinRepository coinRepository = new CoinRepository(this);
        InvestmentRepository investmentRepository = new InvestmentRepository(this);
        mUserActionsListener =
                new DashInvestmentActivityPresenter(this, investmentRepository, coinRepository);

        recyclerView = findViewById(R.id.investment_recycleView);

        recyclerView.addItemDecoration(new DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        mainInvestmentRecyclerViewAdapter = new DashInvestmentRecyclerViewAdapter(dashInvestments, recyclerViewItemClick);
        recyclerView.setAdapter(mainInvestmentRecyclerViewAdapter);

        floatingActionButton = findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashInvestmentActivity.this, DashAddEditInvestmentActivity.class);
                intent.putExtra(LOAD_FRAGMENT, ADD_FRAGMENT);
                startActivityForResult(intent, DASH_ADD_INVESTMENT_ACTIVITY);
            }
        });

        updateIcon(sort);
        if (coinArrayList != null) {
            mUserActionsListener.prepareDataForRecycleView(coinArrayList, sort, isArrowUp);
        } else {
            noInvestmentTextView.setText(R.string.you_are_offline);
            floatingActionButton.setVisibility(View.GONE);
            totalCalculation("0.00", "0.00", "0.00", true);
            showNoInvestmentAvailable(true);
        }

        sortCoinTextView.setOnClickListener(this);
        sortInvestmentTextView.setOnClickListener(this);
        sortCostTextView.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyApplication) getApplicationContext()).addListUpdatorListners(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DASH_ADD_INVESTMENT_ACTIVITY | requestCode == DASH_COIN_INVESTMENT_ACTIVITY) {
            mUserActionsListener.prepareDataForRecycleView(coinArrayList, sort, isArrowUp);
        }
    }

    @Override
    public void itemClick(DashInvestment dashInvestment) {
        Coin mainCoin = null;
        for (Coin coin : coinArrayList) {
            if (coin.getSymbol().equals(dashInvestment.getSymbol())) {
                mainCoin = coin;
                break;
            }
        }
        if (mainCoin != null) {
            Intent intent = new Intent(this, DashAddEditInvestmentActivity.class);
            intent.putExtra(COIN, mainCoin);
            intent.putExtra(CURRENCY, "USD");
            intent.putExtra(COIN_IMAGE_URL, dashInvestment.getImageUrl());
            intent.putExtra(LOAD_FRAGMENT, MY_INVESTMENT_FRAGMENT);
            startActivityForResult(intent, DASH_COIN_INVESTMENT_ACTIVITY);
        }
    }


    @Override
    public void totalCalculation(String profit, String totalCurrentvalue, String totalInvestmentString, boolean isColorGreen) {

        textViewProfit.setText(profit + "%");
        if (isColorGreen) {
            textViewProfit.setTextColor(getResources().getColor(R.color.green));
        } else {
            textViewProfit.setTextColor(getResources().getColor(R.color.red));
        }

        textViewTotalMarketValuePrice.setText(currencySymbol + Util.checkForNull(totalCurrentvalue));
        myTotalCostTextView.setText(currencySymbol + Util.checkForNull(totalInvestmentString));
    }

    @Override
    public void showNoInvestmentAvailable(boolean active) {
        if (active) {
            noInvestmentTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            noInvestmentTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onDestroy() {
        ((MyApplication) getApplicationContext()).removeListUpdatorListners(this);
        removeChildReceiverListner(this);
        super.onDestroy();
    }

    @Override
    public void updateList(ArrayList<Coin> coinArrayList) {
        floatingActionButton.setVisibility(View.VISIBLE);
        this.coinArrayList = ((MyApplication) getApplicationContext()).coinArrayList;
        mUserActionsListener.prepareDataForRecycleView(coinArrayList, sort, isArrowUp);
    }

    @Override
    public void onChildNetworkAvailable() {
        if (coinArrayList!=null) {
            floatingActionButton.setVisibility(View.VISIBLE);
        }

        if (noInvestmentTextView.getVisibility()== View.VISIBLE) {
            noInvestmentTextView.setText(R.string.no_investment_available);
        }
    }

    @Override
    public void onChildNetworkUnavailable() {

    }

    @Override
    public void onClick(View v) {
        int oldSort = sort;
        switch (v.getId()) {
            case R.id.sort_coin_TextView:
                sort = SORT_BY_COIN;
                break;
            case R.id.sort_investment_TextView:
                sort = SORT_BY_INVESTMENT;
                break;
            case R.id.sort_cost_TextView:
                sort = SORT_BY_COST;
                break;
        }
        if (v.getId() == R.id.sort_coin_TextView ||
                v.getId() == R.id.sort_investment_TextView ||
                v.getId() == R.id.sort_cost_TextView) {
            if (oldSort == sort) {
                isArrowUp = !isArrowUp;
                mUserActionsListener.reverse(mainInvestmentRecyclerViewAdapter.getDashInvestments());
            } else {
                isArrowUp = true;
                mUserActionsListener.sortData(sort, mainInvestmentRecyclerViewAdapter.getDashInvestments());
            }
            updateIcon(sort);
        }
    }

    void updateIcon(int sort) {
        sortCoinTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        sortInvestmentTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        sortCostTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        switch (sort) {
            case SORT_BY_COIN:
                if (isArrowUp) {
                    sortCoinTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.up_arrow), null);
                } else {
                    sortCoinTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.down_arrow), null);
                }
                break;
            case SORT_BY_INVESTMENT:
                if (isArrowUp) {
                    sortInvestmentTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.up_arrow), null);
                } else {
                    sortInvestmentTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.down_arrow), null);
                }
                break;
            case SORT_BY_COST:
                if (isArrowUp) {
                    sortCostTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.up_arrow), null);
                } else {
                    sortCostTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.down_arrow), null);
                }
                break;
        }
    }

}
