package bangbit.in.coinanalysis.repository;

import android.content.Context;

import java.util.ArrayList;

import bangbit.in.coinanalysis.database.DatabaseHelper;
import bangbit.in.coinanalysis.pojo.Investment;

/**
 * Created by Nagarajan on 3/28/2018.
 */

public class InvestmentRepository {
    private final Context context;
    private DatabaseHelper databaseHelper;

    public InvestmentRepository(Context context) {
        this.context = context;
        databaseHelper = new DatabaseHelper(context);
    }

    public boolean insertInvestment(String coinSymbol, float quantity, float boughtPrice, String boughtDate, String notes) {
        return databaseHelper.insertInvestment(coinSymbol, quantity, boughtPrice, boughtDate, notes);
    }

    public boolean updateInvestment(int id,String coinSymbol, float quantity, float boughtPrice, String boughtDate, String notes){
        return databaseHelper.updateInvestment(id, coinSymbol, quantity, boughtPrice, boughtDate, notes);
    }
    public boolean deleteInvestment(int id){
        return databaseHelper.deleteInvestment(id);
    }

    public ArrayList<Investment> getAllInvestmrnt(String symbol){
        return databaseHelper.getAllInvestment(symbol);
    }

    public ArrayList<Investment> getAllCoinInvestmrnt(){
        return databaseHelper.getAllCoinInvestment();
    }

}
