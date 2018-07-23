package com.dbms.hms;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;

public class Dashboard extends AppCompatActivity {

    TextView tv1,tv2;
    Calendar mCurrentDate;
    int day,month,year;
    int checkin_day,checkin_month,checkin_year,checkout_day,checkout_month,checkout_year;

    @InjectView(R.id.btn_search) Button search_Button;
    @InjectView(R.id.btn_logout) Button logout_Button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.inject(this);


        tv1 = (TextView) findViewById(R.id.btn_checkin);
        tv2 = (TextView) findViewById(R.id.btn_checkout);
        mCurrentDate = Calendar.getInstance();
        day = mCurrentDate.get(Calendar.DAY_OF_MONTH);
        month = mCurrentDate.get(Calendar.MONTH);
        year = mCurrentDate.get(Calendar.YEAR);

        month = month+1;
        tv1.setText(day+"/"+month+"/"+year);
        tv2.setText(day+"/"+month+"/"+year);
        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Dashboard.this, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear+1;
                        tv1.setText(dayOfMonth+"/"+monthOfYear+"/"+year);
                        checkin_day = dayOfMonth;
                        checkin_month = monthOfYear;
                        checkin_year = year;

                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });
        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(Dashboard.this, new DatePickerDialog.OnDateSetListener(){
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        monthOfYear = monthOfYear+1;
                        tv2.setText(dayOfMonth+"/"+monthOfYear+"/"+year);

                        checkout_day = dayOfMonth;
                        checkout_month = monthOfYear;
                        checkout_year = year;
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        search_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });
        logout_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });

    }


    public void validate(){
        if(checkout_day>checkin_day || checkout_month > checkin_month || checkout_year > checkin_year){
            final ProgressDialog progressDialog = new ProgressDialog(Dashboard.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage("Searching...");
            progressDialog.show();
            progressDialog.setCancelable(true);
            progressDialog.setCanceledOnTouchOutside(false);
            Intent intent = new Intent(getApplicationContext(),CardViewActivity.class);
            String Checkindate = checkin_year+"-"+checkin_month+"-"+checkin_day;
            String Checkoutdate = checkout_year+"-"+checkout_month+"-"+checkout_day;
            intent.putExtra("checkindate",Checkindate);
            intent.putExtra("checkoutdate",Checkoutdate);
            startActivity(intent);
            finish();
        }
        else{
            Toast.makeText(getBaseContext(), "Incorrect Dates", Toast.LENGTH_LONG).show();
        }
    }

    public void logout(){
        startActivity(new Intent(getApplicationContext(),Login.class));
        finish();
    }
}
