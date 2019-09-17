package bangbit.in.coinanalysis.BottomSheetPair;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Coin;

import static bangbit.in.coinanalysis.Constant.COIN;
import static bangbit.in.coinanalysis.Constant.CURRENCY;

public class BottomSheetPairFragment extends BottomSheetDialogFragment implements OnPairInteractionListener {

    private static OnFragmentInteractionListener mListener;
    private static Coin coin;
    ArrayList<String> pairs;
    String currency = "USD";

    public BottomSheetPairFragment() {
        pairs = Util.getPairCurrencyList();
    }

    BottomSheetPairFragment bottomSheetFragment;


    public static BottomSheetPairFragment newInstance(OnFragmentInteractionListener onFragmentInteractionListener,
                                                      String currency, Coin coin) {
        BottomSheetPairFragment bottomSheetFragment = new BottomSheetPairFragment();
        mListener = onFragmentInteractionListener;
        Bundle args = new Bundle();
        args.putString(CURRENCY, currency);
        args.putParcelable(COIN, coin);
        bottomSheetFragment.setArguments(args);
        return bottomSheetFragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bottomSheetFragment = this;

        if (getArguments() != null) {
            currency = getArguments().getString(CURRENCY);
            coin = getArguments().getParcelable(COIN);
            pairs.remove(coin.getSymbol().toUpperCase());
        }
        View view = inflater.inflate(R.layout.pair_bottomsheet_dialog, container, false);
        TextView cancelButton = view.findViewById(R.id.cancelButton);
        RecyclerView pairRecyclerView = view.findViewById(R.id.pair_RecycleView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        pairRecyclerView.setLayoutManager(linearLayoutManager);

        PairAdapter pairAdapter = new PairAdapter(pairs, this, currency, coin);
        pairRecyclerView.setAdapter(pairAdapter);
        int position = pairAdapter.getSelectedPosition();
        if (position > 1) {
            position = position - 2;
        } else if (position == 1) {
            position = 0;
        }
        pairRecyclerView.scrollToPosition(position);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetFragment.dismiss();
            }
        });
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onPairInteraction(String currency) {

        if (this.currency != currency) {
            mListener.onFragmentInteraction(currency);
        }
        bottomSheetFragment.dismiss();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String currency);
    }

}
