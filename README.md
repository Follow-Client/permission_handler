
# Permission Handler Plugin for Flutter

This Flutter plugin allows requesting and checking the status of a single permission of any category using platform channels. The plugin supports Android permissions handling through a custom native implementation.

## Features

- Request a specific permission at runtime.
- Check the current status of a permission.
- Supports platform channels for seamless communication between Flutter and native Android code.


## Installation

Add the following dependency to your pubspec.yaml file:

```bash
  dependencies:
  permission_handler:
    path : https://github.com/Follow-Client/permission_handler
```

## Usage

Import the Plugin
```dart
import 'package:permission_handler/permission_handler.dart';
```
Request Permission

```dart
// For permission android.permission.READ_CALL_LOG
Future<void> requestPermission() async {
  bool result = await PermissionHandler().requestPermission(Permissions.readCallLog);
  print("Permission granted: $result");
}
```

Check Permission Status
```dart
// For permission android.permission.READ_CALL_LOG
Future<void> checkPermission() async {
  bool result = await PermissionHandler().checkPermissionStatus(Permissions.readCallLog);
  print("Permission status: $result");
}
```


## Example
```dart
import 'package:flutter/material.dart';
import 'package:permission_handler/permission_handler.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {

  Future<void> requestPermission() async {
   bool result = await PermissionHandler().requestPermission(Permissions.readCallLog);
   print(result);
  }

  Future<void> checkPermission() async {
    bool result = await PermissionHandler().checkPermissionStatus(
      Permissions.readCallLog,
    );
    print(result);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(title: const Text('Permission Handler example')),
        body: Center(
          child: Column(
            spacing: 20,
            mainAxisAlignment: MainAxisAlignment.center,
            crossAxisAlignment: CrossAxisAlignment.center,
            children: [
              ElevatedButton(
                onPressed: requestPermission,
                child: Text('Request Permission'),
              ),
              ElevatedButton(
                onPressed: checkPermission,
                child: Text('Check Permission'),
              ),
            ],
          ),
        ),
      ),
    );
  }
}

```
## Android Integration
Add the required permissions in your AndroidManifest.xml:

```xml
<uses-permission android:name="android.permission.READ_CALL_LOG"/>
```
## License

This project is proprietary and owned by Follow Client Company. Unauthorized distribution or modification is not permitted.

