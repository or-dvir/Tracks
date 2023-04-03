package com.hotmail.or_dvir.tracks.ui.mainActivity

import androidx.lifecycle.ViewModel
import com.hotmail.or_dvir.tracks.preferences.repositories.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val userPreferencesRepo: UserPreferencesRepository,
) : ViewModel() {
    fun stuff() = userPreferencesRepo.stuff()
}
