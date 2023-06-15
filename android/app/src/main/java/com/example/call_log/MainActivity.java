package com.example.call_log;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import java.util.ArrayList;
import java.util.HashMap;
import io.flutter.embedding.android.FlutterActivity;
import io.flutter.embedding.engine.FlutterEngine;
import io.flutter.plugin.common.MethodChannel;

public class MainActivity extends FlutterActivity {
    private static final String CHANNEL = "dev.crazelu/call_log_manager";

    private CallLogManager callLogManager;
    private Activity activity;
    private MethodChannel.Result result;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void configureFlutterEngine(@NonNull FlutterEngine flutterEngine) {
        super.configureFlutterEngine(flutterEngine);
        activity = getActivity();
        callLogManager = new CallLogManager(getContext());

        new MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL)
                .setMethodCallHandler(
                        (call, result) -> {
                            this.result = result;
                            switch (call.method){
                                case "getCallLog":
                                    if(!doesAppHavePermissions()){
                                        result.error("001", "Permissions not granted. Invoke `requestPermission` first!", null);
                                        break;
                                    }
                                    int page =  call.argument("page") == null ? 1 : call.argument("page");
                                    int limit =  call.argument("itemsPerPage") == null ? 10 : call.argument("itemsPerPage");

                                    ArrayList<HashMap<String, Object>> callLog = callLogManager.getCallLog(page, limit);
                                    result.success(callLog);
                                    break;

                                case "requestPermission":
                                    requestPermissions();
                                    break;

                                default:
                                    result.notImplemented();
                            }
                        }
                );
    }

    @Override
    public void detachFromFlutterEngine(){
        callLogManager.release();
        callLogManager = new CallLogManager(getContext());
        super.detachFromFlutterEngine();
    }


    @Override
    public void cleanUpFlutterEngine(@NonNull FlutterEngine flutterEngine){
        callLogManager.release();
        callLogManager = new CallLogManager(getContext());
        super.cleanUpFlutterEngine(flutterEngine);
    }

    private void requestPermissions(){
        if(!doesAppHavePermissions()){
                ActivityCompat.requestPermissions(
                        activity,
                        new String[]{ Manifest.permission.READ_CALL_LOG },
                        13
                );
        }else{
            result.success(true);
        }
    }

    private boolean doesAppHavePermissions() {
        try{
            return   ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED ;
        }catch(Exception e){
            Log.e("CallLogManager", e.toString());
            return false;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 13: {
                if (grantResults.length < 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    result.success(false);
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }else{
                    result.success(true);
                }
                break;
            }
        }
    }

}
