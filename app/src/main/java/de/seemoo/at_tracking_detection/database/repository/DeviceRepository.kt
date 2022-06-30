package de.seemoo.at_tracking_detection.database.repository

import androidx.annotation.WorkerThread
import de.seemoo.at_tracking_detection.database.daos.DeviceDao
import de.seemoo.at_tracking_detection.database.relations.DeviceBeaconNotification
import de.seemoo.at_tracking_detection.database.models.device.BaseDevice
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

@WorkerThread
class DeviceRepository @Inject constructor(private val deviceDao: DeviceDao) {
    val devices: Flow<List<BaseDevice>> = deviceDao.getAll()

    fun trackingDevicesSince(since: LocalDateTime) = deviceDao.getAllNotificationSince(since)

    fun trackingDevicesSinceFlow(since: LocalDateTime) = deviceDao.getAllNotificationSinceFlow(since)

    fun trackingDevicesSinceCount(since: LocalDateTime) = deviceDao.trackingDevicesCount(since)

    val totalCount: Flow<Int> = deviceDao.getTotalCount()

    fun totalDeviceCountChange(since: LocalDateTime): Flow<Int> =
        deviceDao.getTotalCountChange(since)

    fun devicesCurrentlyMonitored(since: LocalDateTime): Flow<Int> =
        deviceDao.getCurrentlyMonitored(since)

    fun deviceCountSince(since: LocalDateTime): Flow<Int> =
        deviceDao.getCurrentlyMonitored(since)

    val ignoredDevices: Flow<List<BaseDevice>> = deviceDao.getIgnored()

    val ignoredDevicesSync: List<BaseDevice> = deviceDao.getIgnoredSync()

    fun getDevice(deviceAddress: String): BaseDevice? = deviceDao.getByAddress(deviceAddress)

    @WorkerThread
    suspend fun getDeviceBeaconsSince(dateTime: String?): List<DeviceBeaconNotification> {
        return if (dateTime != null) {
            deviceDao.getDeviceBeaconsSince(LocalDateTime.parse(dateTime))
        } else {
            deviceDao.getDeviceBeacons()
        }
    }

    suspend fun getDeviceBeaconsSinceDate(dateTime: LocalDateTime?): List<DeviceBeaconNotification> {
        return if (dateTime != null) {
            deviceDao.getDeviceBeaconsSince(dateTime)
        } else {
            deviceDao.getDeviceBeacons()
        }
    }

    @WorkerThread
    suspend fun insert(baseDevice: BaseDevice) {
        deviceDao.insert(baseDevice)
    }

    @WorkerThread
    suspend fun update(baseDevice: BaseDevice) {
        deviceDao.update(baseDevice)
    }

    @WorkerThread
    suspend fun setIgnoreFlag(deviceAddress: String, state: Boolean) {
        deviceDao.setIgnoreFlag(deviceAddress, state)
    }
}