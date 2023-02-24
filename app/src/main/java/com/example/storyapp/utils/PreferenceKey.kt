package com.example.storyapp.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceKey {
    val TOKEN = stringPreferencesKey("token")
    val USERNAME = stringPreferencesKey("username")
}