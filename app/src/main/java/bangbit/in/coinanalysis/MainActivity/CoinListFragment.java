package bangbit.in.coinanalysis.MainActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.data.LineData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import bangbit.in.coinanalysis.Constant;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.broadcast.NetworkStateReceiver;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.repository.CoinRepository;

public class CoinListFragment extends Fragment implements CoinListFragmentContract.View,
        NetworkStateReceiver.NetworkStateReceiverListener {

    private HashMap<String, LineData> historicalDataHashMap;
    private CoinListFragmentContract.UserActionsListener mActionsListener;
    private CoinRepository coinRepository;
    private RecyclerView recyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private CoinAdapter coinAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    static public boolean isAscending = false;
    private Handler handler;
    private Runnable runnable;
    Context context;
    private ProgressDialog progressDialog;
    private int sort = Constant.SORT_BY_RANK;
    boolean isReverse = false;
    int day7Value = 0, hours24Value = 0, hour1Value = 0;
    String searchQuery = "";
    boolean initialLoad;

    private boolean registerNetworkStateReceiver = false;

    MainActivity.CoinItemListener mItemListener = new MainActivity.CoinItemListener() {
        @Override
        public void onCoinClick(Coin clickedCoin, String imageUrl, boolean isFavorite) {
            fragmentListner.coinClick(clickedCoin, imageUrl, isFavorite);
        }

        @Override
        public void onClickFavorite(final Coin coin, final int coinPosition, boolean isAddingToDb) {
            mActionsListener.insertFavorite(coin.getSymbol(), isAddingToDb);
            final boolean isAddingToDbReverse = !isAddingToDb;
            String message = coin.getName() + " favorited.";
            if (!isAddingToDb) {
                message = coin.getName() + " unfavorited.";
            }

            mySnackbar.setText(message);
            mySnackbar.setDuration(Snackbar.LENGTH_SHORT);

            mySnackbar.setCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar transientBottomBar, int event) {
                    super.onDismissed(transientBottomBar, event);
                    mainActivity.showSnackbar(true);

                }
            });
            mySnackbar.setAction("Undo", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mActionsListener.insertFavorite(coin.getSymbol(), isAddingToDbReverse);
                    mActionsListener.udpateFavorite();
                }
            });
            mySnackbar.show();

        }


    };
    ArrayList<Coin> coinArrayList = new ArrayList<Coin>(0);
    HashMap<String, String> coinImageUrlHashmap = new HashMap<>(0);
    Set<String> coinFavoriteSet = new HashSet<>();
    private NetworkStateReceiver networkStateReceiver;
    private FragmentListner fragmentListner;
    private String TAG = CoinListFragment.class.getSimpleName();
    private TextView messageTextView;
    private Snackbar mySnackbar;
    private MainActivity mainActivity;
    private int i;

    public CoinListFragment() {
        // Required empty public constructor
    }


    public static CoinListFragment newInstance() {
        CoinListFragment fragment = new CoinListFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getContext();
        mainActivity = (MainActivity) getActivity();
        historicalDataHashMap = mainActivity.getHistoricalDataHashMap();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_coin_list, container, false);
        //        Network Repository
        coinRepository = new CoinRepository(getContext());
        coinFavoriteSet = coinRepository.getFavoriteSet();
        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

//        Presenter
        mActionsListener = new CoinListFragmentPresenter(this, coinRepository);
        displaySnackbar();
//        progressBar = view.findViewById(R.id.progress_bar);
        coinAdapter = new CoinAdapter(coinArrayList, coinImageUrlHashmap,
                historicalDataHashMap, coinFavoriteSet, mItemListener, getContext());
        recyclerView = view.findViewById(R.id.recycler_view_coin_list);
        messageTextView = view.findViewById(R.id.textView_message);
        recyclerView.setAdapter(coinAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        mSwipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSwipeRefreshLayout.setRefreshing(false);
                fragmentListner.loadCoinOnSwipeRefreshLayout();
            }
        });
//        mActionsListener.loadCoinsV2(false);

        mActionsListener.getFavoriteSet();
        mActionsListener.getImageHashMap();
        mActionsListener.loadImageUrlFromDb();
        return view;
    }

//    @Override
//    public void onStop() {
//        networkStateReceiver.removeListener(this);
//        context.unregisterReceiver(networkStateReceiver);
//        super.onStop();
//    }

    @Override
    public void onPause() {
        if(registerNetworkStateReceiver==true) {
            networkStateReceiver.removeListener(this);
            context.unregisterReceiver(networkStateReceiver);
            registerNetworkStateReceiver = false;
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        networkStateReceiver = new NetworkStateReceiver(context);
        networkStateReceiver.addListener(this);
        context.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION)); //@@@
        registerNetworkStateReceiver = true;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentListner) {
            fragmentListner = (FragmentListner) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    @Override
    public void setProgressIndicator(boolean active) {

        if (active) {
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    @Override
    public void showCoins(ArrayList<Coin> coin) {
        if (!initialLoad && coin.size() != 0) {
            initialLoad = true;
        }
        coinAdapter.replaceAllData(coin);

        if (MainActivity.isEnableFilter) {
            mActionsListener.filterList(coinAdapter.getmCoinsOriginalCopy(), searchQuery,
                    day7Value, hours24Value, hour1Value);
        } else {
            mActionsListener.filterList(coinAdapter.getmCoinsOriginalCopy(), searchQuery);
        }

        //@@@ ???
        //if (isReverse) {
        //    mActionsListener.sortWithReverse(sort, coinAdapter.getmCoins());
        //} else {
        sort(sort, isReverse);
        //}

    }


    @Override
    public void updateAdapterList(ArrayList<Coin> coin) {
        if (initialLoad) {
            if (coin.size() == 0) {
                messageTextView.setText("No results found.");
                messageTextView.setVisibility(View.VISIBLE);
//            fragmentListner.displayOfflineMessage("No coins found.",true);
            } else {
                messageTextView.setVisibility(View.GONE);
//            fragmentListner.displayOfflineMessage("",true);
            }
        }
        coinAdapter.replaceData(coin);
    }

    @Override
    public void updateImageUrlHashMap(HashMap<String, String> coinImageUrlHashMap) {
        coinAdapter.updateHashMap(coinImageUrlHashMap);
        coinAdapter.notifyDataSetChanged();
    }


    @Override
    public void updateCurrency(double multiplyValue) {
        coinAdapter.updateCurrncy(multiplyValue);
    }

    @Override
    public void updateFavoriteSet(Set<String> favoriteCoinSet) {
        coinAdapter.updateFavoriteSet(favoriteCoinSet);
    }


    @Override
    public void onNetworkAvailable() {
        messageTextView.setVisibility(View.GONE); //@@@
    }

    @Override
    public void onNetworkUnavailable() {
        if (coinAdapter.getmCoins().size() == 0) {
            messageTextView.setText("You are offline!");
            messageTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        if(registerNetworkStateReceiver==true) {
            networkStateReceiver.removeListener(this);
            context.unregisterReceiver(networkStateReceiver);
            registerNetworkStateReceiver = false;
        }
        super.onDestroy();
    }

    void refreshFavorite() {
        if (mActionsListener != null) {
            mActionsListener.udpateFavorite();
        }
    }

    private void displaySnackbar() {
        mySnackbar = Snackbar.make(getActivity().findViewById(R.id.coordinatorLayout),
                "", Snackbar.LENGTH_SHORT);

        TextView snackbarActionTextView = mySnackbar.getView().findViewById(android.support.design.R.id.snackbar_action);
        snackbarActionTextView.setTextAppearance(getActivity(), R.style.bold_16_yellow);
        View snackbarView = mySnackbar.getView();
        TextView textViewSnackbar = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textViewSnackbar.setTextAppearance(getActivity(), R.style.regular_16_white);
    }

    public void filter(String query, int day7, int hours24, int hour1, boolean isFilterPercentageApply) {
        if (coinAdapter != null) {
            day7Value = day7;
            hours24Value = hours24;
            hour1Value = hour1;
            searchQuery = query;
            if (isFilterPercentageApply) {
                mActionsListener.filterList(coinAdapter.getmCoinsOriginalCopy(), query,
                        day7, hours24, hour1);
            } else {
                mActionsListener.filterList(coinAdapter.getmCoinsOriginalCopy(), query);

            }
        }
    }

    public void sort(int sort, boolean isReverse) {
        this.sort = sort;
        this.isReverse = isReverse;
        Log.d(TAG, "sort: " + sort + isReverse);
        mActionsListener.sortList(sort, coinAdapter.getmCoins());
        if (isReverse) {
            mActionsListener.reverseList(coinAdapter.getmCoins());
        }
    }


    public void moveToFirst() {
        recyclerView.scrollToPosition(0);
    }
}
