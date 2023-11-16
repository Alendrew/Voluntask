package com.example.voluntask.models

import android.os.Parcelable
import android.util.Log
import com.example.voluntask.models.enums.TipoConta
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Instituicao(
    override var nome: String,
    var nomeRepresentanteLegal: String,
    override var telefone: String,
    override var tipoConta: TipoConta,
    var cnpj: String,
    override var idUsuario: String,
    var cpfRepresentanteLegal: String,
    override var dataCadastro: Date,
): Usuario(), Parcelable {

    constructor(): this("","","",TipoConta.INSTITUICAO,"","","",Date())

    override fun toMap(): Map<String, Any?> {
        return mapOf(
            "nome" to nome,
            "idUsuario" to idUsuario,
            "telefone" to telefone,
            "tipoConta" to tipoConta.i,
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
                val idUsuario = getString("idUsuario")!!
                val telefone = getString("telefone")!!
                val tipoConta = TipoConta.fromValue(getString("tipoConta")!!)
                val cnpj = getString("cnpj")!!
                val dataCadastro = getTimestamp("dataCadastro")!!.toDate()
                val cpfRepresentanteLegal = getString("cpfRepresentanteLegal")!!
                val nomeRepresentanteLegal = getString("nomeRepresentanteLegal")!!

                return Instituicao(
                    nome = nome,
                    telefone = telefone,
                    cnpj = cnpj,
                    dataCadastro = dataCadastro,
                    tipoConta = tipoConta,
                    cpfRepresentanteLegal = cpfRepresentanteLegal,
                    idUsuario = idUsuario,
                    nomeRepresentanteLegal = nomeRepresentanteLegal
                )
            } catch (e: Exception) {
                Log.e(TAG, "Error converting instituicao", e)
                return null
            }
        }

        private const val TAG = "Instituicao"
    }
}