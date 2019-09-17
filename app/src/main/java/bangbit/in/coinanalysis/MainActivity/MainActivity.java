package bangbit.in.coinanalysis.MainActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.github.mikephil.charting.data.LineData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import bangbit.in.coinanalysis.ChatModule.ChatActivity;
import bangbit.in.coinanalysis.CryptoToFiat.CryptoToFiatActivity;
import bangbit.in.coinanalysis.CurrencyUpdator;
import bangbit.in.coinanalysis.DashboardInvestmentActivity.DashInvestmentActivity;
import bangbit.in.coinanalysis.DetailActivity.DetailActivity;
import bangbit.in.coinanalysis.Dialogs.DialogListner;
import bangbit.in.coinanalysis.Dialogs.PercentageCustomDialog;
import bangbit.in.coinanalysis.MyApplication;
import bangbit.in.coinanalysis.NetworkCall.GetValueFromUsd;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Setting.SettingActivity;
import bangbit.in.coinanalysis.SplashScreen.SplashScreenActivity;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.broadcast.NetworkStateReceiver;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.Currency;
import bangbit.in.coinanalysis.repository.CoinRepository;

import static bangbit.in.coinanalysis.Constant.COIN;
import static bangbit.in.coinanalysis.Constant.COIN_FAVORITE;
import static bangbit.in.coinanalysis.Constant.COIN_IMAGE_URL;
import static bangbit.in.coinanalysis.Constant.SORT_BY_1_HOUR;
import static bangbit.in.coinanalysis.Constant.SORT_BY_24_HOURS;
import static bangbit.in.coinanalysis.Constant.SORT_BY_7_DAY;
import static bangbit.in.coinanalysis.Constant.SORT_BY_MARKET_CAP;
import static bangbit.in.coinanalysis.Constant.SORT_BY_NAME;
import static bangbit.in.coinanalysis.Constant.SORT_BY_PRICE;
import static bangbit.in.coinanalysis.Constant.SORT_BY_RANK;
import static bangbit.in.coinanalysis.Constant.SORT_BY_VOLUME;
import static bangbit.in.coinanalysis.MyApplication.lastUpdatedString;

public class MainActivity extends AppCompatActivity implements MainActivityContract.View,
        NetworkStateReceiver.NetworkStateReceiverListener, SeekBar.OnSeekBarChangeListener,
        DialogListner, FragmentListner {
    private String TAG = MainActivity.class.getSimpleName();
    private boolean initialLoad = false;
    private boolean isNetworkAvailable = false;
    private MainActvityPresenter mActionsListener;
    private NetworkStateReceiver networkStateReceiver;
    private CoordinatorLayout coordinatorLayout;
    private Toolbar toolbar;
    private ViewPager viewPager;
    private CoinPagerAdapter coinPagerAdapter;
    private LinearLayout containerFilterLinearLayout;
    private ImageButton filterImageButton;
    private FrameLayout allFilterContainerFrameLayout;
    private HashMap<String, LineData> historicalDataHashMap = new HashMap<>();

    private SeekBar day7SeekBar, hours24SeekBar, hour1SeekBar;
    private TabLayout tabLayout;
    private Menu menu;
    boolean isitemDescending = false;
    boolean isMainMenu = true, isSortMenu = false;
    private ImageView sortImageView;
    int selectedMenuItem = R.id.rank;
    int sort = SORT_BY_RANK;
    private Snackbar snackbar;
    private TextView textView7day, textView24Hours, textView1Hour;
    int day7Value = 0, hours24Value = 0, hour1Value = 0;
    String searchQuery = "";
    static boolean isEnableFilter;
    boolean isDisplayFilterView;

    private ImageView myInvestmentImageView;
    private ProgressDialog progressDialog;
    private ArrayList<Coin> coinArrayList;
    private LinearLayout slow_network_message;
    private TextView tryAgainButton;
    private Handler handler;
    private Runnable runnable;
    private TextView titleTextView;
    private boolean isSearchviewClicked;

    private boolean registerNetworkStateReceiver = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Util.getSplashScreenStatus(this)) {
            startActivity(new Intent(this, SplashScreenActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);

        ImageView chatFloatingActionButton = findViewById(R.id.chat_fab);
        chatFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                startActivity(new Intent(MainActivity.this, ChatActivity.class));
                startActivity(new Intent(MainActivity.this, ChatActivity.class));

            }
        });

        ((MyApplication) getApplicationContext()).setCurrencyToDatabase();
        CoinRepository coinRepository = new CoinRepository(this);
        mActionsListener = new MainActvityPresenter(this, coinRepository);
        progressDialog = new ProgressDialog(this, R.style.CustomProgressDialog);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);

        slow_network_message = findViewById(R.id.slow_network_message);
        tryAgainButton = findViewById(R.id.try_again_Button);

        toolbar = findViewById(R.id.main_toolbar);
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        titleTextView = findViewById(R.id.title);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        snackbar = Snackbar.make(coordinatorLayout, "No internet connection!" + lastUpdatedString, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.setActionTextColor(Color.WHITE);
        viewPager = findViewById(R.id.main_activity_view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        containerFilterLinearLayout = findViewById(R.id.container);
        filterImageButton = findViewById(R.id.filter_button);
        day7SeekBar = findViewById(R.id.day7_seekbar);
        hours24SeekBar = findViewById(R.id.hours_24_seekbar);
        hour1SeekBar = findViewById(R.id.hour_1_seekbar);
        sortImageView = findViewById(R.id.sort);
        myInvestmentImageView = findViewById(R.id.my_Investment);
        textView7day = findViewById(R.id.textView7day);
        textView24Hours = findViewById(R.id.textView24Hours);
        textView1Hour = findViewById(R.id.textView1Hour);
        allFilterContainerFrameLayout = findViewById(R.id.all_filter_container);
        titleTextView.setText(R.string.app_name);

        day7SeekBar.setOnSeekBarChangeListener(this);
        hours24SeekBar.setOnSeekBarChangeListener(this);
        hour1SeekBar.setOnSeekBarChangeListener(this);

        mActionsListener.switchFilterCilck(isEnableFilter);
        coinPagerAdapter = new CoinPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(coinPagerAdapter);
        viewPager.setOffscreenPageLimit(0);
        tabLayout.setupWithViewPager(viewPager, true);
        tryAgainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionsListener.loadCoinsV2(false);
                slow_network_message.setVisibility(View.GONE);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                coinPagerAdapter.refreshFavorite();
                coinPagerAdapter.filter(searchQuery, day7Value, hours24Value, hour1Value, isEnableFilter);
                coinPagerAdapter.sort(sort, isitemDescending);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        textView7day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PercentageCustomDialog customDialog = new PercentageCustomDialog();
                customDialog.showDialog(MainActivity.this, "7 Days", day7Value);
            }
        });
        textView24Hours.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PercentageCustomDialog customDialog = new PercentageCustomDialog();
                customDialog.showDialog(MainActivity.this, "24 Hrs", hours24Value);
            }
        });
        textView1Hour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PercentageCustomDialog customDialog = new PercentageCustomDialog();
                customDialog.showDialog(MainActivity.this, "1 Hr", hour1Value);
            }
        });


        myInvestmentImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DashInvestmentActivity.class);
                startActivity(intent);
            }
        });

        sortImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMainMenu = false;
                isSortMenu = true;
                openOptionsMenu();
            }
        });

        SearchView searchView = findViewById(R.id.searchView);
        final EditText searchEditText = searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                isSearchviewClicked = false;
                titleTextView.setVisibility(View.VISIBLE);
                return false;
            }
        });
        searchView.setOnSearchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSearchviewClicked = true;
                if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                    titleTextView.setVisibility(View.GONE);
                }
            }
        });

        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && searchEditText.getText().toString().equals("")) {
                    hideKeyboard();
                }
                return false;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchQuery = newText;
                if (isEnableFilter) {
                    if (coinPagerAdapter != null) {
                        coinPagerAdapter.filter(searchQuery, day7Value, hours24Value, hour1Value, true);
                    }
                } else {
                    if (coinPagerAdapter != null) {
                        coinPagerAdapter.filter(searchQuery, day7Value, hours24Value, hour1Value, false);
                    }
                }

                //@@@ ???
               /* if (newText.equals("")) {

                } else {
                }*/

                if (coinPagerAdapter != null) {
                    coinPagerAdapter.sort(sort, isitemDescending);
                }

                return false;
            }
        });
        View searchplate = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        searchplate.getBackground().setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);

        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.hint));

        filterImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActionsListener.filterButtonClick(isDisplayFilterView);
                isDisplayFilterView = !isDisplayFilterView;
            }
        });

        Switch filterEnableSwitch = findViewById(R.id.togger_filter_switch);
        filterEnableSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mActionsListener.switchFilterCilck(isChecked);
                isEnableFilter = isChecked;
                if (!isChecked) {
                    coinPagerAdapter.filter(searchQuery, day7Value, hours24Value, hour1Value, false);
                } else {
                    coinPagerAdapter.filter(searchQuery, day7Value, hours24Value, hour1Value, true);
                }
                coinPagerAdapter.sort(sort, isitemDescending);
            }
        });

        titleTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (coinPagerAdapter != null) {
                    coinPagerAdapter.moveToFirst();
                }
            }
        });

    }

//    @Override
//    protected void onStop() {
//        if(registerNetworkStateReceiver==true) {
//            networkStateReceiver.removeListener(this);
//            this.unregisterReceiver(networkStateReceiver);
//            registerNetworkStateReceiver = false;
//        }
//        super.onStop();
//    }

    @Override
    protected void onPause() {
        if(registerNetworkStateReceiver==true) {
            networkStateReceiver.removeListener(this);
            this.unregisterReceiver(networkStateReceiver);
            registerNetworkStateReceiver = false;
        }
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!initialLoad){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mActionsListener.loadCoinsV2(false);
                }
            },100);
        }
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        super.onResume();
        if (coinPagerAdapter != null) {
            coinPagerAdapter.refreshFavorite();
            coinPagerAdapter.updateCurrency();
        }

        networkStateReceiver = new NetworkStateReceiver(this);
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION)); //@@@
        registerNetworkStateReceiver = true;

        handler = new Handler();
        runnable = new Runnable() {
            public void run() {
                Log.d(TAG, "onResume: updateCoinDetailsBG"); //@@@ //only every 2 mins, need to avoid mutiple bg calls as onResume is wrong place to call
                mActionsListener.loadCoinsV2(true);
                handler.postDelayed(this, 120000); //now is every 2 minutes
            }
        };
        handler.postDelayed(runnable, 120000); //now is every 2 minutes
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (isSearchviewClicked) {
            if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                titleTextView.setVisibility(View.GONE);
            } else {
                titleTextView.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        menu.clear();
        MenuInflater inflater = getMenuInflater();
        if (isMainMenu) {
            inflater.inflate(R.menu.main_menu, menu);
            isSortMenu = false;
        } else {
            inflater.inflate(R.menu.sort, menu);
            setcolor(menu);
        }
        isMainMenu = true;
        return super.onPrepareOptionsMenu(menu);
    }


    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }
        getMenuInflater().inflate(R.menu.sort, menu);
        this.menu = menu;

        return super.onCreateOptionsMenu(menu);
    }

    void sortdata(int newSelectedItem) {
        if (selectedMenuItem == newSelectedItem) {
            isitemDescending = !isitemDescending;
        } else {

            switch (newSelectedItem) {
                case R.id.marketcap:
                case R.id.volume:
                case R.id.day7:
                case R.id.hrs24:
                case R.id.hr1:
                    isitemDescending = true;
                    break;
                default:
                    isitemDescending = false;
                    break;
            }

        }
        coinPagerAdapter.sort(sort, isitemDescending);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.day7:
                sort = SORT_BY_7_DAY;
                sortdata(item.getItemId());
                selectedMenuItem = item.getItemId();
                break;
            case R.id.hrs24:
                sort = SORT_BY_24_HOURS;
                sortdata(item.getItemId());
                selectedMenuItem = item.getItemId();
                break;
            case R.id.hr1:
                sort = SORT_BY_1_HOUR;
                sortdata(item.getItemId());
                selectedMenuItem = item.getItemId();
                break;
            case R.id.price:
                sort = SORT_BY_PRICE;
                sortdata(item.getItemId());
                selectedMenuItem = item.getItemId();
                break;
            case R.id.volume:
                sort = SORT_BY_VOLUME;
                sortdata(item.getItemId());
                selectedMenuItem = item.getItemId();
                break;
            case R.id.marketcap:
                sort = SORT_BY_MARKET_CAP;
                sortdata(item.getItemId());
                selectedMenuItem = item.getItemId();
                break;
            case R.id.rank:
                sort = SORT_BY_RANK;
                sortdata(item.getItemId());
                selectedMenuItem = item.getItemId();
                break;
            case R.id.symbol:
                sort = SORT_BY_NAME;
                sortdata(item.getItemId());
                selectedMenuItem = item.getItemId();
                break;

            case R.id.action_setting:
                Intent aboutIntent = new Intent(this, SettingActivity.class);
                startActivity(aboutIntent);
                break;

//            case R.id.action_investment:
////                Intent settingIntent = new Intent(this, SettingActivity.class);
////                startActivity(settingIntent);
//                break;

            case R.id.action_exchange:
                startActivity(new Intent(this, CryptoToFiatActivity.class));
                break;
        }
        return true;

    }

    private void setcolor(Menu menu) {
        menu.findItem(R.id.rank).setIcon(R.drawable.default_sort_ascending);
        menu.findItem(R.id.price).setIcon(R.drawable.default_sort_ascending);
        menu.findItem(R.id.marketcap).setIcon(R.drawable.default_sort_ascending);
        menu.findItem(R.id.volume).setIcon(R.drawable.default_sort_ascending);
        menu.findItem(R.id.symbol).setIcon(R.drawable.default_sort_ascending);
        menu.findItem(R.id.day7).setIcon(R.drawable.default_sort_ascending);
        menu.findItem(R.id.hrs24).setIcon(R.drawable.default_sort_ascending);
        menu.findItem(R.id.hr1).setIcon(R.drawable.default_sort_ascending);
        Log.d(TAG, "setcolor: " + selectedMenuItem);
        Log.d(TAG, "setcolor: " + R.id.hr24);
        switch (selectedMenuItem) {
            case R.id.price:
                if (!isitemDescending)
                    menu.findItem(R.id.price).setIcon(R.drawable.ic_sort_ascending);
                else
                    menu.findItem(R.id.price).setIcon(R.drawable.ic_sort_decending);
                break;
            case R.id.volume:
                if (!isitemDescending)
                    menu.findItem(R.id.volume).setIcon(R.drawable.ic_sort_ascending);
                else
                    menu.findItem(R.id.volume).setIcon(R.drawable.ic_sort_decending);
                break;
            case R.id.marketcap:
                if (!isitemDescending)
                    menu.findItem(R.id.marketcap).setIcon(R.drawable.ic_sort_ascending);
                else
                    menu.findItem(R.id.marketcap).setIcon(R.drawable.ic_sort_decending);
                break;
            case R.id.rank:
                if (!isitemDescending)
                    menu.findItem(R.id.rank).setIcon(R.drawable.ic_sort_ascending);
                else
                    menu.findItem(R.id.rank).setIcon(R.drawable.ic_sort_decending);
                break;
            case R.id.symbol:
                if (!isitemDescending)
                    menu.findItem(R.id.symbol).setIcon(R.drawable.ic_sort_ascending);
                else
                    menu.findItem(R.id.symbol).setIcon(R.drawable.ic_sort_decending);
                break;
            case R.id.day7:
                if (!isitemDescending)
                    menu.findItem(R.id.day7).setIcon(R.drawable.ic_sort_ascending);
                else
                    menu.findItem(R.id.day7).setIcon(R.drawable.ic_sort_decending);
                break;
            case R.id.hrs24:
                if (!isitemDescending)
                    menu.findItem(R.id.hrs24).setIcon(R.drawable.ic_sort_ascending);
                else
                    menu.findItem(R.id.hrs24).setIcon(R.drawable.ic_sort_decending);
                break;
            case R.id.hr1:
                if (!isitemDescending)
                    menu.findItem(R.id.hr1).setIcon(R.drawable.ic_sort_ascending);
                else
                    menu.findItem(R.id.hr1).setIcon(R.drawable.ic_sort_decending);
                break;
            default:
                menu.findItem(R.id.rank).setIcon(R.drawable.ic_sort_ascending);

        }
    }

    @Override
    public void displayFilter(boolean active) {
//        active= isSlidingDown
        if (active) {
            containerFilterLinearLayout.setVisibility(View.VISIBLE);
            Animation slideDownAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_down);
            allFilterContainerFrameLayout.setAnimation(slideDownAnimation);
            filterImageButton.setImageResource(R.drawable.filter_open);
        } else {

            Animation slideUpAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slid_up);

            slideUpAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    containerFilterLinearLayout.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            allFilterContainerFrameLayout.setAnimation(slideUpAnimation);
            if (isEnableFilter) {
                filterImageButton.setImageResource(R.drawable.filter_enable);
            } else {
                filterImageButton.setImageResource(R.drawable.filter_disabled);
            }
        }
    }

    @Override
    public void enableFilterInteraction(boolean active) {
        day7SeekBar.setEnabled(active);
        hours24SeekBar.setEnabled(active);
        hour1SeekBar.setEnabled(active);
        textView7day.setEnabled(active);
        textView24Hours.setEnabled(active);
        textView1Hour.setEnabled(active);

        if (active) {
            int color = getResources().getColor(R.color.colorAccent);
            changeSeekbarColor(color);
        } else {
            int color = getResources().getColor(R.color.gray);
            changeSeekbarColor(color);
        }
    }

    private void changeSeekbarColor(int color) {
        day7SeekBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        day7SeekBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        hour1SeekBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        hour1SeekBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        hours24SeekBar.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        hours24SeekBar.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    @Override
    public void setSevenDay(int day7Value) {

    }

    @Override
    public void set24Hours(int hour24Value) {

    }

    @Override
    public void set1Hours(int hour1Value) {

    }

    @Override
    public void showCoins(ArrayList<Coin> coinArrayList) {
        if (coinArrayList != null) {
            if (!initialLoad) {
                initialLoad = true;
            }
            this.coinArrayList = coinArrayList;
            coinPagerAdapter.setCoins(coinArrayList);
            showTryAgainMessage(false);
            ((MyApplication) getApplicationContext()).setCoinArrayList(coinArrayList);
        }
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
    public void showTryAgainMessage(boolean isDisplay) {
        //if (coinArrayList == null) {
            if (!initialLoad && isNetworkAvailable) {
                if (isDisplay) {
                    slow_network_message.setVisibility(View.VISIBLE);
                } else {
                    slow_network_message.setVisibility(View.GONE);
                }
            }
        //}

    }

    @Override
    public void setLastUpdatedString(String lastUpdatedString) {
        MyApplication.lastUpdatedString = Util.getLastUpdatedString(lastUpdatedString);
        snackbar.setText("No internet connection!" + MyApplication.lastUpdatedString);
//        snackbar.setText("No internet connection!" );
    }

    public void showSnackbar(boolean display) {
        if (!isNetworkAvailable) {
            if (display) {
                snackbar.show();
            } else {
                snackbar.dismiss();
            }
        }
    }

    @Override
    public void onNetworkAvailable() {
        snackbar.dismiss();
        isNetworkAvailable = true;

        if (!initialLoad) {

            if (((MyApplication) getApplicationContext()).currencyMultiplyingFactorMap == null ||
                    ((MyApplication) getApplicationContext()).currencyMultiplyingFactorMap.size() == 0) {
                ((MyApplication) getApplicationContext()).setCurrencyToDatabase();
            }

            final Currency currency = ((MyApplication) getApplicationContext()).getCurrency();
//            GetValueFromUsd.getBTCUSDValueLatest(new CurrencyUpdator() {
//                @Override
//                public void updateCurrency(JSONObject rates) {
//                }
//                @Override
//                public void updateCurrencyUSD(String currencyUSDValue) {
//                }
//                @Override
//                public void updateBTCUSD(String btcUsdValue) {
                    GetValueFromUsd.getValueFromUsd(new CurrencyUpdator() {
                        @Override
                        public void updateCurrency(JSONObject rates) {
                            if(rates!=null)
                            {
                                try {
                                    MyApplication.currencyMultiplyingFactor = Float.parseFloat(rates.getString(currency.getSymbol()));
                                    coinPagerAdapter.updateCurrency();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

//                            if (Float.parseFloat(usdValue) != 0) {
//                                MyApplication.currencyMultiplyingFactor = Float.parseFloat(newCurrencyValue) / Float.parseFloat(usdValue);
//                                coinPagerAdapter.updateCurrency();
//                            }

                        }

                    });

                    //mActionsListener.loadCoinsV2(false); //@@@
//                }
//            });

            mActionsListener.loadCoinsV2(false);
        }
    }

    @Override
    public void onNetworkUnavailable() {
        slow_network_message.setVisibility(View.GONE);
        isNetworkAvailable = false;
        View snackbarView = snackbar.getView();
        snackbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (snackbar!=null){
                    snackbar.dismiss();
                }
            }
        });
        snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
        TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.day7_seekbar:
                textView7day.setText("> " + (progress - 50) + "%");
                break;
            case R.id.hours_24_seekbar:
                textView24Hours.setText("> " + (progress - 50) + "%");
                break;
            case R.id.hour_1_seekbar:
                textView1Hour.setText("> " + (progress - 50) + "%");
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        switch (seekBar.getId()) {
            case R.id.day7_seekbar:
                day7Value = seekBar.getProgress() - 50;
                break;
            case R.id.hours_24_seekbar:
                hours24Value = seekBar.getProgress() - 50;
                break;
            case R.id.hour_1_seekbar:
                hour1Value = seekBar.getProgress() - 50;
                break;
        }
        if (coinPagerAdapter != null) {
            if (isEnableFilter) {
                coinPagerAdapter.filter(searchQuery, day7Value, hours24Value, hour1Value, true);
                coinPagerAdapter.sort(sort, isitemDescending);
            }
        }

    }

    @Override
    public void onDismissDialog(String time, int value) {
        switch (time) {
            case "7 Days":
                day7Value = value;
                day7SeekBar.setProgress(value + 50);
                textView7day.setText("> " + value + "%");
                if (isEnableFilter) {
                    coinPagerAdapter.filter(searchQuery, day7Value, hours24Value, hour1Value, true);
                    coinPagerAdapter.sort(sort, isitemDescending);
                }
                break;
            case "24 Hrs":
                hours24Value = value;
                hours24SeekBar.setProgress(value + 50);
                textView24Hours.setText("> " + value + "%");
                if (isEnableFilter) {
                    coinPagerAdapter.filter(searchQuery, day7Value, hours24Value, hour1Value, true);
                    coinPagerAdapter.sort(sort, isitemDescending);
                }
                break;
            case "1 Hr":
                hour1Value = value;
                hour1SeekBar.setProgress(value + 50);
                textView1Hour.setText("> " + value + "%");
                if (isEnableFilter) {
                    coinPagerAdapter.filter(searchQuery, day7Value, hours24Value, hour1Value, true);
                    coinPagerAdapter.sort(sort, isitemDescending);
                }
                break;
            default:
        }
        hideKeyboard();
    }

    @Override
    public void coinClick(Coin coin, String imageUrl, boolean isFavorite) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(COIN, coin);
        intent.putExtra(COIN_IMAGE_URL, imageUrl);
        intent.putExtra(COIN_FAVORITE, isFavorite);
        startActivity(intent);
    }

    @Override
    public void loadCoinOnSwipeRefreshLayout() {
        Log.d(TAG, "loadCoinOnSwipeRefreshLayout");
        mActionsListener.loadCoinsV2(true);
    }

    public HashMap<String, LineData> getHistoricalDataHashMap() {
        return historicalDataHashMap;
    }

    public ArrayList<Coin> getCoinArrayList() {
        return coinArrayList;
    }

    public interface CoinItemListener {
        void onCoinClick(Coin clickedCoin, String imageUrl, boolean isFavorite);

        void onClickFavorite(Coin coin, int coinPosition, boolean isAddingToDb);
    }

    @Override
    protected void onDestroy() {
        isEnableFilter = false;
        initialLoad = false;
        MyApplication.openTimeStamp = 0;
        if(registerNetworkStateReceiver==true) {
            networkStateReceiver.removeListener(this);
            this.unregisterReceiver(networkStateReceiver);
            registerNetworkStateReceiver = false;
        }
        super.onDestroy();
    }

    void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}


