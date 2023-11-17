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
    override var telefone: String,
    override var dataCadastro: Date,
    override var tipoConta: TipoConta,
    override var idUsuario: String,
    var dataNascimento: Date,
    var cpf: String,
    var genero: Generos
): Usuario(),Parcelable {

    constructor(): this("","",Date(),TipoConta.VOLUNTARIO,"",Date(),"",Generos.PREFIRO_NAO_DIZER)

    override fun toMap(): Map<String, Any?> {
        return mapOf(
            "nome" to nome,
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
                 val idUsuario = getString("idUsuario")!!
                 val telefone = getString("telefone")!!
                 val cpf = getString("cpf")!!
                 val tipoConta = TipoConta.fromValue(getString("tipoConta")!!)
                 val dataCadastro = getTimestamp("dataCadastro")!!.toDate()
                 val dataNascimento = getTimestamp("dataNascimento")!!.toDate()
                 val genero = Generos.fromValue(getString("genero")!!)
                 return Voluntario(
                     nome = nome,
                     telefone = telefone,
                     cpf = cpf,
                     dataCadastro = dataCadastro,
                     dataNascimento = dataNascimento,
                     tipoConta = tipoConta,
                     idUsuario = idUsuario,
                     genero = genero
                 )
             } catch (e: Exception) {
                 Log.e(TAG, "Error converting voluntario", e)
                 return null
             }
        }

        private const val TAG = "Voluntario"
    }
}