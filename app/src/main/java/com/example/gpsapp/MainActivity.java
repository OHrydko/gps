package com.example.gpsapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.example.gpsapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private MainViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityMainBinding binding = DataBindingUtil.setContentView(this,
                R.layout.activity_main);
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        viewModel.init(this,this);
        binding.setLifecycleOwner(this);
        binding.setViewModel(viewModel);
        binding.getRoot();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        viewModel.onRequestPermissionsResult(requestCode, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
