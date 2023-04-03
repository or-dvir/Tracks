package com.hotmail.or_dvir.tracks.preferences.repositories

import kotlinx.coroutines.flow.Flow

interface UserPreferencesRepository {
    fun isDarkMode(): Flow<Boolean>
    suspend fun setDarkMode(darkMode: Boolean)
}
