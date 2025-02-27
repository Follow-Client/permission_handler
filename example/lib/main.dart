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
