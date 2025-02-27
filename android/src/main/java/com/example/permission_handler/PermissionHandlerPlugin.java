package com.example.permission_handler;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

/** PermissionHandlerPlugin */
public class PermissionHandlerPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware, ActivityCompat.OnRequestPermissionsResultCallback {
  private static final String CHANNEL = "com.example.followclient/permission_handler";
  private MethodChannel channel;
  private Context context;
  private Activity activity;
  private MethodChannel.Result pendingResult;
  private String pendingPermission;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
    context = binding.getApplicationContext();
    channel = new MethodChannel(binding.getBinaryMessenger(), CHANNEL);
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull MethodChannel.Result result) {
    if (activity == null) {
      result.error("NO_ACTIVITY", "Activity is null", null);
      return;
    }

    String permission = call.arguments.toString();

    switch (call.method) {
      case "requestPermission":
        requestPermission(permission, result);
        break;
      case "checkPermissionStatus":
        checkPermissionStatus(permission, result);
        break;
      default:
        result.notImplemented();
    }
  }

  private void requestPermission(String permission, MethodChannel.Result result) {
    if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
      result.success(true);
      return;
    }

    // Store the result and permission for callback
    pendingResult = result;
    pendingPermission = permission;

    // Request the permission
    ActivityCompat.requestPermissions(activity, new String[]{permission}, 1);
  }

  private void checkPermissionStatus(String permission, MethodChannel.Result result) {
    boolean isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    result.success(isGranted);
  }

  // Handle permission result callback
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    if (requestCode == 1 && pendingResult != null && pendingPermission != null) {
      if (permissions.length > 0 && permissions[0].equals(pendingPermission)) {
        boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;
        pendingResult.success(granted);
      } else {
        pendingResult.success(false);
      }
      // Reset pending values
      pendingResult = null;
      pendingPermission = null;
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
    binding.addRequestPermissionsResultListener((requestCode, permissions, grantResults) -> {
      onRequestPermissionsResult(requestCode, permissions, grantResults);
      return true;
    });
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
  }
}
