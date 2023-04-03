package com.hotmail.or_dvir.tracks.preferences.repositories

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

private const val USER_PREFERENCES_NAME = "UserPreferences"
private val Context.dataStore by preferencesDataStore(
    name = USER_PREFERENCES_NAME
)

class UserPreferencesRepositoryImpl @Inject constructor(
    @ApplicationContext val context: Context
) : UserPreferencesRepository {

    we have context. do whatever else needs to be done

    override fun stuff() {
        Log.i("aaaaa", "repository: $context")
    }

}
