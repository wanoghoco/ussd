import 'ussd_platform_interface.dart';

class Ussd {
  final Function(String) onSuccess;
  final Function(String) onFailed;

  const Ussd({required this.onSuccess, required this.onFailed});
  Future<String?> makePurchase(String shortCode) {
    UssdPlatform.onSuccess = onSuccess;
    UssdPlatform.onFailed = onFailed;
    return UssdPlatform.instance.makePurchase(shortCode);
  }
}
