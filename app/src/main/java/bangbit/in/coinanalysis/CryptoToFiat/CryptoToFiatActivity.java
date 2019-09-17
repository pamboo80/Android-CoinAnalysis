package bangbit.in.coinanalysis.CryptoToFiat;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import bangbit.in.coinanalysis.BaseActivity;
import bangbit.in.coinanalysis.CurrencyUpdator;
import bangbit.in.coinanalysis.Dialogs.CryptoDialog.CryptoCurrencySelectorListner;
import bangbit.in.coinanalysis.Dialogs.CryptoDialog.SelectCryptoCurrencyDialog;
import bangbit.in.coinanalysis.Dialogs.FiatDialog.ChangeCurrencyDialog;
import bangbit.in.coinanalysis.Dialogs.FiatDialog.CurrencySelector;
import bangbit.in.coinanalysis.ListUpdatorListner;
import bangbit.in.coinanalysis.MyApplication;
import bangbit.in.coinanalysis.NetworkCall.GetValueFromUsd;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.Currency;
import bangbit.in.coinanalysis.repository.CoinImageUrlHashMapCallback;
import bangbit.in.coinanalysis.repository.CoinRepository;

public class CryptoToFiatActivity extends BaseActivity implements ListUpdatorListner,
        CryptoCurrencySelectorListner, CurrencySelector {

    private static HashMap<String, String> coinImageUrlHashmap;
    private ArrayList<Coin> coinArrayList = new ArrayList<>();
    private ArrayList<Currency> currencyArrayList = new ArrayList<>();
    EditText cryptoEditText;
    private EditText fiatEditText;
    private boolean issetcrypto;
    private boolean issetfiat;
    double mainfiat = 1, maincrypto = 1;

    String currentFiatCurrency = MyApplication.currency.getSymbol();
    private ImageView fiatImageView;
    Currency currency = MyApplication.currency;
    private ImageView cryptoImageView;
    private HashMap<String, Float> currencyMultiplyingFactorMap = new HashMap<>();
    private String TAG = CryptoToFiatActivity.class.getSimpleName();
    Coin coin;
    private String currentCryptoCurrency = "BTC";
    private TextView cryptoTextView, fiatTextView;
    private TextView currencyLogoTextView;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (((MyApplication) getApplicationContext()).coinArrayList != null) {
            coinArrayList.addAll(((MyApplication) getApplicationContext()).coinArrayList);
        }

        ((MyApplication) getApplicationContext()).addListUpdatorListners(this);

        currencyArrayList = Util.getCurrencyList();
        currencyMultiplyingFactorMap = MyApplication.currencyMultiplyingFactorMap;
        CoinRepository coinRepository = new CoinRepository(this);

        setContentView(R.layout.activity_crypto_to_fiat);
        currencyLogoTextView = findViewById(R.id.currency_logo_textView);
        currencyLogoTextView.setText(currency.getCurrencylogo());

        cryptoTextView = findViewById(R.id.crypto_textView);
        cryptoTextView.setText(currentCryptoCurrency);

        fiatTextView = findViewById(R.id.fiat_textView);
        fiatTextView.setText(currentFiatCurrency);

        ImageView backImageView = findViewById(R.id.back_imageView);
        backImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        coinRepository.getCoinUrlFromDb(new CoinImageUrlHashMapCallback() {
            @Override
            public void coinUrlCallback(HashMap<String, String> coinImageUrlHashmap, boolean isSucesssCall) {
                CryptoToFiatActivity.coinImageUrlHashmap = coinImageUrlHashmap;
            }
        });

        cryptoImageView = findViewById(R.id.crypto_ImageView);
        fiatImageView = findViewById(R.id.fiat_ImageView);

        cryptoEditText = findViewById(R.id.amount_of_crypto_currency_EditText);
        fiatEditText = findViewById(R.id.amount_of_fiat_currency_EditText);

        for (Currency currency : currencyArrayList) {
            if (currency.getSymbol().toLowerCase().equals(currentFiatCurrency.toLowerCase())) {
                this.currency = currency;
                break;
            }
        }

        Picasso.with(CryptoToFiatActivity.this).load(currency.getImageLogo())
                .placeholder(R.drawable.default_coin).into(fiatImageView);

        for (Coin coin : coinArrayList) {
            if (coin.getSymbol().toLowerCase().equals(currentCryptoCurrency.toLowerCase())) {
                this.coin = coin;
                float factor = 1;
                if (currencyMultiplyingFactorMap.get(currency.getSymbol()) != null) {
                    factor = currencyMultiplyingFactorMap.get(currency.getSymbol());
                }else {
                    factor=MyApplication.currencyMultiplyingFactor;
                }
                updatEditext(String.valueOf(Float.parseFloat(coin.getPriceUsd()) * factor));
                if (coinImageUrlHashmap != null) {
                    Picasso.with(CryptoToFiatActivity.this).load(coinImageUrlHashmap.get(coin.getSymbol()))
                            .placeholder(R.drawable.default_coin).into(cryptoImageView);
                }
                break;
            }
        }

        cryptoEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!issetcrypto)
                    return;

                issetfiat = false;
                try {
                    if (charSequence.toString().equals("")) {
                        fiatEditText.setText("");
                    } else {
                        maincrypto = Double.parseDouble(charSequence.toString());
                        double updatedfiat = Double.parseDouble(charSequence.toString()) * mainfiat;
                        fiatEditText.setText(Util.getTwoDecimalPointValue(updatedfiat));
                    }

                } catch (NumberFormatException e) {

                }
                issetfiat = true;
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        fiatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!issetfiat) {
                    return;
                }
                issetcrypto = false;
                try {
                    double getcurrentfiat = Double.parseDouble(charSequence.toString());
                    double updatedcrypto = getcurrentfiat / mainfiat;
                    maincrypto = updatedcrypto;
                    String sCrypto = String.format("%.8f", maincrypto).replaceAll("\\.?0*$", "");
                    if (sCrypto.indexOf('.') == -1) {
                        sCrypto += ".00";
                    }
                    cryptoEditText.setText(sCrypto);

                } catch (NumberFormatException e) {
                    cryptoEditText.setText("0.00");
                }
                issetcrypto = true;

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        cryptoImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectCryptoCurrencyDialog selectCryptoCurrencyDialog =
                        SelectCryptoCurrencyDialog.newInstance(coinArrayList, CryptoToFiatActivity.this, coin);
                selectCryptoCurrencyDialog.show(getSupportFragmentManager(), selectCryptoCurrencyDialog.getTag());
            }
        });

        fiatImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeCurrencyDialog changeCurrencyDialog = ChangeCurrencyDialog.newInstance(currency);
                changeCurrencyDialog.show(getSupportFragmentManager(), changeCurrencyDialog.getTag());
            }
        });

        fiatTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeCurrencyDialog changeCurrencyDialog = ChangeCurrencyDialog.newInstance(currency);
                changeCurrencyDialog.show(getSupportFragmentManager(), changeCurrencyDialog.getTag());
            }
        });
        cryptoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectCryptoCurrencyDialog selectCryptoCurrencyDialog =
                        SelectCryptoCurrencyDialog.newInstance(coinArrayList, CryptoToFiatActivity.this, coin);
                selectCryptoCurrencyDialog.show(getSupportFragmentManager(), selectCryptoCurrencyDialog.getTag());
            }
        });

        getCurrencyMultiplyingFactor();

    }

    void updatEditext(String value) {
        issetcrypto = false;
        issetfiat = false;
        mainfiat = Double.parseDouble(value);

        //maincrypto = 1;
        try {
            maincrypto = Double.parseDouble(cryptoEditText.getText().toString());
        } catch (NumberFormatException e) {
        }

        fiatEditText.setText(Util.getTwoDecimalPointValue(mainfiat));

        issetfiat = true;
        issetcrypto = true;

        try {
            String sCrypto = String.format("%.8f", maincrypto).replaceAll("\\.?0*$", "");
            if (sCrypto.indexOf('.') == -1) {
                sCrypto += ".00";
            }
            cryptoEditText.setText(sCrypto);
        } catch (NumberFormatException e) {
            cryptoEditText.setText("0.00");
        }

    }


    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    private void showKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        if (!inputMethodManager.isAcceptingText()) {
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
        }
    }

    @Override
    public void updateList(ArrayList<Coin> coinArrayList) {

        Log.d(TAG, "updateList: ");
        int positionCrypto = cryptoEditText.getSelectionStart();
        int positionFiat = fiatEditText.getSelectionStart();

        if (this.coinArrayList != null && this.coinArrayList.size() != 0) {
            this.coinArrayList.clear();
            this.coinArrayList.addAll(coinArrayList);
            for (Coin coin : coinArrayList) {
                if (coin.getSymbol().toLowerCase().equals(currentCryptoCurrency.toLowerCase())) {
                    float factor = 1;
                    if (currencyMultiplyingFactorMap.get(currency.getSymbol()) != null) {
                        factor = currencyMultiplyingFactorMap.get(currency.getSymbol());
                    }
                    updatEditext(String.valueOf(Float.parseFloat(coin.getPriceUsd()) * factor));
                    break;
                }
            }

        } else {

            if (this.coinArrayList != null && this.coinArrayList.size() == 0) {
                this.coinArrayList.clear();
                this.coinArrayList.addAll(coinArrayList);
            }

            for (Coin coin : coinArrayList) {
                if (coin.getSymbol().toLowerCase().equals(currentCryptoCurrency.toLowerCase())) {
                    float factor = 1;
                    if (currencyMultiplyingFactorMap.get(currency.getSymbol()) != null) {
                        factor = currencyMultiplyingFactorMap.get(currency.getSymbol());
                    }
                    Picasso.with(CryptoToFiatActivity.this).load(currency.getImageLogo())
                            .placeholder(R.drawable.default_coin).into(fiatImageView);
                    updatEditext(String.valueOf(Float.parseFloat(coin.getPriceUsd()) * factor));
                    if (coinImageUrlHashmap != null) {
                        Picasso.with(CryptoToFiatActivity.this).load(coinImageUrlHashmap.get(coin.getSymbol()))
                                .placeholder(R.drawable.default_coin).into(cryptoImageView);
                    }
                    break;
                }
            }
        }

        if (positionCrypto < cryptoEditText.getText().toString().length()) {
            cryptoEditText.setSelection(positionCrypto);
        } else {
            cryptoEditText.setSelection(cryptoEditText.getText().toString().length());
        }

        if (positionFiat < fiatEditText.getText().toString().length()) {
            fiatEditText.setSelection(positionFiat);
        } else {
            fiatEditText.setSelection(fiatEditText.getText().toString().length());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ((MyApplication) getApplicationContext()).removeListUpdatorListners(this);
    }

    @Override
    public void onClickCurrency(final Coin coin) {
        currentCryptoCurrency = coin.getSymbol();

        this.coin = coin;
        float factor = 1;
        if (currencyMultiplyingFactorMap.get(currency.getSymbol()) != null) {
            factor = currencyMultiplyingFactorMap.get(currency.getSymbol());
            cryptoTextView.setText(currentCryptoCurrency);
            Picasso.with(CryptoToFiatActivity.this).load(currency.getImageLogo())
                    .placeholder(R.drawable.default_coin).into(fiatImageView);
            updatEditext(String.valueOf(Float.parseFloat(coin.getPriceUsd()) * factor));
            if (coinImageUrlHashmap != null) {
                Picasso.with(CryptoToFiatActivity.this).load(coinImageUrlHashmap.get(coin.getSymbol()))
                        .placeholder(R.drawable.default_coin).into(cryptoImageView);
            }

        } else {

//            GetValueFromUsd.getValueFromFiatToUsdV2USD(coin.getId(), "USD", new CurrencyUpdator() {
//                @Override
//                public void updateCurrency(JSONObject rates) {
//                }
//
//                @Override
//                public void updateCurrencyUSD(String currencyUSDValue) {
//                    GetValueFromUsd.getValueFromFiatToUsdV2(coin.getId(), currency.getSymbol(), currencyUSDValue, new CurrencyUpdator() {
                      GetValueFromUsd.getValueFromUsd(new CurrencyUpdator() {
                        @Override
                        public void updateCurrency(JSONObject rates) {
                            float factor = 1;
                            if(rates!=null)
                            {
                                try {
                                    factor = Float.parseFloat(rates.getString(currency.getSymbol()));
                                    currencyMultiplyingFactorMap.put(currency.getSymbol(),factor);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            mainfiat = Double.parseDouble(coin.getPriceUsd());

                            cryptoTextView.setText(currentCryptoCurrency);
                            Picasso.with(CryptoToFiatActivity.this).load(currency.getImageLogo())
                                    .placeholder(R.drawable.default_coin).into(fiatImageView);
                            updatEditext(String.valueOf(Float.parseFloat(coin.getPriceUsd()) * factor));

                            if (coinImageUrlHashmap != null) {
                                Picasso.with(CryptoToFiatActivity.this).load(coinImageUrlHashmap.get(coin.getSymbol()))
                                        .placeholder(R.drawable.default_coin).into(cryptoImageView);
                            }
                        }

                    });
                }

//            });
//        }

    }

    @Override
    public void selectcurrency(final Currency currency) {
        this.currency = currency;
        if (currencyMultiplyingFactorMap.get(currency.getSymbol()) != null) {
            currencyLogoTextView.setText(currency.getCurrencylogo());
            currentFiatCurrency = currency.getSymbol();
            fiatTextView.setText(currentFiatCurrency);
            float factor = currencyMultiplyingFactorMap.get(currency.getSymbol());
            Picasso.with(CryptoToFiatActivity.this).load(currency.getImageLogo())
                    .placeholder(R.drawable.default_coin).into(fiatImageView);
            if (coin != null) {
                updatEditext(String.valueOf(Float.parseFloat(coin.getPriceUsd()) * factor));
            }
        }else {

//            GetValueFromUsd.getBTCUSDValueLatest(new CurrencyUpdator() {
//                @Override
//                public void updateCurrency(JSONObject rates) {
//                }
//                @Override
//                public void updateCurrencyUSD(String currencyUSDValue) {
//                }
//
//                @Override
//                public void updateBTCUSD(String btcUsdValue) {
                    GetValueFromUsd.getValueFromUsd(new CurrencyUpdator() {
                        @Override
                        public void updateCurrency(JSONObject rates) {
                            currencyLogoTextView.setText(currency.getCurrencylogo());
                            currentFiatCurrency = currency.getSymbol();
                            float factor = 0;

                            if(rates!=null) {
                                try {
                                    //float factor = Float.parseFloat(newCurrencyValue) / Float.parseFloat(usdValue);
                                    factor = Float.parseFloat(rates.getString(currency.getSymbol()));
                                    currencyMultiplyingFactorMap.put(currency.getSymbol(), factor);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            fiatTextView.setText(currentFiatCurrency);

                            Picasso.with(CryptoToFiatActivity.this).load(currency.getImageLogo())
                                    .placeholder(R.drawable.default_coin).into(fiatImageView);
                            if (coin != null) {
                                updatEditext(String.valueOf(Float.parseFloat(coin.getPriceUsd()) * factor));
                            }
                        }

                    });
//                }
//            });
        }

    }

    void getCurrencyMultiplyingFactor(){

//        GetValueFromUsd.getBTCUSDValueLatest(new CurrencyUpdator() {
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
                        public void updateCurrency(JSONObject rates)
                        {
                            if(rates!=null)
                            {
                                for (final Currency currencyFor : Util.getCurrencyList()) {
                                    try {
                                        float factor = Float.parseFloat(rates.getString(currencyFor.getSymbol()));
                                        currencyMultiplyingFactorMap.put(currencyFor.getSymbol(), factor);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
//                            if (Float.parseFloat(usdValue) != 0) {
//                                float factor = Float.parseFloat(newCurrencyValue) / Float.parseFloat(usdValue);
//                                currencyMultiplyingFactorMap.put(currencyFor.getSymbol(), factor);
//                            }
                        }

                    });

//            }
//        });

    }
}
