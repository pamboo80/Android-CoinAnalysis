package bangbit.in.coinanalysis.Setting;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import bangbit.in.coinanalysis.Dialogs.FiatDialog.ChangeCurrencyDialog;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Currency;

import static bangbit.in.coinanalysis.Constant.APP_URL;

public class SettingFragment extends Fragment implements View.OnClickListener {

    private TextView currencyNameTextView;
    private Currency currency;
    private TextView titleTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        titleTextView = getActivity().findViewById(R.id.title_textView);
        currency = Util.getCurrency(getContext());
        currencyNameTextView = view.findViewById(R.id.currencyName_textView);
        currencyNameTextView.setText(currency.getName() + " (" + currency.getSymbol() + ")");
        LinearLayout currencyRow = view.findViewById(R.id.currency_row);
        currencyRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChangeCurrencyDialog changeCurrencyDialog = new ChangeCurrencyDialog();
                changeCurrencyDialog.show(getActivity().getSupportFragmentManager(), changeCurrencyDialog.getTag());
            }
        });

        titleTextView.setText("Settings");
        TextView aboutTextView = view.findViewById(R.id.about_textView);
        aboutTextView.setOnClickListener(this);
        TextView shareTextView = view.findViewById(R.id.share_textView);
        shareTextView.setOnClickListener(this);
        TextView termsAndConditionTextView = view.findViewById(R.id.terms_and_condition_textView);
        termsAndConditionTextView.setOnClickListener(this);
        TextView feedBackTextView = view.findViewById(R.id.feed_back_textView);
        feedBackTextView.setOnClickListener(this);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
        final String name = currency.getName();
        final String symbol = currency.getSymbol();
        currencyNameTextView.setText(name + " (" + symbol + ")");
    }

    void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment).addToBackStack(fragment.getTag());
        fragmentTransaction.commit();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about_textView:
                loadFragment(new AboutFragment());
                titleTextView.setText("About App");
                break;
            case R.id.share_textView:
                try {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("text/plain");
                    i.putExtra(Intent.EXTRA_SUBJECT, R.string.app_name);
                    i.putExtra(Intent.EXTRA_TEXT, APP_URL);
                    startActivity(Intent.createChooser(i, "Share Application"));
                } catch (Exception e) {

                }
                break;
            case R.id.terms_and_condition_textView:
                loadFragment(new TermsAndConditionFragment());
                titleTextView.setText("Terms & Conditions");
//                TermsAndConditionSheetFragment termsAndConditionSheetFragment=new TermsAndConditionSheetFragment();
//                termsAndConditionSheetFragment.show(getActivity().getSupportFragmentManager(),
//                        termsAndConditionSheetFragment.getTag());
                break;
            case R.id.feed_back_textView:

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto","contact@bangbit.in", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback");
                startActivity(Intent.createChooser(emailIntent, "Send Feedback"));
                break;
            default:
                break;
        }
    }
}
