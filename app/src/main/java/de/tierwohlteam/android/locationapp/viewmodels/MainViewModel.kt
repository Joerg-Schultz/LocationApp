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

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: LocationAppRepository,
) : ViewModel() {

    private val _insertLocationFlow: MutableSharedFlow<Resource<Boolean>> = MutableSharedFlow()
    val insertLocationFlow = _insertLocationFlow as SharedFlow<Resource<Boolean>>

    var allLocations: StateFlow<Resource<List<Location>>> = repository.allLocations.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = Resource.loading(emptyList())
    )

    suspend fun record() {
        viewModelScope.launch {
            try {
                for (i in 1..10) {
                    val location = Location(x = 1.0 + i * 20, y = 2.0 + i * 10,
                        timestamp = i.toLong() * 500)
                    repository.insertLocation(location)
                }
                _insertLocationFlow.emit(Resource.success(true))
            } catch (e: Exception) {
                _insertLocationFlow.emit((Resource.error("Error inserting", false)))
            }
        }
    }
}