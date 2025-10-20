package com.misfinanzas

data class Gasto(
    val id: Int,
    val monto: Double,
    val categoria: String,
    val fecha: String,
    val descripcion: String?
)