package de.seemoo.at_tracking_detection.database

import androidx.room.*
import androidx.room.migration.AutoMigrationSpec
import de.seemoo.at_tracking_detection.database.daos.*
import de.seemoo.at_tracking_detection.database.models.Beacon
import de.seemoo.at_tracking_detection.database.models.Feedback
import de.seemoo.at_tracking_detection.database.models.Notification
import de.seemoo.at_tracking_detection.database.models.Scan
import de.seemoo.at_tracking_detection.database.models.device.BaseDevice
import de.seemoo.at_tracking_detection.util.converter.DateTimeConverter


@Database(
    version = 9,
    entities = [BaseDevice::class, Notification::class, Beacon::class, Feedback::class, Scan::class],
    autoMigrations = [AutoMigration(from = 2, to = 3), AutoMigration(from = 3, to = 4), AutoMigration(from = 4, to = 5) , AutoMigration(from=5, to=6), AutoMigration(from=7, to=8), AutoMigration(from=8, to=9, spec = AppDatabase.RenameScanMigrationSpec::class)],
    exportSchema = true
)
@TypeConverters(Converters::class, DateTimeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun deviceDao(): DeviceDao

    abstract fun beaconDao(): BeaconDao

    abstract fun notificationDao(): NotificationDao

    abstract fun feedbackDao(): FeedbackDao

    abstract  fun scanDao(): ScanDao

    @RenameColumn(
        tableName = "scan",
        fromColumnName = "date",
        toColumnName = "endDate"
    )
    class RenameScanMigrationSpec: AutoMigrationSpec {

    }
}