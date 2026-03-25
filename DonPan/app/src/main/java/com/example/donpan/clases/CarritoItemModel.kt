package com.example.donpan.clases

data class CarritoItemModel(
    val producto: ProductoModel,
    var cantidad: Int = 1
)