package bangbit.in.coinanalysis.Dialogs.FiatDialog;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Currency;

import static bangbit.in.coinanalysis.Constant.CURRENCY;

/**
 * Created by Nagarajan on 4/17/2018.
 */

public class ChangeCurrencyDialog extends DialogFragment implements  CurrencySelector{
    CurrencySelector currencySelector;
    private Currency currency;
    private boolean bsoftKeyboardOpen = false;


    public static ChangeCurrencyDialog newInstance(Currency currency) {
        Bundle args = new Bundle();
        args.putParcelable(CURRENCY,currency);

        ChangeCurrencyDialog fragment = new ChangeCurrencyDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments()!=null){
            currency= (Currency) getArguments().get(CURRENCY);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Context context = getContext();
        if (currency==null) {
            currency = Util.getCurrency(getContext());
        }
        View view = getActivity().getLayoutInflater().inflate(R.layout.select_currency_dialog, container, false);
        RecyclerView recyclerView=view.findViewById(R.id.currency_RecycleView);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        final CurrencyAdapter currencyAdapter=new CurrencyAdapter(getContext(),this,currency);
        recyclerView.setAdapter(currencyAdapter);
        recyclerView.scrollToPosition(currencyAdapter.getSelectedPosition());
        EditText searchEditText=view.findViewById(R.id.search_EditText);
        searchEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bsoftKeyboardOpen=true;
            }
        });
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                currencyAdapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        TextView cancelTextView=view.findViewById(R.id.cancelButton);
        cancelTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
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
        try {
            currencySelector = (CurrencySelector) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement currencySelector");
        }
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
    public void selectcurrency(Currency currency) {
        if(bsoftKeyboardOpen==true){
            hideKeyboard();
        }
        currencySelector.selectcurrency(currency);
        this.getDialog().dismiss();
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
}
