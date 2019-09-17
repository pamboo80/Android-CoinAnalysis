package bangbit.in.coinanalysis.SplashScreenFragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import bangbit.in.coinanalysis.Constant;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.SplashScreen.OnFragmentInteractionListener;


public class SplashScreen1Fragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    public SplashScreen1Fragment() {
        // Required empty public constructor
    }

    public static SplashScreen1Fragment newInstance() {
        SplashScreen1Fragment fragment = new SplashScreen1Fragment();
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
        View view = inflater.inflate(R.layout.fragment_splash_screen1, container, false);
        TextView textView=view.findViewById(R.id.get_started);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener!=null){
                    mListener.onFragmentInteraction(Constant.SPLASHSCREEN1_FRAGMENT);
                }
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDateInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }



}
