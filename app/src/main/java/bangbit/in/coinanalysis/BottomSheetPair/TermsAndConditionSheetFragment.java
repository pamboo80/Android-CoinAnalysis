package bangbit.in.coinanalysis.BottomSheetPair;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bangbit.in.coinanalysis.R;

/**
 * Created by Nagarajan on 4/19/2018.
 */

public class TermsAndConditionSheetFragment extends BottomSheetDialogFragment {

    public static TermsAndConditionSheetFragment newInstance() {
        Bundle args = new Bundle();

        TermsAndConditionSheetFragment fragment = new TermsAndConditionSheetFragment();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.terms_and_condition_bottomsheet_dialog, container, false);
        TextView  gotItTextView=view.findViewById(R.id.gotItButton);
        gotItTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDialog().dismiss();
            }
        });


        return view;
    }


}
