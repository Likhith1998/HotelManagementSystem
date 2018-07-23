package com.dbms.hms;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Vector;

/**
 * Created by LIKHITH on 23-03-2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "dbms.db";
    SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Customer(email_id VARCHAR(40) NOT NULL,customer_id INT,username VARCHAR(20) NOT NULL,password VARCHAR(20) NOT NULL,PRIMARY KEY (customer_id));");
        db.execSQL("CREATE TABLE Room_Type(type_name VARCHAR(20) NOT NULL,description VARCHAR(100) NOT NULL,room_price INT NOT NULL,total_rooms INT NOT NULL,type_id INT,capacity INT NOT NULL,no_of_rooms_available INT NOT NULL,PRIMARY KEY (type_id));");
        db.execSQL("CREATE TABLE Room(room_number INT NOT NULL,type_id INT NOT NULL,PRIMARY KEY (room_number),FOREIGN KEY (type_id) REFERENCES Room_Type(type_id));");
        db.execSQL("CREATE TABLE reserves(reservation_id INT,arrival_date VARCHAR NOT NULL,departure_date VARCHAR NOT NULL,no_of_guests INT NOT NULL,status VARCHAR(20) NOT NULL,room_number INT NOT NULL,customer_id INT NOT NULL,PRIMARY KEY (reservation_id),FOREIGN KEY (room_number) REFERENCES Room(room_number),FOREIGN KEY (customer_id) REFERENCES Customer(customer_id));");
        db.execSQL("INSERT INTO Room_Type VALUES('DELUXE SINGLE','These Deluxe Rooms let you relax as you admire a beautiful view of the pool.',1000,4,1,1,4);");
        db.execSQL("INSERT INTO Room_Type VALUES('DELUXE DOUBLE', 'These Deluxe Rooms let you relax as you admire a beautiful view of the pool.', 1800, 2, 2, 2, 2);");
        db.execSQL("INSERT INTO Room_Type VALUES('DELUXE TRIPLE', 'These Deluxe Rooms let you relax as you admire a beautiful view of the pool.', 2400, 1, 3, 3, 1);");
        db.execSQL("INSERT INTO Room VALUES(1, 1);");
        db.execSQL("INSERT INTO Room VALUES(2, 1)");
        db.execSQL("INSERT INTO Room VALUES(3, 1);");
        db.execSQL("INSERT INTO Room VALUES(4, 1);");
        db.execSQL("INSERT INTO Room VALUES(5, 2);");
        db.execSQL("INSERT INTO Room VALUES(6, 2);");
        db.execSQL("INSERT INTO Room VALUES(7, 3);");
        this.db = db;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Customer;");
        db.execSQL("DROP TABLE IF EXISTS System_Admin;");
        db.execSQL("DROP TABLE IF EXISTS Room_Type;");
        db.execSQL("DROP TABLE IF EXISTS Room;");
        db.execSQL("DROP TABLE IF EXISTS reserves;");
        db.execSQL("DROP TABLE IF EXISTS Payment;");
        onCreate(db);
    }

    public void insertCustomer(String email_id ,int customer_id, String username ,String password ){
        db = this.getWritableDatabase();
        String insertcustomer = "INSERT INTO Customer values('"+email_id+"','"+customer_id+"','"+username+"','"+password+"');";
        db.execSQL(insertcustomer);
        Log.d(DATABASE_NAME,"Successfully inserted customer");
    }

    public void insertRoomtype(String type_name,String description,int room_price,int total_rooms,int type_id,int capacity,int no_of_rooms_available){
        db = this.getWritableDatabase();
        String insertroomtype = "INSERT INTO Room_Type values('"+type_name+"','"+description+"','"+room_price+"','"+total_rooms+"','"+type_id+"','"+capacity+"','"+no_of_rooms_available+"');";
        db.execSQL(insertroomtype);
    }

    public void insertRoom(int room_number,int type_id){
        db = this.getWritableDatabase();
        String insertroom = "INSERT INTO Room values('"+room_number+"','"+type_id+"');";
        db.execSQL(insertroom);
    }

    public void insertReserves(int reservation_id,String arrival_date,String departure_date,int no_of_guests,String status,int room_number,int customer_id){
        db = this.getWritableDatabase();
        String insertreserves = "INSERT INTO reserves values("+reservation_id+",'"+arrival_date+"','"+departure_date+"',"+no_of_guests+",'"+status+"',"+room_number+","+customer_id+");";
        Log.d("insertreserves_query",insertreserves);
        db.execSQL(insertreserves);
    }

    public int availableroom(int roomtype,String checkindate,String checkoutdate){
        db = this.getReadableDatabase();
        String query = "SELECT room_number from Room where room_number not in (select room_number from reserves NATURAL JOIN Room WHERE reserves.status not LIKE 'cancelled' and '"+checkoutdate+"'>= reserves.arrival_date and '"+checkindate+"'<= reserves.departure_date) AND type_id = "+roomtype+";";
        Log.d("searchuser_query",query);
        Cursor cursor = db.rawQuery(query,null);
        cursor.moveToFirst();
        return Integer.valueOf(cursor.getString(0));
    }

    public int searchuser(String email){
        db = this.getReadableDatabase();
        String query = "SELECT * FROM Customer where email_id = '"+email+"';";
        Log.d("searchuser_query",query);
        Cursor cursor = db.rawQuery(query,null);
        if(cursor!=null && cursor.getCount()>0){
            return cursor.getCount();
        }
        return 0;
    }

    public int searchuser(String email,String password){
        db = this.getReadableDatabase();
        String query = "SELECT * FROM Customer where email_id = '"+email+"' and password = '"+password+"';";
        Log.d("searchuser_query",query);
        Cursor cursor = db.rawQuery(query,null);
        if(cursor!=null && cursor.getCount()>0){
            return cursor.getCount();
        }
        return 0;
    }

    public int numberofcolumns(String table){
        db = this.getReadableDatabase();
        String query = "Select * from "+table+";";
        Cursor cursor = db.rawQuery(query,null);
        if(cursor!=null && cursor.getCount()>0){
            return cursor.getCount();
        }
        return 0;
    }

    public int maxreservesid( ){
        db = this.getReadableDatabase();
        String query = "Select reservation_id from reserves";
        int max =0;
        Cursor c = db.rawQuery(query,null);
        if (c.moveToFirst()){
            do {
                String column1 = c.getString(0);
                if(max < Integer.valueOf(column1))
                    max = Integer.valueOf(column1);
            } while(c.moveToNext());
        }
        c.close();
        return max;
    }

    public ArrayList<DataObject> searchRoom(String checkindate,String checkoutdate){
        String query = "SELECT type_name,room_price, capacity,COUNT(room_number),Room_Type.type_id from Room_Type,(SELECT room_number, type_id from Room where room_number not in(select room_number from reserves NATURAL JOIN Room WHERE reserves.status not LIKE 'cancelled' and '"+checkoutdate+"' >= reserves.arrival_date and '"+checkindate+"' <= reserves.departure_date))AS S where Room_Type.type_id = S.type_id GROUP BY type_name, description ,room_price, capacity,Room_Type.type_id HAVING COUNT(room_number)>0;";
        Log.d("searchroom_query",query);
        db = this.getReadableDatabase();
        ArrayList<DataObject> str = new ArrayList<>();
        int index=0;
        Cursor c = db.rawQuery(query,null);
        if (c.moveToFirst()){
            do {
                String column1 = c.getString(0);
                String column2 = c.getString(1);
                String column3 = c.getString(2);
                String column4 = c.getString(3);
                String column5 = c.getString(4);
                DataObject obj = new DataObject(column1,column2,column3,column4,column5);
                str.add(index,obj);
                index=index+1;
            } while(c.moveToNext());
        }
        c.close();
        return str;
    }

    public  ArrayList<DataObject1> bookingHistory(){
        String query = "select reservation_id,arrival_date,departure_date,type_name from reserves,Room,Room_type where reserves.room_number = Room.room_number and Room.type_id = Room_type.type_id;";
        Log.d("searchroom_query",query);
        db = this.getReadableDatabase();
        ArrayList<DataObject1> str = new ArrayList<>();
        int index=0;
        Cursor c = db.rawQuery(query,null);
        if (c.moveToFirst()){
            do {
                String column1 = c.getString(0);
                String column2 = c.getString(1);
                String column3 = c.getString(2);
                String column4 = c.getString(3);
                DataObject1 obj = new DataObject1(column1,column2,column3,column4);
                str.add(index,obj);
                index=index+1;
            } while(c.moveToNext());
        }
        c.close();
        return str;
    }

    public void cancelreservation(int a){
        String query = "DELETE FROM reserves where reservation_id = "+a+";";
        db = this.getWritableDatabase();
        db.execSQL(query);
        return;
    }
}
