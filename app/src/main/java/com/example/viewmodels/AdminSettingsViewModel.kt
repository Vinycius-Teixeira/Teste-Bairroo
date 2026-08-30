package com.example.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.AdminSettings
import com.example.repository.AdminSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AdminSettingsViewModel(private val repository: AdminSettingsRepository) : ViewModel() {
    private val _settings = MutableStateFlow(AdminSettings())
    val settings = _settings.asStateFlow()

    init {
        viewModelScope.launch {
            _settings.value = repository.getSettings()
        }
    }

    fun updateSettings(newSettings: AdminSettings) {
        viewModelScope.launch {
            repository.updateSettings(newSettings)
            _settings.value = newSettings
        }
    }
}
