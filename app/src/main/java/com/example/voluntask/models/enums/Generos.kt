package com.example.voluntask.models.enums

enum class Generos(val i: String) {
    MASCULINO("MASCULINO"),
    FEMININO("FEMININO"),
    PREFIRO_NAO_DIZER("PREFIRO_NAO_DIZER");

    companion object {
        fun fromValue(value: String): Generos {
            return when (value) {
                "MASCULINO" -> Generos.MASCULINO
                "FEMININO" -> Generos.FEMININO
                "PREFIRO_NAO_DIZER" -> Generos.PREFIRO_NAO_DIZER
                else -> PREFIRO_NAO_DIZER
            }
        }
    }
}