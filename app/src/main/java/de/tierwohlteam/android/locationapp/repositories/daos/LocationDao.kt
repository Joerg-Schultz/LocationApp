package de.tierwohlteam.android.locationapp.repositories.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import de.tierwohlteam.android.locationapp.models.Location

@Dao
interface CoordinateDao {
    @Insert
    suspend fun insert(coordinate: Location)

    @Query("SELECT * FROM coordinates ORDER BY timestamp DESC")
    suspend fun getAll(): List<Location>
}