package com.example.voluntask.util

enum class Types(i: Number) {
    SUCESS(0),
    WARNING(1),
    ERROR(2);

    companion object {
        fun fromValue(value: Number): Types {
            return when (value) {
                0 -> Types.SUCESS
                1 -> Types.WARNING
                2 -> Types.ERROR
                else -> Types.ERROR
            }
        }
    }
}
