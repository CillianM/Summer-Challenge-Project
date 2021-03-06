package com.mastercard.simplifyapp.handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mastercard.simplifyapp.objects.Customer;
import com.simplify.android.sdk.Card;

import java.util.Date;

import static com.mastercard.simplifyapp.utility.DbUtils.generateUUID;


public class CustomerHandler {

    //Database management variables
    private static final String TABLE_NAME = "customers";
    private static final String DATA_BASE_NAME = "customer_db";
    private static final int DATABASE_VERSION = 1;


    //Database columns
    private static final String ID = "id"; //id for transaction
    private static final String NAME = "name"; //total cost of transaction
    private static final String EMAIL = "email"; //total cost of transaction
    private static final String PHONE = "phone"; //total cost of transaction
    private static final String CARD_NUM = "num"; //total cost of transaction
    private static final String CARD_CVV = "cvv"; //total cost of transaction
    private static final String CARD_EXP = "exp"; //total cost of transaction
    private static final String CREATED_DATE = "created_date"; //person buying items


    private static final String TABLE_CREATE = "create table " + TABLE_NAME +
            " (" + ID + " text not null, "
            + NAME +  " text not null,"
            + EMAIL +  " text,"
            + PHONE +  " text,"
            + CARD_NUM +  " text,"
            + CARD_CVV +  " text,"
            + CARD_EXP +  " text,"
            + CREATED_DATE +  " int not null);";

    private DataBaseHelper dbhelper;
    private Context ctx;
    private SQLiteDatabase db;

    public CustomerHandler(Context ctx)
    {
        this.ctx = ctx;
        dbhelper = new DataBaseHelper(ctx);
    }

    public Customer getCustomer(String customerId) {
        Customer customer = null;
        Cursor c = db.query(TABLE_NAME, new String[]{NAME}
                , ID + " LIKE ?", new String[]{customerId}, null, null, null);
        while (c.moveToNext()) {
            customer = new Customer(c.getString(0));
        }
        return customer;
    }

    public Card getCustomerCard(String customerId) {
        Card card = null;
        Cursor c = db.query(TABLE_NAME, new String[]{CARD_NUM, CARD_EXP, CARD_CVV}
                , ID + " LIKE ?", new String[]{customerId}, null, null, null);
        while (c.moveToNext()) {
            if (c.getString(0) != null) {
                card = new Card();
                card.setNumber(c.getString(0));
                card.setCvc(c.getString(2));
                String exp = c.getString(1);
                String month = exp.substring(0, exp.indexOf("/"));
                String year = exp.substring(exp.indexOf("/") + 1);
                card.setExpYear(year);
                card.setExpMonth(month);
            }
        }
        return card;
    }

    public void updateCustomerCard(String customerId, String cardNumber, String cvv, String expiry) {
        ContentValues cv = new ContentValues();
        cv.put(CARD_NUM, cardNumber);
        cv.put(CARD_EXP, expiry);
        cv.put(CARD_CVV, cvv);
        String[] args = new String[]{customerId};
        db.update(TABLE_NAME, cv, ID + "=?", args);
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

    public CustomerHandler open()
    {
        db = dbhelper.getWritableDatabase();
        return this;
    }

    public void close()
    {
        dbhelper.close();
    }

    public long insertData(String name)
    {
        ContentValues content = new ContentValues();
        content.put(ID,generateUUID().toString());
        content.put(NAME,name);
        content.put(CREATED_DATE, new Date().toString());
        return db.insert(TABLE_NAME,null,content);
    }

    public void deleteCustomer(String id)
    {
        db.delete(TABLE_NAME, ID + " = ?", new String[] { id });
    }

    public int returnAmount()
    {
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public Cursor returnData()
    {
        return db.query(TABLE_NAME, new String[]{ID, NAME,EMAIL,PHONE,CARD_NUM,CARD_CVV,CARD_EXP,CREATED_DATE}, null, null, null, null, null);
    }


}

