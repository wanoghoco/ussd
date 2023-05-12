import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'ussd_platform_interface.dart';

/// An implementation of [UssdPlatform] that uses method channels.
class MethodChannelUssd extends UssdPlatform {
  @visibleForTesting
  final methodChannel = const MethodChannel('sendcheap_ussd');
  final Function(String) onSuccess;
  final Function(String) onFailed;
  MethodChannelUssd({required this.onSuccess, required this.onFailed}) {
    methodChannel.setMethodCallHandler((call) async {
      if (call.method == "failed") {
        onFailed(call.arguments['data']);
      }
      if (call.method == "success") {
        onSuccess(call.arguments['data']);
      }
    });
  }

  @override
  Future<String?> makePurchase(String shortCode) async {
    final qriosResponse = await methodChannel
        .invokeMethod<String>('make_purchase', {"short_code": shortCode});
    return qriosResponse;
  }
}
