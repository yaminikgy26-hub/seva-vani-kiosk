import 'package:flutter/material.dart';
import 'dart:async';
import 'security_service.dart';
import 'firebase_service.dart';

void main() => runApp(const SevaVaniApp());

class SevaVaniApp extends StatelessWidget {
  const SevaVaniApp({Key? key}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Seva Vani Kiosk',
      theme: ThemeData(primarySwatch: Colors.blue),
      home: const KioskHomeScreen(),
    );
  }
}

class KioskHomeScreen extends StatefulWidget {
  const KioskHomeScreen({Key? key}) : super(key: key);

  @override
  _KioskHomeScreenState createState() => _KioskHomeScreenState();
}

class _KioskHomeScreenState extends State<KioskHomeScreen> {
  Timer? _sessionTimer;
  final SecurityService _securityService = SecurityService();
  final FirebaseService _dbService = FirebaseService();

  @override
  void initState() {
    super.initState();
    _startSessionTimer();
  }

  void _startSessionTimer() {
    _sessionTimer?.cancel();
    _sessionTimer = Timer(const Duration(seconds: 60), () {
      _securityService.wipeSessionData();
      _resetKiosk();
    });
  }

  void _resetKiosk() {
    setState(() {});
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text("Session wiped for your privacy.")),
    );
  }

  void _handleUserQuery(String query, String language) async {
    _startSessionTimer();
    String encryptedData = _securityService.encryptData(query);
    await _dbService.logTransaction(encryptedData, language);
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Seva Vani - Public Utility Kiosk')),
      body: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.mic, size: 100, color: Colors.blue),
            const SizedBox(height: 20),
            const Text("Listening in 5 Regional Languages..."),
            ElevatedButton(
              onPressed: () => _handleUserQuery("Find nearest hospital", "Tamil"),
              child: const Text("Simulate Voice Query"),
            )
          ],
        ),
      ),
    );
  }
}
