package com.mastercard.simplifyapp.handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.mastercard.simplifyapp.utility.DbUtils.generateUUID;


public class StockHandler {

    //Database management variables
    private static final String TABLE_NAME = "stock";
    private static final String DATA_BASE_NAME = "app_db";
    private static final int DATABASE_VERSION = 1;


    //Database columns
    private static final String ID = "id"; //id for transaction
    private static final String NAME = "transaction_amount"; //total cost of transaction
    private static final String DESCRIPTION = "customer"; //person buying items
    private static final String QUANTITY = "items"; //items in transaction
    private static final String COST = "time_sent"; //time message was sent


    private static final String TABLE_CREATE = "create table " + TABLE_NAME +
            " (" + ID + " text not null, "
            + NAME +  " text not null,"
            + DESCRIPTION +  " text not null,"
            + COST +  " real not null,"
            + QUANTITY +  " int not null);";

    private DataBaseHelper dbhelper;
    private Context ctx;
    private SQLiteDatabase db;

    public StockHandler(Context ctx)
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

    public StockHandler open()
    {
        db = dbhelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        dbhelper.close();
    }

    public long insertData(String name, String description,double price,int quantity)
    {
        ContentValues content = new ContentValues();
        content.put(ID,generateUUID().toString());
        content.put(NAME,name);
        content.put(DESCRIPTION,description);
        content.put(COST,price);
        content.put(QUANTITY,quantity);
        return db.insert(TABLE_NAME,null,content);
    }

    public int returnAmount()
    {
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public Cursor returnData()
    {
        return db.query(TABLE_NAME, new String[]{ID, NAME,DESCRIPTION,COST,QUANTITY}, null, null, null, null, null);
    }


}
