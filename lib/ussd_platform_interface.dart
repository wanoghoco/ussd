import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'ussd_method_channel.dart';

abstract class UssdPlatform extends PlatformInterface {
  /// Constructs a UssdPlatform.
  UssdPlatform() : super(token: _token);
  static Function(String)? onSuccess;
  static Function(String)? onFailed;
  static final Object _token = Object();

  static UssdPlatform _instance =
      MethodChannelUssd(onSuccess: onSuccess!, onFailed: onFailed!);

  /// The default instance of [UssdPlatform] to use.
  ///
  /// Defaults to [MethodChannelUssd].
  static UssdPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [UssdPlatform] when
  /// they register themselves.
  static set instance(UssdPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> makePurchase(String shortCode) {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
