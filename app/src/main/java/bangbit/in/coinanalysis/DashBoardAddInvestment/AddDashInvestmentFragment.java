package bangbit.in.coinanalysis.DashBoardAddInvestment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

import bangbit.in.coinanalysis.BottomSheetPair.DateBottomSheetFragment;
import bangbit.in.coinanalysis.Dialogs.CryptoDialog.CryptoCurrencySelectorListner;
import bangbit.in.coinanalysis.Dialogs.CryptoDialog.SelectCryptoCurrencyDialog;
import bangbit.in.coinanalysis.MyApplication;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Coin;

import static bangbit.in.coinanalysis.MyApplication.currencySymbol;

public class AddDashInvestmentFragment extends Fragment implements CryptoCurrencySelectorListner {

    private Coin coin;
    private ArrayList<Coin> coinArrayList = new ArrayList<>();
    private EditText quantityEditText;
    private EditText boughtPriceEditText;
    private TextView boughtDateEditText;
    private EditText notesEditText;

    private String symbol = "BTC";
    private TextView countTextView;
    Calendar dateCalendar = Calendar.getInstance();

    private TextView cryptoTextView;

    public static AddDashInvestmentFragment newInstance() {
        AddDashInvestmentFragment fragment = new AddDashInvestmentFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (((MyApplication) getContext().getApplicationContext()).coinArrayList != null) {
            coinArrayList = (ArrayList<Coin>) ((MyApplication) getContext().getApplicationContext()).coinArrayList.clone();
        } else {
            coinArrayList = new ArrayList<>();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_main_investment, container, false);
        TextView currencySymbolTextView = view.findViewById(R.id.currencySymbol_textView);
        currencySymbolTextView.setText(currencySymbol);

        quantityEditText = view.findViewById(R.id.quantity_EditText);
        boughtPriceEditText = view.findViewById(R.id.bought_price_EditText);
        boughtDateEditText = view.findViewById(R.id.bought_date_EditText);
        notesEditText = view.findViewById(R.id.note_EditText);
        countTextView = view.findViewById(R.id.textView_count_text);
        cryptoTextView = view.findViewById(R.id.crypto_textView);
        cryptoTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectCryptoCurrencyDialog selectCryptoCurrencyDialog =
                        SelectCryptoCurrencyDialog.newInstance(coinArrayList, AddDashInvestmentFragment.this, coin);
                selectCryptoCurrencyDialog.show(getActivity().getSupportFragmentManager(), selectCryptoCurrencyDialog.getTag());
            }
        });

        notesEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                countTextView.setText(s.length() + "/1024");
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        boughtDateEditText.setText(Util.getDayMonthYearFromUnixTime(dateCalendar.getTimeInMillis() / 1000));

        for (Coin coin : coinArrayList) {
            if (coin.getSymbol().equals(symbol)) {
                this.coin = coin;
                cryptoTextView.setText(coin.getName() + " (" + coin.getSymbol() + ")");
            }
        }

        if (coin != null) {
            boughtPriceEditText.setText(Util.getDecimalPointValueIfLessThanZero(
                    Double.parseDouble(coin.getPriceUsd()) * MyApplication.currencyMultiplyingFactor
                    , 6));
        }

        quantityEditText.requestFocus();

        boughtDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateBottomSheetFragment dateBottomSheetFragment = DateBottomSheetFragment.newInstance(new DateBottomSheetFragment.OnDateInteractionListener() {
                    @Override
                    public void onDateInteraction(int date, int month, int year, String monthString) {
                        dateCalendar.set(Calendar.YEAR, year);
                        dateCalendar.set(Calendar.MONTH, month - 1);
                        dateCalendar.set(Calendar.DATE, date);
                        year = year - 2000;
                        if (date <= 9) {
                            boughtDateEditText.setText("0" + date + "/" + monthString + "/" + ((year == 8 || year == 9) ? "0" + year : year));
                        } else {
                            boughtDateEditText.setText(date + "/" + monthString + "/" + ((year == 8 || year == 9) ? "0" + year : year));
                        }
                    }
                }, dateCalendar);
                dateBottomSheetFragment.show(getFragmentManager(), dateBottomSheetFragment.getTag());

            }
        });
        return view;
    }


    public boolean checkValidation() {
        if (coin == null) {
            return false;
        }
        if (cryptoTextView.getText().toString().isEmpty()) {
            cryptoTextView.setError("This field required.");
            cryptoTextView.requestFocus();
            return false;
        }
        if (quantityEditText.getText().toString().isEmpty()) {
            quantityEditText.setError("This field required.");
            quantityEditText.requestFocus();
            return false;
        } else if (quantityEditText.getText().toString().equals(".")) {
            quantityEditText.setError("Enter a valid quantity.");
            quantityEditText.requestFocus();
            return false;
        }
        if (boughtPriceEditText.getText().toString().isEmpty()) {
            boughtPriceEditText.setError("This field required.");
            boughtPriceEditText.requestFocus();
            return false;
        } else if (boughtPriceEditText.getText().toString().equals(".")) {
            boughtPriceEditText.setError("Enter a valid bought price.");
            boughtPriceEditText.requestFocus();
            return false;
        }
        if (boughtDateEditText.getText().toString().isEmpty()) {
            boughtDateEditText.setError("This field required.");
            boughtDateEditText.requestFocus();
            return false;
        }

        Float quantity;
        quantity = Float.parseFloat(quantityEditText.getText().toString());
        if (quantity == 0) {
            quantityEditText.setError("Enter a valid quantity.");
            quantityEditText.requestFocus();
            return false;
        }

        return true;
    }

    public String getBoughtDate() {
        return String.valueOf(dateCalendar.getTimeInMillis() / 1000);
    }

    public String getNotes() {
        if (notesEditText.getText().toString() == null || notesEditText.getText().toString().isEmpty()) {
            return "";
        } else {
            return notesEditText.getText().toString();
        }

    }

    public String getSymbol() {
        return symbol;
    }

    public float getQuantity() {
        return Float.parseFloat(quantityEditText.getText().toString());
    }

    public float getPrice() {
        Float price = 0f;
        if (!boughtPriceEditText.getText().toString().trim().equals("")) {
            price = Float.parseFloat(boughtPriceEditText.getText().toString());
        }
        return price;
    }

    @Override
    public void onClickCurrency(Coin coin) {
        this.coin = coin;
        cryptoTextView.setText(coin.getName() + " (" + coin.getSymbol() + ")");
        boughtPriceEditText.setText(Util.getDecimalPointValueIfLessThanZero(
                Double.parseDouble(coin.getPriceUsd()) * MyApplication.currencyMultiplyingFactor
                , 6));
        this.symbol = coin.getSymbol();
    }
}
