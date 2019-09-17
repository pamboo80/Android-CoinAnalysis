package bangbit.in.coinanalysis.Setting;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bangbit.in.coinanalysis.R;

public class TermsAndConditionFragment extends Fragment {



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.terms_and_condition_bottomsheet_dialog, container, false);
        TextView gotItTextView=view.findViewById(R.id.gotItButton);
        gotItTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        return view;

    }



}
