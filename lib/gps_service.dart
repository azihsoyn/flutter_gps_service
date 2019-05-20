import 'dart:async';

import 'package:flutter/services.dart';

class GpsService {
  static const MethodChannel _channel =
      const MethodChannel('gps_service');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
