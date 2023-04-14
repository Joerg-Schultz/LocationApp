package de.tierwohlteam.android.locationapp.repositories

import de.tierwohlteam.android.locationapp.models.Location
import de.tierwohlteam.android.locationapp.others.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class LocationAppRepository @Inject constructor(
    private val database: LocationAppDB
) {
    /**
     * Location functions
     */
    private val locationDao = database.locationDao()

    /**
     * Insert a rating
     * @param[location] a new Rating
     */
    suspend fun insertLocation(location: Location) = locationDao.insert(location)
    suspend fun getLocations(): List<Location> = locationDao.getLocations()
    suspend fun deleteLocations() = locationDao.deleteLocations()

    /**
     * get all Ratings as Resource Flow
     */
    val allLocations: Flow<Resource<List<Location>>> = flow {
        emit(Resource.loading(null))
        val dataFlow = locationDao.getAll()
        emitAll(dataFlow.map { Resource.success(it) })
    }
}