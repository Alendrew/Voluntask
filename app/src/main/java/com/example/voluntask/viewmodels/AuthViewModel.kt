package com.example.voluntask.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.voluntask.models.Instituicao
import com.example.voluntask.models.Usuario
import com.example.voluntask.models.Voluntario
import com.example.voluntask.services.FirebaseService
import com.example.voluntask.util.Resultado
import com.google.firebase.auth.UserInfo
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    fun login(email: String, senha: String, callback: (Resultado) -> Unit) {
        viewModelScope.launch {
            callback(FirebaseService.loginWithEmail(email, senha))
        }
    }

    fun getUserInfo(idUser: String,callback: (Usuario?) -> Unit){
        viewModelScope.launch {
            callback(FirebaseService<Usuario>("Voluntarios").getUserInfo(idUser))
        }
    }

    fun forget(email: String, callback: (Resultado) -> Unit) {
        viewModelScope.launch {
            callback(FirebaseService.forgetPassword(email))
        }
    }

    fun register(usuario: Usuario, email: String, senha: String, callback: (Resultado) -> Unit) {
        val collection = when (usuario) {
            is Voluntario -> "Voluntarios"
            is Instituicao -> "Instituicoes"
            else -> ""
        }
        viewModelScope.launch {
            callback(FirebaseService<Usuario>(collection).registerWithEmail(usuario, email, senha))
        }
    }
}
