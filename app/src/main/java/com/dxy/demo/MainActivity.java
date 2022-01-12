package com.dxy.demo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mmkv.MMKV;

import cn.dxy.permission.SmartPermission;
import cn.dxy.permission.Permission;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MMKV.initialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        findViewById(R.id.tv_request).setOnClickListener(v -> new SmartPermission.Builder().permissions(Permission.CAMERA, Permission.RECORD_AUDIO)
                .intercept(new AspirinPermissionIntercept()).granted(() ->showShort("权限被允许")).denied(() -> showShort("权限被禁止")).build().request());
    }

    private void showShort(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }
}