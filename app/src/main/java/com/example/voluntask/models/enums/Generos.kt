package com.example.voluntask.models.enums

enum class Generos(val i: String) {
    MASCULINO("Masculino"),
    FEMININO("Feminino"),
    PREFIRO_NAO_DIZER("Prefiro não dizer");

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