package bangbit.in.coinanalysis.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;

/**
 * Created by Nagarajan on 12/21/2017.
 */

public class NetworkStateReceiver extends BroadcastReceiver {

    private ConnectivityManager connectivityManager;
    private ArrayList<NetworkStateReceiverListener> mListeners;
    private boolean mConnected;

    public NetworkStateReceiver(Context context) {
        mListeners = new ArrayList<NetworkStateReceiverListener>();
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        checkStateChanged();
    }

    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getExtras() == null)
            return;

        if (checkStateChanged()) notifyStateToAll();
    }

    public boolean isConnected()
    {
        return mConnected;
    }
    private boolean checkStateChanged() {
        boolean prev = mConnected;
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        mConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return prev != mConnected;
    }

    private void notifyStateToAll() {
        for (NetworkStateReceiverListener listener : mListeners) {
            notifyState(listener);
        }
    }

    private void notifyState(NetworkStateReceiverListener listener) {
        if (listener != null) {
            if (mConnected) listener.onNetworkAvailable();
            else listener.onNetworkUnavailable();
        }
    }

    public void addListener(NetworkStateReceiverListener networkStateReceiverListener) {
        mListeners.add(networkStateReceiverListener);
        notifyState(networkStateReceiverListener);
    }

    public void removeListener(NetworkStateReceiverListener networkStateReceiverListener) {
        mListeners.remove(networkStateReceiverListener);
    }

    public interface NetworkStateReceiverListener {
        void onNetworkAvailable();

        void onNetworkUnavailable();
    }
}
