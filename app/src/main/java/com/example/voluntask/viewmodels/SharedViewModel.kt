package com.example.voluntask.viewmodels

import androidx.lifecycle.MutableLiveData

import androidx.lifecycle.ViewModel
import com.example.voluntask.models.Usuario


class SharedViewModel : ViewModel() {
    val usuario = MutableLiveData<Usuario>()

    fun setUser(usuario: Usuario) {
        this.usuario.value = usuario
    }
}