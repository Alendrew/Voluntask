package com.example.voluntask.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voluntask.models.Evento
import com.example.voluntask.models.Usuario
import com.example.voluntask.services.FirebaseService
import kotlinx.coroutines.launch

class EventoViewModel: ViewModel() {

    fun createEvento(evento: Evento, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            callback(FirebaseService<Evento>("Eventos").createItem(evento))
        }
    }

    fun getAllEventos(callback: (List<Evento>) -> Unit) {
        viewModelScope.launch {
            callback(FirebaseService<Evento>("Eventos").getAllEventos())
        }
    }

    fun updateEvento(evento: Evento, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            callback(FirebaseService<Evento>("Eventos").updateItem(evento.idEvento,evento))
        }
    }

}