package de.seemoo.at_tracking_detection.notifications

import android.app.Notification
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.core.app.NotificationCompat
import dagger.hilt.android.qualifiers.ApplicationContext
import de.seemoo.at_tracking_detection.ATTrackingDetectionApplication
import de.seemoo.at_tracking_detection.R
import de.seemoo.at_tracking_detection.database.models.device.BaseDevice
import de.seemoo.at_tracking_detection.database.models.device.DeviceType
import de.seemoo.at_tracking_detection.ui.MainActivity
import de.seemoo.at_tracking_detection.ui.TrackingNotificationActivity
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationBuilder @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private fun pendingNotificationIntent(bundle: Bundle): PendingIntent {
        val intent = Intent(context, TrackingNotificationActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            action = NotificationConstants.CLICKED_ACTION
            putExtras(bundle)
        }
        val context = ATTrackingDetectionApplication.getCurrentActivity() ?: ATTrackingDetectionApplication.getAppContext()
        val resultPendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(intent)
            var flags = PendingIntent.FLAG_UPDATE_CURRENT
            // For S+ the FLAG_IMMUTABLE or FLAG_MUTABLE must be set
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                flags = flags or PendingIntent.FLAG_IMMUTABLE
            }
            getPendingIntent(0, flags)
        }
        return resultPendingIntent

    }


    private fun packBundle(deviceAddress: String, notificationId: Int): Bundle = Bundle().apply {
        putString("deviceAddress", deviceAddress)
        putInt("notificationId", notificationId)
    }

    private fun buildPendingIntent(
        bundle: Bundle,
        notificationAction: String,
        code: Int
    ): PendingIntent {
        val intent = Intent(context, NotificationActionReceiver::class.java).apply {
            action = notificationAction
            putExtras(bundle)
        }
        return PendingIntent.getBroadcast(
            context,
            code,
            intent,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
        )
    }

    fun buildTrackingNotification(
        deviceAddress: String,
        notificationId: Int
    ): Notification {
        Timber.d("Notification with id $notificationId for device $deviceAddress has been build!")
        val bundle: Bundle = packBundle(deviceAddress, notificationId)
        val notifyText = context.getString(R.string.notification_text_base)
        return NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setContentTitle(context.getString(R.string.notification_title_base))
            .setContentText(notifyText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingNotificationIntent(bundle))
            .setCategory(Notification.CATEGORY_ALARM)
            .setSmallIcon(R.drawable.ic_warning)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notifyText))
            .addAction(
                R.drawable.ic_warning,
                context.getString(R.string.notification_false_alarm),
                buildPendingIntent(
                    bundle,
                    NotificationConstants.FALSE_ALARM_ACTION,
                    NotificationConstants.FALSE_ALARM_CODE
                )
            ).addAction(
                R.drawable.ic_warning,
                context.getString(R.string.notification_ignore_device),
                buildPendingIntent(
                    bundle,
                    NotificationConstants.IGNORE_DEVICE_ACTION,
                    NotificationConstants.IGNORE_DEVICE_CODE
                )
            ).setDeleteIntent(
                buildPendingIntent(
                    bundle,
                    NotificationConstants.DISMISSED_ACTION,
                    NotificationConstants.DISMISSED_CODE
                )
            ).setAutoCancel(true).build()

    }

    fun buildTrackingNotification(baseDevice: BaseDevice, notificationId: Int): Notification {
        Timber.d("Notification with id $notificationId for device ${baseDevice.address} has been build!")
        val deviceAddress = baseDevice.address

        val bundle: Bundle = packBundle(deviceAddress, notificationId)
        val device = baseDevice.device
        val notificationText: String
        val notificationTitle: String
        when (baseDevice.deviceType ?: DeviceType.UNKNOWN) {
            DeviceType.AIRTAG, DeviceType.APPLE, DeviceType.AIRPODS, DeviceType.UNKNOWN -> {
                notificationTitle = context.getString(R.string.notification_title_vocal, device.deviceContext.defaultDeviceName )
                if (baseDevice.deviceType == DeviceType.AIRPODS) {
                    notificationText =  context.getString(R.string.notification_text_multiple, device.deviceContext.defaultDeviceName)
                }else {
                    notificationText =  context.getString(R.string.notification_text_single, device.deviceContext.defaultDeviceName)
                }
            }
            else -> {
                notificationTitle = context.getString(R.string.notification_title_vocal, device.deviceContext.defaultDeviceName )
                notificationText =  context.getString(R.string.notification_text_single, device.deviceContext.defaultDeviceName)
            }
        }

        return NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setContentTitle(notificationTitle)
            .setContentText(notificationText)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingNotificationIntent(bundle))
            .setCategory(Notification.CATEGORY_ALARM)
            .setSmallIcon(R.drawable.ic_warning)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notificationText))
            .addAction(
                R.drawable.ic_warning,
                context.getString(R.string.notification_false_alarm),
                buildPendingIntent(
                    bundle,
                    NotificationConstants.FALSE_ALARM_ACTION,
                    NotificationConstants.FALSE_ALARM_CODE
                )
            ).addAction(
                R.drawable.ic_warning,
                context.getString(R.string.notification_ignore_device),
                buildPendingIntent(
                    bundle,
                    NotificationConstants.IGNORE_DEVICE_ACTION,
                    NotificationConstants.IGNORE_DEVICE_CODE
                )
            ).setDeleteIntent(
                buildPendingIntent(
                    bundle,
                    NotificationConstants.DISMISSED_ACTION,
                    NotificationConstants.DISMISSED_CODE
                )
            ).setAutoCancel(true).build()
    }

    fun buildBluetoothErrorNotification(): Notification {
        val notificationId = -100
        val bundle: Bundle = Bundle().apply { putInt("notificationId", notificationId) }
        return NotificationCompat.Builder(context, NotificationConstants.CHANNEL_ID)
            .setContentTitle(context.getString(R.string.notification_title_ble_error))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingNotificationIntent(bundle))
            .setCategory(Notification.CATEGORY_ERROR)
            .setSmallIcon(R.drawable.ic_scan_icon)
            .setAutoCancel(true)
            .build()
    }
}