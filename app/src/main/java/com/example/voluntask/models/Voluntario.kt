package com.example.voluntask.models

import android.os.Parcelable
import android.util.Log
import com.example.voluntask.models.enums.Generos
import com.example.voluntask.models.enums.TipoConta
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.parcelize.Parcelize
import java.util.Date

@Parcelize
data class Voluntario(
    override  var nome: String,
    override var descricao: String,
    override var telefone: String,
    override var dataCadastro: Date,
    override var tipoConta: TipoConta,
    var dataNascimento: Date,
    var cpf: String,
    var genero: Generos
): Usuario(),Parcelable {

    override var idUsuario = ""

    override fun toMap(): Map<String, Any?> {
        return mapOf(
            "nome" to nome,
            "descricao" to descricao,
            "idUsuario" to idUsuario,
            "telefone" to telefone,
            "tipoConta" to tipoConta,
            "cpf" to cpf,
            "dataCadastro" to dataCadastro,
            "dataNascimento" to dataNascimento,
            "genero" to genero
        )
    }

    companion object {
         fun DocumentSnapshot.toObject(): Voluntario? {
            try {
                val nome = getString("nome")!!
                val descricao = getString("descricao")!!
                val idUsuario = getString("idUsuario")!!
                val telefone = getString("telefone")!!
                val cpf = getString("cpf")!!
                val tipoConta = TipoConta.fromValue(getLong("tipoConta")!!)
                val dataCadastro = getTimestamp("dataCadastro")!!.toDate()
                val dataNascimento = getTimestamp("dataNascimento")!!.toDate()
                val genero = Generos.fromValue(getLong("genero")!!)
                val voluntario = Voluntario(
                    nome = nome,
                    descricao = descricao,
                    telefone = telefone,
                    cpf = cpf,
                    dataCadastro = dataCadastro,
                    dataNascimento = dataNascimento,
                    tipoConta = tipoConta,
                    genero = genero
                )
                voluntario.idUsuario = idUsuario
                return voluntario
            } catch (e: Exception) {
                Log.e(TAG, "Error converting voluntario", e)
                return null
            }
        }

        private const val TAG = "Voluntario"
    }
}