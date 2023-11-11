package com.example.voluntask.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voluntask.models.Evento
import com.example.voluntask.models.Usuario
import com.example.voluntask.services.FirebaseService
import com.example.voluntask.util.Resultado
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {
    var userInfo: Usuario? = null

    fun getAllEventos(callback: (List<Evento>) -> Unit) {
        viewModelScope.launch {
            callback(FirebaseService<Evento>("Eventos").getAllEventos())
        }
    }

}