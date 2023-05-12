package com.example.ussd;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.HashMap;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;

/** UssdPlugin */
public class UssdPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware, PluginRegistry.RequestPermissionsResultListener {

  private MethodChannel channel;
  private Context context;
  ActivityPluginBinding binding;
  TelephonyManager telephonyManager;
  Handler handler = new Handler();

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "sendcheap_ussd");

    channel.setMethodCallHandler(this);
  }


  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("make_purchase")) {
     String shortCode= call.argument("short_code");
      if(telephonyManager==null){
        result.success("permission not accepted");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("data",  "permission not accepted");
        channel.invokeMethod("failed",hashMap);
        return;
      }
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        makePurchase(shortCode);
      }
      else{
        result.success("not supported");
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("data",  "your device does not support this feature");
        channel.invokeMethod("failed",hashMap);
      }
    } else {
      result.success("not implemented");
      HashMap<String, Object> hashMap = new HashMap<>();
      hashMap.put("data",  "this method has not been implemented");
      channel.invokeMethod("failed",hashMap);
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  private void init(){
    telephonyManager= (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
  }


  @RequiresApi(api = Build.VERSION_CODES.O)
  private void makePurchase(String shortCode){
    String networkOperatorName = telephonyManager.getNetworkOperatorName();
    System.out.println(networkOperatorName);
    telephonyManager.sendUssdRequest(shortCode, new TelephonyManager.UssdResponseCallback() {
      @Override
      public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("data",  response);
       channel.invokeMethod("success",hashMap);
      }

      @Override
      public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
        System.out.println(request);       HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("data",  "transaction failed....");
        channel.invokeMethod("failed",hashMap);
      }
    },handler);
  }

  @Override
  public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if(requestCode==011&&grantResults.length>0){
      for(int x=0; x<grantResults.length; x++){
        if(grantResults[x]!=PackageManager.PERMISSION_GRANTED){
          Toast.makeText(context,"Permission Not Yet Granted.",Toast.LENGTH_LONG).show();
          return false;
        }
      }
    }
    init();
    return true;
  }



  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    this.binding=binding;
    binding.addRequestPermissionsResultListener(this);
    context=binding.getActivity().getApplicationContext();
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      checkAppPermission();
    }

  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {

  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {

  }

  @Override
  public void onDetachedFromActivity() {

  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  void checkAppPermission(){
    int per1=binding.getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE);
    int per2=binding.getActivity().checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
    if(per1!= PackageManager.PERMISSION_GRANTED||per2!=PackageManager.PERMISSION_GRANTED){
      binding.getActivity().requestPermissions(new String[]{Manifest.permission.CALL_PHONE,Manifest.permission.READ_PHONE_STATE},011);
      return;
    }
    init();
    return;
  }
}
