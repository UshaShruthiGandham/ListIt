package com.grocerylist.shruti.grocerylist.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.support.v7.widget.Toolbar;

import com.grocerylist.shruti.grocerylist.Data.DBHandler;
import com.grocerylist.shruti.grocerylist.Model.Grocery;
import com.grocerylist.shruti.grocerylist.log.Logger;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.ShareActionProvider;

import com.grocerylist.shruti.grocerylist.R;

public class HomeActivity extends AppCompatActivity {


    private static final String TAG="HomeActivity";

    private AlertDialog.Builder dialogueBuilder;

    private AlertDialog mAlertDialog;

    private EditText itemName;

    private EditText itemQuantity;

    private Button saveButton;

    private Button shareButton;

    private DBHandler db;

    private ShareActionProvider mShareActionProvider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Logger.logCreate(TAG);
        setContentView(R.layout.activity_home);

        db= new DBHandler(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setLogo(R.drawable.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);
        if(db.getCount()>0){

            startActivity(new Intent(HomeActivity.this,ListActivity.class ));
            finish();
        }else{

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/

                    createPopupDialog();
                }
            });

        }


    }

    private void createPopupDialog() {

        dialogueBuilder = new AlertDialog.Builder(this);
        View view=getLayoutInflater().inflate(R.layout.popup,null);

        itemName= (EditText) view.findViewById(R.id.item_name);
        itemQuantity= (EditText)view.findViewById(R.id.item_quantity);
        saveButton=(Button)view.findViewById(R.id.add_button);

        dialogueBuilder.setView(view);
        mAlertDialog= dialogueBuilder.create();
        mAlertDialog.show();

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TODO: Go to next Screen
                if(!itemName.getText().toString().isEmpty() && !itemQuantity.getText().toString().isEmpty()) {
                    saveGroceryToDB(view);

                }
            }
        });
    }

    private void saveGroceryToDB(View view) {

        Grocery grocery = new Grocery();

        String newItem= itemName.getText().toString();
        String newQuantity= itemQuantity.getText().toString();

        grocery.setName(newItem);
        grocery.setQuantity(newQuantity);

        //Save to DB

        db.addGrocery(grocery);

        Snackbar.make(view,"Item Saved!",Snackbar.LENGTH_LONG).show();

        //Log.d("Added Item ID:", String.valueOf(db.getCount()));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAlertDialog.dismiss();
                startActivity(new Intent(HomeActivity.this,ListActivity.class ));
                finish();
            }
        },1000);// 1 second

    }
}
