package bangbit.in.coinanalysis;

import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;

import bangbit.in.coinanalysis.broadcast.NetworkStateReceiver;

import static bangbit.in.coinanalysis.MyApplication.lastUpdatedString;

public class BaseActivity extends AppCompatActivity implements NetworkStateReceiver.NetworkStateReceiverListener {

    private NetworkStateReceiver networkStateReceiver;
    ArrayList<ChildNetworkAvailable> childNetworkAvailables = new ArrayList<>();
    private Snackbar snackbar;

    private boolean registerNetworkStateReceiver = false;

    private boolean isNetworkAvailable = false;
    private boolean isTimeToDisplay=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        setupSnackbar();
    }

    public void addChildReceiverListner(ChildNetworkAvailable childNetworkAvailable) {
        childNetworkAvailables.add(childNetworkAvailable);
//        if (isNetworkAvailable){
//            childNetworkAvailable.onChildNetworkAvailable();
//        }else {
//            childNetworkAvailable.onChildNetworkUnavailable();
//        }
    }

    public void removeChildReceiverListner(ChildNetworkAvailable childNetworkAvailable) {
        childNetworkAvailables.remove(childNetworkAvailable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        networkStateReceiver = new NetworkStateReceiver(this);
        networkStateReceiver.addListener(this);
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION)); //@@@
        registerNetworkStateReceiver = true;
    }

//    @Override
//    protected void onStop() {
//        networkStateReceiver.removeListener(this);
//        this.unregisterReceiver(networkStateReceiver);
//        super.onStop();
//    }

    @Override
    protected void onPause() {
        if(registerNetworkStateReceiver==true) {
            networkStateReceiver.removeListener(this);
            this.unregisterReceiver(networkStateReceiver);
            registerNetworkStateReceiver = false;
        }
        super.onPause();
    }

    @Override
    public void onNetworkAvailable() {
        snackbar.dismiss();
        isNetworkAvailable = true;
        for (ChildNetworkAvailable childNetworkAvailable :
                childNetworkAvailables) {
            childNetworkAvailable.onChildNetworkAvailable();
        }
    }

    @Override
    public void onNetworkUnavailable() {
        snackbar.show();
        isNetworkAvailable = false;
        for (ChildNetworkAvailable childNetworkAvailable :
                childNetworkAvailables) {
            childNetworkAvailable.onChildNetworkUnavailable();
        }
    }

    public void showSnackbar() {
        if (!isNetworkAvailable) {
            if (snackbar==null){
                setupSnackbar();
            }
            snackbar.show();
        }
    }

    private void setupSnackbar() {
        if (isTimeToDisplay){
            snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout),
                    "No internet connection!"+lastUpdatedString, Snackbar.LENGTH_INDEFINITE);
        }else {
            snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout),
                    "No internet connection!", Snackbar.LENGTH_INDEFINITE);
        }
        snackbar.setAction("OK", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackbar.dismiss();
            }
        });
        snackbar.setActionTextColor(Color.WHITE);

//        snackbar = Snackbar.make(findViewById(R.id.coordinatorLayout),
//                "No internet connection!", Snackbar.LENGTH_INDEFINITE);

        View snackbarView = snackbar.getView();
        snackbarView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (snackbar!=null){
                    snackbar.dismiss();
                }
            }
        });
        snackbarView.setBackgroundColor(getResources().getColor(R.color.red));
        TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
    }


    @Override
    protected void onDestroy() {
        if(registerNetworkStateReceiver==true) {
            networkStateReceiver.removeListener(this);
            this.unregisterReceiver(networkStateReceiver);
            registerNetworkStateReceiver = false;
        }
        super.onDestroy();
//        networkStateReceiver.removeListener(this);
//        this.unregisterReceiver(networkStateReceiver);
    }

    public boolean isTimeToDisplay() {
        return isTimeToDisplay;
    }

    public void setTimeToDisplay(boolean timeToDisplay) {
        isTimeToDisplay = timeToDisplay;

    }
    public boolean isNetworkAvailable() {
        return isNetworkAvailable;
    }
}
