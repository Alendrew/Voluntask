package com.example.voluntask.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voluntask.models.Evento
import com.example.voluntask.models.Usuario
import com.example.voluntask.services.FirebaseService
import kotlinx.coroutines.launch

class EventoViewModel: ViewModel() {
    var userInfo: Usuario? = null

    fun createEvento(evento: Evento, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            callback(FirebaseService<Evento>("Eventos").createItem(evento))
        }
    }


    fun updateEvento(evento: Evento, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            callback(FirebaseService<Evento>("Eventos").updateItem(evento.idEvento,evento))
        }
    }

}