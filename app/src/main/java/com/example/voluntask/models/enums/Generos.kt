package com.example.voluntask.models.enums

enum class Generos(val i: Number) {
    MASCULINO(0),
    FEMININO(1),
    PREFIRO_NAO_DIZER(2);

    companion object {
        fun fromValue(value: Number): Generos {
            return when (value) {
                0 -> Generos.MASCULINO
                1 -> Generos.FEMININO
                2 -> Generos.PREFIRO_NAO_DIZER
                else -> PREFIRO_NAO_DIZER
            }
        }
    }
}