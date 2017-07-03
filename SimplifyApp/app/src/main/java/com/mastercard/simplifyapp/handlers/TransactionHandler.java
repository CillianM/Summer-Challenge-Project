package com.mastercard.simplifyapp.handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.mastercard.simplifyapp.utility.DbUtils.generateUUID;
import static com.mastercard.simplifyapp.utility.DbUtils.getCurrentEpochTime;


public class TransactionHandler {

    //Database management variables
    private static final String TABLE_NAME = "transactions";
    private static final String DATA_BASE_NAME = "transaction_db";
    private static final int DATABASE_VERSION = 1;


    //Database columns
    private static final String ID = "id"; //id for transaction
    private static final String TRANSACTION_AMOUNT = "transaction_amount"; //total cost of transaction
    private static final String CUSTOMER = "customer"; //person buying items
    private static final String ITEMS = "items"; //items in transaction
    private static final String METHOD = "method"; //time message was sent
    private static final String TIME_SENT = "time_sent"; //time message was sent


    private static final String TABLE_CREATE = "create table " + TABLE_NAME +
            " (" + ID + " text not null, "
            + TRANSACTION_AMOUNT +  " real not null,"
            + CUSTOMER +  " text not null,"
            + ITEMS +  " text not null,"
            + METHOD + " text not null,"
            + TIME_SENT +  " text not null);";

    private DataBaseHelper dbhelper;
    private Context ctx;
    private SQLiteDatabase db;

    public TransactionHandler(Context ctx)
    {
        this.ctx = ctx;
        dbhelper = new DataBaseHelper(ctx);
    }

    private static class DataBaseHelper extends SQLiteOpenHelper
    {

        DataBaseHelper(Context ctx)
        {
            super(ctx,DATA_BASE_NAME,null,DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            try
            {
                db.execSQL(TABLE_CREATE);
            }

            catch(SQLException e)
            {
                e.printStackTrace();
            }
        }


        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
        {
            db.execSQL("DROP TABLE IF EXIST " + TABLE_NAME);
            onCreate(db);
        }
    }

    public TransactionHandler open()
    {
        db = dbhelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        dbhelper.close();
    }

    public long insertData(double transactionAmount, String customer, String items, String method)
    {
        ContentValues content = new ContentValues();
        content.put(ID,generateUUID().toString());
        content.put(CUSTOMER,customer);
        content.put(TRANSACTION_AMOUNT,transactionAmount);
        content.put(ITEMS,items);
        content.put(METHOD, method);
        content.put(TIME_SENT,getCurrentEpochTime());
        return db.insert(TABLE_NAME,null,content);
    }

    public void deleteTransaction(String id)
    {
        db.delete(TABLE_NAME, ID + " = ?", new String[] { id });
    }

    public int returnAmount()
    {
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public Cursor returnData()
    {
        return db.query(TABLE_NAME, new String[]{ID, TRANSACTION_AMOUNT, CUSTOMER, ITEMS, METHOD, TIME_SENT}, null, null, null, null, null);
    }


}
