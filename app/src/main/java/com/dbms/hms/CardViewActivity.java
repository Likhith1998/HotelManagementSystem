package com.dbms.hms;

/**
 * Created by LIKHITH on 09-04-2018.
 */


import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class CardViewActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static String LOG_TAG = "CardViewActivity";
    ArrayList<String> mtexts;
    DatabaseHelper helper = new DatabaseHelper(this);
    String checkoutdate,checkindate;
    ArrayList<DataObject> results = new ArrayList<>();
    Dialog myDialog;
    int pos;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_view);
        Intent intent = getIntent();
        checkindate = intent.getStringExtra("checkindate");
        checkoutdate = intent.getStringExtra("checkoutdate");

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);
        myDialog = new Dialog(this);

        // Code to Add an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).addItem(obj, index);

        // Code to remove an item with default animation
        //((MyRecyclerViewAdapter) mAdapter).deleteItem(index);
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(getApplicationContext(), Dashboard.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((MyRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MyRecyclerViewAdapter
                .MyClickListener() {
            @Override
            public void onItemClick(int position, View v) {
                Log.i(LOG_TAG, " Clicked on Item " + position);
                pos = position;
                ShowPopup();
            }
        });
    }

    private ArrayList<DataObject> getDataSet() {
        results = helper.searchRoom(checkindate,checkoutdate);
        return results;
    }

    public void ShowPopup() {
        TextView txtclose;
        Button btnFollow;
        myDialog.setContentView(R.layout.custompopup);
        txtclose =(TextView) myDialog.findViewById(R.id.txtclose);
        TextView roomname = (TextView) myDialog.findViewById(R.id.roomname);
        TextView available = (TextView) myDialog.findViewById(R.id.available);
        TextView capacity = (TextView) myDialog.findViewById(R.id.capacity);
        TextView price = (TextView) myDialog.findViewById(R.id.price);
        roomname.setText(results.get(pos).getmText1());
        available.setText(results.get(pos).getmText2());
        capacity.setText(results.get(pos).getmText3());
        price.setText(results.get(pos).getmText4());
        btnFollow = (Button) myDialog.findViewById(R.id.btnfollow);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
        btnFollow.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(CardViewActivity.this);
                progressDialog.setIndeterminate(true);
                progressDialog.setMessage("Payment proceeding...");
                progressDialog.show();
                int n = helper.maxreservesid();
                int roomnumber;
                int roomtype = Integer.valueOf(results.get(pos).getmText5());
                roomnumber = helper.availableroom(roomtype,checkindate,checkoutdate);
                helper.insertReserves(n+1,checkindate,checkoutdate,Integer.valueOf(results.get(pos).getmText3()),"reserved",roomnumber,1);
                Intent intent = new Intent(getApplicationContext(),Booking.class);
                startActivity(intent);
                finish();
            }
        });
        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
    }
}