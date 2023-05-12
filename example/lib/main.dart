import 'package:flutter/material.dart';
import 'dart:async';

import 'package:ussd/ussd.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String qriosRespnose = "";
  late Ussd _ussdPlugin;

  Future<void> initiatePurchase() async {
    qriosRespnose = await _ussdPlugin.makePurchase("*556#") ?? "";
    setState(() {});
  }

  @override
  void initState() {
    _ussdPlugin = Ussd(onFailed: (val) {
      qriosRespnose = val;
      setState(() {});
    }, onSuccess: (val) {
      qriosRespnose = val;
      setState(() {});
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
          appBar: AppBar(
            title: const Text('Plugin example app'),
          ),
          body: SizedBox(
            width: double.infinity,
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Text(qriosRespnose),
                const SizedBox(
                  height: 24,
                ),
                ElevatedButton(
                    onPressed: () {
                      initiatePurchase();
                    },
                    child: const Text("Start Purchase"))
              ],
            ),
          )),
    );
  }
}
