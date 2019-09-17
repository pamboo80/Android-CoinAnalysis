package bangbit.in.coinanalysis.DashBoardAddInvestment;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import bangbit.in.coinanalysis.BaseActivity;
import bangbit.in.coinanalysis.MyInvestmentFragment.MyInvestmentFragment;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.repository.CoinRepository;
import bangbit.in.coinanalysis.repository.InvestmentRepository;

import static bangbit.in.coinanalysis.Constant.COIN;
import static bangbit.in.coinanalysis.Constant.COIN_IMAGE_URL;
import static bangbit.in.coinanalysis.Constant.CURRENCY;
import static bangbit.in.coinanalysis.Constant.LOAD_FRAGMENT;
import static bangbit.in.coinanalysis.Constant.MY_INVESTMENT_FRAGMENT;
import static bangbit.in.coinanalysis.MyApplication.currencyMultiplyingFactor;

public class DashAddEditInvestmentActivity extends BaseActivity {

    private FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction;
    boolean isFavorite = false;
    private CoinRepository coinRepository;
    private Coin coin;
    private String currency;
    private String imageUrl;
    private InvestmentRepository investmentRepository;
    private Snackbar mySnackbar;
    private ImageView favoriteImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_my_investment);
        investmentRepository = new InvestmentRepository(this);
        final TextView titlTextView = findViewById(R.id.coin_name);
        final ImageView coinImageView = findViewById(R.id.coin_icon);
        final ImageView backImageView = findViewById(R.id.back_imageView);
        favoriteImageView = findViewById(R.id.favorite_icon);
        final ImageView saveImageView = findViewById(R.id.save_icon);

        setupSnackbar();
        favoriteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (coin == null) {
                    return;
                }
                isFavorite = !isFavorite;
                if (isFavorite) {
                    coinRepository.insertFavoriteCoin(coin.getSymbol());
                } else {
                    coinRepository.removeFavoriteCoin(coin.getSymbol());
                }

                showSnackbar(isFavorite);
                if (isFavorite) {
                    favoriteImageView.setImageResource(R.drawable.heart_filled);
                } else {
                    favoriteImageView.setImageResource(R.drawable.heart_unfilled_white);
                }


            }
        });
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getInt(LOAD_FRAGMENT) == MY_INVESTMENT_FRAGMENT) {
                coin = bundle.getParcelable(COIN);
                currency = bundle.getString(CURRENCY);
                imageUrl = bundle.getString(COIN_IMAGE_URL);
                Picasso.with(this).load(imageUrl).placeholder(R.drawable.default_coin).into(coinImageView);
                titlTextView.setText(coin.getName());
                loadFragment(MyInvestmentFragment.newInstance(coin, currency, imageUrl));
                coinRepository = new CoinRepository(this);
                isFavorite = coinRepository.isCoinFavorited(coin.getSymbol());
                if (isFavorite) {
                    favoriteImageView.setImageResource(R.drawable.heart_filled);

                } else {
                    favoriteImageView.setImageResource(R.drawable.heart_unfilled_white);
                }

            } else {

                backImageView.setImageResource(R.drawable.close);
                favoriteImageView.setVisibility(View.GONE);
                saveImageView.setVisibility(View.VISIBLE);
                coinImageView.setVisibility(View.GONE);
                titlTextView.setText("Add Investment");
                final AddDashInvestmentFragment addDashInvestmentFragment = AddDashInvestmentFragment.newInstance();
                loadFragment(addDashInvestmentFragment);
                saveImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (addDashInvestmentFragment.checkValidation()) {
                            String symbol = addDashInvestmentFragment.getSymbol();
                            float quantity = addDashInvestmentFragment.getQuantity();
                            float price = addDashInvestmentFragment.getPrice() / currencyMultiplyingFactor;
                            String date = addDashInvestmentFragment.getBoughtDate();
                            String notes = addDashInvestmentFragment.getNotes();
                            investmentRepository.insertInvestment(symbol, quantity, price, date, notes);
                            finish();
                        }
                    }
                });
            }


        }
    }

    private void setupSnackbar() {
        mySnackbar = Snackbar.make(findViewById(R.id.coordinatorLayout),
                "", Snackbar.LENGTH_SHORT);
        mySnackbar.setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                DashAddEditInvestmentActivity.super.showSnackbar();
            }
        });
        TextView snackbarActionTextView = mySnackbar.getView().findViewById(android.support.design.R.id.snackbar_action);
        snackbarActionTextView.setTextAppearance(this, R.style.bold_16_yellow);
        View snackbarView = mySnackbar.getView();
        TextView textViewSnackbar = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textViewSnackbar.setTextAppearance(this, R.style.regular_16_white);
    }

    public void showSnackbar(final boolean isFavorited) {
        isFavorite = isFavorited;
        String message = coin.getName() + " favorited.";
        if (!isFavorited) {
            message = coin.getName() + " unfavorited.";
        }

        mySnackbar.setText(message);
        mySnackbar.setDuration(Snackbar.LENGTH_SHORT);
        mySnackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFavorited) {
                    isFavorite = true;
                    coinRepository.insertFavoriteCoin(coin.getSymbol());
                    favoriteImageView.setImageResource(R.drawable.heart_filled);
                } else {
                    isFavorite = false;
                    coinRepository.removeFavoriteCoin(coin.getSymbol());
                    favoriteImageView.setImageResource(R.drawable.heart_unfilled_white);
                }
            }
        });
        mySnackbar.show();
    }

    private void loadFragment(Fragment fragment) {
        mFragmentManager = getSupportFragmentManager();
        mCurTransaction = mFragmentManager.beginTransaction();
        mCurTransaction.add(R.id.fragment_container, fragment);
        mCurTransaction.commit();
    }
}
