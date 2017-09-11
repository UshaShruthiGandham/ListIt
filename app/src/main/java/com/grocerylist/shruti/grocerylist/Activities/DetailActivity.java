package com.grocerylist.shruti.grocerylist.Activities;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.grocerylist.shruti.grocerylist.R;
import com.grocerylist.shruti.grocerylist.log.Logger;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG="DetailActivity";

    private TextView itemNameTV;
    private TextView itemQtyTV;
    private TextView itemDateTV;
    private int groceryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Logger.logCreate(TAG);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar menu = getSupportActionBar();
        menu.setLogo(R.drawable.ic_launcher);
        menu.setDisplayUseLogoEnabled(true);

        itemNameTV= (TextView)findViewById(R.id.itemNameTV);
        itemQtyTV= (TextView)findViewById(R.id.itemQtyTV);
        itemDateTV= (TextView)findViewById(R.id.itemDateTV);

        Bundle bundle= getIntent().getExtras();

        if(bundle!=null){

            itemNameTV.setText(bundle.getString("name"));
            itemQtyTV.setText(bundle.getString("quantity"));
            itemDateTV.setText(bundle.getString("date"));
            groceryId=bundle.getInt("id");
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_share) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
