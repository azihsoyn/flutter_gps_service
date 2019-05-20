import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:gps_service/gps_service.dart';

void main() {
  const MethodChannel channel = MethodChannel('gps_service');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await GpsService.platformVersion, '42');
  });
}
