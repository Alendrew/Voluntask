package com.example.voluntask.models

import android.os.Parcelable
import com.example.voluntask.models.enums.TipoConta
import com.example.voluntask.models.interfaces.ConvertibleToMap
import java.time.LocalDate
import java.util.Date

abstract class Usuario : ConvertibleToMap, Parcelable {
    abstract var nome: String
    abstract var idUsuario: String
    abstract var telefone: String
    abstract var dataCadastro: String
    abstract var tipoConta: TipoConta
    abstract var email: String
    abstract var idInfoConta: String
}