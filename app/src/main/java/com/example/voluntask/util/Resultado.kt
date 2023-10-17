package com.example.voluntask.util

import com.google.firebase.auth.FirebaseUser

class Resultado(
    val result: Boolean,
    val msg: String,
    val user: FirebaseUser?
){
    constructor(msg: String) : this(false, msg, null) {
    }
    constructor(result: Boolean,msg: String) : this(false, msg, null) {
    }
}


