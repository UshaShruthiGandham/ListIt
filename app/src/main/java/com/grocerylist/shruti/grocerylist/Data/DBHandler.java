package com.grocerylist.shruti.grocerylist.Data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.SyncStateContract;
import android.util.Log;

import com.grocerylist.shruti.grocerylist.Model.Grocery;
import com.grocerylist.shruti.grocerylist.Utils.Constants;
import com.grocerylist.shruti.grocerylist.log.Logger;

import java.util.ArrayList;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;

/**
 * Created by shruti on 8/13/2017.
 */

public class DBHandler extends SQLiteOpenHelper{

    private static final String TAG="DBHandler";

    private Context ctx;


    public DBHandler(Context context) {
        super(context, Constants.DB_Name,null,Constants.DB_VERSION);

        this.ctx=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Logger.logCreate(TAG);

        String CREATE_TABLE= "CREATE TABLE "+ Constants.Table_Name + "("
                +Constants.KEY_ID + " INTEGER PRIMARY KEY,"
                +Constants.KEY_GROCERY_ITEM + " TEXT,"
                +Constants.KEY_QUANTITY+ " TEXT,"
                +Constants.KEY_DATE_NAME+ " LONG);";
        db.execSQL(CREATE_TABLE);

        Log.d("CREATED", "Created the Table"+Constants.Table_Name);

       /* Cursor c= db.rawQuery("SELECT * FROM "+ Constants.Table_Name +" WHERE 0",null);
        String[] columnNames=c.getColumnNames();

        Log.d("Coloum names","COLOUM NAMES"+columnNames.toString());*/
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        db.execSQL("DROP TABLE IF EXISTS"+ Constants.Table_Name);

        onCreate(db);
    }

    /**
     * CRUD Operations: Create, Read, Update, Delete Methods
     */

    //Add Grocery

    public void addGrocery(Grocery grocery){

        SQLiteDatabase db= this.getWritableDatabase();

        ContentValues values= new ContentValues();
        values.put(Constants.KEY_GROCERY_ITEM, grocery.getName());
        values.put(Constants.KEY_QUANTITY, grocery.getQuantity());
        values.put(Constants.KEY_DATE_NAME, java.lang.System.currentTimeMillis());// get system time

        //Insert row
        db.insert(Constants.Table_Name, null ,values);

        Log.d("SAVED", "Saved to DB");


    }

    //Get Item

    public Grocery getGrocery(int id){


        SQLiteDatabase db= this.getWritableDatabase();

        Cursor cursor= db.query(Constants.Table_Name, new String[]{Constants.KEY_ID,
                Constants.KEY_GROCERY_ITEM, Constants.KEY_QUANTITY,Constants.KEY_DATE_NAME},Constants.KEY_ID+"=?",
                new String[]{String.valueOf(id)},null,null,null,null);

        if(cursor!=null)
            cursor.moveToFirst();

            Grocery grocery= new Grocery();
            grocery.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
            grocery.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_GROCERY_ITEM)));
            grocery.setQuantity(cursor.getString(cursor.getColumnIndex(Constants.KEY_QUANTITY)));


            //convert timestamp to readable
            java.text.DateFormat dateFormat= java.text.DateFormat.getDateInstance();
            String formatedDate= dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_DATE_NAME))).getTime());

            grocery.setDateAdded(formatedDate);
        return grocery;
    }

    //Get all list
    public List<Grocery> getAllGroceries(){


        SQLiteDatabase db= this.getReadableDatabase();

        List<Grocery> groceryList= new ArrayList<>();

        Cursor cursor= db.query(Constants.Table_Name, new String[]{Constants.KEY_ID,Constants.KEY_GROCERY_ITEM,
                Constants.KEY_QUANTITY,Constants.KEY_DATE_NAME},null,null,null,null,Constants.KEY_DATE_NAME +" DESC");
        if(cursor.moveToFirst()){

            do{

                Grocery grocery=new Grocery();
                grocery.setId(Integer.parseInt(cursor.getString(cursor.getColumnIndex(Constants.KEY_ID))));
                grocery.setName(cursor.getString(cursor.getColumnIndex(Constants.KEY_GROCERY_ITEM)));
                grocery.setQuantity(cursor.getString(cursor.getColumnIndex(Constants.KEY_QUANTITY)));


                //convert timestamp to readable
                java.text.DateFormat dateFormat= java.text.DateFormat.getDateInstance();
                String formatedDate= dateFormat.format(new Date(cursor.getLong(cursor.getColumnIndex(Constants.KEY_DATE_NAME))).getTime());

                grocery.setDateAdded(formatedDate);

                //add item to list
                groceryList.add(grocery);

            }while (cursor.moveToNext());
        }
        return groceryList ;
    }

    //Update item
    public int updateGrocery(Grocery grocery){

        SQLiteDatabase db= this.getWritableDatabase();

        ContentValues values= new ContentValues();
        values.put(Constants.KEY_GROCERY_ITEM, grocery.getName());
        values.put(Constants.KEY_QUANTITY, grocery.getQuantity());
        values.put(Constants.KEY_DATE_NAME, java.lang.System.currentTimeMillis());// get system time

        //update row
        return db.update(Constants.Table_Name,values,Constants.KEY_ID+"=?",new String[]{String.valueOf(grocery.getId())});


    }

    //Delete Item
    public void deleteItem(int id){

        SQLiteDatabase db= this.getWritableDatabase();

        db.delete(Constants.Table_Name,Constants.KEY_ID+"=?",new String[]{String.valueOf(id)});

        db.close();

    }

    //Get Count

    public int getCount(){

        String countQuery= "SELECT * FROM "+ Constants.Table_Name;
        SQLiteDatabase db= this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery,null);

        return cursor.getCount();
    }


}
