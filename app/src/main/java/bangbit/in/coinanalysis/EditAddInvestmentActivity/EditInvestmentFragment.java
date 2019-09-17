package bangbit.in.coinanalysis.EditAddInvestmentActivity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Calendar;

import bangbit.in.coinanalysis.BottomSheetPair.DateBottomSheetFragment;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.Investment;
import bangbit.in.coinanalysis.repository.InvestmentRepository;

import static bangbit.in.coinanalysis.Constant.COIN;
import static bangbit.in.coinanalysis.Constant.COIN_IMAGE_URL;
import static bangbit.in.coinanalysis.Constant.INVESTMENT;
import static bangbit.in.coinanalysis.MyApplication.currencyMultiplyingFactor;
import static bangbit.in.coinanalysis.MyApplication.currencySymbol;
import static bangbit.in.coinanalysis.Util.getDayMonthYearFromUnixTime;
import static bangbit.in.coinanalysis.Util.getTwoDecimalPointValue;

public class EditInvestmentFragment extends Fragment {

    Coin coin;
    private EditText quantityEditText;
    private EditText boughtPriceEditText;
    private TextView boughtDateEditText;
    private EditText notesEditText;
    private String imageUrl;
    private TextView countTextView;
    private Investment investment;
    private InvestmentRepository investmentRepository;
    Calendar dateCalendar = Calendar.getInstance();

    public EditInvestmentFragment() {
        // Required empty public constructor
    }

    public static EditInvestmentFragment newInstance(Coin coin, String imageUrl, Investment investment) {
        EditInvestmentFragment fragment = new EditInvestmentFragment();
        Bundle args = new Bundle();
        args.putParcelable(COIN, coin);
        args.putParcelable(INVESTMENT, investment);
        args.putString(COIN_IMAGE_URL, imageUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            coin = getArguments().getParcelable(COIN);
            investment = getArguments().getParcelable(INVESTMENT);
            imageUrl = getArguments().getString(COIN_IMAGE_URL);
            long date = Long.parseLong(investment.getBoughtDate());
            date = date * 1000;
            dateCalendar.setTimeInMillis(date);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        investmentRepository = new InvestmentRepository(getContext());
        View view = inflater.inflate(R.layout.fragment_add_investment, container, false);

        TextView currencySymbolTextView = view.findViewById(R.id.currencySymbol_textView);
        currencySymbolTextView.setText(currencySymbol);

        quantityEditText = view.findViewById(R.id.quantity_EditText);
        boughtPriceEditText = view.findViewById(R.id.bought_price_EditText);
        boughtDateEditText = view.findViewById(R.id.bought_date_EditText);
        notesEditText = view.findViewById(R.id.note_EditText);
        countTextView = view.findViewById(R.id.textView_count_text);


        quantityEditText.setText(getTwoDecimalPointValue(investment.getQuantity()));
        boughtPriceEditText.setText(Util.getDecimalPointValueIfLessThanZero(
                Double.valueOf(investment.getBoughtPrice() * currencyMultiplyingFactor)
                , 10));
        boughtDateEditText.setText(getDayMonthYearFromUnixTime(investment.getBoughtDate()));
        notesEditText.setText(investment.getNotes());
        countTextView.setText(investment.getNotes().length() + "/1024");

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

        boughtDateEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DateBottomSheetFragment dateBottomSheetFragment = DateBottomSheetFragment.newInstance(new DateBottomSheetFragment.OnDateInteractionListener() {
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

    public void delete(int id) {
        investmentRepository.deleteInvestment(id);
    }

    public boolean save() {
        if (checkValidation()) {
            String coinSymbol = coin.getSymbol();
            float boughtPrice = 0;
            float quantity = 0;
            try {
                quantity = Float.parseFloat(quantityEditText.getText().toString());
                boughtPrice = Float.parseFloat(boughtPriceEditText.getText().toString());
            } catch (NumberFormatException e) {

            }
            boughtPrice = boughtPrice / currencyMultiplyingFactor;
            String boughtDate = String.valueOf(dateCalendar.getTimeInMillis() / 1000);
            String notes = notesEditText.getText().toString();
            return investmentRepository.updateInvestment(investment.getId(), coinSymbol, quantity, boughtPrice, boughtDate, notes);

//                    boolean insert = investmentRepository.insertInvestment(coinSymbol, quantity, boughtPrice, boughtDate, notes);
        }
        return false;
    }

    private boolean checkValidation() {
        if (quantityEditText.getText().toString().equals("")) {
            quantityEditText.setError("This field is required.");
            quantityEditText.requestFocus();
            return false;
        } else if (quantityEditText.getText().toString().equals(".")) {
            quantityEditText.setError("Enter a valid quantity.");
            quantityEditText.requestFocus();
            return false;
        }
        if (boughtPriceEditText.getText().toString().equals("")) {
            boughtPriceEditText.setError("This field is required.");
            boughtPriceEditText.requestFocus();
            return false;
        } else if (boughtPriceEditText.getText().toString().equals(".")) {
            boughtPriceEditText.setError("Enter a valid bought price.");
            boughtPriceEditText.requestFocus();
            return false;
        }
        if (boughtDateEditText.getText().toString().equals("")) {
            boughtDateEditText.setError("This field is required.");
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


    @Override
    public void onDetach() {
        super.onDetach();
    }


}
