package bangbit.in.coinanalysis.WhereToBuy;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

import bangbit.in.coinanalysis.BottomSheetPair.BottomSheetPairFragment;
import bangbit.in.coinanalysis.ChildNetworkAvailable;
import bangbit.in.coinanalysis.DetailActivity.DetailActivity;
import bangbit.in.coinanalysis.Dialogs.WhereToBuyDialog;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.Currency;
import bangbit.in.coinanalysis.pojo.CurrencyExchange;
import bangbit.in.coinanalysis.pojo.Exchange;
import bangbit.in.coinanalysis.repository.HistoryRepository;

import static bangbit.in.coinanalysis.Constant.COIN;
import static bangbit.in.coinanalysis.Constant.CURRENCY;
import static bangbit.in.coinanalysis.Constant.SORT_BY_EXCHANGE;
import static bangbit.in.coinanalysis.Constant.SORT_BY_PRICE;
import static bangbit.in.coinanalysis.Constant.SORT_BY_VOLUME;

public class WhereToBuyFragment extends Fragment implements WhereToBuyFragmentContract.View,
        View.OnClickListener, BottomSheetPairFragment.OnFragmentInteractionListener,
        ItemClickListner, ChildNetworkAvailable {

    private Coin coin;
    private String currency = "BTC";
    public static String symbol = "BTC";
    public static boolean isCurrencyFiat = false;

    private RecyclerView recyclerView;
    WhereToBuyFragmentContract.UserActionsListener mUserActionsListener;
    private String TAG = WhereToBuyFragment.class.getSimpleName();
    private TextView maxTextView, minTextView, highOnTextView, lowOnTextView;
    private TextView sortExchangeTextView, sortPriceTextView, sortVolumeTextView;
    private WhereToBuyAdapter whereToBuyAdapter;
    private int sort = SORT_BY_VOLUME;
    private boolean isArrowUp = true;
    boolean initialLoad;
    boolean isAttached;
    private TextView noDataTextView;
    private TextView pairTextView;
    private ProgressDialog progressDialog;
    TextView lowFiatTextView, lowCryptoTextView, highFiatTextView, highCryptoTextView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private Handler handler;
    private Runnable backgroundLoad;
    ArrayList<CurrencyExchange> currencyExchangeList = Util.getCurrencyExchangeList();
    private View view;
    private Snackbar mySnackbar;
    private ProgressBar progressBar;
    private DetailActivity detailActivity;
    private TextView tryAgainButtonTextView;

    public WhereToBuyFragment() {
        // Required empty public constructor
    }

    public void selected() {
    }

    public static WhereToBuyFragment newInstance(Coin coin, String currency) {
        WhereToBuyFragment fragment = new WhereToBuyFragment();
        Bundle args = new Bundle();
        args.putParcelable(COIN, coin);
        args.putString(CURRENCY, currency);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            coin = getArguments().getParcelable(COIN);
//          currency = getArguments().getString(CURRENCY);
            if (coin.getSymbol().equalsIgnoreCase("btc")) {
                currency = "USD";
                symbol = "$";
                isCurrencyFiat = true;
            }
        }
        detailActivity = (DetailActivity) getActivity();
    }

    @Override
    public void showTryAgain(boolean isDisplay) {
        if (isDisplay) {
            noDataTextView.setVisibility(View.VISIBLE);
            tryAgainButtonTextView.setVisibility(View.VISIBLE);
        } else {
            tryAgainButtonTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_where_to_buy, container, false);
        HistoryRepository historyRepository = new HistoryRepository();
        mUserActionsListener = new WhereToBuyFragmentPresenter(this, historyRepository);
        detailActivity.addChildReceiverListner(this);
        tryAgainButtonTextView = view.findViewById(R.id.try_again_Button);
        progressBar = view.findViewById(R.id.progressBar);
        progressDialog = new ProgressDialog(getContext(), R.style.CustomProgressDialog);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        lowCryptoTextView = view.findViewById(R.id.low_crypto);
        lowFiatTextView = view.findViewById(R.id.low_fiat);
        highCryptoTextView = view.findViewById(R.id.high_crypto);
        highFiatTextView = view.findViewById(R.id.high_fiat);
        noDataTextView = view.findViewById(R.id.no_data_TextView);
        recyclerView = view.findViewById(R.id.where_to_buy_recycleView);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        whereToBuyAdapter = new WhereToBuyAdapter(null, this);
        recyclerView.setAdapter(whereToBuyAdapter);
        recyclerView.setLayoutManager(linearLayoutManager);
        minTextView = view.findViewById(R.id.low_price_textView);
        maxTextView = view.findViewById(R.id.high_price_textView);
        highOnTextView = view.findViewById(R.id.high_on_textView);
        lowOnTextView = view.findViewById(R.id.low_on_textView);

        sortExchangeTextView = view.findViewById(R.id.sort_exchange_textView);
//        sortPairTextView = view.findViewById(R.id.sort_pair_textView);
        sortPriceTextView = view.findViewById(R.id.sort_price_textView);
        sortVolumeTextView = view.findViewById(R.id.sort_volume_textView);
        pairTextView = view.findViewById(R.id.pair_textView);
        sortExchangeTextView.setOnClickListener(this);
//        sortPairTextView.setOnClickListener(this);
        sortPriceTextView.setOnClickListener(this);
        sortVolumeTextView.setOnClickListener(this);

        pairTextView.setText(coin.getSymbol() + "/" + currency);

        pairTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetPairFragment bottomSheetFragment =
                        BottomSheetPairFragment.newInstance(WhereToBuyFragment.this, currency, coin);

                bottomSheetFragment.show(getFragmentManager(), bottomSheetFragment.getTag());
            }
        });
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                mUserActionsListener.loadExchangeData(coin.getSymbol(), currency, false, false);
            }
        });

        tryAgainButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUserActionsListener.loadExchangeData(coin.getSymbol(), currency, false, false);
            }
        });
        mUserActionsListener.loadExchangeData(coin.getSymbol(), currency, false, true);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        handler = new Handler();
        backgroundLoad = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 120000);
                mUserActionsListener.loadExchangeData(coin.getSymbol(),
                        currency, true, false);
            }
        };
        handler.post(backgroundLoad);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isAttached = true;
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(backgroundLoad);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        isAttached = false;
        symbol = "BTC";
        isCurrencyFiat = false;
        handler.removeCallbacks(backgroundLoad);
        detailActivity.removeChildReceiverListner(this);
    }

    @Override
    public boolean isTryAgainVisible() {
        return (tryAgainButtonTextView.getVisibility() == View.VISIBLE);
    }

    @Override
    public int getExchangeSize() {
        return whereToBuyAdapter.getItemCount();
    }

    @Override
    public void displayExchangeData(ArrayList<Exchange> exchanges) {
        progressBar.setVisibility(View.GONE);
        if (exchanges != null && isAttached) {
            if (!initialLoad) {
                initialLoad = true;
                mUserActionsListener.sortData(exchanges, sort);
                updateIcon(R.id.sort_volume_textView);
                mUserActionsListener.reverseData(exchanges);
                mUserActionsListener.findMaxMin(exchanges);
            } else {
                whereToBuyAdapter.setExchanges(exchanges);
                if (exchanges.size() > 0) {
                    mUserActionsListener.sortData(exchanges, sort);
                    if (isArrowUp) {
                        mUserActionsListener.reverseData(exchanges);
                    }
                }
            }
            lowFiatTextView.setText(symbol);
            highFiatTextView.setText(symbol);
            lowCryptoTextView.setText(symbol);
            highCryptoTextView.setText(symbol);
            showIsCurrencyFiat(isCurrencyFiat);
            tryAgainButtonTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void displaySortedData(ArrayList<Exchange> exchanges) {
        whereToBuyAdapter.setExchanges(exchanges);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void displayMaxMin(String max, String min, String minMarket, String maxMarket) {
        if (min.equals("NA") && max.equals("NA")) {
            lowFiatTextView.setVisibility(View.GONE);
            highFiatTextView.setVisibility(View.GONE);
            lowCryptoTextView.setVisibility(View.GONE);
            highCryptoTextView.setVisibility(View.GONE);
        } else {
            showIsCurrencyFiat(isCurrencyFiat);
        }
        minTextView.setText(min);
        maxTextView.setText(max);
        highOnTextView.setText(maxMarket);
        lowOnTextView.setText(minMarket);
    }

    @Override
    public void showNoDataAvailable(boolean isActive) {
        if (isActive) {
            noDataTextView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);

            //for currency logo
            lowFiatTextView.setVisibility(View.GONE);
            highFiatTextView.setVisibility(View.GONE);
            lowCryptoTextView.setVisibility(View.GONE);
            highCryptoTextView.setVisibility(View.GONE);
        } else {
            noDataTextView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        if (whereToBuyAdapter.getExchanges() == null) {
            return;
        }
        int oldsort = sort;
        switch (v.getId()) {
            case R.id.sort_exchange_textView:
                sort = SORT_BY_EXCHANGE;
                break;
//            case R.id.sort_pair_textView:
//                sort = SORT_BY_PAIR;
//                break;
            case R.id.sort_price_textView:
                sort = SORT_BY_PRICE;
                break;
            case R.id.sort_volume_textView:
                sort = SORT_BY_VOLUME;
                break;
        }
        if (v.getId() == R.id.sort_exchange_textView ||
                v.getId() == R.id.sort_price_textView || v.getId() == R.id.sort_volume_textView) {
            if (oldsort == sort) {
                isArrowUp = !isArrowUp;
                mUserActionsListener.reverseData(whereToBuyAdapter.getExchanges());
            } else {
                isArrowUp = false;
                mUserActionsListener.sortData(whereToBuyAdapter.getExchanges(), sort);
            }
            if (isAttached) {
                updateIcon(v.getId());
            }
        }
    }

    void updateIcon(int id) {
        if (!isAttached) {
            return;
        }
        sortExchangeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        sortPriceTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
//        sortPairTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        sortVolumeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null);
        switch (id) {
            case R.id.sort_exchange_textView:
                if (isArrowUp) {
                    sortExchangeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.down_arrow), null);
                } else {
                    sortExchangeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.up_arrow), null);
                }
                break;
            case R.id.sort_price_textView:
                if (isArrowUp) {
                    sortPriceTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.down_arrow), null);
                } else {
                    sortPriceTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.up_arrow), null);
                }
                break;
            case R.id.sort_volume_textView:
                if (isArrowUp) {
                    sortVolumeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.down_arrow), null);
                } else {
                    sortVolumeTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null,
                            getResources().getDrawable(R.drawable.up_arrow), null);
                }
                break;
        }
    }

    void showIsCurrencyFiat(boolean display) {
        if (display) {
            lowFiatTextView.setVisibility(View.VISIBLE);
            highFiatTextView.setVisibility(View.VISIBLE);
            lowCryptoTextView.setVisibility(View.GONE);
            highCryptoTextView.setVisibility(View.GONE);
        } else {
            lowFiatTextView.setVisibility(View.GONE);
            highFiatTextView.setVisibility(View.GONE);
            lowCryptoTextView.setVisibility(View.VISIBLE);
            highCryptoTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onFragmentInteraction(String currency) {
        this.currency = currency;
        ArrayList<Currency> currencyArrayList = Util.getCurrencyList();
        for (Currency currency1 :
                currencyArrayList) {
            if (currency1.getSymbol().toLowerCase().equals(currency.toLowerCase())) {
                isCurrencyFiat = true;
                symbol = currency1.getCurrencylogo();
                break;
            } else {
                isCurrencyFiat = false;
                symbol = currency;
            }
        }
        mUserActionsListener.loadExchangeData(coin.getSymbol(), currency, false, true);
        pairTextView.setText(coin.getSymbol() + "/" + currency);
    }

    @Override
    public void setProgressIndicator(boolean active) {
        if (DetailActivity.currentPage == 1) {
            if (isAttached) {
                if (active) {
                    tryAgainButtonTextView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    progressDialog.show();
                } else {
                    progressDialog.dismiss();
                }
            }
        }
    }

    @Override
    public void setProgressBarIndicator(boolean active) {
        if (DetailActivity.currentPage == 1) {
            if (isAttached) {
                if (active) {
                    tryAgainButtonTextView.setVisibility(View.GONE);
                    noDataTextView.setVisibility(View.GONE);
                    progressBar.setVisibility(View.VISIBLE);
                    progressDialog.dismiss();
                } else {
                    progressBar.setVisibility(View.GONE);
                }
            }
        }
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
            mySnackbar = Snackbar.make(view, "Apologize. We are not able to find the official website for this exchange."
                    , Snackbar.LENGTH_LONG);
            mySnackbar.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    DetailActivity detailActivity = (DetailActivity) getActivity();
                    detailActivity.showSnackbar();
                }
            });
            View snackbarView = mySnackbar.getView();
            TextView textViewSnackbar = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            textViewSnackbar.setTextAppearance(getContext(), R.style.regular_16_white);
            mySnackbar.show();
        }
    }

    @Override
    public void onChildNetworkAvailable() {
        if (!isAttached) {
            return;
        }
        if (whereToBuyAdapter !=null && whereToBuyAdapter.getExchanges()!=null && whereToBuyAdapter.getExchanges().size()==0){
            mUserActionsListener.loadExchangeData(coin.getSymbol(), currency, false, false);
            noDataTextView.setVisibility(View.GONE);
            tryAgainButtonTextView.setVisibility(View.GONE);
        }else {
            mUserActionsListener.loadExchangeData(coin.getSymbol(), currency, true, false);
            noDataTextView.setVisibility(View.GONE);
            tryAgainButtonTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onChildNetworkUnavailable() {
    }

}
