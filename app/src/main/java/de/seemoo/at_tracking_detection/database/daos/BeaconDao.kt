package de.seemoo.at_tracking_detection.database.daos

import androidx.room.*
import de.seemoo.at_tracking_detection.database.models.Beacon
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface BeaconDao {
    @Query("SELECT * FROM beacon WHERE receivedAt >= :since")
    fun getLatestBeacons(since: LocalDateTime): List<Beacon>

    @Query("SELECT COUNT(DISTINCT(deviceAddress)) FROM beacon WHERE receivedAt >= :since")
    fun getLatestBeaconCount(since: LocalDateTime): Flow<Int>


    @Query("SELECT COUNT(*) FROM beacon WHERE receivedAt >= :since")
    fun getTotalCountChange(since: LocalDateTime): Flow<Int>

    @Query("SELECT * FROM beacon WHERE receivedAt >= :since")
    fun getBeaconsSince(since: LocalDateTime): Flow<List<Beacon>>

    @Query("SELECT COUNT(*) FROM beacon")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM beacon WHERE deviceAddress LIKE :deviceAddress")
    fun getDeviceBeaconsCount(deviceAddress: String): Int

    @Query("SELECT * FROM beacon WHERE deviceAddress LIKE :deviceAddress ORDER BY receivedAt DESC")
    fun getDeviceBeacons(deviceAddress: String): List<Beacon>

    @Query("SELECT * FROM (SELECT * FROM beacon ORDER BY receivedAt DESC, deviceAddress ASC) GROUP BY deviceAddress")
    fun getLatestBeaconPerDevice(): Flow<List<Beacon>>

    @Query("SELECT COUNT(*) FROM beacon WHERE latitude IS NOT NULL AND longitude IS NOT NULL")
    fun getTotalLocationCount(): Flow<Int>

    @Query("SELECT COUNT(DISTINCT(deviceAddress)) FROM beacon WHERE latitude IS NOT NULL AND longitude IS NOT NULL AND receivedAt >= :since")
    fun getLatestLocationsCount(since: LocalDateTime): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(beacon: Beacon): Long

    @Delete
    suspend fun delete(beacon: Beacon)
}