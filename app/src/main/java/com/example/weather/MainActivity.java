package com.example.weather;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements LocationListener {

    protected double latitude, longitude;
    TextView txtLat, txtLong, txtData;

    LocationManager locationManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtLat = (TextView) findViewById(R.id.txtvlat);
        txtLong = (TextView) findViewById(R.id.txtvlong);
        txtData = (TextView) findViewById(R.id.Data);
        //Запрос прав на получение местоположения
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        //Проверка прав на получение местоположения
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        //Подключение и настройка слушателя
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 100, this);
        Thread thread = new Thread(new Runnable() {
        //Открытие нового потока для отправки запроса
            @Override
            public void run() {
                try  {
                    new UrlConnection().execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        latitude = location.getLatitude();
        longitude = location.getLongitude();

        txtLong.setText(String.valueOf(longitude));
        txtLat.setText(String.valueOf((latitude)));


        //Остановка обновлений
        locationManager.removeUpdates(this);
    }

    public class UrlConnection
    {
        HttpURLConnection connection = null;
        BufferedReader bR = null;
        public void execute(){
            try{
                URL url = new URL("https://api.weather.yandex.ru/v2/forecast?lat=37&lon=-122");
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                con.setRequestProperty("X-Yandex-API-Key", "b08cbb39-99b8-4b4b-a4d9-298ca3f291a8");

                int status = con.getResponseCode();
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuffer content = new StringBuffer();
                while((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }
                in.close();
                con.disconnect();
                Log.d("App", content.toString());
                txtData.setText(String.valueOf(content));
                System.out.println(content.toString());

            } catch (Exception e){
                Log.d("App", e.toString());
                System.out.println("Error");
            }
            Log.d("App", "Finih");

        }
    }
}

