#import "GpsServicePlugin.h"
#import <gps_service/gps_service-Swift.h>

@implementation GpsServicePlugin
+ (void)registerWithRegistrar:(NSObject<FlutterPluginRegistrar>*)registrar {
  [SwiftGpsServicePlugin registerWithRegistrar:registrar];
}
@end
