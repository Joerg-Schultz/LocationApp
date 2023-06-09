package de.tierwohlteam.android.locationapp.models

import android.os.Message
import android.util.Log
import de.tierwohlteam.android.locationapp.repositories.LocationAppRepository
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class UWBListener(private val repository: LocationAppRepository) : BTTool() {

    var recording = false
    override suspend fun toolReadAction(msg: Message) {
        if (!recording) return
        val msgText = msg.obj.toString()

        Log.d("UWB",msgText)
        val jsonMessage: BTMessage = Json.decodeFromString(msgText)
        val currentTime = System.currentTimeMillis()
        val newLocation = Location(x=jsonMessage.x, y=jsonMessage.y, timestamp = currentTime)
        repository.insertLocation(newLocation)
    }
}