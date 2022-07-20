package com.example.android.trackmysleepquality.sleepdetailes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.android.trackmysleepquality.database.SleepDatabaseDao
import java.lang.IllegalArgumentException

class SleepDetailsViewModelFactory(
    private val sleepNightId: Long = 0L,
    private val database: SleepDatabaseDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SleepDetailsViewModel::class.java)) {
            return SleepDetailsViewModel(sleepNightId, database)  as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}