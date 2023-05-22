import 'package:flutter/material.dart';
import 'package:ussd_example/global.dart';

class Test extends StatefulWidget {
  const Test({super.key});

  @override
  State<Test> createState() => _TestState();
}

class _TestState extends State<Test> {
  String qriosRespnose = "";

  Future<void> initiatePurchase1() async {
    await globalUssdServices.makePurchase("*123#") ?? "";
  }

  Future<void> initiatePurchase2() async {
    await globalUssdServices.makePurchase("*123#") ?? "";
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
            ],
          ),
        ));
  }
}
