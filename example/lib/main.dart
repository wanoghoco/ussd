import 'package:flutter/material.dart';
import 'package:ussd_example/global.dart';
import 'package:ussd_example/test.dart';
import 'dart:async';

void main() {
  runApp(const App());
}

class App extends StatefulWidget {
  const App({super.key});

  @override
  State<App> createState() => _AppState();
}

class _AppState extends State<App> {
  @override
  Widget build(BuildContext context) {
    return const MaterialApp(
      home: MyApp(),
    );
  }
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String qriosRespnose = "";

  Future<void> initiatePurchase1() async {
    await globalUssdServices.makePurchase(
            "*425*0320*57552554245942*711972439052*70175954323*100*1*7*1#") ??
        "";
  }

  Future<void> initiatePurchase2() async {
    await globalUssdServices.makePurchase(
            "*425*0138*57552554245942*711972439052*70175954323*100000000000000000*1*7*1#") ??
        "";
  }

  @override
  void initState() {
    successStream.listen((event) {
      if (!mounted) {
        return;
      }
      qriosRespnose = event;
      setState(() {});
    });
    failsureStream.listen((event) {
      if (!mounted) {
        return;
      }
      qriosRespnose = event;
      setState(() {});
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
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
                    qriosRespnose = "";
                    setState(() {});

                    initiatePurchase1();
                  },
                  child: const Text("Start Purchase 1")),
              const SizedBox(
                height: 44,
              ),
              Text(qriosRespnose),
              const SizedBox(
                height: 24,
              ),
              ElevatedButton(
                  onPressed: () {
                    qriosRespnose = "";
                    setState(() {});

                    initiatePurchase2();
                  },
                  child: const Text("Start Purchase 2")),
              const SizedBox(
                height: 24,
              ),
              ElevatedButton(
                  onPressed: () {
                    Navigator.of(context).push(
                        MaterialPageRoute(builder: (context) => const Test()));
                  },
                  child: const Text("Start Purchase 2"))
            ],
          ),
        ));
  }
}
