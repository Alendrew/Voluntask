package com.example.voluntask.services

import android.util.Log
import com.example.voluntask.models.Evento
import com.example.voluntask.models.Instituicao
import com.example.voluntask.models.Instituicao.Companion.toObject
import com.example.voluntask.models.Usuario
import com.example.voluntask.models.Voluntario
import com.example.voluntask.models.interfaces.ConvertibleToMap
import com.example.voluntask.util.Resultado
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
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

    suspend fun getItemById(idItem: String): DocumentSnapshot? {
        return try{
            itemCollections.document(idItem).get().await()
        }catch (e: Exception){
            Log.e(TAG,"Error getting item by id", e)
            null
        }
    }

    suspend fun deleteItem(idItem: String) {
        try {
            itemCollections.document(idItem).delete().await()
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting", e)
        }

    }

    suspend fun updateItem(idItem: String, item: T) {
        try {
            itemCollections.document(idItem).update(item.toMap()).await()
        } catch (e: Exception) {
            Log.e(TAG, "Error updating", e)
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
                userInfo.documents[0].toObject(Voluntario::class.java)
            } else {
                userInfo.documents[0].toObject(Instituicao::class.java)
            }

        } catch (e: Exception) {
            Log.e(TAG,"Error getting user info", e)
        }
        return null
    }

    suspend fun getAllEventos(): List<Evento> {
        val lista: MutableList<Evento> = mutableListOf()

        try {
            val doc = itemCollections.get().await()
            for (registro in doc) {
                val itemConvertido = registro.toObject(Evento::class.java)
                lista.add(itemConvertido)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching eventos: $e")
        }

        return lista
    }

    suspend fun registerWithEmail(usuario: Usuario, email: String, senha: String): Resultado {
        try {
            return if (auth.fetchSignInMethodsForEmail(email).await().signInMethods!!.isEmpty()) {
                val user = auth.createUserWithEmailAndPassword(email, senha).await().user
                if (user != null) {
                    usuario.idUsuario = user.uid.toString()
                    val userInfo = itemCollections.add(usuario.toMap()).await()
                    if (userInfo != null) {
                        Resultado(true, "Cadastrado com sucesso", user)
                    } else {
                        user.delete().await()
                        Resultado(false, "Erro ao cadastrar", null)
                    }
                } else {
                    Resultado(false, "Erro ao cadastrar", null)
                }
            } else {
                Resultado(false, "Usuário já existe", null)
            }
        } catch (e: Exception) {
            return Resultado(false, "Erro desconhecido", null)
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

