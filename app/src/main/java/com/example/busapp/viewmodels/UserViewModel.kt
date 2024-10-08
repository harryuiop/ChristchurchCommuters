package com.example.busapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busapp.datastore.Storage
import com.example.busapp.models.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

class UserViewModel(
    private val userDataStorage: Storage<UserData>
): ViewModel() {
    //Only ever one user per device
    private val _users = MutableStateFlow<List<UserData>>(emptyList())
    val user: StateFlow<List<UserData>> = _users.asStateFlow()

    //Again, only one user
    fun getAllUsers() = viewModelScope.launch {
        userDataStorage.getAll().catch { Log.e("USER_VIEW_MODEL", it.toString()) }
            .collect{_users.emit(it)}
    }

}