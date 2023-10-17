package com.example.voluntask.models.enums

enum class Status(i: Number) {
    ATIVO(0),
    ENCERRADO(1);

    companion object {
        fun fromValue(value: Number): Status {
            return when (value) {
                0 -> ATIVO
                1 -> ENCERRADO
                else -> ATIVO
            }
        }
    }
}