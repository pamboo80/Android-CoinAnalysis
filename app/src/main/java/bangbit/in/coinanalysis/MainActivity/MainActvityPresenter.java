package bangbit.in.coinanalysis.MainActivity;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.stream.Collectors;

import bangbit.in.coinanalysis.PojoUtil;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.database.DatabaseHelper;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.repository.CoinListCallback;
import bangbit.in.coinanalysis.repository.CoinRepository;
import bangbit.in.coinanalysis.repository.PojoUtilCallback;

/**
 * Created by Nagarajan on 3/14/2018.
 */

public class MainActvityPresenter implements MainActivityContract.UserActionsListener {

    DatabaseHelper databaseHelper;
    final ArrayList<Coin> coinsBG = new ArrayList<Coin>();

    private final CoinRepository mCoinRepository;
    MainActivityContract.View view;
    private String TAG = MainActvityPresenter.class.getSimpleName();

    HashMap<String, Coin> coinHashMap=new HashMap<>();
    ArrayList<Coin> mainCoinArrayList=new ArrayList<>();
    public MainActvityPresenter(MainActivityContract.View view, CoinRepository mCoinRepository) {
        this.view = view;
        this.mCoinRepository = mCoinRepository;
        databaseHelper = new DatabaseHelper((Context) view);
    }

    @Override
    public void filterButtonClick(boolean showFilter) {
        view.displayFilter(showFilter);

    }

    @Override
    public void switchFilterCilck(boolean active) {
        view.enableFilterInteraction(active);

    }

    boolean isloading;


    public final void coinUpdateUIFirstTime(boolean isSuccess) {
        view.setProgressIndicator(false);
        if (isSuccess) {
            view.showTryAgainMessage(false);
            if (coinsBG!=null && coinsBG.size()>0){
                view.setLastUpdatedString(coinsBG.get(0).getLastUpdated());
                for (Coin coin:coinsBG) {
                    coinHashMap.put(coin.getSymbol(), coin);
                }
                mainCoinArrayList.clear();
                mainCoinArrayList.addAll(coinHashMap.values());
            }
            if (mainCoinArrayList.size()<=0){
                view.showTryAgainMessage(true);
            }else {
                view.showCoins(mainCoinArrayList);
            }

        } else {
            if (coinsBG != null && coinsBG.size() > 0) {
                view.showTryAgainMessage(false);
                view.setLastUpdatedString(coinsBG.get(0).getLastUpdated());
                for (Coin coin:coinsBG) {
                    coinHashMap.put(coin.getSymbol(), coin);
                }
                mainCoinArrayList.clear();
                mainCoinArrayList.addAll(coinHashMap.values());
            }
            if (mainCoinArrayList.size()>0){
                view.showCoins(mainCoinArrayList);
            }else {
                Log.d(TAG, "coinCallback: mainCoinArrayList"+mainCoinArrayList.size());
                view.showTryAgainMessage(true);
            }
        }

        //@@@ update to sqlLiteDB
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String data = Util.getCoinListAsString(coinsBG);
                databaseHelper.updateCoin(data);
            }
        };
        if (coinsBG!=null && coinsBG.size()>0){
            new Thread(runnable).start();
        }

        isloading = false;
    }

    public final void coinUpdateUI() {

        view.showTryAgainMessage(false);
        view.setProgressIndicator(false);
        if (coinsBG!=null && coinsBG.size()>0){
            view.setLastUpdatedString(coinsBG.get(0).getLastUpdated());
            for (Coin coin:coinsBG) {
                coinHashMap.put(coin.getSymbol(), coin);
            }
            mainCoinArrayList.clear();
            mainCoinArrayList.addAll(coinHashMap.values());
        }
        if (mainCoinArrayList.size()>0){
            view.showCoins(mainCoinArrayList);
        }else {
            Log.d(TAG, "coinCallback: mainCoinArrayList"+mainCoinArrayList.size());
            view.showTryAgainMessage(true);
        }

        //@@@ update to sqlLiteDB
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                String data = Util.getCoinListAsString(coinsBG);
                databaseHelper.updateCoin(data);
            }
        };
        if (coinsBG!=null && coinsBG.size()>0){
            new Thread(runnable).start();
        }

        isloading = false;
    }

    @Override
    public void loadCoinsV2(boolean inBackground) {
        if (isloading) {
            return;
        }else {
            isloading = true;
        }
        Log.d(TAG, "loadCoinsV2 called");
        if (inBackground) {

            mCoinRepository.getCoinsInBackground(new CoinListCallback() {
                @Override
                public void getCoinDetailPro(JSONArray jsonArray) {
                    if(jsonArray!=null && jsonArray.length() >0)
                    {
                        PojoUtil.getCoinDetailPro(jsonArray, new PojoUtilCallback() {
                            @Override
                            public void onGetCoinDetailProCallback(ArrayList<Coin> coinArrayList) {
                                synchronized (CoinRepository.class) {
                                    coinsBG.clear();
                                    coinsBG.addAll(coinArrayList); //@@@ synchronized??? .addAll()??? why not coins = coinArrayList
                                    new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            coinUpdateUI();
                                        }
                                    },100);
                                }
                            }
                        });
                    }
                }

                @Override
                public void coinCallback(ArrayList<Coin> coinArrayList, boolean isSuccess) {
                    Log.d(TAG, "coinCallback: updateDataBG "+isSuccess);
                    view.setProgressIndicator(false);
                    if (isSuccess) {
                       view.setProgressIndicator(false);
                        if (coinArrayList!=null && coinArrayList.size()>0){
                            view.setLastUpdatedString(coinArrayList.get(0).getLastUpdated());
                            for (Coin coin:coinArrayList) {
                                coinHashMap.put(coin.getSymbol(), coin);
                            }
                            mainCoinArrayList.clear();
                            mainCoinArrayList.addAll(coinHashMap.values());
                        }
                        if (mainCoinArrayList.size()>0){
                            view.showCoins(mainCoinArrayList);
                        }else {
                            Log.d(TAG, "coinCallback: mainCoinArrayList"+mainCoinArrayList.size());
                            view.showTryAgainMessage(true);
                        }

                    }else {
                        if (mainCoinArrayList.size()>0){
                            //view.setLastUpdatedString(mainCoinArrayList.get(0).getLastUpdated());
                        }
                        else
                        {
                            view.showTryAgainMessage(true);
                        }
                    }
                    isloading = false;
                }

                @Override
                public void coinCallbackFirst(JSONArray coinArrayList) {

                }

                @Override
                public void getDatabaseData(ArrayList<Coin> coinArrayList) {

                }
            });

        } else {
            view.setProgressIndicator(true);
            mCoinRepository.getCoinsV2(new CoinListCallback() {
                @Override
                public void getCoinDetailPro(JSONArray jsonArray) {
                    PojoUtil.getCoinDetailPro(jsonArray, new PojoUtilCallback() {
                        @Override
                        public void onGetCoinDetailProCallback(ArrayList<Coin> coinArrayList) {
                            synchronized (CoinRepository.class) {
                                coinsBG.clear();
                                coinsBG.addAll(coinArrayList); //@@@ synchronized??? .addAll()??? why not coins = coinArrayList
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        coinUpdateUI();
                                    }
                                },100);
                            }
                        }
                    });
                }
                @Override
                public void coinCallback(ArrayList<Coin> coinArrayList, boolean isSuccess) {
                    Log.d(TAG, "coinCallback: updateCoinDetails "+isSuccess);
                    view.setProgressIndicator(false);
                    if (isSuccess) {
                        view.showTryAgainMessage(false);
                        if (coinArrayList!=null && coinArrayList.size()>0){
                            view.setLastUpdatedString(coinArrayList.get(0).getLastUpdated());
                            for (Coin coin:coinArrayList) {
                                coinHashMap.put(coin.getSymbol(), coin);
                            }
                            mainCoinArrayList.clear();
                            mainCoinArrayList.addAll(coinHashMap.values());
                            if (mainCoinArrayList.size()>0){
                                view.showCoins(mainCoinArrayList);
                            }else {
                                Log.d(TAG, "coinCallback: mainCoinArrayList"+mainCoinArrayList.size());
                                view.showTryAgainMessage(true);
                            }
                        }
                        else
                        {
                            if (mainCoinArrayList.size()==0){
                                view.showTryAgainMessage(true);
                            }
                        }

                    }else {
                        if (mainCoinArrayList.size()==0){
                           view.showTryAgainMessage(true);
                        }
                    }
                    isloading = false;

                }

                @Override
                public void coinCallbackFirst(JSONArray jsonArray) {
                    Log.d(TAG, "coinCallbackFirst: updateCoinDetails ");
                    PojoUtil.getCoinDetailPro(jsonArray, new PojoUtilCallback() {
                        @Override
                        public void onGetCoinDetailProCallback(ArrayList<Coin> coinArrayList) {
                            synchronized (CoinRepository.class) {
                                coinsBG.clear();
                                coinsBG.addAll(coinArrayList); //@@@ synchronized??? .addAll()??? why not coins = coinArrayList
                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(coinsBG.size()>0)
                                        {
                                            coinUpdateUIFirstTime(true);
                                        }
                                        else
                                        {
                                            coinUpdateUIFirstTime(false);
                                        }
                                    }
                                },100);
                            }
                        }
                    });

                }

                @Override
                public void getDatabaseData(ArrayList<Coin> coinArrayList) {
                    view.setProgressIndicator(false);
                    if (coinArrayList !=null && coinArrayList.size() > 0) {
                        Log.d("tag", "getCoinsV2: getDatabaseData : updateCoinDetails " + coinArrayList.size());
                        view.showTryAgainMessage(false);
                        if (coinArrayList!=null && coinArrayList.size()>0){
                            view.setLastUpdatedString(coinArrayList.get(0).getLastUpdated());
                            for (Coin coin:coinArrayList) {
                                coinHashMap.put(coin.getSymbol(), coin);
                            }
                            mainCoinArrayList.clear();
                            mainCoinArrayList.addAll(coinHashMap.values());
                        }
                        view.showCoins(mainCoinArrayList);
                    }
                    else
                    {
                        view.showTryAgainMessage(true);
                    }
                    isloading = false;
                }
            });

        }
    }

}
