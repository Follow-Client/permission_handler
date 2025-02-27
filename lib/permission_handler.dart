import 'package:flutter/services.dart';
export 'package:permission_handler/permissions.dart';

class PermissionHandler {
  static const MethodChannel _channel = MethodChannel(
    'com.example.followclient/permission_handler',
  );

  Future<bool> requestPermission(String permission) async {
    try {
      final bool granted = await _channel.invokeMethod(
        'requestPermission',
        permission,
      );
      return granted;
    } on PlatformException catch (e) {
      print("Failed to request permission: '${e.message}'.");
      return false;
    }
  }

  /// Check if the specific permission is granted.
  Future<bool> checkPermissionStatus(String permission) async {
    try {
      final bool granted = await _channel.invokeMethod(
        'checkPermissionStatus',
        permission,
      );
      return granted;
    } on PlatformException catch (e) {
      print("Failed to check permission status: '${e.message}'.");
      return false;
    }
  }
}
