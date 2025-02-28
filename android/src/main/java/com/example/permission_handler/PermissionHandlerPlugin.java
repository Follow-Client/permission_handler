package com.example.permission_handler;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.PluginRegistry;

/** PermissionHandlerPlugin */
public class PermissionHandlerPlugin implements FlutterPlugin, MethodChannel.MethodCallHandler, ActivityAware,
        ActivityCompat.OnRequestPermissionsResultCallback, PluginRegistry.ActivityResultListener {

  private static final String CHANNEL = "com.example.followclient/permission_handler";
  private static final int OVERLAY_PERMISSION_REQ_CODE = 1234;
  private static final String SYSTEM_ALERT_WINDOW = "android.permission.SYSTEM_ALERT_WINDOW";

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
    // Special handling for SYSTEM_ALERT_WINDOW permission
    if (SYSTEM_ALERT_WINDOW.equals(permission)) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if (!Settings.canDrawOverlays(context)) {
          pendingResult = result;
          pendingPermission = permission;

          Intent intent = new Intent(
                  Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                  Uri.parse("package:" + context.getPackageName())
          );
          activity.startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
        } else {
          // Already granted
          result.success(true);
        }
      } else {
        // Permission is automatically granted on Android < M
        result.success(true);
      }
      return;
    }

    // Standard permission handling for other permissions
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
    // Special handling for SYSTEM_ALERT_WINDOW permission
    if (SYSTEM_ALERT_WINDOW.equals(permission)) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        boolean isGranted = Settings.canDrawOverlays(context);
        result.success(isGranted);
      } else {
        // Permission is automatically granted on Android < M
        result.success(true);
      }
      return;
    }

    // Standard permission check for other permissions
    boolean isGranted = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    result.success(isGranted);
  }

  // Handle standard permission result callback
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

  // Handle activity result for SYSTEM_ALERT_WINDOW permission
  @Override
  public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
    if (requestCode == OVERLAY_PERMISSION_REQ_CODE && pendingResult != null && SYSTEM_ALERT_WINDOW.equals(pendingPermission)) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        boolean granted = Settings.canDrawOverlays(context);
        pendingResult.success(granted);
        pendingResult = null;
        pendingPermission = null;
        return true;
      }
    }
    return false;
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
    binding.addActivityResultListener(this);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    activity = null;
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    activity = binding.getActivity();
    binding.addActivityResultListener(this);
  }

  @Override
  public void onDetachedFromActivity() {
    activity = null;
  }
}