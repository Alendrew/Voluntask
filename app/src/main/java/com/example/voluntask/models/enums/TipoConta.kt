package com.example.voluntask.models.enums

enum class TipoConta(val i: String) {
    VOLUNTARIO("VOLUNTARIO"),
    INSTITUICAO("INSTITUICAO");

    companion object {
        fun fromValue(value: String): TipoConta {
            return when (value) {
                "VOLUNTARIO" -> VOLUNTARIO
                "INSTITUICAO" -> INSTITUICAO
                else -> VOLUNTARIO
            }
        }
    }
}