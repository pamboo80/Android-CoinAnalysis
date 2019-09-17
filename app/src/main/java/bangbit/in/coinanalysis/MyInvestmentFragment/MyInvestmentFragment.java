package bangbit.in.coinanalysis.MyInvestmentFragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import bangbit.in.coinanalysis.DetailActivity.CoinUpdator;
import bangbit.in.coinanalysis.DetailActivity.DetailActivity;
import bangbit.in.coinanalysis.EditAddInvestmentActivity.EditAddInvestmentActivity;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.Investment;
import bangbit.in.coinanalysis.repository.InvestmentRepository;

import static bangbit.in.coinanalysis.Constant.ADD_FRAGMENT;
import static bangbit.in.coinanalysis.Constant.COIN;
import static bangbit.in.coinanalysis.Constant.COIN_IMAGE_URL;
import static bangbit.in.coinanalysis.Constant.CURRENCY;
import static bangbit.in.coinanalysis.Constant.EDIT_FRAGMENT;
import static bangbit.in.coinanalysis.Constant.INVESTMENT;
import static bangbit.in.coinanalysis.Constant.LOAD_FRAGMENT;
import static bangbit.in.coinanalysis.Constant.SORT_BY_BOUGHT_PRICE;
import static bangbit.in.coinanalysis.Constant.SORT_BY_COST;
import static bangbit.in.coinanalysis.Constant.SORT_BY_QUANTITY;
import static bangbit.in.coinanalysis.MyApplication.currencySymbol;

public class MyInvestmentFragment extends Fragment implements
        MyInvestmentFragmentContract.View, ListItemListner, CoinUpdator {


    public static Coin coin;
    private MyInvestmentFragmentContract.UserActionsListener mUserActionsListener;
    private InvestmentAdapter investmentAdapter;
    private int ADDINVESTMENT = 100;

    TextView totalMArketValueTextView, gainLossTextView, currentPriceTextView;
    private String imageUrl;
    private String TAG = MyInvestmentFragment.class.getSimpleName();
    private TextView noInvestmentTextView;
    private TextView costTextView;
    private TextView boughtPriceTextView;
    private TextView quanitytyTextView;
    int sort = SORT_BY_QUANTITY;
    boolean initialLoad = false;
    boolean isArrowUp = true;
    private TextView total_bought_TextView;
    private RecyclerView investmentRecyclerView;
    private boolean isAttached = false;
    private DetailActivity detailActivity;
    private FloatingActionButton floatingActionButton;

    public MyInvestmentFragment() {
    }

    public static MyInvestmentFragment newInstance(Coin coin, String currency, String imageUrl) {
        MyInvestmentFragment fragment = new MyInvestmentFragment();
        Bundle args = new Bundle();
        args.putParcelable(COIN, coin);
        args.putString(CURRENCY, currency);
        args.putString(COIN_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    public void selected() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            coin = getArguments().getParcelable(COIN);
            imageUrl = getArguments().getString(COIN_IMAGE_URL);
//            currency = getArguments().getString(CURRENCY);
        }
        if (getActivity() instanceof DetailActivity) {
            detailActivity = (DetailActivity) getActivity();
            detailActivity.addCoinUpdator(this);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_investment, container, false);
        noInvestmentTextView = view.findViewById(R.id.no_investment_TextView);
        quanitytyTextView = view.findViewById(R.id.quantity_textView);
        boughtPriceTextView = view.findViewById(R.id.bought_price_textView);
        costTextView = view.findViewById(R.id.cost_textView);
        total_bought_TextView = view.findViewById(R.id.textView_total_bought_value);

        InvestmentRepository investmentRepository = new InvestmentRepository(getContext());
        mUserActionsListener = new MyInvestmentFragmentPresenter(this, investmentRepository);

        investmentRecyclerView = view.findViewById(R.id.investment_recycleView);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        investmentRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        investmentRecyclerView.setLayoutManager(linearLayoutManager);
        investmentAdapter = new InvestmentAdapter(null, this);
        investmentRecyclerView.setAdapter(investmentAdapter);


        totalMArketValueTextView = view.findViewById(R.id.textView_total_market_value_usd);
        gainLossTextView = view.findViewById(R.id.textView_gain_loss);
        currentPriceTextView = view.findViewById(R.id.textView_current_price_value);

        totalMArketValueTextView.setText(currencySymbol + "0.00");
        gainLossTextView.setText("0%");

        String priceUsd = Util.getFormattedPrice(coin.getPriceUsd());
        currentPriceTextView.setText(priceUsd);

        floatingActionButton = view.findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditAddInvestmentActivity.class);
                intent.putExtra(COIN, coin);
                intent.putExtra(COIN_IMAGE_URL, imageUrl);
                intent.putExtra(LOAD_FRAGMENT, ADD_FRAGMENT);
                startActivityForResult(intent, ADDINVESTMENT);

            }
        });
        mUserActionsListener.loadInvestmentData(coin.getSymbol());
        quanitytyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sort == SORT_BY_QUANTITY) {
                    isArrowUp = !isArrowUp;
                    mUserActionsListener.reverseData(investmentAdapter.getInvestmentList());
                } else {
                    isArrowUp = true;
                    sort = SORT_BY_QUANTITY;
                    mUserActionsListener.sortData(investmentAdapter.getInvestmentList(), sort);
                }
                updateIcon(sort);
            }
        });
        boughtPriceTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (sort == SORT_BY_BOUGHT_PRICE) {
                    isArrowUp = !isArrowUp;
                    mUserActionsListener.reverseData(investmentAdapter.getInvestmentList());
                } else {
                    isArrowUp = true;
                    sort = SORT_BY_BOUGHT_PRICE;
                    mUserActionsListener.sortData(investmentAdapter.getInvestmentList(), sort);
                }
                updateIcon(sort);
            }
        });
        costTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sort == SORT_BY_COST) {
                    isArrowUp = !isArrowUp;
                    mUserActionsListener.reverseData(investmentAdapter.getInvestmentList());
                } else {
                    isArrowUp = true;
                    sort = SORT_BY_COST;
                    mUserActionsListener.sortData(investmentAdapter.getInvestmentList(), sort);
                }
                updateIcon(sort);
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADDINVESTMENT) {
            mUserActionsListener.loadInvestmentData(coin.getSymbol());
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isAttached = true;
    }

    @Override
    public void onDetach() {
        isAttached = false;
        if (detailActivity != null) {
            detailActivity.removeCoinUpdator(this);
        }
        super.onDetach();
    }

    @Override
    public void displayInvestmentData(ArrayList<Investment> investmentList) {
        if (initialLoad == false) {
            initialLoad = true;
            investmentAdapter.setInvestmentList(investmentList);
            mUserActionsListener.sortData(investmentList, sort);

            mUserActionsListener.calculateTotalAndGain(investmentList, Float.parseFloat(coin.getPriceUsd()));

        } else {
            investmentAdapter.setInvestmentList(investmentList);
            mUserActionsListener.calculateTotalAndGain(investmentList, Float.parseFloat(coin.getPriceUsd()));
        }
    }

    @Override
    public void displayNoInvestmentAvailable(boolean active) {
        if (active) {
            noInvestmentTextView.setVisibility(View.VISIBLE);
            investmentRecyclerView.setVisibility(View.GONE);
        } else {
            noInvestmentTextView.setVisibility(View.GONE);
            investmentRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void updateTotalAndGain(String total_current, String total_bought, String gain, boolean isGreen) {

        gainLossTextView.setText(gain);
        totalMArketValueTextView.setText(currencySymbol + Util.checkForNull(total_current));
        total_bought_TextView.setText(currencySymbol + Util.checkForNull(total_bought));
        if (isGreen) {
            gainLossTextView.setTextColor(getContext().getResources().getColor(R.color.green));
        } else {
            gainLossTextView.setTextColor(getContext().getResources().getColor(R.color.red));
        }
    }

    @Override
    public void onItemClickListner(Investment investment) {
        Intent intent = new Intent(getActivity(), EditAddInvestmentActivity.class);
        intent.putExtra(COIN, coin);
        intent.putExtra(INVESTMENT, investment);
        intent.putExtra(LOAD_FRAGMENT, EDIT_FRAGMENT);
        intent.putExtra(COIN_IMAGE_URL, imageUrl);
        startActivityForResult(intent, ADDINVESTMENT);
    }

    void updateIcon(int sort) {
        quanitytyTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        boughtPriceTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        costTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        switch (sort) {
            case SORT_BY_QUANTITY:
                if (isArrowUp) {
                    quanitytyTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.up_arrow), null);
                } else {
                    quanitytyTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.down_arrow), null);
                }
                break;
            case SORT_BY_BOUGHT_PRICE:
                if (isArrowUp) {
                    boughtPriceTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.up_arrow), null);
                } else {
                    boughtPriceTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.down_arrow), null);
                }
                break;
            case SORT_BY_COST:
                if (isArrowUp) {
                    costTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.up_arrow), null);
                } else {
                    costTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.down_arrow), null);
                }
                break;
        }
    }

    @Override
    public void updateCoin(Coin coin) {

        if (isAttached) {
            MyInvestmentFragment.coin = coin;
            String priceUsd = Util.getFormattedPrice(coin.getPriceUsd());
            currentPriceTextView.setText(priceUsd);
            mUserActionsListener.loadInvestmentData(coin.getSymbol());
        }

    }


}
