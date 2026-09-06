package com.example.viewmodels

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.models.BairrooAddress
import com.example.models.User
import com.example.repository.AuthRepository
import com.example.repository.LocationRepository
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(private val repository: AuthRepository = AuthRepository()) : ViewModel() {
    
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    private val _addresses = MutableStateFlow<List<BairrooAddress>>(emptyList())
    val addresses: StateFlow<List<BairrooAddress>> = _addresses

    // Direct instances for simplicity
    private val storage = FirebaseStorage.getInstance()

    fun login(email: String, password: String, allUsers: List<User>): String? {
        val user = repository.authenticate(email, password, allUsers)
        if (user != null) {
            _currentUser.value = user
            loadAddresses(user.id)
            return null // Success
        }
        return "E-mail ou senha incorretos."
    }

    private fun loadAddresses(userId: String) {
        viewModelScope.launch {
            _addresses.value = repository.getAddresses(userId)
        }
    }

    fun saveAddress(userId: String, address: BairrooAddress) {
        viewModelScope.launch {
            repository.saveAddress(userId, address)
            loadAddresses(userId)
        }
    }

    fun updateFcmToken(userId: String) {
        viewModelScope.launch {
            try {
                val token = FirebaseMessaging.getInstance().token.await()
                android.util.Log.d("FCM_DEBUG", "FCM Token obtido para o usuário $userId: $token")
                repository.saveFcmToken(userId, token)
            } catch (e: Exception) {
                android.util.Log.e("FCM_DEBUG", "Erro ao obter/salvar FCM Token para $userId", e)
            }
        }
    }

    suspend fun uploadUserImage(userId: String, imageUri: Uri): String {
        val ref = storage.reference.child("users/profile/$userId.jpg")
        ref.putFile(imageUri).await()
        return ref.downloadUrl.await().toString()
    }

    suspend fun updateUserLocation(context: Context, userId: String) {
        val locationRepository = LocationRepository(context)
        val location = locationRepository.getCurrentLocation()
        if (location != null) {
            // Update location in Firestore - assuming AuthRepository could have this
            // To be added if not existing
        }
    }
}
