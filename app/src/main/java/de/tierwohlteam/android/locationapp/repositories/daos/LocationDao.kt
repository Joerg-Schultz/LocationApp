package de.tierwohlteam.android.locationapp.repositories.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import de.tierwohlteam.android.locationapp.models.Location
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Insert
    suspend fun insert(coordinate: Location)

    @Query("SELECT * FROM location ORDER BY timestamp DESC")
    fun getAll(): Flow<List<Location>>
}