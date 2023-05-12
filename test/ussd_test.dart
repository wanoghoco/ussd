// import 'package:flutter_test/flutter_test.dart';
// import 'package:ussd/ussd.dart';
// import 'package:ussd/ussd_platform_interface.dart';
// import 'package:ussd/ussd_method_channel.dart';
// import 'package:plugin_platform_interface/plugin_platform_interface.dart';

// class MockUssdPlatform
//     with MockPlatformInterfaceMixin
//     implements UssdPlatform {

//   @override
//   Future<String?> getPlatformVersion() => Future.value('42');
// }

// void main() {
//   final UssdPlatform initialPlatform = UssdPlatform.instance;

//   test('$MethodChannelUssd is the default instance', () {
//     expect(initialPlatform, isInstanceOf<MethodChannelUssd>());
//   });

//   test('getPlatformVersion', () async {
//     Ussd ussdPlugin = Ussd();
//     MockUssdPlatform fakePlatform = MockUssdPlatform();
//     UssdPlatform.instance = fakePlatform;

//     expect(await ussdPlugin.getPlatformVersion(), '42');
//   });
// }
