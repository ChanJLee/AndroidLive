package com.wenyu.ylive.biz.splash;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.Window;
import android.view.WindowManager;

import com.wenyu.ylive.R;
import com.wenyu.ylive.base.YLiveActivity;
import com.wenyu.ylive.biz.home.thiz.HomeActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class SplashActivity extends YLiveActivity {

    private static final int REQUEST_CODE_ASK_RUNTIME_PERMISSIONS = 0x0521;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 6.0 以下手机直接启动
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            startUp();
            return;
        }

        List<String> permissionsList = new ArrayList<>();
        permissionsList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissionsList.add(Manifest.permission.CAMERA);
        permissionsList.add(Manifest.permission.RECORD_AUDIO);

        final List<String> result = checkNeedToRequirePermission(permissionsList);

        // 不需要额外权限或者最基本的权限（存储）保证后，直接启动
        if (result.isEmpty()) {
            startUp();
            return;
        }

        AlertDialog dialog = new AlertDialog.Builder(this).setMessage("我们需要一些基本权限来保证App的正常运行")
                .setPositiveButton("我知道了", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(SplashActivity.this, result.toArray(new String[result.size()]), REQUEST_CODE_ASK_RUNTIME_PERMISSIONS);
                    }
                }).create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void startUp() {
        Observable.timer(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        route();
                    }
                });
    }

    private void route() {
        Intent intent = HomeActivity.newIntent(this);
        startActivity(intent);
    }

    private List<String> checkNeedToRequirePermission(List<String> permissions) {
        List<String> result = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                result.add(permission);
            }
        }

        return result;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (REQUEST_CODE_ASK_RUNTIME_PERMISSIONS == requestCode) {
            Map<String, Integer> perms = new HashMap<String, Integer>();
            perms.put(Manifest.permission.READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);
            perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
            for (int i = 0; i < permissions.length; i++) {
                perms.put(permissions[i], grantResults[i]);
            }

            // 如果获得了基本存储权限，则允许执行
            if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                startUp();
                return;
            }

            String msg = "需要存储权限来保存你的学习数据，否则将无法正常使用扇贝听力";
            AlertDialog dialog = new AlertDialog.Builder(this).setMessage(msg)
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            } catch (Exception e) {

                            }
                        }
                    }).create();
            dialog.setCanceledOnTouchOutside(false);
            dialog.show();

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected int contentId() {
        return R.layout.activity_splash;
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
