package bangbit.in.coinanalysis.SplashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import bangbit.in.coinanalysis.CurrencyUpdator;
import bangbit.in.coinanalysis.Dialogs.FiatDialog.CurrencySelector;
import bangbit.in.coinanalysis.MainActivity.MainActivity;
import bangbit.in.coinanalysis.MyApplication;
import bangbit.in.coinanalysis.NetworkCall.GetValueFromUsd;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.SplashScreenFragment.SelectCurrencySplashScreenFragment;
import bangbit.in.coinanalysis.SplashScreenFragment.SplashScreen1Fragment;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Currency;

import static bangbit.in.coinanalysis.Constant.SELECT_CURRENCY_SPLASHSCREEN_FRAGMENT;
import static bangbit.in.coinanalysis.Constant.SPLASHSCREEN1_FRAGMENT;

public class SplashScreenActivity extends AppCompatActivity implements SplashScreenContract.View,
        OnFragmentInteractionListener, CurrencySelector {
    private static final String TAG = "SplashScreenActivity";
    ViewPager viewPagerSplashScreen;
    private ViewPagerAdapter mAdapter;
    FragmentManager mFragmentManager;
    FragmentTransaction mCurTransaction;
    SplashScreenContract.UserActionsListener mUserActionsListener;
    TextView nextTextView, previousTextView;
    private TabLayout tabLayout;
    Fragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        //final DatabaseHelper databaseHelper=new DatabaseHelper(this);
        //databaseHelper.init();
        mUserActionsListener = new SplashScreenPresenter(this);
        //GetValueFromUsd.Init();
        nextTextView = findViewById(R.id.next_textView);
        previousTextView = findViewById(R.id.previous_textView);
        mUserActionsListener.loadMainFragment(SplashScreen1Fragment.newInstance());
        nextTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAdapter != null) {
                    mUserActionsListener.nextButtonClick(viewPagerSplashScreen.getCurrentItem() + 1);
                }
            }
        });
        previousTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int page = viewPagerSplashScreen.getCurrentItem();
                viewPagerSplashScreen.setCurrentItem(page - 1);
            }
        });


    }


    @Override
    public void onFragmentInteraction(int fragment) {
        switch (fragment) {
            case SPLASHSCREEN1_FRAGMENT:
                mCurTransaction = mFragmentManager.beginTransaction();
                mCurTransaction.remove(mFragmentManager.findFragmentById(R.id.fragment_container)).commit();
                mUserActionsListener.setViewPager();
                break;

            case SELECT_CURRENCY_SPLASHSCREEN_FRAGMENT:
                loadMainActivity();
        }

    }

    @Override
    public void showViewPager(boolean isDisplay) {
        if (isDisplay) {
            viewPagerSplashScreen.setVisibility(View.VISIBLE);
            nextTextView.setVisibility(View.VISIBLE);
            tabLayout.setVisibility(View.VISIBLE);
        } else {
            viewPagerSplashScreen.setVisibility(View.GONE);
            nextTextView.setVisibility(View.GONE);
            tabLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void loadFragment(Fragment fragment) {
        this.fragment = fragment;
        mFragmentManager = getSupportFragmentManager();
        mCurTransaction = mFragmentManager.beginTransaction();
        mCurTransaction.replace(R.id.fragment_container, fragment);
        mCurTransaction.commit();
    }

    @Override
    public void setViewPagerAdapter() {
        nextTextView.setVisibility(View.VISIBLE);
        viewPagerSplashScreen = findViewById(R.id.splash_screen_view_pager);
        mAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerSplashScreen.setAdapter(mAdapter);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPagerSplashScreen, true);
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            View tab = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(i);
            ViewGroup.MarginLayoutParams marginLayoutParams = (ViewGroup.MarginLayoutParams) tab.getLayoutParams();
            float dp = Util.convertDpToPixel(10, this);
            marginLayoutParams.setMargins(0, 0, (int) dp, 0);
            tab.requestLayout();
        }
        viewPagerSplashScreen.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                mUserActionsListener.onPageChange(position);

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void showNextButton(boolean setVisible) {
        if (setVisible) {
            nextTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void changeNextButton(String updatedText) {
        nextTextView.setText(updatedText);

    }

    @Override
    public void showPrevButton(boolean setVisible) {
        if (setVisible) {
            previousTextView.setVisibility(View.VISIBLE);
        } else {
            previousTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void loadNextFragment(int position) {
        viewPagerSplashScreen.setCurrentItem(position);
    }

    @Override
    public void loadMainActivity() {
        Util.setSplashScreenStatus(this,true);
        finish();
        startActivity(new Intent(this, MainActivity.class));

    }

    @Override
    public void selectcurrency(final Currency currency) {

//        GetValueFromUsd.getValueFromUsd(new CurrencyUpdator() {
//            @Override
//            public void updateCurrency(JSONObject rates) {
//            }
//            @Override
//            public void updateCurrencyUSD(String currencyUSDValue) {
//            }
//
//            @Override
//            public void updateBTCUSD(String btcUsdValue) {
                GetValueFromUsd.getValueFromUsd(new CurrencyUpdator() {
                    @Override
                    public void updateCurrency(JSONObject rates) {

                        if(rates!=null)
                        {
                            try {
                                float factor =  Float.parseFloat(rates.getString(currency.getSymbol()));
                                ((MyApplication) getApplicationContext()).setCurrencyMultiplyingFactor(factor);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

//                        float factor = Float.parseFloat(newCurrencyValue) / Float.parseFloat(usdValue);
//                        ((MyApplication) getApplicationContext()).setCurrencyMultiplyingFactor(factor);
                    }

                });

//            }
//        });

        Util.setCurrency(currency, this);
        SelectCurrencySplashScreenFragment selectCurrencySplashScreenFragment = (SelectCurrencySplashScreenFragment) fragment;
        selectCurrencySplashScreenFragment.selectcurrency(currency);
    }
}
