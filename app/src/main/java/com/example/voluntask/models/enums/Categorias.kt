package com.example.voluntask.models.enums

enum class Categorias(i: Number) {
    DOACAO(0),
    LIMPEZA(1),
    CARIDADE(2);

    companion object {
        fun fromValue(value: Number): Categorias {
            return when (value) {
                0 -> Categorias.DOACAO
                1 -> Categorias.LIMPEZA
                2 -> Categorias.CARIDADE
                else -> Categorias.DOACAO
            }
        }
    }
}