package com.example.busapp.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.busapp.datastore.Storage
import com.example.busapp.models.BusStop
import com.example.busapp.models.UserData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
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

    fun addUser(user: UserData) = viewModelScope.launch {
        userDataStorage.insert(user).catch {  e ->
            Log.e("USER_VIEW_MODEL", "Error inserting user: ${e.message}", e)}
            .collect()
        userDataStorage.getAll().catch { Log.e("USER_VIEW_MODEL", it.toString()) }
            .collect{_users.emit(it)}
    }

    fun editUserById(busStop: BusStop) = viewModelScope.launch {
        Log.d("USER_VIEW_MODEL", "Editing User")
        userDataStorage.edit(0, UserData(0, busStop)).collect()
        userDataStorage.getAll().catch { Log.e("FLASH_CARD_VIEW_MODEL", it.toString()) }
            .collect { _users.emit(it) }
        }
}

