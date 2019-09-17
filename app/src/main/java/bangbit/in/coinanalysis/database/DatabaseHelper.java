package bangbit.in.coinanalysis.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import bangbit.in.coinanalysis.Chat.ChatData;
import bangbit.in.coinanalysis.Util;
import bangbit.in.coinanalysis.pojo.Investment;

/**
 * Created by Nagarajan on 3/9/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 2;

    // Database Name
    private static final String DATABASE_NAME = "CoinAnalysisDatabase";

    private static final String TABLE_COIN_URL = "COIN_MISC";
    private static final String TABLE_CURRENCY_FACTOR = "CURRENCY_FACTOR";
    private static final String TABLE_FAVORITE_COIN = "FAVORITE_COIN";
    private static final String TABLE_INVESTMENT = "INVESTMENT";
    private static final String TABLE_COINLIST = "COINLIST";

    private static final String COIN_URL = "COIN_IMAGE_URL";
    private static final String COIN_SYMBOL = "COIN_SYMBOL";

    private static final String ID = "ID";
    private static final String QUANTITY = "QUANTITY";
    private static final String BOUGHT_PRICE = "BOUGHT_PRICE";
    private static final String BOUGHT_DATE = "BOUGHT_DATE";
    private static final String NOTES = "NOTES";
    private static final String CURRENCY_SYMBOL = "CURRENCY_SYMBOL";
    private static final String FACTOR = "FACTOR";
    private static final String DATA = "DATA";
    private static final String TIME = "TIME";

    private static final String CHAT_DATA = "CHAT_DATA";
    private static final String DATE = "DATE";
    private static final String TABLE_CHAT = "TABLE_CHAT";

    private static final String CREATE_TABLE_CHAT = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CHAT + "(" + ID + " INTEGER PRIMARY KEY," + CHAT_DATA + " TEXT," + DATE + " TEXT,"
            + TIME + " TEXT" + ")";

    private static final String CREATE_TABLE_INVESTMENT = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INVESTMENT + "(" + ID + " INTEGER PRIMARY KEY," + COIN_SYMBOL + " TEXT," + QUANTITY + " REAL,"
            + BOUGHT_PRICE + " REAL," + BOUGHT_DATE + " TEXT," + NOTES + " TEXT" + ")";

    private static final String CREATE_TABLE_COIN_URL = "CREATE TABLE IF NOT EXISTS "
            + TABLE_COIN_URL + "(" + COIN_SYMBOL + " TEXT PRIMARY KEY," + COIN_URL
            + " TEXT " + ")";

    private static final String CREATE_TABLE_FAVORITE_COIN = "CREATE TABLE IF NOT EXISTS "
            + TABLE_FAVORITE_COIN + "(" + COIN_SYMBOL + " TEXT PRIMARY KEY)";

    private static final String CREATE_TABLE_CURRENCY_FACTOR = "CREATE TABLE IF NOT EXISTS "
            + TABLE_CURRENCY_FACTOR + "(" + CURRENCY_SYMBOL + " TEXT PRIMARY KEY," + FACTOR + " REAL)";

    private static final String CREATE_TABLE_COINLIST = "CREATE TABLE IF NOT EXISTS "
            + TABLE_COINLIST + "(" + ID + " INTEGER PRIMARY KEY," + DATA + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_COIN_URL);
        db.execSQL(CREATE_TABLE_FAVORITE_COIN);
        db.execSQL(CREATE_TABLE_INVESTMENT);
        db.execSQL(CREATE_TABLE_CURRENCY_FACTOR);
        db.execSQL(CREATE_TABLE_COINLIST);
        db.execSQL(CREATE_TABLE_CHAT);
        insertInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void init()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_TABLE_COIN_URL);
        db.execSQL(CREATE_TABLE_FAVORITE_COIN);
        db.execSQL(CREATE_TABLE_INVESTMENT);
        db.execSQL(CREATE_TABLE_CURRENCY_FACTOR);
        db.execSQL(CREATE_TABLE_COINLIST);
        db.execSQL(CREATE_TABLE_CHAT);
        insertInitialData(db);
        db.close();
    }

    public boolean insertOne(String symbol, String coinUrl) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COIN_SYMBOL, symbol);
        values.put(COIN_URL, coinUrl);
        long result = db.insert(TABLE_COIN_URL, null, values);
        db.close();
        return result != -1;
    }


    public boolean insertInvestment(String coinSymbol, float quantity, float boughtPrice, String boughtDate, String notes) {
        SQLiteDatabase db = null;
        long result=-1;
        try{
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COIN_SYMBOL, coinSymbol);
            values.put(QUANTITY, quantity);
            values.put(BOUGHT_PRICE, boughtPrice);
            values.put(BOUGHT_DATE, boughtDate);
            values.put(NOTES, notes);
            result = db.insert(TABLE_INVESTMENT, null, values);
            db.close();
        }
        catch(SQLiteException  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }

        return result != -1;
    }

    public boolean deleteInvestment(int id) {

        SQLiteDatabase db = null;
        long result=-1;
        try{
            db = this.getWritableDatabase();
            result = db.delete(TABLE_INVESTMENT, ID + " = ?",
                    new String[]{String.valueOf(id)});

            db.close();
        }
        catch(SQLiteException  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }

        return result != 0;
    }

    public String getCoins() {

        SQLiteDatabase db = null;
        String data = "";
        try{
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_COINLIST + " WHERE " + ID + "=1", null);
            if (cursor.moveToFirst()) {
                do {
                    data = cursor.getString((cursor.getColumnIndex(DATA)));
                } while (cursor.moveToNext());
            }
            db.close();
        }
        catch(SQLiteException  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }

        return data;
    }

    public boolean updateCoin(String data) {

        SQLiteDatabase db = null;
        long result=-1;
        try{
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(ID, 1);
            values.put(DATA, data);

            result = db.replace(TABLE_COINLIST, null, values);
            db.close();
        }
        catch(SQLiteException  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }

        return result != -1;
    }

    public HashMap<String, Float> getCurrencyFactor() {

        SQLiteDatabase db = null;
        HashMap<String, Float> currencyFactor = new HashMap<>();
        try{
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CURRENCY_FACTOR, null);
            if (cursor.moveToFirst()) {
                do {
                    String key = cursor.getString((cursor.getColumnIndex(CURRENCY_SYMBOL)));
                    float value = cursor.getFloat((cursor.getColumnIndex(FACTOR)));
                    currencyFactor.put(key, value);
                } while (cursor.moveToNext());
            }

            db.close();
        }
        catch(SQLiteException  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }

        return currencyFactor;
    }

    public boolean updateCurrencyFactor(String coinSymbol, float quantity) {

        SQLiteDatabase db = null;
        long result=-1;
        try{
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(CURRENCY_SYMBOL, coinSymbol);
            values.put(FACTOR, quantity);
            result = db.replace(TABLE_CURRENCY_FACTOR, null, values);
//        long result = db.replace(TABLE_CURRENCY_FACTOR, values, CURRENCY_SYMBOL + " = ?",
//                new String[]{coinSymbol});
            db.close();
        }
        catch(SQLiteException  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }

        return result != -1;
    }

    public boolean updateInvestment(int id, String coinSymbol, float quantity, float boughtPrice, String boughtDate, String notes) {

        SQLiteDatabase db = null;
        long result=-1;
        try{
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COIN_SYMBOL, coinSymbol);
            values.put(QUANTITY, quantity);
            values.put(BOUGHT_PRICE, boughtPrice);
            values.put(BOUGHT_DATE, boughtDate);
            values.put(NOTES, notes);
            result = db.update(TABLE_INVESTMENT, values, ID + " = ?",
                    new String[]{String.valueOf(id)});
            db.close();
        }
        catch(SQLiteException  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }

        return result != -1;
    }

    public ArrayList<Investment> getAllInvestment(String coinSymbol) {
        SQLiteDatabase db = null;
        ArrayList<Investment> investmentList = new ArrayList<>(0);
        try{
            db = this.getReadableDatabase();
//        String query="SELECT * FROM " + TABLE_FAVORITE_COIN+" WHERE "+COIN_SYMBOL+"= "+coinSymbol;
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_INVESTMENT + " WHERE " + COIN_SYMBOL + "=\"" + coinSymbol + "\"", null);
            if (cursor.moveToFirst()) {
                do {
                    Investment investment = new Investment();
                    investment.setId(cursor.getInt((cursor.getColumnIndex(ID))));
                    investment.setCoinSymbol(coinSymbol);
                    investment.setQuantity(cursor.getFloat((cursor.getColumnIndex(QUANTITY))));
                    investment.setBoughtPrice(cursor.getFloat((cursor.getColumnIndex(BOUGHT_PRICE))));
                    investment.setBoughtDate(cursor.getString((cursor.getColumnIndex(BOUGHT_DATE))));
                    investment.setNotes(cursor.getString((cursor.getColumnIndex(NOTES))));
                    investmentList.add(investment);
                } while (cursor.moveToNext());
            }
            db.close();
        }
        catch(SQLiteException  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }

        return investmentList;
    }

    public ArrayList<Investment> getAllCoinInvestment() {

        SQLiteDatabase db = null;
        ArrayList<Investment> investmentList = new ArrayList<>(0);
        try{
          db = this.getReadableDatabase();
//        String query="SELECT * FROM " + TABLE_FAVORITE_COIN+" WHERE "+COIN_SYMBOL+"= "+coinSymbol;
//        Cursor cursor = db.rawQuery("SELECT  " + COIN_SYMBOL + ", SUM(" + QUANTITY + "), "+BOUGHT_PRICE + " FROM " + TABLE_INVESTMENT + " GROUP BY " + COIN_SYMBOL, null);
            Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_INVESTMENT, null);
            if (cursor.moveToFirst()) {
                do {
                    Investment investment = new Investment();
//                investment.setCoinSymbol(cursor.getString(0));
//                investment.setQuantity(cursor.getFloat(1));
//                investment.setBoughtPrice(cursor.getFloat(2));
                    investment.setCoinSymbol(cursor.getString((cursor.getColumnIndex(COIN_SYMBOL))));
                    investment.setId(cursor.getInt((cursor.getColumnIndex(ID))));
                    investment.setQuantity(cursor.getFloat((cursor.getColumnIndex(QUANTITY))));
                    investment.setBoughtPrice(cursor.getFloat((cursor.getColumnIndex(BOUGHT_PRICE))));
                    investment.setBoughtDate(cursor.getString((cursor.getColumnIndex(BOUGHT_DATE))));
                    investment.setNotes(cursor.getString((cursor.getColumnIndex(NOTES))));
                    investmentList.add(investment);
                } while (cursor.moveToNext());
            }

            db.close();
        }
        catch(SQLiteException  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }

        return investmentList;
    }

    public void insertNewImageURL(String insertValues)
    {
        SQLiteDatabase db = null;
        try{
            if(insertValues.compareTo("")==0) return;
            String INSERT_COIN_MISC_DATA = "INSERT INTO COIN_MISC(COIN_SYMBOL,COIN_IMAGE_URL) VALUES" + insertValues+";";
            db = this.getWritableDatabase();
            db.execSQL(INSERT_COIN_MISC_DATA);
            db.close();
        }
        catch(SQLiteException  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }
    }

    public int updateImageUrl(String symbol, String coinUrl) {
        SQLiteDatabase db = null;
        int result=-1;
        try{
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COIN_URL, coinUrl);
            result= db.update(TABLE_COIN_URL, values, COIN_SYMBOL + " = ?",
                    new String[]{symbol});
        }
        catch(SQLiteException  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }

        return result;
    }

    public HashMap<String, String> getImageUrlHashMap() {

        SQLiteDatabase db = null;
        HashMap<String, String> coinImageUrlHashmap = new HashMap<>();

        int result=-1;
        try{
            db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from " + TABLE_COIN_URL, null);
            if (cursor.moveToFirst()) {
                do {
                    String key = cursor.getString((cursor.getColumnIndex(COIN_SYMBOL)));
                    String value = cursor.getString((cursor.getColumnIndex(COIN_URL)));
                    coinImageUrlHashmap.put(key, value);
                } while (cursor.moveToNext());
            }
            db.close();
        }
        catch(SQLiteException  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }

        return coinImageUrlHashmap;
    }

    public String getCoinUrl(String coinSymbol) {

        SQLiteDatabase db = null;
        Cursor cursor=null;

        try{
            db = this.getReadableDatabase();
            cursor = db.rawQuery("select * from " + TABLE_COIN_URL+" where "+COIN_SYMBOL+" = \""+coinSymbol+"\"", null);
            if (cursor.moveToFirst()) {
                String value = cursor.getString((cursor.getColumnIndex(COIN_URL)));
                db.close();
                return value;
            }
            db.close();
        }
        catch(SQLiteException  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }

        return null;
    }

    public Set<String> getFavoriteCoinSet() {

        SQLiteDatabase db = null;
        Cursor cursor=null;
        Set<String> coinFavoriteSet = new HashSet<>();

        try{
            db = this.getReadableDatabase();
            cursor = db.rawQuery("select * from " + TABLE_FAVORITE_COIN, null);
            if (cursor.moveToFirst()) {
                do {
                    String key = cursor.getString((cursor.getColumnIndex(COIN_SYMBOL)));
                    coinFavoriteSet.add(key);
                } while (cursor.moveToNext());
            }
            db.close();
        }
        catch(SQLiteException  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }

        return coinFavoriteSet;
    }

    public boolean isCoinFavorited(String coinSymbol) {
        SQLiteDatabase db = null;
        Cursor cursor=null;
        try{
            db = this.getReadableDatabase();
            cursor = db.rawQuery("select * from " + TABLE_FAVORITE_COIN + " WHERE " + COIN_SYMBOL + "=\"" + coinSymbol + "\"", null);
        }
        catch(SQLiteException  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }

        return (cursor==null)?false:cursor.getCount() >= 1;
    }

    public boolean insertFavoriteCoin(String symbol) {
        SQLiteDatabase db = null;
        long result=-1;
        try{
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COIN_SYMBOL, symbol);
            result = db.insert(TABLE_FAVORITE_COIN, null, values);
            db.close();
        }
        catch(SQLiteException  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }

        return result != -1;
    }

    public boolean removeFavoriteCoin(String symbol) {
        SQLiteDatabase db = null;
        long result=-1;
        try{
            db = this.getWritableDatabase();
            result = db.delete(TABLE_FAVORITE_COIN, COIN_SYMBOL + " = ?",
                    new String[]{symbol});

            db.close();
        }
        catch(SQLiteException  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }

        return result != 0;
    }


    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    public void insertInitialData(SQLiteDatabase db) {

        String INSERT_COIN_MISC_DATA1 = "INSERT INTO COIN_MISC(COIN_SYMBOL,COIN_IMAGE_URL) VALUES" +
                "('CRAVE*','https://www.cryptocompare.com/media/27010610/crave_logo.png')," +
                "('MDA','https://www.cryptocompare.com/media/12318340/mda.png')," +
                "('ABT','https://www.cryptocompare.com/media/14913683/abt.png')," +
                "('EPY*','https://www.cryptocompare.com/media/14913643/epy.png')," +
                "('HBT','https://www.cryptocompare.com/media/9350768/hbt.png')," +
                "('CIR','https://www.cryptocompare.com/media/1382506/cir.png'),"+
                "('BLOCKPAY','https://www.cryptocompare.com/media/351508/blockpay.png')," +
                "('LNC','https://www.cryptocompare.com/media/20780792/lnc.png')," +
                "('XLM','https://www.cryptocompare.com/media/20696/str.png')," +
                "('SONG','https://www.cryptocompare.com/media/20432/song.png')," +
                "('CHIP','https://www.cryptocompare.com/media/20685/chip.png')," +
                "('GNX','https://www.cryptocompare.com/media/20780652/gnx.png')," +
                "('MARX','https://www.cryptocompare.com/media/1382578/marx.png')," +
                "('SFC','https://www.cryptocompare.com/media/1382639/sfc.png')," +
                "('MSR','https://www.cryptocompare.com/media/14913574/msr.png')," +
                "('WAND','https://www.cryptocompare.com/media/12318182/wandxlogo_new1.png')," +
                "('BLRY','https://www.cryptocompare.com/media/350908/blry.png')," +
                "('GXC','https://www.cryptocompare.com/media/9350766/gxc.png')," +
                "('CARE','https://www.cryptocompare.com/media/27010669/care.png')," +
                "('CWX','https://www.cryptocompare.com/media/20780607/cwx.png')," +
                "('AERO','https://www.cryptocompare.com/media/19594/aero.png')," +
                "('AIX','https://www.cryptocompare.com/media/1383807/aig.png')," +
                "('WILD','https://www.cryptocompare.com/media/12318298/wild.png')," +
                "('AST','https://www.cryptocompare.com/media/12318279/ast.png')," +
                "('VOT','https://www.cryptocompare.com/media/16746442/vot.png')," +
                "('HUSH','https://www.cryptocompare.com/media/1383138/thehush_300x300.png')," +
                "('SND','https://www.cryptocompare.com/media/12318128/snd.png')," +
                "('UTH','https://www.cryptocompare.com/media/350998/uth.png')," +
                "('PCN','https://www.cryptocompare.com/media/16746648/pcn.png')," +
                "('LINX','https://www.cryptocompare.com/media/9350783/linx.png')," +
                "('CAB','https://www.cryptocompare.com/media/350896/cab.png')," +
                "('UBTC','https://www.cryptocompare.com/media/25792626/ubc.png')," +
                "('CXO','https://www.cryptocompare.com/media/27010577/cxo.png')," +
                "('AEC','https://www.cryptocompare.com/media/350991/aec.png')," +
                "('BITUSD','https://www.cryptocompare.com/media/351491/bitusd.png')," +
                "('GBT','https://www.cryptocompare.com/media/350897/gbt.png')," +
                "('CCOS','https://www.cryptocompare.com/media/16746767/ccos.png')," +
                "('STEPS','https://www.cryptocompare.com/media/350952/steps.png')," +
                "('KRM','https://www.cryptocompare.com/media/27010812/krm.png')," +
                "('EDRC','https://www.cryptocompare.com/media/350858/edrc.jpg')," +
                "('FCS','https://www.cryptocompare.com/media/19808/mrs.png')," +
                "('BTCH','https://www.cryptocompare.com/media/27010951/btch.jpg')," +
                "('COMP','https://www.cryptocompare.com/media/27010929/comp.jpg')," +
                "('SCRPT','https://www.cryptocompare.com/media/351210/scrpt.png')," +
                "('AION','https://www.cryptocompare.com/media/16746538/aion.png')," +
                "('GRAM','https://www.cryptocompare.com/media/20688/gram.png')," +
                "('BRC*','https://www.cryptocompare.com/media/25792572/brc.png')," +
                "('KMD','https://www.cryptocompare.com/media/351408/kmd.png')," +
                "('MED*','https://www.cryptocompare.com/media/16746766/med.png')," +
                "('CRDNC','https://www.cryptocompare.com/media/27010816/crdnc.png')," +
                "('CNMT','https://www.cryptocompare.com/media/351396/cnmt.png')," +
                "('WGO','https://www.cryptocompare.com/media/1382998/wgo.png')," +
                "('PRES','https://www.cryptocompare.com/media/1382432/pres.png')," +
                "('HGT','https://www.cryptocompare.com/media/9350692/hgt.jpg')," +
                "('FOTA','https://www.cryptocompare.com/media/27010497/fota.png')," +
                "('CNBC','https://www.cryptocompare.com/media/16746541/cnbc.png')," +
                "('STCN','https://www.cryptocompare.com/media/9350727/stcn.png')," +
                "('DFT','https://www.cryptocompare.com/media/9350712/dft.png')," +
                "('ADN','https://www.cryptocompare.com/media/350993/adn.png')," +
                "('VANY','https://www.cryptocompare.com/media/27010900/vany.png')," +
                "('DOGETH','https://www.cryptocompare.com/media/352023/dogeth-2.png')," +
                "('BPT','https://www.cryptocompare.com/media/27010454/bpt.png')," +
                "('ESC*','https://www.cryptocompare.com/media/14913585/esc.png')," +
                "('SYNX','https://www.cryptocompare.com/media/15887426/synx.png')," +
                "('SIGT','https://www.cryptocompare.com/media/9350710/sigt.png')," +
                "('BST','https://www.cryptocompare.com/media/350558/bst.png')," +
                "('HEEL','https://www.cryptocompare.com/media/1382051/heel.png')," +
                "('GTO','https://www.cryptocompare.com/media/16746671/gto.png')," +
                "('XBC','https://www.cryptocompare.com/media/20488/xbc.png')," +
                "('BPL','https://www.cryptocompare.com/media/14913604/bpl.png')," +
                "('CPY','https://www.cryptocompare.com/media/25792602/cpy.png')," +
                "('BLN','https://www.cryptocompare.com/media/25792607/bln.png')," +
                "('EAC','https://www.cryptocompare.com/media/19690/eac.png')," +
                "('SXUT','https://www.cryptocompare.com/media/27010511/sxdt.png')," +
                "('CFD','https://www.cryptocompare.com/media/14913552/cfd.png')," +
                "('QBT*','https://www.cryptocompare.com/media/27010469/oloethnw_400x400.jpg')," +
                "('VEE','https://www.cryptocompare.com/media/12318044/vee.png')," +
                "('BMXT','https://www.cryptocompare.com/media/1383984/bmxt.png')," +
                "('ADCN','https://www.cryptocompare.com/media/350983/adcn.png')," +
                "('GRS','https://www.cryptocompare.com/media/20780736/grs.png')," +
                "('FIRE','https://www.cryptocompare.com/media/351625/fire.png')," +
                "('FAIR*','https://www.cryptocompare.com/media/27010514/fair.png')," +
                "('RPX','https://www.cryptocompare.com/media/12318339/rpx.png')," +
                "('START','https://www.cryptocompare.com/media/19916/start.png')," +
                "('CYC','https://www.cryptocompare.com/media/19671/cyc.png')," +
                "('GEMZ','https://www.cryptocompare.com/media/19710/frac.png')," +
                "('VEN','https://www.cryptocompare.com/media/12318129/ven.png')," +
                "('OXY','https://www.cryptocompare.com/media/9350753/oxy.png')," +
                "('VIDZ','https://www.cryptocompare.com/media/1382172/vidz.png')," +
                "('GES','https://www.cryptocompare.com/media/14913547/ges.png')," +
                "('IMPCH','https://www.cryptocompare.com/media/1382606/impch.png')," +
                "('REX','https://www.cryptocompare.com/media/12318086/rex.png')," +
                "('UNAT','https://www.cryptocompare.com/media/20456/unat.png')," +
                "('USX','https://www.cryptocompare.com/media/27010758/usx.png')," +
                "('MPRO','https://www.cryptocompare.com/media/350999/mpro.jpg')," +
                "('GAI','https://www.cryptocompare.com/media/27010839/gai.png')," +
                "('NAS2','https://www.cryptocompare.com/media/350776/nas2.png')," +
                "('CSTL','https://www.cryptocompare.com/media/15887421/cstl.png')," +
                "('XPTX','https://www.cryptocompare.com/media/14913633/xptx.png')," +
                "('UQC','https://www.cryptocompare.com/media/16746443/uqc.png')," +
                "('DIG','https://www.cryptocompare.com/media/30001609/dig.png')," +
                "('RMC','https://www.cryptocompare.com/media/14913570/rmc.png')," +
                "('CAM','https://www.cryptocompare.com/media/20220/cam.png')," +
                "('HYPER','https://www.cryptocompare.com/media/19744/hyper.png')," +
                "('SQL','https://www.cryptocompare.com/media/20441/sql.png')," +
                "('NBT','https://www.cryptocompare.com/media/20363/nbt.png')," +
                "('ZRC','https://www.cryptocompare.com/media/1383548/xzc.png')," +
                "('LTCU','https://www.cryptocompare.com/media/16746704/ltcu.png')," +
                "('ANC','https://www.cryptocompare.com/media/19598/anc.png')," +
                "('BIX','https://www.cryptocompare.com/media/27010664/bix.png')," +
                "('BBP','https://www.cryptocompare.com/media/27010855/bbp.png')," +
                "('CRDS','https://www.cryptocompare.com/media/14913675/crds.png')," +
                "('BQ','https://www.cryptocompare.com/media/12318325/bq.png')," +
                "('C2','https://www.cryptocompare.com/media/19640/c2.png')," +
                "('WOMEN','https://www.cryptocompare.com/media/12318379/women.png')," +
                "('VSX','https://www.cryptocompare.com/media/12318194/vsx.png')," +
                "('UBQ','https://www.cryptocompare.com/media/1382441/ubq.png')," +
                "('CRB','https://www.cryptocompare.com/media/1382904/crbit1.png')," +
                "('INC','https://www.cryptocompare.com/media/1382173/inc.png')," +
                "('UMC','https://www.cryptocompare.com/media/1383983/umb.png')," +
                "('ZUP','https://www.cryptocompare.com/media/30001659/zup.png')," +
                "('CRTM','https://www.cryptocompare.com/media/9350790/crtm.jpg')," +
                "('DRG','https://www.cryptocompare.com/media/25792571/drg.png')," +
                "('RBTC','https://www.cryptocompare.com/media/16746652/rbtc.png')," +
                "('POLL','https://www.cryptocompare.com/media/12318144/poll.png')," +
                "('ARCT','https://www.cryptocompare.com/media/25792638/arct.png')," +
                "('CAN','https://www.cryptocompare.com/media/12318218/canya.png')," +
                "('ICN','https://www.cryptocompare.com/media/351400/icn.png')," +
                "('ABYSS','https://www.cryptocompare.com/media/16746451/abyss.png')," +
                "('CBT','https://www.cryptocompare.com/media/25792645/cbt.png')," +
                "('FST','https://www.cryptocompare.com/media/19720/fst.png')," +
                "('ATS','https://www.cryptocompare.com/media/12318356/ats.png')," +
                "('TSL','https://www.cryptocompare.com/media/20780749/tsl.png')," +
                "('XWC','https://www.cryptocompare.com/media/350911/xwc_1.png')," +
                "('CTR','https://www.cryptocompare.com/media/1384029/ctr.png')," +
                "('FLY','https://www.cryptocompare.com/media/351013/fly.png')," +
                "('1CR','https://www.cryptocompare.com/media/20175/1cr.png')," +
                "('SSV','https://www.cryptocompare.com/media/20060/ssv.png')," +
                "('BDL','https://www.cryptocompare.com/media/14913539/bdl.png')," +
                "('REP','https://www.cryptocompare.com/media/350815/augur-logo.png')," +
                "('SVD','https://www.cryptocompare.com/media/27010949/svd.png')," +
                "('DGDC','https://www.cryptocompare.com/media/351520/dgd.png')," +
                "('BTCM','https://www.cryptocompare.com/media/16746489/btcm.png')," +
                "('VAL','https://www.cryptocompare.com/media/27010515/val.png')," +
                "('EBIT','https://www.cryptocompare.com/media/27010588/ebit-logo.png')," +
                "('CYDER','https://www.cryptocompare.com/media/25792617/cyder.png')," +
                "('DCC','https://www.cryptocompare.com/media/19808/mrs.png')," +
                "('GOFF','https://www.cryptocompare.com/media/27011056/untitled-1.png')," +
                "('GNR','https://www.cryptocompare.com/media/20780791/gnr.png')," +
                "('NODE','https://www.cryptocompare.com/media/20373/node.png')," +
                "('MYC','https://www.cryptocompare.com/media/350947/myc.png')," +
                "('GOTX','https://www.cryptocompare.com/media/351071/gotx.png')," +
                "('BTA','https://www.cryptocompare.com/media/1383113/bta1.png')," +
                "('WEB','https://www.cryptocompare.com/media/12318169/web.png')," +
                "('N7','https://www.cryptocompare.com/media/350995/n7.jpg')," +
                "('BMT','https://www.cryptocompare.com/media/16746638/bmt.png')," +
                "('CHIPS*','https://www.cryptocompare.com/media/16746673/chips.png')," +
                "('FLAP','https://www.cryptocompare.com/media/20032/flap.png')," +
                "('DIME','https://www.cryptocompare.com/media/25792612/dime.png')," +
                "('AST*','https://www.cryptocompare.com/media/1382170/ast.png')," +
                "('HNC*','https://www.cryptocompare.com/media/14913529/hnc.png')," +
                "('MAN','https://www.cryptocompare.com/media/16746574/man.png')," +
                "('CSNO','https://www.cryptocompare.com/media/1383922/csno.png')," +
                "('KLC','https://www.cryptocompare.com/media/352024/klc.png')," +
                "('BRD','https://www.cryptocompare.com/media/20780589/brd.png')," +
                "('MOOND','https://www.cryptocompare.com/media/351558/moond.png')," +
                "('DEA','https://www.cryptocompare.com/media/1383264/dea.png')," +
                "('LTCH','https://www.cryptocompare.com/media/30001658/ltch.png')," +
                "('PBL','https://www.cryptocompare.com/media/16404866/pbl.png')," +
                "('NZC','https://www.cryptocompare.com/media/351366/nzc.png')," +
                "('SAR','https://www.cryptocompare.com/media/350901/sar.png')," +
                "('BLAS','https://www.cryptocompare.com/media/25792611/blas.png')," +
                "('SRN','https://www.cryptocompare.com/media/14913556/srn.png')," +
                "('LIFE','https://www.cryptocompare.com/media/14913568/life.png')," +
                "('ELTCOIN','https://www.cryptocompare.com/media/16746556/eltcoin.png')," +
                "('AMP','https://www.cryptocompare.com/media/350825/amp.png')," +
                "('SRT','https://www.cryptocompare.com/media/9350771/srt.png')," +
                "('REPUX','https://www.cryptocompare.com/media/27010482/repux.png')," +
                "('DBR','https://www.cryptocompare.com/media/14913687/dbr.png')," +
                "('XFC','https://www.cryptocompare.com/media/20574/xfc.png')," +
                "('GRM','https://www.cryptocompare.com/media/20642/grm.png')," +
                "('SPN','https://www.cryptocompare.com/media/1383861/spn.png')," +
                "('BLITZ','https://www.cryptocompare.com/media/350612/blitz.png')," +
                "('EVR','https://www.cryptocompare.com/media/12318332/evr.png')," +
                "('LUX','https://www.cryptocompare.com/media/12318422/lux.png')," +
                "('BUZZ','https://www.cryptocompare.com/media/15887419/buzz.png')," +
                "('RUST','https://www.cryptocompare.com/media/351365/rust.png')," +
                "('DRZ','https://www.cryptocompare.com/media/20605/drz.png')," +
                "('ERY','https://www.cryptocompare.com/media/1382403/ely2.png')," +
                "('AXYS','https://www.cryptocompare.com/media/27011049/axys.jpg')," +
                "('LEAF','https://www.cryptocompare.com/media/27010927/leaf.jpg')," +
                "('PEX','https://www.cryptocompare.com/media/351992/pex.png')," +
                "('FUND','https://www.cryptocompare.com/media/27011031/fund.png')," +
                "('WISH','https://www.cryptocompare.com/media/16404892/wish.png')," +
                "('VRP*','https://www.cryptocompare.com/media/12318284/vrt.png')," +
                "('ICC','https://www.cryptocompare.com/media/15887424/icc.png')," +
                "('CHAN','https://www.cryptocompare.com/media/1383831/chan2.png')," +
                "('TOK','https://www.cryptocompare.com/media/20780659/tok.png')," +
                "('HPC','https://www.cryptocompare.com/media/1383046/hpc.png')," +
                "('HSR','https://www.cryptocompare.com/media/12318137/hsr.png')," +
                "('SMART','https://www.cryptocompare.com/media/1383906/smart.png')," +
                "('TODAY','https://www.cryptocompare.com/media/351521/today.png')," +
                "('RBX','https://www.cryptocompare.com/media/1383197/rbx.png')," +
                "('VMC','https://www.cryptocompare.com/media/19943/vmc.png')," +
                "('FUZZ','https://www.cryptocompare.com/media/351025/fuzz.png')," +
                "('TRIA','https://www.cryptocompare.com/media/16404852/tria.png')," +
                "('FLIK','https://www.cryptocompare.com/media/12318230/flik.png')," +
                "('KCN','https://www.cryptocompare.com/media/1383953/kencoin.png')," +
                "('IBANK','https://www.cryptocompare.com/media/351238/ibank.png')," +
                "('FSBT','https://www.cryptocompare.com/media/25792673/fsbt.png')," +
                "('QSLV','https://www.cryptocompare.com/media/20404/qslv.png')," +
                "('PEN','https://www.cryptocompare.com/media/20384/pen.png')," +
                "('UP','https://www.cryptocompare.com/media/12318374/up_arrow.png')," +
                "('ROCK*','https://www.cryptocompare.com/media/14913607/rock1.png')," +
                "('TCT','https://www.cryptocompare.com/media/27010460/tct.png')," +
                "('VVI','https://www.cryptocompare.com/media/27010869/vvi.png')," +
                "('FCT','https://www.cryptocompare.com/media/1382863/fct1.png')," +
                "('SHA','https://www.cryptocompare.com/media/27010477/sha.png')," +
                "('ONT','https://www.cryptocompare.com/media/30001663/ont.jpg')," +
                "('ETG','https://www.cryptocompare.com/media/12318378/etg.png')," +
                "('GCR','https://www.cryptocompare.com/media/20708/gcr.png')," +
                "('PING','https://www.cryptocompare.com/media/1383706/ping1.png')," +
                "('GVT','https://www.cryptocompare.com/media/14913634/gvt.png')," +
                "('PTA','https://www.cryptocompare.com/media/1382236/pta.png')," +
                "('SUPER','https://www.cryptocompare.com/media/20061/super.png')," +
                "('FUTC','https://www.cryptocompare.com/media/20558/futc.png')," +
                "('BBT','https://www.cryptocompare.com/media/15887410/bbt.png')," +
                "('BON','https://www.cryptocompare.com/media/351045/bon_1.png')," +
                "('FRN','https://www.cryptocompare.com/media/350992/frn.png')," +
                "('MDS','https://www.cryptocompare.com/media/20780773/ipnvhhke_400x400.jpg')," +
                "('MNX','https://www.cryptocompare.com/media/14913648/mnx.png')," +
                "('GBX','https://www.cryptocompare.com/media/15887411/gbx.png')," +
                "('888','https://www.cryptocompare.com/media/351639/888.png')," +
                "('CELL','https://www.cryptocompare.com/media/20227/cell.png')," +
                "('EVE','https://www.cryptocompare.com/media/27010481/eve.png')," +
                "('TIO','https://www.cryptocompare.com/media/14913488/tio.png')," +
                "('TKT','https://www.cryptocompare.com/media/12318093/tkt.png')," +
                "('QUN','https://www.cryptocompare.com/media/27010466/qun.png')," +
                "('SGN','https://www.cryptocompare.com/media/27010455/sgn.png')," +
                "('BCY','https://www.cryptocompare.com/media/350903/bcy.png')," +
                "('DTX','https://www.cryptocompare.com/media/25792685/dtx.png')," +
                "('BTCR','https://www.cryptocompare.com/media/351554/btr.png')," +
                "('BFX','https://www.cryptocompare.com/media/19554/bitfinex.png')," +
                "('CFT*','https://www.cryptocompare.com/media/9350747/credo.jpg')," +
                "('XCE','https://www.cryptocompare.com/media/20573/xce.png')," +
                "('COB','https://www.cryptocompare.com/media/9350700/cobin.png')," +
                "('YOYOW','https://www.cryptocompare.com/media/12318178/yoyow.png')," +
                "('STX','https://www.cryptocompare.com/media/1383946/stx.png')," +
                "('SKC','https://www.cryptocompare.com/media/27010793/skc.jpg')," +
                "('NEBL','https://www.cryptocompare.com/media/1384016/nebl.png')," +
                "('XNX','https://www.cryptocompare.com/media/351033/xnx.jpg')," +
                "('PPC','https://www.cryptocompare.com/media/19864/peercoin-logo.png')," +
                "('SMF','https://www.cryptocompare.com/media/1382468/xmf.png')," +
                "('AERM','https://www.cryptocompare.com/media/16404893/aerm.png')," +
                "('UNO','https://www.cryptocompare.com/media/20065/uno.png')," +
                "('ETS','https://www.cryptocompare.com/media/27011054/ets.jpg')," +
                "('ZEN*','https://www.cryptocompare.com/media/20780609/zen.png')," +
                "('MMC','https://www.cryptocompare.com/media/19795/mmc.png')," +
                "('DON','https://www.cryptocompare.com/media/1382995/don.png')," +
                "('AURS','https://www.cryptocompare.com/media/12318345/aurs.png')," +
                "('SFR','https://www.cryptocompare.com/media/19903/sfr.png')," +
                "('BTX','https://www.cryptocompare.com/media/1383895/btx.png')," +
                "('EZM','https://www.cryptocompare.com/media/12318143/ezm.png')," +
                "('WYR','https://www.cryptocompare.com/media/12318413/wyr.png')," +
                "('HAZE','https://www.cryptocompare.com/media/1382595/haze.png')," +
                "('SETH','https://www.cryptocompare.com/media/16746447/seth.png')," +
                "('CESC','https://www.cryptocompare.com/media/350786/cesc.png')," +
                "('BCCOIN','https://www.cryptocompare.com/media/9350709/bccoin1.png')," +
                "('CLUB','https://www.cryptocompare.com/media/350609/club.png')," +
                "('GROW','https://www.cryptocompare.com/media/350934/grow.png')," +
                "('PXI','https://www.cryptocompare.com/media/350559/pxi.png')," +
                "('MI','https://www.cryptocompare.com/media/20711/mi.png')," +
                "('RBR','https://www.cryptocompare.com/media/20408/rbr.png')," +
                "('CDX','https://www.cryptocompare.com/media/16746425/cdx.png')," +
                "('TES','https://www.cryptocompare.com/media/19927/tes.png')," +
                "('SBTC','https://www.cryptocompare.com/media/16746666/sbtc.png')," +
                "('NEU*','https://www.cryptocompare.com/media/20721/neu.png')," +
                "('MRV','https://www.cryptocompare.com/media/1384009/mrv.png')," +
                "('TKS','https://www.cryptocompare.com/media/352207/tks.jpg')," +
                "('MBT','https://www.cryptocompare.com/media/12318238/mbt.png')," +
                "('STAR*','https://www.cryptocompare.com/media/351043/star.jpg')," +
                "('DUBI','https://www.cryptocompare.com/media/27010453/dubi.png')," +
                "('EVC','https://www.cryptocompare.com/media/12318064/evc.png')," +
                "('SPHR','https://www.cryptocompare.com/media/19808/mrs.png')," +
                "('BNTY','https://www.cryptocompare.com/media/20780588/bnty.png')," +
                "('GUESS','https://www.cryptocompare.com/media/12318217/guess.png')," +
                "('RBIT','https://www.cryptocompare.com/media/351405/rbit.png')," +
                "('UFR','https://www.cryptocompare.com/media/16404855/ufr.png')," +
                "('LTD','https://www.cryptocompare.com/media/1382049/ltd.png')," +
                "('EXRN','https://www.cryptocompare.com/media/20780642/exrn.png')," +
                "('RIPT','https://www.cryptocompare.com/media/15887409/ript.png')," +
                "('CJT','https://www.cryptocompare.com/media/14913575/cjt.png')," +
                "('FLIXX','https://www.cryptocompare.com/media/16404862/flixx.png')," +
                "('BUCKS*','https://www.cryptocompare.com/media/20602/bucks.png')," +
                "('HNC','https://www.cryptocompare.com/media/20702/hnc.png')," +
                "('HTC','https://www.cryptocompare.com/media/350888/htc.png')," +
                "('VIA','https://www.cryptocompare.com/media/20070/via.png')," +
                "('FUNK','https://www.cryptocompare.com/media/27010926/funk.png')," +
                "('TRC','https://www.cryptocompare.com/media/19938/terracoin.png')," +
                "('AXIOM','https://www.cryptocompare.com/media/20686/axiom.png')," +
                "('KNC**','https://www.cryptocompare.com/media/16404850/knc.png')," +
                "('TER','https://www.cryptocompare.com/media/12318324/ter.png')," +
                "('XWT','https://www.cryptocompare.com/media/19808/mrs.png')," +
                "('CCT','https://www.cryptocompare.com/media/12317979/cct1.png')," +
                "('BERN','https://www.cryptocompare.com/media/350973/bern.png')," +
                "('NRN','https://www.cryptocompare.com/media/12318047/nrn.png')," +
                "('ASTRO','https://www.cryptocompare.com/media/14913641/astro.png')," +
                "('GHS','https://www.cryptocompare.com/media/19565/cexio.png')," +
                "('GER','https://www.cryptocompare.com/media/16746741/ger.png')," +
                "('UNITY','https://www.cryptocompare.com/media/350935/unity_1.png')," +
                "('ENE','https://www.cryptocompare.com/media/351506/ene.png')," +
                "('FRAZ','https://www.cryptocompare.com/media/9350764/fraz.png')," +
                "('CARBON','https://www.cryptocompare.com/media/351017/carbon.png')," +
                "('TAM','https://www.cryptocompare.com/media/20565/tam.png')," +
                "('POKER','https://www.cryptocompare.com/media/27011048/poker.jpg')," +
                "('DRKC','https://www.cryptocompare.com/media/20027/drkc.png')," +
                "('LUCKY','https://www.cryptocompare.com/media/351946/lucky.png')," +
                "('CDY','https://www.cryptocompare.com/media/27010814/bcy.jpg')," +
                "('UNIT','https://www.cryptocompare.com/media/350954/unit.png')," +
                "('LGO','https://www.cryptocompare.com/media/25792655/lgo.png')," +
                "('CHAT','https://www.cryptocompare.com/media/1382762/chat.png')," +
                "('SBD','https://www.cryptocompare.com/media/350907/steem.png')," +
                "('DGD','https://www.cryptocompare.com/media/350851/dgd.png')," +
                "('SKB','https://www.cryptocompare.com/media/19808/mrs.png')," +
                "('POA','https://www.cryptocompare.com/media/27010991/poa.png')," +
                "('TPAY*','https://www.cryptocompare.com/media/27010575/tpay.png')," +
                "('VIN','https://www.cryptocompare.com/media/27010882/vin.jpg')," +
                "('ACOIN','https://www.cryptocompare.com/media/20079/acoin.png')," +
                "('VIVO','https://www.cryptocompare.com/media/12318337/vivo.png')," +
                "('ATKN','https://www.cryptocompare.com/media/12318083/atkn.png')," +
                "('COFI','https://www.cryptocompare.com/media/16746551/cofi.png')," +
                "('DTC**','https://www.cryptocompare.com/media/27010849/dtc-1.png')," +
                "('SPF','https://www.cryptocompare.com/media/20780669/spf.png')," +
                "('BAC','https://www.cryptocompare.com/media/350913/bac.png')," +
                "('BIGUP','https://www.cryptocompare.com/media/350980/bigup.png')," +
                "('RCX','https://www.cryptocompare.com/media/350902/rcx.png')," +
                "('BOB','https://www.cryptocompare.com/media/20207/bob.png')," +
                "('EBET','https://www.cryptocompare.com/media/12318358/ebet.png')," +
                "('GB','https://www.cryptocompare.com/media/351411/db.png')," +
                "('XRED','https://www.cryptocompare.com/media/12318269/xred.png')," +
                "('ELF','https://www.cryptocompare.com/media/20780600/elf.png')," +
                "('DAI','https://www.cryptocompare.com/media/27010778/dai.jpg')," +
                "('COV*','https://www.cryptocompare.com/media/351503/cov.png')," +
                "('ABC','https://www.cryptocompare.com/media/12318006/bac.png')," +
                "('STO','https://www.cryptocompare.com/media/351493/sto.png')," +
                "('NUM','https://www.cryptocompare.com/media/350932/num.png')," +
                "('NMS','https://www.cryptocompare.com/media/20780637/nms.png')," +
                "('NKA','https://www.cryptocompare.com/media/20367/nka.png')," +
                "('MBRS','https://www.cryptocompare.com/media/1384010/mbrs.png')," +
                "('ALIS','https://www.cryptocompare.com/media/12318247/alis.png')," +
                "('XRP','https://www.cryptocompare.com/media/19972/ripple.png')," +
                "('ATM','https://www.cryptocompare.com/media/14913430/atm.png')," +
                "('FSN','https://www.cryptocompare.com/media/351495/fsn.png')," +
                "('DOGED','https://www.cryptocompare.com/media/20029/doged.png')," +
                "('RNDR','https://www.cryptocompare.com/media/12318381/rndr.png')," +
                "('DUTCH','https://www.cryptocompare.com/media/14913487/dutch.png')," +
                "('DBET','https://www.cryptocompare.com/media/14913561/dbet.png')," +
                "('LSK','https://www.cryptocompare.com/media/27011060/lsk.png')," +
                "('GP','https://www.cryptocompare.com/media/20656/gp.png')," +
                "('ORLY','https://www.cryptocompare.com/media/351076/orly.png')," +
                "('FOR','https://www.cryptocompare.com/media/20780764/for.png')," +
                "('COVAL','https://www.cryptocompare.com/media/351519/coval.png')," +
                "('MC','https://www.cryptocompare.com/media/351428/mc.png')," +
                "('TRIG','https://www.cryptocompare.com/media/351489/trg.png')," +
                "('FNT','https://www.cryptocompare.com/media/16746591/fnt.png')," +
                "('JDC','https://www.cryptocompare.com/media/1384049/jdc.png')," +
                "('SAT2','https://www.cryptocompare.com/media/19897/sat2.png')," +
                "('IOST','https://www.cryptocompare.com/media/27010459/iost.png')," +
                "('PLM','https://www.cryptocompare.com/media/12318124/plm.jpg')," +
                "('SPK','https://www.cryptocompare.com/media/20780696/spk.png')," +
                "('JET','https://www.cryptocompare.com/media/20780772/jet.png')," +
                "('OBS','https://www.cryptocompare.com/media/351064/obs.png')," +
                "('HZ','https://www.cryptocompare.com/media/20320/hz.png')," +
                "('TRTL','https://www.cryptocompare.com/media/27010966/untitled-1.png')," +
                "('MOT','https://www.cryptocompare.com/media/27010462/mot.png')," +
                "('TRX','https://www.cryptocompare.com/media/12318089/trx.png')," +
                "('BCT','https://www.cryptocompare.com/media/30001675/bct.png')," +
                "('FLOT','https://www.cryptocompare.com/media/20780733/flot.png')," +
                "('DRS','https://www.cryptocompare.com/media/352072/drs.png')," +
                "('ACE','https://www.cryptocompare.com/media/11999076/ace.png')," +
                "('MAT','https://www.cryptocompare.com/media/12318162/mat.png')," +
                "('ENTRP','https://www.cryptocompare.com/media/1383969/ent.png')," +
                "('DOC*','https://www.cryptocompare.com/media/25792637/doc.png')," +
                "('BTD','https://www.cryptocompare.com/media/351498/btd.png')," +
                "('SPEND','https://www.cryptocompare.com/media/27010502/spend.png')," +
                "('XUN','https://www.cryptocompare.com/media/16746548/xun.png')," +
                "('VLT','https://www.cryptocompare.com/media/351514/vlt.png')," +
                "('CCC','https://www.cryptocompare.com/media/1383980/ccc.png')," +
                "('NCASH','https://www.cryptocompare.com/media/27010960/untitled-1.png')," +
                "('DCR','https://www.cryptocompare.com/media/1382607/decred.png')," +
                "('ALTOCAR','https://www.cryptocompare.com/media/9350746/altc.png')," +
                "('GRC','https://www.cryptocompare.com/media/20307/grc.png')," +
                "('AMT','https://www.cryptocompare.com/media/9350756/amt.jpg')," +
                "('IETH','https://www.cryptocompare.com/media/20780777/ieth.png')," +
                "('MTRC','https://www.cryptocompare.com/media/16746642/mtrc.png')," +
                "('LIPC','https://www.cryptocompare.com/media/27011055/lipc.jpg')," +
                "('BON*','https://www.cryptocompare.com/media/12318368/bon.png')," +
                "('CNX','https://www.cryptocompare.com/media/11417632/cnx.png')," +
                "('XSI','https://www.cryptocompare.com/media/20165/xsi.png')," +
                "('MLS','https://www.cryptocompare.com/media/9350696/mls.png');";

        db.execSQL(INSERT_COIN_MISC_DATA1);

        String INSERT_COIN_MISC_DATA2 = "INSERT INTO COIN_MISC(COIN_SYMBOL,COIN_IMAGE_URL) VALUES " +
                "('DEUR','https://www.cryptocompare.com/media/351018/deur.png')," +
                "('NVT','https://www.cryptocompare.com/media/27010863/navi.png')," +
                "('LGBTQ','https://www.cryptocompare.com/media/350996/lgbtq.png')," +
                "('DRXNE','https://www.cryptocompare.com/media/14913608/drxne.png')," +
                "('CROAT','https://www.cryptocompare.com/media/27010508/croat.png')," +
                "('DRKT','https://www.cryptocompare.com/media/20604/drkt.png')," +
                "('NTM','https://www.cryptocompare.com/media/12318286/ntm.png')," +
                "('TRV','https://www.cryptocompare.com/media/9350789/trv.png')," +
                "('NTCC','https://www.cryptocompare.com/media/1382346/ntcc.png')," +
                "('DLT','https://www.cryptocompare.com/media/1384001/delta.png')," +
                "('CASH*','https://www.cryptocompare.com/media/1384020/cpp.png')," +
                "('QLC','https://www.cryptocompare.com/media/20780665/qlc.png')," +
                "('IPC*','https://www.cryptocompare.com/media/27010824/ipc.jpg')," +
                "('XCO','https://www.cryptocompare.com/media/20486/xco.png')," +
                "('ZNE','https://www.cryptocompare.com/media/351515/zne.jpg')," +
                "('DP','https://www.cryptocompare.com/media/1383772/dp.png')," +
                "('UTC','https://www.cryptocompare.com/media/19942/utc.png')," +
                "('BWK','https://www.cryptocompare.com/media/16746590/bwk.png')," +
                "('SOIL','https://www.cryptocompare.com/media/350949/soil.png')," +
                "('WBTC*','https://www.cryptocompare.com/media/1383045/wbtc.png')," +
                "('LTA','https://www.cryptocompare.com/media/9350693/lta.png')," +
                "('XPO','https://www.cryptocompare.com/media/351590/xpo.png')," +
                "('STN','https://www.cryptocompare.com/media/25792600/stn.png')," +
                "('LTCR','https://www.cryptocompare.com/media/1382097/ltcr.png')," +
                "('S8C','https://www.cryptocompare.com/media/1382093/s8c.png')," +
                "('WRT','https://www.cryptocompare.com/media/12317963/wrt.png')," +
                "('VSL','https://www.cryptocompare.com/media/352113/d5a4e4f0366d3ae8cdbc45ad097f664c2557a55f0c237c1710-pimgpsh_fullsize_distr.jpg')," +
                "('SNS','https://www.cryptocompare.com/media/351494/sns.png')," +
                "('EDDIE','https://www.cryptocompare.com/media/12318382/eddie.png')," +
                "('SRC','https://www.cryptocompare.com/media/19918/src.png')," +
                "('RUPX','https://www.cryptocompare.com/media/14913484/rupx.png')," +
                "('XVG','https://www.cryptocompare.com/media/12318032/xvg.png')," +
                "('DSH','https://www.cryptocompare.com/media/20026/dash.png')," +
                "('BENJI','https://www.cryptocompare.com/media/1383163/benji.png')," +
                "('AUTH','https://www.cryptocompare.com/media/1384019/auth.png')," +
                "('EVN','https://www.cryptocompare.com/media/14913587/env.png')," +
                "('GEA','https://www.cryptocompare.com/media/16746475/gea.png')," +
                "('PRM','https://www.cryptocompare.com/media/350906/prm.png')," +
                "('IMV','https://www.cryptocompare.com/media/25792642/imv.png')," +
                "('INN','https://www.cryptocompare.com/media/14913536/inn.png')," +
                "('NRO','https://www.cryptocompare.com/media/16746592/nro.png')," +
                "('PYN','https://www.cryptocompare.com/media/12318033/pyn.png')," +
                "('X8X','https://www.cryptocompare.com/media/16746588/x8x.png')," +
                "('SOJ','https://www.cryptocompare.com/media/9350725/soj.png')," +
                "('CGA','https://www.cryptocompare.com/media/350988/cga.png')," +
                "('KICK','https://www.cryptocompare.com/media/1383929/kick.png')," +
                "('SMC','https://www.cryptocompare.com/media/20059/smc.png')," +
                "('CTC','https://www.cryptocompare.com/media/351496/ctc.png')," +
                "('KNC','https://www.cryptocompare.com/media/12318084/knc.png')," +
                "('TRDT','https://www.cryptocompare.com/media/27010784/trdt.png')," +
                "('CJ','https://www.cryptocompare.com/media/351234/cj.png')," +
                "('EDGE','https://www.cryptocompare.com/media/20556/edge.png')," +
                "('WSH','https://www.cryptocompare.com/media/12317980/wish.png')," +
                "('BTM*','https://www.cryptocompare.com/media/1383996/btm.png')," +
                "('TKN*','https://www.cryptocompare.com/media/351104/tkn.png')," +
                "('PRX','https://www.cryptocompare.com/media/1382603/prx.png')," +
                "('ITNS','https://www.cryptocompare.com/media/16746694/itns.png')," +
                "('RLX','https://www.cryptocompare.com/media/16746570/rlx.png')," +
                "('DCK','https://www.cryptocompare.com/media/351516/dck.png')," +
                "('KORE','https://www.cryptocompare.com/media/14543972/kore.png')," +
                "('BTMI','https://www.cryptocompare.com/media/20213/btmi.png')," +
                "('ACC*','https://www.cryptocompare.com/media/27010467/acc.png')," +
                "('OBITS','https://www.cryptocompare.com/media/350565/obits.png')," +
                "('BKX','https://www.cryptocompare.com/media/14913571/bkx.png')," +
                "('SWM','https://www.cryptocompare.com/media/27010630/swm_logo.png')," +
                "('GUN','https://www.cryptocompare.com/media/20780703/gun.png')," +
                "('KNC*','https://www.cryptocompare.com/media/350895/knc.png')," +
                "('DSB','https://www.cryptocompare.com/media/20034/dsb.png')," +
                "('CTX','https://www.cryptocompare.com/media/12318075/ctx1.png')," +
                "('PURE','https://www.cryptocompare.com/media/14913451/pure.png')," +
                "('BSC','https://www.cryptocompare.com/media/20601/bsc.png')," +
                "('GIM','https://www.cryptocompare.com/media/27010507/gim.png')," +
                "('CIF','https://www.cryptocompare.com/media/27010958/cif.png')," +
                "('POST','https://www.cryptocompare.com/media/350917/post.png')," +
                "('DVC','https://www.cryptocompare.com/media/20563/dvc.png')," +
                "('COIN*','https://www.cryptocompare.com/media/350950/coin.png')," +
                "('OCN','https://www.cryptocompare.com/media/27010448/ocn.png')," +
                "('TAK','https://www.cryptocompare.com/media/19928/tak.png')," +
                "('QTZ','https://www.cryptocompare.com/media/20643/qtz.png')," +
                "('HTML','https://www.cryptocompare.com/media/20780629/html.png')," +
                "('DBC','https://www.cryptocompare.com/media/20780608/dbc.png')," +
                "('DIGS','https://www.cryptocompare.com/media/20706/digs.png')," +
                "('TRI','https://www.cryptocompare.com/media/350568/tri.png')," +
                "('SMART*','https://www.cryptocompare.com/media/20780651/smart.png')," +
                "('MFG','https://www.cryptocompare.com/media/25792563/mfg.png')," +
                "('SWFTC','https://www.cryptocompare.com/media/27010472/swftc.png')," +
                "('EXIT','https://www.cryptocompare.com/media/351065/exit.png')," +
                "('WC','https://www.cryptocompare.com/media/19948/wc.png')," +
                "('TME','https://www.cryptocompare.com/media/1383905/tme.png')," +
                "('CS','https://www.cryptocompare.com/media/352292/cs.png')," +
                "('Z2','https://www.cryptocompare.com/media/9350780/z2.png')," +
                "('HAC','https://www.cryptocompare.com/media/12318176/hac.jpg')," +
                "('ADC','https://www.cryptocompare.com/media/350880/adc.png')," +
                "('AHT*','https://www.cryptocompare.com/media/1383796/aht.png')," +
                "('VIU','https://www.cryptocompare.com/media/14913680/viu.png')," +
                "('BDR','https://www.cryptocompare.com/media/14913485/bdr.png')," +
                "('SHADE','https://www.cryptocompare.com/media/20056/shade.png')," +
                "('LIZ','https://www.cryptocompare.com/media/27010957/liz.png')," +
                "('EBST','https://www.cryptocompare.com/media/14913431/ebst.png')," +
                "('SPRTS','https://www.cryptocompare.com/media/20692/sprts.png')," +
                "('WARP','https://www.cryptocompare.com/media/351395/warp.png')," +
                "('BTX*','https://www.cryptocompare.com/media/20215/btx.png')," +
                "('GAME','https://www.cryptocompare.com/media/350887/game.png')," +
                "('XCS','https://www.cryptocompare.com/media/12318323/xcs.png')," +
                "('WMC','https://www.cryptocompare.com/media/351044/wmc.png')," +
                "('ELC','https://www.cryptocompare.com/media/19694/elc.png')," +
                "('SIGU','https://www.cryptocompare.com/media/19808/mrs.png')," +
                "('CPAY','https://www.cryptocompare.com/media/12318303/cpay.png')," +
                "('LRC','https://www.cryptocompare.com/media/12318135/lrc.png')," +
                "('HPB','https://www.cryptocompare.com/media/20780783/hpb.png')," +
                "('TPG','https://www.cryptocompare.com/media/351948/tpg.png')," +
                "('NXS','https://www.cryptocompare.com/media/1382387/nexus.jpg')," +
                "('XMS','https://www.cryptocompare.com/media/19808/mrs.png')," +
                "('PRPS','https://www.cryptocompare.com/media/27010452/prps.png')," +
                "('CIX','https://www.cryptocompare.com/media/12318222/cnxasterisco.png')," +
                "('PXS','https://www.cryptocompare.com/media/27010505/pxs.png')," +
                "('SMNX','https://www.cryptocompare.com/media/1383998/sx.png')," +
                "('DAV','https://www.cryptocompare.com/media/20780624/dav.png')," +
                "('BOXY','https://www.cryptocompare.com/media/16746605/boxy.png')," +
                "('CSH','https://www.cryptocompare.com/media/351877/csh.png')," +
                "('XRE','https://www.cryptocompare.com/media/350975/xre.jpg')," +
                "('SPOTS','https://www.cryptocompare.com/media/350893/spots.png')," +
                "('URO','https://www.cryptocompare.com/media/19937/uro.png')," +
                "('TWLV','https://www.cryptocompare.com/media/20472/viral.png')," +
                "('ETBT','https://www.cryptocompare.com/media/16746535/etbt.png')," +
                "('XSEED','https://www.cryptocompare.com/media/20163/xseed.png')," +
                "('SNGLS','https://www.cryptocompare.com/media/351368/sngls.png')," +
                "('INSANE','https://www.cryptocompare.com/media/351993/insane.png')," +
                "('ETH','https://www.cryptocompare.com/media/20646/eth_logo.png')," +
                "('ISL','https://www.cryptocompare.com/media/19808/mrs.png')," +
                "('EON','https://www.cryptocompare.com/media/9350791/eon.jpg')," +
                "('ACTN','https://www.cryptocompare.com/media/30001655/actn.jpg')," +
                "('BCPT','https://www.cryptocompare.com/media/16746476/bcpt.png')," +
                "('CLOUT','https://www.cryptocompare.com/media/12318329/clout.png')," +
                "('CV','https://www.cryptocompare.com/media/27010446/cv.png')," +
                "('FOOD','https://www.cryptocompare.com/media/16746427/food.png')," +
                "('PLX','https://www.cryptocompare.com/media/16404895/plx.png')," +
                "('XTRA','https://www.cryptocompare.com/media/20780641/extra-logo-white.png')," +
                "('NSR','https://www.cryptocompare.com/media/20378/nsr.png')," +
                "('ETL','https://www.cryptocompare.com/media/25792566/etl.png')," +
                "('HION','https://www.cryptocompare.com/media/20780646/hion.png')," +
                "('NAMO','https://www.cryptocompare.com/media/12318384/namo.png')," +
                "('XDN','https://www.cryptocompare.com/media/19957/xdn.jpg')," +
                "('BTCZ','https://www.cryptocompare.com/media/12318408/btcz.png')," +
                "('MRP','https://www.cryptocompare.com/media/20357/mrp.png')," +
                "('COE','https://www.cryptocompare.com/media/1383911/coe.png')," +
                "('TROLL','https://www.cryptocompare.com/media/20780762/troll.png')," +
                "('PXL','https://www.cryptocompare.com/media/350930/pxl.png')," +
                "('XCP','https://www.cryptocompare.com/media/19960/xcp.png')," +
                "('ALQO','https://www.cryptocompare.com/media/16404849/alqo.png')," +
                "('GOON','https://www.cryptocompare.com/media/351512/goon.png')," +
                "('VZT','https://www.cryptocompare.com/media/12318414/vzt.png')," +
                "('RYC','https://www.cryptocompare.com/media/19898/ryc.png')," +
                "('SWING','https://www.cryptocompare.com/media/350987/swing.png')," +
                "('ERO','https://www.cryptocompare.com/media/16746561/ero.png')," +
                "('PROOF','https://www.cryptocompare.com/media/27010950/untitled-1.png')," +
                "('BAC*','https://www.cryptocompare.com/media/12318229/bac.png')," +
                "('BTSE','https://www.cryptocompare.com/media/20780666/bte.png')," +
                "('BSTY','https://www.cryptocompare.com/media/27010496/bsty.png')," +
                "('GHOUL','https://www.cryptocompare.com/media/20701/ghoul.png')," +
                "('FAME','https://www.cryptocompare.com/media/352006/fame.png')," +
                "('ARG','https://www.cryptocompare.com/media/19602/arg.png')," +
                "('BET','https://www.cryptocompare.com/media/19621/bet.png')," +
                "('OPTION','https://www.cryptocompare.com/media/1381998/option.png')," +
                "('TOM','https://www.cryptocompare.com/media/1383992/tom.png')," +
                "('BAY','https://www.cryptocompare.com/media/1383137/bay1.png')," +
                "('TESLA','https://www.cryptocompare.com/media/351945/tesla.png')," +
                "('SENSE','https://www.cryptocompare.com/media/12318034/sense.png')," +
                "('ETHB','https://www.cryptocompare.com/media/16746424/ethb.png')," +
                "('DNN','https://www.cryptocompare.com/media/20780744/dnn.png')," +
                "('GRX','https://www.cryptocompare.com/media/16746557/grx.png')," +
                "('TAB','https://www.cryptocompare.com/media/351488/tab.png')," +
                "('VIB','https://www.cryptocompare.com/media/1383893/vib.png')," +
                "('COAL','https://www.cryptocompare.com/media/16746586/coal.png')," +
                "('SOLE','https://www.cryptocompare.com/media/20431/sole.png')," +
                "('SIB','https://www.cryptocompare.com/media/350958/sib.png')," +
                "('EDO','https://www.cryptocompare.com/media/12318082/eiboo.png')," +
                "('EAGS','https://www.cryptocompare.com/media/19808/mrs.png')," +
                "('BLUE','https://www.cryptocompare.com/media/12318407/blue.png')," +
                "('RIPO','https://www.cryptocompare.com/media/20051/ripo.png')," +
                "('RUFF','https://www.cryptocompare.com/media/27010573/fqqzfp9_400x400.png')," +
                "('XSPEC','https://www.cryptocompare.com/media/1382395/xspec.png')," +
                "('SCL','https://www.cryptocompare.com/media/27010947/untitled-1.png')," +
                "('EFYT','https://www.cryptocompare.com/media/1383646/efyt.png')," +
                "('BTG','https://www.cryptocompare.com/media/27011062/btg.png')," +
                "('UCASH','https://www.cryptocompare.com/media/12317962/xuc.png')," +
                "('CAV','https://www.cryptocompare.com/media/12318328/cav.png')," +
                "('NEVA','https://www.cryptocompare.com/media/351026/neva.png')," +
                "('TAG','https://www.cryptocompare.com/media/19925/tag.png')," +
                "('ZNA','https://www.cryptocompare.com/media/12318142/zna.png')," +
                "('RDD','https://www.cryptocompare.com/media/19887/rdd.png')," +
                "('PNK','https://www.cryptocompare.com/media/351557/pnk.png')," +
                "('ROOTS','https://www.cryptocompare.com/media/1383851/roots.png')," +
                "('CMPCO','https://www.cryptocompare.com/media/1384036/cmpo.png')," +
                "('XRA','https://www.cryptocompare.com/media/351032/xra.png')," +
                "('EMC','https://www.cryptocompare.com/media/350611/emc.png')," +
                "('NEU','https://www.cryptocompare.com/media/14913483/neu.png')," +
                "('CMS','https://www.cryptocompare.com/media/14913540/comsa.png')," +
                "('SENT','https://www.cryptocompare.com/media/27010471/sent.png')," +
                "('HAT','https://www.cryptocompare.com/media/20780593/hat.png')," +
                "('SOON','https://www.cryptocompare.com/media/20436/soon.png')," +
                "('FTP','https://www.cryptocompare.com/media/351501/ftp.png')," +
                "('OPES','https://www.cryptocompare.com/media/1382434/opes.png')," +
                "('LCASH','https://www.cryptocompare.com/media/14913550/lcash.png')," +
                "('TRIP','https://www.cryptocompare.com/media/12318231/trip.png')," +
                "('BM','https://www.cryptocompare.com/media/351311/bm.png')," +
                "('DIM','https://www.cryptocompare.com/media/1383761/dim.png')," +
                "('ZCG','https://www.cryptocompare.com/media/16746536/zcg.png')," +
                "('SHREK','https://www.cryptocompare.com/media/351048/shrek.png')," +
                "('RBT','https://www.cryptocompare.com/media/20407/rbt.png')," +
                "('REQ','https://www.cryptocompare.com/media/12318260/req.png')," +
                "('XGOX','https://www.cryptocompare.com/media/14913685/xgox.png')," +
                "('R','https://www.cryptocompare.com/media/12318360/r.png')," +
                "('DTA','https://www.cryptocompare.com/media/25792680/dta.png')," +
                "('CWXT','https://www.cryptocompare.com/media/1382470/cwxt.png')," +
                "('MTX','https://www.cryptocompare.com/media/9350793/mtx.png')," +
                "('MDC','https://www.cryptocompare.com/media/351500/mdc.png')," +
                "('AUA','https://www.cryptocompare.com/media/20780743/aua.png')," +
                "('ETF','https://www.cryptocompare.com/media/16746737/etf.png')," +
                "('SIFT','https://www.cryptocompare.com/media/1384045/sift.jpg')," +
                "('GIFT','https://www.cryptocompare.com/media/1382171/gift.png')," +
                "('NULS','https://www.cryptocompare.com/media/14913548/nuls.png')," +
                "('MOD','https://www.cryptocompare.com/media/12318362/mod.png')," +
                "('ZEIT','https://www.cryptocompare.com/media/350984/zeit.png')," +
                "('1337','https://www.cryptocompare.com/media/350976/1337.png')," +
                "('CZC','https://www.cryptocompare.com/media/12318215/czc.png')," +
                "('ART','https://www.cryptocompare.com/media/12318097/art.png')," +
                "('XDB','https://www.cryptocompare.com/media/350977/xdb.png')," +
                "('ION','https://www.cryptocompare.com/media/350933/ion.jpg')," +
                "('HODL','https://www.cryptocompare.com/media/350840/hodl.png')," +
                "('MNTS','https://www.cryptocompare.com/media/27010946/mnts.png')," +
                "('SLS','https://www.cryptocompare.com/media/350946/sls.png')," +
                "('MUDRA','https://www.cryptocompare.com/media/351394/mudra.png')," +
                "('DB','https://www.cryptocompare.com/media/19808/mrs.png')," +
                "('STS','https://www.cryptocompare.com/media/351022/sts.png')," +
                "('XPOKE','https://www.cryptocompare.com/media/351393/xpoke.png')," +
                "('SRNT','https://www.cryptocompare.com/media/25792618/srnt.png')," +
                "('BOTS','https://www.cryptocompare.com/media/351499/bot.png')," +
                "('WISH*','https://www.cryptocompare.com/media/12317980/wish.png')," +
                "('PAC','https://www.cryptocompare.com/media/20780670/pac.png')," +
                "('DGPT','https://www.cryptocompare.com/media/1383920/dgt.png')," +
                "('DGM','https://www.cryptocompare.com/media/27010944/dgm.png')," +
                "('DFS','https://www.cryptocompare.com/media/20780715/dfs.png')," +
                "('BTDX','https://www.cryptocompare.com/media/14761939/btdx.png')," +
                "('BEE','https://www.cryptocompare.com/media/27010700/bee.png')," +
                "('CWV','https://www.cryptocompare.com/media/16746575/cwv.png')," +
                "('TCR','https://www.cryptocompare.com/media/350918/tcr.png')," +
                "('TRUMP','https://www.cryptocompare.com/media/350905/trump.png')," +
                "('ETP','https://www.cryptocompare.com/media/12318223/etp.png')," +
                "('eFIC','https://www.cryptocompare.com/media/30001623/efic.png')," +
                "('DBG','https://www.cryptocompare.com/media/351047/dbg.png')," +
                "('BTQ','https://www.cryptocompare.com/media/19638/btq.png')," +
                "('BELA','https://www.cryptocompare.com/media/351933/bela.jpg')," +
                "('BITZ','https://www.cryptocompare.com/media/20654/bitz.png')," +
                "('RFL','https://www.cryptocompare.com/media/16746549/rfl.png')," +
                "('NTWK','https://www.cryptocompare.com/media/20780649/ntwk.png')," +
                "('ZAP','https://www.cryptocompare.com/media/25792575/zap.png')," +
                "('ONG','https://www.cryptocompare.com/media/16746484/ong.png')," +
                "('ETK','https://www.cryptocompare.com/media/14913635/etk.png')," +
                "('GAT','https://www.cryptocompare.com/media/12318390/gat.png')," +
                "('MNY','https://www.cryptocompare.com/media/1383973/mny.png')," +
                "('MET','https://www.cryptocompare.com/media/1384013/met1.png')," +
                "('MZC','https://www.cryptocompare.com/media/19816/mzc.png')," +
                "('BTM','https://www.cryptocompare.com/media/20084/btm.png')," +
                "('THC','https://www.cryptocompare.com/media/351699/thc.png')," +
                "('BIO','https://www.cryptocompare.com/media/16746639/bio.png')," +
                "('KUSH','https://www.cryptocompare.com/media/1382401/kush.png')," +
                "('GLT','https://www.cryptocompare.com/media/16746578/glt.png')," +
                "('SOAR','https://www.cryptocompare.com/media/14913644/soar.png')," +
                "('LVG','https://www.cryptocompare.com/media/1382094/lvg.png')," +
                "('EXP','https://www.cryptocompare.com/media/20707/exp.png')," +
                "('GJC','https://www.cryptocompare.com/media/12318166/gjc.png')," +
                "('MCU','https://www.cryptocompare.com/media/27010445/mcu.png')," +
                "('DRPU','https://www.cryptocompare.com/media/27010850/drpu.png')," +
                "('DISK','https://www.cryptocompare.com/media/351023/disk.png')," +
                "('ARGUS','https://www.cryptocompare.com/media/1383149/argus.png')," +
                "('Q2C','https://www.cryptocompare.com/media/19872/q2c.jpg')," +
                "('BAS','https://www.cryptocompare.com/media/20780613/bas.png')," +
                "('XPRO','https://www.cryptocompare.com/media/1382098/xpro.png')," +
                "('ACT','https://www.cryptocompare.com/media/1383700/act.png')," +
                "('SHND','https://www.cryptocompare.com/media/20780741/shnd.png')," +
                "('CBD','https://www.cryptocompare.com/media/351990/cbd.png')," +
                "('TGT','https://www.cryptocompare.com/media/16746629/tgt.png')," +
                "('CO2','https://www.cryptocompare.com/media/30001624/co2.png')," +
                "('CTO','https://www.cryptocompare.com/media/19808/mrs.png')," +
                "('BIOS','https://www.cryptocompare.com/media/350894/bios.png')," +
                "('CRE','https://www.cryptocompare.com/media/20683/cre.png')," +
                "('PCOIN','https://www.cryptocompare.com/media/25792606/pcoin.png')," +
                "('KOLION','https://www.cryptocompare.com/media/12318295/kolion.png')," +
                "('PBC','https://www.cryptocompare.com/media/351062/pbc.png')," +
                "('UNB','https://www.cryptocompare.com/media/19940/unb.png')," +
                "('ASN','https://www.cryptocompare.com/media/20192/asn.png')," +
                "('EMD','https://www.cryptocompare.com/media/20278/emd.png')," +
                "('MTH','https://www.cryptocompare.com/media/1383976/mth.png')," +
                "('HELL','https://www.cryptocompare.com/media/30001610/hell.jpg')," +
                "('PWR','https://www.cryptocompare.com/media/350899/pwr.png')," +
                "('ARB','https://www.cryptocompare.com/media/20645/arb.png')," +
                "('PYLNT','https://www.cryptocompare.com/media/27010479/pylnt.png')," +
                "('SEN','https://www.cryptocompare.com/media/351560/sen.png')," +
                "('RKT','https://www.cryptocompare.com/media/30001617/rkt.jpg')," +
                "('WGC','https://www.cryptocompare.com/media/351310/wgc.png')," +
                "('POLIS','https://www.cryptocompare.com/media/20780731/polis.png')," +
                "('ITT','https://www.cryptocompare.com/media/1384050/itt.png')," +
                "('BCD*','https://www.cryptocompare.com/media/16746454/bcd.png')," +
                "('SGL','https://www.cryptocompare.com/media/25792671/sgl.png')," +
                "('INT','https://www.cryptocompare.com/media/25792603/int.png')," +
                "('XYO','https://www.cryptocompare.com/media/27011014/untitled-1.png')," +
                "('EGC','https://www.cryptocompare.com/media/350614/egc.png')," +
                "('HBZ','https://www.cryptocompare.com/media/27010671/hbz.png')," +
                "('LCT','https://www.cryptocompare.com/media/25792592/lct.png')," +
                "('DBTC','https://www.cryptocompare.com/media/350953/dbtc.png')," +
                "('AURA','https://www.cryptocompare.com/media/25792643/aura.png')," +
                "('BRX','https://www.cryptocompare.com/media/12317960/brx.png')," +
                "('BTF','https://www.cryptocompare.com/media/27010823/btf.jpg')," +
                "('BAN','https://www.cryptocompare.com/media/1382466/ban.png')," +
                "('JINN','https://www.cryptocompare.com/media/20780786/jinn.png')," +
                "('NTK','https://www.cryptocompare.com/media/16746560/ntk.png')," +
                "('BTZ','https://www.cryptocompare.com/media/1382433/btz.png')," +
                "('TFD','https://www.cryptocompare.com/media/27010902/tfood.jpg')," +
                "('GAKH','https://www.cryptocompare.com/media/1382090/gakh.png')," +
                "('ELT','https://www.cryptocompare.com/media/12318259/elt.png')," +
                "('RAIN','https://www.cryptocompare.com/media/1383114/rain.png')," +
                "('NETC','https://www.cryptocompare.com/media/350938/netc.png')," +
                "('GRT','https://www.cryptocompare.com/media/20310/grt.png')," +
                "('GMT','https://www.cryptocompare.com/media/14913642/gmt.png')," +
                "('MTR','https://www.cryptocompare.com/media/19710/frac.png')," +
                "('SUB','https://www.cryptocompare.com/media/1384011/sub1.png')," +
                "('KLK','https://www.cryptocompare.com/media/16746667/klk.png')," +
                "('RUP','https://www.cryptocompare.com/media/12318363/rup.jpg')," +
                "('EMGO','https://www.cryptocompare.com/media/16746703/emgo.png')," +
                "('NPX','https://www.cryptocompare.com/media/12318066/npx.png')," +
                "('AV','https://www.cryptocompare.com/media/1382048/av.png')," +
                "('ACP','https://www.cryptocompare.com/media/351019/acp.png')," +
                "('JC','https://www.cryptocompare.com/media/27010790/jc.png')," +
                "('SSS','https://www.cryptocompare.com/media/27010815/sss.png')," +
                "('TEL','https://www.cryptocompare.com/media/25792569/tel.png')," +
                "('ALC','https://www.cryptocompare.com/media/352022/alc.png')," +
                "('JEW','https://www.cryptocompare.com/media/27010795/untitled-1.png')," +
                "('LDM','https://www.cryptocompare.com/media/16404870/ldm.png')," +
                "('FDX','https://www.cryptocompare.com/media/27010834/fdx.jpg')," +
                "('611','https://www.cryptocompare.com/media/350985/611.png')," +
                "('MNT','https://www.cryptocompare.com/media/9350745/mntp.png')," +
                "('IPL','https://www.cryptocompare.com/media/27010470/ipl.png')," +
                "('NEBU','https://www.cryptocompare.com/media/350990/nebu.png')," +
                "('RHOC','https://www.cryptocompare.com/media/16746544/rhoc.png')," +
                "('MAR','https://www.cryptocompare.com/media/1382577/mar.png')," +
                "('CEDEX','https://www.cryptocompare.com/media/27011030/cedex.png')," +
                "('RUSTBITS','https://www.cryptocompare.com/media/12318085/rustbits.png')," +
                "('DOR','https://www.cryptocompare.com/media/27010676/dor.png')," +
                "('CPN','https://www.cryptocompare.com/media/20780661/cpn.png')," +
                "('PLU','https://www.cryptocompare.com/media/1382431/plu.png')," +
                "('XT','https://www.cryptocompare.com/media/351364/xt.png')," +
                "('CORE','https://www.cryptocompare.com/media/1383950/core.png')," +
                "('PROPS','https://www.cryptocompare.com/media/27011028/props.jpg')," +
                "('HXT','https://www.cryptocompare.com/media/12318282/hxt.png')," +
                "('QSP','https://www.cryptocompare.com/media/15887408/qsp.png')," +
                "('POP','https://www.cryptocompare.com/media/20780610/pop.png')," +
                "('ECC*','https://www.cryptocompare.com/media/1383546/ecc.png')," +
                "('EREAL','https://www.cryptocompare.com/media/20780660/ereal.png')," +
                "('QCN','https://www.cryptocompare.com/media/19877/qcn.png')," +
                "('FIL','https://www.cryptocompare.com/media/1383987/fil.png')," +
                "('FLS','https://www.cryptocompare.com/media/20780701/fuloos.png')," +
                "('OPC','https://www.cryptocompare.com/media/27010474/opc.png')," +
                "('HORSE','https://www.cryptocompare.com/media/27010465/horse.png')," +
                "('EVENT','https://www.cryptocompare.com/media/20277/event.png')," +
                "('PIRL','https://www.cryptocompare.com/media/14543968/pirl.png')," +
                "('COLX','https://www.cryptocompare.com/media/12318297/colx.png')," +
                "('NIHL','https://www.cryptocompare.com/media/27010943/nihl.png')," +
                "('BASHC','https://www.cryptocompare.com/media/27010941/bashc.png')," +
                "('RCN','https://www.cryptocompare.com/media/12318046/rnc.png')," +
                "('HIVE','https://www.cryptocompare.com/media/20780800/flat.png')," +
                "('CRED','https://www.cryptocompare.com/media/16746661/cred.png')," +
                "('THNX','https://www.cryptocompare.com/media/1384039/thnx.jpg')," +
                "('SDRN','https://www.cryptocompare.com/media/20780780/sdrn.png')," +
                "('JOY','https://www.cryptocompare.com/media/27010484/joy.png')," +
                "('UNC','https://www.cryptocompare.com/media/20693/unc.png')," +
                "('LCP','https://www.cryptocompare.com/media/20780774/lcp.png')," +
                "('WORM','https://www.cryptocompare.com/media/1384040/worm.png')," +
                "('MINT','https://www.cryptocompare.com/media/19797/mint.png')," +
                "('QBC','https://www.cryptocompare.com/media/19874/qbc.png')," +
                "('TOA','https://www.cryptocompare.com/media/12318334/toacoin.png')," +
                "('ENJ','https://www.cryptocompare.com/media/11417639/enjt.png')," +
                "('IOP','https://www.cryptocompare.com/media/12318262/iop.png')," +
                "('BRC','https://www.cryptocompare.com/media/20780798/brc.png')," +
                "('RADS','https://www.cryptocompare.com/media/350610/rads.png')," +
                "('USCoin','https://www.cryptocompare.com/media/30001666/untitled-1.png')," +
                "('MAID','https://www.cryptocompare.com/media/352247/maid.png')," +
                "('ACID','https://www.cryptocompare.com/media/1382237/acid.png')," +
                "('POLY','https://www.cryptocompare.com/media/20631/poly.png')," +
                "('PHO','https://www.cryptocompare.com/media/20780639/pho.png')," +
                "('redBUX','https://www.cryptocompare.com/media/27010952/redbux.png')," +
                "('SPC','https://www.cryptocompare.com/media/20655/spc.png')," +
                "('HGS','https://www.cryptocompare.com/media/27010583/hgs.png')," +
                "('WAN','https://www.cryptocompare.com/media/9350742/wan.jpg')," +
                "('WPR','https://www.cryptocompare.com/media/14543969/wpr.png')," +
                "('SNOV','https://www.cryptocompare.com/media/12318100/snov.png')," +
                "('SKR','https://www.cryptocompare.com/media/14913631/skr.png')," +
                "('TNC','https://www.cryptocompare.com/media/25792672/tnc.png');";

        db.execSQL(INSERT_COIN_MISC_DATA2);

        String INSERT_COIN_MISC_DATA3 = "INSERT INTO COIN_MISC(COIN_SYMBOL,COIN_IMAGE_URL) VALUES " +
                "('PUT', 'https://www.cryptocompare.com/media/1383668/put1.png'), " +
                "('PGL','https://www.cryptocompare.com/media/1384018/pgl.png')," +
                "('BRK','https://www.cryptocompare.com/media/350981/brk.png')," +
                "('MARYJ','https://www.cryptocompare.com/media/20343/maryj.png')," +
                "('XBP','https://www.cryptocompare.com/media/25792650/xbp.png')," +
                "('NYC','https://www.cryptocompare.com/media/30001653/nyc.png')," +
                "('BCR','https://www.cryptocompare.com/media/20198/bcr.png')," +
                "('KRAK','https://www.cryptocompare.com/media/351236/krak.png')," +
                "('XID','https://www.cryptocompare.com/media/1383898/xid.jpg')," +
                "('XAUR','https://www.cryptocompare.com/media/351382/xaur.png')," +
                "('CEL','https://www.cryptocompare.com/media/27011043/cel.png')," +
                "('BTCL','https://www.cryptocompare.com/media/16746647/btcl.png')," +
                "('BXT','https://www.cryptocompare.com/media/351509/bxt.png')," +
                "('MONA','https://www.cryptocompare.com/media/19801/mona.png')," +
                "('CRC','https://www.cryptocompare.com/media/20780611/crc.png')," +
                "('NIO','https://www.cryptocompare.com/media/16404894/nio.png')," +
                "('CXC','https://www.cryptocompare.com/media/20246/cxc.png')," +
                "('ZIL','https://www.cryptocompare.com/media/27010464/zil.png')," +
                "('SAGA','https://www.cryptocompare.com/media/20780745/saga.png')," +
                "('RNC','https://www.cryptocompare.com/media/1382240/rnc.png')," +
                "('XIN*','https://www.cryptocompare.com/media/14913486/xin.png')," +
                "('GFT','https://www.cryptocompare.com/media/14913686/gft.jpg')," +
                "('SPM','https://www.cryptocompare.com/media/351002/sup.png')," +
                "('BT','https://www.cryptocompare.com/media/9350775/bt.png')," +
                "('WIZ','https://www.cryptocompare.com/media/12318081/wiz.png')," +
                "('BIFI','https://www.cryptocompare.com/media/27010817/bifi.png')," +
                "('RBIES','https://www.cryptocompare.com/media/350904/rbies.png')," +
                "('SCOR','https://www.cryptocompare.com/media/9350695/scor.jpg')," +
                "('DAXX','https://www.cryptocompare.com/media/16746587/daxx.png')," +
                "('MEDI','https://www.cryptocompare.com/media/1384051/medi.png')," +
                "('EDR','https://www.cryptocompare.com/media/351430/edc.png')," +
                "('RHEA','https://www.cryptocompare.com/media/12318096/rhea.png')," +
                "('PUSHI','https://www.cryptocompare.com/media/27011046/pushi.png')," +
                "('CS*','https://www.cryptocompare.com/media/27011045/cs.png')," +
                "('BCOIN','https://www.cryptocompare.com/media/16746742/bcoin.png')," +
                "('PFR','https://www.cryptocompare.com/media/16404867/pfr.png')," +
                "('ICX','https://www.cryptocompare.com/media/12318192/icx.png')," +
                "('ECC','https://www.cryptocompare.com/media/25792579/ecc.png')," +
                "('LOC*','https://www.cryptocompare.com/media/16746675/loc.png')," +
                "('SNRG','https://www.cryptocompare.com/media/20700/snrg.png')," +
                "('SQP','https://www.cryptocompare.com/media/9350728/sqp.png')," +
                "('PHR*','https://www.cryptocompare.com/media/14913549/phr.png')," +
                "('HOLD','https://www.cryptocompare.com/media/14913647/hold.png')," +
                "('ICE','https://www.cryptocompare.com/media/1383896/46b-uaba_400x400.jpg')," +
                "('WTC','https://www.cryptocompare.com/media/12317959/wtc.png')," +
                "('NIMFA','https://www.cryptocompare.com/media/9350694/nimfa.jpg')," +
                "('IWT','https://www.cryptocompare.com/media/1384048/iwt.png')," +
                "('MAPC','https://www.cryptocompare.com/media/20710/mapc.png')," +
                "('MMNXT','https://www.cryptocompare.com/media/351209/nxtasset.png')," +
                "('HT','https://www.cryptocompare.com/media/27010813/ht.png')," +
                "('GET','https://www.cryptocompare.com/media/20780626/get.png')," +
                "('LBTC','https://www.cryptocompare.com/media/9350763/lbtc.png')," +
                "('GMC','https://www.cryptocompare.com/media/20299/gmc.png')," +
                "('FLVR','https://www.cryptocompare.com/media/351046/2flav.png')," +
                "('ACC','https://www.cryptocompare.com/media/9350776/acc.jpg')," +
                "('GPU','https://www.cryptocompare.com/media/350939/gpu.png')," +
                "('SMT**','https://www.cryptocompare.com/media/12318350/smt.png')," +
                "('DBIC','https://www.cryptocompare.com/media/350891/dbic.png')," +
                "('KAPU','https://www.cryptocompare.com/media/12318035/logo_500x500.png')," +
                "('REBL','https://www.cryptocompare.com/media/14913454/rebl.png')," +
                "('CREDO','https://www.cryptocompare.com/media/14913573/credo-1.png')," +
                "('ELA','https://www.cryptocompare.com/media/27010574/ela.png')," +
                "('PIZZA','https://www.cryptocompare.com/media/351397/pizza.png')," +
                "('LYB','https://www.cryptocompare.com/media/20339/lyb.png')," +
                "('ELLA','https://www.cryptocompare.com/media/14913603/ella.png')," +
                "('REC','https://www.cryptocompare.com/media/12318179/rec.png')," +
                "('LTS','https://www.cryptocompare.com/media/20644/lts.png')," +
                "('RDN','https://www.cryptocompare.com/media/19808/mrs.png')," +
                "('UTIL','https://www.cryptocompare.com/media/20067/util.png')," +
                "('LBC','https://www.cryptocompare.com/media/351211/lbc.png')," +
                "('XPH','https://www.cryptocompare.com/media/20641/xph.png')," +
                "('LIV','https://www.cryptocompare.com/media/352007/liv.png')," +
                "('TMT','https://www.cryptocompare.com/media/12317977/tmt.png')," +
                "('XCD','https://www.cryptocompare.com/media/27010488/xcd.png')," +
                "('CMCT','https://www.cryptocompare.com/media/27010977/cmct.png')," +
                "('LEV','https://www.cryptocompare.com/media/12318249/lev.png')," +
                "('KCS','https://www.cryptocompare.com/media/12318389/kcs.png')," +
                "('LIR','https://www.cryptocompare.com/media/351208/lir.png')," +
                "('FC','https://www.cryptocompare.com/media/12318045/fc.png')," +
                "('LTG','https://www.cryptocompare.com/media/16404871/ltg.png')," +
                "('MND','https://www.cryptocompare.com/media/350616/mnd.png')," +
                "('ICOO','https://www.cryptocompare.com/media/1383904/icoo.jpg')," +
                "('DTRC','https://www.cryptocompare.com/media/27010691/dtr.png')," +
                "('FLASH','https://www.cryptocompare.com/media/12318206/flash.png')," +
                "('LDC*','https://www.cryptocompare.com/media/27010513/ldc.png')," +
                "('TGC','https://www.cryptocompare.com/media/19930/tgc.png')," +
                "('M1','https://www.cryptocompare.com/media/19808/mrs.png')," +
                "('BMC','https://www.cryptocompare.com/media/12318008/bmc.png')," +
                "('TZC','https://www.cryptocompare.com/media/12318285/tzc.png')," +
                "('ITC','https://www.cryptocompare.com/media/20780628/itc.png')," +
                "('SEND','https://www.cryptocompare.com/media/16746577/send.png')," +
                "('CLIN','https://www.cryptocompare.com/media/27010911/clin.png')," +
                "('MUT','https://www.cryptocompare.com/media/16746537/mut.png')," +
                "('ZSC','https://www.cryptocompare.com/media/12318341/zsc.png')," +
                "('LIMX','https://www.cryptocompare.com/media/19769/limx.png')," +
                "('MNM','https://www.cryptocompare.com/media/350982/mnm.png')," +
                "('PMA','https://www.cryptocompare.com/media/20780758/pma.png')," +
                "('CREVA','https://www.cryptocompare.com/media/20571/creva.png')," +
                "('LEO','https://www.cryptocompare.com/media/351988/leo.png')," +
                "('PKT','https://www.cryptocompare.com/media/12318349/playkey.png')," +
                "('PUT*','https://www.cryptocompare.com/media/27010989/untitled-1.png')," +
                "('AXP','https://www.cryptocompare.com/media/27010491/axp.png')," +
                "('EPY','https://www.cryptocompare.com/media/20628/epy.png')," +
                "('OPP','https://www.cryptocompare.com/media/12318188/opp.png')," +
                "('XAI*','https://www.cryptocompare.com/media/1383840/xai.png')," +
                "('SALT','https://www.cryptocompare.com/media/9350744/salt.jpg')," +
                "('CL','https://www.cryptocompare.com/media/20780740/cl.png')," +
                "('BTH','https://www.cryptocompare.com/media/20780656/bth.png')," +
                "('HEDG','https://www.cryptocompare.com/media/20663/hedg.png')," +
                "('EDT','https://www.cryptocompare.com/media/25792615/edt.png')," +
                "('GSX','https://www.cryptocompare.com/media/20314/gsxjpeg.png')," +
                "('SSTC','https://www.cryptocompare.com/media/350937/ssc.png')," +
                "('HLC','https://www.cryptocompare.com/media/27010592/hlc_logo.png')," +
                "('MOIN','https://www.cryptocompare.com/media/350959/moin.png')," +
                "('CIN','https://www.cryptocompare.com/media/20698/cinder.png')," +
                "('GCC*','https://www.cryptocompare.com/media/20780778/gcc.png')," +
                "('SISA','https://www.cryptocompare.com/media/27010587/sisa.png')," +
                "('LUX**','https://www.cryptocompare.com/media/16746739/lux.png')," +
                "('BNC','https://www.cryptocompare.com/media/1383991/bnc.png')," +
                "('ZNY','https://www.cryptocompare.com/media/20691/zny.png')," +
                "('WABI','https://www.cryptocompare.com/media/12318331/wabi.png')," +
                "('XAS','https://www.cryptocompare.com/media/1383997/xas.png')," +
                "('DEEP','https://www.cryptocompare.com/media/12317976/deep.png')," +
                "('OCTO','https://www.cryptocompare.com/media/19983/888.png')," +
                "('MIL','https://www.cryptocompare.com/media/20354/mil.png')," +
                "('RIYA','https://www.cryptocompare.com/media/9350737/riya.png')," +
                "('ADZ','https://www.cryptocompare.com/media/351424/adz1.jpg')," +
                "('JNT','https://www.cryptocompare.com/media/15887422/jnt.jpg')," +
                "('JWL','https://www.cryptocompare.com/media/351432/jwl.png')," +
                "('CON','https://www.cryptocompare.com/media/20717/con_.png')," +
                "('CHSB','https://www.cryptocompare.com/media/27010612/chsb_logo.png')," +
                "('PHILS','https://www.cryptocompare.com/media/20780702/phils.png')," +
                "('BIS','https://www.cryptocompare.com/media/12318191/bis.png')," +
                "('XFT','https://www.cryptocompare.com/media/20780662/xft.png')," +
                "('ADB','https://www.cryptocompare.com/media/16746619/adb.png')," +
                "('ODN','https://www.cryptocompare.com/media/12318145/odn.png')," +
                "('OMGC','https://www.cryptocompare.com/media/20780667/omgc.png')," +
                "('PRG','https://www.cryptocompare.com/media/1384033/prg.png')," +
                "('XEC','https://www.cryptocompare.com/media/1383961/xec.png')," +
                "('SAF','https://www.cryptocompare.com/media/27010473/sfu.png')," +
                "('B2B','https://www.cryptocompare.com/media/12318185/b2bx.png')," +
                "('MTK','https://www.cryptocompare.com/media/12318149/mtk.png')," +
                "('GOLOS','https://www.cryptocompare.com/media/1382246/golos.png')," +
                "('RPC','https://www.cryptocompare.com/media/19895/rpc-2.png')," +
                "('GRO','https://www.cryptocompare.com/media/27010628/gro.png')," +
                "('ORE','https://www.cryptocompare.com/media/20780695/ore.png')," +
                "('ZER','https://www.cryptocompare.com/media/27011053/zer.png')," +
                "('EKO','https://www.cryptocompare.com/media/25792625/eko.png')," +
                "('DRA','https://www.cryptocompare.com/media/1382095/dra.png')," +
                "('CAPP','https://www.cryptocompare.com/media/15887416/capp.png')," +
                "('XMRG','https://www.cryptocompare.com/media/16746651/xmrg.png')," +
                "('HIRE*','https://www.cryptocompare.com/media/350613/hire.png')," +
                "('CND','https://www.cryptocompare.com/media/12318283/cnd.png')," +
                "('PULSE','https://www.cryptocompare.com/media/350994/pulse.jpg')," +
                "('DFBT','https://www.cryptocompare.com/media/1383890/dfbt.png')," +
                "('CYT','https://www.cryptocompare.com/media/351024/cyt.png')," +
                "('WAVES','https://www.cryptocompare.com/media/27010639/waves2.png')," +
                "('FUEL','https://www.cryptocompare.com/media/11999072/fuel.png')," +
                "('ECHT','https://www.cryptocompare.com/media/12318352/echt.png')," +
                "('SPANK','https://www.cryptocompare.com/media/16404890/spank.png')," +
                "('SXDT','https://www.cryptocompare.com/media/27010511/sxdt.png')," +
                "('ZPT','https://www.cryptocompare.com/media/27010506/zpt.png')," +
                "('MANA','https://www.cryptocompare.com/media/1383903/mana.png')," +
                "('XNA','https://www.cryptocompare.com/media/19808/mrs.png')," +
                "('PAK','https://www.cryptocompare.com/media/350788/pak.png')," +
                "('RVN','https://www.cryptocompare.com/media/27011010/rvn.jpg')," +
                "('CBS','https://www.cryptocompare.com/media/27010945/cbs.png')," +
                "('BNB','https://www.cryptocompare.com/media/1383947/bnb.png')," +
                "('STHR','https://www.cryptocompare.com/media/351042/sthr.png')," +
                "('QVT','https://www.cryptocompare.com/media/1383954/qvt.png')," +
                "('XID*','https://www.cryptocompare.com/media/350916/xid.png')," +
                "('CRE*','https://www.cryptocompare.com/media/27010928/creditcoin_logo.png')," +
                "('OK','https://www.cryptocompare.com/media/350819/ok.png')," +
                "('XDCE','https://www.cryptocompare.com/media/16746634/xdc.png')," +
                "('XQN','https://www.cryptocompare.com/media/12318067/xqn.png')," +
                "('LNK','https://www.cryptocompare.com/media/9350738/lnk.png')," +
                "('INK','https://www.cryptocompare.com/media/20780781/ink.png')," +
                "('3DES','https://www.cryptocompare.com/media/12318014/3des.png')," +
                "('GXS','https://www.cryptocompare.com/media/20780746/gxs.png')," +
                "('STAK','https://www.cryptocompare.com/media/16746740/stak.png')," +
                "('FIST','https://www.cryptocompare.com/media/352056/fist.png')," +
                "('SPX','https://www.cryptocompare.com/media/351212/spx.png')," +
                "('BBT*','https://www.cryptocompare.com/media/1383883/bbt.png')," +
                "('MRS','https://www.cryptocompare.com/media/19808/mrs.png')," +
                "('WBTC','https://www.cryptocompare.com/media/27010633/wbtc.png')," +
                "('SYS','https://www.cryptocompare.com/media/25792583/sys.png')," +
                "('ASAFE2','https://www.cryptocompare.com/media/1382096/allsafe.jpg')," +
                "('CPC*','https://www.cryptocompare.com/media/27010501/cpc.png')," +
                "('CLUD','https://www.cryptocompare.com/media/351027/clud.png')," +
                "('RDN*','https://www.cryptocompare.com/media/14913482/rdn.png')," +
                "('WSC','https://www.cryptocompare.com/media/14913560/wsc.png')," +
                "('XBL','https://www.cryptocompare.com/media/14913541/xbl.png')," +
                "('XBTY','https://www.cryptocompare.com/media/27010787/xbty.jpg')," +
                "('CLV','https://www.cryptocompare.com/media/20727/clv.png')," +
                "('APPC','https://www.cryptocompare.com/media/12318370/app.png')," +
                "('HMT','https://www.cryptocompare.com/media/30001660/hmt.jpg')," +
                "('XC','https://www.cryptocompare.com/media/19956/xc.png')," +
                "('MOJO','https://www.cryptocompare.com/media/351003/mojo.png')," +
                "('AR*','https://www.cryptocompare.com/media/14913465/ar.png')," +
                "('TELL','https://www.cryptocompare.com/media/351505/tell.png')," +
                "('PST','https://www.cryptocompare.com/media/9350792/pst.jpg')," +
                "('GIZ','https://www.cryptocompare.com/media/351015/giz.png')," +
                "('BOLI','https://www.cryptocompare.com/media/351008/boli.png')," +
                "('ICASH','https://www.cryptocompare.com/media/20319/icash.png')," +
                "('BINS','https://www.cryptocompare.com/media/27011047/bins.jpg')," +
                "('GAP','https://www.cryptocompare.com/media/350943/gap.png')," +
                "('BYC','https://www.cryptocompare.com/media/20217/byc.png')," +
                "('MAG*','https://www.cryptocompare.com/media/9350711/mag.png')," +
                "('WINGS','https://www.cryptocompare.com/media/1382758/1wings.png')," +
                "('GLOBE','https://www.cryptocompare.com/media/20564/globe.png')," +
                "('TNB','https://www.cryptocompare.com/media/16746672/tnb.png')," +
                "('ATL','https://www.cryptocompare.com/media/9350703/atlant.png')," +
                "('KR','https://www.cryptocompare.com/media/350974/kr.png')," +
                "('ROK','https://www.cryptocompare.com/media/12318268/rok.png')," +
                "('INFX','https://www.cryptocompare.com/media/350919/infx.png')," +
                "('BLZ','https://www.cryptocompare.com/media/27010607/1.png')," +
                "('LTC','https://www.cryptocompare.com/media/19782/litecoin-logo.png')," +
                "('XMR','https://www.cryptocompare.com/media/19969/xmr.png')," +
                "('NXT','https://www.cryptocompare.com/media/20627/nxt.png')," +
                "('ETC','https://www.cryptocompare.com/media/20275/etc2.png')," +
                "('DOGE','https://www.cryptocompare.com/media/19684/doge.png')," +
                "('ZEC','https://www.cryptocompare.com/media/351360/zec.png')," +
                "('BTS','https://www.cryptocompare.com/media/20705/bts.png')," +
                "('DGB','https://www.cryptocompare.com/media/12318264/7638-nty_400x400.jpg')," +
                "('BTCD','https://www.cryptocompare.com/media/19630/btcd_1.png')," +
                "('CRAIG','https://www.cryptocompare.com/media/20022/craig.png')," +
                "('XPY','https://www.cryptocompare.com/media/20076/xpy_1.png')," +
                "('PRC','https://www.cryptocompare.com/media/20393/prc.png')," +
                "('YBC','https://www.cryptocompare.com/media/19975/ybc.png')," +
                "('DANK','https://www.cryptocompare.com/media/20247/dank.png')," +
                "('GIVE','https://www.cryptocompare.com/media/20297/give.png')," +
                "('KOBO','https://www.cryptocompare.com/media/20329/kobo.png')," +
                "('DT','https://www.cryptocompare.com/media/20031/dt.png')," +
                "('CETI','https://www.cryptocompare.com/media/20228/ceti.png')," +
                "('SUP','https://www.cryptocompare.com/media/20442/sup.png')," +
                "('XPD','https://www.cryptocompare.com/media/20162/xpd.png')," +
                "('GEO','https://www.cryptocompare.com/media/20292/geo.png')," +
                "('CHASH','https://www.cryptocompare.com/media/20231/chash.png')," +
                "('SPR','https://www.cryptocompare.com/media/20438/spr.png')," +
                "('NXTI','https://www.cryptocompare.com/media/20376/nxti.png')," +
                "('WOLF','https://www.cryptocompare.com/media/20559/wolf.png')," +
                "('XDP','https://www.cryptocompare.com/media/20560/xdp.png')," +
                "('2015','https://www.cryptocompare.com/media/20180/2015.png')," +
                "('42','https://www.cryptocompare.com/media/12318415/42.png')," +
                "('AC','https://www.cryptocompare.com/media/19593/ac.png')," +
                "('ALF','https://www.cryptocompare.com/media/19600/alf.png')," +
                "('AGS','https://www.cryptocompare.com/media/19595/ags.png')," +
                "('AMC','https://www.cryptocompare.com/media/19601/amc.png')," +
                "('ALN','https://www.cryptocompare.com/media/20080/aln.png')," +
                "('ARI','https://www.cryptocompare.com/media/20082/ari.png')," +
                "('AUR','https://www.cryptocompare.com/media/19608/aur.png')," +
                "('AXR','https://www.cryptocompare.com/media/20086/axr.png')," +
                "('BCX','https://www.cryptocompare.com/media/19620/bcx.png')," +
                "('BEN','https://www.cryptocompare.com/media/19617/ben.png')," +
                "('BITB','https://www.cryptocompare.com/media/350879/bitb.png')," +
                "('BLU','https://www.cryptocompare.com/media/19624/blu.png')," +
                "('BLK','https://www.cryptocompare.com/media/351795/blk.png')," +
                "('BOST','https://www.cryptocompare.com/media/19626/bost.png')," +
                "('BQC','https://www.cryptocompare.com/media/19631/bqc.png')," +
                "('XMY','https://www.cryptocompare.com/media/19815/myr.png')," +
                "('MOON','https://www.cryptocompare.com/media/19802/moon.png')," +
                "('ZET','https://www.cryptocompare.com/media/19993/zet.png')," +
                "('ENRG','https://www.cryptocompare.com/media/19697/enrg.png')," +
                "('QRK','https://www.cryptocompare.com/media/19882/qrk.png')," +
                "('RIC','https://www.cryptocompare.com/media/19888/ric.jpg')," +
                "('DGC','https://www.cryptocompare.com/media/19676/dgc.png')," +
                "('CAIX','https://www.cryptocompare.com/media/20226/caix.png')," +
                "('BTG*','https://www.cryptocompare.com/media/19634/btg.png')," +
                "('BUK','https://www.cryptocompare.com/media/19637/buk.png')," +
                "('CACH','https://www.cryptocompare.com/media/19642/cach.png')," +
                "('CANN','https://www.cryptocompare.com/media/20015/cann.png')," +
                "('CAP','https://www.cryptocompare.com/media/20017/cap.png')," +
                "('CASH','https://www.cryptocompare.com/media/20016/cash.png')," +
                "('CBX','https://www.cryptocompare.com/media/20697/cbx.png')," +
                "('CCN','https://www.cryptocompare.com/media/19643/ccn.png')," +
                "('CINNI','https://www.cryptocompare.com/media/19651/cinni.jpeg')," +
                "('CLAM','https://www.cryptocompare.com/media/20020/clam.png')," +
                "('CLOAK','https://www.cryptocompare.com/media/19994/cloak.png')," +
                "('CLR','https://www.cryptocompare.com/media/19657/clr.png')," +
                "('CMC','https://www.cryptocompare.com/media/20019/cmc.png')," +
                "('CNC','https://www.cryptocompare.com/media/20021/cnc.png')," +
                "('CNL','https://www.cryptocompare.com/media/20024/cnl.png')," +
                "('COMM','https://www.cryptocompare.com/media/19661/comm.png')," +
                "('COOL','https://www.cryptocompare.com/media/19658/cool.png')," +
                "('CRACK','https://www.cryptocompare.com/media/20023/crack.png')," +
                "('CRC*','https://www.cryptocompare.com/media/19665/crc.png')," +
                "('CRYPT','https://www.cryptocompare.com/media/19664/crypt.png')," +
                "('DEM','https://www.cryptocompare.com/media/20028/dem.png')," +
                "('EFL','https://www.cryptocompare.com/media/19692/efl.png')," +
                "('EMC2','https://www.cryptocompare.com/media/20033/emc2.png')," +
                "('EXE','https://www.cryptocompare.com/media/19700/exe.png')," +
                "('EZC','https://www.cryptocompare.com/media/19702/ezc.png')," +
                "('FC2','https://www.cryptocompare.com/media/19719/fuel.png')," +
                "('FFC','https://www.cryptocompare.com/media/19706/ffc.png')," +
                "('FIBRE','https://www.cryptocompare.com/media/20030/fibre.png')," +
                "('FRC','https://www.cryptocompare.com/media/1382629/frc.png')," +
                "('FLT','https://www.cryptocompare.com/media/19709/flt.png')," +
                "('FRAC','https://www.cryptocompare.com/media/19710/frac.png')," +
                "('FTC','https://www.cryptocompare.com/media/19718/ftc.png')," +
                "('GDC','https://www.cryptocompare.com/media/20054/gdc.png')," +
                "('GLC','https://www.cryptocompare.com/media/19724/glc.png')," +
                "('GLD','https://www.cryptocompare.com/media/19723/gld.png')," +
                "('GLX','https://www.cryptocompare.com/media/19728/glx.png')," +
                "('GML','https://www.cryptocompare.com/media/19726/gml.png')," +
                "('GUE','https://www.cryptocompare.com/media/19732/gue.png')," +
                "('HAL','https://www.cryptocompare.com/media/20036/hal.png')," +
                "('HBN','https://www.cryptocompare.com/media/19735/hbn.png')," +
                "('HVC','https://www.cryptocompare.com/media/19745/hvc.png')," +
                "('HYP','https://www.cryptocompare.com/media/20624/hyp.png')," +
                "('ICB','https://www.cryptocompare.com/media/19747/icb.png')," +
                "('IOC','https://www.cryptocompare.com/media/20042/ioc.png')," +
                "('IXC','https://www.cryptocompare.com/media/19761/ixc.png')," +
                "('JKC','https://www.cryptocompare.com/media/19757/jkc.png')," +
                "('KDC','https://www.cryptocompare.com/media/19766/kdc.png')," +
                "('LAB*','https://www.cryptocompare.com/media/20040/lab.png')," +
                "('LGD*','https://www.cryptocompare.com/media/19770/lgd.png')," +
                "('LK7','https://www.cryptocompare.com/media/19776/lk7.png')," +
                "('LSD','https://www.cryptocompare.com/media/20041/lsd.png')," +
                "('LTB','https://www.cryptocompare.com/media/20336/ltb.png')," +
                "('LTCX','https://www.cryptocompare.com/media/19779/ltcx.png')," +
                "('LYC','https://www.cryptocompare.com/media/19785/lyc.png')," +
                "('MAX','https://www.cryptocompare.com/media/19786/max.png')," +
                "('MEC','https://www.cryptocompare.com/media/19789/mec.png')," +
                "('MN','https://www.cryptocompare.com/media/19796/mn1.png')," +
                "('MNC','https://www.cryptocompare.com/media/19805/mincoin.png')," +
                "('MRY','https://www.cryptocompare.com/media/19807/mry.jpg')," +
                "('MYST*','https://www.cryptocompare.com/media/20053/myst.png')," +
                "('NAN','https://www.cryptocompare.com/media/19821/nan.png')," +
                "('NAUT','https://www.cryptocompare.com/media/19822/naut.png')," +
                "('NAV','https://www.cryptocompare.com/media/351431/nav.png')," +
                "('NBL','https://www.cryptocompare.com/media/19825/nbl.png')," +
                "('NEC','https://www.cryptocompare.com/media/19824/nec.png')," +
                "('NMB','https://www.cryptocompare.com/media/20049/nmb.png')," +
                "('NOBL','https://www.cryptocompare.com/media/19833/nobl.png')," +
                "('NRS','https://www.cryptocompare.com/media/19834/nrs.png')," +
                "('NVC','https://www.cryptocompare.com/media/20713/nvc.png')," +
                "('NMC','https://www.cryptocompare.com/media/19830/nmc.png')," +
                "('NYAN','https://www.cryptocompare.com/media/19842/nyan.png')," +
                "('OPAL','https://www.cryptocompare.com/media/20050/opal.png')," +
                "('ORB','https://www.cryptocompare.com/media/19845/orb.png')," +
                "('OSC','https://www.cryptocompare.com/media/19847/osc.png')," +
                "('PHS','https://www.cryptocompare.com/media/19857/phs.png')," +
                "('POINTS','https://www.cryptocompare.com/media/19863/points.png')," +
                "('POT','https://www.cryptocompare.com/media/19865/pot.png')," +
                "('PSEUD','https://www.cryptocompare.com/media/20052/pseud.png')," +
                "('PTS*','https://www.cryptocompare.com/media/19869/pts.png')," +
                "('PXC','https://www.cryptocompare.com/media/20058/pxc.png')," +
                "('PYC','https://www.cryptocompare.com/media/19878/pyc.png')," +
                "('XCR','https://www.cryptocompare.com/media/19710/frac.png')," +
                "('XJO','https://www.cryptocompare.com/media/19962/xjo.png')," +
                "('XLB','https://www.cryptocompare.com/media/19966/xlb.png')," +
                "('XPM','https://www.cryptocompare.com/media/19970/xpm.png')," +
                "('XST','https://www.cryptocompare.com/media/20077/xst.png')," +
                "('YAC','https://www.cryptocompare.com/media/19976/yac.png')," +
                "('ZCC','https://www.cryptocompare.com/media/19979/zcc.png')," +
                "('ZED','https://www.cryptocompare.com/media/19981/zed.png')," +
                "('ZRC*','https://www.cryptocompare.com/media/20078/zrc.png')," +
                "('BCN','https://www.cryptocompare.com/media/12318404/bcn.png')," +
                "('EKN','https://www.cryptocompare.com/media/20270/ekn.png')," +
                "('XAU','https://www.cryptocompare.com/media/20479/xau.png')," +
                "('TMC','https://www.cryptocompare.com/media/20451/tmc.png')," +
                "('XEM','https://www.cryptocompare.com/media/20490/xem.png')," +
                "('SJCX','https://www.cryptocompare.com/media/20422/sjcx.png')," +
                "('HUGE','https://www.cryptocompare.com/media/20318/huge.png')," +
                "('QORA','https://www.cryptocompare.com/media/19876/qora.png')," +
                "('RBY','https://www.cryptocompare.com/media/351279/rby.png')," +
                "('PTC','https://www.cryptocompare.com/media/19868/ptc.png')," +
                "('SSD','https://www.cryptocompare.com/media/20443/ssd.png')," +
                "('XTC','https://www.cryptocompare.com/media/20167/xtc.png')," +
                "('NOTE','https://www.cryptocompare.com/media/19832/note.png')," +
                "('FLO','https://www.cryptocompare.com/media/1383331/flo1.png')," +
                "('8BIT','https://www.cryptocompare.com/media/20176/8bit.png')," +
                "('STV','https://www.cryptocompare.com/media/20444/stv.png')," +
                "('EBS','https://www.cryptocompare.com/media/20267/ebs.png')," +
                "('XMG','https://www.cryptocompare.com/media/20154/xmg.png')," +
                "('AMBER','https://www.cryptocompare.com/media/20187/amber.png')," +
                "('JPC','https://www.cryptocompare.com/media/19759/jpc.png')," +
                "('NKT','https://www.cryptocompare.com/media/20371/nkt.png')," +
                "('J','https://www.cryptocompare.com/media/20350/j.png')," +
                "('GHC','https://www.cryptocompare.com/media/19721/ghc.png')," +
                "('DTC*','https://www.cryptocompare.com/media/19688/dtc.png')," +
                "('ABY','https://www.cryptocompare.com/media/1383379/aby.png')," +
                "('LDOGE','https://www.cryptocompare.com/media/20332/ldoge.png')," +
                "('SWARM','https://www.cryptocompare.com/media/20445/swarm.png')," +
                "('BBR','https://www.cryptocompare.com/media/19609/bbr.png')," +
                "('BTCRY','https://www.cryptocompare.com/media/20210/btcry.png')," +
                "('XPB','https://www.cryptocompare.com/media/20158/xpb.png')," +
                "('XDQ','https://www.cryptocompare.com/media/19959/xdq.png')," +
                "('FLDC','https://www.cryptocompare.com/media/20284/fldc.png')," +
                "('SLR','https://www.cryptocompare.com/media/20699/slr.png')," +
                "('SMAC','https://www.cryptocompare.com/media/20427/smac.png')," +
                "('TRK','https://www.cryptocompare.com/media/20460/trk.png')," +
                "('U','https://www.cryptocompare.com/media/351629/u.jpg')," +
                "('UIS','https://www.cryptocompare.com/media/20455/uis.png')," +
                "('CYP','https://www.cryptocompare.com/media/20248/cyp.png')," +
                "('UFO','https://www.cryptocompare.com/media/12318167/ufo1.png')," +
                "('OC','https://www.cryptocompare.com/media/19843/oc.png')," +
                "('GSM','https://www.cryptocompare.com/media/20316/gsm.png');";
        db.execSQL(INSERT_COIN_MISC_DATA3);

        String INSERT_COIN_MISC_DATA4 = "INSERT INTO COIN_MISC(COIN_SYMBOL,COIN_IMAGE_URL) VALUES " +
                " ('PSY','https://www.cryptocompare.com/media/351362/psy.png')," +
                "('FSN*','https://www.cryptocompare.com/media/27010765/fsn.png')," +
                "('MST','https://www.cryptocompare.com/media/351529/mst1.png')," +
                "('BTO','https://www.cryptocompare.com/media/25792636/bot.png')," +
                "('OMC','https://www.cryptocompare.com/media/1381967/omc.png')," +
                "('NKC','https://www.cryptocompare.com/media/351041/nkc.png')," +
                "('DOT','https://www.cryptocompare.com/media/350909/dot.png')," +
                "('FRK','https://www.cryptocompare.com/media/19712/frk.png')," +
                "('CYG','https://www.cryptocompare.com/media/350997/cygnus.png')," +
                "('UNITS','https://www.cryptocompare.com/media/1382050/units.png')," +
                "('AUT','https://www.cryptocompare.com/media/1383956/aut.png')," +
                "('BTW','https://www.cryptocompare.com/media/27010490/btw.png')," +
                "('ENTER','https://www.cryptocompare.com/media/351305/enter.png')," +
                "('STRAT','https://www.cryptocompare.com/media/351303/stratis-logo.png')," +
                "('TAU','https://www.cryptocompare.com/media/16404857/lamden.png')," +
                "('KEX','https://www.cryptocompare.com/media/9350699/kex.png')," +
                "('GENE','https://www.cryptocompare.com/media/20780630/gene.png')," +
                "('SC','https://www.cryptocompare.com/media/20726/siacon-logo.png')," +
                "('EMT','https://www.cryptocompare.com/media/9350765/emt.png')," +
                "('DTT','https://www.cryptocompare.com/media/12318109/dtt.png')," +
                "('MNT*','https://www.cryptocompare.com/media/16746688/mnt.png')," +
                "('PIX','https://www.cryptocompare.com/media/1384024/pix.png')," +
                "('AIDOC','https://www.cryptocompare.com/media/25792577/aidoc.png')," +
                "('IVZ','https://www.cryptocompare.com/media/350944/ivz.png')," +
                "('300','https://www.cryptocompare.com/media/27010595/300.png')," +
                "('POWR','https://www.cryptocompare.com/media/12318301/powr.png')," +
                "('SOUL','https://www.cryptocompare.com/media/350930/pxl.png')," +
                "('ATM*','https://www.cryptocompare.com/media/351492/atm.png')," +
                "('VIOR','https://www.cryptocompare.com/media/20469/viorjpeg.png')," +
                "('MEOW','https://www.cryptocompare.com/media/16746539/meow.png')," +
                "('ERC20','https://www.cryptocompare.com/media/20780627/erc20.png')," +
                "('BNK','https://www.cryptocompare.com/media/14913602/bnk.png')," +
                "('CIRC','https://www.cryptocompare.com/media/20603/circ.png')," +
                "('BLX','https://www.cryptocompare.com/media/9350748/blx.png')," +
                "('TUR','https://www.cryptocompare.com/media/351020/tur.png')," +
                "('ZLQ','https://www.cryptocompare.com/media/1382238/zlq.png')," +
                "('STK','https://www.cryptocompare.com/media/27010524/stk.png')," +
                "('BM*','https://www.cryptocompare.com/media/12318361/bm.png')," +
                "('NEO','https://www.cryptocompare.com/media/1383858/neo.jpg')," +
                "('VRC','https://www.cryptocompare.com/media/20068/vrc.png')," +
                "('8S','https://www.cryptocompare.com/media/12318091/8s.png')," +
                "('GEN','https://www.cryptocompare.com/media/20640/gen.png')," +
                "('GTC','https://www.cryptocompare.com/media/20780776/gtc.png')," +
                "('FUCK','https://www.cryptocompare.com/media/1384043/fuck.png')," +
                "('HKN','https://www.cryptocompare.com/media/16746674/hkn.png')," +
                "('CAT1','https://www.cryptocompare.com/media/19644/cat.png')," +
                "('ARI*','https://www.cryptocompare.com/media/9350704/ari.png')," +
                "('LOCI','https://www.cryptocompare.com/media/16404856/loci.png')," +
                "('NRC','https://www.cryptocompare.com/media/1382604/nrc.png')," +
                "('QASH','https://www.cryptocompare.com/media/15887431/qash.png')," +
                "('STP','https://www.cryptocompare.com/media/16404874/stp.png')," +
                "('MAY','https://www.cryptocompare.com/media/12318380/may.png')," +
                "('PLC','https://www.cryptocompare.com/media/14913555/plus.png')," +
                "('FLP','https://www.cryptocompare.com/media/12318280/flip.png')," +
                "('TTT','https://www.cryptocompare.com/media/9350755/ttt.jpg')," +
                "('BLOCK','https://www.cryptocompare.com/media/20204/block.png')," +
                "('FLLW','https://www.cryptocompare.com/media/12318412/fllw.png')," +
                "('SFU','https://www.cryptocompare.com/media/25792604/sfu.png')," +
                "('INVOX','https://www.cryptocompare.com/media/30001639/untitled-1.png')," +
                "('SPEC','https://www.cryptocompare.com/media/20689/spec.png')," +
                "('CURE','https://www.cryptocompare.com/media/1383812/cure.png')," +
                "('OLDSF','https://www.cryptocompare.com/media/350936/oldsf.png')," +
                "('ELP','https://www.cryptocompare.com/media/30001611/elp.png')," +
                "('ALX','https://www.cryptocompare.com/media/27010915/alx.jpg')," +
                "('XLR','https://www.cryptocompare.com/media/1382994/xlr.png')," +
                "('I0C','https://www.cryptocompare.com/media/350691/i0c.png')," +
                "('DDD','https://www.cryptocompare.com/media/27010499/ddd.png')," +
                "('DSR','https://www.cryptocompare.com/media/16746481/dsr.png')," +
                "('HMP','https://www.cryptocompare.com/media/350941/hmp.png')," +
                "('NBX','https://www.cryptocompare.com/media/27010901/nbx.png')," +
                "('TIE','https://www.cryptocompare.com/media/1383955/tie.png')," +
                "('MDT','https://www.cryptocompare.com/media/351989/mdt.png')," +
                "('EXB','https://www.cryptocompare.com/media/351401/exb.png')," +
                "('MOBI','https://www.cryptocompare.com/media/1383921/mobi.png')," +
                "('LEND','https://www.cryptocompare.com/media/16746444/lend.png')," +
                "('KBR','https://www.cryptocompare.com/media/20780782/kbr.png')," +
                "('NGC','https://www.cryptocompare.com/media/16746609/ngc.png')," +
                "('ROCK','https://www.cryptocompare.com/media/12318189/rock.png')," +
                "('CTT','https://www.cryptocompare.com/media/1383957/ctt.png')," +
                "('WNET','https://www.cryptocompare.com/media/1383982/wnet1.png')," +
                "('ELTC2','https://www.cryptocompare.com/media/12318300/eltc2.png')," +
                "('DCT','https://www.cryptocompare.com/media/351389/dct.png')," +
                "('DNET','https://www.cryptocompare.com/media/350912/dnet.png')," +
                "('NAS','https://www.cryptocompare.com/media/20780653/nas.png')," +
                "('FYP','https://www.cryptocompare.com/media/16746604/fyp.png')," +
                "('GLYPH','https://www.cryptocompare.com/media/19725/glyph.png')," +
                "('ETT','https://www.cryptocompare.com/media/1383046/ett.png')," +
                "('APC','https://www.cryptocompare.com/media/350956/apc.png')," +
                "('KZC','https://www.cryptocompare.com/media/20780789/kz.png')," +
                "('C20','https://www.cryptocompare.com/media/12318302/c20.png')," +
                "('TDS','https://www.cryptocompare.com/media/20780673/tds.png')," +
                "('BTC','https://www.cryptocompare.com/media/19633/btc.png')," +
                "('ONL','https://www.cryptocompare.com/media/25792573/onl.png')," +
                "('CRX','https://www.cryptocompare.com/media/351388/crx.png')," +
                "('ARCH','https://www.cryptocompare.com/media/20085/arch.png')," +
                "('ATFS','https://www.cryptocompare.com/media/14913545/atfs.png')," +
                "('BIT16','https://www.cryptocompare.com/media/20181/16bit.png')," +
                "('LEPEN','https://www.cryptocompare.com/media/1382507/lepen.png')," +
                "('REV','https://www.cryptocompare.com/media/351061/rev.png')," +
                "('LEMON','https://www.cryptocompare.com/media/351021/lemon.png')," +
                "('BTE','https://www.cryptocompare.com/media/19632/bte.png')," +
                "('SPT','https://www.cryptocompare.com/media/19917/spt.png')," +
                "('STOCKBET','https://www.cryptocompare.com/media/12318119/stockbet.png')," +
                "('VTA','https://www.cryptocompare.com/media/350945/vta.png')," +
                "('GXC*','https://www.cryptocompare.com/media/12318306/gxc.png')," +
                "('ENG','https://www.cryptocompare.com/media/12318287/eng.png')," +
                "('VTC','https://www.cryptocompare.com/media/19945/vtc.png')," +
                "('EVX','https://www.cryptocompare.com/media/1383850/evx.png')," +
                "('GNT','https://www.cryptocompare.com/media/351995/golem_logo.png')," +
                "('CWIS','https://www.cryptocompare.com/media/27010619/cwis_logo.png')," +
                "('ICOS','https://www.cryptocompare.com/media/1383968/icos1.png')," +
                "('XHI','https://www.cryptocompare.com/media/350892/xhi.png')," +
                "('STEX','https://www.cryptocompare.com/media/16404854/stex.png')," +
                "('DIVX','https://www.cryptocompare.com/media/16746540/divx.png')," +
                "('404','https://www.cryptocompare.com/media/351001/404.png')," +
                "('CPC','https://www.cryptocompare.com/media/350560/cpc.png')," +
                "('NUKE','https://www.cryptocompare.com/media/1382626/nuke.png')," +
                "('IDH','https://www.cryptocompare.com/media/25792644/idh.png')," +
                "('MONK','https://www.cryptocompare.com/media/20780785/monk.png')," +
                "('2GIVE','https://www.cryptocompare.com/media/350986/2give.png')," +
                "('EXCL','https://www.cryptocompare.com/media/20035/excl.png')," +
                "('PPP','https://www.cryptocompare.com/media/12318216/ppp.png')," +
                "('DPP','https://www.cryptocompare.com/media/16746618/dpp.png')," +
                "('MAN*','https://www.cryptocompare.com/media/27010516/man.png')," +
                "('AGI','https://www.cryptocompare.com/media/25792653/agi.png')," +
                "('BASH','https://www.cryptocompare.com/media/352004/bash.png')," +
                "('TECH','https://www.cryptocompare.com/media/1382505/tech.png')," +
                "('MXT','https://www.cryptocompare.com/media/1382782/mxt.jpg')," +
                "('NEC*','https://www.cryptocompare.com/media/27010828/nec.png')," +
                "('CHA','https://www.cryptocompare.com/media/19986/a3c.png')," +
                "('POLY*','https://www.cryptocompare.com/media/27010571/poly.png')," +
                "('TIC','https://www.cryptocompare.com/media/1382625/tic.png')," +
                "('XZC','https://www.cryptocompare.com/media/1382780/xzc1.png')," +
                "('APEX','https://www.cryptocompare.com/media/19599/apex.png')," +
                "('MWC','https://www.cryptocompare.com/media/19808/mrs.png')," +
                "('ARNA*','https://www.cryptocompare.com/media/12318005/arna.png')," +
                "('ET4','https://www.cryptocompare.com/media/25792581/et4.png')," +
                "('UNIQ','https://www.cryptocompare.com/media/351387/uniq.png')," +
                "('LAT','https://www.cryptocompare.com/media/9350724/lat.png')," +
                "('SDAO','https://www.cryptocompare.com/media/1383933/sdao.png')," +
                "('LHC','https://www.cryptocompare.com/media/27010598/lhc.png')," +
                "('CCRB','https://www.cryptocompare.com/media/27011026/ccrb.png')," +
                "('HVCO','https://www.cryptocompare.com/media/351014/hvco.png')," +
                "('VRS','https://www.cryptocompare.com/media/352021/vrs.png')," +
                "('SHIFT','https://www.cryptocompare.com/media/1382125/shift.png')," +
                "('SANDG','https://www.cryptocompare.com/media/351556/sandt.png')," +
                "('LAB','https://www.cryptocompare.com/media/16404858/lab.png')," +
                "('365','https://www.cryptocompare.com/media/352070/365.png')," +
                "('SCT*','https://www.cryptocompare.com/media/12318383/sct.png')," +
                "('EXY','https://www.cryptocompare.com/media/14913645/exy.png')," +
                "('CDX*','https://www.cryptocompare.com/media/351404/cdx.png')," +
                "('BCO*','https://www.cryptocompare.com/media/16746477/bco.png')," +
                "('VOX','https://www.cryptocompare.com/media/350824/vox.png')," +
                "('ZET2','https://www.cryptocompare.com/media/351502/zet2.png')," +
                "('LC','https://www.cryptocompare.com/media/351398/lc.png')," +
                "('PRIX','https://www.cryptocompare.com/media/9350797/prix.png')," +
                "('GRF*','https://www.cryptocompare.com/media/12318208/grf.png')," +
                "('ARC*','https://www.cryptocompare.com/media/16746547/arc.png')," +
                "('ARBI','https://www.cryptocompare.com/media/12318258/arbi.png')," +
                "('EBZ','https://www.cryptocompare.com/media/352069/ebz.png')," +
                "('SPHTX','https://www.cryptocompare.com/media/14913551/sphtx.png')," +
                "('VOYA','https://www.cryptocompare.com/media/351304/voya.png')," +
                "('PRP','https://www.cryptocompare.com/media/12318007/prp.png')," +
                "('BSD','https://www.cryptocompare.com/media/351086/bsd.png')," +
                "('MEME','https://www.cryptocompare.com/media/1383050/pepecoin-512.png')," +
                "('NANAS','https://www.cryptocompare.com/media/20575/nanas.png')," +
                "('IRL','https://www.cryptocompare.com/media/20780751/irl.png')," +
                "('WDC','https://www.cryptocompare.com/media/19949/wdc.png')," +
                "('ROUND','https://www.cryptocompare.com/media/1382508/round.png')," +
                "('PKB','https://www.cryptocompare.com/media/20694/pkb.png')," +
                "('BOU','https://www.cryptocompare.com/media/9350750/bou.jpg')," +
                "('LOG','https://www.cryptocompare.com/media/20335/log.png')," +
                "('ORI','https://www.cryptocompare.com/media/27010748/ori.png')," +
                "('BERRY','https://www.cryptocompare.com/media/27010907/berry.jpg')," +
                "('GAM','https://www.cryptocompare.com/media/20293/gam.png')," +
                "('EQ','https://www.cryptocompare.com/media/1383891/eq.png')," +
                "('GREXIT','https://www.cryptocompare.com/media/351016/grexit.png')," +
                "('BTB','https://www.cryptocompare.com/media/20083/bitb.png')," +
                "('LINK','https://www.cryptocompare.com/media/12318078/link.png')," +
                "('ENK','https://www.cryptocompare.com/media/27010914/enk.png')," +
                "('GOOD','https://www.cryptocompare.com/media/9350786/good.png')," +
                "('HDG','https://www.cryptocompare.com/media/9350726/hdg.png')," +
                "('ADA','https://www.cryptocompare.com/media/12318177/ada.png')," +
                "('ELM','https://www.cryptocompare.com/media/12318308/elm.png')," +
                "('ARENA','https://www.cryptocompare.com/media/12318225/arena.png')," +
                "('MINEX','https://www.cryptocompare.com/media/9350702/minex.png')," +
                "('RFR','https://www.cryptocompare.com/media/27011023/rfr.png')," +
                "('SCOT','https://www.cryptocompare.com/media/20416/scot_1.png')," +
                "('TRAC','https://www.cryptocompare.com/media/27010463/trac.png')," +
                "('NTRN','https://www.cryptocompare.com/media/12318281/ntrn.png')," +
                "('CRAVE','https://www.cryptocompare.com/media/20242/crave.png')," +
                "('SGR','https://www.cryptocompare.com/media/20780766/sgr.png')," +
                "('WAY','https://www.cryptocompare.com/media/351433/way.png')," +
                "('BOT','https://www.cryptocompare.com/media/20780753/bot.png')," +
                "('ACT*','https://www.cryptocompare.com/media/16746576/act.png')," +
                "('KRB','https://www.cryptocompare.com/media/25792684/krb.png')," +
                "('PRIME','https://www.cryptocompare.com/media/350979/prime.png')," +
                "('SCRT','https://www.cryptocompare.com/media/351031/scrt.png')," +
                "('VIRAL','https://www.cryptocompare.com/media/20472/viral.png')," +
                "('MAG**','https://www.cryptocompare.com/media/27010851/mag.png')," +
                "('EXTN','https://www.cryptocompare.com/media/27011052/extn.jpg')," +
                "('ELIX','https://www.cryptocompare.com/media/12318172/elix.png')," +
                "('VIBE','https://www.cryptocompare.com/media/12318267/vibe.png')," +
                "('PEPECASH','https://www.cryptocompare.com/media/1382397/pepecash.png')," +
                "('IGNIS','https://www.cryptocompare.com/media/1384046/ignis.png')," +
                "('PIGGY','https://www.cryptocompare.com/media/19854/piggy.png')," +
                "('SWIFT','https://www.cryptocompare.com/media/20446/swift.png')," +
                "('XRB','https://www.cryptocompare.com/media/1383674/xrb.png')," +
                "('AMB','https://www.cryptocompare.com/media/9350739/amb.png')," +
                "('AC3','https://www.cryptocompare.com/media/16746668/ac3.png')," +
                "('VERSA','https://www.cryptocompare.com/media/20629/versa.png')," +
                "('EQUI','https://www.cryptocompare.com/media/27010614/equi.png')," +
                "('EGAS','https://www.cryptocompare.com/media/16746616/egas.png')," +
                "('FND','https://www.cryptocompare.com/media/1383765/fnd.png')," +
                "('ROYAL','https://www.cryptocompare.com/media/351756/royal.png')," +
                "('SPD','https://www.cryptocompare.com/media/27010967/spd.png')," +
                "('CRW','https://www.cryptocompare.com/media/1383378/crw1.png')," +
                "('CAS','https://www.cryptocompare.com/media/12318148/cas.png')," +
                "('ACAT','https://www.cryptocompare.com/media/30001613/acat.jpg')," +
                "('BTLC','https://www.cryptocompare.com/media/352054/btlc.png')," +
                "('SPX*','https://www.cryptocompare.com/media/25792570/transferir-copiar.png')," +
                "('HBC','https://www.cryptocompare.com/media/20780615/hbc.png')," +
                "('CMT*','https://www.cryptocompare.com/media/16746569/cmt.png')," +
                "('PTC*','https://www.cryptocompare.com/media/14913426/ptc.png')," +
                "('LYM','https://www.cryptocompare.com/media/27010755/lym.png')," +
                "('TEK','https://www.cryptocompare.com/media/19929/tek.png')," +
                "('XVC','https://www.cryptocompare.com/media/350813/xvc.png')," +
                "('RGC','https://www.cryptocompare.com/media/12318357/rgc.png')," +
                "('OCL','https://www.cryptocompare.com/media/1383989/ocl.png')," +
                "('MSC','https://www.cryptocompare.com/media/19814/mst.png')," +
                "('NEOS','https://www.cryptocompare.com/media/1382788/neos1.png')," +
                "('GRWI','https://www.cryptocompare.com/media/1383971/grwi.png')," +
                "('PURA','https://www.cryptocompare.com/media/14913533/pura.png')," +
                "('TDFB','https://www.cryptocompare.com/media/351507/tdfb.png')," +
                "('ETHD','https://www.cryptocompare.com/media/12318087/ethd.png')," +
                "('JVY','https://www.cryptocompare.com/media/12318244/jvy.png')," +
                "('CRM','https://www.cryptocompare.com/media/1383915/cream.png')," +
                "('EQM','https://www.cryptocompare.com/media/19808/mrs.png')," +
                "('MBI','https://www.cryptocompare.com/media/1383759/mbi.png')," +
                "('RISE','https://www.cryptocompare.com/media/351059/rise.png')," +
                "('QTL','https://www.cryptocompare.com/media/19879/qtl.png')," +
                "('VNT','https://www.cryptocompare.com/media/1384015/vent.png')," +
                "('STA*','https://www.cryptocompare.com/media/351063/sta.png')," +
                "('HTML5','https://www.cryptocompare.com/media/1383327/html5.png')," +
                "('BTCP','https://www.cryptocompare.com/media/27010791/btcpjpg.png')," +
                "('BFT','https://www.cryptocompare.com/media/25792654/bft.png')," +
                "('SMT*','https://www.cryptocompare.com/media/16746697/smt.png')," +
                "('ONX','https://www.cryptocompare.com/media/1383910/onx.png')," +
                "('ONION','https://www.cryptocompare.com/media/1383894/onion.png')," +
                "('FLX','https://www.cryptocompare.com/media/351007/flx.png')," +
                "('WHL','https://www.cryptocompare.com/media/12318372/whl.png')," +
                "('ETHS','https://www.cryptocompare.com/media/350910/eths.png')," +
                "('DRT','https://www.cryptocompare.com/media/12318099/drt.png')," +
                "('KAT','https://www.cryptocompare.com/media/351028/katz.png')," +
                "('BDG','https://www.cryptocompare.com/media/16746482/bdg.png')," +
                "('DIGIF','https://www.cryptocompare.com/media/27010942/digif.png')," +
                "('DAY','https://www.cryptocompare.com/media/12318271/day.png')," +
                "('ERT','https://www.cryptocompare.com/media/12318226/ert.png')," +
                "('DRC','https://www.cryptocompare.com/media/11417638/drp.png')," +
                "('ZEPH','https://www.cryptocompare.com/media/14913542/zeph.png')," +
                "('XTO','https://www.cryptocompare.com/media/27010572/xto.png')," +
                "('STAC','https://www.cryptocompare.com/media/27010495/stac1.png')," +
                "('TX','https://www.cryptocompare.com/media/20722/tx.png')," +
                "('ELS','https://www.cryptocompare.com/media/1382400/els.png')," +
                "('ATOM*','https://www.cryptocompare.com/media/1383901/cosmos.jpg')," +
                "('VRM','https://www.cryptocompare.com/media/351522/vrm.png')," +
                "('SCR','https://www.cryptocompare.com/media/14913681/scr.png')," +
                "('ANTI','https://www.cryptocompare.com/media/350972/anti.png')," +
                "('CBC','https://www.cryptocompare.com/media/30001676/cbc.png')," +
                "('GRID','https://www.cryptocompare.com/media/14913632/grid.png')," +
                "('MUE','https://www.cryptocompare.com/media/351084/mue.png')," +
                "('GLA','https://www.cryptocompare.com/media/12318141/gla.png')," +
                "('WGR','https://www.cryptocompare.com/media/1383736/wgr.png')," +
                "('BCH','https://www.cryptocompare.com/media/1383919/bch.jpg')," +
                "('ARY','https://www.cryptocompare.com/media/27010774/ary.png')," +
                "('SAFEX','https://www.cryptocompare.com/media/1383986/safex.png')," +
                "('BCD','https://www.cryptocompare.com/media/16404872/bcd.png')," +
                "('ELI','https://www.cryptocompare.com/media/30001620/rpqn1zqi_400x400.jpg')," +
                "('BTCA','https://www.cryptocompare.com/media/16746705/btca.png')," +
                "('MCT','https://www.cryptocompare.com/media/27010616/1717mct_logo.png')," +
                "('DLISK','https://www.cryptocompare.com/media/351237/dlisk.png')," +
                "('AMM','https://www.cryptocompare.com/media/20780616/amm.png')," +
                "('CAG','https://www.cryptocompare.com/media/25792623/cag1.png')," +
                "('LUC','https://www.cryptocompare.com/media/25792608/luc.png')," +
                "('WLK','https://www.cryptocompare.com/media/1383892/wolk.png')," +
                "('BTCE','https://www.cryptocompare.com/media/16746600/btce.png')," +
                "('DIEM','https://www.cryptocompare.com/media/20260/diem_1.png')," +
                "('HHEM','https://www.cryptocompare.com/media/27010617/hhem.png')," +
                "('H2O','https://www.cryptocompare.com/media/12318092/h2o.png')," +
                "('PIVX','https://www.cryptocompare.com/media/1382389/pivx.png')," +
                "('BOLD','https://www.cryptocompare.com/media/27011051/bold.jpg')," +
                "('DTCT','https://www.cryptocompare.com/media/1384025/dtct.png')," +
                "('LOT','https://www.cryptocompare.com/media/27010925/lot.png')," +
                "('NEWB','https://www.cryptocompare.com/media/14913564/newb.png')," +
                "('THETA','https://www.cryptocompare.com/media/27010450/theta.png')," +
                "('RCT','https://www.cryptocompare.com/media/27010590/rct.png')," +
                "('CLINT','https://www.cryptocompare.com/media/351067/clint.png')," +
                "('XCN','https://www.cryptocompare.com/media/20483/xcn.png')," +
                "('ORME','https://www.cryptocompare.com/media/12317975/omes.png')," +
                "('MBC','https://www.cryptocompare.com/media/27010620/mbc_logo.png')," +
                "('STEEM','https://www.cryptocompare.com/media/350907/steem.png')," +
                "('ERR','https://www.cryptocompare.com/media/1382624/err.png')," +
                "('FAIR','https://www.cryptocompare.com/media/20287/fair.png')," +
                "('GCN','https://www.cryptocompare.com/media/1383899/gcn.png')," +
                "('GBRC','https://www.cryptocompare.com/media/351637/gbrc.png')," +
                "('KRONE','https://www.cryptocompare.com/media/9350770/krone.png')," +
                "('DROP','https://www.cryptocompare.com/media/351555/drop.png')," +
                "('DCS.','https://www.cryptocompare.com/media/351407/cloud.png')," +
                "('GAS','https://www.cryptocompare.com/media/1383858/neo.jpg')," +
                "('SLG','https://www.cryptocompare.com/media/20428/slg.png')," +
                "('PRL','https://www.cryptocompare.com/media/16746488/prl.png')," +
                "('RATIO','https://www.cryptocompare.com/media/1382442/ratio.png')," +
                "('YOC','https://www.cryptocompare.com/media/350957/yoc.png')," +
                "('WINK','https://www.cryptocompare.com/media/1383913/wink.png')," +
                "('ILCT','https://www.cryptocompare.com/media/12318299/ilct.png')," +
                "('RT2','https://www.cryptocompare.com/media/19896/rt2.png')," +
                "('007','https://www.cryptocompare.com/media/350595/007.png')," +
                "('TAGR','https://www.cryptocompare.com/media/350940/tagr.png')," +
                "('KING','https://www.cryptocompare.com/media/1383862/king.png')," +
                "('LUX*','https://www.cryptocompare.com/media/20557/lux.png')," +
                "('EGG','https://www.cryptocompare.com/media/20269/egg.png')," +
                "('INXT','https://www.cryptocompare.com/media/12318355/inxt.png')," +
                "('ATCC','https://www.cryptocompare.com/media/12318294/atcc.png')," +
                "('SMLY','https://www.cryptocompare.com/media/20429/smly.png')," +
                "('ERC','https://www.cryptocompare.com/media/350960/erc.png')," +
                "('MCN','https://www.cryptocompare.com/media/20346/mcn.png')," +
                "('EVN*','https://www.cryptocompare.com/media/27011050/evn1.png')," +
                "('ZOI','https://www.cryptocompare.com/media/27011018/zoi.png')," +
                "('BLT','https://www.cryptocompare.com/media/20780668/blt.png')," +
                "('MTN','https://www.cryptocompare.com/media/12318118/mtn.png')," +
                "('VDO','https://www.cryptocompare.com/media/20066/vdo.png')," +
                "('HALAL','https://www.cryptocompare.com/media/27010609/halal_logo.png')," +
                "('AVA','https://www.cryptocompare.com/media/9350772/ava.png')," +
                "('REF','https://www.cryptocompare.com/media/27010509/ref.png')," +
                "('REAL','https://www.cryptocompare.com/media/1383884/rise.png')," +
                "('SET','https://www.cryptocompare.com/media/20780787/set.png')," +
                "('KARMA','https://www.cryptocompare.com/media/350693/karm.png')," +
                "('BITCNY','https://www.cryptocompare.com/media/351490/bitcny.png')," +
                "('ECA','https://www.cryptocompare.com/media/16404869/eca.png')," +
                "('FRD','https://www.cryptocompare.com/media/16404865/frd.png')," +
                "('WIC','https://www.cryptocompare.com/media/12318168/ocfkmb0t_400x400.jpg')," +
                "('DCN','https://www.cryptocompare.com/media/1383999/dcn.png')," +
                "('DNO','https://www.cryptocompare.com/media/27010637/dno.png')," +
                "('DAT','https://www.cryptocompare.com/media/12318265/dat.png')," +
                "('MDT*','https://www.cryptocompare.com/media/27010451/mdt.png')," +
                "('RYZ','https://www.cryptocompare.com/media/12318305/ryz.png')," +
                "('UTT','https://www.cryptocompare.com/media/20780614/utt.png')," +
                "('HIRE','https://www.cryptocompare.com/media/1383882/hite.png')," +
                "('BITCAR','https://www.cryptocompare.com/media/27010931/bitcar.png')," +
                "('AEON','https://www.cryptocompare.com/media/350955/aeon.png')," +
                "('BUN','https://www.cryptocompare.com/media/27010781/bun.png')," +
                "('GTC*','https://www.cryptocompare.com/media/27010485/gtcoin.png')," +
                "('UTK','https://www.cryptocompare.com/media/9350717/utrust.png')," +
                "('HEAT','https://www.cryptocompare.com/media/351399/heat.png')," +
                "('YEE','https://www.cryptocompare.com/media/27010483/yee.png')," +
                "('GRAV','https://www.cryptocompare.com/media/20659/grav.png')," +
                "('DLC','https://www.cryptocompare.com/media/351559/dlc.png')," +
                "('GRLC','https://www.cryptocompare.com/media/27010480/garlic.png')," +
                "('CFTY','https://www.cryptocompare.com/media/25792674/cfty.png')," +
                "('ARN','https://www.cryptocompare.com/media/12318261/arn.png')," +
                "('BCX*','https://www.cryptocompare.com/media/16746738/bcx.png')," +
                "('SAT','https://www.cryptocompare.com/media/27010744/sat.png')," +
                "('CHC','https://www.cryptocompare.com/media/1383907/chc.png')," +
                "('SYNC','https://www.cryptocompare.com/media/19922/sync.png')," +
                "('XSH','https://www.cryptocompare.com/media/16746453/xsh.png')," +
                "('HQX','https://www.cryptocompare.com/media/16746735/hqx.png')," +
                "('CUBE','https://www.cryptocompare.com/media/350948/cube.png')," +
                "('SPICE','https://www.cryptocompare.com/media/27010560/vc.png')," +
                "('MARS','https://www.cryptocompare.com/media/19808/mrs.png')," +
                "('WRC','https://www.cryptocompare.com/media/11999078/wrc.png')," +
                "('SKR*','https://www.cryptocompare.com/media/12318013/skr.png')," +
                "('AUC','https://www.cryptocompare.com/media/27010954/untitled-1.png')," +
                "('MZX','https://www.cryptocompare.com/media/27010540/mzx.png')," +
                "('AIB','https://www.cryptocompare.com/media/350971/aib.png')," +
                "('SP','https://www.cryptocompare.com/media/352018/sp.png')," +
                "('BCDN','https://www.cryptocompare.com/media/27010517/1.png')," +
                "('808','https://www.cryptocompare.com/media/351513/808.png')," +
                "('MNZ','https://www.cryptocompare.com/media/12318336/mnz.png')," +
                "('VLX','https://www.cryptocompare.com/media/30001652/vlx.png')," +
                "('WELL','https://www.cryptocompare.com/media/20780732/well.png')," +
                "('NICE','https://www.cryptocompare.com/media/1382467/nice.png')," +
                "('HST','https://www.cryptocompare.com/media/14913538/hst.png')," +
                "('RZR','https://www.cryptocompare.com/media/20055/rzr.png')," +
                "('DADI','https://www.cryptocompare.com/media/27010852/dadi.png')," +
                "('TIO*','https://www.cryptocompare.com/media/16746450/tio.png')," +
                "('KREDS','https://www.cryptocompare.com/media/27010837/kreds.png')," +
                "('CUZ','https://www.cryptocompare.com/media/27010593/cuz.png')," +
                "('INDI','https://www.cryptocompare.com/media/12318419/indi.png')," +
                "('GX','https://www.cryptocompare.com/media/20780801/gamex.png')," +
                "('CLICK','https://www.cryptocompare.com/media/1382399/click.png')," +
                "('STR*','https://www.cryptocompare.com/media/19920/str.png')," +
                "('DUB','https://www.cryptocompare.com/media/19986/a3c.png');";
        db.execSQL(INSERT_COIN_MISC_DATA4);

        String INSERT_COIN_MISC_DATA5 = "INSERT INTO COIN_MISC(COIN_SYMBOL,COIN_IMAGE_URL) VALUES" +
                "" +
                "('FSC2','https://www.cryptocompare.com/media/19717/fsc.png')," +
                "('NXTTY','https://www.cryptocompare.com/media/20379/nxtty.png')," +
                "('QBK','https://www.cryptocompare.com/media/20400/qbk.png')," +
                "('BLC','https://www.cryptocompare.com/media/19623/blc.png')," +
                "('GIG','https://www.cryptocompare.com/media/20294/gig.png')," +
                "('CC','https://www.cryptocompare.com/media/20225/cc.png')," +
                "('BITS','https://www.cryptocompare.com/media/19622/bits.png')," +
                "('LTBC','https://www.cryptocompare.com/media/20336/ltb.png')," +
                "('VTR','https://www.cryptocompare.com/media/20471/vtr.png')," +
                "('METAL','https://www.cryptocompare.com/media/20359/metal.png')," +
                "('PINK','https://www.cryptocompare.com/media/350588/pinkcoin-logo.png')," +
                "('GRE','https://www.cryptocompare.com/media/1382396/grn.png')," +
                "('XG','https://www.cryptocompare.com/media/20156/xg.png')," +
                "('CHILD','https://www.cryptocompare.com/media/20233/child.png')," +
                "('BOOM','https://www.cryptocompare.com/media/20208/boom.png')," +
                "('MINE','https://www.cryptocompare.com/media/20356/mine.png')," +
                "('SLM','https://www.cryptocompare.com/media/20426/slm.png')," +
                "('GAIA','https://www.cryptocompare.com/media/20290/gaia.png')," +
                "('TRUST','https://www.cryptocompare.com/media/19935/trust.png')," +
                "('FCN','https://www.cryptocompare.com/media/20282/fcn.png')," +
                "('EDC','https://www.cryptocompare.com/media/351066/edc.png')," +
                "('CKC','https://www.cryptocompare.com/media/351068/ckc.png')," +
                "('VIP','https://www.cryptocompare.com/media/351069/vip.png')," +
                "('NXE','https://www.cryptocompare.com/media/351070/nxe.png')," +
                "('ZOOM','https://www.cryptocompare.com/media/351081/zoom.png')," +
                "('DRACO','https://www.cryptocompare.com/media/351390/dt-token.png')," +
                "('YOVI','https://www.cryptocompare.com/media/351073/yovi.png')," +
                "('KUBO','https://www.cryptocompare.com/media/351077/kubo.png')," +
                "('INCP','https://www.cryptocompare.com/media/351078/incp.png')," +
                "('SAK','https://www.cryptocompare.com/media/351079/sak.png')," +
                "('EVIL','https://www.cryptocompare.com/media/351080/evil.png')," +
                "('OMA','https://www.cryptocompare.com/media/20386/oma.png')," +
                "('COX','https://www.cryptocompare.com/media/351083/cox.png')," +
                "('BNT','https://www.cryptocompare.com/media/1383549/bnt.jpg')," +
                "('DES','https://www.cryptocompare.com/media/351087/des.png')," +
                "('PDC','https://www.cryptocompare.com/media/351088/pdc.png')," +
                "('CMT','https://www.cryptocompare.com/media/351090/cmt.png')," +
                "('CHESS','https://www.cryptocompare.com/media/351094/chess.jpg')," +
                "('SPACE','https://www.cryptocompare.com/media/351095/space.png')," +
                "('REE','https://www.cryptocompare.com/media/351096/ree.png')," +
                "('LQD','https://www.cryptocompare.com/media/351097/lqd.png')," +
                "('MARV','https://www.cryptocompare.com/media/351099/marv.png')," +
                "('XDE2','https://www.cryptocompare.com/media/351100/xde2.png')," +
                "('VEC2','https://www.cryptocompare.com/media/351101/vec2.png')," +
                "('OMNI','https://www.cryptocompare.com/media/351102/omni.png')," +
                "('GSY','https://www.cryptocompare.com/media/351103/gsy.png')," +
                "('ROOT','https://www.cryptocompare.com/media/351523/root.png')," +
                "('1ST','https://www.cryptocompare.com/media/351524/1st.png')," +
                "('GPL','https://www.cryptocompare.com/media/351525/gpl.png')," +
                "('DOPE','https://www.cryptocompare.com/media/351526/dope.png')," +
                "('B3','https://www.cryptocompare.com/media/12318367/b3.png')," +
                "('FX','https://www.cryptocompare.com/media/351527/fx.png')," +
                "('PIO','https://www.cryptocompare.com/media/351528/pio.png')," +
                "('GAY','https://www.cryptocompare.com/media/351531/gay.png')," +
                "('SMSR','https://www.cryptocompare.com/media/351543/smsr.png')," +
                "('UBIQ','https://www.cryptocompare.com/media/351544/ubiq.png')," +
                "('ARM','https://www.cryptocompare.com/media/351545/arm.png')," +
                "('RING','https://www.cryptocompare.com/media/351546/ring.png')," +
                "('ERB','https://www.cryptocompare.com/media/351550/erb.png')," +
                "('LAZ','https://www.cryptocompare.com/media/351552/laz.png')," +
                "('FONZ','https://www.cryptocompare.com/media/351553/fonz.png'),        " +
                "('SCN','https://www.cryptocompare.com/media/351563/scn.png')," +
                "('WEX','https://www.cryptocompare.com/media/351564/wex.jpg')," +
                "('LTH','https://www.cryptocompare.com/media/351565/lth.png')," +
                "('BRONZ','https://www.cryptocompare.com/media/351566/bronz.png')," +
                "('SH','https://www.cryptocompare.com/media/351567/sh.png')," +
                "('MG','https://www.cryptocompare.com/media/351588/mg.png')," +
                "('PSI','https://www.cryptocompare.com/media/351589/psi.png')," +
                "('NLC','https://www.cryptocompare.com/media/351591/nlc.png')," +
                "('PSB','https://www.cryptocompare.com/media/351594/psb.jpg')," +
                "('XBTS','https://www.cryptocompare.com/media/351617/beats.png')," +
                "('FIT','https://www.cryptocompare.com/media/351618/fit.png')," +
                "('PINKX','https://www.cryptocompare.com/media/351624/pinkx.png')," +
                "('UNF','https://www.cryptocompare.com/media/351626/unf.png')," +
                "('SPORT','https://www.cryptocompare.com/media/351627/sports.png')," +
                "('PPY','https://www.cryptocompare.com/media/351630/peerplays.png')," +
                "('NTC','https://www.cryptocompare.com/media/351631/ntc.png')," +
                "('EGO','https://www.cryptocompare.com/media/351632/ego.png')," +
                "('BTCL*','https://www.cryptocompare.com/media/351633/btlc.png')," +
                "('RCN*','https://www.cryptocompare.com/media/351634/rcn.png')," +
                "('X2','https://www.cryptocompare.com/media/351635/x2.png')," +
                "('MT','https://www.cryptocompare.com/media/19453/mycelium.png')," +
                "('TIA','https://www.cryptocompare.com/media/351636/tia.png')," +
                "('XUP','https://www.cryptocompare.com/media/351638/xup.png')," +
                "('HALLO','https://www.cryptocompare.com/media/351657/hallo.png')," +
                "('BBCC','https://www.cryptocompare.com/media/351658/bbcc.png')," +
                "('EMIGR','https://www.cryptocompare.com/media/351659/emirg.png')," +
                "('BHC','https://www.cryptocompare.com/media/351660/bhc.png')," +
                "('CRAFT','https://www.cryptocompare.com/media/351681/craft.png')," +
                "('INV','https://www.cryptocompare.com/media/351682/inv.png')," +
                "('OLYMP','https://www.cryptocompare.com/media/351683/olymp.png')," +
                "('DPAY','https://www.cryptocompare.com/media/351684/dpay.png')," +
                "('ATOM','https://www.cryptocompare.com/media/351685/atom.png')," +
                "('HKG','https://www.cryptocompare.com/media/351689/hkg.jpg')," +
                "('ANTC','https://www.cryptocompare.com/media/351690/antc.png')," +
                "('JOBS','https://www.cryptocompare.com/media/351691/jobs.png')," +
                "('DGORE','https://www.cryptocompare.com/media/351697/dgore.png')," +
                "('TRA','https://www.cryptocompare.com/media/351700/tra.png')," +
                "('RMS','https://www.cryptocompare.com/media/351701/rms.png')," +
                "('FJC','https://www.cryptocompare.com/media/27010498/fjc.png')," +
                "('VAPOR','https://www.cryptocompare.com/media/351708/vapor.png')," +
                "('SDP','https://www.cryptocompare.com/media/351709/sdp.jpg')," +
                "('RRT','https://www.cryptocompare.com/media/19554/bitfinex.png')," +
                "('PRE','https://www.cryptocompare.com/media/351711/pre.png')," +
                "('CALC','https://www.cryptocompare.com/media/351712/calc.png')," +
                "('LEA','https://www.cryptocompare.com/media/351729/lea.png')," +
                "('CF','https://www.cryptocompare.com/media/351730/cf.png')," +
                "('CRNK','https://www.cryptocompare.com/media/351731/crnk.png')," +
                "('CFC','https://www.cryptocompare.com/media/351732/cfc.png')," +
                "('VTY','https://www.cryptocompare.com/media/351733/vty.png')," +
                "('SFE','https://www.cryptocompare.com/media/351735/sfe.png')," +
                "('ARDR','https://www.cryptocompare.com/media/351736/ardr.png')," +
                "('BS','https://www.cryptocompare.com/media/351737/bs.png')," +
                "('JIF','https://www.cryptocompare.com/media/351738/jif.png')," +
                "('CRAB','https://www.cryptocompare.com/media/351739/crab.png')," +
                "('AIR*','https://www.cryptocompare.com/media/351740/air.png')," +
                "('HILL','https://www.cryptocompare.com/media/351747/hill.png')," +
                "('FOREX','https://www.cryptocompare.com/media/351748/forex.png')," +
                "('MONETA','https://www.cryptocompare.com/media/351749/moneta.png')," +
                "('EC','https://www.cryptocompare.com/media/351750/ec.jpg')," +
                "('RUBIT','https://www.cryptocompare.com/media/351751/rubit.png')," +
                "('HCC','https://www.cryptocompare.com/media/351752/hcc.png')," +
                "('BRAIN','https://www.cryptocompare.com/media/351753/brain.png')," +
                "('VTX','https://www.cryptocompare.com/media/351754/vertex.png')," +
                "('KRC','https://www.cryptocompare.com/media/351755/krc.png')," +
                "('LFC','https://www.cryptocompare.com/media/351757/lfc.png')," +
                "('ZUR','https://www.cryptocompare.com/media/351758/zur.png')," +
                "('NUBIS','https://www.cryptocompare.com/media/351759/nubis.png')," +
                "('TENNET','https://www.cryptocompare.com/media/351760/tennet.png')," +
                "('PEC','https://www.cryptocompare.com/media/351761/pec.png')," +
                "('GMX','https://www.cryptocompare.com/media/351762/gmx.jpg')," +
                "('32BIT','https://www.cryptocompare.com/media/351763/32bit.png')," +
                "('GNJ','https://www.cryptocompare.com/media/351789/gnj.png')," +
                "('TEAM','https://www.cryptocompare.com/media/351790/team.png')," +
                "('SCT','https://www.cryptocompare.com/media/351791/sct.png')," +
                "('LANA','https://www.cryptocompare.com/media/351792/lana.png')," +
                "('ELE','https://www.cryptocompare.com/media/351793/ele.png')," +
                "('GCC','https://www.cryptocompare.com/media/351796/gcc.jpg')," +
                "('AND','https://www.cryptocompare.com/media/351797/and.png')," +
                "('GBYTE','https://www.cryptocompare.com/media/351835/bytes.png')," +
                "('EQUAL','https://www.cryptocompare.com/media/351867/equal.png')," +
                "('SWEET','https://www.cryptocompare.com/media/351868/sweet.png')," +
                "('2BACCO','https://www.cryptocompare.com/media/351869/2bacco.png')," +
                "('DKC','https://www.cryptocompare.com/media/351870/dkc.png')," +
                "('COC','https://www.cryptocompare.com/media/351872/coc.png')," +
                "('CHOOF','https://www.cryptocompare.com/media/351876/choof.png')," +
                "('ZCL','https://www.cryptocompare.com/media/351926/zcl.png')," +
                "('RYCN','https://www.cryptocompare.com/media/351756/royal.png')," +
                "('PCS','https://www.cryptocompare.com/media/351927/pabyosi.png')," +
                "('NBIT','https://www.cryptocompare.com/media/351928/nbit.png')," +
                "('WINE','https://www.cryptocompare.com/media/351929/wine.png')," +
                "('ARK','https://www.cryptocompare.com/media/351931/ark.png')," +
                "('IFLT','https://www.cryptocompare.com/media/351934/iflt.png')," +
                "('ZECD','https://www.cryptocompare.com/media/351935/zecd.png')," +
                "('ZXT','https://www.cryptocompare.com/media/351936/zxt.png')," +
                "('WASH','https://www.cryptocompare.com/media/351944/wash.png'),        " +
                "('MEGA','https://www.cryptocompare.com/media/352020/mega.png')," +
                "('DRM8','https://www.cryptocompare.com/media/352072/drs.png')," +
                "('FGZ','https://www.cryptocompare.com/media/352082/fgz.png')," +
                "('BOSON','https://www.cryptocompare.com/media/352083/boson.png')," +
                "('ATX','https://www.cryptocompare.com/media/352084/atx.png')," +
                "('PNC','https://www.cryptocompare.com/media/352085/pnc.png')," +
                "('BRDD','https://www.cryptocompare.com/media/352086/brdd.png')," +
                "('TIME','https://www.cryptocompare.com/media/352105/time.png')," +
                "('BIP','https://www.cryptocompare.com/media/352108/bip.png')," +
                "('XNC','https://www.cryptocompare.com/media/352109/xnc.png')," +
                "('EMB','https://www.cryptocompare.com/media/352110/emb.png')," +
                "('BTTF','https://www.cryptocompare.com/media/352111/bttf.png')," +
                "('DLR','https://www.cryptocompare.com/media/352114/dollarcoin.png')," +
                "('CSMIC','https://www.cryptocompare.com/media/352115/csmic.png')," +
                "('FIRST','https://www.cryptocompare.com/media/352116/first.png')," +
                "('SCASH','https://www.cryptocompare.com/media/352117/scash.png')," +
                "('XEN','https://www.cryptocompare.com/media/352119/xen.jpg')," +
                "('JIO','https://www.cryptocompare.com/media/352120/jio.png')," +
                "('IW','https://www.cryptocompare.com/media/352121/iw.png')," +
                "('JNS','https://www.cryptocompare.com/media/352126/jns.png')," +
                "('TRICK','https://www.cryptocompare.com/media/352127/trick.png')," +
                "('DCRE','https://www.cryptocompare.com/media/352128/dcre.png')," +
                "('FRE','https://www.cryptocompare.com/media/352129/fre.png')," +
                "('NPC','https://www.cryptocompare.com/media/352130/npc.png')," +
                "('PLNC','https://www.cryptocompare.com/media/352131/plnc.png')," +
                "('DGMS','https://www.cryptocompare.com/media/352132/dgms.png')," +
                "('ICOB','https://www.cryptocompare.com/media/352133/icb.png')," +
                "('ARCO','https://www.cryptocompare.com/media/352134/arco.png')," +
                "('KURT','https://www.cryptocompare.com/media/352155/kurt.png')," +
                "('XCRE','https://www.cryptocompare.com/media/352156/xcre.png')," +
                "('UR','https://www.cryptocompare.com/media/352182/ur.jpg')," +
                "('MTLM3','https://www.cryptocompare.com/media/352183/mtmc3.png')," +
                "('ODNT','https://www.cryptocompare.com/media/352186/odnt.png')," +
                "('EUC','https://www.cryptocompare.com/media/1382471/euc.png')," +
                "('CCX','https://www.cryptocompare.com/media/352188/ccx.png')," +
                "('BCF','https://www.cryptocompare.com/media/352189/btf.png')," +
                "('SEEDS','https://www.cryptocompare.com/media/352190/seeds.png')," +
                "('POSW','https://www.cryptocompare.com/media/1382902/posw.png')," +
                "('SHORTY','https://www.cryptocompare.com/media/352222/shorty.png')," +
                "('PCM','https://www.cryptocompare.com/media/352223/pcm.png')," +
                "('KC','https://www.cryptocompare.com/media/352224/kc.png')," +
                "('CORAL','https://www.cryptocompare.com/media/352225/coral.png')," +
                "('BamitCoin','https://www.cryptocompare.com/media/352236/bam.png')," +
                "('NXC','https://www.cryptocompare.com/media/352248/nxc.png')," +
                "('MONEY','https://www.cryptocompare.com/media/352249/money.png')," +
                "('BSTAR','https://www.cryptocompare.com/media/352250/bstar.png')," +
                "('HSP','https://www.cryptocompare.com/media/352251/hsp.png')," +
                "('HZT','https://www.cryptocompare.com/media/352291/hzt.png')," +
                "('XSP','https://www.cryptocompare.com/media/352293/xsp.png')," +
                "('BULLS','https://www.cryptocompare.com/media/352295/bulls.png')," +
                "('INCNT','https://www.cryptocompare.com/media/352296/incnt.png')," +
                "('ICON','https://www.cryptocompare.com/media/352297/icon.png')," +
                "('NIC','https://www.cryptocompare.com/media/352309/nic.png')," +
                "('ACN','https://www.cryptocompare.com/media/352310/acn.png')," +
                "('XNG','https://www.cryptocompare.com/media/352311/xng.png')," +
                "('XCI','https://www.cryptocompare.com/media/352312/xci.png')," +
                "('LOOK','https://www.cryptocompare.com/media/1381970/look.png')," +
                "('LOC','https://www.cryptocompare.com/media/1381971/loc.png')," +
                "('MMXVI','https://www.cryptocompare.com/media/1381972/mmxvi.png')," +
                "('TRST','https://www.cryptocompare.com/media/1381975/trst.png')," +
                "('MIS','https://www.cryptocompare.com/media/1381981/mis.png')," +
                "('WOP','https://www.cryptocompare.com/media/1381982/wop.png')," +
                "('CQST','https://www.cryptocompare.com/media/1381983/cqst.png')," +
                "('IMPS','https://www.cryptocompare.com/media/1381984/imps.jpg')," +
                "('IN','https://www.cryptocompare.com/media/1381987/in.png')," +
                "('CHIEF','https://www.cryptocompare.com/media/1381988/chief.png')," +
                "('GOAT','https://www.cryptocompare.com/media/1381990/goat.png')," +
                "('ANAL','https://www.cryptocompare.com/media/1381991/anal.jpg')," +
                "('RC','https://www.cryptocompare.com/media/1381992/rc.png')," +
                "('PND','https://www.cryptocompare.com/media/12318184/pnd.png')," +
                "('PX','https://www.cryptocompare.com/media/1381994/px.png')," +
                "('CND*','https://www.cryptocompare.com/media/1381997/cnd.png')," +
                "('RADI','https://www.cryptocompare.com/media/1382239/rad.png')," +
                "('PASC','https://www.cryptocompare.com/media/1382247/pasc.png')," +
                "('TWIST','https://www.cryptocompare.com/media/1382250/twist1.png')," +
                "('PAYP','https://www.cryptocompare.com/media/1382251/payp.png')," +
                "('DETH','https://www.cryptocompare.com/media/1382252/deth.png')," +
                "('YAY','https://www.cryptocompare.com/media/1382253/yay.png')," +
                "('YES','https://www.cryptocompare.com/media/1382269/yes.png')," +
                "('LENIN','https://www.cryptocompare.com/media/1382270/lenin.png')," +
                "('MRSA','https://www.cryptocompare.com/media/1382287/msra.png')," +
                "('OS76','https://www.cryptocompare.com/media/1382288/os76.png')," +
                "('BOSS','https://www.cryptocompare.com/media/1382289/boss.png')," +
                "('MKR','https://www.cryptocompare.com/media/1382296/mkr.png')," +
                "('BIC','https://www.cryptocompare.com/media/1382337/bic.png')," +
                "('CRPS','https://www.cryptocompare.com/media/1382338/crps.png')," +
                "('MOTO','https://www.cryptocompare.com/media/1382339/moto.png')," +
                "('XNC*','https://www.cryptocompare.com/media/1382347/xnc.png')," +
                "('HXX','https://www.cryptocompare.com/media/1382348/hexx.jpg')," +
                "('SPKTR','https://www.cryptocompare.com/media/1382349/spkr.png')," +
                "('MAC','https://www.cryptocompare.com/media/1382368/mac.png')," +
                "('SEL','https://www.cryptocompare.com/media/1382369/sel.png')," +
                "('NOO','https://www.cryptocompare.com/media/1382370/noo.png')," +
                "('CHAO','https://www.cryptocompare.com/media/1382371/chao.png')," +
                "('XGB','https://www.cryptocompare.com/media/1382372/xgb.png')," +
                "('YMC','https://www.cryptocompare.com/media/1382380/ymc.png')," +
                "('JOK','https://www.cryptocompare.com/media/1382381/jok.png')," +
                "('GBIT','https://www.cryptocompare.com/media/1382382/gbit.jpg')," +
                "('TEC','https://www.cryptocompare.com/media/1382383/tecoin.png')," +
                "('BOMB','https://www.cryptocompare.com/media/1382384/bomb.png')," +
                "('RIDE','https://www.cryptocompare.com/media/1382388/ride.png')," +
                "('KED','https://www.cryptocompare.com/media/1382390/ked.png')," +
                "('CNO','https://www.cryptocompare.com/media/1382391/coino.png')," +
                "('WEALTH','https://www.cryptocompare.com/media/1382392/wealth.png')," +
                "('TAT','https://www.cryptocompare.com/media/1382594/tat.png')," +
                "('EOC','https://www.cryptocompare.com/media/1382628/eoc.png')," +
                "('JANE','https://www.cryptocompare.com/media/1382640/jane.png')," +
                "('PARA','https://www.cryptocompare.com/media/1382641/para.png')," +
                "('MM','https://www.cryptocompare.com/media/1382642/mm.jpg')," +
                "('BXC','https://www.cryptocompare.com/media/1382649/bxc.jpg')," +
                "('NDOGE','https://www.cryptocompare.com/media/1382650/ndoge.png')," +
                "('ZBC','https://www.cryptocompare.com/media/1382652/zbc.png')," +
                "('MLN','https://www.cryptocompare.com/media/1382653/mln.png')," +
                "('FRST','https://www.cryptocompare.com/media/1382654/first.png')," +
                "('PAY','https://www.cryptocompare.com/media/1383687/pay.png')," +
                "('ORO','https://www.cryptocompare.com/media/1382656/oro.png')," +
                "('ALEX','https://www.cryptocompare.com/media/1382657/alex.png')," +
                "('TBCX','https://www.cryptocompare.com/media/1382658/tbcx.png')," +
                "('MCAR','https://www.cryptocompare.com/media/1382659/mcar.png')," +
                "('THS','https://www.cryptocompare.com/media/1382660/ths.png')," +
                "('ACES','https://www.cryptocompare.com/media/1382661/aces.png')," +
                "('UAEC','https://www.cryptocompare.com/media/1382684/uaec.png')," +
                "('EA','https://www.cryptocompare.com/media/1382685/ea.png')," +
                "('PIE','https://www.cryptocompare.com/media/1382686/pie.png')," +
                "('CREA','https://www.cryptocompare.com/media/1382709/crea.png')," +
                "('WISC','https://www.cryptocompare.com/media/1382710/wisc.jpg')," +
                "('BVC','https://www.cryptocompare.com/media/1382711/bvc.png')," +
                "('FIND','https://www.cryptocompare.com/media/1382713/find.png')," +
                "('MLITE','https://www.cryptocompare.com/media/1382725/mlite.png')," +
                "('STALIN','https://www.cryptocompare.com/media/1382726/stalin.png')," +
                "('TSE','https://www.cryptocompare.com/media/1382790/tato1.png')," +
                "('VLTC','https://www.cryptocompare.com/media/1382738/vltc.png')," +
                "('BIOB','https://www.cryptocompare.com/media/1382739/biob.png')," +
                "('SWT','https://www.cryptocompare.com/media/1382740/swt.jpg')," +
                "('PASL','https://www.cryptocompare.com/media/1382741/pasl.png')," +
                "('CDN','https://www.cryptocompare.com/media/1382763/cdn.png')," +
                "('TPAY','https://www.cryptocompare.com/media/1382770/tpay.png')," +
                "('NETKO','https://www.cryptocompare.com/media/1382771/netko.png')," +
                "('HONEY','https://www.cryptocompare.com/media/1382937/honey1.png')," +
                "('MUSIC','https://www.cryptocompare.com/media/1382783/music.png')," +
                "('DTB','https://www.cryptocompare.com/media/1382791/dtb.png')," +
                "('VEG','https://www.cryptocompare.com/media/1382792/veg.png')," +
                "('MBIT','https://www.cryptocompare.com/media/1382793/mbit.png')," +
                "('VOLT','https://www.cryptocompare.com/media/1382794/volt.png')," +
                "('MGO','https://www.cryptocompare.com/media/1382798/mgo.png')," +
                "('EDG','https://www.cryptocompare.com/media/1382799/edg.jpg')," +
                "('B@','https://www.cryptocompare.com/media/1382804/b.png')," +
                "('BEST','https://www.cryptocompare.com/media/1382805/best.jpg')," +
                "('ZENI','https://www.cryptocompare.com/media/1382807/zen.png')," +
                "('PLANET','https://www.cryptocompare.com/media/1382851/planet.png')," +
                "('DUCK','https://www.cryptocompare.com/media/1382852/duckduckcoin.png')," +
                "('BNX','https://www.cryptocompare.com/media/1382853/bnx.png')," +
                "('BSTK','https://www.cryptocompare.com/media/1382858/bstk.png')," +
                "('DBIX','https://www.cryptocompare.com/media/1382860/dbix.png')," +
                "('AMIS','https://www.cryptocompare.com/media/1382862/amis.png')," +
                "('KAYI','https://www.cryptocompare.com/media/1382863/kayi.png')," +
                "('XVP','https://www.cryptocompare.com/media/1382865/xvp.png')," +
                "('BOAT','https://www.cryptocompare.com/media/1382866/boat.png')," +
                "('TAJ','https://www.cryptocompare.com/media/1382867/taj.png')," +
                "('IMX','https://www.cryptocompare.com/media/1382891/imx.png')," +
                "('CJC','https://www.cryptocompare.com/media/1382887/cjc.png')," +
                "('AMY','https://www.cryptocompare.com/media/1382935/amy.jpg')," +
                "('QBT','https://www.cryptocompare.com/media/1382936/qbt.png')," +
                "('SRC*','https://www.cryptocompare.com/media/1382936/src1.png')," +
                "('EB3','https://www.cryptocompare.com/media/1382938/eb3.png')," +
                "('XVE','https://www.cryptocompare.com/media/1382937/xve.png')," +
                "('FAZZ','https://www.cryptocompare.com/media/1382944/fazz.png')," +
                "('APT','https://www.cryptocompare.com/media/1382945/apt.png')," +
                "('BLAZR','https://www.cryptocompare.com/media/1382946/blazr.png')," +
                "('ARPA','https://www.cryptocompare.com/media/1382966/arpa.png')," +
                "('BNB*','https://www.cryptocompare.com/media/1382967/bnb.png')," +
                "('UNI','https://www.cryptocompare.com/media/1382968/uni.png')," +
                "('ECO','https://www.cryptocompare.com/media/1382993/eco.png')," +
                "('DARK','https://www.cryptocompare.com/media/1382995/dark.png')," +
                "('RLC','https://www.cryptocompare.com/media/12318418/rlc.png')," +
                "('ATMS','https://www.cryptocompare.com/media/1383003/atms.png')," +
                "('INPAY','https://www.cryptocompare.com/media/1383004/inpay.png')," +
                "('VISIO','https://www.cryptocompare.com/media/1383047/visio.png')," +
                "('GOT','https://www.cryptocompare.com/media/1383079/got.png')," +
                "('CXT','https://www.cryptocompare.com/media/1383080/cxt.png')," +
                "('EMPC','https://www.cryptocompare.com/media/1383081/empc.png')," +
                "('GNO','https://www.cryptocompare.com/media/1383083/gnosis-logo.png')," +
                "('LGD','https://www.cryptocompare.com/media/1383085/lgd.png')," +
                "('TAAS','https://www.cryptocompare.com/media/1383085/taas.png')," +
                "('BUCKS','https://www.cryptocompare.com/media/1383089/bucks.png')," +
                "('XBY','https://www.cryptocompare.com/media/20780760/xby.png')," +
                "('GUP','https://www.cryptocompare.com/media/1383107/gup.png')," +
                "('MCRN','https://www.cryptocompare.com/media/1383111/mcrn.png')," +
                "('LUN','https://www.cryptocompare.com/media/1383112/lunyr-logo.png')," +
                "('WSX','https://www.cryptocompare.com/media/1383144/wsx.png')," +
                "('IEC','https://www.cryptocompare.com/media/1383144/wsx.png')," +
                "('IMS','https://www.cryptocompare.com/media/1383145/ims.png')," +
                "('CNT','https://www.cryptocompare.com/media/1383150/cnt.png')," +
                "('LMC','https://www.cryptocompare.com/media/1383139/lmc.png')," +
                "('TKN','https://www.cryptocompare.com/media/1383157/tkn.png')," +
                "('BTCS','https://www.cryptocompare.com/media/1383158/btcs.png')," +
                "('PROC','https://www.cryptocompare.com/media/1383159/proc.png')," +
                "('XGR','https://www.cryptocompare.com/media/1383161/xgr.png')," +
                "('WRC*','https://www.cryptocompare.com/media/1383162/wrc.png')," +
                "('HMQ','https://www.cryptocompare.com/media/1383174/hmq.png')," +
                "('BCAP','https://www.cryptocompare.com/media/1383948/bcap1.png')," +
                "('DUO','https://www.cryptocompare.com/media/1383196/duo.png')," +
                "('GRW','https://www.cryptocompare.com/media/1383234/grw.png')," +
                "('APX','https://www.cryptocompare.com/media/1383235/apx.png')," +
                "('MILO','https://www.cryptocompare.com/media/1383236/milo.png')," +
                "('OLV','https://www.cryptocompare.com/media/1383239/xvs.png')," +
                "('ILC','https://www.cryptocompare.com/media/1383238/ilc.png')," +
                "('PZM','https://www.cryptocompare.com/media/1383242/pzm.jpg')," +
                "('PHR','https://www.cryptocompare.com/media/1383243/phr.jpg')," +
                "('ANT','https://www.cryptocompare.com/media/1383244/ant.png')," +
                "('PUPA','https://www.cryptocompare.com/media/1383245/pupa.png')," +
                "('RICE','https://www.cryptocompare.com/media/1383245/rice.png')," +
                "('XCT','https://www.cryptocompare.com/media/1383246/xct.png')," +
                "('RED','https://www.cryptocompare.com/media/1383265/red.png')," +
                "('ZSE','https://www.cryptocompare.com/media/1383266/zse.png')," +
                "('CTIC','https://www.cryptocompare.com/media/1383267/ctic.png')," +
                "('TAP','https://www.cryptocompare.com/media/1383283/tap.png')," +
                "('BITOK','https://www.cryptocompare.com/media/1383282/bitok.jpg')," +
                "('MUU','https://www.cryptocompare.com/media/1383325/muu.png')," +
                "('INF8','https://www.cryptocompare.com/media/1383326/inf8.png')," +
                "('MNE','https://www.cryptocompare.com/media/1383328/mne.png')," +
                "('DICE','https://www.cryptocompare.com/media/1383361/dice.png')," +
                "('SUB*','https://www.cryptocompare.com/media/1383362/sub.png')," +
                "('USC','https://www.cryptocompare.com/media/1383363/usc.png')," +
                "('DUX','https://www.cryptocompare.com/media/1383364/dux.png')," +
                "('XPS','https://www.cryptocompare.com/media/1383365/xps.png')," +
                "('EQT','https://www.cryptocompare.com/media/1383366/eqt.png')," +
                "('INSN','https://www.cryptocompare.com/media/1383366/insn.png')," +
                "('BAT','https://www.cryptocompare.com/media/1383370/bat.png')," +
                "('MAT*','https://www.cryptocompare.com/media/1383371/mat.png')," +
                "('F16','https://www.cryptocompare.com/media/1383372/f16.png')," +
                "('HAMS','https://www.cryptocompare.com/media/1383381/hams.png')," +
                "('QTUM','https://www.cryptocompare.com/media/1383382/qtum.png')," +
                "('NEF','https://www.cryptocompare.com/media/1383383/nef.png')," +
                "('ZEN','https://www.cryptocompare.com/media/25792624/zen.png')," +
                "('BOS','https://www.cryptocompare.com/media/1383521/bos.png')," +
                "('QWARK','https://www.cryptocompare.com/media/1383522/qwark.png')," +
                "('IOT','https://www.cryptocompare.com/media/1383540/iota_logo.png')," +
                "('QRL','https://www.cryptocompare.com/media/1383543/qrl.png')," +
                "('ADL','https://www.cryptocompare.com/media/1383544/adl.png')," +
                "('PTOY','https://www.cryptocompare.com/media/1383547/ptoy.png')," +
                "('LKK','https://www.cryptocompare.com/media/1383553/lkk.png');";
        db.execSQL(INSERT_COIN_MISC_DATA5);

        String INSERT_COIN_MISC_DATA6 = "INSERT INTO COIN_MISC(COIN_SYMBOL,COIN_IMAGE_URL) VALUES" +
                "('ESP','https://www.cryptocompare.com/media/14761907/esp.png')," +
                "('MAD','https://www.cryptocompare.com/media/1383556/mad.png')," +
                "('DYN','https://www.cryptocompare.com/media/1383557/dyn.png')," +
                "('SEQ','https://www.cryptocompare.com/media/1383558/seq.png')," +
                "('MCAP','https://www.cryptocompare.com/media/1383559/mcap.png')," +
                "('MYST','https://www.cryptocompare.com/media/1383561/myst.png')," +
                "('VERI','https://www.cryptocompare.com/media/1383562/veri.png')," +
                "('SNM','https://www.cryptocompare.com/media/1383564/snm.png')," +
                "('SKY','https://www.cryptocompare.com/media/1383565/sky.png')," +
                "('CFI','https://www.cryptocompare.com/media/1383567/cfi.png')," +
                "('SNT','https://www.cryptocompare.com/media/1383568/snt.png')," +
                "('AVT','https://www.cryptocompare.com/media/1383599/avt.png')," +
                "('CVC','https://www.cryptocompare.com/media/1383611/cvc.png')," +
                "('IXT','https://www.cryptocompare.com/media/1383612/ixt.png')," +
                "('DENT','https://www.cryptocompare.com/media/1383613/dent.png')," +
                "('BQX','https://www.cryptocompare.com/media/16404851/ethos.png')," +
                "('STA','https://www.cryptocompare.com/media/1383620/crs.png')," +
                "('TFL','https://www.cryptocompare.com/media/1383621/tfl.png')," +
                "('XTZ','https://www.cryptocompare.com/media/1383651/xbt.png')," +
                "('MCO','https://www.cryptocompare.com/media/1383653/mco.jpg')," +
                "('NMR','https://www.cryptocompare.com/media/1383655/nmr.png')," +
                "('ADX','https://www.cryptocompare.com/media/1383667/adx1.png')," +
                "('QAU','https://www.cryptocompare.com/media/1383669/qau.png')," +
                "('ECOB','https://www.cryptocompare.com/media/1383670/ecob.png')," +
                "('PLBT','https://www.cryptocompare.com/media/1383671/polybius.png')," +
                "('USDT','https://www.cryptocompare.com/media/1383672/usdt.png')," +
                "('AHT','https://www.cryptocompare.com/media/1383688/ahc.png')," +
                "('ATB','https://www.cryptocompare.com/media/1383689/atb.png')," +
                "('TIX','https://www.cryptocompare.com/media/1383690/tix.png')," +
                "('CMP','https://www.cryptocompare.com/media/1383692/compcoin.png')," +
                "('RVT','https://www.cryptocompare.com/media/1383694/rvt.png')," +
                "('HRB','https://www.cryptocompare.com/media/1383695/hrb.png')," +
                "('NET*','https://www.cryptocompare.com/media/1383697/net1.png')," +
                "('8BT','https://www.cryptocompare.com/media/1383698/8bt.png')," +
                "('CDT','https://www.cryptocompare.com/media/1383699/cdt.png')," +
                "('DNT','https://www.cryptocompare.com/media/1383701/dnt.png')," +
                "('SUR','https://www.cryptocompare.com/media/1383696/sur.png')," +
                "('MIV','https://www.cryptocompare.com/media/1383728/miv.png')," +
                "('BET*','https://www.cryptocompare.com/media/1383729/bet.png')," +
                "('SAN','https://www.cryptocompare.com/media/1383730/san.png')," +
                "('KIN','https://www.cryptocompare.com/media/1383731/kin.png')," +
                "('XEL','https://www.cryptocompare.com/media/1383737/xel.png')," +
                "('NVST','https://www.cryptocompare.com/media/1383732/nvst.png')," +
                "('FUN','https://www.cryptocompare.com/media/1383738/fun.png')," +
                "('FUNC','https://www.cryptocompare.com/media/1383739/func.png')," +
                "('PQT','https://www.cryptocompare.com/media/1383741/pqt.png')," +
                "('WTT','https://www.cryptocompare.com/media/1383742/wtt.png')," +
                "('MTL','https://www.cryptocompare.com/media/1383743/mtl.png')," +
                "('HVN','https://www.cryptocompare.com/media/1383745/hvt.png')," +
                "('MYB','https://www.cryptocompare.com/media/1383746/myb.png')," +
                "('PPT','https://www.cryptocompare.com/media/1383747/ppt.png')," +
                "('SNC','https://www.cryptocompare.com/media/1383748/snc.png')," +
                "('STAR','https://www.cryptocompare.com/media/1383750/star1.png')," +
                "('COR','https://www.cryptocompare.com/media/1383753/cor.png')," +
                "('XRL','https://www.cryptocompare.com/media/1383754/xrl.png')," +
                "('OROC','https://www.cryptocompare.com/media/1383755/oroc.png')," +
                "('OAX','https://www.cryptocompare.com/media/1383756/oax.png')," +
                "('DDF','https://www.cryptocompare.com/media/1383760/ddf.png')," +
                "('GGS','https://www.cryptocompare.com/media/1383762/ggs.png')," +
                "('DNA','https://www.cryptocompare.com/media/16746452/dna.png')," +
                "('FYN','https://www.cryptocompare.com/media/1383764/fyn.png')," +
                "('DCY','https://www.cryptocompare.com/media/1383767/dcy.png')," +
                "('CFT','https://www.cryptocompare.com/media/1383769/cft.png')," +
                "('DNR','https://www.cryptocompare.com/media/1383770/dnr.png')," +
                "('VUC','https://www.cryptocompare.com/media/1383773/vuc.png')," +
                "('BTPL','https://www.cryptocompare.com/media/1383774/btpl.png')," +
                "('UNIFY','https://www.cryptocompare.com/media/1383775/unify.png')," +
                "('IPC','https://www.cryptocompare.com/media/1383776/ipc.png')," +
                "('BRIT','https://www.cryptocompare.com/media/1383777/brit.png')," +
                "('AMMO','https://www.cryptocompare.com/media/1383778/ammo.png')," +
                "('SOCC','https://www.cryptocompare.com/media/1383779/socc.png')," +
                "('MASS','https://www.cryptocompare.com/media/1383781/mass.png')," +
                "('IML','https://www.cryptocompare.com/media/1383783/iml.png')," +
                "('XUC','https://www.cryptocompare.com/media/1383784/xuc.png')," +
                "('STU','https://www.cryptocompare.com/media/1383785/stu.png')," +
                "('PLR','https://www.cryptocompare.com/media/27010510/plr.png')," +
                "('GUNS','https://www.cryptocompare.com/media/1383789/guns.png')," +
                "('IFT','https://www.cryptocompare.com/media/12318127/ift.png')," +
                "('CAT*','https://www.cryptocompare.com/media/20780714/cat.png')," +
                "('PRO','https://www.cryptocompare.com/media/1383792/pro.png')," +
                "('SYC','https://www.cryptocompare.com/media/1383793/syc.png')," +
                "('IND','https://www.cryptocompare.com/media/1383794/ind.png')," +
                "('TRIBE','https://www.cryptocompare.com/media/1383797/tribe.jpg')," +
                "('ZRX','https://www.cryptocompare.com/media/1383799/zrx.png')," +
                "('TNT','https://www.cryptocompare.com/media/1383800/tnt.png')," +
                "('PRE*','https://www.cryptocompare.com/media/1383801/pst.png')," +
                "('COSS','https://www.cryptocompare.com/media/1383802/coss.png')," +
                "('STORM','https://www.cryptocompare.com/media/1383803/storm.jpg')," +
                "('STORJ','https://www.cryptocompare.com/media/20422/sjcx.png')," +
                "('OMG','https://www.cryptocompare.com/media/1383814/omisego.png')," +
                "('OTX','https://www.cryptocompare.com/media/1383817/1qrfuod6_400x400.jpg')," +
                "('EQB','https://www.cryptocompare.com/media/1383816/eqb.png')," +
                "('VOISE','https://www.cryptocompare.com/media/12318263/voise.png')," +
                "('ETBS','https://www.cryptocompare.com/media/12318348/etbs.png')," +
                "('CVCOIN','https://www.cryptocompare.com/media/1383821/cvcoin.png')," +
                "('DRP','https://www.cryptocompare.com/media/1383822/drp.png')," +
                "('BOG','https://www.cryptocompare.com/media/1383826/bog.png')," +
                "('NDC','https://www.cryptocompare.com/media/1383827/ndc.png')," +
                "('POE','https://www.cryptocompare.com/media/1383828/poe.png')," +
                "('ADT','https://www.cryptocompare.com/media/1383829/adt.png')," +
                "('AE','https://www.cryptocompare.com/media/1383836/ae.png')," +
                "('UET','https://www.cryptocompare.com/media/1383837/uet.png')," +
                "('PART','https://www.cryptocompare.com/media/1383838/part.png')," +
                "('AGRS','https://www.cryptocompare.com/media/1383839/agrs.png')," +
                "('SAND','https://www.cryptocompare.com/media/1383825/beach.png')," +
                "('DMT','https://www.cryptocompare.com/media/1383841/dmarket.png')," +
                "('DAS','https://www.cryptocompare.com/media/14543970/das.png')," +
                "('ADST','https://www.cryptocompare.com/media/1383846/adst.png')," +
                "('CAT','https://www.cryptocompare.com/media/1383848/bcat1.png')," +
                "('XCJ','https://www.cryptocompare.com/media/1383849/xcj.png')," +
                "('RKC','https://www.cryptocompare.com/media/1383852/rkc.png')," +
                "('NLC2','https://www.cryptocompare.com/media/16746543/nlc2.png')," +
                "('LINDA','https://www.cryptocompare.com/media/1383860/linda.png')," +
                "('ANCP','https://www.cryptocompare.com/media/1383863/ancp.png')," +
                "('RCC','https://www.cryptocompare.com/media/1383864/rcc.png')," +
                "('SNK','https://www.cryptocompare.com/media/1383865/snk.png')," +
                "('CABS','https://www.cryptocompare.com/media/1383869/cabs.png')," +
                "('OPT','https://www.cryptocompare.com/media/1383873/opt.png')," +
                "('ZNT','https://www.cryptocompare.com/media/1383875/znt.png')," +
                "('BITSD','https://www.cryptocompare.com/media/1383878/bitsd.png')," +
                "('XLC','https://www.cryptocompare.com/media/1383879/ivetpxdq_400x400.jpg')," +
                "('SKIN','https://www.cryptocompare.com/media/1383880/dsb_amky_400x400.jpg')," +
                "('MSP','https://www.cryptocompare.com/media/1383881/c9fobrlr_400x400.jpg'),        " +
                "('MCI','https://www.cryptocompare.com/media/12318289/mci.png')," +
                "('COV','https://www.cryptocompare.com/media/12318288/cov.png')," +
                "('WAX','https://www.cryptocompare.com/media/12318290/wax.png')," +
                "('AIR','https://www.cryptocompare.com/media/12318291/air.png')," +
                "('NTO','https://www.cryptocompare.com/media/12318293/nto.png')," +
                "('BAR','https://www.cryptocompare.com/media/14543951/bar.png')," +
                "('ECASH','https://www.cryptocompare.com/media/14543971/ecash.png')," +
                "('DRGN','https://www.cryptocompare.com/media/16746490/drgn.png')," +
                "('ODMC','https://www.cryptocompare.com/media/14761889/odmcoin.png')," +
                "('CABS*','https://www.cryptocompare.com/media/14761917/ctst.png')," +
                "('BRAT','https://www.cryptocompare.com/media/25792621/brat.png')," +
                "('DTR','https://www.cryptocompare.com/media/14761903/dtr.png')," +
                "('TKR','https://www.cryptocompare.com/media/14761909/tkr.png')," +
                "('KEY','https://www.cryptocompare.com/media/14761912/key.png')," +
                "('ELITE','https://www.cryptocompare.com/media/14761914/elite.png')," +
                "('XIOS','https://www.cryptocompare.com/media/14761915/xios.png')," +
                "('DOVU','https://www.cryptocompare.com/media/14761916/dovu.png')," +
                "('ETN','https://www.cryptocompare.com/media/14761932/electroneum.png')," +
                "('REA','https://www.cryptocompare.com/media/14761934/rea.png')," +
                "('AVE','https://www.cryptocompare.com/media/14761937/ave.png')," +
                "('XNN','https://www.cryptocompare.com/media/14761938/xnn.png')," +
                "('LOAN*','https://www.cryptocompare.com/media/14761940/loan.png')," +
                "('ZAB','https://www.cryptocompare.com/media/14761946/zab.png')," +
                "('MDL','https://www.cryptocompare.com/media/14913435/mdl-ico.png')," +
                "('BT1','https://www.cryptocompare.com/media/19633/btc.png')," +
                "('BT2','https://www.cryptocompare.com/media/19633/btc.png')," +
                "('SHP','https://www.cryptocompare.com/media/14761950/shp.png')," +
                "('JCR','https://www.cryptocompare.com/media/14761952/jcr.png')," +
                "('XSB','https://www.cryptocompare.com/media/14761953/xbs.png')," +
                "('KEK','https://www.cryptocompare.com/media/14913432/kek.png')," +
                "('AID','https://www.cryptocompare.com/media/14913433/aidcoin.png')," +
                "('BHC*','https://www.cryptocompare.com/media/14913434/bhc.png')," +
                "('ALTCOM','https://www.cryptocompare.com/media/14913436/altcom.png')," +
                "('OST','https://www.cryptocompare.com/media/14913437/st.png')," +
                "('DATA','https://www.cryptocompare.com/media/14913438/data.png')," +
                "('UGC','https://www.cryptocompare.com/media/14913439/ugt.png')," +
                "('DTC','https://www.cryptocompare.com/media/14913440/dtc.png')," +
                "('PLAY','https://www.cryptocompare.com/media/14913441/play.png')," +
                "('CLD','https://www.cryptocompare.com/media/14913452/cld.png')," +
                "('OTN','https://www.cryptocompare.com/media/14913453/otn.png')," +
                "('POS','https://www.cryptocompare.com/media/14913455/pos.png')," +
                "('NEOG','https://www.cryptocompare.com/media/14913457/neog.png')," +
                "('EXN','https://www.cryptocompare.com/media/14913459/exn.png')," +
                "('INS','https://www.cryptocompare.com/media/14913458/ins.png')," +
                "('TRCT','https://www.cryptocompare.com/media/14913462/trct.png')," +
                "('UKG','https://www.cryptocompare.com/media/14913456/ukg.png')," +
                "('BTCRED','https://www.cryptocompare.com/media/14913463/btcred.png')," +
                "('EBCH','https://www.cryptocompare.com/media/14913464/ebch.png')," +
                "('JPC*','https://www.cryptocompare.com/media/14913466/jpc.png')," +
                "('AXT','https://www.cryptocompare.com/media/14913467/axt.png')," +
                "('HUC','https://www.cryptocompare.com/media/20037/hun.png')," +
                "('IFC','https://www.cryptocompare.com/media/19754/ifc.png')," +
                "('JBS','https://www.cryptocompare.com/media/20044/jbs.png')," +
                "('JUDGE','https://www.cryptocompare.com/media/20038/judge.png')," +
                "('KEY*','https://www.cryptocompare.com/media/20331/key.png')," +
                "('KGC','https://www.cryptocompare.com/media/19763/kgc.png')," +
                "('LKY','https://www.cryptocompare.com/media/19774/lky.png')," +
                "('LTCD','https://www.cryptocompare.com/media/20043/ltcd.png')," +
                "('LXC','https://www.cryptocompare.com/media/20045/lxc.png')," +
                "('MED','https://www.cryptocompare.com/media/20046/med.png')," +
                "('MIN','https://www.cryptocompare.com/media/19793/min.png')," +
                "('NET','https://www.cryptocompare.com/media/19826/net.png')," +
                "('NRB','https://www.cryptocompare.com/media/19839/nrb.png')," +
                "('SBC','https://www.cryptocompare.com/media/19900/sbc.png')," +
                "('SDC','https://www.cryptocompare.com/media/20419/sdc.png')," +
                "('SHLD','https://www.cryptocompare.com/media/19904/shld.png')," +
                "('SILK','https://www.cryptocompare.com/media/20057/silk.png')," +
                "('TIT','https://www.cryptocompare.com/media/20069/tit.png')," +
                "('TOR','https://www.cryptocompare.com/media/19934/tor.png')," +
                "('TTC','https://www.cryptocompare.com/media/20064/ttc.png')," +
                "('ULTC','https://www.cryptocompare.com/media/20063/ultc.png')," +
                "('USDE','https://www.cryptocompare.com/media/20465/usde.png')," +
                "('VOOT','https://www.cryptocompare.com/media/19946/voot.png')," +
                "('XAI','https://www.cryptocompare.com/media/20071/xai.png')," +
                "('XBOT','https://www.cryptocompare.com/media/20073/xbot.png')," +
                "('XBS','https://www.cryptocompare.com/media/351060/xbs_1.png')," +
                "('XCASH','https://www.cryptocompare.com/media/20075/xcash.png')," +
                "('XXX','https://www.cryptocompare.com/media/350617/xxx.png')," +
                "('BURST','https://www.cryptocompare.com/media/16746623/burst.png')," +
                "('TRON','https://www.cryptocompare.com/media/20459/tron.png')," +
                "('SLING','https://www.cryptocompare.com/media/20425/sling.png')," +
                "('KTK','https://www.cryptocompare.com/media/19771/ktk.png')," +
                "('WBB','https://www.cryptocompare.com/media/20477/wbb.png')," +
                "('GRID*','https://www.cryptocompare.com/media/20313/grid.png')," +
                "('MMXIV','https://www.cryptocompare.com/media/19798/mmxiv.png')," +
                "('AM','https://www.cryptocompare.com/media/20191/am.png'),        " +
                "('FRWC','https://www.cryptocompare.com/media/351361/frwc.png')," +
                "('LCK','https://www.cryptocompare.com/media/20780805/luck.png')," +
                "('MRT','https://www.cryptocompare.com/media/350884/waves_1.png')," +
                "('IOU','https://www.cryptocompare.com/media/1383241/iou1.png')," +
                "('EBC','https://www.cryptocompare.com/media/25792622/ebc.png')," +
                "('TKY','https://www.cryptocompare.com/media/27010658/tky.png')," +
                "('MTN*','https://www.cryptocompare.com/media/27010631/mtn_logo.png')," +
                "('LA','https://www.cryptocompare.com/media/27010681/latoken.png')," +
                "('PARETO','https://www.cryptocompare.com/media/27010679/pareto.png')," +
                "('GOA','https://www.cryptocompare.com/media/20780698/goa.png')," +
                "('RNS','https://www.cryptocompare.com/media/1382859/rns.png')," +
                "('BRO','https://www.cryptocompare.com/media/9350701/bro.png')," +
                "('SCORE','https://www.cryptocompare.com/media/1383813/score.png')," +
                "('ARC','https://www.cryptocompare.com/media/1383824/arc.png')," +
                "('XCPO','https://www.cryptocompare.com/media/20780625/xcpo.png')," +
                "('XCXT','https://www.cryptocompare.com/media/9350784/xcxt.png')," +
                "('ENT','https://www.cryptocompare.com/media/352157/ent.jpg')," +
                "('MDC*','https://www.cryptocompare.com/media/14913531/mdc.png')," +
                "('SUCR','https://www.cryptocompare.com/media/20780650/sucr.png')," +
                "('PNX','https://www.cryptocompare.com/media/20780612/pnx.png')," +
                "('ITZ','https://www.cryptocompare.com/media/25792662/itz.png')," +
                "('ACCO','https://www.cryptocompare.com/media/20780654/acco.png')," +
                "('MGN','https://www.cryptocompare.com/media/20780788/mgn.png')," +
                "('AMS','https://www.cryptocompare.com/media/350562/ams.png')," +
                "('SUMO','https://www.cryptocompare.com/media/27010696/sumo.png')," +
                "('XP','https://www.cryptocompare.com/media/12318134/xp.png')," +
                "('DMD','https://www.cryptocompare.com/media/19680/dmd.png')," +
                "('MUN','https://www.cryptocompare.com/media/27010701/mun.png')," +
                "('TIG','https://www.cryptocompare.com/media/27010719/tig.png')," +
                "('ING','https://www.cryptocompare.com/media/27010597/ing_logo.png')," +
                "('DXT','https://www.cryptocompare.com/media/27010739/dxt.png')," +
                "('CRL','https://www.cryptocompare.com/media/27010747/crl.png')," +
                "('REM','https://www.cryptocompare.com/media/20780658/rem.png')," +
                "('BCA','https://www.cryptocompare.com/media/27010760/bca-2.jpg')," +
                "('PBT','https://www.cryptocompare.com/media/1383324/pbt.png')," +
                "('DAR','https://www.cryptocompare.com/media/351930/dar.png')," +
                "('WCT','https://www.cryptocompare.com/media/350884/waves_1.png')," +
                "('MER','https://www.cryptocompare.com/media/14913628/mer.png'),        " +
                "('SHOW','https://www.cryptocompare.com/media/27010825/show.jpg')," +
                "('STC','https://www.cryptocompare.com/media/27010826/stc.jpg')," +
                "('AIT','https://www.cryptocompare.com/media/27010829/ait.jpg')," +
                "('DTT*','https://www.cryptocompare.com/media/14761941/dtt1.png')," +
                "('STQ','https://www.cryptocompare.com/media/27010830/stq.jpg')," +
                "('TEN','https://www.cryptocompare.com/media/27010864/ten.png')," +
                "('BETR','https://www.cryptocompare.com/media/27010594/betr_logo.png')," +
                "('CRPT','https://www.cryptocompare.com/media/25792665/crpt.png')," +
                "('LWF','https://www.cryptocompare.com/media/20780794/lwf.png')," +
                "('DEB','https://www.cryptocompare.com/media/16404861/deb.png')," +
                "('PYP','https://www.cryptocompare.com/media/16746650/pyp.png')," +
                "('GMR','https://www.cryptocompare.com/media/27010596/gmr.png')," +
                "('ALT','https://www.cryptocompare.com/media/27010831/alt.jpg')," +
                "('TRF','https://www.cryptocompare.com/media/27010832/trf.png')," +
                "('KB3','https://www.cryptocompare.com/media/27010833/kb3.jpg')," +
                "('LCC','https://www.cryptocompare.com/media/27010889/lcc.png')," +
                "('NYX','https://www.cryptocompare.com/media/27010735/nyx.png')," +
                "('MWAT','https://www.cryptocompare.com/media/27010794/mwat.jpg')," +
                "('ZYD','https://www.cryptocompare.com/media/351510/zyd.png')," +
                "('DASH','https://www.cryptocompare.com/media/20626/imageedit_27_4355944719.png')," +
                "('PTR','https://www.cryptocompare.com/media/27010888/ptr.png')," +
                "('EBTC','https://www.cryptocompare.com/media/12318175/ebtc.png')," +
                "('FLIP','https://www.cryptocompare.com/media/27010910/flip.png')," +
                "('NLG','https://www.cryptocompare.com/media/19828/nlg.png')," +
                "('UTN','https://www.cryptocompare.com/media/16746611/utn.png')," +
                "('ILT','https://www.cryptocompare.com/media/27010772/iqt.png')," +
                "('LYK','https://www.cryptocompare.com/media/27010724/lyk.png')," +
                "('IQB','https://www.cryptocompare.com/media/27010692/iqb.png')," +
                "('BANCA','https://www.cryptocompare.com/media/27010659/banca.png')," +
                "('SHPING','https://www.cryptocompare.com/media/27010884/shping.png')," +
                "('EARTH','https://www.cryptocompare.com/media/12318190/earth.png')," +
                "('VPRC','https://www.cryptocompare.com/media/350951/vpc.png')," +
                "('DHT','https://www.cryptocompare.com/media/27010912/dht.png')," +
                "('WCG','https://www.cryptocompare.com/media/20780799/wcg.png')," +
                "('XMCC','https://www.cryptocompare.com/media/27010930/xmcc.jpg')," +
                "('CRC**','https://www.cryptocompare.com/media/27010559/crc.png')," +
                "('NBR','https://www.cryptocompare.com/media/27010775/nbr.jpg')," +
                "('HLP','https://www.cryptocompare.com/media/27010877/hlp.png')," +
                "('Q1S','https://www.cryptocompare.com/media/27010561/q1s.png')," +
                "('EQL','https://www.cryptocompare.com/media/27010838/eql.jpg')," +
                "('VULC','https://www.cryptocompare.com/media/27010840/vulc.jpg')," +
                "('MAG','https://www.cryptocompare.com/media/16404853/mag.png')," +
                "('SPA','https://www.cryptocompare.com/media/19911/spa.png')," +
                "('UNRC','https://www.cryptocompare.com/media/27010854/unrc.png')," +
                "('TOKC','https://www.cryptocompare.com/media/27010853/tokc.jpg')," +
                "('SXC','https://www.cryptocompare.com/media/19924/sxc.png')," +
                "('HYS','https://www.cryptocompare.com/media/27010859/hys.jpg')," +
                "('LCWP','https://www.cryptocompare.com/media/27010860/lcwp.jpg')," +
                "('BCR*','https://www.cryptocompare.com/media/27010899/bcr.jpg')," +
                "('SPC*','https://www.cryptocompare.com/media/27010909/spc2.jpg')," +
                "('GOOD*','https://www.cryptocompare.com/media/27010913/good.jpg')," +
                "('DTH','https://www.cryptocompare.com/media/27010917/dth.jpg')," +
                "('SOC','https://www.cryptocompare.com/media/27010918/soc.png')," +
                "('TDX','https://www.cryptocompare.com/media/27010919/tdx.png')," +
                "('CLN','https://www.cryptocompare.com/media/27010933/cln.png')," +
                "('MLT','https://www.cryptocompare.com/media/25792620/mlt.png')," +
                "('VST','https://www.cryptocompare.com/media/25792598/vst.png')," +
                "('REN','https://www.cryptocompare.com/media/27010916/ren.jpg')," +
                "('BAX','https://www.cryptocompare.com/media/27010904/bax.jpg')," +
                "('ABT*','https://www.cryptocompare.com/media/27010666/abt2.png')," +
                "('CSC','https://www.cryptocompare.com/media/19667/csc.png')," +
                "('WT','https://www.cryptocompare.com/media/27010582/wt.png')," +
                "('ADBL','https://www.cryptocompare.com/media/27010818/adbl.png')," +
                "('FILL','https://www.cryptocompare.com/media/27011029/fil1.png')," +
                "('EOS','https://www.cryptocompare.com/media/1383652/eos_1.png')," +
                "('VLR','https://www.cryptocompare.com/media/20780606/vlr.png')," +
                "('DRC*','https://www.cryptocompare.com/media/16746438/drc.png');";

        db.execSQL(INSERT_COIN_MISC_DATA6);
    }


    public boolean insertOneChatData(String date, String time, String chatData) {
        SQLiteDatabase db = null;
        long result=-1;
        try{
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(DATE, date);
            values.put(TIME, time);
            values.put(CHAT_DATA, chatData);
            result = db.insert(TABLE_CHAT, null, values);
            db.close();
        }
        catch(SQLiteException  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }

        return result != -1;
    }

    public List<ChatData> getChatHistory(long lastId) {

        SQLiteDatabase db = null;
        List<ChatData> chatDataArrayList = new ArrayList<>();

        try{
            db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_CHAT + " WHERE " + ID + "< " + lastId + " ORDER BY " + ID + " DESC LIMIT 10", null);
            if (cursor.moveToFirst()) {
                do {
                    ChatData chatData = Util.getChatData(cursor.getString((cursor.getColumnIndex(CHAT_DATA))));
                    chatData.setId(cursor.getLong(cursor.getColumnIndex(ID)));
                    chatDataArrayList.add(chatData);
                } while (cursor.moveToNext());
            }
            db.close();
            Log.d(TAG, "getChatHistory: " + chatDataArrayList.size());
        }
        catch(Exception  ex)
        {
            if (db != null && db.isOpen())
                db.close();
        }

        return chatDataArrayList;
    }

}
