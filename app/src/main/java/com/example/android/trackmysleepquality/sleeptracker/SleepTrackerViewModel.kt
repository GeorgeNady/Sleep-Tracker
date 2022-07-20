/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.trackmysleepquality.sleeptracker

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.*
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import com.example.android.trackmysleepquality.formatNights
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * ViewModel for SleepTrackerFragment.
 */
class SleepTrackerViewModel(
    val database: SleepDatabaseDao,
    application: Application
) : AndroidViewModel(application) {

    // cached
    private val _tonight = MutableLiveData<SleepNight>()
    private val _navigateToSleepQuality = MutableLiveData<SleepNight>()
    private val _navigateToSleepDetails = MutableLiveData<SleepNight>()
    private var _showSnackbarEvent = MutableLiveData<Boolean>()

    // public
    val nights = database.getAllNights()
    val tonight: LiveData<SleepNight> get() = _tonight
    val navigateToSleepQuality: LiveData<SleepNight> get() = _navigateToSleepQuality
    val navigateToSleepDetails: LiveData<SleepNight> get() = _navigateToSleepDetails
    val showSnackBarEvent: LiveData<Boolean> get() = _showSnackbarEvent


    // transformations
    val nightsString = Transformations.map(nights) { nights -> formatNights(nights, application.resources) }
    val startButtonVisible = Transformations.map(tonight) { it == null }
    val stopButtonVisible = Transformations.map(tonight) { it != null }
    val clearButtonVisible = Transformations.map(nights) { it?.isNotEmpty() }

    init {
        initTonight()
    }

    private fun initTonight() = viewModelScope.launch {
        _tonight.postValue(withContext(Dispatchers.IO) {
            var night = database.getTonight()
            if (night?.endTimeMilli != night?.startTimeMilli) {
                night = null
            }
            night
        })
    }

    fun onStartTracking() = viewModelScope.launch {
        val sleepNight = SleepNight()
        withContext(Dispatchers.IO) {
            database.insert(SleepNight())
        }
        _tonight.value = sleepNight
    }

    fun onStopTracking() = viewModelScope.launch {
        val oldNight = _tonight.value ?: return@launch
        val newNight = oldNight.copy(endTimeMilli = System.currentTimeMillis())
        withContext(Dispatchers.IO) {
            database.update(newNight)
        }
        _navigateToSleepQuality.value = newNight
    }

    fun onNavigatingToDetails(sleep:SleepNight) {
        _navigateToSleepDetails.value = sleep
    }

    fun onCLear() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            database.clear()
        }
    }

    fun doneNavigating() {
        _navigateToSleepQuality.value = null
        _navigateToSleepDetails.value = null
    }

}

