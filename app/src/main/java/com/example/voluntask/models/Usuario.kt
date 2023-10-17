package com.example.voluntask.models

import com.example.voluntask.models.interfaces.ConvertibleToMap
import java.time.LocalDate
import java.util.Date

abstract class Usuario : ConvertibleToMap {
    abstract var nome: String
    abstract var descricao: String
    abstract var idUsuario: String
    abstract var telefone: String
    abstract var dataCadastro: Date
}