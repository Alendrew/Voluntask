package com.example.voluntask.models

import android.os.Parcelable
import android.util.Log
import com.example.voluntask.models.enums.Categorias
import com.example.voluntask.models.enums.Status
import com.example.voluntask.models.interfaces.ConvertibleToMap
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.util.Date

@Parcelize
data class Inscricao(
    var idEvento:String,
    var idUsuario:String,
    var dataInscricao:String
): Parcelable, ConvertibleToMap {

    constructor(): this("","", "")

    var idInscricao:String = ""

    override fun toMap(): Map<String, Any?> {
        return mapOf(
            "idEvento" to idEvento,
            "idUsuario" to idUsuario,
            "dataInscricao" to dataInscricao,
        )
    }

    companion object {
        fun DocumentSnapshot.toInscricao(): Inscricao? {
            return try {
                val idEvento = getString("idEvento")!!
                val idUsuario = getString("idUsuario")!!
                val dataInscricao = getString("dataInscricao")!!

                Inscricao(
                    idEvento = idEvento,
                    idUsuario = idUsuario,
                    dataInscricao = dataInscricao
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error converting inscricao", e)
                null
            }
        }

        private const val TAG = "Inscricao"
    }
}
