package bangbit.in.coinanalysis.DetailActivity;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import bangbit.in.coinanalysis.BaseActivity;
import bangbit.in.coinanalysis.ListUpdatorListner;
import bangbit.in.coinanalysis.MyApplication;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.Datum;
import bangbit.in.coinanalysis.repository.CoinRepository;
import bangbit.in.coinanalysis.repository.HistoryRepository;

import static bangbit.in.coinanalysis.Constant.COIN;
import static bangbit.in.coinanalysis.Constant.COIN_FAVORITE;
import static bangbit.in.coinanalysis.Constant.COIN_IMAGE_URL;

public class DetailActivity extends BaseActivity implements DetailActivityContract.View, ListUpdatorListner {
    public static int currentChartdataButton = R.id.hour24Button;
    public static String imageUrl = "";
    public static boolean isArrowUpHistorical = true;
    public static int currentPage = 0;
    ArrayList<CoinUpdator> coinUpdators = new ArrayList<>();


    private ViewPager viewPagerDetailActivity;
    private TabLayout tabLayout;
    private CoinDetailPagerAdapter pagerAdpter;
    Coin coin;

    private boolean isFavorite;
    private String TAG = DetailActivity.class.getSimpleName();
    ImageView backImageView;


    public ArrayList<Datum> data;
    DetailActivityContract.UserActionsListener userActionsListener;
    private String currency = "USD";
    private ImageView favoriteImageView;
    private Snackbar mySnackbar;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ((MyApplication) getApplicationContext()).addListUpdatorListners(this);
        backImageView = findViewById(R.id.back_imageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Bundle bundle = getIntent().getExtras();
        HistoryRepository historyRepository = new HistoryRepository();
        CoinRepository coinRepository = new CoinRepository(this);
        userActionsListener = new DetailActivityPresenter(this, historyRepository, coinRepository);

        favoriteImageView = findViewById(R.id.favorite_icon);
        ImageView coin_icon = findViewById(R.id.coin_icon);
        Picasso.with(this).load(R.drawable.default_coin).into(coin_icon);
        setupSnackbar();


        if (bundle != null) {
            coin = (Coin) bundle.get(COIN);
            if (coin.getPriceUsd() == null) {
                coin.setPriceUsd("0");
            }
            if (coin != null) {
                userActionsListener.loadChartData(coin.getSymbol(), "USD", 2000);
            }

            imageUrl = (String) bundle.get(COIN_IMAGE_URL);
            isFavorite = (boolean) bundle.get(COIN_FAVORITE);
            if (isFavorite) {
                favoriteImageView.setImageResource(R.drawable.heart_filled);
            }

            if (imageUrl != null && !imageUrl.equals("")) {
                Picasso.with(this).load(imageUrl).placeholder(R.drawable.default_coin).into(coin_icon);
            }

            TextView name = findViewById(R.id.coin_name);
            name.setText(coin.getName());

        }

        favoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isFavorite = !isFavorite;
                userActionsListener.favoriteIconClick(coin.getSymbol(), isFavorite);
                showSnackbar(isFavorite);
            }
        });
        toolbar = findViewById(R.id.main_toolbar);
        viewPagerDetailActivity = findViewById(R.id.main_activity_view_pager);
        viewPagerDetailActivity.setOffscreenPageLimit(3);
        tabLayout = findViewById(R.id.tab_layout);
        pagerAdpter = new CoinDetailPagerAdapter(getSupportFragmentManager(), coin, currency);
        viewPagerDetailActivity.setAdapter(pagerAdpter);
        tabLayout.setupWithViewPager(viewPagerDetailActivity, true);
        viewPagerDetailActivity.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;

                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if (position == 0 && CoinDetailFragment.currentPage == 1) {
                        displayTabandToolbar(false);
                    } else {
                        displayTabandToolbar(true);
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


    }

    public void reLoadChartData(){
        userActionsListener.loadChartData(coin.getSymbol(), "USD", 2000);
    }


    private void setupSnackbar() {
        mySnackbar = Snackbar.make(findViewById(R.id.coordinatorLayout),
                "", Snackbar.LENGTH_SHORT);
        mySnackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                showSnackbar();
            }
        });

        TextView snackbarActionTextView = mySnackbar.getView().findViewById(android.support.design.R.id.snackbar_action);
        snackbarActionTextView.setTextAppearance(this, R.style.bold_16_yellow);
        View snackbarView = mySnackbar.getView();
        TextView textViewSnackbar = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textViewSnackbar.setTextAppearance(this, R.style.regular_16_white);
    }

    public Coin getCoin() {
        return coin;
    }

    @Override
    public void loadChartData(ArrayList<Datum> data) {
        this.data = data;
        if (pagerAdpter != null) {
            pagerAdpter.chartDatafetched(data);
        }
    }

    @Override
    public void setFavoriteIconImage(boolean isFavorited) {
        isFavorite = isFavorited;
        if (isFavorited) {
            favoriteImageView.setImageResource(R.drawable.heart_filled);
        } else {
            favoriteImageView.setImageResource(R.drawable.heart_unfilled_white);
        }
    }

    @Override
    public void showSnackbar(final boolean isFavorited) {
        String message = coin.getName() + " favorited.";
        if (!isFavorited) {
            message = coin.getName() + " unfavorited.";
        }
        mySnackbar.setText(message);
        mySnackbar.setDuration(Snackbar.LENGTH_SHORT);
        mySnackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userActionsListener.favoriteIconClick(coin.getSymbol(), !isFavorited);
            }
        });
        mySnackbar.show();
    }


    @Override
    public void displayTabandToolbar(boolean isDisplay) {
        if (isDisplay) {
            toolbar.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
        } else {
            toolbar.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
        }
    }

    public ArrayList<Datum> getData() {
        return data;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        currentChartdataButton = R.id.hour24Button;
        imageUrl = "";
        isArrowUpHistorical = true;
        currentPage = 0;
        ((MyApplication) getApplicationContext()).removeListUpdatorListners(this);
    }

    @Override
    public void updateList(ArrayList<Coin> coinArrayList) {
        for (Coin mycoin :
                coinArrayList) {
            if (coin.getName().equals(mycoin.getName())) {
                coin = mycoin;
                break;
            }
        }
        for (CoinUpdator coinUpdator :
                coinUpdators) {
            coinUpdator.updateCoin(coin);

        }
        Log.d(TAG, "updateList: ");
    }

    public void addCoinUpdator(CoinUpdator coinUpdator) {
        coinUpdators.add(coinUpdator);
    }

    public void removeCoinUpdator(CoinUpdator coinUpdator) {
        coinUpdators.remove(coinUpdator);
    }

}
