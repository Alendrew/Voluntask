package com.example.voluntask.models.enums

enum class Status(val i: String) {
    ATIVO("ATIVO"),
    ENCERRADO("ENCERRADO");

    companion object {
        fun fromValue(value: String): Status {
            return when (value) {
                "ATIVO" -> ATIVO
                "ENCERRADO" -> ENCERRADO
                else -> ATIVO
            }
        }
    }
}