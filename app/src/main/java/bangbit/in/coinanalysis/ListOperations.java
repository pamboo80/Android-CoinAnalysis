package bangbit.in.coinanalysis;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bangbit.in.coinanalysis.pojo.Coin;
import bangbit.in.coinanalysis.pojo.Currency;
import bangbit.in.coinanalysis.pojo.DashInvestment;
import bangbit.in.coinanalysis.pojo.Datum;
import bangbit.in.coinanalysis.pojo.Exchange;
import bangbit.in.coinanalysis.pojo.Investment;
import bangbit.in.coinanalysis.pojo.LambdaHistory;


/**
 * Created by Nagarajan on 3/7/2018.
 */

public class ListOperations {


    private static final String TAG = ListOperations.class.getSimpleName();


    public static ArrayList<Currency> filterCurrencyList(ArrayList<Currency> currencyArrayList, String query) {
        ArrayList<Currency> filteredList1 = new ArrayList<Currency>();
        if (query.length() > 1) {
            for (Currency row : currencyArrayList) {
                if (row.getSymbol().toLowerCase().contains(query.toLowerCase()) ||
                        row.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList1.add(row);
                }
            }
            return filteredList1;
        } else if (query.length() == 1) {
            for (Currency row : currencyArrayList) {
                if (row.getSymbol().toLowerCase().startsWith(query.toLowerCase()) ||
                        row.getName().toLowerCase().startsWith(query.toLowerCase())) {
                    filteredList1.add(row);
                }
            }
            return filteredList1;
        } else {
            return currencyArrayList;
        }

    }

    public static ArrayList<Coin> filterList(ArrayList<Coin> coinArrayList, String query) {
        ArrayList<Coin> filteredList1 = new ArrayList<Coin>();
        if (query.length() > 1) {
            for (Coin row : coinArrayList) {
                if (row.getSymbol().toLowerCase().contains(query.toLowerCase()) ||
                        row.getName().toLowerCase().contains(query.toLowerCase())) {
                    filteredList1.add(row);
                }
            }
            return filteredList1;
        } else if (query.length() == 1) {
            for (Coin row : coinArrayList) {
                if (row.getSymbol().toLowerCase().startsWith(query.toLowerCase()) ||
                        row.getName().toLowerCase().startsWith(query.toLowerCase())) {
                    filteredList1.add(row);
                }
            }
            return filteredList1;
        } else {
            return coinArrayList;
        }

    }

    public static ArrayList<Coin> filterList(ArrayList<Coin> coinArrayList, String query, int day7, int hours24, int hour1) {

        ArrayList<Coin> filteredList = new ArrayList<>();
        if (query.equals("")) {
            for (Coin coinDetail : coinArrayList) {
                try {
                    if (Double.parseDouble(coinDetail.getPercentChange7d()) > day7 &&
                            Double.parseDouble(coinDetail.getPercentChange24h()) > hours24 &&
                            Double.parseDouble(coinDetail.getPercentChange1h()) > hour1) {
                        filteredList.add(coinDetail);
                    }
                } catch (Exception e) {
                }
            }
        } else {
            int length = query.length();
            for (Coin coinDetail : coinArrayList) {
                try {
                    if (Double.parseDouble(coinDetail.getPercentChange7d()) > day7 &&
                            Double.parseDouble(coinDetail.getPercentChange24h()) > hours24 &&
                            Double.parseDouble(coinDetail.getPercentChange1h()) > hour1) {
                        if (length > 1 && (coinDetail.getSymbol().toLowerCase().contains(query.toLowerCase()) ||
                                coinDetail.getName().toLowerCase().contains(query.toLowerCase()))) {
                            filteredList.add(coinDetail);
                        } else if (length == 1 && (coinDetail.getSymbol().toLowerCase().startsWith(query.toLowerCase()) ||
                                coinDetail.getName().toLowerCase().startsWith(query.toLowerCase()))) {
                            filteredList.add(coinDetail);
                        }

                    }
                } catch (Exception e) {
                }
            }
        }
        return filteredList;

    }

    public static ArrayList<Coin> reverse(ArrayList<Coin> coinDetailArrayList) {
        Collections.reverse(coinDetailArrayList);
        return coinDetailArrayList;
    }

    public static ArrayList<Coin> sortByName(ArrayList<Coin> coinDetailArrayList) {
        Collections.sort(coinDetailArrayList, new Comparator<Coin>() {
            @Override
            public int compare(Coin o1, Coin o2) {
                if (o1.getSymbol() == null) {
                    return (o2.getName() == null) ? 0 : -1;
                }
                if (o2.getName() == null) {
                    return 1;
                }
                return o1.getName().compareTo(o2.getName());
            }
        });
        return coinDetailArrayList;
    }


    public static ArrayList<Coin> sortByRank(ArrayList<Coin> coinDetailArrayList) {
        Collections.sort(coinDetailArrayList, new Comparator<Coin>() {
            @Override
            public int compare(Coin o1, Coin o2) {

                if (o1.getRank() == null) {
                    return (o2.getRank() == null) ? 0 : -1;
                }
                if (o2.getRank() == null) {
                    return 1;
                }
                Float ob1 = Float.parseFloat(o1.getRank());
                Float ob2 = Float.parseFloat(o2.getRank());
                return ob1.compareTo(ob2);
            }
        });
        return coinDetailArrayList;
    }

    public static ArrayList<Coin> sortByPrice(ArrayList<Coin> coinDetailArrayList) {
        Collections.sort(coinDetailArrayList, new Comparator<Coin>() {
            @Override
            public int compare(Coin o1, Coin o2) {
                if (o1.getPriceUsd() == null) {
                    return (o2.getPriceUsd() == null) ? 0 : -1;
                }
                if (o2.getPriceUsd() == null) {
                    return 1;
                }
                Float ob1 = Float.parseFloat(o1.getPriceUsd());
                Float ob2 = Float.parseFloat(o2.getPriceUsd());
                return ob1.compareTo(ob2);
            }
        });
        return coinDetailArrayList;
    }

    public static ArrayList<Coin> sortByVolume(ArrayList<Coin> coinDetailArrayList) {
        Collections.sort(coinDetailArrayList, new Comparator<Coin>() {
            @Override
            public int compare(Coin o1, Coin o2) {
                if (o1.get24hVolumeUsd() == null) {
                    return (o2.get24hVolumeUsd() == null) ? 0 : -1;
                }
                if (o2.get24hVolumeUsd() == null) {
                    return 1;
                }
                Float ob1 = Float.parseFloat(o1.get24hVolumeUsd());
                Float ob2 = Float.parseFloat(o2.get24hVolumeUsd());
                return ob1.compareTo(ob2);

            }
        });
        return coinDetailArrayList;
    }

    public static ArrayList<Coin> sortByMarketcap(ArrayList<Coin> coinDetailArrayList) {
        Collections.sort(coinDetailArrayList, new Comparator<Coin>() {
            @Override
            public int compare(Coin o1, Coin o2) {
                if (o1.getMarketCapUsd() == null) {
                    return (o2.getMarketCapUsd() == null) ? 0 : -1;
                }
                if (o2.getMarketCapUsd() == null) {
                    return 1;
                }
                Double ob1 = Double.parseDouble(o1.getMarketCapUsd());
                Double ob2 = Double.parseDouble(o2.getMarketCapUsd());
                return ob1.compareTo(ob2);
            }
        });
        return coinDetailArrayList;
    }

    public static ArrayList<Coin> sortBySupply(ArrayList<Coin> coinDetailArrayList) {


        Collections.sort(coinDetailArrayList, new Comparator<Coin>() {
            @Override
            public int compare(Coin o1, Coin o2) {
                if (o1.getTotalSupply() == null) {
                    return (o2.getTotalSupply() == null) ? 0 : -1;
                }
                if (o2.getTotalSupply() == null) {
                    return 1;
                }
                Double ob1 = Double.parseDouble(o1.getTotalSupply());
                Double ob2 = Double.parseDouble(o2.getTotalSupply());
                return ob1.compareTo(ob2);
            }
        });

        return coinDetailArrayList;
    }

    public static ArrayList<Coin> sortBy7Day(ArrayList<Coin> coinDetailArrayList) {
        Collections.sort(coinDetailArrayList, new Comparator<Coin>() {
            @Override
            public int compare(Coin o1, Coin o2) {
                if (o1.getPercentChange7d() == null) {
                    return (o2.getPercentChange7d() == null) ? 0 : -1;
                }
                if (o2.getPercentChange7d() == null) {
                    return 1;
                }
                Float ob1 = Float.parseFloat(o1.getPercentChange7d());
                Float ob2 = Float.parseFloat(o2.getPercentChange7d());
                return ob1.compareTo(ob2);
            }
        });
        return coinDetailArrayList;
    }

    public static ArrayList<Coin> sortBy24Hrs(ArrayList<Coin> coinDetailArrayList) {
        Collections.sort(coinDetailArrayList, new Comparator<Coin>() {
            @Override
            public int compare(Coin o1, Coin o2) {
                if (o1.getPercentChange24h() == null) {
                    return (o2.getPercentChange24h() == null) ? 0 : -1;
                }
                if (o2.getPercentChange24h() == null) {
                    return 1;
                }
                Float ob1 = Float.parseFloat(o1.getPercentChange24h());
                Float ob2 = Float.parseFloat(o2.getPercentChange24h());
                return ob1.compareTo(ob2);
            }
        });
        return coinDetailArrayList;
    }

    public static ArrayList<Coin> sortBy1Hr(ArrayList<Coin> coinDetailArrayList) {
        Collections.sort(coinDetailArrayList, new Comparator<Coin>() {
            @Override
            public int compare(Coin o1, Coin o2) {
                if (o1.getPercentChange1h() == null) {
                    return (o2.getPercentChange1h() == null) ? 0 : -1;
                }
                if (o2.getPercentChange1h() == null) {
                    return 1;
                }
                Float ob1 = Float.parseFloat(o1.getPercentChange1h());
                Float ob2 = Float.parseFloat(o2.getPercentChange1h());
                return ob1.compareTo(ob2);
            }
        });
        return coinDetailArrayList;
    }


    public static ArrayList<Datum> sortByOpen(ArrayList<Datum> coinDetailArrayList) {
        Collections.sort(coinDetailArrayList, new Comparator<Datum>() {
            @Override
            public int compare(Datum data1, Datum data2) {
                if (data1.getOpen() == null) {
                    return (data2.getOpen() == null) ? 0 : -1;
                }
                if (data2.getOpen() == null) {
                    return 1;
                }
                Double ob1 = data1.getOpen();
                Double ob2 = data2.getOpen();
                return ob1.compareTo(ob2);
            }

        });
        return coinDetailArrayList;
    }

    public static ArrayList<Datum> sortByClose(ArrayList<Datum> coinDetailArrayList) {
        Collections.sort(coinDetailArrayList, new Comparator<Datum>() {
            @Override
            public int compare(Datum data1, Datum data2) {
                if (data1.getClose() == null) {
                    return (data2.getClose() == null) ? 0 : -1;
                }
                if (data2.getClose() == null) {
                    return 1;
                }
                Double ob1 = data1.getClose();
                Double ob2 = data2.getClose();
                return ob1.compareTo(ob2);
            }

        });
        return coinDetailArrayList;
    }

    public static ArrayList<Datum> sortByDate(ArrayList<Datum> coinDetailArrayList) {
        Collections.sort(coinDetailArrayList, new Comparator<Datum>() {
            @Override
            public int compare(Datum data1, Datum data2) {
                if (data1.getTime() == null) {
                    return (data2.getTime() == null) ? 0 : -1;
                }
                if (data2.getTime() == null) {
                    return 1;
                }
                Integer ob1 = data1.getTime();
                Integer ob2 = data2.getTime();
                return ob1.compareTo(ob2);
            }

        });
        return coinDetailArrayList;
    }

    public static ArrayList<Datum> sortByHigh(ArrayList<Datum> coinDetailArrayList) {
        Collections.sort(coinDetailArrayList, new Comparator<Datum>() {
            @Override
            public int compare(Datum data1, Datum data2) {
                if (data1.getHigh() == null) {
                    return (data2.getHigh() == null) ? 0 : -1;
                }
                if (data2.getHigh() == null) {
                    return 1;
                }
                Double ob1 = data1.getHigh();
                Double ob2 = data2.getHigh();
                return ob1.compareTo(ob2);
            }

        });
        return coinDetailArrayList;
    }

    public static ArrayList<Datum> sortByLow(ArrayList<Datum> coinDetailArrayList) {
        Collections.sort(coinDetailArrayList, new Comparator<Datum>() {
            @Override
            public int compare(Datum data1, Datum data2) {
                if (data1.getLow() == null) {
                    return (data2.getLow() == null) ? 0 : -1;
                }
                if (data2.getLow() == null) {
                    return 1;
                }
                Double ob1 = data1.getLow();
                Double ob2 = data2.getLow();
                return ob1.compareTo(ob2);
            }

        });
        return coinDetailArrayList;
    }


    public static ArrayList<Exchange> sortByExchangeVolume(ArrayList<Exchange> exchanges) {
        Collections.sort(exchanges, new Comparator<Exchange>() {
            @Override
            public int compare(Exchange o1, Exchange o2) {
                if (o1.getVOLUME24HOUR() == null) {
                    return (o2.getVOLUME24HOUR() == null) ? 0 : -1;
                }
                if (o2.getVOLUME24HOUR() == null) {
                    return 1;
                }
                Double ob1 = o1.getVOLUME24HOUR();
                Double ob2 = o2.getVOLUME24HOUR();
                return ob1.compareTo(ob2);

            }
        });
        return exchanges;
    }

    public static ArrayList<Exchange> sortByExchangePrice(ArrayList<Exchange> exchanges) {
        Collections.sort(exchanges, new Comparator<Exchange>() {
            @Override
            public int compare(Exchange o1, Exchange o2) {
                if (o1.getPRICE() == null) {
                    return (o2.getPRICE() == null) ? 0 : -1;
                }
                if (o2.getPRICE() == null) {
                    return 1;
                }
                Double ob1 = o1.getPRICE();
                Double ob2 = o2.getPRICE();
                return ob1.compareTo(ob2);


            }
        });
        for (Exchange e :
                exchanges) {
            Log.d(TAG, "sortByExchangePrice: " + e.getPRICE());
        }
        return exchanges;
    }

    public static ArrayList<Exchange> sortByExchangeName(ArrayList<Exchange> exchanges) {
        Collections.sort(exchanges, new Comparator<Exchange>() {
            @Override
            public int compare(Exchange o1, Exchange o2) {
                if (o1.getVOLUME24HOUR() == null) {
                    return (o2.getMARKET() == null) ? 0 : -1;
                }
                if (o2.getMARKET() == null) {
                    return 1;
                }

                return o1.getMARKET().toLowerCase().compareTo(o2.getMARKET().toLowerCase());

            }
        });
        return exchanges;
    }

    public static ArrayList<Investment> sortByQuantity(ArrayList<Investment> exchanges) {
        Collections.sort(exchanges, new Comparator<Investment>() {
            @Override
            public int compare(Investment o1, Investment o2) {
                Float float1 = o1.getQuantity();
                Float float2 = o2.getQuantity();

                return float1.compareTo(float2);

            }
        });
        return exchanges;
    }

    public static ArrayList<Investment> sortByBoughtPrice(ArrayList<Investment> exchanges) {
        Collections.sort(exchanges, new Comparator<Investment>() {
            @Override
            public int compare(Investment o1, Investment o2) {
                Float boughtPrice1 = o1.getBoughtPrice();
                Float boughtPrice2 = o2.getBoughtPrice();

                return boughtPrice1.compareTo(boughtPrice2);

            }
        });
        return exchanges;
    }

    public static ArrayList<Investment> sortByCost(ArrayList<Investment> exchanges) {
        Collections.sort(exchanges, new Comparator<Investment>() {
            @Override
            public int compare(Investment o1, Investment o2) {
                Float cost1 = o1.getQuantity() * o1.getBoughtPrice();
                Float cost2 = o2.getQuantity() * o2.getBoughtPrice();

                return cost1.compareTo(cost2);

            }
        });
        return exchanges;
    }

    public static ArrayList<DashInvestment> sortByDashCoin(ArrayList<DashInvestment> exchanges) {
        Collections.sort(exchanges, new Comparator<DashInvestment>() {
            @Override
            public int compare(DashInvestment o1, DashInvestment o2) {
                if (o1.getSymbol() == null) {
                    return (o2.getSymbol() == null) ? 0 : -1;
                }
                if (o2.getSymbol() == null) {
                    return 1;
                }
                return o1.getSymbol().toLowerCase().compareTo(o2.getSymbol().toLowerCase());

            }
        });
        return exchanges;
    }

    public static ArrayList<DashInvestment> sortByDashInvestment(ArrayList<DashInvestment> exchanges) {
        Collections.sort(exchanges, new Comparator<DashInvestment>() {
            @Override
            public int compare(DashInvestment o1, DashInvestment o2) {
                Float boughtPrice1 = o1.getCurrentInvestmentValue();
                Float boughtPrice2 = o2.getCurrentInvestmentValue();
                return boughtPrice1.compareTo(boughtPrice2);
            }
        });
        return exchanges;
    }

    public static ArrayList<DashInvestment> sortByDashCost(ArrayList<DashInvestment> exchanges) {
        Collections.sort(exchanges, new Comparator<DashInvestment>() {
            @Override
            public int compare(DashInvestment o1, DashInvestment o2) {
                Float cost1 = o1.getBoughtInvestmentValue();
                Float cost2 = o2.getBoughtInvestmentValue();
                return cost1.compareTo(cost2);
            }
        });
        return exchanges;
    }

    public static List<LambdaHistory> sortByTime(List<LambdaHistory> lambdaHistoryArrayList) {
        Collections.sort(lambdaHistoryArrayList, new Comparator<LambdaHistory>() {
            @Override
            public int compare(LambdaHistory o1, LambdaHistory o2) {
                if (o1.getTime() == null) {
                    return (o2.getTime() == null) ? 0 : -1;
                }
                if (o2.getTime() == null) {
                    return 1;
                }
                Long ob1 = Long.parseLong(o1.getTime());
                Long ob2 = Long.parseLong(o2.getTime());
                return ob1.compareTo(ob2);
            }
        });
        return lambdaHistoryArrayList;
    }

}
