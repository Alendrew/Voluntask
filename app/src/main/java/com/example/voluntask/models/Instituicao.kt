package com.example.voluntask.models

import android.os.Parcelable
import android.util.Log
import com.example.voluntask.models.interfaces.ConvertibleToMap
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Instituicao(
    override var nome: String,
    override var descricao: String,
    var nomeRepresentanteLegal: String,
    override var telefone: String,
    var cnpj: String,
    var cpfRepresentanteLegal: String,
    override var dataCadastro: Date,
): Usuario(), Parcelable {

    override var idUsuario = ""

    override fun toMap(): Map<String, Any?> {
        return mapOf(
            "nome" to nome,
            "descricao" to descricao,
            "idUsuario" to idUsuario,
            "telefone" to telefone,
            "cnpj" to cnpj,
            "dataCadastro" to dataCadastro,
            "cpfRepresentanteLegal" to cpfRepresentanteLegal,
            "nomeRepresentanteLegal" to nomeRepresentanteLegal
        )
    }

    companion object {
        fun DocumentSnapshot.toObject(): Instituicao? {
            try {
                val nome = getString("nome")!!
                val descricao = getString("descricao")!!
                val idUsuario = getString("idUsuario")!!
                val telefone = getString("telefone")!!
                val cnpj = getString("cnpj")!!
                val dataCadastro = getTimestamp("dataCadastro")!!.toDate()
                val cpfRepresentanteLegal = getString("cpfRepresentanteLegal")!!
                val nomeRepresentanteLegal = getString("nomeRepresentanteLegal")!!

                val instituicao = Instituicao(
                    nome = nome,
                    descricao = descricao,
                    telefone = telefone,
                    cnpj = cnpj,
                    dataCadastro = dataCadastro,
                    cpfRepresentanteLegal = cpfRepresentanteLegal,
                    nomeRepresentanteLegal = nomeRepresentanteLegal
                )
                instituicao.idUsuario = idUsuario
                return instituicao
            } catch (e: Exception) {
                Log.e(TAG, "Error converting instituicao", e)
                return null
            }
        }

        private const val TAG = "Instituicao"
    }
}