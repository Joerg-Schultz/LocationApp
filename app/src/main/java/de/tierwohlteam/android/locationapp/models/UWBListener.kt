package de.tierwohlteam.android.locationapp.models

import android.os.Message
import android.util.Log
import de.tierwohlteam.android.locationapp.repositories.LocationAppRepository
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject

class UWBListener @Inject constructor(private val repository: LocationAppRepository) : BTTool() {
    override suspend fun toolReadAction(msg: Message) {
        val msgText = msg.obj.toString()

        Log.d("ESP32",msgText)
        val jsonMessage: BTMessage = Json.decodeFromString(msgText)
        val currentTime = System.currentTimeMillis()
        val newLocation = Location(x=jsonMessage.x, y=jsonMessage.y, timestamp = currentTime)
        repository.insertLocation(newLocation)
    }
}