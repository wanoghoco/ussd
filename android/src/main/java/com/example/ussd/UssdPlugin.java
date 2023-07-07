package com.example.ussd;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        checkAppPermission();
      }
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



  public String cleaner(CharSequence data){
    String response=data.toString();
    List<String> value= Arrays.asList(response.split(""));
    for(int x=0; x<value.size(); x++){
     if(value.get(x).equals("Ä")){
       value.set(x,"[");
       continue;
     }
     if(value.get(x).equals("Ň")){
       value.set(x,"]");
       continue;
     }
      if(value.get(x).equals("ä")){
        value.set(x,"{");
        continue;
      }
      if(value.get(x).equals("ñ")){
        value.set(x,"}");
        continue;
      }

    }
    String dataValue=String.join("",value);
    return dataValue;
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
      if(!containSim()){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("data",  "your network type is not supported...");
        channel.invokeMethod("failed",hashMap);
        return;
        }
       if(isAirplaneModeOn(context)){
         HashMap<String, Object> hashMap = new HashMap<>();
         hashMap.put("data",  "network not detected... please try again...");
         channel.invokeMethod("failed",hashMap);
         return;
       }
    telephonyManager.sendUssdRequest(shortCode, new TelephonyManager.UssdResponseCallback() {
      @Override
      public void onReceiveUssdResponse(TelephonyManager telephonyManager, String request, CharSequence response) {
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("data",  cleaner(response));
       channel.invokeMethod("success",hashMap);
      }

      @Override
      public void onReceiveUssdResponseFailed(TelephonyManager telephonyManager, String request, int failureCode) {
         HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("data",  "transaction failed....");
        channel.invokeMethod("failed",hashMap);
      }
    },handler);
  }


  @RequiresApi(api = Build.VERSION_CODES.N)
  public boolean containSim(){
    SubscriptionManager subscriptionManager = SubscriptionManager.from(binding.getActivity());
    List<SubscriptionInfo> subscriptionInfoList = subscriptionManager.getActiveSubscriptionInfoList();
    if(subscriptionInfoList.size()<=0) return false;
    return true;
  }

  @Override
  public boolean onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if(requestCode==1111&&grantResults.length>0){
      for(int x=0; x<grantResults.length; x++){
        if(grantResults[x]!=PackageManager.PERMISSION_GRANTED){
          //Toast.makeText(context,"Permission Not Yet Granted.",Toast.LENGTH_LONG).show();
          return false;
        }
      }
    }
    if(requestCode==1111&&grantResults.length==0){
      return false;
    }
   if(requestCode==1111){
     init();
   }
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

  @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
  public static boolean isAirplaneModeOn(Context context) {
    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    if (connectivityManager != null) {
      int airplaneMode = Settings.Global.getInt(context.getContentResolver(), Settings.Global.AIRPLANE_MODE_ON, 0);
      return airplaneMode != 0;
    }
    return false;
  }

  @RequiresApi(api = Build.VERSION_CODES.M)
  void checkAppPermission(){
    int per1=binding.getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE);
    int per2=binding.getActivity().checkSelfPermission(Manifest.permission.READ_PHONE_STATE);
    int per3=binding.getActivity().checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE);
    if(per1!= PackageManager.PERMISSION_GRANTED||per2!=PackageManager.PERMISSION_GRANTED){
      binding.getActivity().requestPermissions(new String[]{Manifest.permission.CALL_PHONE,Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_NETWORK_STATE},1111);
      return;
    }
    init();
    return;
  }
}
