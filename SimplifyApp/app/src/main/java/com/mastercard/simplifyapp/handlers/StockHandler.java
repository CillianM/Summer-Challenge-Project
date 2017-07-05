package com.mastercard.simplifyapp.handlers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mastercard.simplifyapp.objects.StoreItem;

import static com.mastercard.simplifyapp.utility.DbUtils.generateUUID;


public class StockHandler {

    //Database management variables
    private static final String TABLE_NAME = "stock";
    private static final String DATA_BASE_NAME = "app_db";
    private static final int DATABASE_VERSION = 1;


    //Database columns
    private static final String ID = "id"; //id for transaction
    private static final String CATEGORY_ID = "category_id"; //id for transaction
    private static final String NAME = "name"; //total cost of transaction
    private static final String DESCRIPTION = "description"; //person buying items
    private static final String QUANTITY = "quantity"; //items in transaction
    private static final String COST = "cost"; //time message was sent


    private static final String TABLE_CREATE = "create table " + TABLE_NAME +
            " (" + ID + " text not null, "
            + CATEGORY_ID + " text not null,"
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

    public StoreItem getStoreItem(String id) {
        StoreItem item = null;
        Cursor c = db.query(TABLE_NAME, new String[]{NAME, COST}
                , ID + " LIKE ?", new String[]{id}, null, null, null);
        while (c.moveToNext()) {
            item = new StoreItem(id, c.getString(0), c.getFloat(1));
        }
        return item;
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

    public long insertData(String categoryId, String name, String description, double price, int quantity)
    {
        ContentValues content = new ContentValues();
        content.put(ID,generateUUID().toString());
        content.put(CATEGORY_ID, categoryId);
        content.put(NAME,name);
        content.put(DESCRIPTION,description);
        content.put(COST,price);
        content.put(QUANTITY,quantity);
        return db.insert(TABLE_NAME,null,content);
    }

    public void deleteStock(String name)
    {
        db.delete(TABLE_NAME, NAME + " = ?", new String[] { name });
    }

    public int returnAmount()
    {
        return (int) DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public Cursor returnData()
    {
        return db.query(TABLE_NAME, new String[]{ID, CATEGORY_ID, NAME, DESCRIPTION, COST, QUANTITY}, null, null, null, null, null);
    }

    public Cursor returnCategoryGroup(String categoryId) {
        return db.query(TABLE_NAME, new String[]{ID, CATEGORY_ID, NAME, DESCRIPTION, COST, QUANTITY}, CATEGORY_ID + "=?", new String[]{categoryId}, null, null, null, null);
    }


}
