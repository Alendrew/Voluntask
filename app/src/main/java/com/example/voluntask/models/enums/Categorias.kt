package com.example.voluntask.models.enums

enum class Categorias(val i: String) {
    DOACAO("DOACAO"),
    LIMPEZA("LIMPEZA"),
    CARIDADE("CARIDADE");

    companion object {
        fun fromValue(value: String): Categorias {
            return when (value) {
                "DOACAO" -> Categorias.DOACAO
                "LIMPEZA" -> Categorias.LIMPEZA
                "CARIDADE" -> Categorias.CARIDADE
                else -> Categorias.DOACAO
            }
        }
    }
}