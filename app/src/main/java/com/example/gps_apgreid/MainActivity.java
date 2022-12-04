package com.example.gps_apgreid;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.gps_apgreid.adapter.MainAdapter;
import com.example.gps_apgreid.db.MyDbManager;

import java.time.LocalTime;

public class MainActivity extends AppCompatActivity implements LocListenerInterface, LocListenerInterfaceChet, View.OnClickListener {

    private MyDbManager myDbManager;
    private RecyclerView rcView;
    private MainAdapter mainAdapter;

    private TextView tvTime;
    private TextView tvSpeed;
    private TextView tvS;
    private float minute;
    private TextView tvTest;
    private TextView tvTestSpeed;

    private TextView tvDistance;
    private TextView tvDistanceRest;
    private TextView tvVelocity;
    private TextView tvStart;
    private TextView tvFinish;

    // Проба тормозов
    private TextView proba1;

    private Location lastLocation;
    private Location lastLocationChet;
    private LocationManager locationManager;
    private LocationManager locationManagerChet;
    private MyLocListener myLocListener;
    private MyLocListenerChet myLocListenerChet;
    private int distanceChet;
    private int distanceNechet;
    private int final_distance;
    private int total_distance = 0;
    private int total_distanceStart = 6904000;
    private int rest_distance = 7208000;
    private int distanceProba1;
    private ProgressBar pb;

    private MediaPlayer probatormozov, probatormozov2, voice15, voice25, voice40, voice50, voice55, voice60, voice65, voice70, voice75, voiceprev;

//    private TextView tvText;

    Button btnAdd, btnRead, btnClear;
    EditText etName;

    DBHelper dbHelper;

    private LocalTime time;
    private float speed1;
    private float distanceSpeedTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    private void init()
    {
        myDbManager = new MyDbManager(this);
        rcView = findViewById(R.id.rcView);
        mainAdapter = new MainAdapter(this);
        rcView.setLayoutManager(new LinearLayoutManager(this));
        getItemTouchHelper().attachToRecyclerView(rcView);
        rcView.setAdapter(mainAdapter);

        tvTest = findViewById(R.id.tvTest);
        tvTestSpeed = findViewById(R.id.tvTestSpeed);

//        btnAdd = (Button) findViewById(R.id.btnAdd);
//        btnAdd.setOnClickListener(this);

//        btnRead = (Button) findViewById(R.id.btnRead);
//        btnRead.setOnClickListener(this);

//        btnClear = findViewById(R.id.btnClear);
//        btnClear.setOnClickListener(this);

//        etName = (EditText) findViewById(R.id.etName);

        dbHelper = new DBHelper(this);

//        SQLiteDatabase database = dbHelper.getWritableDatabase();
//
//        List<Integer> tempList = new ArrayList<>();
//
//        Cursor cursor = database.query(dbHelper.TABLE_CONTACTS, null, null, null, null, null, null);
//
//        while (cursor.moveToNext()){
//            int title = cursor.getInt(Integer.parseInt(String.valueOf(cursor.getColumnIndex(dbHelper.KEY_NAME))));
//            tempList.add(title);
//            tvText.append(String.valueOf(title));
//            tvText.append("\n");
//        }
//        cursor.close();
//        database.close();


        tvTime = findViewById(R.id.tvTime);
        minute = minute;

        probatormozov = MediaPlayer.create(this, R.raw.probatormozov);
        probatormozov2 = MediaPlayer.create(this, R.raw.probatormozov2);
        voice15 = MediaPlayer.create(this, R.raw.voice15);
        voice25 = MediaPlayer.create(this, R.raw.voice25);
        voice40 = MediaPlayer.create(this, R.raw.voice40);
        voice50 = MediaPlayer.create(this, R.raw.voice50);
        voice55 = MediaPlayer.create(this, R.raw.voice55);
        voice60 = MediaPlayer.create(this, R.raw.voice60);
        voice65 = MediaPlayer.create(this, R.raw.voice65);
        voice70 = MediaPlayer.create(this, R.raw.voice70);
        voice75 = MediaPlayer.create(this, R.raw.voice75);
        voiceprev = MediaPlayer.create(this, R.raw.voiceprev);

        tvS = findViewById(R.id.tvS);
        tvSpeed = findViewById(R.id.tvSpeed);
        tvVelocity = findViewById(R.id.tvVelocity);
        tvDistance = findViewById(R.id.tvDistance);
        tvDistanceRest = findViewById(R.id.tvDistanceRest);
        tvStart = findViewById(R.id.tvStart);
        tvFinish = findViewById(R.id.tvFinish);

        // Проба тормозов
        proba1 = findViewById(R.id.tvProba1);

        total_distance = total_distance;
        total_distanceStart = total_distanceStart;
        rest_distance = rest_distance;
        final_distance = final_distance;
        distanceChet = distanceChet;
        distanceNechet = distanceNechet;

        distanceProba1 = distanceProba1;

        pb = findViewById(R.id.progressBar);
        pb.setMax(0);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManagerChet = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        myLocListener = new MyLocListener();
        myLocListener.setLocListenerInterface(this);

        myLocListenerChet = new MyLocListenerChet();
        myLocListenerChet.setLocListenerInterfaceChet(this);

        checkPermissions();
    }
    //123

    private void setDistanceStart(String disStart)
    {
        tvStart.setText(disStart);
        total_distance = Integer.parseInt(disStart);
        tvDistance.setText(disStart);
        pb.setMax(rest_distance - total_distance);
    }

    private void setDistanceFinish(String disFinish)
    {
        tvFinish.setText(disFinish);
        final_distance = Integer.parseInt(disFinish);
        tvDistanceRest.setText(disFinish);
        pb.setMax(final_distance - total_distanceStart);
    }

    private void setDisProba1(String Proba)
    {
        proba1.setText(Proba);
        distanceProba1 = Integer.parseInt(Proba);
    }

    private <ConstrainLayout> void showDialogStart()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_start);
        ConstrainLayout cl = (ConstrainLayout) getLayoutInflater().inflate(R.layout.dialog_layout_start,null);
        builder.setPositiveButton(R.string.dialog_button_start, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog ads = (AlertDialog) dialog;
                EditText eds = ads.findViewById(R.id.edStart);
                if(eds != null){
                    if(!eds.getText().toString().equals(""))setDistanceStart(eds.getText().toString());
                }
            }
        });
        builder.setView((View) cl);
        builder.show();
    }

    public void onClickDistanceStart(View view)
    {
        showDialogStart();
    }

    private <ConstrainLayout> void showDialogFinish()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_finish);
        ConstrainLayout cl = (ConstrainLayout) getLayoutInflater().inflate(R.layout.dialog_layout_finish,null);
        builder.setPositiveButton(R.string.dialog_button_finish, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog adf = (AlertDialog) dialog;
                EditText edf = adf.findViewById(R.id.edFinish);
                if(edf != null){
                    if(!edf.getText().toString().equals(""))setDistanceFinish(edf.getText().toString());
                }
            }
        });
        builder.setView((View) cl);
        builder.show();
    }

    public void onClickDistanceFinish(View view)
    {
        showDialogFinish();
    }


    // Проба тормозов
    private <ConstrainLayout> void showDialogProba1()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.dialog_title_proba1);
        ConstrainLayout cl = (ConstrainLayout) getLayoutInflater().inflate(R.layout.dialog_layout_proba1,null);
        builder.setPositiveButton(R.string.dialog_button_proba1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                AlertDialog ado2 = (AlertDialog) dialog;
                EditText edo2 = ado2.findViewById(R.id.edProba1);
                if(edo2 != null){
                    if(!edo2.getText().toString().equals(""))setDisProba1(edo2.getText().toString());
                }
            }
        });
        builder.setView((View) cl);
        builder.show();
    }

    public void onClickProba1(View view)
    {
        showDialogProba1();
    }
    // Конец пробы тормозов

    // Обновление для четных поездов

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateDistanceChet(Location locChet)
    {


        if(locChet.hasSpeed() && lastLocationChet != null)
        {

            time = LocalTime.now();
            minute = time.getSecond();
            speed1 = (locChet.getSpeed() / 1000) * 3600;
            distanceSpeedTime = speed1 * minute;
            distanceSpeedTime = Math.round(distanceSpeedTime);

            distanceChet = rest_distance - total_distance;

            if (total_distance != 0 && (Math.round((locChet.getSpeed() / 1000) * 3600)) >= 1)
            {
                if (total_distance < rest_distance)
                {
                    total_distance += lastLocationChet.distanceTo(locChet) + 0.45;
                    pb.setProgress(distanceChet);


                    // Расчет ограничений для четного направления

                    SQLiteDatabase database = dbHelper.getWritableDatabase();

                    Cursor cursor = database.query(dbHelper.TABLE_CONTACTS, null, null, null, null, null, null);

                    while (cursor.moveToNext()){
                        int title = cursor.getInt(Integer.parseInt(String.valueOf(cursor.getColumnIndex(DBHelper.KEY_NAME))));
                        int speed = cursor.getInt(Integer.parseInt(String.valueOf(cursor.getColumnIndex(DBHelper.KEY_SPEED))));

                        if (Math.round(locChet.getSpeed() / 1000 * 3600) >= speed && total_distance >= title - 1400 && total_distance <= title - 1250 && speed == 15){
                            soundPlay(voiceprev);
                        }

                        if (total_distance >= title - 3000 && total_distance <= title - 2820 && speed == 15){
                            soundPlay(voice15);
                            tvTest.setText(String.valueOf(title));
                            tvTestSpeed.setText(String.valueOf(speed));
                        }

                            if (Math.round(locChet.getSpeed() / 1000 * 3600) >= speed && total_distance >= title - 1400 && total_distance <= title - 1250 && speed == 25){
                                soundPlay(voiceprev);
                            }

                            if (total_distance >= title - 3000 && total_distance <= title - 2820 && speed == 25){
                                soundPlay(voice25);
                                tvTest.setText(String.valueOf(title));
                                tvTestSpeed.setText(String.valueOf(speed));
                            }

                                if (Math.round(locChet.getSpeed() / 1000 * 3600) >= speed && total_distance >= title - 1400 && total_distance <= title - 1250 && speed == 40){
                                    soundPlay(voiceprev);
                                }

                                if (total_distance >= title - 3000 && total_distance <= title - 2820 && speed == 40){
                                    soundPlay(voice40);
                                    tvTest.setText(String.valueOf(title));
                                    tvTestSpeed.setText(String.valueOf(speed));
                                }

                        if (Math.round(locChet.getSpeed() / 1000 * 3600) >= speed && total_distance >= title - 1400 && total_distance <= title - 1250 && speed == 50){
                            soundPlay(voiceprev);
                        }

                        if (total_distance >= title - 3000 && total_distance <= title - 2820 && speed == 50){
                            soundPlay(voice50);
                            tvTest.setText(String.valueOf(title));
                            tvTestSpeed.setText(String.valueOf(speed));
                        }

                            if (Math.round(locChet.getSpeed() / 1000 * 3600) >= speed && total_distance >= title - 1400 && total_distance <= title - 1250 && speed == 55){
                                soundPlay(voiceprev);
                            }

                            if (total_distance >= title - 3000 && total_distance <= title - 2820 && speed == 55){
                                soundPlay(voice55);
                                tvTest.setText(String.valueOf(title));
                                tvTestSpeed.setText(String.valueOf(speed));
                            }

                                if (Math.round(locChet.getSpeed() / 1000 * 3600) >= speed && total_distance >= title - 1400 && total_distance <= title - 1250 && speed == 60){
                                    soundPlay(voiceprev);
                                }

                                if (total_distance >= title - 3000 && total_distance <= title - 2820 && speed == 60){
                                    soundPlay(voice60);
                                    tvTest.setText(String.valueOf(title));
                                    tvTestSpeed.setText(String.valueOf(speed));
                                }

                        if (Math.round(locChet.getSpeed() / 1000 * 3600) >= speed && total_distance >= title - 1400 && total_distance <= title - 1250 && speed == 65){
                            soundPlay(voiceprev);
                        }

                        if (total_distance >= title - 3000 && total_distance <= title - 2820 && speed == 65){
                            soundPlay(voice65);
                            tvTest.setText(String.valueOf(title));
                            tvTestSpeed.setText(String.valueOf(speed));
                        }

                            if (Math.round(locChet.getSpeed() / 1000 * 3600) >= speed && total_distance >= title - 1400 && total_distance <= title - 1250 && speed == 70){
                                soundPlay(voiceprev);
                            }

                            if (total_distance >= title - 3000 && total_distance <= title - 2820 && speed == 70){
                                soundPlay(voice70);
                                tvTest.setText(String.valueOf(title));
                                tvTestSpeed.setText(String.valueOf(speed));
                            }

                                if (Math.round(locChet.getSpeed() / 1000 * 3600) >= speed && total_distance >= title - 1400 && total_distance <= title - 1250 && speed == 75){
                                    soundPlay(voiceprev);
                                }

                                if (total_distance >= title - 3000 && total_distance <= title - 2820 && speed == 75){
                                    soundPlay(voice75);
                                    tvTest.setText(String.valueOf(title));
                                    tvTestSpeed.setText(String.valueOf(speed));
                                }
                    }
                    cursor.close();
                    database.close();
                }

            }

            // Оповещение для четных поездов о пробе тормозов за 5 км
            if (total_distance >= distanceProba1 - 6000 && total_distance <= distanceProba1 - 5900)
            {
                soundPlay(probatormozov);
            }

            // Оповещение для четных поездов о пробе тормозов за 2 км
            if (total_distance >= distanceProba1 - 3000 && total_distance <= distanceProba1 - 2820)
            {
                soundPlay(probatormozov2);
            }

        }

        lastLocationChet = locChet;
        tvDistance.setText(String.valueOf(total_distance));
        tvVelocity.setText(String.valueOf(Math.round((locChet.getSpeed() / 1000) * 3600)));

        tvSpeed.setText((String.valueOf(speed1)));
        tvTime.setText(String.valueOf(time));
        tvS.setText(String.valueOf(distanceSpeedTime));

    }

    // Обновление для нечетных поездов

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void updateDistance(Location loc)
    {


        if(loc.hasSpeed() && lastLocation != null)
        {
            time = LocalTime.now();
            minute = time.getSecond();
            speed1 = (loc.getSpeed() / 1000) * 3600;
            distanceSpeedTime = speed1 * minute;
            distanceSpeedTime = Math.round(distanceSpeedTime);

            distanceNechet = final_distance - total_distanceStart;

            if ((Math.round((loc.getSpeed() / 1000) * 3600)) >= 1)
            {
                if (final_distance > total_distanceStart)
                {
                    final_distance -= lastLocation.distanceTo(loc) - 0.55;
                    pb.setProgress(distanceNechet);

                    // Расчет ограничений для нечётного направления

                    SQLiteDatabase database = dbHelper.getWritableDatabase();

                    Cursor cursor = database.query(dbHelper.TABLE_CONTACTS, null, null, null, null, null, null);

                    while (cursor.moveToNext()){
                        int title = cursor.getInt(Integer.parseInt(String.valueOf(cursor.getColumnIndex(DBHelper.KEY_NAME))));
                        int speed = cursor.getInt(Integer.parseInt(String.valueOf(cursor.getColumnIndex(DBHelper.KEY_SPEED))));

                        if (Math.round(loc.getSpeed() / 1000 * 3600) >= speed && final_distance <= title - 600 && final_distance >= title - 750 && speed == 15){
                            soundPlay(voiceprev);
                        }

                        if (final_distance <= title + 1000 && final_distance >= title + 820 && speed == 15){
                            soundPlay(voice15);
                            tvTest.setText(String.valueOf(title));
                            tvTestSpeed.setText(String.valueOf(speed));
                        }

                                if (Math.round(loc.getSpeed() / 1000 * 3600) >= speed && final_distance <= title - 600 && final_distance >= title - 750 && speed == 25){
                                    soundPlay(voiceprev);
                                }

                                if (final_distance <= title + 1000 && final_distance >= title + 820 && speed == 25){
                                    soundPlay(voice25);
                                    tvTest.setText(String.valueOf(title));
                                    tvTestSpeed.setText(String.valueOf(speed));
                                }

                                    if (Math.round(loc.getSpeed() / 1000 * 3600) >= speed && final_distance <= title - 600 && final_distance >= title - 750 && speed == 40){
                                        soundPlay(voiceprev);
                                    }

                                    if (final_distance <= title + 1000 && final_distance >= title + 820 && speed == 40){
                                        soundPlay(voice40);
                                        tvTest.setText(String.valueOf(title));
                                        tvTestSpeed.setText(String.valueOf(speed));
                                    }

                        if (Math.round(loc.getSpeed() / 1000 * 3600) >= speed && final_distance <= title - 600 && final_distance >= title - 750 && speed == 50){
                            soundPlay(voiceprev);
                        }

                        if (final_distance <= title + 1000 && final_distance >= title + 820 && speed == 50){
                            soundPlay(voice50);
                            tvTest.setText(String.valueOf(title));
                            tvTestSpeed.setText(String.valueOf(speed));
                        }

                                if (Math.round(loc.getSpeed() / 1000 * 3600) >= speed && final_distance <= title - 600 && final_distance >= title - 750 && speed == 55){
                                    soundPlay(voiceprev);
                                }

                                if (final_distance <= title + 1000 && final_distance >= title + 820 && speed == 55){
                                    soundPlay(voice55);
                                    tvTest.setText(String.valueOf(title));
                                    tvTestSpeed.setText(String.valueOf(speed));
                                }

                                    if (Math.round(loc.getSpeed() / 1000 * 3600) >= speed && final_distance <= title - 600 && final_distance >= title - 750 && speed == 60){
                                        soundPlay(voiceprev);
                                    }

                                    if (final_distance <= title + 1000 && final_distance >= title + 820 && speed == 60){
                                        soundPlay(voice60);
                                        tvTest.setText(String.valueOf(title));
                                        tvTestSpeed.setText(String.valueOf(speed));
                                    }

                        if (Math.round(loc.getSpeed() / 1000 * 3600) >= speed && final_distance <= title - 600 && final_distance >= title - 750 && speed == 65){
                            soundPlay(voiceprev);
                        }

                        if (final_distance <= title + 1000 && final_distance >= title + 820 && speed == 65){
                            soundPlay(voice65);
                            tvTest.setText(String.valueOf(title));
                            tvTestSpeed.setText(String.valueOf(speed));
                        }

                                if (Math.round(loc.getSpeed() / 1000 * 3600) >= speed && final_distance <= title - 600 && final_distance >= title - 750 && speed == 70){
                                    soundPlay(voiceprev);
                                }

                                if (final_distance <= title + 1000 && final_distance >= title + 820 && speed == 70){
                                    soundPlay(voice70);
                                    tvTest.setText(String.valueOf(title));
                                    tvTestSpeed.setText(String.valueOf(speed));
                                }

                                    if (Math.round(loc.getSpeed() / 1000 * 3600) >= speed && final_distance <= title - 600 && final_distance >= title - 750 && speed == 75){
                                        soundPlay(voiceprev);
                                    }

                                    if (final_distance <= title + 1000 && final_distance >= title + 820 && speed == 75){
                                        soundPlay(voice75);
                                        tvTest.setText(String.valueOf(title));
                                        tvTestSpeed.setText(String.valueOf(speed));
                                    }
                    }
                    cursor.close();
                    database.close();
                }
            }

            // Оповещение для нечётных поездов о пробе тормозов за 5 км
            if (final_distance <= distanceProba1 + 4000 && final_distance >= distanceProba1 + 3900)
            {
                soundPlay(probatormozov);
            }

            // Оповещение для нечётных поездов о пробе тормозов за 2 км
            if (final_distance <= distanceProba1 + 1000 && final_distance >= distanceProba1 + 820)
            {
                soundPlay(probatormozov2);
            }

        }

        lastLocation = loc;
        tvDistanceRest.setText(String.valueOf(final_distance));
        tvVelocity.setText(String.valueOf(Math.round((loc.getSpeed() / 1000) * 3600)));

        tvSpeed.setText((String.valueOf(speed1)));
        tvTime.setText(String.valueOf(time));
        tvS.setText(String.valueOf(distanceSpeedTime));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 101 && grantResults[0] == RESULT_OK)
        {
            checkPermissions();
        }
    }

    private void checkPermissions()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            int requestCode = 101;
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, requestCode);
        }
        else
        {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 1, myLocListener);
            locationManagerChet.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2, 1, myLocListenerChet);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void OnLocationChanged(Location loc) {
        updateDistance(loc);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void OnLocationChangedChet(Location locChet) {
        updateDistanceChet(locChet);
    }

    public void soundPlay(MediaPlayer sound)
    {
        sound.start();
    }


    @Override
    protected void onResume() {
        super.onResume();

        myDbManager.openDb();
        mainAdapter.updateAdapter(myDbManager.getFromDb());
    }


    public void onClickAdd(View view){
        Intent i = new Intent(MainActivity.this, EditActivity.class);
        startActivity(i);
    }

    protected void onDestroy() {

        super.onDestroy();
        myDbManager.closeDb();
    }

    private ItemTouchHelper getItemTouchHelper(){
        return new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                mainAdapter.removeItem(viewHolder.getAdapterPosition(), myDbManager);
            }
        });
    }


    @Override
    public void onClick(View v) {
//        String name = etName.getText().toString();

//        SQLiteDatabase database = dbHelper.getWritableDatabase();

//        ContentValues contentValues = new ContentValues();

        switch (v.getId()) {

//            case R.id.btnAdd:
//                etName.setText("");
//
//                contentValues.put(DBHelper.KEY_NAME, name);
//
//                database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);
////                tvText.append(name);
////                tvText.append("\n");
//                break;

//            case R.id.btnRead:
//
//                tvText.setText("");
//
//                Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
//
//                if (cursor.moveToNext()) {
//                    int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
//                    int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
//                    do {
//
//                        Log.d("mLog", "ID = " + cursor.getInt(idIndex) +
//                                ", name = " + cursor.getString(nameIndex));
//
//                        tvText.append(cursor.getString(nameIndex));
//                        tvText.append("\n");
//
////                        {
////                            if (Integer.parseInt(cursor.getString(nameIndex)) == primary){
////                                saveInt = Integer.parseInt(cursor.getString(nameIndex));
////                                if (saveInt == primary){
////                                    soundPlay(voice);
////                                    Log.d("mLog", "Всё ОК!!!");
////                                }
////
////                                Log.d("mLog", "Ура у меня все получилось");
////                            } else
////                                Log.d("mLog", "Опять все по новой");
////                        }
//
//                    } while (cursor.moveToNext());
//
//                } else
//                    Log.d("mLog", "0 rows");
//
//                cursor.close();
//                break;

//            case R.id.btnClear:
//                database.delete(DBHelper.TABLE_CONTACTS, null, null);
//                break;
        }
//        dbHelper.close();
    }
}