package com.example.gps_apgreid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.gps_apgreid.db.MyDbManager;

public class EditActivity extends AppCompatActivity {
    private EditText edTitle, edSpeed;
    private MyDbManager myDbManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();

        myDbManager.openDb();
    }

    private void init(){
        myDbManager = new MyDbManager(this);

        edTitle = findViewById(R.id.edTitle);
        edSpeed = findViewById(R.id.edSpeed);
    }

    public void onClickSave(View view){
        String title = edTitle.getText().toString();
        String speed = edSpeed.getText().toString();

        if(title.equals("") || speed.equals("")){

            Toast.makeText(this, R.string.text_empty, Toast.LENGTH_SHORT).show();

        }
        else {

            myDbManager.insertToDb(title, speed);
            Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show();
            finish();
            myDbManager.closeDb();

        }

    }
}