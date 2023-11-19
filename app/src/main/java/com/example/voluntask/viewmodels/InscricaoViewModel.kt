package com.example.voluntask.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voluntask.models.Evento
import com.example.voluntask.models.Inscricao
import com.example.voluntask.services.FirebaseService
import kotlinx.coroutines.launch

class InscricaoViewModel: ViewModel() {

    fun createInscricao(inscricao: Inscricao, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            callback(FirebaseService<Inscricao>("Inscricoes").createItem(inscricao))
        }
    }

    fun inscrito(idEvento: String, idUsuario: String, callback: (Inscricao?) -> Unit){
        viewModelScope.launch {
            callback(FirebaseService<Inscricao>("Inscricoes").searchInscricaoByEvento(idEvento,idUsuario))
        }
    }

    fun deletarInscricao(idInscricao: String, callback: (Boolean) -> Unit){
        viewModelScope.launch {
            callback(FirebaseService<Inscricao>("Inscricoes").deleteItem(idInscricao))
        }
    }

    fun getAllInscricoes(callback: (List<Inscricao>) -> Unit) {
        viewModelScope.launch {
            callback(FirebaseService<Inscricao>("Inscricoes").getAllInscricoes())
        }
    }

    fun getInscricoesAndEventos(idUsuario: String, callback: (List<Evento>) -> Unit){
        viewModelScope.launch {
            callback(FirebaseService<Inscricao>("Inscricoes").getInscricoesAndEventos(idUsuario))
        }
    }
}