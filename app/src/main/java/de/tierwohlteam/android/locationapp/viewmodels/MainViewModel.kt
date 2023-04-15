package de.tierwohlteam.android.locationapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.tierwohlteam.android.locationapp.models.Location
import de.tierwohlteam.android.locationapp.models.UWBListener
import de.tierwohlteam.android.locationapp.others.Resource
import de.tierwohlteam.android.locationapp.repositories.LocationAppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    val repository: LocationAppRepository,
) : ViewModel() {

    var uwbListener: UWBListener? = null
    private val _deleteLocationFlow: MutableSharedFlow<Resource<Boolean>> = MutableSharedFlow()
    val deleteLocationFlow = _deleteLocationFlow as SharedFlow<Resource<Boolean>>

    private val _getLocationsFlow: MutableSharedFlow<Resource<List<Location>>> = MutableSharedFlow()
    val getLocationFlow = _getLocationsFlow as SharedFlow<Resource<List<Location>>>

    var allLocations: StateFlow<Resource<List<Location>>> = repository.allLocations.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Resource.loading(emptyList())
    )


    suspend fun deleteLocations() {
        viewModelScope.launch {
            try {
                repository.deleteLocations();
                _deleteLocationFlow.emit(Resource.success(true))
            } catch (e: Exception) {
                _deleteLocationFlow.emit((Resource.error("Error deleting $e", false)))
            }
        }
    }

    suspend fun getLocations(): List<Location> =
        repository.getLocations()

}