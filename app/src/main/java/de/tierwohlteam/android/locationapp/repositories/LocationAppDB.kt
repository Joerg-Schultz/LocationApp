package de.tierwohlteam.android.locationapp.repositories

import androidx.room.Database
import androidx.room.RoomDatabase
import de.tierwohlteam.android.locationapp.models.Location
import de.tierwohlteam.android.locationapp.repositories.daos.LocationDao

@Database(
    entities = [
        Location::class
    ],
    version = 1,
    exportSchema = true
)

abstract class LocationAppDB : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}