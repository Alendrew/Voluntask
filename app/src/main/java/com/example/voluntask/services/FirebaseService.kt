package com.example.voluntask.services

import android.util.Log
import com.example.voluntask.models.Evento
import com.example.voluntask.models.Evento.Companion.toEvento
import com.example.voluntask.models.Inscricao
import com.example.voluntask.models.Inscricao.Companion.toInscricao
import com.example.voluntask.models.Instituicao
import com.example.voluntask.models.Usuario
import com.example.voluntask.models.Voluntario
import com.example.voluntask.models.interfaces.ConvertibleToMap
import com.example.voluntask.util.Resultado
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await


class FirebaseService<T : ConvertibleToMap>(private val collectionPath: String) {
    private val db = FirebaseFirestore.getInstance()
    private val itemCollections = db.collection(collectionPath)

    suspend fun createItem(item: T): Boolean {
        return try {
            itemCollections.add(item.toMap()).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding", e)
            false
        }
    }

    suspend fun getInscricoesAndEventos(idUsuario: String): List<Evento> {
        val eventosList: MutableList<Evento> = mutableListOf()
        try {
            val inscricoes = getAllInscricoes()
            val eventosCollection = db.collection("Eventos")
            val filteredInscricoes = inscricoes.filter {
                (it.idUsuario == idUsuario)
            }
            for (inscricao in filteredInscricoes) {
                val evento = eventosCollection.document(inscricao.idEvento).get().await()
                if (evento.exists()){
                    val itemConvertido = evento.toEvento()
                    itemConvertido!!.idEvento = evento.id
                    eventosList.add(itemConvertido)
                }

            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting inscricoes&Eventos", e)
        }
        return eventosList
    }

    suspend fun deleteUser(usuario: Usuario): Boolean{
        return try {
            val user: FirebaseUser? = auth.currentUser
            user?.delete()?.await()
            itemCollections.document(usuario.idInfoConta).delete().await()
            auth.signOut()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting user", e)
            false
        }
    }

    suspend fun deleteItem(idItem: String): Boolean {
        return try {
            itemCollections.document(idItem).delete().await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting", e)
            false
        }

    }

    suspend fun updateItem(idItem: String, item: T): Boolean {
        return try {
            itemCollections.document(idItem).update(item.toMap()).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error updating", e)
            false
        }
    }


    suspend fun getUserInfo(idUser: String): Usuario? {
        val voluntariosCollection = db.collection("Voluntarios")
        val instituicaoCollection = db.collection("Instituicoes")
        var userInfo: QuerySnapshot
        try {
            userInfo = voluntariosCollection.whereEqualTo("idUsuario", idUser).get().await()
            if (userInfo.isEmpty) {
                userInfo = instituicaoCollection.whereEqualTo("idUsuario", idUser).get().await()
            }

            val tipoConta = userInfo.documents[0].getString("tipoConta")

            return if (tipoConta == "VOLUNTARIO") {
                val usuario = userInfo.documents[0].toObject(Voluntario::class.java)
                usuario!!.idInfoConta = userInfo.documents[0].id
                usuario
            } else {
                val usuario = userInfo.documents[0].toObject(Instituicao::class.java)
                usuario!!.idInfoConta = userInfo.documents[0].id
                usuario
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error getting user info", e)
        }
        return null
    }

    suspend fun getAllEventos(): List<Evento> {
        val lista: MutableList<Evento> = mutableListOf()

        try {
            val doc = itemCollections.get().await()
            for (registro in doc) {
                val itemConvertido = registro.toEvento()
                if (itemConvertido != null) {
                    itemConvertido.idEvento = registro.id
                    lista.add(itemConvertido)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching eventos: $e")
        }

        return lista
    }

    suspend fun searchInscricaoByEvento(idEvento: String, idUsuario: String): Inscricao? {
        var eventoTemInscricoes: QuerySnapshot
        try {
            eventoTemInscricoes = itemCollections.whereEqualTo("idEvento", idEvento).get().await()
            if (eventoTemInscricoes.isEmpty) {
                return null
            } else {
                val lista: MutableList<Inscricao> = mutableListOf()
                for (registro in eventoTemInscricoes) {
                    val itemConvertido = registro.toInscricao()
                    if (itemConvertido != null) {
                        itemConvertido.idInscricao = registro.id
                        lista.add(itemConvertido)
                    }
                }
                lista.filter {
                    it.idUsuario == idUsuario
                }
                return lista[0]
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error getting user info", e)
        }
        return null
    }

    suspend fun getAllInscricoes(): List<Inscricao> {
        val lista: MutableList<Inscricao> = mutableListOf()

        try {
            val doc = itemCollections.get().await()
            for (registro in doc) {
                val itemConvertido = registro.toInscricao()
                if (itemConvertido != null) {
                    itemConvertido.idInscricao = registro.id
                    lista.add(itemConvertido)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching inscricoes: $e")
        }

        return lista
    }

    suspend fun registerWithEmail(usuario: Usuario, email: String, senha: String): Resultado {
        var user: FirebaseUser? = null
        return try {
            if (auth.fetchSignInMethodsForEmail(email).await().signInMethods!!.isEmpty()) {
                user = auth.createUserWithEmailAndPassword(email, senha).await().user
                usuario.idUsuario = user!!.uid.toString()
                itemCollections.add(usuario.toMap()).await()
                Resultado(true, "Cadastrado com sucesso", user)
            } else {
                Resultado(false, "Usuário já existe", null)
            }
        } catch (e: Exception) {
            user?.delete()?.await()
            Resultado(false, "Erro ao cadastrar", null)
        }
    }

    companion object {
        const val TAG = "FirebaseService"
        private val auth = Firebase.auth

        suspend fun forgetPassword(email: String): Resultado {
            try {
                return if (auth.fetchSignInMethodsForEmail(email)
                        .await().signInMethods!!.isNotEmpty()
                ) {
                    auth.sendPasswordResetEmail(email).await()
                    return Resultado(true, "Sucesso", null)
                } else {
                    Resultado(false, "E-mail não cadastrado")
                }
            } catch (e: FirebaseTooManyRequestsException) {
                Log.e("Forget", "Erro: $e")
                return Resultado(false, "Muitas solicitações, aguarde um pouco", null)
            } catch (e: Exception) {
                Log.e("Forget", "Erro: $e")
                return Resultado(false, "Erro desconhecido", null)
            }
        }

        suspend fun loginWithEmail(email: String, senha: String): Resultado {
            try {
                return if (auth.fetchSignInMethodsForEmail(email)
                        .await().signInMethods!!.isNotEmpty()
                ) {
                    val user = auth.signInWithEmailAndPassword(email, senha).await().user
                    if (user != null) {
                        Resultado(true, "Sucesso", user)
                    } else {
                        Resultado(false, "Erro ao realizar login", null)
                    }
                } else {
                    Resultado(false, "E-mail não cadastrado")
                }
            } catch (e: FirebaseAuthInvalidCredentialsException) {
                return Resultado(false, "Usuário ou senha inválido", null)
            } catch (e: Exception) {
                Log.e("Login", "Erro: $e")
                return Resultado(false, "Erro desconhecido", null)
            }
        }
    }
}

