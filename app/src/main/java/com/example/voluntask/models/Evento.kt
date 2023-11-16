package com.example.voluntask.models

import android.os.Parcelable
import android.util.Log
import com.example.voluntask.models.enums.Categorias
import com.example.voluntask.models.enums.Status
import com.example.voluntask.models.interfaces.ConvertibleToMap
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Evento(
    var nome:String,
    var localizacao:String,
    var descricao:String,
    var idInstituicao:String,
    var dataInicio:Date,
    var dataFim: Date,
    var dataCadastro:Date,
    var categoria:Categorias,
    var status:Status
): Parcelable, ConvertibleToMap {

    constructor(): this("", "","","", Date(),Date(),Date(),Categorias.CARIDADE,Status.ENCERRADO)

    var idEvento:String = ""

    override fun toMap(): Map<String, Any?> {
        return mapOf(
            "nome" to nome,
            "localizacao" to localizacao,
            "descricao" to descricao,
            "idInstituicao" to idInstituicao,
            "idEvento" to idEvento,
            "dataHoraInicio" to dataInicio,
            "dataHoraFim"  to dataFim,
            "dataCadastro" to dataCadastro,
            "categoria" to categoria,
            "status" to status
        )
    }

    companion object {
        fun DocumentSnapshot.toObject(): Evento? {
            try {
                val nome = getString("nome")!!
                val localizacao = getString("localizacao")!!
                val descricao = getString("descricao")!!
                val idInstituicao = getString("idInstituicao")!!
                val idEvento = getString("idEvento")!!
                val dataHoraInicio = getTimestamp("dataHoraInicio")!!.toDate()
                val dataHoraFim = getTimestamp("dataHoraFim")!!.toDate()
                val dataCadastro = getTimestamp("dataCadastro")!!.toDate()
                val categoria = Categorias.fromValue(getString("categoria")!!)
                val status = Status.fromValue(getString("status")!!)

                val evento = Evento(
                    nome = nome,
                    localizacao = localizacao,
                    descricao = descricao,
                    idInstituicao = idInstituicao,
                    dataInicio = dataHoraInicio,
                    dataFim = dataHoraFim,
                    dataCadastro = dataCadastro,
                    categoria = categoria,
                    status = status
                )
                evento.idEvento = idEvento
                return evento
            } catch (e: Exception) {
                Log.e(TAG, "Error converting evento", e)
                return null
            }
        }

        private const val TAG = "Evento"
    }
}
