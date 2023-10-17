package com.example.voluntask.models.enums

enum class Generos(i: Number) {
    HOMEM(0),
    MULHER(1),
    PREFIRO_NAO_DIZER(2);

    companion object {
        fun fromValue(value: Number): Generos {
            return when (value) {
                0 -> Generos.HOMEM
                1 -> Generos.MULHER
                2 -> Generos.PREFIRO_NAO_DIZER
                else -> PREFIRO_NAO_DIZER
            }
        }
    }
}