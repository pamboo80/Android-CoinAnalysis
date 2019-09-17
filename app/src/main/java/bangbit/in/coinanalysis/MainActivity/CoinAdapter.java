package bangbit.in.coinanalysis.MainActivity;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import bangbit.in.coinanalysis.MyApplication;
import bangbit.in.coinanalysis.R;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.Datum;
import bangbit.in.coinanalysis.pojo.HistoricalData;
import bangbit.in.coinanalysis.repository.HistoryRepository;

import static android.content.ContentValues.TAG;
import static bangbit.in.coinanalysis.Constant.CLICK_TIME_INTERVAL;
import static bangbit.in.coinanalysis.MyApplication.currencySymbol;
import static bangbit.in.coinanalysis.Util.getMillionValue;
import static com.google.gson.internal.$Gson$Preconditions.checkNotNull;


class
CoinAdapter extends RecyclerView.Adapter<CoinAdapter.ViewHolder> {

    static public HashMap<String, String> coinImageUrlHashMap;
    private final HashMap<String, LineData> historicalDataHashMap;
    private Set<String> favoriteCoinSet;

    private ArrayList<Coin> mCoins;
    private ArrayList<Coin> mCoinsOriginalCopy = new ArrayList<>();
    private MainActivity.CoinItemListener mItemListener;
    private double multiplyValue = MyApplication.currencyMultiplyingFactor;
    Context context;
    private long mLastClickTime;
    int lastPosition = 4;


    public CoinAdapter(ArrayList<Coin> coins, HashMap<String, String> coinImageUrlHashMap,
                       HashMap<String, LineData> historicalDataHashMap,
                       Set<String> favoriteCoinSet,
                       MainActivity.CoinItemListener itemListener, Context context) {
        mCoins = coins;
        mCoinsOriginalCopy.addAll(coins);
        CoinAdapter.coinImageUrlHashMap = coinImageUrlHashMap;
        this.historicalDataHashMap = historicalDataHashMap;
        this.favoriteCoinSet = favoriteCoinSet;
        this.context = context;
        mItemListener = itemListener;
    }

    @Override
    public CoinAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View coinView = inflater.inflate(R.layout.coin_list_row, parent, false);
        return new CoinAdapter.ViewHolder(coinView);
    }

    @Override
    public void onBindViewHolder(final CoinAdapter.ViewHolder holder, final int position) { //@@@ Crash reported here
        final Coin coinDetail = mCoins.get(position);
        boolean isFavorite = false;
        holder.chart.setVisibility(View.INVISIBLE);
        holder.noChartImageView.setVisibility(View.VISIBLE);
        if (historicalDataHashMap.get(coinDetail.getSymbol()) == null) {
            HistoryRepository historyRepository = new HistoryRepository();
            historyRepository.getHistoricalData(coinDetail.getSymbol(), "USD", 7, new HistoryRepository.HistoricalDataCallBackWithName() {
                @Override
                public void onRetrieveHistoricalData(HistoricalData historicalData, boolean isSuccess,String sym) {
                    if (isSuccess) {
                        LineData lineData = getGraphData(historicalData.getData());
                        if (lineData != null) {
                            historicalDataHashMap.put(sym, lineData);
                            if (sym.equalsIgnoreCase(coinDetail.getSymbol())) {
                                ILineDataSet dataset = lineData.getDataSetByIndex(0);
                                String[] dates = Util.getDayMonthFromILineDataSet(dataset, 7);
                                holder.textViewStartDate.setText(dates[0]);
                                holder.textViewEndDate.setText(dates[1]);

                                holder.chart.setData(lineData);
                                holder.chart.notifyDataSetChanged();
//                                holder.chart.invalidate(); //@@@

                                holder.chart.setVisibility(View.VISIBLE);
                                holder.chart.setVisibility(View.VISIBLE);
                                holder.textViewStartDate.setVisibility(View.VISIBLE);
                                holder.textViewEndDate.setVisibility(View.VISIBLE);
                                holder.noChartImageView.setVisibility(View.INVISIBLE);
                            }

                        } else {
                            historicalDataHashMap.put(coinDetail.getSymbol(), lineData);
                            holder.chart.setVisibility(View.INVISIBLE);
                            holder.textViewStartDate.setVisibility(View.INVISIBLE);
                            holder.textViewEndDate.setVisibility(View.INVISIBLE);
                            holder.noChartImageView.setVisibility(View.VISIBLE);
                        }
                    }
                }
            });
        } else {
            LineData lineData = historicalDataHashMap.get(coinDetail.getSymbol());
            if (lineData != null) {
                holder.chart.setData(lineData);

                holder.chart.notifyDataSetChanged();
//                holder.chart.invalidate();  //@@@

                ILineDataSet dataset = lineData.getDataSetByIndex(0);
                String[] dates = Util.getDayMonthFromILineDataSet(dataset, 7);
                holder.textViewStartDate.setText(dates[0]);
                holder.textViewEndDate.setText(dates[1]);
                holder.chart.setVisibility(View.VISIBLE);
                holder.textViewStartDate.setVisibility(View.VISIBLE);
                holder.textViewEndDate.setVisibility(View.VISIBLE);
                holder.noChartImageView.setVisibility(View.INVISIBLE);
            } else {
                holder.chart.setVisibility(View.INVISIBLE);
                holder.textViewStartDate.setVisibility(View.INVISIBLE);
                holder.textViewEndDate.setVisibility(View.INVISIBLE);
                holder.noChartImageView.setVisibility(View.VISIBLE);
            }
        }

        final String imageUrl = coinImageUrlHashMap.get(coinDetail.getSymbol());
        if (imageUrl != null) {
            Picasso.with(context).load(imageUrl).placeholder(R.drawable.default_coin).fit().into(holder.imageViewLogo);
        } else {
            Picasso.with(context).load(R.drawable.default_coin).fit().into(holder.imageViewLogo);
            Log.d(TAG, "onBindViewHolder: Fail Coin "+coinDetail.getSymbol());
        }
        if (favoriteCoinSet.contains(coinDetail.getSymbol())) {
            isFavorite = true;
            holder.imageViewFavorite.setImageResource(R.drawable.heart_filled);
        } else {
            holder.imageViewFavorite.setImageResource(R.drawable.heart_unfilled);
        }

        try {

            double priceDouble = 0;
            String price = "";
            try {
                priceDouble = Float.valueOf(coinDetail.getPriceUsd()) * multiplyValue;
                price = String.valueOf(priceDouble);
            } catch (NullPointerException e) {
                price = "NA";
            }
            catch (NumberFormatException e) {
                price = "NA";
            }
            holder.textView7DayPercentage.setTextColor(context.getResources().getColor(R.color.textColor));
            holder.textView24HPercentage.setTextColor(context.getResources().getColor(R.color.textColor));
            holder.textView1HPercentage.setTextColor(context.getResources().getColor(R.color.textColor));

            price = getMillionValue(priceDouble, true);
            if (price.equals("0.00000000")) { //@@@ wrong checking I guess
                price = "NA";
                Log.d(TAG, "onBindViewHolder: " + coinDetail.getSymbol());
            }

            price = ": " + ((price.equals("NA")) ? " " : currencySymbol) + price;
            String coinName = position + 1 + ". " + coinDetail.getName() + " (" + coinDetail.getSymbol() + ")";
            holder.textViewCoinName.setText(coinName);
            holder.textViewPriceValue.setText(price);
            holder.textViewRank.setText("Rank: " + coinDetail.getRank());
            holder.textView7DayPercentage.setText(": " + coinDetail.getPercentChange7d() + "%");
            holder.textView24HPercentage.setText(": " + coinDetail.getPercentChange24h() + "%");
            holder.textView1HPercentage.setText(": " + coinDetail.getPercentChange1h() + "%");

            if (coinDetail.getMarketCapUsd() == null) {
                holder.textViewMcapValue.setText(" :  NA");
            } else {
                holder.textViewMcapValue.setText(" : " + currencySymbol + getMillionValue(Double.parseDouble(coinDetail.getMarketCapUsd()) * multiplyValue, false));
            }

            if (coinDetail.get24hVolumeUsd() == null) {
                holder.textViewVolumeValue.setText(" :  NA");
            } else {
                holder.textViewVolumeValue.setText(" : " + currencySymbol + getMillionValue(Double.parseDouble(coinDetail.get24hVolumeUsd()) * multiplyValue, false));
            }

//            if (coinDetail.getTotalSupply() == null) {
//                holder.textViewSupplyValue.setText("  NA");
//            } else {
//                holder.textViewSupplyValue.setText(currencySymbol + " " + getMillionValue(Double.parseDouble(coinDetail.getTotalSupply()) * multiplyValue, false));
//            }

            if (coinDetail.getPercentChange7d() != null) {
                if (Double.parseDouble(coinDetail.getPercentChange7d()) < 0) {
                    holder.textView7DayPercentage.setTextColor(Color.RED);
                } else {
                    holder.textView7DayPercentage.setTextColor(Color.parseColor("#33A318"));
                }
            } else {
                holder.textView7DayPercentage.setText(":  NA");
            }

            if (coinDetail.getPercentChange24h() != null) {
                if (Double.parseDouble(coinDetail.getPercentChange24h()) < 0) {
                    holder.textView24HPercentage.setTextColor(Color.RED);
                } else {
                    holder.textView24HPercentage.setTextColor(Color.parseColor("#33A318"));
                }
            } else {
                holder.textView24HPercentage.setText(":  NA");
            }

            if (coinDetail.getPercentChange1h() != null) {
                if (Double.parseDouble(coinDetail.getPercentChange1h()) < 0) {
                    holder.textView1HPercentage.setTextColor(Color.RED);
                } else {
                    holder.textView1HPercentage.setTextColor(Color.parseColor("#33A318"));
                }
            } else {
                holder.textView1HPercentage.setText(":  NA");
            }

        } catch (IndexOutOfBoundsException e) {
            //Log.d(TAG, "array");
        }
        catch(NumberFormatException e)
        {
        }

        holder.imageViewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (favoriteCoinSet.contains(coinDetail.getSymbol())) {
                    favoriteCoinSet.remove(coinDetail.getSymbol());
                    holder.imageViewFavorite.setImageResource(R.drawable.heart_unfilled);
                    mItemListener.onClickFavorite(coinDetail, position, false);

                } else {
                    favoriteCoinSet.add(coinDetail.getSymbol());
                    holder.imageViewFavorite.setImageResource(R.drawable.heart_filled);
                    mItemListener.onClickFavorite(coinDetail, position, true);
                }
            }
        });

        final boolean finalIsFavorite = isFavorite;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                long now = System.currentTimeMillis();
                if (now - mLastClickTime < CLICK_TIME_INTERVAL) {
                    return;
                }
                mLastClickTime = now;
                mItemListener.onCoinClick(coinDetail, imageUrl, finalIsFavorite);
            }
        });

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textView7Day, textView24H, textView1H, textView7DayPercentage,
                textView24HPercentage, textView1HPercentage, textViewCoinName,
                textViewMcapValue, textViewVolumeValue, textViewRank, textViewPrice,
                textViewPriceValue, textViewStartDate, textViewEndDate;
        public ImageView imageViewLogo, imageViewFavorite, noChartImageView;
        public LineChart chart;

        public ViewHolder(View itemView) {
            super(itemView);
            textViewCoinName = itemView.findViewById(R.id.textViewCoinName);
            textView7Day = itemView.findViewById(R.id.day7);
            textView24H = itemView.findViewById(R.id.hr24);
            textView1H = itemView.findViewById(R.id.hr1);
            textView7DayPercentage = itemView.findViewById(R.id.day7_value);
            textView24HPercentage = itemView.findViewById(R.id.hr24_value);
            textView1HPercentage = itemView.findViewById(R.id.hr1_value);
            textViewMcapValue = itemView.findViewById(R.id.mcap_value);
            textViewVolumeValue = itemView.findViewById(R.id.vol_value);
//            textViewSupplyValue = (TextView) itemView.findViewById(R.id.textView1HPercentage);
            textViewRank = itemView.findViewById(R.id.textViewRank);
            textViewPrice = itemView.findViewById(R.id.price);
            textViewPriceValue = itemView.findViewById(R.id.price_value);
            textViewStartDate = itemView.findViewById(R.id.textViewStartDate);
            textViewEndDate = itemView.findViewById(R.id.textViewEndDate);
            imageViewLogo = itemView.findViewById(R.id.coin_icon);
            imageViewFavorite = itemView.findViewById(R.id.imageViewFavorit);
            noChartImageView = itemView.findViewById(R.id.no_chart_ImageView);

            chart = itemView.findViewById(R.id.chart);
            chart.getDescription().setEnabled(false);
            chart.setDrawBorders(false);
            chart.setDrawGridBackground(false);
            chart.getAxisRight().setEnabled(false);
            chart.getAxisLeft().setEnabled(false);
            chart.getAxisLeft().setSpaceTop(0);
            chart.getAxisLeft().setSpaceBottom(0);
            chart.getXAxis().setEnabled(false);
            chart.getLegend().setEnabled(false);
            chart.setViewPortOffsets(0, 0, 0, 0);
            chart.setTouchEnabled(false);

        }

    }

    public void replaceData(ArrayList<Coin> coins) {
        mCoins = checkNotNull(coins);
        notifyDataSetChanged();
    }

    public void replaceAllData(ArrayList<Coin> coins) {
        mCoins = checkNotNull(coins);
        mCoinsOriginalCopy.clear();
        mCoinsOriginalCopy.addAll(coins);
    }


    private LineData getGraphData(ArrayList<Datum> historicalData) {

        ArrayList<Entry> yVals = new ArrayList<Entry>();
        int i = 0;
        for (Datum datum : historicalData) {
            double open = datum.getOpen();
            float val = (float) open;
            yVals.add(new Entry(datum.getTime(), val));
            i++;
            if (i == 0) {
                Log.d(TAG, "Entry " + val);
            }

        }
        if (yVals.size() == 0) {
            return null;
        }
        // create a dataset and give it a type
        Collections.sort(yVals, new EntryXComparator());
        LineDataSet set1 = new LineDataSet(yVals, "DataSet 1");
        set1.setDrawFilled(true);
        set1.setColor(context.getResources().getColor(R.color.coin_header));
        set1.setFillColor(context.getResources().getColor(R.color.coin_header));

        set1.setDrawValues(false);
        set1.setLineWidth(1f);

        set1.setDrawCircles(false);
        set1.setColor(context.getResources().getColor(R.color.coin_header));
        set1.setDrawValues(false);

        // create a data object with the datasets
        LineData data = new LineData(set1);

        return data;
    }


    public void updateCurrncy(double multiplyValue) {
        this.multiplyValue = multiplyValue;
        notifyDataSetChanged();
    }

    public void updateHashMap(final HashMap<String, String> coinImageUrlHashMap) {

        if (CoinAdapter.coinImageUrlHashMap.size() == 0) {
            CoinAdapter.coinImageUrlHashMap = coinImageUrlHashMap;
            return;
        }


        CoinAdapter.coinImageUrlHashMap = coinImageUrlHashMap;
    }

    public void updateFavoriteSet(Set<String> favoriteCoinSet) {
        this.favoriteCoinSet = favoriteCoinSet;
        notifyDataSetChanged();
    }


    public ArrayList<Coin> getmCoins() {
        return mCoins;
    }

    public ArrayList<Coin> getmCoinsOriginalCopy() {
        return mCoinsOriginalCopy;
    }

    public Set<String> getFavoriteCoinSet() {
        return favoriteCoinSet;
    }

    @Override
    public int getItemCount() {
        return mCoins.size();
    }

    public Coin getItem(int position) {
        return mCoins.get(position);
    }


//    public void filter(String query, int day7, int hours24, int hour1, boolean isFilterPercentageApply) {
//        ArrayList<Coin> filteredList = new ArrayList<>();
//        if (isFilterPercentageApply) {
//            Log.d("CoinAdapter", "filter: ");
//            for (Coin coinDetail : mCoinsOriginalCopy) {
//                try {
//                    if (Double.parseDouble(coinDetail.getPercentChange7d()) > day7 &&
//                            Double.parseDouble(coinDetail.getPercentChange24h()) > hours24 &&
//                            Double.parseDouble(coinDetail.getPercentChange1h()) > hour1) {
//                        filteredList.add(coinDetail);
//                    } else {
////                    Log.d(TAG, day7 + "filter: " + coinDetail.getName() + coinDetail.getPercentChange7d());
//                    }
//                } catch (Exception e) {
//                }
//            }
//            mCoins = filteredList;
//        } else {
//            filteredList.addAll(mCoinsOriginalCopy);
//            mCoins = filteredList;
//
//        }
//        if (!query.equals("")) {
//            ArrayList<Coin> filteredList1 = new ArrayList<Coin>();
//            for (Coin row : mCoins) {
//                if (row.getSymbol().toLowerCase().contains(query.toLowerCase()) ||
//                        row.getName().toLowerCase().contains(query.toLowerCase())) {
//                    filteredList1.add(row);
//                }
//            }
//            mCoins = filteredList1;
//
//        }
//        notifyDataSetChanged();
//    }
}