package bangbit.in.coinanalysis.SplashScreenFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bangbit.in.coinanalysis.Constant;
import bangbit.in.coinanalysis.Dialogs.FiatDialog.ChangeCurrencyDialog;
import bangbit.in.coinanalysis.Dialogs.FiatDialog.CurrencySelector;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.SplashScreen.OnFragmentInteractionListener;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Currency;

public class SelectCurrencySplashScreenFragment extends Fragment implements CurrencySelector {



    private TextView currencyTextView;
    private OnFragmentInteractionListener mListener;
    public SelectCurrencySplashScreenFragment() {
        // Required empty public constructor
    }


    public static SelectCurrencySplashScreenFragment newInstance() {
        SelectCurrencySplashScreenFragment fragment = new SelectCurrencySplashScreenFragment();

        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        Currency currency=Util.getCurrency(getContext());

        View view= inflater.inflate(R.layout.fragment_select_currency_splash_screen, container, false);
        currencyTextView = view.findViewById(R.id.currency_textView);
        currencyTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeCurrencyDialog  changeCurrencyDialog=new ChangeCurrencyDialog();
                changeCurrencyDialog.show(getFragmentManager(), changeCurrencyDialog.getTag());
            }
        });
        TextView okButtonTextView=view.findViewById(R.id.ok_Button);
        okButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFragmentInteraction(Constant.SELECT_CURRENCY_SPLASHSCREEN_FRAGMENT);
            }
        });
        currencyTextView.setText(currency.getName()+" (" +currency.getSymbol()+")");



        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
//        mListener = null;
    }

    @Override
    public void selectcurrency(Currency currency) {
        currencyTextView.setText(currency.getName()+" (" +currency.getSymbol()+")");
    }


}
