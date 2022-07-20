package com.example.android.trackmysleepquality.sleepdetailes

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import com.example.android.trackmysleepquality.database.SleepNight
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SleepDetailsViewModel(
    private val sleepNightId: Long = 0L,
    private val database: SleepDatabaseDao
) : ViewModel() {

    // cashed
    private val _sleepNight = MutableLiveData<SleepNight>()

    // public
    val sleepNight: LiveData<SleepNight> get() = _sleepNight


    init {
        getSleepNight()
    }

    private fun getSleepNight() = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            val sleep = database.get(sleepNightId)
            withContext(Dispatchers.Main) {
                _sleepNight.value = sleep
            }
        }
    }

}