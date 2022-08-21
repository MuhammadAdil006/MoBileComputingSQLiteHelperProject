package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    Button buttonAdd, buttonViewAll,update;
    EditText editName, editRollNumber;
    Switch switchIsActive;
    ListView listViewStudent;
    Boolean modifyMood;
    StudentModel Pre;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        modifyMood=false;
        Pre= new StudentModel();
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonViewAll = findViewById(R.id.buttonViewAll);
        editName = findViewById(R.id.editTextName);
        editRollNumber = findViewById(R.id.editTextRollNumber);
        switchIsActive = findViewById(R.id.switchStudent);
        listViewStudent = findViewById(R.id.listViewStudent);
        update =findViewById(R.id.update);
        update.setVisibility(View.GONE);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            StudentModel studentModel;

            @Override
            public void onClick(View v) {
                try {
                    studentModel = new StudentModel(editName.getText().toString(), Integer.parseInt(editRollNumber.getText().toString()), switchIsActive.isChecked());
                    //Toast.makeText(MainActivity.this, studentModel.toString(), Toast.LENGTH_SHORT).show();
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
                DBHelper dbHelper  = new DBHelper(MainActivity.this);
                dbHelper.addStudent(studentModel);
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                StudentModel cur=new StudentModel();
                cur.setRollNmber(Integer.parseInt(String.valueOf(editRollNumber.getText())));
                cur.setEnroll(switchIsActive.isChecked());
                cur.setName(editName.getText().toString());
                DBHelper dbHelper  = new DBHelper(MainActivity.this);
                dbHelper.update(cur,Pre);
                buttonViewAll.performClick();

                modifyMood=false;
                update.setVisibility(View.GONE);

            }
        });
        buttonViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBHelper dbHelper = new DBHelper(MainActivity.this);
                List<StudentModel> list = dbHelper.getAllStudents();
                ArrayAdapter arrayAdapter = new ArrayAdapter<StudentModel>
                        (MainActivity.this, android.R.layout.simple_list_item_1,list);
                listViewStudent.setAdapter(arrayAdapter);

            }
        });

        listViewStudent.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                new AlertDialog.Builder(MainActivity.this).setTitle("Modifying Records").setMessage("Do You want update or delete?")
                        .setCancelable(false).setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int n) {
                                    //get the student model
                                DBHelper dbHelper  = new DBHelper(MainActivity.this);
                                List<StudentModel> list = dbHelper.getAllStudents();
                                StudentModel s= list.get(i);

                                //remove this student from db
                                dbHelper.DeleteStudent(s);

                                 list = dbHelper.getAllStudents();
                                ArrayAdapter arrayAdapter = new ArrayAdapter<StudentModel>
                                        (MainActivity.this, android.R.layout.simple_list_item_1,list);
                                listViewStudent.setAdapter(arrayAdapter);
                            }
                        }).setNegativeButton("Update", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int n) {
                                modifyMood=true;
                                dialogInterface.dismiss();
                                DBHelper dbHelper  = new DBHelper(MainActivity.this);
                                List<StudentModel> list = dbHelper.getAllStudents();
                                StudentModel s= list.get(i);
                                //fill the entry with this student model

                                editName.setText(s.getName());
                                editRollNumber.setText(String.valueOf(s.getRollNmber()));
                                if(s.isEnroll())
                                {
                                    switchIsActive.setChecked(true);
                                }
                                update.setVisibility(View.VISIBLE);
                                Pre.setEnroll(s.isEnroll());
                                Pre.setName(s.getName());
                                Pre.setRollNmber(s.getRollNmber());
                            }
                        }).show();

            }
        });

    }
}