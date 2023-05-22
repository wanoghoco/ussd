import 'dart:async';
import 'package:ussd_serv/ussd.dart';

final StreamController<String> successEvent =
    StreamController<String>.broadcast();
final StreamController<String> failsureEvent =
    StreamController<String>.broadcast();
Stream successStream = successEvent.stream;
Stream failsureStream = failsureEvent.stream;

Ussd globalUssdServices = Ussd(onFailed: (val) {
  failsureEvent.add(val);
}, onSuccess: (val) {
  successEvent.add(val);
});
