package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import com.example.weatherapp.Models.ChinuData;
import com.example.weatherapp.Models.Weather;
import com.example.weatherapp.Models.main;
import com.example.weatherapp.databinding.ActivityMainBinding;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
public class
MainActivity extends AppCompatActivity {
    ArrayList<Weather> list;
    ActivityMainBinding binding;
    ConstraintLayout constraintLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        constraintLayout = findViewById(R.id.constraint_layout);
        list = new ArrayList<>();
        SimpleDateFormat format = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            format = new SimpleDateFormat("dd MMMM YYYY");
        }
        String currentDate = format.format(new Date());
        binding.date.setText(currentDate);
        requestPermissions();
        fetchweather("Bhubaneswar");
        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeybaord();
                if (TextUtils.isEmpty(binding.searchCityEdittext.getText())){
                    binding.searchCityEdittext.setError("Enter your City Name");
                    return;
                }
                String CITY_NAME =binding.searchCityEdittext.getText().toString().trim();
                fetchweather(CITY_NAME);
                binding.searchCityEdittext.setText("");
            }
        });

    }
    private void hideKeybaord() {
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(constraintLayout.getApplicationWindowToken(),0);
    }

    private void requestPermissions() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                        if (multiplePermissionsReport.areAllPermissionsGranted()) {
                            Toast.makeText(MainActivity.this, "All the permissions are granted..", Toast.LENGTH_SHORT).show();
                        }
                        if (multiplePermissionsReport.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

                    }
                }).withErrorListener(error -> {
                    Toast.makeText(getApplicationContext(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                })
                .onSameThread().check();
    }
    void fetchweather(String city){

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        InterfaceAPI interfaceAPI = retrofit.create(InterfaceAPI.class);
        Call<ChinuData>  call =    interfaceAPI.getData(city,"22d54f87e2e5cb3d7ab766ec38d720a8","metric");
        call.enqueue(new Callback<ChinuData>() {
            @Override
            public void onResponse(Call<ChinuData> call, Response<ChinuData> response) {
                if (response.isSuccessful()){
                    ChinuData mausamData = response.body();
                    Log.d("Th",Thread.currentThread().getName()+" ID" +" "+ Thread.currentThread().getId());
                    main  to = mausamData.getMain();
                    binding.mainTempValue.setText((to.getTemp()) + " \u2103");
                    binding.cityName.setText(mausamData.getName());
                    binding.maxTempValue.setText(String.valueOf(to.getTemp_max()) + " \u2103");
                    binding.minTempValue.setText(String.valueOf(to.getTemp_min()) + " \u2103");
                    binding.pressreValue.setText(String.valueOf(to.getPressure())+"Pa");
                    binding.humidityValue.setText(String.valueOf(to.getHumidity())+"g/m");
                    Log.d("MV",to.getFeels_like()+" " +to.getHumidity()+" "+to.getPressure()+" "+to.getTemp());
                    list = (ArrayList<Weather>) mausamData.getWeather();
                    for( Weather  weather : list ){
                        Log.d("MV2",weather.getId()+" "+weather.getMain()+" "+weather.getDescription());
                        binding.description.setText(weather.getDescription());
                    }



                }
            }

            @Override
            public void onFailure(Call<ChinuData> call, Throwable t) {
                Toast.makeText(MainActivity.this, t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }


    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", (dialog, which) -> {
            dialog.cancel();
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, 101);
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> {
            dialog.cancel();
        });
        builder.show();
    }





}