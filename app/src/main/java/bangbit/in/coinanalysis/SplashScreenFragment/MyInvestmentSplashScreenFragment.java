package bangbit.in.coinanalysis.SplashScreenFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bangbit.in.coinanalysis.R;



public class MyInvestmentSplashScreenFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    public MyInvestmentSplashScreenFragment() {
        // Required empty public constructor
    }

    public static MyInvestmentSplashScreenFragment newInstance() {
        MyInvestmentSplashScreenFragment fragment = new MyInvestmentSplashScreenFragment();

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
        return inflater.inflate(R.layout.fragment_splash_screen4, container, false);
    }



    @Override
    public void onDetach() {
        super.onDetach();
    }

}
