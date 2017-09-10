package com.grocerylist.shruti.grocerylist.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.grocerylist.shruti.grocerylist.Data.DBHandler;
import com.grocerylist.shruti.grocerylist.Model.Grocery;
import com.grocerylist.shruti.grocerylist.R;
import com.grocerylist.shruti.grocerylist.UI.RecyclerViewAdapter;
import com.grocerylist.shruti.grocerylist.log.Logger;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private static final String TAG="ListActivity";

    private RecyclerView recyclerView;

    private RecyclerViewAdapter recyclerViewAdapter;

    private List<Grocery> groceryList;

    private List<Grocery> itemsList;

    private DBHandler db;

    private AlertDialog.Builder dialogueBuilder;

    private AlertDialog mAlertDialog;

    private EditText itemName;

    private EditText itemQuantity;

    private Button saveButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        Logger.logCreate(TAG);

        setContentView(R.layout.activity_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setLogo(R.drawable.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPopupDialog();
            }
        });


        db= new DBHandler(this);

        recyclerView= (RecyclerView) findViewById(R.id.recyclerViewID);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        groceryList= new ArrayList<>();
        itemsList= new ArrayList<>();
        //Get Items From DB

        groceryList= db.getAllGroceries();

        for (Grocery c: groceryList){

            Grocery grocery= new Grocery();
            grocery.setName(c.getName());
            grocery.setQuantity(c.getQuantity());
            grocery.setId(c.getId());
            grocery.setDateAdded("Added on: "+ c.getDateAdded());

            itemsList.add(grocery);
        }

        recyclerViewAdapter= new RecyclerViewAdapter(this, itemsList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
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

        mAlertDialog.dismiss();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAlertDialog.dismiss();
                /*finish();
                startActivity(getIntent());*/
                startActivity(new Intent(ListActivity.this,ListActivity.class ));
                finish();
            }
        },1000);// 1 second

    }
}
