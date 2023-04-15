package de.tierwohlteam.android.locationapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.locationapp.models.Location
import de.tierwohlteam.android.locationapp.others.Resource
import de.tierwohlteam.android.locationapp.repositories.LocationAppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: LocationAppRepository,
) : ViewModel() {

    private val _insertLocationFlow: MutableSharedFlow<Resource<Boolean>> = MutableSharedFlow()
    val insertLocationFlow = _insertLocationFlow as SharedFlow<Resource<Boolean>>

    private val _getLocationsFlow: MutableSharedFlow<Resource<List<Location>>> = MutableSharedFlow()
    val getLocationFlow = _getLocationsFlow as SharedFlow<Resource<List<Location>>>

    var allLocations: StateFlow<Resource<List<Location>>> = repository.allLocations.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Resource.loading(emptyList())
    )


    suspend fun record() {
        viewModelScope.launch {
            try {
                repository.deleteLocations();
                var timeStamp = 0L
                for (i in 1..6) {
                    timeStamp += i * Random.nextLong(200, 500)
                    val location = Location(
                        x = (i * 10).toFloat(), y = (i * 20).toFloat(),
                        timestamp = timeStamp)
                    repository.insertLocation(location)
                }
                _insertLocationFlow.emit(Resource.success(true))
            } catch (e: Exception) {
                _insertLocationFlow.emit((Resource.error("Error inserting", false)))
            }
        }
    }

    suspend fun getLocations(): List<Location> =
        repository.getLocations()

}