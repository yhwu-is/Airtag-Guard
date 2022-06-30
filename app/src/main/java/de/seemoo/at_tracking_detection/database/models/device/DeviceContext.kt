package de.seemoo.at_tracking_detection.database.models.device

import android.bluetooth.le.ScanFilter

interface DeviceContext {
    val bluetoothFilter: ScanFilter

    val deviceType: DeviceType

    val defaultDeviceName: String

    /** Minimum time the device needs to be following in seconds */
    val minTrackingTime: Int
        get() = 30 * 60

    val statusByteDeviceType: UInt
}