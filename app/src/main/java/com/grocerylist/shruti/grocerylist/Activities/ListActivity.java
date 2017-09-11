package com.grocerylist.shruti.grocerylist.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.support.v7.widget.ShareActionProvider;

import com.grocerylist.shruti.grocerylist.Data.DBHandler;
import com.grocerylist.shruti.grocerylist.Model.Grocery;
import com.grocerylist.shruti.grocerylist.R;
import com.grocerylist.shruti.grocerylist.UI.RecyclerViewAdapter;
import com.grocerylist.shruti.grocerylist.log.Logger;

import java.io.File;
import java.io.FileOutputStream;
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

    //private ShareActionProvider mShare;

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);

        MenuItem shareItem = menu.findItem(R.id.action_share);

        ShareActionProvider mShare = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        ArrayList<Uri> uris= new ArrayList<>();
        Uri uri=null;

        Bitmap image= getScreenShot(recyclerView);

        File fileImage= store(image,"List"+".png");

        if(fileImage!=null){

            uri=Uri.fromFile(fileImage);
            uris.add(uri);
        }
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, uris);

        //navigateUpTo(Intent.createChooser(shareIntent,"Send using"));
        mShare.setShareIntent(shareIntent);

        return true;

    }

    public Bitmap getScreenShot(RecyclerView view) {

        int size = view.getAdapter().getItemCount();
        RecyclerView.ViewHolder holder = view.getAdapter().createViewHolder(view, 0);
        view.getAdapter().onBindViewHolder(holder, 0);
        holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
        Bitmap bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), holder.itemView.getMeasuredHeight() * size,
                Bitmap.Config.ARGB_8888);
        Canvas bigCanvas = new Canvas(bigBitmap);
        bigCanvas.drawColor(Color.WHITE);
        Paint paint = new Paint();
        int iHeight = 0;
        holder.itemView.setDrawingCacheEnabled(true);
        holder.itemView.buildDrawingCache();
        bigCanvas.drawBitmap(holder.itemView.getDrawingCache(), 0f, iHeight, paint);
        holder.itemView.setDrawingCacheEnabled(false);
        holder.itemView.destroyDrawingCache();
        iHeight += holder.itemView.getMeasuredHeight();
        for (int i = 1; i < size; i++) {
            view.getAdapter().onBindViewHolder(holder, i);
            holder.itemView.setDrawingCacheEnabled(true);
            holder.itemView.buildDrawingCache();
            bigCanvas.drawBitmap(holder.itemView.getDrawingCache(), 0f, iHeight, paint);
            iHeight += holder.itemView.getMeasuredHeight();
            holder.itemView.setDrawingCacheEnabled(false);
            holder.itemView.destroyDrawingCache();
        }
        return bigBitmap;

    }

    public File  store(Bitmap bm, String fileName){

        Log.e("In store","store method is invoked");
        final  String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Screenshots";
        File dir = new File(dirPath);
        if(!dir.exists()){
            dir.mkdirs();
        }

        File file = new File(dirPath, fileName);
        try {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return file;
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
        },800);
    }
}
