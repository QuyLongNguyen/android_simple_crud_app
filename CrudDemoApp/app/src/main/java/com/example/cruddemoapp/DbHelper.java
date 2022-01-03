package com.example.cruddemoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;



public class DbHelper extends SQLiteOpenHelper {
    private final static String DB_NAME = "student_management.db";
    private final static String TABLE_STUDENT = "student";
    private final static String COL_1_1 = "ID";
    private final static String COL_1_2 = "Name";
    private final static String COL_1_3 = "Phone";
    private final static String COL_1_4 = "Address";
    private final static String COL_1_5 = "ClassID";

    private final static String TABLE_CLASS = "class";
    private final static String COL_2_1 = "ID";
    private final static String COL_2_2 = "Name";

    /*
    Update value when you change
    private static int DB_CURRENT_VERSION = 4;
    */
    private static int DB_REQUEST_VERSION = 4;

    public DbHelper(Context context) {

        super(context, DB_NAME, null, DB_REQUEST_VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String sql = "create table " + TABLE_STUDENT + "(ID integer primary key autoincrement,Name text,Phone text)";
        sqLiteDatabase.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int DB_CURRENT_VERSION, int DB_REQUEST_VERSION) {
        Log.i("Old version(u): ",""+ DB_CURRENT_VERSION);
        Log.i("New version:  ",""+ DB_REQUEST_VERSION);
        if(DB_CURRENT_VERSION < 2){
            sqLiteDatabase.execSQL("alter table "+ TABLE_STUDENT + " add column Address text");
        }
        if(DB_CURRENT_VERSION < 3){
            sqLiteDatabase.execSQL("create table "+ TABLE_CLASS + "(ID integer primary key autoincrement,Name text)");

        }
        if (DB_CURRENT_VERSION < 4){
            sqLiteDatabase.execSQL("Create table student_temp(ID integer primary key autoincrement,Name text,Phone text,Address text" +
                    ",ClassID integer references class(ID))");
            sqLiteDatabase.execSQL("insert into student_temp(ID,Name,Phone) select ID,Name,Phone from student");
            sqLiteDatabase.execSQL("drop table student");
            sqLiteDatabase.execSQL("alter table student_temp rename to student");
        }

    }

    @Override
    public void onDowngrade(SQLiteDatabase sqLiteDatabase, int DB_CURRENT_VERSION, int DB_REQUEST_VERSION) {
        Log.i("Old version(d): ",""+ DB_CURRENT_VERSION);
        Log.i("New version:  ",""+ DB_REQUEST_VERSION);
        if(DB_REQUEST_VERSION < 4){
            sqLiteDatabase.setVersion(3);
        }
    }

    public Cursor getClasses(){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("select * from " + TABLE_CLASS,null);
    }

    public int findClassIdBy(String name){
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + TABLE_CLASS + " where name = ? ",new String[]{name});
        cursor.moveToNext();
        return cursor.getInt(0);
    }
    public String findClassNameBy(int id){
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor cursor= database.rawQuery("select * from " + TABLE_CLASS + " where id = ? ",new String[]{String.valueOf(id)});
        cursor.moveToNext();
        return cursor.getString(1);
    }

    public boolean saveOrUpdate(String id,String name, String phone,String address,String className){
        SQLiteDatabase database = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_1_2,name);
        contentValues.put(COL_1_3,phone);
        contentValues.put(COL_1_4,address);
        contentValues.put(COL_1_5,findClassIdBy(className));

        Cursor cursor = get(id);
        if(cursor.getCount()==0){
            long result = database.insert(TABLE_STUDENT,null,contentValues);
            return result != -1;

        }
        else {
            contentValues.put(COL_1_1,id);
            database.update(TABLE_STUDENT,contentValues,"id = ?",new String[]{id});
            return true;
        }
    }

    public Cursor getAll(){
        SQLiteDatabase database = this.getReadableDatabase();
        return database.rawQuery("select * from "+ TABLE_STUDENT,null );
    }

    public Cursor get(String id){
        SQLiteDatabase database = this.getWritableDatabase();
        return database.rawQuery("select * from "+ TABLE_STUDENT + " where ID = ?",new String[]{id});
    }

    public boolean delete(String id){
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(TABLE_STUDENT,"id = ?",new String[] {id});
        return true;
    }
}
