package bangbit.in.coinanalysis.Dialogs.CryptoDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import bangbit.in.coinanalysis.ListOperations;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.pojo.Coin;

import static bangbit.in.coinanalysis.Constant.COIN_LIST;

/**
 * Created by Nagarajan on 4/17/2018.
 */

public class SelectCryptoCurrencyDialog extends DialogFragment implements CryptoCurrencySelectorListner {

    private static Coin coin;
    private ArrayList<Coin> coinArrayList=new ArrayList<>();
    static CryptoCurrencySelectorListner cryptoCurrencySelectorListner;

    public static SelectCryptoCurrencyDialog newInstance(ArrayList<Coin> coins,
                                                         CryptoCurrencySelectorListner cryptoCurrencySelectorListner,
                                                         Coin coin) {
        Bundle args = new Bundle();
        Log.d("newInstance", "newInstance: "+coins.size());
        args.putParcelableArrayList(COIN_LIST,coins);
        SelectCryptoCurrencyDialog fragment = new SelectCryptoCurrencyDialog();
        fragment.setArguments(args);
        SelectCryptoCurrencyDialog.coin=coin;
        SelectCryptoCurrencyDialog.cryptoCurrencySelectorListner=cryptoCurrencySelectorListner;
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<Coin> coins=getArguments().getParcelableArrayList(COIN_LIST);
        if (coinArrayList!=null) {
            coinArrayList.addAll(coins);
            coinArrayList = ListOperations.sortByRank(coinArrayList);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.select_crypto_currency_dialog, container,
                false);
        TextView noDataTextView= view.findViewById(R.id.no_data_TextView);
        if (coinArrayList.size()==0) {
            noDataTextView.setVisibility(View.VISIBLE);
        }
        RecyclerView recyclerView=view.findViewById(R.id.currency_RecycleView);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        final CryptoCurrencyAdapter cryptoCurrencyAdapter=new CryptoCurrencyAdapter(coinArrayList,
                this,coin);
        recyclerView.setAdapter(cryptoCurrencyAdapter);
        EditText searchEditText=view.findViewById(R.id.search_EditText);
        TextView cancelTextView=view.findViewById(R.id.cancelButton);
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        recyclerView.scrollToPosition(cryptoCurrencyAdapter.getSelectedPosition());

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                cryptoCurrencyAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        return view;

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        onResume();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        try {
//            currencySelector = (CurrencySelector) context;
//        } catch (ClassCastException e) {
//            throw new ClassCastException(context.toString()
//                    + " must implement currencySelector");
//        }
    }


    @Override
    public void onResume() {
        WindowManager w = getActivity().getWindowManager();
        Display d = w.getDefaultDisplay();
        int measuredheight = d.getHeight();
        measuredheight=(measuredheight*80)/100;
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getDialog().getWindow().getAttributes());
//        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
//        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = measuredheight;
//        lp.width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 380/*height value*/, getResources().getDisplayMetrics());
        getDialog().getWindow().setAttributes(lp);
//         Call super onResume after sizing
        super.onResume();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog= super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        return dialog;

    }

    @Override
    public void onClickCurrency(Coin coin) {
        cryptoCurrencySelectorListner.onClickCurrency(coin);
        dismiss();
    }


}
