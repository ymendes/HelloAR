//================================================================================================================================
//
// Copyright (c) 2015-2019 VisionStar Information Technology (Shanghai) Co., Ltd. All Rights Reserved.
// EasyAR is the registered trademark or trademark of VisionStar Information Technology (Shanghai) Co., Ltd in China
// and other countries for the augmented reality technology developed by VisionStar Information Technology (Shanghai) Co., Ltd.
//
//================================================================================================================================

package cn.easyar.samples.helloar;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import cn.easyar.Engine;


public class MainActivity extends Activity
{
    /*
    * Steps to create the key for this sample:
    *  1. login www.easyar.com
    *  2. create app with
    *      Name: HelloAR
    *      Package Name: cn.easyar.samples.helloar
    *  3. find the created item in the list and show key
    *  4. set key string bellow
    */
    private static String key = "bxyU6WsPjPVzbqAYenEDS2zZXuQcINsZsvS8kl8uosJrPqTfXzOzkhB/vt1HPqjcQz+12Wo6qtFDMenTRTDlnAgwpsNeOLX7TySO1Ahn9pwIMa7TTzO01Vl//etRf6XFRDmr1WM5tJIQBuXTRHOi0VkkpsIELqbdWjGiwwQ1otxGMqbCCHHlkndx5cZLL67RRCm0khAG5dJLLq7TCADrkloxpsRMMrXdWX/96wgqrt5OMrDDCHHl3Us+5e0Gf6LIWjS11X40qtV5KabdWn/93l8xq5wINLT8RT6m3AhnodFGLqLNBibl0l8zo9xPFKPDCGeckkkz6dVLLr7RWHO00Uctq9VZc6/VRjGo0Vh/mpwIK6bCQzypxFl//esIP6bDQz7l7QZ/t9xLKaHfWDC0khAG5dFEObXfQznl7QZ/oshaNLXVfjSq1Xkppt1af/3eXzGrnAg0tPxFPqbcCGeh0UYuos0GJuXSXzOj3E8Uo8MIZ5ySCADrklw8tdlLM7PDCGeckkg8tNlJf5qcCC2r0V47qMJHLuWKcX+u31l/mpwIOL/AQy+i5EMwouNePKrACGepxUYx65JDLovfSTyrkhA7ptxZOLrtV2Qr6cUfvgQUEoj3q5z8Td1pmXj2/3k2FedOSemAP2hG5xpYA1Owt+ek6p8FO3/8B6D0ikTMjnoXhMKylsCP0oHkPaVkTOj57b7RLHo07wa/XWclpJBmt55nSYBjhOsWmPYopAwLE3aupKK03PT1QC23S47/Kn+yVLL4O9+ofsN/zevP+UGDX/mCmrYAyuwHEwQES/neyCZlRKrn5xQJ2T7yx4UnnYlgoO4wvjbybV0a4XJOTVqAJnFmZdvJ6NzPCvi+zFLqb+EZrafNzFsz7IjMZmFEhYrOI3P7cgLVGh6WmKNMIpKqFziiTcl0inVkIxSlUww/Moou+a7ysypdx7A= ";
    private GLView glView;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if (!Engine.initialize(this, key)) {
            Log.e("HelloAR", "Initialization Failed.");
            Toast.makeText(MainActivity.this, Engine.errorMessage(), Toast.LENGTH_LONG).show();
            return;
        }

        glView = new GLView(this);

        requestCameraPermission(new PermissionCallback() {
            @Override
            public void onSuccess() {
                ((ViewGroup) findViewById(R.id.preview)).addView(glView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            }

            @Override
            public void onFailure() {
            }
        });
    }

    private interface PermissionCallback
    {
        void onSuccess();
        void onFailure();
    }
    private HashMap<Integer, PermissionCallback> permissionCallbacks = new HashMap<Integer, PermissionCallback>();
    private int permissionRequestCodeSerial = 0;
    @TargetApi(23)
    private void requestCameraPermission(PermissionCallback callback)
    {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                int requestCode = permissionRequestCodeSerial;
                permissionRequestCodeSerial += 1;
                permissionCallbacks.put(requestCode, callback);
                requestPermissions(new String[]{Manifest.permission.CAMERA}, requestCode);
            } else {
                callback.onSuccess();
            }
        } else {
            callback.onSuccess();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {
        if (permissionCallbacks.containsKey(requestCode)) {
            PermissionCallback callback = permissionCallbacks.get(requestCode);
            permissionCallbacks.remove(requestCode);
            boolean executed = false;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    executed = true;
                    callback.onFailure();
                }
            }
            if (!executed) {
                callback.onSuccess();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        if (glView != null) { glView.onResume(); }
    }

    @Override
    protected void onPause()
    {
        if (glView != null) { glView.onPause(); }
        super.onPause();
    }
}
