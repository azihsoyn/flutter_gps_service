package com.azihsoyn.gps_service

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry.Registrar

class GpsServicePlugin : MethodCallHandler, EventChannel.StreamHandler {
    private var locationService: LocationService? = null

    companion object {
        private lateinit var activity: Activity
        private lateinit var context: Context
        private val REQUEST_PERMISSION = 1000

        @JvmStatic
        fun registerWith(registrar: Registrar) {
            Log.v("hoge", "called registerWith")
            val channel = MethodChannel(registrar.messenger(), "gps_service")
            val plugin = GpsServicePlugin()
            activity = registrar.activity()
            context = registrar.context()

            channel.setMethodCallHandler(plugin)

            val eventChannel = EventChannel(registrar.messenger(), "gps_service/events")
            eventChannel.setStreamHandler(plugin)

            val serviceConnection = object : ServiceConnection {
                override fun onServiceConnected(className: ComponentName, service: IBinder) {
                    Log.v("hoge", "onServiceConnected")
                    val name = className.className

                    if (name.endsWith("LocationService")) {
                        plugin.locationService = (service as LocationService.LocationServiceBinder).service

                        plugin.locationService?.startUpdatingLocation()
                    }
                }

                override fun onServiceDisconnected(className: ComponentName) {
                    Log.v("hoge", "onServiceDisconnected")
                    if (className.className == "LocationService") {
                        plugin.locationService?.stopUpdatingLocation()
                        plugin.locationService = null
                    }
                }
            }

            val locationServiceIntent = Intent(registrar.activity().application, LocationService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.v("hoge", "version >= OREO")
                registrar.activity().application.startForegroundService(locationServiceIntent)
            } else {
                Log.v("hoge", "version < OREO")
                registrar.activity().application.startService(locationServiceIntent)
            }
            registrar.activity().application.bindService(locationServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)

            if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    == PackageManager.PERMISSION_GRANTED
            ) {
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_PERMISSION
                    )
                } else {
                    val toast = Toast.makeText(
                            context,
                            "許可されないとアプリが実行できません", Toast.LENGTH_SHORT
                    )
                    toast.show()

                    ActivityCompat.requestPermissions(
                            activity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            REQUEST_PERMISSION
                    )

                }
            }

            if(plugin.locationService == null){
                Log.v("hoge", "locationService is null")
            }
            plugin.locationService?.startLogging()
        }
    }

    override fun onMethodCall(call: MethodCall, result: Result) {
        Log.v("hoge", "called onMedhodCall")
        Log.v("hoge", call.method)
        if (call.method == "getPlatformVersion") {
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else if (call.method == "startService") {
            Log.v("hoge", "hoge")
            result.success("Android ${android.os.Build.VERSION.RELEASE}")
        } else {
            result.notImplemented()
        }
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
        /*
        locationClient.registerLocationUpdatesCallback { result ->
          events.success(Codec.encodeResult(result))
        }
        */
    }

    override fun onCancel(p0: Any?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
