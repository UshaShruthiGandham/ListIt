package com.grocerylist.shruti.grocerylist.UI;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.grocerylist.shruti.grocerylist.Activities.DetailActivity;
import com.grocerylist.shruti.grocerylist.Activities.HomeActivity;
import com.grocerylist.shruti.grocerylist.Activities.ListActivity;
import com.grocerylist.shruti.grocerylist.Data.DBHandler;
import com.grocerylist.shruti.grocerylist.Model.Grocery;
import com.grocerylist.shruti.grocerylist.R;

import java.util.List;

/**
 * Created by shruti on 9/4/2017.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;

    private List<Grocery> mGroceryList;

    private AlertDialog.Builder alertDialogBuilder;

    private AlertDialog alertDialog;

    private LayoutInflater layoutInflater;

    private DBHandler db;

    public RecyclerViewAdapter(Context context, List<Grocery> groceryList) {
        this.context = context;
        mGroceryList = groceryList;
    }



    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_row,parent,false);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {

        Grocery grocery= mGroceryList.get(position);

        holder.name.setText(grocery.getName());
        holder.quantity.setText(grocery.getQuantity());
        holder.date.setText(grocery.getDateAdded());
    }

    @Override
    public int getItemCount() {
        return mGroceryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView name;
        public TextView quantity;
        public TextView date;
        public Button editButton;
        public Button deleteButton;
        public int id;

        public ViewHolder(View View, final Context ctx) {
            super(View);

            context =ctx;

            name= (TextView) View.findViewById(R.id.name);
            quantity= (TextView) View.findViewById(R.id.quantity);
            date= (TextView) View.findViewById(R.id.date);

            editButton= (Button) View.findViewById(R.id.editButton);
            deleteButton= (Button) View.findViewById(R.id.deleteButton);

            editButton.setOnClickListener(this);
            deleteButton.setOnClickListener(this);

            View.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //go to next screen
                    int position= getAdapterPosition();

                    Grocery grocery= mGroceryList.get(position);

                    Intent intent= new Intent(ctx, DetailActivity.class);
                    intent.putExtra("name", grocery.getName());
                    intent.putExtra("quantity", grocery.getQuantity());
                    intent.putExtra("date", grocery.getDateAdded());
                    intent.putExtra("id", grocery.getId());

                    ctx.startActivity(intent);

                }
            });

        }

        @Override
        public void onClick(View v) {

            int position= getAdapterPosition();
            Grocery grocery= mGroceryList.get(position);
            switch (v.getId()){

                case R.id.editButton:

                    editItem(grocery);
                    break;

                case R.id.deleteButton:

                    deleteItem(grocery.getId());
                    break;
            }

        }

        public void deleteItem(final int id){

            //Create Alert

            alertDialogBuilder= new AlertDialog.Builder(context);

            layoutInflater= LayoutInflater.from(context);
            View view=layoutInflater.inflate(R.layout.confirmation_dialog,null);

            Button negativeButton= (Button)view.findViewById(R.id.negativeButton);
            Button positiveButton= (Button)view.findViewById(R.id.positiveButton);

            alertDialogBuilder.setView(view);
            alertDialog= alertDialogBuilder.create();
            alertDialog.show();

            negativeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });

            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //delete Item

                    DBHandler db= new DBHandler(context);

                    db.deleteItem(id);

                    mGroceryList.remove(getAdapterPosition());
                    notifyItemRemoved(getAdapterPosition());

                    if(mGroceryList.size()==0){

                        context.startActivity(new Intent(context,HomeActivity.class ));

                    }
                    alertDialog.dismiss();

                }
            });


        }

        public void editItem(final Grocery grocery){

            alertDialogBuilder= new AlertDialog.Builder(context);
            layoutInflater= LayoutInflater.from(context);
           final View view= layoutInflater.inflate(R.layout.popup,null);
           final EditText itemName= (EditText) view.findViewById(R.id.item_name);
           final EditText itemQuantity= (EditText)view.findViewById(R.id.item_quantity);
           final TextView title= (TextView) view.findViewById(R.id.title);
           Button saveButton=(Button)view.findViewById(R.id.add_button);

            title.setText("Edit List Item");

            alertDialogBuilder.setView(view);
            alertDialog= alertDialogBuilder.create();
            alertDialog.show();

            saveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Snackbar.make(view, "button clicked", Snackbar.LENGTH_LONG).show();

                    String newItem= itemName.getText().toString();
                    String newQuantity= itemQuantity.getText().toString();

                    grocery.setName(newItem);
                    grocery.setQuantity(newQuantity);

                    db= new DBHandler(context);
                    if(!itemName.getText().toString().isEmpty() && !itemQuantity.getText().toString().isEmpty()) {
                        db.updateGrocery(grocery);
                        notifyItemChanged(getAdapterPosition(), grocery);
                        alertDialog.dismiss();
                    }else{

                        Snackbar.make(view, "Add Item and Quantity", Snackbar.LENGTH_LONG).show();
                    }
                }
            });


        }


    }
}
