package com.android.insecurebankv2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;


public class SplashActivity extends Activity {
    boolean isrooted;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showRootStatus();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if(isrooted == true){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }

    void showRootStatus() {
        isrooted = doesSuperuserApkExist("/system/app/Superuser.apk") ||
                doesSUexist();
        if (isrooted == true) {
            Toast.makeText(getApplicationContext(), "루팅된 기기 입니다.", Toast.LENGTH_LONG).show();
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        }
    }

    private boolean doesSUexist() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/bin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            if (in.readLine() != null) return true;
            return false;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    private boolean doesSuperuserApkExist(String s) {
        File rootFile = new File("/system/app/Superuser.apk");
        Boolean doesexist = rootFile.exists();
        if (doesexist == true) {
            return (true);
        } else {
            return (false);
        }
    }
}