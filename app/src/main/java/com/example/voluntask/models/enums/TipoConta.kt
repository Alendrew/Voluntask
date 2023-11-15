package com.example.voluntask.models.enums

enum class TipoConta(val i: Number) {
    VOLUNTARIO(0),
    INSTITUICAO(1);

    companion object {
        fun fromValue(value: Number): TipoConta {
            return when (value) {
                0 -> VOLUNTARIO
                1 -> INSTITUICAO
                else -> VOLUNTARIO
            }
        }
    }
}