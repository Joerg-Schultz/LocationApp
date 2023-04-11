package de.tierwohlteam.android.locationapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "coordinates")
data class Coordinate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val x: Double,
    val y: Double,
    val timestamp: Long
)