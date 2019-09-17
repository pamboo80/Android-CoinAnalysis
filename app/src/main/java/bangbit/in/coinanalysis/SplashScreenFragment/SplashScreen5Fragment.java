package bangbit.in.coinanalysis.SplashScreenFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import bangbit.in.coinanalysis.R;

public class SplashScreen5Fragment extends Fragment {

    public SplashScreen5Fragment() {
        // Required empty public constructor
    }


    public static SplashScreen5Fragment newInstance() {
        SplashScreen5Fragment fragment = new SplashScreen5Fragment();
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
        return inflater.inflate(R.layout.fragment_splash_screen5, container, false);
    }



    @Override
    public void onDetach() {

        super.onDetach();
    }

}
