package com.example.cruddemoapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;


import android.database.Cursor;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    DbHelper dbHelper;
    EditText editId, editName, editPhone, editAddress;
    Button btnSave, btnGetAll, btnGet, btnDelete;
    Spinner spClass;
    ArrayAdapter<String> adapterClass ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DbHelper(this);
        findAllView();
        fillClassList();
        saveOrUpdate();
        getAll();
        get();
        delete();
    }

    private void findAllView(){
        editId = findViewById(R.id.et_id);
        editName =  findViewById(R.id.et_name);
        editPhone =  findViewById(R.id.et_phone);
        editAddress = findViewById(R.id.et_address);
        btnSave =  findViewById(R.id.bt_add_student);
        btnGetAll = findViewById(R.id.bt_get_all);
        btnGet = findViewById(R.id.bt_get_student);
        btnDelete = findViewById(R.id.bt_delete_student);
        spClass = findViewById(R.id.sp_class);
    }

    private void fillClassList(){
        Cursor cursor = dbHelper.getClasses();
        List<String> list = new ArrayList<>();
        while (cursor.moveToNext()){
            list.add(cursor.getString(1));
        }

        adapterClass = new ArrayAdapter<String >(this, android.R.layout.simple_spinner_item, list);
        adapterClass.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spClass.setAdapter(adapterClass);
    }


    public void saveOrUpdate(){
        btnSave.setOnClickListener(view -> {
            boolean result = dbHelper.saveOrUpdate(editId.getText().toString(), editName.getText().toString(),editPhone.getText().toString()
                    , editAddress.getText().toString(), spClass.getSelectedItem().toString());
            if(result){
                Toast.makeText(MainActivity.this,"Saved",Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(MainActivity.this,"Not saved",Toast.LENGTH_LONG).show();

            }
        });
    }


    public void getAll(){
        btnGetAll.setOnClickListener(view -> {
            Cursor cursor = dbHelper.getAll();
            if(cursor.getCount()==0){
                showMessage("Error","Nothing found!");
                return;
            }
            StringBuilder buffer = new StringBuilder();
            while (cursor.moveToNext()){
                buffer.append("Id: ").append(cursor.getString(0)).append("\n");
                buffer.append("Name: ").append(cursor.getString(1)).append("\n");
                buffer.append("Phone: ").append(cursor.getString(2)).append("\n");
                buffer.append("Address: ").append(cursor.getString(3)).append("\n");
                buffer.append("Class: ").append(cursor.getString(4)).append("\n");

            }
            showMessage("Data",buffer.toString());

        });
    }

    public void get(){
        btnGet.setOnClickListener(view -> {
            Cursor cursor = dbHelper.get(editId.getText().toString());
            if(cursor.getCount()==0){
                showMessage("Error","Nothing found!");
                return;
            }
            cursor.moveToNext();
            editName.setText(cursor.getString(1));
            editPhone.setText(cursor.getString(2));
            editAddress.setText(cursor.getString(3));
            spClass.setSelection(adapterClass.getPosition(dbHelper.findClassNameBy(cursor.getInt(4))));
        });
    }

    public  void delete(){
        btnDelete.setOnClickListener(view ->{
            boolean result = dbHelper.delete(editId.getText().toString());
            if(result){
                Toast.makeText(MainActivity.this,"Deleted",Toast.LENGTH_LONG).show();
            }
            else Toast.makeText(MainActivity.this, "Undeleted", Toast.LENGTH_LONG).show();
        });
    }

    public void showMessage(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();

    }
}