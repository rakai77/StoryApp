package com.example.storyapp.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object Constanta {
    val TOKEN = stringPreferencesKey("token")
    val STATE = booleanPreferencesKey("state")
}